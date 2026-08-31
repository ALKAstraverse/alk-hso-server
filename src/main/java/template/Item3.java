package template;

import java.io.IOException;
import java.util.List;
import io.Message;

public class Item3 {
	public short id;
	public byte clazz;
	public byte type;
	public short level;
	public short icon;
	public byte color;
	public byte part;
	public boolean islock;
	public String name;
	public byte tier;
	public List<Option> op;
	public long time_use;

	public static int getClassDamageOptionId(byte playerClazz) {
		switch (playerClazz) {
			case 0: // Chiến Binh -> Lửa
				return 2;
			case 1: // Sát Thủ -> Độc
				return 4;
			case 2: // Pháp Sư -> Băng
				return 1;
			case 3: // Xạ Thủ -> Điện
				return 3;
			default:
				return 2;
		}
	}

	public static boolean isHiddenElementalOption(int optionId, byte playerClazz) {
		int opId = Byte.toUnsignedInt((byte) optionId);
		int ownFlatDmgId;
		int ownPercentDmgId;
		switch (playerClazz) {
			case 0: // Chiến Binh -> Lửa
				ownFlatDmgId = 2;
				ownPercentDmgId = 9;
				break;
			case 1: // Sát Thủ -> Độc
				ownFlatDmgId = 4;
				ownPercentDmgId = 11;
				break;
			case 2: // Pháp Sư -> Băng
				ownFlatDmgId = 1;
				ownPercentDmgId = 8;
				break;
			case 3: // Xạ Thủ -> Điện
				ownFlatDmgId = 3;
				ownPercentDmgId = 10;
				break;
			default:
				return false;
		}

		// Flat elemental damages (1: Ice, 2: Fire, 3: Electric, 4: Poison)
		if (opId >= 1 && opId <= 4) {
			return opId != ownFlatDmgId;
		}
		// Percent elemental damages (8: Ice %, 9: Fire %, 10: Electric %, 11: Poison %)
		if (opId >= 8 && opId <= 11) {
			return opId != ownPercentDmgId;
		}
		return false;
	}

	public int getParamByOptionId(int optionId) {
		if (this.op == null) {
			return 0;
		}
		int total = 0;
		for (Option o : this.op) {
			if (o != null && (o.id == optionId || Byte.toUnsignedInt(o.id) == optionId)) {
				total += o.getParam(this.tier);
			}
		}
		return total;
	}

	public void writeItemOptions(Message m, byte playerClazz) throws IOException {
		if (this.op == null || this.op.isEmpty()) {
			m.writer().writeByte(0);
			return;
		}
		int count = 0;
		for (Option o : this.op) {
			if (o != null && !isHiddenElementalOption(o.id, playerClazz)) {
				count++;
			}
		}
		m.writer().writeByte(count);
		for (Option o : this.op) {
			if (o != null && !isHiddenElementalOption(o.id, playerClazz)) {
				m.writer().writeByte(o.id);
				m.writer().writeInt(o.getParam(this.tier));
			}
		}
	}
}


