package map;

import java.io.IOException;
import client.Player;
import io.Message;

public class Eff_player_in_map {

    public static void add(Player p, int index) throws IOException {
        Message mm = new Message(75);
        mm.writer().writeByte(2);
        mm.writer().writeByte(1);
        mm.writer().writeShort(index);
        mm.writer().writeShort(-1);
        mm.writer().writeByte(0);
        mm.writer().writeShort(p.id);
        for (int i = 0; i < p.map.players.size(); i++) {
            p.map.players.get(i).conn.addmsg(mm);
        }
        mm.cleanup();
    }
}
