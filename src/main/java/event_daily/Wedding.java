package event_daily;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import org.json.simple.JSONArray;
import client.Player;
import core.SQL;
import core.Service;
import core.Util;
import map.MapService;
import template.Item3;
import template.Option;

public class Wedding {
	public static List<Wedding> list = new ArrayList<>();
	public String name_1;
	public String name_2;
	public Item3 it;
	public long exp;

	@SuppressWarnings("unchecked")
	public synchronized static void add_new(int quant, Player p, Player p0) throws IOException {
		Wedding temp = new Wedding();
		temp.name_1 = p.name;
		temp.name_2 = p0.name;
		temp.it = new Item3();
		temp.it.id = 0;
		temp.it.name = "Cặp đôi " + p0.name + " " + p.name;
		temp.it.clazz = 4;
		temp.it.type = 103;
		temp.it.level = 60;
		temp.it.icon = 13166;
		temp.it.color = (byte) (quant - 1);
		temp.it.part = 0;
		temp.it.tier = 0;
		temp.it.islock = true;
		temp.it.op = new ArrayList<>();
		int[] dame = new int[] {250, 500, 750, 1000};
		int[] dame_per = new int[] {1000, 1500, 2000, 2500};
		int[] point = new int[] {100, 200, 300, 400};
		int[] resis = new int[] {1000, 1500, 2000, 2500};
		for (int i = 0; i < 5; i++) {
			temp.it.op.add(new Option(i, Util.random(10, dame[temp.it.color])));
		}
		for (int i = 7; i < 12; i++) {
			temp.it.op.add(new Option(i, Util.random(500, dame_per[temp.it.color])));
		}
		for (int i = 23; i < 27; i++) {
			temp.it.op.add(new Option(i, Util.random(50, point[temp.it.color])));
		}
		for (int i = 16; i < 21; i++) {
			temp.it.op.add(new Option(i, Util.random(500, resis[temp.it.color])));
		}
		temp.it.time_use = 0;
		temp.exp = 0;
		// up sql
		try (Connection connection = SQL.gI().getConnection(); Statement st = connection.createStatement();) {
			String query = "INSERT INTO `wedding` (`name`, `item`) VALUES (%s, %s)";
			JSONArray js = new JSONArray();
			js.add(temp.name_1);
			js.add(temp.name_2);
			//
			JSONArray js2 = new JSONArray();
			js2.add(temp.exp);
			js2.add(temp.it.color);
			js2.add(temp.it.tier);
			JSONArray js22 = new JSONArray();
			for (int i = 0; i < temp.it.op.size(); i++) {
				JSONArray js23 = new JSONArray();
				js23.add(temp.it.op.get(i).id);
				js23.add(temp.it.op.get(i).getParam(0));
				js22.add(js23);
			}
			js2.add(js22);
			st.execute(String.format(query, "'" + js.toJSONString() + "'", "'" + js2.toJSONString() + "'"));
                        connection.commit();
		} catch (SQLException e) {
			e.printStackTrace();
			p.conn.close();
			return;
		}
		Wedding.list.add(temp);
		//
		p.item.wear[23] = temp.it;
		Service.send_wear(p);
		Service.send_char_main_in4(p);
		MapService.update_in4_2_other_inside(p.map, p);
		p.it_wedding = temp;
		//
		p0.item.wear[23] = temp.it;
		Service.send_wear(p0);
		Service.send_char_main_in4(p0);
		MapService.update_in4_2_other_inside(p0.map, p0);
		p0.it_wedding = temp;
	}

	public synchronized static Wedding get_obj(String name) {
		Wedding result = null;
		for (int i = 0; i < Wedding.list.size(); i++) {
			Wedding temp = Wedding.list.get(i);
			if (temp.name_1.equals(name) || temp.name_2.equals(name)) {
				result = temp;
				break;
			}
		}
		return result;
	}

	public synchronized static void remove_wed(Wedding temp) {
		Wedding.list.remove(temp);
                try (Connection conn = SQL.gI().getConnection(); Statement st = conn.createStatement()) {
			String query = "DELETE FROM `wedding` WHERE `name` = '%s'";
			JSONArray js = new JSONArray();
			js.add(temp.name_1);
			js.add(temp.name_2);
			st.execute(String.format(query, js.toString()));
                        conn.commit();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
