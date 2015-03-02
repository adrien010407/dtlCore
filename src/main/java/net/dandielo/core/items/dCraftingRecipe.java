package net.dandielo.core.items;

import org.bukkit.inventory.ItemStack;

public class dCraftingRecipe {
	/* constructors */
	public dCraftingRecipe() { }
	public dCraftingRecipe(dItem item) { }
	public dCraftingRecipe(ItemStack item) { }
	
	/* modifiers */
	public boolean isShapeless() { return false; }
	public void setShapless(boolean shapeless) { }
	
	public void setMatrix(String[] matrix) { }
	public String[] getMatrix() { return null; }
	
	public void setIndegrient(String key, dItem item) { }
	public dItem getIndegrient(String key) { return null; }
	
	/* checks and factories */
	public dItem craftUsing(dItem[] items) { return null; }
	public dItem craftUsing(ItemStack[] items) { return null; }

	public boolean meetsRequirements(dItem[] items) { return false; }
	public boolean meetsRequirements(ItemStack[] items) { return false; }
	
	@Override
	public boolean equals(Object that) { return false; }
	
	@Override
	public int hashCode() { return 0; }
}
