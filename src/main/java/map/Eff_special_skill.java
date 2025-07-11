package map;

import java.io.IOException;
import client.Player;
import io.Message;

public class Eff_special_skill {
	public static void send_eff(Player p, int i, int time) throws IOException {
		Message m = new Message(-49);
		m.writer().writeByte(2);
		m.writer().writeShort(0);
		m.writer().writeByte(0);
		m.writer().writeByte(0);
		switch (i) {
			case 0: {
				m.writer().writeByte(13);
				break;
			}
			case 1: {
				m.writer().writeByte(9);
				break;
			}
			case 2: {
				m.writer().writeByte(12);
				break;
			}
			case 3: {
				m.writer().writeByte(6);
				break;
			}
			case 4: {
				m.writer().writeByte(17);
				break;
			}
		}
		m.writer().writeShort(p.id);
		m.writer().writeByte(0);
		m.writer().writeByte(0);
		m.writer().writeInt(time);
		MapService.send_msg_player_inside(p.map, p, m, true);
		m.cleanup();
		// t6 : eff electric
		// t12 : eff ice
		// t13 : eff fire
		// t9: eff poison
		// t17 : eff vat ly
	}
}
