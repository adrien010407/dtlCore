package net.dandielo.core.items.serialize.core;

import net.dandielo.core.items.dItem;
import net.dandielo.core.items.serialize.Attribute;
import net.dandielo.core.items.serialize.ItemAttribute;

@Attribute(key = "a", name = "Amount")
public class Amount extends ItemAttribute {
	private int amount;
	
	protected Amount(dItem item, String key) {
		super(item, key);
	}
	
	public int getValue() {
		return amount;
	}

	@Override
	public String onSerialize() {
		return null;
	}

	@Override
	public void onLoad(String data) {
	}
}
