package template;

import ai.NhanBan;
import client.Clan;
import map.Map;

public class Mob_MoTaiNguyen {

    public int index;
    public short x, y;
    public int hp;
    public int hp_max;
    public int level;
    public Map map;
    public String name_monster;
    public NhanBan nhanban;
    public NhanBan nhanban_save;
    public Clan clan;
    public boolean is_atk;
    public boolean isbuff_hp;
    public long time_buff;

    public Mob_MoTaiNguyen(int index, int x, int y, int hp, int hp_max, int level, Map map, String name_monster) {
        this.index = Short.toUnsignedInt((short) index);
        this.x = (short) x;
        this.y = (short) y;
        this.hp = hp;
        this.hp_max = hp_max;
        this.level = level;
        this.map = map;
        this.name_monster = name_monster;
        this.is_atk = false;
        this.isbuff_hp = false;
    }
}
