package client;

import java.util.ArrayList;
import java.util.List;
import org.json.simple.JSONArray;
import org.json.simple.JSONValue;
import core.Util;
import template.Level;
import template.Option_pet;

public class Pet {

    public static String[] name_template = new String[]{"Cú", "Dơi", "Sói", "Đại Bàng", "Khỉ", "Rồng lửa", "Thỏ",
        "Phượng hoàng băng", "Zabivaka", "Bóng ma", "Dê con", "Yêu tinh", "Thiên thần", "Sao la"};
    public static byte[] type_template = new byte[]{8, 13, 10, 11, 11, 11, 12, 12, 12, 9, 7, 7, 7, 6, 5, 5, 5, 4, 4, 4,
        3, 3, 3, 2, 2, 2, 1, 1, 1, 0, 0, 0};
    public static byte[] icon_template = new byte[]{26, 41, 32, 33, 34, 35, 36, 37, 38, 29, 21, 22, 23, 20, 15, 16, 17,
        12, 13, 14, 9, 10, 11, 6, 7, 8, 0, 1, 2, 3, 4, 5};
    public List<Option_pet> op;
    public String name;
    public short level;
    public byte type;
    public byte icon;
    public byte nframe;
    public byte color;
    public short grown;
    public short maxgrown;
    public short point1;
    public short point2;
    public short point3;
    public short point4;
    public short maxpoint;
    public boolean is_follow;
    public boolean is_hatch;
    public long exp;
    public long time_born;
    public long time_eat;

    public void setup(JSONArray js) {
        level = Short.parseShort(js.get(0).toString());
        type = Byte.parseByte(js.get(1).toString());
        name = name_template[type];
        icon = Byte.parseByte(js.get(2).toString());
        nframe = Byte.parseByte(js.get(3).toString());
        color = Byte.parseByte(js.get(4).toString());
        grown = Short.parseShort(js.get(5).toString());
        maxgrown = Short.parseShort(js.get(6).toString());
        point1 = Short.parseShort(js.get(7).toString());
        point2 = Short.parseShort(js.get(8).toString());
        point3 = Short.parseShort(js.get(9).toString());
        point4 = Short.parseShort(js.get(10).toString());
        maxpoint = Short.parseShort(js.get(11).toString());
        exp = Long.parseLong(js.get(12).toString());
        is_follow = Byte.parseByte(js.get(13).toString()) == 1;
        is_hatch = Byte.parseByte(js.get(14).toString()) == 1;
        time_born = Long.parseLong(js.get(15).toString());
        op = new ArrayList<>();
        JSONArray js2 = (JSONArray) JSONValue.parse(js.get(16).toString());
        for (int i = 0; i < js2.size(); i++) {
            JSONArray js3 = (JSONArray) JSONValue.parse(js2.get(i).toString());
            Option_pet temp = new Option_pet(Byte.parseByte(js3.get(0).toString()),
                    Integer.parseInt(js3.get(1).toString()), Integer.parseInt(js3.get(2).toString()));
            op.add(temp);
        }
    }

    public int getlevelpercent() {
        return (int) ((exp * 1000) / Level.entrys.get(level - 1).exp);
    }

