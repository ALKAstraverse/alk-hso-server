package client;

import ai.NhanBan;
import ai.Player_Nhan_Ban;
import core.BXH;
import java.io.IOException;
import java.util.Map.Entry;
import core.GameSrc;
import core.Manager;
import core.MenuController;
import core.Service;
import core.Util;
import event.BossEvent;
import event_daily.ChienTruong;
import event_daily.MoLy;
import io.Message;
import io.Session;
import map.Dungeon;
import map.DungeonManager;
import map.Map;
import map.MapService;

public class MessageHandler {

    private final Session conn;

    public MessageHandler(Session conn) {
        this.conn = conn;
    }

    public void process_msg(Message m) throws IOException {
        switch (m.cmd) {
            case -103: {
                if (conn.p.minuong == null) {
                    for (int i = 0; i < BossEvent.MiNuong.size(); i++) {
                        if (BossEvent.MiNuong.get(i).map.equals(conn.p.map)) {
                            synchronized (BossEvent.MiNuong.get(i)) {
                                if (BossEvent.MiNuong.get(i).owner.isBlank()) {
                                    MenuController.send_menu_select(conn, 996, new String[]{"Giải cứu"});
                                } else {
                                    MenuController.send_menu_select(conn, 996,
                                            new String[]{"Tôi không thuộc về anh đâu, tránh xa tôi ra"});
                                }
                            }
                            break;
                        }
                    }
                } else {
                    if (conn.p.minuong.owner.equals(conn.p.name)) {
                        if (conn.p.minuong.power > 0) {
                            MenuController.send_menu_select(conn, 996, new String[]{"Chia tay"});
                        } else {
                            MenuController.send_menu_select(conn, 996,
                                    new String[]{"Chia tay", "Ta mệt quá hãy tiếp sức cho ta"});
                        }
                    } else {
                        MenuController.send_menu_select(conn, 996,
                                new String[]{"Tôi không thuộc về anh đâu, tránh xa tôi ra"});
                    }
                }
                break;
            }
            case -100: {
                GameSrc.Hop_Ngoc_Kham(conn.p, m);
                break;
            }
            case -102: {
                GameSrc.player_store(conn, m);
                break;
            }
            case -91: {
                if (m.reader().available() == 4) {
                    Service.remove_time_use_item(conn, m);
                } else if (m.reader().available() == 2) {
                    MoLy.Lottery_process(conn.p, m);
                } else if (m.reader().available() == 1) {
                    MoLy.show_table_to_choose_item(conn.p);
                }
                break;
            }
            case 77: {
                GameSrc.Wings_Process(conn, m);
                break;
            }
            case -105: {
                GameSrc.Create_Medal(conn, m);
                break;
            }
            case 69: {
                byte type = m.reader().readByte();
                if (type == 11) {
                    Player p0 = Map.get_player_by_name(m.reader().readUTF());
                    if (p0 != null && p0.myclan != null) {
                        p0.myclan.accept_mem(conn, p0);
                    }
                } else if (conn.p.myclan != null) {
                    conn.p.myclan.clan_process(conn, m, type);
                }
                break;
            }
            case 73: {
                GameSrc.replace_item_process(conn.p, m);
                break;
            }
            case 36: {

                if (conn.status != 0) {
                    Service.send_notice_box(conn, "Tài khoản chưa được kích hoạt, hãy kích hoạt");
                    return;
                }
                GameSrc.trade_process(conn, m);
                break;
            }
            case 48: {
                conn.p.map.create_party(conn, m);
                break;
            }
            case 67: {
                GameSrc.rebuild_item(conn, m);
                break;
            }
            case 9: {
                // if (conn.p.map.map_id == 48) {
                // conn.p.dungeon.use_skill(conn, m);
                // } else {
                // conn.p.map.use_skill(;
                // }
                MapService.use_skill(conn.p.map, conn, m, 0);
                break;
            }
            case 6: {
                // if (conn.p.map.map_id == 48) {
                // } else {
                // conn.p.map.use_skill(conn, m, 1);
                // }
                MapService.use_skill(conn.p.map, conn, m, 1);
                break;
            }
            case 40: {
                // if (conn.p.map.map_id == 48) {
                // } else {
                // conn.p.map.buff_skill(conn, m);
                // }
                MapService.buff_skill(conn.p.map, conn, m);
                break;
            }
            case 20: {
                if (conn.p.map.map_id == 48) {
                } else {
                    conn.p.map.pick_item(conn, m);
                }
                break;
            }
            case 11: {
                if (conn.p.time_speed_rebuild > System.currentTimeMillis()) {
                    if (++conn.p.enough_time_disconnect > 2) {
                        conn.close();
                    }
                    return;
                }
                conn.p.time_speed_rebuild = System.currentTimeMillis() + 500L;
                conn.p.enough_time_disconnect = 0;
                UseItem.ProcessItem3(conn, m);
                break;
            }
            case -107: {
                if (conn.p.time_speed_rebuild > System.currentTimeMillis()) {
                    if (++conn.p.enough_time_disconnect > 2) {
                        conn.close();
                    }
                    return;
                }
                conn.p.time_speed_rebuild = System.currentTimeMillis() + 500L;
                conn.p.enough_time_disconnect = 0;
                UseItem.ProcessItem7(conn, m);
                break;
            }
            case 32: {
                if (conn.p.time_speed_rebuild > System.currentTimeMillis()) {
                    if (++conn.p.enough_time_disconnect > 2) {
                        conn.close();
                    }
                    return;
                }
                conn.p.time_speed_rebuild = System.currentTimeMillis() + 500L;
                conn.p.enough_time_disconnect = 0;
                UseItem.ProcessItem4(conn, m);
                break;
            }
            case 24: {
                Service.buy_item(conn.p, m);
                break;
            }
            case 18: {
                Service.sell_item(conn, m);
                break;
            }
            case 37: {
                // arena
                break;
            }
            case 65: {
                conn.p.item.box_process(m);
                break;
            }
            case 44: {
                Service.pet_process(conn, m);
                break;
            }
            case 45: {
                Service.pet_eat(conn, m);
                break;
            }
            case 35: {
                conn.p.friend_process(m);
                break;
            }
            case 34: {
                Service.chat_tab(conn, m);
                break;
            }
            case 22: {
                conn.p.plus_point(m);
                break;
            }
            case -32: {
                Process_Yes_no_box.process(conn, m);
                break;
            }
            case -106: {
                Service.send_item7_template(conn.p, m);
                break;
            }
            case -97: {
                conn.p.down_mount(m);
                break;
            }
            case 28: {
                Service.send_in4_item(conn, m);
                break;
            }
            case 31: {
                // if (conn.p.map.map_id == 48) {
                // conn.p.dungeon.request_livefromdie(conn, m);
                // } else {
                // conn.p.map.request_livefromdie(conn, m);
                // }
                MapService.request_livefromdie(conn.p.map, conn, m);
                break;
            }
            case -31: {
                TextFromClient.process(conn, m);
                break;
            }
            case -53: {
                TextFromClient_2.process(conn, m);
                break;
            }
            case 21: {
                Service.send_param_item_wear(conn, m);
                break;
            }
            case 51: {
                conn.p.change_zone(conn, m);
                break;
            }
            case 42: {
                MapService.change_flag(conn.p.map, conn.p, m.reader().readByte());
                break;
            }
            case 49: {
                Service.send_view_other_player_in4(conn, m);
                break;
            }
            case 71: {
                if (conn.status != 0) {
                    Service.send_notice_box(conn, "Tài khoản chưa được kích hoạt, hãy kích hoạt");
                    return;
                }
                Service.chat_KTG(conn, m);
                Service.chat_KTG(conn, m);
				if (conn.p.nv_tinh_tu[0] == 20 && conn.p.nv_tinh_tu[1] < conn.p.nv_tinh_tu[2]) {
					conn.p.nv_tinh_tu[1]++;
				}
                break;
            }
            case -30: {
                MenuController.processmenu(conn, m);
                break;
            }
            case 23: {
                MenuController.request_menu(conn, m);
                break;
            }
            case 27: {
                // if (conn.p.map.map_id == 48) {
                // conn.p.dungeon.send_chat(conn, m);
                // } else {
                // conn.p.map.send_chat(conn, m);
                // }
                MapService.send_chat(conn.p.map, conn, m);
                break;
            }
            case 12: {
                conn.p.is_changemap = false;
                if (conn.p.map.equals(Manager.gI().bossTG.map)) {
                    Message m3 = new Message(4);
                    m3.writer().writeByte(0);
                    m3.writer().writeShort(0);
                    m3.writer().writeShort(Manager.gI().bossTG.id);
                    m3.writer().writeShort(Manager.gI().bossTG.p.x);
                    m3.writer().writeShort(Manager.gI().bossTG.p.y);
                    m3.writer().writeByte(-1);
                    conn.addmsg(m3);
                    m3.cleanup();
                }

                if (Map.is_map_chien_truong(conn.p.map.map_id)) {
                    ChienTruong.gI().send_info(conn.p);
                    //
                    Message m22 = new Message(4);
                    for (int i = 0; i < ChienTruong.gI().list_ai.size(); i++) {
                        Player_Nhan_Ban p0 = ChienTruong.gI().list_ai.get(i);
                        if (!p0.isdie && p0.map.equals(conn.p.map)) {
                            m22.writer().writeByte(0);
                            m22.writer().writeShort(0);
                            m22.writer().writeShort(p0.id);
                            m22.writer().writeShort(p0.x);
                            m22.writer().writeShort(p0.y);
                            m22.writer().writeByte(-1);
                        }
                    }
                    if (m22.writer().size() > 0) {
                        for (int i = 0; i < conn.p.map.players.size(); i++) {
                            Player p0 = conn.p.map.players.get(i);
                            p0.conn.addmsg(m22);
                        }
                    }
                    m22.cleanup();
                }
                if (conn.p.map.map_id == 48) {
                    // weather map dungeon
                    Message mw = new Message(76);
                    mw.writer().writeByte(4);
                    mw.writer().writeShort(-1);
                    mw.writer().writeShort(-1);
                    conn.addmsg(mw);
                    mw.cleanup();
                }
                if (conn.p.map.ld != null) {
                    if (conn.p.map.ld.p1.id == conn.p.id) {
                        MapService.leave(conn.p.map, conn.p.map.ld.p1);
                        conn.p.map.ld.p1 = conn.p;
                    } else if (conn.p.map.ld.p2.id == conn.p.id) {
                        MapService.leave(conn.p.map, conn.p.map.ld.p2);
                        conn.p.map.ld.p2 = conn.p;
                    }
                    Message m3 = new Message(4);
                    if (conn.p.id == conn.p.map.ld.p1.id) {
                        m3.writer().writeByte(0);
                        m3.writer().writeShort(0);
                        m3.writer().writeShort(conn.p.map.ld.p2.id);
                        m3.writer().writeShort(conn.p.map.ld.p2.x);
                        m3.writer().writeShort(conn.p.map.ld.p2.y);
                        m3.writer().writeByte(-1);
                    } else if (conn.p.id == conn.p.map.ld.p2.id) {
                        m3.writer().writeByte(0);
                        m3.writer().writeShort(0);
                        m3.writer().writeShort(conn.p.map.ld.p1.id);
                        m3.writer().writeShort(conn.p.map.ld.p1.x);
                        m3.writer().writeShort(conn.p.map.ld.p1.y);
                        m3.writer().writeByte(-1);
                    } else {
                        m3.writer().writeByte(0);
                        m3.writer().writeShort(0);
                        m3.writer().writeShort(conn.p.map.ld.p2.id);
                        m3.writer().writeShort(conn.p.map.ld.p2.x);
                        m3.writer().writeShort(conn.p.map.ld.p2.y);
                        m3.writer().writeByte(-1);
                        //
                        m3.writer().writeByte(0);
                        m3.writer().writeShort(0);
                        m3.writer().writeShort(conn.p.map.ld.p1.id);
                        m3.writer().writeShort(conn.p.map.ld.p1.x);
                        m3.writer().writeShort(conn.p.map.ld.p1.y);
                        m3.writer().writeByte(-1);
                    }
                    //
                    conn.addmsg(m3);
                    m3.cleanup();
                    //
                    m3 = new Message(-103);
                    m3.writer().writeByte(1);
                    if (conn.p.id == conn.p.map.ld.p1.id) {
                        m3.writer().writeShort(conn.p.map.ld.p1.id);
                        m3.writer().writeShort(conn.p.map.ld.p2.id);
                    } else {
                        m3.writer().writeShort(conn.p.map.ld.p2.id);
                        m3.writer().writeShort(conn.p.map.ld.p1.id);
                    }
                    conn.addmsg(m3);
                    m3.cleanup();
                    //
                    conn.p.map.ld.update_time_atk();
                }
                break;
            }
            case -44: {
                Dungeon d = DungeonManager.get_list(conn.p.name);
                if (d != null) {
                    d.send_in4_npc(conn, m);
                }
                break;
            }
            case 5: {
                int id = Short.toUnsignedInt(m.reader().readShort());
                Player p0 = null;
                if (id == Short.toUnsignedInt((short) -1)) { // boss tg
                    Manager.gI().bossTG.send_in4(conn.p);
                } else {
                    for (int i = 0; i < conn.p.map.players.size(); i++) {
                        Player p01 = conn.p.map.players.get(i);
                        if (p01.id == id) {
                            p0 = p01;
                            break;
                        }
                    }
                    if (p0 != null) {
                        MapService.send_in4_other_char(conn.p.map, conn.p, p0);
                    } else if (Map.is_map_chiem_mo(conn.p.map, true)) {
                        NhanBan temp = null;
                        for (int i = 0; i < Manager.gI().list_nhanban.size(); i++) {
                            NhanBan temp2 = Manager.gI().list_nhanban.get(i);
                            if (temp2.id_p == id) {
                                temp = temp2;
                                break;
                            }
                        }
                        if (temp != null) {
                            temp.send_in4(conn.p);
                        }
                    } else if (Map.is_map_chien_truong(conn.p.map.map_id)) {
                        ChienTruong.gI().get_ai(conn.p, id);
                    } else {
                        Message m3 = new Message(8);
                        m3.writer().writeShort(id);
                        conn.addmsg(m3);
                        m3.cleanup();
                    }
                }
                break;
            }
            case 7: {
                int n = Short.toUnsignedInt(m.reader().readShort());
                Dungeon d = DungeonManager.get_list(conn.p.name);
                if (d != null) {
                    d.send_mob_in4(conn, n);
                } else {
                    Service.mob_in4(conn.p, n);
                }
                break;
            }
            case 4: {
                // if (conn.p.map.map_id == 48) {
                // conn.p.dungeon.send_move(conn.p, m);
                // } else {
                // conn.p.map.send_move(conn.p, m);
                // }
                MapService.send_move(conn.p.map, conn.p, m);
                break;
            }
            case -51: {
                Service.send_icon(conn, m);
                break;
            }
            case -52: {
                try {
                    byte type = m.reader().readByte();
                    short id = m.reader().readShort();
                    Message m2 = new Message(-52);
                    m2.writer().writeByte(type);
                    m2.writer().writeShort(id);
                    m2.writer().write(Util.loadfile("data/part_char/imgver/x" + conn.zoomlv + "/" + (type + "_" + id)));
                    conn.addmsg(m2);
                    m2.cleanup();
                } catch (IOException e) {
                }
                break;
            }
            case 55: {
                Service.save_rms(conn, m);
                break;
            }
            case 59: {
                Service.send_health(conn.p);
                break;
            }
            case 13: {
                login(m);
                break;
            }
            case 14: {
                conn.char_create(m);
                break;
            }
            case 1: {
                if (!conn.get_in4) {
                    conn.getclientin4(m);
                }
                break;
            }
            case 61: {
                Service.send_msg_data(conn, 61, Manager.gI().msg_61);
                Service.send_item_template(conn);
                Service.send_msg_data(conn, 26, Manager.gI().msg_26);
                break;
            }
            default: {
                System.out.println("default onRecieveMsg : " + m.cmd);
                break;
            }
        }
    }

