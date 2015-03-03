package net.dandielo.core.items;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import net.dandielo.core.items.serialize.ItemAttribute;
import net.dandielo.core.items.serialize.ItemFlag;
import net.dandielo.core.items.serialize.core.Amount;
import net.dandielo.core.items.serialize.core.Name;
import net.dandielo.core.items.serialize.flags.Lore;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;

/**
 * Component based item model.
 * @author dandielo
 *
 */
public class dItem {
	private Material material;
	private MaterialData materialData;
	
	private Set<ItemFlag> flags = new HashSet<ItemFlag>();;
	private Set<ItemAttribute> attributes = new HashSet<ItemAttribute>();;
	
	/**
	 * Creates a abstract item.
	 */
	public dItem() { }
	
	/**
	 * Creates an item refactoring the given item into pieces.
	 * @param item
	 *   The item that will be refactored into components.
	 */
	public dItem(ItemStack item) { 
		material = item.getType();
		materialData = item.getData();
	}
	
	/**
	 * Creates an item from the passed string. The string needs to follow specific rules.
	 * @param data
	 *   Serialized item data.
	 */
	public dItem(String data) { 
		load(data);
	}
	
	/**
	 * Creates an item from the passed string. The string needs to follow specific rules.
	 * @param data
	 *   Serialized item data.
	 * @param lore
	 *   Adds lore to the item that will be created from string data.
	 */
	public dItem(String data, List<String> lore) {
		load(data);
		
		if (hasFlag(Lore.class))
			getFlag(Lore.class, false).setValue(lore);
	}
	
	/**
	 * Serialized the item and all its components in a single string line.
	 * @return
	 *   The serialized item.
	 */
	public String serialize() { return null; }
	
	/**
	 * Loads from a string all item components and data. 
	 * @param data
	 *   Data to read from.
	 */
	public void load(String data) { }
	
	/**
	 * Create a native item stack from all components. 
	 * @return
	 *   The new ItemStack item.
	 */
	public ItemStack getItem() {
		return getItem(false);
	} 
	
	/**
	 * Create a native item stack from all components.
	 * @param abstrac
	 *   Specify if the item should be returned with abstract data, that normal wouldn't be there. For example debug data.
	 * @return
	 *   The new ItemStack item with additional data.
	 */
	public ItemStack getItem(boolean abstrac) {
		@SuppressWarnings("deprecation")
		ItemStack resultItem = new ItemStack(material, 1, materialData.getData());

		//add the lore as the first one
		if ( hasFlag(Lore.class) )
			getFlag(Lore.class, false).onAssign(resultItem, abstrac);

		List<ItemAttribute> firstPass = new ArrayList<ItemAttribute>();
		List<ItemAttribute> secondPass = new ArrayList<ItemAttribute>();
		for (ItemAttribute itemAttr : attributes)
		{
			if (itemAttr instanceof Name)// || itemAttr instanceof Skull || itemAttr instanceof StoredEnchant || itemAttr instanceof Book)
			{
				firstPass.add(itemAttr);
			} 
			else 
			{
				secondPass.add(itemAttr);
			}
		}

		for (ItemAttribute itemAttr : firstPass)
			resultItem = itemAttr.onNativeAssign(resultItem, abstrac);
		
		for (ItemAttribute itemAttr : secondPass)
			resultItem = itemAttr.onNativeAssign(resultItem, abstrac);

		for (ItemFlag flag : flags)
		{
			if ( !flag.getKey().equals(".lore") )
				resultItem = flag.onNativeAssign(resultItem, abstrac);
		}

		return resultItem; 
	}
	
	/**
	 * Returns the items amount.
	 * @return
	 *   Amount of the item,
	 */
	public int getAmount() { 
		return getAttribute(Amount.class, true).getValue(); 
	}
	
