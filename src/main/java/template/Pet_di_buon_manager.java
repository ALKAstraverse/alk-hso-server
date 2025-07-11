package template;

import java.util.HashMap;
import java.util.Map;

public class Pet_di_buon_manager {
	private static final HashMap<String, Pet_di_buon> list = new HashMap<>();

	public static synchronized void add(String name, Pet_di_buon temp) {
		Pet_di_buon_manager.list.put(name, temp);
	}

	public static synchronized void remove(String name) {
		Pet_di_buon_manager.list.remove(name);
	}

	public static synchronized Pet_di_buon check(int n) {
		for (Map.Entry<String, Pet_di_buon> en : Pet_di_buon_manager.list.entrySet()) {
			Pet_di_buon temp = en.getValue();
			if (temp.index == n) {
				return temp;
			}
		}
		return null;
	}
}
