package com.solinia.solinia.Commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.solinia.solinia.Adapters.SoliniaPlayerAdapter;
import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Interfaces.ISoliniaPlayer;
import com.solinia.solinia.Managers.StateManager;
import com.solinia.solinia.Models.Bond;
import com.solinia.solinia.Models.Flaw;
import com.solinia.solinia.Models.Ideal;
import com.solinia.solinia.Models.Trait;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;


public class CommandPersonality implements CommandExecutor {
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender instanceof Player))
		{
			sender.sendMessage("This is a Player only command");
			return false;
		}
		
		if (args.length == 0)
		{
			sendCurrentPersonality(sender);
			return true;
		}
		
		switch(args[0])
		{
			case "award":
				awardCustomTrait(sender,args);
			break;
			case "bond":
				if (args.length == 1)
					sendBondChoices(sender);
				else
					setBondChoice(sender,Integer.parseInt(args[1]));
			break;
			case "ideal":
				if (args.length == 1)
					sendIdealChoices(sender);
				else
					setIdealChoice(sender,Integer.parseInt(args[1]));
			break;
			case "flaw":
				if (args.length == 1)
					sendFlawChoices(sender);
				else
					setFlawChoice(sender,Integer.parseInt(args[1]));
			break;
			case "trait":
				if (args.length == 1)
					sendTraitChoices(sender);
				else
					setTraitChoice(sender,Integer.parseInt(args[1]));
			break;
			default:
				sendCurrentPersonality(sender);
				break;
		}
		
		return true;
	}
	
	private void awardCustomTrait(CommandSender sender, String[] args) {
		if (!sender.isOp())
		{
			sender.sendMessage("This is an OP only command");
			return;
		}
		
		String target = args[1];
		
		if (Bukkit.getPlayer(target) == null)
		{
			sender.sendMessage("Cannot find player");
			return;
		}
		
		try
		{
			ISoliniaPlayer solPlayer = SoliniaPlayerAdapter.Adapt(Bukkit.getPlayer(target));
			if (solPlayer == null)
			{
				sender.sendMessage("That player does not exist");
				return;
			}
			String trait = "";
			for(int i = 0; i < args.length; i++)
			{
				if (i < 2)
					continue;
				
				trait += args[i] + " ";
			}
			
			trait = trait.trim();
			solPlayer.getPersonality().getCustomPersonalityTraits().add(trait);
			sender.sendMessage("Custom trait added");
		} catch (CoreStateInitException e)
		{
			
		}
		
	}

	private void setTraitChoice(CommandSender sender, int id) {
		try {
			ISoliniaPlayer solPlayer = SoliniaPlayerAdapter.Adapt((Player)sender);
			if (solPlayer.getPersonality().getFirstTraitId() > 0 && solPlayer.getPersonality().getSecondTraitId() > 0)
			{
				sender.sendMessage("You have already set your traits");
				return;
			}
			
			Trait trait = StateManager.getInstance().getConfigurationManager().getTrait(id);
			if (trait == null)
			{
				sender.sendMessage("That trait does not exist");
				return;
			}
			
			if (solPlayer.getPersonality().getFirstTraitId() == 0)
			{
				solPlayer.getPersonality().setFirstTraitId(trait.id);
				sender.sendMessage("Personality change confirmed");
			} else {
				solPlayer.getPersonality().setSecondTraitId(trait.id);
				sender.sendMessage("Personality change confirmed");
			}
		} catch (CoreStateInitException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void setFlawChoice(CommandSender sender, int id) {
		try {
			ISoliniaPlayer solPlayer = SoliniaPlayerAdapter.Adapt((Player)sender);
			if (solPlayer.getPersonality().getFlawId() > 0)
			{
				sender.sendMessage("You have already set your flaw");
				return;
			}
			
			Flaw flaw = StateManager.getInstance().getConfigurationManager().getFlaw(id);
			if (flaw == null)
			{
				sender.sendMessage("That flaw does not exist");
				return;
			}
			
			solPlayer.getPersonality().setFlawId(flaw.id);
			sender.sendMessage("Personality change confirmed");
		} catch (CoreStateInitException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void setIdealChoice(CommandSender sender, int id) {
		try {
			ISoliniaPlayer solPlayer = SoliniaPlayerAdapter.Adapt((Player)sender);
			if (solPlayer.getPersonality().getIdealId() > 0)
			{
				sender.sendMessage("You have already set your ideal");
				return;
			}
			
			Ideal ideal = StateManager.getInstance().getConfigurationManager().getIdeal(id);
			if (ideal == null)
			{
				sender.sendMessage("That ideal does not exist");
				return;
			}
			
			solPlayer.getPersonality().setIdealId(ideal.id);
			sender.sendMessage("Personality change confirmed");
		} catch (CoreStateInitException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void setBondChoice(CommandSender sender, int id) {
		try {
			ISoliniaPlayer solPlayer = SoliniaPlayerAdapter.Adapt((Player)sender);
			if (solPlayer.getPersonality().getBondId() > 0)
			{
				sender.sendMessage("You have already set your bond");
				return;
			}
			
			Bond bond = StateManager.getInstance().getConfigurationManager().getBond(id);
			if (bond == null)
			{
				sender.sendMessage("That bond does not exist");
				return;
			}
			
			solPlayer.getPersonality().setBondId(bond.id);
			sender.sendMessage("Personality change confirmed");
		} catch (CoreStateInitException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void sendCurrentPersonality(CommandSender sender) {
		try {
			ISoliniaPlayer solPlayer = SoliniaPlayerAdapter.Adapt((Player)sender);
			solPlayer.getPersonality();
			
			sender.sendMessage(ChatColor.GOLD + "IDEALS" + ChatColor.RESET);
			if (solPlayer.getPersonality().getIdealId() > 0)
			{
				sender.sendMessage("- " + solPlayer.getPersonality().getIdeal().description + " ");
			} else {
				TextComponent tc = new TextComponent();
				tc.setText("- You have no ideal set");
				
				TextComponent tc2 = new TextComponent();
				tc2.setText(ChatColor.AQUA + "[Click to Select]" + ChatColor.RESET);
				String changetext = "/personality ideal";
				tc2.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, changetext));	

				tc.addExtra(tc2);
				sender.spigot().sendMessage(tc);
			}
			
			sender.sendMessage(ChatColor.GOLD + "TRAITS" + ChatColor.RESET);
			if (solPlayer.getPersonality().getFirstTraitId() > 0)
			{
				sender.sendMessage("- " + solPlayer.getPersonality().getFirstTrait().description + " ");
			} else {
				TextComponent tc = new TextComponent();
				tc.setText("- You have no first trait set");
				
				TextComponent tc2 = new TextComponent();
				tc2.setText(ChatColor.AQUA + "[Click to Select]" + ChatColor.RESET);
				String changetext = "/personality trait";
				tc2.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, changetext));	

				tc.addExtra(tc2);
				sender.spigot().sendMessage(tc);
			}
			if (solPlayer.getPersonality().getSecondTraitId() > 0)
			{
				sender.sendMessage("- " + solPlayer.getPersonality().getSecondTrait().description + " ");
			} else {
				TextComponent tc = new TextComponent();
				tc.setText("- You have no second trait set");
				
				TextComponent tc2 = new TextComponent();
				tc2.setText(ChatColor.AQUA + "[Click to Select]" + ChatColor.RESET);
				String changetext = "/personality trait";
				tc2.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, changetext));	

				tc.addExtra(tc2);
				sender.spigot().sendMessage(tc);
			}
			
			sender.sendMessage(ChatColor.GOLD + "BONDS" + ChatColor.RESET);
			if (solPlayer.getPersonality().getBondId() > 0)
			{
				sender.sendMessage("- " + solPlayer.getPersonality().getBond().description + " ");
			} else {
				TextComponent tc = new TextComponent();
				tc.setText("- You have no bond set");
				
				TextComponent tc2 = new TextComponent();
				tc2.setText(ChatColor.AQUA + "[Click to Select]" + ChatColor.RESET);
				String changetext = "/personality bond";
				tc2.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, changetext));	

				tc.addExtra(tc2);
				sender.spigot().sendMessage(tc);
			}
			
			sender.sendMessage(ChatColor.GOLD + "FLAWS" + ChatColor.RESET);
			if (solPlayer.getPersonality().getFlawId() > 0)
			{
				sender.sendMessage("- " + solPlayer.getPersonality().getFlaw().description + " ");
			} else {
				TextComponent tc = new TextComponent();
				tc.setText("- You have no flaw set");
				
				TextComponent tc2 = new TextComponent();
				tc2.setText(ChatColor.AQUA + "[Click to Select]" + ChatColor.RESET);
				String changetext = "/personality flaw";
				tc2.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, changetext));	

				tc.addExtra(tc2);
				sender.spigot().sendMessage(tc);
			}
			
			sender.sendMessage(ChatColor.GOLD + "CUSTOM" + ChatColor.RESET);
			for(String customTrait : solPlayer.getPersonality().getCustomPersonalityTraits())
			{
				sender.sendMessage("- " + customTrait);
			}
			
		} catch (CoreStateInitException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void sendFlawChoices(CommandSender sender)
	{
		try
		{
			sender.sendMessage(ChatColor.GOLD + "FLAWS:" + ChatColor.RESET);
			TextComponent tc = new TextComponent();
			tc.setText(ChatColor.AQUA + "[Click-to-Select] " + ChatColor.RESET);
			for(Flaw flaw : StateManager.getInstance().getConfigurationManager().getFlaws())
			{
				TextComponent tc2 = new TextComponent();
				int maxLength = 6;
				if (flaw.description.length() < 6)
					maxLength = flaw.description.length();
				
				tc2.setText("[" + flaw.description.substring(0, maxLength) + "....] ");
				String changetext = "/personality flaw " + flaw.id;
				tc2.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, changetext));	
				tc2.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(flaw.description).create()));

				tc.addExtra(tc2);
			}
			sender.spigot().sendMessage(tc);
		} catch (CoreStateInitException e)
		{
			sender.sendMessage("Choices are not available right now");
		}
	}
	
	public void sendTraitChoices(CommandSender sender)
	{
		try
		{
			sender.sendMessage(ChatColor.GOLD + "TRAITS:" + ChatColor.RESET);
			TextComponent tc = new TextComponent();
			tc.setText(ChatColor.AQUA + "[Click-to-Select] " + ChatColor.RESET);
			for(Trait trait : StateManager.getInstance().getConfigurationManager().getTraits())
			{
				TextComponent tc2 = new TextComponent();
				int maxLength = 6;
				if (trait.description.length() < 6)
					maxLength = trait.description.length();
				
				tc2.setText("[" + trait.description.substring(0, maxLength) + "..] ");
				String changetext = "/personality trait " + trait.id;
				tc2.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, changetext));	
				tc2.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(trait.description).create()));

				tc.addExtra(tc2);
			}
			sender.spigot().sendMessage(tc);
		} catch (CoreStateInitException e)
		{
			sender.sendMessage("Choices are not available right now");
		}
	}
	
	public void sendIdealChoices(CommandSender sender)
	{
		try
		{
			sender.sendMessage(ChatColor.GOLD + "IDEALS:" + ChatColor.RESET);
			TextComponent tc = new TextComponent();
			tc.setText(ChatColor.AQUA + "[Click-to-Select] " + ChatColor.RESET);
			for(Ideal ideal : StateManager.getInstance().getConfigurationManager().getIdeals())
			{
				TextComponent tc2 = new TextComponent();
				int maxLength = 6;
				if (ideal.description.length() < 6)
					maxLength = ideal.description.length();
				
				tc2.setText("[" + ideal.description.substring(0, maxLength) + "....] ");
				String changetext = "/personality ideal " + ideal.id;
				tc2.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, changetext));	
				tc2.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(ideal.description).create()));

				tc.addExtra(tc2);
			}
			sender.spigot().sendMessage(tc);
		} catch (CoreStateInitException e)
		{
			sender.sendMessage("Choices are not available right now");
		}
	}
	
	public void sendBondChoices(CommandSender sender)
	{
		try
		{
			sender.sendMessage(ChatColor.GOLD + "BONDS:" + ChatColor.RESET);
			TextComponent tc = new TextComponent();
			tc.setText(ChatColor.AQUA + "[Click-to-Select] " + ChatColor.RESET);
			for(Bond bond : StateManager.getInstance().getConfigurationManager().getBonds())
			{
				TextComponent tc2 = new TextComponent();
				int maxLength = 6;
				if (bond.description.length() < 6)
					maxLength = bond.description.length();
				
				tc2.setText("[" + bond.description.substring(0, maxLength) + "....] ");
				String changetext = "/personality bond " + bond.id;
				tc2.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, changetext));	
				tc2.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(bond.description).create()));

				tc.addExtra(tc2);
			}
			sender.spigot().sendMessage(tc);
		} catch (CoreStateInitException e)
		{
			sender.sendMessage("Choices are not available right now");
		}
	}
}
