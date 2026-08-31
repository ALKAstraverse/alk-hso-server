import org.junit.Test;
import static org.junit.Assert.*;

import client.Body;
import client.Item;
import client.Player;
import io.Message;
import template.Item3;
import template.Option;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.ArrayList;

public class EquipmentStatOutputTest {

    private static class ParsedOption {
        int id;
        int param;
        ParsedOption(int id, int param) {
            this.id = id;
            this.param = param;
        }
    }

    private ArrayList<ParsedOption> readOptionsFromMessage(Message m) throws IOException {
        byte[] bytes = m.getData();
        DataInputStream dis = new DataInputStream(new ByteArrayInputStream(bytes));
        int size = Byte.toUnsignedInt(dis.readByte());
        ArrayList<ParsedOption> list = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            int id = Byte.toUnsignedInt(dis.readByte());
            int param = dis.readInt();
            list.add(new ParsedOption(id, param));
        }
        return list;
    }

    private boolean containsOptionId(ArrayList<ParsedOption> list, int id) {
        for (ParsedOption opt : list) {
            if (opt.id == id) {
                return true;
            }
        }
        return false;
    }

    private int getOptionParam(ArrayList<ParsedOption> list, int id) {
        for (ParsedOption opt : list) {
            if (opt.id == id) {
                return opt.param;
            }
        }
        return -1;
    }

    @Test
    public void testWarriorStatOutput() throws IOException {
        // Chiến Binh (clazz = 0):
        // ✓ Lửa (2)
        // ✗ Băng (1), ✗ Điện (3), ✗ Độc (4)
        // ✓ Vật lý (0), ✓ Cơ bản (40), ✓ Phòng thủ (14)
        // ✓ Chính xác (-228/128), ✓ Bộc phá (-226/131), ✓ Light Damage (6), ✓ Dark Damage (5), ✓ Light Crit (74, 75)
        byte warriorClazz = 0;
        Item3 weapon = new Item3();
        weapon.tier = 0;
        weapon.op = new ArrayList<>();
        weapon.op.add(new Option(0, 500));   // Physical Damage
        weapon.op.add(new Option(2, 300));   // Fire Damage (KEEP)
        weapon.op.add(new Option(1, 100));   // Ice Damage (HIDE)
        weapon.op.add(new Option(3, 100));   // Electric Damage (HIDE)
        weapon.op.add(new Option(4, 100));   // Poison Damage (HIDE)
        weapon.op.add(new Option(8, 1000));  // Ice % (HIDE)
        weapon.op.add(new Option(9, 1000));  // Fire % (KEEP)
        weapon.op.add(new Option(10, 1000)); // Electric % (HIDE)
        weapon.op.add(new Option(11, 1000)); // Poison % (HIDE)
        weapon.op.add(new Option(5, 50));    // Dark Damage (KEEP)
        weapon.op.add(new Option(6, 60));    // Light Damage (KEEP)
        weapon.op.add(new Option(74, 5000)); // Light Crit Rate (KEEP)
        weapon.op.add(new Option(75, 150));  // Light Crit Dmg (KEEP)
        weapon.op.add(new Option(33, 10));   // Crit (KEEP)
        weapon.op.add(new Option(36, 15));   // Pierce (KEEP)
        weapon.op.add(new Option(40, 120));  // Base Damage (KEEP)
        weapon.op.add(new Option(128, 90));  // Chính xác (KEEP)
        weapon.op.add(new Option(131, 30));  // Bộc phá (KEEP)

        Message m = new Message(21);
        weapon.writeItemOptions(m, warriorClazz);

        ArrayList<ParsedOption> list = readOptionsFromMessage(m);

        // Verify non-class elemental damages are hidden
        assertFalse("Ice Damage (1) must be HIDDEN for Warrior", containsOptionId(list, 1));
        assertFalse("Electric Damage (3) must be HIDDEN for Warrior", containsOptionId(list, 3));
        assertFalse("Poison Damage (4) must be HIDDEN for Warrior", containsOptionId(list, 4));
        assertFalse("Ice % (8) must be HIDDEN for Warrior", containsOptionId(list, 8));
        assertFalse("Electric % (10) must be HIDDEN for Warrior", containsOptionId(list, 10));
        assertFalse("Poison % (11) must be HIDDEN for Warrior", containsOptionId(list, 11));

        // Verify class elemental damage and all other stats/effects are PRESERVED
        assertTrue("Fire Damage (2) must be KEPT for Warrior", containsOptionId(list, 2));
        assertEquals(300, getOptionParam(list, 2));

        assertTrue("Fire % (9) must be KEPT for Warrior", containsOptionId(list, 9));
        assertEquals(1000, getOptionParam(list, 9));

        assertTrue("Physical Damage (0) must be KEPT", containsOptionId(list, 0));
        assertEquals(500, getOptionParam(list, 0));

        assertTrue("Base Damage (40) must be KEPT", containsOptionId(list, 40));
        assertEquals(120, getOptionParam(list, 40));

        assertTrue("Dark Damage (5) must be KEPT", containsOptionId(list, 5));
        assertEquals(50, getOptionParam(list, 5));

        assertTrue("Light Damage (6) must be KEPT", containsOptionId(list, 6));
        assertEquals(60, getOptionParam(list, 6));

        assertTrue("Light Crit Rate (74) must be KEPT", containsOptionId(list, 74));
        assertTrue("Light Crit Dmg (75) must be KEPT", containsOptionId(list, 75));
        assertTrue("Crit (33) must be KEPT", containsOptionId(list, 33));
        assertTrue("Pierce (36) must be KEPT", containsOptionId(list, 36));
        assertTrue("Chính xác (128) must be KEPT", containsOptionId(list, 128));
        assertTrue("Bộc phá (131) must be KEPT", containsOptionId(list, 131));
    }

    @Test
    public void testGunnerStatOutput() throws IOException {
        // Xạ Thủ (clazz = 3):
        // ✓ Điện (3)
        // ✗ Lửa (2), ✗ Băng (1), ✗ Độc (4)
        // ✓ Chính xác, ✓ Bộc phá, ✓ Light/Dark
        byte gunnerClazz = 3;
        Item3 weapon = new Item3();
        weapon.tier = 0;
        weapon.op = new ArrayList<>();
        weapon.op.add(new Option(0, 400));   // Physical Damage
        weapon.op.add(new Option(3, 250));   // Electric Damage (KEEP)
        weapon.op.add(new Option(2, 150));   // Fire Damage (HIDE)
        weapon.op.add(new Option(1, 100));   // Ice Damage (HIDE)
        weapon.op.add(new Option(4, 50));    // Poison Damage (HIDE)
        weapon.op.add(new Option(6, 70));    // Light Damage (KEEP)
        weapon.op.add(new Option(128, 90));  // Chính xác (KEEP)
        weapon.op.add(new Option(131, 20));  // Bộc phá (KEEP)

        Message m = new Message(21);
        weapon.writeItemOptions(m, gunnerClazz);

        ArrayList<ParsedOption> list = readOptionsFromMessage(m);

        assertFalse("Fire Damage (2) must be HIDDEN for Gunner", containsOptionId(list, 2));
        assertFalse("Ice Damage (1) must be HIDDEN for Gunner", containsOptionId(list, 1));
        assertFalse("Poison Damage (4) must be HIDDEN for Gunner", containsOptionId(list, 4));

        assertTrue("Electric Damage (3) must be KEPT for Gunner", containsOptionId(list, 3));
        assertEquals(250, getOptionParam(list, 3));
        assertTrue("Physical Damage (0) must be KEPT", containsOptionId(list, 0));
        assertTrue("Light Damage (6) must be KEPT", containsOptionId(list, 6));
        assertTrue("Chính xác (128) must be KEPT", containsOptionId(list, 128));
        assertTrue("Bộc phá (131) must be KEPT", containsOptionId(list, 131));
    }

    @Test
    public void testMageStatOutput() throws IOException {
        // Pháp Sư (clazz = 2):
        // ✓ Băng (1)
        // ✗ Lửa (2), ✗ Điện (3), ✗ Độc (4)
        // ✓ Chính xác, ✓ Bộc phá, ✓ Light/Dark
        byte mageClazz = 2;
        Item3 weapon = new Item3();
        weapon.tier = 0;
        weapon.op = new ArrayList<>();
        weapon.op.add(new Option(0, 200));   // Physical Damage
        weapon.op.add(new Option(1, 350));   // Ice Damage (KEEP)
        weapon.op.add(new Option(2, 100));   // Fire Damage (HIDE)
        weapon.op.add(new Option(3, 100));   // Electric Damage (HIDE)
        weapon.op.add(new Option(4, 50));    // Poison Damage (HIDE)
        weapon.op.add(new Option(5, 80));    // Dark Damage (KEEP)
        weapon.op.add(new Option(128, 90));  // Chính xác (KEEP)
        weapon.op.add(new Option(131, 20));  // Bộc phá (KEEP)

        Message m = new Message(21);
        weapon.writeItemOptions(m, mageClazz);

        ArrayList<ParsedOption> list = readOptionsFromMessage(m);

        assertFalse("Fire Damage (2) must be HIDDEN for Mage", containsOptionId(list, 2));
        assertFalse("Electric Damage (3) must be HIDDEN for Mage", containsOptionId(list, 3));
        assertFalse("Poison Damage (4) must be HIDDEN for Mage", containsOptionId(list, 4));

        assertTrue("Ice Damage (1) must be KEPT for Mage", containsOptionId(list, 1));
        assertEquals(350, getOptionParam(list, 1));
        assertTrue("Physical Damage (0) must be KEPT", containsOptionId(list, 0));
        assertTrue("Dark Damage (5) must be KEPT", containsOptionId(list, 5));
        assertTrue("Chính xác (128) must be KEPT", containsOptionId(list, 128));
        assertTrue("Bộc phá (131) must be KEPT", containsOptionId(list, 131));
    }

    @Test
    public void testAssassinStatOutput() throws IOException {
        // Sát Thủ (clazz = 1):
        // ✓ Độc (4)
        // ✗ Lửa (2), ✗ Băng (1), ✗ Điện (3)
        // ✓ Chính xác, ✓ Bộc phá, ✓ Light/Dark
        byte assassinClazz = 1;
        Item3 weapon = new Item3();
        weapon.tier = 0;
        weapon.op = new ArrayList<>();
        weapon.op.add(new Option(0, 450));   // Physical Damage
        weapon.op.add(new Option(4, 320));   // Poison Damage (KEEP)
        weapon.op.add(new Option(1, 100));   // Ice Damage (HIDE)
        weapon.op.add(new Option(2, 100));   // Fire Damage (HIDE)
        weapon.op.add(new Option(3, 100));   // Electric Damage (HIDE)
        weapon.op.add(new Option(6, 90));    // Light Damage (KEEP)
        weapon.op.add(new Option(128, 90));  // Chính xác (KEEP)
        weapon.op.add(new Option(131, 20));  // Bộc phá (KEEP)

        Message m = new Message(21);
        weapon.writeItemOptions(m, assassinClazz);

        ArrayList<ParsedOption> list = readOptionsFromMessage(m);

        assertFalse("Fire Damage (2) must be HIDDEN for Assassin", containsOptionId(list, 2));
        assertFalse("Ice Damage (1) must be HIDDEN for Assassin", containsOptionId(list, 1));
        assertFalse("Electric Damage (3) must be HIDDEN for Assassin", containsOptionId(list, 3));

        assertTrue("Poison Damage (4) must be KEPT for Assassin", containsOptionId(list, 4));
        assertEquals(320, getOptionParam(list, 4));
        assertTrue("Physical Damage (0) must be KEPT", containsOptionId(list, 0));
        assertTrue("Light Damage (6) must be KEPT", containsOptionId(list, 6));
        assertTrue("Chính xác (128) must be KEPT", containsOptionId(list, 128));
        assertTrue("Bộc phá (131) must be KEPT", containsOptionId(list, 131));
    }

    @Test
    public void testArmorStatOutputPreservesAllArmorStats() throws IOException {
        byte warriorClazz = 0;
        Item3 armor = new Item3();
        armor.tier = 0;
        armor.op = new ArrayList<>();
        armor.op.add(new Option(14, 350));  // Defense
        armor.op.add(new Option(16, 50));   // Kháng vật lý
        armor.op.add(new Option(17, 50));   // Kháng băng
        armor.op.add(new Option(18, 50));   // Kháng lửa
        armor.op.add(new Option(27, 1000)); // + Máu
        armor.op.add(new Option(23, 15));   // + Sức mạnh
        armor.op.add(new Option(1, 20));    // Ice Damage on armor (HIDE for warrior)
        armor.op.add(new Option(2, 25));    // Fire Damage on armor (KEEP for warrior)

        Message m = new Message(21);
        armor.writeItemOptions(m, warriorClazz);

        ArrayList<ParsedOption> list = readOptionsFromMessage(m);

        assertFalse("Ice Damage (1) on armor must be HIDDEN for warrior", containsOptionId(list, 1));
        assertTrue("Fire Damage (2) on armor must be KEPT for warrior", containsOptionId(list, 2));
        assertTrue("Defense (14) must be KEPT", containsOptionId(list, 14));
        assertTrue("Kháng vật lý (16) must be KEPT", containsOptionId(list, 16));
        assertTrue("Kháng băng (17) must be KEPT", containsOptionId(list, 17));
        assertTrue("Kháng lửa (18) must be KEPT", containsOptionId(list, 18));
        assertTrue("+ Máu (27) must be KEPT", containsOptionId(list, 27));
        assertTrue("+ Sức mạnh (23) must be KEPT", containsOptionId(list, 23));
    }

    @Test
    public void testTuTienCharacterElementalDamageIsolation() {
        // Create a Warrior (clazz = 0) with Tu Tiên level 5
        Player warrior = new Player(null, 1);
        warrior.clazz = 0;
        warrior.item = new Item(warrior);
        warrior.item.wear = new Item3[24];
        warrior.list_eff = new ArrayList<>();
        warrior.mypet = new ArrayList<>();
        warrior.body = new Body(warrior);
        warrior.tutien = new int[]{5, 1, 100};
        warrior.kinhmach = new int[5];
        warrior.luyenthe = 10;
        warrior.skill_point = new byte[21];
        warrior.skills = new template.Skill[21];
        for (int i = 0; i < 21; i++) {
            warrior.skills[i] = new template.Skill();
            warrior.skills[i].mLvSkill = new template.LvSkill[0];
        }

        Item3 weapon = new Item3();
        weapon.op = new ArrayList<>();
        weapon.op.add(new Option(0, 1000)); // Physical
        weapon.op.add(new Option(2, 500));  // Fire
        weapon.tier = 0;
        warrior.item.wear[11] = weapon;

        // Warrior should only have Fire damage
        int fireDmg = warrior.body.get_dame_prop(2);
        assertTrue("Fire damage should be > 0", fireDmg > 0);

        int iceDmg = warrior.body.get_dame_prop(1);
        assertEquals("Ice damage for Warrior must be 0 even after Tu Tiên", 0, iceDmg);

        int elecDmg = warrior.body.get_dame_prop(3);
        assertEquals("Electric damage for Warrior must be 0 even after Tu Tiên", 0, elecDmg);

        int poisonDmg = warrior.body.get_dame_prop(4);
        assertEquals("Poison damage for Warrior must be 0 even after Tu Tiên", 0, poisonDmg);
    }

    @Test
    public void testCombatStillUsesAllStats() {
        // Verify that combat still uses all stats (Light/Dark gems, Crit, Pierce, etc.)
        Player attacker = new Player(null, 1);
        attacker.clazz = 0;
        attacker.item = new Item(attacker);
        attacker.item.wear = new Item3[24];
        attacker.list_eff = new ArrayList<>();
        attacker.mypet = new ArrayList<>();
        attacker.body = new Body(attacker);
        attacker.tutien = new int[5];
        attacker.kinhmach = new int[5];
        attacker.skill_point = new byte[21];
        attacker.skills = new template.Skill[21];
        for (int i = 0; i < 21; i++) {
            attacker.skills[i] = new template.Skill();
            attacker.skills[i].mLvSkill = new template.LvSkill[0];
        }

        Item3 weapon = new Item3();
        weapon.tier = 0;
        weapon.op = new ArrayList<>();
        weapon.op.add(new Option(6, 500));   // Light Dmg 500
        weapon.op.add(new Option(74, 5000)); // Light Crit Rate 50%
        weapon.op.add(new Option(75, 150));  // Light Crit Dmg 1.5x
        weapon.op.add(new Option(33, 20));   // Crit 20
        weapon.op.add(new Option(36, 15));   // Pierce 15
        attacker.item.wear[11] = weapon;

        // Verify underlying combat stats are preserved
        assertEquals(500, attacker.body.total_item_param(6));
        assertEquals(5000, attacker.body.total_item_param(74));
        assertEquals(150, attacker.body.get_max_crit_damage_param(75));
        assertEquals(20, attacker.body.total_item_param(33));
        assertEquals(15, attacker.body.total_item_param(36));
    }
}
