package client;

import template.NpcTemplate;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import org.json.simple.JSONArray;
import org.json.simple.JSONValue;
import core.Log;
import core.Manager;
import core.SQL;
import core.Service;
import core.Util;
import event_daily.ChienTruong;
import event_daily.Wedding;
import io.Message;
import io.Session;
import map.Dungeon;
import map.Map;
import map.MapService;
import map.Vgo;
import template.EffTemplate;
import template.Item3;
import template.Item47;
import template.ItemTemplate3;
import template.Level;
import template.LvSkill;
import template.Option;
import template.Part_fashion;
import template.Part_player;
import template.Pet_di_buon;
import template.Pet_di_buon_manager;
import template.Player_store;
import template.Skill;
import java.util.Set;
import java.util.HashSet;
import template.Member_ChienTruong;
import template.MiNuong;

public class Player {
    public MiNuong minuong;
	public int id_henshin = -1;
	public long time_henshin = -1;
	public long time_use_item_arena = System.currentTimeMillis() + 250_000L;
    
    public final static int SPEED = 42;
    public List<NpcTemplate> list_npc = new ArrayList<>();
    public boolean is_atk = true;
    public int luyenthe;
    public int[] kinhmach;
    public int[] tutien;
    public Item3 it_change_op;
    public byte it_change_op_index = -1;
    public long time_eff_tutien;

    public boolean is_nhanban;
    public Session conn;
    public final int id;
    public boolean already_setup;
    public String name;
    public Map map;
    public boolean is_changemap;
    public short x;
    public short y;
    public short x_old;
    public short y_old;
    public byte head;
    public byte eye;
    public byte hair;
    public List<EffTemplate> list_eff;
    public Date date;
    public byte diemdanh;
    public byte chucphuc;
    public int hieuchien;
    public int chuyensinh;
    public long xptutien;
    public byte type_exp;
    public byte clazz;
    public short level;
    public long exp;
    private long vang;
    private int kimcuong;
    public boolean isdie;
    public int tiemnang;
    public short kynang;
    public int point1;
    public int point2;
    public int point3;
    public int point4;
    public int suckhoe;
    public int pointarena;
    public int pk;
    public byte typepk;
    public int pointpk;
    public byte[] skill_point;
    public long[] time_delay_skill;
    public Body body;
    public byte maxbag;
    public byte maxbox;
    public Item item;
    public List<String> giftcode;
    public byte[][] rms_save;
    public List<Pet> mypet;
    public boolean pet_follow;
    public List<Friend> list_friend;
    public List<String> list_enemies;
    public long hp;
    public int mp;
    public byte[] fashion;
    public Skill[] skills;
    public byte item_color_can_pick;
    public byte hp_mp_can_pick;
    public HashMap<Integer, Boolean> other_player_inside;
    public HashMap<Integer, Boolean> other_mob_inside;
    public HashMap<Integer, Boolean> other_mob_inside_update;
    public byte type_use_mount;
    public short id_item_rebuild;
    public boolean is_use_mayman;
    public short id_use_mayman;
    public short item_replace;
    public short item_replace2;
    public short id_buffer_126;
    public byte id_index_126;
    public Party party;
    public long time_use_poition_hp;
    public long time_use_poition_mp;
    public byte enough_time_disconnect;
    public int dame_affect_special_sk;
    public int hp_restore;
    public long time_buff_hp;
    public long time_buff_mp;
    public long time_affect_special_sk;
    public long time_speed_rebuild;
    public String name_trade;
    public short[] list_item_trade;
    public boolean lock_trade;
    public boolean accept_trade;
    public int money_trade;
    public Dungeon dungeon;
    public Clan myclan;
    public byte id_medal_is_created;
    public short[] medal_create_material;
    public short fusion_material_medal_id;
    public long pet_atk_speed;
    public long time_eff_medal;
    public int[] point_active;
    public short id_horse;
    public boolean is_create_wing;
    public short id_remove_time_use;
    public byte id_wing_split;
    public byte[] in4_auto;
    public List<Player_store> my_store;
    public String my_store_name;
    public Pet_di_buon pet_di_buon;
    public short id_name;
    public String name_mem_clan_to_appoint = "";
    public byte id_select_mo_ly;
    public short id_hop_ngoc;
    public List<Item3> list_thao_kham_ngoc;
    public int time_atk_ngoc_hon_nguyen = 0;
    public short id_ngoc_tinh_luyen = -1;
    public Wedding it_wedding;
    public String[] in4_wedding;
    public int[] quest_daily;
    public long vang_ct = 10_000;
    public int dropnlmd = 1 ;
   public short[] tinhtu_material;
	public short id_tach_vp = -1;
	public short[] nv_tinh_tu;

    public Player(Session conn, int id) {
        this.conn = conn;
        this.id = id;
    }

