package net.dandielo.core.items.serialize;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

import net.dandielo.core.exceptions.InvalidAttributeValueException;
import net.dandielo.core.items.dItem;

import org.bukkit.inventory.ItemStack;

/**
 * 
 * @author dandielo
 *
 * This class is a wrapper on the default ItemStack and it's ItemMeta classes, allowing more in-game customization and operations.
 * Each attribute is unique on ONE item, so no item may have 2 of the same attributes, however each attribute may consist of one 
 *   general key and several sub-keys allowing to make classes more flexible.</br>
 * Event what one attribute class does work as several "sub-key" attributes, their hash code is always different.</br></br>
 * 
 * Also keep in mind if you want to create a new attribute you need to create one or two constructors. </br></br>
 * If you are registering an general attribute then you need to add in your class a constructor with these params:</br>
 *   </br>(<strong>dItem, String</strong>)</br></br> and pass them to the super constructor.</br></br>
 * If you want your class to extend any other attribute (with sub-keys) you will need to have the following constructor:</br>
 *   </br>(<strong>ditem, String, String</strong>)
 * 
 * @note Remember to always use the @Attribute annotation when creating and registering a attribute class. 
 */
public abstract class ItemAttribute {
	/* protected members */
	protected final dItem item;
	protected final String key;
	protected final String sub;
	
	/* constructors */
	protected ItemAttribute(dItem item, String key) { 
		this(item, key, null);
	}
	protected ItemAttribute(dItem item, String key, String sub) {
		this.item = item;
		this.key = key;
		this.sub = sub;
	}
	
	/* Fighting with items */
	public void onAssign(ItemStack item, boolean abstrac) { }
	public void onRefactor(ItemStack item) throws InvalidAttributeValueException { }
		
	/* advanced interfaces */
	public ItemStack onNativeAssign(ItemStack item, boolean abstrac) {
		onAssign(item, abstrac);
		return item;
	}
	
	/* default operations */
	public String getKey() { return key; }
	public String getSubkey() { return sub; }
	
	/* Serialization */
	@Override
	public String toString() { return onSerialize(); }
	public abstract String onSerialize();
	public abstract void onLoad(String data);
	
	@Override
	public int hashCode() { return 0; }

	/* Static factory maps */
	private static Map<String, Attribute> attributeKeys;
	private static Map<Attribute, Class<? extends ItemAttribute>> attributeClasses;
	
	/* Static constructor */
	static { 
		attributeKeys = new HashMap<String, Attribute>();
		attributeClasses = new HashMap<Attribute, Class<? extends ItemAttribute>>();
	}
	
	/* Factories */
	public static ItemAttribute init(dItem item, Class<? extends ItemAttribute> clazz) {
		Attribute aInfo = clazz.getAnnotation(Attribute.class);
		ItemAttribute result = null;
		
		if (aInfo != null)
		{
			try {
				result = clazz.getConstructor(dItem.class, String.class).newInstance(item, aInfo.key());
			} catch(Exception e) {
				//some kind of error 
			}
		}
		return result;
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
					//some kind of error 
				}
			}
			else
			{		
				try {
					result = clazz.getConstructor(dItem.class, String.class, String.class)
							.newInstance(item, keyPair[0], keyPair[1]);
				} catch(Exception e) {
					//some kind of error 
				}
			}
			
			//if we got a valid attribute
			if (result != null)
				result.onLoad(value);
		}
		return result;
	}
}
