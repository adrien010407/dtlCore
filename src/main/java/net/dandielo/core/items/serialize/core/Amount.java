package net.dandielo.core.items.serialize.core;

import org.bukkit.inventory.ItemStack;

import net.dandielo.core.items.dItem;
import net.dandielo.core.items.serialize.Attribute;
import net.dandielo.core.items.serialize.ItemAttribute;

/**
 * Describes the items amount attribute. 
 * @author dandielo
 *
 */
@Attribute(key = "a", name = "Amount")
public class Amount extends ItemAttribute {
	private int amount;
	
	protected Amount(dItem item, String key) {
		super(item, key);
	}
	
	public int getValue() {
		return amount;
	}

	public void setValue(int amount) {
		this.amount = amount;
	}

	@Override
	public String serialize() {
		return String.valueOf(amount);
	}

	@Override
	public boolean onLoad(String data) {
		amount = Integer.parseInt(data);
		return true;
	}
	
	@Override
	public boolean onRefactor(ItemStack item) {
		amount = item.getAmount();
		return true;
	}
	
	@Override
	public void onAssign(ItemStack item, boolean abstrac) {
		item.setAmount(amount);
	}
}
