package com.solinia.solinia.Commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.solinia.solinia.Managers.StateManager;
import com.solinia.solinia.Models.PacketOpenCharacterCreation;
import com.solinia.solinia.Models.Solinia3UIChannelNames;
import com.solinia.solinia.Models.Solinia3UIPacketDiscriminators;
import com.solinia.solinia.Utils.ForgeUtils;

public class CommandOpenCharacterCreation implements CommandExecutor {
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		
		if (!(sender instanceof Player))
			return false;
		
	    try {
	    	PacketOpenCharacterCreation packet = new PacketOpenCharacterCreation();
	    	packet.fromData(StateManager.getInstance().getConfigurationManager().getCharacterCreationChoices());
			ForgeUtils.QueueSendForgeMessage(((Player)sender),Solinia3UIChannelNames.Outgoing,Solinia3UIPacketDiscriminators.CHARCREATION,packet.toPacketData(), 0);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return true;
	}

}
