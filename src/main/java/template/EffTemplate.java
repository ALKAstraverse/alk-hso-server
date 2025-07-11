package template;

public class EffTemplate {
	// 0 -> 4 : special skill clazz, 4: vat ly
	// 52 : + 10 %vang
	// 53 : + 1% hoi hp
	// -121 : troi 4 he clazz 0
	// -122 : ....
	// -123 : ....
	// -124 : ....
	// 23 : + suc manh skill buff
	// 24 : + phong thu skill buff
	// -126 : chong pk
	// -125 : x2 time
	// = option item = param this option
	public int id;
	public int param;
	public long time;

	public EffTemplate(int id, int param, long time) {
		this.id = id;
		this.param = param;
		this.time = time;
	}
}
