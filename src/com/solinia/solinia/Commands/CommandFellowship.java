package com.solinia.solinia.Commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.solinia.solinia.Adapters.SoliniaPlayerAdapter;
import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Exceptions.FellowshipMemberNotFoundException;
import com.solinia.solinia.Interfaces.ISoliniaPlayer;
import com.solinia.solinia.Managers.StateManager;

public class CommandFellowship implements CommandExecutor {
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		// TODO Auto-generated method stub
		if (!(sender instanceof Player)) {
			return false;
		}
		Player player = (Player)sender;

		if (!sender.hasPermission("solinia.fellowship"))
		{
			sender.sendMessage("You do not have permission to access this command");
			return false;
		}
		
		try
		{
			ISoliniaPlayer solplayer = SoliniaPlayerAdapter.Adapt(player);
			
			if (args.length == 0) {
				player.sendMessage("Valid commands are: /fellowship invite <name>, /fellowship list, /fellowship accept, /fellowship decline, /fellowship leave,/f <msg>");
				return true;
			}
			
			if (args[0].equals("invite"))
			{
				if (args.length == 2)
				{
					Player target = Bukkit.getPlayer(args[1]);
					if (target != null)
					{
						if (!target.equals(player))
						{
							ISoliniaPlayer solOwner = SoliniaPlayerAdapter.Adapt(player);
							ISoliniaPlayer solTarget = SoliniaPlayerAdapter.Adapt(target);
							if (solOwner == null && solTarget == null)
							{
								player.sendMessage("You or the target appears to be unavailable for invite right now");
								return true;
							}
							
							if (solTarget.getFellowship() != null)
							{
								player.sendMessage("That player is already in a fellowship");
								return true;
							}
							
							if (solOwner.getFellowship() != null && !solOwner.getFellowship().getOwnerUuid().equals(solOwner.getCharacterId()))
							{
								player.sendMessage("You are not the leader of this fellowship to invite someone");
								return true;
							}

							StateManager.getInstance().invitePlayerToFellowship(solOwner,solTarget);					
						} else {
							player.sendMessage("You cannot invite yourself to a fellowship");
						}
					} else {
						player.sendMessage(args[1] + " is not online");
					}
					return true;
				} else {
					player.sendMessage("Incorrect arguments ["+args.length+"] for fellowship invite - see /fellowship");
					return true;
				}
			}
			
			if (args[0].equals("leave"))
			{
				if (solplayer.getFellowship() == null)
				{
					System.out.println("Player was not in fellowship to leave");
					return true;
				}
				
				solplayer.getFellowship().removePlayer(solplayer);
				return true;
			}
			if (args[0].equals("list"))
			{
				if (solplayer.getFellowship() == null)
					return true;
				
				try {
					solplayer.getFellowship().sendGroupList(solplayer);
				} catch (FellowshipMemberNotFoundException e) {
					solplayer.getBukkitPlayer().sendMessage("You do not appear to be in a fellowship");
				}
				return true;
			}
			if (args[0].equals("accept"))
			{
				StateManager.getInstance().acceptFellowshipInvite(solplayer);
				return true;
			}
			if (args[0].equals("decline"))
			{
				StateManager.getInstance().declineFellowshipInvite(solplayer);
				return true;
			}
		} catch (CoreStateInitException e)
		{
			sender.sendMessage(e.getMessage());
		}
		
		return true;
	}
}
