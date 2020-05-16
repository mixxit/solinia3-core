package com.solinia.solinia.Utils;

import java.util.Iterator;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.NamespacedKey;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.boss.KeyedBossBar;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import com.solinia.solinia.Adapters.SoliniaLivingEntityAdapter;
import com.solinia.solinia.Interfaces.ISoliniaLivingEntity;
import com.solinia.solinia.Managers.StateManager;
import com.solinia.solinia.Models.CastingSpell;

public class ScoreboardUtils {
	public static void UpdateScoreboard(Player player, int mana) {
		if (player != null) {
			try {
				
				if (StateManager.getInstance().getPlayerManager().hasValidMod(player))
				{
					tryCleanUpBossBar(player);
					return;
				}
					
				BossBar bossbar = StateManager.getInstance().getBossBar(player.getUniqueId());
				if (bossbar == null) {
					bossbar = Bukkit.createBossBar(player.getUniqueId().toString(), BarColor.PINK, BarStyle.SOLID);
					bossbar.addPlayer(player);
					StateManager.getInstance().setBossBar(player.getUniqueId(), bossbar);
				}

				
				boolean found = false;
				for (Player bossbarplayer : bossbar.getPlayers()) {
					if (bossbarplayer == player) {
						found = true;
					}
				}

				if (found == false)
					bossbar.addPlayer(player);

				String target = "";
				LivingEntity entityTarget = StateManager.getInstance().getEntityManager().forceGetEntityTarget(player);

				if (entityTarget != null) {
					ISoliniaLivingEntity solLivingEntity = SoliniaLivingEntityAdapter.Adapt(entityTarget);
					if (solLivingEntity != null) {
						ISoliniaLivingEntity playerLivingEntity = SoliniaLivingEntityAdapter.Adapt(player);
						if (playerLivingEntity != null) {
							target = solLivingEntity.getLevelCon(playerLivingEntity.getMentorLevel()) + entityTarget.getCustomName()
									+ ChatColor.RESET;
						} else {
							target = entityTarget.getCustomName();
						}
					} else {
						target = entityTarget.getCustomName();
					}
				}
				bossbar.setTitle("MANA: " + ChatColor.BLUE + " " + mana + ChatColor.RESET + " TARGET: " + target);

				CastingSpell casting = StateManager.getInstance().getEntityManager().getCasting(player);

				double progress = 0d;
				if (casting != null && casting.timeLeftMilliseconds > 0 && casting.getSpell() != null) {
					double progressmilliseconds = ((double) casting.getSpell().getCastTime()
							- casting.timeLeftMilliseconds);
					progress = (double) ((double) progressmilliseconds / (double) casting.getSpell().getCastTime());

					if (progress < 0d)
						progress = 0d;

					if (progress > 1d)
						progress = 1d;
				}
				bossbar.setProgress(progress);

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private static void tryCleanUpBossBar(Player player) {
		BossBar bossbar = StateManager.getInstance().getBossBar(player.getUniqueId());
		if (bossbar != null) {
			NamespacedKey foundKey = null;
			Iterator<KeyedBossBar> bars = Bukkit.getBossBars();
			while(Bukkit.getBossBars().hasNext()) {
		         KeyedBossBar element = bars.next();
		         if (element.getTitle().equals(player.getUniqueId().toString()))
		        	 foundKey = element.getKey();
		      }
			bossbar.removePlayer(player);
			if (foundKey != null)
			{
				StateManager.getInstance().removeBossBar(player.getUniqueId());
				Bukkit.removeBossBar(foundKey);
			}
		}
	}
}
