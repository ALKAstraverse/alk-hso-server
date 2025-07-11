package client;

import core.Log;
import java.io.IOException;
import java.util.List;
import core.Service;
import io.Message;
import template.Item3;
import template.Item47;
import template.ItemTemplate4;
import template.ItemTemplate7;

public class Item {

    private Player p;
    public Item3[] bag3;
    public Item3[] box3;
    public Item3[] wear;
    public List<Item47> bag47;
    public List<Item47> box47;

    public Item(Player player) {
        this.p = player;
    }

    public void char_inventory(int type) throws IOException {
        switch (type) {
            case 3: {
                Message m = new Message(16);
                m.writer().writeByte(0);
                m.writer().writeByte(3);
                m.writer().writeLong(p.get_vang());
                m.writer().writeInt(p.get_ngoc());
                m.writer().writeByte(3);
                m.writer().writeByte(total_item_by_type(3));
                for (int i = 0; i < bag3.length; i++) {
                    Item3 temp = bag3[i];
                    if (temp != null) {
                        m.writer().writeUTF(temp.name);
                        m.writer().writeByte(temp.clazz); // item clazz
                        m.writer().writeShort(i); // id : index
                        m.writer().writeByte(temp.type); // type only
                        m.writer().writeShort(temp.icon); // idicon
                        m.writer().writeByte(temp.tier); // tier
                        m.writer().writeShort(temp.level); // level
                        m.writer().writeByte(temp.color); // color name
                        m.writer().writeByte(1); // can sell
                        m.writer().writeByte(temp.islock ? 0 : 1); // can trade
                        m.writer().writeByte(temp.op.size()); // size
                        for (int j = 0; j < temp.op.size(); j++) {
                            m.writer().writeByte(temp.op.get(j).id);
                            m.writer().writeInt(temp.op.get(j).getParam(temp.tier));
                        }
                        //
                        if (temp.time_use != 0) {
                            long time_use = temp.time_use - System.currentTimeMillis();
                            time_use /= 60_000;
                            m.writer().writeInt((int) ((time_use > 0) ? time_use : 1)); // time use
                        } else {
                            m.writer().writeInt(0); // time use
                        }
                        m.writer().writeByte(temp.islock ? (byte) 1 : (byte) 0); // islock
                        m.writer().writeByte(0); // b10
                        m.writer().writeByte(0); // canShell_notCanTrade
                    }
                }
                p.conn.addmsg(m);
                m.cleanup();
                break;
            }
            case 5: {
                Message m = new Message(16);
                m.writer().writeByte(0);
                m.writer().writeByte(5);
                m.writer().writeLong(p.get_vang());
                m.writer().writeInt(p.get_ngoc());
                m.writer().writeByte(5);
                m.writer().writeByte(0); // size item quest
                p.conn.addmsg(m);
                m.cleanup();
                break;
            }
            case 4:
            case 7: {
                Message m = new Message(16);
                m.writer().writeByte(0);
                m.writer().writeByte(type);
                m.writer().writeLong(p.get_vang());
                m.writer().writeInt(p.get_ngoc());
                m.writer().writeByte(type);
                m.writer().writeByte(total_item_by_type(type));
                for (int i = 0; i < bag47.size(); i++) {
                    Item47 temp = bag47.get(i);
                    if (temp.category == type) {
                        if (type == 4 && temp.id >= ItemTemplate4.item.size()) {
                            temp.id=0;
                        }
                        m.writer().writeShort(temp.id);
                        m.writer().writeShort(temp.quantity);
                        m.writer().writeByte(1);
                        m.writer().writeByte(0);
                    }
                }
                p.conn.addmsg(m);
                m.cleanup();
                break;
            }
        }
    }

