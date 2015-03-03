package net.dandielo.core.player;

import org.bukkit.entity.Player;

public class Experience {
	// With help from Denizen authors
	// https://github.com/DenizenScript/Denizen-For-Bukkit/blob/master/src/main/java/net/aufdemrand/denizen/scripts/commands/player/ExperienceCommand.java
	public static int getTotalExperience(Player p) {
		return getTotalExperience(p.getLevel(), p.getExp());
	}
	
	public static int getTotalExperience(int level, double bar) {
		return getTotalExpToLevel(level) + (int) (getExpToLevel(level + 1) * bar + 0.5);
	}

	public static int getExpToLevel(int level) {
		if (level < 16) {
			return 17;
		}
		else if (level < 31) {
			return 3 * level - 31;
		}
		else {
			return 7 * level - 155;
		}
	}
	
	public static int getTotalExpToLevel(int level) {
		if (level < 16) {
			return 17 * level;
		}
		else if (level < 31) {
			return (int) (1.5 * level * level - 29.5 * level + 360 );
		}
		else {
			return (int) (3.5 * level * level - 151.5 * level + 2220);
		}
	}

	public static void resetExperience(Player player) {
		player.setTotalExperience(0);
		player.setLevel(0);
		player.setExp(0);
	}

	/* Tail recursive way to count the level for the given exp, maybe better with iteration */
	public static int countLevel(int exp, int toLevel, int level) {
		if (exp < toLevel) {
			return level;
		}
		else {
			return countLevel(exp - toLevel, getTotalExpToLevel(level + 2) - getTotalExpToLevel(level + 1), ++level);
		}
	}

	/* Adding experience using the setExp and setLevel methods, should be soundless (not tested) */
	public static void giveSilentExperience(Player player, int exp) {
		final int currentExp = getTotalExperience(player);
		resetExperience(player);
		final int newexp = currentExp + exp;
		
		if (newexp > 0) {
			final int level = countLevel(newexp, 17, 0);
			player.setLevel(level);
			final int epxToLvl = newexp - getTotalExpToLevel(level);
			player.setExp(epxToLvl < 0 ? 0.0f : (float)epxToLvl / (float)getExpToLevel(level + 1));
		}
	}
}
