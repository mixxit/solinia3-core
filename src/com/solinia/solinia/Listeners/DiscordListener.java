package com.solinia.solinia.Listeners;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import com.solinia.solinia.Solinia3CorePlugin;
import com.solinia.solinia.Managers.StateManager;
import com.solinia.solinia.Models.DiscordChannel;
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
				StateManager.getInstance().getChannelManager().sendToGlobalChannel(event.getAuthor().getName()+"@"+event.getChannel().getName(), event.getMessage().getContent(), true);
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
		
		IRole role = null;
		for(IRole currentRole : event.getGuild().getRoles())
		{
			if (!currentRole.getName().equals("Admin"))
				continue;
			
			role = currentRole;
		}
		
		if (event.getAuthor().getRolesForGuild(event.getGuild()).contains(role))
		{
			if (event.getMessage().getContent().startsWith("?"))
			{
				String[] words = event.getMessage().getContent().split(" "); 
				
				String command = "";
				for (int i = 0; i < words.length; i++)
				{
					if (i == 0)
					{
						command += words[i].replace("?","") + " ";
					} else {
						command += words[i] + " ";
					}
				}
				
				Bukkit.getServer().dispatchCommand(getCommandHandlerForChannelId(event.getChannel().getStringID()), command.trim());
				
			} else {
				StateManager.getInstance().getChannelManager().sendToOps("[OPONLY]"+event.getAuthor().getName()+"@"+event.getChannel().getName(), event.getMessage().getContent(), true);
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
