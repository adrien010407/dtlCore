package net.dandielo.core.items.serialize.core;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import net.dandielo.core.exceptions.InvalidAttributeValueException;
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
	
	@Override
	public void onRefactor(ItemStack item) throws InvalidAttributeValueException {
		ItemMeta meta = item.getItemMeta();
		if (!meta.hasDisplayName())
			throw new InvalidAttributeValueException();
		name = meta.getDisplayName();
	}
	
	@Override
	public void onAssign(ItemStack item, boolean abstrac) {
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(name);
		item.setItemMeta(meta);
	}
}
