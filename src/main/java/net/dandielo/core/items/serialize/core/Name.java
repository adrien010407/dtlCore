package net.dandielo.core.items.serialize.core;

import net.dandielo.core.items.dItem;
import net.dandielo.core.items.serialize.Attribute;
import net.dandielo.core.items.serialize.ItemAttribute;

@Attribute(key = "n", name = "name")
public class Name extends ItemAttribute {
	private String name;
	
	public Name(dItem item, String key) {
		super(item, key);
	}
	
	public String getValue() {
		return name;
	}
	
	public void setValue(String name) {
		this.name = name;
	}

	@Override
	public String onSerialize() {
		return name.replace('§', '&');
	}

	@Override
	public void onLoad(String data) {
		name = data.replace('&', '§');
	}
}
