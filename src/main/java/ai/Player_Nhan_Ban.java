package ai;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import client.Player;
import core.Util;
import event_daily.ChienTruong;
import io.Message;
import map.Map;
import map.MapService;
import template.EffTemplate;

public class Player_Nhan_Ban {
	public static short[][] LOCATION = new short[][] { //
	      new short[] {318, 528, 516, 612}, // 2
	      new short[] {416, 552, 120, 200}, // 3
	      new short[] {424, 552, 304, 416}, // 4
	      new short[] {235, 411, 493, 573} // 5
	};
	public int id;
	public short x, y;
	public Map map;
	public boolean isdie;
	public int hp, hp_max;
	public long time_move;
	public int target;
	public int village;
	public int dame;
	public long time_change_target;
	public long time_refresh;

	public static List<Player_Nhan_Ban> init() {
		int size2 = Map.get_map_by_id(56)[0].maxzone;
		int size3 = Map.get_map_by_id(60)[0].maxzone;
		int size4 = Map.get_map_by_id(58)[0].maxzone;
		int size5 = Map.get_map_by_id(54)[0].maxzone;
		int i = -2;
		List<Player_Nhan_Ban> result = new ArrayList<>();
		int size_linh_canh = 15;
		for (int j = 0; j < size2; j++) {
			for (int j2 = 0; j2 < size_linh_canh; j2++) { // 20 linh canh
				add_linh_canh(result, i--, 56, j, 2);
			}
		}
		for (int j = 0; j < size3; j++) {
			for (int j2 = 0; j2 < size_linh_canh; j2++) { // 20 linh canh
				add_linh_canh(result, i--, 60, j, 3);
			}
		}
		for (int j = 0; j < size4; j++) {
			for (int j2 = 0; j2 < size_linh_canh; j2++) { // 20 linh canh
				add_linh_canh(result, i--, 58, j, 4);
			}
		}
		for (int j = 0; j < size5; j++) {
			for (int j2 = 0; j2 < size_linh_canh; j2++) { // 20 linh canh
				add_linh_canh(result, i--, 54, j, 5);
			}
		}
		return result;
	}

	private static void add_linh_canh(List<Player_Nhan_Ban> result, int i, int map, int zone, int village) {
		Player_Nhan_Ban temp = new Player_Nhan_Ban();
		temp.id = i;
		temp.x = 432;
		temp.y = 520;
		temp.map = Map.get_map_by_id(map)[zone];
		temp.isdie = false;
		temp.hp_max = 10_000_000;
		temp.hp = temp.hp_max;
		temp.target = -1;
		temp.village = village;
		temp.dame = 30_000;
		result.add(temp);
	}

	public static void update(Map map) throws IOException {
		for (int i = 0; i < ChienTruong.gI().list_ai.size(); i++) {
			Player_Nhan_Ban temp = ChienTruong.gI().list_ai.get(i);
			if (!temp.isdie && temp.map.equals(map) && temp.time_move < System.currentTimeMillis()) {
				temp.time_move = System.currentTimeMillis() + Util.random(2000, 5000);
				//
				Player p0 = Map.get_player_by_id(temp.target);
				if (p0 != null) { // atk
					if (!p0.isdie && p0.map.equals(temp.map)) {
						if (Math.abs(p0.x - temp.x) < 150 && Math.abs(p0.y - temp.y) < 150) {
							temp.x = (short) (p0.x + Util.random(-30, 30));
							temp.y = (short) (p0.y + Util.random(-30, 30));
							move(map, temp);
						}
						atk(map, temp, p0);
					} else {
						temp.target = -1;
					}
				} else {
					temp.x = (short) Util.random(Player_Nhan_Ban.LOCATION[temp.village - 2][0],
					      Player_Nhan_Ban.LOCATION[temp.village - 2][1]);
					temp.y = (short) Util.random(Player_Nhan_Ban.LOCATION[temp.village - 2][2],
					      Player_Nhan_Ban.LOCATION[temp.village - 2][3]);
					move(map, temp);
				}
			}
			if (temp.time_refresh < System.currentTimeMillis()) {
				if (temp.isdie) {
					temp.hp = temp.hp_max;
					temp.isdie = false;
				} else {
					temp.hp += 1000;
					if (temp.hp > temp.hp_max) {
						temp.hp = temp.hp_max;
					}
				}
				temp.time_refresh = System.currentTimeMillis() + 60_000L;
			}
		}
	}