    public void char_chest(int type) throws IOException {
        switch (type) {
            case 3: {
                Message m = new Message(65);
                m.writer().writeByte(p.maxbox);
                m.writer().writeByte(0);
                m.writer().writeByte(3);
                m.writer().writeByte(3);
                m.writer().writeByte(total_item_by_type_box(3));
                for (int i = 0; i < box3.length; i++) {
                    Item3 temp = box3[i];
                    if (temp != null) {
                        m.writer().writeUTF(temp.name);
                        m.writer().writeByte(temp.clazz); // item clazz
                        m.writer().writeShort(i); // id : index
                        m.writer().writeByte(temp.type); // type only
                        m.writer().writeShort(temp.icon); // idicon
                        m.writer().writeByte(temp.tier); // tier
                        m.writer().writeShort(temp.level); // level
                        m.writer().writeByte(temp.color); // color name
                        m.writer().writeByte(1); // can sell
                        m.writer().writeByte(temp.islock ? 0 : 1); // can trade
                        m.writer().writeByte(temp.op.size()); // size
                        for (int j = 0; j < temp.op.size(); j++) {
                            m.writer().writeByte(temp.op.get(j).id);
                            m.writer().writeInt(temp.op.get(j).getParam(temp.tier));
                        }
                        if (temp.time_use != 0) {
                            long time_use = temp.time_use - System.currentTimeMillis();
                            time_use /= 3_600_000;
                            m.writer().writeInt((int) ((time_use > 0) ? time_use : 1)); // time use
                        } else {
                            m.writer().writeInt(0); // time use
                        }
                        m.writer().writeByte(temp.islock ? (byte) 1 : (byte) 0); // islock
                        m.writer().writeByte(0); // b10
                        m.writer().writeByte(0); // canShell_notCanTrade
                    }
                }
                p.conn.addmsg(m);
                m.cleanup();
                break;
            }
            case 5: {
                Message m = new Message(65);
                m.writer().writeByte(p.maxbox);
                m.writer().writeByte(0);
                m.writer().writeByte(5);
                m.writer().writeByte(5);
                m.writer().writeByte(0); // size item quest
                p.conn.addmsg(m);
                m.cleanup();
                break;
            }
            case 4:
            case 7: {
                Message m = new Message(65);
                m.writer().writeByte(p.maxbox);
                m.writer().writeByte(0);
                m.writer().writeByte(type);
                m.writer().writeByte(type);
                m.writer().writeByte(total_item_by_type_box(type));
                for (int i = 0; i < box47.size(); i++) {
                    Item47 temp = box47.get(i);
                    if (temp.category == type) {
                        m.writer().writeShort(temp.id);
                        m.writer().writeShort(temp.quantity);
                        m.writer().writeByte(1);
                        m.writer().writeByte(0);
                    }
                }
                p.conn.addmsg(m);
                m.cleanup();
                break;
            }
        }
    }

    private int total_item_by_type(int type) {
        int quantity = 0;
        switch (type) {
            case 3: {
                for (int i = 0; i < bag3.length; i++) {
                    if (bag3[i] != null) {
                        quantity++;
                    }
                }
                break;
            }
            case 4:
            case 7: {
                for (int i = 0; i < bag47.size(); i++) {
                    Item47 temp = bag47.get(i);
                    if (temp.category == type) {
                        quantity++;
                    }
                }
                break;
            }
        }
        return quantity;
    }

    private int total_item_by_type_box(int type) {
        int quantity = 0;
        switch (type) {
            case 3: {
                for (int i = 0; i < box3.length; i++) {
                    if (box3[i] != null) {
                        quantity++;
                    }
                }
                break;
            }
            case 4:
            case 7: {
                for (int i = 0; i < box47.size(); i++) {
                    Item47 temp = box47.get(i);
                    if (temp.category == type) {
                        quantity++;
                    }
                }
                break;
            }
        }
        return quantity;
    }

    public int get_bag_able() {
        return (p.maxbag - (total_item_by_type(3) + total_item_by_type(4) + total_item_by_type(7)));
    }

    public int get_box_able() {
        return (p.maxbox - (total_item_by_type_box(3) + total_item_by_type_box(4) + total_item_by_type_box(7)));
    }

