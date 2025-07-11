package map;

import java.util.List;
import template.Option;

public class ItemMap {
	public long time_exist;
	public byte color;
	public byte category;
	public short idmaster;
	public long time_pick;
	public short id_item;
	public short quantity;
	public List<Option> op;
        public byte lock = 1;
}
