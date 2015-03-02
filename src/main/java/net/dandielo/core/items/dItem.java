package net.dandielo.core.items;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import net.dandielo.core.items.serialize.ItemAttribute;
import net.dandielo.core.items.serialize.ItemFlag;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;

public class dItem {
	// The items material and data
	private Material material;
	private MaterialData data;
	
	// all attributes and flags
	private Set<ItemFlag> flags;
	private Set<ItemAttribute> attributes;
	
	/* Constructors */
	public dItem() {
		flags = new HashSet<ItemFlag>();
		attributes = new HashSet<ItemAttribute>();
	}
	public dItem(ItemStack item) { 
		this();
		material = item.getType();
		data = item.getData();
	}
	public dItem(String data) { 
		this();
		load(data);
	}
	public dItem(String data, List<String> lore) {
		this();
		load(data);
		//TODO add the lore
	}
	
	/* Item generation */
	public ItemStack getItem() {
		ItemStack is = new ItemStack(material);
		is.setData(data);
		
		for (ItemAttribute attribute : attributes)
			is = attribute.onNativeAssign(is, true);
		
		for (ItemFlag flag : flags)
			is = flag.onNativeAssign(is, true);
		
		//if (hasFlag(Lore.class))		
		return is;
	} //does not has stand-alone attributes items
	
	public ItemStack getAbstractItem() {
		ItemStack is = new ItemStack(material);
		is.setData(data);
		
		for (ItemAttribute attribute : attributes)
			is = attribute.onNativeAssign(is, false);
		
		for (ItemFlag flag : flags)
			is = flag.onNativeAssign(is, false);
		
		//if (hasFlag(Lore.class))		
		return is; 
	}
	
	/* Item methods */
	public int getAmount() { return 0; }
	public void setAmount(int amount) { }
	
	public Material getMaterial() { 
		return material; 
	}
	public void setMaterial(Material material) { 
		this.material = material; 
	}
	
	@Deprecated()
	public int getTypeId() { 
		return material.getId(); 
	} 
	public int getItemId() { return 0; } //uses the id attribute
	
	public int getTypeData() { return data.getData(); } //like wool colors
	
	public String getName() { return null; } //returns the items name or material name instead (lower case)
	public void setName(String name) { } //Sets a custom name for an item
	
	public List<String> getLore() { return null; }
	public List<String> getDescription() { return null; } //adds stand-alone attribute descriptions
	
	/* Attribute manipulation */
	@SuppressWarnings("unchecked")
	public <T extends ItemAttribute> T addAttribute(Class<T> clazz) {
		T attribute = (T) ItemAttribute.init(this, clazz);
		attributes.remove(attribute);
		attributes.add(attribute);
		return attribute;
	}
	public void addAttribute(String key, String value) { 
		ItemAttribute attribute = ItemAttribute.init(this, key, value);
		attributes.remove(attribute);
		attributes.add(attribute);
	}
	public void addAttribute(String key, String sub, String value) { 
		ItemAttribute attribute = ItemAttribute.init(this, key + "." + sub, value);
		attributes.remove(attribute);
		attributes.add(attribute);
	}
	
	public ItemAttribute getAttribute(Class<? extends ItemAttribute> clazz) {
		ItemAttribute result = null;
		Iterator<ItemAttribute> it = attributes.iterator();
		while(it.hasNext() && result == null)
		{
			result = it.next();
			if (result.getSubkey() != null)
				result = null;
			else
			if (!clazz.isInstance(result))
				result = null;
		}
		return result;
	} 
	public ItemAttribute getAttribute(String key) { 
		ItemAttribute result = null;
		Iterator<ItemAttribute> it = attributes.iterator();
		while(it.hasNext() && result == null)
		{
			result = it.next();
			String iKey = result.getKey();
			String iSub = result.getSubkey();
			
			if (!key.equals(iKey) 
					&& (iSub == null || !key.equals(iKey + "." + iSub)))
				result = null;
		}
		return result; 
	} 
	public Set<ItemAttribute> getAttributes(String gkey) {
		ItemAttribute temp = null;
		Set<ItemAttribute> result = new HashSet<ItemAttribute>(); 
		Iterator<ItemAttribute> it = attributes.iterator();
		while(it.hasNext())
		{
			temp = it.next();
			if (temp.getKey() == gkey)
				result.add(temp);
		}
		return result;
	}
	
	public boolean hasAttribute(Class<? extends ItemAttribute> clazz) { 
		return getAttribute(clazz) != null; 
	}
	public boolean hasAttribute(String key) { 
		return getAttribute(key) != null; 
	} 
	
	public void removeAttribute(Class<? extends ItemAttribute> clazz) {
		attributes.remove(getAttribute(clazz));
	} 
	public void removeAttribute(String key) { 
		attributes.remove(getAttribute(key));
	} 
	public void removeAttributes(String gkey) {
		for (ItemAttribute attribute : getAttributes(gkey))
			attributes.remove(attribute);
	} 
	
	/* Flag manipulation */
	public void addFlag(Class<? extends ItemFlag> clazz) { }
	public void addFlag(String flag) { } //may pass versions without the dot, but will not check if they are valid
	
	public boolean hasFlag(Class<? extends ItemFlag> clazz) { return false; }
	public boolean hasFlag(String flag) { return false; }
	
	public void removeFlag(Class<? extends ItemFlag> clazz) { }
	public void removeFlag(String flag) { }
	
	/* Checks and comparsions */
	@Override
	public boolean equals(Object that) { return equals((dItem)that); }
	public boolean equals(dItem that) { return false; } //Bypasses stand-alone attributes
	public boolean similar(dItem item) { return false; } //Allows for weaker equality, for example if attributes exists only
	
	/* Serialization */
	@Override
	public String toString() { return serialize(); }
	public String serialize() { return null; }
	public void load(String data) { }
	
	@Override
	public int hashCode() { return 0; }
}

