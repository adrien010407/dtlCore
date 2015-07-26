package net.dandielo.core.items.serialize.core;

import org.bukkit.inventory.ItemStack;

import net.dandielo.core.items.dItem;
import net.dandielo.core.items.serialize.Attribute;
import net.dandielo.core.items.serialize.ItemAttribute;

@Attribute(key = "d", name = "Durability")
public class Durability extends ItemAttribute {
	private short durabilityPercent; 
	private short durability;

	public Durability(dItem item, String key) {
		super(item, key);
		durabilityPercent = -1;
		durability = 0;
	}
	
	public short getValue() {
		return durability;
	}
	
	public double getPercent() {
		return durabilityPercent;
	}

	@Override
	public String serialize() {
		if (durabilityPercent >= -1)
			return String.format("%d%%", durabilityPercent);
		return String.valueOf(durability);
	}

	@Override
	public boolean deserialize(String data) {
		try
		{
			if (data.endsWith("%"))
			{
				durabilityPercent = Short.parseShort(data.substring(0, data.length() - 1));
				durability = (short) (item.getItem(false).getType().getMaxDurability() * durabilityPercent / 100.0);
			}
			else
			{
				durability = Short.parseShort(data.substring(0));
			}
		}
		catch(NumberFormatException e)
		{
			return false;
		}
		return true;
	}
	
	@Override
	public void onAssign(ItemStack item, boolean unused) {
		if (item.getType().getMaxDurability() > 0)
		{
			if (durabilityPercent > -1) 
				durability = (short) (item.getType().getMaxDurability() * ((double)durabilityPercent / 100.0)); 
			item.setDurability(durability);
		}
	}
	
	@Override
	public boolean onRefactor(ItemStack item) {
		if (item.getType().getMaxDurability() == 0) return false;
			
		durability = item.getDurability();
		return true;
	}

	@Override
	public boolean similar(ItemAttribute attr)
	{
		return durability >= ((Durability)attr).durability;
	}
	
	@Override
	public boolean equals(ItemAttribute attr)
	{
		return durability == ((Durability)attr).durability;
	}
}
