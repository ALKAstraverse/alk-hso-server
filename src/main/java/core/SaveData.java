package core;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import org.json.simple.JSONArray;
import org.json.simple.JSONValue;
import client.Clan;
import client.Friend;
import client.Player;
import core.BXH.Memin4;
import map.Map;
import template.EffTemplate;
import template.Item3;
import template.Item47;
import template.Level;
import template.Part_player;
import event.Event_1;
import event_daily.Wedding;
import java.sql.Driver;
import java.sql.DriverManager;
import java.util.Map.Entry;
import org.json.simple.JSONObject;

public class SaveData {

    @SuppressWarnings("unchecked")
    public synchronized static void process() {
//        synchronized (SQL.) {
        long time_check = System.currentTimeMillis();
        try {
            Connection conn = SQL.gI().getConnection();
//Connection conn = DriverManager.getConnection(SQL.gI().url, Manager.gI().mysql_user, Manager.gI().mysql_pass);
            // clan
            BXH.BXH_clan.clear();
            for (Clan clan : Clan.get_all_clan()) {
                BXH.BXH_clan.add(clan);
            }
            Collections.sort(BXH.BXH_clan, new Comparator<Clan>() {
                @Override
                public int compare(Clan o1, Clan o2) {
                    int com1 = (o1.level == o2.level) ? 0 : (o1.level > o2.level) ? -1 : 1;
                    if (com1 != 0) {
                        return com1;
                    }
                    return (o1.exp >= o2.exp) ? -1 : 1;
                }
            });
            PreparedStatement ps = conn.prepareStatement(
                    "UPDATE `clan` SET `level` = ?, `exp` = ?, `slogan` = ?, `rule` = ?, `mems` = ?, `item` = ?, `notice` = ?, `vang` = ?, `kimcuong` = ?, `icon` = ?, `max_mem` = ? WHERE `name` = ?;");
            // clan
            List<Clan> list_to_remove = new ArrayList<>();
            for (int i = 0; i < Clan.entrys.size(); i++) {
                Clan clan = Clan.entrys.get(i);
                if (clan.mems.size() < 1) {
                    list_to_remove.add(clan);
//                    Clan.entrys.remove(clan);
//                    i--;
                } else {
                    ps.clearParameters();
                    ps.setInt(1, clan.level);
                    ps.setLong(2, clan.exp);
                    ps.setNString(3, "");
                    ps.setNString(4, "");
                    ps.setNString(5, Clan.flush_mem_json(clan.mems));
                    ps.setNString(6, Clan.flush_item_json(clan.item_clan));
                    ps.setNString(7, clan.notice);
                    ps.setLong(8, clan.get_vang());
                    ps.setInt(9, clan.get_ngoc());
                    ps.setInt(10, clan.icon);
                    ps.setInt(11, clan.max_mem);
                    ps.setNString(12, clan.name_clan);
                    ps.executeUpdate();
//                    ps.addBatch();
//                    if (i % 50 == 0) {
//                        ps.executeBatch();
//                    }
                }
            }
//            ps.executeBatch();
            //
            ps.close();
            ps = conn.prepareStatement("DELETE FROM `clan` WHERE `name` = ?;");
            for (int i = 0; i < list_to_remove.size(); i++) {
                Clan clan = list_to_remove.get(i);
                ps.clearParameters();
                ps.setNString(1, clan.name_clan);
                ps.executeUpdate();
//                ps.addBatch();
//                if (i % 50 == 0) {
//                    ps.executeBatch();
//                }
            }
//            ps.executeBatch();
            ps.close();
            Clan.entrys.removeAll(list_to_remove);
            list_to_remove.clear();

            ps = conn.prepareStatement("UPDATE `wedding` SET `item` = ? WHERE `name` = ?;");
            for (int i = 0; i < Wedding.list.size(); i++) {
                Wedding temp = Wedding.list.get(i);
                ps.clearParameters();
                JSONArray js2 = new JSONArray();
                js2.add(temp.exp);
                js2.add(temp.it.color);
                js2.add(temp.it.tier);
                JSONArray js22 = new JSONArray();
                for (int i2 = 0; i2 < temp.it.op.size(); i2++) {
                    JSONArray js23 = new JSONArray();
                    js23.add(temp.it.op.get(i2).id);
                    js23.add(temp.it.op.get(i2).getParam(0));
                    js22.add(js23);
                }
                js2.add(js22);
                ps.setNString(1, js2.toJSONString());
                JSONArray js = new JSONArray();
                js.add(temp.name_1);
                js.add(temp.name_2);
                ps.setNString(2, js.toJSONString());
                ps.execute();
            }
            ps.close();

            // flush player
            String query
                    = "UPDATE `player` SET `level` = ?, `exp` = ?, `site` = ?, `body` = ?, `eff` = ?, `friend` = ?, `skill` = ?, `item4` = ?, "
                    + "`item7` = ?, `item3` = ?, `itemwear` = ?, `giftcode` = ?, `enemies` = ?, `rms_save` = ?, `itembox4` = ?, "
                    + "`itembox7` = ?, `itembox3` = ?, `pet` = ?, `medal_create_material` = ?, `point_active` = ?, `vang` = ?, "
                    + "`kimcuong` = ?, `tiemnang` = ?, `kynang` = ?, `diemdanh` = ?, `chucphuc` = ?, `hieuchien` = ?, `typeexp` = ?, "
                    + "`date` = ?, `point1` = ?, `point2` = ?, `point3` = ?, `point4` = ?, `clazz` = ?,`chuyensinh` = ?, `tutien` = ?,"
                    + " `dquest` = ?, `pointarena` = ?, `tinhtu_material` = ?, `nvtt` = ? WHERE `id` = ?;";
            ps = conn.prepareStatement(query);
            for (Map[] map : Map.entrys) {
                for (Map map0 : map) {
                    for (int i1 = 0; i1 < map0.players.size(); i1++) {
                        // for (int i1 = 0; i1 < ServerManager.gI().t1.list_p.size(); i1++) {
                        ps.clearParameters();
                        Player p0 = map0.players.get(i1);
                        // Player p0 = ServerManager.gI().t1.list_p.get(i1);
                        //
                        ps.setInt(1, p0.level);
                        ps.setLong(2, p0.exp);
                        JSONArray jsar = new JSONArray();

                        if (p0.isdie || Map.is_map_cant_save_site(p0.map.map_id)) {
                            jsar.add(1);
                            jsar.add(432);
                            jsar.add(354);
                        } else {
                            jsar.add(p0.map.map_id);
                            jsar.add(p0.x);
                            jsar.add(p0.y);
                        }

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
                        ps.setLong(21, p0.get_vang());
                        ps.setInt(22, p0.get_ngoc());
                        ps.setInt(23, p0.tiemnang);
                        ps.setShort(24, p0.kynang);
                        ps.setByte(25, p0.diemdanh);

                        ps.setByte(26, (byte) 0);
                        ps.setInt(27, 0);
                        ps.setByte(28, p0.type_exp);
                        ps.setNString(29, p0.date.toString());
                        ps.setInt(30, p0.point1);
                        ps.setInt(31, p0.point2);
                        ps.setInt(32, p0.point3);
                        ps.setInt(33, p0.point4);
                        ps.setInt(34, p0.clazz);
                        ps.setInt(35, p0.chuyensinh);
                        jsar.add(p0.luyenthe);
                        jsar.add(p0.kinhmach[0]);
                        jsar.add(p0.kinhmach[1]);
                        jsar.add(p0.tutien[0]);
                        jsar.add(p0.tutien[1]);
                        jsar.add(p0.tutien[2]);
                        ps.setNString(36, jsar.toJSONString());
                        jsar.clear();

                        for (int i = 0; i < p0.quest_daily.length; i++) {
                            jsar.add(p0.quest_daily[i]);
                        }
                        ps.setNString(37, jsar.toJSONString());
                        jsar.clear();
                        ps.setShort(38, (short) p0.pointarena);

                        for (int i = 0; i < p0.tinhtu_material.length; i++) {
                            jsar.add(p0.tinhtu_material[i]);
                        }
                        ps.setNString(39, jsar.toJSONString());
                        jsar.clear();
                        for (int i = 0; i < p0.nv_tinh_tu.length; i++) {
                            jsar.add(p0.nv_tinh_tu[i]);
                        }
                        ps.setNString(40, jsar.toJSONString());
                        jsar.clear();

                        ps.setInt(41, p0.id);

                        ps.executeUpdate();
//                        ps.addBatch();
//                        if (i1 % 50 == 0) {
//                            ps.executeBatch();
//                        }
                    }
                }
            }
            // }
//            ps.executeBatch();
            ps.close();
            // event
            if (Manager.gI().event == 1) {
                ps = conn.prepareStatement("UPDATE `event` SET `data` = ? WHERE `id` = ?;");
                ps.clearParameters();
                //
                JSONArray jsar_1 = new JSONArray();
                for (int i = 0; i < Event_1.list_caythong.size(); i++) {
                    JSONArray jsar_2 = new JSONArray();
                    jsar_2.add(Event_1.list_caythong.get(i).name);
                    jsar_2.add(Event_1.list_caythong.get(i).quant);
                    jsar_1.add(jsar_2);
                }
                JSONArray jsar_3 = new JSONArray();
                for (Entry<String, Integer> en : Event_1.list_naukeo.entrySet()) {
                    JSONArray jsar_2 = new JSONArray();
                    jsar_2.add(en.getKey());
                    jsar_2.add(en.getValue());
                    jsar_3.add(jsar_2);
                }
                //
                JSONObject jsob = new JSONObject();
                jsob.put("list_naukeo", jsar_3);
                jsob.put("list_caythong", jsar_1);
                //
                ps.setNString(1, jsob.toJSONString());
                ps.setInt(2, 0);
                ps.executeUpdate();
                ps.close();
            }
            // bxh
            BXH.BXH_level.clear();
            ps = conn.prepareStatement(
                    "SELECT `id`, `level`, `exp`, `name`, `body`, `itemwear`, `chuyensinh` FROM `player` WHERE `level` > 10 ORDER BY `level` DESC, exp DESC LIMIT 20;");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Memin4 temp = new Memin4();
                temp.level = rs.getShort("level");
                temp.exp = rs.getLong("exp");
                temp.name = rs.getString("name");
                temp.chuyensinh = rs.getInt("chuyensinh");
                JSONArray jsar = (JSONArray) JSONValue.parse(rs.getString("body"));
                if (jsar == null) {
                    return;
                }
                temp.head = Byte.parseByte(jsar.get(0).toString());
                temp.hair = Byte.parseByte(jsar.get(2).toString());
                temp.eye = Byte.parseByte(jsar.get(1).toString());
                jsar.clear();
                jsar = (JSONArray) JSONValue.parse(rs.getString("itemwear"));
                if (jsar == null) {
                    return;
                }
                temp.itemwear = new ArrayList<>();
                for (int i3 = 0; i3 < jsar.size(); i3++) {
                    JSONArray jsar2 = (JSONArray) JSONValue.parse(jsar.get(i3).toString());
                    byte index_wear = Byte.parseByte(jsar2.get(9).toString());
                    if (index_wear != 0 && index_wear != 1 && index_wear != 6 && index_wear != 7 && index_wear != 10) {
                        continue;
                    }
                    Part_player temp2 = new Part_player();
                    temp2.type = Byte.parseByte(jsar2.get(2).toString());
                    temp2.part = Byte.parseByte(jsar2.get(6).toString());
                    temp.itemwear.add(temp2);
                }
                temp.clan = Clan.get_clan_of_player(temp.name);
                String percent = String.format("%.1f", (((float) temp.exp * 1000) / Level.entrys.get(temp.level - 1).exp) / 10f);
                        //= String.format("%.1f", (((float) temp.exp * 1000) / Level.entrys.get(temp.level - 1).exp) / 10f);
                //temp.info = "Lv" + temp.level + " - " + percent + "% - " + temp.chuyensinh + " Chuyển";
                temp.info = "Lv" + temp.level + " - " + percent + "%";
                BXH.BXH_level.add(temp);
            }
            rs.close();
            // BXH chuyển sinh
BXH.BXH_chuyensinh.clear();
ps = conn.prepareStatement(
    "SELECT `id`, `level`, `exp`, `name`, `body`, `itemwear`, `chuyensinh` FROM `player` WHERE `chuyensinh` > 0 ORDER BY `chuyensinh` DESC, `level` DESC, `exp` DESC LIMIT 20;");
rs = ps.executeQuery();
while (rs.next()) {
    BXH.Memin4 temp = new BXH.Memin4();
    temp.level = rs.getShort("level");
    temp.exp = rs.getLong("exp");
    temp.name = rs.getString("name");
    temp.chuyensinh = rs.getInt("chuyensinh");
    //temp.hieuchien = rs.getInt("hieuchien");
    JSONArray jsar = (JSONArray) JSONValue.parse(rs.getString("body"));
    if (jsar == null) continue;
    temp.head = Byte.parseByte(jsar.get(0).toString());
    temp.hair = Byte.parseByte(jsar.get(2).toString());
    temp.eye = Byte.parseByte(jsar.get(1).toString());
    jsar.clear();
    jsar = (JSONArray) JSONValue.parse(rs.getString("itemwear"));
    if (jsar == null) continue;
    temp.itemwear = new ArrayList<>();
    for (int i3 = 0; i3 < jsar.size(); i3++) {
        JSONArray jsar2 = (JSONArray) JSONValue.parse(jsar.get(i3).toString());
        byte index_wear = Byte.parseByte(jsar2.get(9).toString());
        if (index_wear != 0 && index_wear != 1 && index_wear != 6 && index_wear != 7 && index_wear != 10) continue;
        Part_player part = new Part_player();
        part.type = Byte.parseByte(jsar2.get(2).toString());
        part.part = Byte.parseByte(jsar2.get(6).toString());
        temp.itemwear.add(part);
    }
    temp.clan = Clan.get_clan_of_player(temp.name);
    //String percent = String.format("%.1f", (((float) temp.exp * 1000) / Level.entrys.get(temp.level - 1).exp) / 10f);
    temp.info = "Chuyển Sinh: " + temp.chuyensinh + " Lần";
    BXH.BXH_chuyensinh.add(temp);
}
rs.close();
//bxh_hieuchien
BXH.BXH_hieuchien.clear();
ps = conn.prepareStatement(
    "SELECT `id`, `level`, `exp`, `name`, `body`, `itemwear`, `hieuchien` FROM `player` WHERE `hieuchien` >= 0 ORDER BY `hieuchien` DESC, `level` DESC, `exp` DESC LIMIT 20;");
rs = ps.executeQuery();
while (rs.next()) {
    BXH.Memin4 temp = new BXH.Memin4();
    temp.level = rs.getShort("level");
    temp.exp = rs.getLong("exp");
    temp.name = rs.getString("name");
    temp.hieuchien = rs.getInt("hieuchien"); // Dùng hieuchien thay vì chuyensinh
    //int hieuchien = rs.getInt("hieuchien");
    JSONArray jsar = (JSONArray) JSONValue.parse(rs.getString("body"));
    if (jsar == null) continue;
    temp.head = Byte.parseByte(jsar.get(0).toString());
    temp.hair = Byte.parseByte(jsar.get(2).toString());
    temp.eye = Byte.parseByte(jsar.get(1).toString());
    jsar.clear();
    jsar = (JSONArray) JSONValue.parse(rs.getString("itemwear"));
    if (jsar == null) continue;
    temp.itemwear = new ArrayList<>();
    for (int i3 = 0; i3 < jsar.size(); i3++) {
        JSONArray jsar2 = (JSONArray) JSONValue.parse(jsar.get(i3).toString());
        byte index_wear = Byte.parseByte(jsar2.get(9).toString());
        if (index_wear != 0 && index_wear != 1 && index_wear != 6 && index_wear != 7 && index_wear != 10) continue;
        Part_player part = new Part_player();
        part.type = Byte.parseByte(jsar2.get(2).toString());
        part.part = Byte.parseByte(jsar2.get(6).toString());
        temp.itemwear.add(part);
    }
    temp.clan = Clan.get_clan_of_player(temp.name);
    //String percent = String.format("%.1f", (((float) temp.exp * 1000) / Level.entrys.get(temp.level - 1).exp) / 10f);
    temp.info = "Điểm PK: " + temp.hieuchien + " Điểm"; // Hiển thị điểm hiếu chiến
    BXH.BXH_hieuchien.add(temp);
}
rs.close();

//bxh chiến trường 
BXH.BXH_chientruong.clear();
ps = conn.prepareStatement(
    "SELECT `id`, `level`, `exp`, `name`, `body`, `itemwear`, `pointarena` FROM `player` WHERE `pointarena` > 0 ORDER BY `pointarena` DESC, `level` DESC, `exp` DESC LIMIT 20;");
rs = ps.executeQuery();
while (rs.next()) {
    BXH.Memin4 temp = new BXH.Memin4();
    temp.level = rs.getShort("level");
    temp.exp = rs.getLong("exp");
    temp.name = rs.getString("name");
    temp.pointarena = rs.getInt("pointarena"); // Dùng hieuchien thay vì chuyensinh
    //int hieuchien = rs.getInt("hieuchien");
    JSONArray jsar = (JSONArray) JSONValue.parse(rs.getString("body"));
    if (jsar == null) continue;
    temp.head = Byte.parseByte(jsar.get(0).toString());
    temp.hair = Byte.parseByte(jsar.get(2).toString());
    temp.eye = Byte.parseByte(jsar.get(1).toString());
    jsar.clear();
    jsar = (JSONArray) JSONValue.parse(rs.getString("itemwear"));
    if (jsar == null) continue;
    temp.itemwear = new ArrayList<>();
    for (int i3 = 0; i3 < jsar.size(); i3++) {
        JSONArray jsar2 = (JSONArray) JSONValue.parse(jsar.get(i3).toString());
        byte index_wear = Byte.parseByte(jsar2.get(9).toString());
        if (index_wear != 0 && index_wear != 1 && index_wear != 6 && index_wear != 7 && index_wear != 10) continue;
        Part_player part = new Part_player();
        part.type = Byte.parseByte(jsar2.get(2).toString());
        part.part = Byte.parseByte(jsar2.get(6).toString());
        temp.itemwear.add(part);
    }
    temp.clan = Clan.get_clan_of_player(temp.name);
    //String percent = String.format("%.1f", (((float) temp.exp * 1000) / Level.entrys.get(temp.level - 1).exp) / 10f);
    temp.info = temp.pointarena + " Điểm"; // Hiển thị điểm hiếu chiến
    BXH.BXH_chientruong.add(temp);
}
rs.close();
            //

            Map.head = -1;
            Map.eye = -1;
            Map.hair = -1;
            Map.weapon = -1;
            Map.body = -1;
            Map.leg = -1;
            Map.hat = -1;
            Map.wing = -1;
            Map.name_mo = "";
            rs = ps.executeQuery("SELECT * FROM `player` ORDER BY `hieuchien` DESC, `id` LIMIT 1");
            if (rs.next()) {
                Map.name_mo = rs.getString("name");
                JSONArray js = (JSONArray) JSONValue.parse(rs.getString("body"));
                Map.head = Short.parseShort(js.get(0).toString());
                Map.eye = Short.parseShort(js.get(1).toString());
                Map.hair = Short.parseShort(js.get(2).toString());
                js.clear();
                js = (JSONArray) JSONValue.parse(rs.getString("itemwear"));
                for (int i3 = 0; i3 < js.size(); i3++) {
                    JSONArray jsar2 = (JSONArray) JSONValue.parse(js.get(i3).toString());
                    if (jsar2 == null) {
                        return;
                    }
                    byte index_wear = Byte.parseByte(jsar2.get(9).toString());
                    if (index_wear != 0 && index_wear != 1 && index_wear != 2 && index_wear != 7 && index_wear != 10) {
                        continue;
                    }

                    Part_player temp = new Part_player();
                    temp.type = Byte.parseByte(jsar2.get(2).toString());
                    temp.part = Byte.parseByte(jsar2.get(6).toString());
                    if (temp.type == 2) {
                        Map.hat = temp.part;
                    }
                    if (temp.type == 0) {
                        Map.body = temp.part;
                    }
                    if (temp.type == 1) {
                        Map.leg = temp.part;
                    }
                    if (temp.type == 7) {
                        Map.wing = temp.part;
                    }
                    if (temp.type == 10) {
                        Map.weapon = temp.part;
                    }
                }

            }

            conn.commit();
            rs.close();
            ps.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("[" + Util.get_now_by_time() + "] save data fail!");
            return;
        }
        System.out.println("[" + Util.get_now_by_time() + "] save data ok " + (System.currentTimeMillis() - time_check));
        ServerManager.gI().time_l = System.currentTimeMillis() + 60_000L;
//        }
    }
}
