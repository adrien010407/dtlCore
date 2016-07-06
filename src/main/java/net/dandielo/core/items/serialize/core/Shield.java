package net.dandielo.core.items.serialize.core;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Color;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.block.banner.Pattern;
import org.bukkit.block.banner.PatternType;
import org.bukkit.inventory.ItemStack;
import net.dandielo.core.items.dItem;
import net.dandielo.core.items.serialize.Attribute;
import net.dandielo.core.items.serialize.ItemAttribute;
import net.dandielo.core.utils.NBTItemStack;
import net.dandielo.core.utils.NBTReader;
import net.dandielo.core.utils.NBTReader.NBTTagType;
import net.minecraft.server.v1_10_R1.NBTTagCompound;
import net.minecraft.server.v1_10_R1.NBTTagList;

@Attribute(name = "Shield", key = "sh", priority = 5, items = {Material.SHIELD})
public class Shield extends ItemAttribute {
	private int base;
	private List<Pattern> patterns;
	
	public Shield(dItem item, String key) 
	{
		super(item, key);
		patterns = new ArrayList<Pattern>();
	}

	@Override 
	public boolean onRefactor(ItemStack item)
	{
		NBTItemStack nItem = new NBTItemStack(item);
		if (nItem.hasKey("BlockEntityTag"))
		{
			NBTReader beTag = nItem.getTagReader("BlockEntityTag");
			base = beTag.getInt("Base");
	
			NBTReader patterns = beTag.getListReader("Patterns", NBTTagType.COMPOUND);
			for (int i = 0; i < patterns.getListSize(); ++i)
			{
				NBTReader pattern = patterns.getReaderAt(i);
				this.patterns.add(
						new Pattern(
								DyeColor.getByColor(Color.fromRGB(pattern.getInt("Color"))), 
								PatternType.getByIdentifier(pattern.getString("Pattern"))
						)
				);
			}
			return true;
		}
		return false;
	}
	
	@Override 
	public ItemStack onNativeAssign(ItemStack item, boolean abstrac)
	{
		NBTItemStack nItem = new NBTItemStack(item);
		if (base != 0 || !patterns.isEmpty())
		{
			nItem.setTag("BlockEntityTag", new NBTTagCompound());
			NBTReader beTag = nItem.getTagReader("BlockEntityTag");
			
			beTag.setInt("Base", base);
			beTag.setTag("Patterns", new NBTTagList());
			NBTReader patterns = beTag.getListReader("Patterns", NBTTagType.COMPOUND);
			
			for (Pattern pat : this.patterns)
			{
				NBTReader pattern = new NBTReader(new NBTTagCompound());
				pattern.setString("Pattern", pat.getPattern().getIdentifier());
				pattern.setInt("Color", pat.getColor() == null ? 0 : pat.getColor().getColor().asRGB());
				
				// Add to the compound tag list
				patterns.addTag(pattern.asCompoundTag());
			}
		}
		return nItem.getItemStack();
	} 
	
	@Override
	public String serialize() {
		String result = Integer.toString(base);
		for (Pattern pattern : patterns)
		{
			Color rgb = pattern.getColor() == null ? Color.fromRGB(0) : pattern.getColor().getColor();
			String colorString = rgb.getRed() + "." + rgb.getGreen() + "." + rgb.getBlue();
			result += "," + pattern.getPattern().getIdentifier() + "@" + colorString;
		}
		return result;
	}

	@Override
	public boolean deserialize(String data) {
		String[] arrayData = data.split(",");
		for (String strPattern : arrayData)
		{
			if (strPattern.contains("."))
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
			else
			{
				base = Integer.valueOf(strPattern);
			}
		}
		return true;
	}

}
