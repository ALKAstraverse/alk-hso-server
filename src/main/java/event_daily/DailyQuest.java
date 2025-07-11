package event_daily;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import client.Player;
import core.Log;
import core.Service;
import core.Util;
import map.Map;
import map.Mob_in_map;
import template.Item47;
import template.ItemTemplate7;
import template.Mob;

public class DailyQuest {

    public static String[] NV_TEMPLATE = new String[]{ //
        "Tiêu diệt %s %s",
        "Tham gia lôi đài %s lần",
        "Tham gia đi buôn %s lần",
        "Tham gia tài xỉu %s lần",
        "Tham gia vòng xoay %s lần",
        "Tham gia mở ly %s lần",
        "Vượt phó bản %s lần",
        "Tham gia chuyển sinh %s lần",
        "Tham gia hoạt động boss thế giới và đạt top 3 %s lần"
    };

    public static void get_quest(Player p, byte select) throws IOException {
        if (p.quest_daily[0] != -1) {
            Service.send_notice_box(p.conn, "Nhiệm vụ hiện tại chưa hoàn thành");
            return;
        }
        if (p.quest_daily[5]   == -1) {
            return;
        }
        int index_nv = -1;
        switch (select) {
            case 0: {
                index_nv = Util.random(NV_TEMPLATE.length - 3);
                break;
            }
            case 1: {
                index_nv = Util.random(NV_TEMPLATE.length - 2);
                break;
            }
            case 2: {
                index_nv = Util.random(NV_TEMPLATE.length - 1);
                break;
            }
            default: {
                index_nv = Util.random(NV_TEMPLATE.length);
                break;
            }
        }
        if  (index_nv == 1) {
            index_nv = 0;
        }
        switch (index_nv) {
            case 8: {
                p.quest_daily[0] = index_nv;
                p.quest_daily[1] = -1;
                p.quest_daily[2] = select;
                p.quest_daily[3] = 0;
                p.quest_daily[5]--;
                p.quest_daily[4] = 1;
                Service.send_notice_box(p.conn, String.format(
                        "Nhiệm vụ hiện tại:\nTham gia hoạt động boss thế giới và đạt top 3 sát thương.\nHôm nay còn %s lượt.",
                        p.quest_daily[5]));
                break;
            }
            case 7: {
                p.quest_daily[0] = index_nv;
                p.quest_daily[1] = -1;
                p.quest_daily[2] = select;
                p.quest_daily[3] = 0;
                p.quest_daily[5]--;
                switch (select) {
                    case 0: {
                        p.quest_daily[4] = 4;
                        break;
                    }
                    case 1: {
                        p.quest_daily[4] = 10;
                        break;
                    }
                    case 2: {
                        p.quest_daily[4] = 25;
                        break;
                    }
                    case 3: {
                        p.quest_daily[4] = 50;
                        break;
                    }
                }
                Service.send_notice_box(p.conn,
                        String.format("Nhiệm vụ hiện tại:\nTham gia chuyển sinh %s lần.\nHôm nay còn %s lượt.",
                                p.quest_daily[4], p.quest_daily[5]));
                break;
            }
            case 6: {
                p.quest_daily[0] = index_nv;
                p.quest_daily[1] = -1;
                p.quest_daily[2] = select;
                p.quest_daily[3] = 0;
                p.quest_daily[5]--;
                switch (select) {
                    case 0: {
                        p.quest_daily[4] = 5;
                        break;
                    }
                    case 1: {
                        p.quest_daily[4] = 10;
                        break;
                    }
                    case 2: {
                        p.quest_daily[4] = 20;
                        break;
                    }
                    case 3: {
                        p.quest_daily[4] = 50;
                        break;
                    }
                }
                Service.send_notice_box(p.conn,
                        String.format("Nhiệm vụ hiện tại:\nVượt phó bản %s lần.\nHôm nay còn %s lượt.", p.quest_daily[4],
                                p.quest_daily[5]));
                break;
            }
            case 5: {
                p.quest_daily[0] = index_nv;
                p.quest_daily[1] = -1;
                p.quest_daily[2] = select;
                p.quest_daily[3] = 0;
                p.quest_daily[5]--;
                switch (select) {
                    case 0: {
                        p.quest_daily[4] = 20;
                        break;
                    }
                    case 1: {
                        p.quest_daily[4] = 40;
                        break;
                    }
                    case 2: {
                        p.quest_daily[4] = 60;
                        break;
                    }
                    case 3: {
                        p.quest_daily[4] = 100;
                        break;
                    }
                }
                Service.send_notice_box(p.conn,
                        String.format("Nhiệm vụ hiện tại:\nTham gia mở ly %s lần.\nHôm nay còn %s lượt.", p.quest_daily[4],
                                p.quest_daily[5]));
                break;
            }
            case 4: {
                p.quest_daily[0] = index_nv;
                p.quest_daily[1] = -1;
                p.quest_daily[2] = select;
                p.quest_daily[3] = 0;
                p.quest_daily[5]--;
                switch (select) {
                    case 0: {
                        p.quest_daily[4] = 10;
                        break;
                    }
                    case 1: {
                        p.quest_daily[4] = 25;
                        break;
                    }
                    case 2: {
                        p.quest_daily[4] = 50;
                        break;
                    }
                    case 3: {
                        p.quest_daily[4] = 100;
                        break;
                    }
                }
                Service.send_notice_box(p.conn,
                        String.format("Nhiệm vụ hiện tại:\nTham gia vòng xoay %s lần.\nHôm nay còn %s lượt.",
                                p.quest_daily[4], p.quest_daily[5]));
                break;
            }
            case 3: {
                p.quest_daily[0] = index_nv;
                p.quest_daily[1] = -1;
                p.quest_daily[2] = select;
                p.quest_daily[3] = 0;
                p.quest_daily[5]--;
                switch (select) {
                    case 0: {
                        p.quest_daily[4] = 10;
                        break;
                    }
                    case 1: {
                        p.quest_daily[4] = 20;
                        break;
                    }
                    case 2: {
                        p.quest_daily[4] = 40;
                        break;
                    }
                    case 3: {
                        p.quest_daily[4] = 70;
                        break;
                    }
                }
                Service.send_notice_box(p.conn,
                        String.format("Nhiệm vụ hiện tại:\nTham gia tài xỉu %s lần.\nHôm nay còn %s lượt.", p.quest_daily[4],
                                p.quest_daily[5]));
                break;
            }
            case 2: {
                p.quest_daily[0] = index_nv;
                p.quest_daily[1] = -1;
                p.quest_daily[2] = select;
                p.quest_daily[3] = 0;
                p.quest_daily[5]--;
                switch (select) {
                    case 0: {
                        p.quest_daily[4] = 10;
                        break;
                    }
                    case 1: {
                        p.quest_daily[4] = 20;
                        break;
                    }
                    case 2: {
                        p.quest_daily[4] = 40;
                        break;
                    }
                    case 3: {
                        p.quest_daily[4] = 70;
                        break;
                    }
                }
                Service.send_notice_box(p.conn,
                        String.format("Nhiệm vụ hiện tại:\nTham gia đi buôn %s lần.\nHôm nay còn %s lượt.", p.quest_daily[4],
                                p.quest_daily[5]));
                break;
            }
            case 1: {
                p.quest_daily[0] = index_nv;
                p.quest_daily[1] = -1;
                p.quest_daily[2] = select;
                p.quest_daily[3] = 0;
                p.quest_daily[5]--;
                switch (select) {
                    case 0: {
                        p.quest_daily[4] = 5;
                        break;
                    }
                    case 1: {
                        p.quest_daily[4] = 10;
                        break;
                    }
                    case 2: {
                        p.quest_daily[4] = 20;
                        break;
                    }
                    case 3: {
                        p.quest_daily[4] = 50;
                        break;
                    }
                }
                Service.send_notice_box(p.conn,
                        String.format("Nhiệm vụ hiện tại:\nTham gia lôi đài %s lần.\nHôm nay còn %s lượt.", p.quest_daily[4],
                                p.quest_daily[5]));
                break;
            }
            default: {
                List<Mob_in_map> list_mob = new ArrayList<>();
                for (Map[] maps : Map.entrys) {
                    for (Map map : maps) {
                        for (Mob_in_map mob : map.mobs) {
                            if (Math.abs(mob.level - p.level) <= 5) {
                                list_mob.add(mob);
                            }
                        }
                    }
                }
                if (list_mob.size() < 1) {
                    Service.send_notice_box(p.conn, "Không có nhiệm vụ phù hợp");
                    return;
                }
                int index = Util.random(list_mob.size());
                p.quest_daily[0] = index_nv;
                p.quest_daily[1] = list_mob.get(index).template.mob_id;
                p.quest_daily[2] = select;
                p.quest_daily[3] = 0;
                p.quest_daily[5]--;
                switch (select) {
                    case 0: {
                        p.quest_daily[4] = 500;
                        break;
                    }
                    case 1: {
                        p.quest_daily[4] = 1000;
                        break;
                    }
                    case 2: {
                        p.quest_daily[4] = 3000;
                        break;
                    }
                    case 3: {
                        p.quest_daily[4] = 6000;
                        break;
                    }
                }
                Service.send_notice_box(p.conn,
                        String.format("Nhiệm vụ hiện tại:\nTiêu diệt %s/%s %s.\nHôm nay còn %s lượt.", p.quest_daily[3],
                                p.quest_daily[4], Mob.entrys.get(p.quest_daily[1]).name, p.quest_daily[5]));
                break;
            }
        }
    }

