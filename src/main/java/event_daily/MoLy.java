package event_daily;

import java.io.IOException;
import client.Player;
import core.Service;
import core.Util;
import io.Message;
import template.ItemTemplate3;

public class MoLy {

    public static short[] item_mo_ly = new short[]{2939, 4640, 4639, 4641, 4638, 14};
    public static byte[] item_mo_ly_type = new byte[]{3, 3, 3, 3, 3, 7};
    public static byte[] item_mo_ly_percent = new byte[]{15, 5, 5, 5, 5, 10};

    public static void show_table_to_choose_item(Player p) throws IOException {
        p.id_select_mo_ly = -1;
        Message m = new Message(-91);
        m.writer().writeByte(0);
        m.writer().writeByte(item_mo_ly.length);
        for (int i = 0; i < item_mo_ly.length; i++) {
            m.writer().writeByte(item_mo_ly_type[i]);
            switch (item_mo_ly_type[i]) {
                case 3: {
                    ItemTemplate3 temp = ItemTemplate3.item.get(item_mo_ly[i]);
                    m.writer().writeUTF(temp.getName());
                    m.writer().writeByte(temp.getClazz());
                    m.writer().writeShort(temp.getId());
                    m.writer().writeByte(temp.getType());
                    m.writer().writeShort(temp.getIcon());
                    m.writer().writeByte(0); // tier
                    m.writer().writeShort(10);
                    m.writer().writeByte(temp.getColor());
                    m.writer().writeByte(temp.getOp().size());
                    for (int j = 0; j < temp.getOp().size(); j++) {
                        m.writer().writeByte(temp.getOp().get(j).id);
                        m.writer().writeInt(temp.getOp().get(j).getParam(0));
                    }
                    break;
                }
                case 4:
                case 7: {
                    m.writer().writeShort(item_mo_ly[i]);
                    m.writer().writeShort(1); // quant
                    break;
                }
            }
        }
        p.conn.addmsg(m);
        m.cleanup();
    }

    public static void Lottery_process(Player p, Message m2) throws IOException {
        byte step = m2.reader().readByte();
        byte idSelectedItem = m2.reader().readByte();
        // System.out.println(step);
        // System.out.println(idSelectedItem);
        switch (step) {
            case 1: {
                if (p.item.total_item_by_id(4, 52) < 1) {
                    Service.send_notice_box(p.conn, "Không đủ vé trong hành trang!");
                    return;
                }
                if (p.id_select_mo_ly == -1) {
                    Message m = new Message(-91);
                    m.writer().writeByte(1);
                    m.writer().writeByte(idSelectedItem); // index item
                    byte b = (byte) Util.random(5);
                    m.writer().writeByte(b);
                    p.conn.addmsg(m);
                    m.cleanup();
                    p.id_select_mo_ly = idSelectedItem;
                }
                break;
            }
            case 2: {
                if (p.id_select_mo_ly != -1) {
                    p.item.remove(4, 52, 1);
                    if (MoLy.item_mo_ly_percent[p.id_select_mo_ly] > Util.random(250)) {
                        Message m = new Message(-91);
                        m.writer().writeByte(2);
                        m.writer().writeByte(1); // win
                        m.writer().writeByte(idSelectedItem);
                        m.writer().writeByte(idSelectedItem);
                        p.conn.addmsg(m);
                        m.cleanup();
                        Service.open_box_notice_item(p, "Xin chúc mừng",
                                new short[]{-1, MoLy.item_mo_ly[p.id_select_mo_ly], (short) Util.random(6)},
                                new short[]{(short) Util.random(50, 200), 1, (short) Util.random(1, 5)},
                                new short[]{4, MoLy.item_mo_ly_type[p.id_select_mo_ly], 4});
                    } else {
                        Message m = new Message(-91);
                        m.writer().writeByte(2);
                        m.writer().writeByte(0);
                        byte index_win = 0;
                        while (index_win == idSelectedItem) {
                            index_win = (byte) Util.random(5);
                        }
                        m.writer().writeByte(index_win);
                        m.writer().writeByte(idSelectedItem);
                        p.conn.addmsg(m);
                        m.cleanup();
                        Service.open_box_notice_item(p, "Chúc bạn may mắn lần sau",
                                new short[]{-1, (short) Util.random(6), (short) Util.random(6)}, new short[]{
                            (short) Util.random(50, 200), (short) Util.random(1, 5), (short) Util.random(1, 5)},
                                new short[]{4, 4, 4});
                    }
                    p.id_select_mo_ly = -1;
                 if (p.quest_daily[0] == 5 && p.quest_daily[3] < p.quest_daily[4]) {
                        p.quest_daily[3]++;
                    }
					if (p.nv_tinh_tu[0] == 18 && p.nv_tinh_tu[1] < p.nv_tinh_tu[2]) {
						p.nv_tinh_tu[1]++;
					}
                }
                break;
            }
        }
    }
}
