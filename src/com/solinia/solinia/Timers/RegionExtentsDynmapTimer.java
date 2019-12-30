package com.solinia.solinia.Timers;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.scheduler.BukkitRunnable;
import org.dynmap.markers.AreaMarker;
import org.dynmap.markers.Marker;

import com.solinia.solinia.Exceptions.CoreStateInitException;
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
		int numberOfChunks = size/16;
		int numberOfRegions = numberOfChunks/32;
		
		for(int x = 0; x < numberOfRegions;x++)
		{
			for(int y = 0; y < numberOfRegions;y++)
			{
				handleRegion(x,y, newmap, newmark);
			}
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

	private void handleRegion(int regionx, int regiony, Map<String, AreaMarker> newmap, Map<String, Marker> newmark) {
		String name = regionx+"_"+regiony;
        
        /* Build popup */
        String desc = "reigon info: " + name;
        int regionSize = 32*16;
        double[] xVals = {regionx*regionSize, (regionx*regionSize) + regionSize};
        double[] zVals = {regiony*regionSize, (regiony*regionSize) + regionSize};
        
        boolean displayLabel = true;
        boolean persistent = false;
        String markerId = "Region:"+name;
        AreaMarker marker = StateManager.getInstance().regionextentsresareas.remove(markerId);
        int fillColor = Color.YELLOW.asRGB();
        double fillOpacity = 0F;
        
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
