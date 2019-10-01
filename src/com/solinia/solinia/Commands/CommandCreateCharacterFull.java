package com.solinia.solinia.Commands;

import java.util.UUID;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.solinia.solinia.Solinia3CorePlugin;
import com.solinia.solinia.Adapters.SoliniaPlayerAdapter;
import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Interfaces.ISoliniaClass;
import com.solinia.solinia.Interfaces.ISoliniaPlayer;
import com.solinia.solinia.Interfaces.ISoliniaRace;
import com.solinia.solinia.Managers.StateManager;
import com.solinia.solinia.Models.Bond;
import com.solinia.solinia.Models.Flaw;
import com.solinia.solinia.Models.Ideal;
import com.solinia.solinia.Models.Trait;
import com.solinia.solinia.Utils.Utils;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

public class CommandCreateCharacterFull implements CommandExecutor {
Solinia3CorePlugin plugin;
	
	public CommandCreateCharacterFull(Solinia3CorePlugin plugin) {
		this.plugin = plugin;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender instanceof Player))
			return false;

		Player player = (Player) sender;
		if (!player.isOp() && !player.hasPermission("solinia.characternewunlimited") && !Utils.canChangeCharacter(player))
		{
			player.sendMessage("You can only change your character every 10 minutes");
			return true;
		}
		
		if (args.length < 10)
		{
			player.sendMessage("Missing arguments");
			return false;
		}
			
		
		int raceid = Integer.parseInt(args[0]);
		int classid = Integer.parseInt(args[1]);
		String gender = args[2];
		int idealid = Integer.parseInt(args[3]);
		int firsttraitid = Integer.parseInt(args[4]);
		int secondtraitid = Integer.parseInt(args[5]);
		int flawid = Integer.parseInt(args[6]);
		int bondid = Integer.parseInt(args[7]);
		String forename = args[8];
		String lastname = args[9];

		try {
			ISoliniaPlayer solPlayer = SoliniaPlayerAdapter.Adapt(player);
			if (createCharacter(solPlayer,raceid, classid, gender, idealid, firsttraitid, secondtraitid, flawid, bondid, forename, lastname))
			{
				player.sendMessage("Character created");
			} else {
				player.sendMessage("Failed to create character");
			}
		} catch (CoreStateInitException e) {
			e.printStackTrace();
		}
		return true;
	}

	private boolean createCharacter(ISoliniaPlayer solPlayer, int raceId, int classId, String gender, int ideal, int firstTrait, int secondTrait, int flaw, int bond, String foreName, String lastName) throws CoreStateInitException {
		Player player = solPlayer.getBukkitPlayer();
		
		ISoliniaRace solRace = StateManager.getInstance().getConfigurationManager().getRace(raceId);
		if (solRace == null)
			return false;
		
		ISoliniaClass solClass = StateManager.getInstance().getConfigurationManager().getClassObj(classId);
		if (solClass == null)
			return false;
		
		if (!StateManager.getInstance().getConfigurationManager().isValidRaceClass(solRace.getId(), solClass.getId()))
        	return false;
		
		if (!gender.equals("MALE") && !gender.equals("FEMALE"))
        	return false;
		
		Ideal ideal1 = StateManager.getInstance().getConfigurationManager().getIdeal(ideal);
		if (ideal1 == null)
			return false;
		
		if (firstTrait == secondTrait)
			return false;
		
		Trait trait1 = StateManager.getInstance().getConfigurationManager().getTrait(firstTrait);
		if (trait1 == null)
			return false;

		Trait trait2 = StateManager.getInstance().getConfigurationManager().getTrait(secondTrait);
		if (trait2 == null)
			return false;
		
		Flaw flaw1 = StateManager.getInstance().getConfigurationManager().getFlaw(flaw);
		if (flaw1 == null)
			return false;
		
		Bond bond1 = StateManager.getInstance().getConfigurationManager().getBond(bond);
		if (bond1 == null)
			return false;
		
		if (!StateManager.getInstance().getPlayerManager().IsNewNameValid(foreName, lastName))
			return false;
		
		ISoliniaPlayer newPlayer = StateManager.getInstance().getPlayerManager().createNewPlayerAlt(plugin, player);
		if (newPlayer == null)
			return false;
		
		newPlayer.setRaceId(solRace.getId());
		newPlayer.setChosenRace(true);
		newPlayer.setClassId(solClass.getId());
		newPlayer.setChosenClass(true);
		
		newPlayer.setGender(gender);
		
		solPlayer.getPersonality().setIdealId(ideal1.id);
		solPlayer.getPersonality().setFirstTraitId(trait1.id);
		solPlayer.getPersonality().setSecondTraitId(trait2.id);
		solPlayer.getPersonality().setFlawId(flaw1.id);
		solPlayer.getPersonality().setBondId(bond1.id);
		
		solPlayer.setForenameAndLastName(foreName, lastName);
		
		newPlayer.updateMaxHp();
		SoliniaPlayerAdapter.Adapt(player).updateDisplayName();

		player.teleport(solRace.getStartLocation());
		newPlayer.setBindPoint(solRace.getStartWorld() + "," + solRace.getStartX() + "," + solRace.getStartY() + "," + solRace.getStartZ());
		
		player.sendMessage("Your character has been stored and a new character created");
		return true;
	}
}
