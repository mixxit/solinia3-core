package com.solinia.solinia.Commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import com.solinia.solinia.Adapters.SoliniaLivingEntityAdapter;
import com.solinia.solinia.Adapters.SoliniaPlayerAdapter;
import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Interfaces.ISoliniaItem;
import com.solinia.solinia.Interfaces.ISoliniaLivingEntity;
import com.solinia.solinia.Interfaces.ISoliniaNPC;
import com.solinia.solinia.Interfaces.ISoliniaPlayer;
import com.solinia.solinia.Managers.StateManager;
import com.solinia.solinia.Utils.Utils;

import net.md_5.bungee.api.ChatColor;
import net.milkbowl.vault.economy.EconomyResponse;

public class CommandNPCSell implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender instanceof Player))
			return false;
		Player player = (Player) sender;
		try {
			ISoliniaPlayer solplayer = SoliniaPlayerAdapter.Adapt(player);

			if (args.length < 2) {
				player.sendMessage("Invalid syntax missing <itemid> <amount>");
				return true;
			}

			if (solplayer.getInteraction() == null) {
				player.sendMessage(ChatColor.GRAY + "* You are not currently interacting with an NPC merchant");
				return true;
			}

			Entity entity = Bukkit.getEntity(solplayer.getInteraction());

			if (entity == null) {
				player.sendMessage(ChatColor.GRAY
						+ "* The npc merchant you are trying to interact with appears to no longer be available");
				return true;
			}

			if (!(entity instanceof LivingEntity)) {
				player.sendMessage(ChatColor.GRAY
						+ "* The npc merchant you are trying to interact with appears to no longer be living");
				return true;
			}

			ISoliniaLivingEntity solentity = SoliniaLivingEntityAdapter.Adapt((LivingEntity) entity);
			if (solentity.getNpcid() < 1) {
				player.sendMessage(ChatColor.GRAY + "* You are not currently interacting with an NPC merchant");
				return true;
			}

			ISoliniaNPC solnpc = StateManager.getInstance().getConfigurationManager().getNPC(solentity.getNpcid());
			if (solnpc.getMerchantid() < 1) {
				player.sendMessage(ChatColor.GRAY + "* You are not currently interacting with an NPC merchant");
				return true;
			}

			int itemid = Integer.parseInt(args[0]);
			if (itemid < 1) {
				player.sendMessage("ItemID must be greater than 0");
				return true;
			}

			int amount = Integer.parseInt(args[1]);
			if (amount < 1) {
				player.sendMessage("Amount must be greater than 0");
				return true;
			}
			
			ISoliniaItem item = StateManager.getInstance().getConfigurationManager().getItem(itemid);

			if (item.isTemporary())
			{
				player.sendMessage("Cannot sell temporary items");
				return true;
			}
			
			int individualprice = item.getWorth();

			// Total price
			int price = individualprice * amount;

			if (Utils.getPlayerTotalCountOfItemId(player, itemid) < amount) {
				player.sendMessage("Sorry but you do not have the quantity you are trying to sell");
				return true;
			}

			EconomyResponse responsedeposit = StateManager.getInstance().getEconomy().depositPlayer(player, price);
			if (responsedeposit.transactionSuccess()) {
				// Add to buy back list
				StateManager.getInstance().getEntityManager().addTemporaryMerchantItem(solnpc.getId(), itemid, amount);
				// Take players item
				Utils.removeItemsFromInventory(player, itemid, amount);
				player.sendMessage(ChatColor.YELLOW + "* You recieve $" + price + " as payment");
			} else {
				System.out.println(
						"Error depositing money to users account " + String.format(responsedeposit.errorMessage));
				player.sendMessage(ChatColor.YELLOW + "* Error depositing money to your account "
						+ String.format(responsedeposit.errorMessage));
			}

			return true;
		} catch (CoreStateInitException e) {
			player.sendMessage(e.getMessage());
		}
		
		return true;
	}
}
