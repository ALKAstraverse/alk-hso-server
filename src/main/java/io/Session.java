package io;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;
import org.json.simple.JSONArray;
import org.json.simple.JSONValue;
import client.Clan;
import client.MessageHandler;
import client.Player;
import core.Log;
import core.Manager;
import core.SQL;
import core.SaveData;
import core.ServerManager;
import core.Util;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import map.Map;
import map.MapService;
import template.Medal_Material;
import template.Part_player;

public class Session implements Runnable {

    public static final List<Session> client_entrys = new LinkedList<>();
    public static final HashMap<String, Session> players = new HashMap<>();
    public static final HashMap<String, Long> players_off = new HashMap<>();
    public final Socket socket;
    private DataInputStream dis;
    private DataOutputStream dos;
    private Thread sendd;
    private Thread receiv;
    public boolean connected;
    private final BlockingQueue<Message> list_msg;
    private boolean sendKeyComplete;
    private byte curR;
    private byte curW;
    private final byte[] keys = "PhongVu@".getBytes();
    public String user;
    public String pass;
    public String ip;
    private final MessageHandler controller;
    public Player p;
    public String[] list_char;
    public byte zoomlv;
    public byte status = 1;
    public boolean get_in4;
    public Set<String> moc_nap;

    public Session(Socket socket) {
        this.socket = socket;
        this.list_msg = new LinkedBlockingQueue<Message>();
        this.sendKeyComplete = false;
        this.connected = false;
        this.controller = new MessageHandler(this);
        get_in4 = false;
    }

    public void init() {
        try {
            this.ip = this.socket.getInetAddress().getHostAddress();
            this.dis = new DataInputStream(socket.getInputStream());
            this.dos = new DataOutputStream(socket.getOutputStream());
            this.connected = true;
            this.get_in4 = false;
            this.sendd = new Thread(() -> {
                try {
                    while (connected) {
                        Message m = list_msg.poll(5, TimeUnit.SECONDS);
                        if (m != null) {
                            send_msg(m);
                            m.cleanup();
                        }
                    }
                } catch (InterruptedException e) {
                } catch (IOException e) {
                } finally {
                    // disconnect();
                }
            });
            this.receiv = new Thread(this);
            this.receiv.start();
            this.sendd.start();
            synchronized (Session.client_entrys) {
                Session.client_entrys.add(this);
                System.out.println("accecpt ip " + ip + " - online : " + Session.client_entrys.size());
            }
        } catch (IOException e) {
            e.printStackTrace();
            this.connected = false;
        }
    }

