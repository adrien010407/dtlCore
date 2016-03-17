package net.dandielo.core.items.serialize.core;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import com.avaje.ebeaninternal.server.deploy.BeanDescriptor.EntityType;

import net.dandielo.core.items.dItem;
import net.dandielo.core.items.serialize.Attribute;
import net.dandielo.core.items.serialize.ItemAttribute;
import net.dandielo.core.utils.NBTItemStack;
import net.dandielo.core.utils.NBTReader;
import net.minecraft.server.v1_9_R1.NBTTagCompound;

@Attribute(name = "Spawn egg", key = "egg", priority = 5, items = { Material.MONSTER_EGG })
public class SpawnEgg extends ItemAttribute {
	private EntityType entityType;

	protected SpawnEgg(dItem item, String key) {
		super(item, key);
		entityType = null;
	}

	@Override 
	public boolean onRefactor(ItemStack item)
	{
		NBTItemStack nItem = new NBTItemStack(item);
		if (nItem.hasKey("EntityTag"))
		{
			NBTReader tag = nItem.getTagReader("EntityTag");
			if (tag.hasKey("id"))
				entityType = EntityType.valueOf(tag.getString("id"));
		}
		return entityType != null;
	}
	
	@Override 
	public ItemStack onNativeAssign(ItemStack item, boolean abstrac)
	{
		NBTItemStack nItem = new NBTItemStack(item);
		if (entityType != null)
		{
			NBTReader tag = new NBTReader(new NBTTagCompound());
			tag.setString("id", entityType.name());
			nItem.setTag("EntityTag", tag.asCompoundTag());
		}
		return nItem.getItemStack();
	}

	@Override
	public String serialize() {
		return entityType == null ? "unknown" : entityType.name();
	}

	@Override
	public boolean deserialize(String data) {
		entityType = EntityType.valueOf(data);
		return entityType != null;
	}
	
	@Override
	public boolean same(ItemAttribute attr)
	{
		return entityType.equals(((SpawnEgg)attr).entityType);
	}

	@Override 
	public boolean similar(ItemAttribute attr)
	{
		return same(attr);
	}
}