	private static void atk(Map map, Player_Nhan_Ban temp, Player p0) throws IOException {
		int dame = (temp.dame * Util.random(90, 100)) / 100;
		Message m = new Message(6);
		m.writer().writeShort(temp.id);
		m.writer().writeByte(0); // indexskill
		m.writer().writeByte(1);
		m.writer().writeShort(p0.id);
		boolean crit = 10 > Util.random(0, 120);
		int rc_dame = p0.body.get_react_dame();
		EffTemplate ef = p0.get_eff(35);
		if (ef != null) {
			rc_dame += ef.param;
		}
		boolean react_dame = rc_dame > Util.random(0, 15000);
		if (crit) {
			dame *= 2;
		}
		int miss = p0.body.get_miss();
		ef = p0.get_eff(34);
		if (ef != null) {
			miss += ef.param;
		}
		if (miss > Util.random(0, 15_000)) {
			dame = 0;
			react_dame = false;
			crit = false;
		}
		p0.hp -= dame;
		if (p0.hp <= 0) {
			p0.hp = 0;
			if (!p0.isdie) {
				p0.dame_affect_special_sk = 0;
				p0.hp = 0;
				p0.isdie = true;
				Message m2 = new Message(41);
				m2.writer().writeShort(p0.id);
				m2.writer().writeShort(temp.id);
				m2.writer().writeShort(-1); // point pk
				m2.writer().writeByte(1); // type main object
				MapService.send_msg_player_inside(map, p0, m2, true);
				m2.cleanup();
				p0.type_use_mount = -1;
			}
		}
		//
		m.writer().writeInt(dame); // dame
		m.writer().writeInt((int) Math.min(p0.hp, Integer.MAX_VALUE)); // hp after
		//
		if (dame > 0 && crit) {
			if (react_dame) {
				m.writer().writeByte(2); // size color show
				//
				m.writer().writeByte(4); // 1: xuyen giap, 2:hut hp, 3: hut mp, 4: chi mang, 5: phan don
				m.writer().writeInt((int) dame); // par
				//
				m.writer().writeByte(5);
				m.writer().writeInt((int) dame);
			} else {
				m.writer().writeByte(1); // size color show
				//
				m.writer().writeByte(4); // 1: xuyen giap, 2:hut hp, 3: hut mp, 4: chi mang, 5: phan don
				m.writer().writeInt((int) dame); // par
				//
			}
		} else {
			if (react_dame) {
				m.writer().writeByte(2);
				m.writer().writeByte(0);
				m.writer().writeInt((int) dame);
				m.writer().writeByte(5);
				m.writer().writeInt(dame);
			} else {
				m.writer().writeByte(0);
			}
		}
		if (react_dame && dame > 0) {
			temp.hp -= dame;
			if (temp.hp <= 0) {
				temp.hp = 0;
				temp.isdie = true;
				Message m3 = new Message(8);
				m3.writer().writeShort(temp.id);
				for (int i = 0; i < map.players.size(); i++) {
					Player pp = map.players.get(i);
					pp.conn.addmsg(m);
				}
				m3.cleanup();
			}
		}
		m.writer().writeInt(temp.hp);
		m.writer().writeInt(0);
		m.writer().writeByte(11);
		m.writer().writeInt(0);
		for (int i = 0; i < map.players.size(); i++) {
			Player pp = map.players.get(i);
			pp.conn.addmsg(m);
		}
		m.cleanup();
	}

	private static void move(Map map, Player_Nhan_Ban temp) throws IOException {
		Message m22 = new Message(4);
		m22.writer().writeByte(0);
		m22.writer().writeShort(0);
		m22.writer().writeShort(temp.id);
		m22.writer().writeShort(temp.x);
		m22.writer().writeShort(temp.y);
		m22.writer().writeByte(-1);
		for (int i = 0; i < map.players.size(); i++) {
			Player p0 = map.players.get(i);
			p0.conn.addmsg(m22);
		}
		m22.cleanup();
	}

	public static void atk(Map map, Player p, int n2, int indexskill, int dame) throws IOException {
		short id = (short) n2;
		for (int i = 0; i < ChienTruong.gI().list_ai.size(); i++) {
			Player_Nhan_Ban temp = ChienTruong.gI().list_ai.get(i);
			if (temp.id == id) {
                            
                            switch (map.map_id) {
					case 58: {
						if (p.typepk == 4) {
							return;
						}
						break;
					}
					case 56: {
						if (p.typepk == 2) {
							return;
						}
						break;
					}
					case 54: {
						if (p.typepk == 5) {
							return;
						}
						break;
					}
					case 60: {
						if (p.typepk == 1) {
							return;
						}
						break;
					}
				}
                            
				Message m = new Message(6);
				m.writer().writeShort(p.id);
				m.writer().writeByte(indexskill);
				m.writer().writeByte(1);
				m.writer().writeShort(temp.id);
				// if (indexskill == 17) {
				// MapService.add_eff_skill(map, p, p0, indexskill);
				// }
				EffTemplate ef = null;
				int cr = p.body.get_crit();
				ef = p.get_eff(33);
				if (ef != null) {
					cr += ef.param;
				}
				boolean crit = cr > Util.random(0, 15000);
				if (crit) {
					dame *= 2;
				}
				if (20 > Util.random(0, 120)) {
					dame = 0;
					crit = false;
				}
				if (dame < 0) {
					dame = 0;
				}
				if (dame > 2_000_000_000) {
					dame = 2_000_000_000;
				}
				temp.hp -= dame;
				if (temp.hp <= 0) {
					temp.hp = 0;
					temp.isdie = true;
					//
					Message m2 = new Message(41);
					m2.writer().writeShort(temp.id);
					m2.writer().writeShort(p.id);
					m2.writer().writeShort(-1); // point pk
					m2.writer().writeByte(1); // type main object
					for (int i2 = 0; i2 < map.players.size(); i2++) {
						Player pp = map.players.get(i2);
						pp.conn.addmsg(m2);
					}
					m2.cleanup();
					//
					Message m3 = new Message(8);
					m3.writer().writeShort(temp.id);
					for (int i2 = 0; i2 < map.players.size(); i2++) {
						Player pp = map.players.get(i2);
						pp.conn.addmsg(m3);
					}
					m3.cleanup();
					//
					p.update_point_arena(2);
				}
				//
				m.writer().writeInt((int) dame); // dame
				m.writer().writeInt(temp.hp); // hp after
				//
				int dame_react = 0;
				//
				if (dame > 0 && crit) {
					m.writer().writeByte(1); // size color show
					m.writer().writeByte(4); // 1: xuyen giap, 2:hut hp, 3: hut mp, 4: chi mang, 5: phan don
					m.writer().writeInt((int) dame); // par
				} else {
					m.writer().writeByte(0);
				}
				m.writer().writeInt((int) Math.min(p.hp, Integer.MAX_VALUE));
				m.writer().writeInt(p.mp);
				m.writer().writeByte(11);
				m.writer().writeInt(0);
				MapService.send_msg_player_inside(map, p, m, true);
				m.cleanup();
				break;
			}
		}
	}
}
