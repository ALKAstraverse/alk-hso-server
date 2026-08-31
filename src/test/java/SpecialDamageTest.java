import org.junit.Test;
import static org.junit.Assert.*;

import client.Body;
import client.Item;
import client.Player;
import core.GameSrc;
import core.Util;
import template.Item3;
import template.Option;
import template.SpecialDamage;

import java.util.ArrayList;

public class SpecialDamageTest {

    @Test
    public void testLightGemRollStats() {
        // Test level 1 to 5 light gem damage calculation
        for (int level = 1; level <= 5; level++) {
            int lightDmg = 100 * (1 << (level - 1));
            assertEquals(100 * (int)Math.pow(2, level - 1), lightDmg);
        }
        assertEquals(100, 100 * (1 << 0));
        assertEquals(200, 100 * (1 << 1));
        assertEquals(400, 100 * (1 << 2));
        assertEquals(800, 100 * (1 << 3));
        assertEquals(1600, 100 * (1 << 4));

        // Test roll statistics over 10,000 rolls
        int critCount = 0;
        for (int i = 0; i < 10000; i++) {
            if (Util.random(100) < 75) {
                critCount++;
                int critRate = Util.random(1, 80) * 100;
                int critDmg = Util.random(115, 200);
                assertTrue("Crit rate should be >= 1% (100)", critRate >= 100);
                assertTrue("Crit rate should be <= 80% (8000)", critRate <= 8000);
                assertTrue("Crit dmg should be >= 1.15x (115)", critDmg >= 115);
                assertTrue("Crit dmg should be <= 2.00x (200)", critDmg <= 200);
            }
        }
        double critPercentage = (critCount / 10000.0) * 100;
        assertTrue("Crit rate probability should be ~75%", critPercentage >= 72.0 && critPercentage <= 78.0);
    }

    @Test
    public void testDamageCancellation() {
        // Case 1: Light 1000, Dark 500 -> 500 Light
        Player p1 = new Player(null, 1);
        p1.item = new Item(p1);
        p1.item.wear = new Item3[24];
        p1.body = new Body(p1);
        p1.tutien = new int[5];
        p1.kinhmach = new int[5];
        Item3 weapon1 = new Item3();
        weapon1.op = new ArrayList<>();
        weapon1.op.add(new Option(6, 1000)); // Light 1000
        weapon1.op.add(new Option(5, 500));  // Dark 500
        p1.item.wear[11] = weapon1;

        SpecialDamage sp1 = SpecialDamage.calculate(p1);
        assertEquals(SpecialDamage.TYPE_LIGHT, sp1.type);
        assertEquals(500, sp1.rawDamage);

        // Case 2: Light 500, Dark 1000 -> 500 Dark
        Item3 weapon2 = new Item3();
        weapon2.op = new ArrayList<>();
        weapon2.op.add(new Option(6, 500));  // Light 500
        weapon2.op.add(new Option(5, 1000)); // Dark 1000
        p1.item.wear[11] = weapon2;

        SpecialDamage sp2 = SpecialDamage.calculate(p1);
        assertEquals(SpecialDamage.TYPE_DARK, sp2.type);
        assertEquals(500, sp2.rawDamage);

        // Case 3: Light 500, Dark 500 -> 0 (No special damage)
        Item3 weapon3 = new Item3();
        weapon3.op = new ArrayList<>();
        weapon3.op.add(new Option(6, 500)); // Light 500
        weapon3.op.add(new Option(5, 500)); // Dark 500
        p1.item.wear[11] = weapon3;

        SpecialDamage sp3 = SpecialDamage.calculate(p1);
        assertEquals(SpecialDamage.TYPE_NONE, sp3.type);
        assertEquals(0, sp3.damage);
    }

    @Test
    public void testCritDamageRandomInRange() {
        // Light = 1000, Dark = 400 -> Special Damage = 600 Light
        // Light Crit Rate = 100% (10000), Light Crit Damage Max = 200 (x2.00)
        // Each crit should random multiplier in [115, 200]
        Player p = new Player(null, 2);
        p.item = new Item(p);
        p.item.wear = new Item3[24];
        p.body = new Body(p);
        p.tutien = new int[5];
        p.kinhmach = new int[5];

        Item3 weapon = new Item3();
        weapon.op = new ArrayList<>();
        weapon.op.add(new Option(6, 1000));
        weapon.op.add(new Option(5, 400));
        weapon.op.add(new Option(74, 10000)); // 100% crit rate
        weapon.op.add(new Option(75, 200));   // max 2.00x
        p.item.wear[11] = weapon;

        boolean foundLow = false;
        boolean foundHigh = false;
        for (int i = 0; i < 1000; i++) {
            SpecialDamage sp = SpecialDamage.calculate(p);
            assertEquals(SpecialDamage.TYPE_LIGHT, sp.type);
            assertEquals(600, sp.rawDamage);
            assertTrue("Crit should always happen at 100% rate", sp.isCrit);
            // multiplier in [115, 200] => damage in [600*115/100, 600*200/100] = [690, 1200]
            assertTrue("Crit damage should be >= 690 (600*1.15)", sp.damage >= 690);
            assertTrue("Crit damage should be <= 1200 (600*2.00)", sp.damage <= 1200);
            if (sp.damage < 850) foundLow = true;
            if (sp.damage > 1100) foundHigh = true;
        }
        assertTrue("Crit damage should vary - found low values", foundLow);
        assertTrue("Crit damage should vary - found high values", foundHigh);
    }

