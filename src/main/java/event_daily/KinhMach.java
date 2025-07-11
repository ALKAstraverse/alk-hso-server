package event_daily;

import java.io.IOException;
import client.Player;
import core.GameSrc;
import core.Log;
import core.Service;
import core.Util;

public class KinhMach {
	public static String[] NAME = new String[] {"Đốc mạch", "+ Nhâm mạch", "+ Xung mạch", "+ Đới mạch", "+ Âm duy",
	      "+ Dương duy", "+ Âm khiêu", "+ Dương khiêu"};

	public static void start(Player p) throws IOException {
		if (p.kinhmach[0] < 8) {
			if (p.item.total_item_by_id(4, 319) < 100) {
				Service.send_notice_box(p.conn, "Không đủ 100 hộ mạch đan");
				return;
			}
			if (p.get_vang() < 5_000_000) {
				Service.send_notice_box(p.conn, "Không đủ 5tr vàng");
				return;
			}
			if (p.get_ngoc() < 5_000) {
				Service.send_notice_box(p.conn, "Không đủ 5k ngọc");
				return;
			}
			p.item.remove(4, 319, 100);
			p.update_vang(-5_000_000);
			p.update_ngoc(-5_000);
			p.item.char_inventory(4);
			if (30 > Util.random(250)) {
				p.kinhmach[1]++;
				if (p.kinhmach[1] == 6) {
					p.kinhmach[0]++;
					p.kinhmach[1] = 1;
					Service.send_notice_box(p.conn,
					      "Thành công đả thông hoàn toàn " + KinhMach.NAME[p.kinhmach[0] - 2] + "\nĐạt được:\n" + "+ "
					            + ((p.kinhmach[1] - 1) * 5 + p.kinhmach[1]) + "% sát thương \r\n" + "+ "
					            + ((p.kinhmach[0] - 1) * 5 + p.kinhmach[1]) + "% máu\r\n" + "+ "
					            + ((p.kinhmach[0] - 1) * 5 + p.kinhmach[1]) + "% mana\r\n" + "+ "
					            + ((p.kinhmach[0] - 1) * 5 + p.kinhmach[1]) + "% phòng thủ.");
                                } else {
					Service.send_notice_box(p.conn,
					      "Thành công đả thông " + KinhMach.NAME[p.kinhmach[0] - 1] + " lên tầng thứ " + p.kinhmach[1]
					            + "\nĐạt được:\n" + "+ " + ((p.kinhmach[0] - 1) * 5 + p.kinhmach[1]) + "% sát thương \r\n"
					            + "+ " + ((p.kinhmach[0] - 1) * 5 + p.kinhmach[1]) + "% máu\r\n" + "+ "
					            + ((p.kinhmach[0] - 1) * 5 + p.kinhmach[1]) + "% mana\r\n" + "+ "
					            + ((p.kinhmach[0] - 1) * 5 + p.kinhmach[1]) + "% phòng thủ.");
				}
			} else {
				Service.send_notice_box(p.conn, "Thất bại, hãy nghỉ nghơi và thử lại");
			}
		} else {
			Service.send_notice_box(p.conn, "Đã đạt cảnh giới tối đa");
		}
	}

	public static void send_info(Player p) throws IOException {
		if (p.kinhmach[0] < 8) {
			String notice = "Hiện tại: đả thông được ";
			for (int i = 0; i < p.kinhmach[0] - 1; i++) {
				notice += KinhMach.NAME[i] + ", ";
			}
			notice += "\nKinh mạchh hiện tại: " + KinhMach.NAME[p.kinhmach[0] - 1] + " cấp " + p.kinhmach[1];
			Service.send_notice_box(p.conn, notice);
		} else {
			Service.send_notice_box(p.conn, "Đả thông toàn bộ");
		}
	}
}
