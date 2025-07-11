package client;

import core.GameSrc;
import event.Event_1;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.regex.Pattern;
import org.json.simple.JSONArray;
import org.json.simple.JSONValue;
import core.Log;
import core.Manager;
import core.SQL;
import core.Service;
import core.Util;
import io.Message;
import io.Session;
import java.util.HashMap;
import map.Map;
import map.MapService;
import template.Clan_mems;
import template.Item3;
import template.Item47;
import template.ItemTemplate3;
import template.ItemTemplate4;
import template.ItemTemplate7;
import template.Level;

public class TextFromClient {

    public static void process(Session conn, Message m2) throws IOException {
        short idnpc = m2.reader().readShort();
        short idmenu = m2.reader().readShort();
        byte size = m2.reader().readByte();
        if (idmenu != 0) {
            return;
        }
        switch (idnpc) {
            case 23: {
				if (size != 2) {
					return;
				}
				String value1 = m2.reader().readUTF();
				String value2 = m2.reader().readUTF();
				if (!(Util.isnumber(value1)) || !(Util.isnumber(value2))) {
					Service.send_notice_box(conn, "Dữ liệu nhập không phải số!!");
					return;
				}
				int coin_join = Integer.parseInt(value1);
				int type_join = Integer.parseInt(value2);
				if (coin_join < 1_000 || coin_join > 1_000_000_000) {
					Service.send_notice_box(conn, "Coin tối thiểu 1k và tối đa là 1b!");
					return;
				}
				if (!(type_join == 1 || type_join == 2)) {
					Service.send_notice_box(conn, "Nhập 1 nếu bạn chọn tài và 2 nếu bạn chọn xỉu!!");
					return;
				}
				if (!conn.p.update_coin(-coin_join)) {
					Service.send_notice_box(conn, "Không đủ coin!");
					return;
				}
				Manager.gI().tx.join(conn.p, coin_join, type_join);
                                Service.send_notice_box(conn,
				      "Cược " + coin_join + " coin vào " + ((type_join == 1) ? "Tài" : "Xỉu") + " thành công");
                                  Player p = conn.p;
                 if (p.quest_daily[0] == 3 && p.quest_daily[3] < p.quest_daily[4]) {
                        p.quest_daily[3]++;
                    }
				break;
			}
            case 22: {
				if (size != 2) {
					return;
				}
				String value1 = m2.reader().readUTF();
				String value2 = m2.reader().readUTF();
				if (!(Util.isnumber(value1))) {
					Service.send_notice_box(conn, "Dữ liệu nhập không phải số!!");
					return;
				}
				int quant = Integer.parseInt(value1);
				Player p0 = Map.get_player_by_name(value2);
				if (p0 != null) {
					if (p0.item.wear[23] != null) {
						Service.send_notice_box(conn, "Đối phương đã kết đôi với người khác!");
						return;
					}
					if (p0.level < 60) {
						Service.send_notice_box(conn, "Yêu cầu level trên 60");
						return;
					}
					switch (quant) {
						case 1: {
							if (conn.p.get_vang() < 10_000_000_000L) {
								Service.send_notice_box(conn, "Không đủ 10 tỏi vàng");
								return;
							}
							break;
						}
						case 2: {
							if (conn.p.get_ngoc() < 500_000) {
								Service.send_notice_box(conn, "Không đủ 500k ngọc");
								return;
							}
                                                        
							break;
						}
						case 3: {
							if (conn.p.get_ngoc() < 1_000_000) {
								Service.send_notice_box(conn, "Không đủ 1m ngọc");
								return;
							}
							
							break;
						}
						case 4: {
							if (conn.p.get_ngoc() < 2_000_000) {
								Service.send_notice_box(conn, "Không đủ 2m ngọc");
								return;
							}
                                                        
							break;
						}
						default: {
							Service.send_notice_box(conn, "Chọn nhẫn từ 1 -4 thôi!");
							return;
						}
					}
					conn.p.item.char_inventory(5);
					p0.in4_wedding = new String[] {"" + quant, conn.p.name};
					Service.send_box_input_yesno(p0.conn, 110, conn.p.name + " muốn cầu hôn bạn, đồng ý lấy mình nhé?");
				} else {
					Service.send_notice_box(conn, "Không tìm thấy đối phương!");
				}
				break;
			}
            case 16: {
				if (size != 1) {
					return;
				}
				String value = m2.reader().readUTF();
				if (!(Util.isnumber(value))) {
					Service.send_notice_box(conn, "Dữ liệu nhập không phải số!!");
					return;
				}
				int quant = Integer.parseInt(value);
				int quant_ngoc_can_create = conn.p.item.total_item_by_id(7, conn.p.id_ngoc_tinh_luyen);
				if (quant > quant_ngoc_can_create) {
					Service.send_notice_box(conn, "Số lượng trong hành trang không đủ!");
					return;
				}
				int vang_required =
				      (int) (((long) quant) * (GameSrc.get_vang_hopngoc(conn.p.id_ngoc_tinh_luyen) / 50_000L) * 1_000_000L);
				if (conn.p.get_vang() < vang_required) {
					Service.send_notice_box(conn, "Không đủ " + vang_required + " vàng");
					return;
				}
				if (conn.p.get_vang() < vang_required) {
					Service.send_notice_box(conn, "Tinh luyện cần " + vang_required + " vàng!");
					return;
				}
				conn.p.update_vang(-vang_required);
				Item47 it = new Item47();
				it.id = (short) (conn.p.id_ngoc_tinh_luyen + 30);
				it.quantity = (short) quant;
				conn.p.item.add_item_bag47(7, it);
				conn.p.item.remove(7, conn.p.id_ngoc_tinh_luyen, quant);
				Service.send_notice_box(conn,
				      "Tinh luyện thành công " + quant + " " + ItemTemplate7.item.get(it.id).getName());
				conn.p.id_ngoc_tinh_luyen = -1;
				conn.p.item.char_inventory(4);
				conn.p.item.char_inventory(7);
				conn.p.item.char_inventory(3);
                                Log.gI().add_log(conn.p.name,
                                    "tinh luyen ngoc het"  + Util.number_format(vang_required) + " vàng");
				break;
			}
            case 0: {
                if (size != 1) {
                    return;
                }
                if (conn.status != 0) {
                    Service.send_notice_box(conn, "Tài khoản chưa được kích hoạt, hãy kích hoạt");
                    return;
                }
                String text = m2.reader().readUTF();
                text = text.toLowerCase();
                Pattern p = Pattern.compile("^[a-zA-Z0-9]{1,15}$");
                if (!p.matcher(text).matches()) {
                    Service.send_notice_box(conn, "Đã xảy ra lỗi");
                    return;
                }
                for (String txt : conn.p.giftcode) {
                    txt = txt.toLowerCase();
                    if (txt.equals((text))) {
                        Service.send_notice_box(conn, "Bạn đã nhập giftcode này rồi");
                        return;
                    }
                }
                try ( Connection connection = SQL.gI().getConnection();  Statement st = connection.createStatement();  ResultSet rs = st.executeQuery("SELECT * FROM `giftcode` WHERE `giftname` = '" + text + "';")) {
                    if (!rs.next()) {
                        Service.send_notice_box(conn, "Giftcode đã được nhập hoặc không tồn tại");
                    } else {
                        int limit = rs.getInt("limit");
                        String a =  rs.getString("spec");
                        
                        String[] spec;
                        if (a != null) {
                            spec = a.split(",");
                        } else {
                            spec = new String[0];
                        }
                        if (limit < 1) {
                            Service.send_notice_box(conn, "Đã hết lượt dùng giftcode này");
                        } else if (conn.p.item.get_bag_able() > 2) {
                            boolean spec_ = spec.length > 0;
                                for (int i = 0; i < spec.length; i++) {
                                    if (spec[i].equals(conn.p.name)) {
                                        spec_ = false;
                                        break;
                                    }
                                }
                            
                            
                            if (spec_) {
                                Service.send_notice_box(conn, "Giftcode đã được nhập hoặc không tồn tại");
                                return;
                            }
                            //
                            conn.p.giftcode.add(text);
                            JSONArray jsar = (JSONArray) JSONValue.parse(rs.getString("item3"));
                            for (int i = 0; i < jsar.size(); i++) {
                                JSONArray jsar2 = (JSONArray) JSONValue.parse(jsar.get(i).toString());
                                Item3 itbag = new Item3();
                                short it = Short.parseShort(jsar2.get(0).toString());
                                itbag.id = it;
                                itbag.name = ItemTemplate3.item.get(it).getName();
                                itbag.clazz = ItemTemplate3.item.get(it).getClazz();
                                itbag.type = ItemTemplate3.item.get(it).getType();
                                itbag.level = ItemTemplate3.item.get(it).getLevel();
                                itbag.icon = ItemTemplate3.item.get(it).getIcon();
                                itbag.op = ItemTemplate3.item.get(it).getOp();
                                itbag.color = ItemTemplate3.item.get(it).getColor();
                                itbag.part = ItemTemplate3.item.get(it).getPart();
                                itbag.tier = 0;
                                itbag.time_use = 0;
                                itbag.islock = true;
                                conn.p.item.add_item_bag3(itbag);
                            }
                            jsar.clear();
                            //
                            jsar = (JSONArray) JSONValue.parse(rs.getString("item4"));
                            for (int i = 0; i < jsar.size(); i++) {
                                JSONArray jsar2 = (JSONArray) JSONValue.parse(jsar.get(i).toString());
                                Item47 itbag = new Item47();
                                itbag.id = Short.parseShort(jsar2.get(0).toString());
                                itbag.quantity = Short.parseShort(jsar2.get(1).toString());
                                itbag.category = 4;
                                conn.p.item.add_item_bag47(4, itbag);
                            }
                            jsar.clear();
                            //
                            jsar = (JSONArray) JSONValue.parse(rs.getString("item7"));
                            for (int i = 0; i < jsar.size(); i++) {
                                JSONArray jsar2 = (JSONArray) JSONValue.parse(jsar.get(i).toString());
                                Item47 itbag = new Item47();
                                itbag.id = Short.parseShort(jsar2.get(0).toString());
                                itbag.quantity = Short.parseShort(jsar2.get(1).toString());
                                itbag.category = 7;
                                conn.p.item.add_item_bag47(7, itbag);

                            }
                            jsar.clear();
                            //
                            int vang_up = rs.getInt("vang");
                            int ngoc_up = rs.getInt("ngoc");
                            conn.p.item.char_inventory(3);
                            conn.p.item.char_inventory(4);
                            conn.p.item.char_inventory(7);

                            Service.send_notice_box(conn, "Nhận thành công giftcode");
                        } else {
                            Service.send_notice_box(conn, "Hành trang phải trống 3 ô trở lên!");
                        }
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                break;
            }
            case 1: {
//                if (conn.user.equals("admin")) {
//                    if (size != 3) {
//                        return;
//                    }
//                    String type = m2.reader().readUTF();
//                    String id = m2.reader().readUTF();
//                    String quantity = m2.reader().readUTF();
//                    if (!(Util.isnumber(id) && Util.isnumber(quantity))) {
//                        Service.send_notice_box(conn, "Dữ liệu nhập không phải số!!");
//                        return;
//                    }
//                    if (conn.p.item.get_bag_able() > 0) {
//                        switch (type) {
//                            case "3": {
//                                short iditem = (short) Integer.parseInt(id);
//                                if (iditem > (ItemTemplate3.item.size() - 1) || iditem < 0) {
//                                    return;
//                                }
//                                Item3 itbag = new Item3();
//                                itbag.id = iditem;
//                                itbag.name = ItemTemplate3.item.get(iditem).getName();
//                                itbag.clazz = ItemTemplate3.item.get(iditem).getClazz();
//                                itbag.type = ItemTemplate3.item.get(iditem).getType();
//                                itbag.level = ItemTemplate3.item.get(iditem).getLevel();
//                                itbag.icon = ItemTemplate3.item.get(iditem).getIcon();
//                                itbag.op = ItemTemplate3.item.get(iditem).getOp();
//                                itbag.color = ItemTemplate3.item.get(iditem).getColor();
//                                itbag.part = ItemTemplate3.item.get(iditem).getPart();
//                                itbag.tier = 0;
//                                itbag.islock = true;
//                                itbag.time_use = 0;
//                                conn.p.item.add_item_bag3(itbag);
//                                conn.p.item.char_inventory(3);
//                                break;
//                            }
//                            case "4": {
//                                short iditem = (short) Integer.parseInt(id);
//                                if (iditem > (ItemTemplate4.item.size() - 1) || iditem < 0) {
//                                    return;
//                                }
//                                Item47 itbag = new Item47();
//                                itbag.id = iditem;
//                                itbag.quantity = Short.parseShort(quantity);
//                                itbag.category = 4;
//                                conn.p.item.add_item_bag47(4, itbag);
//                                conn.p.item.char_inventory(4);
//                                break;
//                            }
//                            case "7": {
//                                short iditem = (short) Integer.parseInt(id);
//                                if (iditem > (ItemTemplate7.item.size() - 1) || iditem < 0) {
//                                    return;
//                                }
//                                Item47 itbag = new Item47();
//                                itbag.id = iditem;
//                                itbag.quantity = Short.parseShort(quantity);
//                                itbag.category = 7;
//                                conn.p.item.add_item_bag47(7, itbag);
//                                conn.p.item.char_inventory(7);
//                                break;
//                            }
//                        }
//                        Service.send_notice_box(conn, "Nhận Item thành công");
//                    }
//                }
                break;
            }
            case 2: {
                break;
            }
            case 3: {
                if (size != 1) {
                    return;
                }
                String vang_join = m2.reader().readUTF();
                if (!(Util.isnumber(vang_join))) {
                    Service.send_notice_box(conn, "Dữ liệu nhập không phải số!!");
                    return;
                }
                int vang_join_vxmm = Integer.parseInt(vang_join);
                if (vang_join_vxmm < 10_000_000 || conn.p.get_vang() < vang_join_vxmm) {
                    Service.send_notice_box(conn, "vàng không đủ!");
                    return;
                }
                if (vang_join_vxmm > 200_000_000) {
                    Service.send_notice_box(conn, "tối đa 200tr vàng!");
                    return;
                }
                Manager.gI().vxmm.join_vxmm(conn.p, vang_join_vxmm);
                break;
            }
            case 4: {
                break;
            }
            case 5: {
                if (size != 1) {
                    return;
                }
                String value = m2.reader().readUTF();
                if (!(Util.isnumber(value))) {
                    Service.send_notice_box(conn, "Dữ liệu nhập không phải số!!");
                    return;
                }
                int coin_exchange = Integer.parseInt(value);
                if (coin_exchange < 0 || coin_exchange > 300_000) {
                    Service.send_notice_box(conn, "Số nhập không hợp lệ, hãy thử lại");
                    return;
                }
                if (conn.p.update_coin(-coin_exchange)) {
                    conn.p.update_ngoc(coin_exchange * 200);
                    conn.p.item.char_inventory(5);
                    Service.send_notice_box(conn, "Đổi thành công");
                    
                }
                break;
            }
            case 6: {
                if (size != 2) {
                    return;
                }
                String value1 = m2.reader().readUTF();
                String value2 = m2.reader().readUTF();
                Pattern p = Pattern.compile("^[a-zA-Z0-9]{5,15}$");
                if (!p.matcher(value1).matches() || !p.matcher(value2).matches()) {
                    Service.send_notice_box(conn, "Ký tự không hợp lệ, hãy thử lại");
                    return;
                }
                //
                // try (Connection connnect = SQL.gI().getConnection();
                // PreparedStatement ps = connnect.prepareStatement(
                // "INSERT INTO `sm_hso2`.`account` (`user`, `pass`, `char`, `status`, `lock`, `coin`) VALUES ('"
                // + value1 + "', '" + value2 + "', '[]', 0, 0, 0)")) {
                // if (!ps.execute()) {
                // connnect.commit();
                // }
                // } catch (SQLException e) {
                // e.printStackTrace();
                // return;
                // }
                String query = "UPDATE `account` SET `user` = '" + value1 + "', `pass` = '" + value2 + "' WHERE `user` = '"
                        + conn.user + "' LIMIT 1";
                try ( Connection connnect = SQL.gI().getConnection();  Statement statement = connnect.createStatement();) {
                    if (statement.executeUpdate(query) > 0) {
                        connnect.commit();
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                    Service.send_notice_box(conn, "Có lỗi xảy ra hoặc tên đã được sử dụng, hãy thử lại");
                    return;
                }
                Message md = new Message(31);
                md.writer().writeUTF(value1);
                md.writer().writeUTF(value2);
                conn.addmsg(md);
                md.cleanup();
                conn.user = value1;
                conn.pass = value2;
                Service.send_notice_box(conn,
                        "Đăng ký thành công tài khoản :\n Tên đăng nhập : " + value1 + "\nMật khẩu : " + value2);
                break;
            }
            case 7: {
                if (size != 1 || conn.p.fusion_material_medal_id == -1) {
                    return;
                }
                String value = m2.reader().readUTF();
                if (!(Util.isnumber(value))) {
                    Service.send_notice_box(conn, "Dữ liệu nhập không phải số!!");
                    return;
                }
                int quant = Integer.parseInt(value);
                int quant_inbag = conn.p.item.total_item_by_id(7, conn.p.fusion_material_medal_id);
                int quant_real = quant_inbag / 5;
                short id_next_material = (short) (conn.p.fusion_material_medal_id + 100);
                String name_next_material = ItemTemplate7.item.get(id_next_material).getName();
                if ((quant_real - quant) >= 0) {
                    if ((quant * 5000) > conn.p.get_vang()) {
                        Service.send_notice_box(conn, "Vàng không đủ!");
                        return;
                    }
                    conn.p.update_vang(-(quant * 5000));
                    conn.p.item.remove(7, conn.p.fusion_material_medal_id, (quant * 5));
                    Item47 it = new Item47();
                    it.id = id_next_material;
                    it.quantity = (short) quant;
                    conn.p.item.add_item_bag47(7, it);
                    conn.p.item.char_inventory(7);
                    //
                    Message m = new Message(-105);
                    m.writer().writeByte(2);
                    m.writer().writeByte(3);
                    m.writer().writeUTF("Chúc mừng bạn nhận được " + quant + " " + name_next_material);
                    m.writer().writeShort(id_next_material);
                    m.writer().writeByte(7);
                    Log.gI().add_log(conn.p.name,
                                    "hợp nl me day het"  + Util.number_format(quant * 5000) + " vàng");
                    conn.addmsg(m);
                    m.cleanup();
                } else {
                    Service.send_notice_box(conn, "Chỉ có thể hợp thành tối đa " + quant_real + " " + name_next_material);
                }
                conn.p.fusion_material_medal_id = -1;
                break;
            }
            case 8: {
            }
            case 9: {
                if (size != 1) {
                    return;
                }
                String value = m2.reader().readUTF();
                if (!(Util.isnumber(value))) {
                    Service.send_notice_box(conn, "Dữ liệu nhập không phải số!!");
                    return;
                }
                int quant = Integer.parseInt(value);
                if (idnpc == 8) {
                    if (quant > conn.p.get_vang()) {
                        Service.send_notice_box(conn, "Vàng không đủ!");
                        return;
                    }
                    conn.p.myclan.member_contribute_vang(conn, quant);
                } else {
                    if (quant > conn.p.get_ngoc()) {
                        Service.send_notice_box(conn, "Ngọc không đủ!");
                        return;
                    }
                    conn.p.myclan.member_contribute_ngoc(conn, quant);
                }
                break;
            }
            case 10: {
                if (Manager.gI().event == 1) {
                    if (size != 1) {
                        return;
                    }
                    String value = m2.reader().readUTF();
                    if (!(Util.isnumber(value))) {
                        Service.send_notice_box(conn, "Dữ liệu nhập không phải số!!");
                        return;
                    }
                    int quant = Integer.parseInt(value);
                    if (conn.p.get_vang() < (quant * 20_000)) {
                        Service.send_notice_box(conn, "Vàng không đủ!");
                        return;
                    }
                    short[] id = new short[]{118, 119, 120, 121, 122};
                    for (int i = 0; i < id.length; i++) {
                        if (conn.p.item.total_item_by_id(4, id[i]) < (quant * 50)) {
                            Service.send_notice_box(conn, (ItemTemplate4.item.get(id[i]).getName() + " không đủ!"));
                            return;
                        }
                    }
                    conn.p.update_vang(-(quant * 20_000));
                    for (int i = 0; i < id.length; i++) {
                        conn.p.item.remove(4, id[i], quant * 50);
                    }
                    Item47 it = new Item47();
                    it.category = 4;
                    it.id = (short) 158;
                    it.quantity = (short) quant;
                    conn.p.item.add_item_bag47(4, it);
                    //
                    conn.p.item.char_inventory(4);
                    conn.p.item.char_inventory(7);
                    conn.p.item.char_inventory(3);
                    //
                    Service.send_notice_box(conn, "Đổi thành công " + quant + " hộp đồ chơi");
                    Log.gI().add_log(conn.p.name,
                                    "doi hop do choi het"  + Util.number_format(quant * 20_000) + " vàng");
                    
                }
                break;
            }
            case 11: {
                if (Manager.gI().event == 1) {
                    if (size != 1) {
                        return;
                    }
                    String value = m2.reader().readUTF();
                    if (!(Util.isnumber(value))) {
                        Service.send_notice_box(conn, "Dữ liệu nhập không phải số!!");
                        return;
                    }
                    int quant = Integer.parseInt(value);
                    //
                    short[] id = new short[]{153, 154, 155, 156};
                    for (int i = 0; i < id.length; i++) {
                        if (conn.p.item.total_item_by_id(4, id[i]) < (quant)) {
                            Service.send_notice_box(conn, (ItemTemplate4.item.get(id[i]).getName() + " không đủ!"));
                            return;
                        }
                    }
                    for (int i = 0; i < id.length; i++) {
                        conn.p.item.remove(4, id[i], quant);
                    }
                    Event_1.add_material(conn.p.name, quant);
                    //
                    conn.p.item.char_inventory(4);
                    conn.p.item.char_inventory(7);
                    conn.p.item.char_inventory(3);
                    //
                    Service.send_notice_box(conn, "Đóng góp nguyên liệu tạo " + quant + " kẹo");
                }
                break;
            }
            case 12: {
                if (Manager.gI().event == 1) {
                    if (size != 1) {
                        return;
                    }
                    String value = m2.reader().readUTF();
                    if (!(Util.isnumber(value))) {
                        Service.send_notice_box(conn, "Dữ liệu nhập không phải số!!");
                        return;
                    }
                    int quant = Integer.parseInt(value);
                    if (conn.p.get_vang() < (quant * 50_000)) {
                        Service.send_notice_box(conn, "Vàng không đủ!");
                        return;
                    }
                    if (conn.p.item.total_item_by_id(4, 162) < (quant * 5)) {
                        Service.send_notice_box(conn, (ItemTemplate4.item.get(162).getName() + " không đủ!"));
                        return;
                    }
                    conn.p.update_vang(-(quant * 50_000));
                    conn.p.item.remove(4, 162, quant * 5);
                    //
                    Item47 it = new Item47();
                    it.category = 4;
                    it.id = 157;
                    it.quantity = (short) quant;
                    conn.p.item.add_item_bag47(4, it);
                    //
                    conn.p.item.char_inventory(4);
                    conn.p.item.char_inventory(7);
                    conn.p.item.char_inventory(3);
                    //
                    Service.send_notice_box(conn, "Đổi thành công " + quant + " túi kẹo");
                    Log.gI().add_log(conn.p.name,
                                    "doi tui keo het"  + Util.number_format(quant * 50_000) + " vàng");
                }
                break;
            }
            case 13: {
                if (size != 1) {
                    return;
                }
                String name = m2.reader().readUTF();
                Pattern p = Pattern.compile("^[a-zA-Z0-9]{6,10}$");
                if (!p.matcher(name).matches()) {
                    Service.send_notice_box(conn, "tên không hợp lệ, nhập lại đi!!");
                    return;
                }
                if (conn.p.myclan != null && !conn.p.myclan.mems.get(0).name.equals(name)) {

                    conn.p.name_mem_clan_to_appoint = name;
                    Service.send_box_input_yesno(conn, 113, "Xác nhận nhường thủ lĩnh cho " + name);
                }
                break;
            }
            case 14: {
                if (size != 1) {
                    return;
                }
                String value = m2.reader().readUTF();
                if (!(Util.isnumber(value))) {
                    Service.send_notice_box(conn, "Dữ liệu nhập không phải số!!");
                    return;
                }
                int coin_exchange = Integer.parseInt(value);
                if (coin_exchange < 0 || coin_exchange > 300_000) {
                    Service.send_notice_box(conn, "Số nhập không hợp lệ, hãy thử lại");
                    return;
                }
                if (conn.p.update_coin(-coin_exchange)) {
                    conn.p.update_vang(coin_exchange * 10_00000);
                    conn.p.item.char_inventory(5);
                    Service.send_notice_box(conn, "Đổi thành công");
                }
                break;
            }
            case 15: {
				if (size != 1) {
					return;
				}
				String value = m2.reader().readUTF();
				if (!(Util.isnumber(value))) {
					Service.send_notice_box(conn, "Dữ liệu nhập không phải số!!");
					return;
				}
				int quant = Integer.parseInt(value);
				int quant_ngoc_can_create = conn.p.item.total_item_by_id(7, conn.p.id_hop_ngoc) / 5;
				if (quant > quant_ngoc_can_create) {
					Service.send_notice_box(conn, "Số lượng trong hành trang không đủ!");
					return;
				}
				int vang_required = GameSrc.get_vang_hopngoc(conn.p.id_hop_ngoc) * quant;
				if (conn.p.get_vang() < vang_required) {
					Service.send_notice_box(conn, "Không đủ " + vang_required + " vàng");
					return;
				}
				conn.p.update_vang(-vang_required);
				conn.p.item.remove(7, conn.p.id_hop_ngoc, (quant * 5));
				Item47 itbag = new Item47();
				itbag.id = (short) (conn.p.id_hop_ngoc + 1);
				itbag.quantity = (short) quant;
				itbag.category = 7;
				conn.p.item.add_item_bag47(7, itbag);
				conn.p.item.char_inventory(4);
				conn.p.item.char_inventory(7);
				conn.p.item.char_inventory(3);
				conn.p.id_hop_ngoc = -1;
				//
                                Log.gI().add_log(conn.p.name,
                                    "hop ngoc het"  + Util.number_format(vang_required) + " vàng");
				Message m = new Message(-100);
				m.writer().writeByte(3);
				m.writer().writeUTF("Nhận được " + quant + " " + ItemTemplate7.item.get(itbag.id).getName());
				m.writer().writeShort(itbag.id);
				m.writer().writeByte(7);
				conn.addmsg(m);
				m.cleanup();
				break;
			}
            
            case 20: {
                if (conn.p.item.get_bag_able() < 1) {
                        Service.send_notice_box(conn, "Hành trang không đủ chỗ trống!");
                        return;
                    }
                String value1 = m2.reader().readUTF();
                String value2 = m2.reader().readUTF();
                Pattern p = Pattern.compile("^[a-zA-Z0-9]{5,15}$");
                if (!p.matcher(value1).matches()) {
                    Service.send_notice_box(conn, "Ký tự không hợp lệ, hãy thử lại");
                    return;
                } else {
                    short[] id_4 = new short[]{2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 5, 5, 5, 5, 5, 5, 5, 5, 52, 279, 281, 294};
                    short[] id_7 = new short[]{0, 4, 23, 34, 39, 352, 357, 362, 367, 372, 377, 382, 387, 392, 397, 402,
                        407, 412,};

                    HashMap<Short, Short> list_4 = new HashMap<>();
                    HashMap<Short, Short> list_7 = new HashMap<>();
                    for (int i = 0; i < 1; i++) {
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
                    Event_1.add_tanghoa(conn.p.name, 1);
                    conn.p.item.remove(4, (151), 1);
                    //conn.p.item.char_inventory(3);
                    conn.p.item.char_inventory(4);
                    conn.p.item.char_inventory(7);
                    String item_receiv = "\n";

                    for (java.util.Map.Entry<Short, Short> en : list_4.entrySet()) {
                        item_receiv += ItemTemplate4.item.get(en.getKey()).getName() + " " + en.getValue() + "\n";
                    }
                    for (java.util.Map.Entry<Short, Short> en : list_7.entrySet()) {
                        item_receiv += ItemTemplate7.item.get(en.getKey()).getName() + " " + en.getValue() + "\n";
                    }
                    Service.send_notice_box(conn, "Tặng Quà Thành Công  và nhận được:" + item_receiv);
                    Manager.gI().chatKTGprocess(  conn.p.name + " Gửi lời chúc : " +value2+ " Đến  " +value1);
                }
                break;
            }
            case 24: {
                if (size != 1) {
                    return;
                }
                String value = m2.reader().readUTF();
                if (!(Util.isnumber(value))) {
                    Service.send_notice_box(conn, "Dữ liệu nhập không phải số!!");
                    return;
                }
                int value_ = Integer.parseInt(value);
                conn.p.tiemnang -= value_;
                conn.p.point1 += value_;
                  MapService.update_in4_2_other_inside(conn.p.map, conn.p);
                Service.send_char_main_in4(conn.p);
                break;
            }
            case 25: {
                if (size != 1) {
                    return;
                }
                String value = m2.reader().readUTF();
                if (!(Util.isnumber(value))) {
                    Service.send_notice_box(conn, "Dữ liệu nhập không phải số!!");
                    return;
                }
                int value_ = Integer.parseInt(value);
                if ((conn.p.point2 + value_) < 32000) {
                conn.p.tiemnang -= value_;
                conn.p.point2 += value_;     
                }
               
                  MapService.update_in4_2_other_inside(conn.p.map, conn.p);
                Service.send_char_main_in4(conn.p);
                Service.send_notice_box(conn, "Tăng thành công!");
                break;
            }
            case 26: {
                if (size != 1) {
                    return;
                }
                String value = m2.reader().readUTF();
                if (!(Util.isnumber(value))) {
                    Service.send_notice_box(conn, "Dữ liệu nhập không phải số!!");
                    return;
                }
                int value_ = Integer.parseInt(value);
                conn.p.tiemnang -= value_;
                conn.p.point3 += value_;
                  MapService.update_in4_2_other_inside(conn.p.map, conn.p);
                Service.send_char_main_in4(conn.p);
                Service.send_notice_box(conn, "Tăng thành công!");
                break;
            }
            case 27: {
                if (size != 1) {
                    return;
                }
                String value = m2.reader().readUTF();
                if (!(Util.isnumber(value))) {
                    Service.send_notice_box(conn, "Dữ liệu nhập không phải số!!");
                    return;
                }
                int value_ = Integer.parseInt(value);
                conn.p.tiemnang -= value_;
                conn.p.point4 += value_;
                  MapService.update_in4_2_other_inside(conn.p.map, conn.p);
                Service.send_char_main_in4(conn.p);
                Service.send_notice_box(conn, "Tăng thành công!");
                break;
            }
            default: {
                Service.send_notice_box(conn, "Đã xảy ra lỗi");
                break;
            }
        }
    }
}
