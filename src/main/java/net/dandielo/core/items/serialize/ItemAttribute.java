package net.dandielo.core.items.serialize;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.dandielo.core.bukkit.dtlCore;
import net.dandielo.core.items.dItem;

import org.bukkit.inventory.ItemStack;

/**
 * 
 * @author dandielo
 *
 * @note Remember to always use the @Attribute annotation when creating and registering a attribute class. 
 */
public abstract class ItemAttribute {
	protected final dItem item;
	protected final String key;
	protected final String sub;
	
	protected ItemAttribute(dItem item, String key) { 
		this(item, key, null);
	}
	protected ItemAttribute(dItem item, String key, String sub) {
		this.item = item;
		this.key = key;
		this.sub = sub;
	}
	
	/**
	 * Called when the a result item is assembled.
	 * @param item
	 *   The result that will receive the data.
	 * @param abstrac
	 *   if <b>true</b> the item may contain additional data, not really connected with the item. 
	 */
	public void onAssign(ItemStack item, boolean abstrac) { }
	
	/**
	 * Called when an items is analyzed to get all it's attributes.
	 * @param item
	 *   The item to be analyzed.
	 * @return
	 *   <b>true</b> if the item contains the given attribute.
	 */
	public boolean onRefactor(ItemStack item) { 
		return false; 
	}
		
	/**
	 * Called when the result item is assembled. 
	 * <p>This function should be used by attributes that modify the NBT structure of an item, because such a item is almost always a
	 * copy of the original. So that copy will be substituted with the original one after this function finishes.</p>  
	 * @param item
	 *   Then item that will receive the data.
	 * @param abstrac
	 *   if <b>true</> the item may contain additional data, not really connected with the item. 
	 * @return
	 *   The new item to be substituted.
	 */
	public ItemStack onNativeAssign(ItemStack item, boolean abstrac) {
		onAssign(item, abstrac);
		return item;
	}
	 
	/**
	 * Should return additional information about the attribute, it's only used for debug information.
	 * @param description
	 *   A list where the description should be added.
	 */
	public void getDescription(List<String> description) {
	}
	
	/**
	 * The attribute key. 
	 * @return
	 *   A string with the attribute general key.
	 */
	public String getKey() { return key; }
	
	/**
	 * The attribute subkey.
	 * @return
	 *   The subkey of an attribute or null if it's a general attribute. //TODO change the name from general to whateva
	 */
	public String getSubkey() { return sub; }
	
	/**
	 * The attributes description.
	 * @return
	 *   Returns the attributes {@code @Attribute} annotation, or null if not found. 
	 */
	public Attribute getInfo() {
		return getClass().getAnnotation(Attribute.class);
	}
	
	/**
	 * Uses the {@code serialize} method to return a combination along with the key and subkey values. 
	 */
	@Override
	public final String toString() { 
		return key + (sub != null ? "." + sub : "") + ":" + serialize(); 
	}
	
	/**
	 * Serializes the attribute.
	 * @return
	 *   The attributes string representation.
	 */
	public abstract String serialize();
	
	/**
	 * Deserializes all data from the given string.
	 * @param data
	 *   The serialized item attribute data.
	 * @return
	 *   <b>true</b> if the serialization was successful.
	 */
	public abstract boolean deserialize(String data);

	/**
	 * Checks if both attributes are strict equal
	 * @param that
	 *   The second item.
	 * @return
	 *   <b>true</b> if equal.
	 */
	public boolean equals(ItemAttribute that) {
		return key.equals(that.key);
	}
	
	/**
	 * Checks if both attributes are similar. 
	 * <p>Allows for smooth differences in the attributes values.</p> 
	 * @param that
	 *   The second item.
	 * @return
	 *   <b>true</b> if similar.
	 */
	public boolean similar(ItemAttribute that) {
		return equals(that);
	}
	
	/**
	 * Unlike the two other comparisons this one is strictly implemented to allow only one object of this key-subkey pairs in a 
	 * HashSet or HashMap. So there are never two of the same attributes on one item.
	 * <p>Only the <b>key</b> and <b>subkey</b> values are compared</p>
	 */
	@Override
	public final boolean equals(Object that) {
		return (
			that instanceof ItemAttribute 
			&& key.equals(((ItemAttribute)that).key) 
			&& (sub == null ? 
				/* if */ ((ItemAttribute)that).sub == null 
				/* else */ : sub.equals(((ItemAttribute)that).sub))
		);
	}
	
	@Override
	public int hashCode() { 
		return (key + (sub == null ? "" : "." + sub)).hashCode(); 
	}

	
	/*
	 * Static section of the ItemAttribute class, used to instantiate attributes in a proper way. 
	 */
	private static Map<String, Attribute> attributeKeys;
	private static Map<Attribute, Class<? extends ItemAttribute>> attributeClasses;
	
