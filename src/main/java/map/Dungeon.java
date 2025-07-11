package map;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import client.Pet;
import client.Player;
import core.Manager;
import core.Service;
import core.Util;
import io.Message;
import io.Session;
import template.EffTemplate;
import template.Mob;
import template.Mob_Dungeon;
import template.Option_pet;

public class Dungeon {

    public static List<BXH_Dungeon_Finished> bxh_time_complete = new ArrayList<>();
    public Map template;
    public long time_live;
    public int state;
    private int mob_speed;
    private int wave;
    private int num_mob;
    private int num_mob_max;
    private int dame_buff;
    private int id_mob_in_wave;
    public int index_mob;
    private int hp;
    public String name_party;
    private byte mode_now;
    private List<Mob_Dungeon> mobs;

    public Dungeon() throws IOException {
        init();
    }

    private void init() throws IOException {
        Map temp = Map.get_map_dungeon(48);
        template = new Map(48, 0, temp.npc_name_data, temp.name, temp.typemap, temp.ismaplang, temp.showhs,
                temp.maxplayer, temp.maxzone, temp.vgos);
        template.mobs = new Mob_in_map[0];
        template.start_map();
        template.d = this;
        time_live = System.currentTimeMillis();
        index_mob = Manager.gI().size_mob;
        state = -1;
        wave = 1;
        mobs = new ArrayList<>();
        // num_boss = 0;
    }

    public void finish_dungeon() {
    }

