package client;

import java.io.IOException;
import java.util.regex.Pattern;
import core.Log;
import core.Service;
import io.Message;
import io.Session;

public class TextFromClient_2 {
	public static void process(Session conn, Message m2) throws IOException {
		short type = m2.reader().readShort();
		byte size = m2.reader().readByte();
		String[] value = new String[size];
		for (int i = 0; i < size; i++) {
			value[i] = m2.reader().readUTF();
		}
		switch (type) {
			case 0: {
				if (!value[0].equals("") && !value[1].equals("")) {
					// Service.send_notice_box(conn, "Bạn đã đặt tên bang là \"" + value[0] + "\" và tên viết tắt là \""
					// + value[1] + "\" đúng không?\n đang test thôi nên éo có bang đâu kkk :v");
					if (value[0].contains("_") || value[0].contains("-") || value[0].contains("@") || value[0].contains("#")
					      || value[0].contains("^") || value[0].contains("$") || value[0].length() > 20
					      || value[0].length() < 4) {
						Service.send_notice_box(conn, "Tên nhập vào không hợp lệ");
						return;
					}
					Pattern p = Pattern.compile("^[a-zA-Z0-9]{3,3}$");
					if (!p.matcher(value[1]).matches()) {
						Service.send_notice_box(conn, "Tên rút gọn nhập vào không hợp lệ");
						return;
					}
					if (conn.p.get_ngoc() < 20000) {
						Service.send_notice_box(conn, "Không đủ 20k ngọc!");
						return;
					}
					if (Clan.create_clan(conn, value[0], value[1])) {
						conn.p.item.char_inventory(5);
						Service.send_box_UI(conn, 20);
						Service.send_notice_box(conn, "Hãy chọn một icon bất kỳ đặt làm biểu tượng");
					}
				} else {
					Service.send_notice_box(conn, "bỏ ô trống thì tạo bang cho mày thế éo nào đc hả?");
				}
				break;
			}
		}
	}
}
