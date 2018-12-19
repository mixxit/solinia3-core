package com.solinia.solinia.Listeners;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import com.solinia.solinia.Solinia3CorePlugin;
import com.solinia.solinia.Managers.StateManager;
import com.solinia.solinia.Models.DiscordChannel;
import com.solinia.solinia.Utils.Utils;

import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.ReadyEvent;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IRole;

public class DiscordListener {
	Solinia3CorePlugin plugin;
	
	public DiscordListener(Solinia3CorePlugin solinia3CorePlugin) {
		this.plugin = solinia3CorePlugin;
	}

	@EventSubscriber
    public void onReadyEvent(ReadyEvent event) { // This method is called when the ReadyEvent is dispatched
        System.out.println("Discord Ready");
    }
  
	@EventSubscriber
    public void onMessageReceivedEvent(MessageReceivedEvent event) { // This method is NOT called because it doesn't have the @EventSubscriber annotation
		System.out.println(event.getAuthor().getName()+"@"+event.getChannel().getName()+":"+event.getMessage().getContent());
		if (event.getChannel().getStringID().equals(StateManager.getInstance().getChannelManager().getDiscordMainChannelId()))
		{
			// Handle command messaging here
			if (event.getMessage().getContent().startsWith("?"))
			{
				StateManager.getInstance().getChannelManager().handleDiscordCommand(DiscordChannel.DEFAULT,event);
			} else {
				StateManager.getInstance().getChannelManager().sendToGlobalChannel("[O" + ChatColor.AQUA + "DISC" + ChatColor.RESET + "|" + ChatColor.YELLOW + event.getChannel().getName().toUpperCase().substring(0, 2) + ChatColor.RESET + "]" + ChatColor.YELLOW + "~"+event.getAuthor().getName() + ChatColor.RESET, event.getMessage().getContent(), true, null);
			}
		}
		
		if (event.getChannel().getStringID().equals(StateManager.getInstance().getChannelManager().getDiscordInCharacterChannelId()))
		{
			// Handle command messaging here
			if (event.getMessage().getContent().startsWith("?"))
			{
				StateManager.getInstance().getChannelManager().handleDiscordCommand(DiscordChannel.INCHARACTER,event);
			}
		}
		
		if (event.getChannel().getStringID().equals(StateManager.getInstance().getChannelManager().getDiscordContentTeamChannelId()))
		{
			// Handle command messaging here
			if (event.getMessage().getContent().startsWith("?"))
			{
				StateManager.getInstance().getChannelManager().handleDiscordCommand(DiscordChannel.CONTENTTEAM,event);
			}
		}
		
		IRole adminRole = null;
		IRole contentTeamRole = null;
		
		for(IRole currentRole : event.getGuild().getRoles())
		{
			if (currentRole.getName().equals("Admin"))
			{
				adminRole = currentRole;
			}
			
			if (currentRole.getName().equals("Content Team"))
			{
				contentTeamRole = currentRole;
			}
		}
		
		if (event.getAuthor().getRolesForGuild(event.getGuild()).contains(adminRole))
		{
			if (event.getMessage().getContent().startsWith("^"))
			{
				String[] words = event.getMessage().getContent().split(" "); 
				
				String command = "";
				for (int i = 0; i < words.length; i++)
				{
					if (i == 0)
					{
						command += words[i].replace("^","") + " ";
					} else {
						command += words[i] + " ";
					}
				}
				
				Utils.dispatchCommandLater(plugin,getCommandHandlerForChannelId(event.getChannel().getStringID()), command.trim());
				
			}
		}
		
		if (event.getAuthor().getRolesForGuild(event.getGuild()).contains(contentTeamRole))
		{
			if (event.getMessage().getContent().startsWith("?"))
			{
				String[] words = event.getMessage().getContent().split(" "); 
				
				String commandName = "";
				String command = "";
				for (int i = 0; i < words.length; i++)
				{
					if (i == 0)
					{
						command += words[i].replace("?","") + " ";
						commandName = words[i].replace("?","");
					} else {
						command += words[i] + " ";
					}
				}
				
				boolean hasAccessToCommand = false;
				
				switch (commandName.trim())
				{
					case "flushdiscord":
					case "time":
					case "broadcast":
					case "ban":
					case "kick":
					case "dmarker":
					case "tp":
					case "weather":
					case "addlootdropitem":
					case "addloottablelootdrop":
					case "addmerchantitem":
					case "characternewunlimited":
					case "convertmerchanttolootdrop":
					case "createallarmorsets":
					case "createcraft":
					case "createfaction":
					case "createitem":
					case "createloottable":
					case "createmerchantlist":
					case "createnpc":
					case "createnpccopy":
					case "createnpcevent":
					case "createspawngroup":
					case "createzone":
					case "editchunk":
					case "editcraft":
					case "editfaction":
					case "edititem":
					case "editlootdrop":
					case "editloottable":
					case "editmerchantlist":
					case "editnpc":
					case "editnpcevent":
					case "editquest":
					case "editspawngroup":
					case "editzone":
					case "forcelevel":
					case "gmnpc":
					case "gmspells":
					case "granttitle":
					case "inspirationgrant":
					case "listclasses":
					case "listcrafts":
					case "listfactions":
					case "listitems":
					case "givehead":
					case "listlootdrops":
					case "listmerchantlists":
					case "listnpcs":
					case "listnpcspells":
					case "listquests":
					case "listraces":
					case "listspawngroups":
					case "listspells":
					case "listzones":
					case "npcsay":
					case "playeremote":
					case "resetpersonality":
					case "spawnitem":
					case "spawnnpc":
						hasAccessToCommand = true;
					break;
					default:
						hasAccessToCommand = false;
				}
				
				if (hasAccessToCommand)
					Utils.dispatchCommandLater(plugin,getCommandHandlerForChannelId(event.getChannel().getStringID()), command.trim());
				
			}
		}
    }

	private CommandSender getCommandHandlerForChannelId(String channelId) {
		if (channelId.equals(StateManager.getInstance().getChannelManager().getDiscordMainChannelId()))
			return StateManager.getInstance().getDiscordDefaultChannelCommandSender();
		
		if (channelId.equals(StateManager.getInstance().getChannelManager().getDiscordInCharacterChannelId()))
			return StateManager.getInstance().getDiscordInCharacterChannelCommandSender();
		
		if (channelId.equals(StateManager.getInstance().getChannelManager().getDiscordContentTeamChannelId()))
			return StateManager.getInstance().getDiscordContentTeamChannelCommandSender();
		
		if (channelId.equals(StateManager.getInstance().getChannelManager().getDiscordAdminChannelId()))
			return StateManager.getInstance().getDiscordAdminChannelCommandSender();
			
		return StateManager.getInstance().getDiscordDefaultChannelCommandSender();
	}
}
