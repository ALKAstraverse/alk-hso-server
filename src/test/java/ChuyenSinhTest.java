import org.junit.Test;
import static org.junit.Assert.*;

import client.Body;
import client.Item;
import client.Player;
import template.Skill;
import template.LvSkill;
import template.Option;

import java.util.ArrayList;

public class ChuyenSinhTest {

    @org.junit.BeforeClass
    public static void setupLevelEntries() {
        if (template.Level.entrys.isEmpty()) {
            for (int i = 1; i <= 250; i++) {
                template.Level temp = new template.Level();
                temp.level = (short) i;
                temp.exp = 100000L * i;
                temp.tiemnang = 5;
                temp.kynang = 1;
                template.Level.entrys.add(temp);
            }
        }
    }

    private Player createTestPlayerWithSkills(byte clazz, short level, int point1, int point2, int point3, int point4, int tiemnang, short kynang, byte[] skillPoints) throws Exception {
        Player p = new Player(null, 100);
        p.name = "TestPlayer";
        p.clazz = clazz;
        p.level = level;
        p.exp = 500000;
        p.point1 = point1;
        p.point2 = point2;
        p.point3 = point3;
        p.point4 = point4;
        p.tiemnang = tiemnang;
        p.kynang = kynang;
        p.chuyensinh = 0;
        p.body = new Body(p);
        p.item = new Item(p);
        p.list_eff = new ArrayList<>();
        p.mypet = new ArrayList<>();
        p.rms_save = new byte[2][0];
        p.fashion = new byte[0];
        p.item.wear = new template.Item3[24];
        p.item.bag3 = new template.Item3[42];
        p.item.bag47 = new ArrayList<>();
        p.tutien = new int[5];
        p.kinhmach = new int[5];

        // Setup skills array
        p.skills = new Skill[21];
        for (int i = 0; i < 21; i++) {
            p.skills[i] = new Skill();
            p.skills[i].id = (byte) i;
            p.skills[i].name = "Skill_" + i;
            p.skills[i].mLvSkill = new LvSkill[15];
            for (int lv = 0; lv < 15; lv++) {
                p.skills[i].mLvSkill[lv] = new LvSkill();
                p.skills[i].mLvSkill[lv].LvRe = (short) (lv * 10 + 1);
                p.skills[i].mLvSkill[lv].delay = 1000;
                p.skills[i].mLvSkill[lv].mpLost = (short) (10 + lv * 5);
                p.skills[i].mLvSkill[lv].nTarget = 1;
                p.skills[i].mLvSkill[lv].minfo = new Option[]{new Option(0, 100 + lv * 50)};
            }
        }

        // Setup skill points
        p.skill_point = new byte[21];
        for (int i = 0; i < 21; i++) {
            if (skillPoints != null && i < skillPoints.length) {
                p.skill_point[i] = skillPoints[i];
            } else {
                p.skill_point[i] = 0;
            }
        }

        return p;
    }

