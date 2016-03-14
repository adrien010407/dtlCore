package net.dandielo.core.items.serialize.core;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;

import net.dandielo.core.items.dItem;
import net.dandielo.core.items.serialize.Attribute;
import net.dandielo.core.items.serialize.ItemAttribute;

@Attribute(name="Potion", key="pt", priority = 5, items = {Material.POTION, Material.SPLASH_POTION, Material.LINGERING_POTION})
public class Potion extends ItemAttribute {
	private List<PotionEffect> effects = new ArrayList<PotionEffect>();
	private boolean extended;
	private boolean upgraded;
	private PotionType type;
	
	public Potion(dItem item, String key)
	{
		super(item, key);
	}

	@Override
	public boolean deserialize(String data)
	{
		String[] potionData = data.split("@");
		String[] baseData = potionData[0].split("\\.");
		
		// Get the potion base data
		type = PotionType.valueOf(baseData[0]);
		for (int i = 1; i < baseData.length; ++i)
		{
			if (baseData[i].equals("ext"))
			{
				extended = true;
			}
			else if (baseData[i].equals("upg"))
			{
				upgraded = true;
			}
		}
		
		// Get the potion effects
		if (potionData.length > 1 && potionData[1] != null && potionData[1].length() > 0)
		{
			String[] savedEffects = potionData[1].split(",");
			for ( String savedEffect : savedEffects )
			{
				String[] effectData = savedEffect.split("/");
				PotionEffect effect = new PotionEffect(
						PotionEffectType.getByName(effectData[0]),
						Integer.parseInt(effectData[1]),
						Integer.parseInt(effectData[2]),
						Boolean.parseBoolean(effectData[3]));
				effects.add(effect);
			}
		}
		return true;
	}

	@Override
	public String serialize()
	{
		String result = type.toString();
		if (extended)
			result += ".ext";
		if (upgraded)
			result += ".upg";
		result += "@";
		
		//save each potion effect with a comma separated
		for ( PotionEffect e : effects )
			result += e.getType().getName() + "/" + e.getDuration() + "/" + e.getAmplifier() + "/" + e.isAmbient();
		return result;
	}

	@Override
	public void onAssign(ItemStack item, boolean unused)
	{
		if ( !(item.getType().equals(Material.POTION) || item.getType().equals(Material.SPLASH_POTION) || 
				item.getType().equals(Material.LINGERING_POTION)) ) return;

		PotionMeta meta = (PotionMeta) item.getItemMeta();
		if (!effects.isEmpty())
		{
			for ( PotionEffect effect : effects )
			    meta.addCustomEffect(effect, false);
		}
		
		//set the main effect
		meta.setBasePotionData(new PotionData(type, extended, upgraded));
		item.setItemMeta(meta);
	}

	@Override
	public boolean onRefactor(ItemStack item)
	{
		if ( !(item.getType().equals(Material.POTION) || item.getType().equals(Material.SPLASH_POTION) || 
				item.getType().equals(Material.LINGERING_POTION)) ) 
			return false;
		
		PotionMeta meta = (PotionMeta) item.getItemMeta(); 
		effects = meta.getCustomEffects(); 
		
		PotionData data = meta.getBasePotionData();
		extended = data.isExtended();
		upgraded = data.isUpgraded();
		type = data.getType();

		return true;
	}

	@Override
	public boolean same(ItemAttribute attr)
	{
		if ( !type.equals(((Potion)attr).type) ) return false;
		if ( extended != ((Potion)attr).extended ) return false;
		if ( upgraded != ((Potion)attr).upgraded ) return false;
		if ( ((Potion)attr).effects.size() != effects.size() ) return false;
		
		
		boolean equals = true;
		for ( PotionEffect effect : ((Potion)attr).effects )
		    equals = equals ? effects.contains(effect) : equals;
			
		return equals;
	}
	
	@Override
	public boolean similar(ItemAttribute attr)
	{
		return same(attr);
	}
}
