package event_daily;

import java.io.IOException;
import client.Player;
import core.Service;
import core.Util;
import io.Message;
import template.Item47;
import template.ItemTemplate4;
import template.ItemTemplate7;

public class NV_TinhTu {
	// 0: type, 1: num0, 2: num2, 3: tong nhiem vu
	public static String[] NAME = new String[] { //
	      "Mua bán", // 0
	      "Thu thập", // 1
	      "Cường hóa", // 2
	      "Cường hóa 2", // 3
	      "Cường hóa 3", // 4
	      "Thử vận may", // 5
	      "Thử vận may 2", // 6
	      "Đánh Boss", // 7
	      "Đánh Boss 2", // 8
	      "Tiêu xài", // 9
	      "Tiêu xài 2", // 10
	      "Mở rương", // 11
	      "Phó bản", // 12
	      "Phó bản 2", // 13
	      "Thách đấu", // 14
	      "Làm giàu", // 15
	      "Rèn luyện", // 16
	      "Rèn luyện 2", // 17
	      "Mở ly", // 18
	      "Bán đồ", // 19
	      "Rêu rao", // 20
	      "Tìm đồ quý", // 21
	      "Tìm đồ quý 2", // 22
	      "Khám phá", // 23
	      "Chăm chỉ", // 24
	      "Lôi đài", // 25
	      "Thể hiện", // 26
	      "Chiếm mỏ", // 27
	      "Chế tạo", // 28
	      "Săn kho báu", // 29
	      "Ăn cướp", // 30
	      "Quấy phá", // 31
	      "Chữa trị", // 32
	      "Kết bạn" // 33
	};
	public static String[] CONTENT = new String[] { //
	      "Tham gia mua bán 1 món đồ ở khu mua bán\r\n" + "[Hoặc treo bán 1 món đồ cũng tính]", // 0
	      "Thu thập 50 ngọc phong ma từ khu mỏ", // 1
	      "Cường hóa thành công 5 lần", // 2
	      "Cường hóa 1 món bất kì lên +6", // 3
	      "Cường hóa thất bại 10 lần", // 4
	      "Tham gia vòng xoay may mắn vip 1 lần", // 5
	      "Chiến thắng vxmm 1 lần", // 6
	      "	Tiêu diệt 2 con Sói Bất Tử\r\n" + "Tiêu diệt 2 con Thủy Quái", // 7
	      "Tiêu diệt random 1 Boss level\r\n" + "[Hoặc Boss nhiệm vụ 3x4x.. đều tính]", // 8
	      "Xài 30.000 vàng", // 9
	      "Mua 1 vật phẩm bất kì bằng ngọc", // 10
	      "Mở 1 rương may mắn bất kì", "Vào phó bản 1 lần", "Vào 5 lần phó bản", "Chiến thắng 3 người trong top pk",
	      "Làm thương nhân và đi buôn 1 lần", "Tiêu diệt 500 quái +-5 level", "Chạy 10.000 km", "Sử dụng 5 vé mở ly",
	      "Bán 20 trang bị bất kì", "Chat kênh thế giới 1 lần", "Đánh quái ,nhặt 5 trang bị tím",
	      "Đánh quái ,nhặt 3 trang bị cam", "Vào mê cung 1 lần", "Hoàn thành 3 nhiệm vụ hằng ngày",
	      "Tham gia 3 trận lôi đài", "PK vàng với 10 người +-5 level",
	      "Đánh sập mỏ 1 lần\r\n" + "[áp dụng party vẫn tính]", "Chế tạo 1 mề đay", "Đập 5 thùng gỗ",
	      "Cướp hàng từ thương nhân và hoàn thành chuyến hàng đó", "Đồ sát 5 người = >level", "Dùng 1 cơm ga",
	      "Kết bạn 3 người"};

