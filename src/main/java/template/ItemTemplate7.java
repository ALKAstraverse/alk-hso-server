package template;

import java.util.ArrayList;
import java.util.List;

public class ItemTemplate7 {
	public static final List<ItemTemplate7> item = new ArrayList<>();
	private short id;
	private short icon;
	private long price;
	private String name;
	private String content;
	private byte type;
	private byte pricetype;
	private byte sell;
	private short value;
	private byte trade;
	private byte color;

	public short getId() {
		return id;
	}

	public void setId(short id) {
		this.id = id;
	}

	public short getIcon() {
		return icon;
	}

	public void setIcon(short icon) {
		this.icon = icon;
	}

	public long getPrice() {
		return price;
	}

	public void setPrice(long price) {
		this.price = price;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public byte getType() {
		return type;
	}

	public void setType(byte type) {
		this.type = type;
	}

	public byte getPricetype() {
		return pricetype;
	}

	public void setPricetype(byte pricetype) {
		this.pricetype = pricetype;
	}

	public byte getSell() {
		return sell;
	}

	public void setSell(byte sell) {
		this.sell = sell;
	}

	public short getValue() {
		return value;
	}

	public void setValue(short value) {
		this.value = value;
	}

	public byte getTrade() {
		return trade;
	}

	public void setTrade(byte trade) {
		this.trade = trade;
	}

	public byte getColor() {
		return color;
	}

	public void setColor(byte color) {
		this.color = color;
	}
}
