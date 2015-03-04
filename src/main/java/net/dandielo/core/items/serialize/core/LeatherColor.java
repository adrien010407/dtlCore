package net.dandielo.core.items.serialize.core;

import net.dandielo.core.items.dItem;
import net.dandielo.core.items.serialize.Attribute;
import net.dandielo.core.items.serialize.ItemAttribute;

@Attribute(key = "lc", name = "Leather color")
public class LeatherColor extends ItemAttribute {
	protected LeatherColor(dItem item, String key) {
		super(item, key);
	}

	@Override
	public String serialize() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean deserialize(String data) {
		// TODO Auto-generated method stub
		return false;
	}
}
