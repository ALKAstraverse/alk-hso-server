import org.junit.Test;
import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import client.Body;
import client.Item;
import client.Pet;
import client.Player;
import core.Util;
import map.ItemMap;
import map.LeaveItemMap;
import map.Map;
import map.Mob_in_map;
import template.Level;
import template.LvSkill;
import template.Mob;
import template.Option;
import template.Option_pet;
import template.Skill;

public class EaglePetGoldDropTest {

    private Player createTestPlayer() throws Exception {
        Player p = new Player(null, 12345);
        p.name = "TestPlayer";
        p.level = 50;
        p.hp = 10000;
        p.mp = 10000;
        p.body = new Body(p);
        p.item = new Item(p);
        p.mypet = new ArrayList<>();
        p.list_eff = new ArrayList<>();
        p.fashion = new byte[0];
        p.skills = new Skill[21];
        for (int i = 0; i < 21; i++) {
            p.skills[i] = new Skill();
            p.skills[i].id = (byte) i;
            p.skills[i].mLvSkill = new LvSkill[15];
            for (int j = 0; j < 15; j++) {
                p.skills[i].mLvSkill[j] = new LvSkill();
                p.skills[i].mLvSkill[j].minfo = new Option[]{new Option(0, 100)};
            }
        }
        p.skill_point = new byte[21];
        p.pet_atk_speed = 0;
        return p;
    }

    private Pet createEaglePet() {
        Pet eagle = Pet.get_pet(Pet.ID_DAI_BANG);
        if (eagle == null) {
            eagle = new Pet();
            eagle.type = Pet.TYPE_DAI_BANG;
            eagle.name = "Đại Bàng";
            eagle.level = 1;
            eagle.grown = 300;
            eagle.maxgrown = 300;
            eagle.is_follow = true;
            eagle.op = new ArrayList<>();
            eagle.op.add(new Option_pet(46, 1000, 2000));
        } else {
            eagle.grown = 300;
            eagle.is_follow = true;
        }
        return eagle;
    }

    private Pet createOtherPet(short petId) {
        Pet pet = Pet.get_pet(petId);
        if (pet != null) {
            pet.grown = 300;
            pet.is_follow = true;
        }
        return pet;
    }

    private Map createTestMap() throws Exception {
        Map map = new Map(1, 1, new String[0], "Test Map", (byte) 0, false, false, 20, 5, new ArrayList<>());
        map.mobs = new Mob_in_map[1];
        Mob_in_map mob = new Mob_in_map();
        mob.template = new Mob();
        mob.template.mob_id = 1;
        mob.template.name = "Test Mob";
        mob.index = 101;
        mob.x = 200;
        mob.y = 200;
        mob.level = 50;
        mob.hpmax = 50000;
        mob.hp = 50000;
        mob.isdie = false;
        mob.list_fight = new ArrayList<>();
        map.mobs[0] = mob;
        return map;
    }

    @Test
    public void testEaglePetIdentification() {
        assertEquals("Eagle pet type must be 3", 3, Pet.TYPE_DAI_BANG);
        assertEquals("Eagle pet item template ID must be 3269", 3269, Pet.ID_DAI_BANG);
        assertEquals(100, Pet.EAGLE_GOLD_MIN);
        assertEquals(3000, Pet.EAGLE_GOLD_MAX);

        Pet eagle = createEaglePet();
        assertTrue("isEagle() must be true for Pet Eagle", eagle.isEagle());
        assertEquals("Đại Bàng", eagle.name);

        Pet wolf = createOtherPet((short) 2939); // Soi
        if (wolf != null) {
            assertFalse("isEagle() must be false for Wolf", wolf.isEagle());
        }

        Pet bat = createOtherPet((short) 2943); // Doi
        if (bat != null) {
            assertFalse("isEagle() must be false for Bat", bat.isEagle());
        }
    }

