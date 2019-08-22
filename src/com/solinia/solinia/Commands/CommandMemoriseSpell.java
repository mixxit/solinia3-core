package com.solinia.solinia.Commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.solinia.solinia.Adapters.SoliniaPlayerAdapter;
import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Interfaces.ISoliniaPlayer;
import com.solinia.solinia.Interfaces.ISoliniaSpell;
import com.solinia.solinia.Managers.StateManager;
import com.solinia.solinia.Models.GenericPacketMessage;
import com.solinia.solinia.Models.Solinia3UIChannelNames;
import com.solinia.solinia.Models.Solinia3UIPacketDiscriminators;
import com.solinia.solinia.Utils.ForgeUtils;
import com.solinia.solinia.Utils.Utils;

public class CommandMemoriseSpell implements CommandExecutor {
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender instanceof Player))
		{
			sender.sendMessage("This is a player only command");
			return false;
		}
		
		if (args.length < 2)
		{
			sender.sendMessage("You must provide the spell slot and spellid to memorise");
			return false;
		}
		
		try
		{
			int spellSlot = Integer.parseInt(args[0]);
			int spellId = Integer.parseInt(args[1]);
			
			ISoliniaSpell spell = StateManager.getInstance().getConfigurationManager().getSpell(spellId);
			if (spell == null)
			{
				sender.sendMessage("That spell does not exist");
				return false;
			}
			
			ISoliniaPlayer solPlayer = SoliniaPlayerAdapter.Adapt((Player)sender);
			if (solPlayer == null)
			{
				sender.sendMessage("Could not find player");
				return false;
			}

			if (!solPlayer.canUseSpell(spell))
			{
				sender.sendMessage("You are not the correct class/level to memorise this spell");
				return false;
			}
			
			if (spellSlot < 1)
			{
				sender.sendMessage("That spell slot does not exist");
				return false;
			}
			
			if (spellSlot > solPlayer.getMaxSpellSlots())
			{
				sender.sendMessage("That spell slot does not exist");
				return false;
			}
			
			if (!solPlayer.getSpellBookSpellIds().contains(spellId))
			{
				sender.sendMessage("This spell is not in your spell book");
				return false;
			}
			
			if (!solPlayer.memoriseSpell(spellSlot, spellId))
			{
				return false;
			} else {
				try
				{
					GenericPacketMessage message = new GenericPacketMessage();
					message.setMemorisedSpellSlots(solPlayer.getMemorisedSpellSlots());
			    	String json = Utils.getObjectAsJson(message);
					ForgeUtils.sendForgeMessage(((Player)solPlayer.getBukkitPlayer()),Solinia3UIChannelNames.Outgoing,Solinia3UIPacketDiscriminators.GENERIC_MESSAGE,json);
					System.out.println("Sent Memorised Spell Data data: " + json);
				} catch (Exception e)
				{
					System.out.println("Could not generated memorised spell data: " + e.getMessage());
					e.printStackTrace();
				}
			}
		} catch (CoreStateInitException e)
		{
			return false;
		}
		
		return true;
	}
}
