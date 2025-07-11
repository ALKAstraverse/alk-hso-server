package template;

import java.util.ArrayList;
import java.util.List;

public class ItemTemplate3 {
	public static final List<ItemTemplate3> item = new ArrayList<>();
	private short id;
	private String name;
	private byte type;
	private byte part;
	private byte clazz;
	private short icon;
	private short level;
	private List<Option> op;
	private byte color;

	public short getId() {
		return id;
	}

	public void setId(short id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public byte getType() {
		return type;
	}

	public void setType(byte type) {
		this.type = type;
	}

	public byte getPart() {
		return part;
	}

	public void setPart(byte part) {
		this.part = part;
	}

	public byte getClazz() {
		return clazz;
	}

	public void setClazz(byte clazz) {
		this.clazz = clazz;
	}

	public short getIcon() {
		return icon;
	}

	public void setIcon(short icon) {
		this.icon = icon;
	}

	public short getLevel() {
		return level;
	}

	public void setLevel(short level) {
		this.level = level;
	}

	public List<Option> getOp() {
		return op;
	}

	public void setOp(List<Option> op) {
		this.op = op;
	}

	public byte getColor() {
		return color;
	}

	public void setColor(byte color) {
		this.color = color;
	}
}
