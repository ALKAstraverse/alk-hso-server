package event_daily;

import java.io.IOException;
import client.Player;
import core.GameSrc;
import core.Log;
import core.Service;
import core.Util;

public class LuyenThe {
	public static void start(Player p) throws IOException {
		if (p.luyenthe < 12) {
			if (p.item.total_item_by_id(4, 318) < 100) {
				Service.send_notice_box(p.conn, "Không đủ 100 ngọc thể đan");
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
                        
			p.item.remove(4, 318, 100);
			p.update_vang(-5_000_000);
			p.update_ngoc(-5_000);
                        
			p.item.char_inventory(4);
			if (30 > Util.random(250)) {
				p.luyenthe++;
				Service.send_char_main_in4(p);
				Service.send_notice_box(p.conn,
				      "Thành công luyện thể đến tầng " + p.luyenthe + "\nĐạt được:\n" + "+ " + (p.luyenthe * 20)
				            + " sát thương\r\n" + "+ " + (p.luyenthe * 20) + " phòng thủ\r\n" + "+ "
				            + String.format("%.2f", p.luyenthe * 0.2f) + "% Chí Mạng\r\n" + "+ "
				            + String.format("%.2f", p.luyenthe * 0.2f) + "% Xuyên giáp\r\n" + "+ " + (p.luyenthe * 2000)
				            + " máu\r\n" + "+ " + (p.luyenthe * 200) + " mana");
			} else {
				Service.send_notice_box(p.conn, "Thất bại, hãy nghỉ nghơi và thử lại");
			}
                        Service.send_char_main_in4(p);
                                
		} else {
			Service.send_notice_box(p.conn, "Đã đạt cảnh giới tối đa");
		}
	}

	public static void send_info(Player p) throws IOException {
		String notice = "Hiện tại: luyện đến tầng " + p.luyenthe
		      + " / 12.\nTầng tiếp theo cần 100 Ngọc thể đan, 5tr vàng và 5k ngọc, tỷ lệ 30%";
		Service.send_notice_box(p.conn, notice);
	}
}