    public void add_item_bag47(int type, Item47 item) {
        switch (type) {
            case 4:
            case 7: {
                item.category = (byte) type;
                int size = total_item_by_id(type, item.id);
                if (size > 0) {
                    Item47 itbuffer = item;
                    size += item.quantity;
                    if (size > 32000) {
                        itbuffer.quantity = 32000;
                    } else {
                        itbuffer.quantity = (short) size;
                    }
                    int index = -1;
                    for (int i = 0; i < bag47.size(); i++) {
                        if (bag47.get(i).category == type && bag47.get(i).id == item.id) {
                            index = i;
                            break;
                        }
                    }
                    bag47.set(index, item);
                } else {
                    bag47.add(item);
                }
                if (type == 7) {
                    String txt = "[VANG] Package: %s, Class: %s, Method: %s, Change: %s";
                    
                }
                break;
            }
        }
    }

    public int total_item_by_id(int type, int id) {
        int quantity = 0;
        switch (type) {
            case 3: {
                for (Item3 it : bag3) {
                    if (it != null && it.id == id) {
                        quantity += 1;
                    }
                }
                break;
            }
            case 4:
            case 7: {
                for (Item47 it : bag47) {
                    if (it.category == type && it.id == id) {
                        quantity += it.quantity;
                    }
                }
                break;
            }
        }
        return quantity;
    }

    public int total_item_by_id_box(int type, short id) {
        int quantity = 0;
        switch (type) {
            case 3: {
                for (Item3 it : box3) {
                    if (it != null && it.id == id) {
                        quantity += 1;
                    }
                }
                break;
            }
            case 4:
            case 7: {
                for (Item47 it : box47) {
                    if (it.category == type && it.id == id) {
                        quantity += it.quantity;
                    }
                }
                break;
            }
        }
        return quantity;
    }

    public void add_item_box47(int type, Item47 item) {
        switch (type) {
            case 4:
            case 7: {
                item.category = (byte) type;
                int size = total_item_by_id_box(type, item.id);
                if (size > 0) {
                    Item47 itbuffer = item;
                    size += item.quantity;
                    if (size > 32000) {
                        itbuffer.quantity = 32000;
                    } else {
                        itbuffer.quantity = (short) size;
                    }
                    int index = -1;
                    for (int i = 0; i < box47.size(); i++) {
                        if (box47.get(i).category == type && box47.get(i).id == item.id) {
                            index = i;
                            break;
                        }
                    }
                    box47.set(index, item);
                } else {
                    box47.add(item);
                }
                break;
            }
        }
    }

    public void add_item_bag3(Item3 buffer) {
        for (int j = 0; j < bag3.length; j++) {
            if (bag3[j] == null) {
                bag3[j] = buffer;
                break;
            }
        }
    }

    public void add_item_box3(Item3 buffer) {
        for (int j = 0; j < box3.length; j++) {
            if (box3[j] == null) {
                box3[j] = buffer;
                break;
            }
        }
    }

    public void remove(int type, int id, int quantity) {
        switch (type) {
            case 3: {
                bag3[id] = null;
                break;
            }
            case 4:
            case 7: {
                int index_remove = -1;
                for (int j = bag47.size() - 1; j >= 0; j--) {
                    if (bag47.get(j).category == type && bag47.get(j).id == id) {
                        if (bag47.get(j).quantity > quantity) {
                            bag47.get(j).quantity -= quantity;
                        } else {
                            index_remove = j;
                        }
                        break;
                    }
                }
                if (index_remove > -1) {
                    bag47.remove(index_remove);
                }
                break;
            }
        }
    }

    public void remove_box(int type, short id, int quantity) {
        switch (type) {
            case 3: {
                box3[id] = null;
                break;
            }
            case 4:
            case 7: {
                int index_remove = -1;
                for (int j = box47.size() - 1; j >= 0; j--) {
                    if (box47.get(j).category == type && box47.get(j).id == id) {
                        if (box47.get(j).quantity > quantity) {
                            box47.get(j).quantity -= quantity;
                        } else {
                            index_remove = j;
                        }
                        break;
                    }
                }
                if (index_remove > -1) {
                    box47.remove(index_remove);
                }
                break;
            }
        }
    }

