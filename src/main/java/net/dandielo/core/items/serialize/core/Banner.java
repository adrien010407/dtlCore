package net.dandielo.core.items.serialize.core;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Color;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.block.banner.Pattern;
import org.bukkit.block.banner.PatternType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BannerMeta;

import net.dandielo.core.items.dItem;
import net.dandielo.core.items.serialize.Attribute;
import net.dandielo.core.items.serialize.ItemAttribute;

@Attribute(key = "bn", name = "Banner", priority = 5, items = {Material.BANNER})
public class Banner extends ItemAttribute {
	private List<Pattern> patterns = new ArrayList<Pattern>();
	
	public Banner(dItem item, String key) {
		super(item, key);
	}

	// General serialization: 
	//
	// key(.sub):([unique_data]#)[array_data(, ...)]
	// 
	// Array data serialization:
	//
	// data@color
	//
	// Color serialization:
	//
	// red.green.blue
	//
	@Override
	public String serialize() 
	{
		String result = "unused";
		for (Pattern pattern : patterns)
		{
			Color rgb = pattern.getColor().getColor();
			String colorString = rgb.getRed() + "." + rgb.getGreen() + "." + rgb.getBlue();
			result += "," + pattern.getPattern().getIdentifier() + "@" + colorString;
		}
		return result;
	}

	@Override
	public boolean deserialize(String data)
	{
		String[] arrayData = data.split(",");
		for (String strPattern : arrayData)
		{
			if (!strPattern.equals("unused"))
			{
				String[] patternData = strPattern.split("@");
				String[] colorData = patternData[1].split("\\.");
				Color color = Color.fromRGB(
						  Integer.parseInt(colorData[0]) //red
						, Integer.parseInt(colorData[1]) //green
						, Integer.parseInt(colorData[2]) //blue
				);
				patterns.add(new Pattern(
						DyeColor.getByColor(color), 
						PatternType.getByIdentifier(patternData[0].toLowerCase())
					)
				);
			}
		}
		return true;
	}
	
	@Override
	public boolean onRefactor(ItemStack item) 
	{
		if ( !(item.getItemMeta() instanceof BannerMeta) ) return false;
		
		//check is a owner is set
		BannerMeta meta = (BannerMeta) item.getItemMeta();
		patterns.addAll(meta.getPatterns());
		return true;
	}
	
	@Override
	public void onAssign(ItemStack item, boolean unused) 
	{
		if (item.getItemMeta() instanceof BannerMeta)
		{
			BannerMeta meta = (BannerMeta) item.getItemMeta();
			for (Pattern pattern : patterns)
				meta.addPattern(pattern);
			item.setItemMeta(meta);
		}
	}
}
