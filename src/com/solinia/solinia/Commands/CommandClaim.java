package com.solinia.solinia.Commands;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.solinia.solinia.Adapters.SoliniaLivingEntityAdapter;
import com.solinia.solinia.Adapters.SoliniaPlayerAdapter;
import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Interfaces.ISoliniaAAAbility;
import com.solinia.solinia.Interfaces.ISoliniaAARank;
import com.solinia.solinia.Interfaces.ISoliniaItem;
import com.solinia.solinia.Interfaces.ISoliniaLivingEntity;
import com.solinia.solinia.Interfaces.ISoliniaPlayer;
import com.solinia.solinia.Managers.StateManager;
import com.solinia.solinia.Models.SoliniaAccountClaim;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

public class CommandClaim implements CommandExecutor {
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender instanceof Player))
			return false;

		Player player = (Player) sender;

		try {
			ISoliniaPlayer solplayer = SoliniaPlayerAdapter.Adapt(player);
			player.sendMessage("Current Claims: " + solplayer.getAccountClaims().size());

			if (args.length < 1) {
				player.sendMessage(ChatColor.GRAY + "Insufficient arguments (list,claim)");
				return true;
			}

			switch (args[0]) {
			case "list":
				for (SoliniaAccountClaim claim : solplayer.getAccountClaims()) 
				{
					ISoliniaItem item = StateManager.getInstance().getConfigurationManager().getItem(claim.getItemid());
					
					if (item == null)
						continue;
					
					TextComponent tc = new TextComponent();
					tc.setText(ChatColor.LIGHT_PURPLE + item.getDisplayname() + " /claim claim " + claim.getId());
					
					tc.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_ITEM,
							new ComponentBuilder(item.asJsonString()).create()));
					sender.spigot().sendMessage(tc);
				}
				
				return true;
			case "give":
				if (!player.isOp())
					return false;
				
				if (args.length < 3) {
					player.sendMessage("That is not a claim id - /claim give mcaccountname itemid");
					return true;
				}
				
				String mcaccountname = args[1];
				int itemId = Integer.parseInt(args[2].toUpperCase());
				
				SoliniaAccountClaim newclaim = new SoliniaAccountClaim();
				newclaim.setId(StateManager.getInstance().getConfigurationManager().getNextAccountClaimId());
				newclaim.setMcname(mcaccountname);
				newclaim.setItemid(itemId);
				newclaim.setClaimed(false);
				
				StateManager.getInstance().getConfigurationManager().addAccountClaim(newclaim);
				player.sendMessage("Account claim added!");
				return true;
			case "claim":
				if (args.length < 2) {
					player.sendMessage("That is not a claim id - /claim claim claimid (see /claim list)");
					return true;
				}

				int seekClaimId = Integer.parseInt(args[1].toUpperCase());
				SoliniaAccountClaim claim = StateManager.getInstance().getConfigurationManager().getAccountClaim(player.getName(),seekClaimId);
				if (claim == null) {
					player.sendMessage("That is not a valid claim - /claim claim claimid (see /aa claim)");
					return true;
				}

				ISoliniaItem item = StateManager.getInstance().getConfigurationManager()
						.getItem(claim.getItemid());
				if (item == null) {
					player.sendMessage("That is not a valid claim - /claim claim claimid (see /aa claim)");
					return true;
				}

				player.getWorld().dropItemNaturally(player.getLocation(), item.asItemStack());
				player.sendMessage("Claim item dropped at your feet - ID: " + claim.getId());
				StateManager.getInstance().getConfigurationManager().removeClaim(claim.getId());

				return true;

			default:
				player.sendMessage(ChatColor.GRAY + "Invalid arguments (list,claim)");
				return true;
			}

		} catch (CoreStateInitException e) {

		}
		return true;
	}
}
