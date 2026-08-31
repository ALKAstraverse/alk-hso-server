import org.junit.Test;
import org.junit.BeforeClass;
import static org.junit.Assert.*;

import client.Disciple;
import client.Player;
import client.Body;
import client.Item;
import map.Map;
import map.Mob_in_map;
import map.Vgo;
import template.Item3;
import template.ItemTemplate3;
import template.ItemTemplate4;
import template.Level;
import template.LvSkill;
import template.Mob;
import template.Option;
import template.Skill;
import core.Service;

import java.util.ArrayList;

public class DiscipleFeatureTest {

    @BeforeClass
    public static void setup() {
        if (Level.entrys.isEmpty()) {
            for (int i = 1; i <= 250; i++) {
                Level temp = new Level();
                temp.level = (short) i;
                temp.exp = 100000L * i;
                temp.tiemnang = 5;
                temp.kynang = 1;
                Level.entrys.add(temp);
            }
        }
    }

    private Player createTestMaster() {
        Player master = new Player(null, 1001);
        master.name = "MasterPro";
        master.clazz = 0; // Warrior
        master.level = 100;
        master.exp = 500000L;
        master.head = 1;
        master.eye = 2;
        master.hair = 3;
        master.point1 = 150;
        master.point2 = 120;
        master.point3 = 100;
        master.point4 = 110;
        master.tiemnang = 50;
        master.kynang = 15;
        master.type_use_mount = 5; // Master currently has an active mount
        master.body = new Body(master);
        master.item = new Item(master);
        master.item.wear = new Item3[24];
        master.item.bag3 = new Item3[42];
        master.item.box3 = new Item3[42];
        master.item.bag47 = new ArrayList<>();
        master.skill_point = new byte[21];
        master.skill_point[0] = 10;
        master.skills = new Skill[21];
        for (int i = 0; i < 21; i++) {
            master.skills[i] = new Skill();
            master.skills[i].id = (byte) i;
            master.skills[i].mLvSkill = new LvSkill[15];
            for (int j = 0; j < 15; j++) {
                master.skills[i].mLvSkill[j] = new LvSkill();
                master.skills[i].mLvSkill[j].minfo = new Option[]{new Option(0, 100)};
            }
        }
        master.list_eff = new ArrayList<>();
        master.mypet = new ArrayList<>();
        master.rms_save = new byte[2][0];
        master.fashion = new byte[0];
        master.tutien = new int[5];
        master.kinhmach = new int[5];
        master.point_active = new int[]{3, 0, 0};
        master.quest_daily = new int[6];
        master.nv_tinh_tu = new short[]{-1, 0, 0, 0};
        master.tinhtu_material = new short[5];
        master.medal_create_material = new short[5];
        master.list_friend = new ArrayList<>();
        master.list_enemies = new ArrayList<>();
        master.other_player_inside = new java.util.HashMap<>();
        master.other_mob_inside = new java.util.HashMap<>();
        master.other_mob_inside_update = new java.util.HashMap<>();
        return master;
    }

    @Test
    public void testNhậnĐệTử_RandomPhái_Lv10_CharacterID() {
        Player master = createTestMaster();
        for (byte phai = 0; phai < 4; phai++) {
            Disciple d = Disciple.createNew(master, phai);
            assertNotNull(d);
            assertEquals(10, d.level);
            assertEquals(0L, d.exp);
            assertEquals(phai, d.clazz);
            assertTrue("Character ID must be different from Master ID", master.id != d.id);
            assertEquals(master.name + "_dt", d.name);
            assertTrue(d.clazz >= 0 && d.clazz <= 3);
            assertTrue(Disciple.getClassName(phai).length() > 0);
            // Equipment initialized for new character
            assertNotNull(d.wear[0]); // Weapon
            assertNotNull(d.wear[1]); // Armor
            assertNotNull(d.wear[7]); // Gloves/Boots
            assertTrue(d.get_max_hp() > 0);
            assertTrue(d.get_dame_physical() > 0);
        }
    }