    public static Pet get_pet(short id) {
        Pet temp = new Pet();
        temp.level = 1;
        temp.nframe = 3;
        temp.color = 0;
        temp.grown = 0;
        temp.maxgrown = 300;
        temp.point1 = 0;
        temp.point2 = 0;
        temp.point3 = 0;
        temp.point4 = 0;
        temp.maxpoint = 10_000;
        temp.exp = 0;
        temp.is_follow = false;
        temp.is_hatch = true;
        temp.time_born = System.currentTimeMillis() + 1000L * 60 * 10;
        temp.op = new ArrayList<>();
        byte[] id_ = new byte[]{23, 24, 25, 26};
        int[] param_ = new int[]{1, 1, 1, 1};
        int[] maxdam_ = new int[]{0, 0, 0, 0};
        switch (id) {
            case 2943: {
                temp.icon = 0;
                temp.type = 1;
                id_ = new byte[]{23, 24, 25, 26, 47, 4};
                param_ = new int[]{1, 1, 1, 1, 400, 1000};
                maxdam_ = new int[]{0, 0, 0, 0, 500, 2000};
                break;
            }
            case 2944: {
                temp.icon = 3;
                temp.type = 0;
                id_ = new byte[]{23, 24, 25, 26, 44, 45, 1};
                param_ = new int[]{1, 1, 1, 1, 200, 3000, 1000};
                maxdam_ = new int[]{0, 0, 0, 0, 300, 5000, 2000};
                break;
            }
            case 2939: {
                temp.icon = 6;
                temp.type = 2;
                id_ = new byte[]{23, 24, 25, 26, 49, 48, 0};
                param_ = new int[]{1, 1, 1, 1, 100, 1500, 1000};
                maxdam_ = new int[]{0, 0, 0, 0, 100, 4500, 2000};
                break;
            }
            case 3269: {
                temp.icon = 9;
                temp.type = 3;
                id_ = new byte[]{23, 24, 25, 26, 46, 48, 3};
                param_ = new int[]{1, 1, 1, 1, 1000, 1500, 1000};
                maxdam_ = new int[]{0, 0, 0, 0, 2000, 4500, 2000};
                break;
            }
            case 3616: {
                temp.icon = 12;
                temp.type = 4;
                id_ = new byte[]{23, 24, 25, 26, 48, 49, 2};
                param_ = new int[]{1, 1, 1, 1, 1500, 100, 1000};
                maxdam_ = new int[]{0, 0, 0, 0, 4500, 100, 2000};
                break;
            }
            case 4617: {
                temp.icon = 15;
                temp.type = 5;
                id_ = new byte[]{23, 24, 25, 26, 48, 97, 98, 2};
                param_ = new int[]{1, 1, 1, 1, 1500, 1300, 2000, 1000};
                maxdam_ = new int[]{0, 0, 0, 0, 4500, 2300, 4500, 2000};
                break;
            }
            case 4626: {
                temp.icon = 21;
                temp.type = 7;
                id_ = new byte[]{23, 24, 25, 26, 67, 113, 114, 1}; // chir so
                param_ = new int[]{1, 1, 1, 1, 1350, 300, 2, 1000};
                maxdam_ = new int[]{0, 0, 0, 0, 2350, 500, 3, 2000};
                break;
            }
            case 4631: {
                temp.icon = 24;
                temp.type = 8;
                id_ = new byte[]{23, 24, 25, 26, 48, 80, 85, 86, 114, 0}; // chir so
                param_ = new int[]{1, 1, 1, 1, 1500, 100, 100, 1000, 2, 1000};
                maxdam_ = new int[]{0, 0, 0, 0, 4500, 200, 150, 1500, 3, 2000};
                break;
            }
            case 4699: {
                temp.icon = 27;
                temp.type = 9;
                id_ = new byte[]{23, 24, 25, 26, 48, 80, 85, 86, 114, 1}; // chir so
                param_ = new int[]{1, 1, 1, 1, 1500, 100, 100, 1000, 2, 1000};
                maxdam_ = new int[]{0, 0, 0, 0, 4500, 200, 150, 1500, 3, 2000};
                break;
            }
            case 4761: {
                temp.icon = 32;
                temp.type = 11;
                id_ = new byte[]{23, 24, 25, 26, 48, 80, 114, 2}; // chir so
                param_ = new int[]{1, 1, 1, 1, 1500, 100, 2, 1000};
                maxdam_ = new int[]{0, 0, 0, 0, 4500, 200, 3, 2000};
                break;
            }
            case 4762: {
                temp.icon = 36;
                temp.type = 12;
                id_ = new byte[]{23, 24, 25, 26, 48, 80, 114, 3}; // chir so
                param_ = new int[]{1, 1, 1, 1, 1500, 100, 2, 1000};
                maxdam_ = new int[]{0, 0, 0, 0, 4500, 200, 3, 2000};
                break;
            }
            case 4768: {
                temp.icon = 39;
                temp.type = 13;
                id_ = new byte[]{23, 24, 25, 26, 48, 80, 85, 86, 114, 1}; // chir so
                param_ = new int[]{1, 1, 1, 1, 1500, 100, 100, 1000, 2, 1000};
                maxdam_ = new int[]{0, 0, 0, 0, 4500, 200, 150, 1500, 3, 2000};
                break;
            }
            default: {
                return null;
            }
        }
        temp.name = name_template[temp.type];
        for (int i = 0; i < id_.length; i++) {
            Option_pet op = new Option_pet(id_[i], param_[i], maxdam_[i]);
            temp.op.add(op);
        }
        return temp;
    }

    public static int get_id(byte type) {
        if (type == 0) {
            return 2944;

        } else if (type == 2) {
            return 2939;

        } else if (type == 3) {
            return 3269;

        } else if (type == 4) {
            return 3616;

        } else if (type == 5) {
            return 4617;

        } else if (type == 7) {
            return 4626;

        } else if (type == 8) {
            return 4631;

        } else if (type == 9) {
            return 4699;

        } else if (type == 10) {
            return 4761;

        } else if (type == 11) {
            return 4762;

        } else if (type == 12) {
            return 4768;

        }
        return 2943;

    }

    public void update_exp(int i) {
        if (i <= 0) {
            return;
        }
        exp += i;
        if ((this.level == 9 || this.level == 19 || this.level == 29) && exp >= Level.entrys.get(level - 1).exp) {
            exp = Level.entrys.get(level - 1).exp - 1;
        } else {
            while (exp >= Level.entrys.get(level - 1).exp) {
                exp -= Level.entrys.get(level - 1).exp;
                level++;
                for (int j = 0; j < op.size(); j++) {
                    if (op.get(j).id >= 23 && op.get(j).id <= 26) {
                        op.get(j).param += Util.random(0, 5);
                    } else {
                        int par_plus = Util.random(50, 150);
                        op.get(j).param += par_plus;
                        op.get(j).maxdam += par_plus;
                    }
                }
            }
        }
    }

    public int get_age() {
        long age = System.currentTimeMillis() - time_born;
        age /= 3_600_000;
        if (age < 0) {
            age = 0;
        } else if (age > Integer.MAX_VALUE) {
            age = Integer.MAX_VALUE;
        }
        return (int) age;
    }

    public boolean can_revolution() {
        if ((this.exp == (Level.entrys.get(this.level-1).exp - 1))
                && (this.level == 9 || this.level == 19 || this.level == 29)) {
            return true;
        }
        return false;
    }

    public void update_grown(long t) {
        this.grown -= t;
        if (this.grown < 0) {
            this.grown = 0;
        }
    }
}
