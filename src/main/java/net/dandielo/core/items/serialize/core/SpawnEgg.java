package net.dandielo.core.items.serialize.core;

import java.util.HashMap;

import org.apache.commons.lang3.text.WordUtils;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;

import net.dandielo.core.items.dItem;
import net.dandielo.core.items.serialize.Attribute;
import net.dandielo.core.items.serialize.ItemAttribute;
import net.dandielo.core.utils.NBTItemStack;
import net.dandielo.core.utils.NBTReader;
import net.minecraft.server.v1_10_R1.NBTTagCompound;

@Attribute(name = "Spawn egg", key = "egg", priority = 5, items = { Material.MONSTER_EGG })
public class SpawnEgg extends ItemAttribute {
	private EntityType entityType;
	
	private static HashMap<String, EntityType> nameMappings;
	private static HashMap<EntityType, String> typeMappings;
	
	static {
		// These NBT value that contains the name of the EggType is not the same as the one in the enum
		// This forces me to create a map of names to types, and here it is!// 
		// 
		// Types that work: Creeper, Skeleton, Spider, Zombie, Slime, Ghast, Enderman, Silverfish, Blaze
		// Bat, Witch, Endermite, Guardian, Shulker, Pig, Sheep, Cow, Chicken, Squid, Villager, Rabbit
		nameMappings = new HashMap<String, EntityType>();
		nameMappings.put("ozelot", EntityType.OCELOT);
		nameMappings.put("entityhorse", EntityType.HORSE);
		nameMappings.put("pigzombie", EntityType.PIG_ZOMBIE);
		nameMappings.put("cavespider", EntityType.CAVE_SPIDER);
		nameMappings.put("lavaslime", EntityType.MAGMA_CUBE);
		nameMappings.put("mushroomcow", EntityType.MUSHROOM_COW);
		
		// And this goes in both ways...
		typeMappings = new HashMap<EntityType, String>();
		typeMappings.put(EntityType.OCELOT, "Ozelot");
		typeMappings.put(EntityType.HORSE, "EntityHorse");
		typeMappings.put(EntityType.PIG_ZOMBIE, "PigZombie");
		typeMappings.put(EntityType.CAVE_SPIDER, "CaveSpider");
		typeMappings.put(EntityType.MAGMA_CUBE, "LavaSlime");
		typeMappings.put(EntityType.MUSHROOM_COW, "MushroomCow");
	}

	public SpawnEgg(dItem item, String key) {
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
			{
				// Ok this one is STUPID, how could ppl get the name spelled wrong in a ENUMERATION?!
				String id = tag.getString("id").toLowerCase();
				if (nameMappings.containsKey(id))
					entityType = nameMappings.get(id);
				else
					entityType = EntityType.valueOf(id.toUpperCase());
			}
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
			String id = typeMappings.get(entityType);
			if (id == null)
				id = WordUtils.capitalize(entityType.name().toLowerCase());
			
			tag.setString("id", id);
			nItem.setTag("EntityTag", tag.asCompoundTag());
		}
		return nItem.getItemStack();
	}

	@Override
	public String serialize() {
		return entityType == null ? "unknown" : entityType.name().toLowerCase();
	}

	@Override
	public boolean deserialize(String data) {
		entityType = EntityType.valueOf(data.toUpperCase());
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
