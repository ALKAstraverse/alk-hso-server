package map;

import ai.NhanBan;
import ai.Player_Nhan_Ban;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import client.Party;
import client.Pet;
import client.Player;
import core.GameSrc;
import core.Log;
import core.Manager;
import core.MenuController;
import core.Service;
import core.Util;
import event.BossEvent;
import event_daily.ChienTruong;
import event_daily.LoiDai;
import io.Message;
import io.Session;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import template.EffTemplate;
import template.Item3;
import template.Item47;
import template.ItemTemplate3;
import template.ItemTemplate4;
import template.ItemTemplate7;
import template.Mob_MoTaiNguyen;
import template.NpcTemplate;
import template.Option_pet;

public class Map implements Runnable {

    public long time_use_item_arena;

    public static final List<Map[]> entrys = new ArrayList<>();
    public final List<Player> players;
    public final byte map_id;
    public final byte zone_id;
    public final ItemMap[] item_map;
    private final Thread mapthread;
    public Mob_in_map[] mobs;
    public static short head;
    public static short eye;
    public static short hair;
    public static short weapon;
    public static short body;
    public static short leg;
    public static short hat;
    public static short wing;
    public static String name_mo = "";
    public final String[] npc_name_data;
    public final String name;
    public final List<Vgo> vgos;
    public final byte typemap;
    public final boolean ismaplang;
    public final boolean showhs;
    public final byte maxplayer;
    public final byte maxzone;
    private final byte[] map_data;
    private boolean running;
    public int num_mob_super;
    public Dungeon d;
    public LoiDai ld;
    private long time_chat;

    public Map(int id, int zone, String[] npc_name, String name, byte typemap, boolean ismaplang, boolean showhs,
            int maxplayer, int maxzone, List<Vgo> vgo) throws IOException {
        this.map_id = (byte) id;
        this.zone_id = (byte) zone;
        this.npc_name_data = npc_name;
        this.name = name;
        this.typemap = typemap;
        this.ismaplang = ismaplang;
        this.showhs = showhs;
        this.maxplayer = (byte) maxplayer;
        this.maxzone = (byte) maxzone;
        this.item_map = new ItemMap[100];
        this.mapthread = new Thread(this);
        this.players = new ArrayList<>();
        this.vgos = vgo;
        this.running = false;
        this.num_mob_super = 0;
        this.map_data = Util.loadfile("data/mapnew/" + this.map_id);
    }

    public long time_ct;

    @Override
    public void run() {
		this.running = true;
		long time1 = 0;
		long time2 = 0;
		long time3 = 0;
		while (this.running) {
			try {
				time1 = System.currentTimeMillis();
				update();
				if (this.time_chat < System.currentTimeMillis()) {
					this.time_chat = System.currentTimeMillis() + 8000L;
					auto_chat_npc();
				}
				if (Map.is_map_chiem_mo(this, true)) {
					update_nhanban();
				}
				if (ChienTruong.gI().getStatus() == 2 && Map.is_map_chien_truong(this.map_id)) {
					if (this.time_ct < System.currentTimeMillis()) {
						this.time_ct = System.currentTimeMillis() + 5000L;
						for (int i = 0; i < this.players.size(); i++) {
							Player p0 = players.get(i);
							ChienTruong.gI().send_info(p0);
						}
					}
					Player_Nhan_Ban.update(this);
				}
				if (this.map_id == 48 && d != null) {
					d.update();
				}
				if (this.ld != null && LoiDai.state == 1) {
					this.ld.update_atk();
				}
				time2 = System.currentTimeMillis();
				time3 = (1_000L - (time2 - time1));
				if (time3 > 0) {
					if (time3 < 20) {
						System.err.println("map_id " + this.map_id + " - zone " + (this.zone_id + 1) + " overload...");
					}
					Thread.sleep(time3);
				}
			} catch (InterruptedException e) {
			} catch (Exception e) {
			}
		}
	}

