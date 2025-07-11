package client;

import event_daily.TuTien;
import template.EffTemplate;
import template.Item3;
import template.Option;
import template.Option_pet;

public class Body {

    private final Player p;

    public Body(Player player) {
        this.p = player;
    }

    private int get_point(int i) {
        int point = 0;
        switch (i) {
            case 1: {
                point += p.point1 + get_plus_point(23);
                break;
            }
            case 2: {
                point += p.point2 + get_plus_point(24);
                break;
            }
            case 3: {
                point += p.point3 + get_plus_point(25);
                break;
            }
            case 4: {
                point += p.point4 + get_plus_point(26);
                break;
            }
        }
        return point;
    }

    public int get_plus_point(int i) {
        int param = 0;
        switch (i) {
            case 23: {
                param += total_item_param(i);
                EffTemplate ef = p.get_eff(23);
                if (ef != null) {
                    param += (p.point1 * (ef.param / 100)) / 100;
                }
                for (Pet temp : p.mypet) {
                    if (temp.is_follow) {
                        for (Option_pet op : temp.op) {
                            if (op.id == 23) {
                                param += op.param;
                                break;
                            }
                        }
                        break;
                    }
                }
                break;
            }
            case 24: {
                param += total_item_param(i);
                for (Pet temp : p.mypet) {
                    if (temp.is_follow) {
                        for (Option_pet op : temp.op) {
                            if (op.id == 24) {
                                param += op.param;
                                break;
                            }
                        }
                        break;
                    }
                }
                break;
            }
            case 25: {
                param += total_item_param(i);
                for (Pet temp : p.mypet) {
                    if (temp.is_follow) {
                        for (Option_pet op : temp.op) {
                            if (op.id == 25) {
                                param += op.param;
                                break;
                            }
                        }
                        break;
                    }
                }
                break;
            }
            case 26: {
                param += total_item_param(i);
                for (Pet temp : p.mypet) {
                    if (temp.is_follow) {
                        for (Option_pet op : temp.op) {
                            if (op.id == 26) {
                                param += op.param;
                                break;
                            }
                        }
                        break;
                    }
                }
                break;
            }
        }
        return param;
    }

    public int total_item_param(int id) {
        int param = 0;
        for (int i = 0; i < p.item.wear.length; i++) {
            Item3 temp = p.item.wear[i];
            if (temp != null) {
                for (Option op : temp.op) {
                    if (op.id == id) {
                        param += op.getParam(temp.tier);
                    }
                }
            }
        }

        for (int i = 0; i < p.tutien[0]; i++) {
            for (int j = 0; j < TuTien.op[i].length; j++) {
                if (TuTien.op[i][j] == id) {
                    param += TuTien.par[i][j];
                }

            }
        }

        return param;
    }

    public long get_max_hp() {
        long hpm = 2500;
        switch (p.clazz) {
            case 0: {
                hpm += 550 + get_point(3) * 320;
                break;
            }
            case 1: {
                hpm += get_point(3) * 300;
                break;
            }
            case 2: {
                hpm += 50 + get_point(3) * 310;
                break;
            }
            case 3: {
                hpm += 120 + get_point(3) * 300;
                break;
            }
        }
        hpm += (p.luyenthe * 2000);
        int percent = total_item_param(27);
        if (p.skill_point[9] > 0) {
            for (Option op : p.skills[9].mLvSkill[p.skill_point[9] - 1].minfo) {
                if (op.id == 27) {
                    percent += op.getParam(0);
                    break;
                }
            }
        }
        percent += ((p.kinhmach[0] - 1) * 5 + p.kinhmach[1]) * 100;
        hpm += ((hpm * (percent / 100)) / 100);
        if (p.type_use_mount == 11 || p.type_use_mount == 12 || p.type_use_mount == 13
                || (p.type_use_mount == 20 && (p.id_horse == 114 || p.id_horse == 121))) {
            hpm += (hpm / 10);
        }
        EffTemplate ef = p.get_eff(2);
        if (ef != null) {
            hpm = (hpm * 8) / 10;
        }
//        if (hpm > 2_000_000_000) {
//            hpm = 2_000_000_000;
//        }
        return (long) hpm;
    }

    public int get_max_mp() {
        long mpm = 250;
        switch (p.clazz) {
            case 0:
            case 1: {
                mpm += get_point(4) * 10;
                break;
            }
            case 2: {
                mpm += 10 + get_point(3) + get_point(4) * 11;
                break;
            }
            case 3: {
                mpm += 5 + get_point(3) + get_point(4) * 11;
                break;
            }
        }
        mpm += (p.luyenthe * 200);
        int percent = total_item_param(28);
        if (p.skill_point[10] > 0) {
            for (Option op : p.skills[10].mLvSkill[p.skill_point[10] - 1].minfo) {
                if (op.id == 28) {
                    percent += op.getParam(0);
                    break;
                }
            }
        }
        percent += ((p.kinhmach[0] - 1) * 5 + p.kinhmach[1]) * 100;
        mpm += ((mpm * (percent / 100)) / 100);
        EffTemplate ef = p.get_eff(2);
        if (ef != null) {
            mpm = (mpm * 8) / 10;
        }
        if (mpm > 2_000_000_000) {
            mpm = 2_000_000_000;
        }
        return (int) mpm;
    }

