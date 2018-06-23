package com.solinia.solinia.Timers;

import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.solinia.solinia.Adapters.SoliniaPlayerAdapter;
import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Interfaces.ISoliniaAAAbility;
import com.solinia.solinia.Interfaces.ISoliniaPlayer;
import com.solinia.solinia.Managers.StateManager;
import com.solinia.solinia.Models.SoliniaPlayerSkill;
import com.solinia.solinia.Models.SoliniaZone;
import com.solinia.solinia.Utils.Utils;

public class PlayerRegenTickTimer extends BukkitRunnable {

	@Override
	public void run() {

		for (Player player : Bukkit.getServer().getOnlinePlayers()) {
			grantPlayerRegenBonuses(player);
		}
	}

	private void grantPlayerRegenBonuses(Player player) {
		if (player.isDead())
			return;
		
		// Apply Crouch Mana Regen Bonus
		int manaregen = 1;
		
		ISoliniaPlayer solplayer = null;
		try {
			solplayer = SoliniaPlayerAdapter.Adapt(player);
		} catch (CoreStateInitException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		if (solplayer == null)
			return;
		
		int zonehpregen = 0;
		int zonempregen = 0;
		
		try
		{
			for (SoliniaZone zone : StateManager.getInstance().getConfigurationManager().getZones()) {
				if (player.getLocation().distance(new Location(player.getWorld(), zone.getX(), zone.getY(), zone.getZ())) < zone.getSize())
				{
					zonehpregen += zone.getHpRegen();
					zonempregen += zone.getManaRegen();
				}
			}
		} catch (CoreStateInitException e)
		{
			
		}
		
		SoliniaZone zone = solplayer.isInZone();
		if (zone != null)
		{
			zonehpregen = zone.getHpRegen();
			zonempregen = zone.getManaRegen();
		}
		
		manaregen += zonempregen;
		
		// a players mana regen based on if they are meditating (sneaking)
		manaregen += getPlayerMeditatingManaBonus(solplayer);
		
		ISoliniaAAAbility aa = null;
		try
		{
			if(solplayer.getAARanks().size() > 0)
			{
				for(ISoliniaAAAbility ability : StateManager.getInstance().getConfigurationManager().getAAbilitiesBySysname("MENTALCLARITY"))
				{
					if (!solplayer.hasAAAbility(ability.getId()))
						continue;
					
					aa = ability;
				}
			}
			
		} catch (CoreStateInitException e)
		{
			
		}
		
		int aamanaregenrank = 0;
		
		if (aa != null)
		{
			if(solplayer.getAARanks().size() > 0)
			aamanaregenrank = Utils.getRankPositionOfAAAbility(player, aa);
			manaregen += aamanaregenrank;
		}
		
		ISoliniaAAAbility emaa = null;
		try
		{
			if(solplayer.getAARanks().size() > 0)
			{
				for(ISoliniaAAAbility ability : StateManager.getInstance().getConfigurationManager().getAAbilitiesBySysname("MENTALCLARITY"))
				{
					if (!solplayer.hasAAAbility(ability.getId()))
						continue;
					
					emaa = ability;
				}
			}
		} catch (CoreStateInitException e)
		{
			
		}
		
		int emaamanaregenrank = 0;
		
		if (emaa != null)
		{
			if(solplayer.getAARanks().size() > 0)
			emaamanaregenrank = Utils.getRankPositionOfAAAbility(player, emaa);
			manaregen += emaamanaregenrank;
		}

		manaregen += solplayer.getItemMpRegenBonuses();
		
		// Hp and Mana Regen from Items
		int hpregen = 0;
		
		ISoliniaAAAbility hpaa = null;
		try
		{
			if(solplayer.getAARanks().size() > 0)
			{
				for(ISoliniaAAAbility ability : StateManager.getInstance().getConfigurationManager().getAAbilitiesBySysname("INNATEREGENERATION"))
				{
					if (!solplayer.hasAAAbility(ability.getId()))
						continue;
					
					hpaa = ability;
				}
			}
		} catch (CoreStateInitException e)
		{
			
		}
		
		int aahpregenrank = 0;
		
		if (hpaa != null)
		{
			aahpregenrank = Utils.getRankPositionOfAAAbility(player, hpaa);
			hpregen += aahpregenrank;
		}
		
		hpregen += zonehpregen;
		
		hpregen += solplayer.getItemHpRegenBonuses();

		// Process HP Regeneration
		if (hpregen > 0) {
			int amount = (int) Math.round(player.getHealth()) + hpregen;
			if (amount > player.getMaxHealth()) {
				amount = (int) Math.round(player.getMaxHealth());
			}
			
			if (amount < 0)
				amount = 0;

			if (!player.isDead())
			player.setHealth(amount);
		}
		
		// Process Mana Regeneration
		//System.out.println(player.getName() + " was found to have " + manaregen + " mana regen");
		solplayer.increasePlayerMana(manaregen);
	}

	private int getPlayerMeditatingManaBonus(ISoliniaPlayer solplayer) {
		int manaregen = 0;
		if (solplayer.isMeditating())
		{
			SoliniaPlayerSkill meditationskill = solplayer.getSkill("MEDITATION");
			int bonusmana = 3 + (meditationskill.getValue() / 15);

			manaregen += bonusmana;

			// apply meditation skill increase
			Random r = new Random();
			int randomInt = r.nextInt(100) + 1;
			if (randomInt > 90) {
				int currentvalue = 0;
				SoliniaPlayerSkill skill = solplayer.getSkill("MEDITATION");
				if (skill != null) {
					currentvalue = skill.getValue();
				}

				if ((currentvalue + 1) <= solplayer.getSkillCap("MEDITATION")) {
					solplayer.setSkill("MEDITATION", currentvalue + 1);
				}

			}
		}
		
		// update last location
		solplayer.setLastLocation(solplayer.getBukkitPlayer().getLocation());
		
		return manaregen;
	}
}
