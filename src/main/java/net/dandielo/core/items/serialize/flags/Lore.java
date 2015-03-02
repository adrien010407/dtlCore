package net.dandielo.core.items.serialize.flags;

import java.util.List;

import org.bukkit.inventory.ItemStack;

import net.dandielo.core.items.dItem;
import net.dandielo.core.items.serialize.Attribute;
import net.dandielo.core.items.serialize.ItemFlag;

@Attribute(key = "", name = "")
public class Lore extends ItemFlag {
	public Lore(dItem item, String key) {
		super(item, key);
	}

	@Override
	public void onAssign(ItemStack item, boolean abstrac) {
	}

	public List<String> getValue() {
		return null;
	}

	public void setValue(List<String> lore) {
	} 
}
