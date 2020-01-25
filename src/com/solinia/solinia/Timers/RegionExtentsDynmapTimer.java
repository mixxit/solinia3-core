package com.solinia.solinia.Timers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.scheduler.BukkitRunnable;
import org.dynmap.markers.AreaMarker;
import org.dynmap.markers.Marker;

import com.palmergames.bukkit.towny.object.Coord;
import com.solinia.solinia.Managers.StateManager;

public class RegionExtentsDynmapTimer extends BukkitRunnable {
	@Override
	public void run() {
		updateRegions();
	}
	
	private void updateRegions() {
		
		Map<String,AreaMarker> newmap = new HashMap<String,AreaMarker>(); /* Build new map */
        Map<String,Marker> newmark = new HashMap<String,Marker>(); /* Build new map */
        
        int size = (int)Math.floor(Bukkit.getWorld("world").getWorldBorder().getSize());
        Location center = Bukkit.getWorld("world").getWorldBorder().getCenter();
		int numberOfChunks = size/16;
		int numberOfRegions = numberOfChunks/32;
		if (numberOfRegions > 1000)
		{
			System.out.println("Too many regions to generate Dynmap Region Extents");
			return;
		}
		
		for(Coord coord : getRegionsInsideBorder(Bukkit.getWorld("world")))
		{
			handleRegion(coord, newmap, newmark);
		}
		
        /* Now, review old map - anything left is gone */
        for(AreaMarker oldm : StateManager.getInstance().regionextentsresareas.values()) {
        	if (oldm == null)
        		continue;
        	
            oldm.deleteMarker();
        }
        for(Marker oldm : StateManager.getInstance().regionextentsresmark.values()) {
        	if (oldm == null)
        		continue;

            oldm.deleteMarker();
        }
        /* And replace with new map */
        StateManager.getInstance().regionextentsresareas = newmap;
        StateManager.getInstance().regionextentsresmark = newmark;
	}
	
	private List<Coord> getChunksInsideBorder(org.bukkit.World world)
	{
		return new ArrayList<Coord>();
	}

	private List<Coord> getRegionsInsideBorder(org.bukkit.World world) {
		List<Coord> coords = new ArrayList<Coord>();
		
		double minX = world.getWorldBorder().getCenter().getX() - (world.getWorldBorder().getSize()/2);
		double minZ = world.getWorldBorder().getCenter().getZ() - (world.getWorldBorder().getSize()/2);
		
		Location location1 = new Location(world,minX,0D,minZ);
		
		double maxX = world.getWorldBorder().getCenter().getX() + (world.getWorldBorder().getSize()/2);
		double maxZ = world.getWorldBorder().getCenter().getZ() + (world.getWorldBorder().getSize()/2);
		
		Location location2 = new Location(world,maxX,0D,maxZ);
		
		int xMin; int xMax;
        if(location1.getChunk().getX() > location2.getChunk().getX()) {
            xMin = location2.getChunk().getX();
            xMax = location1.getChunk().getX();
        }else {
            xMin = location1.getChunk().getX();
            xMax = location2.getChunk().getX();
        }
     
        int zMin; int zMax;
        if(location1.getChunk().getZ() > location2.getChunk().getZ()) {
            zMin = location2.getChunk().getZ();
            zMax = location1.getChunk().getZ();
        }else {
            zMin = location1.getChunk().getZ();
            zMax = location2.getChunk().getZ();
        }       
        
        for (int x = xMin; x <= xMax; x++) {
            for (int z = zMin; z <= zMax; z++) {
            	int regionX = (int)Math.floor(x / 32.0);
            	int regionZ = (int)Math.floor(z / 32.0);
            	
            	if (coords.contains(new Coord(regionX,regionZ)))
            		continue;
            	
            	coords.add(new Coord(regionX,regionZ));
            }
        }
        
		return coords;
	}

	private void handleRegion(Coord coord, Map<String, AreaMarker> newmap, Map<String, Marker> newmark) {
		String name = coord.getX()+"_"+coord.getZ();
        
        /* Build popup */
        String desc = "region info: " + name;
        int regionSize = 32*16;
        double[] xVals = {coord.getX()*regionSize, (coord.getX()*regionSize) + regionSize};
        double[] zVals = {coord.getZ()*regionSize, (coord.getZ()*regionSize) + regionSize};
        
        boolean displayLabel = true;
        boolean persistent = false;
        String markerId = "Region:"+name;
        AreaMarker marker = StateManager.getInstance().regionextentsresareas.remove(markerId);
        int fillColor = Color.YELLOW.asRGB();
        double fillOpacity = 0.5F;
        
        if(marker == null) {
        	marker = StateManager.getInstance().getRegionExtentsMarkerSet().createAreaMarker(markerId, name, displayLabel, "world",xVals, zVals, persistent);
        	if(marker == null) {
        		System.out.println("error adding marker " + markerId);
        		return;
            } else {
            	marker.setCornerLocations(xVals, zVals); /* Replace corner locations */
            	marker.setLabel(name);   /* Update label */
            	marker.setFillStyle(fillOpacity, fillColor);
            	marker.setLineStyle(3, 1.0, fillColor);
            }
        } else {
        	marker.setCornerLocations(xVals, zVals); /* Replace corner locations */
        	marker.setLabel(name);   /* Update label */
        	marker.setFillStyle(fillOpacity, fillColor);
        	marker.setLineStyle(3, 1.0, fillColor);
        }
        newmap.put(markerId, marker);
	}
}