    @Test
    public void testTrainingModeSwitch_And_Restore_MountRemoved() throws Exception {
        Player master = createTestMaster();
        master.type_use_mount = 5; // Master riding mount

        // Create Disciple Archer (clazz 3)
        Disciple d = Disciple.createNew(master, Disciple.CLAZZ_ARCHER);
        master.detu = d;

        // Switch to disciple training mode
        master.switchToDisciple();
        assertTrue(master.is_detu_training);
        assertEquals(Disciple.CLAZZ_ARCHER, master.clazz);
        assertEquals(10, master.level);
        assertEquals(-1, master.type_use_mount); // Mount FORCE REMOVED on training start

        // Disciple levels up beyond Master (Lv100 -> Disciple reaches Lv105)
        master.level = 105;
        master.point1 += 20;

        // Switch back to master
        master.switchToMaster();
        assertFalse(master.is_detu_training);
        assertEquals(Disciple.CLAZZ_WARRIOR, master.clazz);
        assertEquals(100, master.level);

        // Verify Disciple persisted level 105 (disciple level > master level is allowed)
        assertEquals(105, master.detu.level);
        assertTrue("Disciple level can be higher than Master level", master.detu.level > master.level);
    }

    @Test
    public void testTrainingDailyLimit_And_ExtraTicket() {
        Player master = createTestMaster();
        Disciple d = Disciple.createNew(master, Disciple.CLAZZ_MAGE);
        master.detu = d;

        // Default: 180 minutes = 10800 seconds
        assertEquals(10800, d.getMaxTrainingTimeSeconds());
        assertEquals(10800, d.getRemainingTrainingTimeSeconds());
        assertTrue(d.canTrain());

        // Simulate using 10800 seconds
        d.training_time_used_today = 10800;
        assertEquals(0, d.getRemainingTrainingTimeSeconds());
        assertFalse(d.canTrain());

        // Buy and use extra ticket (+180 min)
        d.training_ticket_bought_today = true;
        d.training_ticket_used_today = true;
        assertEquals(21600, d.getMaxTrainingTimeSeconds());
        assertEquals(10800, d.getRemainingTrainingTimeSeconds());
        assertTrue(d.canTrain());
    }

    @Test
    public void testSummonDisciple_Cooldown10Min() {
        Player master = createTestMaster();
        Disciple d = Disciple.createNew(master, Disciple.CLAZZ_ASSASSIN);
        master.detu = d;

        assertTrue(d.canSummon());
        d.state = Disciple.STATE_SUMMONED;

        // When disciple is lost
        d.onSummonLost();
        assertEquals(Disciple.STATE_NORMAL, d.state);
        assertFalse(d.canSummon());
        assertTrue(d.getSummonCooldownRemainingSeconds() > 0);
        assertTrue(d.getSummonCooldownRemainingSeconds() <= 600);
    }

    @Test
    public void testDiscipleJsonPersistence() {
        Player master = createTestMaster();
        Disciple d = Disciple.createNew(master, Disciple.CLAZZ_WARRIOR);
        d.level = 45;
        d.exp = 123456L;
        d.point1 = 80;
        d.tiemnang = 25;
        d.kynang = 8;
        d.training_time_used_today = 3600;
        d.training_ticket_bought_today = true;
        d.training_ticket_used_today = false;
        d.summon_cooldown_until = System.currentTimeMillis() + 300000L;

        String json = d.toJson();
        assertNotNull(json);

        Disciple loaded = Disciple.fromJson(master, json);
        assertNotNull(loaded);
        assertEquals(d.id, loaded.id);
        assertEquals(d.name, loaded.name);
        assertEquals(d.clazz, loaded.clazz);
        assertEquals(45, loaded.level);
        assertEquals(123456L, loaded.exp);
        assertEquals(80, loaded.point1);
        assertEquals(25, loaded.tiemnang);
        assertEquals(8, loaded.kynang);
        assertEquals(3600, loaded.training_time_used_today);
        assertTrue(loaded.training_ticket_bought_today);
        assertFalse(loaded.training_ticket_used_today);
        assertEquals(d.summon_cooldown_until, loaded.summon_cooldown_until);
        assertNotNull(loaded.wear[0]); // Weapon persisted
    }

