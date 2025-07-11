package client;

import core.BXH;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import org.json.simple.JSONArray;
import core.Log;
import core.SQL;
import core.Service;
import core.Util;
import io.Message;
import io.Session;
import java.sql.PreparedStatement;
import map.Map;
import map.MapService;
import template.Clan_mems;
import template.Item3;
import template.Item47;
import template.Level;
import template.Mob_MoTaiNguyen;
import template.Part_player;

public class Clan {

    public static final short[] item_shop
            = new short[]{19, 20, 146, 159, 160, 161, 163, 228, 229, 230, 231, 232, 233, 234};
    public static List<Clan> entrys = new ArrayList<>();
    public static int[] vang_upgrade = new int[]{0, 1_000_000};
    public static int[] ngoc_upgrade = new int[]{0, 1_000};
    public List<Clan_mems> mems;
    public String name_clan;
    public String name_clan_shorted;
    public short icon;
    public short level;
    public long exp;
    public String slogan;
    public String rule;
    public int kimcuong;
    public long vang;
    public String notice;
    public int max_mem;
    public List<Item47> item_clan;
    public List<Mob_MoTaiNguyen> mo_tai_nguyen;

    public synchronized void add_mo_tai_nguyen(Mob_MoTaiNguyen temp_mob) {
		this.mo_tai_nguyen.add(temp_mob);
	}

	public synchronized void remove_mo_tai_nguyen(Mob_MoTaiNguyen temp_mob) {
		this.mo_tai_nguyen.remove(temp_mob);
	}
    
