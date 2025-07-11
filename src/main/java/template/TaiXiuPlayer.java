package template;

public class TaiXiuPlayer {
	public String time;
	public String namep;
	public int coin_win;
	public byte type;

	public TaiXiuPlayer(String time, int coin, int type, String namep) {
		this.time = time;
		this.coin_win = coin;
		this.type = (byte) type;
		this.namep = namep;
	}
}
