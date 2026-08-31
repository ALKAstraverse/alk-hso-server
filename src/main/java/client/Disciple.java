package client;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import core.Manager;
import core.Service;
import core.Util;
import io.Message;
import map.Map;
import map.MapService;
import map.Mob_in_map;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import template.Item3;
import template.Item47;
import template.ItemTemplate3;
import template.ItemTemplate4;
import template.LvSkill;
import template.Mob_MoTaiNguyen;
import template.Option;
import template.Part_fashion;
import template.Skill;

public class Disciple {
    public static final int STATE_NORMAL = 0;
    public static final int STATE_TRAINING = 1;
    public static final int STATE_SUMMONED = 2;

    // DEBUG: Set to true to disable AI completely for testing
    public static boolean DEBUG_AI_OFF = false;

    public static final int MAX_FREE_TRAIN_TIME_SECONDS = 180 * 60; // 180 minutes (3 hours)
    public static final int EXTRA_TRAIN_TICKET_SECONDS = 180 * 60;   // 180 minutes extra
    public static final long SUMMON_COOLDOWN_MS = 10 * 60 * 1000L;   // 10 minutes

    public static final short ITEM_BUA_GOI_DETU = 219;
    public static final short ITEM_KEO_HO_LO = 220;
    public static final short ITEM_VE_LUYEN_DETU = 227;

    public static final byte CLAZZ_WARRIOR = 0;
    public static final byte CLAZZ_ASSASSIN = 1;
    public static final byte CLAZZ_MAGE = 2;
    public static final byte CLAZZ_ARCHER = 3;

    // Core Player/Character identity
    public Player master;
    public int id; // Unique Character ID (distinct from master.id)
    public String name = "";
    public byte clazz;
    public short level;
    public long exp;
    public byte head;
    public byte eye;
    public byte hair;
    public Item3[] wear; // Disciple equipment wear (size 24)
    public int point1;
    public int point2;
    public int point3;
    public int point4;
    public int tiemnang;
    public short kynang;
    public byte[] skill_point;
    public Skill[] skills;
    public int hp;
    public int mp;

    // Daily training & state tracking
    public int state = STATE_NORMAL;
    public int training_time_used_today; // in seconds
    public boolean training_ticket_bought_today;
    public boolean training_ticket_used_today;
    public String last_train_date = "";
    public long summon_cooldown_until; // timestamp ms
    public long current_train_session_start; // timestamp ms

    // Summoned follow & AI in map
    public int summon_map_id = -1;
    public int summon_zone_id = -1;
    public short summon_x;
    public short summon_y;
    public Mob_in_map target_mob;
    public Mob_MoTaiNguyen target_mo;
    public long time_atk;
    public long time_move;
    public long time_wander;

    // Combat AI fields
    public short lastFarmMonsterType = -1;
    public long time_use_potion_hp;
    public long time_use_potion_mp;
    public long[] time_delay_skill = new long[21];

    public Disciple(Player master) {
        this.master = master;
        this.wear = new Item3[24];
        this.skill_point = new byte[21];
        this.skills = new Skill[0];
    }

    public static String getTodayString() {
        return new java.text.SimpleDateFormat("yyyyMMdd").format(new java.util.Date());
    }

    public static Item3 createStarterItem(short id, byte clazz, byte type, short level, short icon, byte color, byte part, Option... options) {
        Item3 it = new Item3();
        it.id = id;
        try {
            it.name = ItemTemplate3.item.get(id).getName() + " [Khóa]";
        } catch (Exception e) {
            it.name = "Trang bị tân thủ [Khóa]";
        }
        it.clazz = clazz;
        it.type = type;
        it.level = level;
        it.icon = icon;
        it.color = color;
        it.part = part;
        it.islock = true;
        it.tier = 0;
        it.op = new ArrayList<>();
        if (options != null) {
            for (Option op : options) {
                it.op.add(op);
            }
        }
        it.time_use = 0;
        return it;
    }

    public static Disciple createNew(Player master, byte clazz) {
        Disciple d = new Disciple(master);
        // Character ID riêng, distinct from master.id
        d.id = -20000 - Math.abs(master.id);
        d.name = master.name + "_dt";
        d.clazz = clazz;
        d.level = 10;
        d.exp = 0;
        d.head = master.head;
        d.eye = master.eye;
        // Random initial hair per class: Male (Warrior/Assassin/Archer: 0, 1, 3) = {0, 1}, Female (Mage: 2) = {2, 3}
        if (clazz == CLAZZ_MAGE) {
            d.hair = (byte) (Util.random(2, 4)); // returns 2 or 3 (Female)
        } else {
            d.hair = (byte) (Util.random(0, 2)); // returns 0 or 1 (Male: Warrior, Assassin, Archer)
        }
        d.wear = new Item3[24];

        // Setup starter equipment matching new character flow at Level 10
        switch (clazz) {
            case CLAZZ_WARRIOR: {
                d.wear[0] = createStarterItem((short) 0, (byte) 0, (byte) 8, (short) 1, (short) 0, (byte) 0, (byte) 0, new Option(0, 54), new Option(40, 120));
                d.wear[1] = createStarterItem((short) 80, (byte) 0, (byte) 0, (short) 1, (short) 16, (byte) 0, (byte) 0, new Option(14, 52), new Option(16, 100));
                d.wear[7] = createStarterItem((short) 120, (byte) 0, (byte) 1, (short) 1, (short) 24, (byte) 0, (byte) 0, new Option(14, 18), new Option(25, 3));
                break;
            }
            case CLAZZ_ASSASSIN: {
                d.wear[0] = createStarterItem((short) 5, (byte) 1, (byte) 9, (short) 1, (short) 1, (byte) 0, (byte) 0, new Option(0, 54), new Option(40, 120));
                d.wear[1] = createStarterItem((short) 105, (byte) 1, (byte) 0, (short) 1, (short) 21, (byte) 0, (byte) 1, new Option(14, 52), new Option(20, 100));
                d.wear[7] = createStarterItem((short) 145, (byte) 1, (byte) 1, (short) 1, (short) 29, (byte) 0, (byte) 1, new Option(14, 18), new Option(24, 3));
                break;
            }
            case CLAZZ_MAGE: {
                d.wear[0] = createStarterItem((short) 10, (byte) 2, (byte) 11, (short) 1, (short) 2, (byte) 0, (byte) 0, new Option(0, 50), new Option(40, 120));
                d.wear[1] = createStarterItem((short) 90, (byte) 2, (byte) 0, (short) 1, (short) 18, (byte) 0, (byte) 2, new Option(14, 42), new Option(16, 200));
                d.wear[6] = createStarterItem((short) 50, (byte) 2, (byte) 2, (short) 1, (short) 10, (byte) 0, (byte) 2, new Option(7, 200), new Option(14, 12));
                d.wear[7] = createStarterItem((short) 130, (byte) 2, (byte) 1, (short) 1, (short) 26, (byte) 0, (byte) 2, new Option(14, 12), new Option(26, 4));
                break;
            }
            case CLAZZ_ARCHER:
            default: {
                d.wear[0] = createStarterItem((short) 15, (byte) 3, (byte) 10, (short) 1, (short) 3, (byte) 0, (byte) 0, new Option(0, 50), new Option(40, 120));
                d.wear[1] = createStarterItem((short) 95, (byte) 3, (byte) 0, (short) 1, (short) 19, (byte) 0, (byte) 3, new Option(14, 44), new Option(16, 200));
                d.wear[6] = createStarterItem((short) 55, (byte) 3, (byte) 2, (short) 1, (short) 11, (byte) 0, (byte) 3, new Option(7, 200), new Option(14, 14));
                d.wear[7] = createStarterItem((short) 135, (byte) 3, (byte) 1, (short) 1, (short) 27, (byte) 0, (byte) 3, new Option(14, 14), new Option(24, 4));
                break;
            }
        }

        // Stats initialized for Lv 10 Character
        d.point1 = 14;
        d.point2 = 14;
        d.point3 = 14;
        d.point4 = 14;
        d.tiemnang = 40;
        d.kynang = 10;
        d.skill_point = new byte[21];
        d.skill_point[0] = 1;
        d.training_time_used_today = 0;
        d.training_ticket_bought_today = false;
        d.training_ticket_used_today = false;
        d.last_train_date = getTodayString();
        d.summon_cooldown_until = 0;
        d.state = STATE_NORMAL;
        try {
            d.loadSkills();
        } catch (Exception e) {
            // Ignore during standalone test
        }
        d.hp = d.get_max_hp();
        d.mp = d.get_max_mp();
        return d;
    }

