package net.dandielo.core.items.serialize.flags;

import org.bukkit.inventory.ItemStack;

import net.dandielo.core.items.dItem;
import net.dandielo.core.items.serialize.Attribute;
import net.dandielo.core.items.serialize.ItemFlag;
import net.dandielo.core.utils.NBTItemStack;

@Attribute(name = "Unbreakable", key = ".unbreakable")
public class UnbreakableFlag extends ItemFlag {

	public UnbreakableFlag(dItem item, String key) {
		super(item, key);
	}
	
	@Override
	public ItemStack onNativeAssign(ItemStack item, boolean unused)
	{
		NBTItemStack helper = new NBTItemStack(item);
		helper.setUnbreakable(true);
		return helper.getItemStack();
	}
	
	@Override
	public boolean onRefactor(ItemStack item) 
	{
		NBTItemStack helper = new NBTItemStack(item);
		if (helper.isUnbreakable())
			return true;
		return false;
	}

}
