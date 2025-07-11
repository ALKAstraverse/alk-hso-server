package template;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import client.Player;
import core.Log;
import core.Service;
import core.Util;
import io.Message;
import map.MapService;

public class Pet_di_buon {
	public Player p;
	public short type;
	public int index;
	public List<Short> item;
	public short x, y;
	public int id_map;
	public long time_move;
	public String name;
	public int hp;
	public int hp_max;
	public long time_skill;
	public int speed;
    public int ngoc = 5;

	public Pet_di_buon(int type, int index_mob, int x, int y, int id_map, String name, Player p) {
		this.type = (short) type;
		this.index = Short.toUnsignedInt((short) index_mob);
		this.item = new ArrayList<>();
		this.x = (short) x;
		this.y = (short) y;
		this.time_move = System.currentTimeMillis() + 1000L;
		this.id_map = id_map;
		this.name = name;
		this.hp = 1_000_000;
		this.hp_max = 1_000_000;
		this.time_skill = System.currentTimeMillis() + 15_000L;
		this.speed = 1;
		this.p = p;
	}

	public synchronized void update_hp(Player p, int hp) throws IOException {
		if (this.time_skill < System.currentTimeMillis()) {
			this.time_skill = System.currentTimeMillis() + 5_000L;
			this.hp += hp;
			if (this.hp > this.hp_max) {
				this.hp = this.hp_max;
			}
			if (this.hp < 0) {
				this.hp = 0;
			}
			this.update_all(p);
			p.update_ngoc(-5);
			p.item.char_inventory(5);
		} else {
			Service.send_notice_box(p.conn,
			      "Thời gian dùng lần tiếp theo : " + (this.time_skill - System.currentTimeMillis()) + "ms");
		}
	}

	public synchronized void update_speed(Player p) throws IOException {
		if (this.time_skill < System.currentTimeMillis()) {
			this.time_skill = System.currentTimeMillis() + 10_000L;
			this.speed = 4;
			this.update_all(p);
			p.update_ngoc(-(p.pet_di_buon.ngoc));
			p.item.char_inventory(5);
                        p.pet_di_buon.ngoc+=5;
		} else {
			Service.send_notice_box(p.conn,
			      "Thời gian dùng lần tiếp theo : " + (this.time_skill - System.currentTimeMillis()) + "ms");
		}
	}

	public void update_all(Player p) throws IOException {
		Message mm = new Message(7);
		mm.writer().writeShort(this.index);
		mm.writer().writeByte(120);
		mm.writer().writeShort(this.x);
		mm.writer().writeShort(this.y);
		mm.writer().writeInt(this.hp);
		mm.writer().writeInt(this.hp_max);
		mm.writer().writeByte(0);
		mm.writer().writeInt(-1);
		mm.writer().writeShort(-1);
		mm.writer().writeByte(1);
		mm.writer().writeByte(this.speed);
		mm.writer().writeByte(0);
		mm.writer().writeUTF(this.name);
		mm.writer().writeLong(-11111);
		mm.writer().writeByte(4);
		MapService.send_msg_player_inside(p.map, p, mm, true);
		mm.cleanup();
	}
}
