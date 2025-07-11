package event_daily;

import io.Message;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import map.Map;
import template.Mob_MoTaiNguyen;

public class ChiemMo {

    private boolean running;
    private List<Mob_MoTaiNguyen> list_mo_tai_nguyen;

    public boolean isRunning() {
        return running;
    }

    public void setRunning(boolean running) {
        this.running = running;
    }

    public List<Mob_MoTaiNguyen> getList_mo_tai_nguyen() {
        return list_mo_tai_nguyen;
    }

    public void setList_mo_tai_nguyen(List<Mob_MoTaiNguyen> list_mo_tai_nguyen) {
        this.list_mo_tai_nguyen = list_mo_tai_nguyen;
    }

    public void init() {
        this.running = false;
        this.list_mo_tai_nguyen = new ArrayList<>();
        int[] x_ = new int[]{444, 1068, 228, 804, 516, 684, 540, 612, 1020, 444, 228, 612, 540, 492, 492, 756};
        int[] y_ = new int[]{156, 348, 516, 972, 372, 588, 588, 204, 204, 108, 372, 708, 396, 612, 420, 300};
        int[] map_ = new int[]{3, 5, 8, 9, 11, 12, 15, 16, 19, 21, 22, 24, 26, 27, 37, 42};
        String[] name_ = new String[]{"Mỏ Vàng", "Mỏ Tri Thức", "Mỏ Ngọc", "Mỏ Tri Thức", "Mỏ Vàng", "Mỏ Vàng",
            "Mỏ Tri Thức", "Mỏ Vàng", "Mỏ Vàng", "Mỏ Ngọc", "Mỏ Tri Thức", "Mỏ Vàng", "Mỏ Tri Thức", "Mỏ Ngọc",
            "Mỏ Vàng", "Mỏ Ngọc"};
        for (int i = 0; i < x_.length; i++) {
            this.list_mo_tai_nguyen.add(new Mob_MoTaiNguyen((i - 19), x_[i], y_[i], 4_000_000, 4_000_000, 120,
                    Map.get_map_by_id(map_[i])[4], name_[i]));
        }
    }

    public Mob_MoTaiNguyen get_mob_in_map(Map map) {
        for (Mob_MoTaiNguyen mob_MoTaiNguyen : list_mo_tai_nguyen) {
            if (mob_MoTaiNguyen.map.equals(map)) {
                return mob_MoTaiNguyen;
            }
        }
        return null;
    }

    public void mo_open_atk() {
        for (Mob_MoTaiNguyen mob_MoTaiNguyen : list_mo_tai_nguyen) {
            mob_MoTaiNguyen.is_atk = true;
        }
    }

    public void mo_close_atk() throws IOException {
        for (Mob_MoTaiNguyen mob_MoTaiNguyen : list_mo_tai_nguyen) {
            mob_MoTaiNguyen.is_atk = false;
            mob_MoTaiNguyen.hp_max = 4_000_000;
            mob_MoTaiNguyen.hp = mob_MoTaiNguyen.hp_max;
            //
             Message m_hp = new Message(32);
                m_hp.writer().writeByte(1);
                m_hp.writer().writeShort(mob_MoTaiNguyen.index);
                m_hp.writer().writeShort(-1); // id potion in bag
                m_hp.writer().writeByte(0);
                m_hp.writer().writeInt(mob_MoTaiNguyen.hp_max); // max hp
                m_hp.writer().writeInt(mob_MoTaiNguyen.hp); // hp
                m_hp.writer().writeInt(0); // param use
                for (int i = 0; i <  mob_MoTaiNguyen.map.players.size(); i++) {
                    mob_MoTaiNguyen.map.players.get(i).conn.addmsg(m_hp);
                }
                m_hp.cleanup();
        }
        
    }

    public void harvest_all() {
        for (Mob_MoTaiNguyen mob_MoTaiNguyen : list_mo_tai_nguyen) {
            if (mob_MoTaiNguyen.clan != null) {
                switch (mob_MoTaiNguyen.name_monster) {
                    case "Mỏ Vàng": {
                        mob_MoTaiNguyen.clan.vang += 25_000;
                        break;
                    }
                    case "Mỏ Ngọc": {
                        mob_MoTaiNguyen.clan.kimcuong += 50;
                        break;
                    }
                    case "Mỏ Tri Thức": {
                        mob_MoTaiNguyen.clan.exp += 50_000;
                        break;
                    }
                }
            }
        }
    }
}
