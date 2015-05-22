package net.dandielo.core.items.serialize.flags;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import net.dandielo.core.bukkit.NBTUtils;
import net.dandielo.core.items.dItem;
import net.dandielo.core.items.serialize.Attribute;
import net.dandielo.core.items.serialize.ItemFlag;

@Attribute(name="Lore", key=".lore")
public class Lore extends ItemFlag {
	public static final String dCoreLorePrefix = "§3§d§d§f"; 
	private List<String> lore = new ArrayList<String>();

	public Lore(dItem item, String key) {
		super(item, key);
	}
	
	public void setLore(List<String> lore)
	{
		this.lore = new ArrayList<String>(lore.size());
		for (String unescaped : lore) {
			this.lore.add(escape(unescaped));
		}
	}
	
	public List<String> getRawLore()
	{
		return lore;
	}
	
	public static String escape(String lore) {
		return lore.replace('§', '^');
	}
	
	public static String unescape(String lore) {
		return lore.replace('^', '§').replace('&', '§');
	}

	@Override
	public void onAssign(ItemStack item, boolean unused)
	{
		//get the existing lore
		List<String> itemLore = item.getItemMeta().getLore();
		if ( itemLore == null )
			itemLore = new ArrayList<String>();
		
		//add this lore
		for ( String lore : this.lore )
			itemLore.add(unescape(lore));
		
		//save the new lore
		ItemMeta meta = item.getItemMeta();
		meta.setLore(itemLore);
		item.setItemMeta(meta);
	}
	
	public boolean onRefactor(ItemStack item) 
	{	
		if ( !item.getItemMeta().hasLore() )
			return false;
		
		//get the lore without any dtlTrader lore lines
		List<String> cleanedLore = cleanLore(item.getItemMeta().getLore());
		if ( cleanedLore.isEmpty() )
			return false;

		//set the new lore
		setLore(cleanedLore);
		return true;
	}

	public List<String> getLore() {
		//parse the whole lore
		List<String> itemLore = new ArrayList<String>();
		for (String lore : this.lore)
			itemLore.add(unescape(lore));
		//return the parsed lore
		return itemLore;
	}
	
	//this should be always 0 to be assigned first
	@Override
	public int hashCode()
	{
		return 0;
	}
	
	@Override
	public boolean equals(ItemFlag o)
	{		
		Lore itemLore = (Lore) o;
		//TODO: more thingies? if ( item.hasFlag(AnyLore.class) ) return true;
		if ( !(itemLore.lore == null && this.lore == null) && !(itemLore.lore != null && this.lore != null) ) return false;
		if ( itemLore.lore.size() != this.lore.size() ) return false;

		boolean equals = true;
		for ( int i = 0 ; i < itemLore.lore.size() && equals ; ++i )
			equals = itemLore.lore.get(i).equals(this.lore.get(i));
		return equals;
	}

	@Override
	public boolean similar(ItemFlag flag)
	{
		return equals(flag);
	}
	
	public static List<String> cleanLore(List<String> lore)
	{
		List<String> cleaned = new ArrayList<String>();
		for (String entry : lore)
			if ( !entry.startsWith(dCoreLorePrefix) ) {
				cleaned.add(entry);
			}
		return cleaned;
	}
	
	//static helper methods
	public static ItemStack addLore(ItemStack item, List<String> lore)
	{
		// Set this the raw way to avoid wiping other custom NBT data
		// Thanks! ;) - NathanWolf
		
		ItemStack newItem = NBTUtils.addLore(item, lore);
		if (newItem != null) {
			return newItem;
		}
		
		// If that fails, then add to existing lore via Bukkit API
		
		//get the lore
		ItemMeta meta = item.getItemMeta();	
		List<String> newLore = meta.getLore();	
		if (newLore == null) {
			newLore = new ArrayList<String>();
		}
		
		//add the new lore
		newLore.addAll(lore);
		meta.setLore(newLore);
		
		//create a new item
		newItem = item.clone();
		newItem.setItemMeta(meta);
		return newItem;
	}
	
	public static boolean hasTraderLore(ItemStack item)
	{
		if ( !item.hasItemMeta() || !item.getItemMeta().hasLore() ) return false;
		
		boolean has = false;
		for (String entry : item.getItemMeta().getLore())
			if ( !has && entry.startsWith(dCoreLorePrefix) )
				has = true;
		return has;
	}
	
}