    public boolean setup() throws IOException {
        String query = "SELECT * FROM `player` WHERE `id` = '" + this.id + "' LIMIT 1;";
        try ( Connection connection = SQL.gI().getConnection();  Statement ps = connection.createStatement();  ResultSet rs = ps.executeQuery(query)) {
            if (!rs.next()) {
                return false;
            }
            //
            
        pointarena = rs.getShort("pointarena");
            this.name = rs.getString("name");
            JSONArray jsar = (JSONArray) JSONValue.parse(rs.getString("body"));
            if (jsar == null) {
                return false;
            }
            head = Byte.parseByte(jsar.get(0).toString());
            eye = Byte.parseByte(jsar.get(1).toString());
            hair = Byte.parseByte(jsar.get(2).toString());
            jsar.clear();
            jsar = (JSONArray) JSONValue.parse(rs.getString("tutien"));
            luyenthe = Integer.parseInt(jsar.get(0).toString());
            kinhmach = new int[2];
            tutien = new int[3];
            kinhmach[0] = Integer.parseInt(jsar.get(1).toString());
            kinhmach[1] = Integer.parseInt(jsar.get(2).toString());
            tutien[0] = Integer.parseInt(jsar.get(3).toString());
            tutien[1] = Integer.parseInt(jsar.get(4).toString());
            tutien[2] = Integer.parseInt(jsar.get(5).toString());
            jsar.clear();

            jsar = (JSONArray) JSONValue.parse(rs.getString("dquest"));
            quest_daily = new int[6];
            for (int i = 0; i < quest_daily.length; i++) {
                quest_daily[i] = Integer.parseInt(jsar.get(i).toString());
            }
            jsar.clear();

            jsar = (JSONArray) JSONValue.parse(rs.getString("site"));
            if (jsar == null) {
                return false;
            }
            Map[] map_enter = Map.get_map_by_id(Byte.parseByte(jsar.get(0).toString()));
            if (map_enter != null) {
                x = Short.parseShort(jsar.get(1).toString());
                y = Short.parseShort(jsar.get(2).toString());
            } else {
                map_enter = Map.entrys.get(1);
                x = 432;
                y = 354;
            }
            map = map_enter[0];
            other_player_inside = new HashMap<>();
            other_mob_inside = new HashMap<>();
            other_mob_inside_update = new HashMap<>();
            jsar.clear();
            jsar = (JSONArray) JSONValue.parse(rs.getString("nvtt"));
			if (jsar == null) {
				return false;
			}
			nv_tinh_tu = new short[] {-1, 0, 0, 0};
			for (int i = 0; i < nv_tinh_tu.length; i++) {
				nv_tinh_tu[i] = Short.parseShort(jsar.get(i).toString());
			}
			jsar.clear();
            jsar = (JSONArray) JSONValue.parse(rs.getString("eff"));
            if (jsar == null) {
                return false;
            }
            list_eff = new ArrayList<>();
            for (int i = 0; i < jsar.size(); i++) {
                JSONArray jsar2 = (JSONArray) JSONValue.parse(jsar.get(i).toString());
                if (jsar2 == null) {
                    return false;
                }
                list_eff.add(
                        new EffTemplate(Integer.parseInt(jsar2.get(0).toString()), Integer.parseInt(jsar2.get(1).toString()),
                                (System.currentTimeMillis() + Long.parseLong(jsar2.get(2).toString()))));
            }
            jsar.clear();

            date = Util.getDate(rs.getString("date"));
            diemdanh = rs.getByte("diemdanh");
            chucphuc = rs.getByte("chucphuc");
            hieuchien = rs.getInt("hieuchien");
            type_exp = rs.getByte("typeexp");
            clazz = rs.getByte("clazz");
            level = rs.getShort("level");
            chuyensinh = rs.getInt("chuyensinh");
            dropnlmd = rs.getInt("dropnlmd");
            exp = rs.getLong("exp");
            //
            if (level > Manager.gI().lvmax) {
                level = (short) Manager.gI().lvmax;
                if (exp >= Level.entrys.get(level - 1).exp) {
                    exp = Level.entrys.get(level - 1).exp - 1;
                }
            }
            //
            vang = rs.getLong("vang");
            kimcuong = rs.getInt("kimcuong");
            isdie = false;
            tiemnang = rs.getInt("tiemnang");
            kynang = rs.getShort("kynang");
            point1 = rs.getInt("point1");
            point2 = rs.getInt("point2");
            point3 = rs.getInt("point3");
            point4 = rs.getInt("point4");

            short it_name_ = rs.getShort("id_name");
            if (it_name_ != -1) {
                id_name = (short) (ItemTemplate3.item.get(it_name_).getPart() + 41);
                id_name
                        = (short) (((it_name_ >= 4720 && it_name_ <= 4727) || (it_name_ >= 4765 && it_name_ <= 4767)) ? id_name
                                : 78);
            } else {
                id_name = -1;
            }
            skill_point = new byte[21];
            time_delay_skill = new long[21];
            jsar = (JSONArray) JSONValue.parse(rs.getString("skill"));
            if (jsar == null) {
                return false;
            }
            for (int i = 0; i < 21; i++) {
                skill_point[i] = Byte.parseByte(jsar.get(i).toString());
                time_delay_skill[i] = 0;
            }
            jsar.clear();
            // load item
            body = new Body(this);
            maxbag = rs.getByte("maxbag");
            maxbox = 42;
            item = new Item(this);
            item.bag3 = new Item3[maxbag];
            item.box3 = new Item3[maxbag];
            item.wear = new Item3[24];
            item.bag47 = new ArrayList<>();
            item.box47 = new ArrayList<>();
            for (int i = 0; i < 24; i++) {
                item.wear[i] = null;
            }
            for (int i = 0; i < maxbag; i++) {
                item.bag3[i] = null;
                item.box3[i] = null;
            }
            jsar = (JSONArray) JSONValue.parse(rs.getString("item4"));
            if (jsar == null) {
                return false;
            }
            for (int i = 0; i < jsar.size(); i++) {
                JSONArray jsar2 = (JSONArray) JSONValue.parse(jsar.get(i).toString());
                Item47 temp = new Item47();
                temp.id = Short.parseShort(jsar2.get(0).toString());
                temp.quantity = Short.parseShort(jsar2.get(1).toString());
                temp.category = 4;
                if (temp.quantity > 0) {
                    item.bag47.add(temp);
                }
                jsar2.clear();
            }
            jsar.clear();
            jsar = (JSONArray) JSONValue.parse(rs.getString("item7"));
            if (jsar == null) {
                return false;
            }
            for (int i = 0; i < jsar.size(); i++) {
                JSONArray jsar2 = (JSONArray) JSONValue.parse(jsar.get(i).toString());
                Item47 temp = new Item47();
                temp.id = Short.parseShort(jsar2.get(0).toString());
                temp.quantity = Short.parseShort(jsar2.get(1).toString());
                temp.category = 7;
                if (temp.quantity > 0) {
                    item.bag47.add(temp);
                }
                jsar2.clear();
            }
            jsar.clear();
            jsar = (JSONArray) JSONValue.parse(rs.getString("item3"));
            if (jsar == null) {
                return false;
            }
            for (int i = 0; i < jsar.size(); i++) {
                JSONArray jsar2 = (JSONArray) JSONValue.parse(jsar.get(i).toString());
                Item3 temp = new Item3();
                temp.id = Short.parseShort(jsar2.get(0).toString());
                temp.clazz = Byte.parseByte(jsar2.get(1).toString());
                temp.type = Byte.parseByte(jsar2.get(2).toString());
                temp.level = Short.parseShort(jsar2.get(3).toString());
                temp.icon = Short.parseShort(jsar2.get(4).toString());
                temp.color = Byte.parseByte(jsar2.get(5).toString());
                temp.part = Byte.parseByte(jsar2.get(6).toString());
                temp.islock = Byte.parseByte(jsar2.get(7).toString()) == 1;
                temp.name = ItemTemplate3.item.get(temp.id).getName();
                if (temp.islock) {
                    temp.name += " [Khóa]";
                }
                temp.tier = Byte.parseByte(jsar2.get(8).toString());
                // if (temp.type == 15) {
                // temp.tier = 0;
                // }
                JSONArray jsar3 = (JSONArray) JSONValue.parse(jsar2.get(9).toString());
                temp.op = new ArrayList<>();
                for (int j = 0; j < jsar3.size(); j++) {
                    JSONArray jsar4 = (JSONArray) JSONValue.parse(jsar3.get(j).toString());
                    temp.op.add(
                            new Option(Byte.parseByte(jsar4.get(0).toString()), Integer.parseInt(jsar4.get(1).toString())));
                }
                temp.time_use = 0;
                if (jsar2.size() == 11) {
                    temp.time_use = Long.parseLong(jsar2.get(10).toString());
                }
                item.bag3[i] = temp;
            }
            jsar.clear();
            jsar = (JSONArray) JSONValue.parse(rs.getString("itemwear"));
            if (jsar == null) {
                return false;
            }
            for (int i = 0; i < jsar.size(); i++) {
                JSONArray jsar2 = (JSONArray) JSONValue.parse(jsar.get(i).toString());
                if (jsar2 == null) {
                    return false;
                }
                Item3 temp = new Item3();
                temp.id = Short.parseShort(jsar2.get(0).toString());
                temp.name = ItemTemplate3.item.get(temp.id).getName() + " [Khóa]";
                temp.clazz = Byte.parseByte(jsar2.get(1).toString());
                temp.type = Byte.parseByte(jsar2.get(2).toString());
                temp.level = Short.parseShort(jsar2.get(3).toString());
                temp.icon = Short.parseShort(jsar2.get(4).toString());
                temp.color = Byte.parseByte(jsar2.get(5).toString());
                temp.part = Byte.parseByte(jsar2.get(6).toString());
                temp.tier = Byte.parseByte(jsar2.get(7).toString());
                // if (temp.type == 15) {
                // temp.tier = 0;
                // }
                temp.islock = true;
                JSONArray jsar3 = (JSONArray) JSONValue.parse(jsar2.get(8).toString());
                temp.op = new ArrayList<>();
                for (int j = 0; j < jsar3.size(); j++) {
                    JSONArray jsar4 = (JSONArray) JSONValue.parse(jsar3.get(j).toString());
                    if (jsar4 == null) {
                        return false;
                    }
                    temp.op.add(
                            new Option(Byte.parseByte(jsar4.get(0).toString()), Integer.parseInt(jsar4.get(1).toString())));
                }
                temp.time_use = 0;
                item.wear[Byte.parseByte(jsar2.get(9).toString())] = temp;
            }
            jsar.clear();
            jsar = (JSONArray) JSONValue.parse(rs.getString("giftcode"));
            if (jsar == null) {
                return false;
            }
            giftcode = new ArrayList<>();
            for (int i = 0; i < jsar.size(); i++) {
                giftcode.add(jsar.get(i).toString());
            }
            jsar.clear();
            // box
            jsar = (JSONArray) JSONValue.parse(rs.getString("itembox4"));
            if (jsar == null) {
                return false;
            }
            for (int i = 0; i < jsar.size(); i++) {
                JSONArray jsar2 = (JSONArray) JSONValue.parse(jsar.get(i).toString());
                if (jsar2 == null) {
                    return false;
                }
                Item47 temp = new Item47();
                temp.id = Short.parseShort(jsar2.get(0).toString());
                temp.quantity = Short.parseShort(jsar2.get(1).toString());
                temp.category = 4;
                if (temp.quantity > 0) {
                    item.box47.add(temp);
                }
                jsar2.clear();
            }
            jsar.clear();
            jsar = (JSONArray) JSONValue.parse(rs.getString("itembox7"));
            if (jsar == null) {
                return false;
            }
            for (int i = 0; i < jsar.size(); i++) {
                JSONArray jsar2 = (JSONArray) JSONValue.parse(jsar.get(i).toString());
                if (jsar2 == null) {
                    return false;
                }
                Item47 temp = new Item47();
                temp.id = Short.parseShort(jsar2.get(0).toString());
                temp.quantity = Short.parseShort(jsar2.get(1).toString());
                temp.category = 7;
                if (temp.quantity > 0) {
                    item.box47.add(temp);
                }
                jsar2.clear();
            }
            jsar.clear();
            jsar = (JSONArray) JSONValue.parse(rs.getString("itembox3"));
            if (jsar == null) {
                return false;
            }
            for (int i = 0; i < jsar.size(); i++) {
                JSONArray jsar2 = (JSONArray) JSONValue.parse(jsar.get(i).toString());
                if (jsar2 == null) {
                    return false;
                }
                Item3 temp = new Item3();
                temp.id = Short.parseShort(jsar2.get(0).toString());
                temp.clazz = Byte.parseByte(jsar2.get(1).toString());
                temp.type = Byte.parseByte(jsar2.get(2).toString());
                temp.level = Short.parseShort(jsar2.get(3).toString());
                temp.icon = Short.parseShort(jsar2.get(4).toString());
                temp.color = Byte.parseByte(jsar2.get(5).toString());
                temp.part = Byte.parseByte(jsar2.get(6).toString());
                temp.islock = Byte.parseByte(jsar2.get(7).toString()) == 1;
                temp.name = ItemTemplate3.item.get(temp.id).getName();
                if (temp.islock) {
                    temp.name += " [Khóa]";
                }
                temp.tier = Byte.parseByte(jsar2.get(8).toString());
                // if (temp.type == 15) {
                // temp.tier = 0;
                // }
                JSONArray jsar3 = (JSONArray) JSONValue.parse(jsar2.get(9).toString());
                temp.op = new ArrayList<>();
                for (int j = 0; j < jsar3.size(); j++) {
                    JSONArray jsar4 = (JSONArray) JSONValue.parse(jsar3.get(j).toString());
                    temp.op.add(
                            new Option(Byte.parseByte(jsar4.get(0).toString()), Integer.parseInt(jsar4.get(1).toString())));
                }
                temp.time_use = 0;
                if (jsar2.size() == 11) {
                    temp.time_use = Long.parseLong(jsar2.get(10).toString());
                }
                item.box3[i] = temp;
            }
            jsar.clear();
            jsar = (JSONArray) JSONValue.parse(rs.getString("rms_save"));
            if (jsar == null) {
                return false;
            }
            rms_save = new byte[jsar.size()][];
            for (int i = 0; i < rms_save.length; i++) {
                JSONArray js = (JSONArray) JSONValue.parse(jsar.get(i).toString());
                rms_save[i] = new byte[js.size()];
                for (int j = 0; j < rms_save[i].length; j++) {
                    rms_save[i][j] = Byte.parseByte(js.get(j).toString());
                }
            }
            jsar.clear();
            //
            mypet = new ArrayList<>();
            pet_follow = false;
            jsar = (JSONArray) JSONValue.parse(rs.getString("pet"));
            long t_off = 0;
            if (Session.players_off.containsKey(this.name)) {
                t_off = System.currentTimeMillis() - Session.players_off.get(this.name);
                Session.players_off.remove(this.name);
                t_off /= 60_000;
                if (t_off > 0) {
                    t_off /= 5;
                }
            }
            if (jsar == null) {
                return false;
            }
            for (int i = 0; i < jsar.size(); i++) {
                JSONArray js = (JSONArray) JSONValue.parse(jsar.get(i).toString());
                Pet temp = new Pet();
                temp.setup(js);
                temp.update_grown(t_off);
                if (temp.is_follow) {
                    pet_follow = true;
                }
                mypet.add(temp);
            }
            jsar.clear();
            list_friend = new ArrayList<>();
            jsar = (JSONArray) JSONValue.parse(rs.getString("friend"));
            if (jsar == null) {
                return false;
            }
            for (int i = 0; i < jsar.size(); i++) {
                JSONArray js12 = (JSONArray) JSONValue.parse(jsar.get(i).toString());
                Friend temp = new Friend();
                temp.name = js12.get(0).toString();
                temp.level = Short.parseShort(js12.get(1).toString());
                temp.head = Byte.parseByte(js12.get(2).toString());
                temp.hair = Byte.parseByte(js12.get(3).toString());
                temp.eye = Byte.parseByte(js12.get(4).toString());
                temp.itemwear = new ArrayList<>();
                JSONArray js2 = (JSONArray) JSONValue.parse(js12.get(5).toString());
                for (int j = 0; j < js2.size(); j++) {
                    JSONArray js3 = (JSONArray) JSONValue.parse(js2.get(j).toString());
                    Part_player part = new Part_player();
                    part.type = Byte.parseByte(js3.get(0).toString());
                    part.part = Byte.parseByte(js3.get(1).toString());
                    temp.itemwear.add(part);
                }
                list_friend.add(temp);
            }
            jsar.clear();
            list_enemies = new ArrayList<>();
            jsar = (JSONArray) JSONValue.parse(rs.getString("enemies"));
            if (jsar == null) {
                return false;
            }
            for (int i = 0; i < jsar.size(); i++) {
                list_enemies.add(jsar.get(i).toString());
            }
            jsar.clear();
            jsar = (JSONArray) JSONValue.parse(rs.getString("medal_create_material"));
            if (jsar == null) {
                return false;
            }
            medal_create_material = new short[jsar.size()];
            for (int i = 0; i < jsar.size(); i++) {
                medal_create_material[i] = Short.parseShort(jsar.get(i).toString());
            }
            jsar.clear();
            jsar = (JSONArray) JSONValue.parse(rs.getString("tinhtu_material"));
			if (jsar == null) {
				return false;
			}
			tinhtu_material = new short[jsar.size()];
			for (int i = 0; i < jsar.size(); i++) {
				tinhtu_material[i] = Short.parseShort(jsar.get(i).toString());
			}
			jsar.clear();
            jsar = (JSONArray) JSONValue.parse(rs.getString("point_active"));
            if (jsar == null) {
                return false;
            }
            point_active = new int[jsar.size()];
            for (int i = 0; i < jsar.size(); i++) {
                point_active[i] = Integer.parseInt(jsar.get(i).toString());
            }
            jsar.clear();
            myclan = Clan.get_clan_of_player(this.name);
            //
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        //
        already_setup = true;
        return true;
    }

    private void load_skill() throws IOException {
        ByteArrayInputStream bais = null;
        DataInputStream dis = null;
        try {
            switch (clazz) {
                case 0: { // chien binh
                    bais = new ByteArrayInputStream(Manager.gI().msg_29_chienbinh);
                    break;
                }
                case 1: { // sat thu
                    bais = new ByteArrayInputStream(Manager.gI().msg_29_satthu);
                    break;
                }
                case 2: { // phap su
                    bais = new ByteArrayInputStream(Manager.gI().msg_29_phapsu);
                    break;
                }
                case 3: { // xa thu
                    bais = new ByteArrayInputStream(Manager.gI().msg_29_xathu);
                    break;
                }
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
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            if (dis != null) {
                dis.close();
            }
            if (bais != null) {
                bais.close();
            }
        }
    }

    public EffTemplate get_eff(int id) {
        for (int i = 0; i < list_eff.size(); i++) {
            EffTemplate temp = list_eff.get(i);
            if (temp.id == id) {
                return temp;
            }
        }
        return null;
    }

    public synchronized long get_vang() {
        return this.vang;
    }

    public synchronized int get_ngoc() {
        return this.kimcuong;
    }

    public synchronized void update_vang(long i) {
        
        String txt = "[VANG] Package: %s, Class: %s, Method: %s, Change: %s";
        
        if ((i + vang) > 2__000_000_000_000_000L) {
            vang = 2__000_000_000_000_000L;
        } else {
            vang += i;
        }
        	if (i<0 &&conn.p.nv_tinh_tu[0] == 9 && conn.p.nv_tinh_tu[1] < conn.p.nv_tinh_tu[2]) {
			if (i < -30_000) {
				i = -30_000;
			}
			conn.p.nv_tinh_tu[1] -= i;
		}
    }

    public synchronized void update_ngoc(long i) {
         String txt = "[NGOC] Package: %s, Class: %s, Method: %s, Change: %s";
        
        if ((i + kimcuong) > 2_000_000_000L) {
            kimcuong = 2_000_000_000;
        } else {
            kimcuong += i;
        }
        if (i < 0 &&conn.p.nv_tinh_tu[0] == 10 && conn.p.nv_tinh_tu[1] < conn.p.nv_tinh_tu[2]) {
			conn.p.nv_tinh_tu[1]++;
		}
    }

    @SuppressWarnings("unchecked")
    public void flush() {
        if (!already_setup) {
            return;
        }
        try ( Connection connection = SQL.gI().getConnection();  Statement ps = connection.createStatement()) {
            String a = "`level` = " + level;
            a += ",`exp` = " + exp;
            JSONArray jsar = new JSONArray();
            if (isdie || Map.is_map_cant_save_site(map.map_id)) {
                jsar.add(1);
                jsar.add(432);
                jsar.add(354);
            } else {
                jsar.add(map.map_id);
                jsar.add(x);
                jsar.add(y);
            }
            a += ",`site` = '" + jsar.toJSONString() + "'";
            jsar.clear();
            jsar.add(head);
            jsar.add(eye);
            jsar.add(hair);
            a += ",`body` = '" + jsar.toJSONString() + "'";
            jsar.clear();
            for (int i = 0; i < nv_tinh_tu.length; i++) {
				jsar.add(nv_tinh_tu[i]);
			}
			a += ",`nvtt` = '" + jsar.toJSONString() + "'";
			jsar.clear();
            jsar.add(luyenthe);
            jsar.add(kinhmach[0]);
            jsar.add(kinhmach[1]);
            jsar.add(tutien[0]);
            jsar.add(tutien[1]);
            jsar.add(tutien[2]);
            a += ",`tutien` = '" + jsar.toJSONString() + "'";
            jsar.clear();

            for (int i = 0; i < quest_daily.length; i++) {
                jsar.add(quest_daily[i]);
            }
            a += ",`dquest` = '" + jsar.toJSONString() + "'";
            jsar.clear();

            //
            for (int i = 0; i < list_eff.size(); i++) {
                EffTemplate temp = list_eff.get(i);
                if (temp.id != -126 && temp.id != -125) {
                    continue;
                }

                JSONArray jsar21 = new JSONArray();
                jsar21.add(temp.id);
                jsar21.add(temp.param);
                long time = temp.time - System.currentTimeMillis();
                jsar21.add(time);
                jsar.add(jsar21);
            }
            a += ",`eff` = '" + jsar.toJSONString() + "'";
            jsar.clear();
            for (int i = 0; i < list_friend.size(); i++) {
                JSONArray js12 = new JSONArray();
                Friend temp = list_friend.get(i);
                js12.add(temp.name);
                js12.add(temp.level);
                js12.add(temp.head);
                js12.add(temp.hair);
                js12.add(temp.eye);
                JSONArray js = new JSONArray();
                for (Part_player part : temp.itemwear) {
                    JSONArray js2 = new JSONArray();
                    js2.add(part.type);
                    js2.add(part.part);
                    js.add(js2);
                }
                js12.add(js);
                jsar.add(js12);
            }
            a += ",`friend` = '" + jsar.toJSONString() + "'";
            jsar.clear();
            for (int i = 0; i < 21; i++) {
                jsar.add(skill_point[i]);
            }
            a += ",`skill` = '" + jsar.toJSONString() + "'";
            jsar.clear();
            //
            for (Item47 it : item.bag47) {
                if (it.category == 4) {
                    JSONArray jsar2 = new JSONArray();
                    jsar2.add(it.id);
                    jsar2.add(it.quantity);
                    jsar.add(jsar2);
                }
            }
            a += ",`item4` = '" + jsar.toJSONString() + "'";
            jsar.clear();
            //
            for (Item47 it : item.bag47) {
                if (it.category == 7) {
                    JSONArray jsar2 = new JSONArray();
                    jsar2.add(it.id);
                    jsar2.add(it.quantity);
                    jsar.add(jsar2);
                }
            }
            a += ",`item7` = '" + jsar.toJSONString() + "'";
            jsar.clear();
            //
            for (int i = 0; i < item.bag3.length; i++) {
                Item3 temp = item.bag3[i];
                if (temp != null) {
                    JSONArray jsar2 = new JSONArray();
                    jsar2.add(temp.id);
                    jsar2.add(temp.clazz);
                    jsar2.add(temp.type);
                    jsar2.add(temp.level);
                    jsar2.add(temp.icon);
                    jsar2.add(temp.color);
                    jsar2.add(temp.part);
                    jsar2.add(temp.islock ? 1 : 0);
                    jsar2.add(temp.tier);
                    JSONArray jsar3 = new JSONArray();
                    for (int j = 0; j < temp.op.size(); j++) {
                        JSONArray jsar4 = new JSONArray();
                        jsar4.add(temp.op.get(j).id);
                        jsar4.add(temp.op.get(j).getParam(0));
                        jsar3.add(jsar4);
                    }
                    jsar2.add(jsar3);
                    jsar2.add(temp.time_use);
                    jsar.add(jsar2);
                }
            }
            a += ",`item3` = '" + jsar.toJSONString() + "'";
            jsar.clear();
            //
            for (int i = 0; i < item.wear.length - 1; i++) {
                Item3 temp = item.wear[i];
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
                    jsar.add(jsar2);
                }
            }
            a += ",`itemwear` = '" + jsar.toJSONString() + "'";
            jsar.clear();
            //
            for (int i = 0; i < giftcode.size(); i++) {
                jsar.add(giftcode.get(i));
            }
            a += ",`giftcode` = '" + jsar.toJSONString() + "'";
            jsar.clear();
            for (int i = 0; i < list_enemies.size(); i++) {
                jsar.add(list_enemies.get(i));
            }
            a += ",`enemies` = '" + jsar.toJSONString() + "'";
            jsar.clear();
            for (int i = 0; i < rms_save.length; i++) {
                JSONArray js = new JSONArray();
                for (int i1 = 0; i1 < rms_save[i].length; i1++) {
                    js.add(rms_save[i][i1]);
                }
                jsar.add(js);
            }
            a += ",`rms_save` = '" + jsar.toJSONString() + "'";
            jsar.clear();
            //
            for (int i = 0; i < item.box47.size(); i++) {
                if (item.box47.get(i).category == 4) {
                    JSONArray jsar2 = new JSONArray();
                    jsar2.add(item.box47.get(i).id);
                    jsar2.add(item.box47.get(i).quantity);
                    jsar.add(jsar2);
                }
            }
            a += ",`itembox4` = '" + jsar.toJSONString() + "'";
            jsar.clear();
            //
            for (int i = 0; i < item.box47.size(); i++) {
                if (item.box47.get(i).category == 7) {
                    JSONArray jsar2 = new JSONArray();
                    jsar2.add(item.box47.get(i).id);
                    jsar2.add(item.box47.get(i).quantity);
                    jsar.add(jsar2);
                }
            }
            a += ",`itembox7` = '" + jsar.toJSONString() + "'";
            jsar.clear();
            //
            for (int i = 0; i < item.box3.length; i++) {
                Item3 temp = item.box3[i];
                if (temp != null) {
                    JSONArray jsar2 = new JSONArray();
                    jsar2.add(temp.id);
                    jsar2.add(temp.clazz);
                    jsar2.add(temp.type);
                    jsar2.add(temp.level);
                    jsar2.add(temp.icon);
                    jsar2.add(temp.color);
                    jsar2.add(temp.part);
                    jsar2.add(temp.islock ? 1 : 0);
                    jsar2.add(temp.tier);
                    JSONArray jsar3 = new JSONArray();
                    for (int j = 0; j < temp.op.size(); j++) {
                        JSONArray jsar4 = new JSONArray();
                        jsar4.add(temp.op.get(j).id);
                        jsar4.add(temp.op.get(j).getParam(0));
                        jsar3.add(jsar4);
                    }
                    jsar2.add(jsar3);
                    jsar2.add(temp.time_use);
                    jsar.add(jsar2);
                }
            }
            a += ",`itembox3` = '" + jsar.toJSONString() + "'";
            jsar.clear();
            //
            for (int i = 0; i < mypet.size(); i++) {
                JSONArray js1 = new JSONArray();
                js1.add(mypet.get(i).level);
                js1.add(mypet.get(i).type);
                js1.add(mypet.get(i).icon);
                js1.add(mypet.get(i).nframe);
                js1.add(mypet.get(i).color);
                js1.add(mypet.get(i).grown);
                js1.add(mypet.get(i).maxgrown);
                js1.add(mypet.get(i).point1);
                js1.add(mypet.get(i).point2);
                js1.add(mypet.get(i).point3);
                js1.add(mypet.get(i).point4);
                js1.add(mypet.get(i).maxpoint);
                js1.add(mypet.get(i).exp);
                js1.add(mypet.get(i).is_follow ? 1 : 0);
                js1.add(mypet.get(i).is_hatch ? 1 : 0);
                js1.add(mypet.get(i).time_born);
                JSONArray js2 = new JSONArray();
                for (int i2 = 0; i2 < mypet.get(i).op.size(); i2++) {
                    JSONArray js3 = new JSONArray();
                    js3.add(mypet.get(i).op.get(i2).id);
                    js3.add(mypet.get(i).op.get(i2).param);
                    js3.add(mypet.get(i).op.get(i2).maxdam);
                    js2.add(js3);
                }
                js1.add(js2);
                jsar.add(js1);
            }
            a += ",`pet` = '" + jsar.toJSONString() + "'";
            jsar.clear();
            //
            for (int i = 0; i < medal_create_material.length; i++) {
                jsar.add(medal_create_material[i]);
            }
            a += ",`medal_create_material` = '" + jsar.toJSONString() + "'";
            jsar.clear();
            for (int i = 0; i < tinhtu_material.length; i++) {
				jsar.add(tinhtu_material[i]);
			}
			a += ",`tinhtu_material` = '" + jsar.toJSONString() + "'";
			jsar.clear();
            for (int i = 0; i < point_active.length; i++) {
                jsar.add(point_active[i]);
            }
            a += ",`point_active` = '" + jsar.toJSONString() + "'";
            jsar.clear();

            //
            a += ",`vang` = " + vang;
            a += ",`kimcuong` = " + kimcuong;
            a += ",`tiemnang` = " + tiemnang;
            a += ",`pointarena` = " + pointarena;
            a += ",`kynang` = " + kynang;
            a += ",`diemdanh` = " + diemdanh;
            a += ",`chucphuc` = " + chucphuc;
            a += ",`hieuchien` = " + hieuchien;
            a += ",`chuyensinh` = " + chuyensinh;
            a += ",`dropnlmd` = " + dropnlmd;
            a += ",`typeexp` = " + type_exp;
            a += ",`date` = '" + date.toString() + "'";
            a += ",`clazz` = " + clazz;
            a += ",`point1` = " + point1;
            a += ",`point2` = " + point2;
            a += ",`point3` = " + point3;
            a += ",`point4` = " + point4;

            if (ps.executeUpdate("UPDATE `player` SET " + a + " WHERE `id` = " + this.id + ";") > 0) {
                connection.commit();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
//        System.out.println("flush " + this.conn.user);
    }

    public void change_new_date() {
        if (!Util.is_same_day(Date.from(Instant.now()), date)) {
            // diem danh
            diemdanh = 1;
            chucphuc = 1;
            point_active[0] = 3;
            point_active[1] = 0;
            quest_daily[5] = 20;
            	nv_tinh_tu[0] = -1;
			nv_tinh_tu[1] = 0;
			nv_tinh_tu[2] = 0;
			nv_tinh_tu[3] = 0;
            //
            date = Date.from(Instant.now());
        }
    }

    public void set_x2_xp(int type) throws IOException {
        switch (type) {
            case 0: {
                Message m = new Message(62);
                m.writer().writeByte(0);
                m.writer().writeShort(0);
                conn.addmsg(m);
                m.cleanup();
                break;
            }
            case 1: {
                EffTemplate tempp = conn.p.get_eff(-125);
                if (tempp != null) {
                    long time_eff = tempp.time - System.currentTimeMillis();
                    Message m = new Message(62);
                    m.writer().writeByte(1);
                    m.writer().writeShort((short) (time_eff / 60000L));
                    conn.addmsg(m);
                    m.cleanup();
                    add_eff(-125, 5000, (int) time_eff);
                }
                break;
            }
        }
    }

    public void add_eff(int id, int param, int time) {
        synchronized (list_eff) {
            if (param == 0) {
                return;
            }
            EffTemplate temp_test = get_eff(id);
            while (temp_test != null) {
                list_eff.remove(temp_test);
                temp_test = get_eff(id);
            }
            EffTemplate temp = new EffTemplate(id, param, (System.currentTimeMillis() + time));
            list_eff.add(temp);
        }
    }

    public int getlevelpercent() {
        return (int) ((exp * 1000) / Level.entrys.get(level - 1).exp);
    }

    public void load_in4_autoplayer(byte[] num) {
        this.in4_auto = num;
        // System.out.println(hp_mp_can_pick);
        // num[0]; on off auto use poition (0 = off)
        // num[1]; %hp use poition
        // num[2]; %mp use poition
        // num[3]; on off pick item (0 = off)
        // num[4];(0 = all, 1 ->)
        // num[5]; (0 = all, 1 = non)
        // num[6]; (0 = all, 1 = hp, 2 = mp, 3 = non)
    }

    public void change_map(Player p, Vgo vgo) throws IOException {
        if (map.map_id == 0) {
            Message m = new Message(55);
            m.writer().writeByte(1);
            m.writer().writeShort(2);
            m.writer().writeByte(-1);
            m.writer().writeByte(0);
            conn.addmsg(m);
            m.cleanup();
        }
        if (p.map.zone_id == p.map.maxzone) {
            boolean check_map_dibuon = false;
            for (Vgo vgo_check : map.vgos) {
                if (vgo.id_map_go == vgo_check.id_map_go) {
                    check_map_dibuon = true;
                    break;
                }
            }
            if (!check_map_dibuon) {
                Service.send_notice_box(p.conn, "Có lỗi xảy ra khi chuyển map hoặc đã đầy, hãy thử lại sau");
                return;
            }
        }
        boolean pet_di_buon_can_goto = false;
        for (int i = 0; i < map.vgos.size(); i++) {
            if (map.vgos.get(i).id_map_go == vgo.id_map_go) {
                pet_di_buon_can_goto = true;
                break;
            }
        }
        p.is_changemap = false;
        p.x_old = vgo.x_old;
        p.y_old = vgo.y_old;
        Map[] mbuffer = Map.get_map_by_id(vgo.id_map_go);
        if (mbuffer != null) {
            Map mbuffer2 = null;
            if (party != null) {
                for (int i = 0; i < party.get_mems().size(); i++) {
                    Player p0 = party.get_mems().get(i);
                    if (p0.map.map_id == mbuffer[0].map_id) {
                        mbuffer2 = p0.map;
                    }
                }
            }
            if (conn.p.item.wear[11] != null && (conn.p.item.wear[11].id == 3599 || conn.p.item.wear[11].id == 3593
                    || conn.p.item.wear[11].id == 3596)) {
                mbuffer2 = mbuffer[mbuffer[0].maxzone];
            } else {
                if (mbuffer2 == null) {
                    for (Map mapp : mbuffer) {
                        if (mapp.players.size() < mapp.maxplayer) {
                            mbuffer2 = mapp;
                            break;
                        }
                    }
                }
            }
            if (mbuffer2 == null) {
                Service.send_notice_box(p.conn, "Có lỗi xảy ra khi chuyển map hoặc đã đầy, hãy thử lại sau");
                return;
            }
            // di buon
            if (p.pet_di_buon != null && (Math.abs(p.pet_di_buon.x - p.x) < 125 && Math.abs(p.pet_di_buon.y - p.y) < 125)
                    && pet_di_buon_can_goto) {
                Message mout = new Message(8);
                mout.writer().writeShort(p.pet_di_buon.index);
                for (int i = 0; i < map.players.size(); i++) {
                    Player p0 = map.players.get(i);
                    if (p0 != null) {
                        p0.conn.addmsg(mout);
                    }
                }
                mout.cleanup();
                p.pet_di_buon.x = vgo.x_new;
                p.pet_di_buon.y = vgo.y_new;
                p.pet_di_buon.id_map = mbuffer2.map_id;
                Message m22 = new Message(4);
                m22.writer().writeByte(1);
                m22.writer().writeShort(131);
                m22.writer().writeShort(conn.p.pet_di_buon.index);
                m22.writer().writeShort(conn.p.pet_di_buon.x);
                m22.writer().writeShort(conn.p.pet_di_buon.y);
                m22.writer().writeByte(-1);
                conn.addmsg(m22);
                m22.cleanup();
            }
            //
            MapService.leave(p.map, p);
            p.map = mbuffer2;
            p.x = vgo.x_new;
            p.y = vgo.y_new;
            p.x_old = p.x;
            p.y_old = p.y;
            MapService.enter(p.map, p);
        } else {
            Service.send_notice_box(p.conn, "Có lỗi xảy ra khi chuyển map");
        }
    }

    public void update_Exp(long expup, boolean expmulti) throws IOException {
        long dame_exp = expup;
        if (expmulti && this.getlevelpercent() >= 0) {
            dame_exp *= Manager.gI().exp;
        }
        if (type_use_mount == 4) {
            dame_exp += ((dame_exp * 5) / 100);
        }
        if ((type_exp == 0 && this.typepk != 0) || this.getlevelpercent() < (-500)) {
            return;
        }
        if (level >= Manager.gI().lvmax || type_exp == 0) {
            return;
        }
        Message m;
        if (this.getlevelpercent() < 0) {
            if (dame_exp > 0) {
                dame_exp /= 5;
            } else {
                dame_exp *= 2;
            }
        }
        exp += dame_exp;
        if (this.getlevelpercent() < (-500)) {
            exp = -(Level.entrys.get(level - 1).exp * 15) / 10;
        }
        int exp_as_int = 0;
        if (dame_exp > 2_000_000_000L) {
            exp_as_int = 2_000_000_000;
        } else {
            exp_as_int = (int) dame_exp;
        }
        if (exp >= Level.entrys.get(level - 1).exp) {
            while (exp >= Level.entrys.get(level - 1).exp && level < Manager.gI().lvmax) {
                exp -= Level.entrys.get(level - 1).exp;
                level++;
                if ((tiemnang + point1 + point2 + point3 + point4) < 2000000000) {
                    point1++;
                    point2++;
                    point3++;
                    point4++;
                    if (kynang < 10000) {
                        kynang += Level.entrys.get(level - 1).kynang;
                    }
                    tiemnang += Level.entrys.get(level - 1).tiemnang;
                }
            }
            if (level == Manager.gI().lvmax && exp >= Level.entrys.get(level - 1).exp) {
                exp = Level.entrys.get(level - 1).exp - 1;
            }
            hp = body.get_max_hp();
            mp = body.get_max_mp();
            m = new Message(33);
            m.writer().writeShort(id);
            m.writer().writeByte(level);
            MapService.send_msg_player_inside(map, this, m, true);
            m.cleanup();
            Service.send_char_main_in4(this);
            MapService.update_in4_2_other_inside(map, this);
            if (party != null) {
                party.sendin4();
            }
        }
        m = new Message(30);
        m.writer().writeShort(id);
        m.writer().writeShort(getlevelpercent());
        m.writer().writeInt(exp_as_int);
        conn.addmsg(m);
        m.cleanup();
    }

    public void change_zone(Session conn2, Message m2) throws IOException {
        if (this.map.map_id == 0) {
            Message m = new Message(55);
            m.writer().writeByte(1);
            m.writer().writeShort(2);
            m.writer().writeByte(-1);
            m.writer().writeByte(0);
            conn.addmsg(m);
            m.cleanup();
        }
        byte zone = m2.reader().readByte();
        if (zone < this.map.maxzone || (conn.p.item.wear[11] != null && (conn.p.item.wear[11].id == 3599
                || conn.p.item.wear[11].id == 3593 || conn.p.item.wear[11].id == 3596))) {
            if (zone != this.map.zone_id) {
                Map map = Map.get_map_by_id(this.map.map_id)[zone];
                if (map.players.size() >= map.maxplayer) {
                    Service.send_notice_box(conn, "Có lỗi xảy ra khi chuyển map hoặc đã đầy, hãy thử lại sau");
                    return;
                }
                MapService.leave(this.map, this);
                this.map = map;
                MapService.enter(this.map, this);
            } else {
                Service.send_notice_box(conn, "mày đang ở khu này rồi đấy!!");
            }
        }
    }

    public synchronized boolean update_coin(int coin_exchange) throws IOException {
        String query = "SELECT `coin` FROM `account` WHERE `user` = '" + conn.user + "' LIMIT 1;";
        int coin_old = 0;
        try ( Connection connection = SQL.gI().getConnection();  Statement ps = connection.createStatement();  ResultSet rs = ps.executeQuery(query)) {
            rs.next();
            coin_old = rs.getInt("coin");
            if (coin_old + coin_exchange < 0) {
                Service.send_notice_box(conn, "Không đủ coin");
                return false;
            }
            coin_old += coin_exchange;
            if (ps.executeUpdate(
                    "UPDATE `account` SET `coin` = " + coin_old + " WHERE `user` = '" + conn.user + "'") == 1) {
                connection.commit();
            }
        } catch (SQLException e) {
            Service.send_notice_box(conn, "Đã xảy ra lỗi");
        }
        return true;
    }

    public void down_mount(Message m2) throws IOException {
        byte type = m2.reader().readByte();
        if (type == -1) {
            Message m = new Message(-97);
            m.writer().writeByte(0);
            m.writer().writeByte(-1);
            m.writer().writeShort(this.id);
            MapService.send_msg_player_inside(this.map, this, m, true);
            m.cleanup();
            this.type_use_mount = -1;
            this.id_horse = -1;
            MapService.update_in4_2_other_inside(this.map, this);
            Service.send_char_main_in4(this);
        }
    }

    public void rest_skill_point() throws IOException {
        for (int i = 0; i < skill_point.length - 2; i++) {
            if (skill_point[i] > 0) {
                kynang += skill_point[i];
            }
        }
        for (int i = 0; i < skill_point.length - 2; i++) {
            if (skill_point[i] > 0) {
                skill_point[i] = 0;
            }
        }
        skill_point[0] = 1;
        kynang -= 1;
        hp = body.get_max_hp();
        mp = body.get_max_mp();
        Service.send_char_main_in4(this);
        for (int i = 0; i < map.players.size(); i++) {
            Player p0 = map.players.get(i);
            if (p0.id != this.id && ((Math.abs(p0.x - this.x) < 200 && Math.abs(p0.y - this.y) < 200)
                    || Map.is_map__load_board_player(map.map_id))) {
                MapService.send_in4_other_char(p0.map, p0, this);
            }
        }
    }

    public void rest_potential_point() throws IOException {
        tiemnang += (point1 + point2 + point3 + point4);
        point1 = (4 + level);
        point2 = (4 + level);
        point3 = (4 + level);
        point4 = (4 + level);
        tiemnang -= (point1 + point2 + point3 + point4);
        hp = body.get_max_hp();
        mp = body.get_max_mp();
        Service.send_char_main_in4(this);
        for (int i = 0; i < map.players.size(); i++) {
            Player p0 = map.players.get(i);
            if (p0.id != this.id && ((Math.abs(p0.x - this.x) < 200 && Math.abs(p0.y - this.y) < 200)
                    || Map.is_map__load_board_player(map.map_id))) {
                MapService.send_in4_other_char(p0.map, p0, this);
            }
        }
    }

    public void player_wear(Session conn2, Item3 temp3, int index_bag, byte index_wear) throws IOException {
        byte b = -1;
        switch (temp3.type) {
            case 0: {// coat
                b = 1;
                break;
            }
            case 1: {// pant
                b = 7;
                break;
            }
            case 2: {// crown
                b = 6;
                break;
            }
            case 3: {// grove
                b = 2;
                break;
            }
            case 4: {// ring
                if (index_wear == 3 || index_wear == 9) {
                    b = index_wear;
                } else {
                    b = 3;
                }
                break;
            }
            case 5: {// chain
                b = 4;
                break;
            }
            case 6: {// shoes
                b = 8;
                break;
            }
            case 7: {// wing
                b = 10;
                break;
            }
            case 8:
            case 9:
            case 10:
            case 11: { // weapon
                b = 0;
                break;
            }
            case 15: {
                b = 11;
                break;
            }
            case 16: {
                b = 12;
                break;
            }
            case 21: {
                b = 13;
                break;
            }
            case 22: {
                b = 14;
                break;
            }
            case 23: {
                b = 15;
                break;
            }
            case 24: {
                b = 17;
                break;
            }
            case 25: {
                b = 16;
                break;
            }
            case 26: {
                b = 18;
                break;
            }
        }
        if (b == -1) {
            Service.send_notice_box(conn, "Có lỗi xảy ra, hãy thử lại");
            return;
        }
        if (item.wear[b] == null) {
            temp3.name = ItemTemplate3.item.get(temp3.id).getName() + " [Khóa]";
            item.wear[b] = temp3;
            item.remove(3, index_bag, 1);
        } else {
            Item3 buffer = item.wear[b];
            temp3.name = ItemTemplate3.item.get(temp3.id).getName() + " [Khóa]";
            item.wear[b] = temp3;
            item.remove(3, index_bag, 1);
            if (buffer.id != 3593 && buffer.id != 3599 && buffer.id != 3596) {
                item.add_item_bag3(buffer);
            }
        }
        if (b == 11) {
            fashion = Part_fashion.get_part(this);
        }
        item.char_inventory(4);
        item.char_inventory(7);
        item.char_inventory(3);
        Service.send_wear(this);
        Service.send_char_main_in4(conn.p);
        	MapService.change_flag(conn.p.map, conn.p, typepk);
        MapService.update_in4_2_other_inside(this.map, this);
    }

    public void plus_point(Message m) throws IOException {
        byte type = m.reader().readByte();
        byte index = m.reader().readByte();
        short value = 1;
        try {
            value = m.reader().readShort();
        } catch (IOException e) {
        }
        if (isdie || value < 0) {
            return;
        }
        if (type == 1) {
            if (kynang >= value) {
                if (skill_point[index] == 0 && skills[index].mLvSkill[0].LvRe > this.level) {
                    Service.send_notice_box(conn, "Level quá thấp!");
                    return;
                }
                if (skill_point[index] == 0 && (index == 19 || index == 20)) {
                    boolean dont_have_book_skill_110 = true;
                    switch (clazz) {
                        case 0: {
                            if (item.total_item_by_id(3, 4577) > 0 && index == 19) {
                                dont_have_book_skill_110 = false;
                                for (int i = 0; i < item.bag3.length; i++) {
                                    if (item.bag3[i] != null && item.bag3[i].id == 4577) {
                                        item.bag3[i] = null;
                                    }
                                }
                            } else if (item.total_item_by_id(3, 4578) > 0 && index == 20) {
                                dont_have_book_skill_110 = false;
                                for (int i = 0; i < item.bag3.length; i++) {
                                    if (item.bag3[i] != null && item.bag3[i].id == 4578) {
                                        item.bag3[i] = null;
                                    }
                                }
                            }
                            break;
                        }
                        case 1: {
                            if (item.total_item_by_id(3, 4579) > 0 && index == 19) {
                                dont_have_book_skill_110 = false;
                                for (int i = 0; i < item.bag3.length; i++) {
                                    if (item.bag3[i] != null && item.bag3[i].id == 4579) {
                                        item.bag3[i] = null;
                                    }
                                }
                            } else if (item.total_item_by_id(3, 4580) > 0 && index == 20) {
                                dont_have_book_skill_110 = false;
                                for (int i = 0; i < item.bag3.length; i++) {
                                    if (item.bag3[i] != null && item.bag3[i].id == 4580) {
                                        item.bag3[i] = null;
                                    }
                                }
                            }
                            break;
                        }
                        case 2: {
                            if (item.total_item_by_id(3, 4581) > 0 && index == 19) {
                                dont_have_book_skill_110 = false;
                                for (int i = 0; i < item.bag3.length; i++) {
                                    if (item.bag3[i] != null && item.bag3[i].id == 4581) {
                                        item.bag3[i] = null;
                                    }
                                }
                            } else if (item.total_item_by_id(3, 4582) > 0 && index == 20) {
                                dont_have_book_skill_110 = false;
                                for (int i = 0; i < item.bag3.length; i++) {
                                    if (item.bag3[i] != null && item.bag3[i].id == 4582) {
                                        item.bag3[i] = null;
                                    }
                                }
                            }
                            break;
                        }
                        case 3: {
                            if (item.total_item_by_id(3, 4583) > 0 && index == 19) {
                                dont_have_book_skill_110 = false;
                                for (int i = 0; i < item.bag3.length; i++) {
                                    if (item.bag3[i] != null && item.bag3[i].id == 4583) {
                                        item.bag3[i] = null;
                                    }
                                }
                            } else if (item.total_item_by_id(3, 4584) > 0 && index == 20) {
                                dont_have_book_skill_110 = false;
                                for (int i = 0; i < item.bag3.length; i++) {
                                    if (item.bag3[i] != null && item.bag3[i].id == 4584) {
                                        item.bag3[i] = null;
                                    }
                                }
                            }
                            break;
                        }
                    }
                    if (dont_have_book_skill_110) {
                        Service.send_notice_box(conn, "Chưa có sách kỹ năng để học!");
                        return;
                    }
                    item.char_inventory(3);
                }
                if (skill_point[index] + value > skills[index].mLvSkill.length - 5) {
                    value = (short) (skills[index].mLvSkill.length - 5 - skill_point[index]);
                    kynang -= value;
                    skill_point[index] = (byte) (skills[index].mLvSkill.length - 5);
                } else {
                    kynang -= value;
                    skill_point[index] += value;
                }
                while (skill_point[index] > 1 && this.level < (skills[index].mLvSkill[skill_point[index] - 1].LvRe - 1)) {
                    kynang += 1;
                    skill_point[index] -= 1;
                }
                MapService.update_in4_2_other_inside(this.map, this);
                Service.send_char_main_in4(this);
            }
        } else if (type == 0) {
            if (tiemnang >= value) {
                switch (index) {
                    case 0: {
                        if ((point1 + value) <= 2000000000) {
                            point1 += value;
                            tiemnang -= value;
                        }
                        break;
                    }
                    case 1: {
                        if ((point2 + value) <= 32_000) {
                            point2 += value;
                            tiemnang -= value;
                        }
                        break;
                    }
                    case 2: {
                        if ((point3 + value) <= 2000000000) {
                            point3 += value;
                            tiemnang -= value;
                        }
                        break;
                    }
                    case 3: {
                        if ((point4 + value) <= 2000000000) {
                            point4 += value;
                            tiemnang -= value;
                        }
                        break;
                    }
                }
                MapService.update_in4_2_other_inside(this.map, this);
                Service.send_char_main_in4(this);
            }
        }
    }

    public void friend_process(Message m2) throws IOException {
        byte type = m2.reader().readByte();
        String name = m2.reader().readUTF();
        switch (type) {
            case 0: { // request friend
                for (Friend name0 : list_friend) {
                    if (name0.name.equals(name)) {
                        Service.send_notice_box(conn, (name + " đã có trong danh sách bạn bè!"));
                        return;
                    }
                }
                Player p0 = Map.get_player_by_name(name);
                if (p0 == null) {
                    Service.send_notice_box(conn, "Có lỗi xảy ra, hãy thử lại!");
                } else {
                    Message m = new Message(35);
                    m.writer().writeByte(0);
                    m.writer().writeUTF(this.name);
                    p0.conn.addmsg(m);
                    m.cleanup();
                }
                break;
            }
            case 1: { // accept
                Player p0 = Map.get_player_by_name(name);
                if (p0 == null) {
                    Service.send_notice_box(conn, "Có lỗi xảy ra, hãy thử lại!");
                } else {
                    boolean is_fr = false;
                    for (int i = 0; i < list_friend.size(); i++) {
                        if (list_friend.get(i).name.equals(name)) {
                            is_fr = true;
                            break;
                        }
                    }
                    if (!is_fr) {
                        Friend temp = new Friend();
                        temp.name = p0.name;
                        temp.level = p0.level;
                        temp.head = p0.head;
                        temp.hair = p0.hair;
                        temp.eye = p0.eye;
                        temp.itemwear = new ArrayList<>();
                        for (int i = 0; i < p0.item.wear.length; i++) {
                            Item3 it = p0.item.wear[i];
                            if (it != null && (i == 0 || i == 1 || i == 6 || i == 7 || i == 10)) {
                                Part_player part = new Part_player();
                                part.type = it.type;
                                part.part = it.part;
                                temp.itemwear.add(part);
                            }
                        }
                        list_friend.add(temp);
                        //
                        Message m = new Message(35);
                        m.writer().writeByte(1);
                        m.writer().writeUTF(temp.name);
                        m.writer().writeByte(temp.head);
                        m.writer().writeByte(temp.eye);
                        m.writer().writeByte(temp.hair);
                        m.writer().writeShort(temp.level);
                        m.writer().writeByte(temp.itemwear.size()); // part
                        for (Part_player part : temp.itemwear) {
                            m.writer().writeByte(part.part);
                            m.writer().writeByte(part.type);
                        }
                        m.writer().writeByte(1); // type onl
                        if (p0.myclan != null) {
                            m.writer().writeShort(p0.myclan.icon);
                            m.writer().writeUTF(p0.myclan.name_clan_shorted);
                            m.writer().writeByte(p0.myclan.get_mem_type(p0.name));
                        } else {
                            m.writer().writeShort(-1); // clan
                        }
                        conn.addmsg(m);
                        m.cleanup();
                        // //
                        temp = new Friend();
                        temp.name = this.name;
                        temp.level = level;
                        temp.head = head;
                        temp.hair = hair;
                        temp.eye = eye;
                        temp.itemwear = new ArrayList<>();
                        for (int i = 0; i < item.wear.length; i++) {
                            Item3 it = item.wear[i];
                            if (it != null && (i == 0 || i == 1 || i == 6 || i == 7 || i == 10)) {
                                Part_player part = new Part_player();
                                part.type = it.type;
                                part.part = it.part;
                                temp.itemwear.add(part);
                            }
                        }
                        //
                        p0.list_friend.add(temp);
                        //
                        m = new Message(35);
                        m.writer().writeByte(1);
                        m.writer().writeUTF(temp.name);
                        m.writer().writeByte(temp.head);
                        m.writer().writeByte(temp.eye);
                        m.writer().writeByte(temp.hair);
                        m.writer().writeShort(temp.level);
                        m.writer().writeByte(temp.itemwear.size()); // part
                        for (Part_player part : temp.itemwear) {
                            m.writer().writeByte(part.part);
                            m.writer().writeByte(part.type);
                        }
                        m.writer().writeByte(1); // type onl
                        if (this.myclan != null) {
                            m.writer().writeShort(this.myclan.icon);
                            m.writer().writeUTF(this.myclan.name_clan_shorted);
                            m.writer().writeByte(this.myclan.get_mem_type(this.name));
                        } else {
                            m.writer().writeShort(-1); // clan
                        }
                        p0.conn.addmsg(m);
                        m.cleanup();
                        if (conn.p.nv_tinh_tu[0] == 33 && conn.p.nv_tinh_tu[1] < conn.p.nv_tinh_tu[2]) {
							conn.p.nv_tinh_tu[1]++;
						}
                    } else {
                        Service.send_notice_box(conn, name + " đã là bạn");
                    }
                }
                break;
            }
            case 2: {
                Player p0 = Map.get_player_by_name(name);
                if (p0 == null) {
                    Service.send_notice_box(conn, "Có lỗi xảy ra, hãy thử lại!");
                } else {
                    Service.send_notice_box(p0.conn, (conn.p.name + " cự tuyệt lời mời kết bạn của bạn kkk!"));
                }
                break;
            }
            case 3: { // remove friend
                for (int i = 0; i < list_friend.size(); i++) {
                    Friend temp = list_friend.get(i);
                    if (temp.name.equals(name)) {
                        list_friend.remove(temp);
                        break;
                    }
                }
                break;
            }
            case 4: {
                Friend.send_list_friend(this);
                break;
            }
        }
    }

    public int get_pramskill_byid(byte index_skill, byte id_param) {
        int param = 0;
        for (Option temp : skills[index_skill].mLvSkill[body.get_skill_point(index_skill) - 1].minfo) {
            if (temp.id == id_param) {
                param += temp.getParam(0);
            }
        }
        return param;
    }

    public void set_in4() throws IOException {
        this.already_setup = true;
        load_skill();
        suckhoe = 30000;
        pk = 0;
        typepk = -1;
        pointpk = 0;
        hp = body.get_max_hp();
        mp = body.get_max_mp();
        fashion = Part_fashion.get_part(this);
        type_use_mount = -1;
        id_item_rebuild = -1;
        is_use_mayman = false;
        id_use_mayman = -1;
        item_replace = -1;
        item_replace2 = -1;
        id_buffer_126 = -1;
        id_index_126 = -1;
        id_medal_is_created = -1;
        fusion_material_medal_id = -1;
        id_remove_time_use = -1;
        id_horse = -1;
        is_create_wing = false;
        id_wing_split = -1;
        in4_auto = new byte[]{0, 50, 50, 0, 0, 0, 0, 0, 0, 0, 0};
        my_store = new ArrayList<>();
        my_store_name = "";
        id_select_mo_ly = -1;
        id_hop_ngoc = -1;
        list_thao_kham_ngoc = new ArrayList<>();
        this.it_wedding = Wedding.get_obj(this.name);
        if (this.it_wedding != null) {
            this.item.wear[23] = this.it_wedding.it;
        }
        //
        Map[] map_enter = Map.get_map_by_id(map.map_id);
        int d = 0;
        while ((d < (map_enter[d].maxzone - 1)) && map_enter[d].players.size() >= map_enter[d].maxplayer) {
            d++;
        }
        map = map_enter[d];
        //
        this.is_changemap = false;
        this.x_old = this.x;
        this.y_old = this.y;
        //
        HashMap<Short, Integer> hm = new HashMap<>();
        for (Item47 it : item.bag47) {
            if (it.category == 7) {
                if (!hm.containsKey(it.id)) {
                    hm.put(it.id, (int) it.quantity);
                } else {
                    int quant = hm.get(it.id);
                    hm.replace(it.id, quant, quant + it.quantity);
                }
            }
        }
        HashMap<Short, Integer> hm2 = new HashMap<>();
        for (Item47 it : item.bag47) {
            if (it.category == 4) {
                if (!hm2.containsKey(it.id)) {
                    hm2.put(it.id, (int) it.quantity);
                } else {
                    int quant = hm2.get(it.id);
                    hm2.replace(it.id, quant, quant + it.quantity);
                }
            }
        }
        item.bag47.clear();
        for (Entry<Short, Integer> entry : hm.entrySet()) {
            Item47 temp = new Item47();
            temp.category = 7;
            temp.id = entry.getKey();
            int quant_ = entry.getValue();
            temp.quantity = (short) quant_;
            item.bag47.add(temp);
        }
        for (Entry<Short, Integer> entry : hm2.entrySet()) {
            Item47 temp = new Item47();
            temp.category = 4;
            temp.id = entry.getKey();
            int quant_ = entry.getValue();
            temp.quantity = (short) quant_;
            item.bag47.add(temp);
        }
        //
        item.char_inventory(4);
        item.char_chest(4);
        item.char_inventory(7);
        item.char_chest(7);
        item.char_inventory(3);
        item.char_chest(3);
    }

    public void update_wings_time() throws IOException {
        boolean check = false;
        for (int i = 0; i < item.bag3.length; i++) {
            Item3 it = item.bag3[i];
            if (it != null && it.type == 7 && it.time_use != 0) {
                if ((it.time_use - System.currentTimeMillis()) < 0) {
                    it.time_use = 0;
                    check = true;
                }
            }
        }
        if (check) {
            item.char_inventory(4);
            item.char_inventory(7);
            item.char_inventory(3);
        }
    }

    @SuppressWarnings("unchecked")
    public static void flush_all() {
        String query
                = "UPDATE `player` SET `level` = ?, `exp` = ?, `site` = ?, `body` = ?, `eff` = ?, `friend` = ?, `skill` = ?, `item4` = ?, "
                + "`item7` = ?, `item3` = ?, `itemwear` = ?, `giftcode` = ?, `enemies` = ?, `rms_save` = ?, `itembox4` = ?, "
                + "`itembox7` = ?, `itembox3` = ?, `pet` = ?, `medal_create_material` = ?, `point_active` = ?, `vang` = ?, "
                + "`kimcuong` = ?, `tiemnang` = ?, `kynang` = ?, `diemdanh` = ?, `chucphuc` = ?, `hieuchien` = ?, `chuyensinh` = ?, `dropnlmd` = ?, `typeexp` = ?, "
                + "`date` = ?, `point1` = ?, `point2` = ?, `point3` = ?, `point4` = ?  WHERE `id` = ?;";
        try ( Connection connection = SQL.gI().getConnection();  PreparedStatement ps = connection.prepareStatement(query)) {
            for (Map[] map : Map.entrys) {
                for (Map map0 : map) {
                    for (int i1 = 0; i1 < map0.players.size(); i1++) {
                        ps.clearParameters();
                        Player p0 = map0.players.get(i1);
                        //
                        ps.setInt(1, p0.level);
                        ps.setLong(2, p0.exp);
                        JSONArray jsar = new JSONArray();
                        jsar.add(p0.map.map_id);
                        jsar.add(p0.x);
                        jsar.add(p0.y);
                        ps.setNString(3, jsar.toJSONString());
                        jsar.clear();
                        jsar.add(p0.head);
                        jsar.add(p0.eye);
                        jsar.add(p0.hair);
                        ps.setNString(4, jsar.toJSONString());
                        jsar.clear();
                        for (int i = 0; i < p0.list_eff.size(); i++) {
                            EffTemplate temp = p0.list_eff.get(i);
                            if (temp.id != -126 && temp.id != -125) {
                                continue;
                            }
                            JSONArray jsar21 = new JSONArray();
                            jsar21.add(temp.id);
                            jsar21.add(temp.param);
                            long time = temp.time - System.currentTimeMillis();
                            jsar21.add(time);
                            jsar.add(jsar21);
                        }
                        ps.setNString(5, jsar.toJSONString());
                        jsar.clear();
                        for (int i = 0; i < p0.list_friend.size(); i++) {
                            JSONArray js12 = new JSONArray();
                            Friend temp = p0.list_friend.get(i);
                            js12.add(temp.name);
                            js12.add(temp.level);
                            js12.add(temp.head);
                            js12.add(temp.hair);
                            js12.add(temp.eye);
                            JSONArray js = new JSONArray();
                            for (Part_player part : temp.itemwear) {
                                JSONArray js2 = new JSONArray();
                                js2.add(part.type);
                                js2.add(part.part);
                                js.add(js2);
                            }
                            js12.add(js);
                            jsar.add(js12);
                        }
                        ps.setNString(6, jsar.toJSONString());
                        jsar.clear();
                        for (int i = 0; i < 21; i++) {
                            jsar.add(p0.skill_point[i]);
                        }
                        ps.setNString(7, jsar.toJSONString());
                        jsar.clear();
                        //
                        for (Item47 it : p0.item.bag47) {
                            if (it.category == 4) {
                                JSONArray jsar2 = new JSONArray();
                                jsar2.add(it.id);
                                jsar2.add(it.quantity);
                                jsar.add(jsar2);
                            }
                        }
                        ps.setNString(8, jsar.toJSONString());
                        jsar.clear();
                        //
                        for (Item47 it : p0.item.bag47) {
                            if (it.category == 7) {
                                JSONArray jsar2 = new JSONArray();
                                jsar2.add(it.id);
                                jsar2.add(it.quantity);
                                jsar.add(jsar2);
                            }
                        }
                        ps.setNString(9, jsar.toJSONString());
                        jsar.clear();
                        //
                        for (int i = 0; i < p0.item.bag3.length; i++) {
                            Item3 temp = p0.item.bag3[i];
                            if (temp != null) {
                                JSONArray jsar2 = new JSONArray();
                                jsar2.add(temp.id);
                                jsar2.add(temp.clazz);
                                jsar2.add(temp.type);
                                jsar2.add(temp.level);
                                jsar2.add(temp.icon);
                                jsar2.add(temp.color);
                                jsar2.add(temp.part);
                                jsar2.add(temp.islock ? 1 : 0);
                                jsar2.add(temp.tier);
                                JSONArray jsar3 = new JSONArray();
                                for (int j = 0; j < temp.op.size(); j++) {
                                    JSONArray jsar4 = new JSONArray();
                                    jsar4.add(temp.op.get(j).id);
                                    jsar4.add(temp.op.get(j).getParam(0));
                                    jsar3.add(jsar4);
                                }
                                jsar2.add(jsar3);
                                jsar2.add(temp.time_use);
                                jsar.add(jsar2);
                            }
                        }
                        ps.setNString(10, jsar.toJSONString());
                        jsar.clear();
                        //
                        for (int i = 0; i < p0.item.wear.length - 1; i++) {
                            Item3 temp = p0.item.wear[i];
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
                                jsar.add(jsar2);
                            }
                        }
                        ps.setNString(11, jsar.toJSONString());
                        jsar.clear();
                        //
                        for (int i = 0; i < p0.giftcode.size(); i++) {
                            jsar.add(p0.giftcode.get(i));
                        }
                        ps.setNString(12, jsar.toJSONString());
                        jsar.clear();
                        for (int i = 0; i < p0.list_enemies.size(); i++) {
                            jsar.add(p0.list_enemies.get(i));
                        }
                        ps.setNString(13, jsar.toJSONString());
                        jsar.clear();
                        for (int i = 0; i < p0.rms_save.length; i++) {
                            JSONArray js = new JSONArray();
                            for (int i2 = 0; i2 < p0.rms_save[i].length; i2++) {
                                js.add(p0.rms_save[i][i2]);
                            }
                            jsar.add(js);
                        }
                        ps.setNString(14, jsar.toJSONString());
                        jsar.clear();
                        //
                        for (int i = 0; i < p0.item.box47.size(); i++) {
                            if (p0.item.box47.get(i).category == 4) {
                                JSONArray jsar2 = new JSONArray();
                                jsar2.add(p0.item.box47.get(i).id);
                                jsar2.add(p0.item.box47.get(i).quantity);
                                jsar.add(jsar2);
                            }
                        }
                        ps.setNString(15, jsar.toJSONString());
                        jsar.clear();
                        //
                        for (int i = 0; i < p0.item.box47.size(); i++) {
                            if (p0.item.box47.get(i).category == 7) {
                                JSONArray jsar2 = new JSONArray();
                                jsar2.add(p0.item.box47.get(i).id);
                                jsar2.add(p0.item.box47.get(i).quantity);
                                jsar.add(jsar2);
                            }
                        }
                        ps.setNString(16, jsar.toJSONString());
                        jsar.clear();
                        //
                        for (int i = 0; i < p0.item.box3.length; i++) {
                            Item3 temp = p0.item.box3[i];
                            if (temp != null) {
                                JSONArray jsar2 = new JSONArray();
                                jsar2.add(temp.id);
                                jsar2.add(temp.clazz);
                                jsar2.add(temp.type);
                                jsar2.add(temp.level);
                                jsar2.add(temp.icon);
                                jsar2.add(temp.color);
                                jsar2.add(temp.part);
                                jsar2.add(temp.islock ? 1 : 0);
                                jsar2.add(temp.tier);
                                JSONArray jsar3 = new JSONArray();
                                for (int j = 0; j < temp.op.size(); j++) {
                                    JSONArray jsar4 = new JSONArray();
                                    jsar4.add(temp.op.get(j).id);
                                    jsar4.add(temp.op.get(j).getParam(0));
                                    jsar3.add(jsar4);
                                }
                                jsar2.add(jsar3);
                                jsar2.add(temp.time_use);
                                jsar.add(jsar2);
                            }
                        }
                        ps.setNString(17, jsar.toJSONString());
                        jsar.clear();
                        //
                        for (int i = 0; i < p0.mypet.size(); i++) {
                            JSONArray js1 = new JSONArray();
                            js1.add(p0.mypet.get(i).level);
                            js1.add(p0.mypet.get(i).type);
                            js1.add(p0.mypet.get(i).icon);
                            js1.add(p0.mypet.get(i).nframe);
                            js1.add(p0.mypet.get(i).color);
                            js1.add(p0.mypet.get(i).grown);
                            js1.add(p0.mypet.get(i).maxgrown);
                            js1.add(p0.mypet.get(i).point1);
                            js1.add(p0.mypet.get(i).point2);
                            js1.add(p0.mypet.get(i).point3);
                            js1.add(p0.mypet.get(i).point4);
                            js1.add(p0.mypet.get(i).maxpoint);
                            js1.add(p0.mypet.get(i).exp);
                            js1.add(p0.mypet.get(i).is_follow ? 1 : 0);
                            js1.add(p0.mypet.get(i).is_hatch ? 1 : 0);
                            js1.add(p0.mypet.get(i).time_born);
                            JSONArray js2 = new JSONArray();
                            for (int i2 = 0; i2 < p0.mypet.get(i).op.size(); i2++) {
                                JSONArray js3 = new JSONArray();
                                js3.add(p0.mypet.get(i).op.get(i2).id);
                                js3.add(p0.mypet.get(i).op.get(i2).param);
                                js3.add(p0.mypet.get(i).op.get(i2).maxdam);
                                js2.add(js3);
                            }
                            js1.add(js2);
                            jsar.add(js1);
                        }
                        ps.setNString(18, jsar.toJSONString());
                        jsar.clear();
                        //
                        for (int i = 0; i < p0.medal_create_material.length; i++) {
                            jsar.add(p0.medal_create_material[i]);
                        }
                        ps.setNString(19, jsar.toJSONString());
                        jsar.clear();
                        for (int i = 0; i < p0.point_active.length; i++) {
                            jsar.add(p0.point_active[i]);
                        }
                        ps.setNString(20, jsar.toJSONString());
                        jsar.clear();
                        //
                        ps.setLong(21, p0.vang);
                        ps.setInt(22, p0.kimcuong);
                        ps.setInt(23, p0.tiemnang);
                        ps.setShort(24, p0.kynang);
                        ps.setByte(25, p0.diemdanh);
                        ps.setByte(26, p0.chucphuc);
                        ps.setInt(27, p0.hieuchien);
                        ps.setInt(27, p0.chuyensinh);
                        ps.setByte(28, p0.type_exp);
                        ps.setNString(29, p0.date.toString());
                        ps.setInt(30, p0.point1);
                        ps.setInt(31, p0.point2);
                        ps.setInt(32, p0.point3);
                        ps.setInt(33, p0.point4);
                        ps.setInt(34, p0.id);
                        ps.setInt(27, p0.dropnlmd);
                        ps.addBatch();
                        if (i1 % 50 == 0) {
                            ps.executeBatch();
                        }
                    }
                }
            }
            // }
            ps.executeBatch();
            connection.commit();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void change_map_di_buon(Player p) throws IOException {
        p.is_changemap = false;
        Map[] mbuffer = Map.get_map_by_id(p.map.map_id);
        if (mbuffer != null) {
            MapService.leave(p.map, p);
            p.map = mbuffer[p.map.maxzone];
            MapService.enter(p.map, p);
            if (p.pet_di_buon != null) {
                Message mout = new Message(8);
                mout.writer().writeShort(p.pet_di_buon.index);
                for (int i = 0; i < p.map.players.size(); i++) {
                    Player p0 = p.map.players.get(i);
                    if (p0 != null) {
                        p0.conn.addmsg(mout);
                    }
                }
                mout.cleanup();
                //
                Pet_di_buon_manager.remove(p.pet_di_buon.name);
                p.pet_di_buon = null;
            }
        } else {
            Service.send_notice_box(p.conn, "Có lỗi xảy ra khi chuyển map");
        }
    }

    public void process_eff_ngoc_kham(Item3 it, int index) throws IOException {
        for (int i = 0; i < it.op.size(); i++) {
            if (it.op.get(i).id == (58 + index)) {
                // ngoc hon nguyen
                if (it.op.get(i).getParam(0) >= 382 && it.op.get(i).getParam(0) <= 386) {
                    conn.p.time_atk_ngoc_hon_nguyen++;
                    if (conn.p.time_atk_ngoc_hon_nguyen >= ((387 - it.op.get(i).getParam(0)) * 5)) {
                        conn.p.time_atk_ngoc_hon_nguyen = 0;
                        EffTemplate eff = conn.p.get_eff(32000);
                        if (eff == null) {
                            conn.p.add_eff(32000, ((387 - it.op.get(i).getParam(0)) * 5), 3000);
                            // } else {
                            // eff.param = Math.min(((387 - it.op.get(i).getParam(0)) * 5), eff.param);
                        }
                    }
                }
                // ngoc phong ma
                switch (it.op.get(i).getParam(0)) {
                    case 397: {
                        if (15 > Util.random(1000) && hp < ((body.get_max_hp() / 10) * 2)) {
                            EffTemplate eff = conn.p.get_eff(32000);
                            if (eff == null) {
                                conn.p.add_eff(32001, 1, 5000);
                            }
                            show_eff_p(20, 5000);
                        }
                        break;
                    }
                    case 398: {
                        if (20 > Util.random(1000) && hp < ((body.get_max_hp() / 10) * 3)) {
                            EffTemplate eff = conn.p.get_eff(32000);
                            if (eff == null) {
                                conn.p.add_eff(32001, 2, 5000);
                            }
                            show_eff_p(20, 5000);
                        }
                        break;
                    }
                    case 399: {
                        if (25 > Util.random(1000) && hp < ((body.get_max_hp() / 10) * 4)) {
                            EffTemplate eff = conn.p.get_eff(32000);
                            if (eff == null) {
                                conn.p.add_eff(32001, 5, 5000);
                            }
                            show_eff_p(20, 5000);
                        }
                        break;
                    }
                    case 400: {
                        if (35 > Util.random(1000) && hp < ((body.get_max_hp() / 10) * 5)) {
                            EffTemplate eff = conn.p.get_eff(32000);
                            if (eff == null) {
                                conn.p.add_eff(32001, 7, 5000);
                            }
                            show_eff_p(20, 5000);
                        }
                        break;
                    }
                    case 401: {
                        if (45 > Util.random(1000) && hp < ((body.get_max_hp() / 10) * 7)) {
                            EffTemplate eff = conn.p.get_eff(32000);
                            if (eff == null) {
                                conn.p.add_eff(32001, 10, 5000);
                            }
                            show_eff_p(20, 5000);
                        }
                        break;
                    }
                }
            }
        }
    }

    public void show_eff_p(int id_eff, int time) throws IOException {
        Message m = new Message(-49);
        m.writer().writeByte(2);
        m.writer().writeShort(0);
        m.writer().writeByte(0);
        m.writer().writeByte(0);
        m.writer().writeByte(id_eff);
        m.writer().writeShort(this.id);
        m.writer().writeByte(0);
        m.writer().writeByte(0);
        m.writer().writeInt(time);
        MapService.send_msg_player_inside(this.map, this, m, true);
        m.cleanup();
    }

    public synchronized static void update_coin(String name, int coin_exchange) throws IOException {
        String query = "SELECT `coin` FROM `account` WHERE `user` = '" + name + "' LIMIT 1;";
        int coin_old = 0;
        try ( Connection connection = SQL.gI().getConnection();  Statement ps = connection.createStatement();  ResultSet rs = ps.executeQuery(query)) {
            rs.next();
            coin_old = rs.getInt("coin");
            if (coin_old + coin_exchange < 0) {
                return;
            }
            coin_old += coin_exchange;
            if (ps.executeUpdate("UPDATE `account` SET `coin` = " + coin_old + " WHERE `user` = '" + name + "'") == 1) {
                connection.commit();
            }
        } catch (SQLException e) {
        }
    }

    public synchronized int get_tongnap() throws IOException {
        int result = 0;
        String query = "SELECT `tongnap` FROM `account` WHERE `user` = '" + conn.user + "' LIMIT 1;";
        try ( Connection connection = SQL.gI().getConnection();  Statement ps = connection.createStatement();  ResultSet rs = ps.executeQuery(query)) {
            rs.next();
            result = rs.getInt("tongnap");
        } catch (SQLException e) {
            result = 0;
        }
        return result;
    }


      
    public boolean check_moc_nap(int i) {
        return conn.moc_nap.contains(i + "");
    }
    public void update_point_arena(int i) throws IOException {
		Member_ChienTruong temp = ChienTruong.gI().get_infor_register(this.name);
		if (temp != null) {
			temp.point += i;
			this.pointarena += i;
			Service.send_health(this);
			Message m = new Message(-95);
			m.writer().writeByte(0);
			m.writer().writeShort(this.id);
			m.writer().writeShort(this.pointarena);
			this.conn.addmsg(m);
			m.cleanup();
		}
	}
	
	

public void update_chucphuc(int value) {
    this.chucphuc = (byte) value;
    try (Connection conn = SQL.gI().getConnection();
         PreparedStatement ps = conn.prepareStatement("UPDATE player SET chucphuc = ? WHERE id = ?")) {
        ps.setInt(1, value);
        ps.setInt(2, this.id);
        ps.executeUpdate();
        conn.commit();
    } catch (SQLException e) {
        e.printStackTrace();
    }
}
    

    public void update_id_name(int i) {
         try ( Connection connection = SQL.gI().getConnection();
                 Statement ps = connection.createStatement()) {
            ps.execute("UPDATE `player` SET `id_name` = "+i+" WHERE `id` = "+ this.id+";");
            connection.commit();
        } catch (SQLException e) {
        }
         this.id_name = (short) i;
    }
}