    @Test
    public void testCritRateCapAndMaxCritDamage() {
        Player p = new Player(null, 3);
        p.item = new Item(p);
        p.item.wear = new Item3[24];
        p.body = new Body(p);
        p.tutien = new int[5];
        p.kinhmach = new int[5];

        // Multiple equipment pieces with Light stats
        Item3 weapon = new Item3();
        weapon.op = new ArrayList<>();
        weapon.op.add(new Option(6, 400));
        weapon.op.add(new Option(74, 6000)); // 60%
        weapon.op.add(new Option(75, 130));   // max 1.30x
        p.item.wear[11] = weapon;

        Item3 ring = new Item3();
        ring.op = new ArrayList<>();
        ring.op.add(new Option(6, 200));
        ring.op.add(new Option(74, 6000)); // 60% -> Total 120% -> Capped at 100%
        ring.op.add(new Option(75, 180));   // max 1.80x -> Max should be 180 (1.80x)
        p.item.wear[4] = ring;

        SpecialDamage sp = SpecialDamage.calculate(p);
        assertEquals(SpecialDamage.TYPE_LIGHT, sp.type);
        assertEquals(600, sp.rawDamage); // 400 + 200
        assertTrue(sp.isCrit);
        // multiplier in [115, 180] => damage in [600*115/100, 600*180/100] = [690, 1080]
        assertTrue("Crit damage should be >= 690 (600*1.15)", sp.damage >= 690);
        assertTrue("Crit damage should be <= 1080 (600*1.80)", sp.damage <= 1080);
    }

    @Test
    public void testDarkCritDamageRandomInRange() {
        // Dark = 1000, Light = 300 -> Special Damage = 700 Dark
        // Dark Crit Rate = 100% (10000), Dark Crit Damage Max = 200 (x2.00)
        Player p = new Player(null, 4);
        p.item = new Item(p);
        p.item.wear = new Item3[24];
        p.body = new Body(p);
        p.tutien = new int[5];
        p.kinhmach = new int[5];

        Item3 weapon = new Item3();
        weapon.op = new ArrayList<>();
        weapon.op.add(new Option(6, 300));
        weapon.op.add(new Option(5, 1000));
        weapon.op.add(new Option(72, 10000)); // 100% dark crit rate
        weapon.op.add(new Option(73, 200));   // max 2.00x
        p.item.wear[11] = weapon;

        boolean foundLow = false;
        boolean foundHigh = false;
        for (int i = 0; i < 1000; i++) {
            SpecialDamage sp = SpecialDamage.calculate(p);
            assertEquals(SpecialDamage.TYPE_DARK, sp.type);
            assertEquals(700, sp.rawDamage);
            assertTrue("Dark crit should always happen", sp.isCrit);
            // multiplier in [115, 200] => damage in [700*115/100, 700*200/100] = [805, 1400]
            assertTrue("Dark crit damage should be >= 805", sp.damage >= 805);
            assertTrue("Dark crit damage should be <= 1400", sp.damage <= 1400);
            if (sp.damage < 1000) foundLow = true;
            if (sp.damage > 1300) foundHigh = true;
        }
        assertTrue("Dark crit damage should vary - found low values", foundLow);
        assertTrue("Dark crit damage should vary - found high values", foundHigh);
    }

    @Test
    public void testGemCombinationGoldCosts() {
        // Light Gems (23..27) and Dark Gems (28..32)
        assertEquals(50_000, GameSrc.get_vang_hopngoc(23)); // Lv 1 -> 2
        assertEquals(100_000, GameSrc.get_vang_hopngoc(24)); // Lv 2 -> 3
        assertEquals(150_000, GameSrc.get_vang_hopngoc(25)); // Lv 3 -> 4
        assertEquals(200_000, GameSrc.get_vang_hopngoc(26)); // Lv 4 -> 5
        assertEquals(250_000, GameSrc.get_vang_hopngoc(27)); // Lv 5 (Max)

        assertEquals(50_000, GameSrc.get_vang_hopngoc(28)); // Lv 1 -> 2
        assertEquals(100_000, GameSrc.get_vang_hopngoc(29)); // Lv 2 -> 3
        assertEquals(150_000, GameSrc.get_vang_hopngoc(30)); // Lv 3 -> 4
        assertEquals(200_000, GameSrc.get_vang_hopngoc(31)); // Lv 4 -> 5
        assertEquals(250_000, GameSrc.get_vang_hopngoc(32)); // Lv 5 (Max)
    }
}
