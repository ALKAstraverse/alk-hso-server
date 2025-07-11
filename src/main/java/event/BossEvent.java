package event;

import java.util.ArrayList;
import java.util.List;
import map.Mob_in_map;
import template.MiNuong;

public class BossEvent {
	public static List<Mob_in_map> LIST = new ArrayList<>();
	public static List<MiNuong> MiNuong = new ArrayList<>();
	public static long[] time;

	public static void call_boss() {
		for (int i = 0; i < BossEvent.LIST.size(); i++) {
			if (BossEvent.LIST.get(i).isdie && !BossEvent.LIST.get(i).is_boss_active
			      && time[i] < System.currentTimeMillis()) {
				BossEvent.LIST.get(i).is_boss_active = true;
			}
		}
	}
}
