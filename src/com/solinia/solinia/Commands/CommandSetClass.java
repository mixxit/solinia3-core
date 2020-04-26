package com.solinia.solinia.Commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import com.solinia.solinia.Adapters.SoliniaPlayerAdapter;
import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Interfaces.ISoliniaClass;
import com.solinia.solinia.Interfaces.ISoliniaPlayer;
import com.solinia.solinia.Interfaces.ISoliniaRace;
import com.solinia.solinia.Managers.StateManager;
import com.solinia.solinia.Utils.EntityUtils;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

public class CommandSetClass implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender instanceof Player))
			return false;
		
		try {
			String prolist = "";
	        String prolistunformatted = "";
	        List<ISoliniaClass> pros = new ArrayList<ISoliniaClass>();
	        List<ISoliniaClass> rawpros = StateManager.getInstance().getConfigurationManager().getClasses();
	        
			if ((
					sender instanceof ConsoleCommandSender
					))
			{
				String tmplist = "";
				for(ISoliniaClass pro : rawpros)
		    	{
					tmplist = prolist + " " + ChatColor.LIGHT_PURPLE + pro.getName().toUpperCase() + ChatColor.RESET + "(A:" + pro.isAdmin() + ")";
		    	}
				sender.sendMessage(tmplist);
				return true;
			}
			
			Player player = (Player) sender;
			
			try {
				ISoliniaPlayer soliniaplayer = SoliniaPlayerAdapter.Adapt(player);
				if (soliniaplayer.hasChosenRace() == false)
		        {
					sendClassInfo(sender);
		        	sender.sendMessage("You cannot pick a profession until you set your race with /setrace"); 
		        	return true;
		        }
		        
		        if (soliniaplayer.hasChosenClass() == true)
		        {
		        	sendClassInfo(sender);
		        	sender.sendMessage("You cannot pick a profession as you have already chosen one");
		        	return true;
		        }
		        
		    	for(ISoliniaClass pro : rawpros)
		    	{
		    		if (!pro.isAdmin() && StateManager.getInstance().getConfigurationManager().isValidRaceClass(soliniaplayer.getRaceId(), pro.getId()))
		    		{
		    			prolist = prolist + " " + ChatColor.LIGHT_PURPLE + pro.getName().toUpperCase() + ChatColor.RESET;
		    			prolistunformatted = prolistunformatted + " " + pro.getName().toUpperCase();
		    			pros.add(pro);
		    		}
		    	}
		    	
		    	if (args.length == 0)
		        {
		    		sendClassInfo(sender);
		    		sender.sendMessage("Insufficient arguments Valid Professions are: ["+prolist+"]");
		        	if (soliniaplayer != null)
		        	{
		        		ISoliniaClass profession = StateManager.getInstance().getConfigurationManager().getClassObj(soliniaplayer.getClassId());
		        		
		        		if (profession != null)
		        			sender.sendMessage("Your current profession is: " + profession.getName());
		        		else
		        			sender.sendMessage("Your current profession is: UNKNOWN");
		        	}
		        	return false;
		        }
		    	
		    	String profession = args[0].toUpperCase();
		        
		        boolean found = false;
		        
		        ISoliniaClass solprofession = null;
		        
		        for(ISoliniaClass allowedprofession : pros)
		    	{
		    		if (allowedprofession.getName().equals(profession))
		    		{
		    			solprofession = allowedprofession;
		    			found = true;
		    		}
		    	}
		        
		        if (found == false)
		        {
		    		sendClassInfo(sender);
		        	return false;
		        }
		        
		        if (!StateManager.getInstance().getConfigurationManager().isValidRaceClass(soliniaplayer.getRaceId(), solprofession.getId()))
		        {
		    		sendClassInfo(sender);
		        	sender.sendMessage("That is not a valid Race / Profession");
		        	return true;
		        }
		        
		        soliniaplayer.setClassId(solprofession.getId());
		        soliniaplayer.setChosenClass(true);
	        	sender.sendMessage("* Profession set to " + profession);
	        	EntityUtils.teleportSafely(player,solprofession.getRaceClass(soliniaplayer.getRaceId()).getStartLocation());
				soliniaplayer.setBindPoint(solprofession.getRaceClass(soliniaplayer.getRaceId()).getStartWorld() + "," + solprofession.getRaceClass(soliniaplayer.getRaceId()).getStartX() + ","
					+ solprofession.getRaceClass(soliniaplayer.getRaceId()).getStartY() + "," + solprofession.getRaceClass(soliniaplayer.getRaceId()).getStartZ());

	            ISoliniaPlayer solplayer = SoliniaPlayerAdapter.Adapt(player);

	            solplayer.updateMaxHp();
	        	return true;
		        
			} catch (CoreStateInitException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		} catch (CoreStateInitException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			sender.sendMessage(e1.getMessage());
			return false;
		}
        
        return true;
	}
	
	public void SendProfessionFull(CommandSender sender, List<ISoliniaClass> allprofessions)
	{
		
	}
	public static void sendClassInfo(CommandSender sender) throws CoreStateInitException {
		List<ISoliniaRace> races = StateManager.getInstance().getConfigurationManager().getRaces();

		for (ISoliniaClass classObj : StateManager.getInstance().getConfigurationManager().getClasses()) {
			if (classObj.isAdmin())
				continue;

			String classBuilder = "";
			for (ISoliniaRace solrace : races) {
				if (classObj.getValidRaceClasses().containsKey(solrace.getId()))
					classBuilder += solrace.getName() + " ";
			}

			TextComponent tc = new TextComponent();
			tc.setText(ChatColor.RED + "~ CLASS: " + ChatColor.GOLD + classObj.getName().toUpperCase() + ChatColor.GRAY
					+ " [" + classObj.getId() + "] - " + ChatColor.RESET);
			TextComponent tc2 = new TextComponent();
			tc2.setText("Hover for more details");
			String details = ChatColor.GOLD + classObj.getName() + ChatColor.RESET + System.lineSeparator() + ChatColor.RESET + System.lineSeparator() + classObj.getDescription() + System.lineSeparator() + 
					 System.lineSeparator() + ChatColor.GOLD + "Races: " + ChatColor.RESET + classBuilder;
			tc2.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(details).create()));
			tc.addExtra(tc2);
			sender.spigot().sendMessage(tc);
		}
	}
}