    public void box_process(Message m2) throws IOException {
        byte type = m2.reader().readByte();
        short id = m2.reader().readShort();
        byte tem = m2.reader().readByte();
        short num = m2.reader().readShort();
        if (num <= 0) {
            return;
        }
        if (type == -1) {
            char_chest(3);
            char_chest(4);
            char_chest(7);
            return;
        }
        if (type == 1) { // cat vao
            if ((get_box_able() < 1 && tem == 3) || (get_box_able() < 1 && tem == 4 && total_item_by_id_box(4, id) < 1)
                    || (get_box_able() < 1 && tem == 7 && total_item_by_id_box(7, id) < 1)) {
                Service.send_notice_box(p.conn, "Rương đầy!");
                return;
            }
            switch (tem) {
                case 3: {
                    add_item_box3(bag3[id]);
                    remove(3, id, 1);
                    break;
                }
                case 4:
                case 7: {
                    if ((total_item_by_id(tem, id) + total_item_by_id_box(tem, id)) > 32000) {
                        Service.send_notice_box(p.conn, "Số lượng tổng lớn hơn 32k, không thể thực hiện!");
                        return;
                    }
                    if (num > total_item_by_id(tem, id)) {
                        num = (short) total_item_by_id(tem, id);
                        Item47 itbuffer = new Item47();
                        itbuffer.id = id;
                        itbuffer.quantity = num;
                        itbuffer.category = tem;
                        add_item_box47(tem, itbuffer);
                    } else {
                        Item47 itbuffer = new Item47();
                        itbuffer.id = id;
                        itbuffer.quantity = num;
                        itbuffer.category = tem;
                        add_item_box47(tem, itbuffer);
                    }
                    remove(tem, id, num);
                    break;
                }
            }
            char_inventory(tem);
            char_chest(tem);
        } else { // lay ra
            if ((get_bag_able() < 1 && tem == 3) || (get_bag_able() < 1 && tem == 4 && total_item_by_id(4, id) < 1)
                    || (get_bag_able() < 1 && tem == 7 && total_item_by_id(7, id) < 1)) {
                Service.send_notice_box(p.conn, "Hành trang đầy!");
                return;
            }
            switch (tem) {
                case 3: {
                    add_item_bag3(box3[id]);
                    remove_box(3, id, 1);
                    break;
                }
                case 4:
                case 7: {
                    if ((total_item_by_id(tem, id) + total_item_by_id_box(tem, id)) > 32000) {
                        Service.send_notice_box(p.conn, "Số lượng tổng lớn hơn 32k, không thể thực hiện!");
                        return;
                    }
                    if (num > total_item_by_id_box(tem, id)) {
                        num = (short) total_item_by_id_box(tem, id);
                        Item47 itbuffer = new Item47();
                        itbuffer.id = id;
                        itbuffer.quantity = num;
                        itbuffer.category = tem;
                        add_item_bag47(tem, itbuffer);
                    } else {
                        Item47 itbuffer = new Item47();
                        itbuffer.id = id;
                        itbuffer.quantity = num;
                        itbuffer.category = tem;
                        add_item_bag47(tem, itbuffer);
                    }
                    remove_box(tem, id, num);
                    break;
                }
            }
            char_inventory(tem);
            char_chest(tem);
        }
    }

    public short[] check_kham_ngoc(Item3 it3) {
        short[] result = new short[]{-2, -2, -2};
        for (int i = 0; i < it3.op.size(); i++) {
            if (it3.op.get(i).id == 58) {
                result[0] = (short) it3.op.get(i).getParam(0);
            } else if (it3.op.get(i).id == 59) {
                result[1] = (short) it3.op.get(i).getParam(0);
            } else if (it3.op.get(i).id == 60) {
                result[2] = (short) it3.op.get(i).getParam(0);
            }
        }
        return result;
    }
    public static int get_level_tinh_tu(int i) {
		return ((i - 45) / 10 + 1);
	}

}
