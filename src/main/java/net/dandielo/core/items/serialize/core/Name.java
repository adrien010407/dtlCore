package net.dandielo.core.items.serialize.core;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

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
	public String serialize() {
		return name.replace('§', '&');
	}

	@Override
	public void onLoad(String data) {
		name = data.replace('&', '§');
	}
	
	@Override
	public boolean onRefactor(ItemStack item) {
		ItemMeta meta = item.getItemMeta();
		if (!meta.hasDisplayName())
			return false;
		
		name = meta.getDisplayName();
		return true;
	}
	
	@Override
	public void onAssign(ItemStack item, boolean abstrac) {
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(name);
		item.setItemMeta(meta);
	}
}
