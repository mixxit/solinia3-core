package com.solinia.solinia.Commands;

import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.solinia.solinia.Adapters.SoliniaPlayerAdapter;
import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Interfaces.ISoliniaItem;
import com.solinia.solinia.Interfaces.ISoliniaPlayer;
import com.solinia.solinia.Managers.StateManager;
import com.solinia.solinia.Models.SoliniaAccountClaim;
import com.solinia.solinia.Utils.Utils;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

public class CommandClaim implements CommandExecutor {
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		try {
			if (args.length < 1) {
				sender.sendMessage(ChatColor.GRAY + "Insufficient arguments (list,claim)");
				return true;
			}

			switch (args[0]) {
			case "list":
				if (!(sender instanceof Player))
					return false;

				Player player = (Player) sender;
				ISoliniaPlayer solplayer = SoliniaPlayerAdapter.Adapt(player);
				sender.sendMessage("Current Claims: " + solplayer.getAccountClaims().stream().filter(claim -> claim.getItemid() > 0).collect(Collectors.toList()).size());
				
				for (SoliniaAccountClaim claim : solplayer.getAccountClaims().stream().filter(claim -> claim.getItemid() > 0).collect(Collectors.toList())) 
				{
					//sender.sendMessage("Item: " + claim.getItemid());
					ISoliniaItem item = StateManager.getInstance().getConfigurationManager().getItem(claim.getItemid());
					
					if (item == null)
						continue;
					
					TextComponent tc = new TextComponent();
					tc.setText(ChatColor.LIGHT_PURPLE + item.getDisplayname() + ChatColor.AQUA + " [ Click here to remove ]");
					tc.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/claim claim " + claim.getId()));
					tc.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_ITEM,
							new ComponentBuilder(item.asJsonString()).create()));
					sender.spigot().sendMessage(tc);
				}
				
				sender.sendMessage("Use /claim claim all - to claim all your items");
				return true;
			case "give":
				if (!sender.isOp() && !sender.hasPermission("solinia.claim.give"))
					return false;
				
				if (args.length < 3) {
					sender.sendMessage("That is not a claim id - /claim give mcaccountname itemid");
					return true;
				}
				
				String mcaccountname = args[1];
				int itemId = Integer.parseInt(args[2].toUpperCase());
				
				if (Utils.AddAccountClaim(mcaccountname,itemId))
					sender.sendMessage("Account claim added!");
				
				Player targetplayer = Bukkit.getPlayer(mcaccountname);
				if (targetplayer != null && targetplayer.isOnline())
				{
					targetplayer.sendMessage(ChatColor.YELLOW + "* You seem to have received an item in your /claims");
				}
				
				return true;
			case "claim":
				if (!(sender instanceof Player))
					return false;

				Player claimPlayer = (Player) sender;
				ISoliniaPlayer solClaimPlayer = SoliniaPlayerAdapter.Adapt(claimPlayer);
				
				if (args.length < 2) {
					sender.sendMessage("That is not a claim id or the word all - /claim claim claimid (see /claim list) or /claim claim all");
					return true;
				}
				
				if (args[1].toUpperCase().equals("ALL"))
				{
					for(SoliniaAccountClaim claim : StateManager.getInstance().getConfigurationManager().getAccountClaims(claimPlayer.getName().toUpperCase()))
					{
						getClaim(claimPlayer,claim.getId());
					}
				} else {
					int seekClaimId = Integer.parseInt(args[1].toUpperCase());
					getClaim(claimPlayer,seekClaimId);
				}

				return true;

			default:
				sender.sendMessage(ChatColor.GRAY + "Invalid arguments (list,claim)");
				return true;
			}

		} catch (CoreStateInitException e) {

		}
		return true;
	}

	private void getClaim(Player claimPlayer, int seekClaimId) {
		try
		{
			SoliniaAccountClaim claim = StateManager.getInstance().getConfigurationManager().getAccountClaim(claimPlayer.getName().toUpperCase(),seekClaimId);
			if (claim == null) {
				TextComponent tc = new TextComponent();
				tc.setText("That is not a valid claim - /claim claim claimid (see /claim list)");
				claimPlayer.spigot().sendMessage(ChatMessageType.ACTION_BAR,tc);
				return;
			}
	
			ISoliniaItem item = StateManager.getInstance().getConfigurationManager()
					.getItem(claim.getItemid());
			if (item == null) {
				TextComponent tc = new TextComponent();
				tc.setText("That is not a valid claim item - /claim claim claimid (see /claim list)");
				claimPlayer.spigot().sendMessage(ChatMessageType.ACTION_BAR,tc);
				return;
			}
			
			claimPlayer.getWorld().dropItemNaturally(claimPlayer.getLocation(), item.asItemStack());
			TextComponent tc = new TextComponent();
			tc.setText("Claim item dropped at your feet - ID: " + claim.getId());
			claimPlayer.spigot().sendMessage(ChatMessageType.ACTION_BAR,tc);
			StateManager.getInstance().getConfigurationManager().removeClaim(claim.getId());
		} catch (CoreStateInitException e)
		{
			
		}

	}
}
