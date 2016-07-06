package net.dandielo.core.utils;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.inventory.ItemStack;

import net.minecraft.server.v1_10_R1.NBTTagCompound;
import net.minecraft.server.v1_10_R1.NBTTagList;


public class NBTItemStack extends NBTReader {
	private NBTReader displayCompound;
	
	public NBTItemStack(ItemStack item) {
		super(item);
	}
	
	private NBTReader getDisplayTag() 
	{
		if (!this.hasKey("display") || displayCompound == null)
		{
			this.setTag("display", new NBTTagCompound());
			displayCompound = this.getTagReader("display");
		}
		return displayCompound;
	}
	
	public List<String> getLore()
	{
		ArrayList<String> result = new ArrayList<String>();
		if (getDisplayTag().hasKeyOfType("Lore", NBTTagType.LIST))
		{
			NBTReader lore = getDisplayTag().getListReader("Lore", NBTTagType.STRING);
			for (int i = 0; i < lore.getListSize(); ++i)
				result.add(lore.getStringAt(i));
		}
		return result;
	}
	
	public void setLore(List<String> list)
	{
		if (!getDisplayTag().hasKeyOfType("Lore", NBTTagType.LIST))
			getDisplayTag().setTag("Lore", new NBTTagList());

		NBTReader lore = getDisplayTag().getListReader("Lore", NBTTagType.STRING);
		for (String line : list)
			lore.addString(line);
	}
	
	// Item specific NBT methods
	public String getName()
	{
		return getDisplayTag().getString("Name");
	}
	
	public void setName(String name)
	{
		getDisplayTag().setString("Name", name);
	}
	
	public byte getCount() 
	{
		return this.getByte("Count");
	}
	
	public short getDamage()
	{
		return this.getShort("Damage");
	}
	
	public String getID()
	{
		return this.getString("id");
	}

	public boolean isUnbreakable() {
		return this.hasKey("Unbreakable") ? this.getBoolean("Unbreakable") : false;
	}
	
	public void setUnbreakable(boolean value) {
		this.setBoolean("Unbreakable", value);
	}
}
