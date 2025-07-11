package map;

import java.io.IOException;
import client.Player;
import io.Message;

public class Npc {

    public static String CHAT_MR_BALLARD = "Chiến trường bắt đầu vào 21h30 phút hàng ngày";
    public static String CHAT_TOP = "Nỡ lòng nào các bạn nhổ nước bọt vào mình :(";
    public static String CHAT_PHO_CHI_HUY = "Đến đây rồi các bạn sẽ biết 1 người phó lãnh đạo là như thế nào";
    public static String CHAT_PHAP_SU = "Đập đồ ở đây có tỷ lệ lên rất cao khi đã trừ đi tỷ lệ xịt :D";
    public static String CHAT_ZORO = "Cụ đi chân lạnh toát khi bang chiến không thắng";
    public static String CHAT_AMAN = "Test chút nhân phẩm với chức năng chuyển sinh nèo hehe";
    public static String CHAT_ODA = "Đừng gọi em là thiếu nữ. Trong khi thứ em thiếu là anh.";
    public static String CHAT_LISA = "Cuộc đời thật lắm bất công ta 2 bịch sữa mi không bịch nào khụ khụ :D";
    public static String CHAT_SOPHIA = "Bao nhiêu cân thính cho vừa\n"
            + "Bao nhiêu cân bả mới lừa được em";
    public static String CHAT_HAMMER = "Người ta dính phốt ngoại tình\n" +
"Còn tôi dính phốt một mình lâu năm.";
    public static String CHAT_ZULU = "Anh ơi trái đất dẫu tròn\n"
            + " anh trốn không kỹ là còn gặp em.";
    public static String CHAT_DOUBA = "Biết ông Thương không? Thương nào?";
    public static String CHAT_ANNA = "Hoa chỉ nở khi có người tưới nước.\n"
            + " Em chỉ cười khi đứng trước người em yêu";
    public static String CHAT_BXH = "Hiệp Sĩ Ong Đất Chúc các chú Ong online vui vẻ";

    public static void chat(Map map, String txt, int id) throws IOException {
        Message m = new Message(23);
        m.writer().writeUTF(txt);
        m.writer().writeByte(id);
        for (int j = 0; j < map.players.size(); j++) {
            Player p0 = map.players.get(j);
            if (p0 != null && p0.map.equals(map)) {
                p0.conn.addmsg(m);
            }
        }
        m.cleanup();
    }
}