    private void login(Message m) throws IOException {
        synchronized (Session.client_entrys) {
            if (conn.p == null) {
                m.reader().readByte(); // type login
                int id_player_login = m.reader().readInt();
                Player p0 = new Player(conn, id_player_login);
                if (p0.setup()) {
                    if (!Session.players.containsKey(conn.user)) {
                        Session.players.put(conn.user, conn);
                    } else {
//                        for (int i = Session.client_entrys.size() - 1; i >= 0; i--) {
//                            if (Session.client_entrys.get(i).user != null && Session.client_entrys.get(i).user.equals(conn.user)) {
//                                Session.client_entrys.get(i).close();
//                            }
//                        }
                        Session.players.get(conn.user).close();
                        Session.players.remove(conn.user);
                        conn.close();
                        return;
                    }

                    p0.set_in4();
                    conn.p = p0;
                    MessageHandler.dataloginmap(conn);
                }
            }
        }
    }

    private static void dataloginmap(Session conn) throws IOException {
        Service.send_quest(conn);
        Service.send_auto_atk(conn);
        Service.send_char_main_in4(conn.p);
        Service.send_msg_data(conn, 1, Manager.gI().msg_1);
        Service.send_skill(conn.p);
        Service.send_login_rms(conn);
        Service.send_notice_nobox_yellow(conn, ("Số người online : " + (Session.client_entrys.size())));
        // add x2 xp
        conn.p.set_x2_xp(1);
        MapService.enter(conn.p.map, conn.p);
        //
        if (Map.name_mo.equals(conn.p.name)) {
            Manager.gI().chatKTGprocess("Ông thần top hiếu chiến " + conn.p.name + " đã online game ae cẩn thận không thì hút xì gà đấy!!!");
        }
        if (conn.p.myclan != null && BXH.BXH_clan.indexOf(conn.p.myclan) < 3) {
            Manager.gI().chatKTGprocess("Thủ lĩnh bang top (" + (BXH.BXH_clan.indexOf(conn.p.myclan) + 1) + ") " + conn.p.name + " đã online game!!!");
        }
    }

}