    public synchronized void clan_process(Session conn, Message m2, int type) throws IOException {
        switch (type) {
            case 21: {
                this.open_box_clan(conn);
                break;
            }
            case 22: {
    if (!conn.p.myclan.mems.get(0).name.equals(conn.p.name)) {
        Service.send_notice_box(conn, "Bạn không phải thủ lĩnh!");
        return;
    }
    this.chiaLuongBang(conn);
    break;
}
            case 4: {
                if (!conn.p.myclan.mems.get(0).name.equals(conn.p.name)) {
                    Service.send_notice_box(conn, "Bạn không phải thủ lĩnh!");
                    return;
                }
                byte mem_type = m2.reader().readByte();
                String name_mem = m2.reader().readUTF();
                for (int i = 0; i < conn.p.myclan.mems.size(); i++) {
                    if (conn.p.myclan.mems.get(i).name.equals(name_mem)) {
                        conn.p.myclan.mems.get(i).mem_type = mem_type;
                        break;
                    }
                }
                String name_mem_type = "";
                switch (mem_type) {
                    case 126: {
                        name_mem_type += "Phó Chỉ Huy";
                        break;
                    }
                    case 125: {
                        name_mem_type += "Đại Hiệp Sĩ";
                        break;
                    }
                    case 124: {
                        name_mem_type += "Hiệp Sĩ Cao Quý";
                        break;
                    }
                    case 123: {
                        name_mem_type += "Hiệp Sĩ Danh Dự";
                        break;
                    }
                    case 122: {
                        name_mem_type += "Thành Viên Mới";
                        break;
                    }
                }
                Service.send_notice_box(conn, "Bổ nhiệm thành công " + name_mem + " thành " + name_mem_type);
                Player p0 = Map.get_player_by_name(name_mem);
                if (p0 != null) {
                    Service.send_notice_box(p0.conn, "Bạn được bổ nhiệm làm " + name_mem_type);
                    MapService.update_in4_2_other_inside(conn.p.map, p0);
                    MapService.send_in4_other_char(p0.map, p0, p0);
                    Service.send_char_main_in4(p0);
                }
                // update list
                this.update_list_mem(conn, name_mem, mem_type);
                break;
            }
            case 18: {
                if (!conn.p.myclan.mems.get(0).name.equals(conn.p.name)) {
                    Service.send_notice_box(conn, "Bạn không phải thủ lĩnh!");
                    return;
                }
                String name = m2.reader().readUTF();
                this.remove_mem(name);
                Service.send_notice_box(conn, "Đuổi cổ " + name + " thành công!");
                Player p0 = Map.get_player_by_name(name);
                if (p0 != null) {
                    Service.send_notice_box(p0.conn, "Bạn bị đá khỏi bang vì lý do quá kém cỏi!");
                    p0.myclan = null;
                    MapService.update_in4_2_other_inside(conn.p.map, p0);
                    MapService.send_in4_other_char(p0.map, p0, p0);
                    Service.send_char_main_in4(p0);
                }
                this.update_list_mem(conn, name, 121);
                break;
            }
            case 13: {
                send_list_mem(conn);
                break;
            }
            case 10: {
                if (this.mems.size() >= this.max_mem) {
                    Service.send_notice_box(conn, "Số lượng thành viên đã đầy!");
                } else {
                    Player p0 = Map.get_player_by_name(m2.reader().readUTF());
                    if (p0 != null) {
                        if (p0.myclan != null) {
                            if (p0.myclan.name_clan.equals(conn.p.myclan.name_clan)) {
                                Service.send_notice_box(conn, "Đối phương đã là thành viên của bang!");
                            } else {
                                Service.send_notice_box(conn, "Đối phương là thành viên của bang khác!");
                            }
                            return;
                        }
                        Message m = new Message(69);
                        m.writer().writeByte(10);
                        m.writer().writeUTF(conn.p.name);
                        p0.conn.addmsg(m);
                        m.cleanup();
                    }
                }
                break;
            }
            case 6: {
                long value = m2.reader().readInt();
                if (value < 0 || value > 2_000_000_000 || ((value + this.vang) > 2_000_000_000L)
                        || value > conn.p.get_vang()) {
                    Service.send_notice_box(conn, "Số nhập vào không hợp lệ");
                    return;
                }
                this.member_contribute_vang(conn, value);
                break;
            }
            case 7: {
                long value = m2.reader().readInt();
                if (value < 0 || value > 2_000_000_000 || ((value + this.kimcuong) > 2_000_000_000L)
                        || value > conn.p.get_ngoc()) {
                    Service.send_notice_box(conn, "Số nhập vào không hợp lệ");
                    return;
                }
                this.member_contribute_ngoc(conn, value);
                break;
            }
            case 14: {
                String name = m2.reader().readUTF();
                Clan_mems p0 = null;
                for (int i = 0; i < mems.size(); i++) {
                    Clan_mems mem = mems.get(i);
                    if (mem.name.equals(name)) {
                        p0 = mem;
                        break;
                    }
                }
                if (p0 != null) {
                    Message m = new Message(69);
                    m.writer().writeByte(14);
                    m.writer().writeUTF(p0.name);
                    m.writer().writeShort(p0.level);
                    m.writer().writeByte(p0.mem_type);
                    m.writer().writeLong(this.get_mem_contribution_vang(p0.name));
                    m.writer().writeInt(this.get_mem_contribution_ngoc(p0.name));
                    conn.addmsg(m);
                    m.cleanup();
                } else {
                    Service.send_notice_box(conn, "Có lỗi xảy ra!");
                }
                break;
            }
            case 2: {
                this.notice = m2.reader().readUTF();
                this.update_in4_clan_box_notice(conn, 2);
                Service.send_notice_box(conn, "Thay đổi thông báo thành công");
                break;
            }
            case 16: {
                this.slogan = m2.reader().readUTF();
                this.update_in4_clan_box_notice(conn, 16);
                Service.send_notice_box(conn, "Thay đổi khẩu hiệu thành công");
                break;
            }
            case 17: {
                this.rule = m2.reader().readUTF();
                this.update_in4_clan_box_notice(conn, 17);
                Service.send_notice_box(conn, "Thay đổi nội quy thành công");
                break;
            }
            case 15: {
                Message m = new Message(69);
                m.writer().writeByte(15);
                if (this.mems.get(0).name.equals(conn.p.name)) {
                    m.writer().writeByte(0);
                } else {
                    m.writer().writeByte(1);
                }
                m.writer().writeByte(0);
                m.writer().writeInt(Clan.entrys.indexOf(this));
                m.writer().writeShort(this.icon);
                m.writer().writeUTF(this.name_clan_shorted);
                m.writer().writeUTF(this.name_clan);
                m.writer().writeShort(this.level);
                m.writer().writeShort(this.get_percent_level());
                if (BXH.BXH_clan.contains(this)) {
                    m.writer().writeShort((BXH.BXH_clan.indexOf(this) + 1)); // index bxh
                } else {
                    m.writer().writeShort(9999); // index bxh
                }
                m.writer().writeShort(this.mems.size()); // mem
                m.writer().writeShort(this.max_mem); // max mem
                m.writer().writeUTF(this.mems.get(0).name);
                m.writer().writeUTF(this.getSlogan()); // slogan
                m.writer().writeUTF(this.getRule()); // noi quy
                m.writer().writeLong(this.vang);
                m.writer().writeInt(this.kimcuong);
                m.writer().writeByte(0); // thanh tich
                conn.addmsg(m);
                m.cleanup();
                break;
            }
            default: { // type 8
                Service.send_notice_box(conn, "Có lỗi xảy ra!");
                break;
            }
        }
    }

