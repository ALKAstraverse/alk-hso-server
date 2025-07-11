package event_daily;

import java.io.IOException;
import client.Player;
import core.GameSrc;
import core.Log;
import core.Service;
import core.Util;

public class TuTien {

    public static String[] NAME = new String[]{"Luyện khí", "Trúc cơ", "Kết đan", "Nguyên anh", "Hoá thần", "Luyện hư",
        "Hợp thể", "Đại thừa", "Chân tiên", "Độ kiếp"};
    public static String[] NAME_2 = new String[]{"Sơ kỳ", "Trung kỳ", "Hậu kỳ", "Viên mãn", "Đại viên mãn"};
    public static int[][] op = new int[][]{ //
        new int[]{8, 9, 10, 11, 15}, // luyen khi
        new int[]{8, 9, 10, 11, 15}, // trúc cơ
        new int[]{8, 9, 10, 11, 33, 15}, //Kết đan
        new int[]{8, 9, 10, 11, 33, 15, 51}, //Nguyên anh
        new int[]{8, 9, 10, 11, 33, 15, 51}, //Hoá thần
        new int[]{8, 9, 10, 11, 33, 15, 51, 36}, //Luyện hư
        new int[]{8, 9, 10, 11, 33, 15, 51, 36}, //Hợp thể
        new int[]{8, 9, 10, 11, 33, 15, 51, 36}, //Đại thừa
        new int[]{8, 9, 10, 11, 33, 15, 51, 36}, //Chân tiên
        new int[]{8, 9, 10, 11, 33, 15, 51, 36}, //Độ kiếp
    };
    public static int[][] par = new int[][]{ //
        new int[]{1000, 1000, 1000, 1000, 1000}, //  luyen khi
        new int[]{2000, 2000, 2000, 2000, 2000}, //trúc cơ
        new int[]{2000, 2000, 2000, 2000, 2000, 2000}, //Kết đan
        new int[]{2000, 2000, 2000, 2000, 2000, 2000, 500},  //Nguyên anh
        
        new int[]{2000, 2000, 2000, 2000, 2000, 2000, 500}, //Hoá thần
        new int[]{2000, 2000, 2000, 2000, 2000, 2000, 1000, 2000}, //Luyện hư
        new int[]{2000, 2000, 2000, 2000, 2000, 2000, 1000, 2000}, //Hợp thể
        new int[]{2000, 2000, 2000, 2000, 2000, 2000, 1000, 2000}, //Đại thừa
        new int[]{2000, 2000, 2000, 2000, 2000, 2000, 1000, 2000}, //Chân tiên
        new int[]{2000, 2000, 2000, 2000, 2000, 2000, 1000, 2000}, //Độ kiếp
    };

    public static void start(Player p) throws IOException {
        if (p.tutien[0] < 10) {
            if (p.tutien[1] < 5) {
                if (p.tutien[2] < 500) {
                    Service.send_notice_box(p.conn, "Không đủ 500 exp tu tiên");
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
                
                p.tutien[2] -= 500;
                p.update_vang(-5_000_000);
                p.update_ngoc(-5_000);
                p.item.char_inventory(5);
                p.tutien[1]++;
                Service.send_char_main_in4(p);
                Service.send_notice_box(p.conn, "Thành công tu tiên tại cảnh giới " + TuTien.NAME[p.tutien[0] - 1]
                        + " đạt cấp " + TuTien.NAME_2[p.tutien[1] - 1]);
            } else {
                if (p.tutien[2] < 1000) {
                    Service.send_notice_box(p.conn, "Không đủ 1000 exp tu tiên");
                    return;
                }
                if (p.item.total_item_by_id(4, 320) < 100) {
                    Service.send_notice_box(p.conn, "Không đủ 100 thông thiên đan");
                    return;
                }
                if (p.get_vang() < 10_000_000) {
                    Service.send_notice_box(p.conn, "Không đủ 10tr vàng");
                    return;
                }
                if (p.get_ngoc() < 10_000) {
                    Service.send_notice_box(p.conn, "Không đủ 10k ngọc");
                    return;
                }
                p.tutien[2] -= 1000;
                p.item.remove(4, 320, 100);
                p.update_vang(-10_000_000);
                p.update_ngoc(-10_000);
                p.item.char_inventory(4);
            
            if (10 > Util.random(250) && p.tutien[1] == 5) {
                p.tutien[1] = 1;
                p.tutien[0]++;
                Service.send_notice_box(p.conn, "Thành công đột phá cảnh giới " + TuTien.NAME[p.tutien[0] - 2]
                        + "\nĐạt cảnh giới mới: + " + TuTien.NAME[p.tutien[0] - 1]);
                Service.send_char_main_in4(p);
            } else {
                Service.send_notice_box(p.conn, "Thất bại, hãy nghỉ nghơi và thử lại");
            }
            }
        } else {
            Service.send_notice_box(p.conn, "Đã đạt cảnh giới tối đa");
        }
    }

    public static void send_info(Player p) throws IOException {
        if (p.tutien[0] < 10) {
            String notice = "Hiện tại: đả thông được ";
            for (int i = 0; i < p.tutien[0] - 1; i++) {
                notice += TuTien.NAME[i] + ", ";
            }
            notice += "\nCảnh giới hiện tại: " + TuTien.NAME[p.tutien[0] - 1] + " cấp " + TuTien.NAME_2[p.tutien[1] - 1]
                    + "\nExp: " + p.tutien[2];
            Service.send_notice_box(p.conn, notice);
        } else {
            Service.send_notice_box(p.conn, "Đả thông toàn bộ");
        }
    }
}