	public static void get_quest(Player p) throws IOException {
		if (p.nv_tinh_tu[0] != -1) {
			info_quest(p);
		} else {
			p.nv_tinh_tu[0] = (short) Util.random(NAME.length);
			p.nv_tinh_tu[1] = 0;
			Service.send_notice_box(p.conn, "Nhiệm vụ " + NAME[p.nv_tinh_tu[0]] + "\n" + CONTENT[p.nv_tinh_tu[0]]);
			switch (p.nv_tinh_tu[0]) {
				case 0: {
					p.nv_tinh_tu[2] = 1;
					break;
				}
				case 1: {
					p.nv_tinh_tu[2] = 50;
					break;
				}
				case 2: {
					p.nv_tinh_tu[2] = 5;
					break;
				}
				case 3: {
					p.nv_tinh_tu[2] = 1;
					break;
				}
				case 4: {
					p.nv_tinh_tu[2] = 10;
					break;
				}
				case 5: {
					p.nv_tinh_tu[2] = 1;
					break;
				}
				case 6: {
					p.nv_tinh_tu[2] = 1;
					break;
				}
				case 7: {
					p.nv_tinh_tu[2] = 2;
					break;
				}
				case 8: {
					p.nv_tinh_tu[2] = 2;
					break;
				}
				case 9: {
					p.nv_tinh_tu[2] = 30_000;
					break;
				}
				case 10: {
					p.nv_tinh_tu[2] = 1;
					break;
				}
				case 11: {
					p.nv_tinh_tu[2] = 1;
					break;
				}
				case 12: {
					p.nv_tinh_tu[2] = 1;
					break;
				}
				case 13: {
					p.nv_tinh_tu[2] = 5;
					break;
				}
				case 14: {
					p.nv_tinh_tu[2] = 3;
					break;
				}
				case 15: {
					p.nv_tinh_tu[2] = 1;
					break;
				}
				case 16: {
					p.nv_tinh_tu[2] = 500;
					break;
				}
				case 17: {
					p.nv_tinh_tu[2] = 500;
					break;
				}
				case 18: {
					p.nv_tinh_tu[2] = 10_000;
					break;
				}
				case 19: {
					p.nv_tinh_tu[2] = 5;
					break;
				}
				case 20: {
					p.nv_tinh_tu[2] = 20;
					break;
				}
				case 21: {
					p.nv_tinh_tu[2] = 1;
					break;
				}
				case 22: {
					p.nv_tinh_tu[2] = 5;
					break;
				}
				case 23: {
					p.nv_tinh_tu[2] = 3;
					break;
				}
				case 24: {
					p.nv_tinh_tu[2] = 1;
					break;
				}
				case 25: {
					p.nv_tinh_tu[2] = 3;
					break;
				}
				case 26: {
					p.nv_tinh_tu[2] = 3;
					break;
				}
				case 27: {
					p.nv_tinh_tu[2] = 10;
					break;
				}
				case 28: {
					p.nv_tinh_tu[2] = 1;
					break;
				}
				case 29: {
					p.nv_tinh_tu[2] = 5;
					break;
				}
				case 30: {
					p.nv_tinh_tu[2] = 1;
					break;
				}
				case 31: {
					p.nv_tinh_tu[2] = 5;
					break;
				}
				case 32: {
					p.nv_tinh_tu[2] = 1;
					break;
				}
				case 33: {
					p.nv_tinh_tu[2] = 3;
					break;
				}
			}
		}
	}

	public static void remove_quest(Player p) throws IOException {
		p.nv_tinh_tu[0] = -1;
		p.nv_tinh_tu[1] = 0;
		p.nv_tinh_tu[2] = 0;
		Service.send_notice_box(p.conn, "Hủy thành công");
	}

