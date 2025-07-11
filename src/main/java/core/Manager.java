package core;

import ai.BossTG;
import ai.NhanBan;
import event.*;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.json.simple.JSONArray;
import org.json.simple.JSONValue;
import client.Clan;
import event_daily.ChiemMo;
import event_daily.ChienTruong;
import event_daily.Wedding;
import gamble.VXMM;
import io.Message;
import map.LeaveItemMap;
import map.Map;
import map.Mob_in_map;
import map.Vgo;
import org.json.simple.JSONObject;
import template.Clan_mems;
import template.Item3;
import template.Item47;
import template.ItemSell3;
import template.ItemTemplate3;
import template.ItemTemplate4;
import template.ItemTemplate7;
import template.Level;
import template.Medal_Material;
import template.Mob;
import template.Option;
import template.OptionItem;
import template.Part_fashion;
import template.Part_player;
import gamble.TaiXiu;
import java.util.logging.Logger;

public class Manager {

    public int size_mob;
    private static Manager instance;
    public final HashMap<String, Integer> ip_create_char = new HashMap<>();
    public final HashMap<String, Long> time_login_client = new HashMap<>();
    public final byte[] msg_25_new;
    public final byte[] msg_29_chienbinh;
    public final byte[] msg_29_satthu;
    public final byte[] msg_29_phapsu;
    public final byte[] msg_29_xathu;
    public final byte[][] data_part_char_x1;
    public final byte[][] data_part_char_x2;
    public final byte[][] data_part_char_x3;
    public final byte[][] data_part_char_x4;
    public final byte[] msg_1;
    public final byte[] msg_61;
    public final byte[] msg_26;
    public boolean debug;
    public String mysql_host;
    public String mysql_database;
    public String mysql_user;
    public String mysql_pass;
    public int event;
    public int server_port;
    public byte indexRes;
    public int indexCharPar;
    public int exp;
    public int lvmax;
    public int allow_ip_client;
    public int time_login;
    public List<ItemSell3[]> itemsellTB;
    public short[] itempoitionsell;
    public short[] item7sell;
    public VXMM vxmm;
    public int size_mob_now = -20;
    public ChiemMo chiem_mo;
    public List<NhanBan> list_nhanban;
    public BossTG bossTG;
    public TaiXiu tx;

    public Manager() throws IOException {
        this.msg_25_new = Util.loadfile("data/msg/msg_25_new");
        this.msg_29_chienbinh = Util.loadfile("data/msg/msg_29_chienbinh");
        this.msg_29_satthu = Util.loadfile("data/msg/msg_29_satthu");
        this.msg_29_phapsu = Util.loadfile("data/msg/msg_29_phapsu");
        this.msg_29_xathu = Util.loadfile("data/msg/msg_29_xathu");
        this.msg_1 = Util.loadfile("data/msg/msg_1");
        this.msg_61 = Util.loadfile("data/msg/msg_61");
        this.msg_26 = Util.loadfile("data/msg/msg_26");
        //
        data_part_char_x1 = new byte[966][];
        data_part_char_x2 = new byte[966][];
        data_part_char_x3 = new byte[966][];
        data_part_char_x4 = new byte[966][];
        for (int i = 0; i < 966; i++) {
            data_part_char_x1[i] = Util.loadfile("data/part_char/x1/" + i + "_msg_-52");
            data_part_char_x2[i] = Util.loadfile("data/part_char/x2/" + i);
            data_part_char_x3[i] = Util.loadfile("data/part_char/x3/" + i);
            data_part_char_x4[i] = Util.loadfile("data/part_char/x4/" + i);
        }
    }

    public static Manager gI() {
        if (instance == null) {
            try {
                instance = new Manager();
            } catch (IOException e) {
                e.printStackTrace();
                System.err.println("create cache fail!");
                System.exit(0);
            }
        }
        return instance;
    }

