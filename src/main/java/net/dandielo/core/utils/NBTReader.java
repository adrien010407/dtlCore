package net.dandielo.core.utils;

import org.bukkit.craftbukkit.v1_10_R1.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_10_R1.inventory.CraftItemStack;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;

import net.minecraft.server.v1_10_R1.NBTBase;
import net.minecraft.server.v1_10_R1.NBTTagCompound;
import net.minecraft.server.v1_10_R1.NBTTagList;
import net.minecraft.server.v1_10_R1.NBTTagString;


public class NBTReader {
	public static enum NBTTagType { 
		UNKNOWN(-1)
		, END(0)
		, BYTE(1)
		, SHORT(2)
		, INT(3)
		, LONG(4)
		, FLOAT(5)
		, DOUBLE(6)
		, BYTE_ARRAY(7)
		, STRING(8)
		, LIST(9)
		, COMPOUND(10)
		, INT_ARRAY(11)
		;
		
		public int ID;
		NBTTagType(int typeID)
		{
			ID = typeID;
		}
	}
	private static enum ObjectType { Unknown, Item, Entity, NBTTag, NBTList, };
	private ObjectType objectType = ObjectType.Unknown;
	private Object object;
	
	public NBTReader(ItemStack item)
	{
		objectType = ObjectType.Item;
		object = CraftItemStack.asNMSCopy(item);

		if (asNativeItemStack().getTag() == null)
			asNativeItemStack().setTag(new NBTTagCompound());
	}
	
	public NBTReader(Entity entity)
	{
		objectType = ObjectType.Entity;
		object = ((CraftEntity)entity).getHandle();
	}

	public NBTReader(NBTTagCompound nbtTag)
	{
		objectType = ObjectType.NBTTag;
		object = nbtTag;
	}

	public NBTReader(NBTTagList nbtTagList)
	{
		objectType = ObjectType.NBTList;
		object = nbtTagList;
	}
	
	public ItemStack getItemStack()
	{
		return objectType == ObjectType.Item ? CraftItemStack.asCraftMirror(asNativeItemStack()) : null;
	}
	
	public Entity getEntity()
	{
		return (Entity) object;
	}
	
	public NBTTagCompound asCompoundTag() 
	{
		return objectType == ObjectType.NBTTag ? (NBTTagCompound) object : null;
	}
	
	// Helper methods
	protected net.minecraft.server.v1_10_R1.ItemStack asNativeItemStack()
	{
		return object instanceof net.minecraft.server.v1_10_R1.ItemStack ? (net.minecraft.server.v1_10_R1.ItemStack) object : null;
	}
	
	protected net.minecraft.server.v1_10_R1.Entity asNativeEntity()
	{
		return object instanceof net.minecraft.server.v1_10_R1.Entity ? (net.minecraft.server.v1_10_R1.Entity) object : null;
	}
	
	protected NBTTagCompound asNBTTagCompound()
	{
		NBTTagCompound result = null;
		if (objectType == ObjectType.NBTTag)
			result = (NBTTagCompound) object;
		if (objectType == ObjectType.Item)
			result = asNativeItemStack().getTag();
//		if (objectType == ObjectType.Entity) todo fix this in 1.9
//			result = asNativeEntity().getNBTTag();
		return result;
	}
	
	public NBTTagList asNBTTagList()
	{
		NBTTagList result = null;
		if (objectType == ObjectType.NBTList)
			result = (NBTTagList) object;
		return result;
	}
	
	// NBTTagCompound getters
	public boolean isTagCompound()
	{
		return objectType == ObjectType.NBTTag;
	}
	
	public boolean hasKey(String subKey)
	{
		return asNBTTagCompound().hasKey(subKey);
	}
	public boolean hasKeyOfType(String subKey, NBTTagType type)
	{
		return asNBTTagCompound().hasKeyOfType(subKey, type.ID);
	}
	
	public String getString(String subKey)
	{
		return asNBTTagCompound().getString(subKey);
	}
	
	public boolean getBoolean(String subKey)
	{
		return asNBTTagCompound().getBoolean(subKey);
	}
	
	public double getDouble(String subKey)
	{
		return asNBTTagCompound().getDouble(subKey);
	}
	
	public float getFloat(String subKey)
	{
		return asNBTTagCompound().getFloat(subKey);
	}
	
	public long getLong(String subKey)
	{
		return asNBTTagCompound().getLong(subKey);
	}
	
	public int getInt(String subKey)
	{
		return asNBTTagCompound().getInt(subKey);
	}

	public short getShort(String subKey)
	{
		return asNBTTagCompound().getShort(subKey);
	}

	public byte getByte(String subKey)
	{
		return asNBTTagCompound().getByte(subKey);
	}

	public byte[] getByteArray(String subKey)
	{
		return asNBTTagCompound().getByteArray(subKey);
	}
	
