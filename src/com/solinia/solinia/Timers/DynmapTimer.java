package com.solinia.solinia.Timers;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.Color;
import org.bukkit.scheduler.BukkitRunnable;
import org.dynmap.markers.AreaMarker;
import org.dynmap.markers.Marker;

import com.palmergames.bukkit.towny.object.Town;
import com.palmergames.bukkit.towny.object.TownBlock;
import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Managers.StateManager;
import com.solinia.solinia.Models.SoliniaZone;

public class DynmapTimer extends BukkitRunnable {
	@Override
	public void run() {
		updateZones();
	}
	
	private void updateZones() {
		
		Map<String,AreaMarker> newmap = new HashMap<String,AreaMarker>(); /* Build new map */
        Map<String,Marker> newmark = new HashMap<String,Marker>(); /* Build new map */
        
        /* Loop through zones */
        try
        {
        	for(SoliniaZone zone : StateManager.getInstance().getConfigurationManager().getZones())
        	{
        		handleZone(zone, newmap, newmark);
        	}
        	
        	if (StateManager.getInstance().renderTownsOnDynmap)
        	for (Entry<String, Town> town : StateManager.getInstance().getTowny().getTownyUniverse().getTownsMap().entrySet())
        	{
        		for(TownBlock townBlock : town.getValue().getTownBlocks())
        		{
        			handleTown(townBlock, newmap, newmark);
        		}
        	}
        	
        } catch (CoreStateInitException e)
        {
        	
        }
        
        
        /* Now, review old map - anything left is gone */
        for(AreaMarker oldm : StateManager.getInstance().resareas.values()) {
        	if (oldm == null)
        		continue;
        	
            oldm.deleteMarker();
        }
        for(Marker oldm : StateManager.getInstance().resmark.values()) {
        	if (oldm == null)
        		continue;

            oldm.deleteMarker();
        }
        /* And replace with new map */
        StateManager.getInstance().resareas = newmap;
        StateManager.getInstance().resmark = newmark;
	}

	private void handleTown(TownBlock townBlock, Map<String, AreaMarker> newmap, Map<String, Marker> newmark) {
		String name = townBlock.getWorldCoord().getX()+"_"+townBlock.getWorldCoord().getZ();
        
        /* Build popup */
        String desc = "info: " + name;
        
        int townBlockSize = 16;
        int cornerPosX = townBlock.getX()*townBlockSize;
        int cornerPosZ = townBlock.getZ()*townBlockSize;
        
        double[] xVals = {cornerPosX - townBlockSize, cornerPosX + townBlockSize};
        double[] zVals = {cornerPosZ - townBlockSize, cornerPosZ + townBlockSize};
        
        boolean displayLabel = true;
        boolean persistent = false;
        String markerId = "TownyArea:"+name;
        AreaMarker marker = StateManager.getInstance().resareas.remove(markerId);
        int fillColor = Color.YELLOW.asRGB();
        double fillOpacity = 0.1;
        
        if(marker == null) {
        	marker = StateManager.getInstance().getMarkerSet().createAreaMarker(markerId, name, displayLabel, "world",xVals, zVals, persistent);
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

	private void handleZone(SoliniaZone zone, Map<String, AreaMarker> newmap, Map<String, Marker> newmark) {
		String name = zone.getName();
                
        /* Build popup */
        String desc = "info: " + name;
        
        double[] xVals = {zone.getX() - zone.getSize(), zone.getX() + zone.getSize()};
        double[] zVals = {zone.getZ() - zone.getSize(), zone.getZ() + zone.getSize()};
        
        boolean displayLabel = true;
        boolean persistent = false;
        String markerId = "SoliniaZone:"+zone.getId();
        AreaMarker marker = StateManager.getInstance().resareas.remove(markerId);
        int fillColor = Color.AQUA.asRGB();
        double fillOpacity = 0.1;
        
        if(marker == null) {
        	marker = StateManager.getInstance().getMarkerSet().createAreaMarker(markerId, name, displayLabel, "world",xVals, zVals, persistent);
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
