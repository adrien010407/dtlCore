package net.dandielo.core.items;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.dandielo.core.bukkit.NBTUtils;
import net.dandielo.core.items.serialize.ItemAttribute;
import net.dandielo.core.items.serialize.ItemFlag;
import net.dandielo.core.items.serialize.core.Amount;
import net.dandielo.core.items.serialize.core.Banner;
import net.dandielo.core.items.serialize.core.Book;
import net.dandielo.core.items.serialize.core.Durability;
import net.dandielo.core.items.serialize.core.Name;
import net.dandielo.core.items.serialize.core.Skull;
import net.dandielo.core.items.serialize.core.StoredEnchant;
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
	
	protected Set<ItemFlag> flags = new HashSet<ItemFlag>();;
	protected Set<ItemAttribute> attributes = new HashSet<ItemAttribute>();;
	
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
		refactor(item);
	}
	
	/**
	 * Creates an item from the passed string. The string needs to follow specific rules.
	 * @param data
	 *   Serialized item data.
	 */
	public dItem(String data) { 
		deserialize(data);
	}
	
	/**
	 * Creates an item from the passed string. The string needs to follow specific rules.
	 * @param data
	 *   Serialized item data.
	 * @param lore
	 *   Adds lore to the item that will be created from string data.
	 */
	public dItem(String data, List<String> lore) {
		deserialize(data);
		
		if (hasFlag(Lore.class))
			getFlag(Lore.class, false).setLore(lore);
	}
	
	/**
	 * Serialized the item and all its components in a single string line.
	 * @return
	 *   The serialized item.
	 */
	@SuppressWarnings("deprecation")
	public String serialize() {
		String result = material.name().toLowerCase();
		
		if (material.getMaxDurability() == 0 && materialData.getData() != 0)
			result += ":" + materialData.getData();

		for (ItemAttribute entry : attributes)
			result += " " + entry.toString();

		for (ItemFlag flag : flags)
			result += " " + flag.getKey();		
		
		return result; 
		
	}
	
	/**
	 * Loads from a string all item components and data. 
	 * @param data
	 *   Data to read from.
	 */
	@SuppressWarnings("deprecation")
	public void deserialize(String data) {
		String[] itemData = data.split(" ", 2);
		String[] itemMaterial = itemData[0].split(":");
		
		clearItem();
		
		material = Material.getMaterial(itemMaterial[0].toUpperCase());
		if (itemMaterial.length > 1)
			materialData = new MaterialData(material, Byte.parseByte(itemMaterial[1]));//material.getNewData(Byte.parseByte(itemMaterial[1]));
		else
			materialData = new MaterialData(material);
		
		if ( itemData.length == 1 ) return;

		final String ITEM_PATTERN = "(([^ :]+):([^ :]+))|([^ :]*)";
		Matcher matcher = Pattern.compile(ITEM_PATTERN).matcher(itemData[1]);

		String key = "", value = "";
		while(matcher.find())
		{
			if (matcher.group(2) != null)
			{ 
				if (key.startsWith("."))
					addFlag(key);
				else  
				if (!key.isEmpty() && value != null)
					addAttribute(key, value.trim());
				
				//set new values
				key = matcher.group(2);
				value = matcher.group(3);
			}
			else
			if (matcher.group(4) != null)
			{
				if (matcher.group(4).startsWith("."))
				{
					if (key.startsWith("."))
						addFlag(key);
					else
					if (!key.isEmpty() && value != null)
						addAttribute(key, value.trim());
					
					//set new values
					key = matcher.group(4);
					value = "";
				}
				else if ( !matcher.group(4).isEmpty() )
				{
					value += " " + matcher.group(4);
				}
			}
		}
		if ( key.startsWith(".") )
			addFlag(key);
		else
		if (!key.isEmpty() && value != null)
			addAttribute(key, value.trim());
	}
	
	/**
	 * Tries to read all data associated with the given item.
	 * <p>If a attribute that may exist, is not found in the given item, this attribute will not be added to the component list.</p>
	 * @param item
	 *   The item that should be "took part by part".
	 */
	public void refactor(ItemStack item)
	{
		for (ItemAttribute iAttr : ItemAttribute.initAllAttributes(this))
		{
			if (iAttr.onRefactor(item))
			{
				attributes.add(iAttr);
			}
		}
		for (ItemFlag iFlag : ItemFlag.getAllFlags(this))
		{
			if(iFlag.onRefactor(item))
			{
				flags.add(iFlag);	
			}
		}

		//TODO: check if removing from the metadata the lore component it will preserve till this check. ?? WTF?!
		Lore lore  = (Lore) ItemFlag.init(this, ".lore");
		if(lore.onRefactor(item))
			flags.add(lore); 
	}
	
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
	 * @return
	 *   The new ItemStack item.
	 */
	public ItemStack getItem(boolean abstrac) {
		return getItem(abstrac, null);
	} 
	
	/**
	 * Create a native item stack from all components.
	 * @param abstrac
	 *   Specify if the item should be returned with abstract data, that normal wouldn't be there. For example debug data.
	 * @return
	 *   The new ItemStack item with additional data.
	 */
	public ItemStack getItem(boolean abstrac, List<String> lore) {
		ItemStack resultItem = materialData.toItemStack();
		
		//add the lore as the first one
		if (lore != null)
			resultItem = NBTUtils.addLore(resultItem, lore);
		else if (hasFlag(Lore.class))
			getFlag(Lore.class, false).onAssign(resultItem, abstrac);

		List<ItemAttribute> firstPass = new ArrayList<ItemAttribute>();
		List<ItemAttribute> secondPass = new ArrayList<ItemAttribute>();
		for (ItemAttribute itemAttr : attributes)
		{
			if (itemAttr instanceof Name           || 
				itemAttr instanceof Skull          || 
				itemAttr instanceof StoredEnchant  || 
				itemAttr instanceof Book           || 
				itemAttr instanceof Banner)
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
		return getAttribute(Amount.class, true).getAmount(); 
	}
	
	/**
	 * Sets the items new amount
	 * @param amount
	 *   The amount to set.
	 */
	public void setAmount(int amount) {
		getAttribute(Amount.class, true).setAmount(amount);
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
	 * Returns the material data based on the material. 
	 * @return
	 *   The items material data.
	 */
	public MaterialData getMaterialData() {
		return materialData;
	}
	
	/**
	 * Sets a new material for the item. 
	 * <p>This operation may remove attributes that are not compatible with the new material.</p> 
	 * @param material
	 *   The new material for the item.
	 */
	public void setMaterial(Material material) { 
		this.material = material;
		this.materialData = new MaterialData(material);//material.getNewData((byte)0);
	}

	
	/**
	 * Sets a new material for the item. 
	 * <p>This operation may remove attributes that are not compatible with the new material.</p> 
	 * @param material
	 *   The new material for the item.
	 */
	public void setMaterialData(MaterialData materialData) { 
		this.material = materialData.getItemType();
		this.materialData = materialData;
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
	public int getTypeData() { 
		return materialData.getData(); 
	} 
	
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
	 * Will return the items lore or a empty list. It won't add the lore component to the item.
	 * @return
	 */
	public List<String> getLore() { 
		return hasFlag(Lore.class) ? getFlag(Lore.class, false).getLore() 
				/* else */: new ArrayList<String>(); 
	}
	
	/**
	 * Returns a items full description and applies the default lore to the end of the list.
	 * @return
	 *   Full items description.
	 */
	public List<String> getDescription() {
		List<String> result = new ArrayList<String>();
		for (ItemAttribute attribute : attributes)
			attribute.getDescription(result);
		for (ItemFlag flag : flags)
			if (!(flag instanceof Lore))
				flag.getDescription(result);
		result.addAll(getLore());
		return result; 
	} 
	
	/**
	 * Returns the items durability, and only it's durability.
	 * <p>This attribute (unlike the native ItemStack version) does only return a values other that 0 if the item's material
	 * has a durability defined. MaterialData is not returned.</p>  
	 * @return
	 *   The items current durability.
	 */
	public int getDurability() {
		return hasAttribute(Durability.class) ? getAttribute(Durability.class, false).getValue() : 0;
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
			if (temp.getKey().equals(gkey))
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
	
	/**
	 * Adds a flag to the item.
	 * @param clazz
	 *   The class of the flag that should be added.
	 */
	public void addFlag(Class<? extends ItemFlag> clazz) { 
		flags.add(ItemFlag.init(this, clazz));
	}
	
	/**
	 * Adds a flag to the item.
	 * @param flag
	 *   The flag that should be added.
	 */
	public void addFlag(String flag) {
		flags.add(ItemFlag.init(this, flag));
	} 
	
	/**
	 * Checks if the item has a flag with the specific class.
	 * @param clazz
	 *   The class of the flag that we are looking for/
	 * @return
	 *   <b>true</b> if the flag was found.
	 */
	public boolean hasFlag(Class<? extends ItemFlag> clazz) { 
		return getFlag(clazz, false) != null;
	}
	
	/**
	 * Checks if the item has a flag with the specific key.
	 * @param flag
	 *   The flag key we are looking for
	 * @return
	 *   <b>true</b> if the flag was found.
	 */
	public boolean hasFlag(String flag) {
		return getFlag(flag) != null;
	}
	
	/**
	 * Returns the given flag if it's found.
	 * <p>If the create parameter is set to true, if the flag wasn't found a new one will be instantinated with default values, added to the items
	 * flags and then returned.</p>
	 * @param clazz
	 *   The class of the flag we want to get.
	 * @param create
	 *   If <b>true</b> it will create the flag if it's not found. 
	 * @return
	 *   The flag that we want to have returned.
	 */
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
	
	/**
	 * Returns the specified flag.
	 * @param flag
	 *   The flag key we are looking for.
	 * @return
	 *   The flag that was found or null otherwise.
	 */
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
	
	/**
	 * Removes a flag from the item.
	 * @param clazz
	 *   The class of the flag that should be removed.
	 */
	public void removeFlag(Class<? extends ItemFlag> clazz) {
		flags.remove(getFlag(clazz, false));
	}
	
	/**
	 * Removes a flag from the item.
	 * @param flag
	 *   The key of the flag that should be removed.
	 */
	public void removeFlag(String flag) {
		flags.remove(getFlag(flag));
	}
	
	/**
	 * Removes all attributes and flags from the item.
	 */
	public void clearItem() {
		attributes.clear();
		flags.clear();
	}
	
	/**
	 * Compares two items using the strict method.
	 */
	@Override
	public boolean equals(Object that) { return that instanceof dItem && equals((dItem)that); }
	
	/**
	 * Strict comparing, all values need to be equal. 
	 * @param that
	 *   The second item we are comparing against.
	 * @return
	 *   <b>true</> if both items are equal.
	 */
	public boolean equals(dItem that) { 
		boolean equals = material.equals(that.getMaterial());
		
		equals &= material.getMaxDurability() == 0 ? materialData.equals(that.materialData) : true;  
		
		if (equals)
		{
			for (ItemAttribute itemAttr : attributes)
			{
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

	/**
	 * Weak comparing, attributes needs to be the same, but values of attributes may differ. 
	 * @param that
	 *   The second item we are comparing against.
	 * @return
	 *   <b>true</> if both items are equal.
	 */
	public boolean similar(dItem that) {
		boolean equals = material.equals(that.getMaterial());

		equals &= material.getMaxDurability() == 0 ? materialData.equals(that.materialData) : true;  
		
		if (equals)
		{
			for (ItemAttribute itemAttr : attributes)
			{
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
	
	public final int priorityMatch(dItem that)
	{
		int priority = 0;
		
		if (material.getMaxDurability() == 0)
		{
			if (!material.equals(Material.AIR))
			{
				priority += materialData.equals(that.materialData) ? 130 : -2;
			}
			else 
			{
				priority += this.getDurability() == that.getDurability() ? 120 : -2;
			}
		}
		else
		{
			if (!material.equals(Material.AIR))
			{
				priority += material.equals(that.material) && materialData.equals(that.materialData) ? 130 : -2;
			}
		}
		
		//now a if block to not make thousands of not needed checks 
		if ( priority < 0 ) return priority;

		for (ItemAttribute thisItemAttr : attributes)
		{
			if (!thisItemAttr.getInfo().standalone())
			{
				for (ItemAttribute thatItemAttr : that.attributes)
				{
					if (thisItemAttr.getClass().equals(thatItemAttr.getClass()) && thisItemAttr.equals(thatItemAttr))
						priority += thisItemAttr.getInfo().priority();
				}
			}
		}

		for (ItemFlag thisItemFlag : flags)
		{
			if (!thisItemFlag.getInfo().standalone())
			{
				for (ItemFlag thatItemFlag : that.flags)
				{
					if (thisItemFlag.getClass().equals(thatItemFlag.getClass()) && thisItemFlag.equals(thatItemFlag))
						priority += thisItemFlag.getInfo().priority();
				}
			}
		}

		return priority;
	}
	
	/**
	 * Uses the {@code serialize} method.
	 */
	@Override
	public String toString() { return serialize(); }
	
	@Override
	public int hashCode() {
	    int hash = 7;

	    hash = 73 * hash + (this.material != null ? this.material.hashCode() : 0);
	    hash = 73 * hash + (this.materialData != null ? this.materialData.hashCode() : 0);
	    hash = 73 * hash + (this.attributes != null ? this.attributes.hashCode() : 0);
	    hash = 73 * hash + (this.flags != null ? this.flags.hashCode() : 0);
	    
	    return hash;
	}
}

