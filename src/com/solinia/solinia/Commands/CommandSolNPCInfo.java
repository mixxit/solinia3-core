package com.solinia.solinia.Commands;

import java.util.Set;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_14_R1.entity.CraftEntity;
import org.bukkit.entity.Creature;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.metadata.MetadataValue;

import com.solinia.solinia.Adapters.SoliniaLivingEntityAdapter;
import com.solinia.solinia.Adapters.SoliniaPlayerAdapter;
import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Interfaces.ISoliniaItem;
import com.solinia.solinia.Interfaces.ISoliniaLivingEntity;
import com.solinia.solinia.Interfaces.ISoliniaPlayer;
import com.solinia.solinia.Managers.StateManager;
import com.solinia.solinia.Models.ActiveSpellEffect;
import com.solinia.solinia.Models.SoliniaActiveSpell;

import net.minecraft.server.v1_14_R1.EntityCreature;
import net.minecraft.server.v1_14_R1.EntityInsentient;
import net.minecraft.server.v1_14_R1.PathfinderGoalSelector;

public class CommandSolNPCInfo implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender instanceof Player))
		{
			sender.sendMessage("This is a player only command");
			return true;
		}
		
		sender.sendMessage("Fetching information about NPC targetted");
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
			solLivingEntity.sendStats(player);

			player.sendMessage("GUID: " + targetmob.getUniqueId());
			for (MetadataValue val : targetmob.getMetadata("mobname")) {
				player.sendMessage("mobname tag: " + val.asString());
			}

			for (MetadataValue val : targetmob.getMetadata("npcid")) {
				player.sendMessage("npcid tag: " + val.asString());
			}
			
			player.sendMessage("IsNPC: " + solLivingEntity.isNPC());
			
			if (solLivingEntity.isNPC())
			{
				player.sendMessage("NPCID: " + solLivingEntity.getNpcid());
			}
			player.sendMessage("EQUIPMENT");
			for(ISoliniaItem solItem : solLivingEntity.getEquippedSoliniaItems())
			{
				System.out.println("SolItemId: " + solItem.getId() + " " + solItem.getDisplayname());
				player.sendMessage("SolItemId: " + solItem.getId() + " " + solItem.getDisplayname());
			}
			
			player.sendMessage("METADATA");
			if (solLivingEntity.getBukkitLivingEntity().hasMetadata("mobname"))
			{
				String metadata = "";
				for (MetadataValue val : solLivingEntity.getBukkitLivingEntity().getMetadata("mobname")) {
					metadata = val.asString();
				}
				
				player.sendMessage("mobname: " + metadata);
			}
			
			if (solLivingEntity.getSpawnPoint() == null)
			{
				player.sendMessage("spawnpoint: null");
			} else {
				player.sendMessage("spawnpoint: " + solLivingEntity.getSpawnPoint().getWorld().getName() + "," + solLivingEntity.getSpawnPoint().getX() + "," + solLivingEntity.getSpawnPoint().getY() + "," + solLivingEntity.getSpawnPoint().getZ());
			}
			
			if (solLivingEntity.getBukkitLivingEntity().hasMetadata("mythicmob"))
			{
				String metadata = "";
				for (MetadataValue val : solLivingEntity.getBukkitLivingEntity().getMetadata("mythicmob")) {
					metadata = val.asString();
				}
				
				player.sendMessage("mythicmob: " + metadata);
			}
			
			if (solLivingEntity.getBukkitLivingEntity().hasMetadata("Faction"))
			{
				String metadata = "";
				for (MetadataValue val : solLivingEntity.getBukkitLivingEntity().getMetadata("Faction")) {
					metadata = val.asString();
				}
				
				player.sendMessage("Faction: " + metadata);
			}
			
			player.sendMessage("LISTEFFECTS");
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
			player.sendMessage("HATELIST");
			solLivingEntity.sendHateList(player);
			player.sendMessage("MINECRAFT AGGRO");
			player.sendMessage("Minecraft attack target: " + solLivingEntity.getAttackTarget());
			
		} catch (CoreStateInitException e)
		{
			player.sendMessage("SoliniaNPCInfo: " + "Could not fetch information");
		} 

		return true;
	}

}
