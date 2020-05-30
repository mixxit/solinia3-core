package com.solinia.solinia.Commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.solinia.solinia.Adapters.SoliniaPlayerAdapter;
import com.solinia.solinia.Interfaces.ISoliniaPlayer;
import com.solinia.solinia.Models.PacketOpenSpellbook;
import com.solinia.solinia.Models.Solinia3UIChannelNames;
import com.solinia.solinia.Models.Solinia3UIPacketDiscriminators;
import com.solinia.solinia.Utils.ForgeUtils;
import com.solinia.solinia.Utils.MathUtils;

public class CommandOpenSpellbook implements CommandExecutor {
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		
		if (!(sender instanceof Player))
			return false;
	    
		int pageNo = 0;
		
		if (args.length > 0)
		{
			if (!MathUtils.isInteger(args[0]))
				return false;				

			pageNo = Integer.parseInt(args[0]);	
		}
		
	    try {
	    	ISoliniaPlayer solPlayer = SoliniaPlayerAdapter.Adapt((Player)sender);
	    	PacketOpenSpellbook spellbookPacket = new PacketOpenSpellbook();
	    	spellbookPacket.fromData(solPlayer.getSpellbookPage(pageNo));
			ForgeUtils.QueueSendForgeMessage(((Player)solPlayer.getBukkitPlayer()),Solinia3UIChannelNames.Outgoing,Solinia3UIPacketDiscriminators.SPELLBOOKPAGE,spellbookPacket.toPacketData(),0);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return true;
	}
}
