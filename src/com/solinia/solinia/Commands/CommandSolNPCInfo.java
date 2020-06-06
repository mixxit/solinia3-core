package com.solinia.solinia.Commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Creature;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Tameable;
import org.bukkit.metadata.MetadataValue;

import com.solinia.solinia.Adapters.SoliniaLivingEntityAdapter;
import com.solinia.solinia.Adapters.SoliniaPlayerAdapter;
import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Interfaces.ISoliniaItem;
import com.solinia.solinia.Interfaces.ISoliniaLivingEntity;
import com.solinia.solinia.Interfaces.ISoliniaNPC;
import com.solinia.solinia.Interfaces.ISoliniaPlayer;
import com.solinia.solinia.Managers.StateManager;
import com.solinia.solinia.Models.ActiveSpellEffect;
import com.solinia.solinia.Models.SoliniaActiveSpell;
import com.solinia.solinia.Utils.EntityUtils;
import com.solinia.solinia.Utils.NPCUtils;

public class CommandSolNPCInfo implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender instanceof Player))
		{
			sender.sendMessage("This is a player only command");
			return true;
		}
		
		//sender.sendMessage("Fetching information about NPC targetted");
		Player player = (Player)sender;
		
		try
		{
			ISoliniaPlayer solPlayer = SoliniaPlayerAdapter.Adapt(player);
			LivingEntity targetmob = solPlayer.getEntityTarget();
			if (targetmob == null)
			{
				player.sendMessage("You need to target an NPC for info about it");
				return true;
			}

			ISoliniaLivingEntity solLivingEntity = SoliniaLivingEntityAdapter.Adapt(targetmob);

			String mobtag = "";
			String npcidtag = "";
			
			player.sendMessage("EQUIPMENT");
			for(ISoliniaItem solItem : solLivingEntity.getEquippedSoliniaItems())
			{
				player.sendMessage("SolItemId: " + solItem.getId() + " " + solItem.getDisplayname());
			}
			
			if (solLivingEntity.getBukkitLivingEntity().hasMetadata("Faction"))
			{
				String metadata = "";
				for (MetadataValue val : solLivingEntity.getBukkitLivingEntity().getMetadata("Faction")) {
					metadata = val.asString();
				}
				
				player.sendMessage("Faction: " + metadata);
			}
			
			player.sendMessage("Active Effects");
			try {
				for (SoliniaActiveSpell spell : StateManager.getInstance().getEntityManager()
						.getActiveEntitySpells(solLivingEntity.getBukkitLivingEntity()).getActiveSpells()) {
					player.sendMessage(spell.getSpell().getName());
					for (ActiveSpellEffect effect : spell.getActiveSpellEffects()) {
						player.sendMessage(
								" - " + effect.getSpellEffectType().name() + " " + effect.getRemainingValue());
					}
				}
			} catch (CoreStateInitException e) {
				//
			}
			solLivingEntity.sendHateList(player);
			
			solLivingEntity.sendStats(player);
			
			for (MetadataValue val : targetmob.getMetadata("mobname"))
				mobtag = "mobname tag: " + ChatColor.GOLD + val.asString() + ChatColor.RESET;

			for (MetadataValue val : targetmob.getMetadata("npcid"))
				npcidtag = "npcid tag: " + ChatColor.GOLD + val.asString() + ChatColor.RESET;
			
			//player.sendMessage("GUID: " + targetmob.getUniqueId() + " IsNPC: " + solLivingEntity.isNPC() + " npcid: " + npcid);
			player.sendMessage(mobtag + " " + npcidtag);

			
			if (solLivingEntity.getBukkitLivingEntity() instanceof Creature)
			{
				String parent = "Owner: " + ChatColor.GOLD + solLivingEntity.getActiveMob().getOwner() + ChatColor.RESET;
				String owner = "Parent: " + ChatColor.GOLD + solLivingEntity.getActiveMob().getParent() + ChatColor.RESET;
				String tame = "";
				if (solLivingEntity.getBukkitLivingEntity() instanceof Tameable)
					tame = "TameOwner: " +  ChatColor.GOLD + ((Tameable)(solLivingEntity.getBukkitLivingEntity())).getOwner() +  ChatColor.RESET;
				
				player.sendMessage(parent + " " + owner + " " + tame);
			}

			String canSee = "";
				if(player.isOp())
					canSee = "Can i see you: "+ ChatColor.GOLD + solLivingEntity.checkLosFN(SoliniaLivingEntityAdapter.Adapt(player));
			String attackTarget = "Minecraft attack target: " + ChatColor.GOLD + (solLivingEntity.getAttackTarget() == null ? "None" : solLivingEntity.getAttackTarget().getName()) + ChatColor.RESET + " " + canSee;
			if (solLivingEntity.getSpawnPoint() == null)
			{
				player.sendMessage("spawnpoint: "+ChatColor.GOLD+"null");
			} else {
				player.sendMessage("spawnpoint: " + ChatColor.GOLD+ solLivingEntity.getSpawnPoint().getWorld().getName() + "," + solLivingEntity.getSpawnPoint().getX() + "," + solLivingEntity.getSpawnPoint().getY() + "," + solLivingEntity.getSpawnPoint().getZ());
			}

			if(player.isOp() && solLivingEntity.isNPC())
			{
				ISoliniaNPC npc = solLivingEntity.getNPC();
				double hp = EntityUtils.getStatMaxHP(npc.getClassObj(), npc.getLevel(), 75);
				if (solLivingEntity.isNPC())
					hp = hp / 4;
				
				if (npc.isHeroic()) {
					hp += (NPCUtils.getHeroicHPMultiplier() * npc.getLevel());
				}

				if (npc.isBoss()) {
					hp += (NPCUtils.getBossHPMultiplier(npc.isHeroic()) * npc.getLevel());
				}

				if (npc.isRaidheroic()) {
					hp += (NPCUtils.getRaidHeroicHPMultiplier() * npc.getLevel());
				}

				if (npc.isRaidboss()) {
					hp += (NPCUtils.getRaidBossHPMultiplier() * npc.getLevel());
				}
				
				if (npc.getForcedMaxHp() > 0)
					hp = npc.getForcedMaxHp();
				
				double damage = npc.getMaxDamage();
				
				player.sendMessage("Debug calc hp: " + hp + " " + "Debug calc dmg: " + npc.getMaxDamage());
			}
			
			player.sendMessage(attackTarget);
			
		} catch (CoreStateInitException e)
		{
			player.sendMessage("SoliniaNPCInfo: " + "Could not fetch information");
		} 

		return true;
	}

}
