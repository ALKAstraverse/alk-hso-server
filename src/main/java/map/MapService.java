package map;

import ai.NhanBan;
import ai.Player_Nhan_Ban;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map.Entry;
import client.Clan;
import client.Pet;
import client.Player;
import core.Log;
import core.Manager;
import core.MenuController;
import core.Service;
import core.Util;
import event.BossEvent;
import event_daily.ChienTruong;
import event_daily.LoiDai;
import io.Message;
import io.Session;
import template.EffTemplate;
import template.Item3;
import template.LvSkill;
import template.MiNuong;
import template.Mob;
import template.Mob_Dungeon;
import template.Mob_MoTaiNguyen;
import template.Option;
import template.Option_pet;
import template.Pet_di_buon;
import template.Pet_di_buon_manager;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class MapService {

    public static void enter(Map map, Player p) {
        synchronized (map) {
            map.players.add(p);
        }
        p.change_new_date();
        //
        try {
            // if (map.map_id != 48) {
            if (map.zone_id == map.maxzone) {
                MapService.change_flag(map, p, -1);
            }
            map.send_map_data(p);
            Service.send_char_main_in4(p);
            Service.send_combo(p.conn);
            Service.send_point_pk(p);
            Service.send_health(p);
            Service.send_wear(p);
            MapService.change_flag(map, p, p.typepk);
            // }
            if (p.party != null) {
                p.party.sendin4();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        p.other_player_inside.clear();
        p.other_mob_inside.clear();
        p.other_mob_inside_update.clear();
    }

    public static void leave(Map map, Player p) {
        synchronized (map) {
            map.players.remove(p);
        }
        if (Manager.gI().bossTG.map.equals(map)) {
            Manager.gI().bossTG.time_enter_map.put(p.name, (System.currentTimeMillis() + 30_000L));
        }
        try {
            if (map.ld != null) {
                Message m = new Message(-104);
                m.writer().writeByte(1);
                m.writer().writeByte(1);
                m.writer().writeShort(0);
                m.writer().writeUTF("");
                p.conn.addmsg(m);
                m.cleanup();
                if (map.ld.p1.id != p.id && map.ld.p2.id != p.id) {
                    m = new Message(8);
                    m.writer().writeShort(p.id);
                    send_msg_player_inside(map, p, m, false);
                    m.cleanup();
                }
                p.typepk = -1;
            } else {
                Message m = new Message(8);
                m.writer().writeShort(p.id);
                send_msg_player_inside(map, p, m, false);
                m.cleanup();
                	if (p.minuong != null && Math.abs(p.x - p.minuong.mob.x) < 100 && Math.abs(p.y - p.minuong.mob.y) < 100) {
					p.minuong.map = null;
					//
					Message m2 = new Message(8);
					m2.writer().writeShort(p.minuong.mob.index);
					for (int i = 0; i < map.players.size(); i++) {
						map.players.get(i).conn.addmsg(m2);
					}
					m2.cleanup();
				}
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        p.list_npc.clear();
    }

    public static void send_msg_player_inside(Map map, Player p, Message m, boolean included) {
        for (int i = 0; i < map.players.size(); i++) {
            Player p0 = map.players.get(i);
            if (p0 != null && ((Math.abs(p0.x - p.x) < 200 && Math.abs(p0.y - p.y) < 200)
                    || Map.is_map__load_board_player(map.map_id)) && (included || (p.id != p0.id))) {
                p0.conn.addmsg(m);
            }
        }
    }

    public static void update_in4_2_other_inside(Map map, Player p) throws IOException {
        for (int i = 0; i < map.players.size(); i++) {
            Player p0 = map.players.get(i);
            if (p0.id != p.id && ((Math.abs(p0.x - p.x) < 200 && Math.abs(p0.y - p.y) < 200)
                    || Map.is_map__load_board_player(map.map_id))) {
                MapService.send_in4_other_char(map, p0, p);
            }
        }
    }

    public static void send_move(Map map, Player p, Message m2) throws IOException {
        short x_new = m2.reader().readShort();
        short y_new = m2.reader().readShort();
        
		Session conn = p.conn;
		if (conn.p.nv_tinh_tu[0] == 17 && conn.p.nv_tinh_tu[1] < conn.p.nv_tinh_tu[2]) {
			conn.p.nv_tinh_tu[1] += (Math.abs(x_new - p.y) + Math.abs(y_new - p.x));
		}
        
        
        
        int speed_ = Player.SPEED + ((p.conn.zoomlv > 2) ? 12 : 0);
        speed_ += (p.type_use_mount != -1 ? ((p.conn.zoomlv > 2) ? 9 : 7) * 2 : 0);

        if ((Math.abs(x_new - p.x) > speed_) || (Math.abs(y_new - p.y) > speed_)) {
//            p.is_atk = false;
//            return;
        }
        boolean changeee = false;
        p.is_atk = true;
        p.x = x_new;
        p.y = y_new;
        if (p.is_changemap) {
            for (Vgo vgo : map.vgos) {
                if (Math.abs(vgo.x_old - p.x) < 40 && Math.abs(vgo.y_old - p.y) < 40) {

                    boolean ch = true;
                    if (Map.is_map_chien_truong(map.map_id)) {
                        switch (map.map_id) {
                            case 54: {
                                if (vgo.id_map_go == 53 && p.typepk != 5) {
                                    ch = false;
                                }
                                break;
                            }
                            case 56: {
                                if (vgo.id_map_go == 55 && p.typepk != 2) {
                                    ch = false;
                                }
                                break;
                            }
                            case 58: {
                                if (vgo.id_map_go == 57 && p.typepk != 4) {
                                    ch = false;
                                }
                                break;
                            }
                            case 60: {
                                if (vgo.id_map_go == 59 && p.typepk != 1) {
                                    ch = false;
                                }
                                break;
                            }
                        }
                    }
                    if (ch) {
                        p.change_map(p, vgo);	changeee = true;
                    }
                    return;
                }
            }
        } else if (!(Math.abs(p.x_old - p.x) < 45 && Math.abs(p.y_old - p.y) < 45)) {
            p.is_changemap = true;
        }
        //
        if (map.map_id != 50) {
            Message m = new Message(4);
            m.writer().writeByte(0);
            m.writer().writeShort(0);
            m.writer().writeShort(p.id);
            m.writer().writeShort(p.x);
            m.writer().writeShort(p.y);
            m.writer().writeByte(-1);
            //
            MapService.update_inside_player(map, m, p);
            //
            m.cleanup();
        }
        if (p.pet_di_buon != null && p.pet_di_buon.id_map == p.map.map_id && p.map.zone_id == p.map.maxzone) {
            if (p.pet_di_buon.time_move < System.currentTimeMillis()) {
                p.pet_di_buon.time_move = System.currentTimeMillis() + 1000L;
                if (Math.abs(p.pet_di_buon.x - p.x) < (85 * p.pet_di_buon.speed)
                        && Math.abs(p.pet_di_buon.y - p.y) < (85 * p.pet_di_buon.speed)) {
                    p.pet_di_buon.x = p.x;
                    p.pet_di_buon.y = p.y;
                    if (p.pet_di_buon.speed != 1 && p.pet_di_buon.time_skill < System.currentTimeMillis()) {
                        p.pet_di_buon.speed = 1;
                        //
                        Message mm = new Message(7);
                        mm.writer().writeShort(p.pet_di_buon.index);
                        mm.writer().writeByte((byte) 120);
                        mm.writer().writeShort(p.pet_di_buon.x);
                        mm.writer().writeShort(p.pet_di_buon.y);
                        mm.writer().writeInt(p.pet_di_buon.hp);
                        mm.writer().writeInt(p.pet_di_buon.hp_max);
                        mm.writer().writeByte(0);
                        mm.writer().writeInt(-1);
                        mm.writer().writeShort(-1);
                        mm.writer().writeByte(1);
                        mm.writer().writeByte(p.pet_di_buon.speed);
                        mm.writer().writeByte(0);
                        mm.writer().writeUTF(p.pet_di_buon.name);
                        mm.writer().writeLong(-11111);
                        mm.writer().writeByte(4);
                        for (int i = 0; i < map.players.size(); i++) {
                            Player p0 = map.players.get(i);
                            if (p0 != null) {
                                p0.conn.addmsg(mm);
                            }
                        }
                        mm.cleanup();
                    }
                }
                Message m22 = new Message(4);
                m22.writer().writeByte(1);
                m22.writer().writeShort(131);
                m22.writer().writeShort(p.pet_di_buon.index);
                m22.writer().writeShort(p.pet_di_buon.x);
                m22.writer().writeShort(p.pet_di_buon.y);
                m22.writer().writeByte(-1);
                for (int i = 0; i < map.players.size(); i++) {
                    Player p0 = map.players.get(i);
                    if (p0 != null) {
                        p0.conn.addmsg(m22);
                    }
                }
                m22.cleanup();
            }
        }
        	if (!changeee && p.minuong != null && p.minuong.map.equals(p.map)) {
			if (p.minuong.time_move < System.currentTimeMillis() && Math.abs(p.x - p.minuong.mob.x) < 200
			      && Math.abs(p.y - p.minuong.mob.y) < 200 && p.minuong.power > 0) {
				p.minuong.power -= (Math.abs(p.y - p.minuong.mob.y) + Math.abs(p.x - p.minuong.mob.x));
				p.minuong.mob.x = p.x;
				p.minuong.mob.y = p.y;
				p.minuong.time_move = System.currentTimeMillis() + 2000L;
			}
			Message m4 = new Message(4);
			m4.writer().writeByte(1);
			m4.writer().writeShort(p.minuong.mob.template.mob_id);
			m4.writer().writeShort(p.minuong.mob.index);
			m4.writer().writeShort(p.minuong.mob.x);
			m4.writer().writeShort(p.minuong.mob.y);
			m4.writer().writeByte(-1);
			send_msg_player_inside(map, p, m4, true);
			m4.cleanup();
		}
    }

    private static void update_inside_player(Map map, Message m, Player p) throws IOException {
        Message m4 = new Message(4);
        for (Mob_in_map temp : map.mobs) {
            if (temp.isdie) {
                continue;
            }
            if ((Math.abs(temp.x - p.x) < 200 && Math.abs(temp.y - p.y) < 200)
                    || Map.is_map__load_board_player(map.map_id)) {
                synchronized (map) {
                    if (!temp.list_fight.contains(p)
                            && (temp.is_boss || (Math.abs(temp.x - p.x) < 50) && (Math.abs(temp.y - p.y) < 50))) {
                        temp.list_fight.add(p);
                    }
                }
                if (!p.other_mob_inside.containsKey(temp.index)) {
                    p.other_mob_inside.put(temp.index, true);
                }
                if (!p.other_mob_inside_update.containsKey(temp.index)) {
                    p.other_mob_inside_update.put(temp.index, false);
                }
                if (p.other_mob_inside.get(temp.index)) {
                    m4.writer().writeByte(1);
                    m4.writer().writeShort(temp.template.mob_id);
                    m4.writer().writeShort(temp.index);
                    m4.writer().writeShort(temp.x);
                    m4.writer().writeShort(temp.y);
                    m4.writer().writeByte(-1);
                    p.other_mob_inside.replace(temp.index, true, false);
                } else if (p.other_mob_inside_update.get(temp.index)) {
                    //
                    Service.mob_in4(p, temp.index);
                    p.other_mob_inside_update.replace(temp.index, true, false);
                }
            } else if (p.other_mob_inside_update.containsKey(temp.index) && !p.other_mob_inside_update.get(temp.index)) {
                p.other_mob_inside_update.replace(temp.index, false, true);
            }
        }
        //
        for (int i = 0; i < map.players.size(); i++) {
            Player p0 = map.players.get(i);
            if (p0.id == p.id) {
                continue;
            }
            if ((Math.abs(p0.x - p.x) < 200 && Math.abs(p0.y - p.y) < 200) || Map.is_map__load_board_player(map.map_id)) {
                if (!p.other_player_inside.containsKey(p0.id)) {
                    p.other_player_inside.put(p0.id, true);
                }
                p0.conn.addmsg(m);
                if (p.other_player_inside.get(p0.id)) {
                    m4.writer().writeByte(0);
                    m4.writer().writeShort(0);
                    m4.writer().writeShort(p0.id);
                    m4.writer().writeShort(p0.x);
                    m4.writer().writeShort(p0.y);
                    m4.writer().writeByte(-1);
                    //
                    p.other_player_inside.replace(p0.id, true, false);
                }
            } else if (p.other_player_inside.containsKey(p0.id)) {
                Message m3 = new Message(8);
                m3.writer().writeShort(p.id);
                p0.conn.addmsg(m3);
                m3.cleanup();
                m3 = new Message(8);
                m3.writer().writeShort(p0.id);
                p.conn.addmsg(m3);
                m3.cleanup();
                p.other_player_inside.remove(p0.id);
            }
        }
        if (m4.writer().size() > 0) {
            p.conn.addmsg(m4);
        }
        m4.cleanup();
    }

    public static Mob_in_map get_mob_by_index(Map map, int n) {
        if (map != null) {
            for (Mob_in_map m : map.mobs) {
                if (m.index == n) {
                    return m;
                }
            }
        }
        return null;
    }

    public static void mob_fire(Map map, Mob_in_map mob, Player p_target) throws IOException {
        if (mob.template.mob_id >= 89 && mob.template.mob_id <= 92) {
            return;
        }
        if (!mob.isdie && p_target != null) {
            // dame
            int dmob = Util.random(mob.level * 75, mob.level * 80);
            if (mob.level > 30 && mob.level <= 50) {
                dmob = (dmob * 13) / 10;
            } else if (mob.level > 50 && mob.level <= 70) {
                dmob = (dmob * 16) / 10;
            } else if (mob.level > 70 && mob.level <= 100) {
                dmob = (dmob * 19) / 10;
            } else if (mob.level > 100 && mob.level <= 600) {
                dmob = (dmob * 21) / 10;
            }
            if (dmob > 10) {
                dmob -= Util.random(0, dmob / 10);
            }
            // def enemy
            int def = p_target.body.get_def();
            EffTemplate ef = p_target.get_eff(15);
            if (ef != null) {
                def += (def * (ef.param / 100)) / 100;
            }
            dmob -= def;
            if (dmob <= 0) {
                dmob = 1;
            }
            if (mob.color_name != 0) {
                dmob *= 2;
            }
            if (p_target.getlevelpercent() < 0) {
                dmob *= 2;
            }
            int percent_stun = 0;
            int time_stun = 0;
            if (mob.is_boss) {
                switch (p_target.level - mob.level) {
                    case 5:
                    case 4:
                    case 3:
                    case 2:
                    case 1:
                    case 0: {
                        percent_stun = 15;
                        time_stun = 2;
                        break;
                    }
                    default: {
                        dmob *= 50;
                        percent_stun = 50;
                        time_stun = 5;
                        break;
                    }
                }
            }
            p_target.hp -= dmob;
            if (p_target.hp <= 0) {
                MapService.die_by_mob(map, p_target, mob);
                Player p0 = p_target;
                if (p0.minuong != null) {
					Mob_in_map temp = p0.minuong.mob;
					Message m22 = new Message(7);
					m22.writer().writeShort(temp.index);
					m22.writer().writeByte((byte) temp.level);
					m22.writer().writeShort(temp.x);
					m22.writer().writeShort(temp.y);
					m22.writer().writeInt(temp.hp);
					m22.writer().writeInt(temp.hpmax);
					m22.writer().writeByte(0); // id skill monster (Spec: 32, ...)
					m22.writer().writeInt(Mob_in_map.time_refresh);
					m22.writer().writeShort(-1); // clan monster
					m22.writer().writeByte(1);
					m22.writer().writeByte(1); // speed
					m22.writer().writeByte(0);
					m22.writer().writeUTF("");
					m22.writer().writeLong(-11111);
					m22.writer().writeByte(temp.color_name); // color name 1: blue, 2: yellow
					MapService.send_msg_player_inside(map, p0, m22, true);
					m22.cleanup();
					p0.minuong.owner = "";
					p0.minuong = null;
				}
            }
            Message m = new Message(10);
            m.writer().writeByte(1);
            m.writer().writeShort(mob.index);
            m.writer().writeInt(mob.hp);
            m.writer().writeByte(0);
            m.writer().writeByte(1);
            m.writer().writeShort(p_target.id);
            m.writer().writeInt(dmob); // dame mob
            m.writer().writeInt((int) Math.min(p_target.hp, Integer.MAX_VALUE));
            m.writer().writeByte(2); // id skill mob
            m.writer().writeByte(0);
            MapService.send_msg_player_inside(map, p_target, m, true);
            m.cleanup();
            // boss skill
            if (mob.is_boss && (percent_stun > Util.random(0, 100))) {
                int[] list = new int[]{7, 4, 5, 6};
                EffTemplate ef1 = p_target.get_eff(-121);
                EffTemplate ef2 = p_target.get_eff(-122);
                EffTemplate ef3 = p_target.get_eff(-123);
                EffTemplate ef4 = p_target.get_eff(-124);
                if (ef1 == null && ef2 == null && ef3 == null && ef4 == null) {
                    MapService.add_eff_stun(map, p_target, null, time_stun, list[Util.random(0, list.length)], mob.index);
                }
            }
        }
    }

    private static void add_eff_stun(Map map, Player p, Player p2, int time, int type, int index_mob)
            throws IOException {
        if (p2 == null) {
            p2 = p;
        }
        Message m = new Message(75);
        m.writer().writeByte(type);
        m.writer().writeByte(0);
        m.writer().writeShort(p2.id);
        m.writer().writeShort(time);
        m.writer().writeByte(0);
        m.writer().writeShort(p.id);
        MapService.send_msg_player_inside(map, p, m, true);
        m.cleanup();
        //
        int time_ = 1000 * time;
        switch (type) {
            case 7: {
                p2.add_eff(-124, 1000, time_);
                break;
            }
            case 4: {
                p2.add_eff(-123, 1000, time_);
                break;
            }
            case 5: {
                p2.add_eff(-122, 1000, time_);
                break;
            }
            case 6: {
                p2.add_eff(-121, 1000, time_);
                break;
            }
        }
    }

    private static void die_by_mob(Map map, Player p_target, Mob_in_map mob) throws IOException {
        p_target.dame_affect_special_sk = 0;
        p_target.hp = 0;
        p_target.isdie = true;
        MapService.change_flag(map, p_target, -1);
        //
        Message m = new Message(41);
        m.writer().writeShort(p_target.id);
        m.writer().writeShort(mob.index);
        m.writer().writeShort(-1); // point pk
        m.writer().writeByte(1); // type main object
        MapService.send_msg_player_inside(map, p_target, m, true);
        m.cleanup();
        p_target.type_use_mount = -1;
    }

    public static void change_flag(Map map, Player p_target, int type) throws IOException {
        if ((map.zone_id == map.maxzone && type != -1 && p_target.item.wear[11] != null
                && p_target.item.wear[11].id != 3593) || map.map_id == 102 || Map.is_map_chien_truong(map.map_id)) {
            return;
        }
        Message m = new Message(42);
        m.writer().writeShort(p_target.id);
        m.writer().writeByte(type);
        p_target.typepk = (byte) type;
        MapService.send_msg_player_inside(map, p_target, m, true);
        m.cleanup();
    }

    @SuppressWarnings("unused")
    public static void buff_skill(Map map, Session conn, Message m2) throws IOException {
        byte type = m2.reader().readByte();
        byte tem = m2.reader().readByte();
        byte size_buff = m2.reader().readByte();
        // System.out.println(type);
        // System.out.println(tem);
        // System.out.println(size_buff);
        MapService.add_eff_skill(map, conn.p, null, type);
        for (int i = 0; i < size_buff; i++) {
            int id = Short.toUnsignedInt(m2.reader().readShort());
            // System.out.println(id);
        }
    }

    private static void add_eff_skill(Map map, Player p, Player p2, byte index_skill) throws IOException {
        int sk_point = p.body.get_skill_point(index_skill);
        if (sk_point < 1) {
            return;
        }
        int time_buff = p.skills[index_skill].mLvSkill[sk_point - 1].timeBuff;
        int range = p.skills[index_skill].mLvSkill[sk_point - 1].range_lan;
        int n_target = p.skills[index_skill].mLvSkill[sk_point - 1].nTarget - 1;
        switch (p.clazz) {
            case 0: {
                if (index_skill == 18) {
                    p.add_eff(23, 1000, 60_000);
                    Message m = new Message(75);
                    m.writer().writeByte(12);
                    m.writer().writeByte(0);
                    m.writer().writeShort(p.id);
                    m.writer().writeShort(60);
                    m.writer().writeByte(0);
                    m.writer().writeShort(p.id);
                    p.conn.addmsg(m);
                    m.cleanup();
                    MapService.send_eff_other(p.map, p, 23);
                    Service.send_char_main_in4(p);
                } else if (index_skill == 13) {
                    byte[] id_sk = new byte[]{15, 35};
                    int[] param_sk = new int[]{p.get_pramskill_byid(index_skill, id_sk[0]),
                        p.get_pramskill_byid(index_skill, id_sk[1])};
                    for (int i = 0; i < p.map.players.size(); i++) {
                        if (n_target < 1) {
                            continue;
                        }
                        Player p0 = p.map.players.get(i);
                        if (p0 != null && p0.id != p.id && !p0.isdie && Math.abs(p0.x - p.x) < range
                                && Math.abs(p0.y - p.y) < range && p0.typepk != 0 && p.typepk == p0.typepk) {
                            for (int j = 0; j < id_sk.length; j++) {
                                p0.add_eff(id_sk[j], param_sk[j], time_buff);
                            }
                            MapService.add_eff_skill_msg(map, p, p0, index_skill, time_buff, id_sk, param_sk);
                            n_target--;
                        }
                    }
                    for (int j = 0; j < id_sk.length; j++) {
                        p.add_eff(id_sk[j], param_sk[j], time_buff);
                    }
                    MapService.add_eff_skill_msg(map, p, p, index_skill, time_buff, id_sk, param_sk);
                } else if (index_skill == 14) {
                    byte[] id_sk = new byte[]{33, 9, 7};
                    int[] param_sk = new int[]{p.get_pramskill_byid(index_skill, id_sk[0]),
                        p.get_pramskill_byid(index_skill, id_sk[1]), p.get_pramskill_byid(index_skill, id_sk[2])};
                    for (int i = 0; i < p.map.players.size(); i++) {
                        if (n_target < 1) {
                            continue;
                        }
                        Player p0 = p.map.players.get(i);
                        if (p0 != null && p0.id != p.id && !p0.isdie && Math.abs(p0.x - p.x) < range
                                && Math.abs(p0.y - p.y) < range && p0.typepk != 0 && p.typepk == p0.typepk) {
                            for (int j = 0; j < id_sk.length; j++) {
                                p0.add_eff(id_sk[j], param_sk[j], time_buff);
                            }
                            MapService.add_eff_skill_msg(map, p, p0, index_skill, time_buff, id_sk, param_sk);
                            n_target--;
                        }
                    }
                    for (int j = 0; j < id_sk.length; j++) {
                        p.add_eff(id_sk[j], param_sk[j], time_buff);
                    }
                    MapService.add_eff_skill_msg(map, p, p, index_skill, time_buff, id_sk, param_sk);
                } else if (index_skill == 17 && p2 != null) {
                    MapService.add_eff_stun(map, p, p2, 5, 7, -1);
                }
                break;
            }
            case 1: {
                if (index_skill == 18) {
                    p.add_eff(24, 1000, 60_000);
                    Message m = new Message(75);
                    m.writer().writeByte(12);
                    m.writer().writeByte(0);
                    m.writer().writeShort(p.id);
                    m.writer().writeShort(60);
                    m.writer().writeByte(0);
                    m.writer().writeShort(p.id);
                    p.conn.addmsg(m);
                    m.cleanup();
                    MapService.send_eff_other(map, p, 24);
                    Service.send_char_main_in4(p);
                } else if (index_skill == 13) {
                    byte[] id_sk = new byte[]{15, 34};
                    int[] param_sk = new int[]{p.get_pramskill_byid(index_skill, id_sk[0]),
                        p.get_pramskill_byid(index_skill, id_sk[1])};
                    for (int i = 0; i < p.map.players.size(); i++) {
                        if (n_target < 1) {
                            continue;
                        }
                        Player p0 = p.map.players.get(i);
                        if (p0 != null && p0.id != p.id && !p0.isdie && Math.abs(p0.x - p.x) < range
                                && Math.abs(p0.y - p.y) < range && p0.typepk != 0 && p.typepk == p0.typepk) {
                            for (int j = 0; j < id_sk.length; j++) {
                                p0.add_eff(id_sk[j], param_sk[j], time_buff);
                            }
                            MapService.add_eff_skill_msg(map, p, p0, index_skill, time_buff, id_sk, param_sk);
                            n_target--;
                        }
                    }
                    for (int j = 0; j < id_sk.length; j++) {
                        p.add_eff(id_sk[j], param_sk[j], time_buff);
                    }
                    MapService.add_eff_skill_msg(map, p, p, index_skill, time_buff, id_sk, param_sk);
                } else if (index_skill == 14) {
                    byte[] id_sk = new byte[]{33, 11, 7};
                    int[] param_sk = new int[]{p.get_pramskill_byid(index_skill, id_sk[0]),
                        p.get_pramskill_byid(index_skill, id_sk[1]), p.get_pramskill_byid(index_skill, id_sk[2])};
                    for (int i = 0; i < p.map.players.size(); i++) {
                        if (n_target < 1) {
                            continue;
                        }
                        Player p0 = p.map.players.get(i);
                        if (p0 != null && p0.id != p.id && !p0.isdie && Math.abs(p0.x - p.x) < range
                                && Math.abs(p0.y - p.y) < range && p0.typepk != 0 && p.typepk == p0.typepk) {
                            for (int j = 0; j < id_sk.length; j++) {
                                p0.add_eff(id_sk[j], param_sk[j], time_buff);
                            }
                            MapService.add_eff_skill_msg(map, p, p0, index_skill, time_buff, id_sk, param_sk);
                            n_target--;
                        }
                    }
                    for (int j = 0; j < id_sk.length; j++) {
                        p.add_eff(id_sk[j], param_sk[j], time_buff);
                    }
                    MapService.add_eff_skill_msg(map, p, p, index_skill, time_buff, id_sk, param_sk);
                } else if (index_skill == 17 && p2 != null) {
                    MapService.add_eff_stun(map, p, p2, 10, 4, -1);
                }
                break;
            }
            case 2: {
                if (index_skill == 18) {
                    p.add_eff(52, 1000, 60_000);
                    Message m = new Message(75);
                    m.writer().writeByte(12);
                    m.writer().writeByte(0);
                    m.writer().writeShort(p.id);
                    m.writer().writeShort(60);
                    m.writer().writeByte(0);
                    m.writer().writeShort(p.id);
                    p.conn.addmsg(m);
                    m.cleanup();
                    MapService.send_eff_other(map, p, 52);
                } else if (index_skill == 13) {
                    byte[] id_sk = new byte[]{15, 35};
                    int[] param_sk = new int[]{p.get_pramskill_byid(index_skill, id_sk[0]),
                        p.get_pramskill_byid(index_skill, id_sk[1])};
                    for (int i = 0; i < p.map.players.size(); i++) {
                        if (n_target < 1) {
                            continue;
                        }
                        Player p0 = p.map.players.get(i);
                        if (p0 != null && p0.id != p.id && !p0.isdie && Math.abs(p0.x - p.x) < range
                                && Math.abs(p0.y - p.y) < range && p0.typepk != 0 && p.typepk == p0.typepk) {
                            for (int j = 0; j < id_sk.length; j++) {
                                p0.add_eff(id_sk[j], param_sk[j], time_buff);
                            }
                            MapService.add_eff_skill_msg(map, p, p0, index_skill, time_buff, id_sk, param_sk);
                            n_target--;
                        }
                    }
                    for (int j = 0; j < id_sk.length; j++) {
                        p.add_eff(id_sk[j], param_sk[j], time_buff);
                    }
                    MapService.add_eff_skill_msg(map, p, p, index_skill, time_buff, id_sk, param_sk);
                } else if (index_skill == 14) {
                    byte[] id_sk = new byte[]{36, 8, 7};
                    int[] param_sk = new int[]{p.get_pramskill_byid(index_skill, id_sk[0]),
                        p.get_pramskill_byid(index_skill, id_sk[1]), p.get_pramskill_byid(index_skill, id_sk[2])};
                    for (int i = 0; i < p.map.players.size(); i++) {
                        if (n_target < 1) {
                            continue;
                        }
                        Player p0 = p.map.players.get(i);
                        if (p0 != null && p0.id != p.id && !p0.isdie && Math.abs(p0.x - p.x) < range
                                && Math.abs(p0.y - p.y) < range && p0.typepk != 0 && p.typepk == p0.typepk) {
                            for (int j = 0; j < id_sk.length; j++) {
                                p0.add_eff(id_sk[j], param_sk[j], time_buff);
                            }
                            MapService.add_eff_skill_msg(map, p, p0, index_skill, time_buff, id_sk, param_sk);
                            n_target--;
                        }
                    }
                    for (int j = 0; j < id_sk.length; j++) {
                        p.add_eff(id_sk[j], param_sk[j], time_buff);
                    }
                    MapService.add_eff_skill_msg(map, p, p, index_skill, time_buff, id_sk, param_sk);
                } else if (index_skill == 17 && p2 != null) {
                    MapService.add_eff_stun(map, p, p2, 10, 5, -1);
                }
                break;
            }
            case 3: {
                if (index_skill == 18) {
                    p.add_eff(53, 1000, 60_000);
                    Message m = new Message(75);
                    m.writer().writeByte(12);
                    m.writer().writeByte(0);
                    m.writer().writeShort(p.id);
                    m.writer().writeShort(60);
                    m.writer().writeByte(0);
                    m.writer().writeShort(p.id);
                    p.conn.addmsg(m);
                    m.cleanup();
                    MapService.send_eff_other(map, p, 53);
                } else if (index_skill == 13) {
                    byte[] id_sk = new byte[]{15, 34};
                    int[] param_sk = new int[]{p.get_pramskill_byid(index_skill, id_sk[0]),
                        p.get_pramskill_byid(index_skill, id_sk[1])};
                    for (int i = 0; i < p.map.players.size(); i++) {
                        if (n_target < 1) {
                            continue;
                        }
                        Player p0 = p.map.players.get(i);
                        if (p0 != null && p0.id != p.id && !p0.isdie && Math.abs(p0.x - p.x) < range
                                && Math.abs(p0.y - p.y) < range && p0.typepk != 0 && p.typepk == p0.typepk) {
                            for (int j = 0; j < id_sk.length; j++) {
                                p0.add_eff(id_sk[j], param_sk[j], time_buff);
                            }
                            MapService.add_eff_skill_msg(map, p, p0, index_skill, time_buff, id_sk, param_sk);
                            n_target--;
                        }
                    }
                    for (int j = 0; j < id_sk.length; j++) {
                        p.add_eff(id_sk[j], param_sk[j], time_buff);
                    }
                    MapService.add_eff_skill_msg(map, p, p, index_skill, time_buff, id_sk, param_sk);
                } else if (index_skill == 14) {
                    byte[] id_sk = new byte[]{36, 10, 7};
                    int[] param_sk = new int[]{p.get_pramskill_byid(index_skill, id_sk[0]),
                        p.get_pramskill_byid(index_skill, id_sk[1]), p.get_pramskill_byid(index_skill, id_sk[2])};
                    for (int i = 0; i < p.map.players.size(); i++) {
                        if (n_target < 1) {
                            continue;
                        }
                        Player p0 = p.map.players.get(i);
                        if (p0 != null && p0.id != p.id && !p0.isdie && Math.abs(p0.x - p.x) < range
                                && Math.abs(p0.y - p.y) < range && p0.typepk != 0 && p.typepk == p0.typepk) {
                            for (int j = 0; j < id_sk.length; j++) {
                                p0.add_eff(id_sk[j], param_sk[j], time_buff);
                            }
                            MapService.add_eff_skill_msg(map, p, p0, index_skill, time_buff, id_sk, param_sk);
                            n_target--;
                        }
                    }
                    for (int j = 0; j < id_sk.length; j++) {
                        p.add_eff(id_sk[j], param_sk[j], time_buff);
                    }
                    MapService.add_eff_skill_msg(map, p, p, index_skill, time_buff, id_sk, param_sk);
                } else if (index_skill == 17 && p2 != null) {
                    MapService.add_eff_stun(map, p, p2, 10, 6, -1);
                }
                break;
            }
        }
    }

    private static void add_eff_skill_msg(Map map, Player p, Player p0, byte index_skill, int time_buff, byte[] id_sk,
            int[] param_sk) throws IOException {
        int index_skill2 = 0;
        switch (p.clazz) {
            case 0: {
                index_skill2 = index_skill;
                break;
            }
            case 1: {
                if (index_skill == 13) {
                    index_skill2 = 30;
                } else {
                    index_skill2 = 31;
                }
                break;
            }
            case 2: {
                index_skill2 = index_skill;
                break;
            }
            case 3: {
                if (index_skill == 13) {
                    index_skill2 = 30;
                } else {
                    index_skill2 = 31;
                }
                break;
            }
        }
        Message m = new Message(40);
        m.writer().writeByte(1);
        m.writer().writeByte(1);
        m.writer().writeShort(p.id);
        m.writer().writeByte(index_skill);
        m.writer().writeInt(time_buff);
        m.writer().writeShort(p0.id);
        m.writer().writeByte(0);
        m.writer().writeByte(index_skill2);
        if (index_skill == 13) {
            int index = -1;
            m.writer().writeByte(id_sk.length + 1);
            for (int i = 0; i < id_sk.length; i++) {
                m.writer().writeByte(id_sk[i]);
                m.writer().writeInt(param_sk[i]);
                if (id_sk[i] == 15) {
                    index = i;
                }
            }
            int param;
            if (index == -1) {
                param = 0;
            } else {
                param = param_sk[index];
            }
            m.writer().writeByte(14);
            m.writer().writeInt(p0.body.get_def() * (param / 100) / 100);
        } else if (index_skill == 14) {
            int index = -1;
            int index1 = -1;
            int index2 = -1;
            int index3 = -1;
            int index4 = -1;
            m.writer().writeByte(id_sk.length + 5);
            for (int i = 0; i < id_sk.length; i++) {
                m.writer().writeByte(id_sk[i]);
                m.writer().writeInt(param_sk[i]);
                if (id_sk[i] == 7) {
                    index = i;
                }
                if (id_sk[i] == 8) {
                    index1 = i;
                }
                if (id_sk[i] == 9) {
                    index2 = i;
                }
                if (id_sk[i] == 10) {
                    index3 = i;
                }
                if (id_sk[i] == 11) {
                    index4 = i;
                }
            }
            int pr0, pr1, pr2, pr3, pr4;
            if (index == -1) {
                pr0 = 0;
            } else {
                pr0 = param_sk[index];
            }
            if (index1 == -1) {
                pr1 = 0;
            } else {
                pr1 = param_sk[index1];
            }
            if (index2 == -1) {
                pr2 = 0;
            } else {
                pr2 = param_sk[index2];
            }
            if (index3 == -1) {
                pr3 = 0;
            } else {
                pr3 = param_sk[index3];
            }
            if (index4 == -1) {
                pr4 = 0;
            } else {
                pr4 = param_sk[index4];
            }
            m.writer().writeByte(0);
            m.writer().writeInt((p0.body.get_dame_physical() * (pr0 / 100)) / 100);
            m.writer().writeByte(1);
            m.writer().writeInt((p0.body.get_dame_prop(1) * (pr1 / 100)) / 100);
            m.writer().writeByte(2);
            m.writer().writeInt((p0.body.get_dame_prop(2) * (pr2 / 100)) / 100);
            m.writer().writeByte(3);
            m.writer().writeInt((p0.body.get_dame_prop(3) * (pr3 / 100)) / 100);
            m.writer().writeByte(4);
            m.writer().writeInt((p0.body.get_dame_prop(4) * (pr4 / 100)) / 100);
        } else {
            m.writer().writeByte(0);
        }
        p0.conn.addmsg(m);
        m.cleanup();
        if (p0.id != p.id) {
            m = new Message(40);
            m.writer().writeByte(1);
            m.writer().writeByte(1);
            m.writer().writeShort(p.id);
            m.writer().writeByte(index_skill);
            m.writer().writeInt(time_buff);
            m.writer().writeShort(p0.id);
            m.writer().writeByte(0);
            m.writer().writeByte(index_skill2);
            m.writer().writeByte(0);
            p.conn.addmsg(m);
            m.cleanup();
        }
        //
        m = new Message(40);
        m.writer().writeByte(0);
        m.writer().writeByte(1);
        m.writer().writeShort(p.id);
        m.writer().writeByte(index_skill);
        m.writer().writeInt(time_buff);
        m.writer().writeShort(p0.id);
        m.writer().writeByte(0);
        m.writer().writeByte(index_skill2);
        if (index_skill == 13) {
            int index = -1;
            m.writer().writeByte(id_sk.length + 1);
            for (int i = 0; i < id_sk.length; i++) {
                m.writer().writeByte(id_sk[i]);
                m.writer().writeInt(param_sk[i]);
                if (id_sk[i] == 15) {
                    index = i;
                }
            }
            int param;
            if (index == -1) {
                param = 0;
            } else {
                param = param_sk[index];
            }
            m.writer().writeByte(14);
            m.writer().writeInt(p0.body.get_def() * (param / 100) / 100);
        } else if (index_skill == 14) {
            int index = -1;
            int index1 = -1;
            int index2 = -1;
            int index3 = -1;
            int index4 = -1;
            m.writer().writeByte(id_sk.length + 5);
            for (int i = 0; i < id_sk.length; i++) {
                m.writer().writeByte(id_sk[i]);
                m.writer().writeInt(param_sk[i]);
                if (id_sk[i] == 7) {
                    index = i;
                }
                if (id_sk[i] == 8) {
                    index1 = i;
                }
                if (id_sk[i] == 9) {
                    index2 = i;
                }
                if (id_sk[i] == 10) {
                    index3 = i;
                }
                if (id_sk[i] == 11) {
                    index4 = i;
                }
            }
            int pr0, pr1, pr2, pr3, pr4;
            if (index == -1) {
                pr0 = 0;
            } else {
                pr0 = param_sk[index];
            }
            if (index1 == -1) {
                pr1 = 0;
            } else {
                pr1 = param_sk[index1];
            }
            if (index2 == -1) {
                pr2 = 0;
            } else {
                pr2 = param_sk[index2];
            }
            if (index3 == -1) {
                pr3 = 0;
            } else {
                pr3 = param_sk[index3];
            }
            if (index4 == -1) {
                pr4 = 0;
            } else {
                pr4 = param_sk[index4];
            }
            m.writer().writeByte(0);
            m.writer().writeInt((p0.body.get_dame_physical() * (pr0 / 100)) / 100);
            m.writer().writeByte(1);
            m.writer().writeInt((p0.body.get_dame_prop(1) * (pr1 / 100)) / 100);
            m.writer().writeByte(2);
            m.writer().writeInt((p0.body.get_dame_prop(2) * (pr2 / 100)) / 100);
            m.writer().writeByte(3);
            m.writer().writeInt((p0.body.get_dame_prop(3) * (pr3 / 100)) / 100);
            m.writer().writeByte(4);
            m.writer().writeInt((p0.body.get_dame_prop(4) * (pr4 / 100)) / 100);
        } else {
            m.writer().writeByte(0);
        }
        p0.conn.addmsg(m);
        m.cleanup();
    }

    private static void send_eff_other(Map map, Player p, int id) throws IOException {
        EffTemplate temp = p.get_eff(id);
        if (temp != null) {
            switch (id) {
                case -121: {
                    Message m = new Message(75);
                    m.writer().writeByte(6);
                    m.writer().writeByte(0);
                    m.writer().writeShort(p.id);
                    long time_exist = temp.time - System.currentTimeMillis();
                    if (time_exist < 1000) {
                        return;
                    }
                    m.writer().writeShort((short) (time_exist / 1000));
                    m.writer().writeByte(0);
                    m.writer().writeShort(p.id);
                    MapService.send_msg_player_inside(map, p, m, false);
                    m.cleanup();
                    break;
                }
                case -122: {
                    Message m = new Message(75);
                    m.writer().writeByte(5);
                    m.writer().writeByte(0);
                    m.writer().writeShort(p.id);
                    long time_exist = temp.time - System.currentTimeMillis();
                    if (time_exist < 1000) {
                        return;
                    }
                    m.writer().writeShort((short) (time_exist / 1000));
                    m.writer().writeByte(0);
                    m.writer().writeShort(p.id);
                    MapService.send_msg_player_inside(map, p, m, false);
                    m.cleanup();
                    break;
                }
                case -123: {
                    Message m = new Message(75);
                    m.writer().writeByte(4);
                    m.writer().writeByte(0);
                    m.writer().writeShort(p.id);
                    long time_exist = temp.time - System.currentTimeMillis();
                    if (time_exist < 1000) {
                        return;
                    }
                    m.writer().writeShort((short) (time_exist / 1000));
                    m.writer().writeByte(0);
                    m.writer().writeShort(p.id);
                    MapService.send_msg_player_inside(map, p, m, false);
                    m.cleanup();
                    break;
                }
                case -124: {
                    Message m = new Message(75);
                    m.writer().writeByte(7);
                    m.writer().writeByte(0);
                    m.writer().writeShort(p.id);
                    long time_exist = temp.time - System.currentTimeMillis();
                    if (time_exist < 1000) {
                        return;
                    }
                    m.writer().writeShort((short) (time_exist / 1000));
                    m.writer().writeByte(0);
                    m.writer().writeShort(p.id);
                    MapService.send_msg_player_inside(map, p, m, false);
                    m.cleanup();
                    break;
                }
                case 23:
                case 24:
                case 52:
                case 53: {
                    Message m = new Message(75);
                    m.writer().writeByte(12);
                    m.writer().writeByte(0);
                    m.writer().writeShort(p.id);
                    long time_exist = temp.time - System.currentTimeMillis();
                    if (time_exist < 1000) {
                        return;
                    }
                    m.writer().writeShort((short) (time_exist / 1000));
                    m.writer().writeByte(0);
                    m.writer().writeShort(p.id);
                    MapService.send_msg_player_inside(map, p, m, false);
                    m.cleanup();
                    break;
                }
            }
        }
    }

    public static void send_chat(Map map, Session conn, Message m2) throws IOException {
        String chat = m2.reader().readUTF();
        if (conn.user.equals("admin") && chat.equals("admin")) {
            MenuController.send_menu_select(conn, 126, new String[]{"Bảo trì", "Cộng vàng x1.000.000.000",
                "Cộng ngọc x1.000.000", "Update data", "Lấy item", "Mở/Đóng Chiếm Mỏ", "Ấp Trứng Nhanh", "Làm Mới Boss TG", "Mở/Đóng/End Chiến Trường", "Mở Boss Level", "Buff Tài Khoản"});
        } else if (conn.user.equals("admin") && chat.equals("xem")) {
            int num = 0;
            for (Map[] maps : Map.entrys) {
                for (Map map_ : maps) {
                    num += map_.players.size();
                }
            }
            String boss_in4 = "\nBoss:\n";
            for (Mob_in_map m : Mob_in_map.list_boss) {
                if (m.is_boss_active) {
                    boss_in4 += m.template.name + " - Map " + Map.get_map_by_id(m.map_id)[0].name + " - Khu "
                            + (m.zone_id + 1) + "\n";
                }
            }
            Service.send_notice_box(conn,
                    "Vị Trí " + conn.p.x + " - " + conn.p.y + "\n Map id : " + map.map_id + "\n Zone : " + map.zone_id
                    + "\n Số Người kết nối : " + Session.client_entrys.size() + "\n Số Người online : " + num
                    + boss_in4);
        } else if (chat.equals("/tp") && (conn.p.map.ld == null
                || (conn.p.map.ld != null && conn.p.map.ld.p1.id != conn.p.id && conn.p.map.ld.p2.id != conn.p.id))) {
            if (conn.p.item.wear[11] != null && (conn.p.item.wear[11].id == 3599 || conn.p.item.wear[11].id == 3593
                    || conn.p.item.wear[11].id == 3596)) {
                return;
            }
            if (conn.p.minuong != null) {
					conn.p.minuong.owner = "";
					conn.p.minuong = null;
				}
            Vgo vgo = new Vgo();
            vgo.id_map_go = 1;
            vgo.x_new = 432;
            vgo.y_new = 354;
            conn.p.change_map(conn.p, vgo);
            // } else if (chat.substring(0, 1).equals("t")) {
            // int n = Integer.parseInt(chat.substring(1, chat.length()));
            // conn.p.type_use_mount = (byte) n;
            // this.send_mount(conn.p);
            // Message m = new Message(-49);
            // m.writer().writeByte(2);
            // m.writer().writeShort(0);
            // m.writer().writeByte(0);
            // m.writer().writeByte(0);
            // m.writer().writeByte(n);
            // m.writer().writeShort(conn.p.id);
            // m.writer().writeByte(0);
            // m.writer().writeByte(0);
            // m.writer().writeInt(3000);
            // send_msg_player_inside(conn.p, m, true);
            // m.cleanup();
        } else if (conn.user.equals("admin") && chat.equals("ok")) {
            try {
							Mob_in_map mob = new Mob_in_map();
							mob.template = Mob.entrys.get(150);
							mob.hpmax = 100;
							mob.hp = mob.hpmax;
							mob.level = 11;
							mob.isdie = false;
							mob.color_name = 0;
							mob.is_boss = false;
							mob.time_back = 0;
							mob.list_fight = new ArrayList<>();
							mob.is_boss_active = false;
							MiNuong mn = new MiNuong();
							mn.mob = mob;
							mn.owner = "";
							mn.power = 1000;
							//
							short[] id_random = new short[] {9, 26, 27, 16, 13, 12, 17, 39, 40, 44, 20, 45, 41, 51, 52, 65, 73,
							      76, 94, 97, 98};
							// id_random = new short[] {26};
							Map[] map_rd = Map.get_map_by_id(id_random[Util.random(id_random.length)]);
							// while (map_rd[0].mobs.length < 1) {
							// map_rd = Map.get_map_by_id(id_random[Util.random(id_random.length)]);
							// }
							mn.map = map_rd[Util.random(2, 4)];
							Mob_in_map temppp = mn.map.mobs[Util.random(mn.map.mobs.length)];
							mob.x = temppp.x;
							mob.y = temppp.y;
							mob.map_id = mn.map.map_id;
							mob.zone_id = mn.map.zone_id;
							System.out.println("mi nuong " + mn.map.name + " khu " + (mn.map.zone_id + 1));
							//
							BossEvent.MiNuong.add(mn);
							mob.index = MiNuong.TEMPLATE--;
							// - BossEvent.MiNuong.indexOf(mn);
							BossEvent.LIST.forEach(l -> {
								if (BossEvent.time[BossEvent.LIST.indexOf(l)] < System.currentTimeMillis()) {
									l.is_boss_active = true;
								}
							});
						} catch (Exception e) {
						}
//            ChienTruong.gI().open_register();
//            ChienTruong.gI().register(conn.p, 0);
//            ChienTruong.gI().setTime(3);
} else if (conn.user.equals("admin") && chat.startsWith("/setlv ")) {
    try {
        int lv = Integer.parseInt(chat.substring(7).trim());
        if (lv < 1 || lv > Manager.gI().lvmax) {
            Service.send_notice_box(conn, "Level không hợp lệ.");
        } else {
            conn.p.level = (short) lv;
            conn.p.exp = 0;
            
            Service.send_char_main_in4(conn.p);
            
            Service.send_notice_nobox_white(conn, "Level của bạn đã được đặt thành: " + lv);
        }
    } catch (NumberFormatException e) {
        Service.send_notice_box(conn, "Lệnh sai định dạng. Ví dụ: /setlv 50");
    }

        } else if (chat.equals("/pk")) {
            Service.send_notice_box(conn, "Bạn đang có " + conn.p.hieuchien + " Điểm Pk.");
        } else if (chat.equals("bosstg") && conn.user.equals("admin")) {
            Manager.gI().bossTG.refresh();
        } else if (chat.equals("/help")) {
    Service.send_notice_box(conn,
        "/pk: xem điểm pk\n" +
        "/tp: về làng khi bị kẹt map\n" +
        "/time: xem giờ server\n" +
        "/sk: xem các sự kiện server\n" +
        "/info: xem thông tin server"
    );

        } else if (chat.equals("/time")) {
    java.time.LocalDateTime now = java.time.LocalDateTime.now();
    java.time.format.DateTimeFormatter formatter = java.time.format.DateTimeFormatter.ofPattern("HH:mm:ss dd/MM/yyyy");
    String formattedTime = now.format(formatter);
    Service.send_notice_box(conn, "Giờ server: " + formattedTime);
         }else if (chat.equals("/sk")) {
    Service.send_notice_box(conn,
        "Sự kiện server:\n" +
        "- Mỗi 1 tiếng: Boss hồi sinh\n" +
        "- 08:00 - 09:00: Chiếm Mỏ\n" +
        "- 18:00 - 18:45: Lôi Đài\n" +
        "- 19:00 - 19:30: Boss Thế Giới\n" +
        "- 20:00 - 21:00: Chiếm Mỏ\n" +
        "- 21:30 - 22:30: Chiến Trường"
    );
}else if (chat.equals("/info")) {
    Service.send_notice_box(conn,
        "Thông tin Server:\n" +
        "- Tên: Hiệp Sĩ Ong Đất\n" +
        "- Phiên bản: 1.2w67 Custom\n" +
        "- Địa chỉ IP: serveo & ngrok\n" +
        "- Ngày mở: 01/05/2025\n" +
        "- Trạng thái: Beta Test (Không reset nhân vật)"
    );
}else {
            Message m = new Message(27);
            m.writer().writeShort(conn.p.id);
            m.writer().writeByte(0);
            m.writer().writeUTF(chat);
            MapService.send_msg_player_inside(map, conn.p, m, false);
            m.cleanup();
        }
    }

    public static void send_in4_other_char(Map map, Player p, Player p0) throws IOException {
        synchronized (map) {
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
m.writer().writeInt ((int) Math.min(p0.hp, Integer.MAX_VALUE));
        m.writer().writeInt((int) Math.min(p0.body.get_max_hp(), Integer.MAX_VALUE));
            if (p.item.wear[11] != null && (p.item.wear[11].id == 4790 || p.item.wear[11].id == 4791)) {
                if (p0.item.wear[11] != null && (p0.item.wear[11].id == 4790 || p0.item.wear[11].id == 4791)) {
                    m.writer().writeByte(p0.item.wear[11].id == 4790 ? 12 : 13);

                } else {
                    m.writer().writeByte(11);

                }
            } else {
                m.writer().writeByte(p0.typepk);
            }
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

                    if (i == 10 && p0.item.wear[14] != null && (p0.item.wear[14].id >= 4638 && p0.item.wear[14].id <= 4648)) {
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
            m.writer().writeShort(p0.id_henshin);
            m.writer().writeByte(p0.type_use_mount);
            m.writer().writeBoolean(false);
            m.writer().writeByte(1);
            m.writer().writeByte(0);
            m.writer().writeShort(p0.id_name != -1 ? p0.id_name : Service.get_id_mat_na(p0)); // mat na
            m.writer().writeByte(1); // paint mat na trc sau
            m.writer().writeShort(Service.get_id_phiphong(p0)); // phi phong
            m.writer().writeShort(Service.get_id_weapon(p0)); // weapon
            m.writer().writeShort(p0.id_horse);
            m.writer().writeShort(Service.get_id_hair(p0)); // hair
            m.writer().writeShort(Service.get_id_wing(p0)); // wing
            m.writer().writeShort(-1); // body
            m.writer().writeShort(-1); // leg
            m.writer().writeShort(-1); // bienhinh
            p.conn.addmsg(m);
            m.cleanup();
            for (int i = 0; i < p0.list_eff.size(); i++) {
                EffTemplate temp = p0.list_eff.get(i);
                if (temp.id == 52 || temp.id == 53 || temp.id == 23 || temp.id == 24 || temp.id == -124 || temp.id == -123
                        || temp.id == -122 || temp.id == -121) {
                    // this.send_eff_other(p0, temp.id);
                }
            }
            if (p.id != p0.id && !p0.my_store_name.isEmpty()) {
                m = new Message(-102);
                m.writer().writeByte(1);
                m.writer().writeShort(p0.id);
                m.writer().writeUTF(p0.my_store_name);
                p.conn.addmsg(m);
                m.cleanup();
            }
        }
    }

    public static void request_livefromdie(Map map, Session conn, Message m) throws IOException {
        byte type = m.reader().readByte();
        if (map.ld != null) {
            return;
        }
        if (Manager.gI().bossTG.map.equals(map)) {
            type = 0;
        }
        if (type == 1) { // hsl
            if (Map.is_map_chien_truong(map.map_id)) {
                Service.send_box_input_yesno(conn, 9, "Cần " + conn.p.vang_ct + " vàng cho mỗi lần hồi sinh tại chỗ ok?");
            } else {
                Service.send_box_input_yesno(conn, 9, "Cần 5 ngọc cho mỗi lần hồi sinh tại chỗ ok?");
            }
        } else if (type == 0) { // ve lang
            conn.p.isdie = false;
            conn.p.hp = conn.p.body.get_max_hp();
            conn.p.mp = conn.p.body.get_max_mp();
            Vgo vgo = new Vgo();
            if (Map.is_map_chien_truong(map.map_id)) {
                System.out.println(conn.p.typepk);
                switch (conn.p.typepk) {
                    case 1: {
                        vgo.id_map_go = 59;
                        vgo.x_new = 240;
                        vgo.y_new = 224;
                        break;
                    }
                    case 2: {
                        vgo.id_map_go = 55;
                        vgo.x_new = 224;
                        vgo.y_new = 256;
                        break;
                    }
                    case 4: {
                        vgo.id_map_go = 57;
                        vgo.x_new = 264;
                        vgo.y_new = 272;
                        break;
                    }
                    case 5: {
                        vgo.id_map_go = 53;
                        vgo.x_new = 276;
                        vgo.y_new = 246;
                        break;
                    }
                }
            } else {
                vgo.id_map_go = 1;
                vgo.x_new = (short) 528;
                vgo.y_new = (short) 480;
            }
            conn.p.change_map(conn.p, vgo);
            Service.usepotion(conn.p, 0, conn.p.body.get_max_hp());
            Service.usepotion(conn.p, 1, conn.p.body.get_max_mp());
        }
    }

    public static void use_skill(Map map, Session conn, Message m, int type_atk) throws IOException {
        if (!conn.p.is_atk) {
            return;
        }
        synchronized (map) {
            byte index_skill = m.reader().readByte();
            int n = m.reader().readByte();
            int sk_point = conn.p.body.get_skill_point(index_skill);
            if (sk_point < 1) {
                return;
            }
            if (conn.p.item.wear[0] == null) {
                Service.send_notice_nobox_white(conn, "Chưa có vũ khí");
                return;
            }
            LvSkill temp = conn.p.skills[index_skill].mLvSkill[sk_point - 1];
            while (sk_point > 1 && temp.LvRe > (conn.p.level + 1)) {
                sk_point--;
                temp = conn.p.skills[index_skill].mLvSkill[sk_point - 1];
            }
            if (temp.LvRe > (conn.p.level + 1)) {
                return;
            }
            if (conn.p.mp - temp.mpLost < 0) {
                Service.send_notice_nobox_white(conn, "Không đủ mp");
                return;
            }
            conn.p.mp -= temp.mpLost;
            if (conn.p.time_delay_skill[index_skill] > System.currentTimeMillis()) {
                if (++conn.p.enough_time_disconnect > 5) {
                    conn.close();
                }
            } else {
                n = (temp.nTarget < n) ? temp.nTarget : n;
                conn.p.time_delay_skill[index_skill] = System.currentTimeMillis() + ((temp.delay * 199) / 200);
                conn.p.enough_time_disconnect = 0;
                if (conn.p.get_eff(-124) != null || conn.p.get_eff(-122) != null || conn.p.get_eff(-121) != null) {
                    return;
                }
                EffTemplate ef;
                long dame = conn.p.body.total_item_param(40);
                byte type = 0;
                if (index_skill == 2 || index_skill == 4 || index_skill == 6 || index_skill == 8 || index_skill == 19
                        || index_skill == 20) {
                    type = index_skill == 20 && (conn.p.clazz == 2 || conn.p.clazz == 1)
                            || index_skill == 19 && (conn.p.clazz == 0 || conn.p.clazz == 3) ? (byte) 0 : 1;
                }
                if (index_skill == 0) {
                    type = 2;
                }
                if (type == 0) {
                    dame += conn.p.body.get_dame_physical();
                    ef = conn.p.get_eff(7);
                    if (ef != null) {
                        dame += dame * (ef.param / 100) / 100;
                    }
                } else if (type == 1) {
                    switch (conn.p.clazz) {
                        case 0: {
                            dame += conn.p.body.get_dame_prop(2);
                            ef = conn.p.get_eff(9);
                            if (ef != null) {
                                dame += dame * (ef.param / 100) / 100;
                            }
                            break;
                        }
                        case 1: {
                            dame += conn.p.body.get_dame_prop(4);
                            ef = conn.p.get_eff(11);
                            if (ef != null) {
                                dame += dame * (ef.param / 100) / 100;
                            }
                            break;
                        }
                        case 2: {
                            dame += conn.p.body.get_dame_prop(1);
                            ef = conn.p.get_eff(8);
                            if (ef != null) {
                                dame += dame * (ef.param / 100) / 100;
                            }
                            break;
                        }
                        case 3: {
                            dame += conn.p.body.get_dame_prop(3);
                            ef = conn.p.get_eff(10);
                            if (ef != null) {
                                dame += dame * (ef.param / 100) / 100;
                            }
                            break;
                        }
                    }
                } else {
                    dame += conn.p.body.get_dame_physical() / 2;
                }
                if (index_skill == 19 && conn.p.clazz == 1) {
                    for (Option op : temp.minfo) {
                        if (op.id == 4) {
                            dame += op.getParam(0);
                        }
                        if (op.id == 11) {
                            dame += dame * (op.getParam(0) / 100) / 100;
                        }
                    }
                } else {
                    for (int i = temp.minfo.length - 1; i >= 0; i--) {
                        Option op = temp.minfo[i];
                        if (type == 0) {
                            if (op.id == 0) {
                                dame += op.getParam(0);
                            }
                            if (op.id == 7) {
                                dame += dame * (op.getParam(0) / 100) / 100;
                            }
                        } else {
                            if (op.id == 1 || op.id == 2 || op.id == 3 || op.id == 4) {
                                dame += op.getParam(0);
                            }
                            if (op.id == 9 || op.id == 10 || op.id == 11 || op.id == 8) {
                                dame += dame * (op.getParam(0) / 100) / 100;
                            }
                        }
                    }
                }
                if (conn.p.type_use_mount == 3) {
                    dame = (dame / 10) * 12;
                } else if (conn.p.type_use_mount == 5) {
                    dame = (dame / 10) * 14;
                } else if (conn.p.type_use_mount == 11 || conn.p.type_use_mount == 12) {
                    dame = (dame / 10) * 11;
                } else if ((conn.p.type_use_mount == 20 && conn.p.id_horse == 114)
                        || (conn.p.type_use_mount == 22 && conn.p.id_horse == 117)) {
                    dame = (dame / 100) * 115;
                } else if ((conn.p.type_use_mount == 20 && conn.p.id_horse == 116)) {
                    dame = (dame / 100) * 135;
                }
                ef = conn.p.get_eff(53);
                if (ef != null && conn.p.hp < conn.p.body.get_max_hp()) {
                    long param = conn.p.body.get_max_hp() / 100;
                    if (conn.p.hp + param > conn.p.body.get_max_hp()) {
                        param = conn.p.body.get_max_hp() - conn.p.hp;
                    }
                    Service.usepotion(conn.p, 0, param);
                }
//                m.writer().writeInt ((int) Math.min(p0.hp, Integer.MAX_VALUE));
//        m.writer().writeInt((int) Math.min(p0.body.get_max_hp(), Integer.MAX_VALUE));
                ef = conn.p.get_eff(3);
                if (ef != null) {
                    dame = (dame / 10) * 8;
                }
                if (conn.p.getlevelpercent() < 0) {
                    dame /= 2;
                }
                // power wing active
                EffTemplate temp2 = conn.p.get_eff(-120);
                if (temp2 == null) {
                    Item3 it = conn.p.item.wear[10];
                    if (it != null) {
                        int percent = 0;
                        int time = 0;
                        for (Option op : it.op) {
                            if (op.id == 41) {
                                percent = op.getParam(0);
                            } else if (op.id == 42) {
                                time = op.getParam(0);
                            }
                        }
                        if (percent > Util.random(10_000)) {
                            //
                            conn.p.add_eff(-120, 1000, time);
                            //
                            Message mw = new Message(40);
                            mw.writer().writeByte(0);
                            mw.writer().writeByte(1);
                            mw.writer().writeShort(conn.p.id);
                            mw.writer().writeByte(21);
                            mw.writer().writeInt(time);
                            mw.writer().writeShort(conn.p.id);
                            mw.writer().writeByte(0);
                            mw.writer().writeByte(30);
                            byte[] id__ = new byte[]{7, 8, 9, 10, 11, 15, 0, 1, 2, 3, 4, 14};
                            int[] par__ = new int[]{2000, 2000, 2000, 2000, 2000, 2000,
                                2 * (conn.p.body.get_param_view_in4(0) / 10), 2 * (conn.p.body.get_param_view_in4(1) / 10),
                                2 * (conn.p.body.get_param_view_in4(2) / 10), 2 * (conn.p.body.get_param_view_in4(3) / 10),
                                2 * (conn.p.body.get_param_view_in4(4) / 10), 2 * (conn.p.body.get_param_view_in4(14) / 10)};
                            mw.writer().writeByte(id__.length);
                            //
                            for (int i = 0; i < id__.length; i++) {
                                mw.writer().writeByte(id__[i]);
                                mw.writer().writeInt(par__[i]);
                            }
                            //
                            MapService.send_msg_player_inside(map, conn.p, mw, true);
                            mw.cleanup();
                        }
                    }
                } else {
                    dame = (dame / 10) * 12;
                }

                // tac dung ngoc kham
                for (int i = 0; i < conn.p.item.wear.length; i++) {
                    Item3 it = conn.p.item.wear[i];
                    if (it != null) {
                        short[] b = conn.p.item.check_kham_ngoc(it);
                        if (b[0] != -2 && b[0] != -1) {
                            conn.p.process_eff_ngoc_kham(it, 0);
                        }
                        if (b[1] != -2 && b[1] != -1) {
                            conn.p.process_eff_ngoc_kham(it, 1);
                        }
                        if (b[2] != -2 && b[2] != -1) {
                            conn.p.process_eff_ngoc_kham(it, 2);
                        }
                    }
                }
                temp2 = conn.p.get_eff(32000);
                if (temp2 != null) {
                    dame *= 2;
                }
                temp2 = conn.p.get_eff(32001);
                if (temp2 != null) {
                    long hp = (temp2.param * conn.p.body.get_max_hp()) / 100;
                    conn.p.hp += hp;
                    if (conn.p.hp > conn.p.body.get_max_hp()) {
                        conn.p.hp = conn.p.body.get_max_hp();
                    }
                    Service.usepotion(conn.p, 0, hp);
                }

                if (dame > 2_000_000_000) {
                    dame = 2_000_000_000;
                }
                if (type_atk == 0) {
                    for (int i = 0; i < n; ++i) {
                        int n2 = Short.toUnsignedInt(m.reader().readShort());
                        if (map.zone_id == map.maxzone) {
                            Pet_di_buon pet_di_buon = Pet_di_buon_manager.check(n2);
                            if (pet_di_buon != null) {
                                pet_di_buon.hp -= dame;
                                if (pet_di_buon.hp <= 0) {
                                    pet_di_buon.hp = 0;
                                    Message mout = new Message(8);
                                    mout.writer().writeShort(pet_di_buon.index);
                                    for (int i1 = 0; i1 < map.players.size(); i1++) {
                                        Player p0 = map.players.get(i1);
                                        if (p0 != null) {
                                            p0.conn.addmsg(mout);
                                        }
                                    }
                                    mout.cleanup();
                                    Pet_di_buon_manager.remove(pet_di_buon.name);
                                    pet_di_buon.p.pet_di_buon = null;
                                    for (int j = 0; j < pet_di_buon.item.size(); j++) {
                                        ItemMap it_leave = new ItemMap();
                                        it_leave.id_item = (short) pet_di_buon.item.get(j);
                                        it_leave.color = (byte) 0;
                                        it_leave.quantity = 1;
                                        it_leave.category = 3;
                                        it_leave.idmaster = (short) pet_di_buon.p.id;
                                        it_leave.op = new ArrayList<>();
                                        it_leave.time_exist = System.currentTimeMillis() + 60_000L;
                                        it_leave.time_pick = System.currentTimeMillis() + 1_500L;
                                        map.add_item_map_leave(map, conn.p, it_leave, pet_di_buon.index);
                                    }
                                }
                                Message m_atk = new Message(9);
                                m_atk.writer().writeShort(conn.p.id);
                                m_atk.writer().writeByte(index_skill);
                                m_atk.writer().writeByte(1);
                                m_atk.writer().writeShort(pet_di_buon.index);
                                m_atk.writer().writeInt((int) dame); // dame
                                m_atk.writer().writeInt(pet_di_buon.hp); // hp mob after
                                m_atk.writer().writeByte(0);
                                m_atk.writer().writeInt((int) Math.min(conn.p.hp, Integer.MAX_VALUE));
                                m_atk.writer().writeInt(conn.p.mp);
                                m_atk.writer().writeByte(11); // 1: green, 5: small white 9: big white, 10: st dien, 11: st bang
                                m_atk.writer().writeInt(0); // dame plus
                                MapService.send_msg_player_inside(map, conn.p, m_atk, true);
                                m_atk.cleanup();
                                pet_di_buon.update_all(conn.p);
                            }
                        } else {
                            Mob_in_map mob_target = MapService.get_mob_by_index(map, n2);
                            if (mob_target != null) {
                                MapService.fire_mob(map, mob_target, conn.p, index_skill, dame);
                            } else if (conn.p.map.map_id == 48) {
                                } else if (conn.p.map.map_id == 48) {
    Dungeon d = DungeonManager.get_list(conn.p.party != null ? conn.p.party.getLeader().name : conn.p.name);
                                if (d != null) {
                                    Mob_Dungeon mod_target_dungeon = d.get_mob(n2);
                                    if (mod_target_dungeon != null) {
                                        d.fire_mob(map, mod_target_dungeon, conn.p, index_skill, (int) Math.min(dame, Integer.MAX_VALUE));
                                    }
                                }
                            } else if (Map.is_map_chiem_mo(conn.p.map, true) && conn.p.myclan != null) {
                                Mob_MoTaiNguyen temp_mob = conn.p.myclan.get_mo_tai_nguyen(n2);
                                if (temp_mob == null) {
                                    temp_mob = Manager.gI().chiem_mo.get_mob_in_map(conn.p.map);
                                    Message m_atk = new Message(9);
                                    m_atk.writer().writeShort(conn.p.id);
                                    m_atk.writer().writeByte(index_skill);
                                    m_atk.writer().writeByte(1);
                                    m_atk.writer().writeShort(temp_mob.index);
                                    if (temp_mob.nhanban != null) {
                                        int def_ = (int) ((((long) temp_mob.nhanban.def) * (10_001 - temp_mob.nhanban.pierce))
                                                / 10_000L);
                                        dame = (int) ((((long) dame) * (50_001 - def_)) / 50_000L);
                                    }
                                    if (!temp_mob.is_atk) {
                                        dame = 1;
                                    }
                                    m_atk.writer().writeInt((int) dame); // dame
                                    temp_mob.hp -= dame;
                                    if (temp_mob.hp <= 0 && temp_mob.is_atk) {
//                                        temp_mob.is_atk = false;
                                        temp_mob.hp = 0;
                                        if (conn.p.nv_tinh_tu[0] == 27 && conn.p.nv_tinh_tu[1] < conn.p.nv_tinh_tu[2]) {
											conn.p.nv_tinh_tu[1]++;
										}
                                        Manager.gI()
                                                .chatKTGprocess("@Server : @" + conn.p.name + " thuộc bang "
                                                        + conn.p.myclan.name_clan_shorted.toUpperCase() + " chiếm được "
                                                        + temp_mob.name_monster + " tại " + conn.p.map.name);
                                        conn.p.myclan.add_mo_tai_nguyen(temp_mob);
                                        if (temp_mob.clan != null) {
                                            temp_mob.clan.remove_mo_tai_nguyen(temp_mob);
                                        }
                                        temp_mob.clan = conn.p.myclan;
                                        if (temp_mob.nhanban != null) {
                                            Message m13 = new Message(8);
                                            m13.writer().writeShort(temp_mob.nhanban.id_p);
                                            for (int j = 0; j < map.players.size(); j++) {
                                                map.players.get(j).conn.addmsg(m13);
                                            }
                                            m13.cleanup();
                                            Manager.gI().remove_list_nhanbban(temp_mob.nhanban);
                                        }
                                        temp_mob.nhanban = new NhanBan();
                                        temp_mob.nhanban_save = temp_mob.nhanban;
                                        temp_mob.nhanban.setup(conn.p);
                                        temp_mob.nhanban.p_skill_id = index_skill;
                                        Manager.gI().add_list_nhanbban(temp_mob.nhanban);
                                        Message m12 = new Message(4);
                                        m12.writer().writeByte(0);
                                        m12.writer().writeShort(0);
                                        m12.writer().writeShort(temp_mob.nhanban.id_p);
                                        m12.writer().writeShort(temp_mob.nhanban.x);
                                        m12.writer().writeShort(temp_mob.nhanban.y);
                                        m12.writer().writeByte(-1);
                                        MapService.send_msg_player_inside(map, conn.p, m12, true);
                                        m12.cleanup();
                                    } else if (temp_mob.nhanban != null) {
                                        temp_mob.nhanban.p_target = conn.p;
                                        temp_mob.nhanban.is_move = false;
                                    }
                                    m_atk.writer().writeInt(temp_mob.hp); // hp mob after
                                    m_atk.writer().writeByte(0);
                                    m_atk.writer().writeInt((int) Math.min(conn.p.hp, Integer.MAX_VALUE));
                                    m_atk.writer().writeInt(conn.p.mp);
                                    m_atk.writer().writeByte(11);
                                    m_atk.writer().writeInt(0); // dame plus
                                    MapService.send_msg_player_inside(map, conn.p, m_atk, true);
                                    m_atk.cleanup();
                                    if (temp_mob.hp <= 0) {
                                        temp_mob.hp = temp_mob.hp_max = 4_000_000;
                                        temp_mob.hp = temp_mob.hp = temp_mob.hp = temp_mob.hp_max;
                                        //
                                        Message mm = new Message(7);
                                        mm.writer().writeShort(temp_mob.index);
                                        mm.writer().writeByte((byte) temp_mob.level);
                                        mm.writer().writeShort(temp_mob.x);
                                        mm.writer().writeShort(temp_mob.y);
                                        mm.writer().writeInt(temp_mob.hp);
                                        mm.writer().writeInt(temp_mob.hp_max);
                                        mm.writer().writeByte(0);
                                        mm.writer().writeInt(4);
                                        if (temp_mob.clan != null) {
                                            mm.writer().writeShort(temp_mob.clan.icon);
                                            mm.writer().writeInt(Clan.get_id_clan(temp_mob.clan));
                                            mm.writer().writeUTF(temp_mob.clan.name_clan_shorted);
                                            mm.writer().writeByte(122);
                                        } else {
                                            mm.writer().writeShort(-1);
                                        }
                                        mm.writer().writeUTF(temp_mob.name_monster);
                                        mm.writer().writeByte(0);
                                        mm.writer().writeByte(2);
                                        mm.writer().writeByte(0);
                                        mm.writer().writeUTF("");
                                        mm.writer().writeLong(-11111);
                                        mm.writer().writeByte(4);
                                        final int a = temp_mob.index;
                                        new Thread(() -> {
                                            try {
                                                Thread.sleep(5500L);
                                                MapService.send_msg_player_inside(map, conn.p, mm, true);
                                                mm.cleanup();
                                                Eff_player_in_map.add(conn.p, a);
                                            } catch (InterruptedException e) {
                                                e.printStackTrace();
                                            } catch (IOException e) {
                                                e.printStackTrace();
                                            }
                                        }).start();
                                    }
                                } else if (conn.p.myclan.mems.get(0).name.equals(conn.p.name) && conn.p.myclan.kimcuong >= 100
                                        && temp_mob.nhanban_save != null && temp_mob.nhanban == null) {
                                    conn.p.myclan.kimcuong -= 100;
                                    temp_mob.nhanban_save.hp = temp_mob.nhanban_save.hp_max;
                                    temp_mob.nhanban = temp_mob.nhanban_save;
                                    Manager.gI().add_list_nhanbban(temp_mob.nhanban);
                                    //
                                    Message m12 = new Message(4);
                                    m12.writer().writeByte(0);
                                    m12.writer().writeShort(0);
                                    m12.writer().writeShort(temp_mob.nhanban.id_p);
                                    m12.writer().writeShort(temp_mob.nhanban.x);
                                    m12.writer().writeShort(temp_mob.nhanban.y);
                                    m12.writer().writeByte(-1);
                                    MapService.send_msg_player_inside(map, conn.p, m12, true);
                                    m12.cleanup();
                                }
                            }
                        }
                    }
                } else if (type_atk == 1) {
                    for (int i = 0; i < n; ++i) {
                        int n2 = Short.toUnsignedInt(m.reader().readShort());

                        if (n2 == Short.toUnsignedInt((short) -1)) {
                            MapService.fire_player(map, Manager.gI().bossTG.p, conn.p, index_skill, dame, type);
                        } else {
                            Player p_target = MapService.get_player_by_id(map, n2);
                            if (p_target != null) {
                                MapService.fire_player(map, p_target, conn.p, index_skill, dame, type);
                            } else if (Map.is_map_chiem_mo(conn.p.map, true) && conn.p.myclan != null) {
                                Mob_MoTaiNguyen temp_mob = conn.p.myclan.get_mo_tai_nguyen(n2);
                                if (temp_mob == null) {
                                    temp_mob = Manager.gI().chiem_mo.get_mob_in_map(conn.p.map);
                                    if (temp_mob.nhanban != null && temp_mob.nhanban.id_p == n2) {
                                        Message m_p_2_nb = new Message(6);
                                        m_p_2_nb.writer().writeShort(conn.p.id);
                                        m_p_2_nb.writer().writeByte(index_skill);
                                        m_p_2_nb.writer().writeByte(1);
                                        m_p_2_nb.writer().writeShort(temp_mob.nhanban.id_p);
                                        boolean crit = conn.p.body.get_crit() > Util.random(0, 10000);
                                        if (crit) {
                                            dame *= 2;
                                        }
                                        int pierce_ = conn.p.body.get_pierce();
                                        if (pierce_ > 5000) {
                                            pierce_ = 5000;
                                        }
                                        int def_ = (int) ((((long) temp_mob.nhanban.def) * (10_001 - pierce_)) / 10_000L);
                                        dame = (int) ((((long) dame) * (50_001 - def_)) / 50_000L);
                                        if (dame <= 1) {
                                            dame = 1;
                                        }
                                        //
                                        temp_mob.nhanban.hp -= dame;
                                        if (temp_mob.nhanban.hp <= 0) {
                                            Manager.gI().remove_list_nhanbban(temp_mob.nhanban);
                                            temp_mob.nhanban.hp = 0;
                                            Message m_out = new Message(8);
                                            m_out.writer().writeShort(temp_mob.nhanban.id_p);
                                            for (int j = 0; j < map.players.size(); j++) {
                                                map.players.get(j).conn.addmsg(m_out);
                                            }
                                            m_out.cleanup();
                                            temp_mob.nhanban = null;
                                        } else {
                                            temp_mob.nhanban.p_target = conn.p;
                                            temp_mob.nhanban.is_move = false;
                                        }
                                        //
                                        m_p_2_nb.writer().writeInt((int) dame); // dame
                                        m_p_2_nb.writer().writeInt((temp_mob.nhanban != null) ? temp_mob.nhanban.hp : 0); // hp after
                                        //
                                        if (dame > 0 && crit) {
                                            m_p_2_nb.writer().writeByte(1); // size color show
                                            m_p_2_nb.writer().writeByte(4); // 1: xuyen giap, 2:hut hp, 3: hut mp, 4: chi mang, 5:
                                            // phan don
                                            m_p_2_nb.writer().writeInt((int) dame); // par
                                        } else {
                                            m_p_2_nb.writer().writeByte(0);
                                        }
                                        m_p_2_nb.writer().writeInt((int) Math.min(conn.p.hp, Integer.MAX_VALUE));
                                        m_p_2_nb.writer().writeInt(conn.p.mp); // mp nhan ban
                                        m_p_2_nb.writer().writeByte(11);
                                        m_p_2_nb.writer().writeInt(0);
                                        MapService.send_msg_player_inside(conn.p.map, conn.p, m_p_2_nb, true);
                                        m_p_2_nb.cleanup();
                                    }

                                }
                            } else {
                                Player_Nhan_Ban.atk(map, conn.p, n2, index_skill, (int) dame);
                            }
                        }
                    }
                }
            }
        }
    }

    private static Player get_player_by_id(Map map, int n2) {
        for (Player p0 : map.players) {
            if (p0.id == n2) {
                return p0;
            }
        }
        return null;
    }

    public static void fire_player(Map map, Player p0, Player p, byte indexskill, long dame_atk, int type_atk)
            throws IOException {
        if (p0 == null || p0.id == p.id || p.map.ismaplang || p.level < 11 || p0.level < 11
                || (p.typepk != 0 && p.typepk == p0.typepk) || p.hieuchien > 320_000) {
            return;
        }
        if (map.ld != null) {
            if (!(map.ld != null && map.ld.state_ld == 1
                    && (p0.id == map.ld.p1.id || p0.id == map.ld.p2.id) && (p.id == map.ld.p1.id || p.id == map.ld.p2.id))) {
                return;
            }
        }
        if (p0.isdie) {
            MapService.die_by_player(map, p0, p);
            return;
        }
        	if (Manager.gI().event == 2 && p.item.wear[11] != null
		      && (p.item.wear[11].id == 4790 || p.item.wear[11].id == 4791) && Math.abs(p0.level - p.level) > 5) {
			return;
		}
        //
        dame_atk -= ((dame_atk * (Util.random(10))) / 100);
        long dame = dame_atk;
        Message m = new Message(6);
        m.writer().writeShort(p.id);
        m.writer().writeByte(indexskill);
        m.writer().writeByte(1);
        m.writer().writeShort(p0.id);
        if (indexskill == 17) {
            MapService.add_eff_skill(map, p, p0, indexskill);
        }
        //
        if ((p0.get_eff(-126) != null || p.get_eff(-126) != null) && p0.map.ld == null) {
            return;
        }
        EffTemplate ef = null;
        int cr = p.body.get_crit();
        ef = p.get_eff(33);
        if (ef != null) {
            cr += ef.param;
        }
        boolean crit = cr > Util.random(0, 15000);
        boolean pierce = false;
        int rc_dame = p0.body.get_react_dame();
        ef = p0.get_eff(35);
        if (ef != null) {
            rc_dame += ef.param;
        }
        boolean react_dame = rc_dame > Util.random(0, 15000);
        if (crit) {
            dame *= 2;
        }
        if (!crit) {
            int pier = p.body.get_pierce();
            ef = p.get_eff(36);
            if (ef != null) {
                pier += ef.param;
            }
            if (pier > Util.random(0, 15000)) {
                pierce = true;
            }
        }
        int tylene = 80; // ty le hieu qua ne don

        if (!pierce) {
            int def = p0.body.get_def();
            ef = p0.get_eff(15);
            if (ef != null) {
                def += (def * (ef.param / 100)) / 100;
            }
            def -= (def * tylene) / 100;
            dame -= def;
            if (dame <= 0) {
                dame = 1;
            }
        }
        long resist_dame = (dame * (p0.body.get_resist_dame(type_atk, p.clazz, p0) / 100)) / 500; // khang vat ly;
        resist_dame -= (resist_dame * tylene) / 100;
        dame -= resist_dame;
        if (dame < 0) {
            dame = 1;
        }

        int miss = p0.body.get_miss();
        ef = p0.get_eff(34);
        if (ef != null) {
            miss += ef.param;
        }

        if (miss > Util.random(0, 2_000_000)) {
            dame = 0;
            pierce = false;
            react_dame = false;
            crit = false;
        }
        if (dame < 0) {
            dame = 0;
        }
        if (dame > 2_000_000_000) {
            dame = 2_000_000_000;
        }
        //
        boolean affect_special_skill = 5 > Util.random(0, 100);
        if (affect_special_skill && p.clazz == 2 && type_atk == 1) {
            p0.hp_restore = ((int) Math.min(p0.hp, Integer.MAX_VALUE) * 2) / 10;
            p0.hp -= p0.hp_restore;
        }
        //

        if (Manager.gI().bossTG.p.equals(p0)) {
            Manager.gI().bossTG.update_dame(p.name, dame);
            if (p0.hp < p0.body.get_max_hp() / 2) {
                Service.usepotion(p0, 0, p0.body.get_max_hp());
            }
        }

        p0.hp -= dame;
        if (p0.hp <= 0) {

            p0.hp = 0;
            if (!p0.isdie) {
                	Session conn = p.conn;
				if (conn.p.nv_tinh_tu[0] == 26 && conn.p.nv_tinh_tu[1] < conn.p.nv_tinh_tu[2]) {
					conn.p.nv_tinh_tu[1]++;
				}
				if (conn.p.nv_tinh_tu[0] == 31 && conn.p.nv_tinh_tu[1] < conn.p.nv_tinh_tu[2]) {
					conn.p.nv_tinh_tu[1]++;
				}
                if (p0.minuong != null) {
					Mob_in_map temp = p0.minuong.mob;
					Message m22 = new Message(7);
					m22.writer().writeShort(temp.index);
					m22.writer().writeByte((byte) temp.level);
					m22.writer().writeShort(temp.x);
					m22.writer().writeShort(temp.y);
					m22.writer().writeInt(temp.hp);
					m22.writer().writeInt(temp.hpmax);
					m22.writer().writeByte(0); // id skill monster (Spec: 32, ...)
					m22.writer().writeInt(Mob_in_map.time_refresh);
					m22.writer().writeShort(-1); // clan monster
					m22.writer().writeByte(1);
					m22.writer().writeByte(1); // speed
					m22.writer().writeByte(0);
					m22.writer().writeUTF("");
					m22.writer().writeLong(-11111);
					m22.writer().writeByte(temp.color_name); // color name 1: blue, 2: yellow
					MapService.send_msg_player_inside(map, p0, m22, true);
					m22.cleanup();
					p0.minuong.owner = "";
					p0.minuong = null;
				}
                if (Map.is_map_chien_truong(map.map_id)) {
                    p.update_point_arena(1);
                }
                if (p0.typepk == -1) {
                    if (!p0.list_enemies.contains(p.name) && !Manager.gI().bossTG.p.equals(p)) {
                        p0.list_enemies.add(p.name);
                        if (p0.list_enemies.size() > 20) {
                            p0.list_enemies.remove(0);
                        }
                    }
                }
                if (p.list_enemies.contains(p0.name) && !Manager.gI().bossTG.p.equals(p0)) {
                    p.list_enemies.remove(p0.name);
                    Service.send_notice_nobox_white(p.conn, "Báo thù thành công kkk");
                }
                //chet khi bat do sat

                MapService.die_by_player(map, p0, p);
                if (map.ld != null && map.ld.state_ld == 1) {
                    LoiDai.update_atk_when_someone_die(map.ld);
                } else if (map.ld == null) {
                    if (p.typepk == 0) {
                        p.update_vang(-500 * p.level);
                        p.update_Exp(-500 * p.level, true);
                        p.item.char_inventory(5);
                        p.hieuchien += 100;
                    }
                }
            }
        }
        //
        m.writer().writeInt((int) dame); // dame
        m.writer().writeInt((int) Math.min(p0.hp, Integer.MAX_VALUE)); // hp after
        //
        int dame_react = 0;
        //
        if (dame > 0 && crit) {
            if (react_dame) {
                m.writer().writeByte(2); // size color show
                //
                m.writer().writeByte(4); // 1: xuyen giap, 2:hut hp, 3: hut mp, 4: chi mang, 5: phan don
                m.writer().writeInt((int) dame); // par
                //
                m.writer().writeByte(5);
                m.writer().writeInt((int) dame);
            } else {
                m.writer().writeByte(1); // size color show
                //
                m.writer().writeByte(4); // 1: xuyen giap, 2:hut hp, 3: hut mp, 4: chi mang, 5: phan don
                m.writer().writeInt((int) dame); // par
                //
            }
        } else if (dame > 0 && pierce) {
            if (react_dame) {
                m.writer().writeByte(2); // size color show
                //
                m.writer().writeByte(1); // 1: xuyen giap, 2:hut hp, 3: hut mp, 4: chi mang, 5: phan don
                m.writer().writeInt((int) dame); // par
                //
                m.writer().writeByte(5);
                m.writer().writeInt((int) dame);
            } else {
                m.writer().writeByte(1); // size color show
                //
                m.writer().writeByte(1); // 1: xuyen giap, 2:hut hp, 3: hut mp, 4: chi mang, 5: phan don
                m.writer().writeInt((int) dame); // par
                //
            }
        } else {
            if (react_dame) {
                dame_react = (int) dame;
                int def2 = p.body.get_def();
                ef = p.get_eff(15);
                if (ef != null) {
                    def2 += (def2 * (ef.param / 100)) / 100;
                }
                long dameresist2 = (3L * def2);
                dameresist2 /= 1000L;
                dame_react -= (dame_react * (int) dameresist2) / 100;
                dame_react -= (dame_react * (p.body.get_resist_dame(type_atk, p.clazz, p) / 100)) / 100; // khang vat ly
                //
                m.writer().writeByte(2);
                m.writer().writeByte(0);
                m.writer().writeInt((int) dame);
                m.writer().writeByte(5);
                m.writer().writeInt(dame_react);
            } else {
                m.writer().writeByte(0);
            }
        }
        if (react_dame && dame_react > 0) {
            p.hp -= dame_react;
            if (p.hp <= 0) {
                MapService.die_by_player(map, p, p0);
            }
        }
        m.writer().writeInt((int) Math.min(p.hp, Integer.MAX_VALUE));
        m.writer().writeInt(p.mp);
        m.writer().writeByte(11);
        m.writer().writeInt(0);
        MapService.send_msg_player_inside(map, p, m, true);
        m.cleanup();
        //
        // affect_special_skill
        if (affect_special_skill) {
            int time_affect = 5000 + (p.level / 10) * 1000;
            if (type_atk == 0) { // st vl
                p0.add_eff(4, 99, time_affect);
                Eff_special_skill.send_eff(p0, 4, time_affect);
            } else if (type_atk == 1) { // st he
                if (p.clazz == 1) {
                    p0.dame_affect_special_sk = ((int) dame * 8) / 100;
                }
                p0.add_eff(p.clazz, 99, time_affect);
                Eff_special_skill.send_eff(p0, p.clazz, time_affect);
            }
            Service.send_char_main_in4(p0);
            for (int i = 0; i < map.players.size(); i++) {
                Player p2 = map.players.get(i);
                if (p2 != null && p2.id != p0.id) {
                    MapService.send_in4_other_char(map, p2, p0);
                }
            }
        }
        // exp_pk
        // if (p.typepk == 0 && p0.typepk != 0) {
        // if (p0.isdie) {
        // p.update_Exp(-(Level.entrys.get(p.level - 1).exp / 5), false);
        // } else {
        // p.update_Exp(-(dame * 10), false);
        // }
        // }
    }

    public static void die_by_player(Map map, Player p, Player p0) throws IOException {
        if (map.ld != null) {
            if (map.ld.p1.id == p.id) {
                for (int i = 0; i < map.players.size(); i++) {
                    Player p00 = map.players.get(i);
                    Service.send_notice_nobox_white(p00.conn, map.ld.p2.name + " dành chiến thắng ở hiệp " + map.ld.round);
                }
            } else if (map.ld.p2.id == p.id) {
                for (int i = 0; i < map.players.size(); i++) {
                    Player p00 = map.players.get(i);
                    Service.send_notice_nobox_white(p00.conn, map.ld.p1.name + " dành chiến thắng ở hiệp " + map.ld.round);
                }
            }
        }
        p.dame_affect_special_sk = 0;
        p.hp = 0;
        p.isdie = true;
        MapService.change_flag(map, p, -1);
        Message m = new Message(41);
        m.writer().writeShort(p.id);
        m.writer().writeShort(p0.id);
        m.writer().writeShort(-1); // point pk
        m.writer().writeByte(1); // type main object
        MapService.send_msg_player_inside(map, p, m, true);
        m.cleanup();
        if ( p.type_use_mount != 10) {
        p.type_use_mount = -1;
        }}

    private static void fire_mob(Map map, Mob_in_map mob, Player p, byte index_skill, long dame_atk) throws IOException {
        if (mob.isdie) {
            Message m2 = new Message(17);
            m2.writer().writeShort(p.id);
            m2.writer().writeShort(mob.index);
            p.conn.addmsg(m2);
            m2.cleanup();
            return;
        }
        switch (mob.template.mob_id) {
            case 89: {
                if (p.typepk == 4) {
                    return;
                }
                break;
            }
            case 90: {
                if (p.typepk == 2) {
                    return;
                }
                break;
            }
            case 91: {
                if (p.typepk == 5) {
                    return;
                }
                break;
            }
            case 92: {
                if (p.typepk == 1) {
                    return;
                }
                break;
            }
        }
        //
        dame_atk -= ((dame_atk * (Util.random(10))) / 100);
        long dame = dame_atk;
        //
        EffTemplate ef = null;
        int cr = p.body.get_crit();
        ef = p.get_eff(33);
        if (ef != null) {
            cr += ef.param;
        }
        boolean crit = cr > Util.random(0, 15000);
        boolean pierce = false;
        if (crit) {
            dame *= 2;
        }
        if (!crit) {
            int pier = p.body.get_pierce();
            ef = p.get_eff(36);
            if (ef != null) {
                pier += ef.param;
            }
            if (pier > Util.random(0, 15000)) {
                pierce = true;
            }
        }
        if (!pierce) {
            long dameresist = (mob.level * mob.level / 2);
            if (mob.is_boss) {
                dameresist *= 2;
            }
            dame -= dameresist;
            if (dame <= 0) {
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
        boolean check_mob_roi_ngoc_kham
                = mob.template.mob_id == 167 || mob.template.mob_id == 168 || mob.template.mob_id == 169
                || mob.template.mob_id == 170 || mob.template.mob_id == 171 || mob.template.mob_id == 172;
        if (check_mob_roi_ngoc_kham) {
            if (50 > Util.random(100)) {
                dame = 0;
            } else {
                dame = 1;
            }
        }
        if (5 > Util.random(0, 100)) { // mob get miss
            dame = 0;
        }
        if (mob.template.mob_id != 174 && !Map.is_map_chien_truong(map.map_id) && (dame < 0 || (mob.is_boss && (Math.abs(mob.level - p.level) > 5)))) {
            dame = 0;
        }
        if (dame > 2_000_000_000) {
            dame = 2_000_000_000;
        }

        if (mob.template.mob_id >= 89 && mob.template.mob_id <= 92) { // house chien truong
            dame /= 100;
        }

        mob.hp -= dame;
        // top dame
        if (!mob.top_dame.containsKey(p.name)) {
            mob.top_dame.put(p.name, (long) dame);
        } else {
            long dame_boss = dame + mob.top_dame.get(p.name);
            mob.top_dame.put(p.name, dame_boss);
        }
        // if mob die
        if (mob.hp <= 0) {
            mob.hp = 0;
            // mob die
            if (!mob.isdie) {
                mob.isdie = true;
                mob.time_back = System.currentTimeMillis() + (Mob_in_map.time_refresh * 1000) - 1000L;
if (mob.template.mob_id == 23 || mob.template.mob_id == 17) {
					Session conn = p.conn;
					if (conn.p.nv_tinh_tu[0] == 7 && conn.p.nv_tinh_tu[1] < conn.p.nv_tinh_tu[2]) {
						conn.p.nv_tinh_tu[1]++;
					}
					if (conn.p.nv_tinh_tu[0] == 8 && conn.p.nv_tinh_tu[1] < conn.p.nv_tinh_tu[2]) {
						conn.p.nv_tinh_tu[1]++;
					}
				}
	if (Math.abs(mob.level - p.level) <= 5) {
					Session conn = p.conn;
					if (conn.p.nv_tinh_tu[0] == 16 && conn.p.nv_tinh_tu[1] < conn.p.nv_tinh_tu[2]) {
						conn.p.nv_tinh_tu[1]++;
					}
				}
                if (mob.template.mob_id >= 89 && mob.template.mob_id <= 92) { // house chien truong
                    p.update_point_arena(20);
                    Manager.gI().chatKTGprocess("@Server : " + p.name + " đã đánh sập " + mob.template.name);
                    ChienTruong.gI().update_house_die(mob.template.mob_id);
                }

                if (mob.is_boss) {
                    String p_name = "";
                    long top_dame = 0;
                    for (Entry<String, Long> en : mob.top_dame.entrySet()) {
                        if (en.getValue() > top_dame) {
                            top_dame = en.getValue();
                            p_name = en.getKey();
                        }
                    }

                    mob.is_boss_active = false;
                    Manager.gI().chatKTGprocess("@Server : " + p.name + " đã tiêu diệt " + mob.template.name + ", Top sát thương lên boss: " + p_name + " gây " + top_dame + " sát thương");
                    LeaveItemMap.leave_item_boss(map, mob, p);
                    LeaveItemMap.leave_item_boss(map, mob, p);
                    LeaveItemMap.leave_item_boss(map, mob, p);
                    Mob_in_map.num_boss--;
                } else {
                    // item drop
                    if (Math.abs(mob.level - p.level) <= 10 && !check_mob_roi_ngoc_kham && p.dropnlmd == 1) {
                        if (10 > Util.random(0, 300) || mob.color_name != 0) {
                            LeaveItemMap.leave_item_3(map, mob, p);
                        }
                        if (20 > Util.random(0, 300)) {
                            LeaveItemMap.leave_item_4(map, mob, p);
                        }
                        if (20 > Util.random(0, 300)) {
                            LeaveItemMap.leave_item_7(map, mob, p);
                        }
                        if (30 > Util.random(0, 100)) {
                            LeaveItemMap.leave_vang(map, mob, p);
                        }
                        if (30 > Util.random(0, 100)) {
                            LeaveItemMap.leave_material(map, mob, p);
                        }
                        if (Manager.gI().event != 0 && 30 > Util.random(0, 100) && Math.abs(mob.level - p.level) <= 5) {
                            LeaveItemMap.leave_item_event(map, mob, p);
                        }
                    }if (Math.abs(mob.level - p.level) <= 10 && !check_mob_roi_ngoc_kham && p.dropnlmd == 0) {
                        if (10 > Util.random(0, 300) || mob.color_name != 0) {
                            LeaveItemMap.leave_item_3(map, mob, p);
                        }
                        if (20 > Util.random(0, 300)) {
                            LeaveItemMap.leave_item_4(map, mob, p);
                        }
                        if (20 > Util.random(0, 300)) {
                            LeaveItemMap.leave_item_7(map, mob, p);
                        }
                        if (30 > Util.random(0, 100)) {
                            LeaveItemMap.leave_vang(map, mob, p);
                        }
                        if (Manager.gI().event != 0 && 30 > Util.random(0, 100) && Math.abs(mob.level - p.level) <= 5) {
                            LeaveItemMap.leave_item_event(map, mob, p);
                        }
                    }
                    if (check_mob_roi_ngoc_kham) {
                        LeaveItemMap.leave_material_ngockham(map, mob, p);
                    }
                    if (mob.color_name != 0) {
                        map.num_mob_super--;
                    }
                    //
                    if (p.quest_daily[0] == 0 && p.quest_daily[1] == mob.template.mob_id && p.quest_daily[3] < p.quest_daily[4]) {
                        p.quest_daily[3]++;
                    }
                }
                if (((p.tutien[0] == 10) ? 20 : ((p.tutien[0] == 9) ? 10 : -1)) > Util.random(500)) {
                    p.update_ngoc((p.tutien[0] == 10) ? 2 : ((p.tutien[0] == 9) ? 1 : 0));
                    p.item.char_inventory(5);
                }
                if (((p.kinhmach[0] == 8) ? 20 : ((p.kinhmach[0] == 7) ? 10 : -1)) > Util.random(500)) {
                    p.update_ngoc((p.kinhmach[0] == 8) ? 2 : ((p.kinhmach[0] == 7) ? 1 : 0));
                    p.item.char_inventory(5);
                }
                if (p.luyenthe == 12 && 20 > Util.random(500)) {
                    p.update_ngoc(1);
                    p.item.char_inventory(5);
                }
                // send p outside
                Message m2 = new Message(17);
                m2.writer().writeShort(p.id);
                m2.writer().writeShort(mob.index);
                for (int i = 0; i < map.players.size(); i++) {
                    Player p2 = map.players.get(i);
                    if (!((Math.abs(p2.x - p.x) < 200) && (Math.abs(p2.y - p.y) < 200))) {
                        p2.conn.addmsg(m2);
                    }
                }
                m2.cleanup();
            }
        }
        // attached in4
        Message m = new Message(9);
        m.writer().writeShort(p.id);
        m.writer().writeByte(index_skill);
        m.writer().writeByte(1);
        m.writer().writeShort(mob.index);
        m.writer().writeInt((int) dame); // dame
        m.writer().writeInt(mob.hp); // hp mob after
        if (dame > 0 && crit) {
            m.writer().writeByte(1); // size color show
            //
            m.writer().writeByte(4); // 1: xuyen giap, 2:hut hp, 3: hut mp, 4: chi mang, 5: phan don
            m.writer().writeInt((int) dame); // par
            //
        } else if (dame > 0 && pierce) {
            m.writer().writeByte(1); // size color show
            //
            m.writer().writeByte(1); // 1: xuyen giap, 2:hut hp, 3: hut mp, 4: chi mang, 5: phan don
            m.writer().writeInt((int) dame); // par
            //
        } else {
            m.writer().writeByte(0);
        }
        if (!mob.list_fight.contains(p)) {
            mob.list_fight.add(p);
        }
        for (int i = 0; i < ChienTruong.gI().list_ai.size(); i++) {
            Player_Nhan_Ban temp = ChienTruong.gI().list_ai.get(i);
            if (!temp.isdie && temp.map.equals(map) && temp.time_change_target < System.currentTimeMillis()) {
                temp.time_change_target = System.currentTimeMillis() + 5000L;
                temp.target = p.id;
            }
        }
        m.writer().writeInt((int) Math.min(p.hp, Integer.MAX_VALUE));
        m.writer().writeInt(p.mp);
        m.writer().writeByte(11); // 1: green, 5: small white 9: big white, 10: st dien, 11: st bang
        m.writer().writeInt(0); // dame plus
        MapService.send_msg_player_inside(map, p, m, true);
        m.cleanup();
        // exp
        long expup = 0;
        expup = dame; // tinh exp
        if (p.level <= 10) {
            expup = expup * 3;
        }
        if (Math.abs(mob.level - p.level) == 0) {
            expup = expup;
        } else if (Math.abs(mob.level - p.level) > 1) {
            expup = (expup * 11) / 10;
        } else if (Math.abs(mob.level - p.level) > 2) {
            expup = (expup * 12) / 10;
        } else if (Math.abs(mob.level - p.level) > 3) {
            expup = (expup * 13) / 10;
        } else if (Math.abs(mob.level - p.level) > 4) {
            expup = (expup * 14) / 10;
        } else if (Math.abs(mob.level - p.level) > 5) {
            expup = (expup * 15) / 10;
        } else if (Math.abs(p.level - mob.level) > 1) {
            expup = (expup * 9) / 10;
        } else if (Math.abs(p.level - mob.level) > 2) {
            expup = (expup * 8) / 10;
        } else if (Math.abs(p.level - mob.level) > 3) {
            expup = (expup * 7) / 10;
        } else if (Math.abs(p.level - mob.level) > 4) {
            expup = (expup * 6) / 10;
        } else if (Math.abs(p.level - mob.level) > 5) {
            expup = (expup * 5) / 10;
        }
        if (p.hieuchien > 0) {
            expup /= 2;
        }
        if (Math.abs(mob.level - p.level) <= 10 && expup > 0) {
            if (p.party != null) {
                for (int i = 0; i < p.party.get_mems().size(); i++) {
                    Player pm = p.party.get_mems().get(i);
                    if (pm.id != p.id && (Math.abs(pm.level - p.level) < 10) && pm.map.map_id == p.map.map_id
                            && pm.map.zone_id == p.map.zone_id) {
                        pm.update_Exp((expup / 10), true);
                    }
                }
                // nhan cuoi
                if (p.it_wedding != null) {
                    for (int i = 0; i < p.party.get_mems().size(); i++) {
                        Player pm = p.party.get_mems().get(i);
                        if (p.it_wedding.equals(pm.it_wedding)) {
                            p.it_wedding.exp += dame / 100;
                            break;
                        }
                    }
                }
            }
            ef = p.get_eff(-125);
            if (ef != null) {
                expup += (expup * (ef.param / 100)) / 100;
            }
            p.update_Exp(expup, true);
        } else if (expup > 0) {
            p.update_Exp(2, false);
        }
        // exp clan
        if (p.myclan != null) {
            int exp_clan = ((int) dame) / 10_000;
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
                int dame_pet = Util.random(a1, Math.max((a2 + 1), (a1 + 1))) - (mob.level * 15);
                if (dame_pet <= 0) {
                    dame_pet = 1;
                }
                if (((mob.hp - dame_pet) > 0) && (p.pet_atk_speed < System.currentTimeMillis()) && (a2 > 1)) {
                    p.pet_atk_speed = System.currentTimeMillis() + 1500L;
                    m = new Message(84);
                    m.writer().writeByte(2);
                    m.writer().writeShort(p.id);
                    m.writer().writeByte(1);
                    m.writer().writeByte(1);
                    m.writer().writeShort(mob.index);
                    m.writer().writeInt(dame_pet);
                    mob.hp -= dame_pet;
                    m.writer().writeInt(mob.hp);
                    m.writer().writeInt((int) Math.min(p.hp, Integer.MAX_VALUE));
                    m.writer().writeInt((int) Math.min(p.body.get_max_hp(), Integer.MAX_VALUE));
                    p.conn.addmsg(m);
                    m.cleanup();
                }
            }
        }
    }

    public static void use_item_arena(Map map, Player p, short id) throws IOException {
        if (Map.is_map_chien_truong(map.map_id)) {
            if (p.time_use_item_arena < System.currentTimeMillis()) {
                if (map.time_use_item_arena < System.currentTimeMillis()) {
                    boolean ch = false;
                    switch (map.map_id) {
                        case 54: {
                            if (p.typepk == 5) {
                                ch = true;
                            }
                            break;
                        }
                        case 56: {
                            if (p.typepk == 2) {
                                ch = true;
                            }
                            break;
                        }
                        case 58: {
                            if (p.typepk == 4) {
                                ch = true;
                            }
                            break;
                        }
                        case 60: {
                            if (p.typepk == 1) {
                                ch = true;
                            }
                            break;
                        }
                    }
                    if (ch) {
                        switch (id) {
                            case 57: {
                                break;
                            }
                            case 58: {
                                for (int i2 = 0; i2 < map.players.size(); i2++) {
                                    Player p0 = map.players.get(i2);
                                    if (p0.typepk != p.typepk) {
                                        // Message m = new Message(6);
                                        // m.writer().writeByte(1);
                                        // m.writer().writeShort(102);
                                        // m.writer().writeShort(p0.id);
                                        // for (int i = 0; i < map.players.size(); i++) {
                                        // Player p01 = map.players.get(i);
                                        // p01.conn.addmsg(m);
                                        // }
                                        // m.cleanup();
                                        p0.id_henshin = 102;
                                        p0.time_henshin = System.currentTimeMillis() + 6_000L;
                                        for (int i = 0; i < map.players.size(); i++) {
                                            Player p01 = map.players.get(i);
                                            MapService.send_in4_other_char(map, p01, p0);
                                        }
                                    }
                                }
                                break;
                            }
                        }
                        p.item.remove(4, id, 1);
                        p.update_point_arena(10);
                        map.time_use_item_arena = System.currentTimeMillis() + 250_000L;
                        p.time_use_item_arena = System.currentTimeMillis() + 250_000L;
                    }
                } else {
                    Service.send_notice_box(p.conn,
                            "Sử dụng sau " + (map.time_use_item_arena - System.currentTimeMillis()) / 1000 + " s");
                }
            } else {
                Service.send_notice_box(p.conn,
                        "Sử dụng sau " + (p.time_use_item_arena - System.currentTimeMillis()) / 1000 + " s");
            }
        }
    }
}
