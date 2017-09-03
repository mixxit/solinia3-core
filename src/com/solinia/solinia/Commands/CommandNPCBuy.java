package com.solinia.solinia.Commands;

import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.solinia.solinia.Adapters.SoliniaLivingEntityAdapter;
import com.solinia.solinia.Adapters.SoliniaPlayerAdapter;
import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Interfaces.ISoliniaItem;
import com.solinia.solinia.Interfaces.ISoliniaLivingEntity;
import com.solinia.solinia.Interfaces.ISoliniaNPC;
import com.solinia.solinia.Interfaces.ISoliniaNPCMerchantEntry;
import com.solinia.solinia.Interfaces.ISoliniaPlayer;
import com.solinia.solinia.Managers.StateManager;

import net.md_5.bungee.api.ChatColor;
import net.milkbowl.vault.economy.EconomyResponse;

public class CommandNPCBuy implements CommandExecutor {

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

			int itemid = Integer.parseInt(args[0]);
			if (itemid < 1) {
				player.sendMessage("ItemID must be greater than 0");
				return true;
			}

			int amount = Integer.parseInt(args[1]);
			if (amount < 1) {
				player.sendMessage("Amount must be greater than 0");
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

			List<ISoliniaNPCMerchantEntry> items = StateManager.getInstance().getConfigurationManager()
					.getNPCMerchant(solnpc.getMerchantid()).getEntries();
			if (items.size() < 1) {
				player.sendMessage(ChatColor.GRAY + "* This NPC is not interested in this item");
				return true;
			}

			boolean found = false;
			for (ISoliniaNPCMerchantEntry item : items) {
				if (item.getItemid() != itemid)
					continue;

				found = true;
				break;
			}

			if (found == false) {
				player.sendMessage(ChatColor.GRAY + "* This NPC is not interested in this item");
				return true;
			}

			ISoliniaItem item = StateManager.getInstance().getConfigurationManager().getItem(itemid);
			if (item == null) {
				player.sendMessage(ChatColor.GRAY + "* This is not a valid item");
				return true;
			}

			int price = item.getWorth();
			price = price * amount;

			if (price > StateManager.getInstance().getEconomy().getBalance(player)) {
				player.sendMessage("You do not have sufficient balance to buy this item in that quantity.");
				return true;
			}

			EconomyResponse responsewithdraw = StateManager.getInstance().getEconomy()
					.withdrawPlayer(Bukkit.getOfflinePlayer(player.getUniqueId()), price);
			if (responsewithdraw.transactionSuccess()) {
				// Give player item

				int amountleft = amount;
				while (amountleft > 0) {
					if (amountleft >= 64) {
						ItemStack itemstack = item.asItemStack();
						itemstack.setAmount(64);
						amountleft = amountleft - 64;
						player.getWorld().dropItem(player.getLocation(), itemstack);
					} else {
						ItemStack itemstack = item.asItemStack();
						itemstack.setAmount(amountleft);
						amountleft = 0;
						player.getWorld().dropItem(player.getLocation(), itemstack);
					}
				}

				player.sendMessage(
						ChatColor.YELLOW + "* You pay $" + price + " for " + amount + " " + item.getDisplayname());
			} else {
				System.out.println(
						"Error withdrawing money from your account " + String.format(responsewithdraw.errorMessage));
				player.sendMessage(ChatColor.YELLOW + "* Error withdrawing money from your account "
						+ String.format(responsewithdraw.errorMessage));
			}

			return true;
		} catch (CoreStateInitException e) {
			player.sendMessage(e.getMessage());
		}

		return true;
	}
}