    public synchronized void remove_mem(String name) {
        Clan_mems mem = null;
        for (int i = 1; i < this.mems.size(); i++) {
            if (this.mems.get(i).name.equals(name)) {
                mem = this.mems.get(i);
            }
        }
        if (mem != null) {
            this.mems.remove(mem);
        }
    }

    public void member_contribute_ngoc(Session conn, long value) throws IOException {
        conn.p.update_ngoc(-value);
        conn.p.item.char_inventory(5);
        this.kimcuong += value;
        this.update_in4_clan_box_notice(conn, 7);
        this.update_contribution_ngoc(conn.p.name, (int) value);
        Service.send_notice_box(conn, "Đóng góp " + Util.number_format(value) + " ngọc thành công");
    }

    public void member_contribute_vang(Session conn, long value) throws IOException {
        conn.p.update_vang(-value);
        conn.p.item.char_inventory(5);
        this.vang += value;
        this.update_in4_clan_box_notice(conn, 6);
        this.update_contribution_vang(conn.p.name, (int) value);
        Service.send_notice_box(conn, "Đóng góp " + Util.number_format(value) + " vàng thành công");
    }

    private void update_list_mem(Session conn, String name_mem, int mem_type) throws IOException {
        Message m = new Message(69);
        m.writer().writeByte(19);
        m.writer().writeShort(32000);
        m.writer().writeUTF(name_mem);
        m.writer().writeInt(Clan.entrys.indexOf(this));
        m.writer().writeUTF(this.name_clan);
        m.writer().writeUTF(this.name_clan_shorted);
        m.writer().writeShort(this.icon);
        m.writer().writeByte(mem_type);
        conn.addmsg(m);
        m.cleanup();
    }

    private synchronized void send_list_mem(Session conn) throws IOException {
        Message m = new Message(56);
        m.writer().writeByte(4);
        m.writer().writeUTF(this.name_clan);
        m.writer().writeByte(99);
        m.writer().writeInt(0);
        m.writer().writeByte(this.mems.size());
        for (int i = 0; i < this.mems.size(); i++) {
            Clan_mems mem = this.mems.get(i);
            Player p0 = Map.get_player_by_name(mem.name);
            if (p0 != null) {
                mem.head = p0.head;
                mem.eye = p0.eye;
                mem.hair = p0.hair;
                mem.level = p0.level;
                mem.head = p0.head;
                mem.itemwear.clear();
                for (int i1 = 0; i1 < p0.item.wear.length; i1++) {
                    Item3 it = p0.item.wear[i1];
                    if (it != null && (i1 == 0 || i1 == 1 || i1 == 6 || i1 == 7 || i1 == 10)) {
                        Part_player part = new Part_player();
                        part.type = it.type;
                        part.part = it.part;
                        mem.itemwear.add(part);
                    }
                }
            }
            m.writer().writeUTF(mem.name);
            m.writer().writeByte(mem.head);
            m.writer().writeByte(mem.eye);
            m.writer().writeByte(mem.hair);
            m.writer().writeShort(mem.level);
            m.writer().writeByte(mem.itemwear.size());
            for (Part_player it : mem.itemwear) {
                m.writer().writeByte(it.part);
                m.writer().writeByte(it.type);
            }

            if (p0 != null) {
                m.writer().writeByte(1);
            } else {
                m.writer().writeByte(0);
            }
            switch (mem.mem_type) {
                case 127: {
                    m.writer().writeUTF("Thủ lĩnh");
                    break;
                }
                default: { // type 122
                    m.writer().writeUTF("Thành viên mới");
                    break;
                }
            }
            m.writer().writeShort(this.icon);
            m.writer().writeUTF(this.name_clan_shorted);
            m.writer().writeByte(mem.mem_type);
        }
        conn.addmsg(m);
        m.cleanup();
    }

