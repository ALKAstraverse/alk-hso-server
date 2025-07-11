package client;

import core.GameSrc;
import core.Log;
import java.io.IOException;
import core.Manager;
import core.Service;
import core.Util;
import event_daily.CongNap;
import event_daily.Wedding;
import io.Message;
import io.Session;
import java.util.ArrayList;
import java.util.List;
import map.Dungeon;
import map.DungeonManager;
import map.LeaveItemMap;
import map.Map;
import map.MapService;
import template.Clan_mems;
import template.EffTemplate;
import template.Item3;
import template.Item47;
import template.ItemTemplate3;
import template.ItemTemplate4;
import template.ItemTemplate7;
import template.Level;
import template.Option;

public class Process_Yes_no_box {

    public static void process(Session conn, Message m) throws IOException {
        short id = m.reader().readShort(); // id
        if (id != conn.p.id) {
            return;
        }
        byte type = m.reader().readByte(); // type
        byte value = m.reader().readByte(); // value
        if (value != 1) {
            switch (type) {
                case 110: {
                    Player p0 = Map.get_player_by_name(conn.p.in4_wedding[1]);
                    Service.send_notice_box(p0.conn, "rất tiếc " + conn.p.name + " đã từ chối lời cầu hôn của bạn");
                    conn.p.in4_wedding = null;
                    break;
                }
                case 115: {
                    conn.p.id_remove_time_use = -1;
                    break;
                }
                case 120: {
                    Service.send_notice_box(conn, "Cần 20k ngọc!");
                    break;
                }
                case 126: {
                    conn.p.id_buffer_126 = -1;
                    conn.p.id_index_126 = -1;
                    break;
                }
                case 114: {
                    conn.p.id_wing_split = -1;
                    break;
                }
                case 113: {
                    conn.p.name_mem_clan_to_appoint = "";
                    break;
                }
                case 108:
                case 109: {
                    conn.p.it_change_op = null;
                    conn.p.it_change_op_index = -1;
                    break;
                }
            }
        } else {
            switch (type) {
                
                case 107: {
					if (conn.p.nv_tinh_tu[3] < 10) {
						Service.send_notice_box(conn,
						      "Bạn mới hoàn thành " + conn.p.nv_tinh_tu[3] + " / 10 nhiệm vụ tinh tú hôm nay");
						return;
					}
					if (CongNap.CAT[CongNap.NV_CONG_NAP] == 4 && CongNap.ID[CongNap.NV_CONG_NAP] == -1) {
						if (conn.p.get_vang() < 1_000_000) {
							Service.send_notice_box(conn, "Không đủ 1tr vàng");
							return;
						}
						conn.p.update_vang(-1_000_000);
						List<Short> it_ = new ArrayList<>();
						if (conn.p.level < 30) {
							it_.addAll(LeaveItemMap.item2x);
						} else if (conn.p.level < 40) {
							it_.addAll(LeaveItemMap.item3x);
						} else if (conn.p.level < 50) {
							it_.addAll(LeaveItemMap.item4x);
						} else if (conn.p.level < 60) {
							it_.addAll(LeaveItemMap.item5x);
						} else if (conn.p.level < 70) {
							it_.addAll(LeaveItemMap.item6x);
						} else if (conn.p.level < 80) {
							it_.addAll(LeaveItemMap.item7x);
						} else if (conn.p.level < 90) {
							it_.addAll(LeaveItemMap.item8x);
						} else if (conn.p.level < 100) {
							it_.addAll(LeaveItemMap.item9x);
						} else if (conn.p.level < 110) {
							it_.addAll(LeaveItemMap.item10x);
						} else if (conn.p.level < 120) {
							it_.addAll(LeaveItemMap.item11x);
						} else if (conn.p.level < 130) {
							it_.addAll(LeaveItemMap.item12x);
						} else if (conn.p.level < 140) {
							it_.addAll(LeaveItemMap.item13x);
						}
						if (it_.size() < 1) {
							return;
						}
						int id_item_can_drop = it_.get(Util.random(it_.size()));
						int dem = 0;
						if (50 > Util.random(120)) {
							while (dem < 50 && it_.size() > 2 && conn.p.level > 0
							      && !(ItemTemplate3.item.get(id_item_can_drop).getType() == 3)
							      && !(ItemTemplate3.item.get(id_item_can_drop).getType() == 4)
							      && !(ItemTemplate3.item.get(id_item_can_drop).getType() == 5)
							      && !(ItemTemplate3.item.get(id_item_can_drop).getType() == 6)) {
								id_item_can_drop = it_.get(Util.random(it_.size()));
								dem++;
							}
						} else {
							while (dem < 50 && it_.size() > 2 && conn.p.level > 0
							      && !(ItemTemplate3.item.get(id_item_can_drop).getType() == 2
							            && ItemTemplate3.item.get(id_item_can_drop).getClazz() == conn.p.clazz)
							      && !(ItemTemplate3.item.get(id_item_can_drop).getType() == 0
							            && ItemTemplate3.item.get(id_item_can_drop).getClazz() == conn.p.clazz)
							      && !(ItemTemplate3.item.get(id_item_can_drop).getType() == 1
							            && ItemTemplate3.item.get(id_item_can_drop).getClazz() == conn.p.clazz)
							      && !(ItemTemplate3.item.get(id_item_can_drop).getType() == 8
							            && ItemTemplate3.item.get(id_item_can_drop).getClazz() == conn.p.clazz)
							      && !(ItemTemplate3.item.get(id_item_can_drop).getType() == 9
							            && ItemTemplate3.item.get(id_item_can_drop).getClazz() == conn.p.clazz)
							      && !(ItemTemplate3.item.get(id_item_can_drop).getType() == 10
							            && ItemTemplate3.item.get(id_item_can_drop).getClazz() == conn.p.clazz)
							      && !(ItemTemplate3.item.get(id_item_can_drop).getType() == 11
							            && ItemTemplate3.item.get(id_item_can_drop).getClazz() == conn.p.clazz)) {
								id_item_can_drop = it_.get(Util.random(it_.size()));
								dem++;
							}
						}
						if (dem >= 50) {
							return;
						}
						byte color_ = 4;
						byte tier_ = (byte) 0;
						//
						short index_real = 0;
						String name = ItemTemplate3.item.get(id_item_can_drop).getName();
						for (int i = id_item_can_drop - 5; i < id_item_can_drop + 5; i++) {
							if (ItemTemplate3.item.get(i).getName().equals(name)
							      && ItemTemplate3.item.get(i).getColor() == color_) {
								index_real = (short) i;
								break;
							}
						}
						ItemTemplate3 item = ItemTemplate3.item.get(index_real);
						//
						// Message m = new Message(78);
						// if (id == 207) {
						// m.writer().writeUTF("Rương tím");
						// } else if (id == 205) {
						// m.writer().writeUTF("Rương đỏ");
						// }
						// m.writer().writeByte(1); // size
						// for (int i = 0; i < 1; i++) {
						// m.writer().writeUTF(item.getName()); // name
						// m.writer().writeShort(item.getIcon()); // icon
						// m.writer().writeInt(1); // quantity
						// m.writer().writeByte(3); // type in bag
						// m.writer().writeByte(tier_); // tier
						// m.writer().writeByte(color_); // color
						// }
						// m.writer().writeUTF("");
						// m.writer().writeByte(1);
						// m.writer().writeByte(0);
						// conn.addmsg(m);
						// m.cleanup();
						//
						Item3 itbag = new Item3();
						itbag.id = item.getId();
						itbag.name = item.getName();
						itbag.clazz = item.getClazz();
						itbag.type = item.getType();
						itbag.level = item.getLevel();
						itbag.icon = item.getIcon();
						itbag.op = new ArrayList<>();
						itbag.op.addAll(item.getOp());
						itbag.color = item.getColor();
						itbag.part = item.getPart();
						itbag.tier = tier_;
						itbag.islock = false;
						itbag.time_use = 0;
						conn.p.item.add_item_bag3(itbag);
						Service.send_notice_box(conn, "Nhận được " + itbag.name);
						conn.p.item.char_inventory(4);
						conn.p.item.char_inventory(7);
						conn.p.item.char_inventory(3);
					} else if (CongNap.CAT[CongNap.NV_CONG_NAP] == 4 && CongNap.ID[CongNap.NV_CONG_NAP] == -2) {
						if (conn.p.get_ngoc() < 5) {
							Service.send_notice_box(conn, "Không đủ 5 ngọc");
							return;
						}
						conn.p.update_ngoc(-5);
						Item47 itbag = new Item47();
						itbag.id = 459;
						itbag.quantity = 1;
						itbag.category = 7;
						conn.p.item.add_item_bag47(7, itbag);
						Service.send_notice_box(conn, "Nhận 1 " + ItemTemplate7.item.get(itbag.id).getName());
						conn.p.item.char_inventory(4);
						conn.p.item.char_inventory(7);
						conn.p.item.char_inventory(3);
					} else {
						if (CongNap.CAT[CongNap.NV_CONG_NAP] == 4) {
							if (conn.p.item.total_item_by_id(CongNap.CAT[CongNap.NV_CONG_NAP],
							      CongNap.ID[CongNap.NV_CONG_NAP]) < CongNap.NUM[CongNap.NV_CONG_NAP]) {
								Service.send_notice_box(conn, "Không đủ " + CongNap.NUM[CongNap.NV_CONG_NAP] + " "
								      + ItemTemplate4.item.get(CongNap.ID[CongNap.NV_CONG_NAP]).getName());
								return;
							}
						} else {
							if (conn.p.item.total_item_by_id(CongNap.CAT[CongNap.NV_CONG_NAP],
							      CongNap.ID[CongNap.NV_CONG_NAP]) < CongNap.NUM[CongNap.NV_CONG_NAP]) {
								Service.send_notice_box(conn, "Không đủ " + CongNap.NUM[CongNap.NV_CONG_NAP] + " "
								      + ItemTemplate7.item.get(CongNap.ID[CongNap.NV_CONG_NAP]).getName());
								return;
							}
						}
						conn.p.item.remove(CongNap.CAT[CongNap.NV_CONG_NAP], CongNap.ID[CongNap.NV_CONG_NAP],
						      CongNap.NUM[CongNap.NV_CONG_NAP]);
						switch (CongNap.NV_CONG_NAP) {
							case 1: {
								conn.p.update_vang(100_000);
								Service.send_notice_box(conn, "Nhận 100k vàng");
								break;
							}
							case 2: {
								Item47 itbag = new Item47();
								itbag.id = 463;
								itbag.quantity = 1;
								itbag.category = 7;
								conn.p.item.add_item_bag47(7, itbag);
								Service.send_notice_box(conn, "Nhận 1 " + ItemTemplate7.item.get(itbag.id).getName());
								break;
							}
							case 4: {
								Item47 itbag = new Item47();
								itbag.id = 346;
								itbag.quantity = 1;
								itbag.category = 7;
								conn.p.item.add_item_bag47(7, itbag);
								Service.send_notice_box(conn, "Nhận 1 " + ItemTemplate7.item.get(itbag.id).getName());
								break;
							}
							case 5: {
								Item47 itbag = new Item47();
								itbag.id = 14;
								itbag.quantity = 2;
								itbag.category = 7;
								conn.p.item.add_item_bag47(7, itbag);
								Service.send_notice_box(conn, "Nhận 2 " + ItemTemplate7.item.get(itbag.id).getName());
								break;
							}
							case 6: {
								Item47 itbag = new Item47();
								itbag.id = 251;
								itbag.quantity = 20;
								itbag.category = 4;
								conn.p.item.add_item_bag47(4, itbag);
								Service.send_notice_box(conn, "Nhận 20 " + ItemTemplate4.item.get(itbag.id).getName());
								break;
							}
							case 7: {
								Item47 itbag = new Item47();
								itbag.id = 3;
								itbag.quantity = 100;
								itbag.category = 7;
								conn.p.item.add_item_bag47(7, itbag);
								Service.send_notice_box(conn, "Nhận 100 " + ItemTemplate7.item.get(itbag.id).getName());
								break;
							}
							case 8: {
								Item47 itbag = new Item47();
								itbag.id = 131;
								itbag.quantity = 1;
								itbag.category = 4;
								conn.p.item.add_item_bag47(4, itbag);
								Service.send_notice_box(conn, "Nhận 1 " + ItemTemplate4.item.get(itbag.id).getName());
								break;
							}
							case 10: {
								Item47 itbag = new Item47();
								itbag.id = 462;
								itbag.quantity = 1;
								itbag.category = 7;
								conn.p.item.add_item_bag47(7, itbag);
								Service.send_notice_box(conn, "Nhận 1 " + ItemTemplate7.item.get(itbag.id).getName());
								break;
							}
							case 11: {
								Item47 itbag = new Item47();
								itbag.id = 457;
								itbag.quantity = 1;
								itbag.category = 7;
								conn.p.item.add_item_bag47(7, itbag);
								Service.send_notice_box(conn, "Nhận 1 " + ItemTemplate7.item.get(itbag.id).getName());
								break;
							}
							case 12: {
								Item47 itbag = new Item47();
								itbag.id = 9;
								itbag.quantity = 10;
								itbag.category = 7;
								conn.p.item.add_item_bag47(7, itbag);
								Service.send_notice_box(conn, "Nhận 10 " + ItemTemplate7.item.get(itbag.id).getName());
								break;
							}
							case 13: {
								Item47 itbag = new Item47();
								itbag.id = 10;
								itbag.quantity = 10;
								itbag.category = 7;
								conn.p.item.add_item_bag47(7, itbag);
								Service.send_notice_box(conn, "Nhận 10 " + ItemTemplate7.item.get(itbag.id).getName());
								break;
							}
							case 14: {
								Item47 itbag = new Item47();
								itbag.id = 11;
								itbag.quantity = 10;
								itbag.category = 7;
								conn.p.item.add_item_bag47(7, itbag);
								Service.send_notice_box(conn, "Nhận 10 " + ItemTemplate7.item.get(itbag.id).getName());
								break;
							}
							case 15: {
								Item47 itbag = new Item47();
								itbag.id = 461;
								itbag.quantity = 1;
								itbag.category = 7;
								conn.p.item.add_item_bag47(7, itbag);
								Service.send_notice_box(conn, "Nhận 1 " + ItemTemplate7.item.get(itbag.id).getName());
								break;
							}
							case 16: {
								Item47 itbag = new Item47();
								itbag.id = 460;
								itbag.quantity = 1;
								itbag.category = 7;
								conn.p.item.add_item_bag47(7, itbag);
								Service.send_notice_box(conn, "Nhận 1 " + ItemTemplate7.item.get(itbag.id).getName());
								break;
							}
						}
						conn.p.item.char_inventory(4);
						conn.p.item.char_inventory(7);
						conn.p.item.char_inventory(3);
					}
					break;
				}
                
                
                case 108:
                case 109: {
                    for (int i = 0; i < conn.p.item.wear.length; i++) {
                        if (conn.p.item.wear[i] != null && conn.p.it_change_op != null
                                && conn.p.item.wear[i].id == conn.p.it_change_op.id) {
                            switch (i) {
                                case 0:
                                case 1:
                                case 6:
                                case 7: {
                                    if (type == 108) {
                                        if (conn.p.get_ngoc() < 10_000) {
                                            Service.send_notice_box(conn, "Không đủ 10k ngọc");
                                            return;
                                        }
                                        if (conn.p.get_vang() < 100_000_000) {
                                            Service.send_notice_box(conn, "Không đủ 100tr vàng");
                                            return;
                                        }
                                        conn.p.update_ngoc(-10_000);
                                        conn.p.update_vang(-100_000_000);

                                    } else {
                                        if (conn.p.get_ngoc() < 5_000) {
                                            Service.send_notice_box(conn, "Không đủ 5k ngọc");
                                            return;
                                        }
                                        if (conn.p.get_vang() < 50_000_000) {
                                            Service.send_notice_box(conn, "Không đủ 50tr vàng");
                                            return;
                                        }
                                        conn.p.update_ngoc(-5_000);
                                        conn.p.update_vang(-50_000_000);

                                    }
                                    break;
                                }
                                case 2:
                                case 3:
                                case 4:
                                case 8:
                                case 9:
                                case 10:
                                case 11: 
                                case 13:
                                case 14:
                                case 15:
                                case 16:
                                case 17:
                                case 18:{
                                    if (type == 108) {
                                        if (conn.p.get_ngoc() < 10_000) {
                                            Service.send_notice_box(conn, "Không đủ 10k ngọc");
                                            return;
                                        }
                                        if (conn.p.get_vang() < 100_000_000) {
                                            Service.send_notice_box(conn, "Không đủ 100tr vàng");
                                            return;
                                        }
                                        conn.p.update_ngoc(-10_000);
                                        conn.p.update_vang(-100_000_000);
                                    } else {
                                        if (conn.p.get_ngoc() < 5_000) {
                                            Service.send_notice_box(conn, "Không đủ 5k ngọc");
                                            return;
                                        }
                                        if (conn.p.get_vang() < 50_000) {
                                            Service.send_notice_box(conn, "Không đủ 50tr vàng");
                                            return;
                                        }
                                        conn.p.update_ngoc(-5_000);

                                    }
                                    break;
                                }
                                default: {
                                    Service.send_notice_box(conn, "Không thể sử dụng đối với loại vật phẩm này");
                                    conn.p.it_change_op = null;
                                    conn.p.it_change_op_index = -1;
                                    return;
                                }
                            }
                            conn.p.item.wear[i] = conn.p.it_change_op;
                            conn.p.item.char_inventory(5);
                            Service.send_notice_box(conn, "Đổi thành công!");
                            break;
                        }
                    }
                    conn.p.it_change_op = null;
                    conn.p.it_change_op_index = -1;
                    Service.send_wear(conn.p);
                    Service.send_char_main_in4(conn.p);
                    break;
                }
                case 112: {
                    Wedding temp = Wedding.get_obj(conn.p.name);
                    if (temp.exp < Level.entrys.get(temp.it.tier).exp) {
                        Service.send_notice_box(conn, "chưa đủ 100% exp!");
                        return;
                    }
                    long vang_req = (5 * (temp.it.tier + 1)) * 1_000_000L;
                    int ngoc_req = (5 * (temp.it.tier + 1)) * 1_000;
                    if (conn.p.get_vang() < vang_req) {
                        Service.send_notice_box(conn, "chưa đủ " + vang_req + " vàng!");
                        return;
                    }
                    if (conn.p.get_ngoc() < ngoc_req) {
                        Service.send_notice_box(conn, "chưa đủ " + ngoc_req + " ngọc!");
                        return;
                    }
                    conn.p.update_vang(-vang_req);
                    conn.p.item.char_inventory(5);
                    boolean suc = 50 > Util.random(100);
                    if (suc) {
                        temp.exp -= Level.entrys.get(temp.it.tier).exp;
                        temp.it.tier++;
                        Service.send_notice_box(conn, "nâng cấp thành công lên +" + temp.it.tier);
                        conn.p.item.wear[23] = temp.it;
                        Service.send_wear(conn.p);
                        Service.send_char_main_in4(conn.p);
                        MapService.update_in4_2_other_inside(conn.p.map, conn.p);
                        //
                        Player p0 = Map.get_player_by_name(temp.name_1.equals(conn.p.name) ? temp.name_2 : temp.name_1);
                        if (p0 != null) {
                            p0.item.wear[23] = temp.it;
                            Service.send_wear(p0);
                            Service.send_char_main_in4(p0);
                            MapService.update_in4_2_other_inside(p0.map, p0);
                        }
                    } else {
                        Service.send_notice_box(conn, "nâng cấp thất bại!");
                    }
                    break;
                }
                case 111: {
                    Wedding temp = Wedding.get_obj(conn.p.name);
                    conn.p.item.wear[23] = null;
                    Service.send_wear(conn.p);
                    Service.send_char_main_in4(conn.p);
                    MapService.update_in4_2_other_inside(conn.p.map, conn.p);
                    Service.send_notice_box(conn, "Ly hôn thành công");
                    conn.p.it_wedding = null;
                    //
                    Player p0 = Map.get_player_by_name(temp.name_1.equals(conn.p.name) ? temp.name_2 : temp.name_1);
                    if (p0 != null) {
                        p0.item.wear[23] = null;
                        Service.send_wear(p0);
                        Service.send_char_main_in4(p0);
                        MapService.update_in4_2_other_inside(p0.map, p0);
                        Service.send_notice_box(p0.conn, conn.p.name + " đã rời xa bạn");
                        p0.it_wedding = null;
                    }
                    Wedding.remove_wed(temp);
                    break;
                }
                case 110: {
                    Player p0 = Map.get_player_by_name(conn.p.in4_wedding[1]);
                    Wedding.add_new(Integer.parseInt(conn.p.in4_wedding[0]), p0, conn.p);
                    conn.p.in4_wedding = null;
                    Service.send_notice_box(p0.conn, "chúc mừng " + conn.p.name + " trở thành bạn đời của bạn");
                    Service.send_notice_box(conn, "chúc mừng " + p0.name + " trở thành bạn đời của bạn");
                    break;
                }
                case 113: {
                    if (conn.p.name_mem_clan_to_appoint.isEmpty()) {
                        return;
                    }
                    if (conn.p.myclan != null && conn.p.myclan.mems.get(0).name.equals(conn.p.name)) {
                        boolean suc = false;
                        for (int i = 1; i < conn.p.myclan.mems.size(); i++) {
                            if (conn.p.myclan.mems.get(i).name.equals(conn.p.name_mem_clan_to_appoint)) {
                                Clan_mems temp = conn.p.myclan.mems.get(0);
                                //
                                conn.p.myclan.mems.get(i).mem_type = 127;
                                conn.p.myclan.mems.get(0).mem_type = 122;
                                //
                                conn.p.myclan.mems.set(0, conn.p.myclan.mems.get(i));
                                conn.p.myclan.mems.set(i, temp);

                                //
                                MapService.update_in4_2_other_inside(conn.p.map, conn.p);
                                MapService.send_in4_other_char(conn.p.map, conn.p, conn.p);
                                Service.send_char_main_in4(conn.p);

                                Player p0 = Map.get_player_by_name(conn.p.myclan.mems.get(0).name);
                                if (p0 != null) {
                                    MapService.update_in4_2_other_inside(p0.map, p0);
                                    MapService.send_in4_other_char(p0.map, p0, p0);
                                    Service.send_char_main_in4(p0);
                                }
                                //
                                suc = true;
                                break;
                            }
                        }
                        if (suc) {
                            Service.send_notice_box(conn, "Thành công!");
                        } else {
                            Service.send_notice_box(conn, "Tên không tồn tại");
                        }
                    } else {
                        Service.send_notice_box(conn, "Đã xảy ra lỗi");
                    }
                    break;
                }
                case 114: {
                    Item3 item = null;
                    int count = 0;
                    for (int i = 0; i < conn.p.item.bag3.length; i++) {
                        Item3 it = conn.p.item.bag3[i];
                        if (it != null && it.type == 7 && it.tier > 0) {
                            if (count == conn.p.id_wing_split) {
                                item = it;
                                break;
                            }
                            count++;
                        }
                    }
                    if (item != null) {

                        int quant1 = 40;
                        int quant2 = 10;
                        int quant3 = 50;
                        for (int i = 0; i < item.tier; i++) {
                            quant1 += GameSrc.wing_upgrade_material_long_khuc_xuong[i];
                            quant2 += GameSrc.wing_upgrade_material_kim_loai[i];
                            quant3 += GameSrc.wing_upgrade_material_da_cuong_hoa[i];
                            if ((i + 1) == 10 || (i + 1) == 20 || (i + 1) == 30) {
                                item.part--;
                            }
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
                        short[] id_ = new short[]{8, 9, 10, 11, 3, 0};
                        int[] quant_ = new int[]{quant1, quant1, quant1, quant1, quant2, quant3};
                        for (int i = 0; i < id_.length; i++) {
                            Item47 it = new Item47();
                            it.category = 7;
                            it.id = id_[i];
                            it.quantity = (short) quant_[i];
                            conn.p.item.add_item_bag47(7, it);
                        }
                        //
//                                                item = null;
                        count = 0;
                        for (int i = 0; i < conn.p.item.bag3.length; i++) {

                            Item3 it = conn.p.item.bag3[i];
                            if (it != null && it.type == 7 && it.tier > 0) {
                                if (count == conn.p.id_wing_split) {
                                    conn.p.item.bag3[i] = null;
                                    break;
                                }
                                count++;
                            }
                        }
                        conn.p.id_wing_split = -1;
//                                                for (int i = 0; i < item.op.size(); i++) {
//                                                if ((item.op.get(i).id > 26 || item.op.get(i).id < 23)
//							      && (item.op.get(i).id != 41 && item.op.get(i).id != 42)) {
//                                                    item.op.remove(i--);
//                                                }
//                                            }
                        //
                        conn.p.item.char_inventory(4);
                        conn.p.item.char_inventory(7);
                        conn.p.item.char_inventory(3);
                        Service.send_notice_box(conn, "Thành công");
                    } else {
                        Service.send_notice_box(conn, "Có lỗi xảy ra, hãy thử lại!");
                        conn.p.id_wing_split = -1;
                    }
                    break;
                }
                case 9: {
                    if (Map.is_map_chien_truong(conn.p.map.map_id)) {
                         if (conn.p.get_vang() >= conn.p.vang_ct) {
                            conn.p.isdie = false;
                            conn.p.hp = conn.p.body.get_max_hp();
                            conn.p.mp = conn.p.body.get_max_mp();
                            conn.p.update_vang(-conn.p.vang_ct);
                            conn.p.vang_ct = (conn.p.vang_ct * 15)/10;
                            conn.p.item.char_inventory(5);
                            Service.send_char_main_in4(conn.p);
                            // chest in4
                            Service.send_combo(conn);
                            Service.usepotion(conn.p, 0, conn.p.body.get_max_hp());
                            Service.usepotion(conn.p, 1, conn.p.body.get_max_mp());
                        } else {
                            Service.send_notice_box(conn, "Không đủ vàng để thực hiện");
                        }
                    } else {
                        if (conn.p.get_ngoc() >= 5) {
                            conn.p.isdie = false;
                            conn.p.hp = conn.p.body.get_max_hp();
                            conn.p.mp = conn.p.body.get_max_mp();
                            conn.p.update_ngoc(-5);
                            conn.p.item.char_inventory(5);
                            Service.send_char_main_in4(conn.p);
                            // chest in4
                            Service.send_combo(conn);
                            Service.usepotion(conn.p, 0, conn.p.body.get_max_hp());
                            Service.usepotion(conn.p, 1, conn.p.body.get_max_mp());
                        } else {
                            Service.send_notice_box(conn, "Không đủ ngọc để thực hiện");
                        }
                    }
                    break;
                }
                case 85: {
                    Manager.gI().tx.send_in4(conn.p);
                    break;
                }
                case 86: {
                    Manager.gI().vxmm.send_in4(conn.p);
                    break;
                }
                case 115: {
                    if (conn.p.id_remove_time_use != -1) {
                        Item3 it = conn.p.item.bag3[conn.p.id_remove_time_use];
                        if (it != null && it.time_use > 0) {
                            int ngoc_ = conn.p.get_ngoc();
                            if (ngoc_ > 4) {
                                long price = it.time_use - System.currentTimeMillis();
                                price /= 30_600_000;
                                price = (price > 4) ? (price + 1) : 5;
                                boolean ch = false;
                                if (ngoc_ >= price) {
                                    ch = true;
                                } else {
                                    price = ngoc_;
                                }
                                it.time_use -= (price * 30_600_000);
                                conn.p.update_ngoc(-price);
                                conn.p.item.char_inventory(4);
                                conn.p.item.char_inventory(7);
                                conn.p.item.char_inventory(3);
                                conn.p.id_remove_time_use = -1;
                                if (ch) {
                                    Service.send_notice_box(conn, "Nhận được " + it.name + " +" + it.tier + "!");
                                }
                            } else {
                                Service.send_notice_box(conn, "Tối thiểu 5 ngọc!");
                            }
                        }
                    }
                    break;
                }
                case 116: {
                    if (conn.p.myclan != null && conn.p.myclan.mems.get(0).name.equals(conn.p.name)) {
                        conn.p.myclan.remove_all_mem();
                        conn.p.myclan.remove_mem(conn.p.name);
                        conn.p.myclan = null;
                        MapService.update_in4_2_other_inside(conn.p.map, conn.p);
                        MapService.send_in4_other_char(conn.p.map, conn.p, conn.p);
                        Service.send_char_main_in4(conn.p);
                        Service.send_notice_box(conn, "Hủy bang thành công");
                    }
                    break;
                }
                case 40: {
    if (conn.p != null && conn.p.myclan != null) {
        conn.p.myclan.clan_process(conn, null, 22);
    } else {
        Service.send_notice_box(conn, "Bạn không thuộc bang hội nào!");
    }
    break;
}
                case 117: {
                    if (conn.p.myclan != null) {
                        conn.p.myclan.remove_mem(conn.p.name);
                        conn.p.myclan = null;
                        MapService.update_in4_2_other_inside(conn.p.map, conn.p);
                        MapService.send_in4_other_char(conn.p.map, conn.p, conn.p);
                        Service.send_char_main_in4(conn.p);
                        Service.send_notice_box(conn, "Rời bang thành công");
                    }
                    break;
                }
                case 118: {
                    if (conn.p.myclan.mems.get(0).name.equals(conn.p.name)) {
                        if ((Clan.vang_upgrade[1] * conn.p.myclan.level) > conn.p.myclan.get_vang()) {
                            Service.send_notice_box(conn, "Không đủ vàng để thực hiện");
                            return;
                        }
                        if ((Clan.ngoc_upgrade[1] * conn.p.myclan.level) > conn.p.myclan.get_ngoc()) {
                            Service.send_notice_box(conn, "Không đủ ngọc để thực hiện");
                            return;
                        }
                        conn.p.myclan.update_vang(-Clan.vang_upgrade[1] * conn.p.myclan.level);
                        conn.p.myclan.update_ngoc(-Clan.ngoc_upgrade[1] * conn.p.myclan.level);
                        conn.p.myclan.level++;
                        conn.p.myclan.exp = 0;
                        if (conn.p.myclan.max_mem < 45 && conn.p.myclan.level % 5 == 0) {
                            conn.p.myclan.max_mem += 5;

                        }
                        Service.send_notice_box(conn, "Nâng bang lên cấp " + conn.p.myclan.level + " thành công");
                    }
                    break;
                }
                case 119: {
    Dungeon d = DungeonManager.get_list(conn.p.party != null ? conn.p.party.getLeader().name : conn.p.name);
    if (d == null) {
        try {
            d = new Dungeon();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (d != null) {
            List<Player> playersToEnter = new ArrayList<>();

            if (conn.p.party != null) {
                if (!conn.p.party.isLeader(conn.p)) {
                    Service.send_notice_box(conn, "Chỉ đội trưởng mới được xác nhận.");
                    return;
                }

                for (Player p : conn.p.party.get_mems()) {
                    if (p.level < 30 || p.point_active[0] < 1) {
                        Service.send_notice_box(conn, p.name + " không đủ điều kiện vào phó bản.");
                        return;
                    }
                    playersToEnter.add(p);
                }
            } else {
                if (conn.p.level < 30 || conn.p.point_active[0] < 1) {
                    Service.send_notice_box(conn, "Bạn chưa đủ điều kiện vào phó bản.");
                    return;
                }
                playersToEnter.add(conn.p);
            }

            for (Player p : playersToEnter) {
                p.point_active[0]--;
                p.dungeon = d;
                p.x = 684;
                p.y = 672;
                MapService.leave(p.map, p);
                p.map = d.template;
                d.template.players.add(p);
                MapService.enter(p.map, p);
                d.send_map_data(p);              // Gửi thông tin map
                d.send_mob_move_when_exit(p);    // Gửi vị trí quái (quan trọng!)
            }

            d.name_party = conn.p.party != null ? conn.p.party.getLeader().name : conn.p.name;
            d.setMode(0); // độ khó mặc định
            DungeonManager.add_list(d);

            // nhiệm vụ tinh tú
            for (Player p : playersToEnter) {
                if (p.nv_tinh_tu[0] == 12 && p.nv_tinh_tu[1] < p.nv_tinh_tu[2]) p.nv_tinh_tu[1]++;
                if (p.nv_tinh_tu[0] == 13 && p.nv_tinh_tu[1] < p.nv_tinh_tu[2]) p.nv_tinh_tu[1]++;
            }

        } else {
            Service.send_notice_box(conn, "Lỗi tạo phó bản, hãy thử lại sau!");
        }

    } else {
        // Nếu dungeon đã tồn tại → chỉ cho vào lại
        MapService.leave(conn.p.map, conn.p);
        conn.p.map = d.template;
        conn.p.dungeon = d;
        d.template.players.add(conn.p);
        MapService.enter(conn.p.map, conn.p);
        d.send_map_data(conn.p);
        d.send_mob_move_when_exit(conn.p);
    }
    break;
}
                case 120: {
                    if (conn.p.get_ngoc() < 20000) {
                        Service.send_notice_box(conn, "20k ngọc còn không có thì không xứng đáng làm anh hùng!");
                        return;
                    }
                    Message m12 = new Message(-53);
                    m12.writer().writeShort(0);
                    String[] txt = new String[]{"Tên (4-20 ký tự) :", "Tên viết tắt (3 ký tự) :"};
                    m12.writer().writeByte(txt.length);
                    for (String string : txt) {
                        m12.writer().writeUTF(string);
                    }
                    m12.writer().writeUTF("Đăng ký bang");
                    m12.writer().writeUTF("Bang hội");
                    conn.addmsg(m12);
                    m12.cleanup();
                    break;
                }
                case 121: {
                    if (conn.p.get_ngoc() < 1000) {
                        Service.send_notice_box(conn, "Không đủ 1000 ngọc!");
                        return;
                    }
                    conn.p.update_ngoc(-1000);
                    conn.p.item.char_inventory(5);
                    EffTemplate ef = conn.p.get_eff(-126);
                    if (ef != null) {
                        long time_extra = (ef.time - System.currentTimeMillis()) + (1000 * 60 * 60 * 2);
                        if (time_extra > (1000 * 60 * 60 * 24 * 3)) {
                            time_extra = 1000 * 60 * 60 * 24 * 3;
                        }
                        conn.p.add_eff(-126, 99, (int) time_extra);
                    } else {
                        conn.p.add_eff(-126, 99, (1000 * 60 * 60 * 2));
                    }
                    Service.send_notice_box(conn, "Đăng ký thành công");
                    break;
                }
                case 122: {
                    int fee = 100 * conn.p.item.bag3[conn.p.item_replace].tier;
                    if (conn.p.item.total_item_by_id(7, 348) < 2) {
                    Service.send_notice_box(conn, "Không đủ 2 viên đá thạch anh cấp 3");
                    return;
                } else {
                    Service.send_notice_box(conn, "Không đủ 2 viên đá thạch anh cấp 3");
                }
                    if (conn.p.get_ngoc() < fee) {
                        Service.send_notice_box(conn, "Không đủ " + fee + " ngọc!");
                        return;
                    }
                    conn.p.item.bag3[conn.p.item_replace2].tier = conn.p.item.bag3[conn.p.item_replace].tier;
                    conn.p.item.bag3[conn.p.item_replace].tier = 0;
                    if (conn.p.item.bag3[conn.p.item_replace2].type == 5
                            && conn.p.item.bag3[conn.p.item_replace2].tier >= 9) {
                        for (Option op_ : conn.p.item.bag3[conn.p.item_replace2].op) {
                            if (op_.id == 37 && op_.getParam(conn.p.item.bag3[conn.p.item_replace2].tier) > 1) {
                                op_.setParam(op_.getParam(conn.p.item.bag3[conn.p.item_replace2].tier));
                            }
                        }
                    }
                    conn.p.update_ngoc(-fee);
                    conn.p.item.char_inventory(3);
                    Service.send_notice_box(conn, "Chuyển hóa thành công!");
                    Message m3 = new Message(73);
                    m3.writer().writeByte(0);
                    m3.writer().writeShort(conn.p.item_replace2);
                    m3.writer().writeByte(0);
                    conn.addmsg(m3);
                    m3.cleanup();
                    //
                    m3 = new Message(73);
                    m3.writer().writeByte(0);
                    m3.writer().writeShort(conn.p.item_replace);
                    m3.writer().writeByte(1);
                    conn.addmsg(m3);
                    m3.cleanup();
                    //
                    break;
                }
                case 123: {
                    break;
                }
                case 124: {
                    conn.p.rest_skill_point();
                    conn.p.item.remove(4, 7, 1);
                    conn.p.item.char_inventory(4);
                    Service.send_notice_box(conn, "Tẩy điểm kỹ năng thành công");
                    break;
                }
                case 125: {
                    conn.p.rest_potential_point();
                    conn.p.item.remove(4, 6, 1);
                    conn.p.item.char_inventory(4);
                    Service.send_notice_box(conn, "Tẩy điểm tiềm năng thành công");
                    break;
                }
                case 126: {
                    if (conn.p.id_buffer_126 != -1) {
                        Item3 temp3 = conn.p.item.bag3[conn.p.id_buffer_126];
                        temp3.islock = true;
                        switch (temp3.type) {
                            case 0: // coat
                            case 1: // pant
                            case 2: // crown
                            case 3: // grove
                            case 4: // ring
                            case 5: // chain
                            case 6: // shoes
                            case 7: // wing
                            case 15:
                            case 8:
                            case 9:
                            case 10:
                            case 16:
                            case 21:
                            case 22:
                            case 23:
                            case 24:
                            case 25:
                            case 26:
                            case 11: { // weapon
                                conn.p.player_wear(conn, temp3, conn.p.id_buffer_126, conn.p.id_index_126);
                                break;
                            }
                            default: {
                                Service.send_notice_nobox_white(conn, "3Chưa có chức năng này");
                                break;
                            }
                        }
                    }
                    conn.p.id_buffer_126 = -1;
                    conn.p.id_index_126 = -1;
                    break;
                }
                case 127: {
                    if (conn.p.get_ngoc() < 200) {
                        Service.send_notice_box(conn, "Cần 200 Ngọc!");
                        
                        return;
                    }
                    if (conn.p.point_active[0] != 0) {
                        Service.send_notice_box(conn, "Bạn vẫn còn lượt đi, Đi hết đi rồi mua!");
                        return;
                    }
                    conn.p.point_active[0] += 1;
                    break;
                }
            }
        }
    }
}
