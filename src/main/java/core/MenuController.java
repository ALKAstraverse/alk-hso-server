package core;

import event.Event_1;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import client.Clan;
import client.Pet;
import client.Player;
import event.BossEvent;
import event_daily.ChienTruong;
import event_daily.CongNap;
import event_daily.DailyQuest;
import event_daily.KinhMach;
import event_daily.LoiDai;
import event_daily.LuyenThe;
import event_daily.MoLy;
import event_daily.NV_TinhTu;
import event_daily.TuTien;
import event_daily.Wedding;
import io.Message;
import io.Session;
import java.net.Socket;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map.Entry;
import map.Dungeon;
import map.Map;
import map.MapService;
import map.Mob_in_map;
import map.TinhTu_Material;
import map.Vgo;
import template.EffTemplate;
import template.Item3;
import template.Item47;
import template.ItemTemplate3;
import template.ItemTemplate4;
import template.ItemTemplate7;
import template.Level;
import template.Medal_Material;
import template.Member_ChienTruong;
import template.Option;
import template.OptionItem;
import template.Option_pet;
import template.Part_fashion;
import template.Pet_di_buon_manager;
import template.TaiXiuPlayer;
import template.Top_Dame;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MenuController {

    public static void request_menu(Session conn, Message m) throws IOException {
        byte idnpc = m.reader().readByte();
        boolean npc_in_map = false;
        for (int i = 0; i < conn.p.list_npc.size(); i++) {
            if (conn.p.list_npc.get(i).id == idnpc) {
                npc_in_map = true;
                break;
            }
        }
        if (!npc_in_map) {
//            return;
        }
        if (idnpc == -43 || idnpc == -45 || idnpc == -48 || idnpc == -46) {
            Menu_ChangeZone(conn);
            return;
        }
        // create menu per id npc
        String[] menu;
        switch (idnpc) {
            case -90: {
                menu = new String[]{"Cống nạp", "Nhận nhiệm vụ tinh tú", "Hủy nhiệm vụ tinh tú", "Trả nhiệm vụ tinh tú",
                    "Xem nhiệm vụ tinh tú"};
                break;
            }
            case -67: {
                menu = new String[]{"Trả mị nương", "Giáp sơn tinh", "Giáp thủy tinh", "Giáp sơn tinh đặc biệt",
                    "Giáp thủy tinh đặc biệt", "Đổi quà may mắn"};
                break;
            }
            case -53: {
                menu = new String[]{"Đăng ký chiến trường", "Vào chiến trường"};
                break;
            }
            case -81: {
                menu = new String[]{"Đăng ký lôi đài", "Vào lôi đài", "Xem lôi đài", "Thông tin", "Xem điểm lôi đài", "Huong dan", "Tháo ngọc khảm", "Vào map boss", "Top Dame Boss TG", "Nhận quà top dame boss"};
                break;
            }
            case -20:
            case -3: { // Lisa
                menu = new String[]{"Mua bán", "Mở ly", "Thuế", "Tiền cống nạp", "Nhận quà chiến trường",
                    "Nhận quà chiếm thành", "Tặng ngọc", "Hướng dẫn tặng ngọc", "Nhận nguyên liệu"};
                break;
            }

            case -77: {
            } // Alisama
            case -5: { // Hammer
                if (conn.p.dropnlmd == 1) {
                    menu = new String[]{"Chiến Binh", "Sát Thủ", "Pháp Sư", "Xạ Thủ", "Chỉ Số Sát Thương", "Tắt NLMD", "Đồ tinh tú"};
                } else {
                    menu = new String[]{"Chiến Binh", "Sát Thủ", "Pháp Sư", "Xạ Thủ", "Chỉ Số Sát Thương", "Mở NLMD", "Đồ tinh tú"};
                }
                break;
            }
            case -4: {// Doubar
                menu = new String[]{"Chiến Binh", "Sát Thủ", "Pháp Sư", "Xạ Thủ", "Thông Tin Boss", "Shop trang bị coin"};
                break;
            }
            case -33: { // da dich chuyen
                menu = new String[]{"Thành Phố Cảng", "Thành Phố Kho Báu", "Khu Mua Bán", "Sa Mạc", "Vực Lún",
                    "Nghĩa Địa Cát", "Suối Ma", "Hầm Mộ Tầng 1", "Hầm Mộ Tầng 3", "Rừng Cao Nguyên", "Vách Đá Cheo Leo",
                    "Lối Lên Thượng Giới", "Đường Xuống Lòng Đất", "Cổng Vào Hạ Giới", "Khu Vườn"};
                break;
            }
            case -55: { // da dich chuyen
                menu = new String[]{"Thành Phố Cảng", "Khu Mua Bán", "Mê Cung", "Mê Cung Tầng 3", "Thị Trấn Mùa Đông",
                    "Thung lũng băng giá", "Chân núi tuyết", "Đèo băng giá", "Vực thẳm sương mù", "Trạm núi tuyết",
                    "Thành Phố Kho Báu"};
                break;
            }
            case -10: { // da dich chuyen
                menu = new String[]{"Làng Sói Trắng", "Thành Phố Kho Báu", "Khu Mua Bán", "Hang Lửa", "Rừng Ảo Giác",
                    "Thung Lũng Kỳ Bí", "Hồ Kí Ức", "Bờ Biển", "Vực Đá", "Rặng Đá Ngầm", "Đầm Lầy", "Đền Cổ", "Hang Dơi"};
                break;
            }
            case -8: {
                if (conn.p.type_exp == 1) {
                    menu = new String[]{"Cửa Hàng Tóc", "Điểm danh hàng ngày", "Đổi coin sang ngọc", "Đổi coin sang vàng",
                        "Đăng ký treo chống pk", "Tgian còn lại", "Tắt nhận exp", "Đổi phái", "Cộng tiềm năng", "Xem Điểm Tiềm Năng"};
                } else {
                    menu = new String[]{"Cửa Hàng Tóc", "Điểm danh hàng ngày", "Đổi coin sang ngọc", "Đổi coin sang vàng",
                        "Đăng ký treo chống pk", "Tgian còn lại", "Bật nhận exp", "Đổi phái", "Cộng tiềm năng", "Xem Điểm Tiềm Năng"};
                }
                break;
            }
            case -36: {
                menu = new String[]{"Cường Hóa Trang Bị", "Shop Nguyên Liệu", "Chuyển hóa", "Hợp nguyên liệu mề đay",
                    "Mề đay chiến binh", "Mề đay pháp sư", "Mề đay sát thủ", "Mề đay xạ thủ", "Nâng cấp mề đay",
                    "Đổi dòng sát thương", "Đổi dòng % sát thương", "Hợp ngọc", "Khảm ngọc", "Đục lỗ"};
                break;
            }
            case -44: {
                menu = new String[]{"Nhận GiftCode", "Tháo cánh", "Tháo cải trang", "Tháo mề đay", "Tháo mặt nạ",
                    "Tháo cánh thời trang", "Tháo áo choàng", "Tháo tóc thời trang", "Tháo vũ khí thời trang",
                    "Tháo tai nghe thời trang", "Tiệc cưới"};
                break;
            }
            case -32: {
                menu = new String[]{"BXH Level", "BXH Chuyển Sinh", "BXH Hiếu Chiến", "BXH Chiến Trường", "BXH Bang Hội"};
                break;
            }
            case -21: { // blackeye
                menu = new String[]{"Chiến Binh", "Sát Thủ", "Pháp Sư", "Xạ Thủ"};
                break;
            }
            case -52: { // Mr Paul
                menu = new String[]{"Thú Cưỡi"};
                break;
            }
            case -7: {
                if (conn.user.contains("knightauto_hsod_")) {
                    menu = new String[]{"Rương giữ đồ", "Mở thêm ô hành trang", "Đăng ký tài khoản", "Chuyển Sinh", "Số Lần Chuyển Sinh"};
                } else {
                    menu = new String[]{"Rương giữ đồ", "Mở thêm ô hành trang", "Mật khẩu rương", "Chuyển Sinh", "Số Lần Chuyển Sinh", "NVHN"};
                }
                break;
            }
            case -34: { // cuop bien
                menu = new String[]{"Vòng xoay Vàng", "Vòng xoay ngọc", "Lịch sử", "Tài Xỉu"};
                break;
            }
            case -22: { // alisama
                menu = new String[]{"Chiến Binh", "Sát Thủ", "Pháp Sư", "Xạ Thủ", "Chế Đồ Tự Chọn"};
                break;
            }
            case -2: { // zoro
                if (conn.p.myclan != null) {
                    if (conn.p.myclan.mems.get(0).name.equals(conn.p.name)) {
                        menu = new String[]{"Quản lý bang", "Shop Icon", "Shop Bang"};
                    } else {
                        menu = new String[]{"Kho Bang", "Góp Vàng", "Góp Ngọc", "Rời bang"};
                    }
                } else {
                    menu = new String[]{"Đăng ký bang", "Thông tin"};
                }
                break;
            }
            case -85: { // mr edgar
                menu = new String[]{"Báo Thù", "Thông tin"};
                break;
            }
            case -42: { // pet
                menu = new String[]{"Chuồng thú", "Shop thức ăn", "Shop trứng", "Tháo pet"};
                break;
            }
            case -37: {
                menu = new String[]{"Vào Ngã Tư Tử Thần", "Giới thiệu", "BXH phó bản", "Đăng ký chiếm thành", "Hướng dẫn",
                    "Xem Điểm Hiện Tại", "Nhận phần thưởng", "Trở thành hiệp sĩ", "Mua Lượt Phó Bản"};
                break;
            }
            case -38:
            case -40: {
                if (conn.p.dungeon != null && conn.p.dungeon.getWave() == 20) {
                    menu = new String[]{"Tiếp tục chinh phục", "Bỏ cuộc", "Hướng dẫn"};
                } else {
                    menu = new String[]{"Coming soon", "hiepsirom.com"};
                }
                break;
            }
            case -41: {
                menu = new String[]{"Tạo cánh", "Nâng cấp cánh", "Kích hoạt cánh", "Tách cánh"};
                break;
            }
            case -49: {
                menu = new String[]{"Phỉ nhổ", "Tu Tiên", "Luyện Thể", "Đả Thông Kinh Mạch", "Thông Tin Cá Nhân", "Nhận Quà Mốc Nạp" };
                break;
            }
            case -93: { // cây lồng đèn
                menu = new String[]{"."};
                break;
            }
            case -82: {
                menu = new String[]{"Về Làng"};
                break;
            }
//            case -81: {
//                menu = new String[]{"Điểm Pk"};
//                break;
//            }
            case -69: {
                if (Manager.gI().event == 1) {
                    menu = new String[]{"Đổi hộp đồ chơi", "Hướng dẫn", "Đăng ký nấu kẹo", "Bỏ nguyên liệu vào nồi kẹo",
                        "Lấy kẹo đã nấu", "Đổi túi kẹo", "Đổi trứng phượng hoàng băng", "Đổi trứng yêu tinh",
                        "Đổi giày băng giá", "Đổi mặt nạ băng giá", "Đổi kẹo gậy", "Đổi gậy tuyết", "Đổi xe trượt tuyết",
                        "Đổi trứng khỉ nâu"};
                } else {
                    menu = new String[]{"Coming soon", "hiepsirom.com"};
                }
                break;
            }
            case -62: {
                if (Manager.gI().event == 1) {
                    menu = new String[]{"Tăng tốc nấu", "Hướng dẫn", "Thông tin", "Top Nguyên Liệu", "Top 8 Tháng 3"};
                } else {
                    menu = new String[]{"Coming soon", "hiepsirom.com"};
                }
                break;
            }
            case -66: {
                if (Manager.gI().event == 1) {
                    menu = new String[]{"Hoa tuyết", "Ngôi sao", "Quả châu", "Thiệp", "Top trang trí cây thông"};
                } else {
                    menu = new String[]{"Coming soon", "hiepsirom.com"};
                }
                break;
            }
            case -57: {
                menu = new String[]{"Mua bán"};
                break;
            }
            case -58: {
                menu = new String[]{"Mua lạc đà", "Bán đá quý", "Đồ thương nhân"};
                break;
            }
            case -59: {
                menu = new String[]{"Mua lạc đà", "Bán đá quý", "Đồ cướp"};
                break;
            }
            default: {
                menu = new String[]{"Coming soon", "hiepsirom.com"};
                break;
            }
        }
        //
        send_menu_select(conn, idnpc, menu);
    }

    public static void processmenu(Session conn, Message m) throws IOException {
        short idnpc = m.reader().readShort();
        @SuppressWarnings("unused")
        byte idmenu = m.reader().readByte();
        byte index = m.reader().readByte();
        // System.out.println(idnpc);
        // System.out.println(idmenu);
        // System.out.println(index);
        if (idnpc == -56) {
            send_menu_select(conn, 119, new String[]{"Thông tin", "Bảo hộ", "Hồi máu", "Tăng tốc"});
            return;
        }
        boolean npc_in_map = false;
        for (int i = 0; i < conn.p.list_npc.size(); i++) {
            if (conn.p.list_npc.get(i).id == idnpc && Math.abs(conn.p.list_npc.get(i).x - conn.p.x) < 10000
                    && Math.abs(conn.p.list_npc.get(i).y - conn.p.y) < 10000) {
                npc_in_map = true;
                break;
            }
        }
        if (!npc_in_map && idnpc != 119 && idnpc != 126) {
//             return;
        }

        switch (idnpc) {
            case -90: {
                if (index == 0) { // cong nap
                    if (CongNap.CAT[CongNap.NV_CONG_NAP] == 4 && CongNap.ID[CongNap.NV_CONG_NAP] == -1) {
                        Service.send_box_input_yesno(conn, 109,
                                "Vật phẩm cống nạp hôm nay là 1tr vàng bạn có muốn cống nạp?");
                    } else if (CongNap.CAT[CongNap.NV_CONG_NAP] == 4 && CongNap.ID[CongNap.NV_CONG_NAP] == -2) {
                        Service.send_box_input_yesno(conn, 109, "Vật phẩm cống nạp hôm nay là 5 ngọc bạn có muốn cống nạp?");
                    } else {
                        if (CongNap.CAT[CongNap.NV_CONG_NAP] == 4) {
                            Service.send_box_input_yesno(conn, 109,
                                    "Vật phẩm cống nạp hôm nay là" + CongNap.NUM[CongNap.NV_CONG_NAP] + " "
                                    + ItemTemplate4.item.get(CongNap.ID[CongNap.NV_CONG_NAP]).getName()
                                    + " bạn có muốn cống nạp?");
                        } else {
                            Service.send_box_input_yesno(conn, 107,
                                    "Vật phẩm cống nạp hôm nay là" + CongNap.NUM[CongNap.NV_CONG_NAP] + " "
                                    + ItemTemplate7.item.get(CongNap.ID[CongNap.NV_CONG_NAP]).getName()
                                    + " bạn có muốn cống nạp?");
                        }
                    }
                } else if (index == 1) {
                    NV_TinhTu.get_quest(conn.p);
                } else if (index == 2) {
                    NV_TinhTu.remove_quest(conn.p);
                } else if (index == 3) {
                    NV_TinhTu.finish_quest(conn.p);
                } else if (index == 4) {
                    if (conn.p.nv_tinh_tu[0] == -1) {
                        Service.send_notice_box(conn, "Chưa nhận nhiệm vụ");
                    } else {
                        NV_TinhTu.info_quest(conn.p);
                    }
                }
                break;
            }
            case 993: {
                if (conn.p.id_tach_vp != -1 && conn.p.id_tach_vp < conn.p.item.bag3.length) {
                    if (conn.p.item.bag3[conn.p.id_tach_vp] != null) {
                        if (!(conn.p.item.bag3[conn.p.id_tach_vp].level >= 50
                                && conn.p.item.bag3[conn.p.id_tach_vp].color >= 2)) {
                            Service.send_notice_box(conn,
                                    "Trang bị được tách phải có Lv từ 50 trở lên và là trang bị màu vàng - tím - cam trở lên");
                            return;
                        }
                        int num = 0;
                        if (conn.p.item.bag3[conn.p.id_tach_vp].tier < 8) {
                            num = 1;
                        } else if (conn.p.item.bag3[conn.p.id_tach_vp].tier < 12) {
                            num = 2;
                        } else {
                            num = 3;
                        }
                        if (30 > Util.random(120)) { // xit
                            num = 0;
                        }
                        if (num > 0) {
                            short[] id_RD = new short[num];
                            short[] num_RD = new short[num];
                            for (int i = 0; i < num_RD.length; i++) {
                                int num_material = TinhTu_Material.NHOM_1[Util.random(TinhTu_Material.NHOM_1.length)];
                                if (conn.p.item.bag3[conn.p.id_tach_vp].color == 2) {
                                    num_material = TinhTu_Material.NHOM_1[Util.random(5)];
                                } else if (conn.p.item.bag3[conn.p.id_tach_vp].color == 3) {
                                    num_material = TinhTu_Material.NHOM_1[Util.random(7)];
                                }
                                id_RD[i] = (short) num_material;
                                num_RD[i] = (short) Util.random(1, 3);
                            }
                            //
                            Message m12 = new Message(78);
                            m12.writer().writeUTF("Tách trang bị");
                            m12.writer().writeByte(num); // size
                            for (int i = 0; i < num_RD.length; i++) {
                                m12.writer().writeUTF(""); // name
                                m12.writer().writeShort(ItemTemplate7.item.get(id_RD[i]).getIcon()); // icon
                                m12.writer().writeInt(num_RD[i]); // quantity
                                m12.writer().writeByte(7); // type in bag
                                m12.writer().writeByte(0); // tier
                                m12.writer().writeByte(0); // color
                                //
                                Item47 it = new Item47();
                                it.id = id_RD[i];
                                it.quantity = num_RD[i];
                                it.category = 7;
                                conn.p.item.add_item_bag47(7, it);
                            }
                            m12.writer().writeUTF("");
                            m12.writer().writeByte(1);
                            m12.writer().writeByte(0);
                            conn.addmsg(m12);
                            m12.cleanup();
                            conn.p.item.bag3[conn.p.id_tach_vp] = null;
                            conn.p.item.char_inventory(4);
                            conn.p.item.char_inventory(7);
                            conn.p.item.char_inventory(3);
                            //
                        } else {
                            Service.send_notice_box(conn, "Không nhận được gì");
                        }
                    }
                }
                break;
            }
            case 994: {
                Menu_DoTinhTu(conn.p, index);
                break;
            }
            case 995: {
                if (index < 4) {
                    conn.p.id_medal_is_created = index;
                    send_menu_select(conn, 994, new String[]{"Nón tinh tú", "Áo tinh tú", "Quần tinh tú", "Giày tinh tú",
                        "Găng tay tinh tú", "Nhẫn tinh tú", "Vũ khí tinh tú", "Dây chuyền tinh tú"});
                } else if (index == 4) { // nang cap do tinh tu
                    Message m2 = new Message(23);
                    m2.writer().writeUTF("Nâng cấp đồ tinh tú");
                    m2.writer().writeByte(20);
                    m2.writer().writeShort(0);
                    conn.addmsg(m2);
                    m2.cleanup();
                }
                break;
            }
            case 996: {
                if (conn.p.get_eff(-126) != null) {
                    return;
                }
                if (conn.p.item.wear[11] == null || (conn.p.item.wear[11].id != 4790 && conn.p.item.wear[11].id != 4791)) {
                    return;
                }
                if (conn.p.minuong == null) {
                    for (int i = 0; i < BossEvent.MiNuong.size(); i++) {
                        if (BossEvent.MiNuong.get(i).map.equals(conn.p.map)) {
                            synchronized (BossEvent.MiNuong.get(i)) {
                                if (BossEvent.MiNuong.get(i).owner.isBlank()) {
                                    BossEvent.MiNuong.get(i).owner = conn.p.name;
                                    //
                                    Mob_in_map temp = BossEvent.MiNuong.get(i).mob;
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
                                    m22.writer().writeUTF(conn.p.name);
                                    m22.writer().writeLong(-11111);
                                    m22.writer().writeByte(temp.color_name); // color name 1: blue, 2: yellow
                                    MapService.send_msg_player_inside(conn.p.map, conn.p, m22, true);
                                    m22.cleanup();
                                    conn.p.minuong = BossEvent.MiNuong.get(i);
                                    break;
                                }
                            }
                            break;
                        }
                    }
                } else if (conn.p.minuong != null && index == 0) {
                    synchronized (conn.p.minuong) {
                        if (!conn.p.minuong.owner.isBlank()) {
                            Mob_in_map temp = conn.p.minuong.mob;
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
                            MapService.send_msg_player_inside(conn.p.map, conn.p, m22, true);
                            m22.cleanup();
                            conn.p.minuong.owner = "";
                            conn.p.minuong = null;
                        }
                    }
                }
                if (conn.p.minuong != null && conn.p.minuong.power < 1 && index == 1) {
                    if (conn.p.get_ngoc() < 10) {
                        Service.send_notice_box(conn, "Ta cần 10 ngọc");
                        return;
                    }
                    conn.p.update_ngoc(-10);
                    conn.p.item.char_inventory(5);
                    conn.p.minuong.power += 1000;
                    Service.send_notice_box(conn, "Ta đi tiếp thôi");
                }
                break;
            }
            case -67: {
                Menu_VuaHung_Event_2(conn, index);
                break;
            }
            case 998: {
                if (index == 0) {
                    if (ChienTruong.gI().getStatus() == 1) {
                        if (conn.p.get_vang() < 1_000_000) {
                            Service.send_notice_box(conn, "Không đủ 1.000.000 vàng");
                            return;
                        }
                        conn.p.update_vang(-1_000_000);
                        conn.p.item.char_inventory(5);
                        ChienTruong.gI().register(conn.p, 0);
                    } else {
                        Service.send_notice_box(conn, "Không trong thời gian diễn ra");
                    }
                } else if (index == 1) {
                    if (ChienTruong.gI().getStatus() == 1) {
                        if (conn.p.get_ngoc() < 500) {
                            Service.send_notice_box(conn, "Không đủ 500 ngọc");
                            return;
                        }
                        conn.p.update_ngoc(-500);
                        conn.p.item.char_inventory(5);
                        ChienTruong.gI().register(conn.p, 1);
                    } else {
                        Service.send_notice_box(conn, "Không trong thời gian diễn ra");
                    }
                }
                break;
            }
            case -53: {
                Menu_Mr_Ballard(conn, index);
                break;
            }
            case 557: {
                QuaTang.get_qua(conn.p, index);
                break;
            }
            case 556: {
                DailyQuest.get_quest(conn.p, index);
                break;
            }
            case 555: {
                Menu_DailyQuest(conn, index);
                break;
            }
            case 1000: {
                Menu_LuyenThe(conn, index);
                break;
            }
            case 1001: {
                Menu_KinhMach(conn, index);
                break;
            }
            case 1002: {
                Menu_TuTien(conn, index);
                break;
            }
            case 129: {
                Menu_MocNap(conn, index);
                break;
            }
            case 130: {
                Menu_Thongtincanhan(conn, index);
                break;
            }    
            case 109: {
                Menu_ChuyenPhai(conn, index);
                break;
            }
            case 110: {
                if (conn.p.it_change_op != null) {
                    Menu_ChangeOptionItem(conn, index);
                }
                break;
            }
            case 112: {
                conn.p.it_change_op_index = index;
                send_menu_select(conn, 110, new String[]{"Đổi loại sát thương", "Buff thêm điểm"});
                break;
            }
            case 111: {
                Item3 it_change = null;
                for (int i = 0; i < conn.p.item.wear.length; i++) {
                    if (conn.p.item.wear[i] != null) {
                        if (index == 0) {
                            it_change = conn.p.item.wear[i];
                            break;
                        }
                        index--;
                    }
                }
                if (it_change != null) {
                    //
                    Item3 it_new = new Item3();
                    it_new.id = it_change.id;
                    it_new.name = it_change.name;
                    it_new.clazz = it_change.clazz;
                    it_new.type = it_change.type;
                    it_new.level = it_change.level;
                    it_new.icon = it_change.icon;
                    it_new.op = new ArrayList<>();
                    for (int i = 0; i < it_change.op.size(); i++) {
                        it_new.op.add(new Option(it_change.op.get(i).id, it_change.op.get(i).getParam(0)));
                    }
                    it_new.color = it_change.color;
                    it_new.part = it_change.part;
                    it_new.tier = it_change.tier;
                    it_new.islock = it_change.islock;
                    it_new.time_use = it_change.time_use;
                    conn.p.it_change_op = it_new;
                    //
                    List<String> list1 = new ArrayList<>();
                    for (int i = 0; i < it_change.op.size(); i++) {
                        OptionItem optionItem = OptionItem.get(it_change.op.get(i).id);
                        list1.add(optionItem.getName() + " "
                                + ((optionItem.getIspercent() == 1) ? (it_change.op.get(i).getParam(it_change.tier) / 100)
                                : it_change.op.get(i).getParam(it_change.tier)));
                    }
                    String[] list = new String[list1.size()];
                    for (int i = 0; i < list1.size(); i++) {
                        list[i] = list1.get(i);
                    }
                    send_menu_select(conn, 112, list);
                }
                break;
            }
            case 113: {
                Menu_TaiXiu(conn, index);
                break;
            }
            case 114: {
                Menu_Wedding(conn, index);
                break;
            }
            case 115: {
                Menu_ThayDongMeDay_percent(conn, index);
                break;
            }
            case 116: {
                Menu_ThayDongMeDay(conn, index);
                break;
            }
            case 117: {
                Menu_ThaoKhamNgoc(conn, index);
                break;
            }
            case -81: {
                Menu_Mrs_Oda(conn, index);
                break;
            }
            case -82: {
                if (conn.p.map.ld != null) {
                    Menu_Mrs_Oda_trong_LoiDai(conn, index);
                } else {
                    Menu_MissAnwen(conn, index);
                }

                break;
            }
            case 118: {
                Menu_View_LoiDai(conn, index);
                break;
            }
            case 119: {
                Menu_Pet_di_buon(conn, index);
                break;
            }
            case 127: {
                Service.send_box_input_text(conn, (24 + index), "Nhập điểm : ", new String[]{"Nhập điểm"});
                break;
            }
            case -57: {
                Menu_Mr_Dylan(conn, index);
                break;
            }
            case -58: {
                Menu_Graham(conn, index);
                break;
            }
            case -59: {
                Menu_Mr_Frank(conn, index);
                break;
            }
            case 121: {
                Menu_TachCanh(conn, index);
                break;
            }
            case -3: { // Lisa
                Menu_Lisa(conn, index);
                break;
            }
            case -20: { // Lisa
                Menu_Lisa(conn, index);
                break;
            }
            case -4: {
                Menu_Doubar(conn, index);
                break;
            }
            case -5: {
                Menu_Hammer(conn, index);
                break;
            }

            case -33: {
                if (conn.p.minuong != null) {
                    conn.p.minuong.owner = "";
                    conn.p.minuong = null;
                }
                Menu_DaDichChuyen33(conn, index);
                break;
            }
            case -55: {
                if (conn.p.minuong != null) {
                    conn.p.minuong.owner = "";
                    conn.p.minuong = null;
                }
                Menu_DaDichChuyen55(conn, index);
                break;
            }
            case -10: {
                if (conn.p.minuong != null) {
                    conn.p.minuong.owner = "";
                    conn.p.minuong = null;
                }
                Menu_DaDichChuyen10(conn, index);
                break;
            }
            case -22: {
                Menu_Alisama(conn, index);
                break;
            }
            case -52: {
                Menu_Mr_paul(conn, index);
                break;
            }
            case -8: {
                Menu_Zulu(conn, index);
                break;
            }
            case 126: {
                Menu_Admin(conn, index);
                break;
            }
            case -36: {
                Menu_PhapSu(conn, index);
                break;
            }
            case -44: {
                Menu_Miss_Anna(conn, index);
                break;
            }
            case -32: {
                Menu_BXH(conn, index);
                break;
            }
            case -21: {
                Menu_Black_Eye(conn, index);
                break;
            }

            case -7: {
                Menu_Aman(conn, index);
                break;
            }
            case -34: {
                Menu_CuopBien(conn, index);
                break;
            }
            case 125: { // vxmm
                Menu_VXMM(conn, index);
                break;
            }
            case -2: { // vxmm
                Menu_Zoro(conn, index);
                break;
            }
            case -85: { //
                Menu_Mr_Edgar(conn, index);
                break;
            }
            case 124: {
                Service.revenge(conn, index);
                break;
            }
            case 123: {
                Menu_Dungeon_Mode_Selection(conn, index);
                break;
            }
            case 122: {
                Menu_Clan_Manager(conn, index);
                break;
            }
            case -42: {
                Menu_Pet_Manager(conn, index);
                break;
            }
            case -37: {
                Menu_PhoChiHuy(conn, index);
                break;
            }
            case -38:
            case -40: {
                Menu_LinhCanh(conn, index);
                break;
            }
            case -41: {
                Menu_TienCanh(conn, index);
                break;
            }
            case -49: {
                Menu_top(conn, index);
                break;
            }
//            case -81: {
//                Menu_diempk(conn, index);
//                break;
//            }
            case -69: {
                if (Manager.gI().event == 1) {
                    Menu_Event(conn, index);
                }
                break;
            }
            case -62: {
                if (Manager.gI().event == 1) {
                    Menu_NauKeo(conn, index);
                }
                break;
            }
            case -66: {
                if (Manager.gI().event == 1) {
                    Menu_CayThong(conn, index);
                }
                break;
            }
            case 120: {
                break;
            }
            
            default: {
                Service.send_notice_box(conn, "Đã xảy ra lỗi");
                break;
            }
        }
    }

    private static void Menu_View_LoiDai(Session conn, byte index) throws IOException {
        if (LoiDai.state != 1) {
            Service.send_notice_box(conn, "Không trong thời gian diễn ra lôi đài");
            return;
        }
        Map map = LoiDai.get_map_view_loidai(index);
        if (map != null) {
            if (map.ld.p1.name.equals(conn.p.name) || map.ld.p2.name.equals(conn.p.name)) {
                Service.send_notice_box(conn, "Đã đăng ký thì không được xuống ngồi làm khán giả");
                return;
            }
            MapService.change_flag(map, conn.p, -1);
            conn.p.typepk = -1;
            //
            conn.p.is_changemap = false;
            //
            MapService.leave(conn.p.map, conn.p);
            conn.p.map = map;
            conn.p.x = 360;
            conn.p.y = 396;
            MapService.enter(conn.p.map, conn.p);
        } else {
            Service.send_notice_box(conn, "Đã xảy ra lỗi");
        }
    }

    private static void Menu_Mrs_Oda_trong_LoiDai(Session conn, byte index) throws IOException {
        switch (index) {
            case 0: {
                if (conn.p.map.ld.p1.id == conn.p.id) {
                    Player p0 = new Player(new Session(new Socket()), conn.p.id);
                    p0.setup();
                    p0.set_in4();
                    p0.map = conn.p.map;
                    p0.x = conn.p.x;
                    p0.y = conn.p.y;
                    conn.p.map.ld.p1 = p0;
                    synchronized (conn.p.map) {
                        conn.p.map.players.add(p0);
                    }
                } else if (conn.p.map.ld.p2.id == conn.p.id) {
                    Player p0 = new Player(new Session(new Socket()), conn.p.id);
                    p0.setup();
                    p0.set_in4();
                    p0.map = conn.p.map;
                    p0.x = conn.p.x;
                    p0.y = conn.p.y;
                    conn.p.map.ld.p2 = p0;
                    synchronized (conn.p.map) {
                        conn.p.map.players.add(p0);
                    }
                }
                Vgo vgo = new Vgo();
                vgo.id_map_go = 1;
                vgo.x_new = 432;
                vgo.y_new = 354;
                conn.p.change_map(conn.p, vgo);
                break;
            }
            default: {
                Service.send_notice_box(conn, "Chưa có chức năng");
                break;
            }
        }
    }

    private static void Menu_Mrs_Oda(Session conn, byte index) throws IOException {
        if (conn.p.point_active.length < 3) {
            int a0 = conn.p.point_active[0];
            int a1 = conn.p.point_active[1];
            conn.p.point_active = new int[]{a0, a1, 0};
        }
        switch (index) {
            case 0: {
                if (conn.p.get_ngoc() < 5) {
                    Service.send_notice_box(conn, "Không đủ 5 ngọc");
                    return;
                }
                if (LoiDai.state != 0) {
                    Service.send_notice_box(conn, "Không trong thời gian đăng ký");
                    return;
                }
                if (LoiDai.check_register(conn.p)) {
                    Service.send_notice_box(conn, "Đã có tên trong danh sách đăng ký");
                    return;
                }
                if (LoiDai.size_register() >= 16) {
                    Service.send_notice_box(conn, "Số lượng đăng ký đã đầy, hãy quay lại vào lần tới");
                    return;
                }
                if (conn.p.level < 30) {
                    Service.send_notice_box(conn, "Không đủ level");
                    return;
                }
                conn.p.update_ngoc(-5);
                conn.p.item.char_inventory(5);
                Service.send_notice_box(conn, "Đợi trong giây lát....");
                LoiDai.add_name_register(conn.p);
                Service.send_notice_box(conn, "Đăng ký thành công!");
                Player p = conn.p;
                if (p.quest_daily[0] == 1 && p.quest_daily[3] < p.quest_daily[4]) {
                    p.quest_daily[3]++;
                }
                break;
            }
            case 1: {
                if (LoiDai.state != 1) {
                    Service.send_notice_box(conn, "Không trong thời gian diễn ra lôi đài");
                    return;
                }
                // if (!LoiDai.check_register(conn.p)) {
                // Service.send_notice_box(conn, "Không có tên trong danh sách đăng ký");
                // return;
                // }
                Map map = LoiDai.get_map_atk_loidai(conn.p);
                if (map != null) {
                    conn.p.is_changemap = false;
                    //
                    MapService.leave(conn.p.map, conn.p);
                    conn.p.map = map;
                    if (conn.p.id == map.ld.p1.id) {
                        conn.p.x = map.ld.p1.x;
                        conn.p.y = map.ld.p1.y;
                        conn.p.hp = map.ld.p1.hp;
                        conn.p.isdie = map.ld.p1.isdie;
                    } else {
                        conn.p.x = map.ld.p2.x;
                        conn.p.y = map.ld.p2.y;
                        conn.p.hp = map.ld.p2.hp;
                        conn.p.isdie = map.ld.p2.isdie;
                    }
                    conn.p.typepk = 0;
                    MapService.enter(conn.p.map, conn.p);
                    //
                } else {
                    Service.send_notice_box(conn, "Không có tên trong danh sách đăng ký");
                }
                break;
            }
            case 2: {
                send_menu_select(conn, 118, LoiDai.get_list_loidai_is_atk());
                // Vgo vgo = new Vgo();
                // vgo.id_map_go = 102;
                // vgo.x_new = 360;
                // vgo.y_new = 396;
                // conn.p.change_map(conn.p, vgo);
                break;
            }
            case 3: {
                String notice = "Hiện tại : \n";
                switch (LoiDai.state) {
                    case 0: {
                        notice += "thời gian đăng ký còn " + (LoiDai.time_state) + "s nữa\n";
                        notice += "Danh sách đăng ký : \n" + LoiDai.get_list_register();
                        break;
                    }
                    case 1: {
                        notice += "thời gian diễn ra lôi đài được " + (0 - LoiDai.time_state) + "s";
                        break;
                    }
                    case 2: {
                        notice += LoiDai.get_notice_break_time();
                        break;
                    }
                }
                Service.send_notice_box(conn, notice);
                break;
            }
            case 4: {
                Service.send_notice_box(conn, "Điểm lôi đài hiện tại: " + conn.p.point_active[2]);
                break;
            }
            case 6: {
                conn.p.list_thao_kham_ngoc.clear();
                for (int i = 0; i < conn.p.item.wear.length; i++) {
                    Item3 it = conn.p.item.wear[i];
                    if (it != null) {
                        short[] b = conn.p.item.check_kham_ngoc(it);
                        boolean check = false;
                        if ((b[0] != -2 && b[0] != -1) || (b[1] != -2 && b[1] != -1) || (b[2] != -2 && b[2] != -1)) {
                            check = true;
                        }
                        if (check) {
                            conn.p.list_thao_kham_ngoc.add(it);
                        }
                    }
                }
                String[] list_show = new String[]{"Trống"};
                if (conn.p.list_thao_kham_ngoc.size() > 0) {
                    list_show = new String[conn.p.list_thao_kham_ngoc.size()];
                    for (int i = 0; i < list_show.length; i++) {
                        list_show[i] = conn.p.list_thao_kham_ngoc.get(i).name;
                    }
                }
                MenuController.send_menu_select(conn, 117, list_show);
                break;
            }
            case 7: {

                if (Manager.gI().bossTG.p.isdie) {
                    Service.send_notice_box(conn, "Chưa thể vào map thời gian này!");
                    return;
                }

                boolean enter = true;
                long t = 0;
                if (Manager.gI().bossTG.time_enter_map.containsKey(conn.p.name)) {
                    t = Manager.gI().bossTG.time_enter_map.get(conn.p.name);
                    if (t > System.currentTimeMillis()) {
                        enter = false;
                    }
                } else {
                    Manager.gI().bossTG.time_enter_map.put(conn.p.name, 0L);
                }
                if (enter) {
                    MapService.leave(conn.p.map, conn.p);
                    conn.p.map = Manager.gI().bossTG.map;
                    conn.p.x = 504;
                    conn.p.y = 288;
                    MapService.enter(conn.p.map, conn.p);
                } else {
                    Service.send_notice_box(conn, "Hãy chờ " + ((t - System.currentTimeMillis()) / 1000) + " s để vào lại");
                }
                break;
            }
            case 8: {
                if (Manager.gI().bossTG.top_dame.size() > 1) {
                    Collections.sort(Manager.gI().bossTG.top_dame, new Comparator<Top_Dame>() {
                        @Override
                        public int compare(Top_Dame o1, Top_Dame o2) {
                            return (o1.dame > o2.dame) ? -1 : 1;
                        }
                    });
                }
                String[] list = new String[]{"Trống"};
                if (Manager.gI().bossTG.top_dame.size() > 0) {
                    int size = Manager.gI().bossTG.top_dame.size();
                    list = new String[(size > 10) ? 10 : size];
                    for (int i = 0; i < list.length; i++) {
                        list[i] = "Top " + (i + 1) + " : " + Manager.gI().bossTG.top_dame.get(i).name + " "
                                + String.format("%.02f", ((float) Manager.gI().bossTG.top_dame.get(i).dame / 1_000_000f)) + "m";
                    }
                }
                send_menu_select(conn, 120, list);
                break;
            }
            case 9: {
                if (!Manager.gI().bossTG.p.isdie) {
                    Service.send_notice_box(conn, "Chưa thể nhận vào thời gian này!");
                    return;
                }
                Top_Dame td = null;
                for (int i = 0; i < Manager.gI().bossTG.top_dame.size(); i++) {
                    if (i >= 10) {
                        break;
                    }
                    Top_Dame td2 = Manager.gI().bossTG.top_dame.get(i);
                    if (td2.name.equals(conn.p.name)) {
                        if (!td2.receiv) {
                            td = td2;
                            break;
                        } else {
                            Service.send_notice_box(conn, "Đã nhận rồi, khôn vừa thôi!");
                            return;
                        }
                    }
                }
                if (td != null) {
                    if (conn.p.item.get_bag_able() < 3) {
                        Service.send_notice_box(conn, "Hành trang không đủ chỗ!");
                        return;
                    }
                    switch (Manager.gI().bossTG.top_dame.indexOf(td)) {

                        case 0: {
                            ItemTemplate3 item = ItemTemplate3.item.get(3268);
                            Item3 itbag = new Item3();
                            itbag.id = item.getId();
                            itbag.name = item.getName();
                            itbag.clazz = item.getClazz();
                            itbag.type = item.getType();
                            itbag.level = 40;
                            itbag.icon = item.getIcon();
                            itbag.op = new ArrayList<>();
                            itbag.op.addAll(item.getOp());
                            itbag.color = item.getColor();
                            itbag.part = item.getPart();
                            itbag.tier = 0;
                            itbag.islock = false;
                            itbag.time_use = 0;
                            conn.p.item.add_item_bag3(itbag);
                            conn.p.item.char_inventory(3);

                            Item47 it = new Item47();
                            it.category = 7;
                            it.id = 472;
                            it.quantity = (short) (10);
                            conn.p.item.add_item_bag47(7, it);
                            Player p = conn.p;
                            if (p.quest_daily[0] == 7 && p.quest_daily[3] < p.quest_daily[4]) {
                                p.quest_daily[3]++;
                            }

                            break;
                        }
                        case 1: {
                            conn.p.item.char_inventory(5);
                            Item47 it = new Item47();
                            it.category = 7;
                            it.id = 472;
                            it.quantity = (short) (7);
                            conn.p.item.add_item_bag47(7, it);
                            Player p = conn.p;
                            if (p.quest_daily[0] == 7 && p.quest_daily[3] < p.quest_daily[4]) {
                                p.quest_daily[3]++;
                            }
                            break;
                        }
                        case 2: {
                            conn.p.item.char_inventory(5);

                            Item47 it = new Item47();
                            it.category = 7;
                            it.id = 472;
                            it.quantity = (short) (5);
                            conn.p.item.add_item_bag47(7, it);
//                            conn.p.item.char_inventory(7);
                            Player p = conn.p;
                            if (p.quest_daily[0] == 7 && p.quest_daily[3] < p.quest_daily[4]) {
                                p.quest_daily[3]++;
                            }
                            break;
                        }
                        case 3:
                        case 4: {
                            conn.p.update_vang(200_000);
                            conn.p.update_ngoc(10_000);
                            conn.p.item.char_inventory(5);

                            Item47 it = new Item47();
                            it.category = 7;
                            it.id = 472;
                            it.quantity = (short) (3);
                            conn.p.item.add_item_bag47(7, it);
                            break;
                        }
                        case 5:
                        case 6:
                        case 7:
                        case 8:
                        case 9: {
                            conn.p.update_vang(100_000);
                            conn.p.update_ngoc(5_000);
                            conn.p.item.char_inventory(5);
                            break;
                        }
                    }
                    td.receiv = true;
                } else {
                    Service.send_notice_box(conn, "Không có trong top mà đòi nhận thưởng cái éo gì?");
                }
                break;
            }
            default: {
                Service.send_notice_box(conn, "Chưa có chức năng");
                break;
            }
        }
    }

    private static void Menu_Pet_di_buon(Session conn, byte index) throws IOException {
        switch (index) {
            case 0: {
                String notice = null;
                if (conn.p.pet_di_buon != null && conn.p.pet_di_buon.item.size() > 0) {
                    notice = "%s " + ItemTemplate3.item.get(3590).getName() + "\n";
                    notice += "%s " + ItemTemplate3.item.get(3591).getName() + "\n";
                    notice += "%s " + ItemTemplate3.item.get(3592).getName() + "\n";
                    int n1 = 0, n2 = 0, n3 = 0;
                    for (int i = 0; i < conn.p.pet_di_buon.item.size(); i++) {
                        if (conn.p.pet_di_buon.item.get(i) == 3590) {
                            n1++;
                        } else if (conn.p.pet_di_buon.item.get(i) == 3591) {
                            n2++;
                        } else {
                            n3++;
                        }
                    }
                    notice = String.format(notice, n1, n2, n3);
                } else {
                    notice = "Trống";
                }
                Service.send_notice_box(conn, notice);
                break;
            }
            case 1: {
                break;
            }
            case 2: {
                if (conn.p.get_ngoc() > 5) {
                    conn.p.pet_di_buon.update_hp(conn.p, 100);
                } else {
                    Service.send_notice_box(conn, "Không đủ 5 ngọc");
                }
                break;
            }
            case 3: {
                if (conn.p.get_ngoc() > conn.p.pet_di_buon.ngoc) {
                    conn.p.pet_di_buon.update_speed(conn.p);
                } else {
                    Service.send_notice_box(conn, "Không đủ " + conn.p.pet_di_buon.ngoc + " ngọc");
                }
                break;
            }
            default: {
                Service.send_notice_box(conn, "Chưa có chức năng");
                break;
            }
        }
    }

    private static void Menu_Mr_Frank(Session conn, byte index) throws IOException {
        switch (index) {
            case 0: {
                Service.send_box_UI(conn, 32);
                break;
            }
            case 1: {
                if (conn.p.pet_di_buon != null && Math.abs(conn.p.pet_di_buon.x - conn.p.x) < 75
                        && Math.abs(conn.p.pet_di_buon.y - conn.p.y) < 75 && conn.p.item.wear[11] != null
                        && conn.p.item.wear[11].id == 3593) {
                    //
                    int vang_recei = 0;
                    for (int i = 0; i < conn.p.pet_di_buon.item.size(); i++) {
                        if (conn.p.pet_di_buon.item.get(i) == 3590) {
                            vang_recei += 500_000;
                        } else if (conn.p.pet_di_buon.item.get(i) == 3591) {
                            vang_recei += 750_000;
                        } else if (conn.p.pet_di_buon.item.get(i) == 3592) {
                            vang_recei += 1_000_000;
                        }
                    }
                    if (vang_recei > 0) {
                        conn.p.update_vang(vang_recei);
                        conn.p.item.char_inventory(5);
                        //
                        Message mout = new Message(8);
                        mout.writer().writeShort(conn.p.pet_di_buon.index);
                        for (int i = 0; i < conn.p.map.players.size(); i++) {
                            Player p0 = conn.p.map.players.get(i);
                            if (p0 != null) {
                                p0.conn.addmsg(mout);
                            }
                        }
                        mout.cleanup();
                        //
                        Pet_di_buon_manager.remove(conn.p.pet_di_buon.name);
                        conn.p.pet_di_buon = null;
                        Service.send_notice_box(conn, "Nhận được " + vang_recei + " vàng!");
                        	if (conn.p.nv_tinh_tu[0] == 30 && conn.p.nv_tinh_tu[1] < conn.p.nv_tinh_tu[2]) {
							conn.p.nv_tinh_tu[1]++;
						}
                    } else {
                        Service.send_notice_box(conn, "Chưa cướp được gì cả, thật kém cỏi!");
                    }
                } else {
                    Service.send_notice_box(conn, "Ta không thấy con vật đi buôn của ngươi");
                }
                break;
            }
            case 2: {
                Item3 itbag = new Item3();
                itbag.id = 3593;
                itbag.clazz = ItemTemplate3.item.get(3593).getClazz();
                itbag.type = ItemTemplate3.item.get(3593).getType();
                itbag.level = ItemTemplate3.item.get(3593).getLevel();
                itbag.icon = ItemTemplate3.item.get(3593).getIcon();
                itbag.op = new ArrayList<>();
                itbag.op.addAll(ItemTemplate3.item.get(3593).getOp());
                itbag.color = 5;
                itbag.part = ItemTemplate3.item.get(3593).getPart();
                itbag.tier = 0;
                itbag.islock = true;
                itbag.time_use = 0;
                // thao do
                if (conn.p.item.wear[11] != null && conn.p.item.wear[11].id != 3593 && conn.p.item.wear[11].id != 3599
                        && conn.p.item.wear[11].id != 3596) {
                    Item3 buffer = conn.p.item.wear[11];
                    conn.p.item.wear[11] = null;
                    conn.p.item.add_item_bag3(buffer);
                }
                itbag.name = ItemTemplate3.item.get(3593).getName() + " [Khóa]";
                conn.p.item.wear[11] = itbag;
                conn.p.item.char_inventory(4);
                conn.p.item.char_inventory(7);
                conn.p.item.char_inventory(3);
                conn.p.fashion = Part_fashion.get_part(conn.p);
                conn.p.change_map_di_buon(conn.p);
                Service.send_notice_box(conn, "Nhận thành công");
                break;
            }
            default: {
                Service.send_notice_box(conn, "Chưa có chức năng");
                break;
            }
        }
    }

    private static void Menu_Graham(Session conn, byte index) throws IOException {
        switch (index) {
            case 0: {
                Service.send_box_UI(conn, 32);
                break;
            }
            case 1: {
                if (conn.p.pet_di_buon != null && Math.abs(conn.p.pet_di_buon.x - conn.p.x) < 75
                        && Math.abs(conn.p.pet_di_buon.y - conn.p.y) < 75 && conn.p.item.wear[11] != null
                        && conn.p.item.wear[11].id == 3599) {
                    //
                    int vang_recei = 0;
                    for (int i = 0; i < conn.p.pet_di_buon.item.size(); i++) {
                        if (conn.p.pet_di_buon.item.get(i) == 3590) {
                            vang_recei += 2_500_000;
                        } else if (conn.p.pet_di_buon.item.get(i) == 3591) {
                            vang_recei += 3_500_000;
                        } else if (conn.p.pet_di_buon.item.get(i) == 3592) {
                            vang_recei += 4_500_000;
                        }
                    }
                    if (vang_recei > 0) {
                        conn.p.update_vang(vang_recei);
                        conn.p.item.char_inventory(5);
                        
                        Message mout = new Message(8);
                        mout.writer().writeShort(conn.p.pet_di_buon.index);
                        for (int i = 0; i < conn.p.map.players.size(); i++) {
                            Player p0 = conn.p.map.players.get(i);
                            if (p0 != null) {
                                p0.conn.addmsg(mout);
                            }
                        }
                        mout.cleanup();
                        //
                        Pet_di_buon_manager.remove(conn.p.pet_di_buon.name);
                        conn.p.pet_di_buon = null;
                        Service.send_notice_box(conn, "Nhận được " + vang_recei + " vàng!");
                        Player p = conn.p;
                        if (p.quest_daily[0] == 2 && p.quest_daily[3] < p.quest_daily[4]) {
                            p.quest_daily[3]++;
                        }
                        if (conn.p.nv_tinh_tu[0] == 15 && conn.p.nv_tinh_tu[1] < conn.p.nv_tinh_tu[2]) {
							conn.p.nv_tinh_tu[1]++;
						}
                    } else {
                        Service.send_notice_box(conn, "Ngươi chưa có gì mà hay bị cướp mất hết hàng rồi!");
                    }
                } else {
                    Service.send_notice_box(conn, "Ta không thấy con vật đi buôn của ngươi");
                }
                break;
            }
            case 2: {
                Item3 itbag = new Item3();
                itbag.id = 3599;
                itbag.clazz = ItemTemplate3.item.get(3599).getClazz();
                itbag.type = ItemTemplate3.item.get(3599).getType();
                itbag.level = ItemTemplate3.item.get(3599).getLevel();
                itbag.icon = ItemTemplate3.item.get(3599).getIcon();
                itbag.op = new ArrayList<>();
                itbag.op.addAll(ItemTemplate3.item.get(3599).getOp());
                itbag.color = 5;
                itbag.part = ItemTemplate3.item.get(3599).getPart();
                itbag.tier = 0;
                itbag.islock = true;
                itbag.time_use = 0;
                // thao do
                if (conn.p.item.wear[11] != null && conn.p.item.wear[11].id != 3593 && conn.p.item.wear[11].id != 3599
                        && conn.p.item.wear[11].id != 3596) {
                    Item3 buffer = conn.p.item.wear[11];
                    conn.p.item.wear[11] = null;
                    conn.p.item.add_item_bag3(buffer);
                }
                itbag.name = ItemTemplate3.item.get(3599).getName() + " [Khóa]";
                conn.p.item.wear[11] = itbag;
                conn.p.item.char_inventory(4);
                conn.p.item.char_inventory(7);
                conn.p.item.char_inventory(3);
                conn.p.fashion = Part_fashion.get_part(conn.p);
                conn.p.change_map_di_buon(conn.p);
                Service.send_notice_box(conn, "Nhận thành công");
                break;
            }
            default: {
                Service.send_notice_box(conn, "Chưa có chức năng");
                break;
            }
        }
    }

    private static void Menu_Mr_Dylan(Session conn, byte index) throws IOException {
        if (conn.p.item.wear[11] == null || (conn.p.item.wear[11] != null && conn.p.item.wear[11].id != 3599)) {
            Service.send_notice_box(conn, "Con không phải là thương nhân");
            return;
        }
        if (conn.p.pet_di_buon != null && Math.abs(conn.p.pet_di_buon.x - conn.p.x) < 75
                && Math.abs(conn.p.pet_di_buon.y - conn.p.y) < 75) {
            switch (index) {
                case 0: {
                    Service.send_box_UI(conn, 31);
                    break;
                }
                default: {
                    Service.send_notice_box(conn, "Chưa có chức năng");
                    break;
                }
            }
        } else {
            Service.send_notice_box(conn, "Ta không thấy con vật đi buôn của ngươi");
        }
    }

    private static void Menu_NauKeo(Session conn, byte index) throws IOException {
        if (Manager.gI().event == 1) {
            switch (index) {
                case 0: {
                    // Service.send_box_input_text(conn, 11, "Nhập số lượng", new String[] {"Số lượng :"});
                    if (conn.p.get_ngoc() < 10) {
                        Service.send_notice_box(conn, "Không đủ 10 ngọc");
                        return;
                    }
                    if (Event_1.naukeo.time <= 30) {
                        Service.send_notice_box(conn, "Không thể tăng tốc");
                        return;
                    }
                    conn.p.update_ngoc(-10);
                    conn.p.item.char_inventory(5);
                    Event_1.naukeo.update(1);
                    Service.send_notice_box(conn, "Tăng tốc thành công");

                    break;
                }
                case 1: {
                    Service.send_notice_box(conn, "Nguyên liệu cần để nấu kẹo như sau: Đường, Sữa, Bơ, Vani\r\n"
                            + "- Mỗi ngày server cho nấu kẹo 1 lần vào lúc 17h , thời gian nấu là 2 tiếng.\r\n"
                            + "- Thời gian đăng ký là từ 19h ngày hôm trước đến 16h30 ngày hôm sau. Phí đăng ký là 5 ngọc\r\n"
                            + "- Một lần tăng tốc mất 10 ngọc và sẽ giảm được 2 phút nấu\r\n"
                            + "- Số kẹo tối đa nhận được là 20 kẹo.Tuy nhiên nếu các hiệp sĩ góp càng nhiều thì càng có lợi vì 10 người chơi góp nhiều nguyên liệu nhất sẽ được cộng thêm 20 cái\r\n"
                            + "+ Số kẹo nhận được sẽ tính theo công thức 1 Kẹo = 1 Đường + 1 Sữa + 1 Bơ+ 1 Vani");
                    break;
                }
                case 2: {
                    Service.send_notice_box(conn,
                            "Thông tin:\nĐã góp : " + Event_1.get_keo_now(conn.p.name) + "\nThời gian nấu còn lại : "
                            + ((Event_1.naukeo.time == 0) ? "Không trong thời gian nấu"
                                    : ("Còn lại " + Event_1.naukeo.time + "p")));
                    break;
                }
                case 3: {
                    send_menu_select(conn, 120, Event_1.get_top_naukeo());
                    break;
                }
                case 4: {
                    send_menu_select(conn, 120, Event_1.get_top_tanghoa());
                    break;
                }
                default: {
                    Service.send_notice_box(conn, "Chưa có chức năng");
                    break;
                }
            }
        }
    }

    private static void Menu_Event(Session conn, byte index) throws IOException {
        if (Manager.gI().event == 1) {
            switch (index) {
                case 0: {
                    Service.send_box_input_text(conn, 10, "Nhập số lượng", new String[]{"Số lượng :"});
                    break;
                }
                case 1: {
                    Service.send_notice_box(conn,
                            "Để đổi thành Hộp đồ chơi hoàn chỉnh theo công thức: 20.000 vàng + 50 Bức tượng rồng + 50 Kiếm đồ chơi + 50 Đôi giày nhỏ xíu + 50 Trang phục tí hon + 50 Mũ lính chì."
                            + "\nĐể đổi thành Túi kẹo hoàn chỉnh theo công thức: 50.000 vàng + 5 Kẹo.");
                    break;
                }
                case 2: {
                    if (!Event_1.check_time_can_register()) {
                        Service.send_notice_box(conn, "Không trong thời gian đăng ký!");
                        return;
                    }
                    if (conn.p.get_ngoc() < 5) {
                        Service.send_notice_box(conn, "Không đủ 5 ngọc");
                        return;
                    }
                    if (Event_1.check(conn.p.name)) {
                        Service.send_notice_box(conn, "Đã đăng ký rồi, quên à!");
                        return;
                    }
                    conn.p.update_ngoc(-5000);
                    conn.p.item.char_inventory(5);
                    Event_1.add_material(conn.p.name, 0);
                    Service.send_notice_box(conn, "Đăng ký thành công, có thể góp nguyên liệu rồi");
                    break;
                }
                case 3: {
                    if (!Event_1.check_time_can_register()) {
                        Service.send_notice_box(conn, "Không trong thời gian đăng ký!");
                        return;
                    }
                    if (Event_1.check(conn.p.name)) {
                        Service.send_box_input_text(conn, 11, "Nhập số lượng", new String[]{"Số lượng :"});
                    } else {
                        Service.send_notice_box(conn, "Chưa đăng ký nấu kẹo, hãy đăng ký!");
                    }
                    break;
                }
                case 4: {
                    int quant = Event_1.get_keo(conn.p.name);
                    if (quant > 0) {
                        quant = (quant > 20) ? 20 : quant;
                        if (Event_1.list_bxh_naukeo_name.contains(conn.p.name)) {
                            quant += 20;
                        }
                        quant *= 3;
                        Item47 it = new Item47();
                        it.category = 4;
                        it.id = 162;
                        it.quantity = (short) quant;
                        conn.p.item.add_item_bag47(4, it);
                        conn.p.item.char_inventory(4);
                        Service.send_notice_box(conn, "Nhận được " + quant + " kẹo");
                    } else {
                        Service.send_notice_box(conn, "Đã nhận rồi hoặc chưa tham gia!");
                    }
                    break;
                }
                case 5: {
                    Service.send_box_input_text(conn, 12, "Nhập số lượng", new String[]{"Số lượng :"});
                    break;
                }
                case 6:
                case 7:
                case 8:
                case 9:
                case 10:
                case 11:
                case 12:
                case 13: {
                    if (conn.p.item.get_bag_able() < 1) {
                        Service.send_notice_box(conn, "Hành trang không đủ chỗ trống!");
                        return;
                    }
                    short[] id_receiv = new short[]{4626, 4761, 3610, 4636, 4709, 4710, 281, 3616};
                    short[] tuikeo_required = new short[]{500, 500, 400, 400, 400, 400, 15, 250};
                    short[] hopdochoi_required = new short[]{500, 500, 400, 400, 400, 400, 15, 250};
                    int[] ngoc_required = new int[]{50000, 50000, 40000, 40000, 40000, 40000, 15000, 25000};
                    if (tuikeo_required[index - 6] > conn.p.item.total_item_by_id(4, 157)) {
                        Service.send_notice_box(conn, "Không đủ " + tuikeo_required[index - 6] + " túi kẹo!");
                        return;
                    }
                    if (hopdochoi_required[index - 6] > conn.p.item.total_item_by_id(4, 158)) {
                        Service.send_notice_box(conn, "Không đủ " + hopdochoi_required[index - 6] + " hộp đồ chơi!");
                        return;
                    }
                    if (ngoc_required[index - 6] > conn.p.get_ngoc()) {
                        Service.send_notice_box(conn, "Không đủ " + ngoc_required[index - 6] + " ngọc!");
                        return;
                    }
                    if (index != 12) {
                        Item3 itbag = new Item3();
                        ItemTemplate3 it_temp = ItemTemplate3.item.get(id_receiv[index - 6]);
                        itbag.id = it_temp.getId();
                        itbag.name = it_temp.getName();
                        itbag.clazz = it_temp.getClazz();
                        itbag.type = it_temp.getType();
                        itbag.level = 1;
                        itbag.icon = it_temp.getIcon();
                        itbag.op = new ArrayList<>();
                        itbag.op.addAll(it_temp.getOp());
                        itbag.color = 4;
                        itbag.part = it_temp.getPart();
                        itbag.tier = 0;
                        itbag.islock = false;
                        itbag.time_use = 0;
                        conn.p.item.add_item_bag3(itbag);
                        Service.send_notice_box(conn, "Nhận được " + itbag.name + ".");
                    } else {
                        Item47 itbag = new Item47();
                        itbag.id = id_receiv[index - 6];
                        itbag.quantity = (short) 20;
                        itbag.category = 4;
                        conn.p.item.add_item_bag47(4, itbag);
                        Service.send_notice_box(conn, "Nhận được 20 xe trượt tuyết.");
                    }
                    conn.p.item.remove(4, 157, tuikeo_required[index - 6]);
                    conn.p.item.remove(4, 158, hopdochoi_required[index - 6]);
                    conn.p.update_ngoc(-ngoc_required[index - 6]);
                    conn.p.item.char_inventory(4);
                    break;
                }
                default: {
                    Service.send_notice_box(conn, "Đang được chuẩn bị");
                    break;
                }
            }
        }
    }

//    private static void Menu_diempk(Session conn, byte index) throws IOException {
//        switch (index) {
//            case 0: {
//                Service.send_notice_box(conn, "Bạn đang có " + conn.p.hieuchien + " Điểm Pk.");
//                break;
//            }
//            default: {
//                Service.send_notice_box(conn, "Chưa có chức năng");
//                break;
//            }
//        }
//    }
    private static void Menu_MissAnwen(Session conn, byte index) throws IOException {
        Vgo vgo = null;
        switch (index) {
            case 0: {
                vgo = new Vgo();
                vgo.id_map_go = 1;
                vgo.x_new = 432;
                vgo.y_new = 354;
                conn.p.change_map(conn.p, vgo);
                break;
            }
            default: {
                Service.send_notice_box(conn, "Chưa có chức năng");
                break;
            }
        }
    }
    
    

    private static void Menu_top(Session conn, byte index) throws IOException {
        switch (index) {
            case 0: {
                if (conn.p.chucphuc == 1) {
                    int ngoc_ = Util.random(100, 500);
                    int vang_ = Util.random(1000, 10000);
                    conn.p.update_ngoc(ngoc_);
                    conn.p.update_vang(vang_);
                    conn.p.item.char_inventory(5);
                    conn.p.chucphuc = 0;
                    conn.p.update_chucphuc(0);
                    Service.send_notice_box(conn,
                            "Thành công nước bọt của bạn rất hôi nên mới nhận được " + ngoc_ + " ngọc," + vang_ + "Vàng.");
                } else {
                    Service.send_notice_box(conn, "1 ngày nhổ 1 lần thôi phí nước bọt lắm");
                }
                break;
            }
//            
            case 1: { 
                send_menu_select(conn, 1002, new String[]{"Hướng dẫn", "Luyện", "Hiện tại"});
                break;
            }
            case 2: { 
                send_menu_select(conn, 1000, new String[]{"Hướng dẫn", "Luyện", "Hiện tại"});
                break;
            }
            case 3: { 
                send_menu_select(conn, 1001, new String[]{"Hướng dẫn", "Luyện", "Hiện tại"});
                break;
            }            
            case 4: {
                  send_menu_select(conn, 130, new String[]{"Thông Tin Tài Khoản", "Thông Tin Bản Thân"});
                  break;
            }
            case 5: { 
                send_menu_select(conn, 129, new String[]{"Nhận Mốc", "Hướng Dẫn", "Tổng Nạp"});
                break;
            }      
            default: {
                Service.send_notice_box(conn, "Chưa có chức năng");
                break;
            }
        }
    }

    private static void Menu_TachCanh(Session conn, byte index) throws IOException {
        Item3 item = null;
        int count = 0;
        for (int i = 0; i < conn.p.item.bag3.length; i++) {
            Item3 it = conn.p.item.bag3[i];
            if (it != null && it.type == 7 && it.tier > 0) {
                if (count == index) {
                    item = it;
                    break;
                }
                count++;
            }
        }
        if (item != null) {
            conn.p.id_wing_split = index;
            int quant1 = 40;
            int quant2 = 10;
            int quant3 = 50;
            for (int i = 0; i < item.tier; i++) {
                quant1 += GameSrc.wing_upgrade_material_long_khuc_xuong[i];
                quant2 += GameSrc.wing_upgrade_material_kim_loai[i];
                quant3 += GameSrc.wing_upgrade_material_da_cuong_hoa[i];
            }
            if (item.tier > 15) {
                quant1 /= 2;
                quant2 /= 2;
                quant3 /= 2;
            } else {
                quant1 /= 3;
                quant2 /= 3;
                quant3 /= 3;
            }
            Service.send_box_input_yesno(conn, 114, "Bạn có muốn tách cánh này và nhận được: " + quant1
                    + " lông và khúc xương, " + quant2 + " kim loại, " + quant3 + " đá cường hóa?");
        }
    }

    private static void Menu_TienCanh(Session conn, byte index) throws IOException {
        switch (index) {
            case 0: {
                // Service.send_box_UI(conn, index);
                Service.send_msg_data(conn, 23, "create_wings");
                break;
            }
            case 1: {
                Message m2 = new Message(77);
                m2.writer().writeByte(6);
                conn.addmsg(m2);
                m2.cleanup();
                //
                m2 = new Message(77);
                m2.writer().writeByte(1);
                m2.writer().writeUTF("Nâng cấp cánh");
                conn.addmsg(m2);
                m2.cleanup();
                conn.p.is_create_wing = false;
                break;
            }
            case 3: {
                conn.p.id_wing_split = -1;
                List<String> list = new ArrayList<>();
                for (int i = 0; i < conn.p.item.bag3.length; i++) {
                    Item3 it = conn.p.item.bag3[i];
                    if (it != null && it.type == 7 && it.tier > 0) {
                        list.add((it.name + " +" + it.tier));
                    }
                }
                if (list.size() > 0) {
                    String[] list_2 = new String[list.size()];
                    for (int i = 0; i < list_2.length; i++) {
                        list_2[i] = list.get(i);
                    }
                    send_menu_select(conn, 121, list_2);
                } else {
                    Service.send_notice_box(conn, "Làm gì có cánh mà đòi tách?");
                }
                break;
            }
            default: {
                Service.send_notice_box(conn, "Chưa có chức năng");
                break;
            }
        }
    }

    private static void Menu_Clan_Manager(Session conn, byte index) throws IOException {
        if (conn.p.myclan.mems.get(0).name.equals(conn.p.name)) {
            switch (index) {
                case 0: {
                    conn.p.myclan.open_box_clan(conn);
                    break;
                }
                case 1: {
                    if (conn.p.myclan.get_percent_level() >= 100) {
                        Service.send_box_input_yesno(conn, 118,
                                "Bạn có muốn nâng cấp bang lên level " + (conn.p.myclan.level + 1) + " với "
                                + (Clan.vang_upgrade[1] * conn.p.myclan.level) + " vàng và " + (conn.p.myclan.level + 1)
                                + " với " + (Clan.ngoc_upgrade[1] * conn.p.myclan.level) + " ngọc không?");
                    } else {
                        Service.send_notice_box(conn, "Chưa đủ exp để nâng cấp!");
                    }
                    break;
                }
                case 2: {
                    Service.send_box_input_yesno(conn, 116,
                            "Hãy xác nhận việc hủy bang, đừng hối hận khóc lóc xin admin khôi phục lại nhá!");
                    break;
                }
                case 3: {
                    Service.send_box_input_text(conn, 13, "Nhập tên :", new String[]{"Nhập tên :"});
                    break;
                }
                case 4: {
    Service.send_box_input_yesno(conn, 40, "Bạn có chắc muốn chia toàn bộ quỹ bang cho các thành viên online không?");
    break;
}
                default: {

                    Service.send_notice_box(conn, "Chưa có chức năng");
                    break;
                }
            }
        }
    }

    private static void Menu_Dungeon_Mode_Selection(Session conn, byte index) throws IOException {
//        if (conn.p.dungeon != null && conn.p.dungeon.getWave() == 20) {
//            if (index != 2 && conn.p.party != null && conn.p.party.get_mems().get(0).id != conn.p.id) {
//                Service.send_notice_box(conn, "Chỉ có đội trưởng mới có quyền quyết định!");
//                return;
//            }
//            conn.p.dungeon.setMode(index);
//            if (conn.p.dungeon != null) {
//                conn.p.dungeon.setWave(21);
//                conn.p.dungeon.state = 1;
//            } else {
//                Service.send_notice_box(conn, "Có lỗi xảy ra, hãy thử chọn lại!");
//            }
//        }
    }

    private static void Menu_LinhCanh(Session conn, byte index) throws IOException {
        if (conn.p.dungeon != null && conn.p.dungeon.getWave() == 20) {
            if (index != 2 && conn.p.party != null && conn.p.party.get_mems().get(0).id != conn.p.id) {
                Service.send_notice_box(conn, "Chỉ có đội trưởng mới có quyền quyết định!");
                return;
            }
            switch (index) {
                case 0: {
                    send_menu_select(conn, 123, new String[]{"Easy", "Normal", "Hard", "Nightmare", "Hell"});
                    break;
                }
                case 1: {
                    if (conn.p.dungeon != null) {
                        conn.p.dungeon.state = 6;
                    } else {
                        Service.send_notice_box(conn, "Có lỗi xảy ra, hãy thử chọn lại!");
                    }
                    Service.send_notice_box(conn, "gà thật, éo dám đi tiếp à");
                    break;
                }
                case 2: {
                    Service.send_notice_box(conn,
                            "Đến được đây quả là có cố gắng, hãy nói chuyện với phó chỉ huy để nhận thưởng hoàn thành phó bản, hoặc chọn tiếp tục chinh phục để có thể nhận được nhiều phần thưởng hơn");
                    break;
                }
                default: {
                    Service.send_notice_box(conn, "Chưa có chức năng");
                    break;
                }
            }
        }
    }

    private static void Menu_PhoChiHuy(Session conn, byte index) throws IOException {
        switch (index) {
            case 0: {
    if (conn.p.level < 30) {
        Service.send_notice_box(conn, "Đạt level 30 mới có thể vào phó bản");
        return;
    }

    if (conn.p.point_active[0] < 1) {
        Service.send_notice_box(conn, "Đã hết lượt đi, hãy quay lại vào ngày mai");
        return;
    }

    String notice = "Danh sách người chơi vào phó bản:\n";

    if (conn.p.party != null) {
        // Party mode
        if (!conn.p.party.isLeader(conn.p)) {
            Service.send_notice_box(conn, "Chỉ đội trưởng mới có thể đưa tổ đội vào phó bản.");
            return;
        }

        int stt = 1;
        for (Player mem : conn.p.party.get_mems()) {
            if (mem.level < 30) {
                Service.send_notice_box(conn, mem.name + " chưa đủ level 30 để vào phó bản.");
                return;
            }
            if (mem.point_active[0] < 1) {
                Service.send_notice_box(conn, mem.name + " đã hết lượt đi.");
                return;
            }
            notice += stt++ + ") " + mem.name + " : level " + mem.level + "\n";
        }
    } else {
        // Solo mode
        notice += "1) " + conn.p.name + " : level " + conn.p.level + "\n";
    }

    notice += "\nĐộ khó: ???\nHãy xác nhận!";
    conn.p.dungeon = null; // Xoá dungeon cũ nếu có

    Service.send_box_input_yesno(conn, 119, notice); // 👈 Hiện confirm box
    break;
}
            case 1: {
                Service.send_notice_box(conn,
                        "Ngã tư tử thần nâng cấp:\nSau khi vượt qua 20 ải đầu sẽ nhận phần thưởng hoàn thành phó bản, sau đó hãy nói chuyện với npc trong phó bản để quyết định độ khó, cuối cùng là tiếp tục chinh phục phó bản.\n Càng tích nhiều điểm càng được nhiều phần thưởng nhé, điểm sẽ được tính dựa trên số ải vượt qua và lượng dame gây ra.\nLưu ý mỗi ngày đi tối đa 3 lần.");
                break;
            }
            case 2: {
                synchronized (Dungeon.bxh_time_complete) {
                    String notice;
                    if (Dungeon.bxh_time_complete.size() > 0) {
                        notice = "BXH thời gian hoàn thành:\n";
                        int dem = 1;
                        for (Dungeon.BXH_Dungeon_Finished set : Dungeon.bxh_time_complete) {
                            notice += (dem++) + ". " + set.name + " : " + set.time + "s\n";
                        }
                    } else {
                        notice = "Chưa có thông tin";
                    }
                    Service.send_notice_box(conn, notice);
                }
                break;
            }
            case 5: {
                Service.send_notice_box(conn, "Thông tin:\nSố lần còn lại " + conn.p.point_active[0]
                        + ": lần\nTổng điểm hôm nay : " + conn.p.point_active[1]);
                break;
            }
            case 6: {
                if (conn.p.point_active[1] < 1) {
                    Service.send_notice_box(conn, "Hôm nay chưa làm gì cả, không làm mà đòi có ăn à con??");
                    return;
                }
                if (conn.p.item.get_bag_able() < 3) {
                    Service.send_notice_box(conn, "Hành trang không đủ chỗ!");
                    return;
                }
                while (conn.p.point_active[1] > 0) {
                    conn.p.point_active[1]--;
                    short id_ = Medal_Material.m_blue[Util.random(0, 10)];
                    if (25 > Util.random(0, 100)
                            && ((conn.p.item.get_bag_able() > 0) || (conn.p.item.total_item_by_id(7, id_) > 0))) {
                        Item47 itbag = new Item47();
                        itbag.id = id_;
                        itbag.quantity = (short) Util.random(0, 500);
                        itbag.category = 7;
                        conn.p.item.add_item_bag47(7, itbag);
                    }
                    //
                    conn.p.update_vang(Util.random(10, 50));
                    Log.gI().add_log(conn.p.name, "nhận quà pho ban nhan duoc " + Util.number_format(Util.random(10, 50)) + " vàng");
                }
                conn.p.item.char_inventory(7);
                Service.send_notice_box(conn, "Nhận thành công");
                break;
            }
            case 7: {
                Item3 itbag = new Item3();
                itbag.id = 3596;
                itbag.clazz = ItemTemplate3.item.get(3596).getClazz();
                itbag.type = ItemTemplate3.item.get(3596).getType();
                itbag.level = ItemTemplate3.item.get(3596).getLevel();
                itbag.icon = ItemTemplate3.item.get(3596).getIcon();
                itbag.op = new ArrayList<>();
                itbag.op.addAll(ItemTemplate3.item.get(3596).getOp());
                itbag.color = 5;
                itbag.part = ItemTemplate3.item.get(3596).getPart();
                itbag.tier = 0;
                itbag.islock = true;
                itbag.time_use = 0;
                // thao do
                if (conn.p.item.wear[11] != null && conn.p.item.wear[11].id != 3593 && conn.p.item.wear[11].id != 3599
                        && conn.p.item.wear[11].id != 3596) {
                    Item3 buffer = conn.p.item.wear[11];
                    conn.p.item.wear[11] = null;
                    conn.p.item.add_item_bag3(buffer);
                }
                itbag.name = ItemTemplate3.item.get(3596).getName() + " [Khóa]";
                conn.p.item.wear[11] = itbag;
                conn.p.item.char_inventory(4);
                conn.p.item.char_inventory(7);
                conn.p.item.char_inventory(3);
                conn.p.fashion = Part_fashion.get_part(conn.p);
                conn.p.change_map_di_buon(conn.p);
                Service.send_notice_box(conn, "Nhận thành công");
                break;
            }
            case 8: {
                Service.send_box_input_yesno(conn, 127, "Bạn có muốn mua lượt đi phó bản phí là 2000 ngọc");
                break;
            }
            default: {
                Service.send_notice_box(conn, "Chưa có chức năng");
                break;
            }
        }
    }

    private static void Menu_Pet_Manager(Session conn, byte index) throws IOException {
        switch (index) {
            case 0: {
                Service.send_box_UI(conn, 21);
                break;
            }
            case 1: {
                Service.send_box_UI(conn, 22);
                break;
            }
            case 2: {
                Service.send_box_UI(conn, 23);
                break;
            }
            case 3: {
                if (conn.p.pet_follow) {
                    for (Pet temp : conn.p.mypet) {
                        if (temp.is_follow) {
                            temp.is_follow = false;
                            Message m = new Message(44);
                            m.writer().writeByte(28);
                            m.writer().writeByte(1);
                            m.writer().writeByte(9);
                            m.writer().writeByte(9);
                            m.writer().writeUTF(temp.name);
                            m.writer().writeByte(temp.type);
                            m.writer().writeShort(conn.p.mypet.indexOf(temp)); // id
                            m.writer().writeShort(temp.level);
                            m.writer().writeShort(temp.getlevelpercent()); // exp
                            m.writer().writeByte(temp.type);
                            m.writer().writeByte(temp.icon);
                            m.writer().writeByte(temp.nframe);
                            m.writer().writeByte(temp.color);
                            m.writer().writeInt(temp.get_age());
                            m.writer().writeShort(temp.grown);
                            m.writer().writeShort(temp.maxgrown);
                            m.writer().writeShort(temp.point1);
                            m.writer().writeShort(temp.point2);
                            m.writer().writeShort(temp.point3);
                            m.writer().writeShort(temp.point4);
                            m.writer().writeShort(temp.maxpoint);
                            m.writer().writeByte(temp.op.size());
                            for (int i2 = 0; i2 < temp.op.size(); i2++) {
                                Option_pet temp2 = temp.op.get(i2);
                                m.writer().writeByte(temp2.id);
                                m.writer().writeInt(temp2.param);
                                m.writer().writeInt(temp2.maxdam);
                            }
                            conn.p.conn.addmsg(m);
                            m.cleanup();
                            break;
                        }
                    }
                    conn.p.pet_follow = false;
                    Service.send_wear(conn.p);
                    Service.send_char_main_in4(conn.p);
                } else {
                    Service.send_notice_box(conn, "Đã đeo pet đâu mà đòi tháo??");
                }
                break;
            }
            default: {
                Service.send_notice_box(conn, "Chưa có chức năng");
                break;
            }
        }
    }

    private static void Menu_Mr_Edgar(Session conn, byte index) throws IOException {
        switch (index) {
            case 0: {
                if (conn.p.list_enemies.size() > 0) {
                    String[] name = new String[conn.p.list_enemies.size()];
                    for (int i = 0; i < name.length; i++) {
                        name[i] = conn.p.list_enemies.get(name.length - i - 1);
                    }
                    send_menu_select(conn, 124, name);
                } else {
                    Service.send_notice_box(conn, "Danh sách chưa có ai, hãy đi cà khịa để tạo thêm!");
                }
                break;
            }
            case 1: {
                Service.send_notice_box(conn,
                        "Bị người chơi khác pk thì sẽ được lưu vào danh sách, "
                        + "mỗi lần trả thù sẽ được đưa tới nơi kẻ thù đang đứng với chi phí chỉ vỏn vẹn 2 ngọc.\n"
                        + "Sau khi được đưa tới nơi, tên kẻ thù sẽ được loại ra khỏi danh sách");
                break;
            }
            default: {
                Service.send_notice_box(conn, "Chưa có chức năng");
                break;
            }
        }
    }

    private static void Menu_Zoro(Session conn, byte index) throws IOException {
        if (conn.p.myclan != null) {
            if (conn.p.myclan.mems.get(0).name.equals(conn.p.name)) {
                switch (index) {
                    case 0: {
                        send_menu_select(conn, 122,
                                new String[]{"Kho bang", "Nâng cấp bang", "Hủy bang hội", "Chuyển thủ lĩnh", "Chia lương bang"});
                        break;
                    }
                    case 1: { //
                        Service.send_box_UI(conn, 29);
                        break;
                    }
                    case 2: {
                        Service.send_box_UI(conn, 30);
                        break;
                    }
                    default: {
                        Service.send_notice_box(conn, "Chưa có chức năng");
                        break;
                    }
                }
            } else {
                switch (index) {
                    case 0: {
                        conn.p.myclan.open_box_clan(conn);
                        break;
                    }
                    case 1: {
                        Service.send_box_input_text(conn, 8, "Góp vàng", new String[]{"Số lượng :"});
                        break;
                    }
                    case 2: {
                        Service.send_box_input_text(conn, 9, "Góp Ngọc", new String[]{"Số lượng :"});
                        break;
                    }
                    case 3: {
                        Service.send_box_input_yesno(conn, 117,
                                "Hãy xác nhận việc rời bang, có khi đi rồi quay lại éo đc đâu nhá");
                        break;
                    }
                    default: {
                        Service.send_notice_box(conn, "Chưa có chức năng");
                        break;
                    }
                }
            }
        } else {
            switch (index) {
                case 0: {
                    Service.send_box_input_yesno(conn, 120, "Bạn có muốn đăng ký tạo bang với phí là 20.000 ngọc");
                    break;
                }
                default: {
                    Service.send_notice_box(conn, "Chưa có chức năng");
                    break;
                }
            }
        }
    }

    private static void Menu_VXMM(Session conn, byte index) throws IOException {
        switch (index) {

            case 0: {
                Manager.gI().vxmm.send_in4(conn.p);
                break;
            }
            case 1: {
                Service.send_box_input_text(conn, 3, "Vòng xoay vàng", new String[]{"Tham gia (tối thiểu 10tr) :"});
                break;
            }
            default: {
                Service.send_notice_box(conn, "Chưa có chức năng");
                break;
            }
        }
    }

    private static void Menu_CuopBien(Session conn, byte index) throws IOException {
        switch (index) {
            case 0: {
                send_menu_select(conn, 125, new String[]{"Xem thông tin", "Tham gia"});
                break;
            }
            case 1: {
                Service.send_notice_box(conn, "Sắp ra mắt");
                break;
            }
            case 2: {
                Service.send_notice_box(conn, "Sắp ra mắt");
                break;
            }
            case 3: {
                send_menu_select(conn, 113, new String[]{"Tham gia", "Kết quả", "Lịch Sử"});
                break;
            }
            default: {
                Service.send_notice_box(conn, "Chưa có chức năng");
                break;
            }
        }
    }

    public static void send_menu_select(Session conn, int idnpc, String[] menu) throws IOException {
        if (!conn.p.isdie) {
            if (menu != null && menu.length > 0) {
                Message m2 = new Message(-30);
                m2.writer().writeShort(idnpc);
                m2.writer().writeByte(0);
                m2.writer().writeByte(menu.length);
                for (int i = 0; i < menu.length; i++) {
                    m2.writer().writeUTF(menu[i]);
                }
                m2.writer().writeUTF("MENU :" + idnpc);
                conn.addmsg(m2);
                m2.cleanup();
            }
        }
    }

    private static void Menu_Aman(Session conn, byte index) throws IOException {
        switch (index) {
            case 0: {
                Message m = new Message(23);
                m.writer().writeUTF("Rương đồ");
                m.writer().writeByte(3);
                m.writer().writeShort(0);
                conn.addmsg(m);
                m.cleanup();
                break;
            }
            case 1: {
                if (conn.p.maxbag >= 126) {
                    Service.send_notice_box(conn, "Đã mở rương rồi!");
                    return;
                }
                try ( Connection connection = SQL.gI().getConnection();  Statement statement = connection.createStatement();) {
                    if (statement.executeUpdate("UPDATE `player` SET `maxbag` = 98 WHERE `id` = " + conn.p.id + ";") > 0) {
                        connection.commit();
                    }
                    Service.send_notice_box(conn, "Mở thành công 98 ô, hãy thoát game vào lại để cập nhật!");
                } catch (SQLException e) {
                    e.printStackTrace();
                    Service.send_notice_box(conn, "Có lỗi xảy ra, hãy thử lại!");
                }
                break;
            }
            case 2: {
                // if (conn.p.update_coin(-25_000)) {
                // if (true) {
                // String js =
                // "[1,%s,%s,%s,%s,10,100,300,1,1,1,1,100,[[0,%s,%s],[23,%s,0],[24,%s,0],[25,%s,0],[26,%s,0]]]";
                // byte[] type = new byte[] {8, 13, 10, 11, 11, 11, 12, 12, 12, 9, 7, 7, 7, 6, 5, 5, 5, 4, 4, 4, 3,
                // 3, 3, 2,
                // 2, 2, 1, 1, 1, 0, 0, 0};
                // byte[] icon = new byte[] {26, 41, 32, 33, 34, 35, 36, 37, 38, 29, 21, 22, 23, 20, 15, 16, 17, 12,
                // 13, 14,
                // 9, 10, 11, 6, 7, 8, 0, 1, 2, 3, 4, 5};
                // int rd = Util.random(0, 32);
                // js = String.format(js, type[rd], icon[rd], 3, 0, Util.random(0, 99), Util.random(99, 999),
                // Util.random(1, 50), Util.random(1, 50), Util.random(1, 50), Util.random(1, 50));
                // if (conn.p.mypet == null) {
                // conn.p.mypet = new Pet(conn.p);
                // }
                // conn.p.mypet.setup((JSONArray) JSONValue.parse(js));
                // Service.send_wear(conn.p);
                // Service.send_char_main_in4(conn.p);
                // conn.p.map.update_in4_inside(conn.p);
                // Service.send_box_notice(conn, "Nhận thành công!");
                // // } else {
                // // Service.send_box_notice(conn, "Éo đủ coin, đi nạp đi!");
                // }
                if (conn.user.contains("knightauto_hsod_")) {
                    if (conn.p.level < 10) {
                        Service.send_notice_box(conn, "Đạt level 10 mới có thể đăng ký tài khoản");
                        return;
                    }
                    Service.send_box_input_text(conn, 6, "Nhập thông tin",
                            new String[]{"Tên đăng nhập mới", "Mật khẩu mới"});
                } else {
                    Service.send_notice_box(conn, "Chức năng đang phát triển...");
                }
                break;
            }
            case 3: {
                if (conn.p.item.total_item_by_id(7, 472) < 5) {
                    Service.send_notice_box(conn, "Không đủ 5 ngọc chuyển sinh");
                    return;
                } 
                if (conn.p.level < 140) {
                    Service.send_notice_box(conn, "Cần level 140");
                    return;
                }
                if (conn.p.get_vang() < 10_000_000) {
                    Service.send_notice_box(conn, "Cần 10.000.000 vàng");
                    return;
                }
                if (conn.p.get_ngoc() < 5000) {
                    Service.send_notice_box(conn, "Cần 5.000 Ngọc");
                    return;
                }
                int tyle = Util.random(100);

                if (tyle < 90) {
                    conn.p.tiemnang += 5;
                    conn.p.level = 139;
                    conn.p.item.remove(7, 472, 5);
                    conn.p.update_ngoc(-2_500);
                    conn.p.update_vang(-5_000_000);
                    Service.send_char_main_in4(conn.p);
                    Service.send_notice_box(conn, "Cuộc sống đôi khi công bằng lắm, hôm qua tặng mình may mắn thì hôm nay khuyến mãi thêm cục xui.\n Nhận 5 điểm tiềm năng.");
                    Manager.gI().chatKTGprocess("Quá đen " + conn.p.name + " Đã Chuyển Sinh thất bại :(.");
                    Player p = conn.p;
                    if (p.quest_daily[0] == 7 && p.quest_daily[3] < p.quest_daily[4]) {
                        p.quest_daily[3]++;
                    }
                    break;
                } else {
                    conn.p.level = 10;
                    conn.p.exp = 0;
                    conn.p.item.remove(7, 472, 10);
                    conn.p.update_ngoc(-5_000);
                    conn.p.update_vang(-10_000_000L);
                    conn.p.chuyensinh++;
                    Service.send_notice_box(conn, "+ Chuyển Sinh Thành công, nhớ thoát ra vào lại rồi hãy đi up nhé, \n+ HÃY UOT GAME. \n+ Số Lần: " + conn.p.chuyensinh);
                    Manager.gI().chatKTGprocess("Chúc Mừng " + conn.p.name + " Đã Chuyển Sinh Thành Công " + conn.p.chuyensinh + " Lần.");
                    conn.p.update_Exp(1, false);
                    Service.send_char_main_in4(conn.p);
                    Player p = conn.p;
                    if (p.quest_daily[0] == 7 && p.quest_daily[3] < p.quest_daily[4]) {
                        p.quest_daily[3]++;
                    }
                    break;
                }

            }
            case 4: {
                Service.send_notice_box(conn, "Lần Chuyển Sinh:" + conn.p.chuyensinh + "\nDame : " + conn.p.body.get_dame_physical() + "\nHp :" + conn.p.body.get_max_hp() + "\nMp :" + conn.p.body.get_max_mp() + "\nCm :" + conn.p.body.get_crit() + "\nDef :" + conn.p.body.get_def() + "\nNé :" + conn.p.body.get_miss());
                break;
            }
            case 5: {
                if (conn.status == 0) {
                    send_menu_select(conn, 555, new String[]{"Nhận Nhiệm Vụ", "Trả Nhiệm Vụ", "Hủy Nhiệm Vụ", "Thông Tin", "Thêm lượt"});
                } else {
                    Service.send_notice_box(conn, "TK chua kich hoat");
                }
                break;
            }
            default: {
                Service.send_notice_box(conn, "Chưa có chức năng");
                break;
            }
        }
    }

    private static void Menu_Black_Eye(Session conn, byte index) throws IOException {
        switch (index) {
            case 0: {
                Service.send_box_UI(conn, 13);
                break;
            }
            case 1: {
                Service.send_box_UI(conn, 14);
                break;
            }
            case 2: {
                Service.send_box_UI(conn, 15);
                break;
            }
            case 3: {
                Service.send_box_UI(conn, 16);
                break;
            }
            default: {
                Service.send_notice_box(conn, "Chưa có chức năng");
                break;
            }
        }
    }

    private static void Menu_BXH(Session conn, byte index) throws IOException {
    switch (index) {
        case 0: {
            BXH.send(conn, 0); // BXH LEVEL
            break;
        }
        case 1: {
            BXH.send(conn, 1); // BXH CHUYỂN SINH
            break;
        }
        case 2: {
            BXH.send(conn, 2); // BXH HIẾU CHIẾN
            break;
        }
        case 3: {
            BXH.send(conn, 3); // BXH chiến trường 
            break;
        }
        case 4: {
            String[] list = new String[Math.min(20, BXH.BXH_clan.size())];
            for (int i = 0; i < list.length; i++) {
                list[i] = ("Top " + (i + 1) + " : " + BXH.BXH_clan.get(i).name_clan + "(" + BXH.BXH_clan.get(i).name_clan_shorted + ")");
            }
            if (list.length > 0) {
                send_menu_select(conn, 120, list);
            } else {
                Service.send_notice_box(conn, "Chưa có thông tin");
            }
            break;
        }
        default: {
            Service.send_notice_box(conn, "Chưa có chức năng");
            break;
        }
    }
}

    private static void Menu_Miss_Anna(Session conn, byte index) throws IOException {
        switch (index) {
            case 0: {
                Service.send_box_input_text(conn, 0, "Nhập mã code", new String[]{"Code"});
                break;
            }
            case 1:
            case 2:
            case 3:
            case 4:
            case 5:
            case 6:
            case 7:
            case 8:
            case 9: {
                if (conn.p.item.wear[index + 9] == null) {
                    Service.send_notice_box(conn, "Mặc đâu mà tháo hả thằng ong đất");
                } else if (conn.p.item.get_bag_able() > 0) {
                    Item3 buffer = conn.p.item.wear[index + 9];
                    conn.p.item.wear[index + 9] = null;
                    if (buffer.id != 3599 && buffer.id != 3593 && buffer.id != 3596) {
                        conn.p.item.add_item_bag3(buffer);
                    }
                    conn.p.item.char_inventory(3);
                    conn.p.fashion = Part_fashion.get_part(conn.p);
                    Service.send_wear(conn.p);
                    Service.send_char_main_in4(conn.p);
                    MapService.update_in4_2_other_inside(conn.p.map, conn.p);
                    Service.send_notice_box(conn, "Tháo thành công");
                    //
                    if (index == 2 && conn.p.pet_di_buon != null) {
                        Message mout = new Message(8);
                        mout.writer().writeShort(conn.p.pet_di_buon.index);
                        for (int i = 0; i < conn.p.map.players.size(); i++) {
                            Player p0 = conn.p.map.players.get(i);
                            if (p0 != null) {
                                p0.conn.addmsg(mout);
                            }
                        }
                        mout.cleanup();
                        //
                        Pet_di_buon_manager.remove(conn.p.pet_di_buon.name);
                        conn.p.pet_di_buon = null;
                    }
                } else {
                    Service.send_notice_box(conn, "Hành trang đầy!");
                }
                break;
            }
            case 10: {
                if (conn.p.level < 60) {
                    Service.send_notice_box(conn, "Yêu cầu level trên 60");
                    return;
                }
                send_menu_select(conn, 114, new String[]{"Cầu hôn", "Ly hôn", "Nâng cấp nhẫn", "Hướng dẫn"});
                break;
            }
            default: {
                Service.send_notice_box(conn, "Chưa có chức năng");
                break;
            }
        }
        MapService.change_flag(conn.p.map, conn.p, -1);
    }

    private static void Menu_PhapSu(Session conn, byte index) throws IOException {
        switch (index) {
            case 0: {
                conn.p.id_item_rebuild = -1;
                conn.p.is_use_mayman = false;
                conn.p.id_use_mayman = -1;
                Service.send_box_UI(conn, 18);
                break;
            }
            case 1: {
                Service.send_box_UI(conn, 17);
                break;
            }
            case 2: {
                conn.p.item_replace = -1;
                conn.p.item_replace2 = -1;
                Service.send_box_UI(conn, 19);
                break;
            }
            case 3: {
                Service.send_box_UI(conn, 24);
                break;
            }
            case 4: {
                Service.send_box_UI(conn, 25);
                conn.p.id_medal_is_created = 0;
                break;
            }
            case 5: {
                Service.send_box_UI(conn, 26);
                conn.p.id_medal_is_created = 1;
                break;
            }
            case 6: {
                Service.send_box_UI(conn, 27);
                conn.p.id_medal_is_created = 2;
                break;
            }
            case 7: {
                Service.send_box_UI(conn, 28);
                conn.p.id_medal_is_created = 3;
                break;
            }
            case 8: {
                Service.send_box_UI(conn, 33);
                break;
            }
            case 9: {
                List<String> list = new ArrayList<>();
                for (int i = 0; i < conn.p.item.bag3.length; i++) {
                    Item3 it = conn.p.item.bag3[i];
                    if (it != null && it.type == 16) {
                        list.add(it.name + " +" + it.tier);
                    }
                }

                String[] list_2 = new String[]{"Trống"};
                if (list.size() > 0) {
                    list_2 = new String[list.size()];
                    for (int i = 0; i < list_2.length; i++) {
                        list_2[i] = list.get(i);
                    }
                }
                MenuController.send_menu_select(conn, 116, list_2);
                break;
            }
            case 10: {
                List<String> list = new ArrayList<>();
                for (int i = 0; i < conn.p.item.bag3.length; i++) {
                    Item3 it = conn.p.item.bag3[i];

                    if (it != null && it.type == 16) {
                        list.add(it.name + " +" + it.tier);
                    }
                }
                String[] list_2 = new String[]{"Trống"};
                if (list.size() > 0) {
                    list_2 = new String[list.size()];
                    for (int i = 0; i < list_2.length; i++) {
                        list_2[i] = list.get(i);
                    }
                }
                MenuController.send_menu_select(conn, 115, list_2);
                break;
            }
            case 11: {
                Service.send_box_UI(conn, 34);
                break;
            }
            case 12: {
                Service.send_box_UI(conn, 35);
                break;
            }
            case 13: {
                Service.send_box_UI(conn, 36);
                break;
            }
            default: {
                Service.send_notice_box(conn, "Chưa có chức năng");
                break;
            }
        }
    }

    private static void Menu_Admin(Session conn, byte index) throws IOException {
    switch (index) {
        case 0: {
            // Close server processing
            ServerManager.gI().close();
            System.out.println("Close server is processing....");
            new Thread(new Runnable() {
                @Override
                public void run() {
                    // Flush clan
                    // Clan.flush();
                    SaveData.process();
                    // Kick players
                    for (int k = Session.client_entrys.size() - 1; k >= 0; k--) {
                        Session.client_entrys.get(k).p = null;
                        try {
                            Session.client_entrys.get(k).close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    // Stop service
                    Manager.gI().close();
                }
            }).start();
            break;
        }
        case 1: {
            // Công vàng
            conn.p.update_vang(1_000_000_000);
            conn.p.item.char_inventory(5);
            Service.send_notice_nobox_white(conn, "+ 1.000.000.000 vàng");
            break;
        }
        case 2: {
            // Công ngọc
            conn.p.update_ngoc(1_000_000);
            conn.p.item.char_inventory(5);
            Service.send_notice_nobox_white(conn, "+ 1.000.000 ngọc");
            break;
        }
        case 3: {
            // Cập nhật dữ liệu
            SaveData.process();
            Service.send_notice_nobox_white(conn, "Đã Cập Nhật Data");
            break;
        }
        case 4: {
            // Lấy vật phẩm
            Service.send_box_input_text(conn, 1, "Get Item",
                    new String[]{"Nhập loại (3,4,7) vật phẩm :", "Nhập id vật phẩm", "Nhập số lượng"});
            break;
        }
        case 5: {
    if (!Manager.gI().chiem_mo.isRunning()) {
        // Nếu chưa chạy thì mở chiếm mỏ
        Manager.gI().chiem_mo.setRunning(true);
        Manager.gI().chiem_mo.mo_open_atk();
        Manager.gI().chatKTGprocess(" Thời gian chiếm mỏ đã đến!");
    } else {
        // Nếu đang chạy thì đóng chiếm mỏ
        Manager.gI().chiem_mo.setRunning(false);
        try {
            Manager.gI().chiem_mo.mo_close_atk();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Manager.gI().chatKTGprocess(" Thời gian chiếm mỏ đã đóng!");
    }
    break;
}
//        case 6: {
//            // Mở/đóng chiếm mỏ (đóng)
//            Manager.gI().chiem_mo.mo_close_atk();
//            Manager.gI().chatKTGprocess(" Thời gian chiếm mỏ đã đóng!");
//            break;
//        }    
//        case 7: {
//            // Đóng/mở vòng xoay
//            Manager.isLockVX = !Manager.isLockVX;
//            Service.send_notice_box(conn, "Vòng xoay vàng ngọc đã " + (Manager.isLockVX ? "khóa" : "mở"));
//            break;
//        }
//        case 8: {
//            // Đóng/mở giao dịch
//            Manager.isGiaoDich = !Manager.isGiaoDich;
//            Service.send_notice_box(conn, "Giao dịch đã " + (Manager.isGiaoDich ? "mở" : "khóa"));
//            break;
//        }
//        case 9: {
//            // Đóng/mở KMB
//            Manager.isKmb = !Manager.isKmb;
//            Service.send_notice_box(conn, "Giao dịch đã " + (Manager.isKmb ? "mở" : "khóa"));
//            break;
//        }
        case 6: {
            // Rút ngắn thời gian pet
            for (Pet pet : conn.p.mypet) {
                if (pet.time_born > 0) {
                    pet.time_born = 3; // rút ngắn thời gian
                }
            }
            Service.send_notice_box(conn, "Đã xong");
            break;
        }
//        case 11: {
//            // Mở/đóng chiếm mỏ
//            Manager.gI().chiem_mo.mo_open_atk();
//            Manager.gI().chatKTGprocess(" Thời gian chiếm mỏ đã đến!");
//            break;
//        }
//        case 12: {
//            // Mở/đóng chiếm mỏ (đóng)
//            Manager.gI().chiem_mo.mo_close_atk();
//            Manager.gI().chatKTGprocess(" Thời gian chiếm mỏ đã đóng!");
//            break;
//        }
//        case 13: {
//            // Mở/đóng Lôi Đài
//            if (!LoiDaiManager.isRegister) {
//                LoiDaiManager.gI().startRegister();
//                Service.send_notice_box(conn, "Đã mở đăng kí ld!");
//            } else {
//                LoiDaiManager.timeRegister = System.currentTimeMillis() - 1000;
//                Service.send_notice_box(conn, "Đã đóng đăng kí ld!");
//            }
//            break;
//        }
        case 7: {
            // Mở boss tg
            Manager.gI().bossTG.refresh();
            Manager.gI().chatKTGprocess("[ADMIN] Đã Làm Mới Boss Thế Giới");
            break;
        }
        case 8: {
    switch (ChienTruong.gI().getStatus()) {
        case 0: // Chưa mở
            ChienTruong.gI().open_register();
            //Manager.gI().chatKTGprocess("Đã Mở Đăng Ký Chiến Trường");
            ChienTruong.gI().setStatus(1);
            break;
        case 1: // Đang đăng ký
            ChienTruong.gI().close_and_start();
            //Manager.gI().chatKTGprocess("Chiến Trường Bắt Đầu!");
            ChienTruong.gI().setStatus(2);
            break;
        case 2: // Đang chiến đấu
            ChienTruong.gI().end_now();
            //Manager.gI().chatKTGprocess("Chiến Trường Đã Kết Thúc!");
            ChienTruong.gI().setStatus(0);
            break;
    }
    break;
}
//        case 10: {
//            // Đóng dky chiến trường 
//            ChienTruong.gI().close_and_start();
//            //Manager.gI().chatKTGprocess("Đã Mở Đăng Ký Chiến Trường");
//            break;
//        }
//        case 11: {
//            // end chiến trường 
//            ChienTruong.gI().end_now();
//            //Manager.gI().chatKTGprocess("Đã Mở Đăng Ký Chiến Trường");
//            break;
//        }
        case 9: { //boss level
            Mob_in_map.mo_boss_now();
            break;
        }
        case 10: {
            // Thông tin nhân vật, tiền, coin
            Service.send_box_input_text(conn, 99, "Nhập thông tin", new String[]{"Tên nhân vật", "Số tiền", "Coin"});
            break;
        }
        default: {
            // Chưa có chức năng
            Service.send_notice_box(conn, "Chưa có chức năng");
            break;
        }
    }
}

    private static void Menu_Zulu(Session conn, byte index) throws IOException {
        switch (index) {
            case 0: {
                switch (conn.p.clazz) {
                    case 0: {
                        Service.send_msg_data(conn, 23, "tocchienbinh");
                        break;
                    }
                    case 1: {
                        Service.send_msg_data(conn, 23, "tocsatthu");
                        break;
                    }
                    case 2: {
                        Service.send_msg_data(conn, 23, "tocxathu");
                        break;
                    }
                    case 3: {
                        Service.send_msg_data(conn, 23, "tocphapsu");
                        break;
                    }
                }
                break;
            }
           case 1: {
    if (conn.p.diemdanh == 1) {
        conn.p.diemdanh = 0;
        int ngoc__ = Util.random(100, 1000);
         conn.p.update_ngoc(ngoc__);
        conn.p.item.char_inventory(5);

        // Cộng điểm chúc phúc
        conn.p.chucphuc++;
        conn.p.update_chucphuc(conn.p.chucphuc);

        Service.send_notice_box(conn, "Bạn đã điểm danh thành công, được " + ngoc__ + " ngọc và 1 lần phỉ nhổ");
    } else {
        Service.send_notice_box(conn, "Bạn đã điểm danh hôm nay rồi");
    }
    break;
}
            case 2: {
                Service.send_box_input_text(conn, 5, "Đổi coin sang ngọc", new String[]{"Tỷ lệ 1000 coin = 500 ngọc"});
                break;
            }
            case 3: {
                Service.send_box_input_text(conn, 14, "Đổi coin sang vàng", new String[]{"Tỷ lệ 1000 coin = 5tr vàng"});
                break;
            }
            case 4: {
//                Service.send_notice_box(conn, "chức năng đã bị tắt");
                Service.send_box_input_yesno(conn, 121, "1000 ngọc cho 2h, hãy xác nhận");
                break;
            }
            case 5: {
                EffTemplate ef = conn.p.get_eff(-126);
                if (ef != null && ef.time > System.currentTimeMillis()) {
                    Service.send_notice_box(conn,
                            "Thời gian còn lại : " + Util.getTime((int) (ef.time - System.currentTimeMillis()) / 1000));
                } else {
                    Service.send_notice_box(conn, "Chưa đăng ký kiểm tra cái gì?");
                }
                break;
            }
            case 6: {
                if (conn.p.type_exp == 0) {
                    conn.p.type_exp = 1;
                    Service.send_notice_box(conn, "Đã bật nhận exp");
                } else {
                    conn.p.type_exp = 0;
                    Service.send_notice_box(conn, "Đã tắt nhận exp");
                }
                break;
            }
            case 7: {
                send_menu_select(conn, 109, new String[]{"Chiến binh", "Sát thủ", "Pháp sư", "Xạ thủ"});
                break;
            }
            case 8: {
                send_menu_select(conn, 127, new String[]{"Sức mạnh", "Khéo léo", "Thể lực", "Tinh thần"});
                break;
            }
            case 9: {
                Service.send_notice_box(conn, "Điểm Tiểm Năng :" + conn.p.tiemnang + "."
                        + "\n Sức Mạnh: " + conn.p.point1
                        + "\n Khéo Léo: " + conn.p.point2
                        + "\n Thể Lực: " + conn.p.point3
                        + "\n Tinh Thần: " + conn.p.point4);
                break;
            }
            default: {
                Service.send_notice_box(conn, "Chưa có chức năng");
                break;
            }
        }
    }

    private static void Menu_ChangeZone(Session conn) throws IOException {
        Map[] map = Map.get_map_by_id(conn.p.map.map_id);
        if (map != null) {
            Message m = new Message(54);
            if (Map.is_map_cant_save_site(conn.p.map.map_id)) {
                m.writer().writeByte(conn.p.map.maxzone);
            } else {
                m.writer().writeByte(conn.p.map.maxzone + 1);
            }
            //
            for (int i = 0; i < conn.p.map.maxzone; i++) {
                if (map[i].players.size() > (map[i].maxplayer - 2)) {
                    m.writer().writeByte(2); // redzone
                } else if (map[i].players.size() >= (map[i].maxplayer / 2)) {
                    m.writer().writeByte(1); // yellow zone
                } else {
                    m.writer().writeByte(0); // green zone
                }
                if (i == 4 && Map.is_map_chiem_mo(conn.p.map, false)) {
                    m.writer().writeByte(4);
                } else {
                    m.writer().writeByte(0);
                }
            }
            if (!Map.is_map_cant_save_site(conn.p.map.map_id)) {
                m.writer().writeByte(1);
                m.writer().writeByte(5);
            }
            for (int i = 0; i < conn.p.map.maxzone; i++) {
                m.writer().writeUTF(
                        "Khu " + (map[i].zone_id + 1) + " (" + map[i].players.size() + "/" + map[i].maxplayer + ")");
            }
            if (!Map.is_map_cant_save_site(conn.p.map.map_id)) {
                m.writer().writeUTF("Khu đi buôn");
            }
            //
            conn.addmsg(m);
            m.cleanup();
        }
    }

    private static void Menu_Alisama(Session conn, byte index) throws IOException {
        switch (index) {
            case 0: {
                Service.send_box_UI(conn, 9);
                break;
            }
            case 1: {
                Service.send_box_UI(conn, 10);
                break;
            }
            case 2: {
                Service.send_box_UI(conn, 11);
                break;
            }
            case 3: {
                Service.send_box_UI(conn, 12);
                break;
            }
            case 4: {
                Service.send_box_UI(conn, 332);
                break;
            }
            default: {
                Service.send_notice_box(conn, "Chưa có chức năng");
                break;
            }
        }
    }

    private static void Menu_DaDichChuyen10(Session conn, byte index) throws IOException {
        if (conn.p.item.wear[11] != null && (conn.p.item.wear[11].id == 3599 || conn.p.item.wear[11].id == 3593
                || conn.p.item.wear[11].id == 3596)) {
            return;
        }
        Vgo vgo = null;
        switch (index) {
            case 0: {
                vgo = new Vgo();
                vgo.id_map_go = 1;
                vgo.x_new = 432;
                vgo.y_new = 354;
                conn.p.change_map(conn.p, vgo);
                break;
            }
            case 1: {
                vgo = new Vgo();
                vgo.id_map_go = 33;
                vgo.x_new = 432;
                vgo.y_new = 480;
                conn.p.change_map(conn.p, vgo);
                break;
            }
            case 2: {
                if (conn.status != 0) {
                    Service.send_notice_box(conn, "Tài khoản chưa được kích hoạt, hãy kích hoạt");
                    return;
                }
                vgo = new Vgo();
                vgo.id_map_go = 82;
                vgo.x_new = 432;
                vgo.y_new = 354;
                conn.p.change_map(conn.p, vgo);
                break;
            }
            case 3: {
                vgo = new Vgo();
                vgo.id_map_go = 4;
                vgo.x_new = 888;
                vgo.y_new = 672;
                conn.p.change_map(conn.p, vgo);
                break;
            }
            case 4: {
                vgo = new Vgo();
                vgo.id_map_go = 5;
                vgo.x_new = 1056;
                vgo.y_new = 864;
                conn.p.change_map(conn.p, vgo);
                break;
            }
            case 5: {
                vgo = new Vgo();
                vgo.id_map_go = 8;
                vgo.x_new = 576;
                vgo.y_new = 222;
                conn.p.change_map(conn.p, vgo);
                break;
            }
            case 6: {
                vgo = new Vgo();
                vgo.id_map_go = 9;
                vgo.x_new = 1243;
                vgo.y_new = 876;
                conn.p.change_map(conn.p, vgo);
                break;
            }
            case 7: {
                vgo = new Vgo();
                vgo.id_map_go = 11;
                vgo.x_new = 286;
                vgo.y_new = 708;
                conn.p.change_map(conn.p, vgo);
                break;
            }
            case 8: {
                vgo = new Vgo();
                vgo.id_map_go = 12;
                vgo.x_new = 240;
                vgo.y_new = 732;
                conn.p.change_map(conn.p, vgo);
                break;
            }
            case 9: {
                vgo = new Vgo();
                vgo.id_map_go = 13;
                vgo.x_new = 150;
                vgo.y_new = 979;
                conn.p.change_map(conn.p, vgo);
                break;
            }
            case 10: {
                vgo = new Vgo();
                vgo.id_map_go = 15;
                vgo.x_new = 469;
                vgo.y_new = 1099;
                conn.p.change_map(conn.p, vgo);
                break;
            }
            case 11: {
                vgo = new Vgo();
                vgo.id_map_go = 16;
                vgo.x_new = 673;
                vgo.y_new = 1093;
                conn.p.change_map(conn.p, vgo);
                break;
            }
            case 12: {
                vgo = new Vgo();
                vgo.id_map_go = 17;
                vgo.x_new = 660;
                vgo.y_new = 612;
                conn.p.change_map(conn.p, vgo);
                break;
            }
            default: {
                Service.send_notice_box(conn, "Chưa có chức năng");
                break;
            }
        }
    }

    private static void Menu_DaDichChuyen33(Session conn, byte index) throws IOException {
        if (conn.p.item.wear[11] != null && (conn.p.item.wear[11].id == 3599 || conn.p.item.wear[11].id == 3593
                || conn.p.item.wear[11].id == 3596)) {
            return;
        }
        Vgo vgo = null;
        switch (index) {
            case 0: {
                vgo = new Vgo();
                vgo.id_map_go = 67;
                vgo.x_new = 576;
                vgo.y_new = 222;
                conn.p.change_map(conn.p, vgo);
                break;
            }
            case 1: {
                vgo = new Vgo();
                vgo.id_map_go = 33;
                vgo.x_new = 432;
                vgo.y_new = 480;
                conn.p.change_map(conn.p, vgo);
                break;
            }
            case 2: {
                // vgo = new Vgo();
                // vgo.idmapgo = 82;
                // vgo.xnew = 432;
                // vgo.ynew = 354;
                // conn.p.changemap(conn.p, vgo);
                Service.send_notice_box(conn, "qua Làng Sói Trắng để đến Khu mua bán");
                break;
            }
            case 3: {
                vgo = new Vgo();
                vgo.id_map_go = 20;
                vgo.x_new = 787;
                vgo.y_new = 966;
                conn.p.change_map(conn.p, vgo);
                break;
            }
            case 4: {
                vgo = new Vgo();
                vgo.id_map_go = 22;
                vgo.x_new = 120;
                vgo.y_new = 678;
                conn.p.change_map(conn.p, vgo);
                break;
            }
            case 5: {
                vgo = new Vgo();
                vgo.id_map_go = 24;
                vgo.x_new = 576;
                vgo.y_new = 222;
                conn.p.change_map(conn.p, vgo);
                break;
            }
            case 6: {
                vgo = new Vgo();
                vgo.id_map_go = 26;
                vgo.x_new = 576;
                vgo.y_new = 222;
                conn.p.change_map(conn.p, vgo);
                break;
            }
            case 7: {
                vgo = new Vgo();
                vgo.id_map_go = 29;
                vgo.x_new = 576;
                vgo.y_new = 222;
                conn.p.change_map(conn.p, vgo);
                break;
            }
            case 8: {
                vgo = new Vgo();
                vgo.id_map_go = 31;
                vgo.x_new = 360;
                vgo.y_new = 624;
                conn.p.change_map(conn.p, vgo);
                break;
            }
            case 9: {
                vgo = new Vgo();
                vgo.id_map_go = 37;
                vgo.x_new = 150;
                vgo.y_new = 674;
                conn.p.change_map(conn.p, vgo);
                break;
            }
            case 10: {
                vgo = new Vgo();
                vgo.id_map_go = 39;
                vgo.x_new = 199;
                vgo.y_new = 882;
                conn.p.change_map(conn.p, vgo);
                break;
            }
            case 11: {
                vgo = new Vgo();
                vgo.id_map_go = 41;
                vgo.x_new = 187;
                vgo.y_new = 462;
                conn.p.change_map(conn.p, vgo);
                break;
            }
            case 12: {
                vgo = new Vgo();
                vgo.id_map_go = 43;
                vgo.x_new = 228;
                vgo.y_new = 43;
                conn.p.change_map(conn.p, vgo);
                break;
            }
            case 13: {
                vgo = new Vgo();
                vgo.id_map_go = 45;
                vgo.x_new = 576;
                vgo.y_new = 222;
                conn.p.change_map(conn.p, vgo);
                break;
            }
            case 14: {
                vgo = new Vgo();
                vgo.id_map_go = 50;
                vgo.x_new = 300;
                vgo.y_new = 300;
                conn.p.change_map(conn.p, vgo);
                break;
            }
            default: {
                Service.send_notice_box(conn, "Chưa có chức năng");
                break;
            }
        }
    }

    private static void Menu_DaDichChuyen55(Session conn, byte index) throws IOException {
        if (conn.p.item.wear[11] != null && (conn.p.item.wear[11].id == 3599 || conn.p.item.wear[11].id == 3593
                || conn.p.item.wear[11].id == 3596)) {
            return;
        }
        Vgo vgo = null;
        switch (index) {
            case 0: {
                vgo = new Vgo();
                vgo.id_map_go = 67;
                vgo.x_new = 576;
                vgo.y_new = 222;
                conn.p.change_map(conn.p, vgo);
                break;
            }
            case 1: {
                // vgo = new Vgo();
                // vgo.idmapgo = 82;
                // vgo.xnew = 432;
                // vgo.ynew = 354;
                // conn.p.changemap(conn.p, vgo);
                Service.send_notice_box(conn, "Đang bảo trì khu vực này");
                break;
            }
            case 2: {
                vgo = new Vgo();
                vgo.id_map_go = 74;
                vgo.x_new = 258;
                vgo.y_new = 354;
                conn.p.change_map(conn.p, vgo);
                break;
            }
            case 3: {
                vgo = new Vgo();
                vgo.id_map_go = 77;
                vgo.x_new = 576;
                vgo.y_new = 222;
                conn.p.change_map(conn.p, vgo);
                break;
            }
            case 4: {
                vgo = new Vgo();
                vgo.id_map_go = 93;
                vgo.x_new = 462;
                vgo.y_new = 342;
                conn.p.change_map(conn.p, vgo);
                break;
            }
            case 5: {
                vgo = new Vgo();
                vgo.id_map_go = 94;
                vgo.x_new = 306;
                vgo.y_new = 240;
                conn.p.change_map(conn.p, vgo);
                break;
            }
            case 6: {
                vgo = new Vgo();
                vgo.id_map_go = 95;
                vgo.x_new = 390;
                vgo.y_new = 162;
                conn.p.change_map(conn.p, vgo);
                break;
            }
            case 7: {
                vgo = new Vgo();
                vgo.id_map_go = 96;
                vgo.x_new = 198;
                vgo.y_new = 666;
                conn.p.change_map(conn.p, vgo);
                break;
            }
            case 8: {
                vgo = new Vgo();
                vgo.id_map_go = 97;
                vgo.x_new = 432;
                vgo.y_new = 168;
                conn.p.change_map(conn.p, vgo);
                break;
            }
            case 9: {
                vgo = new Vgo();
                vgo.id_map_go = 98;
                vgo.x_new = 270;
                vgo.y_new = 132;
                conn.p.change_map(conn.p, vgo);
                break;
            }
            case 10: {
                vgo = new Vgo();
                vgo.id_map_go = 33;
                vgo.x_new = 432;
                vgo.y_new = 480;
                conn.p.change_map(conn.p, vgo);
                break;
            }
            default: {
                Service.send_notice_box(conn, "Chưa có chức năng");
                break;
            }
        }
    }

    private static void Menu_Hammer(Session conn, byte index) throws IOException {
        switch (index) {
            case 0: {
                Service.send_box_UI(conn, 5);
                break;
            }
            case 1: {
                Service.send_box_UI(conn, 6);
                break;
            }
            case 2: {
                Service.send_box_UI(conn, 7);
                break;
            }
            case 3: {
                Service.send_box_UI(conn, 8);
                break;
            }
            case 4: {
//                List<String> list1 = new ArrayList<>();
//                for (int i = 0; i < conn.p.item.wear.length; i++) {
//                    if (conn.p.item.wear[i] != null) {
//                        list1.add(conn.p.item.wear[i].name);
//                    }
//                }
//                String[] list = new String[list1.size()];
//                for (int i = 0; i < list1.size(); i++) {
//                    list[i] = list1.get(i);
//                }
//                send_menu_select(conn, 111, list);
                break;
            }
            case 5: {
                if (conn.p.dropnlmd == 1) {
                    conn.p.dropnlmd = 0;
                    Service.send_notice_box(conn, "đã tắt rơi nlmd");
                } else {
                    conn.p.dropnlmd = 1;
                    Service.send_notice_box(conn, "đã bật rơi nlmd");
                }
                break;
            }
            case 6: {
                send_menu_select(conn, 995,
                        new String[]{"Chiến Binh", "Sát Thủ", "Pháp Sư", "Xạ Thủ", "Nâng cấp đồ tinh tú"});
                break;
            }
            default: {
                Service.send_notice_box(conn, "Chưa có chức năng");
                break;
            }
        }
    }

    private static void Menu_Doubar(Session conn, byte index) throws IOException {
        // cua hang item wear
        switch (index) {
            case 0: {
                Service.send_box_UI(conn, 1);
                break;
            }
            case 1: {
                Service.send_box_UI(conn, 2);
                break;
            }
            case 2: {
                Service.send_box_UI(conn, 3);
                break;
            }
            case 3: {
                Service.send_box_UI(conn, 4);
                break;
            }
            case 4: {
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
                if (Manager.gI().event == 2) {
                    boss_in4 += "Boss sự kiện:\n";
                    for (int i = 0; i < BossEvent.LIST.size(); i++) {
                        if (BossEvent.LIST.get(i).isdie) {
                            String time = Util.getTime((int) ((BossEvent.time[i] - System.currentTimeMillis()) / 1000));
                            boss_in4 += "Xuất hiện sau " + time + " nữa\n";
                        } else {
                            boss_in4 += "Đang ở map Suối ma khu " + (BossEvent.LIST.get(i).zone_id + 1) + "\n";
                        }
                    }
                }
                Service.send_notice_box(conn, boss_in4);
                break;
            }
            case 5: {
                Service.send_box_UI(conn, 37);
                break;
            }
            default: {
                Service.send_notice_box(conn, "Chưa có chức năng");
                break;
            }
        }
    }

    private static void Menu_Lisa(Session conn, byte index) throws IOException {
        switch (index) {
            case 0: { // cua hang potion
                Service.send_box_UI(conn, 0);
                break;
            }
            case 1: {
                if (conn.p.item.total_item_by_id(4, 52) > 0) {
                    MoLy.show_table_to_choose_item(conn.p);
                } else {
                    Service.send_notice_box(conn, "Không đủ vé mở trong hành trang");
                }
                break;
            }
            case 4: {
                Member_ChienTruong temp = ChienTruong.gI().get_bxh(conn.p.name);
                if (temp != null && !temp.received) {
                    switch (ChienTruong.gI().get_index_bxh(temp)) {
                        case 0: {
                            short[] id_ = new short[]{3, 2, 53, 54, 18};
                            short[] id2_ = new short[]{5, 5, 1, 1, 10};
                            short[] id3_ = new short[]{7, 7, 4, 4, 4};
                            for (int i = 0; i < id_.length; i++) {
                                Item47 it = new Item47();
                                it.id = id_[i];
                                it.quantity = id2_[i];
                                conn.p.item.add_item_bag47(id3_[i], it);
                            }
                            break;
                        }
                        case 1:
                        case 2: {
                            short[] id_ = new short[]{3, 2, 18};
                            short[] id2_ = new short[]{5, 5, 10};
                            short[] id3_ = new short[]{7, 7, 4};
                            for (int i = 0; i < id_.length; i++) {
                                Item47 it = new Item47();
                                it.id = id_[i];
                                it.quantity = id2_[i];
                                conn.p.item.add_item_bag47(id3_[i], it);
                            }
                            break;
                        }
                        case 3:
                        case 4:
                        case 5:
                        case 6:
                        case 7:
                        case 8:
                        case 9: {
                            short[] id_ = new short[]{3, 18};
                            short[] id2_ = new short[]{5, 10};
                            short[] id3_ = new short[]{7, 4};
                            for (int i = 0; i < id_.length; i++) {
                                Item47 it = new Item47();
                                it.id = id_[i];
                                it.quantity = id2_[i];
                                conn.p.item.add_item_bag47(id3_[i], it);
                            }
                            break;
                        }
                    }
                    temp.received = true;
                    conn.p.item.char_inventory(4);
                    conn.p.item.char_inventory(7);
                    conn.p.item.char_inventory(3);
                } else {
                    Service.send_notice_box(conn, "Không có tên trong danh sách");
                }
                break;
            }
            default: {
                Service.send_notice_box(conn, "Chưa có chức năng");
                break;
            }
        }
    }

    private static void Menu_CayThong(Session conn, byte index) throws IOException {
        if (Manager.gI().event == 1) {
            switch (index) {
                case 0:
                case 1:
                case 2:
                case 3: {
                    int quant = conn.p.item.total_item_by_id(4, (113 + index));
                    if (quant > 0) {
                        //
                        short[] id_4 = new short[]{2, 5, 52, 142, 225, 271};
                        short[] id_7 = new short[]{0, 4, 23, 34, 39, 352, 357, 362, 367, 372, 377, 382, 387, 392, 397, 402,
                            407, 412,};
                        HashMap<Short, Short> list_4 = new HashMap<>();
                        HashMap<Short, Short> list_7 = new HashMap<>();
                        for (int i = 0; i < quant; i++) {
                            if (conn.p.item.get_bag_able() > 1) {
                                if (80 > Util.random(100)) {
                                    Item47 it = new Item47();
                                    it.category = 4;
                                    it.id = id_4[Util.random(id_4.length)];
                                    it.quantity = (short) Util.random(1, 3);
                                    if (!list_4.containsKey(it.id)) {
                                        list_4.put(it.id, it.quantity);
                                    } else {
                                        short quant_ = it.quantity;
                                        list_4.put(it.id, (short) (list_4.get(it.id) + quant_));
                                    }
                                    conn.p.item.add_item_bag47(4, it);
                                } else {
                                    Item47 it = new Item47();
                                    it.category = 7;
                                    it.id = id_7[Util.random(id_7.length)];
                                    it.quantity = (short) Util.random(1, 2);
                                    if (!list_7.containsKey(it.id)) {
                                        list_7.put(it.id, it.quantity);
                                    } else {
                                        short quant_ = it.quantity;
                                        list_7.put(it.id, (short) (list_7.get(it.id) + quant_));
                                    }
                                    conn.p.item.add_item_bag47(7, it);
                                }
                            }
                        }
                        //
                        Event_1.add_caythong(conn.p.name, quant);
                        conn.p.item.remove(4, (113 + index), quant);
                        conn.p.item.char_inventory(4);
                        conn.p.item.char_inventory(7);
                        String item_receiv = "\n";
                        for (Entry<Short, Short> en : list_4.entrySet()) {
                            item_receiv += ItemTemplate4.item.get(en.getKey()).getName() + " " + en.getValue() + "\n";
                        }
                        for (Entry<Short, Short> en : list_7.entrySet()) {
                            item_receiv += ItemTemplate7.item.get(en.getKey()).getName() + " " + en.getValue() + "\n";
                        }
                        Service.send_notice_box(conn, "Trang trí thành công " + quant + " lần và nhận được:" + item_receiv);
                    } else {
                        Service.send_notice_box(conn, "Không đủ trong hành trang!");
                    }
                    break;
                }
                case 4: {
                    send_menu_select(conn, 120, Event_1.get_top_caythong());
                    break;
                }
                default: {
                    Service.send_notice_box(conn, "Đang bảo trì");
                    break;
                }
            }
        }
    }

    private static void Menu_ThaoKhamNgoc(Session conn, byte index) throws IOException {
        if (conn.p.list_thao_kham_ngoc.size() > 0) {
            if (conn.p.item.get_bag_able() < 3) {
                Service.send_notice_box(conn, "Hành trang không đủ chỗ");
                return;
            }
            Item3 it = conn.p.list_thao_kham_ngoc.get(index);
            if (it != null) {
                for (int i = 0; i < it.op.size(); i++) {
                    if (it.op.get(i).id == 58) {
                        if (it.op.get(i).getParam(0) != -1) {
                            Item47 it_add = new Item47();
                            it_add.id = (short) (it.op.get(i).getParam(0));
                            it_add.quantity = 1;
                            it_add.category = 7;
                            conn.p.item.add_item_bag47(7, it_add);
                        }
                        it.op.get(i).setParam(-1);
                    }
                    if (it.op.get(i).id == 59) {
                        if (it.op.get(i).getParam(0) != -1) {
                            Item47 it_add = new Item47();
                            it_add.id = (short) (it.op.get(i).getParam(0));
                            it_add.quantity = 1;
                            it_add.category = 7;
                            conn.p.item.add_item_bag47(7, it_add);
                        }
                        it.op.get(i).setParam(-1);
                    }
                    if (it.op.get(i).id == 60) {
                        if (it.op.get(i).getParam(0) != -1) {
                            Item47 it_add = new Item47();
                            it_add.id = (short) (it.op.get(i).getParam(0));
                            it_add.quantity = 1;
                            it_add.category = 7;
                            conn.p.item.add_item_bag47(7, it_add);
                        }
                        it.op.get(i).setParam(-1);
                    }
                }
                conn.p.item.char_inventory(4);
                conn.p.item.char_inventory(7);
                conn.p.item.char_inventory(3);
                Service.send_wear(conn.p);
                Service.send_notice_box(conn, "Tháo thành công");
            }
        }
    }

    private static void Menu_ThayDongMeDay_percent(Session conn, byte index) throws IOException {
        if (conn.p.get_ngoc() < 10_000) {
            Service.send_notice_box(conn, "Không đủ 10k ngọc");
            return;
        }
        Item3 it_process = null;
        for (int i = 0; i < conn.p.item.bag3.length; i++) {
            Item3 it = conn.p.item.bag3[i];
            if (it != null && it.type == 16) {
                if (index == 0) {
                    it_process = it;
                    break;
                }
                index--;
            }
        }
        if (it_process != null) {
            Option[] process = new Option[2];
            for (int i = 0; i < it_process.op.size(); i++) {
                if (it_process.op.get(i).id >= 7 && it_process.op.get(i).id <= 11) {
                    if (process[0] == null) {
                        process[0] = it_process.op.get(i);
                    } else if (process[1] == null) {
                        process[1] = it_process.op.get(i);
                    } else {
                        break;
                    }
                }
            }
            if (process[0] != null) {
                process[0].id = (byte) Util.random(7, 12);
//				process[0].setParam(Util.random(1500, 5000));
                process[0].setParam(process[0].getParam(0) + Util.random(50, 100));
            }
            if (process[1] != null) {
                process[1].id = (byte) Util.random(7, 12);
//				process[1].setParam(Util.random(1500, 5000));
                process[1].setParam(process[1].getParam(0) + Util.random(50, 100));
            }
            Service.send_notice_box(conn, "Thành công");
            conn.p.item.char_inventory(3);
        }
    }

    private static void Menu_ThayDongMeDay(Session conn, byte index) throws IOException {
        if (conn.p.get_ngoc() < 10_000) {
            Service.send_notice_box(conn, "Không đủ 10k ngọc");
            return;
        }
        Item3 it_process = null;
        for (int i = 0; i < conn.p.item.bag3.length; i++) {
            Item3 it = conn.p.item.bag3[i];
            if (it != null && it.type == 16) {
                if (index == 0) {
                    it_process = it;
                    break;
                }
                index--;
            }
        }
        if (it_process != null) {
            Option process = null;
            for (int i = 0; i < it_process.op.size(); i++) {
                if (it_process.op.get(i).id < 5) {
                    if (process == null) {
                        process = it_process.op.get(i);
                    } else {
                        break;
                    }
                }
            }
            if (process != null) {
                process.id = (byte) Util.random(5);
                //process.setParam(Util.random(100,600));
                process.setParam(process.getParam(0) + Util.random(10, 30));
            }
            Service.send_notice_box(conn, "Thành công");
            conn.p.item.char_inventory(3);
        }
    }

    private static void Menu_Wedding(Session conn, byte index) throws IOException {
        switch (index) {
            case 0: {
                if (conn.p.item.wear[23] == null) {
                    Service.send_box_input_text(conn, 22, "Nhập thông tin",
                            new String[]{"Chọn nhẫn (1-4) : ", "Tên đối phương : "});
                } else {
                    Service.send_notice_box(conn, "Nhẫn cưới thì đeo đấy mà đòi cưới thêm ai??");
                }
                break;
            }
            case 1: {
                if (conn.p.item.wear[23] != null) {
                    Wedding temp = Wedding.get_obj(conn.p.name);
                    if (temp != null) {
                        String name_target = "";
                        if (temp.name_1.equals(conn.p.name)) {
                            name_target = temp.name_2;
                        } else {
                            name_target = temp.name_1;
                        }
                        Service.send_box_input_yesno(conn, 111, "Xác định hủy hôn ước với " + name_target);
                    }
                } else {
                    Service.send_notice_box(conn, "Đã cưới ai éo đâu, ảo tưởng à??");
                }
                break;
            }
            case 2: {
                Item3 it = conn.p.item.wear[23];
                if (it != null) {
                    float perc = (((float) Wedding.get_obj(conn.p.name).exp) / Level.entrys.get(it.tier).exp) * 100f;
                    String notice = "Exp hiện tại : %s, nâng cấp cần %str vàng và %sk ngọc";
                    String a = String.format("%.2f", perc) + "%";
                    Service.send_box_input_yesno(conn, 112,
                            String.format(notice, a, (5 * (it.tier + 1)), (5 * (it.tier + 1))));
                } else {
                    Service.send_notice_box(conn, "Đã cưới ai éo đâu, ảo tưởng à??");
                }
                break;
            }
            case 3: {
                String notice = "Nhẫn cưới\r\n" + "- 10 tỷ vàng \" nhẫn cưới 1\" \r\n" + "- 500k ngọc \" nhẫn cưới 2\"\r\n"
                        + "- 1 triệu ngọc \" nhẫn cưới 3\"\r\n" + "- 2 triệu ngọc \" nhẫn cưới 4\"\r\n" + "Nâng cấp nhẫn:\r\n"
                        + "Khi đã kết hôn vợ và chồng cùng chung 1 nhóm đi up quái hoặc giết boss thì nhẫn cưới sẽ đc + exp\r\n"
                        + "Đến npc Anna để tiến hành nâng cấp nhẫn, mỗi lần nâng cấp tốn 5k ngọc 5 tr vàng.\r\n"
                        + "Cấp tối đa của nhẫn là 30.";
                Service.send_notice_box(conn, notice);
                break;
            }
            default: {
                Service.send_notice_box(conn, "Chưa có chức năng");
                break;
            }
        }
    }

    private static void Menu_TaiXiu(Session conn, byte index) throws IOException {
        switch (index) {
            case 0: {
                Service.send_box_input_text(conn, 23, "Nhập thông tin",
                        new String[]{"Nhập số coin cược", "Chọn tài (1) hoặc xỉu (2)"});
                break;
            }
            case 1: {
                Manager.gI().tx.send_in4(conn.p);
                break;
            }
            case 2: {
                List<TaiXiuPlayer> temp = Manager.gI().tx.get_tx_history(conn.user);
                if (temp != null) {
                    String notice = "";
                    int dem = 0;
                    for (int i = temp.size() - 1; i >= 0; i--) {
                        if (dem > 20) {
                            break;
                        }
                        dem++;
                        notice += temp.get(i).time + " : ";
                        if (temp.get(i).coin_win > 0) {
                            notice += "thắng " + (temp.get(i).coin_win * 1) + " coin khi chọn "
                                    + ((temp.get(i).type == 1) ? " Tài" : "Xỉu");
                        } else {
                            notice += "thua " + (temp.get(i).coin_win * -1) + " coin khi chọn "
                                    + ((temp.get(i).type == 1) ? " Tài" : "Xỉu");
                        }
                        notice += "\n";
                    }
                    Service.send_notice_box(conn, notice);
                } else {
                    Service.send_notice_box(conn, "Chưa có thông tin");
                }
                break;
            }
            default: {
                Service.send_notice_box(conn, "Chưa có chức năng");
                break;
            }
        }
    }

    private static void Menu_ChangeOptionItem(Session conn, byte index) throws IOException {
        if (conn.p.it_change_op != null && conn.p.it_change_op_index != -1) {
            Option op_change = conn.p.it_change_op.op.get(conn.p.it_change_op_index);
            if (index == 0) {
                if (op_change.id < 5) {
                    op_change.id = (byte) Util.random(5);

                } else if (op_change.id < 12) {
                    op_change.id = (byte) Util.random(7, 12);

                } else if (op_change.id < 16) {
                    op_change.id = (byte) Util.random(14, 16);
                } else if (op_change.id < 21) {
                    op_change.id = (byte) Util.random(16, 21);
                } else if (op_change.id < 27) {
                    op_change.id = (byte) Util.random(23, 27);
                } else if (op_change.id < 37) {
                    op_change.id = (byte) Util.random(33, 37);
                }
                Service.send_box_input_yesno(conn, 108, "Xác nhận đổi dòng " + conn.p.it_change_op.name + " ?");
            } else {
                int old = op_change.getParam(0);
                OptionItem op_temp = OptionItem.get(op_change.id);
                op_change.setParam(old + ((op_temp.getIspercent() == 1) ? Util.random(1, 50) : Util.random(1, 20)));
                Service.send_box_input_yesno(conn, 109, "Xác nhận đổi dòng % " + conn.p.it_change_op.name + " ?");
            }
        }
    }

    private static void Menu_ChuyenPhai(Session conn, byte index) throws IOException {
        if (index == conn.p.clazz) {
            Service.send_notice_box(conn, "Hiện tại đang là class này");
            return;
        }
        if (conn.p.get_ngoc() < 350_000) {
            Service.send_notice_box(conn, "Không đủ 350k ngọc");
            return;
        }
        if (conn.p.item.total_item_by_id(7, 473) < 1) {
            Service.send_notice_box(conn, "Cần Thẻ Đổi Phái Để Thực Hiện");
            return;
        }
        conn.p.item.remove(7, 473, 1);

        conn.p.update_ngoc(-350_000);
        conn.p.item.char_inventory(4);
        conn.p.clazz = index;
        conn.p.rest_potential_point();
        conn.p.rest_skill_point();
        MapService.leave(conn.p.map, conn.p);
        Service.send_skill(conn.p);
        conn.p.set_in4();
        MapService.enter(conn.p.map, conn.p);
        Service.send_notice_box(conn, "Đổi thành công");

    }

    private static void Menu_TuTien(Session conn, byte index) throws IOException {
        switch (index) {
            case 0: {
                String notice = "Cảnh giới tu tiên:\r\n" + "+ Luyện khí\r\n" + "+ Trúc cơ\r\n" + "+ Kết đan\r\n"
                        + "+ Nguyên anh\r\n" + "+ Hoá thần,  \r\n" + "+ Luyện hư\r\n" + "+ Hợp thể \r\n" + "+ Đại thừa\r\n"
                        + "+ Chân tiên\r\n" + "+ Độ kiếp \r\n" + "Mỗi cảnh giới có 5 cấp:\r\n" + "+ Sơ kỳ\r\n"
                        + "+ Trung kỳ\r\n" + "+ Hậu kỳ\r\n" + "+ Viên mãn\r\n" + "+ Đại viên mãn\r\n"
                        + "- Điều kiện cần để có thể thu tiên:\r\n" + "• Đả thông đủ 8 mạch\r\n" + "• Ít nhất là lv 110\r\n"
                        + "• Luyện thể tầng 12 \r\n" + "- Khi tích đủ kinh nghiệm đến npc: .... để tăng cấp.\r\n"
                        + "- Phí mỗi cấp là 5tr vàng và 5k ngọc, 500 exp tu tiên, 100% tỷ lệ thành công.\r\n"
                        + "- Khi cảnh giới đạt đến đại viên mãn, đến npc: .... để đột phá.\r\n"
                        + "Phí đột phá là 10tr vàng và 10k ngọc, 100 thông thiên đan, 1000 exp tu tiên  10% tỷ lệ thành công. \r\n"
                        + "Có thể kiếm thông thiên đan khi up quái 10x trở lên.";
                Service.send_notice_box(conn, notice);
                break;
            }
            case 1: {
                if (conn.p.level >= 110 && conn.p.kinhmach[0] >= 8 && conn.p.luyenthe == 12) {
                    TuTien.start(conn.p);
                } else {
                    Service.send_notice_box(conn, "Chưa đủ điều kiện để có thể tu tiên");
                }
                break;
            }
            case 2: {
                TuTien.send_info(conn.p);
                break;
            }
            default: {
                Service.send_notice_box(conn, "Chưa có chức năng");
                break;
            }
        }
    }

    private static void Menu_KinhMach(Session conn, byte index) throws IOException {
        switch (index) {
            case 0: {
                String notice = "Đả thông kinh mạch\r\n" + "- Gồm có 8 mạch:\r\n" + "+ Đốc mạch\r\n" + "+ Nhâm mạch\r\n"
                        + "+ Xung mạch\r\n" + "+ Đới mạch\r\n" + "+ Âm duy\r\n" + "+ Dương duy\r\n" + "+ Âm khiêu\r\n"
                        + "+ Dương khiêu\r\n" + "- Mỗi mạch gồm có 5 cấp\r\n" + "- Tỷ lệ nâng cấp thành công là 30% \r\n"
                        + "- Khi nâng cấp cần \" Hộ mạch đan\",  vàng và ngọc. \r\n"
                        + "- Mỗi cấp cần 100 \" Hộ mạch đan\", 5 tr vàng, 5000 ngọc.\r\n"
                        + "- \" Hộ mạch đan\" rơi khi giết boss, có thể giao dịch.\r\n"
                        + "- Khi đả thông mỗi cấp người chơi sẽ nhận đc:\r\n" + "+ 1% sát thương \r\n" + "+ 1% máu\r\n"
                        + "+ 1% mana\r\n" + "+ 1% phòng thủ.";
                Service.send_notice_box(conn, notice);
                break;
            }
            case 1: {
                KinhMach.start(conn.p);
                break;
            }
            case 2: {
                KinhMach.send_info(conn.p);
                break;
            }
            default: {
                Service.send_notice_box(conn, "Chưa có chức năng");
                break;
            }
        }
    }

    private static void Menu_LuyenThe(Session conn, byte index) throws IOException {
        switch (index) {
            case 0: {
                String notice = "Chức năng luyện thể:\r\n" + "+Tổng cộng có 12 tầng \r\n" + "Điều kiện để đột phá:\r\n"
                        + "• Các hiệp sĩ phải trên lv 60 \r\n" + "• 100 ngọc thể đan.\r\n" + "• 5tr vàng\r\n"
                        + "• 5k ngọc \r\n"
                        + "- Khi có đủ nguyên liệu thì các hiệp sĩ sẽ đến NPC ..... Để tiến hành đột phá \r\n"
                        + "- Tỷ lệ thành công 30% \r\n" + "Mỗi cấp sẽ đạt đc:\r\n" + "+ 20 sát thương ( theo hệ)\r\n"
                        + "+ 20 phòng thủ\r\n" + "+ 0,2% Chí Mạng\r\n" + "+ 0,2% Xuyên giáp\r\n" + "+ 2000 máu\r\n"
                        + "+ 200 mana";
                Service.send_notice_box(conn, notice);
                break;
            }
            case 1: {
                if (conn.p.level < 60) {
                    Service.send_notice_box(conn, "Level chưa đủ");
                } else {
                    LuyenThe.start(conn.p);
                }
                break;
            }
            case 2: {
                LuyenThe.send_info(conn.p);
                break;
            }
            default: {
                Service.send_notice_box(conn, "Chưa có chức năng");
                break;
            }
        }
    }

    private static void Menu_DailyQuest(Session conn, byte index) throws IOException {
        switch (index) {
            case 0: {
                send_menu_select(conn, 556, new String[]{"Dễ", "Bình thường", "Khó", "Rất khó"});
                break;
            }
            case 1: {
                DailyQuest.finish_quest(conn.p);
                break;
            }
            case 2: {
                DailyQuest.remove_quest(conn.p);
                break;
            }
            case 3: {
                Service.send_notice_box(conn, DailyQuest.info_quest(conn.p));
                break;
            }
            case 4: {
                if (conn.p.get_vang() < 1_000_000_000) {
                    Service.send_notice_box(conn, "Không đủ 1tỷ vàng");
                    return;
                }

                if (conn.p.item.total_item_by_id(4, 321) < 1) {
                    Service.send_notice_box(conn, "Không đủ " + ItemTemplate4.item.get(321).getName());
                    return;
                }

                conn.p.update_vang(-1_000_000_000);
                conn.p.item.remove(4, 321, 1);
                conn.p.item.char_inventory(4);
                conn.p.quest_daily[5] += 5;
                Service.send_notice_box(conn, "Số lần nhận nhiệm vụ còn lại " + conn.p.quest_daily[5]);
                break;
            }
        }
    }

    private static void Menu_MocNap(Session conn, byte index) throws IOException {
        switch (index) {
            case 0: {
                send_menu_select(conn, 557, new String[]{"Mốc 50k", "Mốc 100k", "Mốc 200k", "Mốc 500k", "Mốc 1m", "Mốc 1m5", "Mốc 2m", "Mốc 3m"});
                break;
            }
            case 1: {
                Service.send_notice_box(conn,
                        "Khi các bạn tiến hành nạp game thì sẽ được cộng vào tổng nạp, có thể nhận luân hồi"
                        + "\nCó các mốc và quà: 50k, 100k, 200k, 500k, 1m, 1m5, 2m, 3m."
                        + "\n + Khi nạp đủ các bạn sẽ được nhận quà"
                        + "\n + Lưu ý: từ mốc 500k chỉ được nhận 1 lần DUY NHẤT"
                        + "\n + Các mốc có dòng mề thì hãy nhắn ad để được thêm dòng mề, nhận xong mốc 3tr thì nhắn ad để ad reset mốc nạp cho bạn nhé");
                break;
            }
            case 2: {
               int tongNap = conn.p.get_tongnap(); // Tổng tiền đã nạp, đơn vị: VNĐ
               String msg = "Tổng số tiền bạn đã nạp: " + tongNap + " VNĐ";
               Service.send_notice_box(conn, msg);
             break;
            }
                
        }
    }
    
 private static void Menu_Thongtincanhan(Session conn, byte index) throws IOException {
    switch (index) {
        case 0: {
            int coin = 0;
            try (Connection connect = SQL.gI().getConnection();
                 PreparedStatement ps = connect.prepareStatement("SELECT coin FROM account WHERE user = ?")) {
                ps.setString(1, conn.user);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    coin = rs.getInt("coin");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            
            String timeNow = new SimpleDateFormat("HH:mm:ss").format(new Date());

            Service.send_notice_box(conn,
                "Thông tin:\n Hiệp Sĩ Ong Đất" +
                "\nGiờ Server: " + timeNow +
                "\nĐịa Chỉ IP: " + conn.ip +
                "\nTài Khoản: " + conn.user +
                "\nMật Khẩu: " + conn.pass +
                "\nSố Coin Còn Lại: " + coin +
                "\nTrạng Thái Tài Khoản: " + (conn.status == 0 ? "Đã Kích Hoạt" : "Chưa Kích Hoạt")
            );
            break;
        }

        case 1: {
            Service.send_notice_box(conn,
                "Thông Tin:\n" +
                "Lần Chuyển Sinh: " + conn.p.chuyensinh +
                "\nDame: " + conn.p.body.get_dame_physical() +
                "\nHp: " + conn.p.body.get_max_hp() +
                "\nMp: " + conn.p.body.get_max_mp() +
                "\nChí Mạng: " + conn.p.body.get_crit() +
                "\nPhòng Thủ: " + conn.p.body.get_def() +
                "\nNé Đòn: " + conn.p.body.get_miss()
            );
            break;
        }
        default: {
            Service.send_notice_box(conn, "Chưa có chức năng");
            break;
        }
    }
}
    private static void Menu_Mr_Ballard(Session conn, byte index) throws IOException {
        switch (index) {
            case 0: { // dang ky
                send_menu_select(conn, 998, new String[]{"Đăng ký bằng 500.000 vàng", "Đăng ký bằng 2 ngọc"});
                break;
            }
            case 1: {
                if (ChienTruong.gI().getStatus() == 2) {

                    if (conn.p.get_eff(-126) != null) {
                        return;
                    }

                    if (conn.p.time_use_item_arena > System.currentTimeMillis()) {
                        Service.send_notice_box(conn,
                                "Chờ sau " + (conn.p.time_use_item_arena - System.currentTimeMillis()) / 1000 + " s");
                        return;
                    }
                    Member_ChienTruong info = ChienTruong.gI().get_infor_register(conn.p.name);
                    if (info != null) {
                        conn.p.time_use_item_arena = System.currentTimeMillis() + 250_000;
                        Vgo vgo = new Vgo();
                        switch (info.village) {
                            case 2: { // lang gio
                                vgo.id_map_go = 55;
                                vgo.x_new = 224;
                                vgo.y_new = 256;
                                MapService.change_flag(conn.p.map, conn.p, 2);
                                break;
                            }
                            case 3: { // lang lua
                                vgo.id_map_go = 59;
                                vgo.x_new = 240;
                                vgo.y_new = 224;
                                MapService.change_flag(conn.p.map, conn.p, 1);
                                break;
                            }
                            case 4: { // lang set
                                vgo.id_map_go = 57;
                                vgo.x_new = 264;
                                vgo.y_new = 272;
                                MapService.change_flag(conn.p.map, conn.p, 4);
                                break;
                            }
                            default: { // 5 lang anh sang
                                vgo.id_map_go = 53;
                                vgo.x_new = 276;
                                vgo.y_new = 246;
                                MapService.change_flag(conn.p.map, conn.p, 5);
                                break;
                            }
                        }
                        conn.p.change_map(conn.p, vgo);
                    } else {
                        Service.send_notice_box(conn, "Chưa đăng ký");
                    }
                    // Vgo vgo = new Vgo();
                    // vgo.id_map_go = 61;
                    // vgo.x_new = 432;
                    // vgo.y_new = 354;
                    // conn.p.change_map(conn.p, vgo);
                } else {
                    Service.send_notice_box(conn, "Không trong thời gian diễn ra");
                }
                break;
            }
        }
    }

    private static void Menu_VuaHung_Event_2(Session conn, byte index) throws IOException {
        switch (index) {
            case 0: {
                if (conn.p.minuong == null || !(Math.abs(conn.p.minuong.mob.x - conn.p.x) < 100
                        && Math.abs(conn.p.minuong.mob.x - conn.p.x) < 100)) {
                    Service.send_notice_box(conn, "Ta không thấy mị nương đâu cả");
                } else {
                    Message m2 = new Message(8);
                    m2.writer().writeShort(conn.p.minuong.mob.index);
                    for (int i = 0; i < conn.p.map.players.size(); i++) {
                        conn.p.map.players.get(i).conn.addmsg(m2);
                    }
                    m2.cleanup();
                    BossEvent.MiNuong.remove(conn.p.minuong);
                    conn.p.minuong = null;
                    //
                    if (50 > Util.random(120)) {
                        short[] id_receiv = new short[]{48, 49, 50, 51, 54, 0, 1, 2, 3, 4, 5, 53, 205, 207};
                        Item47 itbag = new Item47();
                        itbag.id = id_receiv[Util.random(id_receiv.length)];
                        itbag.quantity = (short) Util.random(1, 4);
                        itbag.category = 4;
                        conn.p.item.add_item_bag47(4, itbag);
                        Service.send_notice_box(conn, "Nhận được " + ItemTemplate4.item.get(itbag.id).getName());
                    } else {
                        short[] id_receiv = new short[]{0, 1, 2, 3, 8, 9, 10, 11, 12, 13};
                        Item47 itbag = new Item47();
                        itbag.id = id_receiv[Util.random(id_receiv.length)];
                        itbag.quantity = (short) Util.random(1, 4);
                        itbag.category = 7;
                        conn.p.item.add_item_bag47(7, itbag);
                        Service.send_notice_box(conn, "Nhận được " + ItemTemplate7.item.get(itbag.id).getName());
                    }
                    conn.p.item.char_inventory(4);
                    conn.p.item.char_inventory(7);
                    conn.p.item.char_inventory(3);
                }
                break;
            }
            case 1: {
                for (int i = 137; i < 140; i++) {
                    if (conn.p.item.total_item_by_id(4, i) < 50) {
                        Service.send_notice_box(conn, "Không đủ " + 50 + " " + ItemTemplate4.item.get(i).getName());
                        return;
                    }
                }
                if (conn.p.item.total_item_by_id(4, 141) < 50) {
                    Service.send_notice_box(conn, "Không đủ " + 50 + " " + ItemTemplate4.item.get(141).getName());
                    return;
                }
                for (int i = 137; i < 140; i++) {
                    conn.p.item.remove(4, i, 50);
                }
                conn.p.item.remove(4, 141, 1);
                ItemTemplate3 temp3 = ItemTemplate3.item.get(4790);
                Item3 itbag = new Item3();
                itbag.id = 4790;
                itbag.clazz = temp3.getClazz();
                itbag.type = temp3.getType();
                itbag.level = temp3.getLevel();
                itbag.icon = temp3.getIcon();
                itbag.color = temp3.getColor();
                itbag.part = temp3.getPart();
                itbag.islock = true;
                itbag.name = temp3.getName();
                itbag.tier = 0;
                itbag.op = new ArrayList<>();
                for (int i = 0; i < temp3.getOp().size(); i++) {
                    Option op_temp = temp3.getOp().get(i);
                    itbag.op.add(new Option(op_temp.id, op_temp.getParam(0)));
                }
               // itbag.time_use = 60 * 60 * 24 * 30;
                itbag.time_use = 0;
                conn.p.item.add_item_bag3(itbag);
                conn.p.item.char_inventory(4);
                conn.p.item.char_inventory(7);
                conn.p.item.char_inventory(3);
                Service.send_notice_box(conn, "Nhận được " + temp3.getName());
                break;
            }
            case 2: {
                for (int i = 137; i < 140; i++) {
                    if (conn.p.item.total_item_by_id(4, i) < 50) {
                        Service.send_notice_box(conn, "Không đủ " + 50 + " " + ItemTemplate4.item.get(i).getName());
                        return;
                    }
                }
                if (conn.p.item.total_item_by_id(4, 141) < 50) {
                    Service.send_notice_box(conn, "Không đủ " + 50 + " " + ItemTemplate4.item.get(141).getName());
                    return;
                }
                for (int i = 137; i < 140; i++) {
                    conn.p.item.remove(4, i, 50);
                }
                conn.p.item.remove(4, 141, 1);
                ItemTemplate3 temp3 = ItemTemplate3.item.get(4791);
                Item3 itbag = new Item3();
                itbag.id = 4791;
                itbag.clazz = temp3.getClazz();
                itbag.type = temp3.getType();
                itbag.level = temp3.getLevel();
                itbag.icon = temp3.getIcon();
                itbag.color = temp3.getColor();
                itbag.part = temp3.getPart();
                itbag.islock = true;
                itbag.name = temp3.getName();
                itbag.tier = 0;
                itbag.op = new ArrayList<>();
                for (int i = 0; i < temp3.getOp().size(); i++) {
                    Option op_temp = temp3.getOp().get(i);
                    itbag.op.add(new Option(op_temp.id, op_temp.getParam(0)));
                }
                //itbag.time_use = 60 * 60 * 24 * 30;
                itbag.time_use = 0;
          
                conn.p.item.add_item_bag3(itbag);
                conn.p.item.char_inventory(4);
                conn.p.item.char_inventory(7);
                conn.p.item.char_inventory(3);
                Service.send_notice_box(conn, "Nhận được " + temp3.getName());
                break;
            }
            case 3: {
                for (int i = 137; i < 140; i++) {
                    if (conn.p.item.total_item_by_id(4, i) < 50) {
                        Service.send_notice_box(conn, "Không đủ " + 50 + " " + ItemTemplate4.item.get(i).getName());
                        return;
                    }
                }
                if (conn.p.item.total_item_by_id(4, 140) < 50) {
                    Service.send_notice_box(conn, "Không đủ " + 50 + " " + ItemTemplate4.item.get(140).getName());
                    return;
                }
                for (int i = 137; i < 140; i++) {
                    conn.p.item.remove(4, i, 50);
                }
                conn.p.item.remove(4, 140, 20);
                ItemTemplate3 temp3 = ItemTemplate3.item.get(4790);
                Item3 itbag = new Item3();
                itbag.id = 4790;
                itbag.clazz = temp3.getClazz();
                itbag.type = temp3.getType();
                itbag.level = temp3.getLevel();
                itbag.icon = temp3.getIcon();
                itbag.color = temp3.getColor();
                itbag.part = temp3.getPart();
                itbag.islock = true;
                itbag.name = temp3.getName();
                itbag.tier = 0;
                itbag.op = new ArrayList<>();
                for (int i = 0; i < temp3.getOp().size(); i++) {
                    Option op_temp = temp3.getOp().get(i);
                    itbag.op.add(new Option(op_temp.id, ((op_temp.getParam(0) * 15) / 10)));
                }
                //itbag.time_use = 60 * 60 * 24 * 30;
                itbag.time_use = 0;
                
                conn.p.item.add_item_bag3(itbag);
                conn.p.item.char_inventory(4);
                conn.p.item.char_inventory(7);
                conn.p.item.char_inventory(3);
                Service.send_notice_box(conn, "Nhận được " + temp3.getName());
                break;
            }
            case 4: {
                for (int i = 137; i < 140; i++) {
                    if (conn.p.item.total_item_by_id(4, i) < 9) {
                        Service.send_notice_box(conn, "Không đủ " + 50 + " " + ItemTemplate4.item.get(i).getName());
                        return;
                    }
                }
                if (conn.p.item.total_item_by_id(4, 140) < 50) {
                    Service.send_notice_box(conn, "Không đủ " + 50 + " " + ItemTemplate4.item.get(140).getName());
                    return;
                }
                for (int i = 137; i < 140; i++) {
                    conn.p.item.remove(4, i, 50);
                }
                conn.p.item.remove(4, 140, 20);
                ItemTemplate3 temp3 = ItemTemplate3.item.get(4791);
                Item3 itbag = new Item3();
                itbag.id = 4791;
                itbag.clazz = temp3.getClazz();
                itbag.type = temp3.getType();
                itbag.level = temp3.getLevel();
                itbag.icon = temp3.getIcon();
                itbag.color = temp3.getColor();
                itbag.part = temp3.getPart();
                itbag.islock = true;
                itbag.name = temp3.getName();
                itbag.tier = 0;
                itbag.op = new ArrayList<>();
                for (int i = 0; i < temp3.getOp().size(); i++) {
                    Option op_temp = temp3.getOp().get(i);
                    itbag.op.add(new Option(op_temp.id, ((op_temp.getParam(0) * 15) / 10)));
                }
                //itbag.time_use = 60 * 60 * 24 * 30;
                itbag.time_use = 0;
                
                conn.p.item.add_item_bag3(itbag);
                conn.p.item.char_inventory(4);
                conn.p.item.char_inventory(7);
                conn.p.item.char_inventory(3);
                Service.send_notice_box(conn, "Nhận được " + temp3.getName());
                break;
            }
            case 5: {
                boolean spec = false;
                if (conn.p.item.wear[11] != null) {
                    for (Option o : conn.p.item.wear[11].op) {
                        if (o.getParam(0) == 1500) {
                            spec = true;
                            break;
                        }
                    }
                }
                if (spec) { // dawc biet
                    for (int i = 137; i < 140; i++) {
                        if (conn.p.item.total_item_by_id(4, i) < 100) {
                            Service.send_notice_box(conn, "Không đủ 100 " + ItemTemplate4.item.get(i).getName());
                            return;
                        }
                    }
                    if (conn.p.get_ngoc() < 1000) {
                        Service.send_notice_box(conn, "Không đủ 1000 ngọc");
                    }
                    for (int i = 137; i < 140; i++) {
                        conn.p.item.remove(4, i, 50);
                    }
                    conn.p.update_ngoc(-1000);
                    //
                    if (10 == Util.random(1000)) { // skill
                        ItemTemplate3 temp3 = ItemTemplate3.item.get(Util.random(4577, 4585));
                        Item3 itbag = new Item3();
                        itbag.id = temp3.getId();
                        itbag.clazz = temp3.getClazz();
                        itbag.type = temp3.getType();
                        itbag.level = temp3.getLevel();
                        itbag.icon = temp3.getIcon();
                        itbag.color = temp3.getColor();
                        itbag.part = temp3.getPart();
                        itbag.islock = true;
                        itbag.name = temp3.getName();
                        itbag.tier = 0;
                        itbag.op = new ArrayList<>();
                        itbag.time_use = 0;
                        conn.p.item.add_item_bag3(itbag);
                        Service.send_notice_box(conn, "Nhận được " + temp3.getName());
                    } else if (50 > Util.random(120)) { // item 7
                        Item47 itbag = new Item47();
                        itbag.id = (short) Util.random(2, 4);
                        itbag.quantity = (short) Util.random(1, 4);
                        itbag.category = 7;
                        conn.p.item.add_item_bag47(7, itbag);
                        Service.send_notice_box(conn, "Nhận được " + ItemTemplate7.item.get(itbag.id).getName());
                    } else {
                        short[] id_receiv = new short[]{206, 84, 10};
                        Item47 itbag = new Item47();
                        itbag.id = id_receiv[Util.random(id_receiv.length)];
                        itbag.quantity = (short) Util.random(1, 4);
                        itbag.category = 4;
                        conn.p.item.add_item_bag47(4, itbag);
                        Service.send_notice_box(conn, "Nhận được " + ItemTemplate4.item.get(itbag.id).getName());
                    }
                    conn.p.item.char_inventory(4);
                    conn.p.item.char_inventory(7);
                    conn.p.item.char_inventory(3);
                    break;

                } else { // k
                    //

                    for (int i = 137; i < 140; i++) {
                        if (conn.p.item.total_item_by_id(4, i) < 100) {
                            Service.send_notice_box(conn, "Không đủ 100 " + ItemTemplate4.item.get(i).getName());
                            return;
                        }
                    }
                    if (conn.p.get_ngoc() < 1000) {
                        Service.send_notice_box(conn, "Không đủ 1000 ngọc");
                    }
                    for (int i = 137; i < 140; i++) {
                        conn.p.item.remove(4, i, 50);
                    }
                    conn.p.update_ngoc(-1000);
                    //
                    if (10 == Util.random(1000)) { // skill
                        ItemTemplate3 temp3 = ItemTemplate3.item.get(Util.random(4577, 4585));
                        Item3 itbag = new Item3();
                        itbag.id = temp3.getId();
                        itbag.clazz = temp3.getClazz();
                        itbag.type = temp3.getType();
                        itbag.level = temp3.getLevel();
                        itbag.icon = temp3.getIcon();
                        itbag.color = temp3.getColor();
                        itbag.part = temp3.getPart();
                        itbag.islock = true;
                        itbag.name = temp3.getName();
                        itbag.tier = 0;
                        itbag.op = new ArrayList<>();
                        itbag.time_use = 0;
                        conn.p.item.add_item_bag3(itbag);
                        Service.send_notice_box(conn, "Nhận được " + temp3.getName());
                    } else if (50 > Util.random(120)) { // item 7
                        Item47 itbag = new Item47();
                        itbag.id = (short) Util.random(2, 4);
                        itbag.quantity = (short) Util.random(1, 4);
                        itbag.category = 7;
                        conn.p.item.add_item_bag47(7, itbag);
                        Service.send_notice_box(conn, "Nhận được " + ItemTemplate7.item.get(itbag.id).getName());
                    } else {
                        short[] id_receiv = new short[]{206, 84, 10};
                        Item47 itbag = new Item47();
                        itbag.id = id_receiv[Util.random(id_receiv.length)];
                        itbag.quantity = (short) Util.random(1, 4);
                        itbag.category = 4;
                        conn.p.item.add_item_bag47(4, itbag);
                        Service.send_notice_box(conn, "Nhận được " + ItemTemplate4.item.get(itbag.id).getName());
                    }
                    conn.p.item.char_inventory(4);
                    conn.p.item.char_inventory(7);
                    conn.p.item.char_inventory(3);
                    break;
                }
            }
        }
    }

    private static void Menu_Mr_paul(Session conn, byte index) throws IOException {
        switch (index) {
            case 0: { // cua hang thú cưỡi
                Service.send_box_UI(conn, 333);
                break;
            }

        }
    }

    private static void Menu_DoTinhTu(Player p, byte index) throws IOException {
        switch (index) {
            case 0:
            case 1:
            case 2:
            case 3:
            case 4:
            case 5:
            case 6:
            case 7: {
                Message m = new Message(23);
                m.writer().writeUTF("Đồ tinh tú");
                m.writer().writeByte(19);
                m.writer().writeShort(0);
                m.writer().writeByte(p.tinhtu_material.length);
                for (int i = 0; i < p.tinhtu_material.length; i++) {
                    m.writer().writeShort(p.tinhtu_material[i]);
                    m.writer().writeByte(10);
                }
                p.conn.addmsg(m);
                m.cleanup();
                p.id_medal_is_created = (byte) (3 + (p.id_medal_is_created * 8) + (index + 1));
//				System.out.println(p.id_medal_is_created);
                // System.out.println((p.id_medal_is_created - 3) % 8);
                break;
            }
        }
    }
}
