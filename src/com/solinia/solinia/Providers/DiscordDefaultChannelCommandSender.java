package com.solinia.solinia.Providers;

import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.PermissibleBase;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.plugin.Plugin;

import com.solinia.solinia.Managers.StateManager;

public class DiscordDefaultChannelCommandSender implements CommandSender {
	private final PermissibleBase perm;
	
	public DiscordDefaultChannelCommandSender()
	{
		this.perm = new PermissibleBase(this);
	}
	
	@Override
	public PermissionAttachment addAttachment(Plugin plugin) {
		return perm.addAttachment(plugin);
	}

	@Override
	public PermissionAttachment addAttachment(Plugin plugin, int i) {
		return perm.addAttachment(plugin, i);
	}

	@Override
	public PermissionAttachment addAttachment(Plugin plugin, String s, boolean b) {
		return perm.addAttachment(plugin, s, b);
	}

	@Override
	public PermissionAttachment addAttachment(Plugin plugin, String s, boolean b, int i) {
		return perm.addAttachment(plugin, s, b, i);
	}

	@Override
	public Set<PermissionAttachmentInfo> getEffectivePermissions() {
		// TODO Auto-generated method stub
		return perm.getEffectivePermissions();
	}

	@Override
	public boolean hasPermission(String arg0) {
		return true;
	}

	@Override
	public boolean hasPermission(Permission arg0) {
		return true;
	}

	@Override
	public boolean isPermissionSet(String arg0) {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean isPermissionSet(Permission arg0) {
		return true;
	}

	@Override
	public void recalculatePermissions() {
		perm.recalculatePermissions();
		
	}

	@Override
	public void removeAttachment(PermissionAttachment attachment) {
		perm.removeAttachment(attachment);
	}

	@Override
	public boolean isOp() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public void setOp(boolean arg0) {
		return;
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "Discord";
	}

	@Override
	public Server getServer() {
		return Bukkit.getServer();
	}

	@Override
	public void sendMessage(String message) {
		StateManager.getInstance().getChannelManager().sendToDiscordMC(null, StateManager.getInstance().getChannelManager().getDefaultDiscordChannel(), message);
	}

	@Override
	public void sendMessage(String[] args) {
		String message = "";
		for(int i = 0; i < args.length; i++)
		{
			message += args[i] + " ";
		}
		
		sendMessage(message.trim());
	}

	@Override
	public Spigot spigot() {
		return null;
	}

}