    public void checkDailyReset() {
        String today = getTodayString();
        if (!today.equals(this.last_train_date)) {
            this.training_time_used_today = 0;
            this.training_ticket_bought_today = false;
            this.training_ticket_used_today = false;
            this.last_train_date = today;
        }
    }

    public int getMaxTrainingTimeSeconds() {
        checkDailyReset();
        return MAX_FREE_TRAIN_TIME_SECONDS + (training_ticket_used_today ? EXTRA_TRAIN_TICKET_SECONDS : 0);
    }

    public int getRemainingTrainingTimeSeconds() {
        checkDailyReset();
        int max = getMaxTrainingTimeSeconds();
        int used = training_time_used_today;
        if (state == STATE_TRAINING && current_train_session_start > 0) {
            used += (int) ((System.currentTimeMillis() - current_train_session_start) / 1000L);
        }
        return Math.max(0, max - used);
    }

    public void startTraining() {
        checkDailyReset();
        this.state = STATE_TRAINING;
        this.current_train_session_start = System.currentTimeMillis();
    }

    public void stopTraining() {
        if (this.state == STATE_TRAINING) {
            if (this.current_train_session_start > 0) {
                int elapsed = (int) ((System.currentTimeMillis() - this.current_train_session_start) / 1000L);
                this.training_time_used_today += elapsed;
                this.current_train_session_start = 0;
            }
            this.state = STATE_NORMAL;
        }
    }

    public boolean canTrain() {
        return getRemainingTrainingTimeSeconds() > 0;
    }

    public boolean canSummon() {
        return System.currentTimeMillis() >= summon_cooldown_until;
    }

    public long getSummonCooldownRemainingSeconds() {
        long rem = (summon_cooldown_until - System.currentTimeMillis()) / 1000L;
        return Math.max(0, rem);
    }

    public void onSummonLost() {
        System.out.println("[DISCIPLE] ON_SUMMON_LOST id=" + this.id + " reason=state_change cooldown=10min");
        this.state = STATE_NORMAL;
        this.summon_cooldown_until = System.currentTimeMillis() + SUMMON_COOLDOWN_MS;
        this.summon_map_id = -1;
        this.summon_zone_id = -1;
        this.target_mob = null;
    }

    public void loadSkills() throws IOException {
        ByteArrayInputStream bais = null;
        DataInputStream dis = null;
        try {
            switch (clazz) {
                case 0:
                    bais = new ByteArrayInputStream(Manager.gI().msg_29_chienbinh);
                    break;
                case 1:
                    bais = new ByteArrayInputStream(Manager.gI().msg_29_satthu);
                    break;
                case 2:
                    bais = new ByteArrayInputStream(Manager.gI().msg_29_phapsu);
                    break;
                case 3:
                default:
                    bais = new ByteArrayInputStream(Manager.gI().msg_29_xathu);
                    break;
            }
            dis = new DataInputStream(bais);
            int size = dis.readByte();
            skills = new Skill[size];
            for (int i = 0; i < size; i++) {
                Skill skill = new Skill();
                skill.id = dis.readByte();
                skill.iconid = dis.readByte();
                skill.name = dis.readUTF();
                skill.type = dis.readByte();
                skill.range = dis.readShort();
                skill.detail = dis.readUTF();
                skill.typeBuff = dis.readByte();
                skill.subEff = dis.readByte();
                byte b2 = dis.readByte();
                skill.mLvSkill = new LvSkill[(int) b2];
                for (int j = 0; j < (int) b2; j++) {
                    skill.mLvSkill[j] = new LvSkill();
                    skill.mLvSkill[j].mpLost = dis.readShort();
                    skill.mLvSkill[j].LvRe = dis.readShort();
                    skill.mLvSkill[j].delay = dis.readInt();
                    skill.mLvSkill[j].timeBuff = dis.readInt();
                    skill.mLvSkill[j].per_Sub_Eff = dis.readByte();
                    skill.mLvSkill[j].time_Sub_Eff = dis.readShort();
                    skill.mLvSkill[j].plus_Hp = dis.readShort();
                    skill.mLvSkill[j].plus_Mp = dis.readShort();
                    byte b3 = dis.readByte();
                    skill.mLvSkill[j].minfo = new Option[(int) b3];
                    for (int k = 0; k < (int) b3; k++) {
                        skill.mLvSkill[j].minfo[k] = new Option(dis.readUnsignedByte(), dis.readInt());
                    }
                    skill.mLvSkill[j].nTarget = dis.readByte();
                    skill.mLvSkill[j].range_lan = dis.readShort();
                }
                skill.performDur = dis.readShort();
                skill.typePaint = dis.readByte();
                skills[skill.id] = skill;
            }
        } finally {
            if (dis != null) dis.close();
            if (bais != null) bais.close();
        }
    }

