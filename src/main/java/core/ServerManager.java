package core;

import event.Event_1;
import event.NauKeo;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Calendar;
import client.Clan;
import client.Player;
import event.BossEvent;
import event_daily.ChienTruong;
import event_daily.CongNap;
import event_daily.LoiDai;
import io.Session;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import map.Map;
import map.Mob_in_map;
import template.MiNuong;
import template.Mob;
import java.util.TimeZone;

public class ServerManager implements Runnable {

    private static ServerManager instance;
    private final Thread mythread;
    private Thread server_live;
    private boolean running;
    private ServerSocket server;
    private final long time;
    public long time_l;

    public ServerManager() {
        this.time = System.currentTimeMillis();
        this.time_l = System.currentTimeMillis() + 60_000L;
        this.mythread = new Thread(this);
    }

    public static ServerManager gI() {
        if (instance == null) {
            instance = new ServerManager();
        }
        return instance;
    }

    public void init() {
        Manager.gI().init();
        Mob_in_map.create_boss();
        server_update_right_time();
        this.running = true;
        this.mythread.start();
        this.server_live.start();
        new Thread(() -> {
            while (this.running) {
                if (this.time_l < System.currentTimeMillis()) {
//                    System.exit(0);
//Manager.gI().debug = true;
                }
                try {
                    Thread.sleep(60_000L);
                } catch (InterruptedException ex) {

                }
            }
        }).start();
    }

