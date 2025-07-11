package ai;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import client.Clan;
import client.Player;
import core.Manager;
import core.Service;
import io.Message;
import template.Item3;
import template.Part_player;

public class NhanBan {
	public int map_id;
	public int id_p;
	public short x, y;
	public String name;
	public List<Part_player> part_p;
	private byte clazz;
	private byte head;
	private byte eye;
	private byte hair;
	private short level;
	public int hp;
	public int hp_max;
	private int pointpk;
	private short clan_icon;
	private int clan_id;
	private String clan_name_clan_shorted;
	private byte clan_mem_type;
	private byte[] fashion;
	private short mat_na;
	private short phi_phong;
	private short weapon;
	private short id_horse;
	private short id_hair;
	private short id_wing;
	private byte type_use_mount;
	public int dame;
	public long act_time;
	public boolean is_move;
	public Player p_target;
	public int p_skill_id;
	public int crit;
	public long time_hp_buff;
	public int def;
	public int pierce;

	public NhanBan() {}

	public void setup(Player p0) {
		this.id_p = Short.toUnsignedInt((short) Manager.gI().get_index_mob_new());
		this.x = p0.x;
		this.y = p0.y;
		this.part_p = new ArrayList<>();
		for (int i = 0; i < p0.item.wear.length; i++) {
			Part_player temp_add = new Part_player();
			if (i != 0 && i != 1 && i != 6 && i != 7 && i != 10) {
				continue;
			}
			Item3 temp = p0.item.wear[i];
			if (temp != null) {
				temp_add.type = temp.type;
				if (i == 10 && p0.item.wear[14] != null && (p0.item.wear[14].id >= 4638 && p0.item.wear[14].id <= 4648)) {
					temp_add.part = p0.item.wear[14].part;
				} else {
					temp_add.part = temp.part;
				}
			}
			this.part_p.add(temp_add);
		}
		this.name = "Nhân bản - " + p0.name;
		this.clazz = p0.clazz;
		this.head = p0.head;
		this.eye = p0.eye;
		this.hair = p0.hair;
		this.level = p0.level;
		this.hp = (int) Math.min(p0.hp, Integer.MAX_VALUE);
		this.hp_max = (int) Math.min(p0.body.get_max_hp(), Integer.MAX_VALUE);
		this.pointpk = p0.pointpk;
		this.clan_icon = p0.myclan.icon;
		this.clan_id = Clan.get_id_clan(p0.myclan);
		this.clan_name_clan_shorted = p0.myclan.name_clan_shorted;
		this.clan_mem_type = p0.myclan.get_mem_type(p0.name);
		this.fashion = p0.fashion;
		this.mat_na = Service.get_id_mat_na(p0);
		this.phi_phong = Service.get_id_phiphong(p0);
		this.weapon = Service.get_id_weapon(p0);
		this.id_horse = p0.id_horse;
		this.id_hair = Service.get_id_hair(p0);
		this.id_wing = Service.get_id_wing(p0);
		this.type_use_mount = p0.type_use_mount;
		this.dame = (p0.body.get_dame_physical() + p0.body.get_dame_prop(1) + p0.body.get_dame_prop(2)
		      + p0.body.get_dame_prop(3) + p0.body.get_dame_prop(4)) / 2;
		this.map_id = p0.map.map_id;
		this.crit = p0.body.get_crit();
		this.def = p0.body.get_def();
		this.pierce = p0.body.get_pierce();
		if (this.pierce > 5000) {
			this.pierce = 5000;
		}
		this.is_move = true;
	}

	public void send_in4(Player p) throws IOException {
		Message m = new Message(5);
		m.writer().writeShort(this.id_p);
		m.writer().writeUTF(this.name);
		m.writer().writeShort(this.x);
		m.writer().writeShort(this.y);
		m.writer().writeByte(this.clazz);
		m.writer().writeByte(-1);
		m.writer().writeByte(this.head);
		m.writer().writeByte(this.eye);
		m.writer().writeByte(this.hair);
		m.writer().writeShort(this.level);
		m.writer().writeInt(this.hp);
		m.writer().writeInt(this.hp_max);
		m.writer().writeByte(0); // type pk
		m.writer().writeShort(this.pointpk);
		m.writer().writeByte(this.part_p.size());
		//
		for (int i = 0; i < this.part_p.size(); i++) {
			m.writer().writeByte(this.part_p.get(i).type);
			m.writer().writeByte(this.part_p.get(i).part);
			m.writer().writeByte(3);
			m.writer().writeShort(-1);
			m.writer().writeShort(-1);
			m.writer().writeShort(-1);
			m.writer().writeShort(-1); // eff
		}
		//
		m.writer().writeShort(this.clan_icon);
		m.writer().writeInt(this.clan_id);
		m.writer().writeUTF(this.clan_name_clan_shorted);
		m.writer().writeByte(this.clan_mem_type);
		m.writer().writeByte(-1); // pet
		m.writer().writeByte(this.fashion.length);
		for (int i = 0; i < this.fashion.length; i++) {
			m.writer().writeByte(this.fashion[i]);
		}
		//
		m.writer().writeShort(-1);
		m.writer().writeByte(this.type_use_mount);
		m.writer().writeBoolean(false);
		m.writer().writeByte(1);
		m.writer().writeByte(0);
		m.writer().writeShort(this.mat_na); // mat na
		m.writer().writeByte(1); // paint mat na trc sau
		m.writer().writeShort(this.phi_phong); // phi phong
		m.writer().writeShort(this.weapon); // weapon
		m.writer().writeShort(this.id_horse);
		m.writer().writeShort(this.id_hair); // hair
		m.writer().writeShort(this.id_wing); // wing
		m.writer().writeShort(-1); // body
		m.writer().writeShort(-1); // leg
		m.writer().writeShort(-1); // bienhinh
		p.conn.addmsg(m);
		m.cleanup();
	}
}
