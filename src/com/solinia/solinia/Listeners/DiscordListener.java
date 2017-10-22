package com.solinia.solinia.Listeners;

import com.solinia.solinia.Solinia3CorePlugin;
import com.solinia.solinia.Managers.StateManager;
import com.solinia.solinia.Models.DiscordChannel;

import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.ReadyEvent;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;

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
		if (event.getChannel().getID().equals(StateManager.getInstance().getChannelManager().getDefaultDiscordChannel()))
		{
			// Handle command messaging here
			if (event.getMessage().getContent().startsWith("?"))
			{
				StateManager.getInstance().getChannelManager().handleDiscordCommand(DiscordChannel.DEFAULT,event);
			} else {
				StateManager.getInstance().getChannelManager().sendToGlobalChannel(event.getAuthor().getName()+"@"+event.getChannel().getName(), event.getMessage().getContent());
			}
		} 
		
		if (event.getChannel().getID().equals(StateManager.getInstance().getChannelManager().getAdminDiscordChannel()))
		{
			if (event.getMessage().getContent().startsWith("?"))
			{
				StateManager.getInstance().getChannelManager().handleDiscordCommand(DiscordChannel.ADMIN,event);
				
			} else {
				StateManager.getInstance().getChannelManager().sendToOps("[OPONLY]"+event.getAuthor().getName()+"@"+event.getChannel().getName(), event.getMessage().getContent());
			}
		}
    }
}
