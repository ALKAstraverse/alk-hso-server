package event_daily;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import client.Clan;
import client.Pet;
import client.Player;
import core.Manager;
import core.Service;
import core.Util;
import io.Message;
import io.Session;
import map.Map;
import map.MapService;
import map.Mob_in_map;
import map.Vgo;
import template.Item3;

public class LoiDai {

    private static List<LoiDai> list_loidai = new ArrayList<>();
    private static List<Player> list_loidai_name = new ArrayList<>();
    public static int time_state = 120;
    public static int state = 0;
    public Player p1;
    public Player p2;
    public int time;
    public int state_ld;
    public Map map;
    public byte result = 0;
    public byte round;

    public LoiDai() {
        this.time = 60;
        this.state_ld = 0;
    }

    public synchronized static boolean check_register(Player p) {
        for (int i = 0; i < LoiDai.list_loidai_name.size(); i++) {
            if (LoiDai.list_loidai_name.get(i).name.equals(p.name)) {
                return true;
            }
        }
        return false;
    }

    public synchronized static void add_name_register(Player p) throws IOException {
        Player p0 = new Player(new Session(new Socket()), p.id);
        p0.setup();
        p0.set_in4();
        LoiDai.list_loidai_name.add(p0);
        
        	if (p.nv_tinh_tu[0] == 25 && p.nv_tinh_tu[1] < p.nv_tinh_tu[2]) {
			p.nv_tinh_tu[1]++;
		}
    }

    public synchronized static void update_state() throws IOException {
        LoiDai.time_state--;
        if (LoiDai.time_state == -1) {
            LoiDai.state++;
            if (LoiDai.state == 3) {
                if (LoiDai.list_loidai.size() < 1 && LoiDai.list_loidai_name.size() > 1) {
                    LoiDai.state = 1;
                    // LoiDai.init_atk();
                } else {
                    LoiDai.state = 0;
                }
            }
            switch (LoiDai.state) {
                case 2: {
                }
                case 0: {
                    LoiDai.list_loidai_name.clear();
                    LoiDai.time_state = 60 * 5;
                    Manager.gI().chatKTGprocess("@Server : Lôi đài đã mở đăng ký mới, mọi người hãy vào đăng ký");
                    break;
                }
                case 1: {
                    LoiDai.time_state = -2;
                    new Thread(() -> {
                        LoiDai.init_atk();
                    }).start();
                    break;
                }
            }
        } else if (LoiDai.state == 1 && LoiDai.list_loidai.size() < 1 && LoiDai.list_loidai_name.size() > 1) {
            LoiDai.time_state = 60 * 5;
            LoiDai.state = 2;
        } else if (LoiDai.state == 1 && LoiDai.list_loidai.size() < 1 && LoiDai.list_loidai_name.size() <= 1) {
            // LoiDai.time_state = 0;
            LoiDai.time_state = 60 * 5;
            LoiDai.state = 2;
        }
    }

