package net.dandielo.core.items.serialize.flags;

import org.bukkit.inventory.ItemStack;

import net.dandielo.core.items.dItem;
import net.dandielo.core.items.serialize.Attribute;
import net.dandielo.core.items.serialize.ItemAttribute;
import net.dandielo.core.utils.NBTItemStack;

@Attribute(name = "HideFlags", key = "hidef", priority = 5)
public class HideFlags extends ItemAttribute {
	private int flags;
	
	public HideFlags(dItem item, String key) {
		super(item, key);
		flags = 0;
	}
	
	@Override
	public ItemStack onNativeAssign(ItemStack item, boolean unused)
	{
		NBTItemStack helper = new NBTItemStack(item);
		helper.setInt("HideFlags", flags);
		return helper.getItemStack();
	}
	
	@Override
	public boolean onRefactor(ItemStack item) 
	{
		NBTItemStack helper = new NBTItemStack(item);
		flags = helper.getInt("HideFlags");
		return flags != 0;
	}

	@Override
	public String serialize() {
		return Integer.toString(flags);
	}

	@Override
	public boolean deserialize(String data) {
		boolean result = false;
		try {
			flags = Integer.parseInt(data);
			result = true;
		} catch(Exception e) { }
		return result;
	}

}
