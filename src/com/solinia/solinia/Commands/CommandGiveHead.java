package com.solinia.solinia.Commands;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import com.solinia.solinia.Solinia3CorePlugin;
import com.solinia.solinia.Utils.Utils;

public class CommandGiveHead implements CommandExecutor {
	Plugin plugin;
	public CommandGiveHead(Solinia3CorePlugin solinia3CorePlugin) {
		this.plugin = solinia3CorePlugin;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender instanceof Player) && !(sender instanceof CommandSender))
			return false;
		
		if (!sender.isOp() && !sender.hasPermission("solinia.givehead"))
		{
			sender.sendMessage("You do not have permission to access this command");
			return false;
		}
		
		// Args
		// playername
		// websiteurl
		
		if (args.length < 2)
		{
			return false;
		}
		
		if (!args[1].contains("minecraft-heads.com"))
		{
			sender.sendMessage("URL is not pointing to minecraft-heads.com");
			return true;
		}
		
		String playername = args[0];
		if (Bukkit.getPlayer(playername) == null)
		{
			sender.sendMessage("Invalid player");
			return true;
		}
		
		String httpSite = Utils.getHttpUrlAsString(args[1]);
		
		if (httpSite == null || httpSite.equals(""))
		{
			sender.sendMessage("Could not fetch site");
			return true;
		}
		
		Document doc = Jsoup.parse(httpSite);
		Element link = doc.select("textarea[name=UUID-Code-MC1-13]").first();
		String data = link.html();
		data = data.replaceAll("/give", "/minecraft:give");
		data = data.replaceAll("@p", args[0]);
		Utils.dispatchCommandLater(plugin, data);
		sender.sendMessage("Debug: " + data);
		sender.sendMessage("Head sent to " + args[0]);
		
		return true;
	}
}
