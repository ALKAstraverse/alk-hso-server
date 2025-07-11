package gamble;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import client.Player;
import core.Log;
import core.Manager;
import core.Service;
import core.Util;
import io.Message;
import io.Session;

public class VXMM implements Runnable {
	public Thread mainloopThread;
	private boolean running;
	private boolean started;
	private short time;
	private String last_winner = "Chưa có";
	private long vang_win = 0;
	private int vang_join = 0;
	private HashMap<Player, Integer> list_playerHashMap = new HashMap<>();

	public VXMM() {
		time = 120;
		started = false;
		mainloopThread = new Thread(this);
		mainloopThread.start();
	}

	public void send_in4(Player p) throws IOException {
		Message m = new Message(-32);
		m.writer().writeShort(p.id);
		m.writer().writeByte(86);
		String text = "Vòng xoay Vip\r\n" + "Thời gian\r\n" + get_time() + "\r\n" + " Vàng tổng: \r\n"
		      + Util.number_format(get_total_vang()) + "\r\n" + " Vàng tham gia: \r\n"
		      + Util.number_format((list_playerHashMap.containsKey(p)) ? list_playerHashMap.get(p) : 0) + "\r\n"
		      + "Tỷ lệ thắng: " + get_percent(p) + "%\r\n" + "Số người hiện tại: " + get_join() + "\r\n"
		      + "Người vừa chiến thắng: " + get_last_winner() + "\r\n" + "Số vàng ăn được: " + get_vang_win() + "\r\n"
		      + "Số vàng tham gia: " + get_vang_join();
		m.writer().writeUTF(text);
		p.conn.addmsg(m);
		m.cleanup();
	}

	private String get_percent(Player p) {
		if (list_playerHashMap.containsKey(p)) {
			float percent = ((float) list_playerHashMap.get(p) * 100) / get_total_vang();
			return String.format("%.3f", percent);
		}
		return "0.0";
	}

	private int get_join() {
		return list_playerHashMap.size();
	}

	private long get_total_vang() {
		int total = 0;
		for (Map.Entry<Player, Integer> player : list_playerHashMap.entrySet()) {
			total += player.getValue();
		}
		return total;
	}

	private String get_vang_join() {
		if (vang_join > 0) {
			return Util.number_format(vang_join);
		}
		return "chưa có";
	}

	private String get_vang_win() {
		if (vang_win > 0) {
			return Util.number_format(vang_win);
		}
		return "chưa có";
	}

	private String get_last_winner() {
		return last_winner;
	}

	private String get_time() {
		if (started) {
			if (time <= 120 && time > 60) {
				if (time > 60 && time <= 69) {
					return "01:0" + (time - 60);
				} else {
					return "01:" + (time - 60);
				}
			} else if (time >= 0 && time <= 60) {
				if (time > 0 && time <= 9) {
					return "00:0" + time;
				} else {
					return "00:" + time;
				}
			}
		} else {
			return "02:00";
		}
		return "0";
	}

	@Override
	public void run() {
		running = true;
		long time1 = 0;
		long time2 = 0;
		while (running) {
			try {
				time1 = System.currentTimeMillis();
				update();
				time2 = System.currentTimeMillis();
				long time3 = (1000L - (time2 - time1));
				if (time3 > 0) {
					Thread.sleep(time3);
				}
			} catch (InterruptedException e) {
			}
		}
	}

	private synchronized void update() {
		if (started) {
			time--;
			if (time < 0) {
				try {
					notice_winner();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private synchronized void notice_winner() throws IOException {
		int index = -1;
		int dem = 0;
		for (Map.Entry<Player, Integer> player : list_playerHashMap.entrySet()) {
			long percent = (((long) list_playerHashMap.get(player.getKey())) * 100L) / get_total_vang();
			if (percent > Util.random(250)) {
				index = dem;
			}
			if (index != -1) {
				break;
			}
			dem++;
		}
		if (index == -1) {
			index = Util.random(0, list_playerHashMap.size()); // random win :v
		}
		dem = 0;
		for (Map.Entry<Player, Integer> player : list_playerHashMap.entrySet()) {
			if (dem == index) {
				Player p0 = player.getKey();
				if (p0 != null && p0.map != null) {
					synchronized (p0.map) {
						last_winner = player.getKey().name;
						vang_join = player.getValue();
						vang_win = (get_total_vang() * 8L) / 10L;
						Manager.gI().chatKTGprocess(last_winner + " đã thắng " + Util.number_format(vang_win)
						      + " vàng khi tham gia vòng xoay may mắn");
						player.getKey().update_vang(vang_win);
						player.getKey().item.char_inventory(5);
                                              Session conn = player.getKey().conn;
						if (conn.p.nv_tinh_tu[0] == 6 && conn.p.nv_tinh_tu[1] < conn.p.nv_tinh_tu[2]) {
							conn.p.nv_tinh_tu[1]++;
						}
					}
				} else {
					Manager.gI().chatKTGprocess("Người thắng cuộc đã offline nên kết quả bị hủy kakaka :v");
				}
				break;
			}
			dem++;
		}
		refresh();
	}

	public synchronized void refresh() {
		started = false;
		this.list_playerHashMap.clear();
		time = 120;
	}

	public void close() {
		running = false;
		mainloopThread.interrupt();
		mainloopThread = null;
	}

	public synchronized void join_vxmm(Player p, int vang_join_vxmm) throws IOException {
		if (time > 10) {
			if (list_playerHashMap.containsKey(p) && (list_playerHashMap.get(p) + vang_join_vxmm) > 200_000_000) {
				Service.send_notice_box(p.conn, "Chỉ có thể tham gia tối đa 200.000.000 vàng");
				return;
			}
			if ((get_total_vang() + vang_join_vxmm) > 2_000_000_000) {
				Service.send_notice_box(p.conn, "Tổng vàng trong vòng xoay tối đa chỉ 2tỷ");
				return;
			}
			p.update_vang(-vang_join_vxmm);
			p.item.char_inventory(5);
			Service.send_notice_box(p.conn, "tham gia " + Util.number_format(vang_join_vxmm) + " vàng thành công");
                        Session conn = p.conn;
			if (conn.p.nv_tinh_tu[0] == 5 && conn.p.nv_tinh_tu[1] < conn.p.nv_tinh_tu[2]) {
				conn.p.nv_tinh_tu[1]++;
			}
			if (!list_playerHashMap.containsKey(p)) {
				list_playerHashMap.put(p, vang_join_vxmm);
                 if (p.quest_daily[0] == 4 && p.quest_daily[3] < p.quest_daily[4]) {
                        p.quest_daily[3]++;
                    }
			} else {
				int add = list_playerHashMap.get(p) + vang_join_vxmm;
				list_playerHashMap.replace(p, list_playerHashMap.get(p), add);
			}
			if (list_playerHashMap.size() > 1 && !started) {
				this.start_rotate();
			}
		} else {
			Service.send_notice_box(p.conn, "Không thể thêm khi chỉ còn 10s nữa");
		}
	}

	private synchronized void start_rotate() throws IOException {
		Manager.gI().chatKTGprocess("Vòng xoay vip bắt đầu xoay!!");
		started = true;
	}
}