    private synchronized void update_nhanban() throws IOException {
        // update mo tai nguyen
        Mob_MoTaiNguyen mobtainguyen = Manager.gI().chiem_mo.get_mob_in_map(this);
        if (mobtainguyen != null) {
            if (mobtainguyen.hp <= 0) {
                mobtainguyen.hp_max = (mobtainguyen.hp_max / 10) * 12;
                if (mobtainguyen.hp_max > 20_000_000) {
                    mobtainguyen.hp_max = 20_000_000;
                }
                mobtainguyen.hp = mobtainguyen.hp_max;
                mobtainguyen.isbuff_hp = false;
            }
            if (!mobtainguyen.isbuff_hp && mobtainguyen.hp < mobtainguyen.hp_max / 2) {
                mobtainguyen.hp_max = (mobtainguyen.hp_max / 10) * 12;
                if (mobtainguyen.hp_max > 20_000_000) {
                    mobtainguyen.hp_max = 20_000_000;
                }
                mobtainguyen.isbuff_hp = true;
            }
            if (mobtainguyen.isbuff_hp && mobtainguyen.time_buff < System.currentTimeMillis()) {
                mobtainguyen.time_buff = System.currentTimeMillis() + 2500L;
                int par = mobtainguyen.hp_max / 20;
                mobtainguyen.hp += par;
                if (mobtainguyen.hp > mobtainguyen.hp_max) {
                    mobtainguyen.hp = mobtainguyen.hp_max;
                    mobtainguyen.isbuff_hp = false;
                }
                Message m_hp = new Message(32);
                m_hp.writer().writeByte(1);
                m_hp.writer().writeShort(mobtainguyen.index);
                m_hp.writer().writeShort(-1); // id potion in bag
                m_hp.writer().writeByte(0);
                m_hp.writer().writeInt(mobtainguyen.hp_max); // max hp
                m_hp.writer().writeInt(mobtainguyen.hp); // hp
                m_hp.writer().writeInt(par); // param use
                for (int i = 0; i < this.players.size(); i++) {
                    this.players.get(i).conn.addmsg(m_hp);
                }
                m_hp.cleanup();
            }
        }
        //
        NhanBan temp = null;
        for (int i = 0; i < Manager.gI().list_nhanban.size(); i++) {
            if (Manager.gI().list_nhanban.get(i).map_id == this.map_id) {
                temp = Manager.gI().list_nhanban.get(i);
                break;
            }
        }
        if (temp != null) {
            if (temp.time_hp_buff < System.currentTimeMillis()) {
                temp.time_hp_buff = System.currentTimeMillis() + 2500L;
                if (temp.hp < temp.hp_max) {
                    int par = temp.hp_max / 20;
                    temp.hp += par;
                    if (temp.hp > temp.hp_max) {
                        temp.hp = temp.hp_max;
                    }
                    Message m_hp = new Message(32);
                    m_hp.writer().writeByte(0);
                    m_hp.writer().writeShort(temp.id_p);
                    m_hp.writer().writeShort(-1); // id potion in bag
                    m_hp.writer().writeByte(0);
                    m_hp.writer().writeInt(temp.hp_max); // max hp
                    m_hp.writer().writeInt(temp.hp); // hp
                    m_hp.writer().writeInt(par); // param use
                    for (int i = 0; i < this.players.size(); i++) {
                        this.players.get(i).conn.addmsg(m_hp);
                    }
                    m_hp.cleanup();
                }
            }
            if (temp.is_move && temp.act_time < System.currentTimeMillis()) {
                temp.act_time = System.currentTimeMillis() + 2000L;
                int[] x_ = new int[]{444, 1068, 228, 804, 516, 684, 540, 612, 1020, 444, 228, 612, 540, 492, 492, 756};
                int[] y_ = new int[]{156, 348, 516, 972, 372, 588, 588, 204, 204, 108, 372, 708, 396, 612, 420, 300};
                int[] map_ = new int[]{3, 5, 8, 9, 11, 12, 15, 16, 19, 21, 22, 24, 26, 27, 37, 42};
                for (int i = 0; i < map_.length; i++) {
                    if (map_[i] == temp.map_id) {
                        int x_old = temp.x;
                        int y_old = temp.y;
                        temp.x = (short) Util.random(x_[i] - 50, x_[i] + 50);
                        temp.y = (short) Util.random(y_[i] - 50, y_[i] + 50);
                        double a = Math.sqrt(Math.pow((x_old - temp.x), 2) + Math.pow((y_old - temp.y), 2));
                        if (a < 50) {
                            temp.x = (short) x_old;
                            temp.y = (short) y_old;
                        }
                        break;
                    }
                }
                Message m12 = new Message(4);
                m12.writer().writeByte(0);
                m12.writer().writeShort(0);
                m12.writer().writeShort(temp.id_p);
                m12.writer().writeShort(temp.x);
                m12.writer().writeShort(temp.y);
                m12.writer().writeByte(-1);
                for (int i = 0; i < this.players.size(); i++) {
                    Player p0 = this.players.get(i);
                    if (p0.map.map_id == this.map_id) {
                        p0.conn.addmsg(m12);
                    }
                }
                m12.cleanup();
            } else if (temp.p_target != null) {
                if (temp.p_target.conn.connected && temp.p_target.map.map_id == temp.map_id
                        && temp.p_target.map.zone_id == 4
                        && (Math.abs(temp.x - temp.p_target.x) < 200 && Math.abs(temp.y - temp.p_target.y) < 200)
                        && !temp.p_target.isdie) {
                    int dame_nhanban = temp.dame;
                    Message m = new Message(6);
                    m.writer().writeShort(temp.id_p);
                    m.writer().writeByte(temp.p_skill_id);
                    m.writer().writeByte(1);
                    m.writer().writeShort(temp.p_target.id);
                    boolean crit = temp.crit > Util.random(0, 10000);
                    if (crit) {
                        dame_nhanban *= 2;
                    }
                    int def = temp.p_target.body.get_def();
                    dame_nhanban = (int) ((((long) dame_nhanban) * (50_001 - def)) / 50_000L);
                    if (dame_nhanban <= 1) {
                        dame_nhanban = 1;
                    }
                    //
                    temp.p_target.hp -= dame_nhanban;
                    if (temp.p_target.hp <= 0) {
                        temp.p_target.hp = 0;
                        if (!temp.p_target.isdie) {
                            MapService.die_by_player(temp.p_target.map, temp.p_target, temp.p_target);
                        }
                    }
                    //
                    m.writer().writeInt(dame_nhanban); // dame
                    m.writer().writeInt((int) Math.min(temp.p_target.hp, Integer.MAX_VALUE)); // hp after
                    //
                    if (dame_nhanban > 0 && crit) {
                        m.writer().writeByte(1); // size color show
                        m.writer().writeByte(4); // 1: xuyen giap, 2:hut hp, 3: hut mp, 4: chi mang, 5: phan don
                        m.writer().writeInt(dame_nhanban); // par
                    } else {
                        m.writer().writeByte(0);
                    }
                    m.writer().writeInt(temp.hp);
                    m.writer().writeInt(1000); // mp nhan ban
                    m.writer().writeByte(11);
                    m.writer().writeInt(0);
                    MapService.send_msg_player_inside(temp.p_target.map, temp.p_target, m, true);
                    m.cleanup();
                } else {
                    temp.p_target = null;
                    temp.is_move = true;
                }
            }
        }
    }
private void auto_chat_npc() {
		try {
			switch (this.map_id) {
				case 4: {
					Npc.chat(this, Npc.CHAT_MR_BALLARD, -53);
					break;
				}
				case 1: {
					Npc.chat(this, Npc.CHAT_TOP, -49);
					Npc.chat(this, Npc.CHAT_PHO_CHI_HUY, -37);
					Npc.chat(this, Npc.CHAT_PHAP_SU, -36);
					Npc.chat(this, Npc.CHAT_ZORO, -2);
					Npc.chat(this, Npc.CHAT_AMAN, -7);
					Npc.chat(this, Npc.CHAT_ODA, -81);
					Npc.chat(this, Npc.CHAT_LISA, -3);
					Npc.chat(this, Npc.CHAT_SOPHIA, -69);
					Npc.chat(this, Npc.CHAT_HAMMER, -5);
					Npc.chat(this, Npc.CHAT_ZULU, -8);
					Npc.chat(this, Npc.CHAT_DOUBA, -4);
					Npc.chat(this, Npc.CHAT_ANNA, -44);
					Npc.chat(this, Npc.CHAT_BXH, -49);
					break;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

    private void update() {
        try {
            for (int i1 = 0; i1 < players.size(); i1++) {
                Player p = players.get(i1);
                if (this.map_id == 50) { // pet_manager
                    long now_time = System.currentTimeMillis();
                    for (Pet temp : p.mypet) {
                        if (temp.is_hatch && temp.time_born < now_time) {
                            temp.is_hatch = false;
                            //
                            Message m = new Message(44);
                            //
                            m.writer().writeByte(28);
                            m.writer().writeByte(0);
                            m.writer().writeByte(3);
                            m.writer().writeByte(3);
                            int dem = 0;
                            for (Pet temp2 : p.mypet) {
                                if (temp.is_hatch && temp2.time_born > now_time) {
                                    dem++;
                                }
                            }
                            m.writer().writeByte(dem);
                            for (Pet temp2 : p.mypet) {
                                if (temp.is_hatch && temp2.time_born > now_time) {
                                    int id_ = Pet.get_id(temp2.type);
                                    m.writer().writeUTF(ItemTemplate3.item.get(id_).getName());
                                    m.writer().writeByte(4); // clazz
                                    m.writer().writeShort(id_);
                                    m.writer().writeByte(14); // type
                                    m.writer().writeShort(ItemTemplate3.item.get(id_).getIcon());
                                    m.writer().writeByte(0); // tier
                                    m.writer().writeShort(10); // level
                                    m.writer().writeByte(0); // color
                                    m.writer().writeByte(1);
                                    m.writer().writeByte(1);
                                    m.writer().writeByte(0); // op size
                                    long time2 = ((temp2.time_born - now_time) / 60000) + 1;
                                    m.writer().writeInt((int) time2);
                                    m.writer().writeByte(0);
                                }
                            }
                            p.conn.addmsg(m);
                            m.cleanup();
                            //
                            m = new Message(44);
                            m.writer().writeByte(28);
                            m.writer().writeByte(1);
                            m.writer().writeByte(9);
                            m.writer().writeByte(9);
                            m.writer().writeUTF(temp.name);
                            m.writer().writeByte(temp.type);
                            m.writer().writeShort(p.mypet.indexOf(temp)); // id
                            m.writer().writeShort(temp.level);
                            m.writer().writeShort(temp.getlevelpercent()); // exp
                            m.writer().writeByte(temp.type);
                            m.writer().writeByte(temp.icon);
                            m.writer().writeByte(temp.nframe);
                            m.writer().writeByte(temp.color);
                            m.writer().writeInt(temp.get_age());
                            m.writer().writeShort(temp.grown);
                            m.writer().writeShort(temp.maxgrown);
                            m.writer().writeShort(temp.point1);
                            m.writer().writeShort(temp.point2);
                            m.writer().writeShort(temp.point3);
                            m.writer().writeShort(temp.point4);
                            m.writer().writeShort(temp.maxpoint);
                            m.writer().writeByte(temp.op.size());
                            for (int i2 = 0; i2 < temp.op.size(); i2++) {
                                Option_pet temp2 = temp.op.get(i2);
                                m.writer().writeByte(temp2.id);
                                m.writer().writeInt(temp2.param);
                                m.writer().writeInt(temp2.maxdam);
                            }
                            p.conn.addmsg(m);
                            m.cleanup();
                        }
                    }
                }
                p.update_wings_time();
                for (Pet pet : p.mypet) {
                    if (pet.grown > 0 && pet.time_eat < System.currentTimeMillis()) {
                        pet.time_eat = System.currentTimeMillis() + 180_000L;
                        pet.grown -= 1;
                        if (pet.is_follow) {
//                            Service.send_wear(p);
                        }
                    }
                }
                if (!p.isdie) {
                    // auto +hp,mp
                    int percent = p.body.total_skill_param(29) + p.body.total_item_param(29);
                    if (p.time_buff_hp < System.currentTimeMillis()) {
                        p.time_buff_hp = System.currentTimeMillis() + 5000L;
                        if (percent > 0 && p.hp < p.body.get_max_hp()) {
                            long param = (((long) p.body.get_max_hp()) * (percent / 100)) / 100;
                            Service.usepotion(p, 0, param);
                        }
                    }
                    percent = p.body.total_skill_param(30) + p.body.total_item_param(30);
                    if (p.time_buff_mp < System.currentTimeMillis()) {
                        p.time_buff_mp = System.currentTimeMillis() + 5000L;
                        if (percent > 0 && p.mp < p.body.get_max_mp()) {
                            long param = (((long) p.body.get_max_mp()) * (percent / 100)) / 100;
                            Service.usepotion(p, 1, param);
                        }
                    }
                    // eff medal
                    Item3 it = p.item.wear[12];
                    if (it != null && p.time_eff_medal < System.currentTimeMillis()) {
                        p.time_eff_medal = System.currentTimeMillis() + 2_000L;
                        Message m = new Message(-49);
                        m.writer().writeByte(2);
                        m.writer().writeShort(0);
                        m.writer().writeByte(0);
                        m.writer().writeByte(0);
                        switch (it.id) {
                            case 4588: {
                                switch (it.tier) {
                                    case 3:
                                    case 4:
                                    case 5: {
                                        m.writer().writeByte(0);
                                        break;
                                    }
                                    case 6:
                                    case 7:
                                    case 8: {
                                        m.writer().writeByte(1);
                                        break;
                                    }
                                    case 9:
                                    case 10:
                                    case 11: {
                                        m.writer().writeByte(2);
                                        break;
                                    }
                                    case 12:
                                    case 13:
                                    case 14: {
                                        m.writer().writeByte(25);
                                        break;
                                    }
                                    case 15: {
                                        m.writer().writeByte(26);
                                        break;
                                    }
                                }

                                break;
                            }
                            case 4589: {
                                switch (it.tier) {
                                    case 3:
                                    case 4:
                                    case 5: {
                                        m.writer().writeByte(9);
                                        break;
                                    }
                                    case 6:
                                    case 7:
                                    case 8: {
                                        m.writer().writeByte(10);
                                        break;
                                    }
                                    case 9:
                                    case 10:
                                    case 11: {
                                        m.writer().writeByte(11);
                                        break;
                                    }
                                    case 12:
                                    case 13:
                                    case 14: {
                                        m.writer().writeByte(27);
                                        break;
                                    }
                                    case 15: {
                                        m.writer().writeByte(28);
                                        break;
                                    }
                                }

                                break;
                            }
                            case 4590: {
                                switch (it.tier) {
                                    case 3:
                                    case 4:
                                    case 5: {
                                        m.writer().writeByte(6);
                                        break;
                                    }
                                    case 6:
                                    case 7:
                                    case 8: {
                                        m.writer().writeByte(7);
                                        break;
                                    }
                                    case 9:
                                    case 10:
                                    case 11: {
                                        m.writer().writeByte(8);
                                        break;
                                    }
                                    case 12:
                                    case 13:
                                    case 14: {
                                        m.writer().writeByte(31);
                                        break;
                                    }
                                    case 15: {
                                        m.writer().writeByte(32);
                                        break;
                                    }
                                }

                                break;
                            }
                            default: { // 4587
                                switch (it.tier) {
                                    case 3:
                                    case 4:
                                    case 5: {
                                        m.writer().writeByte(3);
                                        break;
                                    }
                                    case 6:
                                    case 7:
                                    case 8: {
                                        m.writer().writeByte(4);
                                        break;
                                    }
                                    case 9:
                                    case 10:
                                    case 11: {
                                        m.writer().writeByte(5);
                                        break;
                                    }
                                    case 12:
                                    case 13:
                                    case 14: {
                                        m.writer().writeByte(29);
                                        break;
                                    }
                                    case 15: {
                                        m.writer().writeByte(30);
                                        break;
                                    }
                                }
                                break;
                            }
                        }
                        m.writer().writeShort(p.id);
                        m.writer().writeByte(0);
                        m.writer().writeByte(0);
                        m.writer().writeInt(1000);
                        MapService.send_msg_player_inside(this, p, m, true);
                        m.cleanup();
                    }

                }
                // eff tu tiên
                if (p.tutien[0] >= 6 && p.time_eff_tutien < System.currentTimeMillis()) {
                    p.time_eff_tutien = System.currentTimeMillis() + 2_000L;
                    Message m = new Message(-49);
                    m.writer().writeByte(2);
                    m.writer().writeShort(0);
                    m.writer().writeByte(0);
                    m.writer().writeByte(0);
                    if (p.tutien[0] <= 7) {
                        m.writer().writeByte(19); //
                    } else if (p.tutien[0] <= 8) {
                        m.writer().writeByte(21);

                    } else if (p.tutien[0] <= 9) {
                        m.writer().writeByte(46);//46

                    } else if (p.tutien[0] <= 10) {
                        m.writer().writeByte(39);//43

                    } else { // 6
                        m.writer().writeByte(19);
                    }
                    
                    m.writer().writeShort(p.id);
                    m.writer().writeByte(0);
                    m.writer().writeByte(0);
                    m.writer().writeInt(1000);
                    MapService.send_msg_player_inside(this, p, m, true);
                    m.cleanup();
                }
                // process eff
                for (int i = 0; i < p.list_eff.size(); i++) {
                    EffTemplate temp = p.list_eff.get(i);
                    // remove
                    if (temp.time <= System.currentTimeMillis()) {
                        synchronized (p.list_eff) {
                            p.list_eff.remove(temp);
                            i--;
                        }
                        if (temp.id == -125) {
                            p.set_x2_xp(0);
                        }
                        if (temp.id == 24 || temp.id == 23 || temp.id == 0 || temp.id == 2 || temp.id == 3 || temp.id == 4) {
                            if (temp.id == 2) {
                                p.hp += p.hp_restore;
                            }
                            Service.send_char_main_in4(p);
                            for (int j = 0; j < players.size(); j++) {
                                Player p2 = players.get(j);
                                if (p2 != null && p2.id != p.id) {
                                    MapService.send_in4_other_char(this, p2, p);
                                }
                            }
                        }
                    }
                    //
                    if (temp.id == 1 && !p.isdie) {
                        if (p.time_affect_special_sk < System.currentTimeMillis() && p.dame_affect_special_sk > 0) {
                            p.time_affect_special_sk = System.currentTimeMillis() + 1000L;
                            p.hp -= p.dame_affect_special_sk;
                            Service.usepotion(p, 0, -p.dame_affect_special_sk);
                            if (p.hp < 0) {
                                MapService.die_by_player(this, p, p);
                                MapService.change_flag(this, p, -1);
                            }
                        }
                    }
                    // System.out.println(temp.id + " : " + (temp.time - System.currentTimeMillis()));
                }
            }
            // mob
            for (Mob_in_map mob : this.mobs) {

                if ((mob.is_boss && !mob.is_boss_active && mob.time_back < System.currentTimeMillis())
                        || (mob.isdie && mob.template.mob_id >= 89 && mob.template.mob_id <= 92
                        && mob.time_back < System.currentTimeMillis())) {
                    mob.time_back = System.currentTimeMillis() + (Mob_in_map.time_refresh * 1000) - 1000L;
                    Message m2 = new Message(17);
                    m2.writer().writeShort(-1);
                    m2.writer().writeShort(mob.index);
                    for (int i = 0; i < this.players.size(); i++) {
                        Player p0 = this.players.get(i);
                        p0.conn.addmsg(m2);
                    }
                    m2.cleanup();
                }

                if (mob.isdie && ((mob.is_boss && mob.is_boss_active) || !mob.is_boss)
                        && mob.time_back < System.currentTimeMillis()) {
                    if (mob.is_boss) {
                        Manager.gI().chatKTGprocess("@Server : " + mob.template.name + " xuất hiện.");
                        System.out.println("refresh boss : " + mob.template.name + " : map : " + this.name + " : khu : "
                                + (this.zone_id + 1));
                    }
                    mob.isdie = false;
                    mob.list_fight.clear();
                    mob.hp = mob.hpmax;
                    if (mob.is_boss) {
                        mob.color_name = 3;
                    } else if (5 > Util.random(200) && num_mob_super < 2 && mob.level > 50) {
                        mob.color_name = (new byte[]{1, 2, 4, 5})[Util.random(4)];
                        num_mob_super++;
                    } else {
                        mob.color_name = 0;
                    }
                    for (int j = 0; j < players.size(); j++) {
                        Player pp = players.get(j);
                        if ((Math.abs(pp.x - mob.x) < 200) && (Math.abs(pp.y - mob.y) < 200)) {
                            if (!pp.other_mob_inside.containsKey(mob.index)) {
                                pp.other_mob_inside.put(mob.index, true);
                            }
                            if (pp.other_mob_inside.get(mob.index)) {
                                Message mm = new Message(4);
                                mm.writer().writeByte(1);
                                mm.writer().writeShort(mob.template.mob_id);
                                mm.writer().writeShort(mob.index);
                                mm.writer().writeShort(mob.x);
                                mm.writer().writeShort(mob.y);
                                mm.writer().writeByte(-1);
                                pp.conn.addmsg(mm);
                                mm.cleanup();
                                pp.other_mob_inside.replace(mob.index, true, false);
                            } else {
                                Service.mob_in4(pp, mob.index);
                            }
                        }
                    }
                }
                // mob fire
                if (!mob.isdie && mob.list_fight.size() > 0) {
                    Player p0 = mob.list_fight.get(Util.random(mob.list_fight.size()));
                    if (p0 != null && !p0.isdie && p0.map.map_id == this.map_id && p0.map.zone_id == this.zone_id) {
                        if (Math.abs(mob.x - p0.x) < 200 && Math.abs(mob.y - p0.y) < 200) {
                            if (mob.time_fight < System.currentTimeMillis()) {
                                mob.time_fight = System.currentTimeMillis() + 1200L;
                                // if (mob.is_boss) {
                                // mob.time_fight -= 600L;
                                // }
                                MapService.mob_fire(this, mob, p0);
                            }
                        } else {
                            mob.list_fight.remove(p0);
                            //
                            Message m = new Message(10);
                            m.writer().writeByte(0);
                            m.writer().writeShort(mob.index);
                            MapService.send_msg_player_inside(this, p0, m, true);
                            m.cleanup();
                        }
                    }
                    if (p0.isdie) {
                        mob.list_fight.remove(p0);
                        //
                        Message m = new Message(10);
                        m.writer().writeByte(0);
                        m.writer().writeShort(mob.index);
                        MapService.send_msg_player_inside(this, p0, m, true);
                        m.cleanup();
                    }
                    if (mob.list_fight.contains(p0) && !(p0.map.map_id == this.map_id && p0.map.zone_id == this.zone_id)) {
                        mob.list_fight.remove(p0);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        // update item map
        for (int i = 0; i < this.item_map.length; i++) {
            if (this.item_map[i] != null && this.item_map[i].idmaster != -1
                    && ((this.item_map[i].time_exist - System.currentTimeMillis()) < 15000L)) {
                this.item_map[i].idmaster = -1;
            }
            if (this.item_map[i] != null && this.item_map[i].time_exist < System.currentTimeMillis()) {
                this.item_map[i] = null;
            }
        }
    }

    public void start_map() {
        this.mapthread.start();
    }

    public void stop_map() {
        this.running = false;
        this.mapthread.interrupt();
    }

    public static Player get_player_by_name(String name) {
        for (Map[] maps : entrys) {
            for (Map map : maps) {
                for (Player p0 : map.players) {
                    if (p0.name.equals(name)) {
                        return p0;
                    }

                }
            }
        }
        return null;
    }

    public static Map[] get_map_by_id(int id) {
        for (Map[] temp : entrys) {
            if (temp[0].map_id == id) {
                return temp;
            }
        }
        return null;
    }

    public void send_map_data(Player p) throws IOException {
        Message m = new Message(12);
        m.writer().writeShort(this.map_id);
        m.writer().writeShort((short) (p.x / 24));
        m.writer().writeShort((short) (p.y / 24));
        m.writer().write(this.map_data);
        m.writer().writeByte(this.zone_id); // zone
        m.writer().writeByte(this.typemap);
        m.writer().writeBoolean(this.ismaplang);
        m.writer().writeBoolean(this.showhs);
        p.conn.addmsg(m);
        m.cleanup();
        // send npc;
        String path = "data/npc/";
        if (Manager.gI().event == 1 && this.map_id == 1) {
            path = "data/npc/event" + Manager.gI().event + "/";
            Service.send_msg_data(p.conn, -49, "event1_1");
        } else if (Manager.gI().event == 2) {
            switch (this.map_id) {
                case 7:
                case 24:
                case 72:
                case 96: {
                    Service.send_msg_data(p.conn, -50, ("vuahung_ev2_" + this.map_id));
                    break;
                }
            }
        }
        if (p.list_npc.size() > 0) {
            p.list_npc.clear();
        }
        for (int i = 0; i < this.npc_name_data.length; i++) {
            m = new Message(-50);
            byte[] npc_data_bytes = Util.loadfile(path + this.npc_name_data[i]);
            m.writer().write(npc_data_bytes);
            p.conn.addmsg(m);
            m.cleanup();
            //
            ByteArrayInputStream bais = null;
            DataInputStream dis = null;
            try {
                bais = new ByteArrayInputStream(npc_data_bytes);
                dis = new DataInputStream(bais);
                int size = dis.readByte();
                for (int j = 0; j < size; j++) {
                    dis.readUTF();
                    dis.readUTF();
                    NpcTemplate temp = new NpcTemplate();
                    temp.id = dis.readByte();
                    dis.readByte();
                    temp.x = dis.readShort();
                    temp.y = dis.readShort();
                    dis.readByte();
                    dis.readByte();
                    dis.readByte();
                    dis.readByte();
                    dis.readUTF();
                    dis.readByte();
                    dis.readByte();
                    p.list_npc.add(temp);
                }
            } catch (IOException e) {
            } finally {
                try {
                    if (dis != null) {
                        dis.close();
                    }
                } catch (IOException e) {
                }
                try {
                    if (bais != null) {
                        bais.close();
                    }
                } catch (IOException e) {
                }
            }
        }
        // mob mo tai nguyen
        if (Map.is_map_chiem_mo(p.map, true)) {
            Mob_MoTaiNguyen mob_tainguyen = Manager.gI().chiem_mo.get_mob_in_map(p.map);
            m = new Message(4);
            m.writer().writeByte(1);
            m.writer().writeShort(64);
            m.writer().writeShort(mob_tainguyen.index);
            m.writer().writeShort(mob_tainguyen.x);
            m.writer().writeShort(mob_tainguyen.y);
            m.writer().writeByte(-1);
            if (mob_tainguyen.nhanban != null) {
                m.writer().writeByte(0);
                m.writer().writeShort(0);
                m.writer().writeShort(mob_tainguyen.nhanban.id_p);
                m.writer().writeShort(mob_tainguyen.nhanban.x);
                m.writer().writeShort(mob_tainguyen.nhanban.y);
                m.writer().writeByte(-1);
            }
            p.conn.addmsg(m);
            m.cleanup();
        }
        if (this.map_id == 52 && this.zone_id == this.maxzone) {
            m = new Message(-50);
            m.writer().writeByte(1);
            m.writer().writeUTF("Mr Dylan");
            m.writer().writeUTF("Mua bán");
            m.writer().writeByte(-57);
            m.writer().writeByte(34);
            m.writer().writeShort(384);
            m.writer().writeShort(432);
            m.writer().writeByte(1);
            m.writer().writeByte(1);
            m.writer().writeByte(2);
            m.writer().writeByte(26);
            m.writer().writeUTF("Ta chuyên bán các loại hàng hóa phục vụ cho việc đi buôn. Hân hạnh phục vụ quý khách");
            m.writer().writeByte(1);
            m.writer().writeByte(0);
            p.conn.addmsg(m);
            m.cleanup();
            //
            NpcTemplate temp = new NpcTemplate();
            temp.id = -57;
            temp.x = 384;
            temp.y = 432;
            p.list_npc.add(temp);
        }
        // monument
        if (this.map_id == 1) {
            NpcTemplate temp = new NpcTemplate();
            temp.id = -49;
            temp.x = 288;
            temp.y = 312;
            p.list_npc.add(temp);
            //
            m = new Message(-96);
            m.writer().writeShort(288);
            m.writer().writeShort(312);
            m.writer().writeShort(264);
            m.writer().writeShort(288);
            m.writer().writeByte(3);
            m.writer().writeByte(1);
            m.writer().writeByte(-1);
            m.writer().writeByte(-25);
            m.writer().writeByte(1);
            m.writer().writeUTF("TOP Hiếu Chiến");
            m.writer().writeUTF(Map.name_mo);
            m.writer().writeByte(-49);
            m.writer().writeByte(15);
            //
            m.writer().writeShort(Map.weapon); // weapon
            m.writer().writeShort(Map.body); // body
            m.writer().writeShort(-1);
            m.writer().writeShort(-1);
            m.writer().writeShort(-1);
            m.writer().writeShort(3); // pet
            m.writer().writeShort(Map.hat); // hat
            m.writer().writeShort(Map.leg); // leg
            m.writer().writeShort(-1);
            m.writer().writeShort(-1);
            m.writer().writeShort(Map.wing); // wing
            m.writer().writeShort(-1);
            m.writer().writeShort(Map.head); // head
            m.writer().writeShort(Map.eye); // eye
            m.writer().writeShort(Map.hair); // hair
            //
            m.writer().write(Util.loadfile("data/msg/msg_-96_x" + p.conn.zoomlv));
            p.conn.addmsg(m);
            m.cleanup();
        } else if (this.map_id == 50) { // map pet
            m = new Message(44);
            m.writer().writeByte(28);
            m.writer().writeByte(0);
            m.writer().writeByte(3);
            m.writer().writeByte(3);
            int dem = 0;
            long now_time = System.currentTimeMillis();
            for (Pet temp2 : p.mypet) {
                if (temp2.is_hatch && temp2.time_born > now_time) {
                    dem++;
                }
            }
            m.writer().writeByte(dem);
            for (Pet temp2 : p.mypet) {
                if (temp2.is_hatch && temp2.time_born > now_time) {
                    int id_ = Pet.get_id(temp2.type);
                    m.writer().writeUTF(ItemTemplate3.item.get(id_).getName());
                    m.writer().writeByte(4); // clazz
                    m.writer().writeShort(id_);
                    m.writer().writeByte(14); // type
                    m.writer().writeShort(ItemTemplate3.item.get(id_).getIcon());
                    m.writer().writeByte(0); // tier
                    m.writer().writeShort(10); // level
                    m.writer().writeByte(0); // color
                    m.writer().writeByte(1);
                    m.writer().writeByte(1);
                    m.writer().writeByte(0); // op size
                    long time2 = ((temp2.time_born - now_time) / 60000) + 1;
                    m.writer().writeInt((int) time2);
                    m.writer().writeByte(0);
                }
            }
            p.conn.addmsg(m);
            m.cleanup();
            //
            m = new Message(44);
            m.writer().writeByte(28);
            m.writer().writeByte(0);
            m.writer().writeByte(9);
            m.writer().writeByte(9);
            m.writer().writeByte(0);
            p.conn.addmsg(m);
            m.cleanup();
            //
            m = new Message(44);
            m.writer().writeByte(28);
            m.writer().writeByte(0);
            m.writer().writeByte(9);
            m.writer().writeByte(9);
            dem = 0;
            for (Pet temp : p.mypet) {
                if (!temp.is_follow && !temp.is_hatch) {
                    dem++;
                }
            }
            m.writer().writeByte(dem); // size pet
            //
            for (int i = 0; i < p.mypet.size(); i++) {
                if (!p.mypet.get(i).is_follow && !p.mypet.get(i).is_hatch) {
                    m.writer().writeUTF(p.mypet.get(i).name);
                    m.writer().writeByte(p.mypet.get(i).type);
                    m.writer().writeShort(i); // id
                    m.writer().writeShort(p.mypet.get(i).level);
                    m.writer().writeShort(p.mypet.get(i).getlevelpercent()); // exp
                    m.writer().writeByte(p.mypet.get(i).type);
                    m.writer().writeByte(p.mypet.get(i).icon);
                    m.writer().writeByte(p.mypet.get(i).nframe);
                    m.writer().writeByte(p.mypet.get(i).color);
                    m.writer().writeInt(p.mypet.get(i).get_age());
                    m.writer().writeShort(p.mypet.get(i).grown);
                    m.writer().writeShort(p.mypet.get(i).maxgrown);
                    m.writer().writeShort(p.mypet.get(i).point1);
                    m.writer().writeShort(p.mypet.get(i).point2);
                    m.writer().writeShort(p.mypet.get(i).point3);
                    m.writer().writeShort(p.mypet.get(i).point4);
                    m.writer().writeShort(p.mypet.get(i).maxpoint);
                    m.writer().writeByte(p.mypet.get(i).op.size());
                    for (int i2 = 0; i2 < p.mypet.get(i).op.size(); i2++) {
                        Option_pet temp = p.mypet.get(i).op.get(i2);
                        m.writer().writeByte(temp.id);
                        m.writer().writeInt(temp.param);
                        m.writer().writeInt(temp.maxdam);
                    }
                }
            }
            p.conn.addmsg(m);
            m.cleanup();
        }
        if (Manager.gI().event == 2) {
            if (p.minuong != null && p.minuong.map == null) {
                p.minuong.map = p.map;
                p.minuong.mob.x = p.x;
                p.minuong.mob.y = p.y;
                // Message m4 = new Message(4);
                // m4.writer().writeByte(1);
                // m4.writer().writeShort(BossEvent.MiNuong.get(i).mob.template.mob_id);
                // m4.writer().writeShort(BossEvent.MiNuong.get(i).mob.index);
                // m4.writer().writeShort(BossEvent.MiNuong.get(i).mob.x);
                // m4.writer().writeShort(BossEvent.MiNuong.get(i).mob.y);
                // m4.writer().writeByte(-1);
                // p.conn.addmsg(m4);
                // // m4.cleanup();
            }
            for (int i = 0; i < BossEvent.MiNuong.size(); i++) {
                if (BossEvent.MiNuong.get(i).map != null && BossEvent.MiNuong.get(i).map.equals(p.map)) {
                    Message m4 = new Message(4);
                    m4.writer().writeByte(1);
                    m4.writer().writeShort(BossEvent.MiNuong.get(i).mob.template.mob_id);
                    m4.writer().writeShort(BossEvent.MiNuong.get(i).mob.index);
                    m4.writer().writeShort(BossEvent.MiNuong.get(i).mob.x);
                    m4.writer().writeShort(BossEvent.MiNuong.get(i).mob.y);
                    m4.writer().writeByte(-1);
                    p.conn.addmsg(m4);
                    m4.cleanup();
                    break;
                }
                if (BossEvent.MiNuong.get(i).map == null) {
                    BossEvent.MiNuong.remove(i);
                    i--;
                }
            }
        }
    }

    public static Map get_map_dungeon(int id) {
        for (Map[] temp : entrys) {
            if (temp[0].map_id == id) {
                return temp[0];
            }
        }
        return null;
    }

    public synchronized void drop_item(Player p, byte type, short id) throws IOException {
        switch (type) {
            case 3: {
                Item3 temp = p.item.bag3[id];
                if (temp != null) {
                    if (temp.islock) {
                        Service.send_notice_box(p.conn, "Vật phẩm đã khóa");
                        return;
                    }
                    p.item.bag3[id] = null;
                }
                break;
            }
            case 4: {
            }
            case 7: {
                p.item.remove(type, id, p.item.total_item_by_id(type, id));
                break;
            }
        }
        p.item.char_inventory(4);
        p.item.char_inventory(7);
        p.item.char_inventory(3);
    }

    public void send_mount(Player p) throws IOException {
        Message m = new Message(-97);
        m.writer().writeByte(0);
        m.writer().writeByte(p.type_use_mount);
        m.writer().writeShort(p.id);
        MapService.send_msg_player_inside(this, p, m, true);
        m.cleanup();
        Service.send_char_main_in4(p);
    }

    public synchronized void pick_item(Session conn, Message m2) throws IOException {
        short id = m2.reader().readShort();
        byte type = m2.reader().readByte();
        if (item_map[id] == null) {
            Message m = new Message(20);
            m.writer().writeByte(type);
            m.writer().writeShort(id);
            m.writer().writeShort(conn.p.id);
            MapService.send_msg_player_inside(this, conn.p, m, true);
            m.cleanup();
            item_map[id] = null;
            return;
        }
        if (type == 3 && item_map[id] != null
                && (item_map[id].id_item == 3590 || item_map[id].id_item == 3591 || item_map[id].id_item == 3592)) {
            if (item_map[id].idmaster != -1 && conn.p.id != item_map[id].idmaster) {
                Service.send_notice_nobox_white(conn, "Vật phẩm của người khác");
                return;
            }
            if (conn.p.pet_di_buon != null && conn.p.pet_di_buon.item.size() < 12) {
                conn.p.pet_di_buon.item.add(item_map[id].id_item);
                //
                Message m = new Message(20);
                m.writer().writeByte(type);
                m.writer().writeShort(id);
                m.writer().writeShort(conn.p.id);
                MapService.send_msg_player_inside(this, conn.p, m, true);
                m.cleanup();
                item_map[id] = null;
                //
            } else {
                Service.send_notice_nobox_white(conn, "Không thể nhặt!");
            }
            return;
        }
        type = item_map[id].category;
        if (conn.p.isdie || conn.p.in4_auto[3] == 0
                || (conn.p.in4_auto[4] == -1 && conn.p.in4_auto[5] == 1 && conn.p.in4_auto[6] == 3)) {
            return;
        }
        if (item_map[id].idmaster != -1 && conn.p.id != item_map[id].idmaster) {
            Service.send_notice_nobox_white(conn, "Vật phẩm của người khác");
            return;
        }
        if (item_map[id] != null && (item_map[id].idmaster == -1 || conn.p.id == item_map[id].idmaster)
                && (item_map[id].time_pick < System.currentTimeMillis())) {
            if (type == 4 && item_map[id].id_item == -1) { // vang
                if (conn.p.in4_auto[5] == 0) {
                    conn.p.update_vang(item_map[id].quantity);
                    conn.p.item.char_inventory(5);
                    Message m = new Message(20);
                    m.writer().writeByte(type);
                    m.writer().writeShort(id);
                    m.writer().writeShort(conn.p.id);
                    MapService.send_msg_player_inside(this, conn.p, m, true);
                    m.cleanup();
                    item_map[id] = null;

                }
            } else if (item_map[id].id_item != -1) {
                if (conn.p.item.get_bag_able() > 0
                        || ((type == 4 || type == 7) && (conn.p.item.total_item_by_id(type, item_map[id].id_item) > 0))) {
                    switch (type) {
                        case 3: {
                            if (item_map[id].id_item < ItemTemplate3.item.size()) {
                                Short idadd = item_map[id].id_item;
                                Item3 itbag = new Item3();
                                itbag.id = idadd;
                                itbag.name = ItemTemplate3.item.get(idadd).getName();
                                itbag.clazz = ItemTemplate3.item.get(idadd).getClazz();
                                itbag.type = ItemTemplate3.item.get(idadd).getType();
                                itbag.level = ItemTemplate3.item.get(idadd).getLevel();
                                itbag.icon = ItemTemplate3.item.get(idadd).getIcon();
                                itbag.op = new ArrayList<>();
                                itbag.op.addAll(item_map[id].op);
                                itbag.color = ItemTemplate3.item.get(idadd).getColor();
                                itbag.part = ItemTemplate3.item.get(idadd).getPart();
                                itbag.tier = 0;
                                itbag.islock = false;
                                itbag.time_use = 0;
                                if (itbag.color == 3) {
									if (conn.p.nv_tinh_tu[0] == 21 && conn.p.nv_tinh_tu[1] < conn.p.nv_tinh_tu[2]) {
										conn.p.nv_tinh_tu[1]++;
									}
								}
								if (itbag.color == 4) {
									if (conn.p.nv_tinh_tu[0] == 22 && conn.p.nv_tinh_tu[1] < conn.p.nv_tinh_tu[2]) {
										conn.p.nv_tinh_tu[1]++;
									}
								}
                                if (conn.p.in4_auto[4] > itbag.color) {
                                    return;
                                }
                                conn.p.item.add_item_bag3(itbag);
                                conn.p.item.char_inventory(3);
                            }
                            break;
                        }
                        case 4: {
                            if (item_map[id].id_item < ItemTemplate4.item.size()) {
                                Short idadd = item_map[id].id_item;
                                if (ItemTemplate4.item.get(idadd).getType() == 1 && conn.p.in4_auto[6] == 1) {
                                    return;
                                } else if (ItemTemplate4.item.get(idadd).getType() == 0 && conn.p.in4_auto[6] == 2) {
                                    return;
                                }
                                Item47 itbag = new Item47();
                                itbag.id = idadd;
                                itbag.quantity = (short) item_map[id].quantity;
                                itbag.category = 4;
                                conn.p.item.add_item_bag47(4, itbag);
                                conn.p.item.char_inventory(4);
                            }
                            break;
                        }
                        case 7: {
                            if (item_map[id].id_item < ItemTemplate7.item.size()) {
                                Short idadd = item_map[id].id_item;
                                Item47 itbag = new Item47();
                                itbag.id = idadd;
                                itbag.quantity = (short) item_map[id].quantity;
                                itbag.category = 7;
                                conn.p.item.add_item_bag47(7, itbag);
                                conn.p.item.char_inventory(7);
                                if (idadd == 397) {
									if (conn.p.nv_tinh_tu[0] == 1 && conn.p.nv_tinh_tu[1] < conn.p.nv_tinh_tu[2]) {
										conn.p.nv_tinh_tu[1]++;
									}
								}
                            }
                            break;
                        }
                    }
                    Message m = new Message(20);
                    m.writer().writeByte(type);
                    m.writer().writeShort(id);
                    m.writer().writeShort(conn.p.id);
                    MapService.send_msg_player_inside(this, conn.p, m, true);
                    m.cleanup();
                    item_map[id] = null;
                }
            }
        }
    }

    public int get_item_map_index_able() {
        for (int i = 0; i < item_map.length; i++) {
            if (item_map[i] == null) {
                return i;
            }
        }
        return -1;
    }

    public void create_party(Session conn, Message m2) throws IOException {
        byte type = m2.reader().readByte();
        String name = "";
        Player p0 = null;
        if (type != 0 && type != 5 && type != 4) {
            name = m2.reader().readUTF();
            p0 = Map.get_player_by_name(name);
        }
        switch (type) {
            case 1: { // request party other
                if (p0 == null) {
                    Service.send_notice_box(conn, "Có lỗi xảy ra, hãy thử lại");
                    return;
                }
                if (p0.party != null) {
                    if (conn.p.party != null && conn.p.party.get_mems().contains(p0)) {
                        Service.send_notice_box(conn, "Đối phương đã ở trong đội");
                    } else {
                        Service.send_notice_box(conn, "Đối phương đang trong đội khác");
                    }
                    return;
                }
                if (conn.p.party != null) {
                    if (conn.p.party.get_mems().get(0).id != conn.p.id) {
                        Service.send_notice_box(conn, "Bạn éo phải đội trưởng, đừng có ra dẻ!!!");
                        return;
                    }
                    if (conn.p.party.get_mems().size() > 4) {
                        Service.send_notice_box(conn, "không thể rủ rê thêm thành viên");
                        return;
                    }
                }
                if (conn.p.party == null) {
    conn.p.party = new Party();
    conn.p.party.setLeader(conn.p); // Gán người tạo nhóm làm đội trưởng
    conn.p.party.add_mems(conn.p);
    conn.p.party.sendin4();
}
                //
                Message m = new Message(48);
                m.writer().writeByte(type);
                m.writer().writeUTF(conn.p.name);
                p0.conn.addmsg(m);
                m.cleanup();
                break;
            }
            case 2: { // accept
                if (conn.p.party != null) {
                    Service.send_notice_box(conn, "Bạn đã ở trong nhóm");
                    return;
                }
                if (p0 == null || (p0 != null && p0.party == null)) {
                    Service.send_notice_box(conn, "Nhóm không còn tồn tại");
                    return;
                }
                if (p0.party.get_mems().size() > 4) {
                    Service.send_notice_box(conn, "Nhóm đầy");
                    return;
                } else {
                    conn.p.party = p0.party;
                    p0.party.add_mems(conn.p);
                    p0.party.sendin4();
                    p0.party.send_txt_notice(conn.p.name + " vào nhóm");
                }
                break;
            }
            case 3: { // kick
                if (conn.p.party == null) {
                    Service.send_notice_box(conn, "Nhóm không tồn tại");
                    return;
                }
                Player p01 = null;
                for (int i = 0; i < conn.p.party.get_mems().size(); i++) {
                    if (conn.p.party.get_mems().get(i).name.equals(name)) {
                        p01 = conn.p.party.get_mems().get(i);
                        break;
                    }
                }
                if (p01 == null || name.equals("")) {
                    Service.send_notice_box(conn, "Có lỗi xảy ra, hãy thử lại");
                }
                p01.party.remove_mems(p01);
                p01.party.sendin4();
                p01.party = null;
                conn.p.party.send_txt_notice(p01.name + " đã bị đá khỏi đội");
                Service.send_notice_nobox_white(p01.conn, "Bạn đã bị đá khỏi đội ehehe");
                Message m22 = new Message(48);
                m22.writer().writeByte(5);
                p01.conn.addmsg(m22);
                m22.cleanup();
                break;
            }
            case 4: { // giai tan
                Message m = new Message(48);
                m.writer().writeByte(4);
                for (int i = 1; i < conn.p.party.get_mems().size(); i++) {
                    Player p02 = conn.p.party.get_mems().get(i);
                    p02.conn.addmsg(m);
                    p02.party = null;
                }
                conn.addmsg(m);
                conn.p.party.get_mems().clear();
                conn.p.party = null;
                m.cleanup();
                break;
            }
            case 5: { // leave
                if (conn.p.party.get_mems().get(0).id == conn.p.id) {
                    Service.send_notice_box(conn, "Là đội trưởng thì phải ra dáng, không đc bỏ nhóm!");
                    return;
                }
                conn.p.party.remove_mems(conn.p);
                conn.p.party.sendin4();
                conn.p.party.send_txt_notice(conn.p.name + " rời nhóm");
                conn.p.party = null;
                //
                Message m = new Message(48);
                m.writer().writeByte(5);
                conn.addmsg(m);
                m.cleanup();
                break;
            }
            default: {
                Service.send_notice_box(conn, "Chưa có chức năng");
                break;
            }
        }
    }

    public static Player get_player_by_id(int id_player_login) {
        for (Map[] maps : entrys) {
            for (Map map : maps) {
                for (Player p0 : map.players) {
                    if (p0.id == id_player_login) {
                        return p0;
                    }
                }

            }
        }
        return null;
    }

    public static boolean is_map_cant_save_site(byte id) {
        return id == 48 || id == 88 || id == 89 || id == 90 || id == 91 || id == 82 || id == 102
                || Map.is_map_chien_truong(id);
    }

    public synchronized void add_item_map_leave(Map map, Player p_master, ItemMap temp, int mob_index)
            throws IOException {
        for (int i = 0; i < item_map.length; i++) {
            if (item_map[i] == null) {
                item_map[i] = temp;
                Message mi = new Message(19);
                mi.writer().writeByte(temp.category);
                mi.writer().writeShort(mob_index); // index mob die
                switch (temp.category) {
                    case 3: {
                        mi.writer().writeShort(ItemTemplate3.item.get(temp.id_item).getIcon());
                        mi.writer().writeShort(i); //
                        mi.writer().writeUTF(ItemTemplate3.item.get(temp.id_item).getName());
                        break;
                    }
                    case 4: {
                        mi.writer().writeShort(ItemTemplate4.item.get(temp.id_item).getIcon());
                        mi.writer().writeShort(i); //
                        mi.writer().writeUTF(ItemTemplate4.item.get(temp.id_item).getName());
                        break;
                    }
                    case 7: {
                        mi.writer().writeShort(ItemTemplate7.item.get(temp.id_item).getIcon());
                        mi.writer().writeShort(i); //
                        mi.writer().writeUTF(ItemTemplate7.item.get(temp.id_item).getName());
                        break;
                    }
                }
                mi.writer().writeByte(0); // color
                mi.writer().writeShort(-1); // id player
                MapService.send_msg_player_inside(map, p_master, mi, true);
                mi.cleanup();
                break;
            }
        }
    }

    public static boolean is_map_chiem_mo(Map map, boolean is_zone) {
        boolean is_map = false;
        int[] map_ = new int[]{3, 5, 8, 9, 11, 12, 15, 16, 19, 21, 22, 24, 26, 27, 37, 42};
        for (int i = 0; i < map_.length; i++) {
            if (map_[i] == map.map_id) {
                is_map = true;
                break;
            }
        }
        return (is_zone) ? (map.zone_id == 4 && is_map) : is_map;
    }

    public static boolean is_map__load_board_player(byte id) {
        return id == 102;
    }

    public static boolean is_map_chien_truong(byte id) {
        return id >= 53 && id <= 61;
    }
    
    
}
