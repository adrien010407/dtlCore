package net.dandielo.core.items.serialize.core;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.inventory.ItemStack;

import net.dandielo.core.items.dItem;
import net.dandielo.core.items.serialize.Attribute;
import net.dandielo.core.items.serialize.ItemAttribute;

/**
 * Describes the items amount attribute. 
 * @author dandielo
 *
 */
@Attribute(key = "a", name = "Amount", required = true, priority = 5)
public class Amount extends ItemAttribute {
	private List<Integer> amounts = new ArrayList<Integer>();
	
	public Amount(dItem item, String key) {
		super(item, key);
		amounts.add(1);
	}
	
	public int getAmount() {
		return amounts.get(0);
	}
	
	public int getAmount(int at) {
		return amounts.get(at);
	}

	public void addAmount(int a) {
		amounts.add(a);
	}

	public void setAmount(int amount) {
		amounts.set(0, amount);
	}

	public boolean hasMultipleAmounts() {
		return amounts.size() > 1;
	}

	public List<Integer> getAmounts() {
		return amounts;
	}

	@Override
	public String serialize() {
		String result = "";
		for ( int i = 0 ; i < amounts.size() ; ++i )
			result += amounts.get(i) + ( i + 1 < amounts.size() ? "," : "" );
		return result;
	}

	@Override
	public boolean deserialize(String data) {
		amounts.clear();
		try
		{
		    for ( String amout : data.split(",") )
			    amounts.add( Integer.parseInt(amout) < 1 ? 1 : Integer.parseInt(amout) );
		} 
		catch (NumberFormatException e)
		{
			return false;
		}
		return true;
	}
	
	@Override
	public boolean onRefactor(ItemStack item) {
		//amount = item.getAmount();
		amounts.clear();
		amounts.add(item.getAmount());
		return true;
	}
	
	@Override
	public void onAssign(ItemStack item, boolean abstrac) {
		item.setAmount(amounts.get(0));
	}
	
	public boolean equals(ItemAttribute that) {
		return (that == null ? false : ((Amount)that).getAmount() == this.getAmount());
	}
	
	public boolean similar(ItemAttribute that) {
		return true;//equals(that);
	}
}
