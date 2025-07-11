package client;

import java.io.IOException;
import java.util.List;
import io.Message;
import map.Map;
import template.Item3;
import template.Part_player;

public class Friend {
	public short level;
	public String name;
	public byte head;
	public byte eye;
	public byte hair;
	public Clan clan;
	public List<Part_player> itemwear;

	public static void send_list_friend(Player p) throws IOException {
		Message m = new Message(35);
		m.writer().writeByte(4);
		m.writer().writeUTF("Danh Sách Bạn Bè");
		m.writer().writeByte(0); // fake
		m.writer().writeByte(p.list_friend.size());
		for (int i = 0; i < p.list_friend.size(); i++) {
			Friend temp = p.list_friend.get(i);
			//
			// short icon_clan = -1;
			// String name_clan_shorted = "";
			// byte mem_type = -1;
			// boolean onl = false;
			Player p0 = Map.get_player_by_name(temp.name);
			if (p0 != null) {
				temp.head = p0.head;
				temp.eye = p0.eye;
				temp.hair = p0.hair;
				temp.level = p0.level;
				temp.itemwear.clear();
				for (int i1 = 0; i1 < p0.item.wear.length; i1++) {
					Item3 it = p0.item.wear[i1];
					if (it != null && (i1 == 0 || i1 == 1 || i1 == 6 || i1 == 7 || i1 == 10)) {
						Part_player part = new Part_player();
						part.type = it.type;
						part.part = it.part;
						temp.itemwear.add(part);
					}
				}
				temp.clan = p0.myclan;
			}
			//
			m.writer().writeUTF(temp.name);
			m.writer().writeByte(temp.head);
			m.writer().writeByte(temp.eye);
			m.writer().writeByte(temp.hair);
			m.writer().writeShort(temp.level);
			m.writer().writeByte(temp.itemwear.size()); // part
			for (Part_player part : temp.itemwear) {
				m.writer().writeByte(part.part);
				m.writer().writeByte(part.type);
			}
			m.writer().writeByte((p0 != null) ? (byte) 1 : (byte) 0); // type online
			if (temp.clan != null) {
				m.writer().writeShort(temp.clan.icon);
				m.writer().writeUTF(temp.clan.name_clan_shorted);
				m.writer().writeByte(temp.clan.get_mem_type(temp.name));
			} else {
				m.writer().writeShort(-1); // clan
			}
		}
		p.conn.addmsg(m);
		m.cleanup();
	}
}
