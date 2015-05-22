package net.dandielo.core.items.serialize.core;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import net.dandielo.core.items.dItem;
import net.dandielo.core.items.serialize.Attribute;
import net.dandielo.core.items.serialize.ItemAttribute;

@Attribute(key = "n", name = "name", priority = 300)
public class Name extends ItemAttribute {
	private String name;
	
	public Name(dItem item, String key) {
		super(item, key);
	}
	
	public String getValue() {
		return name;
	}
	
	public void setValue(String name) {
		this.name = name;
	}

	@Override
	public String serialize() {
		return name.replace('§', '&');
	}

	@Override
	public boolean deserialize(String data) {
		name = data.replace('&', '§');
		return true;
	}
	
	@Override
	public boolean onRefactor(ItemStack item) {
		ItemMeta meta = item.getItemMeta();
		if (!meta.hasDisplayName())
			return false;
		
		name = meta.getDisplayName();
		return true;
	}
	
	@Override
	public void onAssign(ItemStack item, boolean abstrac) {
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(name);
		item.setItemMeta(meta);
	}
	
	public boolean extendedCheck(ItemAttribute attr)
	{
		Matcher match = Pattern.compile(name).matcher(((Name)attr).name);
		return match.matches();
	}
	
	@Override
	public boolean similar(ItemAttribute attr)
	{			
		return equals(attr);
	}
	
	@Override
	public boolean equals(ItemAttribute attr)
	{
		return name.equals(((Name)attr).name);//item.hasFlag(Regex.class) ? extendedCheck(attr) : name.equals(((Name)attr).name);
	}
}
