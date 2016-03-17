package net.dandielo.core.items.serialize.core;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;

import net.dandielo.core.items.dItem;
import net.dandielo.core.items.serialize.Attribute;
import net.dandielo.core.items.serialize.ItemAttribute;

@Attribute(name="StoredEnchants", key="se", priority = 5, items = {Material.ENCHANTED_BOOK})
public class StoredEnchant extends ItemAttribute {
	private Map<Enchantment, Integer> enchants = new HashMap<Enchantment, Integer>();
	
	public StoredEnchant(dItem item, String key)
	{
		super(item, key);
	}

	@Override
	public boolean deserialize(String data) 
	{
		//split all enchants into name/id
		for ( String enchantment : data.split(",") )
		{
			//split the string into name and lvl values
			String[] enchData = enchantment.split("/");
			
			//get the enchant by name or id
			Enchantment ench = Enchantment.getByName( enchData[0].toUpperCase() );
			if (ench == null) 
				return false;
			
			try
			{
				//save the enchant with lvl
				enchants.put(ench, Integer.parseInt(enchData[1]));
			}
			catch(NumberFormatException e)
			{
				return false;
			}
		}
		return true;
	}

	@Override
	public String serialize()
	{
		String result = "";
		
		//for each enchant saved, with name and lvl
		for ( Map.Entry<Enchantment, Integer> enchant : enchants.entrySet() )
			result += "," + enchant.getKey().getName().toLowerCase() + "/" + enchant.getValue();
		
		//return the save string
		return result.substring(1);
	}

	@Override
	public void onAssign(ItemStack item, boolean unused)
	{
		if ( !item.getType().equals(Material.ENCHANTED_BOOK) ) return;
		
		//get the meta
		EnchantmentStorageMeta meta = (EnchantmentStorageMeta) item.getItemMeta();

		//set the enchanted book with enchants
		for ( Map.Entry<Enchantment, Integer> enchant : enchants.entrySet() )
		    meta.addStoredEnchant(enchant.getKey(), enchant.getValue(), true);
		
		//re-assign the meta
		item.setItemMeta(meta);
	}

	@Override
	public boolean onRefactor(ItemStack item)
	{
		//if its the wrong item
		if ( !item.getType().equals(Material.ENCHANTED_BOOK) ) 
			return false;
		
		//if enchants are not present the just say goodbye ;)
		if ( !((EnchantmentStorageMeta)item.getItemMeta()).hasStoredEnchants() ) 
			return false;
		
		//saving all enchants into the list
		for ( Map.Entry<Enchantment, Integer> enchant : ((EnchantmentStorageMeta)item.getItemMeta()).getStoredEnchants().entrySet() )
			enchants.put(enchant.getKey(), enchant.getValue());
		return true;
	}

	@Override
	public boolean same(ItemAttribute data)
	{
		if ( ((StoredEnchant)data).enchants.size() != enchants.size() ) return false;
		
		boolean equals = true;
		for ( Map.Entry<Enchantment, Integer> enchant : ((StoredEnchant)data).enchants.entrySet() )
		{
			if ( equals && enchants.get(enchant.getKey()) != null )
				equals = enchants.get(enchant.getKey()) == enchant.getValue();
			else
				equals = false;
		}
		return equals;
	}
	
	@Override
	public boolean similar(ItemAttribute data)
	{
		return same(data);
	}
}