	public static void finish_quest(Player p) throws IOException {
		if (p.nv_tinh_tu[0] == -1) {
			Service.send_notice_box(p.conn, "Chưa nhận nhiệm vụ");
		} else if (p.nv_tinh_tu[1] < p.nv_tinh_tu[2]) {
			info_quest(p);
		} else {
			switch (p.nv_tinh_tu[0]) {
				case 0: {
					p.update_Exp(10_000, false);
					receiv_gift(p, 7, 2, 10);
					break;
				}
				case 1: {
					receiv_gift(p, 7, 3, 20);
					break;
				}
				case 2: {
					receiv_gift(p, 7, 12, 10);
					break;
				}
				case 3: {
					receiv_gift(p, 7, 11, 10);
					break;
				}
				case 4: {
					receiv_gift(p, 4, 52, 5);
					break;
				}
				case 5: {
					receiv_gift(p, 4, 206, 1);
					break;
				}
				case 6: {
					receiv_gift(p, 7, 11, 20);
					break;
				}
				case 7: {
					receiv_gift(p, 4, 61, 50);
					break;
				}
				case 8: {
					receiv_gift(p, 4, 241, 5);
					break;
				}
				case 9: {
					receiv_gift(p, 4, 206, 1);
					break;
				}
				case 10: {
					receiv_gift(p, 7, 13, 10);
					break;
				}
				case 11: {
					receiv_gift(p, 4, 207, 1);
					break;
				}
				case 12: {
					receiv_gift(p, 4, 142, 50);
					break;
				}
				case 13: {
					receiv_gift(p, 7, 1, 20);
					break;
				}
				case 14: {
					receiv_gift(p, 4, 241, 1);
					break;
				}
				case 15: {
					receiv_gift(p, 4, 205, 1);
					break;
				}
				case 16: {
					receiv_gift(p, 7, 9, 5);
					break;
				}
				case 17: {
					receiv_gift(p, 4, 142, 20);
					break;
				}
				case 18: {
					receiv_gift(p, 4, (50 > Util.random(120) ? 2 : 5), 50);
					break;
				}
				case 19: {
					receiv_gift(p, 7, 362, 10);
					break;
				}
				case 20: {
					receiv_gift(p, 7, 13, 10);
					break;
				}
				case 21: {
					receiv_gift(p, 4, 207, 1);
					break;
				}
				case 22: {
					receiv_gift(p, 7, 2, 50);
					break;
				}
				case 23: {
					receiv_gift(p, 4, 52, 10);
					break;
				}
				case 24: {
					receiv_gift(p, 4, 205, 1);
					break;
				}
				case 25: {
					receiv_gift(p, 7, 10, 20);
					break;
				}
				case 26: {
					receiv_gift(p, 4, 18, 20);
					break;
				}
				case 27: {
					receiv_gift(p, 4, 124, 20);
					break;
				}
				case 28: {
					receiv_gift(p, 4, 222, 30);
					break;
				}
				case 29: {
					receiv_gift(p, 7, 367, 10);
					break;
				}
				case 30: {
					receiv_gift(p, 4, 61, 50);
					break;
				}
				case 31: {
					receiv_gift(p, 4, 24, 2);
					break;
				}
				case 32: {
					receiv_gift(p, 7, 8, 5);
					break;
				}
				case 33: {
					receiv_gift(p, 4, 206, 1);
					break;
				}
			}
			p.nv_tinh_tu[0] = -1;
			p.nv_tinh_tu[1] = 0;
			p.nv_tinh_tu[2] = 0;
			p.nv_tinh_tu[3]++;
		}
	}

	private static void receiv_gift(Player p, int i, int j, int k) throws IOException {
		Message m12 = new Message(78);
		m12.writer().writeUTF("Phần thưởng nhiệm vụ");
		m12.writer().writeByte(1); // size
		//
		if (i == 4) {
			m12.writer().writeUTF(""); // name
			m12.writer().writeShort(ItemTemplate4.item.get(j).getIcon()); // icon
			m12.writer().writeInt(k); // quantity
			m12.writer().writeByte(4); // type in bag
			m12.writer().writeByte(0); // tier
			m12.writer().writeByte(0); // color
		} else {
			m12.writer().writeUTF(""); // name
			m12.writer().writeShort(ItemTemplate7.item.get(j).getIcon()); // icon
			m12.writer().writeInt(k); // quantity
			m12.writer().writeByte(7); // type in bag
			m12.writer().writeByte(0); // tier
			m12.writer().writeByte(0); // color
		}
		//
		Item47 it = new Item47();
		it.id = (short) j;
		it.quantity = (short) k;
		it.category = (byte) i;
		p.item.add_item_bag47(i, it);
		//
		m12.writer().writeUTF("");
		m12.writer().writeByte(1);
		m12.writer().writeByte(0);
		p.conn.addmsg(m12);
		m12.cleanup();
		p.item.char_inventory(4);
		p.item.char_inventory(7);
		p.item.char_inventory(3);
	}

	public static void info_quest(Player p) throws IOException {
		String notice = "Nhiệm vụ hiện tại của bạn là " + CONTENT[p.nv_tinh_tu[0]] + "\nQuá trình : " + p.nv_tinh_tu[1]
		      + " / " + p.nv_tinh_tu[2] + "\nHôm nay đã hoàn thành " + p.nv_tinh_tu[3] + " / 10";
		Service.send_notice_box(p.conn, notice);
	}
}
