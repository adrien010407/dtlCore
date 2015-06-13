package net.dandielo.core.items.serialize;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.dandielo.core.exceptions.InvalidAttributeValueException;
import net.dandielo.core.items.dItem;
import net.dandielo.core.items.serialize.flags.Lore;

import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;

/**
 * Flags and item for specific data cases. Flagged items can be checked fery fast and allow to confgure
 * basic custom features.
 * 
 * @author dandielo
 */
public abstract class ItemFlag {
	
	/**
	 * Attribute key, used for saving and identification (is Unique)
	 */
	protected final String key;
	
	/**
	 * The item associated with the attribute
	 */
	protected dItem item;
	
	/**
	 * default constructor (needs a key)
	 * @param key
	 *     the flag key
	 */
	public ItemFlag(dItem item, String key) {
		this.item = item;
		this.key = key;
	}
	
	/**
	 * Called when the given item needs attributes re-set
	 * @return 
	 *     The updated item
	 * @param item
	 *     The item for which we set the attribute values
	 * @param abstrac
	 *     tells the method if the item is just displayed in the traders inventory or if it's the users end-item he bought  
	 */
	public ItemStack onNativeAssign(ItemStack item, boolean abstrac) {
		onAssign(item, abstrac);
		return item;
	}

	/**
	 * Called when the given item needs flags re-set
	 * @param item
	 *     The item for which we set the flag values
	 * @param abstrac 
	 *     tells the method if the item is just displayed in the traders inventory or if it's the users end-item he bought
	 */
	public void onAssign(ItemStack item, boolean abstrac) { }
	
	/**
	 * Called when trying to get flag data information from the given item. If no valid data for this flag is found then it throws an exception.
	 * @param item
	 * @return 
	 * @throws InvalidAttributeValueException
	 */
	public boolean onRefactor(ItemStack item) {
		return false;
	}
	 
	/* When we want to describe an item */
	public void getDescription(List<String> result) {
	}
	
	/**
	 * Called when a week equality is needed. Allows sometimes a value to be in range of another value, used for priority requests
	 * @return
	 *    true when equal, false instead 
	 */
	public boolean equals(ItemFlag that) {
		return key.equals(that.key);
	}
	public boolean similar(ItemFlag that) {
		return equals(that);
	}

	public Attribute getInfo() {
		return getClass().getAnnotation(Attribute.class);
	}
	
	/**
	 * @return returns the flags save string.
	 */
	@Override
	public final String toString()
	{
		return key;
	}
	
	/**
	 * @return
	 *     the flags unique key
	 */
	public String getKey() {
		return key;
	}
	
	@Override
	public int hashCode()
	{
		return key.hashCode();
	}
	
	@Override
	public final boolean equals(Object o)
	{
		return (o instanceof ItemFlag && key.equals(((ItemFlag)o).key));
	}
	
	
	
	
	//getting item datas
	private final static Map<Attribute, Class<? extends ItemFlag>> flags = new HashMap<Attribute, Class<? extends ItemFlag>>();
	
	/**
	 * Returns all flag instances in a list. This list is used later to factorize data from a item.
	 * @return
	 *     A list of each flag instance
	 */
	public static List<ItemFlag> getAllFlags(ItemStack item)
	{
		//create the list holding all flag instances
		List<ItemFlag> result = new ArrayList<ItemFlag>();
		for ( Map.Entry<Attribute, Class<? extends ItemFlag>> flag : flags.entrySet() )
		{
			//we don't want the lore flag in here
			if ( flag.getValue().equals(Lore.class) ) continue;

			Attribute attrInfo = flag.getKey();
			
			//check if we need this flag
			if (attrInfo.required() || Arrays.binarySearch(attrInfo.items(), item.getType()) >= 0 ||
					(attrInfo.items().length == 0 && !attrInfo.standalone()))
			{
				try 
				{
					ItemFlag iFlag = flag.getValue().getConstructor(String.class).newInstance(flag.getKey().key());
					result.add(iFlag);
				} 
				catch (Exception e)
				{
				}
			}
		}
		return result;
	}
	
	/**
	 * Registers a new flag to the system, should be done before Citizens2 loading.
	 * @param clazz
	 *     The falg class that should be registered.
	 * @throws InvalidDataNodeException
	 */
	public static void registerFlag(Class<? extends ItemFlag> clazz)
	{
	//	if ( !clazz.isAnnotationPresent(Attribute.class) )
	//		throw new AttributeInvalidClassException();
		
		Attribute attr = clazz.getAnnotation(Attribute.class);

		//debug low
	//	dB.low("Registering flag \'", ChatColor.GREEN, attr.name(), ChatColor.RESET, "\' with key: ", attr.key());
		
		flags.put(attr, clazz);
	}
	
	/**
	 * Creates a flag based on the key. 
	 * @param stockItem
	 *     The item associated with the flag
	 * @param key
	 *     The flag key, this is the unique key for each flag.
	 * @return
	 *     Returns the initialized flag if successful.
	 * @throws AttributeInvalidClassException 
	 * @throws AttributeInvalidValueException 
	 */
	public static ItemFlag init(dItem stockItem, String key)
	{
		//Search for the attribute
		Attribute attr = null;
		for ( Attribute attrEntry : flags.keySet() )
			if ( attrEntry.key().equals(key) )
				attr = attrEntry;
		
		try 
		{
			//get the attribute declaring class
			ItemFlag itemflag = flags.get(attr).getConstructor(String.class).newInstance(key);
			//assoc the item
			itemflag.item = stockItem;
			//returning the initialized attribute
			return itemflag;
		} 
		catch (Exception e)
		{
		} 
		return null;
	}

	/**
	 * Creates a flag based on the key. 
	 * @param stockItem
	 *     The item associated with the flag
	 * @param key
	 *     The flag key, this is the unique key for each flag.
	 * @return
	 *     Returns the initialized flag if successful.
	 * @throws AttributeInvalidClassException 
	 * @throws AttributeInvalidValueException 
	 */
	public static ItemFlag init(dItem stockItem, Class<? extends ItemFlag> clazz)
	{
		//Search for the attribute
		Attribute attr = clazz.getAnnotation(Attribute.class);
		
		try 
		{
			//get the attribute declaring class
			ItemFlag itemflag = clazz.getConstructor(dItem.class, String.class).newInstance(attr.key());
			//assoc the item
			itemflag.item = stockItem;
			//returning the initialized attribute
			return itemflag;
		} 
		catch (Exception e)
		{
		} 
		return null;
	}

	/**
	 * Registers all core flags
	 */
	public static void registerCoreFlags()
	{
		try 
		{
			registerFlag(Lore.class);
		} 
		catch (Exception e) 
		{
		}
	}
	
	protected static String flagsAsString()
	{
		String result = "";
		//format the string
		for ( Attribute attr : flags.keySet() )
			result += ", " + ChatColor.YELLOW + attr.name() + ChatColor.RESET;
		
		return ChatColor.WHITE + "[" + result.substring(2) + ChatColor.WHITE + "]";
	}
}