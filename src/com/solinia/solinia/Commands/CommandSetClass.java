package com.solinia.solinia.Commands;

import java.util.ArrayList;
import java.util.Arrays;
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
import com.solinia.solinia.Managers.StateManager;

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
					SendProfessionFull(sender,rawpros);
		        	sender.sendMessage("You cannot pick a profession until you set your race with /setrace"); 
		        	return true;
		        }
		        
		        if (soliniaplayer.hasChosenClass() == true)
		        {
		        	SendProfessionFull(sender,rawpros);
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
		    		SendProfessionList(sender,rawpros,prolistunformatted);
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
		        	SendProfessionList(sender,rawpros,prolistunformatted);
		        	return false;
		        }
		        
		        if (!StateManager.getInstance().getConfigurationManager().isValidRaceClass(soliniaplayer.getRaceId(), solprofession.getId()))
		        {
		        	SendProfessionList(sender,rawpros,prolistunformatted);
		        	sender.sendMessage("That is not a valid Race / Profession");
		        	return true;
		        }
		        
		        soliniaplayer.setClassId(solprofession.getId());
		        soliniaplayer.setChosenClass(true);
	        	sender.sendMessage("* Profession set to " + profession);
				player.teleport(solprofession.getRaceClass(soliniaplayer.getRaceId()).getStartLocation());
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
		for(ISoliniaClass pro : allprofessions)
    	{
   			sender.sendMessage(ChatColor.AQUA + pro.getName() + ChatColor.RESET + " - " + ChatColor.GRAY + pro.getDescription());
    	}	
	}
	
	public void SendProfessionList(CommandSender sender, List<ISoliniaClass> allprofessions,String allowedpronames_spacedelim)
	{
		List<String> allowedprofessions = (Arrays.asList(allowedpronames_spacedelim.split(" ")));
		for(ISoliniaClass pro : allprofessions)
    	{
    		if (allowedprofessions.contains(pro.getName()))
    		{
    			sender.sendMessage(ChatColor.AQUA + pro.getName() + ChatColor.RESET + " - " + ChatColor.GRAY + pro.getDescription());
    		}
    	}	
	}
}
