package net.dandielo.core.utils;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.inventory.ItemStack;

import net.minecraft.server.v1_8_R3.NBTTagCompound;
import net.minecraft.server.v1_8_R3.NBTTagList;


public class NBTItemStack extends NBTReader {
	private NBTReader displayCompound;
	
	public NBTItemStack(ItemStack item) {
		super(item);
		
		// Get the item "display" compound
		if (!this.hasKey("display"))
			this.setTag("display", new NBTTagCompound());
		displayCompound = this.getTagReader("display");
	}
	
	public List<String> getLore()
	{
		ArrayList<String> result = new ArrayList<String>();
		if (displayCompound.hasKeyOfType("Lore", NBTTagType.LIST))
		{
			NBTReader lore = displayCompound.getListReader("Lore", NBTTagType.STRING);
			for (int i = 0; i < lore.getListSize(); ++i)
				result.add(lore.getStringAt(i));
		}
		return result;
	}
	
	public void setLore(List<String> list)
	{
		if (!displayCompound.hasKeyOfType("Lore", NBTTagType.LIST))
			displayCompound.setTag("Lore", new NBTTagList());

		NBTReader lore = displayCompound.getListReader("Lore", NBTTagType.STRING);
		for (String line : list)
			lore.addString(line);
	}
	
	// Item specific NBT methods
	public String getName()
	{
		return displayCompound.getString("Name");
	}
	
	public void setName(String name)
	{
		displayCompound.setString("Name", name);
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
}
