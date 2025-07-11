package map;

import java.util.ArrayList;
import java.util.List;
import client.Player;
import core.Util;
import java.util.HashMap;
import template.Mob;

public class Mob_in_map {
	public static final List<Mob_in_map> list_boss = new ArrayList<>();
	public static final int time_refresh = 3;
	public static int num_boss = 0;
	public Mob template;
	public int index;
	public short x;
	public short y;
	public short level;
	public byte map_id;
	public boolean isdie;
	public byte color_name;
	public boolean is_boss;
	public int hpmax;
	public int hp;
	public long time_back;
	public byte zone_id;
	public List<Player> list_fight;
	public long time_fight;
	public boolean is_boss_active;
        public HashMap<String, Long> top_dame = new HashMap<>();

	public synchronized static void create_boss() {
		if (Mob_in_map.list_boss.size() > 10) {
			List<Mob_in_map> list = new ArrayList<>();
			for (Mob_in_map mob : Mob_in_map.list_boss) {
				if (!mob.is_boss_active) {
					list.add(mob);
				}
			}
			if (list.size() > 0) {
				Mob_in_map boss = list.get(Util.random(list.size()));
				boss.is_boss_active = true;
				num_boss++;
			}
		}
	}
	
	public static synchronized void mo_boss_now() {
    for (int i = 0; i < 5; i++) {
        create_boss();
        }
    }
    
    
}