    private void disconnect() {
        Manager.gI().time_login_client.put(this.user, (System.currentTimeMillis() + Manager.gI().time_login));
        //
        if (this.p != null) {
            try {
                if (p.party != null && p.party.get_mems().size() > 1) {
                    p.party.remove_mems(p);
                    p.party.sendin4();
                    p.party.send_txt_notice(p.name + " rời nhóm");
                    p.party = null;
                }
                if (p.name_trade != null && !p.name_trade.equals("")) {
                    Player p0;
                    p0 = Map.get_player_by_name(p.name_trade);
                    if (p0 != null) {
                        Message m = new Message(36);
                        m.writer().writeByte(6);
                        p0.conn.addmsg(m);
                        m.cleanup();
                        p0.name_trade = "";
                        p0.lock_trade = false;
                        p0.money_trade = 0;
                        p0.accept_trade = false;
                        p0.list_item_trade = null;
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            p.flush();
            if (p.map.ld == null || (p.map.ld != null && p.map.ld.p1.id != p.id && p.map.ld.p2.id != p.id)) {
                MapService.leave(p.map, p);
            }
            //

        }
        //
        this.connected = false;
        this.sendd.interrupt();
        this.receiv.interrupt();
        try {
            if (!this.socket.isClosed()) {
                this.socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        synchronized (Session.client_entrys) {
            if (this.p != null && Session.players.containsKey(this.user)) {
                Session.players.remove(this.user);
            }
            Session.client_entrys.remove(this);
            System.out.println("disconnect session " + user + " - online : " + Session.client_entrys.size());
        }
    }

    public void addmsg(Message m) {
        if (connected) {
            this.list_msg.add(m);
        }
    }

    @Override
    public void run() {
        try {
            while (connected) {
                Message m = read_msg();
                if (m != null) {
                    if (this.ip.equals("127.0.0.1") && m.cmd == -1) {
                        ServerManager.gI().close();
                        System.out.println("Close server is processing....");
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                // flush clan
//                        Clan.flush();
                                SaveData.process();
                                // kick player
                                // while (Session.client_entrys.size() > 0) {
                                // synchronized (Session.client_entrys) {
                                for (int k = Session.client_entrys.size() - 1; k >= 0; k--) {
                                    Session.client_entrys.get(k).p = null;
                                    try {
                                        Session.client_entrys.get(k).close();
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                                // }
                                // }
                                // stop service
                                Manager.gI().close();
                            }
                        }).start();
                    } else if (m.cmd == -40) {
                        sendkeys();
                    } else if (sendKeyComplete
                            && (this.p != null || m.cmd == 61 || m.cmd == 1 || m.cmd == 14 || m.cmd == 13)) {
                        try {
                            controller.process_msg(m);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    m.cleanup();
                }
            }
        } catch (IOException e) {
        } finally {
            if (!this.socket.isClosed()) {
                disconnect();
            }
        }
    }

    private void send_msg(Message msg) throws IOException {
        byte[] data = msg.getData();
        if (msg.cmd == 25) {
            msg.cmd = 126;
        }
        if (sendKeyComplete) {
            byte b = writeKey(msg.cmd);
            dos.writeByte(b);
        } else {
            dos.writeByte(msg.cmd);
        }
        if (data != null) {
            int size = data.length;
            if (msg.cmd == -51 || msg.cmd == -52 || msg.cmd == -54 || msg.cmd == 126) {
                if (msg.cmd == 126) {
                    byte bspec = writeKey((byte) 25);
                    dos.writeByte(bspec);
                }
                byte b4 = (byte) (size);
                byte b3 = (byte) ((byte) (size >> 8));
                byte b2 = (byte) ((byte) (size >> 16));
                byte b1 = (byte) ((byte) (size >> 24));
                final int byte4 = this.writeKey(b4);
                final int byte3 = this.writeKey(b3);
                final int byte2 = this.writeKey(b2);
                final int byte1 = this.writeKey(b1);
                this.dos.writeByte(byte1);
                this.dos.writeByte(byte2);
                this.dos.writeByte(byte3);
                this.dos.writeByte(byte4);
            } else if (sendKeyComplete) {
                int byte1 = writeKey((byte) (size >> 8));
                dos.writeByte(byte1);
                int byte2 = writeKey((byte) (size));
                dos.writeByte(byte2);
            } else {
                final int byte1 = (byte) (size & 0xFF00);
                this.dos.writeByte(byte1);
                final int byte2 = (byte) (size & 0xFF);
                this.dos.writeByte(byte2);
            }
            if (sendKeyComplete) {
                for (int i = 0; i < data.length; i++) {
                    data[i] = writeKey(data[i]);
                }
            }
            dos.write(data);
        } else {
            final int byte1 = (byte) (0 & 0xFF00);
            this.dos.writeByte(byte1);
            final int byte2 = (byte) (0 & 0xFF);
            this.dos.writeByte(byte2);
        }
        dos.flush();
        msg.cleanup();
        Util.logconsole("___send msg : " + msg.cmd + " - size : " + data.length + " : " + user, 1, msg.cmd);
    }

    private Message read_msg() throws IOException {
        byte cmd = dis.readByte();
        if (sendKeyComplete) {
            cmd = readKey(cmd);
        }
        int size;
        if (sendKeyComplete) {
            byte b1 = dis.readByte();
            byte b2 = dis.readByte();
            size = (readKey(b1) & 255) << 8 | readKey(b2) & 255;
        } else {
            size = dis.readShort();
        }
        byte data[] = new byte[size];
        int len = 0;
        int byteRead = 0;
        while (len != -1 && byteRead < size) {
            len = dis.read(data, byteRead, size - byteRead);
            if (len > 0) {
                byteRead += len;
            }
        }
        if (sendKeyComplete) {
            for (int i = 0; i < data.length; i++) {
                data[i] = readKey(data[i]);
            }
        }
        Util.logconsole("Read msg : " + cmd + " - size : " + data.length, 0, cmd);
        return new Message(cmd, data);
    }

    private byte readKey(final byte b) {
        final byte curR = this.curR;
        this.curR = (byte) (curR + 1);
        final byte i = (byte) ((keys[curR] & 0xFF) ^ (b & 0xFF));
        if (this.curR >= keys.length) {
            this.curR %= (byte) keys.length;
        }
        return i;
    }

    private byte writeKey(final byte b) {
        final byte curW = this.curW;
        this.curW = (byte) (curW + 1);
        final byte i = (byte) ((keys[curW] & 0xFF) ^ (b & 0xFF));
        if (this.curW >= keys.length) {
            this.curW %= (byte) keys.length;
        }
        return i;
    }

    private void sendkeys() throws IOException {
        Message m = new Message(-40);
        m.writer().writeByte(keys.length);
        m.writer().writeByte(keys[0]);
        for (int i = 1; i < keys.length; i++) {
            m.writer().writeByte(keys[i] ^ keys[i - 1]);
        }
        send_msg(m);
        m.cleanup();
        sendKeyComplete = true;
    }

    public void getclientin4(Message m) throws IOException {
        String user_ = m.reader().readUTF();
        String pass_ = m.reader().readUTF();
        moc_nap = new HashSet<>();
        
        
        // chỉ admin vào được sv
//        if (!user_.equals("admin")) {
//            return;
//        }


        m.reader().readUTF(); // version
        m.reader().readUTF(); // clinePro
        m.reader().readUTF(); // pro
        m.reader().readUTF(); // agent
        this.zoomlv = m.reader().readByte();
        m.reader().readByte(); // device
        m.reader().readInt(); // id
        m.reader().readByte(); // area
        m.reader().readByte(); // !Main.isPC ? 0 : 1
        m.reader().readByte(); // IndexRes
        m.reader().readByte(); // indexInfoLogin
        m.reader().readByte(); // fake byte
        short indexCharPar = m.reader().readShort();
        m.reader().readUTF(); // stringPackageName
        //
        Pattern p = Pattern.compile("^[a-zA-Z0-9@.]{1,15}$");
        if ((!user_.contains("knightauto_hod_")) && (!p.matcher(user_).matches() || !p.matcher(pass_).matches())) {
            noticelogin("ký tự nhập vào không hợp lệ!!");
            return;
        }
        long time_can_login = 0;
        if (Manager.gI().time_login_client.containsKey(user_)) {
            time_can_login = Manager.gI().time_login_client.get(user_) - System.currentTimeMillis();
        }
        if (!user_.equals("admin") && time_can_login > 0) {
            float t_ = ((float) time_can_login) / 1000f;
            noticelogin("sau " + String.format("%.1f", t_) + "s nữa mới có thể vào!");
            return;
        }
        int dem = 0;
        for (int i = 0; i < Session.client_entrys.size(); i++) {
            if (Session.client_entrys.get(i).ip.equals(this.ip)) {
                dem++;
            }
        }
        if (dem > Manager.gI().allow_ip_client) {
            noticelogin("Vượt quá số lượng ip có thể truy cập vào thời điểm này!");
            return;
        }
        if (pass_.equals("1") && user_.equals("1")) {
            user = "knightauto_hsod_" + String.valueOf(System.nanoTime());
            pass = "hsr_132";

            try ( Connection connnect = SQL.gI().getConnection();  Statement ps = connnect.createStatement()) {
                if (!ps.execute("INSERT INTO `account` (`user`, `pass`, `char`,  `lock`, `coin`, `ip`, `tongnap`) VALUES ('" + user + "', '" + pass + "', '[]', '0', '0','0', 0)")) {
                    connnect.commit();
                }
            } catch (SQLException e) {
                e.printStackTrace();
                noticelogin("Có lỗi xảy ra, hãy thử lại!");
                return;
            }
            this.list_char = new String[3];
            for (int i = 0; i < 3; i++) {
                this.list_char[i] = "";
            }
        } else {
            //
            String query = "SELECT * FROM `account` WHERE `user` = '" + user_ + "' AND `pass` = '" + pass_ + "' LIMIT 1;";
            try ( Connection conn = SQL.gI().getConnection();  Statement ps = conn.createStatement();  ResultSet rs = ps.executeQuery(query)) {
                if (!rs.next()) {
                    noticelogin("Sai cmn pass rồi");
                    return;
                }
                this.status = rs.getByte("status");
                System.out.println(this.status);
                if (rs.getByte("lock") == 1) {
                    noticelogin("Tài khoản đã bị khóa vì có hành vi xấu ảnh hưởng server");
                    return;
                }
                JSONArray jsar = (JSONArray) JSONValue.parse(rs.getString("char"));
                if (jsar == null) {
                    noticelogin("Có lỗi xảy ra, hãy thử lại!");
                    return;
                }
                this.list_char = new String[3];
                for (int i = 0; i < 3; i++) {
                    this.list_char[i] = "";
                }
                for (int i = 0; i < jsar.size(); i++) {
                    this.list_char[i] = jsar.get(i).toString();
                }
                jsar.clear();
                String moc_nap_ = rs.getString("checkmocnap");
                if (moc_nap_ != null && !moc_nap_.isEmpty()) {
                    jsar = (JSONArray) JSONValue.parse(moc_nap_);
                    for (int i = 0; i < jsar.size(); i++) {
                        moc_nap.add(jsar.get(i).toString());
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
                noticelogin("Có lỗi xảy ra, hãy thử lại!");
                return;
            }
            this.user = user_;
            this.pass = pass_;
        }
        if (indexCharPar != Manager.gI().indexCharPar) {
            Message m13 = new Message(63);
            m13.writer().writeByte(Manager.gI().indexRes);
            addmsg(m13);
            m13.cleanup();
            //
            send_char_part();
        } else {
            send_listchar_board();
        }
        //
        Message md = new Message(31);
        md.writer().writeUTF(user_);
        md.writer().writeUTF(pass_);
        addmsg(md);
        md.cleanup();
        //
        for (int id0 = 10200; id0 < 10242; id0++) {
            Message m22 = new Message(-51);
            m22.writer().writeShort(id0);
            m22.writer().write(Util.loadfile("data/icon/x" + zoomlv + "/" + id0 + ".png"));
            addmsg(m22);
            m22.cleanup();
        }
        this.get_in4 = true;
    }

    public void send_char_part() {
        new Thread(() -> {
            try {
                Message m = new Message(-57);
                m.writer().writeShort(Manager.gI().indexCharPar);
                m.writer().writeShort(966);
                addmsg(m);
                m.cleanup();
                //
                for (int i = 0; i < 966; i++) {
                    m = new Message(-52);
                    switch (zoomlv) {
                        case 1: {
                            m.writer().write(Manager.gI().data_part_char_x1[i]);
                            break;
                        }
                        case 2: {
                            m.writer().write(Manager.gI().data_part_char_x2[i]);
                            break;
                        }
                        case 3: {
                            m.writer().write(Manager.gI().data_part_char_x3[i]);
                            break;
                        }
                        case 4: {
                            m.writer().write(Manager.gI().data_part_char_x4[i]);
                            break;
                        }
                        default: {
                            return;
                        }
                    }
                    addmsg(m);
                    m.cleanup();
                }
                send_listchar_board();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void noticelogin(String s) throws IOException {
        Message m = new Message(2);
        m.writer().writeUTF(s);
        m.writer().writeByte(0);
        addmsg(m);
        m.cleanup();
    }

    public void send_listchar_board() throws IOException {
        //
        if (this.list_char == null) {
            this.disconnect();
            return;
        }
        String name_querry = String.format("`name` = '%s' OR `name` = '%s' OR `name` = '%s'", this.list_char[0],
                this.list_char[1], this.list_char[2]);
        try ( Connection connection = SQL.gI().getConnection();  Statement ps = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);  ResultSet rs
                = ps.executeQuery("SELECT * FROM `player` WHERE " + name_querry + " ORDER BY `id` DESC LIMIT 3;");) {
            rs.last();
            int row = rs.getRow();
            rs.afterLast();
            Message m = new Message(13);
            m.writer().writeByte(row); // list char size
            while (rs.previous()) {
                m.writer().writeInt(rs.getShort("id"));
                String name = rs.getString("name");
                m.writer().writeUTF(name);
                JSONArray jsar = (JSONArray) JSONValue.parse(rs.getString("body"));
                if (jsar == null) {
                    return;
                }
                m.writer().writeByte(Byte.parseByte(jsar.get(0).toString())); // head
                m.writer().writeByte(Byte.parseByte(jsar.get(2).toString())); // hair
                m.writer().writeByte(Byte.parseByte(jsar.get(1).toString())); // eye
                //
                jsar.clear();
                List<Part_player> itemwear = new ArrayList<>();
                jsar = (JSONArray) JSONValue.parse(rs.getString("itemwear"));
                if (jsar == null) {
                    return;
                }
                for (int i3 = 0; i3 < jsar.size(); i3++) {
                    JSONArray jsar2 = (JSONArray) JSONValue.parse(jsar.get(i3).toString());
                    if (jsar2 == null) {
                        return;
                    }
                    byte index_wear = Byte.parseByte(jsar2.get(9).toString());
                    if (index_wear != 0 && index_wear != 1 && index_wear != 6 && index_wear != 7 && index_wear != 10) {
                        continue;
                    }
                    Part_player temp = new Part_player();
                    temp.type = Byte.parseByte(jsar2.get(2).toString());
                    temp.part = Byte.parseByte(jsar2.get(6).toString());
                    itemwear.add(temp);
                }
                jsar.clear();
                m.writer().writeByte(itemwear.size()); // size part
                for (int j = 0; j < itemwear.size(); j++) {
                    m.writer().writeByte(itemwear.get(j).type);
                    m.writer().writeByte(itemwear.get(j).part);
                }
                short level_ = rs.getShort("level");
                if (level_ > Manager.gI().lvmax) {
                    level_ = (short) Manager.gI().lvmax;
                }
                m.writer().writeShort(level_);
                m.writer().writeByte(Byte.parseByte(rs.getString("clazz")));
                m.writer().writeByte(1); // fake
                m.writer().writeByte(0); // fake
                Clan clan = Clan.get_clan_of_player(name);
                if (clan != null) {
                    m.writer().writeShort(clan.icon);
                    m.writer().writeUTF(clan.name_clan_shorted);
                    m.writer().writeByte(clan.get_mem_type(name));
                } else {
                    m.writer().writeShort(-1); // clan in4
                }
            }
            addmsg(m);
            m.cleanup();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    public void char_create(Message m) throws IOException {
        if (!Manager.gI().ip_create_char.containsKey(this.ip)) {
            Manager.gI().ip_create_char.put(this.ip, 0);
        }
        int time_ = Manager.gI().ip_create_char.get(this.ip);
        if (time_ > 2) {
            notice_create_char("Đã quá lượt tạo nhân vật hôm nay!!");
            // return;
        }
        if (this.list_char == null || !this.list_char[2].isEmpty()) {
            return;
        }
        byte clazz = m.reader().readByte();
        String name = m.reader().readUTF().toLowerCase();
        byte hair = m.reader().readByte();
        byte eye = m.reader().readByte();
        byte head = m.reader().readByte();
        //
        Pattern p = Pattern.compile("^[a-zA-Z0-9]{6,10}$");
        if (!p.matcher(name).matches()) {
            notice_create_char("tên không hợp lệ, nhập lại đi!!");
            return;
        }
        try ( Connection connnect = SQL.gI().getConnection();  PreparedStatement ps = connnect.prepareStatement(
                "INSERT INTO `player` (`name`, `body`, `clazz`, `level`, `exp`, `site`, `item4`, `item7`, `vang`, `kimcuong`, `tiemnang`,"
                        + " `kynang`, `point1`, `point2`, `point3`, `point4`, `skill`, `item3`, `item5`,`itemwear`,`giftcode`,"
                        + " `pet`, `maxbag`, `itembox3`, `itembox4`, `itembox7`, `rms_save`, `date`, `diemdanh`, `eff`,"
                        + " `itembox5`, `friend`, `enemies`, `typeexp`, `medal_create_material`,`point_active`,`tutien`,`dquest`,`tinhtu_material`, `nvtt`)"
                        + " VALUES (?,?,?,?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,?);")) {
            ps.setNString(1, name);
            
            JSONArray jsar = new JSONArray();
            jsar.add(head);
            jsar.add(eye);
            jsar.add(hair);
            ps.setNString(2, jsar.toJSONString());
            jsar.clear();
            ps.setByte(3, clazz);
            ps.setShort(4, (short) 1); // level
            ps.setLong(5, 0L); // exp
            ps.setNString(6, "[0,132,354]");
            ps.setNString(7, "[[11,1],[14,1]]");
            ps.setNString(8, "[]");
            ps.setLong(9, 3000L);
            ps.setInt(10, 3000);
            ps.setShort(11, (short) 5);
            ps.setShort(12, (short) 1);
            ps.setShort(13, (short) 5);
            ps.setShort(14, (short) 5);
            ps.setShort(15, (short) 5);
            ps.setShort(16, (short) 5);
            jsar.add(1);
            for (int i = 0; i < 20; i++) {
                jsar.add(0);
            }
            ps.setNString(17, jsar.toJSONString());
            ps.setNString(18, "[]");
            ps.setNString(19, "[]");
            switch (clazz) {
                case 0: {
                    ps.setNString(20,
                            "[[0,0,8,1,0,0,0,0,[[0,54],[40,120]],0],[80,0,0,1,16,0,0,0,[[14,52],[16,100]],1],[120,0,1,1,24,0,0,0,[[14,18],[25,3]],7]]");
                    break;
                }
                case 1: {
                    ps.setNString(20,
                            "[[5,1,9,1,1,0,0,0,[[0,54],[40,120]],0],[105,1,0,1,21,0,1,0,[[14,52],[20,100]],1],[145,1,1,1,29,0,1,0,[[14,18],[24,3]],7]]");
                    break;
                }
                case 2: {
                    ps.setNString(20,
                            "[[10,2,11,1,2,0,0,0,[[0,50],[40,120]],0],[90,2,0,1,18,0,2,0,[[14,42],[16,200]],1],[50,2,2,1,10,0,2,0,[[7,200],[14,12]],6],[130,2,1,1,26,0,2,0,[[14,12],[26,4]],7]]");
                    break;
                }
                default: {
                    ps.setNString(20,
                            "[[15,3,10,1,3,0,0,0,[[0,50],[40,120]],0],[95,3,0,1,19,0,3,0,[[14,44],[16,200]],1],[55,3,2,1,11,0,3,0,[[7,200],[14,14]],6],[135,3,1,1,27,0,3,0,[[14,14],[24,4]],7]]");
                    break;
                }
            }
            ps.setNString(21, "[]");
            ps.setNString(22, "[]");
            ps.setByte(23, (byte) 42);
            ps.setNString(24, "[]");
            ps.setNString(25, "[]");
            ps.setNString(26, "[]");
            ps.setNString(27, "[[],[]]");
            ps.setNString(28, Date.from(Instant.now()).toString());
            ps.setByte(29, (byte) 1);
            ps.setNString(30, "[]");
            ps.setNString(31, "[]");
            ps.setNString(32, "[]");
            ps.setNString(33, "[]");
            ps.setByte(34, (byte) 1);
            ps.setNString(35, "[295,261,318,328,341,249,285,321,329,344,284,280,316,327,344,288,280,317,327,342]");
            ps.setNString(36, "[3,0]");
            ps.setNString(37, "[0,1,1,1,1,0]");
            ps.setNString(38, "[-1,-1,0,0,0,20]");
            	ps.setNString(39,
			      String.format("[453,455,%s,%s,459]", Medal_Material.m_yellow[0], Medal_Material.m_violet[0]));
			ps.setNString(40, "[-1,0,0,0]");
            jsar.clear();
            if (!ps.execute()) {
                connnect.commit();
            }
            //
            for (int i = 0; i < this.list_char.length; i++) {
                if (this.list_char[i].isEmpty()) {
                    this.list_char[i] = name;
                    break;
                }
            }
            Manager.gI().ip_create_char.replace(this.ip, time_, (time_ + 1));
            send_listchar_board();
            flush();
        } catch (SQLException e) {
            notice_create_char("Tên này đã sử dụng, hãy thử lại");
            e.printStackTrace();
        }
    }

    private void flush() {
        if (list_char != null) {
            String query = "[";
            for (int i = 0; i < list_char.length; i++) {
                if (!this.list_char[i].isEmpty()) {
                    query += "\"" + this.list_char[i] + "\",";
                }
            }
            query = query.substring(0, query.length() - 1);
            query += "]";
            try ( Connection conn = SQL.gI().getConnection();  Statement statement = conn.createStatement()) {
                if (statement.executeUpdate(
                        "UPDATE `account` SET `char` = '" + query + "' WHERE `user` = '" + this.user + "';") > 0) {
                    conn.commit();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private void notice_create_char(String s) throws IOException {
        Message m = new Message(37);
        m.writer().writeUTF(s);
        m.writer().writeUTF("");
        m.writer().writeByte(0);
        addmsg(m);
        m.cleanup();
    }

    public void close() throws IOException {
        this.disconnect();
    }

    public void save_moc_nap() {
        JSONArray js = new JSONArray();
        moc_nap.forEach(s -> {
            js.add(s);
        });
        try ( Connection conn = SQL.gI().getConnection();  Statement statement = conn.createStatement()) {
            if (statement.executeUpdate(
                    "UPDATE `account` SET `checkmocnap` = '" + js.toJSONString() + "' WHERE `user` = '" + this.user + "';") > 0) {
                conn.commit();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