    public void init() {
        try {
            load_config();
            if (!load_database()) {
                System.err.println("load database err");
                System.exit(0);
                return;
            }
            list_nhanban = new ArrayList<>();
            chiem_mo = new ChiemMo();
            chiem_mo.init();
            System.out.println("cache loaded!");
            this.vxmm = new VXMM();
            Log.gI().start_log();
            	bossTG = new BossTG();
			bossTG.start_boss_tg();
                        tx = new TaiXiu();
			tx.start_service();
            for (Map[] temp : Map.entrys) {
                for (Map temp2 : temp) {
                    temp2.start_map();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("load database err");
            System.exit(0);
        }
    }

    private boolean load_database() throws SQLException {
        // load item3
        Connection conn = SQL.gI().getConnection();
        Statement ps = conn.createStatement();
        ResultSet rs;
        String query = "SELECT * FROM `item3`;";
        rs = ps.executeQuery(query);
        while (rs.next()) {
            ItemTemplate3 temp = new ItemTemplate3();
            temp.setId(rs.getShort("id"));
            temp.setName(rs.getString("name"));
            temp.setType(rs.getByte("type"));
            temp.setPart(rs.getByte("part"));
            temp.setClazz(rs.getByte("clazz"));
            temp.setIcon(rs.getShort("iconid"));
            temp.setLevel(rs.getShort("level"));
            temp.setColor(rs.getByte("color"));
            JSONArray jsar = (JSONArray) JSONValue.parse(rs.getString("data"));
            if (jsar == null) {
                return false;
            }
            List<Option> buffer = new ArrayList<>();
            for (int i = 0; i < jsar.size(); i++) {
                JSONArray jsar2 = (JSONArray) JSONValue.parse(jsar.get(i).toString());
                if (jsar2 == null) {
                    return false;
                }
                buffer.add(new Option(Byte.parseByte(jsar2.get(0).toString()), Integer.parseInt(jsar2.get(1).toString())));
            }
            temp.setOp(buffer);
            // set type leave
            if (temp.getType() < 12 && temp.getType() != 7) {
                // load item leave
                switch (temp.getLevel()) {
                    case 1: {
                        LeaveItemMap.item0x.add(temp.getId());
                        break;
                    }
                    case 10: {
                        LeaveItemMap.item1x.add(temp.getId());
                        break;
                    }
                    case 20: {
                        LeaveItemMap.item2x.add(temp.getId());
                        break;
                    }
                    case 30: {
                        LeaveItemMap.item3x.add(temp.getId());
                        break;
                    }
                    case 40: {
                        LeaveItemMap.item4x.add(temp.getId());
                        break;
                    }
                    case 50: {
                        LeaveItemMap.item5x.add(temp.getId());
                        break;
                    }
                    case 60: {
                        LeaveItemMap.item6x.add(temp.getId());
                        break;
                    }
                    case 70: {
                        LeaveItemMap.item7x.add(temp.getId());
                        break;
                    }
                    case 80: {
                        LeaveItemMap.item8x.add(temp.getId());
                        break;
                    }
                    case 90: {
                        LeaveItemMap.item9x.add(temp.getId());
                        break;
                    }
                    case 100: {
                        LeaveItemMap.item10x.add(temp.getId());
                        break;
                    }
                    case 110: {
                        LeaveItemMap.item11x.add(temp.getId());
                        break;
                    }
                    case 120: {
                        LeaveItemMap.item12x.add(temp.getId());
                        break;
                    }
                    case 130: {
                        LeaveItemMap.item13x.add(temp.getId());
                        break;
                    }
                }
            }
            if (temp.getColor() == 2) {
                temp.setLevel((short) (temp.getLevel() + 2));
            } else if (temp.getColor() == 3) {
                temp.setLevel((short) (temp.getLevel() + 4));
            } else if (temp.getColor() == 4) {
                temp.setLevel((short) (temp.getLevel() + 5));
            }
            ItemTemplate3.item.add(temp);
        }
        rs.close();
        // load item4
        query = "SELECT * FROM `item4`;";
        rs = ps.executeQuery(query);
        while (rs.next()) {
            ItemTemplate4 temp = new ItemTemplate4();
            temp.setId(rs.getShort("id"));
            temp.setIcon(rs.getShort("icon"));
            if (temp.getId() >= 113 && temp.getId() <= 116) {
                temp.setPrice(50);
            } else {
                temp.setPrice(rs.getLong("price"));
            }

            temp.setName(rs.getString("name"));
            temp.setContent(rs.getString("content"));
            temp.setType(rs.getByte("typepotion"));
            temp.setPricetype(rs.getByte("moneytype"));
            temp.setSell(rs.getByte("sell"));
            temp.setValue(rs.getShort("value"));
            temp.setTrade(rs.getByte("canTrade"));
            ItemTemplate4.item.add(temp);
        }
        rs.close();
        // load item7
        query = "SELECT * FROM `item7`;";
        rs = ps.executeQuery(query);
        while (rs.next()) {
            ItemTemplate7 temp = new ItemTemplate7();
            temp.setId(rs.getShort("id"));
            temp.setIcon(rs.getShort("imgid"));
            temp.setPrice(rs.getLong("price"));
            temp.setName(rs.getString("name"));
            temp.setContent(rs.getString("content"));
            temp.setType(rs.getByte("type"));
            temp.setPricetype(rs.getByte("pricetype"));
            temp.setSell(rs.getByte("sell"));
            temp.setValue(rs.getShort("value"));
            temp.setTrade(rs.getByte("trade"));
            temp.setColor(rs.getByte("setcolorname"));
            ItemTemplate7.item.add(temp);
        }
        // load item medal
        for (int i = 0; i < 10; i++) {
            Medal_Material.m_blue[i] = (short) (i + 116);
            Medal_Material.m_yellow[i] = (short) (i + 126);
            Medal_Material.m_violet[i] = (short) (i + 136);
        }
        Medal_Material.m_white = new short[7][];
        int dem = 46;
        for (int i = 0; i < 7; i++) {
            Medal_Material.m_white[i] = new short[10];
            for (int j = 0; j < 10; j++) {
                Medal_Material.m_white[i][j] = (short) (dem++);
            }
        }
        //
        rs.close();
        // load item option
        query = "SELECT * FROM `itemoption`;";
        rs = ps.executeQuery(query);
        while (rs.next()) {
            OptionItem temp = new OptionItem();
            temp.setName(rs.getString("name"));
            temp.setColor(rs.getByte("colorInfoItem"));
            temp.setIspercent(rs.getByte("isPercentInfoItem"));
            OptionItem.entrys.add(temp);
        }
        rs.close();
        // load item sell
        query = "SELECT * FROM `itemsell`;";
        rs = ps.executeQuery(query);
        itemsellTB = new ArrayList<>();
        while (rs.next()) {
            byte type = rs.getByte("id");
            JSONArray jsar = (JSONArray) JSONValue.parse(rs.getString("data"));
            if (jsar == null) {
                return false;
            }
            switch (type) {
                case 0: {
                    switch (this.event) {
                        case 1: {
                            itempoitionsell = new short[jsar.size() + 4];
                            for (int i = 0; i < jsar.size(); i++) {
                                itempoitionsell[i] = Short.parseShort(jsar.get(i).toString());
                            }
                            itempoitionsell[itempoitionsell.length - 4] = 113;
                            itempoitionsell[itempoitionsell.length - 3] = 114;
                            itempoitionsell[itempoitionsell.length - 2] = 115;
                            itempoitionsell[itempoitionsell.length - 1] = 116;
                            break;
                        }
                        	case 2: {
							itempoitionsell = new short[jsar.size() + 2];
							for (int i = 0; i < jsar.size(); i++) {
								itempoitionsell[i] = Short.parseShort(jsar.get(i).toString());
							}
							itempoitionsell[itempoitionsell.length - 2] = 141;
							itempoitionsell[itempoitionsell.length - 1] = 140;
							break;
						}
                        default: {
                            itempoitionsell = new short[jsar.size()];
                            for (int i = 0; i < itempoitionsell.length; i++) {
                                itempoitionsell[i] = Short.parseShort(jsar.get(i).toString());
                            }
                            break;
                        }
                    }

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
                case 9:
                case 10:
                case 11:
                case 12:
                case 13:
                case 14:
                case 15:
                case 16: {
                    ItemSell3[] itemsell3 = new ItemSell3[jsar.size()];
                    for (int i = 0; i < itemsell3.length; i++) {
                        itemsell3[i] = new ItemSell3();
                        JSONArray jsar2 = (JSONArray) JSONValue.parse(jsar.get(i).toString());
                        itemsell3[i].id = Short.parseShort(jsar2.get(0).toString());
                        itemsell3[i].clazz = Byte.parseByte(jsar2.get(1).toString());
                        itemsell3[i].type = Byte.parseByte(jsar2.get(2).toString());
                        itemsell3[i].price = Long.parseLong(jsar2.get(3).toString());
                        itemsell3[i].level = Short.parseShort(jsar2.get(4).toString());
                        itemsell3[i].color = Byte.parseByte(jsar2.get(5).toString());
                        itemsell3[i].option = new ArrayList<>();
                        JSONArray jsar3 = (JSONArray) JSONValue.parse(jsar2.get(6).toString());
                        for (int j = 0; j < jsar3.size(); j++) {
                            JSONArray jsar4 = (JSONArray) JSONValue.parse(jsar3.get(j).toString());
                            itemsell3[i].option.add(new Option(Byte.parseByte(jsar4.get(0).toString()),
                                    Integer.parseInt(jsar4.get(1).toString())));
                        }
                        itemsell3[i].pricetype = Byte.parseByte(jsar2.get(7).toString());
                    }
                    itemsellTB.add(itemsell3);
                    break;
                }
                case 17: {
                    item7sell = new short[jsar.size()];
                    for (int i = 0; i < item7sell.length; i++) {
                        item7sell[i] = Short.parseShort(jsar.get(i).toString());
                    }
                    break;
                }
            }
        }
        rs.close();
        //
        System.out.println("item loaded!");
        // load mob temp
        query = "SELECT * FROM `mobs`;";
        rs = ps.executeQuery(query);
        while (rs.next()) {
            Mob temp = new Mob();
            temp.mob_id = Short.parseShort(rs.getString("id"));
            temp.name = rs.getString("name");
            temp.level = Short.parseShort(rs.getString("level"));
            temp.hpmax = Integer.parseInt(rs.getString("hp"));
            temp.typemove = Byte.parseByte(rs.getString("typemove"));
            Mob.entrys.add(temp);
        }
        rs.close();
        // load map
        query = "SELECT * FROM `maps`;";
        rs = ps.executeQuery(query);
        int index_mob = 1;
        while (rs.next()) {
            byte maxzone = rs.getByte("maxzone");
            Map[] temp_all_zone = new Map[maxzone + 1];
            byte map_id = rs.getByte("id");
            String name = rs.getString("name");
            //
            List<Vgo> vgo_temp = new ArrayList<>();
            JSONArray jsar = (JSONArray) JSONValue.parse(rs.getString("vgos"));
            if (jsar == null) {
                return false;
            }
            for (int i = 0; i < jsar.size(); i++) {
                JSONArray jsar2 = (JSONArray) JSONValue.parse(jsar.get(i).toString());
                Vgo vgo = new Vgo();
                vgo.id_map_go = Byte.parseByte(jsar2.get(0).toString());
                vgo.x_old = Short.parseShort(jsar2.get(1).toString());
                vgo.y_old = Short.parseShort(jsar2.get(2).toString());
                vgo.name_map_go = jsar2.get(3).toString();
                vgo.x_new = Short.parseShort(jsar2.get(4).toString());
                vgo.y_new = Short.parseShort(jsar2.get(5).toString());
                vgo_temp.add(vgo);
            }
            jsar.clear();
            //
            jsar = (JSONArray) JSONValue.parse(rs.getString("npcs"));
            if (jsar == null) {
                return false;
            }
            String[] name_npc = new String[jsar.size()];
            for (int i = 0; i < jsar.size(); i++) {
                name_npc[i] = jsar.get(i).toString();
            }
            jsar.clear();
            //
            jsar = (JSONArray) JSONValue.parse(rs.getString("mobs"));
            if (jsar == null) {
                return false;
            }
            List<Mob_in_map> mob_in_map = new ArrayList<>();
            for (int i = 0; i < jsar.size(); i++) {
                JSONArray jsar2 = (JSONArray) JSONValue.parse(jsar.get(i).toString());
                Mob_in_map mob = new Mob_in_map();
                short id = Short.parseShort(jsar2.get(0).toString());
                mob.template = Mob.entrys.get(id);
                mob.x = Short.parseShort(jsar2.get(1).toString());
                mob.y = Short.parseShort(jsar2.get(2).toString());
                mob.level = mob.template.level;
                mob.map_id = map_id;
                mob.isdie = false;
                mob.time_back = System.currentTimeMillis() + 4_000L;
                mob.color_name = 0;
                if (id == 101 || id == 84 || id == 83 || id == 103 || id == 104 || id == 105 || id == 106 || id == 149
                        || id == 155 || id == 174) {
                    mob.isdie = true;
                    mob.is_boss = true;
                } else {
                    mob.is_boss = false;
                }
                mob.hpmax = Mob.entrys.get(id).hpmax;
                mob.hp = mob.hpmax;
                mob_in_map.add(mob);
            }
            jsar.clear();
            //
            byte typemap = rs.getByte("type");
            byte maxplayer = rs.getByte("maxplayer");
            boolean ismaplang = rs.getByte("ismaplang") == 1;
            boolean showhs = rs.getByte("showhs") == 1;
            for (int i = 0; i < maxzone + 1; i++) {
                Map m = null;
                try {
                    m = new Map(map_id, i, name_npc, name, typemap, ismaplang, showhs, maxplayer, maxzone, vgo_temp);
                } catch (IOException e) {
                    System.err.println("load data map err " + map_id);
                    System.exit(0);
                }
                //
                if (i < maxzone) {
                    m.mobs = new Mob_in_map[mob_in_map.size()];
                    for (int i1 = 0; i1 < mob_in_map.size(); i1++) {
                        Mob_in_map mob = new Mob_in_map();
                        mob.index = index_mob++;
                        mob.template = mob_in_map.get(i1).template;
                        mob.x = mob_in_map.get(i1).x;
                        mob.y = mob_in_map.get(i1).y;
                        mob.hpmax = mob_in_map.get(i1).hp;
                        mob.hp = mob.hpmax;
                        mob.level = mob_in_map.get(i1).level;
                        mob.map_id = map_id;
                        mob.zone_id = (byte) i;
                        mob.isdie = mob_in_map.get(i1).isdie;
                        mob.color_name = mob_in_map.get(i1).color_name;
                        mob.is_boss = mob_in_map.get(i1).is_boss;
                        mob.time_back = mob_in_map.get(i1).time_back;
                        mob.list_fight = new ArrayList<>();
                        mob.is_boss_active = false;
                        m.mobs[i1] = mob;
                      	if (mob_in_map.get(i1).template.mob_id == 174) {
							BossEvent.LIST.add(mob);
							mob.is_boss_active = false;
							if (i1 == 1) {
								mob.level = 50;
							} else if (i1 == 3) {
								mob.level = 95;
							} else if (i1 == 4) {
								mob.level = 115;
							}
						} else if (mob.is_boss) {
							if (Map.is_map_chien_truong(m.map_id)) {
								ChienTruong.gI().boss.add(mob);
								mob.level = 10;
							} else {
								Mob_in_map.list_boss.add(mob);
							}
						}
                        
                    }
                } else {
                    m.mobs = new Mob_in_map[0];
                }
                //
                temp_all_zone[i] = m;
            }
            Map.entrys.add(temp_all_zone);
        }
        rs.close();
        this.size_mob = --index_mob;
        BossEvent.time = new long[BossEvent.LIST.size()];
		for (int j = 0; j < BossEvent.time.length; j++) {
			BossEvent.time[j] = System.currentTimeMillis() + 1000L * Util.random(60 * 60 * 1, 60 * 60 * 4);
		}
        //
        System.out.println("map loaded, mob size " + (this.size_mob));
        // load level
        query = "SELECT * FROM `level`;";
        rs = ps.executeQuery(query);
        while (rs.next()) {
            Level temp2 = new Level();
            temp2.level = rs.getShort("level");
            temp2.exp = rs.getLong("exp");
            temp2.tiemnang = rs.getShort("tiemnang");
            temp2.kynang = rs.getShort("kynang");
            Level.entrys.add(temp2);
        }
        if (lvmax > Level.entrys.size()) {
            for (int i = Level.entrys.size() + 1; i < lvmax + 2; i++) {
                Level temp2 = new Level();
                temp2.level = (short) i;
                temp2.exp = Level.entrys.get(i - 2).exp;
                temp2.tiemnang = 5;
                temp2.kynang = 1;
                Level.entrys.add(temp2);
            }
        }
        rs.close();
        // load part fashion temp
        query = "SELECT * FROM `fashiontemplate`;";
        rs = ps.executeQuery(query);
        while (rs.next()) {
            Part_fashion temp = new Part_fashion();
            temp.id = (short) rs.getInt("id");
            JSONArray jsar = (JSONArray) JSONValue.parse(rs.getString("part"));
            if (jsar == null) {
                return false;
            }
            temp.part = new byte[jsar.size()];
            for (int i = 0; i < temp.part.length; i++) {
                temp.part[i] = Byte.parseByte(jsar.get(i).toString());
            }
            Part_fashion.entrys.add(temp);
        }
        System.out.println("part_fashion loaded!");
        rs.close();
        // load clan
        query = "SELECT * FROM `clan`;";
        rs = ps.executeQuery(query);
        List<Clan> clan_list = new ArrayList<>();
        while (rs.next()) {
            Clan temp = new Clan();
            temp.name_clan = rs.getString("name");
            temp.name_clan_shorted = rs.getString("name_short");
            temp.icon = rs.getShort("icon");
            temp.level = rs.getShort("level");
            temp.exp = rs.getLong("exp");
            temp.slogan = rs.getString("slogan");
            temp.rule = rs.getString("rule");
            temp.notice = rs.getString("notice");
            temp.setVang(rs.getLong("vang"));
            temp.setKimcuong(rs.getInt("kimcuong"));
            temp.max_mem = rs.getShort("max_mem");
            temp.max_mem = Clan.get_mem_by_level(temp.level);
            //
            temp.item_clan = new ArrayList<>();
            JSONArray jsar = (JSONArray) JSONValue.parse(rs.getString("item"));
            if (jsar == null) {
                return false;
            }
            for (int i = 0; i < jsar.size(); i++) {
                JSONArray js2 = (JSONArray) JSONValue.parse(jsar.get(i).toString());
                Item47 item = new Item47();
                item.id = Short.parseShort(js2.get(0).toString());
                item.quantity = Short.parseShort(js2.get(1).toString());;
                temp.item_clan.add(item);
            }
            //
            jsar.clear();
            jsar = (JSONArray) JSONValue.parse(rs.getString("mems"));
            if (jsar == null) {
                return false;
            }
            temp.mems = new ArrayList<>();
            for (int i = 0; i < jsar.size(); i++) {
                JSONArray jsar2 = (JSONArray) JSONValue.parse(jsar.get(i).toString());
                Clan_mems mem = new Clan_mems();
                mem.name = jsar2.get(0).toString();
                mem.mem_type = Byte.parseByte(jsar2.get(1).toString());
                mem.kimcuong = Integer.parseInt(jsar2.get(2).toString());
                mem.vang = Long.parseLong(jsar2.get(3).toString());
                mem.head = Byte.parseByte(jsar2.get(4).toString());
                mem.eye = Byte.parseByte(jsar2.get(5).toString());
                mem.hair = Byte.parseByte(jsar2.get(6).toString());
                mem.level = Short.parseShort(jsar2.get(7).toString());
                mem.itemwear = new ArrayList<>();
                JSONArray jsar3 = (JSONArray) JSONValue.parse(jsar2.get(8).toString());
                for (int j = 0; j < jsar3.size(); j++) {
                    JSONArray jsar4 = (JSONArray) JSONValue.parse(jsar3.get(j).toString());
                    Part_player part = new Part_player();
                    part.part = Byte.parseByte(jsar4.get(0).toString());
                    part.type = Byte.parseByte(jsar4.get(1).toString());
                    mem.itemwear.add(part);
                }
                temp.mems.add(mem);
            }
            temp.mo_tai_nguyen = new ArrayList<>();
            clan_list.add(temp);
        }
        Clan.set_clan(clan_list);
        System.out.println("clan loaded!");
        rs.close();
        // load event
        if (this.event == 1) {
            Event_1.list_caythong.clear();
            Event_1.list_naukeo.clear();
            Event_1.list_bxh_naukeo.clear();
            query = "SELECT * FROM `event` WHERE `id` = 0;";
            rs = ps.executeQuery(query);
            long t_ = System.currentTimeMillis();
            while (rs.next()) {
                JSONObject jsob = (JSONObject) JSONValue.parse(rs.getString("data"));
                JSONArray jsar_1 = (JSONArray) JSONValue.parse(jsob.get("list_caythong").toString());
                for (int i = 0; i < jsar_1.size(); i++) {
                    JSONArray jsar_2 = (JSONArray) JSONValue.parse(jsar_1.get(i).toString());
                    Event_1.list_caythong.add(new Event_1.BXH_naukeo(jsar_2.get(0).toString(), Integer.parseInt(jsar_2.get(1).toString()), t_));
                }
                jsar_1.clear();
                //
                jsar_1 = (JSONArray) JSONValue.parse(jsob.get("list_naukeo").toString());
                for (int i = 0; i < jsar_1.size(); i++) {
                    JSONArray jsar_2 = (JSONArray) JSONValue.parse(jsar_1.get(i).toString());
                    Event_1.list_naukeo.put(jsar_2.get(0).toString(), Integer.parseInt(jsar_2.get(1).toString()));
                    Event_1.list_bxh_naukeo.add(new Event_1.BXH_naukeo(jsar_2.get(0).toString(), Integer.parseInt(jsar_2.get(1).toString()), t_));
                }
            }
            rs.close();
        }
        System.out.println("event loaded!");
        query = "SELECT * FROM `wedding`;";
		rs = ps.executeQuery(query);
		while (rs.next()) {
			Wedding temp_wed = new Wedding();
			JSONArray js_w = (JSONArray) JSONValue.parse(rs.getString("name"));
			temp_wed.name_1 = js_w.get(0).toString();
			temp_wed.name_2 = js_w.get(1).toString();
			js_w.clear();
			js_w = (JSONArray) JSONValue.parse(rs.getString("item"));
			temp_wed.exp = Long.parseLong(js_w.get(0).toString());
			temp_wed.it = new Item3();
			temp_wed.it.id = 0;
			temp_wed.it.name = "Cặp đôi " + temp_wed.name_1 + " " + temp_wed.name_2;
			temp_wed.it.clazz = 4;
			temp_wed.it.type = 103;
			temp_wed.it.level = 60;
			temp_wed.it.icon = 13166;
			temp_wed.it.color = Byte.parseByte(js_w.get(1).toString());
			temp_wed.it.part = 0;
			temp_wed.it.tier = Byte.parseByte(js_w.get(2).toString());
			temp_wed.it.islock = true;
			temp_wed.it.op = new ArrayList<>();
			JSONArray js_op = (JSONArray) JSONValue.parse(js_w.get(3).toString());
			for (int i = 0; i < js_op.size(); i++) {
				JSONArray js_op_2 = (JSONArray) JSONValue.parse(js_op.get(i).toString());
				temp_wed.it.op.add(
				      new Option(Byte.parseByte(js_op_2.get(0).toString()), Integer.parseInt(js_op_2.get(1).toString())));
			}
			temp_wed.it.time_use = 0;
			Wedding.list.add(temp_wed);
		}
        
        
        // close all
        rs.close();
        ps.close();
        conn.close();
        return true;
    }

    private void load_config() throws IOException {
        final byte[] ab = Util.loadfile("hso.conf");
        if (ab == null) {
            System.out.println("Config file not found!");
            System.exit(0);
        }
        final String data = new String(ab);
        final HashMap<String, String> configMap = new HashMap<String, String>();
        final StringBuilder sbd = new StringBuilder();
        boolean bo = false;
        for (int i = 0; i <= data.length(); ++i) {
            final char es;
            if (i == data.length() || (es = data.charAt(i)) == '\n') {
                bo = false;
                final String sbf = sbd.toString().trim();
                if (sbf != null && !sbf.equals("") && sbf.charAt(0) != '#') {
                    final int j = sbf.indexOf(58);
                    if (j > 0) {
                        final String key = sbf.substring(0, j).trim();
                        final String value = sbf.substring(j + 1).trim();
                        configMap.put(key, value);
                        System.out.println("config: " + key + ": " + value);
                    }
                }
                sbd.setLength(0);
            } else {
                if (es == '#') {
                    bo = true;
                }
                if (!bo) {
                    sbd.append(es);
                }
            }
        }
        if (configMap.containsKey("port")) {
            this.server_port = Integer.parseInt(configMap.get("port"));
        } else {
            this.server_port = 19129;
        }
        if (configMap.containsKey("debug")) {
            this.debug = Boolean.parseBoolean(configMap.get("debug"));
        } else {
            this.debug = false;
        }
        if (configMap.containsKey("mysql-host")) {
            this.mysql_host = configMap.get("mysql-host");
        } else {
            this.mysql_host = "127.0.0.1";
        }
        if (configMap.containsKey("mysql-user")) {
            this.mysql_user = configMap.get("mysql-user");
        } else {
            this.mysql_user = "root";
        }
        if (configMap.containsKey("mysql-password")) {
            this.mysql_pass = configMap.get("mysql-password");
        } else {
            this.mysql_pass = "";
        }
        if (configMap.containsKey("mysql-database")) {
            this.mysql_database = configMap.get("mysql-database");
        } else {
            this.mysql_database = "hso2";
        }
        if (configMap.containsKey("indexRes")) {
            this.indexRes = Byte.parseByte(configMap.get("indexRes"));
        } else {
            this.indexRes = 60;
        }
        if (configMap.containsKey("indexCharPar")) {
            this.indexCharPar = Short.parseShort(configMap.get("indexCharPar"));
        } else {
            this.indexCharPar = -22031;
        }
        if (configMap.containsKey("exp")) {
            this.exp = Integer.parseInt(configMap.get("exp"));
        } else {
            this.exp = 500;
        }
        if (configMap.containsKey("lvmax")) {
            this.lvmax = Short.parseShort(configMap.get("lvmax"));
        } else {
            this.lvmax = 1000;
        }
        if (configMap.containsKey("allow_ip_client")) {
            this.allow_ip_client = Integer.parseInt(configMap.get("allow_ip_client"));
        } else {
            this.allow_ip_client = 3;
        }
        if (configMap.containsKey("time_login")) {
            this.time_login = Integer.parseInt(configMap.get("time_login"));
        } else {
            this.time_login = 3000;
        }
        if (configMap.containsKey("event")) {
            this.event = Integer.parseInt(configMap.get("event"));
        } else {
            this.event = 0;
        }
    }

    public void chatKTGprocess(String s) throws IOException {
        Message m = new Message(53);
        m.writer().writeUTF(s);
        m.writer().writeByte(1);
        for (Map[] map : Map.entrys) {
            for (Map map0 : map) {
                for (int i = 0; i < map0.players.size(); i++) {
                    map0.players.get(i).conn.addmsg(m);
                }

            }
        }
        m.cleanup();
    }

    public void close() {
        vxmm.close();
        Log.gI().close_log();
        //
        for (int i = 0; i < Map.entrys.size(); i++) {
            for (int j = 0; j < Map.entrys.get(i).length; j++) {
                Map.entrys.get(i)[j].stop_map();
            }
        }
        bossTG.stop_boss_tg();
        try {
            tx.finish();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        tx.stop_service();
//		instance = null;
    }

    public synchronized int get_index_mob_new() {
        if (this.size_mob_now < -5000) {
            this.size_mob_now = -20;
        }
        return (--this.size_mob_now);
    }

    public synchronized void add_list_nhanbban(NhanBan nhanban) {
        this.list_nhanban.add(nhanban);
    }

    public synchronized void remove_list_nhanbban(NhanBan nhanban) {
        this.list_nhanban.remove(nhanban);
    }
}
