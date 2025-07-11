package event;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Event_1 {
	public static final HashMap<String, Integer> list_naukeo = new HashMap<>();
	public static final HashMap<String, Integer> list_nhankeo = new HashMap<>();
	public static NauKeo naukeo;
	public static final List<BXH_naukeo> list_bxh_naukeo = new ArrayList<>();
        public static final Set<String> list_bxh_naukeo_name = new HashSet<>();
        public static final List<BXH_naukeo> list_caythong = new ArrayList<>();
        public static final List<BXH_naukeo> list_tanghoa = new ArrayList<>();

	public synchronized static boolean check(String name) {
		return Event_1.list_naukeo.containsKey(name);
	}

	public synchronized static void add_material(String name, int quant) {
		if (quant == 0) {
			Event_1.list_naukeo.put(name, 0);
		} else {
			Event_1.list_naukeo.put(name, (Event_1.list_naukeo.get(name) + quant));
		}
		boolean check = false;
		for (int i = 0; i < list_bxh_naukeo.size(); i++) {
			if (list_bxh_naukeo.get(i).name.equals(name)) {
				list_bxh_naukeo.get(i).quant = Event_1.list_naukeo.get(name);
				list_bxh_naukeo.get(i).time = System.currentTimeMillis();
				check = true;
				break;
			}
		}
		if (!check) {
			list_bxh_naukeo.add(new BXH_naukeo(name, Event_1.list_naukeo.get(name), System.currentTimeMillis()));
		}
	}
        
        public synchronized static void add_caythong(String name, int quant) {
		boolean check = false;
		for (int i = 0; i < list_caythong.size(); i++) {
			if (list_caythong.get(i).name.equals(name)) {
				list_caythong.get(i).quant += quant;
				list_caythong.get(i).time = System.currentTimeMillis();
				check = true;
				break;
			}
		}
		if (!check) {
			list_caythong.add(new BXH_naukeo(name, quant, System.currentTimeMillis()));
		}
	}
        public synchronized static void add_tanghoa(String name, int quant) {
		boolean check = false;
		for (int i = 0; i < list_tanghoa.size(); i++) {
			if (list_tanghoa.get(i).name.equals(name)) {
				list_tanghoa.get(i).quant += quant;
				list_tanghoa.get(i).time = System.currentTimeMillis();
				check = true;
				break;
			}
		}
		if (!check) {
			list_tanghoa.add(new BXH_naukeo(name, quant, System.currentTimeMillis()));
		}
	}

	public synchronized static void finish() {
                Event_1.list_nhankeo.clear();
		Event_1.list_nhankeo.putAll(Event_1.list_naukeo);
		Event_1.list_naukeo.clear();
                Event_1.list_bxh_naukeo_name.clear();
                for (int i = 0; i < Event_1.list_bxh_naukeo.size(); i++) {
                if (i > 9) {
                    break;
                }
                Event_1.list_bxh_naukeo_name.add(Event_1.list_bxh_naukeo.get(i).name);
            }
                Event_1.list_bxh_naukeo.clear();
                
	}

	public synchronized static int get_keo(String name) {
		int quant = 0;
		if (Event_1.list_nhankeo.containsKey(name)) {
			quant += Event_1.list_nhankeo.get(name);
			Event_1.list_nhankeo.remove(name);
		}
		return quant;
	}

	public synchronized static boolean check_time_can_register() {
		if (Event_1.naukeo.h >= 17 && Event_1.naukeo.h <= 18) {
			return false;
		}
		if (Event_1.naukeo.h == 16 && Event_1.naukeo.m >= 30) {
			return false;
		}
		return true;
	}

	public synchronized static int get_keo_now(String name) {
		int quant = 0;
		if (Event_1.list_naukeo.containsKey(name)) {
			quant += Event_1.list_naukeo.get(name);
		}
		return quant;
	}

	public synchronized static String[] get_top_naukeo() {
		if (Event_1.list_bxh_naukeo.size() == 0) {
			return new String[] {"Chưa có thông tin"};
		}
		String[] top;
		if (Event_1.list_bxh_naukeo.size() < 10) {
			top = new String[Event_1.list_bxh_naukeo.size()];
		} else {
			top = new String[10];
		}
		for (int i = 0; i < top.length; i++) {
			top[i] = "Top " + (i + 1) + " : " + Event_1.list_bxh_naukeo.get(i).name + " : " + Event_1.list_bxh_naukeo.get(i).quant;
		}
		return top;
	}
        public synchronized static String[] get_top_tanghoa() {
		if (Event_1.list_tanghoa.size() == 0) {
			return new String[] {"Chưa có thông tin"};
		}
		String[] top;
		if (Event_1.list_tanghoa.size() < 10) {
			top = new String[Event_1.list_tanghoa.size()];
		} else {
			top = new String[10];
		}
		for (int i = 0; i < top.length; i++) {
			top[i] = "Top " + (i + 1) + " : " + Event_1.list_tanghoa.get(i).name + " : "+Event_1.list_tanghoa.get(i).quant+" lần" ;
		}
		return top;
	}
        public synchronized static String[] get_top_caythong() {
		if (Event_1.list_caythong.size() == 0) {
			return new String[] {"Chưa có thông tin"};
		}
		String[] top;
		if (Event_1.list_caythong.size() < 10) {
			top = new String[Event_1.list_caythong.size()];
		} else {
			top = new String[10];
		}
		for (int i = 0; i < top.length; i++) {
			top[i] = "Top " + (i + 1) + " : " + Event_1.list_caythong.get(i).name + " : "+Event_1.list_caythong.get(i).quant+" lần" ;
		}
		return top;
	}

	public synchronized static void sort_bxh() {
		Collections.sort(Event_1.list_bxh_naukeo, new Comparator<BXH_naukeo>() {
			@Override
			public int compare(BXH_naukeo o1, BXH_naukeo o2) {
				int compare = (o1.quant == o2.quant) ? 0 : ((o1.quant > o2.quant) ? -1 : 1);
				if (compare != 0) {
					return compare;
				}
				return (o1.time > o2.time) ? 1 : -1;
			}
		});
                while (Event_1.list_bxh_naukeo.size() > 10) {
                    Event_1.list_bxh_naukeo.remove(Event_1.list_bxh_naukeo.size()-1);
                }
                Collections.sort(Event_1.list_caythong, new Comparator<BXH_naukeo>() {
			@Override
			public int compare(BXH_naukeo o1, BXH_naukeo o2) {
				int compare = (o1.quant == o2.quant) ? 0 : ((o1.quant > o2.quant) ? -1 : 1);
				if (compare != 0) {
					return compare;
				}
				return (o1.time > o2.time) ? 1 : -1;
			}
		});
//                while (Event_1.list_caythong.size() > 10) {
//                    Event_1.list_caythong.remove(Event_1.list_caythong.size()-1);
//                }
	}

	public static class BXH_naukeo {
		public String name;
		public int quant;
		public long time;

		public BXH_naukeo(String name2, int integer, long t) {
			name = name2;
			quant = integer;
			time = t;
		}
	}
        
}