    public synchronized void update_atk() throws IOException {
        this.time--;
        // System.out.println("state " + this.state_ld + " time " + this.time);
        if (this.time >= 5 && this.time % 5 == 0) {
            this.update_time_atk();
        }
        if (this.time <= -1 && this.round < 3) {
            if (this.time < 0) {
                this.time = 0;
            }
            this.state_ld++;
            if (this.state_ld == 2) {
                this.state_ld = 0;
                if (!this.p1.isdie && !this.p2.isdie) {
                    if (50 > Util.random(100) // && this.round == 2 && this.result == 1
                            ) {
                        MapService.die_by_player(map, map.ld.p1, map.ld.p2);
                        this.result--;
                    } else {
                        MapService.die_by_player(map, map.ld.p2, map.ld.p1);
                        this.result++;
                    }
                }
            }
            switch (this.state_ld) {
                case 0: {
                    // System.out.println("round " + this.round + " result " + this.result);
                    if (this.p1.id == this.p2.id) {
						if (this.p1.is_nhanban) {
							this.result = -2;
						} else {
							this.result = 2;
						}
						
					}
                    if (this.round >= 3 || this.result == 2 || this.result == -2) {
                        if (this.result > 0) {
                            this.update_point_player(this.p2, this.p1);
                            for (int i = 0; i < map.players.size(); i++) {
                                Player p00 = map.players.get(i);
                                Service.send_notice_nobox_white(p00.conn, map.ld.p1.name + " dành chiến thắng chung cuộc");
                            }
                            LoiDai.remove_list_name(map.ld.p2);
                        } else {
                            this.update_point_player(this.p1, this.p2);
                            for (int i = 0; i < map.players.size(); i++) {
                                Player p00 = map.players.get(i);
                                Service.send_notice_nobox_white(p00.conn, map.ld.p2.name + " dành chiến thắng chung cuộc");
                            }
                            LoiDai.remove_list_name(map.ld.p1);
                        }
                        if (LoiDai.list_loidai_name.size() == 1) {
                            LoiDai.list_loidai_name.get(0).point_active[2] += 50;
                        }
                        this.time = 5;
                        this.state_ld = 2;
                    } else {
                        this.time = 15;
                    }
                    break;
                }
                case 1: {
                    
                    if (this.round >= 3 || this.result == 2 || this.result == -2) {
                          if (this.result > 0) {
                            this.update_point_player(this.p2, this.p1);
                            for (int i = 0; i < map.players.size(); i++) {
                                Player p00 = map.players.get(i);
                                Service.send_notice_nobox_white(p00.conn, map.ld.p1.name + " dành chiến thắng chung cuộc");
                            }
                            LoiDai.remove_list_name(map.ld.p2);
                        } else {
                            this.update_point_player(this.p1, this.p2);
                            for (int i = 0; i < map.players.size(); i++) {
                                Player p00 = map.players.get(i);
                                Service.send_notice_nobox_white(p00.conn, map.ld.p2.name + " dành chiến thắng chung cuộc");
                            }
                            LoiDai.remove_list_name(map.ld.p1);
                        }
                        if (LoiDai.list_loidai_name.size() == 1) {
                            LoiDai.list_loidai_name.get(0).point_active[2] += 50;
                        }
                        this.time = 5;
                        this.state_ld = 2;
                    } else {
                    
                    round++;
                    if (this.p1.isdie) {
                        this.p1.isdie = false;
                        this.p1.hp = this.p1.body.get_max_hp();
                        this.p1.mp = this.p1.body.get_max_mp();
                        Service.send_char_main_in4(this.p1);
                        Service.send_combo(this.p1.conn);
                        Service.usepotion(this.p1, 0, this.p1.hp);
                        Service.usepotion(this.p1, 1, this.p1.mp);
                    }
                    if (this.p2.isdie) {
                        this.p2.isdie = false;
                        this.p2.hp = this.p2.body.get_max_hp();
                        this.p2.mp = this.p2.body.get_max_mp();
                        Service.send_char_main_in4(this.p2);
                        Service.send_combo(this.p2.conn);
                        Service.usepotion(this.p2, 0, this.p2.hp);
                        Service.usepotion(this.p2, 1, this.p2.mp);
                    }
                    this.time = 60 * 2;
                    }
                    break;
                }
                case 3: {
                   
                    while (this.map.players.size() > 0) {
                        if (this.map.players.get(0).conn.connected) {
                            Vgo vgo = new Vgo();
                            vgo.id_map_go = 1;
                            vgo.x_new = 432;
                            vgo.y_new = 354;
                            this.map.players.get(0).hp = this.map.players.get(0).body.get_max_hp();
                            this.map.players.get(0).isdie = false;
                            this.map.players.get(0).change_map(this.map.players.get(0), vgo);
                        } else {
                            this.map.players.remove(0);
                        }
                    }
                    LoiDai.remove_loidai(this);
                    break;
                }
            }
            this.update_time_atk();
        } else if (this.state_ld == 1 && (this.round >= 3 || this.round < 0)) {
            if (this.result > 0) {
                this.update_point_player(this.p2, this.p1);
                for (int i = 0; i < map.players.size(); i++) {
                    Player p00 = map.players.get(i);
                    Service.send_notice_nobox_white(p00.conn, map.ld.p1.name + " dành chiến thắng chung cuộc");
                }
                LoiDai.remove_list_name(map.ld.p2);
            } else {
                this.update_point_player(this.p1, this.p2);
                for (int i = 0; i < map.players.size(); i++) {
                    Player p00 = map.players.get(i);
                    Service.send_notice_nobox_white(p00.conn, map.ld.p2.name + " dành chiến thắng chung cuộc");
                }
                LoiDai.remove_list_name(map.ld.p1);
            }
            if (LoiDai.list_loidai_name.size() == 1) {
                LoiDai.list_loidai_name.get(0).point_active[2] += 50;
            }
            this.time = 5;
            this.state_ld = 2;

        }
        //
        if (this.state_ld == 1) {
            update_nhanban_atk();
        }
    }

