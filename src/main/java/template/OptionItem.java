package template;

import java.util.ArrayList;
import java.util.List;

public class OptionItem {
	public static final List<OptionItem> entrys = new ArrayList<>();
	private String name;
	private byte color;
	private byte ispercent;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public byte getColor() {
		return color;
	}

	public void setColor(byte color) {
		this.color = color;
	}

	public byte getIspercent() {
		return ispercent;
	}

	public void setIspercent(byte ispercent) {
		this.ispercent = ispercent;
	}
        public static OptionItem get(int id) {
            
		return OptionItem.entrys.get(Byte.toUnsignedInt((byte) id));
	}
}
