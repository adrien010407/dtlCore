package net.dandielo.core.items.serialize.core;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.MapMeta;

import net.dandielo.core.items.dItem;
import net.dandielo.core.items.serialize.Attribute;
import net.dandielo.core.items.serialize.ItemAttribute;

@Attribute(name = "Map", key = "map", priority = 5, items = {Material.MAP})
public class Map extends ItemAttribute {
	private int scale = 1;

	public Map(dItem item, String key)
	{
		super(item, key);
	}

	@Override
	public boolean deserialize(String data) 
	{
		return true;
	}

	@Override
	public String serialize()
	{
		return null;
	}

	@Override
	public void onAssign(ItemStack item, boolean unused)
	{
	}

	@Override
	public boolean onRefactor(ItemStack item)
	{
		if ( !(item.getItemMeta() instanceof MapMeta) )
			return false;
		return true;
	}
}
