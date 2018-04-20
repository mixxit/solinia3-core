package com.solinia.solinia.Commands;

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

import net.md_5.bungee.api.ChatColor;
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
				sender.sendMessage("Current Claims: " + solplayer.getAccountClaims().size());
				
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
				if (!sender.isOp())
					return false;
				
				if (args.length < 3) {
					sender.sendMessage("That is not a claim id - /claim give mcaccountname itemid");
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
				sender.sendMessage("Account claim added!");
				return true;
			case "claim":
				if (!(sender instanceof Player))
					return false;

				Player claimPlayer = (Player) sender;
				ISoliniaPlayer solClaimPlayer = SoliniaPlayerAdapter.Adapt(claimPlayer);
				sender.sendMessage("Current Claims: " + solClaimPlayer.getAccountClaims().size());
				
				if (args.length < 2) {
					sender.sendMessage("That is not a claim id - /claim claim claimid (see /claim list)");
					return true;
				}

				int seekClaimId = Integer.parseInt(args[1].toUpperCase());
				System.out.println("Looking up claims for player name: " + claimPlayer.getName() + " for claim ID: " + seekClaimId);
				SoliniaAccountClaim claim = StateManager.getInstance().getConfigurationManager().getAccountClaim(claimPlayer.getName().toUpperCase(),seekClaimId);
				if (claim == null) {
					sender.sendMessage("That is not a valid claim - /claim claim claimid (see /claim list)");
					return true;
				}

				ISoliniaItem item = StateManager.getInstance().getConfigurationManager()
						.getItem(claim.getItemid());
				if (item == null) {
					sender.sendMessage("That is not a valid claim item - /claim claim claimid (see /claim list)");
					return true;
				}

				claimPlayer.getWorld().dropItemNaturally(claimPlayer.getLocation(), item.asItemStack());
				sender.sendMessage("Claim item dropped at your feet - ID: " + claim.getId());
				StateManager.getInstance().getConfigurationManager().removeClaim(claim.getId());

				return true;

			default:
				sender.sendMessage(ChatColor.GRAY + "Invalid arguments (list,claim)");
				return true;
			}

		} catch (CoreStateInitException e) {

		}
		return true;
	}
}
