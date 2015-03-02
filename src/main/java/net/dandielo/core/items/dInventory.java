package net.dandielo.core.items;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

/**
 * Should this inventory be a wrapper over the default API Inventory or a "limitless" one?
 * Should i create for this purpose a Slot.class attribute?  
 * @author dandielo
 *
 */
public class dInventory implements InventoryHolder {
	private dItem[] vInventory;
	private List<dItem> items;
	
	/* constructors */
	public dInventory(int size) {
		items = new ArrayList<dItem>();
		vInventory = new dItem[size];
	}
	public dInventory(Inventory inventory, int size) { 
		this(Math.max(size, inventory.getSize()));
	}
	public dInventory(List<dItem> items, int size) { 
		this(Math.max(size, items.size()));
	}
	
	/* Native inventory */
	@Override
	public Inventory getInventory() { 
		Inventory result = Bukkit.createInventory(this, 6 * 9);
		return result;
	}
	
	/* managing a inventory */
	public void addItem(dItem item) { 
		items.add(item);
		
		//TODO if has slot -> replace on slot or do nothing
		//vInventory[item.getSlot()] = item;
	}
	public void addItem(ItemStack item) { 
		addItem(new dItem(item));
	}
	public void setItem(int at, dItem item) {
		items.add(item);
		
		//TODO replace on slot and set information
		//item.setSlot(at);
		//vInventory[at] = item;
	}
	public void setItem(int at, ItemStack item) {
		setItem(at, new dItem(item));
	}
	
	public dItem getItemAt(int at) { 
		//TODO returns an item, from the virtual inventory
		return vInventory[at];
	}
	public dItem removeAt(int at) { 
		//Removes from the virtual inventory
		dItem result = vInventory[at];
		vInventory[at] = null;
		//items[at] = null;
		//TODO remove slot information
		return result;
	}
	
	public int removeItem(dItem item) { return -1; } //will try to remove the requested amount using strong equality
	public int removeItem(ItemStack item) { return -1; } //will try to remove the requested item stack using strong equality
	
	public void clear() { }
	
	/* Native inventory information */
	public int nextEmptySlot() { 
		return 0;
	}
	
	/* checks and comparsion */	
	public int findItem(dItem item) { return -1; } //uses the strong equality
	public int findSimilarItem(dItem item) { return -1; } //uses a weak equality
	
	public boolean containsItem(dItem item) { return false; } //uses strong equality
	public boolean containsItem(ItemStack item) { return false; } //uses strong equality
	
	/* item information */
	public int totalAmountOf(dItem item) { return -1; } //uses strong equality
	public int totalAmountOf(ItemStack item) { return -1; } //uses strong equality
	
	/* serialization */
	@Override
	public String toString() { return serialize(); }
	public String serialize() { return null; }
	public void saveToFile(File file) { }
	public YamlConfiguration toYaml() { return null; }
	
	/* static constructors */
	public static dInventory fromFile(File file) { return null; }
	public static dInventory fromString(String str) { return null; }
	public static dInventory fromYaml(YamlConfiguration yaml) { return null; }
	
	/* build-in comparsion */
	@Override
	public boolean equals(Object that) { return false; }
	
	@Override
	public int hashCode() { return 0; }
}
