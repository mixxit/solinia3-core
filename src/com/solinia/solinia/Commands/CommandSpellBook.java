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
import com.solinia.solinia.Interfaces.ISoliniaSpell;
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
					solplayer.getBukkitPlayer().sendMessage("You must type part of the name to search for, ie: /spellbook search Healing");
					return true;
				}
				performSpellBookSearch(solplayer,args[1]);
				return true;
			case "list":
				performSpellBookList(solplayer);
				return true;
			case "remove":
				if (args.length < 2) {
					solplayer.getBukkitPlayer().sendMessage("That is not a claim id - /spellbook remove itemid");
					return true;
				}
				performSpellBookRemove(solplayer, Integer.parseInt(args[1]));
				return true;
			case "add":
				performSpellBookAdd(solplayer);
				return true;

			default:
				sender.sendMessage(ChatColor.GRAY + "Invalid arguments (list,search,remove,add)");
				return true;
			}

		} catch (CoreStateInitException e) {

		}
		return true;
	}

	private void performSpellBookAdd(ISoliniaPlayer solPlayer) {
		ItemStack primaryItem = solPlayer.getBukkitPlayer().getInventory().getItemInMainHand();
		if (primaryItem.getType().equals(Material.AIR)) {
			solPlayer.getBukkitPlayer().sendMessage(ChatColor.GRAY
					+ "Empty item in primary hand. You must hold the spell you want to place in your spellbook in your main hand");
			return;
		}
		
		if (!Utils.IsSoliniaItem(primaryItem))
		{
			solPlayer.getBukkitPlayer().sendMessage("This item is not a spell");
			return;
		}
		
		ISoliniaItem item = null;
		try {
			item = SoliniaItemAdapter.Adapt(primaryItem);
		} catch (SoliniaItemException e) {
			
		} catch (CoreStateInitException e) {
			return;
		}
		
		if (solPlayer.getClassObj() == null)
		{
			solPlayer.getBukkitPlayer().sendMessage("Players without a class cannot manipulate a magical spellbook");
			return;
		}
		
		if (item == null)
		{
			solPlayer.getBukkitPlayer().sendMessage("This item is not a spell");
			return;
		}
		
		if (!item.isSpellscroll())
		{
			solPlayer.getBukkitPlayer().sendMessage("This item is not a spell");
			return;
		}
		
		if (solPlayer.getSpellBookItems().contains(item.getId()))
		{
			solPlayer.getBukkitPlayer().sendMessage("This spell is already in your spell book");
			return;
		}
		
		try
		{
			ISoliniaSpell spell = StateManager.getInstance().getConfigurationManager().getSpell(item.getAbilityid());
			if (spell == null)
			{
				solPlayer.getBukkitPlayer().sendMessage("This item is not a valid spell");
				return;
			}
			
			if (!solPlayer.canUseSpell(spell))
			{
				solPlayer.getBukkitPlayer().sendMessage("Your lack sufficient knowledge to place such a spell in your magical spell book (Wrong class or Wrong level)");
				return;
			}
			
		} catch (CoreStateInitException e)
		{
			
		}
		
		solPlayer.getSpellBookItems().add(item.getId());
		solPlayer.getBukkitPlayer().getInventory().setItemInMainHand(null);
		solPlayer.getBukkitPlayer().updateInventory();
		solPlayer.getBukkitPlayer().sendMessage("Spell added to your spell book!");
	}

	private void performSpellBookRemove(ISoliniaPlayer solPlayer, int itemId) {
		List<Integer> itemIdsRemoved = new ArrayList<Integer>();
		
		for (int foundItemId : solPlayer.getSpellBookItems()) {
			try
			{
				ISoliniaItem item = StateManager.getInstance().getConfigurationManager().getItem(foundItemId);
	
				if (item == null)
					continue;
				
				if (item.getId() != itemId)
					continue;
				
				String mcaccountname = solPlayer.getBukkitPlayer().getName();
	
				SoliniaAccountClaim newclaim = new SoliniaAccountClaim();
				newclaim.setId(StateManager.getInstance().getConfigurationManager().getNextAccountClaimId());
				newclaim.setMcname(mcaccountname);
				newclaim.setItemid(foundItemId);
				newclaim.setClaimed(false);
				
				itemIdsRemoved.add(foundItemId);
	
				StateManager.getInstance().getConfigurationManager().addAccountClaim(newclaim);
				solPlayer.getBukkitPlayer().sendMessage("");
				
				TextComponent tc = new TextComponent(TextComponent.fromLegacyText("Added " + item.getDisplayname() + " to your /claim list " + ChatColor.AQUA + "[ Click here ]" + ChatColor.RESET));
				tc.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/claim list"));
				solPlayer.getBukkitPlayer().spigot().sendMessage(tc);
			} catch (CoreStateInitException e)
			{
				
			}
		}
		
		for (int itemIdRemoved : itemIdsRemoved)
		{
			solPlayer.getSpellBookItems().remove((Integer)itemIdRemoved);
		}
	}

	private void performSpellBookList(ISoliniaPlayer solPlayer) {
		solPlayer.getBukkitPlayer().sendMessage("All Spells: ");

		for (int itemId : solPlayer.getSpellBookItems()) {
			try
			{
				ISoliniaItem item = StateManager.getInstance().getConfigurationManager().getItem(itemId);
	
				if (item == null)
					continue;
	
				TextComponent tc = new TextComponent();
				
				tc.setText(ChatColor.LIGHT_PURPLE + item.getDisplayname() + " (/castspellbook " + item.getId() + ") " + ChatColor.AQUA + " [ Click here to remove ]");
				tc.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/spellbook remove " + itemId));
				tc.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_ITEM,
						new ComponentBuilder(item.asJsonString()).create()));
				solPlayer.getBukkitPlayer().spigot().sendMessage(tc);
			} catch (CoreStateInitException e)
			{
				// skip over
			}
		}
	}

	private void performSpellBookSearch(ISoliniaPlayer solPlayer, String searchTerm) {
		solPlayer.getBukkitPlayer().sendMessage("Matching Spells: ");

		for (int itemId : solPlayer.getSpellBookItems()) {
			try
			{
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
				solPlayer.getBukkitPlayer().spigot().sendMessage(tc);
			} catch (CoreStateInitException e)
			{
				
			}
		}
	}
}