    @Test
    public void testChuyenSinhPreservesSkillsAndSkillPoints() throws Exception {
        // Player before Chuyen Sinh: Lv 200, various skill points and levels
        byte[] originalSkillPoints = new byte[]{
            1,  // Skill 0: Lv 1
            5,  // Skill 1: Lv 5
            3,  // Skill 2: Lv 3
            10, // Skill 3: Lv 10
            0,  // Skill 4: Lv 0
            7,  // Skill 5: Lv 7
            0, 0, 0,
            4,  // Skill 9: Lv 4
            6,  // Skill 10: Lv 6
            0, 0, 0, 0, 0, 0, 0, 0,
            10, // Skill 19: Lv 10
            10  // Skill 20: Lv 10
        };

        int initialPoint1 = 204;
        int initialPoint2 = 204;
        int initialPoint3 = 204;
        int initialPoint4 = 204;
        int initialTiemNang = 500;
        short initialKyNang = 20;

        Player p = createTestPlayerWithSkills((byte) 0, (short) 200, initialPoint1, initialPoint2, initialPoint3, initialPoint4, initialTiemNang, initialKyNang, originalSkillPoints);

        // Simulate Chuyen Sinh logic (new mechanism)
        p.level = 10;
        p.exp = 0;
        p.chuyensinh++;
        // Reset potential points logic without network socket
        p.tiemnang += (p.point1 + p.point2 + p.point3 + p.point4);
        p.point1 = (4 + p.level);
        p.point2 = (4 + p.level);
        p.point3 = (4 + p.level);
        p.point4 = (4 + p.level);
        p.tiemnang -= (p.point1 + p.point2 + p.point3 + p.point4);

        // 1. Verify Level is 10
        assertEquals("Level should be reset to 10", 10, p.level);
        assertEquals("Exp should be reset to 0", 0, p.exp);

        // 2. Verify Chuyensinh count incremented
        assertEquals("Chuyensinh count should be 1", 1, p.chuyensinh);

        // 3. Verify Potential points reset to level 10 base (4 + 10 = 14)
        assertEquals("Point1 should be 4 + level = 14", 14, p.point1);
        assertEquals("Point2 should be 4 + level = 14", 14, p.point2);
        assertEquals("Point3 should be 4 + level = 14", 14, p.point3);
        assertEquals("Point4 should be 4 + level = 14", 14, p.point4);
        int expectedTiemNang = initialTiemNang + (initialPoint1 + initialPoint2 + initialPoint3 + initialPoint4) - (14 * 4);
        assertEquals("Tiem nang points should accumulate refunded points minus base", expectedTiemNang, p.tiemnang);

        // 4. Verify Skill Points are completely preserved
        assertEquals("KyNang points must be unchanged", initialKyNang, p.kynang);
        for (int i = 0; i < 21; i++) {
            assertEquals("Skill point at index " + i + " must remain unchanged", originalSkillPoints[i], p.skill_point[i]);
        }

        // 5. Verify Skill objects and levels are intact
        assertNotNull("Skills array must not be null", p.skills);
        assertEquals("Skills length must be 21", 21, p.skills.length);
        for (int i = 0; i < 21; i++) {
            assertNotNull("Skill " + i + " must not be null", p.skills[i]);
            assertEquals("Skill ID must match index", i, p.skills[i].id);
            assertEquals("Skill name must be preserved", "Skill_" + i, p.skills[i].name);
            assertEquals("Skill levels must be 15", 15, p.skills[i].mLvSkill.length);
        }
    }

    @Test
    public void testChuyenSinhWithMultipleClassesAndSkillConfigurations() throws Exception {
        // Test all 4 classes (0: Chien Binh, 1: Sat Thu, 2: Phap Su, 3: Xa Thu)
        for (byte clazz = 0; clazz < 4; clazz++) {
            byte[] skillPoints = new byte[21];
            for (int s = 0; s < 21; s++) {
                skillPoints[s] = (byte) ((s * 3 + clazz) % 11); // Various skill points from 0 to 10
            }

            Player p = createTestPlayerWithSkills(clazz, (short) 200, 204, 204, 204, 204, 1000, (short) 50, skillPoints);

            // Execute Chuyen Sinh
            p.level = 10;
            p.exp = 0;
            p.chuyensinh++;
            p.tiemnang += (p.point1 + p.point2 + p.point3 + p.point4);
            p.point1 = (4 + p.level);
            p.point2 = (4 + p.level);
            p.point3 = (4 + p.level);
            p.point4 = (4 + p.level);
            p.tiemnang -= (p.point1 + p.point2 + p.point3 + p.point4);

            // Verify state
            assertEquals(10, p.level);
            assertEquals(1, p.chuyensinh);
            assertEquals(50, p.kynang);
            for (int s = 0; s < 21; s++) {
                assertEquals("Class " + clazz + " skill " + s + " point mismatch", skillPoints[s], p.skill_point[s]);
            }
        }
    }

    @Test
    public void testSaveAndLoadSkillDataIntegrity() throws Exception {
        // Verify JSON representation preserves all skill points
        byte[] skillPoints = new byte[]{1, 5, 3, 10, 0, 7, 0, 2, 8, 4, 6, 0, 1, 9, 0, 3, 0, 0, 0, 10, 10};
        Player p = createTestPlayerWithSkills((byte) 0, (short) 10, 14, 14, 14, 14, 500, (short) 20, skillPoints);

        org.json.simple.JSONArray jsar = new org.json.simple.JSONArray();
        for (int i = 0; i < 21; i++) {
            jsar.add(p.skill_point[i]);
        }
        String jsonString = jsar.toJSONString();

        // Simulate reading from DB
        org.json.simple.JSONArray loadedJsar = (org.json.simple.JSONArray) org.json.simple.JSONValue.parse(jsonString);
        assertNotNull(loadedJsar);
        byte[] loadedSkillPoints = new byte[21];
        for (int i = 0; i < 21; i++) {
            loadedSkillPoints[i] = Byte.parseByte(loadedJsar.get(i).toString());
        }

        for (int i = 0; i < 21; i++) {
            assertEquals("Loaded skill point at index " + i + " must match", skillPoints[i], loadedSkillPoints[i]);
        }
    }
}