    public int get_param_view_in4(int type) {
        switch (type) {
            case 0: {
                return get_dame_physical();
            }
            case 1:
            case 2:
            case 3:
            case 4: {
                return get_dame_prop(type);
            }
            case 7: {
                return get_percent_damePhysical();
            }
            case 8:
            case 9:
            case 10:
            case 11: {
                return get_percent_dameProp(type);
            }
            case 14: {
                return get_def();
            }
            case 15: {
                return get_percent_def();
            }
            case 33: {
                return get_crit();
            }
            case 34: {
                return get_miss();
            }
            case 35: {
                return get_react_dame();
            }
            case 36: {
                return get_pierce();
            }
            case 16:
            case 17:
            case 18:
            case 19:
            case 20:
            case 21:
            case 27:
            case 28:
            case 22: {
                return (total_item_param(type) + total_skill_param(type));
            }
            default: {
                return total_item_param(type);
            }
        }
    }

    public int total_skill_param(int id) {
        int param = 0;
        for (int i = 0; i < p.skill_point.length; i++) {
            if (p.skill_point[i] > 0) {
                Option[] temp = p.skills[i].mLvSkill[get_skill_point(i) - 1].minfo;
                for (Option op : temp) {
                    if (op.id == id) {
                        param += op.getParam(0);
                    }
                }
            }
        }
        return param;
    }

    public int get_pierce() {
        int pie = total_item_param(36) + total_skill_param(36);
        pie += get_point(4) * 2;
        pie += (p.luyenthe * 20);
        return pie;
    }

    public int get_react_dame() {
        int param = 2 * get_point(3);
        param += total_item_param(35);
        return param;
    }

    public int get_miss() {
        int param = 2 * get_point(2);
        param += total_item_param(34);
        return param;
    }

    public int get_crit() {
        int crit = total_item_param(33) + total_skill_param(33);
        crit += get_point(1) * 2;
        crit += (p.luyenthe * 20);
        return crit;
    }

    public int get_percent_def() {
        int def = total_item_param(15);
        def += get_point(2) * 10;
        if (get_skill_point(15) > 0) {
            for (Option op : p.skills[15].mLvSkill[get_skill_point(15) - 1].minfo) {
                if (op.id == 15) {
                    def += op.getParam(0);
                    break;
                }
            }
        }
        def += ((p.kinhmach[0] - 1) * 5 + p.kinhmach[1]) * 100;
        EffTemplate ef = p.get_eff(24);
        if (ef != null) {
            def += ef.param;
        }
        return def;
    }

    public int get_def() {
        long def = total_item_param(14);
        switch (p.clazz) {
            case 0: {
                def += get_point(2) * 20;
                break;
            }
            case 1: {
                def += get_point(2) * 22;
                break;
            }
            case 2: {
                def += get_point(2) * 20;
                break;
            }
            case 3: {
                def += get_point(2) * 22;
                break;
            }
        }
        def += (p.luyenthe * 20);
        def += ((def * (get_percent_def() / 100)) / 100);
        if (p.type_use_mount == 2 || (p.type_use_mount == 15 && p.id_horse == 106)) {
            def += ((def * 2) / 10);
        } else if (p.type_use_mount == 3 || p.type_use_mount == 5 || (p.type_use_mount == 17 && p.id_horse == 111)
                || (p.type_use_mount == 20 && p.id_horse == 115)) {
            def += ((def * 1) / 10);
        } else if ((p.type_use_mount == 22 && p.id_horse == 117)
                || (p.type_use_mount == 20 && (p.id_horse == 114 || p.id_horse == 116))) {
            def += ((def * 15) / 100);
        }
        EffTemplate ef = p.get_eff(0);
        if (ef != null) {
            def = (def * 8) / 10;
        }
        return (int) def;
    }

