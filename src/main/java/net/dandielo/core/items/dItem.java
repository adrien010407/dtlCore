package net.dandielo.core.items;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import net.dandielo.core.items.serialize.ItemAttribute;
import net.dandielo.core.items.serialize.ItemFlag;
import net.dandielo.core.items.serialize.core.Amount;
import net.dandielo.core.items.serialize.core.Name;

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
	} 
	
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
	
	/**
	 * Returns the items amount.
	 * @return
	 *   Amount of the item,
	 */
	public int getAmount() { 
		return getAttribute(Amount.class).getValue(); 
	}
	
	/**
	 * Sets the items new amount
	 * @param amount
	 *   The amount to set.
	 */
	public void setAmount(int amount) {
		getAttribute(Amount.class).setValue(amount);
	}
	
	/**
	 * Returns the current material of this item.
	 * @return
	 *   The items current material.
	 */
	public Material getMaterial() { 
		return material; 
	}
	
	/**
	 * Sets a new material for the item. 
	 * <p>This operation may remove attributes that are not compatible with the new material.</p> 
	 * @param material
	 *   The new material for the item.
	 */
	public void setMaterial(Material material) { 
		this.material = material; 
	}

	/**
	 * Returns the items material ID. 
	 * <p>This shouldn't be used anymore as it's likely to be removed in the near future.</p>
	 *  
	 * @deprecated Magic value
	 * @return
	 *   The items Material ID
	 */
	public int getTypeId() { 
		return material.getId(); 
	} 
	
	/**
	 * Returns the materials additional data value.
	 * @return
	 *   The materials data value.
	 */
	public int getTypeData() { return data.getData(); } //like wool colors
	
	/**
	 * Returns the items custom name
	 * <p>If no name is set then the material name is used instead.</p>
	 * 
	 * @return
	 *   The items name.
	 */
	public String getName() { 
		return hasAttribute(Name.class) ? getAttribute(Name.class).getValue() 
				/* else */ : material.name().toLowerCase(); 
	} 
	
	/**
	 * Sets a new custom name for the item.
	 * <p>If the name attribute is not preset to set the new custom name, a new attribute will be created automatically.</p>
	 * <p>Also you should pass a prepared string that already contains the '<b>§</b>' character as 
	 * the color-code prefix.</p>
	 * @param name
	 *   The items new custom name.
	 */
	public void setName(String name) { 
		if (hasAttribute(Name.class))
			getAttribute(Name.class).setValue(name);
		else
			addAttribute(Name.class).setValue(name);
	}
	
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

	@SuppressWarnings("unchecked")
	public <T extends ItemAttribute> T getAttribute(Class<T> clazz) {
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
		return (T) result;
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

