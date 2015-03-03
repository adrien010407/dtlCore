package net.dandielo.core.items.serialize.core;

import org.bukkit.inventory.ItemStack;

import net.dandielo.core.items.dItem;
import net.dandielo.core.items.serialize.Attribute;
import net.dandielo.core.items.serialize.ItemAttribute;

@Attribute(key = "d", name = "Durability")
public class Durability extends ItemAttribute {
	private double durabilityPercent; 
	private short durability;

	public Durability(dItem item, String key) {
		super(item, key);
	}
	
	public short getValue() {
		return durability;
	}
	
	public double getPercent() {
		return durabilityPercent;
	}

	@Override
	public String serialize() {
		if (durabilityPercent >= 0.0)
			return String.format("%.0f%%", durabilityPercent * 100);
		return String.valueOf(durability);
	}

	@Override
	public void onLoad(String data) {
		try
		{
			if (data.endsWith("%"))
			{
				durabilityPercent = Integer.parseInt(data.substring(0, data.length() - 1)) / 100.0;
				durability = (short) (item.getItem(false).getType().getMaxDurability() * durabilityPercent);
			}
			else
			{
				durability = Short.parseShort(data.substring(0));
			}
		}
		catch(NumberFormatException e)
		{
			//TODO Maybe another way to indicate a attribute failed to load? 
			//Simple boolean return?
		}
	}
	
	@Override
	public void onAssign(ItemStack item, boolean unused) {
		if (item.getType().getMaxDurability() > 0)//TODO what now?;
			
		if (durabilityPercent >= 0.0)
			durability = (short) (item.getType().getMaxDurability() * durabilityPercent); 
		item.setDurability(durability);
	}
	
	@Override
	public void onRefactor(ItemStack item) {
		if (item.getType().getMaxDurability() > 0)//TODO what now?;
			
		durability = item.getDurability();
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
