import org.junit.Test;
import static org.junit.Assert.*;

import map.LeaveItemMap;
import map.Map;
import map.Mob_in_map;
import client.Player;
import core.GameSrc;

public class LightDarkGemDropTest {

    @Test
    public void testConstantsAndItemIds() {
        assertEquals("Light Gem Lv1 ID must be 23", 23, LeaveItemMap.ITEM_LIGHT_GEM_LV1);
        assertEquals("Light Gem Lv2 ID must be 24", 24, LeaveItemMap.ITEM_LIGHT_GEM_LV2);
        assertEquals("Dark Gem Lv1 ID must be 28", 28, LeaveItemMap.ITEM_DARK_GEM_LV1);
        assertEquals("Dark Gem Lv2 ID must be 29", 29, LeaveItemMap.ITEM_DARK_GEM_LV2);

        // Rates
        assertEquals("Normal Lv1 rate should be 5% (500/10000)", 500, LeaveItemMap.NORMAL_LIGHT_DARK_GEM_LV1_RATE);
        assertEquals("Normal Lv2 rate must be 0%", 0, LeaveItemMap.NORMAL_LIGHT_DARK_GEM_LV2_RATE);
        assertTrue("Zone 2 Lv1 rate should be greater than normal Lv1 rate",
                LeaveItemMap.ZONE2_LIGHT_DARK_GEM_LV1_RATE > LeaveItemMap.NORMAL_LIGHT_DARK_GEM_LV1_RATE);
        assertTrue("Zone 2 Lv2 rate should be 1-2% (100 to 200 / 10000)",
                LeaveItemMap.ZONE2_LIGHT_DARK_GEM_LV2_RATE >= 100 && LeaveItemMap.ZONE2_LIGHT_DARK_GEM_LV2_RATE <= 200);
    }

    @Test
    public void testGemCombinationPreserved() {
        // Test combining formulas 5 Lv1 -> 1 Lv2 ... 5 Lv4 -> 1 Lv5
        // Light gem combination cost
        assertEquals(50_000, GameSrc.get_vang_hopngoc(LeaveItemMap.ITEM_LIGHT_GEM_LV1));
        assertEquals(100_000, GameSrc.get_vang_hopngoc(LeaveItemMap.ITEM_LIGHT_GEM_LV2));
        assertEquals(150_000, GameSrc.get_vang_hopngoc(25));
        assertEquals(200_000, GameSrc.get_vang_hopngoc(26));
        assertEquals(250_000, GameSrc.get_vang_hopngoc(27));

        // Dark gem combination cost
        assertEquals(50_000, GameSrc.get_vang_hopngoc(LeaveItemMap.ITEM_DARK_GEM_LV1));
        assertEquals(100_000, GameSrc.get_vang_hopngoc(LeaveItemMap.ITEM_DARK_GEM_LV2));
        assertEquals(150_000, GameSrc.get_vang_hopngoc(30));
        assertEquals(200_000, GameSrc.get_vang_hopngoc(31));
        assertEquals(250_000, GameSrc.get_vang_hopngoc(32));
    }