    public void update() {
        if (this.index_mob > 65_000) {
            this.state = 7;
        }
        int time_now = getTime_live();
        try {
            if (this.state == -1) {
                if (time_now == 4) {
                    this.state = 0;
                    notice_all_player_in_dungeon("Hiệp 1 sẽ bắt đầu sau 10s");
                }
            } else if (this.state == 0) { // wait to begin
                if (time_now == 13) {
                    this.state = 1;
                }
            } else if (this.state == 1) { // wait at wave 20
                if (wave != 21) {
                    int dem = 0;
                    if (mobs.size() > 0) {
                        for (Mob_Dungeon mob_temp : mobs) {
                            for (Player p0 : template.players) {
                                Message m2 = new Message(17);
                                m2.writer().writeShort(p0.id);
                                m2.writer().writeShort(mob_temp.index_mob);
                                p0.conn.addmsg(m2);
                                m2.cleanup();
                                dem++;
                                if (dem > 10_000) {
                                    break;
                                }
                            }
                            if (dem > 10_000) {
                                break;
                            }
                        }
                    }
                    this.state = 2;
                    notice_all_player_in_dungeon("Hiệp " + (wave++) + " bắt đầu");
                    this.num_mob = num_mob_max;
                    mobs.clear();
                    if (wave <= 198) {
                        id_mob_in_wave = wave - 1;
                    } else {
                        id_mob_in_wave = Util.random(0, 198);
                    }
                } else {
                   
                    notice_all_player_in_dungeon("Vượt thành công, xin chúc mừng các đại hiệp, sẽ tự động thoát sau 30s");
                    this.num_mob = num_mob_max;
                    mobs.clear();
                    this.state = 8;
                    // cal prime
                    synchronized (Dungeon.bxh_time_complete) {
                        BXH_Dungeon_Finished bxh = new BXH_Dungeon_Finished(this.name_party, this.getTime_live());
                        Dungeon.bxh_time_complete.add(bxh);
                        //
                        List<BXH_Dungeon_Finished> buffer = new ArrayList<>();
                        List<Integer> list_sorted = new ArrayList<>();
                        for (BXH_Dungeon_Finished temp : Dungeon.bxh_time_complete) {
                            list_sorted.add(Integer.valueOf(temp.time));
                        }
                        Collections.sort(list_sorted);
                        Set<String> check = new HashSet<>();
                        for (int i = 0; i < list_sorted.size(); i++) {
                            for (int i1 = 0; i1 < Dungeon.bxh_time_complete.size(); i1++) {
                                BXH_Dungeon_Finished temp = Dungeon.bxh_time_complete.get(i1);
                                if (temp.time == list_sorted.get(i)) {
                                    if (!check.contains(temp.name)) {
                                        buffer.add(temp);
                                        check.add(temp.name);
                                    }
                                    Dungeon.bxh_time_complete.remove(temp);
                                    break;
                                }
                            }
                            if (buffer.size() > 9) {
                                break;
                            }
                        }
                        Dungeon.bxh_time_complete.clear();
                        Dungeon.bxh_time_complete.addAll(buffer);
                        buffer.clear();
                        check.clear();
                    }
                }
            } else if (this.state == 2) { // fight
                if (mobs.size() < num_mob_max) {
                    Mob_Dungeon mob = new Mob_Dungeon(this.index_mob++, Mob.entrys.get(id_mob_in_wave));
                    mob.x = 1308;
                    mob.y = 672 + Util.random(-25, 25);
                    mob.from_gate = 2;
                    if (this.wave >= 21) {
                        mob.hpmax = mob.template.hpmax * (this.wave / 10);
                    } else {
                        mob.hpmax = mob.template.hpmax;
                    }
                    mob.hp = mob.hpmax;
                    mobs.add(mob);
                  //  this.template.set_mob((Mob_in_map) mob); // ✅ Cực kỳ quan trọng: cho map biết mob tồn tại // ✅ ép kiểu về Mob_in_map // ✅ Cho map chứa mob để player đánh được
                    Message m = new Message(4);
                    m.writer().writeByte(1);
                    m.writer().writeShort(mob.template.mob_id);
                    m.writer().writeShort(mob.index_mob); // index
                    m.writer().writeShort(mob.x);
                    m.writer().writeShort(mob.y);
                    m.writer().writeByte(-1);
                    for (int i = 0; i < template.players.size(); i++) {
                        Player p0 = template.players.get(i);
                        p0.conn.addmsg(m);
                    }
                    m.cleanup();
                }
                update_mob();
            } else if (this.state == 4) {
                // this.state = 6;
            } else if (this.state == 5) {
                for (int i = 0; i < mobs.size(); i++) {
                    Mob_Dungeon mob = mobs.get(i);
                    Message m2 = new Message(17);
                    m2.writer().writeShort(-1);
                    m2.writer().writeShort(mob.index_mob);
                    for (int i2 = 0; i2 < template.players.size(); i2++) {
                        Player p0 = template.players.get(i2);
                        p0.conn.addmsg(m2);
                    }
                    m2.cleanup();
                }
                mobs.clear();
                for (int i2 = 0; i2 < template.players.size(); i2++) {
                    Player p0 = template.players.get(i2);
                    Service.send_notice_box(p0.conn,
                            "Thất bại:\n Kết quả : thời gian vượt ải : " + getTime_live() + "s\nTự thoát sau 5s");
                }
                notice_all_player_in_dungeon("Thiên thạch đã vỡ, vượt phó bản thất bại!");
                this.state = 6;
            } else if (this.state == 6) {
                this.state = 7;
                //
                try {
                    Thread.sleep(5000L);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } else if (this.state == 7) {
                for (int i = 0; i < template.players.size(); i++) {
                    Player p0 = template.players.get(i);
                    
                     Player p = p0;
                 if (p.quest_daily[0] == 6 && p.quest_daily[3] < p.quest_daily[4]) {
                        p.quest_daily[3]++;
                    }
                    
                    p0.x = 432;
                    p0.y = 354;
                    Map[] map_enter = Map.get_map_by_id(1);
                    int d = 0;
                    while ((d < (map_enter[d].maxzone - 1)) && map_enter[d].players.size() >= map_enter[d].maxplayer) {
                        d++;
                    }
                    p0.map = map_enter[d];
                    //
                    p0.is_changemap = false;
                    p0.x_old = p0.x;
                    p0.y_old = p0.y;
                    MapService.enter(p0.map, p0);
                    //
                    p0.dungeon = null;
                }
                this.template.stop_map();
                this.template.d = null;
                DungeonManager.remove_list(this);
            } else if (this.state >= 8 && this.state <= 38) {
                if (this.state == 38) {
                    this.state = -2;
                }
                this.state++;
                this.state = -2;
            } else if (this.state == -2) {
                this.state = 7;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void notice_all_player_in_dungeon(String s) throws IOException {
        Message m = new Message(53);
        m.writer().writeUTF(s);
        m.writer().writeByte(0);
        for (int i = 0; i < template.players.size(); i++) {
            Player p0 = template.players.get(i);
            p0.conn.addmsg(m);
        }
        m.cleanup();
    }

    private void update_mob() throws IOException {
        for (int i = 0; i < mobs.size(); i++) {
            Mob_Dungeon mob = mobs.get(i);
            if (!mob.isdie) {
                mob_act(mob);
            }
        }
    }

    private void mob_act(Mob_Dungeon mob) throws IOException {
        int distance = Util.random(1, mob_speed);
        boolean is_atk = true;
        switch (mob.from_gate) {
            case 1: {
                if (!(Math.abs((mob.y + distance) - 672) < mob_speed)) {
                    mob.y += distance;
                    is_atk = false;
                }
                break;
            }
            case 2: {
                if (!(Math.abs((mob.x - distance) - 684) < mob_speed)) {
                    mob.x -= distance;
                    is_atk = false;
                }
                break;
            }
            case 3: {
                if (!(Math.abs((mob.y - distance) - 672) < mob_speed)) {
                    mob.y -= distance;
                    is_atk = false;
                }
                break;
            }
            case 4: {
                if (!(Math.abs((mob.x + distance) - 684) < mob_speed)) {
                    mob.x += distance;
                    is_atk = false;
                }
                break;
            }
        }
        if (is_atk) {
            long dame = (mob.hpmax / 100) * (mob.template.level / 2);
            dame += this.wave * 10;
            dame *= dame_buff;
            Message m = new Message(10);
            m.writer().writeByte(1);
            m.writer().writeShort(mob.index_mob);
            m.writer().writeInt((int)Math.min(mob.hp,Integer.MAX_VALUE));
            m.writer().writeByte(2);
            m.writer().writeByte(1);
            m.writer().writeShort(32001); // index thien thach
            m.writer().writeInt((int)dame); // dame mob
            this.hp -= dame;
            if (this.hp < 0) {
                this.hp = 0;
                this.state = 5;
            }
            m.writer().writeInt(this.hp);
            m.writer().writeByte(6); // id skill mob
            m.writer().writeByte(0);
            for (int i = 0; i < template.players.size(); i++) {
                Player p0 = template.players.get(i);
                p0.conn.addmsg(m);
            }
            m.cleanup();
        } else {
            Message m = new Message(4);
            m.writer().writeByte(1);
            m.writer().writeShort(mob.template.mob_id);
            m.writer().writeShort(mob.index_mob); // index
            m.writer().writeShort(mob.x);
            m.writer().writeShort(mob.y);
            m.writer().writeByte(-1);
            for (int i = 0; i < template.players.size(); i++) {
                Player p0 = template.players.get(i);
                p0.conn.addmsg(m);
            }
            m.cleanup();
        }
    }

    public int getTime_live() {
        return (int) ((System.currentTimeMillis() - time_live + 2) / 1000);
    }

    public void send_map_data(Player p) throws IOException {
        // thien thach
        Message m = new Message(4);
        m.writer().writeByte(2);
        m.writer().writeShort(0);
        m.writer().writeShort(32001); // index
        m.writer().writeShort(684);
        m.writer().writeShort(672);
        m.writer().writeByte(-1);
        p.conn.addmsg(m);
        m.cleanup();
    }

    public void send_in4_npc(Session conn, Message m2) throws IOException {
        short type = m2.reader().readShort();
        if (type == 32001) {
            Message m = new Message(-44);
            m.writer().writeShort(32001); // index
            m.writer().writeUTF("Thiên thạch");
            m.writer().writeInt(this.hp);
            m.writer().writeInt(this.hp);
            m.writer().writeShort(0);
            m.writer().writeShort(684);
            m.writer().writeShort(672);
            m.writer().writeByte(2);
            m.writer().writeByte(2);
            m.writer().writeUTF("");
            m.writer().writeShort(25);
            m.writer().writeByte(1);
            m.writer().writeShort(17);
            conn.addmsg(m);
            m.cleanup();
        }
    }

    public void send_mob_in4(Session conn, int index) throws IOException {
        int index_ = -1;
        for (int i = 0; i < mobs.size(); i++) {
            Mob_Dungeon mob = mobs.get(i);
            if (mob.index_mob == index) {
                index_ = i;
                break;
            }
        }
        if (index_ > -1) {
            Mob_Dungeon temp = mobs.get(index_);
            Message m = new Message(7);
            m.writer().writeShort(index);
            m.writer().writeByte(temp.template.level);
            m.writer().writeShort(temp.x);
            m.writer().writeShort(temp.y);
            m.writer().writeInt((int) Math.min(temp.hp,Integer.MAX_VALUE));
            m.writer().writeInt((int) Math.min(temp.hpmax,Integer.MAX_VALUE));
            m.writer().writeByte(20); // id skill monster (Spec: 32, ...)
            m.writer().writeInt(-4);
            m.writer().writeShort(temp.x);
            m.writer().writeShort(temp.y);
            m.writer().writeShort(-1); // clan monster
            m.writer().writeByte(1);
            m.writer().writeByte(this.mob_speed / 25); // speed
            m.writer().writeByte(0);
            m.writer().writeUTF("");
            m.writer().writeLong(-11111);
            m.writer().writeByte(0); // color name 1: blue, 2: yellow
            conn.addmsg(m);
            m.cleanup();
        }
    }

    public class BXH_Dungeon_Finished {

        public String name;
        public int time;

        public BXH_Dungeon_Finished(String name, int time_live) {
            this.name = name;
            this.time = time_live;
        }
    }

    public int getWave() {
        return wave;
    }

    public void setWave(int i) {
        wave = i;
    }

    public void setMode(int mode) throws IOException {
        String name = "";
        this.mode_now = (byte) mode;
        switch (mode) {
            case 0: {
                hp = 50_000;
                mob_speed = 50;
                num_mob_max = 20;
                num_mob = num_mob_max;
                dame_buff = 1;
                name = "Easy";
                break;
            }
            case 1: {
                hp = 50_000;
                mob_speed = 75;
                num_mob_max = 30;
                num_mob = num_mob_max;
                dame_buff = 2;
                name = "Normal";
                break;
            }
            case 2: {
                hp = 50_000;
                mob_speed = 100;
                num_mob_max = 50;
                num_mob = num_mob_max;
                dame_buff = 4;
                name = "Hard";
                break;
            }
            case 3: {
                hp = 50_000;
                mob_speed = 150;
                num_mob_max = 75;
                num_mob = num_mob_max;
                dame_buff = 8;
                name = "Nightmare";
                break;
            }
            case 4: {
                hp = 50_000;
                mob_speed = 200;
                num_mob_max = 100;
                num_mob = num_mob_max;
                dame_buff = 16;
                name = "Hell";
                break;
            }
        }
        if (this.wave == 20) {
            notice_all_player_in_dungeon("Đội trưởng đã chọn độ khó " + name);
        }
    }

    public Mob_Dungeon get_mob(int id) {
        for (int i = 0; i < this.mobs.size(); i++) {
            if (this.mobs.get(i).index_mob == id) {
                return this.mobs.get(i);
            }
        }
        return null;
    }

    public synchronized void fire_mob(Map map, Mob_Dungeon mob, Player p, byte index_skill, int dame_atk)
        throws IOException {

    // ✅ Check người chơi phải thuộc đúng dungeon
    if (p.dungeon != this || !this.template.players.contains(p)) {
        return;
    }

    if (mob.isdie) {
        Message m2 = new Message(17);
        m2.writer().writeShort(p.id);
        m2.writer().writeShort(mob.index_mob);
        p.conn.addmsg(m2);
        m2.cleanup();
        return;
    }

    // ✅ phần còn lại giữ nguyên...
        //
        int dame = Util.random((dame_atk * 9) / 10, dame_atk);
        //
        EffTemplate ef = null;
        long cr = p.body.get_crit();
        ef = p.get_eff(33);
        if (ef != null) {
            cr += ef.param;
        }
        boolean crit = cr > Util.random(0, 10000);
        boolean pierce = false;
        if (crit) {
            dame *= 2;
        }
        if (!crit) {
            long pier = p.body.get_pierce();
            ef = p.get_eff(36);
            if (ef != null) {
                pier += ef.param;
            }
            if (pier > Util.random(0, 10000)) {
                pierce = true;
            }
        }
        if (!pierce) {
            long dameresist = (this.wave * this.wave * this.wave / 2);
            // if (mob.is_boss) {
            // dameresist *= 2;
            // }
            dame -= dameresist;
            if (dame < 0) {
                dame = 1;
            }
        }
        if (mob.color_name != 0) {
            dame = (dame * 8) / 10;
            switch (mob.color_name) {
                case 1: {
                    if (p.clazz == 2) {
                        dame /= 2;
                    }
                    break;
                }
                case 2: {
                    if (p.clazz == 3) {
                        dame /= 2;
                    }
                    break;
                }
                case 4: {
                    if (p.clazz == 0) {
                        dame /= 2;
                    }
                    break;
                }
                case 5: {
                    if (p.clazz == 1) {
                        dame /= 2;
                    }
                    break;
                }
            }
        }
        if (mob.template.mob_id == 167 || mob.template.mob_id == 168 || mob.template.mob_id == 169
                || mob.template.mob_id == 170 || mob.template.mob_id == 171 || mob.template.mob_id == 172) {
            dame = 1;
        }
        if (15 > Util.random(0, 100)) { // mob get miss
            dame = 0;
        }
        if (dame < 0) {
            dame = 0;
        }
        mob.hp -= dame;
        // if mob die
        if (mob.hp < 0) {
            mob.hp = 0;
            // mob die
            if (!mob.isdie) {
                mob.isdie = true;
                p.xptutien +=1;
                // send p outside
                Message m2 = new Message(17);
                m2.writer().writeShort(p.id);
                m2.writer().writeShort(mob.index_mob);
                for (int i = 0; i < map.players.size(); i++) {
                    Player p2 = map.players.get(i);
                    if (!((Math.abs(p2.x - p.x) < 200) && (Math.abs(p2.y - p.y) < 200))) {
                        p2.conn.addmsg(m2);
                    }
                }
                m2.cleanup();
            }
            p.tutien[2]+= Util.random(1,10);
            p.point_active[1] += (this.wave / 5);
            this.num_mob--;
            if (this.num_mob == 0) {
                this.state = 1;
            }
        }
        // attached in4
        Message m = new Message(9);
        m.writer().writeShort(p.id);
        m.writer().writeByte(index_skill);
        m.writer().writeByte(1);
        m.writer().writeShort(mob.index_mob);
        m.writer().writeInt(dame); // dame
        m.writer().writeInt((int) Math.min(mob.hp,Integer.MAX_VALUE)); // hp mob after
        if (dame > 0 && crit) {
            m.writer().writeByte(1); // size color show
            //
            m.writer().writeByte(4); // 1: xuyen giap, 2:hut hp, 3: hut mp, 4: chi mang, 5: phan don
            m.writer().writeInt(dame); // par
            //
        } else if (dame > 0 && pierce) {
            m.writer().writeByte(1); // size color show
            //
            m.writer().writeByte(1); // 1: xuyen giap, 2:hut hp, 3: hut mp, 4: chi mang, 5: phan don
            m.writer().writeInt(dame); // par
            //
        } else {
            m.writer().writeByte(0);
        }
        m.writer().writeInt((int) Math.min(p.hp,Integer.MAX_VALUE));
        m.writer().writeInt((int) Math.min(p.mp,Integer.MAX_VALUE));
        m.writer().writeByte(11); // 1: green, 5: small white 9: big white, 10: st dien, 11: st bang
        m.writer().writeInt(0); // dame plus
        MapService.send_msg_player_inside(map, p, m, true);
        m.cleanup();
        // exp
        int expup = 0;
        expup = dame; // tinh exp
        ef = p.get_eff(-125);
        if (ef != null) {
            expup += (expup * (ef.param / 100)) / 100;
        }
        p.update_Exp(expup, true);
        // exp clan
        if (p.myclan != null) {
            int exp_clan = dame / 10_000;
            if (exp_clan < 1 || exp_clan > 50) {
                exp_clan = 1;
            }
            p.myclan.update_exp(exp_clan);
        }
        // pet attack
        if (!mob.isdie && p.pet_follow) {
            Pet my_pet = null;
            for (Pet pett : p.mypet) {
                if (pett.is_follow) {
                    my_pet = pett;
                    break;
                }
            }
            if (my_pet != null && my_pet.grown > 0) {
                int a1 = 0;
                int a2 = 1;
                for (Option_pet temp : my_pet.op) {
                    if (temp.maxdam > 0) {
                        a1 = temp.param;
                        a2 = temp.maxdam;
                        break;
                    }
                }
                int dame_pet = Util.random(a1, a2+1) - (this.wave * this.wave * 5);
                if (dame_pet < 0) {
                    dame_pet = 1;
                }
                if (((mob.hp - dame_pet) > 0) && (p.pet_atk_speed < System.currentTimeMillis()) && (a2 > 1)) {
                    p.pet_atk_speed = System.currentTimeMillis() + 1500L;
                    m = new Message(84);
                    m.writer().writeByte(2);
                    m.writer().writeShort(p.id);
                    m.writer().writeByte(1);
                    m.writer().writeByte(1);
                    m.writer().writeShort(mob.index_mob);
                    m.writer().writeInt(dame_pet);
                    mob.hp -= dame_pet;
                    m.writer().writeInt((int) Math.min(mob.hp,Integer.MAX_VALUE));
                    m.writer().writeInt((int) Math.min(p.hp,Integer.MAX_VALUE));
                    m.writer().writeInt((int) Math.min(p.body.get_max_hp(),Integer.MAX_VALUE));
                    p.conn.addmsg(m);
                    m.cleanup();
                }
            }
        }
    }

    public void send_mob_move_when_exit(Player p) throws IOException {
        Message m = new Message(4);
        for (int i = 0; i < this.mobs.size(); i++) {
            Mob_Dungeon mob = this.mobs.get(i);
            m.writer().writeByte(1);
            m.writer().writeShort(mob.template.mob_id);
            m.writer().writeShort(mob.index_mob); // index
            m.writer().writeShort(mob.x);
            m.writer().writeShort(mob.y);
            m.writer().writeByte(-1);
        }
        for (int i = 0; i < template.players.size(); i++) {
            Player p0 = template.players.get(i);
            p0.conn.addmsg(m);
        }
        m.cleanup();
    }
}
