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
	// -127 : khu 2 farm time
	// -200 : kien ma thuat (giam 50% st neu hp > 10%)
	// -201 : bong lanh (mat mp/s)
	// -202 : bong lua (mat hp/s)
	// -203 : giap hac am (chuyen st thanh giap)
	// -204 : tang hinh
	// -205 : lu loan
	// -206 : vet thuong sau (giam 50% hoi hp)
	// -210 : ngoc hon nguyen (x2 dame 3s)
	// -211 : ngoc khai hoan (khang hieu ung 3s)
	// -212 : ngoc phong ma (hoi hp khi hp < 20%)
	// -213 : ngoc luc bao (chuyen 10% st thanh hp)
	// -220 : mien st lua (bo qua st theo ty le)
	// -221 : mien st doc (bo qua st theo ty le)
	// -222 : mien st vat ly (bo qua st theo ty le)
	// -223 : mien st bang (bo qua st theo ty le)
	// -224 : mien st dien (bo qua st theo ty le)
	// -225 : giap bao ho (giam st cua nguoi danh: 100%->75%->50%)
	// -226 : boc pha (no gay st 10-40% hp quanh 6 o)
	// -227 : hung tan (yeu cau hit -> hut 3-5% max hp)
	// -228 : chinh xac (giam ne tranh 90% cua ke thu)
	// -229 : giap bach kim (giam chi mang 90% cua ke thu)
	// -230 : giap thien su (giam phan dame 90% cua ke thu)
	// -231 : giap ve binh (giam xuyen giap 90% cua ke danh)
	// -232 : ngu dan (tat pet doi thu 30s)
	// -233 : mu mat (lam skill hu 5s)
	// -234 : bat tu (hp <= 5% -> bat tu 5s)
	// -235 : thieu chay (aoe 5000 dame/3s, 20s, hp <= 30%)
	// -236 : tan phere (giam mp doi thu con 1%, cam hoi mp 5s)
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