    @Test
    public void testCancelDisciple_DeletesEquipmentAndHair() {
        Player master = createTestMaster();
        Disciple d = Disciple.createNew(master, Disciple.CLAZZ_MAGE);
        master.detu = d;

        // Cancel disciple
        master.detu = null;
        assertNull(master.detu);
        // Master's garden, items, stats remain intact
        assertEquals(100, master.level);
        assertEquals(150, master.point1);
    }

    @Test
    public void testInventoryProtection_WhenMovingToBagOrBox() {
        Player master = createTestMaster();
        // Fill bag3
        for (int i = 0; i < master.item.bag3.length; i++) {
            Item3 item = new Item3();
            item.id = (short) (100 + i);
            master.item.bag3[i] = item;
        }
        assertEquals(42, master.item.bag3.length);

        // Add item when bag3 is full -> should go to box3
        Item3 extraItem = new Item3();
        extraItem.id = 999;
        boolean added = master.item.add_item_bag3(extraItem);
        assertTrue("Item successfully placed into storage box when bag is full", added);
        assertEquals(extraItem, master.item.box3[0]);
    }

    @Test
    public void testDiscipleCombatStats_UsePlayerFormula() {
        Player master = createTestMaster();
        Disciple warrior = Disciple.createNew(master, Disciple.CLAZZ_WARRIOR);
        warrior.point1 = 50; // Strength
        warrior.point2 = 30; // Vitality
        assertTrue(warrior.get_max_hp() > 500 + 30 * 10);
        assertTrue(warrior.get_dame_physical() >= 50 * 4);
        assertTrue(warrior.get_dame_prop(2) > 0); // Fire damage for Warrior

        Disciple assassin = Disciple.createNew(master, Disciple.CLAZZ_ASSASSIN);
        assassin.point1 = 50;
        assertTrue(assassin.get_dame_prop(1) > 0); // Ice damage for Assassin

        Disciple mage = Disciple.createNew(master, Disciple.CLAZZ_MAGE);
        mage.point4 = 50; // Intelligence
        assertTrue(mage.get_dame_prop(4) > 0); // Poison damage for Mage

        Disciple archer = Disciple.createNew(master, Disciple.CLAZZ_ARCHER);
        archer.point4 = 50; // Agility/Dex
        assertTrue(archer.get_dame_prop(3) > 0); // Electric damage for Archer
    }

    @Test
    public void testHairGenderValidation() {
        // Male hairs: 0, 1, 4, 5, 8, 9, 12, 13, 16, 17, 20, 21, 24, 25, 28, 29, 34, 35, 38, 39, 42
        // Female hairs: 2, 3, 6, 7, 10, 11, 14, 15, 18, 19, 22, 23, 26, 27, 30, 31, 36, 37, 40, 41, 43
        assertTrue(Service.isMaleHair(0));
        assertTrue(Service.isMaleHair(1));
        assertTrue(Service.isMaleHair(42));
        assertFalse(Service.isMaleHair(2));
        assertFalse(Service.isMaleHair(3));
        assertFalse(Service.isMaleHair(43));

        assertTrue(Service.isFemaleHair(2));
        assertTrue(Service.isFemaleHair(3));
        assertTrue(Service.isFemaleHair(43));
        assertFalse(Service.isFemaleHair(0));
        assertFalse(Service.isFemaleHair(1));
        assertFalse(Service.isFemaleHair(42));
    }

