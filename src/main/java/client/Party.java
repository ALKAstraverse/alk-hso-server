package client;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import core.Service;
import io.Message;

public class Party {
	private List<Player> mems;
	private Player leader;

	public Party() {
		this.mems = new ArrayList<>();
	}
	
	public void setLeader(Player p) {
    this.leader = p;
}

public Player getLeader() {
    return this.leader;
}

public boolean isLeader(Player p) {
    return this.leader != null && this.leader.id == p.id;
}

	public void sendin4() throws IOException {
		synchronized (mems) {
			Message m = new Message(48);
			m.writer().writeByte(2);
			m.writer().writeByte(mems.size());
			for (int i = 0; i < mems.size(); i++) {
				m.writer().writeUTF(mems.get(i).name);
				m.writer().writeShort(mems.get(i).level);
				if (mems.get(i).dungeon != null) {
					m.writer().writeByte(48);
					m.writer().writeByte(0);
				} else {
					m.writer().writeByte(mems.get(i).map.map_id);
					m.writer().writeByte(mems.get(i).map.zone_id);
				}
			}
			for (Player p1 : mems) {
				p1.conn.addmsg(m);
			}
			m.cleanup();
		}
	}

	public void send_txt_notice(String text) throws IOException {
		synchronized (mems) {
			for (Player p1 : mems) {
				Service.send_notice_nobox_white(p1.conn, text);
			}
		}
	}

	public List<Player> get_mems() {
		return this.mems;
	}

	public synchronized void add_mems(Player p) {
		this.mems.add(p);
	}

	public synchronized void remove_mems(Player p) {
		this.mems.remove(p);
	}
}
