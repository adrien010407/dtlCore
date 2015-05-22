package net.dandielo.core.items.serialize.core;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import net.dandielo.core.items.dItem;
import net.dandielo.core.items.serialize.Attribute;
import net.dandielo.core.items.serialize.ItemAttribute;

@Attribute(name="Potion", key="pt", priority = 5, items = {Material.POTION})
public class Potion extends ItemAttribute {
	private List<PotionEffect> effects = new ArrayList<PotionEffect>();
	
	public Potion(dItem item, String key)
	{
		super(item, key);
	}

	@Override
	public boolean deserialize(String data)
	{
		String[] savedEffects = data.split(",");
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
		return false;
	}

	@Override
	public String serialize()
	{
		String result = "";
		
		//save each potion effect with a comma separated
		for ( PotionEffect e : effects )
			result += "," + e.getType().getName() + "/" + e.getDuration() + "/" + e.getAmplifier() + "/" + e.isAmbient();
		
		return result.substring(1);
	}

	@Override
	public void onAssign(ItemStack item, boolean unused)
	{
		if ( !item.getType().equals(Material.POTION) ) return;

		PotionMeta meta = (PotionMeta) item.getItemMeta();
		
		for ( PotionEffect effect : effects )
		    meta.addCustomEffect(effect, false);
		
		//set the main effect
		meta.setMainEffect(effects.get(0).getType());
		
		item.setItemMeta(meta);
	}

	@Override
	public boolean onRefactor(ItemStack item)
	{
		if ( !item.hasItemMeta() || !item.getType().equals(Material.POTION) ) 
			return false;
		
		PotionMeta meta = (PotionMeta) item.getItemMeta(); 
		
		effects = meta.getCustomEffects(); 
		return true;
	}

	@Override
	public boolean equals(ItemAttribute attr)
	{
		if ( ((Potion)attr).effects.size() != effects.size() ) return false;
		
		boolean equals = true;
		for ( PotionEffect effect : ((Potion)attr).effects )
		    equals = equals ? effects.contains(effect) : equals;
			
		return equals;
	}
	
	@Override
	public boolean similar(ItemAttribute attr)
	{
		return equals(attr);
	}
}