    public static String getClassName(byte clazz) {
        switch (clazz) {
            case 0: return "Chiến Binh";
            case 1: return "Sát Thủ";
            case 2: return "Pháp Sư";
            case 3: return "Xạ Thủ";
            default: return "Chiến Binh";
        }
    }

    // ================== STATS & COMBAT CALCULATION ==================

    public int total_item_param(int id_option) {
        int total = 0;
        if (wear != null) {
            for (Item3 it : wear) {
                if (it != null && it.op != null) {
                    for (Option op : it.op) {
                        if (op.id == id_option) {
                            total += op.getParam(it.tier);
                        }
                    }
                }
            }
        }
        return total;
    }

    public int get_max_hp() {
        return 500 + point2 * 10 + total_item_param(14) + (level * 20);
    }

    public int get_max_mp() {
        return 500 + point3 * 10 + total_item_param(15) + (level * 20);
    }

    public int get_dame_physical() {
        long dame = total_item_param(0);
        switch (clazz) {
            case CLAZZ_WARRIOR:
            case CLAZZ_ASSASSIN:
                dame += point1 * 4;
                break;
            case CLAZZ_MAGE:
            case CLAZZ_ARCHER:
                dame += point4 * 4;
                break;
        }
        return (int) Math.min(2_000_000_000L, Math.max(1L, dame));
    }

    public int get_dame_prop(int type) {
        int dprop = 0;
        switch (type) {
            case 1: // Ice
                dprop = total_item_param(8) + (clazz == CLAZZ_ASSASSIN ? point1 * 3 : 0);
                break;
            case 2: // Fire
                dprop = total_item_param(9) + (clazz == CLAZZ_WARRIOR ? point1 * 3 : 0);
                break;
            case 3: // Elec
                dprop = total_item_param(10) + (clazz == CLAZZ_ARCHER ? point4 * 3 : 0);
                break;
            case 4: // Poison
                dprop = total_item_param(11) + (clazz == CLAZZ_MAGE ? point4 * 3 : 0);
                break;
        }
        return dprop;
    }

    public int get_def() {
        return total_item_param(1) + point2 * 2;
    }

    public int get_crit() {
        return total_item_param(27);
    }

    public int get_pierce() {
        return total_item_param(28);
    }

    public short get_id_weapon() {
        if (wear != null && wear[17] != null) {
            try {
                return (short) (ItemTemplate3.item.get(wear[17].id).getPart() + 41);
            } catch (Exception e) {
                return -1;
            }
        }
        return -1;
    }

    public short get_id_phiphong() {
        if (wear != null && wear[15] != null) {
            try {
                return (short) (ItemTemplate3.item.get(wear[15].id).getPart() + 41);
            } catch (Exception e) {
                return -1;
            }
        }
        return -1;
    }

    public short get_id_wing() {
        if (wear != null && wear[14] != null) {
            switch (wear[14].id) {
                case 4638:
                case 4639:
                case 4640:
                case 4641:
                case 4642:
                case 4643:
                case 4644:
                case 4645:
                case 4646:
                case 4647:
                case 4648: {
                    break;
                }
                case 4707: {
                    return 75;
                }
                case 4712: {
                    return 82;
                }
                case 4713: {
                    return 83;
                }
            }
        }
        return -1;
    }

    public short get_id_mat_na() {
        if (wear != null && wear[13] != null) {
            try {
                return (short) (ItemTemplate3.item.get(wear[13].id).getPart() + 41);
            } catch (Exception e) {
                return -1;
            }
        }
        return -1;
    }

    public short get_id_hair() {
        if (wear != null && wear[16] != null) {
            try {
                return (short) (ItemTemplate3.item.get(wear[16].id).getPart() + 41);
            } catch (Exception e) {
                return -1;
            }
        }
        return -1;
    }

    public byte[] get_fashion() {
        return Part_fashion.get_part_from_wear(this.wear);
    }

    // ================== NETWORK PACKET SERIALIZATION ==================

    /**
     * Send Player Entity information for Disciple (Message 5)
     */
    public void send_in4(Map map, Player receiver) throws IOException {
        if (receiver == null || receiver.conn == null) return;

        int dem = 0;
        for (int i = 0; i < this.wear.length; i++) {
            if (i != 0 && i != 1 && i != 6 && i != 7 && i != 10) continue;
            if (this.wear[i] != null) dem++;
        }

        System.out.println("[DISCIPLE] SEND_IN4 id=" + this.id + " receiver=" + receiver.name + " pos=" + this.summon_x + "," + this.summon_y + " hp=" + this.hp + "/" + this.get_max_hp() + " level=" + this.level + " name=" + this.name + " wear_count=" + dem + " weapon_id=" + get_id_weapon());

        Message m = new Message(5);
        m.writer().writeShort(this.id);
        m.writer().writeUTF(this.name);
        m.writer().writeShort(this.summon_x);
        m.writer().writeShort(this.summon_y);
        m.writer().writeByte(this.clazz);
        m.writer().writeByte(-1);
        m.writer().writeByte(this.head);
        m.writer().writeByte(this.eye);
        m.writer().writeByte(this.hair);
        m.writer().writeShort(this.level);
        m.writer().writeInt((int) Math.min(this.hp > 0 ? this.hp : get_max_hp(), Integer.MAX_VALUE));
        m.writer().writeInt((int) Math.min(this.get_max_hp(), Integer.MAX_VALUE));
        m.writer().writeByte(this.master != null ? this.master.typepk : 0);
        m.writer().writeShort(this.master != null ? this.master.pointpk : 0);
        m.writer().writeByte(dem);

        // Write equipped items (body parts 0,1,6,7,10)
        for (int i = 0; i < this.wear.length; i++) {
            if (i != 0 && i != 1 && i != 6 && i != 7 && i != 10) continue;
            Item3 temp = this.wear[i];
            if (temp != null) {
                m.writer().writeByte(temp.type);
                if (i == 10 && this.wear[14] != null && (this.wear[14].id >= 4638 && this.wear[14].id <= 4648)) {
                    m.writer().writeByte(this.wear[14].part);
                } else {
                    m.writer().writeByte(temp.part);
                }
                m.writer().writeByte(3);
                m.writer().writeShort(-1);
                m.writer().writeShort(-1);
                m.writer().writeShort(-1);
                m.writer().writeShort(-1);
            }
        }

        m.writer().writeShort(-1); // clan = no clan
        m.writer().writeByte(-1);  // pet = no pet

        // Fashion: ALWAYS send 7 entries (1 byte each). Client creates dF[7] array.
        // Priority: Fashion equipment (wear[11]) > Normal equipment (wear[0, 1, 6, 7])
        byte[] fPart = get_fashion();
        m.writer().writeByte(fPart.length);
        for (int i = 0; i < fPart.length; i++) {
            m.writer().writeByte(fPart[i]);
        }

        m.writer().writeShort(-1); // id_henshin
        m.writer().writeByte(-1);  // type_use_mount = no mount
        m.writer().writeBoolean(false);
        m.writer().writeByte(1);
        m.writer().writeByte(0);
        m.writer().writeShort(get_id_mat_na()); // mat na
        m.writer().writeByte(1);   // paint mat na
        m.writer().writeShort(get_id_phiphong()); // phi phong
        m.writer().writeShort(get_id_weapon()); // weapon fashion
        m.writer().writeShort(-1); // id_horse
        m.writer().writeShort(get_id_hair()); // hair fashion
        m.writer().writeShort(get_id_wing()); // wing fashion
        m.writer().writeShort(-1); // id_name
        m.writer().writeShort(-1); // body
        m.writer().writeShort(-1); // leg
        m.writer().writeShort(-1); // bienhinh
        System.out.println("[DISCIPLE_FASHION] discipleId=" + this.id + " name=" + this.name
                + " head=" + this.head + " eye=" + this.eye + " hair=" + this.hair
                + " fashion=" + java.util.Arrays.toString(fPart)
                + " mat_na=" + get_id_mat_na() + " phi_phong=" + get_id_phiphong()
                + " weapon=" + get_id_weapon() + " hairFashion=" + get_id_hair()
                + " wing=" + get_id_wing());

        receiver.conn.addmsg(m);
        m.cleanup();
    }

