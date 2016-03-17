package net.dandielo.core.bukkit;


import net.dandielo.core.items.serialize.core.Amount;
import net.dandielo.core.items.serialize.core.Banner;
import net.dandielo.core.items.serialize.core.Book;
import net.dandielo.core.items.serialize.core.Durability;
import net.dandielo.core.items.serialize.core.Enchants;
import net.dandielo.core.items.serialize.core.Firework;
import net.dandielo.core.items.serialize.core.GenericDamage;
import net.dandielo.core.items.serialize.core.GenericHealth;
import net.dandielo.core.items.serialize.core.GenericKnockback;
import net.dandielo.core.items.serialize.core.GenericSpeed;
import net.dandielo.core.items.serialize.core.HideFlags;
import net.dandielo.core.items.serialize.core.LeatherColor;
import net.dandielo.core.items.serialize.core.Map;
import net.dandielo.core.items.serialize.core.Name;
import net.dandielo.core.items.serialize.core.Potion;
import net.dandielo.core.items.serialize.core.Shield;
import net.dandielo.core.items.serialize.core.Skull;
import net.dandielo.core.items.serialize.core.StoredEnchant;
import net.dandielo.core.items.serialize.flags.Lore;
import net.dandielo.core.items.serialize.flags.SplashPotion;
import net.dandielo.core.items.serialize.flags.UnbreakableFlag;

import org.bukkit.ChatColor;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import static net.dandielo.core.items.serialize.ItemAttribute.registerAttr;
import static net.dandielo.core.items.serialize.ItemAttribute.extendAttrKey;
import static net.dandielo.core.items.serialize.ItemFlag.registerFlag;

public class dtlCore extends JavaPlugin {
	//console prefix
		public static final String PREFIX = "[dtlCore]" + ChatColor.WHITE; 
		
		//bukkit resources
		private static ConsoleCommandSender console;
		private static dtlCore instance;
		
		@Override
		public void onLoad()
		{
		}
		
		@Override
		public void onEnable()
		{
			//set the plugin instance
			instance = this;
			
			//set the console sender
			console = getServer().getConsoleSender();
			
			info("Loading config files");
			
			info("Registering attributes and flags...");
			// Default attributes
			registerAttr(StoredEnchant.class);
			registerAttr(LeatherColor.class);
			registerAttr(Durability.class);
			registerAttr(HideFlags.class);
			registerAttr(Enchants.class);
			registerAttr(Firework.class);
			registerAttr(Amount.class);
			registerAttr(Banner.class);
			registerAttr(Shield.class);
			registerAttr(Potion.class);
			registerAttr(Skull.class);
			registerAttr(Book.class);
			registerAttr(Name.class);
			registerAttr(Map.class);
			
			// Attribute extensions
			extendAttrKey("g", GenericKnockback.class);
			extendAttrKey("g", GenericDamage.class);
			extendAttrKey("g", GenericHealth.class);
			extendAttrKey("g", GenericSpeed.class);

			registerFlag(UnbreakableFlag.class);
			registerFlag(SplashPotion.class);
			registerFlag(Lore.class);
			
			info("Enabled");
		}
		
		@Override
		public void onDisable()
		{
		}
		
		//static methods
		public static dtlCore getInstance() 
		{
			return instance;
		}

		//static logger warning
		public static void info(String message)
		{
			console.sendMessage(PREFIX + "[INFO] " + message);
		}
		
		//static logger warning
		public static void warning(String message)
		{
			console.sendMessage(PREFIX + ChatColor.GOLD + "[WARNING] " + ChatColor.RESET + message);
		}
		
		//static logger severe
		public static void severe(String message)
		{
			console.sendMessage(PREFIX + ChatColor.RED + "[SEVERE] " + ChatColor.RESET + message);
		}
}