	static { 
		attributeKeys = new HashMap<String, Attribute>();
		attributeClasses = new HashMap<Attribute, Class<? extends ItemAttribute>>();
	}
	
	/**
	 * Returns required attribute instances in a list. This list is used later to init any item with required attributes.
	 * Initial attributes do not contain sub-attributes.
	 * @return
	 *     A list of each attribute instance
	 */
	public static Set<ItemAttribute> getRequiredAttributes() {
		Set<ItemAttribute> result = new HashSet<ItemAttribute>();
		for ( Map.Entry<Attribute, Class<? extends ItemAttribute>> attr : attributeClasses.entrySet() )
		{
			try 
			{
				if ( attr.getKey().required() )
				{
					ItemAttribute attrInstance = attr.getValue().getConstructor(dItem.class, String.class).newInstance(attr.getKey().key());
					result.add(attrInstance);
				}
			} 
			catch (Exception e)
			{
				//TODO error information
			}
		}
		return result;
	}
	
	/**
	 * Returns all attribute instances in a list. This list is used later to factorize data from a item.
	 * @param item
	 *     The item thats material will be checked for getting all attributes for it.</br>
	 *     Set to <strong>null</strong> if you want to get ALL attributes
	 * @return
	 *     A list of each attribute instance
	 */
	public static List<ItemAttribute> initAllAttributes(dItem item) {
		List<ItemAttribute> result = new ArrayList<ItemAttribute>();
		for (Map.Entry<Attribute, Class<? extends ItemAttribute>> attributeEntry : attributeClasses.entrySet())
		{
			Attribute attrInfo = attributeEntry.getKey();
			
			//check if we need this attribute
			if (attrInfo.required() || Arrays.binarySearch(attrInfo.items(), item.getMaterial()) >= 0 ||
				(attrInfo.items().length == 0 && !attrInfo.standalone()))
			{
				try 
				{
					try {
						Constructor<? extends ItemAttribute> constr = attributeEntry.getValue()
								.getConstructor(dItem.class, String.class);

						ItemAttribute itemAttribute = constr.newInstance(item, attrInfo.key());
						result.add(itemAttribute);
					}catch(NoSuchMethodException ex) {
					}

					//With subkeys
					for (String sub : attrInfo.sub())
					{
						ItemAttribute iAttr = attributeEntry.getValue()
								.getConstructor(dItem.class, String.class, String.class)
								.newInstance(item, attrInfo.key(), sub);
						result.add(iAttr);
					}
				} 
				catch (Exception e)
				{
				}
			}
		}
		return result;
	}
	
	/* Factories */
	@SuppressWarnings("unchecked")
	public static <T extends ItemAttribute> T init(dItem item, Class<T> clazz) {
		Attribute aInfo = clazz.getAnnotation(Attribute.class);
		ItemAttribute result = null;
		
		if (aInfo != null)
		{
			try {
				result = clazz.getConstructor(dItem.class, String.class).newInstance(item, aInfo.key());
			} catch(Exception e) {
				//TODO on exception
			}
		}
		return (T) result;
	}
	
	public static ItemAttribute init(dItem item, String key, String value) {
		Attribute aInfo = attributeKeys.get(key);
		Class<? extends ItemAttribute> clazz;
		ItemAttribute result = null;
		
		if (aInfo != null && (clazz = attributeClasses.get(aInfo)) != null)
		{
			String[] keyPair = key.split("\\.");
			if (keyPair.length == 1)
			{			
				try {
					result = clazz.getConstructor(dItem.class, String.class)
							.newInstance(item, keyPair[0]);
				} catch(Exception e) {
					//TODO some kind of error 
				}
			}
			else
			{		
				try {
					result = clazz.getConstructor(dItem.class, String.class, String.class)
							.newInstance(item, keyPair[0], keyPair[1]);
				} catch(Exception e) {
					//TODO some kind of error 
				}
			}
			
			if (result != null && !result.deserialize(value))
				result = null;
		}
		return result;
	}
	
	/**
	 * Registers a new attribute to the system, should be done before Citizens2 loading.
	 * @param clazz
	 *     The attribute class that should be registered.
	 * @throws InvalidDataNodeException
	 */
	public static void registerAttr(Class<? extends ItemAttribute> clazz) 
	{
		if ( !clazz.isAnnotationPresent(Attribute.class) ) {
			dtlCore.warning("Couldnt register the following attribute class: " + clazz.getSimpleName());
			return;
		}

		Attribute attr = clazz.getAnnotation(Attribute.class);

		attributeClasses.put(attr, clazz);

		//create all key pairs
		attributeKeys.put(attr.key(), attr);
		for (String sub : attr.sub())
			attributeKeys.put(attr.key() + "." + sub, attr);
	}
}