    public void send_move(Map map) throws IOException {
        System.out.println("[DISCIPLE] SEND_MOVE id=" + this.id + " x=" + this.summon_x + " y=" + this.summon_y + " players=" + map.players.size());
        Message m22 = new Message(4);
        m22.writer().writeByte(0); // type 0 = player
        m22.writer().writeShort(0);
        m22.writer().writeShort(this.id);
        m22.writer().writeShort(this.summon_x);
        m22.writer().writeShort(this.summon_y);
        m22.writer().writeByte(-1);
        for (int i = 0; i < map.players.size(); i++) {
            Player p0 = map.players.get(i);
            if (p0 != null && p0.conn != null) {
                p0.conn.addmsg(m22);
            }
        }
        m22.cleanup();
    }

    public void send_die(Map map) throws IOException {
        System.out.println("[DISCIPLE] SEND_DIE(8) id=" + this.id + " map=" + map.map_id + ":" + map.zone_id);
        Message m8 = new Message(8);
        m8.writer().writeShort(this.id);
        for (int i = 0; i < map.players.size(); i++) {
            Player p0 = map.players.get(i);
            if (p0 != null && p0.conn != null) {
                p0.conn.addmsg(m8);
            }
        }
        m8.cleanup();
    }

    // ================== CONSTANTS ==================
    private static final int MAX_ASSIST_DISTANCE = 300;
    private static final long ATTACK_COOLDOWN = 1000L;
    private static final long SKILL_COOLDOWN = 1500L;
    private static final double HP_POTION_THRESHOLD = 0.5;
    private static final double MP_POTION_THRESHOLD = 0.4;
    private static final long POTION_COOLDOWN = 2500L;

    public int getAttackRange() {
        int skillIdx = selectBestSkill();
        if (skillIdx >= 0 && skills != null && skillIdx < skills.length && skills[skillIdx] != null) {
            short r = skills[skillIdx].range;
            if (r > 60) return Math.min((int) r, 220);
        }
        if (clazz == CLAZZ_MAGE || clazz == CLAZZ_ARCHER) {
            return 180;
        }
        return 100;
    }

    // ================== AI FOLLOW & COMBAT ==================

    public static boolean isMapRestrictedForDisciple(Map map) {
        if (map == null) return true;
        byte id = (byte) map.map_id;
        return id == 2 || id == 36 || id == 102 || Map.is_map_chien_truong(id);
    }

    public void onMasterMove(Map map) {
        if (state != STATE_SUMMONED || master == null || master.isdie) return;
        if (DEBUG_AI_OFF) return;

        // If disciple is actively fighting target in attack range, don't pull away on every step
        if (target_mob != null && !target_mob.isdie && target_mob.hp > 0) {
            double distTarget = Math.sqrt(Math.pow(target_mob.x - summon_x, 2) + Math.pow(target_mob.y - summon_y, 2));
            if (distTarget <= getAttackRange()) {
                return;
            }
        }
        if (target_mo != null && target_mo.hp > 0) {
            double distTarget = Math.sqrt(Math.pow(target_mo.x - summon_x, 2) + Math.pow(target_mo.y - summon_y, 2));
            if (distTarget <= getAttackRange()) {
                return;
            }
        }

        int dx = master.x - summon_x;
        int dy = master.y - summon_y;
        double distMaster = Math.sqrt(dx * dx + dy * dy);

        if (distMaster > 90) {
            long now = System.currentTimeMillis();
            if (now > time_move) {
                time_move = now + 350L;
                int step = 75;
                if (distMaster > 350) {
                    this.summon_x = (short) (master.x + Util.random(-25, 25));
                    this.summon_y = (short) (master.y + Util.random(-15, 15));
                } else {
                    this.summon_x = (short) (summon_x + (int) (step * (dx / distMaster)));
                    this.summon_y = (short) (summon_y + (int) (step * (dy / distMaster)));
                }
                try {
                    send_move(map);
                } catch (Exception e) {}
            }
        }
    }

