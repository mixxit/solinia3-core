package com.solinia.solinia.Commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.solinia.solinia.Adapters.SoliniaPlayerAdapter;
import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Interfaces.ISoliniaAAAbility;
import com.solinia.solinia.Interfaces.ISoliniaAARank;
import com.solinia.solinia.Interfaces.ISoliniaPlayer;
import com.solinia.solinia.Managers.StateManager;
import com.solinia.solinia.Utils.Utils;

import net.md_5.bungee.api.ChatColor;

public class CommandAA implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		// TODO Auto-generated method stub
		
		if (sender instanceof Player) {
			Player player = (Player) sender;

			try {
				ISoliniaPlayer solplayer = SoliniaPlayerAdapter.Adapt(player);
				player.sendMessage("Current AA Points: " + solplayer.getAAPoints());

				if (args.length < 1) {
					player.sendMessage(ChatColor.GRAY + "Insufficient arguments (list,buy)");
					return true;
				}

				switch (args[0]) {
				case "list":
					for (ISoliniaAARank aarank : solplayer.getBuyableAARanks()) {
						ISoliniaAAAbility aaAbility = StateManager.getInstance().getConfigurationManager()
								.getAAAbility(aarank.getAbilityid());
						if (aaAbility != null)
							if (aarank.getCost() <= solplayer.getAAPoints()) {
								player.sendMessage(aaAbility.getName() + " Cost: " + aarank.getCost()
										+ " AA points /aa buy " + aarank.getId());
							} else {
								player.sendMessage(ChatColor.GRAY + aaAbility.getName() + " Cost: " + aarank.getCost()
										+ " (Insufficient points to buy)");
							}

					}
					player.sendMessage("End of available AA points list based on your current stacked up AA points");
					break;
				case "give":
					if (args.length < 3) {
						player.sendMessage("That is not a valid player name and amount /aa give playername amount");
						return true;
					}
					
					if (!player.isOp())
					{
						player.sendMessage("Op only command");
						return true;
					}
					
					Player targetplayer = Bukkit.getPlayer(args[1]);
					if (targetplayer == null)
					{
						player.sendMessage("Cannot find player");
						return true;
					}
					
					int newpoints = Integer.parseInt(args[2]);
					
					if (newpoints > 5)
					{
						player.sendMessage("Too many points man.... never give them more than 5, its supposed to be hard to achieve");
						return true;
					}
					
					ISoliniaPlayer targetsolplayer = SoliniaPlayerAdapter.Adapt(targetplayer);
					targetsolplayer.setAAPoints(targetsolplayer.getAAPoints() + newpoints);
					player.sendMessage("* Granted player " + newpoints + " aa points");
					targetplayer.sendMessage("* You have been granted " + newpoints + " AA points by Operator " + player.getName());
					break;
				case "buy":
					if (args.length < 2) {
						player.sendMessage("That is not a valid AA rank id - /aa buy aaabilityrankid (see /aa list)");
						return true;
					}
					
					int seekRankId = Integer.parseInt(args[1].toUpperCase());
					ISoliniaAARank aarank = StateManager.getInstance().getConfigurationManager().getAARank(seekRankId);
					if (aarank == null) {
						player.sendMessage("That is not a valid AA rank id - /aa buy aaabilityrankid (see /aa list)");
						return true;
					}
					
					ISoliniaAAAbility aa = StateManager.getInstance().getConfigurationManager().getAAAbility(aarank.getAbilityid());
					if (aa == null) {
						player.sendMessage("That is not a valid AA rank id - /aa buy aaabilityrankid (see /aa list)");
						return true;
					}
					
					if (!solplayer.canPurchaseAlternateAdvancementRank(aa, aarank)) 
					{
						player.sendMessage("You cannot buy that AA");
						return true;
					}

					solplayer.purchaseAlternateAdvancementRank(aa, aarank);

					if (player.getMaxHealth() < Utils.getStatMaxHP(solplayer)) {
						solplayer.updateMaxHp();
					}

				default:
					player.sendMessage(ChatColor.GRAY + "Invalid arguments (list,buy)");
					return true;
				}

			} catch (CoreStateInitException e) {

			}
		}
		return true;
	}

}
