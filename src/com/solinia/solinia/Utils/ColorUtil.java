package com.solinia.solinia.Utils;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import org.bukkit.ChatColor;

//https://gist.github.com/Vexentric/1a2a565377a0970951172a39283dad53
public class ColorUtil {

	private static Map<ChatColor, ColorSet<Integer, Integer, Integer>> colorMap = new HashMap<ChatColor, ColorSet<Integer, Integer, Integer>>();

	static {
		colorMap.put(ChatColor.BLACK, new ColorSet<Integer, Integer, Integer>(0, 0, 0));
		colorMap.put(ChatColor.DARK_BLUE, new ColorSet<Integer, Integer, Integer>(0, 0, 170));
		colorMap.put(ChatColor.DARK_GREEN, new ColorSet<Integer, Integer, Integer>(0, 170, 0));
		colorMap.put(ChatColor.DARK_AQUA, new ColorSet<Integer, Integer, Integer>(0, 170, 170));
		colorMap.put(ChatColor.DARK_RED, new ColorSet<Integer, Integer, Integer>(170, 0, 0));
		colorMap.put(ChatColor.DARK_PURPLE, new ColorSet<Integer, Integer, Integer>(170, 0, 170));
		colorMap.put(ChatColor.GOLD, new ColorSet<Integer, Integer, Integer>(255, 170, 0));
		colorMap.put(ChatColor.GRAY, new ColorSet<Integer, Integer, Integer>(170, 170, 170));
		colorMap.put(ChatColor.DARK_GRAY, new ColorSet<Integer, Integer, Integer>(85, 85, 85));
		colorMap.put(ChatColor.BLUE, new ColorSet<Integer, Integer, Integer>(85, 85, 255));
		colorMap.put(ChatColor.GREEN, new ColorSet<Integer, Integer, Integer>(85, 255, 85));
		colorMap.put(ChatColor.AQUA, new ColorSet<Integer, Integer, Integer>(85, 255, 255));
		colorMap.put(ChatColor.RED, new ColorSet<Integer, Integer, Integer>(255, 85, 85));
		colorMap.put(ChatColor.LIGHT_PURPLE, new ColorSet<Integer, Integer, Integer>(255, 85, 255));
		colorMap.put(ChatColor.YELLOW, new ColorSet<Integer, Integer, Integer>(255, 255, 85));
		colorMap.put(ChatColor.WHITE, new ColorSet<Integer, Integer, Integer>(255, 255, 255));
	}

	private static class ColorSet<R, G, B> {
		R red = null;
		G green = null;
		B blue = null;

		ColorSet(R red, G green, B blue) {
			this.red = red;
			this.green = green;
			this.blue = blue;
		}

		public R getRed() {
			return red;
		}

		public G getGreen() {
			return green;
		}

		public B getBlue() {
			return blue;
		}

	}

	public static ChatColor fromRGB(int r, int g, int b) {
		TreeMap<Integer, ChatColor> closest = new TreeMap<Integer, ChatColor>();
		colorMap.forEach((color, set) -> {
			int red = Math.abs(r - set.getRed());
			int green = Math.abs(g - set.getGreen());
			int blue = Math.abs(b - set.getBlue());
			closest.put(red + green + blue, color);
		});
		return closest.firstEntry().getValue();
	}

}