    private void update_nhanban_atk() throws IOException {
        if (this.p1.is_nhanban && !this.p1.isdie) {
            Player p0 = this.p1;
            int dame = (p0.body.get_dame_physical() + p0.body.get_dame_prop(1) + p0.body.get_dame_prop(2)
                    + p0.body.get_dame_prop(3) + p0.body.get_dame_prop(4)) / 2;
            MapService.fire_player(map, this.p2, this.p1, (byte) 0, dame, 0);
        }
        if (this.p2.is_nhanban && !this.p2.isdie) {
            Player p0 = this.p2;
            int dame = (p0.body.get_dame_physical() + p0.body.get_dame_prop(1) + p0.body.get_dame_prop(2)
                    + p0.body.get_dame_prop(3) + p0.body.get_dame_prop(4)) / 2;
            MapService.fire_player(map, this.p1, this.p2, (byte) 0, dame, 0);
        }

    }

    private synchronized static void remove_list_name(Player p22) {
        for (int i = 0; i < LoiDai.list_loidai_name.size(); i++) {
            if (LoiDai.list_loidai_name.get(i).id == p22.id) {
                LoiDai.list_loidai_name.remove(i);
                break;
            }
        }
    }

    private synchronized static void remove_loidai(LoiDai loiDai) {
        LoiDai.list_loidai.remove(loiDai);
         loiDai.map.stop_map();
    }

    private synchronized static void init_atk() {
        try {
            if (LoiDai.list_loidai_name.size() > 0) {
                int size_register = (int) Math.pow(2, Util.log_2(LoiDai.list_loidai_name.size()));
                if (size_register < 2) {
                    LoiDai.refresh();
                    return;
                }
                int i = 0;
                while (LoiDai.list_loidai_name.size() < size_register) {
                    Player p0 = new Player(new Session(new Socket()), LoiDai.list_loidai_name.get(i).id);
                    p0.setup();
                    p0.set_in4();
                    p0.is_nhanban = true;
                    p0.name = "(nb)" + p0.name;
                    LoiDai.list_loidai_name.add(p0);
                    i++;
                }
                Collections.shuffle(LoiDai.list_loidai_name);
                i = 2;
                while (i < LoiDai.list_loidai_name.size()) {
                    Player p01 = LoiDai.list_loidai_name.get(i);
                    Player p02 = LoiDai.list_loidai_name.get(i + 1);
                    if (p01.id == p02.id) {
                        LoiDai.list_loidai_name.set(i, LoiDai.list_loidai_name.get(i - 1));
                        LoiDai.list_loidai_name.set((i - 1), p01);
                    }
                    i += 2;
                }
                i = 0;
                while (i < LoiDai.list_loidai_name.size()) {
                    LoiDai temp = new LoiDai();
                    temp.p1 = LoiDai.list_loidai_name.get(i);
                    temp.p1.x = 280; // 360
                    temp.p1.y = 228;
                    temp.p1.typepk = 0;
                    temp.p2 = LoiDai.list_loidai_name.get(i + 1);
                    temp.p2.x = 440;
                    temp.p2.y = 228;
                    temp.p2.typepk = 0;
                    i += 2;
                    Map map_temp = Map.get_map_by_id(102)[0];
                    temp.map = new Map(102, 0, map_temp.npc_name_data, map_temp.name, map_temp.typemap, map_temp.ismaplang,
                            map_temp.showhs, map_temp.maxplayer, map_temp.maxzone, map_temp.vgos);
                    temp.map.mobs = new Mob_in_map[0];
                    temp.map.start_map();
                    temp.map.ld = temp;
                    temp.p1.map = temp.map;
                    temp.p2.map = temp.map;
                    MapService.enter(temp.map, temp.p1);
                    MapService.enter(temp.map, temp.p2);
                    temp.round = 0;
                    LoiDai.list_loidai.add(temp);
                }
                Manager.gI().chatKTGprocess("@Server : Lôi đài đang được diễn ra, mọi người hãy vào xem");
            } else {
                LoiDai.refresh();
            }
        } catch (IOException e) {
            LoiDai.refresh();
        }
    }

    private synchronized static void refresh() {
        time_state = 300;
        state = 0;
        for (int i = 0; i < LoiDai.list_loidai.size(); i++) {
            LoiDai.list_loidai.get(i).map.stop_map();
        }
        LoiDai.list_loidai.clear();
        LoiDai.list_loidai_name.clear();
    }