    public static void remove_quest(Player p) throws IOException {
        if (p.quest_daily[0] == -1) {
            Service.send_notice_box(p.conn, "Hiện tại không nhận nhiệm vụ nào!");
        } else {
            p.quest_daily[0] = -1;
            p.quest_daily[1] = -1;
            p.quest_daily[2] = 0;
            p.quest_daily[3] = 0;
            p.quest_daily[4] = 0;
            Service.send_notice_box(p.conn, "Hủy nhiệm vụ thành công, nào rảnh quay lại nhận tiếp nhá!");
        }
    }

    public static String info_quest(Player p) throws IOException {
        if (p.quest_daily[0] == -1) {
            return String.format("Bạn chưa nhận nhiệm vụ.\nHôm nay còn %s lượt.", p.quest_daily[5]);
        } else {
            switch (p.quest_daily[0]) {
                case 8:
                    return String.format(
                            "Nhiệm vụ hiện tại:\nTham gia hoạt động boss thế giới và đạt top 3 sát thương %s/1 lần.\nHôm nay còn %s lượt.",
                            p.quest_daily[3], p.quest_daily[5]);
                case 7:
                    return String.format("Nhiệm vụ hiện tại:\nTham gia chuyển sinh %s / %s lần.\nHôm nay còn %s lượt.",
                            p.quest_daily[3], p.quest_daily[4], p.quest_daily[5]);
                case 6:
                    return String.format("Nhiệm vụ hiện tại:\nVượt phó bản %s / %s lần.\nHôm nay còn %s lượt.",
                            p.quest_daily[3], p.quest_daily[4], p.quest_daily[5]);
                case 5:
                    return String.format("Nhiệm vụ hiện tại:\nTham gia mở ly %s / %s lần.\nHôm nay còn %s lượt.",
                            p.quest_daily[3], p.quest_daily[4], p.quest_daily[5]);
                case 4:
                    return String.format("Nhiệm vụ hiện tại:\nTham gia vòng xoay %s / %s lần.\nHôm nay còn %s lượt.",
                            p.quest_daily[3], p.quest_daily[4], p.quest_daily[5]);
                case 3:
                    return String.format("Nhiệm vụ hiện tại:\nTham gia tài xỉu %s / %s lần.\nHôm nay còn %s lượt.",
                            p.quest_daily[3], p.quest_daily[4], p.quest_daily[5]);
                case 2:
                    return String.format("Nhiệm vụ hiện tại:\nTham gia đi buôn %s / %s lần.\nHôm nay còn %s lượt.",
                            p.quest_daily[3], p.quest_daily[4], p.quest_daily[5]);
                case 1:
                    return String.format("Nhiệm vụ hiện tại:\nTham gia lôi đài %s / %s lần.\nHôm nay còn %s lượt.",
                            p.quest_daily[3], p.quest_daily[4], p.quest_daily[5]);
                default: {
                    return String.format("Nhiệm vụ hiện tại:\nTiêu diệt %s/%s %s.\nHôm nay còn %s lượt.", p.quest_daily[3],
                            p.quest_daily[4], Mob.entrys.get(p.quest_daily[1]).name, p.quest_daily[5]);
                }
            }
        }
    }

