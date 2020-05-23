package com.solinia.solinia.Utils;

import org.bukkit.Location;
import org.bukkit.util.Vector;

public class LocationUtils {
	public static Location getLocationAroundCircle(Location center, double radius, double angleInRadian, double y) {
		double x = center.getX() + radius * Math.cos(angleInRadian);
		double z = center.getZ() + radius * Math.sin(angleInRadian);

		Location loc = new Location(center.getWorld(), x, y, z);
		Vector difference = center.toVector().clone().subtract(loc.toVector());
		loc.setDirection(difference);

		return loc;
	}
}
