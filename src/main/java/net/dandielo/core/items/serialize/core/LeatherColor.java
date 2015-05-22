package net.dandielo.core.items.serialize.core;

import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;

import net.dandielo.core.items.dItem;
import net.dandielo.core.items.serialize.Attribute;
import net.dandielo.core.items.serialize.ItemAttribute;

@Attribute(name="Leather color", key="lc", priority = 5, standalone = true, 
items={Material.LEATHER_BOOTS, Material.LEATHER_CHESTPLATE, Material.LEATHER_HELMET, Material.LEATHER_LEGGINGS})
public class LeatherColor extends ItemAttribute {
	private Color color;

	public LeatherColor(dItem item, String key)
	{
		super(item, key);
	}

	@Override
	public boolean deserialize(String data) 
	{
		try
		{
		    String[] colors = data.split("\\^", 3);
			color = Color.fromRGB(Integer.parseInt(colors[0]), Integer.parseInt(colors[1]), Integer.parseInt(colors[2]));
		}
		catch(Exception e)
		{
			return false;
		}
		return true;
	}

	@Override
	public String serialize()
	{
		//save as rgb
		return color.getRed() + "^" + color.getGreen() + "^" + color.getBlue();
	}

	@Override
	public void onAssign(ItemStack item, boolean unused)
	{
		//no leather armor no color
		if ( !isLeatherArmor(item) ) return;

		//set the new color to the item meta
		LeatherArmorMeta meta = (LeatherArmorMeta) item.getItemMeta();
		meta.setColor(color);
		
		//save the meta to the item
		item.setItemMeta(meta);
	}

	@Override
	public boolean onRefactor(ItemStack item)
	{
		//no leather armor no color
		if ( !isLeatherArmor(item) || !item.hasItemMeta() ) return false;
		
		color = ((LeatherArmorMeta)item.getItemMeta()).getColor();
		return true;
	}
	
	@Override
	public boolean equals(ItemAttribute data)
	{
		return ((LeatherColor)data).color.equals(color);
	}
	
	@Override
	public boolean similar(ItemAttribute data)
	{
		return equals(data);
	}

	/**
	 * Simple check if the given item has the LeatherArmorMeta.
	 * @param item
	 *     item to check
	 * @return
	 *     true if the item is a leather armor piece
	 */
	private static boolean isLeatherArmor(ItemStack item)
	{
		Material mat = item.getType();
		return mat.equals(Material.LEATHER_BOOTS) || mat.equals(Material.LEATHER_CHESTPLATE) || mat.equals(Material.LEATHER_HELMET) || mat.equals(Material.LEATHER_LEGGINGS);
	}
}