    public static void finish_quest(Player p) throws IOException {
        if (p.quest_daily[0] == -1) {
            Service.send_notice_box(p.conn, "Hiện tại không nhận nhiệm vụ nào!");
        } else if (p.quest_daily[3] == p.quest_daily[4]) {

            int ngoc = p.quest_daily[2] == 3 ? 5_000
                    : (p.quest_daily[2] == 2 ? 3_000
                            : (p.quest_daily[2] == 1 ? 1_500 : 1_000));
            p.update_ngoc(ngoc);
            if (p.quest_daily[2] == 3 || p.quest_daily[2] == 2) {
                Item47 itbag = new Item47();
                itbag.id = 472;
                itbag.quantity = (short) (p.quest_daily[2] == 3 ? 5 : 3);
                itbag.category = 7;
                p.item.add_item_bag47(7, itbag);
            }

            Service.send_notice_box(p.conn,
                    "Trả thành công, nhận được " + ngoc + " ngoc"
                    + ((p.quest_daily[2] == 3 || p.quest_daily[2] == 2) ? " và "
                            + ((p.quest_daily[2] == 3 ? 5 + "" : 3 + "") + " " + ItemTemplate7.item.get(472).getName())
                            : ""));
            p.quest_daily[0] = -1;
            p.quest_daily[1] = -1;
            p.quest_daily[2] = -1;
            p.quest_daily[3] = 0;
            p.quest_daily[4] = 0;
            p.item.char_inventory(7);
        } else {
            Service.send_notice_box(p.conn, "Chưa hoàn thành được nhiệm vụ!");
        }
    }
}