    public int get_percent_dameProp(int type) {
        int perct = 0;
        switch (p.clazz) {
            case 0: {
                if (type == 9 || type == 2) {
                    perct += get_point(1) * 20;
                    perct += total_item_param(9);
                    if (get_skill_point(12) > 0) {
                        for (Option op : p.skills[12].mLvSkill[get_skill_point(12) - 1].minfo) {
                            if (op.id == 9) {
                                perct += op.getParam(0);
                                break;
                            }
                        }
                    }
                }
                break;
            }
            case 1: {
                if (type == 11 || type == 4) {
                    perct += get_point(1) * 20;
                    perct += total_item_param(11);
                    if (get_skill_point(12) > 0) {
                        for (Option op : p.skills[12].mLvSkill[get_skill_point(12) - 1].minfo) {
                            if (op.id == 11) {
                                perct += op.getParam(0);
                                break;
                            }
                        }
                    }
                }
                break;
            }
            case 2: {
                if (type == 8 || type == 1) {
                    perct += get_point(1) * 20 + get_point(4) * 18;
                    perct += total_item_param(8);
                    if (get_skill_point(12) > 0) {
                        for (Option op : p.skills[12].mLvSkill[get_skill_point(12) - 1].minfo) {
                            if (op.id == 8) {
                                perct += op.getParam(0);
                                break;
                            }
                        }
                    }
                }
                break;
            }
            case 3: {
                if (type == 10 || type == 3) {
                    perct += get_point(1) * 20 + get_point(4) * 18;
                    perct += total_item_param(10);
                    if (get_skill_point(12) > 0) {
                        for (Option op : p.skills[12].mLvSkill[get_skill_point(12) - 1].minfo) {
                            if (op.id == 10) {
                                perct += op.getParam(0);
                                break;
                            }
                        }
                    }
                }
                break;
            }
        }
        perct += ((p.kinhmach[0] - 1) * 5 + p.kinhmach[1]) * 100;
        return perct;
    }

    public int get_skill_point(int i) {
        if (p.skill_point[i] > 0) {
            int par_ = p.skill_point[i] + get_skill_point_plus(i);
            return (par_ > 15 ? 15 : par_);
        }
        return 0;
    }

    public int get_percent_damePhysical() {
        int percent = total_item_param(7);
        switch (p.clazz) {
            case 0:
            case 1: {
                percent += get_point(1) * 20;
                break;
            }
            case 2:
            case 3: {
                percent += get_point(1) * 20 + get_point(4) * 18;
                break;
            }
        }
        if (get_skill_point(11) > 0) {
            for (Option op : p.skills[11].mLvSkill[get_skill_point(11) - 1].minfo) {
                if (op.id == 7) {
                    percent += op.getParam(0);
                    break;
                }
            }
        }
        return percent;
    }

    public int get_dame_prop(int type) {
        long dprop = 0;
        switch (p.clazz) {
            case 0: {
                if (type == 2) {
                    dprop += get_point(1) * 4;
                    dprop += total_item_param(2);
                }
                break;
            }
            case 1: {
                if (type == 4) {
                    dprop += get_point(1) * 4;
                    dprop += total_item_param(4);
                }
                break;
            }
            case 2: {
                if (type == 1) {
                    dprop += get_point(4) * 4;
                    dprop += total_item_param(1);
                }
                break;
            }
            case 3: {
                if (type == 3) {
                    dprop += get_point(4) * 4;
                    dprop += total_item_param(3);
                }
                break;
            }
        }
        dprop += (p.luyenthe * 20);
        dprop += ((dprop * (get_percent_dameProp(type) / 100)) / 100);
        if (dprop > 2_000_000_000) {
            dprop = 2_000_000_000;
        }
        return (int) dprop;
    }

    public int get_dame_physical() {
        long dame = total_item_param(0);
        switch (p.clazz) {
            case 0: {
                dame += get_point(1) * 4;
                break;
            }
            case 1: {
                dame += get_point(1) * 4;
                break;
            }
            case 2: {
                dame += get_point(4) * 4;
                break;
            }
            case 3: {
                dame += get_point(4) * 4;
                break;
            }
        }
        dame += ((dame * (get_percent_damePhysical() / 100)) / 100);
        if (dame > 2_000_000_000) {
            dame = 2_000_000_000;
        }
        return (int) dame;
    }

    public int get_skill_point_plus(int i) {
        int par = 0;
        if (i >= 1 && i <= 8 || i == 19 || i == 20 || i == 17) {
            par = total_item_param(37);
        }
        if ((i >= 9 && i <= 16) || i == 18) {
            par = total_item_param(38);
        }
        return (par > 5) ? 5 : par;
    }

    public int get_resist_dame(int type, byte clazz, Player p0) {
        int param = 0;
        if (type == 0) {
            param += p0.body.total_item_param(16) + p0.body.total_skill_param(16);
        } else if (type == 1) {
            switch (clazz) {
                case 0: {
                    param += p0.body.total_item_param(18) + p0.body.total_skill_param(18);
                    break;
                }
                case 1: {
                    param += p0.body.total_item_param(20) + p0.body.total_skill_param(20);
                    break;
                }
                case 2: {
                    param += p0.body.total_item_param(17) + p0.body.total_skill_param(17);
                    break;
                }
                case 3: {
                    param += p0.body.total_item_param(19) + p0.body.total_skill_param(19);
                    break;
                }
            }
        }
        EffTemplate ef = p.get_eff(4);
        if (ef != null) {
            param -= 1000;
        }
        if (param < 0) {
            param = 0;
        }
        return param;
    }
}
