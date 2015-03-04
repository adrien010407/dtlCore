package net.dandielo.core.items.serialize.flags;

import org.bukkit.inventory.ItemStack;

import net.dandielo.core.items.dItem;
import net.dandielo.core.items.serialize.Attribute;
import net.dandielo.core.items.serialize.ItemFlag;

@Attribute(key = ".splash", name = "Splash potion")
public class SplashPotion extends ItemFlag {
	public SplashPotion(dItem item, String key) {
		super(item, key);
	}

	@Override
	public void onAssign(ItemStack item, boolean abstrac) {
		// TODO Auto-generated method stub
	}
}
