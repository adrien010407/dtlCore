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
import net.dandielo.core.items.serialize.core.LeatherColor;
import net.dandielo.core.items.serialize.core.Map;
import net.dandielo.core.items.serialize.core.Name;
import net.dandielo.core.items.serialize.core.Potion;
import net.dandielo.core.items.serialize.core.Skull;
import net.dandielo.core.items.serialize.core.StoredEnchant;
import net.dandielo.core.items.serialize.flags.Lore;
import net.dandielo.core.items.serialize.flags.SplashPotion;

import org.bukkit.ChatColor;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import static net.dandielo.core.items.serialize.ItemAttribute.registerAttr;
import static net.dandielo.core.items.serialize.ItemFlag.registerFlag;

public class dtlCore extends JavaPlugin {
	//console prefix
		public static final String PREFIX = "[dtlTraders]" + ChatColor.WHITE; 
		
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
			
			//init plugin settings
			saveDefaultConfig();
			
			info("Loading config files");
			
			info("Registering attributes and flags...");
			registerAttr(Durability.class);
			registerAttr(Amount.class);
			registerAttr(Book.class);
			registerAttr(Banner.class);
			registerAttr(Enchants.class);
			registerAttr(Firework.class);
			registerAttr(GenericDamage.class);
			registerAttr(GenericHealth.class);
			registerAttr(GenericKnockback.class);
			registerAttr(GenericSpeed.class);
			registerAttr(LeatherColor.class);
			registerAttr(Map.class);
			registerAttr(Name.class);
			registerAttr(Potion.class);
			registerAttr(Skull.class);
			registerAttr(StoredEnchant.class);
			
			registerFlag(Lore.class);
			registerFlag(SplashPotion.class);
			
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
