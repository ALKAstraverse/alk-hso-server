package gamble;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import client.Player;
import core.Log;
import core.Manager;
import core.Service;
import core.Util;
import io.Message;
import map.Map;
import template.TaiXiuPlayer;

public class TaiXiu implements Runnable {
	private Thread myth;
	private boolean running;
	private int time;
	private int state;
	private byte[] result;
	private HashMap<String, List<TaiXiuPlayer>> history;
	private HashMap<String, TaiXiuPlayer> list;

	public TaiXiu() {
		this.running = false;
		this.time = 60 * 5;
		this.state = 0;
		this.list = new HashMap<>();
		this.history = new HashMap<>();
		this.myth = new Thread(this);
		this.result = new byte[] {(byte) Util.random(1, 7), (byte) Util.random(1, 7), (byte) Util.random(1, 7)};
	}

	public void start_service() {
		this.running = true;
		this.myth.start();
	}

	public void stop_service() {
		this.running = false;
		this.myth.interrupt();
	}

	@Override
	public void run() {
		while (this.running) {
			this.time--;
			if (this.time == 0 && this.state == 0) { // begin
				// } else if (this.time == 0 && this.state == 1) { // finish
				try {
					finish();
					Manager.gI().chatKTGprocess(
					      "@Server : Tài xỉu mở tại npc Cướp biển ở cánh đồng sói, hãy đến tham gia và bắt đầu trở nên giàu có");
				} catch (IOException e) {
					e.printStackTrace();
				}
				this.time = 60 * 5;
				this.state = 0;
			}
			try {
				Thread.sleep(1000L);
			} catch (InterruptedException e) {
			} catch (Exception e) {
			}
		}
	}

	public synchronized void finish() throws IOException {
		this.result = new byte[] {(byte) Util.random(1, 7), (byte) Util.random(1, 7), (byte) Util.random(1, 7)};
		boolean tai = this.result[0] + this.result[1] + this.result[2] > 10;
		boolean nhacaiwin = (10 > Util.random(1000));
		if (nhacaiwin) {
			byte r = (byte) Util.random(1, 7);
			this.result = new byte[] {r, r, r};
		}
		for (Entry<String, TaiXiuPlayer> e : list.entrySet()) {
			TaiXiuPlayer temp = e.getValue();
			if (nhacaiwin) {
				temp.coin_win *= -1;
			} else {
				if (tai && temp.type == 1) {
					temp.coin_win = (temp.coin_win*198)/100;
					Player.update_coin(e.getKey(), temp.coin_win);
					//
					Player p0 = Map.get_player_by_name(temp.namep);
					if (p0 != null) {
						Service.send_notice_nobox_white(p0.conn, "Thắng " + temp.coin_win + " coin tài");
					}
                                        
				} else if (!tai && e.getValue().type == 2) {
					temp.coin_win = (temp.coin_win*198)/100;
					Player.update_coin(e.getKey(), temp.coin_win);
					//
					Player p0 = Map.get_player_by_name(temp.namep);
					if (p0 != null) {
						Service.send_notice_nobox_white(p0.conn, "Thắng " + temp.coin_win + " coin xỉu");
					}
                                        
				} else {
					temp.coin_win *= -1;
				}
			}
			//
			List<TaiXiuPlayer> temp_get = history.get(e.getKey());
			if (temp_get != null) {
				temp_get.add(temp);
			} else {
				temp_get = new ArrayList<>();
				temp_get.add(temp);
				history.put(e.getKey(), temp_get);
			}
		}
		//
		this.list.clear();
	}

	public synchronized void join(Player p, int coin_join, int type_join) {
		TaiXiuPlayer temp = new TaiXiuPlayer(Util.get_now_by_time(), coin_join, type_join, p.name);
		this.list.put(p.conn.user, temp);
	}

public void send_in4(Player p) throws IOException {
    Message m = new Message(-32);
    m.writer().writeShort(p.id);
    m.writer().writeByte(85);

    int tong = this.result[0] + this.result[1] + this.result[2];
    String loai = (this.result[0] == this.result[1] && this.result[1] == this.result[2]) ? "Nhà cái ăn"
               : (tong > 10 ? "Tài" : "Xỉu");
    String ketQua = "[" + this.result[0] + "] [" + this.result[1] + "] [" + this.result[2] + "]";

    String text = "Tài Xỉu\r\n"
                + "Thời gian\r\n" + this.time + "\r\n"
                + "Số người hiện tại: " + this.list.size() + "\r\n"
                + "Kết quả: " + ketQua + " - " + loai + "\r\n";

    TaiXiuPlayer temp = list.get(p.conn.user);
    if (temp != null) {
        text += "Đã tham gia " + temp.coin_win + " coin vào " + (temp.type == 1 ? "Tài" : "Xỉu");
    } else {
        text += "Chưa tham gia";
    }

    m.writer().writeUTF(text);
    p.conn.addmsg(m);
    m.cleanup();
}

	public synchronized List<TaiXiuPlayer> get_tx_history(String name) {
		return this.history.get(name);
	}
}