    public synchronized static String get_list_register() {
        String s = "";
        for (int i = 0; i < LoiDai.list_loidai_name.size(); i++) {
            s += (i + 1) + ". " + LoiDai.list_loidai_name.get(i).name + "\n";
        }
        return s.isEmpty() ? "Trống" : s;
    }

    public synchronized static String[] get_list_loidai_is_atk() {
        String[] list = new String[]{"Không có trận nào"};
        if (LoiDai.list_loidai.size() > 0) {
            list = new String[LoiDai.list_loidai.size()];
            for (int i = 0; i < list.length; i++) {
                list[i] = LoiDai.list_loidai.get(i).p1.name + " Lv " + LoiDai.list_loidai.get(i).p1.level + " vs "
                        + LoiDai.list_loidai.get(i).p2.name + " Lv " + LoiDai.list_loidai.get(i).p2.level;
            }
        }
        return list;
    }

    public synchronized static Map get_map_view_loidai(byte index) {
        for (int i = 0; i < LoiDai.list_loidai.size(); i++) {
            if (i == index) {
                return LoiDai.list_loidai.get(i).map;
            }
        }
        return null;
    }

    public synchronized static Map get_map_atk_loidai(Player p) {
        for (int i = 0; i < LoiDai.list_loidai.size(); i++) {
            if ((LoiDai.list_loidai.get(i).p1.name.equals(p.name) && !LoiDai.list_loidai.get(i).p1.is_nhanban)
                    || (LoiDai.list_loidai.get(i).p2.name.equals(p.name) && !LoiDai.list_loidai.get(i).p2.is_nhanban)) {
                return LoiDai.list_loidai.get(i).map;
            }
        }
        return null;
    }

    public static int size_register() {
        return LoiDai.list_loidai_name.size();
    }

    public static void send_in4(LoiDai ld, Player p, int id) throws IOException {
        Player p0 = null;
        if (ld.p1.id == id) {
            p0 = ld.p1;
        } else {
            p0 = ld.p2;
        }
        if (p0 != null) {
            int dem = 0;
            for (int i = 0; i < 11; i++) {
                if (i != 0 && i != 1 && i != 6 && i != 7 && i != 10) {
                    continue;
                }
                if (p0.item.wear[i] != null) {
                    dem++;
                }
            }
            Message m = new Message(5);
            m.writer().writeShort(p0.id);
            m.writer().writeUTF(p0.name);
            m.writer().writeShort(p0.x);
            m.writer().writeShort(p0.y);
            m.writer().writeByte(p0.clazz);
            m.writer().writeByte(-1);
            m.writer().writeByte(p0.head);
            m.writer().writeByte(p0.eye);
            m.writer().writeByte(p0.hair);
            m.writer().writeShort(p0.level);
//            m.writer().writeInt(p0.hp);
//            m.writer().writeInt(p0.body.get_max_hp());

        m.writer().writeInt ((int) Math.min(p.hp, Integer.MAX_VALUE));
        m.writer().writeInt((int) Math.min(p.body.get_max_hp(), Integer.MAX_VALUE));
            m.writer().writeByte(p0.typepk);
            m.writer().writeShort(p0.pointpk);
            m.writer().writeByte(dem);
            //
            for (int i = 0; i < p0.item.wear.length; i++) {
                if (i != 0 && i != 1 && i != 6 && i != 7 && i != 10) {
                    continue;
                }
                Item3 temp = p0.item.wear[i];
                if (temp != null) {
                    m.writer().writeByte(temp.type);
                    if (i == 10 && p0.item.wear[14] != null
                            && (p0.item.wear[14].id >= 4638 && p0.item.wear[14].id <= 4648)) {
                        m.writer().writeByte(p0.item.wear[14].part);
                    } else {
                        m.writer().writeByte(temp.part);
                    }
                    m.writer().writeByte(3);
                    m.writer().writeShort(-1);
                    m.writer().writeShort(-1);
                    m.writer().writeShort(-1);
                    m.writer().writeShort(-1); // eff
                }
            }
            //
            if (p0.myclan != null) {
                m.writer().writeShort(p0.myclan.icon);
                m.writer().writeInt(Clan.get_id_clan(p0.myclan));
                m.writer().writeUTF(p0.myclan.name_clan_shorted);
                m.writer().writeByte(p0.myclan.get_mem_type(p0.name));
            } else {
                m.writer().writeShort(-1); // clan
            }
            if (p0.pet_follow) {
                for (Pet temp : p0.mypet) {
                    if (temp.is_follow) {
                        m.writer().writeByte(temp.type); // type
                        m.writer().writeByte(temp.icon); // icon
                        m.writer().writeByte(temp.nframe); // nframe
                        break;
                    }
                }
            } else {
                m.writer().writeByte(-1); // pet
            }
            m.writer().writeByte(p0.fashion.length);
            for (int i = 0; i < p0.fashion.length; i++) {
                m.writer().writeByte(p0.fashion[i]);
            }
            //
            m.writer().writeShort(-1);
            m.writer().writeByte(p0.type_use_mount);
            m.writer().writeBoolean(false);
            m.writer().writeByte(1);
            m.writer().writeByte(0);
            m.writer().writeShort(Service.get_id_mat_na(p0)); // mat na
            m.writer().writeByte(1); // paint mat na trc sau
            m.writer().writeShort(Service.get_id_phiphong(p0)); // phi phong
            m.writer().writeShort(Service.get_id_weapon(p0)); // weapon
            m.writer().writeShort(p0.id_horse);
            m.writer().writeShort(Service.get_id_hair(p0)); // hair
            m.writer().writeShort(Service.get_id_wing(p0)); // wing
            m.writer().writeShort(p0.id_name); // body
            m.writer().writeShort(-1); // leg
            m.writer().writeShort(-1); // bienhinh
            p.conn.addmsg(m);
            m.cleanup();
        }
    }

