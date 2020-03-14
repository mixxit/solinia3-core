package com.solinia.solinia.Listeners;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import com.solinia.solinia.Solinia3CorePlugin;
import com.solinia.solinia.Adapters.SoliniaPlayerAdapter;
import com.solinia.solinia.Events.PlayerTrackEvent;
import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Interfaces.ISoliniaPlayer;
import com.solinia.solinia.Managers.StateManager;

public class PlayerTrackListener implements Listener {

	public PlayerTrackListener(Solinia3CorePlugin solinia3CorePlugin) {
		// TODO Auto-generated constructor stub
	}
	

	@EventHandler
	public void onPlayerTrackEvent(PlayerTrackEvent event) {
		org.bukkit.entity.Entity playerEntity = Bukkit.getEntity(event.getPlayerUuid());
		if (playerEntity == null)
			return;
		if (!(playerEntity instanceof Player))
			return;
		Player player = (Player)playerEntity;
		
		Location target = event.getTrackingLocation();
		Location source = player.getLocation();
		org.bukkit.util.Vector inBetween = target.clone().subtract(source).toVector();
		org.bukkit.util.Vector lookVec = source.getDirection();


		double angleDir = (Math.atan2(inBetween.getZ(),inBetween.getX()) / 2 / Math.PI * 360 + 360) % 360;
		// This is probably related to yaw somehow but whatever
		double angleLook = (Math.atan2(lookVec.getZ(),lookVec.getX()) / 2 / Math.PI * 360 + 360) % 360;

		double angle = (angleDir - angleLook + 360) % 360;
		
		
		System.out.println(angle);

		if (angle > 315)
			playerEntity.sendMessage("Your tracking target appears to be straight ahead");
		else if (angle > 225 && angle <= 315)
			playerEntity.sendMessage("Your tracking target appears to be to the left");
		else if (angle > 135 && angle <= 225)
			playerEntity.sendMessage("Your tracking target appears to be behind you");
		else if (angle > 45 && angle <= 135)
			playerEntity.sendMessage("Your tracking target appears to be to the right");
		else if (angle >= 0 && angle <= 45)
			playerEntity.sendMessage("Your tracking target appears to be straight ahead");
		
		
		
		try {
			ISoliniaPlayer solplayer = SoliniaPlayerAdapter.Adapt((Player) player);
			solplayer.tryIncreaseSkill("TRACKING", 1);
			
			if (player.getLocation().distance(event.getTrackingLocation()) < 4)
				StateManager.getInstance().getEntityManager().stopTracking(player.getUniqueId());

			} catch (CoreStateInitException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
	}


	// All numbers derived from the client
	double calculateHeadingToTarget(Location location, Location targetLocation)
	{
		return 0;
	}

}
