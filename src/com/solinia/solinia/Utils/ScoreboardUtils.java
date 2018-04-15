package com.solinia.solinia.Utils;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;

import com.solinia.solinia.Adapters.SoliniaLivingEntityAdapter;
import com.solinia.solinia.Adapters.SoliniaPlayerAdapter;
import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Interfaces.ISoliniaGroup;
import com.solinia.solinia.Interfaces.ISoliniaLivingEntity;
import com.solinia.solinia.Interfaces.ISoliniaPlayer;
import com.solinia.solinia.Interfaces.ISoliniaSpell;
import com.solinia.solinia.Managers.StateManager;
import com.solinia.solinia.Models.SoliniaActiveSpell;
import com.solinia.solinia.Models.SoliniaEntitySpells;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

public class ScoreboardUtils {
	public static void UpdateScoreboard(Player player, int maxmp, int mana) {

		if (player != null) {
			BossBar bossbar = StateManager.getInstance().getBossBar(player.getUniqueId());
			if (bossbar == null) {
				bossbar = Bukkit.createBossBar(player.getUniqueId().toString(), BarColor.BLUE, BarStyle.SOLID);
				bossbar.addPlayer(player);
				StateManager.getInstance().setBossBar(player.getUniqueId(), bossbar);
			}

			try {
				boolean found = false;
				for (Player bossbarplayer : bossbar.getPlayers()) {
					if (bossbarplayer == player) {
						found = true;
					}
				}

				if (found == false)
					bossbar.addPlayer(player);

				double maxmana = maxmp;
				
				String target = "";
				LivingEntity entityTarget = StateManager.getInstance().getEntityManager().getEntityTarget(player);
				if (entityTarget != null)
				{
					target = entityTarget.getCustomName();
				}
				
				bossbar.setTitle("MANA: " + mana + " TARGET: " + target);
				double progress = (double) ((double) mana / (double) maxmana);
				if (progress < 0d)
					progress = 0d;
				
				if (progress > 1d)
					progress = 1d;
				bossbar.setProgress(progress);

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public static void UpdateGroupScoreboard(UUID uuid, ISoliniaGroup group) {
		if (StateManager.getInstance().getScoreboard(Bukkit.getPlayer(uuid)).getObjective("playercard") != null)
			StateManager.getInstance().getScoreboard(Bukkit.getPlayer(uuid)).getObjective("playercard").unregister();
		Objective objective = StateManager.getInstance().getScoreboard(Bukkit.getPlayer(uuid))
				.registerNewObjective("playercard", "dummy");
		objective.setDisplaySlot(DisplaySlot.SIDEBAR);
		objective.setDisplayName("Party");
		if (StateManager.getInstance().getScoreboard(Bukkit.getPlayer(uuid)).getObjective("health") != null)
			StateManager.getInstance().getScoreboard(Bukkit.getPlayer(uuid)).getObjective("health").unregister();
		Objective health = StateManager.getInstance().getScoreboard(Bukkit.getPlayer(uuid))
				.registerNewObjective("health", "health");
		health.setDisplayName(ChatColor.RED + "❤");
		health.setDisplaySlot(DisplaySlot.BELOW_NAME);

		if (group != null)
		for (UUID groupmemberuuid : group.getMembers()) {
			try {
				ISoliniaPlayer solplayer = SoliniaPlayerAdapter.Adapt(Bukkit.getPlayer(groupmemberuuid));
				if (groupmemberuuid.equals(group.getOwner())) {
					Score score = objective.getScore(ChatColor.GOLD + solplayer.getFullName() + "");
					score.setScore(solplayer.getLevel());
				} else {
					Score score = objective.getScore(ChatColor.WHITE + solplayer.getFullName() + "");
					score.setScore(solplayer.getLevel());
				}
			} catch (CoreStateInitException e) {
				e.printStackTrace();
			}
		}
	}

	public static void RemoveScoreboard(UUID uuid) {
		if (StateManager.getInstance().getScoreboard(Bukkit.getPlayer(uuid)).getObjective("playercard") != null)
			StateManager.getInstance().getScoreboard(Bukkit.getPlayer(uuid)).getObjective("playercard").unregister();
		Bukkit.getPlayer(uuid).setScoreboard(StateManager.getInstance().getScoreboard(Bukkit.getPlayer(uuid)));
		Objective objective = StateManager.getInstance().getScoreboard(Bukkit.getPlayer(uuid))
				.registerNewObjective("playercard", "dummy");
		objective.setDisplaySlot(DisplaySlot.SIDEBAR);
		objective.setDisplayName("Party");
		if (StateManager.getInstance().getScoreboard(Bukkit.getPlayer(uuid)).getObjective("showhealth") != null)
			StateManager.getInstance().getScoreboard(Bukkit.getPlayer(uuid)).getObjective("showhealth").unregister();
		Objective health = StateManager.getInstance().getScoreboard(Bukkit.getPlayer(uuid))
				.registerNewObjective("showhealth", "health");
		health.setDisplayName(ChatColor.RED + "❤");
		health.setDisplaySlot(DisplaySlot.BELOW_NAME);

		try {
			ISoliniaPlayer solplayer = SoliniaPlayerAdapter.Adapt(Bukkit.getPlayer(uuid));
			Score score = objective.getScore(ChatColor.GOLD + solplayer.getFullName() + "");
			score.setScore(solplayer.getLevel());
		} catch (CoreStateInitException e) {
			e.printStackTrace();
		}
	}
}