	public int[] getIntArray(String subKey)
	{
		return asNBTTagCompound().getIntArray(subKey);
	}
	
	// NBTTagCompound setters
	public void setTag(String subKey, NBTBase compound)
	{
		asNBTTagCompound().set(subKey, compound);
	}
	
	public void setString(String key, String value)
	{
		asNBTTagCompound().setString(key, value);
	}
	
	public void setByte(String key, byte value) {
		asNBTTagCompound().setByte(key, value);
	}

	public void setInt(String key, int value) {
		asNBTTagCompound().setInt(key, value);
	}

	public void setBoolean(String key, boolean value) {
		asNBTTagCompound().setBoolean(key, value);
	}
	
	// Reader creation
	public NBTReader getTagReader(String subKey)
	{
		if (asNBTTagCompound().hasKeyOfType(subKey, NBTTagType.COMPOUND.ID))
			return new NBTReader(asNBTTagCompound().getCompound(subKey));
		else return null;
	}
	
	public NBTReader getListReader(String subKey, NBTTagType type)
	{
		if (asNBTTagCompound().hasKeyOfType(subKey, NBTTagType.LIST.ID))
			return new NBTReader(asNBTTagCompound().getList(subKey, type.ID));
		else return null;
	}
	
	// NBTTagList methods
	public boolean isTagList()
	{
		return objectType == ObjectType.NBTList;
	}
	
	public int getListSize() 
	{
		return objectType == ObjectType.NBTList ? asNBTTagList().size() : -1;
	}
	
	public String getStringAt(int index)
	{
		return asNBTTagList().getString(index);
	}
	
	public void addString(String value)
	{
		asNBTTagList().add(new NBTTagString(value));
	}
	
	public void addTag(NBTBase base)
	{
		asNBTTagList().add(base);
	}
	
	public NBTReader getReaderAt(int index)
	{
		if (objectType == ObjectType.NBTList)
			return new NBTReader(asNBTTagList().get(index));
		return null;
	}

	// Fast static helper methods
	public static NBTTagCompound buildTagTree(NBTTagCompound tag, String key)
	{
		String tkey = key;
		while(tkey.contains("."))
		{
			int end = tkey.indexOf(".");
			String sub = tkey.substring(0, end);
			
			if (!tag.hasKeyOfType(sub, NBTTagType.COMPOUND.ID))
				tag.set(sub, new NBTTagCompound());
			
			tag = tag.getCompound(sub);
			tkey = tkey.substring(end + 1);
		}

		if (!tag.hasKeyOfType(tkey, NBTTagType.COMPOUND.ID))
			tag.set(tkey, new NBTTagCompound());
		return tag.getCompound(tkey);
	}
	
	// Fast static helper methods
	public static NBTTagCompound getTagCompound(NBTTagCompound tag, String key)
	{
		String tkey = key;
		NBTTagCompound result = tag;
		while(tkey.contains(".") && result != null)
		{
			int end = tkey.indexOf(".");
			String sub = tkey.substring(0, end);
			
			result = result.hasKeyOfType(sub, NBTTagType.COMPOUND.ID) ? result.getCompound(sub) : null;
			tkey = tkey.substring(end + 1);
		}

		return result != null && result.hasKeyOfType(tkey, NBTTagType.COMPOUND.ID) ? result.getCompound(tkey) : null;
	}
	
	public static ItemStack setString(ItemStack item, String key, String value)
	{
		net.minecraft.server.v1_10_R1.ItemStack nmsItem = CraftItemStack.asNMSCopy(item);
		
		NBTTagCompound tag = nmsItem.getTag();
		if (tag == null)
			tag = new NBTTagCompound();
		
		// Build the key base
		String keyBase = null;
		String keyName = key;
		if (keyName.contains("."))
		{
			keyBase = key.substring(0, keyName.lastIndexOf("."));
			keyName = key.substring(keyName.lastIndexOf(".") + 1);
		}
		
		NBTTagCompound endTag = tag;
		if (keyBase != null)
			endTag = buildTagTree(tag, keyBase);
		
		// Set the value		
		endTag.setString(keyName, value);
		nmsItem.setTag(tag);
		
		return CraftItemStack.asCraftMirror(nmsItem);
	}
	
	public static String getString(ItemStack item, String key)
	{
		net.minecraft.server.v1_10_R1.ItemStack nmsItem = CraftItemStack.asNMSCopy(item);

		NBTTagCompound tag = nmsItem.getTag();
		if (tag == null) return null;

		String keyBase = null;
		String keyName = key;
		if (keyName.contains("."))
		{
			keyBase = key.substring(0, keyName.lastIndexOf("."));
			keyName = key.substring(keyName.lastIndexOf(".") + 1);
		}
		
		if (keyBase != null)
			tag = getTagCompound(tag, keyBase);
		
		return tag != null ? tag.getString(keyName) : null;
	}
}