    public static void update_atk_when_someone_die(LoiDai ld) throws IOException {
        synchronized (ld.map) {
            if (ld.p1.isdie) {
                ld.result--;
            } else {
                ld.result++;
            }
            ld.time = 1;
//            ld.update_atk();
        }
    }

    private void update_point_player(Player p_lose, Player p_win) {
        int point_delta = p_lose.point_active[2] - p_win.point_active[2];
        if (point_delta == 0) {
            point_delta = 30;
        } else if (point_delta < 0) {
            point_delta = Math.abs(point_delta);
            point_delta = 30 - ((point_delta > 15) ? 15 : point_delta);
        } else {
            point_delta = 30 + ((point_delta > 15) ? 15 : point_delta);
        }
        update_point_player_2(p_lose, -point_delta);
        update_point_player_2(p_win, point_delta);
    }

    private void update_point_player_2(Player p, int par) {
        Player p0 = Map.get_player_by_id(p.id);
        if (p0 != null) {
            p0.point_active[2] += par;
        } else {
            p.point_active[2] += par;
        }
    }

    public void update_time_atk() throws IOException {
        Message m = new Message(-104);
        m.writer().writeByte(1);
        m.writer().writeByte(1);
        m.writer().writeShort(this.time);
        m.writer().writeUTF(((this.state_ld == 1) ? ("Lôi Đài : Hiệp " + this.round) : ("Lôi Đài : Nghỉ ")));
        for (int i = 0; i < this.map.players.size(); i++) {
            this.map.players.get(i).conn.addmsg(m);
        }
        m.cleanup();
    }

    public static void close() {
        for (int i = 0; i < LoiDai.list_loidai.size(); i++) {
            LoiDai.list_loidai.get(i).map.stop_map();
        }
    }

    public synchronized static String get_notice_break_time() {
        String s = "thời gian nghỉ ngơi\nHãy quay lại sau " + (LoiDai.time_state) + "s nữa";
        if (LoiDai.list_loidai.size() < 1 && LoiDai.list_loidai_name.size() > 1) {
            s = "thời gian nghỉ ngơi\nTrận đấu tiếp theo bắt đầu sau " + (LoiDai.time_state)
                    + "s nữa\nDanh sách tham gia :\n";
            for (int i = 0; i < LoiDai.list_loidai_name.size(); i++) {
                s += (i + 1) + ". " + LoiDai.list_loidai_name.get(i).name + "\n";
            }
        } else if (LoiDai.list_loidai_name.size() == 1) {
            s = "thời gian nghỉ ngơi\nMở đăng ký tiếp theo bắt đầu sau " + (LoiDai.time_state)
                    + "s nữa\nNgười vừa chiến thắng :\n" + LoiDai.list_loidai_name.get(0).name;
        }
        return s;
    }
}