	/**
	 * Sets the items new amount
	 * @param amount
	 *   The amount to set.
	 */
	public void setAmount(int amount) {
		getAttribute(Amount.class, true).setValue(amount);
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
	@SuppressWarnings("deprecation")
	public int getTypeData() { return materialData.getData(); } //like wool colors
	
	/**
	 * Returns the items custom name
	 * <p>If no name is set then the material name is used instead.</p>
	 * 
	 * @return
	 *   The items name.
	 */
	public String getName() { 
		return hasAttribute(Name.class) ? getAttribute(Name.class, false).getValue() 
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
		getAttribute(Name.class, true).setValue(name);
	}
	
	/**
	 * Trying to access the lore of an item will result in initializing a lore flag.
	 * <p>Should it init that flag?</p> 
	 * @return
	 */
	public List<String> getLore() { 
		return hasFlag(Lore.class) ? getFlag(Lore.class, false).getValue() 
				/* else */: new ArrayList<String>(); 
	}
	
	/**
	 * Returns a items full description and applies the default lore to the end of the list.
	 * @return
	 *   Full items description.
	 */
	public List<String> getDescription() {
		List<String> result = new ArrayList<String>();
		result.addAll(getLore());
		return result; 
	} 
	
	/**
	 * Creates a new attributes with default values. 
	 * @param clazz
	 *   An attribute class that will be used to create the object.
	 * @return
	 *   The new created object.
	 */
	public <T extends ItemAttribute> T addAttribute(Class<T> clazz) {
		T attribute = ItemAttribute.init(this, clazz);
		attributes.remove(attribute);
		attributes.add(attribute);
		return attribute;
	}
	
	/**
	 * Creates an attribute using a valid registered key and data that will be used to load the attribute.
	 * @param key
	 *   The registered attribute key.
	 * @param value
	 *   The data to load the attribute.
	 */
	public void addAttribute(String key, String value) { 
		ItemAttribute attribute = ItemAttribute.init(this, key, value);
		attributes.remove(attribute);
		attributes.add(attribute);
	}
	
	/**
	 * Returns the attribute that is instance of this class. 
	 * <p>If no attribute of this class exists in this item the <b>create</b> param will be used
	 * to check if a new attribute should be created.</p>
	 *  
	 * @param clazz
	 *   The attribute class.
	 * @param create
	 *   Flag if a new attribute should be created if it's not found.
	 * @return
	 *   The corresponding attribute or <b>null</b> if nothing was found.
	 */
	@SuppressWarnings("unchecked")
	public <T extends ItemAttribute> T getAttribute(Class<T> clazz, boolean create) {
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
		if (create && result == null)
		{
			result = ItemAttribute.init(this, clazz);
			attributes.add(result);
		}
		return (T) result;
	} 

	/**
	 * Returns the attribute with the same key.
	 *  
	 * @param key
	 *   The Attribute key that should be found.
	 * @return
	 *   The corresponding attribute or <b>null</b> if nothing was found.
	 */
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

	/**
	 * Returns all attributes with the given general key.
	 * <p>The result will contain all subkeys of the given general key</p>
	 *  
	 * @param gkey
	 *   The Attribute general key. 
	 * @return
	 *   The corresponding attribute or <b>null</b> if nothing was found.
	 */
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
	
	/**
	 * Checks if the items has a specific attribute.
	 * @param clazz
	 *   The attribute class that should we check for,
	 * @return
	 *   <b>true</b> if the attribute was found.
	 */
	public boolean hasAttribute(Class<? extends ItemAttribute> clazz) { 
		return getAttribute(clazz, false) != null; 
	}
	
	/**
	 * Checks if an attribute with the specific key exists.
	 * @param key
	 *   The attribute key we are looking for.
	 * @return
	 *   <b>true</b> if we find a exact attribute.
	 */
	public boolean hasAttribute(String key) { 
		return getAttribute(key) != null; 
	} 
	
	/**
	 * Removes a attribute from this item.
	 * @param clazz
	 *   The class of the attribute that should be removed. 
	 */
	public void removeAttribute(Class<? extends ItemAttribute> clazz) {
		attributes.remove(getAttribute(clazz, false));
	} 
	
	/**
	 * Removes a attribute from this item.
	 * @param key
	 *   The key of the attribute that should be removed.
	 */
	public void removeAttribute(String key) { 
		attributes.remove(getAttribute(key));
	} 
	 
	/**
	 * Removes all attributes with the specified general key.
	 * @param gkey
	 *   The general key to be removed from this item.
	 */
	public void removeAttributes(String gkey) {
		for (ItemAttribute attribute : getAttributes(gkey))
			attributes.remove(attribute);
	} 
	
	/* Flag manipulation */
	public void addFlag(Class<? extends ItemFlag> clazz) { 
		flags.add(ItemFlag.init(this, clazz));
	}
	public void addFlag(String flag) {
		flags.add(ItemFlag.init(this, flag));
	} 
	
	public boolean hasFlag(Class<? extends ItemFlag> clazz) { 
		ItemAttribute result = null;
		Iterator<ItemAttribute> it = attributes.iterator();
		while(it.hasNext() && result == null)
		{
			result = it.next();
			if (!clazz.isInstance(result))
				result = null;
		}
		return result != null;
	}
	public boolean hasFlag(String flag) {
		ItemAttribute result = null;
		Iterator<ItemAttribute> it = attributes.iterator();
		while(it.hasNext() && result == null)
		{
			result = it.next();
			if (!result.getKey().equals(flag))
				result = null;
		}
		return result != null;
	}
	
	@SuppressWarnings("unchecked")
	public <T extends ItemFlag> T getFlag(Class<T> clazz, boolean create) {
		ItemFlag result = null;
		Iterator<ItemFlag> it = flags.iterator();
		while(it.hasNext() && result == null)
		{
			result = it.next();
			if (!clazz.isInstance(result))
				result = null;
		}
		if (create && result == null)
		{
			result = ItemFlag.init(this, clazz);
			flags.add(result);
		}
		return (T) result;
	}
	public ItemFlag getFlag(String flag) {
		ItemFlag result = null;
		Iterator<ItemFlag> it = flags.iterator();
		while(it.hasNext() && result == null)
		{
			result = it.next();
			if (!result.getKey().equals(flag))
				result = null;
		}
		return result;
	}
	
	public void removeFlag(Class<? extends ItemFlag> clazz) {
		flags.remove(getFlag(clazz, false));
	}
	
	public void removeFlag(String flag) {
		flags.remove(getFlag(flag));
	}
	
	/* Checks and comparsions */
	@Override
	public boolean equals(Object that) { return that instanceof dItem && equals((dItem)that); }
	public boolean equals(dItem that) { 
		boolean equals = material.equals(that.getMaterial());
		//TODO Durability! // equals &= !ItemUtils.itemHasDurability(item.item) ? item.item.getDurability() == this.item.getDurability() : equals; 

		if ( equals )
		{
			for (ItemAttribute itemAttr : attributes)
			{
				//TODO This loooks baaaaad!
				if (!equals) break;
				if (!itemAttr.getInfo().standalone())
				{
					for (ItemAttribute thatItemAttr : that.attributes)
					{
						if (itemAttr.getClass().equals(thatItemAttr.getClass()))
							equals &= itemAttr.equals(thatItemAttr);
					}
				}
			}

			//for each attribute in this item
			for (ItemFlag itemFlag : flags)
			{
				//TODO This loooks baaaaad!
				if (!equals) break;
				if (!itemFlag.getInfo().standalone())
				{
					for (ItemFlag thatItemFlag : that.flags)
						if (itemFlag.getClass().equals(thatItemFlag.getClass()))
							equals &= itemFlag.equals(thatItemFlag);
				}
			}
		}
		return equals;
	}
	
	public boolean similar(dItem that) {
		boolean equals = material.equals(that.getMaterial());
		
		//if equals check data, if not durability
		//TODO: durability // equals &= !ItemUtils.itemHasDurability(item.item) ? item.item.getDurability() == this.item.getDurability() : equals; 
		if (equals)
		{
			for (ItemAttribute itemAttr : attributes)
			{
				//TODO: WTF?!
				if (!equals) break;
				if (!itemAttr.getInfo().standalone())
				{
					for (ItemAttribute thatItemAttr : that.attributes)
					{
						if ( itemAttr.getClass().equals(thatItemAttr.getClass()) )
							equals &= itemAttr.similar(thatItemAttr);
					}
				}
			}
			
			//for each attribute in this item
			for (ItemFlag itemFlag : flags)
			{				
				//TODO: WTF?!
				if (!equals) break;
				if (!itemFlag.getInfo().standalone())
				{
					for (ItemFlag thatItemFlag : that.flags)
						if (itemFlag.getClass().equals(thatItemFlag.getClass()))
							equals &= itemFlag.similar(thatItemFlag);
				}
			}
		}
		return equals;
	} 
	
	/* Serialization */
	@Override
	public String toString() { return serialize(); }
	
	@Override
	public int hashCode() {
	    int hash = 7;

	    hash = 73 * hash + (this.material != null ? this.material.hashCode() : 0);
	    hash = 73 * hash + (this.materialData != null ? this.materialData.hashCode() : 0);
	    hash = 73 * hash + (this.attributes != null ? this.attributes.hashCode() : 0);
	    hash = 73 * hash + (this.flags != null ? this.flags.hashCode() : 0);
//	    hash = 73 * hash + (this.loreManaged ? 1 : 0); TODO?
	    
	    return hash;
	}
}

