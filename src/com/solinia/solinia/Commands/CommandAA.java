package com.solinia.solinia.Commands;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.attribute.Attribute;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.solinia.solinia.Adapters.SoliniaLivingEntityAdapter;
import com.solinia.solinia.Adapters.SoliniaPlayerAdapter;
import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Interfaces.ISoliniaAAAbility;
import com.solinia.solinia.Interfaces.ISoliniaAARank;
import com.solinia.solinia.Interfaces.ISoliniaLivingEntity;
import com.solinia.solinia.Interfaces.ISoliniaPlayer;
import com.solinia.solinia.Managers.StateManager;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

public class CommandAA implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender instanceof Player))
			return false;

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
				int pageno = 1;
				if (args.length > 1)
					pageno = Integer.parseInt(args[1]);

				pageno = pageno - 1;
				int sizePerPage = 8;
				List<ISoliniaAARank> fullaaranks = solplayer.getBuyableAARanks();
				List<ISoliniaAARank> aaranks = fullaaranks.stream().skip(pageno * sizePerPage).limit(sizePerPage)
						.collect(Collectors.toCollection(ArrayList::new));

				for (ISoliniaAARank aarank : aaranks) {
					ISoliniaAAAbility aaAbility = StateManager.getInstance().getConfigurationManager()
							.getAAAbility(aarank.getAbilityid());
					if (aaAbility != null)
						if (!aaAbility.isEnabled())
							continue;

					if (aarank.getCost() <= solplayer.getAAPoints()) {
						if (solplayer.canPurchaseAlternateAdvancementRank(aaAbility, aarank)) {
							TextComponent tc = new TextComponent(TextComponent.fromLegacyText(ChatColor.LIGHT_PURPLE + aaAbility.getName() + " Rank " + aarank.getPosition()
							+ ChatColor.RESET + " Cost: " + ChatColor.YELLOW + aarank.getCost()
							+ ChatColor.RESET + " AA " + ChatColor.LIGHT_PURPLE + "[Click to Buy]" + ChatColor.RESET));
							tc.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND,"/aa buy " + aarank.getId()));
							String details = ChatColor.GOLD + aaAbility.getName() + " Rank: " + aarank.getPosition()
									+ ChatColor.RESET + System.lineSeparator() + aarank.getDescription() + ChatColor.RESET + System.lineSeparator() + "Cost: " + aarank.getCost() + " AA points";
							tc.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
									new ComponentBuilder(details).create()));
							sender.spigot().sendMessage(tc);
						} else {
							if (aarank.getLevel_req() <= solplayer.getActualLevel())
							{
								TextComponent tc = new TextComponent(TextComponent.fromLegacyText(ChatColor.GRAY + "[NB] " + aaAbility.getName() + " Rank " + aarank.getPosition()
								+ ChatColor.GRAY + " Cost: " + ChatColor.GRAY + aarank.getCost() + ChatColor.GRAY
								+ " Cannot purchase yet"));
								String details = ChatColor.GOLD + aaAbility.getName() + " Rank: " + aarank.getPosition()
										+ ChatColor.RESET + System.lineSeparator() + aarank.getDescription() + ChatColor.RESET;
								tc.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
										new ComponentBuilder(details).create()));
								sender.spigot().sendMessage(tc);
							}
						}
					} else {
						TextComponent tc = new TextComponent(TextComponent.fromLegacyText(ChatColor.GRAY + "[NA] " + aaAbility.getName() + " Rank " + aarank.getPosition()
						+ ChatColor.GRAY + " Cost: " + ChatColor.RED + aarank.getCost() + ChatColor.GRAY
						+ " Insufficient AA points"));
						String details = ChatColor.GOLD + aaAbility.getName() + " Rank: " + aarank.getPosition()
								+ ChatColor.RESET + System.lineSeparator() + aarank.getDescription() + ChatColor.RESET;
						tc.setHoverEvent(
								new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(details).create()));
						sender.spigot().sendMessage(tc);
					}
				}
				player.sendMessage("Displayed Page " + ChatColor.GOLD + (pageno + 1) + ChatColor.RESET + "/"
						+ ChatColor.GOLD + Math.ceil((float) fullaaranks.size() / (float) sizePerPage) + ChatColor.RESET
						+ " (See /aa list <pageno>");
				player.sendMessage("More items may appear when you have more AA points available to spend");
				break;
			case "give":
				if (args.length < 3) {
					player.sendMessage("That is not a valid player name and amount /aa give playername amount");
					return true;
				}

				if (!player.isOp()) {
					player.sendMessage("Op only command");
					return true;
				}

				Player targetplayer = Bukkit.getPlayer(args[1]);
				if (targetplayer == null) {
					player.sendMessage("Cannot find player");
					return true;
				}

				int newpoints = Integer.parseInt(args[2]);

				if (newpoints < 1) {
					player.sendMessage("Too few points never give them less than 1");
					return true;
				}

				if (newpoints > 5) {
					player.sendMessage(
							"Too many points man.... never give them more than 5, its supposed to be hard to achieve");
					return true;
				}

				ISoliniaPlayer targetsolplayer = SoliniaPlayerAdapter.Adapt(targetplayer);
				targetsolplayer.setAAPoints(targetsolplayer.getAAPoints() + newpoints);
				player.sendMessage("* Granted player " + newpoints + " aa points");
				targetplayer.sendMessage(
						"* You have been granted " + newpoints + " AA points by Operator " + player.getName());
				break;
			case "buy":
				if (args.length < 2) {
					player.sendMessage("That is not a valid AA rank id - /aa buy aaabilityrankid (see /aa list)");
					return true;
				}

				int seekRankId = Integer.parseInt(args[1].toUpperCase());
				ISoliniaAARank aarank = StateManager.getInstance().getConfigurationManager().getAARankCache(seekRankId);
				if (aarank == null) {
					player.sendMessage("That is not a valid AA rank id - /aa buy aaabilityrankid (see /aa list)");
					return true;
				}

				ISoliniaAAAbility aa = StateManager.getInstance().getConfigurationManager()
						.getAAAbility(aarank.getAbilityid());
				if (aa == null) {
					player.sendMessage("That is not a valid AA rank id - /aa buy aaabilityrankid (see /aa list)");
					return true;
				}

				if (!solplayer.canPurchaseAlternateAdvancementRank(aa, aarank)) {
					player.sendMessage("You cannot buy that AA");
					return true;
				}

				solplayer.purchaseAlternateAdvancementRank(aa, aarank);

				try {
					ISoliniaLivingEntity solentity = SoliniaLivingEntityAdapter.Adapt(player);

					if (player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue() < solentity.getMaxHP()) {
						solplayer.updateMaxHp();
					}
				} catch (CoreStateInitException e) {

				}

				return true;

			default:
				player.sendMessage(ChatColor.GRAY + "Invalid arguments (list,buy)");
				return true;
			}

		} catch (CoreStateInitException e) {

		}
		return true;
	}

}