    @Test
    public void testDropSimulationNormalZone() {
        // Simulation of roll logic in normal zone
        int totalRolls = 100_000;
        int lv1Count = 0;
        int lv2Count = 0;
        int lightLv1Count = 0;
        int darkLv1Count = 0;

        for (int i = 0; i < totalRolls; i++) {
            int roll = core.Util.random(0, 10000);
            int rateLv2 = LeaveItemMap.NORMAL_LIGHT_DARK_GEM_LV2_RATE; // 0
            int rateLv1 = LeaveItemMap.NORMAL_LIGHT_DARK_GEM_LV1_RATE; // 500

            short gemToDrop = -1;
            if (rateLv2 > 0 && roll < rateLv2) {
                gemToDrop = (core.Util.random(0, 2) == 0) ? LeaveItemMap.ITEM_LIGHT_GEM_LV2 : LeaveItemMap.ITEM_DARK_GEM_LV2;
            } else if (roll < rateLv2 + rateLv1) {
                gemToDrop = (core.Util.random(0, 2) == 0) ? LeaveItemMap.ITEM_LIGHT_GEM_LV1 : LeaveItemMap.ITEM_DARK_GEM_LV1;
            }

            if (gemToDrop == LeaveItemMap.ITEM_LIGHT_GEM_LV2 || gemToDrop == LeaveItemMap.ITEM_DARK_GEM_LV2) {
                lv2Count++;
            } else if (gemToDrop == LeaveItemMap.ITEM_LIGHT_GEM_LV1) {
                lv1Count++;
                lightLv1Count++;
            } else if (gemToDrop == LeaveItemMap.ITEM_DARK_GEM_LV1) {
                lv1Count++;
                darkLv1Count++;
            }
        }

        assertEquals("Normal zone must drop 0 Lv2 gems", 0, lv2Count);
        double lv1Rate = (double) lv1Count / totalRolls;
        // ~5% rate (allow 4.5% to 5.5%)
        assertTrue("Lv1 drop rate in normal zone should be ~5%", lv1Rate >= 0.045 && lv1Rate <= 0.055);
        assertTrue("Both Light and Dark Lv1 should drop", lightLv1Count > 0 && darkLv1Count > 0);
    }

    @Test
    public void testDropSimulationZone2() {
        // Simulation of roll logic in Zone 2
        int totalRolls = 100_000;
        int lv1Count = 0;
        int lv2Count = 0;
        int lightLv2Count = 0;
        int darkLv2Count = 0;

        for (int i = 0; i < totalRolls; i++) {
            int roll = core.Util.random(0, 10000);
            int rateLv2 = LeaveItemMap.ZONE2_LIGHT_DARK_GEM_LV2_RATE; // 150 (1.5%)
            int rateLv1 = LeaveItemMap.ZONE2_LIGHT_DARK_GEM_LV1_RATE; // 1000 (10.0%)

            short gemToDrop = -1;
            if (rateLv2 > 0 && roll < rateLv2) {
                gemToDrop = (core.Util.random(0, 2) == 0) ? LeaveItemMap.ITEM_LIGHT_GEM_LV2 : LeaveItemMap.ITEM_DARK_GEM_LV2;
            } else if (roll < rateLv2 + rateLv1) {
                gemToDrop = (core.Util.random(0, 2) == 0) ? LeaveItemMap.ITEM_LIGHT_GEM_LV1 : LeaveItemMap.ITEM_DARK_GEM_LV1;
            }

            if (gemToDrop == LeaveItemMap.ITEM_LIGHT_GEM_LV2) {
                lv2Count++;
                lightLv2Count++;
            } else if (gemToDrop == LeaveItemMap.ITEM_DARK_GEM_LV2) {
                lv2Count++;
                darkLv2Count++;
            } else if (gemToDrop == LeaveItemMap.ITEM_LIGHT_GEM_LV1 || gemToDrop == LeaveItemMap.ITEM_DARK_GEM_LV1) {
                lv1Count++;
            }
        }

        double lv1Rate = (double) lv1Count / totalRolls;
        double lv2Rate = (double) lv2Count / totalRolls;

        // Lv1 should be ~10% (higher than normal 5%)
        assertTrue("Lv1 drop rate in Zone 2 should be ~10%", lv1Rate >= 0.09 && lv1Rate <= 0.11);
        // Lv2 should be ~1.5% (between 1% and 2%)
        assertTrue("Lv2 drop rate in Zone 2 should be ~1.5% (between 1.2% and 1.8%)", lv2Rate >= 0.012 && lv2Rate <= 0.018);
        assertTrue("Both Light and Dark Lv2 should drop in Zone 2", lightLv2Count > 0 && darkLv2Count > 0);
    }
}
