package net.dandielo.core.items.serialize.core;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

import net.dandielo.core.items.dItem;
import net.dandielo.core.items.serialize.Attribute;
import net.dandielo.core.items.serialize.ItemAttribute;

@Attribute(name="Enchants", key="e", priority = 5)
public class Enchants extends ItemAttribute {
	private Map<Enchantment, Integer> enchants = new HashMap<Enchantment, Integer>();
	
	public Enchants(dItem item, String key)
	{
		super(item, key);
	}

	@Override
	@SuppressWarnings("deprecation")
	public boolean deserialize(String data) 
	{
		//split all enchants into name/id
		for ( String enchantment : data.split(",") )
		{
			//split the string into name and lvl values
			String[] enchData = enchantment.split("/");
			
			//get the enchant by name or id
			Enchantment ench = Enchantment.getByName( enchData[0].toUpperCase() );
			if ( ench == null )
				ench = Enchantment.getById( Integer.parseInt(enchData[0]));
			
			try
			{
				//save the enchant with lvl
				enchants.put(ench, Integer.parseInt(enchData[1]));
			}
			catch(NumberFormatException e)
			{
				//TODO: Add exceptions throw new AttributeInvalidValueException(getInfo(), data);
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
		for ( Map.Entry<Enchantment, Integer> enchant : enchants.entrySet() )
		    item.addUnsafeEnchantment(enchant.getKey(), enchant.getValue());
	}

	@Override
	public boolean onRefactor(ItemStack item)
	{
		//if enchants are not present the just say goodbye ;)
		if ( !item.getItemMeta().hasEnchants() ) return false;
		
		//saving all enchants into the list
		for ( Map.Entry<Enchantment, Integer> enchant : item.getEnchantments().entrySet() )
			enchants.put(enchant.getKey(), enchant.getValue());
		return true;
	}

	@Override
	public boolean equals(ItemAttribute data)
	{
		if ( ((Enchants)data).enchants.size() != enchants.size() ) return false;
		
		boolean equals = true;
		for ( Map.Entry<Enchantment, Integer> enchant : ((Enchants)data).enchants.entrySet() )
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
		return equals(data);
	}
}
