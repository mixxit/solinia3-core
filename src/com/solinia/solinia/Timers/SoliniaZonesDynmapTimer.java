package com.solinia.solinia.Timers;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.Color;
import org.bukkit.scheduler.BukkitRunnable;
import org.dynmap.markers.AreaMarker;
import org.dynmap.markers.Marker;
import org.dynmap.markers.MarkerIcon;

import com.palmergames.bukkit.towny.object.Town;
import com.palmergames.bukkit.towny.object.TownBlock;
import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Interfaces.ISoliniaSpawnGroup;
import com.solinia.solinia.Managers.StateManager;
import com.solinia.solinia.Models.SoliniaZone;

import net.minecraft.server.v1_14_R1.Tuple;

public class SoliniaZonesDynmapTimer extends BukkitRunnable {
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
        	
        	if (StateManager.getInstance().renderTownsOnDynmap != null && !StateManager.getInstance().renderTownsOnDynmap.equals(""))
            	for (Entry<String, Town> town : StateManager.getInstance().getTowny().getTownyUniverse().getTownsMap().entrySet())
            	{
            		if (!town.getKey().toUpperCase().equals(StateManager.getInstance().renderTownsOnDynmap.toUpperCase()))
            			continue;
            		
            		RenderTown(town,newmap);
            	}
        	
        } catch (CoreStateInitException e)
        {
        	
        }
        
        
        /* Now, review old map - anything left is gone */
        for(AreaMarker oldm : StateManager.getInstance().soliniazonesresareas.values()) {
        	if (oldm == null)
        		continue;
        	
            oldm.deleteMarker();
        }
        for(Marker oldm : StateManager.getInstance().soliniazonesresmark.values()) {
        	if (oldm == null)
        		continue;

            oldm.deleteMarker();
        }
        /* And replace with new map */
        StateManager.getInstance().soliniazonesresareas = newmap;
        StateManager.getInstance().soliniazonesresmark = newmark;
	}

	private void RenderTown(Entry<String, Town> town, Map<String, AreaMarker> newmap) {
		HashMap<Point,Boolean> rows = new HashMap<Point,Boolean>();
		for(TownBlock townBlock : town.getValue().getTownBlocks())
		{
			rows.put(new Point(townBlock.getX(),townBlock.getZ()), true);
		}
		HashMap<String,List<Point>> strips = GetStripsX(rows);
		for(String stripName : strips.keySet())
		{
			
			String name = stripName;

	        int townBlockSize = 16;
	        
	        int minX = strips.get(name).stream().min(Comparator.comparing(v -> v.x)).get().x;
			int minZ = strips.get(name).stream().min(Comparator.comparing(v -> v.y)).get().y;
			int maxX = strips.get(name).stream().max(Comparator.comparing(v -> v.x)).get().x;
			int maxZ = strips.get(name).stream().max(Comparator.comparing(v -> v.y)).get().y;
	        
	        double[] xVals = {minX*townBlockSize, (maxX*townBlockSize) + townBlockSize};
	        double[] zVals = {minZ*townBlockSize, (maxZ*townBlockSize) + townBlockSize};
	        
			handleAreaMarker(name, xVals,zVals, newmap);
		}
	}

	public static HashMap<String, List<Point>> GetStripsX(HashMap<Point, Boolean> rows) {
		HashMap<String, List<Point>> strips = new HashMap<String,List<Point>>();
		
		int minX = rows.keySet().stream().min(Comparator.comparing(v -> v.x)).get().x;
		int minZ = rows.keySet().stream().min(Comparator.comparing(v -> v.y)).get().y;
		int maxX = rows.keySet().stream().max(Comparator.comparing(v -> v.x)).get().x;
		int maxZ = rows.keySet().stream().max(Comparator.comparing(v -> v.y)).get().y;

//		System.out.println("minX: " + minX);
//		System.out.println("minZ: " + minZ);
//		System.out.println("maxX: " + maxX);
//		System.out.println("maxZ: " + maxZ);
		Tuple<String,List<Point>> newStrip = null;
		
		for (int x = minX; x <= maxX; x++)
		{
			for (int z = minZ; z <= maxZ; z++)
    		{
				//System.out.println("x: " + x + " z: " + z);
				if (!rows.containsKey(new Point(x,z)))
				{
					// Skip it and save last Strip
					if (newStrip != null)
					{
						strips.put(newStrip.a(), newStrip.b());
						newStrip = null;
					}
					continue;
				}
				
				if (newStrip == null)
				{
					newStrip = new Tuple<String,List<Point>>(x+"_"+z,new ArrayList<Point>());
				}
				
				newStrip.b().add(new Point(x,z));
    		}
			
			// at end, we can write it if we havent already
			if (newStrip != null)
			{
				strips.put(newStrip.a(), newStrip.b());
				newStrip = null;
			}
		}
		
		return strips;
	}

	private void handleAreaMarker(String name, double[] xVals, double[] zVals, Map<String, AreaMarker> newmap) {
        
        /* Build popup */
        String desc = "info: " + name;
        
        boolean displayLabel = true;
        boolean persistent = false;
        String markerId = "TownyArea:"+name;
        AreaMarker marker = StateManager.getInstance().soliniazonesresareas.remove(markerId);
        int fillColor = Color.YELLOW.asRGB();
        double fillOpacity = 0.1;
        
        if(marker == null) {
        	marker = StateManager.getInstance().getSoliniaZonesMarkerSet().createAreaMarker(markerId, name, displayLabel, "world",xVals, zVals, persistent);
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
        
        double[] xVals = {zone.getBottomLeftCornerX(), zone.getTopRightCornerX()};
        double[] zVals = {zone.getBottomLeftCornerZ(), zone.getTopRightCornerZ()};
        
        boolean displayLabel = true;
        boolean persistent = false;
        String markerId = "SoliniaZone:"+zone.getId();
        AreaMarker marker = StateManager.getInstance().soliniazonesresareas.remove(markerId);
        int fillColor = Color.AQUA.asRGB();
        double fillOpacity = 0.1;
        
        if(marker == null) {
        	marker = StateManager.getInstance().getSoliniaZonesMarkerSet().createAreaMarker(markerId, name, displayLabel, "world",xVals, zVals, persistent);
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
        
        if (StateManager.getInstance().showSpawns == true)
        for(ISoliniaSpawnGroup spawnGroup : zone.getSpawnGroups())
        {
        	if (spawnGroup.isDisabled())
        		continue;
        	
        	String sgMarkerId = "SPAWNGROUP_"+spawnGroup.getId();
        	Marker sgmarker = StateManager.getInstance().soliniazonesresmark.remove(sgMarkerId);
        	MarkerIcon icon = StateManager.getInstance().getDynmap().getMarkerAPI().getMarkerIcon("skull");
        	sgmarker = StateManager.getInstance().getSoliniaZonesMarkerSet().createMarker(sgMarkerId, spawnGroup.getName(), spawnGroup.getWorld(), spawnGroup.getX(), spawnGroup.getY(), spawnGroup.getZ(), icon,true);
        	
        	newmark.put("SPAWNGROUP_"+spawnGroup.getId(), sgmarker);
        }
        
        newmap.put(markerId, marker);
	}
}