    private synchronized void update_in4_clan_box_notice(Session conn, int type) throws IOException {
        switch (type) {
            case 6:
            case 7: {
                Message m = new Message(69);
                m.writer().writeByte(15);
                m.writer().writeByte(0);
                m.writer().writeByte(1);
                m.writer().writeLong(this.vang);
                m.writer().writeInt(this.kimcuong);
                conn.addmsg(m);
                m.cleanup();
                break;
            }
            case 2:
            case 17: {
                Message m = new Message(69);
                m.writer().writeByte(15);
                m.writer().writeByte(0);
                m.writer().writeByte(2);
                m.writer().writeUTF(this.getRule());
                conn.addmsg(m);
                m.cleanup();
                break;
            }
            case 16: {
                Message m = new Message(69);
                m.writer().writeByte(15);
                m.writer().writeByte(0);
                m.writer().writeByte(3);
                m.writer().writeUTF(this.getSlogan());
                conn.addmsg(m);
                m.cleanup();
                break;
            }
        }
    }

    private synchronized void update_contribution_ngoc(String name, int quant) {
        for (int i = 0; i < mems.size(); i++) {
            Clan_mems temp = mems.get(i);
            if (temp.name.equals(name)) {
                temp.kimcuong += quant;
            }
        }
    }

    private synchronized void update_contribution_vang(String name, int quant) {
        for (int i = 0; i < mems.size(); i++) {
            Clan_mems temp = mems.get(i);
            if (temp.name.equals(name)) {
                temp.vang += quant;
            }
        }
    }

    private synchronized int get_mem_contribution_ngoc(String name) {
        for (int i = 0; i < mems.size(); i++) {
            Clan_mems temp = mems.get(i);
            if (temp.name.equals(name)) {
                return temp.kimcuong;
            }
        }
        return 0;
    }

    private synchronized long get_mem_contribution_vang(String name) {
        for (int i = 0; i < mems.size(); i++) {
            Clan_mems temp = mems.get(i);
            if (temp.name.equals(name)) {
                return temp.vang;
            }
        }
        return 0;
    }

    private synchronized String getRule() {
        String text = "";
        if (this.notice.equals("")) {
            if (this.rule.equals("")) {
                return "";
            }
            return ("@Nội quy: " + this.rule);
        } else {
            if (this.rule.equals("")) {
                text += "\n";
            } else {
                text += "@Nội quy: " + this.rule;
                text += "\n";
            }
            text += "@Thông báo: " + this.notice;
        }
        return text;
    }

    private synchronized String getSlogan() {
        if (this.slogan.equals("")) {
            return "";
        }
        return ("@Khẩu hiệu: " + this.slogan);
    }

    public int get_percent_level() {
        return (int) ((exp * 1000) / Level.entrys.get(level - 1).exp);
    }

