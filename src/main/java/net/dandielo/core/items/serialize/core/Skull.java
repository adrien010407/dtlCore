package net.dandielo.core.items.serialize.core;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import net.dandielo.core.items.dItem;
import net.dandielo.core.items.serialize.Attribute;
import net.dandielo.core.items.serialize.ItemAttribute;

@Attribute(name="Skull", key="sk", priority = 5, items = {Material.SKULL, Material.SKULL_ITEM})
public class Skull  extends ItemAttribute {
	private String owner;
	
	protected Skull(dItem item, String key) {
		super(item, key);
	}

	@Override
	public String serialize() {
		return owner;
	}

	@Override
	public boolean deserialize(String data) {
		owner = data;
		return true;
	}
	
	@Override
	public void onAssign(ItemStack item, boolean unused) {
		if (item.getItemMeta() instanceof SkullMeta)
		{
			SkullMeta meta = (SkullMeta) item.getItemMeta();
			meta.setOwner(owner);
			item.setItemMeta(meta);
		}
	}
	
	@Override
	public boolean onRefactor(ItemStack item) {
		if (!(item.getItemMeta() instanceof SkullMeta)) return false;

		SkullMeta meta = (SkullMeta) item.getItemMeta();
		meta.setOwner(owner);
		item.setItemMeta(meta);
		return true;
	}
}
