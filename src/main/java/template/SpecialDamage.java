package template;

import client.Player;
import core.Util;

public class SpecialDamage {
    public static final int TYPE_NONE = 0;
    public static final int TYPE_LIGHT = 1;
    public static final int TYPE_DARK = 2;

    public static final int CRIT_DMG_MIN = 115; // x1.15

    public int type;
    public int damage;
    public boolean isCrit;
    public int rawDamage;

    public static SpecialDamage calculate(Player attacker) {
        SpecialDamage result = new SpecialDamage();
        if (attacker == null || attacker.body == null) {
            return result;
        }

        int lightDmg = attacker.body.total_item_param(6);
        int darkDmg = attacker.body.total_item_param(5);

        int diff = lightDmg - darkDmg;
        if (diff > 0) {
            result.type = TYPE_LIGHT;
            result.damage = diff;
        } else if (diff < 0) {
            result.type = TYPE_DARK;
            result.damage = -diff;
        } else {
            result.type = TYPE_NONE;
            result.damage = 0;
            return result;
        }

        result.rawDamage = result.damage;

        int critRate = 0;
        int critDmgMax = 0;

        if (result.type == TYPE_LIGHT) {
            critRate = attacker.body.total_item_param(74);
            if (critRate > 10000) {
                critRate = 10000;
            }
            critDmgMax = attacker.body.get_max_crit_damage_param(75);
        } else {
            critRate = attacker.body.total_item_param(72);
            if (critRate > 10000) {
                critRate = 10000;
            }
            critDmgMax = attacker.body.get_max_crit_damage_param(73);
        }

        if (critRate > 0 && critRate > Util.random(10000)) {
            result.isCrit = true;
            int multiplier = Util.random(CRIT_DMG_MIN, critDmgMax);
            result.damage = (int) (((long) result.damage * multiplier) / 100L);
        }

        return result;
    }
}