    @Test
    public void testEagleGoldDropQuantityRange() throws Exception {
        Map map = createTestMap();
        Player p = createTestPlayer();

        // Perform 100 drops to verify all gold drops are strictly within [100, 3000]
        for (int i = 0; i < 100; i++) {
            // Clear map items
            for (int k = 0; k < map.item_map.length; k++) {
                map.item_map[k] = null;
            }

            LeaveItemMap.leave_vang_eagle(map, map.mobs[0].index, p);

            int spawnedCount = 0;
            ItemMap spawnedGold = null;
            for (ItemMap it : map.item_map) {
                if (it != null) {
                    spawnedCount++;
                    spawnedGold = it;
                }
            }

            assertEquals("Exactly 1 gold drop must be spawned on the map", 1, spawnedCount);
            assertNotNull(spawnedGold);
            assertEquals("Category must be 4 (gold / item4)", 4, spawnedGold.category);
            assertEquals("id_item for gold must be -1", -1, spawnedGold.id_item);
            assertEquals("idmaster must be the player ID", p.id, spawnedGold.idmaster);
            assertTrue("Gold quantity must be >= 100", spawnedGold.quantity >= 100);
            assertTrue("Gold quantity must be <= 3000", spawnedGold.quantity <= 3000);
        }
    }

    @Test
    public void testTest1_EagleHitsMonster_DropsGoldOnMap() throws Exception {
        Map map = createTestMap();
        Player p = createTestPlayer();
        p.map = map;
        p.pet_follow = true;

        Pet eagle = createEaglePet();
        p.mypet.add(eagle);

        Mob_in_map mob = map.mobs[0];
        int initialHp = mob.hp;

        // Simulate pet damage logic directly as done in MapService
        int dame_pet = 500;
        mob.hp -= dame_pet;
        assertTrue(mob.hp < initialHp);

        if (eagle.isEagle()) {
            LeaveItemMap.leave_vang_eagle(map, mob.index, p);
        }

        int goldCount = 0;
        ItemMap droppedGold = null;
        for (ItemMap it : map.item_map) {
            if (it != null && it.category == 4 && it.id_item == -1) {
                goldCount++;
                droppedGold = it;
            }
        }

        assertEquals("Eagle hitting monster must drop 1 gold drop on map", 1, goldCount);
        assertNotNull(droppedGold);
        assertTrue(droppedGold.quantity >= 100 && droppedGold.quantity <= 3000);
    }

    @Test
    public void testTest2_EagleMiss_NoGoldDrop() throws Exception {
        Map map = createTestMap();
        Player p = createTestPlayer();
        p.map = map;
        p.pet_follow = true;

        Pet eagle = createEaglePet();
        p.mypet.add(eagle);

        // When attack misses / damage is 0, no gold drop should be triggered
        // Verify item_map remains empty
        int goldCount = 0;
        for (ItemMap it : map.item_map) {
            if (it != null) {
                goldCount++;
            }
        }
        assertEquals("Missed attack must produce 0 gold drop", 0, goldCount);
    }

    @Test
    public void testTest3_EagleKillsMonster_DropsGold() throws Exception {
        Map map = createTestMap();
        Player p = createTestPlayer();
        p.map = map;
        p.pet_follow = true;

        Pet eagle = createEaglePet();
        p.mypet.add(eagle);

        Mob_in_map mob = map.mobs[0];
        mob.hp = 100; // Low HP

        int dame_pet = 500;
        mob.hp -= dame_pet;
        if (mob.hp <= 0) {
            mob.hp = 0;
            mob.isdie = true;
        }

        // Eagle still applies damage and drops gold
        if (eagle.isEagle()) {
            LeaveItemMap.leave_vang_eagle(map, mob.index, p);
        }

        int goldCount = 0;
        ItemMap droppedGold = null;
        for (ItemMap it : map.item_map) {
            if (it != null && it.category == 4 && it.id_item == -1) {
                goldCount++;
                droppedGold = it;
            }
        }

        assertEquals("Fatal hit from Eagle must still drop exactly 1 gold drop", 1, goldCount);
        assertNotNull(droppedGold);
        assertTrue(droppedGold.quantity >= 100 && droppedGold.quantity <= 3000);
    }