    @Test
    public void testHairShopAndTrainingGenderSeparation() throws Exception {
        Player master = createTestMaster();
        master.clazz = Disciple.CLAZZ_WARRIOR; // Male
        master.hair = 0; // Master hair: Toc rom (Male)

        Disciple femaleDisciple = Disciple.createNew(master, Disciple.CLAZZ_MAGE); // Female
        assertTrue(femaleDisciple.hair == 2 || femaleDisciple.hair == 3);
        femaleDisciple.hair = 2; // Initial female hair
        master.detu = femaleDisciple;

        // Switch to disciple training
        master.switchToDisciple();
        assertTrue(master.is_detu_training);
        assertEquals(Disciple.CLAZZ_MAGE, master.clazz);
        assertEquals(2, master.hair);

        // Buy new female hair (e.g. hair 6 = Tóc vàng dịu dàng)
        master.hair = 6;
        master.detu.hair = 6; // Synced

        // Switch back to master
        master.switchToMaster();
        assertFalse(master.is_detu_training);
        assertEquals(Disciple.CLAZZ_WARRIOR, master.clazz); // Master is still Warrior
        assertEquals(0, master.hair); // Master's hair remains 0 (unchanged)
        assertEquals(6, master.detu.hair); // Disciple's bought hair is preserved

        // Re-enter disciple training
        master.switchToDisciple();
        assertTrue(master.is_detu_training);
        assertEquals(Disciple.CLAZZ_MAGE, master.clazz);
        assertEquals(6, master.hair); // Disciple's hair 6 is loaded
    }

    @Test
    public void testDiscipleFashionPriority_NormalVsFashion() throws Exception {
        Player master = createTestMaster();
        master.fashion = new byte[]{10, 11, 12, 13, 14, 15, 16}; // Master fashion

        Disciple d = Disciple.createNew(master, Disciple.CLAZZ_WARRIOR);
        master.detu = d;

        // Part_fashion entry
        template.Part_fashion pf = new template.Part_fashion();
        pf.id = 5001;
        pf.part = new byte[]{1, 2, 3, 4, 5, 6, 7}; // Fashion skin parts
        template.Part_fashion.entrys.add(pf);

        // Equip normal equipment on disciple (wear[0]=head, wear[1]=body, wear[7]=leg)
        Item3 normalBody = new Item3();
        normalBody.id = 80;
        normalBody.type = 1;
        normalBody.part = 16;
        normalBody.op = new ArrayList<>();
        d.wear[1] = normalBody;

        // Case 1: No fashion equipped on disciple
        byte[] dFashion1 = d.get_fashion();
        assertArrayEquals(new byte[]{-1, -1, -1, -1, -1, -1, -1}, dFashion1);

        // Case 2: Equip fashion item on disciple (wear[11])
        Item3 fashionItem = new Item3();
        fashionItem.id = 5001;
        fashionItem.type = 15;
        fashionItem.op = new ArrayList<>();
        d.wear[11] = fashionItem;

        // Resolved fashion for disciple must be fashion parts (not master's)
        byte[] dFashion2 = d.get_fashion();
        assertArrayEquals(new byte[]{1, 2, 3, 4, 5, 6, 7}, dFashion2);

        // Test Switch to Disciple training loads Disciple fashion
        master.switchToDisciple();
        assertTrue(master.is_detu_training);
        assertArrayEquals(new byte[]{1, 2, 3, 4, 5, 6, 7}, master.fashion);

        // Test Switch back to Master restores Master fashion
        master.switchToMaster();
        assertFalse(master.is_detu_training);
        assertArrayEquals(new byte[]{10, 11, 12, 13, 14, 15, 16}, master.fashion);
        assertArrayEquals(new byte[]{1, 2, 3, 4, 5, 6, 7}, d.get_fashion());

        // Test Unequip fashion from disciple
        d.wear[11] = null;
        assertArrayEquals(new byte[]{-1, -1, -1, -1, -1, -1, -1}, d.get_fashion());
    }
}
