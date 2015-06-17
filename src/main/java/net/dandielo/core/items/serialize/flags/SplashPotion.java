package net.dandielo.core.items.serialize.flags;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.Potion;

import net.dandielo.core.items.dItem;
import net.dandielo.core.items.serialize.Attribute;
import net.dandielo.core.items.serialize.ItemFlag;

@Attribute(name="SplashPotion", key = ".splash", items = {Material.POTION}, priority = 5)
public class SplashPotion extends ItemFlag {

	public SplashPotion(dItem item, String key)
	{
		super(item, key);
	}

	@Override
	public void onAssign(ItemStack item, boolean unused)
	{
		if (!item.getType().equals(Material.POTION)) return;
		
		//set the potion as splash potion
		//bit representation 
		//
		// 0 X X 0 | 0 0 0 0 | 0 0 0 0 | 0 0 0 0
		//
		// first (from left) X bit should be 1 and the second one should be 0
		// we are achieving this masking the durability in this way
		//
		// 0 0 0 1 | 1 1 1 1 | 1 1 1 1 | 1 1 1 1
		//
		// and setting the second bit (from left) to 1 with a OR function 
		//
		// 0 1 0 0 | 0 0 0 0 | 0 0 0 0 | 0 0 0 0
		//
		//splash potion ready :)
		item.setDurability((short) ((item.getDurability()&0x1fff)|0x4000));
	}
	
	@Override
	public boolean onRefactor(ItemStack item) 
	{
		if (!item.getType().equals(Material.POTION))
			return false;
		
		//get the potion and check if it's a splash potion
		try
		{
		    Potion potion = Potion.fromItemStack(item);
		    if ( !potion.isSplash() )
				return false;
		}
		catch ( Exception e )
		{
			return false;
		}
		return true;
	}
}