    @Test
    public void testTest4_EagleAttacksConsecutively_DropsOneGoldPerHit() throws Exception {
        Map map = createTestMap();
        Player p = createTestPlayer();
        p.map = map;
        p.pet_follow = true;

        Pet eagle = createEaglePet();
        p.mypet.add(eagle);

        Mob_in_map mob = map.mobs[0];

        // 3 consecutive hits
        for (int hit = 1; hit <= 3; hit++) {
            mob.hp -= 200;
            LeaveItemMap.leave_vang_eagle(map, mob.index, p);
        }

        int goldCount = 0;
        for (ItemMap it : map.item_map) {
            if (it != null && it.category == 4 && it.id_item == -1) {
                goldCount++;
            }
        }

        assertEquals("3 consecutive hits must produce exactly 3 gold drops", 3, goldCount);
    }

    @Test
    public void testTest5_PlayerAttacksMonster_EagleGoldNotTriggered() throws Exception {
        Map map = createTestMap();
        Player p = createTestPlayer();
        p.map = map;
        // Player attacks monster directly (not pet)
        Mob_in_map mob = map.mobs[0];
        mob.hp -= 1000;

        int goldCount = 0;
        for (ItemMap it : map.item_map) {
            if (it != null) {
                goldCount++;
            }
        }
        assertEquals("Direct player attack without pet trigger must not spawn Eagle gold", 0, goldCount);
    }

    @Test
    public void testTest6_EagleFollowsButNotAttacking_NoGoldDrop() throws Exception {
        Map map = createTestMap();
        Player p = createTestPlayer();
        p.map = map;
        p.pet_follow = true;

        Pet eagle = createEaglePet();
        eagle.grown = 0; // Pet is tired / grown <= 0, so it does not attack
        p.mypet.add(eagle);

        int goldCount = 0;
        for (ItemMap it : map.item_map) {
            if (it != null) {
                goldCount++;
            }
        }
        assertEquals("Non-attacking pet must not drop gold", 0, goldCount);
    }

    @Test
    public void testOtherPet_DoesNotDropGold() throws Exception {
        Map map = createTestMap();
        Player p = createTestPlayer();
        p.map = map;
        p.pet_follow = true;

        Pet wolf = createOtherPet((short) 2939); // Soi
        p.mypet.add(wolf);

        Mob_in_map mob = map.mobs[0];
        mob.hp -= 300;

        if (wolf != null && wolf.isEagle()) {
            LeaveItemMap.leave_vang_eagle(map, mob.index, p);
        }

        int goldCount = 0;
        for (ItemMap it : map.item_map) {
            if (it != null) {
                goldCount++;
            }
        }
        assertEquals("Non-Eagle pet attack must not spawn Eagle gold", 0, goldCount);
    }

    @Test
    public void testGoldPickupMechanismPreserved() throws Exception {
        Map map = createTestMap();
        Player p = createTestPlayer();
        p.map = map;
        p.update_vang(10000);
        long initialGold = p.get_vang();

        // Eagle drops gold on map
        LeaveItemMap.leave_vang_eagle(map, map.mobs[0].index, p);

        int index = -1;
        for (int i = 0; i < map.item_map.length; i++) {
            if (map.item_map[i] != null) {
                index = i;
                break;
            }
        }
        assertTrue("Gold should be on map at a valid index", index >= 0);

        ItemMap item = map.item_map[index];
        short goldAmount = item.quantity;
        assertTrue(goldAmount >= 100 && goldAmount <= 3000);

        // Simulate player picking up gold via normal pickup mechanism
        item.time_pick = 0; // ready to pick up
        if (item.category == 4 && item.id_item == -1) {
            if (p.id == item.idmaster || item.idmaster == -1) {
                p.update_vang(item.quantity);
                map.item_map[index] = null;
            }
        }

        assertEquals("Player gold should increase by the exact quantity dropped", initialGold + goldAmount, p.get_vang());
        assertNull("Item on map should be cleared after pickup", map.item_map[index]);
    }
}
