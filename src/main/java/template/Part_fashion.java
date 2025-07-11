package template;

import java.util.ArrayList;
import java.util.List;
import client.Player;

public class Part_fashion {

    public static final List<Part_fashion> entrys = new ArrayList<>();
    public short id;
    public byte[] part;

    public static byte[] get_part(Player p) {
        if (p.item.wear[11] != null) {
            for (Part_fashion temp : entrys) {
                if (temp.id == p.item.wear[11].id) {
                    return temp.part;
                }
            }
        }
        return new byte[]{-1, -1, -1, -1, -1, -1, -1};
    }
}