    public synchronized static boolean create_clan(Session conn, String name, String name_shorted) throws IOException {
        for (Clan clan : entrys) {
            if (clan.name_clan.equals(name)) {
                Service.send_notice_box(conn, "Tên này đã tồn tại, xin hãy chọn lại!");
                return false;
            }
            if (clan.name_clan_shorted.equals(name_shorted)) {
                Service.send_notice_box(conn, "Tên rút gọn này đã tồn tại, xin hãy chọn lại!");
                return false;
            }
        }
        Clan temp = new Clan();
        temp.mems = new ArrayList<>();
        //
        Clan_mems temp_mem = new Clan_mems();
        temp_mem.name = conn.p.name;
        temp_mem.mem_type = 127; // thu linh
        temp_mem.kimcuong = 0;
        temp_mem.vang = 0;
        temp_mem.head = conn.p.head;
        temp_mem.eye = conn.p.eye;
        temp_mem.hair = conn.p.hair;
        temp_mem.level = conn.p.level;
        temp_mem.itemwear = new ArrayList<>();
        temp.mo_tai_nguyen = new ArrayList<>();
        for (int i = 0; i < conn.p.item.wear.length; i++) {
            if (conn.p.item.wear[i] == null || (i != 0 && i != 1 && i != 6 && i != 7 && i != 10)) {
                continue;
            }
            Part_player temp2 = new Part_player();
            temp2.type = conn.p.item.wear[i].type;
            temp2.part = conn.p.item.wear[i].part;
            temp_mem.itemwear.add(temp2);
        }
        //
        temp.mems.add(temp_mem);
        temp.name_clan = name;
        temp.name_clan_shorted = name_shorted;
        temp.icon = 0;
        temp.level = 1;
        temp.exp = 0;
        temp.slogan = "";
        temp.rule = "";
        temp.notice = "";
        temp.setVang(0);
        temp.setKimcuong(0);
        temp.max_mem = 5;
        temp.item_clan = new ArrayList<>();
        //
        // short[] list_it = new short[] {275, 279, 281, 294, 296, 299, 301};
        // //
        // for (int i = 0; i < list_it.length; i++) {
        // Item47 it = new Item47();
        // it.id = list_it[i];
        // it.quantity = 1;
        // temp.item_clan.add(it);
        // }
        //
        entrys.add(temp);
        conn.p.myclan = temp;
        String query
                = "INSERT INTO `clan` (`name`, `name_short`, `mems`, `item`, `level`, `exp`, `slogan`, `rule`, `notice`, `vang`, `kimcuong`, `max_mem`, `icon`) VALUES ('"
                + name + "', '" + name_shorted + "', '" + Clan.flush_mem_json(temp.mems) + "', '"
                + Clan.flush_item_json(temp.item_clan) + "', " + temp.level + ", " + temp.exp + ", '" + temp.slogan
                + "', '" + temp.rule + "', '" + temp.notice + "', " + temp.vang + ", " + temp.kimcuong + ", "
                + temp.max_mem + ", " + temp.icon + ")";
        try ( Connection connection = SQL.gI().getConnection();  Statement statement = connection.createStatement();) {
            if (statement.executeUpdate(query) > 0) {
                connection.commit();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        MapService.update_in4_2_other_inside(conn.p.map, conn.p);
        Service.send_char_main_in4(conn.p);
        return true;
    }

    @SuppressWarnings("unchecked")
    public static String flush_item_json(List<Item47> item) {
        JSONArray js = new JSONArray();
        for (Item47 temp : item) {
            JSONArray js2 = new JSONArray();
            js2.add(temp.id);
            js2.add(temp.quantity);
            js.add(js2);
        }
        return js.toJSONString();
    }

    @SuppressWarnings("unchecked")
    public synchronized static String flush_mem_json(List<Clan_mems> mems2) {
        JSONArray js = new JSONArray();
        for (Clan_mems temp : mems2) {
            JSONArray js2 = new JSONArray();
            js2.add(temp.name);
            js2.add(temp.mem_type);
            js2.add(temp.kimcuong);
            js2.add(temp.vang);
            js2.add(temp.head);
            js2.add(temp.eye);
            js2.add(temp.hair);
            js2.add(temp.level);
            JSONArray js3 = new JSONArray();
            for (Part_player part : temp.itemwear) {
                JSONArray js4 = new JSONArray();
                js4.add(part.part);
                js4.add(part.type);
                js3.add(js4);
            }
            js2.add(js3);
            js.add(js2);
        }
        return js.toJSONString();
    }

    public synchronized static Clan get_clan_of_player(String name) {
        for (int i = 0; i < entrys.size(); i++) {
            Clan temp = entrys.get(i);
            for (int j = 0; j < temp.mems.size(); j++) {
                Clan_mems temp2 = temp.mems.get(j);
                if (temp2.name.equals(name)) {
                    return temp;
                }
            }
        }
        return null;
    }

    public synchronized byte get_mem_type(String name) {
        for (int i = 0; i < mems.size(); i++) {
            Clan_mems temp = mems.get(i);
            if (temp.name.equals(name)) {
                return temp.mem_type;
            }
        }
        return 121;
    }

    public synchronized static int get_id_clan(Clan myclan) {
        return entrys.indexOf(myclan);
    }

    public synchronized static void set_clan(List<Clan> clan_list) {
        Clan.entrys.addAll(clan_list);
    }
    // public long getVang() {
    // return vang;
    // }

    public synchronized void setVang(long vang) {
        this.vang = vang;
    }
    // public int getKimcuong() {
    // return kimcuong;
    // }

    public synchronized void setKimcuong(int kimcuong) {
        this.kimcuong = kimcuong;
    }

    public synchronized static void flush() {
        List<Clan> list_to_remove = new ArrayList<>();
        String query
                = "UPDATE `clan` SET `level` = ?, `exp` = ?, `slogan` = ?, `rule` = ?, `mems` = ?, `item` = ?, `notice` = ?, `vang` = ?, `kimcuong` = ?, `icon` = ?, `max_mem` = ? WHERE `name` = ?;";
        try {
            Connection conn = SQL.gI().getConnection();
            PreparedStatement ps = conn.prepareStatement(query);
            for (int i = 0; i < Clan.entrys.size(); i++) {
                Clan clan = Clan.entrys.get(i);
                if (clan.mems.size() < 1) {
                    list_to_remove.add(clan);
                    Clan.entrys.remove(clan);
                    i--;
                } else {
                    ps.clearParameters();
                    ps.setInt(1, clan.level);
                    ps.setLong(2, clan.exp);
                    ps.setNString(3, clan.slogan);
                    ps.setNString(4, clan.rule);
                    ps.setNString(5, Clan.flush_mem_json(clan.mems));
                    ps.setNString(6, Clan.flush_item_json(clan.item_clan));
                    ps.setNString(7, clan.notice);
                    ps.setLong(8, clan.vang);
                    ps.setInt(9, clan.kimcuong);
                    ps.setInt(10, clan.icon);
                    ps.setInt(11, clan.max_mem);
                    ps.setNString(12, clan.name_clan);
                    ps.addBatch();
                    if (i % 50 == 0) {
                        ps.executeBatch();
                    }
                }
            }
            ps.executeBatch();
            conn.commit();
            //
            ps.close();
            ps = conn.prepareStatement("DELETE FROM `clan` WHERE `name` = ?;");
            for (int i = 0; i < list_to_remove.size(); i++) {
                Clan clan = list_to_remove.get(i);
                ps.clearParameters();
                ps.setNString(1, clan.name_clan);
                ps.addBatch();
                if (i % 50 == 0) {
                    ps.executeBatch();
                }
            }
            ps.executeBatch();
            conn.commit();
            //
            ps.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public synchronized void accept_mem(Session conn, Player p0) throws IOException {
        for (int i = 0; i < p0.myclan.mems.size(); i++) {
            if (p0.myclan.mems.get(i).name.equals(conn.p.name)) {
                Service.send_notice_box(conn, "Đã ở trong bang rồi!");
                return;
            }
        }
        if (p0.myclan.mems.size() >= p0.myclan.max_mem) {
            Service.send_notice_box(conn, "Số lượng thành viên đã đầy!");
        } else {
            Clan temp = new Clan();
            temp.mems = new ArrayList<>();
            //
            Clan_mems temp_mem = new Clan_mems();
            temp_mem.name = conn.p.name;
            temp_mem.mem_type = 122;
            temp_mem.kimcuong = 0;
            temp_mem.vang = 0;
            temp_mem.head = conn.p.head;
            temp_mem.eye = conn.p.eye;
            temp_mem.hair = conn.p.hair;
            temp_mem.level = conn.p.level;
            temp_mem.itemwear = new ArrayList<>();
            for (int i = 0; i < conn.p.item.wear.length; i++) {
                if (conn.p.item.wear[i] == null || (i != 0 && i != 1 && i != 6 && i != 7 && i != 10)) {
                    continue;
                }
                Part_player temp2 = new Part_player();
                temp2.type = conn.p.item.wear[i].type;
                temp2.part = conn.p.item.wear[i].part;
                temp_mem.itemwear.add(temp2);
            }
            p0.myclan.mems.add(temp_mem);
            //
            conn.p.myclan = p0.myclan;
            MapService.update_in4_2_other_inside(conn.p.map, conn.p);
            Service.send_char_main_in4(conn.p);
            Service.send_notice_box(conn, ("Gia nhập bang " + p0.myclan.name_clan + " thành công"));
            Service.send_notice_box(p0.conn, (conn.p.name + " gia nhập bang của bạn"));
        }
    }

    public void open_box_clan(Session conn) throws IOException {
        Message m = new Message(69);
        m.writer().writeByte(21);
        m.writer().writeByte(3);
        m.writer().writeShort(this.item_clan.size());
        for (Item47 it : this.item_clan) {
            m.writer().writeShort(it.id);
            m.writer().writeShort(it.quantity);
        }
        conn.addmsg(m);
        m.cleanup();
    }

    public synchronized void update_exp(int exp) {
        this.exp += exp;
        if (this.exp > Level.entrys.get(this.level).exp) {
            this.exp = Level.entrys.get(this.level).exp;
        }
    }

    public synchronized long get_vang() {
        return this.vang;
    }

    public synchronized int get_ngoc() {
        return this.kimcuong;
    }

    public synchronized void update_vang(long quant) {
        this.vang += quant;
    }

    public synchronized void update_ngoc(int quant) {
        this.kimcuong += quant;
    }

    public boolean check_id(short id) {
        for (Item47 it : this.item_clan) {
            if (it.id == id && it.quantity > 0) {
                return true;
            }
        }
        return false;
    }

    public synchronized void remove_all_mem() throws IOException {
        while (this.mems.size() > 1) {
            Clan_mems mem = this.mems.get(1);
            this.mems.remove(mem);
            Player p0 = Map.get_player_by_name(mem.name);
            if (p0 != null) {
                p0.myclan = null;
                MapService.update_in4_2_other_inside(p0.map, p0);
                MapService.send_in4_other_char(p0.map, p0, p0);
                Service.send_char_main_in4(p0);
                Service.send_notice_box(p0.conn, "Bang của bạn đã giải tán!");
            }
        }
        this.mems.clear();
    }

    public static List<Clan> get_all_clan() {
        return Clan.entrys;
    }

    public static int get_mem_by_level(short level) {
        int quant = (level / 5) * 5;
        quant += 5;
        return (quant < 45) ? quant : 45;
    }
    public synchronized Mob_MoTaiNguyen get_mo_tai_nguyen(int n2) {
		for (int j = 0; j < this.mo_tai_nguyen.size(); j++) {
			if (this.mo_tai_nguyen.get(j).index == n2) {
				return this.mo_tai_nguyen.get(j);
			}
		}
		return null;
	}
	
	public synchronized void chiaLuongBang(Session conn) throws IOException {
    if (this.mems.size() <= 0) {
        Service.send_notice_box(conn, "Không có thành viên trong bang.");
        return;
    }

    int soTV = this.mems.size();
    long vangMoiNguoi = this.vang / soTV;
    int ngocMoiNguoi = this.kimcuong / soTV;

    if (vangMoiNguoi == 0 && ngocMoiNguoi == 0) {
        Service.send_notice_box(conn, "Quỹ bang không đủ để chia lương.");
        return;
    }

    int dem = 0;
    for (Clan_mems mem : this.mems) {
        Player p = Map.get_player_by_name(mem.name);
        if (p != null) {
            p.update_vang(vangMoiNguoi);
            p.update_ngoc(ngocMoiNguoi);
            p.item.char_inventory(5); // Cập nhật hành trang
            Service.send_notice_box(p.conn, "Bạn nhận được lương bang: " + 
                Util.number_format(vangMoiNguoi) + " vàng và " + 
                Util.number_format(ngocMoiNguoi) + " ngọc.");
            dem++;
        }
    }

    this.vang = 0;
    this.kimcuong = 0;
    this.update_in4_clan_box_notice(conn, 15); // cập nhật box
    Service.send_notice_box(conn, "Đã chia lương bang cho " + dem + " thành viên.");
}
}