package map;

import java.util.ArrayList;
import java.util.List;

public class DungeonManager {
	private static final List<Dungeon> list = new ArrayList<>();

	public static synchronized void add_list(Dungeon d) {
		DungeonManager.list.add(d);
	}

	public static synchronized void remove_list(Dungeon d) {
		DungeonManager.list.remove(d);
	}

	public static synchronized Dungeon get_list(String name) {
    for (Dungeon dungeon : DungeonManager.list) {
        if (dungeon.name_party != null && dungeon.name_party.equals(name)) {
            return dungeon;
        }
    }
    return null;
}
}
