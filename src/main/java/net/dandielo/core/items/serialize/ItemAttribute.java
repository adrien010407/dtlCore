package net.dandielo.core.items.serialize;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.dandielo.core.exceptions.InvalidAttributeValueException;
import net.dandielo.core.items.dItem;

import org.bukkit.inventory.ItemStack;

/**
 * 
 * @author dandielo
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
	
	public Attribute getInfo() {
		//TODO maybe save it in this object for some reason?
		return getClass().getAnnotation(Attribute.class);
	}
	
	/* Serialization */
	@Override
	public String toString() { return serialize(); }
	public abstract String serialize();
	public abstract void onLoad(String data);

	public boolean equals(ItemAttribute that) {
		return key.equals(that.key);
	}
	public boolean similar(ItemAttribute that) {
		return equals(that);
	}
	
	@Override
	@SuppressWarnings("all")
	public final boolean equals(Object o)
	{
		return (
			o instanceof ItemAttribute 
			&& key.equals(((ItemAttribute)o).key) 
			&& (sub == null ? 
				/* if */ ((ItemAttribute)o).sub == null 
				/* else */ : sub.equals(((ItemAttribute)o).sub))
		);
	}
	
	@Override
	public int hashCode() { 
		return (key + (sub == null ? "" : "." + sub)).hashCode(); 
	}

	/* Static factory maps */
	private static Map<String, Attribute> attributeKeys;
	private static Map<Attribute, Class<? extends ItemAttribute>> attributeClasses;
	
	/* Static constructor */
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
	public static Set<ItemAttribute> getRequiredAttributes()
	{
		//create the list holding all attribute instances
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
	public static List<ItemAttribute> getAllAttributes(ItemStack item)
	{
		//create the list holding all attribute instances
		List<ItemAttribute> result = new ArrayList<ItemAttribute>();
		for ( Map.Entry<Attribute, Class<? extends ItemAttribute>> attr : attributeClasses.entrySet() )
		{
			Attribute attrInfo = attr.getKey();
			
			//check if we need this attribute
			if (attrInfo.required() || Arrays.binarySearch(attrInfo.items(), item.getType()) >= 0 ||
				(attrInfo.items().length == 0 && !attrInfo.standalone()))
			{
				try 
				{
					try {
						Constructor<? extends ItemAttribute> constr = attr.getValue()
								.getConstructor(dItem.class, String.class);
						if (constr != null)
						{
							ItemAttribute iAttr = constr.newInstance(attrInfo.key());
							result.add(iAttr);
						}
					}catch(NoSuchMethodException ex) {
					}

					//With subkeys
					for (String sub : attrInfo.sub())
					{
						ItemAttribute iAttr = attr.getValue()
								.getConstructor(dItem.class, String.class, String.class)
								.newInstance(attrInfo.key(), sub);
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
			
			//if we got a valid attribute
			if (result != null)
				result.onLoad(value);
		}
		return result;
	}
}
