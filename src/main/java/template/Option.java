package template;

public class Option {
	private static final int[] parafterupdate =
	      new int[] {1, 110, 120, 130, 140, 150, 160, 172, 184, 196, 208, 220, 235, 250, 265, 280};
	public byte id;
	private int param;

	public Option(int id, int param) {
		this.id = (byte) id;
		this.param = param;
	}

	public int getParam(int tier) {
		if (tier == 0) {
			return param;
		}
		//
		int parbuffer = this.param;
		if (this.id >= 29 && this.id <= 36 || this.id >= 16 && this.id <= 22 || this.id == 41) {
			parbuffer += 20 * tier;
			return parbuffer;
		}
		if (this.id >= 23 && this.id <= 26) {
			return (parbuffer + tier);
		}
		if (this.id == 42) {
			return (parbuffer + tier * 400);
		}
		if ((this.id >= 7 && this.id <= 13) || this.id == 15 || this.id == 27 || this.id == 28) {
			return (parbuffer + 100 * tier);
		}
		if ((this.id == 37 || this.id == 38) && tier < 9) {
			return 1;
		}
		if (tier > 15) {
			tier = 15;
		}
		if ((this.id >= 0 && this.id <= 6) || this.id == 14 || this.id == 40) {
			parbuffer = (parafterupdate[tier] * this.param) / 100;
			return parbuffer;
		}
		return parbuffer;
	}

	public void setParam(int param) {
		this.param = param;
	}
}
