package com.solinia.solinia.Utils;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
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
import com.solinia.solinia.Managers.StateManager;
import com.solinia.solinia.Models.CastingSpell;
import net.md_5.bungee.api.ChatColor;

public class ScoreboardUtils {

	public static void UpdateScoreboard(Player player, int mana) {

		if (player != null) {
			BossBar bossbar = StateManager.getInstance().getBossBar(player.getUniqueId());
			if (bossbar == null) {
				bossbar = Bukkit.createBossBar(player.getUniqueId().toString(), BarColor.PINK, BarStyle.SOLID);
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

				String target = "";
				LivingEntity entityTarget = StateManager.getInstance().getEntityManager().getEntityTarget(player);
				if (entityTarget != null) {
					ISoliniaLivingEntity solLivingEntity = SoliniaLivingEntityAdapter.Adapt(entityTarget);
					if (solLivingEntity != null) {
						ISoliniaLivingEntity playerLivingEntity = SoliniaLivingEntityAdapter.Adapt(player);
						if (playerLivingEntity != null) {
							target = solLivingEntity.getLevelCon(playerLivingEntity) + entityTarget.getCustomName()
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
		health.setDisplayName(ChatColor.RED + Utils.HeartSymbol);
		health.setDisplaySlot(DisplaySlot.BELOW_NAME);
		try {
			ISoliniaPlayer solplayer = SoliniaPlayerAdapter.Adapt(Bukkit.getPlayer(uuid));
			Score playerscore = objective.getScore(ChatColor.WHITE + solplayer.getFullName() + "");
			playerscore.setScore((int) solplayer.getSoliniaLivingEntity().getHPRatio());
			
			LivingEntity playerpet = StateManager.getInstance().getEntityManager().getPet(uuid);
			if (playerpet != null)
			{
				ISoliniaLivingEntity solPet = SoliniaLivingEntityAdapter.Adapt(playerpet);
				Score score = objective.getScore(ChatColor.GRAY + playerpet.getCustomName() + "");
				score.setScore((int) solPet.getHPRatio());
			}
			
			if (group != null)
				for (UUID groupmemberuuid : group.getMembers()) {
					if (groupmemberuuid.toString().equals(uuid.toString()))
						continue;

					solplayer = SoliniaPlayerAdapter.Adapt(Bukkit.getPlayer(groupmemberuuid));
					if (groupmemberuuid.equals(group.getOwner())) {
						Score score = objective.getScore(ChatColor.GOLD + solplayer.getFullName() + "");
						score.setScore((int) solplayer.getSoliniaLivingEntity().getHPRatio());
					} else {
						Score score = objective.getScore(ChatColor.WHITE + solplayer.getFullName() + "");
						score.setScore((int) solplayer.getSoliniaLivingEntity().getHPRatio());
					}
					
					LivingEntity pet = StateManager.getInstance().getEntityManager().getPet(groupmemberuuid);
					if (pet != null)
					{
						ISoliniaLivingEntity solPet = SoliniaLivingEntityAdapter.Adapt(pet);
						Score score = objective.getScore(ChatColor.GRAY + pet.getCustomName() + "");
						score.setScore((int) solPet.getHPRatio());
					}

				}
		} catch (CoreStateInitException e) {
			e.printStackTrace();
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
		health.setDisplayName(ChatColor.RED + Utils.HeartSymbol);
		health.setDisplaySlot(DisplaySlot.BELOW_NAME);

		try {
			ISoliniaPlayer solplayer = SoliniaPlayerAdapter.Adapt(Bukkit.getPlayer(uuid));
			Score score = objective.getScore(ChatColor.GOLD + solplayer.getFullName() + "");
			score.setScore((int) solplayer.getSoliniaLivingEntity().getHPRatio());
		} catch (CoreStateInitException e) {
			e.printStackTrace();
		}
	}

	public static void UpdateGroupScoreboardForEveryone(UUID uniqueId, ISoliniaGroup group) {
		ScoreboardUtils.UpdateGroupScoreboard(uniqueId, group);
		if (group != null) {
			for (UUID uuid : group.getMembers()) {
				ScoreboardUtils.UpdateGroupScoreboard(uuid, group);
			}
		}

	}
}