    public void run() {
        try {
            this.server = new ServerSocket(Manager.gI().server_port);
            System.out.println("Khởi Chạy Trong " + (System.currentTimeMillis() - this.time) + "s");
            System.out.println();
            System.out.println("Server Đang Chạy Ở Cổng " + Manager.gI().server_port +"" );
            while (this.running) {
                Socket client = this.server.accept();
//				synchronized (Session.client_entrys) {
                if (this.running) {
                    Session ss = new Session(client);
                    ss.init();
                }
//				}
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void server_update_right_time() {
        this.server_live = new Thread(() -> {
            Calendar now;
            int hour, min, sec, millis;
            SaveData.process();
            while (this.running) {
                try {
                    now = Calendar.getInstance(TimeZone.getTimeZone("GMT+7"));
                    hour = now.get(Calendar.HOUR_OF_DAY);
                    min = now.get(Calendar.MINUTE);
                    sec = now.get(Calendar.SECOND);
                    millis = now.get(Calendar.MILLISECOND);
                    //
                    if (Mob_in_map.num_boss < 5 && (min == 0) && sec == 3 && (hour % 1 == 0)) {
                        Mob_in_map.create_boss();
                        Mob_in_map.create_boss();
                        Mob_in_map.create_boss();
                        Mob_in_map.create_boss();
                        Mob_in_map.create_boss();
                    }
                    if (min % 7 == 0 && sec == 0) {
                        Manager.gI().chatKTGprocess("Bạn Đang Chơi Game Hiệp Sĩ Ong Đất ver.1.2w67 Custom Không Reset Nhân Vật");
                    }
                    if (min % 11 == 0 && sec == 0) {
                        Manager.gI().chatKTGprocess("Nhận điểm danh hàng ngày tại NPC Zulu, Đến Top BXH để xem thông tin cá nhân");
                    }
                    if (min % 17 == 0 && sec == 0) {
                        Manager.gI().chatKTGprocess("Chat '/help' Để Xem Danh Sách Dòng Lệnh");
                    }
                    if (min % 1 == 0 && sec == 10) { // update BXH + luu data
                        new Thread(() -> {
                            SaveData.process();
                        }).start();
                    }
                    if (hour == 0 && min == 0 && sec == 1) {
                        Manager.gI().ip_create_char.clear();
                        CongNap.NV_CONG_NAP = (byte) Util.random(16);
                        for (Map[] map : Map.entrys) {
                            for (Map map0 : map) {
                                for (int i = 0; i < map0.players.size(); i++) {
                                    map0.players.get(i).change_new_date();
                                }
                            }
                        }
                    }
                    if (Manager.gI().event == 1) {
                        if (Event_1.naukeo == null) {
                            Event_1.naukeo = new NauKeo();
                        }
                        Event_1.naukeo.h = hour;
                        Event_1.naukeo.m = min;
                        if (hour == 10 && min == 00 && sec == 00) {
                            Event_1.list_nhankeo.clear();
                            Event_1.naukeo.start();
                            Manager.gI().chatKTGprocess("@Server: Bắt đầu nấu kẹo, các hiệp sĩ có thể tăng tốc thời gian nấu");
                        }
                        if (sec == 0 && min % 1 == 0) {
                            Event_1.naukeo.update(1);
                        }
                        if (min % 5 == 0 && sec == 0) {
                            Event_1.sort_bxh();
                        }
                    }
                    if (sec == 3 && min == 0 && (hour == 1 || hour == 13)) {
                        Manager.gI().chiem_mo.mo_open_atk();
                        Manager.gI().chatKTGprocess("@Server : Thời gian chiếm mỏ đã đến!");
                    } else if (sec == 3 && min == 0 && (hour == 2 || hour == 14)) {
                        Manager.gI().chiem_mo.mo_close_atk();
                        Manager.gI().chatKTGprocess("@Server : Thời gian chiếm mỏ đã đóng!");
                    }
                    if ((hour % 3 == 0) && min == 0 && sec == 5) {
                     Manager.gI().bossTG.refresh();
}
                    if ((hour % 3 == 0) && min == 29 && sec == 58) {
                     Manager.gI().bossTG.finish();
}
                    if (min % 1 == 0 && sec == 4) {
                        Manager.gI().chiem_mo.harvest_all();
                    }
                    if (hour == 13 && min == 45 && sec == 5) {
                        ChienTruong.gI().open_register();
                    }
                    if (sec % 1 == 0) {
                        LoiDai.update_state();
                        ChienTruong.gI().update();
                    }

                    if (Manager.gI().event == 2 && min % 10 == 0 && sec == 0 && BossEvent.MiNuong.size() < 10) {
                        try {
                            Mob_in_map mob = new Mob_in_map();
                            mob.template = Mob.entrys.get(150);
                            mob.hpmax = 100;
                            mob.hp = mob.hpmax;
                            mob.level = 11;
                            mob.isdie = false;
                            mob.color_name = 0;
                            mob.is_boss = false;
                            mob.time_back = 0;
                            mob.list_fight = new ArrayList<>();
                            mob.is_boss_active = false;
                            MiNuong mn = new MiNuong();
                            mn.mob = mob;
                            mn.owner = "";
                            mn.power = 1000;
                            //
                            short[] id_random = new short[]{9, 26, 27, 16, 13, 12, 17, 39, 40, 44, 20, 45, 41, 51, 52, 65, 73,
                                76, 94, 97, 98};
                            // id_random = new short[] {26};
                            Map[] map_rd = Map.get_map_by_id(id_random[Util.random(id_random.length)]);
                            // while (map_rd[0].mobs.length < 1) {
                            // map_rd = Map.get_map_by_id(id_random[Util.random(id_random.length)]);
                            // }
                            mn.map = map_rd[Util.random(2, 4)];
                            Mob_in_map temppp = mn.map.mobs[Util.random(mn.map.mobs.length)];
                            mob.x = temppp.x;
                            mob.y = temppp.y;
                            mob.map_id = mn.map.map_id;
                            mob.zone_id = mn.map.zone_id;
                            System.out.println("mi nuong " + mn.map.name + " khu " + (mn.map.zone_id + 1));
                            //
                            BossEvent.MiNuong.add(mn);
                            mob.index = MiNuong.TEMPLATE--;
                            // - BossEvent.MiNuong.indexOf(mn);
                            BossEvent.LIST.forEach(l -> {
                                if (BossEvent.time[BossEvent.LIST.indexOf(l)] < System.currentTimeMillis()) {
                                    l.is_boss_active = true;
                                }
                            });
                        } catch (Exception e) {
                        }
                    }
                    //
                    long time_sleep = 1000 - millis;
                    if (time_sleep > 0) {
                        if (time_sleep < 100) {
                            System.err.println("server time update process is overloading...");
                        }
                        Thread.sleep(time_sleep);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void close() throws IOException {
        running = false;
        server_live.interrupt();
        server.close();
//        instance = null;
    }
}