    public void updateAI(Map map) {
        if (state != STATE_SUMMONED || master == null || master.isdie) {
            return;
        }
        if (DEBUG_AI_OFF) return;

        // Check if master moved to a different map
        if (master.map != null && !map.equals(master.map)) {
            System.out.println("[DISCIPLE] MAP TRANSITION old_map=" + map.map_id + ":" + map.zone_id + " new_map=" + master.map.map_id + ":" + master.map.zone_id);
            if (isMapRestrictedForDisciple(master.map)) {
                onSummonLost();
                try {
                    send_die(map);
                    Service.send_notice_box(master.conn, "Đệ Tử bị mất! Cần 10 phút sau mới có thể gọi lại.");
                } catch (Exception e) {}
                return;
            }
            this.summon_map_id = master.map.map_id;
            this.summon_zone_id = master.map.zone_id;
            this.summon_x = master.x;
            this.summon_y = master.y;
            this.target_mob = null;
            this.lastFarmMonsterType = -1;
            try {
                send_die(map);
                for (Player p : master.map.players) {
                    if (p != null && p.conn != null) {
                        Message mSpawn = new Message(4);
                        mSpawn.writer().writeByte(0);
                        mSpawn.writer().writeShort(0);
                        mSpawn.writer().writeShort(this.id);
                        mSpawn.writer().writeShort(this.summon_x);
                        mSpawn.writer().writeShort(this.summon_y);
                        mSpawn.writer().writeByte(-1);
                        p.conn.addmsg(mSpawn);
                        mSpawn.cleanup();
                        send_in4(master.map, p);
                    }
                }
            } catch (Exception e) {}
            return;
        }

        long now = System.currentTimeMillis();

        // 1. Auto-use potions from master's inventory
        tryAutoPotion(map);

        // 2. Validate current targets
        if (target_mo != null && (target_mo.hp <= 0 || target_mo.map == null || !target_mo.map.equals(map))) {
            target_mo = null;
        }
        if (target_mob != null && (target_mob.isdie || target_mob.hp <= 0)) {
            short deadType = target_mob.template != null ? target_mob.template.mob_id : -1;
            System.out.println("[DISCIPLE_COMBAT] target=" + (target_mob.template != null ? target_mob.template.name : "null") + " action=TARGET_DEAD type=" + deadType);
            lastFarmMonsterType = deadType;
            target_mob = null;
        }

        // 3. Distance to Master
        int dx = master.x - summon_x;
        int dy = master.y - summon_y;
        double distMaster = Math.sqrt(dx * dx + dy * dy);

        // If master is extremely far, teleport near master
        if (distMaster > 400) {
            this.summon_x = (short) (master.x + Util.random(-25, 25));
            this.summon_y = (short) (master.y + Util.random(-15, 15));
            this.target_mob = null;
            this.target_mo = null;
            try {
                send_move(map);
            } catch (Exception e) {}
            return;
        }

        // 4. Check Mỏ tài nguyên target (Master attacks Mỏ)
        if (master.currentTargetMo != null && master.currentTargetMo.hp > 0 && master.currentTargetMo.map != null && master.currentTargetMo.map.equals(map)) {
            target_mo = master.currentTargetMo;
            target_mob = null;
        }

        int attackRange = getAttackRange();

        if (target_mo != null) {
            int mdx = target_mo.x - summon_x;
            int mdy = target_mo.y - summon_y;
            double distTarget = Math.sqrt(mdx * mdx + mdy * mdy);

            if (distTarget > attackRange) {
                if (now > time_move) {
                    time_move = now + 350L;
                    int step = 80;
                    this.summon_x = (short) (summon_x + (int) (step * (mdx / distTarget)));
                    this.summon_y = (short) (summon_y + (int) (step * (mdy / distTarget)));
                    try {
                        send_move(map);
                    } catch (Exception e) {}
                }
            } else {
                if (now > time_atk) {
                    time_atk = now + ATTACK_COOLDOWN;
                    try {
                        executeAttackMo(map, target_mo);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            return;
        }

        // 5. Target Selection: Master target has ABSOLUTE HIGHEST priority!
        // If Master attacks a valid target, Disciple MUST sync to it immediately (even if currently fighting another mob)
        if (master.currentTarget != null && !master.currentTarget.isdie && master.currentTarget.hp > 0) {
            int tdx = master.currentTarget.x - master.x;
            int tdy = master.currentTarget.y - master.y;
            if (Math.sqrt(tdx * tdx + tdy * tdy) <= MAX_ASSIST_DISTANCE) {
                if (target_mob != master.currentTarget) {
                    System.out.println("[DISCIPLE_COMBAT] oldTarget=" + (target_mob != null && target_mob.template != null ? target_mob.template.name : "none") + " newTarget=" + (master.currentTarget.template != null ? master.currentTarget.template.name : "null") + " action=SYNC_MASTER_TARGET");
                    target_mob = master.currentTarget;
                }
            }
        }

        // If no master target, pick from same monster type or closest monster
        if (target_mob == null) {
            selectTarget(map);
        }

        // 5. Validate target still alive and in range
        if (target_mob != null) {
            if (target_mob.isdie || target_mob.hp <= 0) {
                System.out.println("[DISCIPLE_COMBAT] action=TARGET_INVALID reason=dead_or_zero_hp");
                lastFarmMonsterType = target_mob.template != null ? target_mob.template.mob_id : -1;
                target_mob = null;
                selectTarget(map);
            }
        }

        // 6. Check assist distance
        if (target_mob != null) {
            int tdx = target_mob.x - master.x;
            int tdy = target_mob.y - master.y;
            double distToMaster = Math.sqrt(tdx * tdx + tdy * tdy);
            if (distToMaster > MAX_ASSIST_DISTANCE) {
                System.out.println("[DISCIPLE_COMBAT] action=DROP_TARGET reason=too_far_from_master dist=" + (int)distToMaster);
                target_mob = null;
            }
        }

        // 7. Combat or Follow
        if (target_mob != null) {
            int mdx = target_mob.x - summon_x;
            int mdy = target_mob.y - summon_y;
            double distTarget = Math.sqrt(mdx * mdx + mdy * mdy);

            if (distTarget > attackRange) {
                // Move towards target
                if (now > time_move) {
                    time_move = now + 350L;
                    int step = 80;
                    this.summon_x = (short) (summon_x + (int) (step * (mdx / distTarget)));
                    this.summon_y = (short) (summon_y + (int) (step * (mdy / distTarget)));
                    try {
                        send_move(map);
                    } catch (Exception e) {}
                }
            } else {
                // In attack range -> Attack!
                if (now > time_atk) {
                    time_atk = now + ATTACK_COOLDOWN;
                    try {
                        executeAttackMob(map, target_mob);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            return;
        }

        // 8. Follow Master (when no combat target)
        if (distMaster > 80) {
            if (now > time_move) {
                time_move = now + 500L;
                int step = 45;
                this.summon_x = (short) (summon_x + (int) (step * (dx / distMaster)));
                this.summon_y = (short) (summon_y + (int) (step * (dy / distMaster)));
                try {
                    send_move(map);
                } catch (Exception e) {}
            }
        } else {
            if (now > time_wander) {
                time_wander = now + Util.random(2500, 4500);
                short targetWanderX = (short) (master.x + Util.random(-40, 40));
                short targetWanderY = (short) (master.y + Util.random(-25, 25));
                if (Math.abs(targetWanderX - summon_x) > 10 || Math.abs(targetWanderY - summon_y) > 10) {
                    this.summon_x = targetWanderX;
                    this.summon_y = targetWanderY;
                    try {
                        send_move(map);
                    } catch (Exception e) {}
                }
            }
        }
    }

    private void selectTarget(Map map) {
        // Priority 1: Master's current target
        if (master.currentTarget != null && !master.currentTarget.isdie && master.currentTarget.hp > 0) {
            Mob_in_map m = master.currentTarget;
            int tdx = m.x - master.x;
            int tdy = m.y - master.y;
            if (Math.sqrt(tdx * tdx + tdy * tdy) <= MAX_ASSIST_DISTANCE) {
                target_mob = m;
                System.out.println("[DISCIPLE_COMBAT] masterTarget=" + (m.template != null ? m.template.name : "null") + " action=SYNC_MASTER_TARGET");
                return;
            }
        }

        // Priority 2: Same monster type as last killed
        if (lastFarmMonsterType >= 0) {
            double minDist = Double.MAX_VALUE;
            Mob_in_map best = null;
            for (Mob_in_map mob : map.mobs) {
                if (mob.isdie || mob.hp <= 0) continue;
                if (mob.template == null || mob.template.mob_id != lastFarmMonsterType) continue;
                int tdx = mob.x - master.x;
                int tdy = mob.y - master.y;
                double distMasterMob = Math.sqrt(tdx * tdx + tdy * tdy);
                if (distMasterMob > MAX_ASSIST_DISTANCE) continue;
                int mdx = mob.x - summon_x;
                int mdy = mob.y - summon_y;
                double distSelf = Math.sqrt(mdx * mdx + mdy * mdy);
                if (distSelf < minDist) {
                    minDist = distSelf;
                    best = mob;
                }
            }
            if (best != null) {
                target_mob = best;
                System.out.println("[DISCIPLE_COMBAT] lastFarmType=" + lastFarmMonsterType + " nextTarget=" + (best.template != null ? best.template.name : "null") + " action=FARM_SAME_TYPE");
                return;
            }
        }

        // Priority 3: Nearest valid monster near master
        double minMobDist = 150;
        for (Mob_in_map mob : map.mobs) {
            if (mob.isdie || mob.hp <= 0) continue;
            int mdx = mob.x - master.x;
            int mdy = mob.y - master.y;
            double mdist = Math.sqrt(mdx * mdx + mdy * mdy);
            if (mdist < minMobDist) {
                minMobDist = mdist;
                target_mob = mob;
            }
        }
    }

    // ================== POTION USAGE ==================

    private void tryAutoPotion(Map map) {
        if (master == null || master.conn == null) return;
        long now = System.currentTimeMillis();

        // HP Potion
        int maxHp = get_max_hp();
        if (maxHp > 0 && (double) hp / maxHp <= HP_POTION_THRESHOLD && hp > 0 && hp < maxHp) {
            if (now > time_use_potion_hp) {
                Item47 potion = findPotionInMasterInventory(0);
                if (potion != null) {
                    int healAmount = ItemTemplate4.item.get(potion.id).getValue();
                    if (healAmount > 0) {
                        time_use_potion_hp = now + POTION_COOLDOWN;
                        master.item.remove(4, potion.id, 1);
                        long canAdd = 2_000_000_000L - hp;
                        int actualHeal = (int) Math.min(healAmount, canAdd);
                        hp += actualHeal;
                        if (hp > maxHp) hp = maxHp;
                        System.out.println("[DISCIPLE_POTION] item=HP_POTION_" + potion.id + " itemType=HP hp=" + hp + "/" + maxHp + " heal=" + actualHeal + " action=USE_POTION");
                    }
                }
            }
        }

        // MP Potion
        int maxMp = get_max_mp();
        if (maxMp > 0 && (double) mp / maxMp <= MP_POTION_THRESHOLD && mp > 0 && mp < maxMp) {
            if (now > time_use_potion_mp) {
                Item47 potion = findPotionInMasterInventory(1);
                if (potion != null) {
                    int healAmount = ItemTemplate4.item.get(potion.id).getValue();
                    if (healAmount > 0) {
                        time_use_potion_mp = now + POTION_COOLDOWN;
                        master.item.remove(4, potion.id, 1);
                        long canAdd = 2_000_000_000L - mp;
                        int actualHeal = (int) Math.min(healAmount, canAdd);
                        mp += actualHeal;
                        if (mp > maxMp) mp = maxMp;
                        System.out.println("[DISCIPLE_POTION] item=MP_POTION_" + potion.id + " itemType=MP mp=" + mp + "/" + maxMp + " heal=" + actualHeal + " action=USE_POTION");
                    }
                }
            }
        }
    }

    private Item47 findPotionInMasterInventory(int potionType) {
        if (master == null || master.item == null || master.item.bag47 == null) return null;
        Item47 bestPotion = null;
        int bestValue = 0;
        for (Item47 item : master.item.bag47) {
            if (item == null || item.quantity <= 0) continue;
            if (item.category != 4) continue;
            ItemTemplate4 tpl = ItemTemplate4.item.get(item.id);
            if (tpl == null) continue;
            if (tpl.getType() != potionType) continue;
            if (tpl.getValue() <= 0) continue;
            if (bestPotion == null || tpl.getValue() < bestValue) {
                bestPotion = item;
                bestValue = tpl.getValue();
            }
        }
        return bestPotion;
    }

    // ================== ATTACK ==================

    private int selectBestSkill() {
        if (skills == null || skills.length == 0) {
            try {
                loadSkills();
            } catch (Exception e) {}
        }
        if (skills == null || skills.length == 0 || skill_point == null) return 0;
        long now = System.currentTimeMillis();

        // 1. Try to find the strongest ACTIVE COMBAT SKILL (i > 0)
        for (int i = skill_point.length - 1; i >= 1; i--) {
            if (i >= skills.length) continue;
            if (skill_point[i] < 1) continue;
            Skill sk = skills[i];
            if (sk == null || sk.mLvSkill == null) continue;
            if (sk.type == 2) continue; // skip passive skills

            int skLv = skill_point[i] - 1;
            if (skLv < 0 || skLv >= sk.mLvSkill.length) continue;
            LvSkill lvInfo = sk.mLvSkill[skLv];
            if (lvInfo == null) continue;
            if (lvInfo.LvRe > (level + 1)) continue;
            if (mp < lvInfo.mpLost) continue;
            if (now < time_delay_skill[i]) continue;

            return i; // Found ready active skill!
        }

        // 2. Fallback to basic attack (Skill 0)
        return 0;
    }

    private void executeAttackMob(Map map, Mob_in_map mob) throws IOException {
        if (mob.isdie || mob.hp <= 0) return;

        int skillIdx = selectBestSkill();
        byte skillIndex = (byte) skillIdx;
        boolean usedSkill = (skillIdx > 0 && skills != null && skillIdx < skills.length && skills[skillIdx] != null);

        long dame;
        if (usedSkill) {
            LvSkill lvInfo = skills[skillIdx].mLvSkill[skill_point[skillIdx] - 1];
            mp -= lvInfo.mpLost;
            time_delay_skill[skillIdx] = System.currentTimeMillis() + ((lvInfo.delay * 199) / 200);
            dame = get_dame_physical() + get_dame_prop(1) + get_dame_prop(2) + get_dame_prop(3) + get_dame_prop(4);
            System.out.println("[DISCIPLE_COMBAT] CAST_SKILL id=" + skillIdx + " name=" + skills[skillIdx].name + " mp_lost=" + lvInfo.mpLost + " delay=" + lvInfo.delay);
        } else {
            dame = get_dame_physical() + get_dame_prop(1) + get_dame_prop(2) + get_dame_prop(3) + get_dame_prop(4);
        }

        dame -= ((dame * Util.random(10)) / 100);

        int cr = get_crit();
        boolean crit = cr > Util.random(0, 15000);
        if (crit) {
            dame *= 2;
        }

        int pier = get_pierce();
        boolean pierce = pier > Util.random(0, 15000);
        if (!pierce) {
            long dameresist = (mob.level * mob.level / 2);
            if (mob.is_boss) dameresist *= 2;
            dame -= dameresist;
        }
        if (dame <= 0) dame = 1;
        if (dame > 2_000_000_000) dame = 2_000_000_000;

        if (mob.is_boss && Math.abs(mob.level - this.level) > 5) {
            dame = 0;
        }

        mob.hp -= dame;
        if (mob.hp < 0) mob.hp = 0;

        // Message(9) is PvE Attack (Player/Disciple attacks Mob)
        Message m = new Message(9);
        m.writer().writeShort(this.id); // attacker ID
        m.writer().writeByte(skillIndex);
        m.writer().writeByte(1); // 1 target
        m.writer().writeShort(mob.index);
        m.writer().writeInt((int) dame); // dame
        m.writer().writeInt(mob.hp); // hp mob after hit
        if (dame > 0 && crit) {
            m.writer().writeByte(1); // 1 effect
            m.writer().writeByte(4); // 4: crit
            m.writer().writeInt((int) dame);
        } else if (dame > 0 && pierce) {
            m.writer().writeByte(1); // 1 effect
            m.writer().writeByte(1); // 1: pierce
            m.writer().writeInt((int) dame);
        } else {
            m.writer().writeByte(0); // 0 effects
        }
        m.writer().writeInt((int) Math.min(this.hp > 0 ? this.hp : get_max_hp(), Integer.MAX_VALUE));
        m.writer().writeInt(this.mp > 0 ? this.mp : get_max_mp());
        m.writer().writeByte(11);
        m.writer().writeInt(0);
        MapService.send_msg_player_inside(map, master, m, true);
        m.cleanup();

        System.out.println("[DISCIPLE_COMBAT] ATTACK_MOB mob=" + (mob.template != null ? mob.template.name : "null") + " index=" + mob.index + " dame=" + dame + " remaining_hp=" + mob.hp + " crit=" + crit + " pierce=" + pierce);

        if (mob.hp <= 0 && !mob.isdie) {
            mob.isdie = true;
            mob.time_back = System.currentTimeMillis() + (Mob_in_map.time_refresh * 1000) - 1000L;
            lastFarmMonsterType = mob.template != null ? mob.template.mob_id : -1;
            target_mob = null;
            System.out.println("[DISCIPLE_COMBAT] target=" + (mob.template != null ? mob.template.name : "null") + " action=TARGET_DEAD type=" + lastFarmMonsterType);
            if (master != null) {
                if (master.currentTarget == mob) {
                    master.currentTarget = null;
                }
                master.update_Exp((mob.level * 100L) / 2, true);
            }
        }
    }

    private void executeAttackMo(Map map, Mob_MoTaiNguyen mo) throws IOException {
        if (mo == null || mo.hp <= 0) return;

        int skillIdx = selectBestSkill();
        byte skillIndex = (byte) skillIdx;
        boolean usedSkill = (skillIdx > 0 && skills != null && skillIdx < skills.length && skills[skillIdx] != null);

        long dame;
        if (usedSkill) {
            LvSkill lvInfo = skills[skillIdx].mLvSkill[skill_point[skillIdx] - 1];
            mp -= lvInfo.mpLost;
            time_delay_skill[skillIdx] = System.currentTimeMillis() + ((lvInfo.delay * 199) / 200);
            dame = get_dame_physical() + get_dame_prop(1) + get_dame_prop(2) + get_dame_prop(3) + get_dame_prop(4);
            System.out.println("[DISCIPLE_COMBAT] CAST_SKILL_MO id=" + skillIdx + " name=" + skills[skillIdx].name + " mp_lost=" + lvInfo.mpLost + " delay=" + lvInfo.delay);
        } else {
            dame = get_dame_physical() + get_dame_prop(1) + get_dame_prop(2) + get_dame_prop(3) + get_dame_prop(4);
        }

        if (mo.nhanban != null) {
            int def_ = (int) ((((long) mo.nhanban.def) * (10_001 - get_pierce())) / 10_000L);
            dame = (int) ((((long) dame) * (50_001 - def_)) / 50_000L);
        }
        if (!mo.is_atk) {
            dame = 1;
        }

        dame -= ((dame * Util.random(10)) / 100);
        if (dame <= 0) dame = 1;
        if (dame > 2_000_000_000) dame = 2_000_000_000;

        mo.hp -= (int) dame;
        if (mo.hp < 0) mo.hp = 0;

        // Send Message(9) PvE attack to map
        Message m = new Message(9);
        m.writer().writeShort(this.id);
        m.writer().writeByte(skillIndex);
        m.writer().writeByte(1);
        m.writer().writeShort(mo.index);
        m.writer().writeInt((int) dame);
        m.writer().writeInt(mo.hp);
        m.writer().writeByte(0); // effect count
        m.writer().writeInt((int) Math.min(this.hp > 0 ? this.hp : get_max_hp(), Integer.MAX_VALUE));
        m.writer().writeInt(this.mp > 0 ? this.mp : get_max_mp());
        m.writer().writeByte(11);
        m.writer().writeInt(0);
        MapService.send_msg_player_inside(map, master, m, true);
        m.cleanup();

        System.out.println("[DISCIPLE_COMBAT] ATTACK_MO name=" + mo.name_monster + " index=" + mo.index + " dame=" + dame + " hp=" + mo.hp);

        if (mo.hp <= 0) {
            target_mo = null;
            if (master != null && master.currentTargetMo == mo) {
                master.currentTargetMo = null;
            }
        }
    }

    // ================== JSON PERSISTENCE ==================

    @SuppressWarnings("unchecked")
    public String toJson() {
        JSONObject obj = new JSONObject();
        obj.put("id", id);
        obj.put("name", name);
        obj.put("clazz", (int) clazz);
        obj.put("level", (int) level);
        obj.put("exp", exp);
        obj.put("head", (int) head);
        obj.put("eye", (int) eye);
        obj.put("hair", (int) hair);
        obj.put("p1", point1);
        obj.put("p2", point2);
        obj.put("p3", point3);
        obj.put("p4", point4);
        obj.put("tiemnang", tiemnang);
        obj.put("kynang", (int) kynang);

        JSONArray jsSkills = new JSONArray();
        for (int i = 0; i < skill_point.length; i++) {
            jsSkills.add(skill_point[i]);
        }
        obj.put("skills", jsSkills);

        JSONArray jsWear = new JSONArray();
        for (int i = 0; i < wear.length; i++) {
            Item3 temp = wear[i];
            if (temp != null) {
                JSONArray jsar2 = new JSONArray();
                jsar2.add(temp.id);
                jsar2.add(temp.clazz);
                jsar2.add(temp.type);
                jsar2.add(temp.level);
                jsar2.add(temp.icon);
                jsar2.add(temp.color);
                jsar2.add(temp.part);
                jsar2.add(temp.tier);
                JSONArray jsar3 = new JSONArray();
                for (int j = 0; j < temp.op.size(); j++) {
                    JSONArray jsar4 = new JSONArray();
                    jsar4.add(temp.op.get(j).id);
                    jsar4.add(temp.op.get(j).getParam(0));
                    jsar3.add(jsar4);
                }
                jsar2.add(jsar3);
                jsar2.add(i);
                jsWear.add(jsar2);
            }
        }
        obj.put("wear", jsWear);

        obj.put("time_used", training_time_used_today);
        obj.put("ticket_bought", training_ticket_bought_today ? 1 : 0);
        obj.put("ticket_used", training_ticket_used_today ? 1 : 0);
        obj.put("last_train_date", last_train_date);
        obj.put("summon_cd", summon_cooldown_until);

        return obj.toJSONString();
    }

    public static Disciple fromJson(Player master, String jsonStr) {
        if (jsonStr == null || jsonStr.trim().isEmpty() || jsonStr.equals("null") || jsonStr.equals("{}")) {
            return null;
        }
        try {
            JSONObject obj = (JSONObject) JSONValue.parse(jsonStr);
            if (obj == null) return null;

            Disciple d = new Disciple(master);
            d.id = -20000 - Math.abs(master.id);
            d.name = obj.get("name") != null ? obj.get("name").toString() : (master.name + "_dt");
            d.clazz = Byte.parseByte(obj.get("clazz").toString());
            d.level = Short.parseShort(obj.get("level").toString());
            d.exp = Long.parseLong(obj.get("exp").toString());
            d.head = Byte.parseByte(obj.get("head").toString());
            d.eye = Byte.parseByte(obj.get("eye").toString());
            d.hair = Byte.parseByte(obj.get("hair").toString());
            d.point1 = Integer.parseInt(obj.get("p1").toString());
            d.point2 = Integer.parseInt(obj.get("p2").toString());
            d.point3 = Integer.parseInt(obj.get("p3").toString());
            d.point4 = Integer.parseInt(obj.get("p4").toString());
            d.tiemnang = Integer.parseInt(obj.get("tiemnang").toString());
            d.kynang = Short.parseShort(obj.get("kynang").toString());

            JSONArray jsSkills = (JSONArray) obj.get("skills");
            if (jsSkills != null) {
                for (int i = 0; i < jsSkills.size() && i < d.skill_point.length; i++) {
                    d.skill_point[i] = Byte.parseByte(jsSkills.get(i).toString());
                }
            }

            JSONArray jsWear = (JSONArray) obj.get("wear");
            if (jsWear != null) {
                for (int i = 0; i < jsWear.size(); i++) {
                    JSONArray jsar2 = (JSONArray) jsWear.get(i);
                    Item3 temp = new Item3();
                    temp.id = Short.parseShort(jsar2.get(0).toString());
                    try {
                        temp.name = ItemTemplate3.item.get(temp.id).getName() + " [Khóa]";
                    } catch (Exception e) {
                        temp.name = "Trang bị [Khóa]";
                    }
                    temp.clazz = Byte.parseByte(jsar2.get(1).toString());
                    temp.type = Byte.parseByte(jsar2.get(2).toString());
                    temp.level = Short.parseShort(jsar2.get(3).toString());
                    temp.icon = Short.parseShort(jsar2.get(4).toString());
                    temp.color = Byte.parseByte(jsar2.get(5).toString());
                    temp.part = Byte.parseByte(jsar2.get(6).toString());
                    temp.tier = Byte.parseByte(jsar2.get(7).toString());
                    temp.islock = true;
                    JSONArray jsar3 = (JSONArray) jsar2.get(8);
                    temp.op = new ArrayList<>();
                    for (int j = 0; j < jsar3.size(); j++) {
                        JSONArray jsar4 = (JSONArray) jsar3.get(j);
                        temp.op.add(new Option(Byte.parseByte(jsar4.get(0).toString()), Integer.parseInt(jsar4.get(1).toString())));
                    }
                    temp.time_use = 0;
                    d.wear[Byte.parseByte(jsar2.get(9).toString())] = temp;
                }
            }

            d.training_time_used_today = obj.get("time_used") != null ? Integer.parseInt(obj.get("time_used").toString()) : 0;
            d.training_ticket_bought_today = obj.get("ticket_bought") != null && Integer.parseInt(obj.get("ticket_bought").toString()) == 1;
            d.training_ticket_used_today = obj.get("ticket_used") != null && Integer.parseInt(obj.get("ticket_used").toString()) == 1;
            d.last_train_date = obj.get("last_train_date") != null ? obj.get("last_train_date").toString() : "";
            d.summon_cooldown_until = obj.get("summon_cd") != null ? Long.parseLong(obj.get("summon_cd").toString()) : 0L;

            d.checkDailyReset();
            try {
                d.loadSkills();
            } catch (Exception e) {}
            d.hp = d.get_max_hp();
            d.mp = d.get_max_mp();
            return d;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
