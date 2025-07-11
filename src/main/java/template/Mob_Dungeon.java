package template;

public class Mob_Dungeon {
	public short index_mob;
	public Mob template;
	public int hp;
	public int x;
	public int y;
	public int from_gate;
	public boolean is_atk;
	public boolean isdie;
	public byte color_name;
	public int hpmax;

	public Mob_Dungeon(int index, Mob mob) {
		this.index_mob = (short) index;
		this.template = mob;
		is_atk = false;
		isdie = false;
		color_name = 0;
	}
}
