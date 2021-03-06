package com.solinia.solinia.Commands;

import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import com.earth2me.essentials.Essentials;
import com.earth2me.essentials.User;
import com.earth2me.essentials.craftbukkit.InventoryWorkaround;
import com.earth2me.essentials.utils.MaterialUtil;
import com.solinia.solinia.Adapters.SoliniaLivingEntityAdapter;
import com.solinia.solinia.Adapters.SoliniaPlayerAdapter;
import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Interfaces.ISoliniaLivingEntity;
import com.solinia.solinia.Interfaces.ISoliniaNPC;
import com.solinia.solinia.Interfaces.ISoliniaPlayer;
import com.solinia.solinia.Managers.StateManager;
import com.solinia.solinia.Utils.ItemStackUtils;
import com.solinia.solinia.Utils.Utils;

import net.md_5.bungee.api.ChatColor;

public class CommandEvent implements CommandExecutor {
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender instanceof Player))
			return false;
		
		Player player = (Player) sender;
		
		if (!sender.isOp() && !sender.hasPermission("solinia.event"))
		{
			player.sendMessage("You do not have permission to access this command");
			return false;
		}
		
		// Args
		// NPCID
		// Setting
		// NewValue
		
		if (args.length == 0)
		{
			player.sendMessage(ChatColor.RED + "These commands are for DM events only and are not to be used for personal gain"+ChatColor.RESET);
			player.sendMessage(ChatColor.GOLD + "Dungeon Master Event Commands"+ChatColor.RESET);
			player.sendMessage("------------------------------");
			player.sendMessage(ChatColor.GOLD + "/event EMOTE <msg>"+ChatColor.RESET+" - Makes your current target emote");
			player.sendMessage(ChatColor.GOLD + "/event ATTACK <entityname>"+ChatColor.RESET+" - Makes your current target attack");
			player.sendMessage(ChatColor.GOLD + "/event SAY <msg>"+ChatColor.RESET+" - Makes your current target say something");
			player.sendMessage(ChatColor.GOLD + "/event LISTNPCS"+ChatColor.RESET+" - Lists event usable npcs");
			player.sendMessage(ChatColor.GOLD + "/event BROADCAST <msg>"+ChatColor.RESET+" - Sends a GM event broadcast");
			player.sendMessage(ChatColor.GOLD + "/event SPAWN <ID>"+ChatColor.RESET+" - Spawns an event usable npc id");
			player.sendMessage(ChatColor.GOLD + "/event INSPIRATION"+ChatColor.RESET+" - Grants you 5 inspiration");
			player.sendMessage(ChatColor.GOLD + "/event GIVEHEAD IDENTIFIER NAME"+ChatColor.RESET+" - Gives you a head from https://minecraft-heads.com/ Essentials-Code");
			return true;
		}
		
		String command = args[0].toUpperCase();
		String text = "";
		if (args.length > 1)
		{
			text = "";
			int current = 0;
			for (String entry : args) {
				current++;
				if (current <= 1)
					continue;

				text = text + entry + " ";
			}

			text = text.trim();
		}
		
		try
		{
			switch (command)
			{
				case "INSPIRATION":
					sendInspiration(player);
					break;
				case "EMOTE":
					return emote(player, text);
				case "ATTACK":
					String target = args[1];
					return attack(player, target);
				case "SAY":
					return say(player, text);
				case "LISTNPCS":
					return listnpcs(player);
				case "GIVEHEAD":
					return givehead(player,args[1],args[2]);
				case "BROADCAST":
					Bukkit.broadcastMessage(ChatColor.YELLOW+"<GMEvent-"+player.getName().toUpperCase() + ">: " + text.toUpperCase()+ChatColor.RESET);
					break;
				case "SPAWN":
					int npcid = Integer.parseInt(args[1]);
					return spawn(player, npcid);
				default:
					player.sendMessage("That is not a valid sub command, see /event");
					return true;
			}
		} catch (CoreStateInitException e)
		{
			player.sendMessage("Plugin not initialised");
		}
		return true;
	}

	private boolean givehead(Player player, String url, String name) {
		Essentials ess = (Essentials) Bukkit.getPluginManager().getPlugin("Essentials");
		if (ess != null)
		{
			final Material SKULL_ITEM = Material.PLAYER_HEAD;
			String owner = url;
			
			ItemStack itemSkull = player.getItemInHand();
	        SkullMeta metaSkull = null;
	        boolean spawn = false;

	        if (itemSkull != null && MaterialUtil.isPlayerHead(itemSkull.getType(), itemSkull.getDurability())) {
	            metaSkull = (SkullMeta) itemSkull.getItemMeta();
	        } else {
	            itemSkull = new ItemStack(SKULL_ITEM, 1, (byte) 3);
	            metaSkull = (SkullMeta) itemSkull.getItemMeta();
	            spawn = true;
	        }

	        metaSkull.setDisplayName(name);
	        metaSkull.setOwner(url);

	        itemSkull.setItemMeta(metaSkull);
	        InventoryWorkaround.addItems(player.getInventory(), itemSkull);
		}
		
		return true;
	}

	private boolean spawn(Player player, int npcid) throws CoreStateInitException {
		ISoliniaPlayer solPlayer = SoliniaPlayerAdapter.Adapt(player);
		if (solPlayer == null)
		{
			player.sendMessage("Your player is not loaded correctly");
			return true;
		}
		
		ISoliniaNPC npc = StateManager.getInstance().getConfigurationManager().getNPC(npcid);
		
		if (npc != null) {
			if (!npc.isEventUsable())
			{
				player.sendMessage("Cannot find an event usable npc by the ID you have provided. Please see /event listnpcs");
				return true;
			}
			
			npc.Spawn(player.getLocation(), 1);
			player.sendMessage("NPC spawned at your position");
		} else {
			player.sendMessage("Cannot find an event usable npc by the ID you have provided. Please see /event listnpcs");
			return true;
		}
		
		return true;
	}

	private boolean listnpcs(Player player) throws CoreStateInitException {
		ISoliniaPlayer solPlayer = SoliniaPlayerAdapter.Adapt(player);
		if (solPlayer == null)
		{
			player.sendMessage("Your player is not loaded correctly");
			return true;
		}
		
		for (ISoliniaNPC npc : StateManager.getInstance().getConfigurationManager().getNPCs().stream().filter(e -> e.isEventUsable()).collect(Collectors.toList()))
		{
			String racename = "";
			String classname = "";
			if (npc.getClassObj() != null)
				classname = npc.getClassObj().getShortName();
			if (npc.getRace() != null)
				racename = npc.getRace().getShortName();
			
			String boss = "";
			if (npc.isHeroic())
				boss += "HEROIC";
			if (npc.isBoss())
				boss += "BOSS";
			if (npc.isRaidheroic())
				boss += "RAIDHEROIC";
			if (npc.isRaidboss())
				boss += "RAIDBOSS";
			if (boss.equals(""))
				boss = "NORM";
			
			player.sendMessage("ID: " + npc.getId() + " Name: " + ChatColor.GOLD + npc.getName().toUpperCase() + ChatColor.RESET + " Level " + npc.getLevel() + " " + racename + " " + classname + " ["+boss+"]");
		}
		
		return true;
	}

	private boolean say(Player player,String text) throws CoreStateInitException {
		ISoliniaPlayer solPlayer = SoliniaPlayerAdapter.Adapt(player);
		if (solPlayer == null)
		{
			player.sendMessage("Your player is not loaded correctly");
			return true;
		}
		
		if (solPlayer.getEntityTarget() == null || solPlayer.getEntityTarget() instanceof Player || !(solPlayer.getEntityTarget() instanceof LivingEntity))
		{
			player.sendMessage("This command requires an event capable target to control");
			return true;
		}
		
		ISoliniaLivingEntity solLivingEntitySay = SoliniaLivingEntityAdapter.Adapt(solPlayer.getEntityTarget());
		if (solLivingEntitySay == null)
		{
			player.sendMessage("This command requires an event capable target to control");
			return true;
		}
		
		solLivingEntitySay.say(text);
		return true;

	}

	private boolean attack(Player player, String target) throws CoreStateInitException {
		ISoliniaPlayer solPlayer = SoliniaPlayerAdapter.Adapt(player);
		if (solPlayer == null)
		{
			player.sendMessage("Your player is not loaded correctly");
			return true;
		}
		
		if (solPlayer.getEntityTarget() == null || solPlayer.getEntityTarget() instanceof Player || !(solPlayer.getEntityTarget() instanceof LivingEntity))
		{
			player.sendMessage("This command requires an event capable target to control");
			return true;
		}
		
		ISoliniaLivingEntity solLivingEntityAttack = SoliniaLivingEntityAdapter.Adapt(solPlayer.getEntityTarget());
		if (solLivingEntityAttack == null || !solLivingEntityAttack.isNPC() || solLivingEntityAttack.getNPC() == null || !solLivingEntityAttack.getNPC().isEventUsable())
		{
			player.sendMessage("This command requires an event capable target to control");
			return true;
		}
		
		boolean found = false;
		for(Entity targetEntity : player.getNearbyEntities(25, 25, 25))
		{
			// dont attack self
			if (targetEntity.getUniqueId().equals(solLivingEntityAttack.getBukkitLivingEntity().getUniqueId()))
				continue;
			
			if (!targetEntity.getName().toUpperCase().equals(target.toUpperCase()))
				continue;
			
			solLivingEntityAttack.addToHateList(targetEntity.getUniqueId(), 999, true);
			player.sendMessage("Added " + targetEntity.getName() + " to hate list of " + solLivingEntityAttack.getName() + " with 999 hate");
			found = true;
			break;
		}
		if (!found)
			player.sendMessage("Could not find the target entity name ["+target+"] within 25 blocks of you");
			
		return true;

	}

	private boolean emote(Player player, String text) throws CoreStateInitException {
		ISoliniaPlayer solPlayer = SoliniaPlayerAdapter.Adapt(player);
		if (solPlayer == null)
		{
			player.sendMessage("Your player is not loaded correctly");
			return true;
		}
		
		if (solPlayer.getEntityTarget() == null || solPlayer.getEntityTarget() instanceof Player || !(solPlayer.getEntityTarget() instanceof LivingEntity))
		{
			player.sendMessage("This command requires an event capable target to control");
			return true;
		}
		
		ISoliniaLivingEntity solLivingEntityEmote = SoliniaLivingEntityAdapter.Adapt(solPlayer.getEntityTarget());
		if (solLivingEntityEmote == null)
		{
			player.sendMessage("This command requires an event capable target to control");
			return true;
		}
		
		solLivingEntityEmote.emote(solLivingEntityEmote.getName() + " " + text);
		return true;

	}

	private void sendInspiration(Player player) throws CoreStateInitException {
		ISoliniaPlayer targetPlayer = SoliniaPlayerAdapter.Adapt(player);
		targetPlayer.setInspiration(targetPlayer.getInspiration() + 5);
		player.sendMessage("You have granted " + 5 + " inspiration to " + player.getName());
	}
}
