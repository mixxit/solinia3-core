package com.solinia.solinia.Commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.solinia.solinia.Adapters.SoliniaItemAdapter;
import com.solinia.solinia.Adapters.SoliniaPlayerAdapter;
import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Exceptions.SoliniaItemException;
import com.solinia.solinia.Interfaces.ISoliniaItem;
import com.solinia.solinia.Interfaces.ISoliniaPlayer;
import com.solinia.solinia.Managers.StateManager;
import com.solinia.solinia.Models.SoliniaAccountClaim;
import com.solinia.solinia.Utils.Utils;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

public class CommandSpellBook implements CommandExecutor {
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		
		if (!(sender instanceof Player))
			return false;
		
		try {
			if (args.length < 1) {
				sender.sendMessage(ChatColor.GRAY + "Insufficient arguments (list,search,remove,add)");
				return true;
			}

			Player player = (Player) sender;
			ISoliniaPlayer solplayer = SoliniaPlayerAdapter.Adapt(player);
			
			switch (args[0].toLowerCase()) {
			case "search":
				if (args.length < 2)
				{
					player.sendMessage("You must type part of the name to search for, ie: /spellbook search Healing");
					return true;
				}
				
				String searchTerm = args[1].toUpperCase();
				
				sender.sendMessage("Matching Spells: ");

				for (int itemId : solplayer.getSpellBookItems()) {
					ISoliniaItem item = StateManager.getInstance().getConfigurationManager().getItem(itemId);

					if (item == null)
						continue;

					TextComponent tc = new TextComponent();
					if (!item.getDisplayname().toUpperCase().contains(searchTerm))
						continue;
					
					tc.setText(ChatColor.LIGHT_PURPLE + item.getDisplayname() + ChatColor.AQUA + " [ Click here to remove ]");
					tc.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/spellbook remove " + itemId));
					tc.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_ITEM,
							new ComponentBuilder(item.asJsonString()).create()));
					sender.spigot().sendMessage(tc);
				}

				return true;
			case "list":
				sender.sendMessage("All Spells: ");

				for (int itemId : solplayer.getSpellBookItems()) {
					ISoliniaItem item = StateManager.getInstance().getConfigurationManager().getItem(itemId);

					if (item == null)
						continue;

					TextComponent tc = new TextComponent();
					
					tc.setText(ChatColor.LIGHT_PURPLE + item.getDisplayname() + ChatColor.AQUA + " [ Click here to remove ]");
					tc.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/spellbook remove " + itemId));
					tc.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_ITEM,
							new ComponentBuilder(item.asJsonString()).create()));
					sender.spigot().sendMessage(tc);
				}

				return true;
			case "remove":
				if (args.length < 2) {
					sender.sendMessage("That is not a claim id - /spellbook remove itemid");
					return true;
				}
				
				int itemId = Integer.parseInt(args[1]);
				List<Integer> itemIdsRemoved = new ArrayList<Integer>();
				
				for (int foundItemId : solplayer.getSpellBookItems()) {
					ISoliniaItem item = StateManager.getInstance().getConfigurationManager().getItem(foundItemId);

					if (item == null)
						continue;
					
					if (item.getId() != itemId)
						continue;
					
					String mcaccountname = player.getName();

					SoliniaAccountClaim newclaim = new SoliniaAccountClaim();
					newclaim.setId(StateManager.getInstance().getConfigurationManager().getNextAccountClaimId());
					newclaim.setMcname(mcaccountname);
					newclaim.setItemid(foundItemId);
					newclaim.setClaimed(false);
					
					itemIdsRemoved.add(foundItemId);

					StateManager.getInstance().getConfigurationManager().addAccountClaim(newclaim);
					sender.sendMessage("");
					
					TextComponent tc = new TextComponent(TextComponent.fromLegacyText("Added " + item.getDisplayname() + " to your /claim list " + ChatColor.AQUA + "[ Click here ]" + ChatColor.RESET));
					tc.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/claim list"));
					sender.spigot().sendMessage(tc);
				}
				
				for (int itemIdRemoved : itemIdsRemoved)
				{
					solplayer.getSpellBookItems().remove((Integer)itemIdRemoved);
				}
				
				return true;
			case "add":
				ItemStack primaryItem = player.getInventory().getItemInMainHand();
				if (primaryItem.getType().equals(Material.AIR)) {
					player.sendMessage(ChatColor.GRAY
							+ "Empty item in primary hand. You must hold the spell you want to place in your spellbook in your main hand");
					return false;
				}
				
				if (!Utils.IsSoliniaItem(primaryItem))
				{
					player.sendMessage("This item is not a spell");
					return false;
				}
				
				ISoliniaItem item = null;
				try {
					item = SoliniaItemAdapter.Adapt(primaryItem);
				} catch (SoliniaItemException e) {
					
				}
				
				if (item == null)
				{
					player.sendMessage("This item is not a spell");
					return false;
				}
				
				if (!item.isSpellscroll())
				{
					player.sendMessage("This item is not a spell");
					return false;
				}
				
				if (solplayer.getSpellBookItems().contains(item.getId()))
				{
					player.sendMessage("This spell is already in your spell book");
					return false;
				}
				
				solplayer.getSpellBookItems().add(item.getId());
				player.getInventory().setItemInMainHand(null);
				player.updateInventory();
				player.sendMessage("Spell added to your spell book!");
				return true;

			default:
				sender.sendMessage(ChatColor.GRAY + "Invalid arguments (list,search,remove,add)");
				return true;
			}

		} catch (CoreStateInitException e) {

		}
		return true;
	}
}
