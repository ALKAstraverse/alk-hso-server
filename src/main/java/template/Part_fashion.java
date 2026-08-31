package template;

import java.util.ArrayList;
import java.util.List;
import client.Player;

public class Part_fashion {

    public static final List<Part_fashion> entrys = new ArrayList<>();
    public short id;
    public byte[] part;

    public static byte[] get_part(Player p) {
        if (p != null && p.item != null && p.item.wear != null) {
            return get_part_from_wear(p.item.wear);
        }
        return new byte[]{-1, -1, -1, -1, -1, -1, -1};
    }

    public static byte[] get_part_from_wear(Item3[] wear) {
        if (wear != null && wear.length > 11 && wear[11] != null) {
            for (Part_fashion temp : entrys) {
                if (temp.id == wear[11].id) {
                    return temp.part;
                }
            }
        }
        return new byte[]{-1, -1, -1, -1, -1, -1, -1};
    }
}
