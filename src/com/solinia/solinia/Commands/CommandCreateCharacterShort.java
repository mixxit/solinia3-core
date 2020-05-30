package com.solinia.solinia.Commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.solinia.solinia.Solinia3CorePlugin;
import com.solinia.solinia.Adapters.SoliniaPlayerAdapter;
import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Factories.SoliniaPlayerFactory;
import com.solinia.solinia.Interfaces.ISoliniaClass;
import com.solinia.solinia.Interfaces.ISoliniaPlayer;
import com.solinia.solinia.Interfaces.ISoliniaRace;
import com.solinia.solinia.Managers.StateManager;
import com.solinia.solinia.Utils.EntityUtils;
import com.solinia.solinia.Utils.PlayerUtils;

public class CommandCreateCharacterShort implements CommandExecutor {
Solinia3CorePlugin plugin;
	
	public CommandCreateCharacterShort(Solinia3CorePlugin plugin) {
		this.plugin = plugin;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender instanceof Player))
			return false;

		Player player = (Player) sender;
		if (!player.isOp() && !player.hasPermission("solinia.characternewunlimited") && !PlayerUtils.canChangeCharacter(player))
		{
			player.sendMessage("You can only change your character every 10 minutes");
			return true;
		}
		
		if (args.length < 2)
		{
			player.sendMessage("Missing arguments");
			return false;
		}
			
		
		int raceid = Integer.parseInt(args[0]);
		int classid = Integer.parseInt(args[1]);

		try {
			ISoliniaPlayer solPlayer = SoliniaPlayerAdapter.Adapt(player);
			createCharacter(solPlayer,raceid, classid);
			player.sendMessage("Character created");
		} catch (CoreStateInitException e) {
			e.printStackTrace();
		} catch (Exception e) {
			player.sendMessage(e.getMessage());
			player.sendMessage("Failed to create character");
		}
		return true;
	}

	private void createCharacter(ISoliniaPlayer solPlayer, int raceId, int classId) throws Exception {
		Player player = solPlayer.getBukkitPlayer();
		
		ISoliniaRace solRace = StateManager.getInstance().getConfigurationManager().getRace(raceId);
		if (solRace == null)
			throw new Exception("Invalid race");
		
		ISoliniaClass solClass = StateManager.getInstance().getConfigurationManager().getClassObj(classId);
		if (solClass == null)
			throw new Exception("Invalid class");
		
		if (!StateManager.getInstance().getConfigurationManager().isValidRaceClass(solRace.getId(), solClass.getId()))
			throw new Exception("Invalid race/class combination");

		String foreName = SoliniaPlayerFactory.getRandomNames(5, 1)[0];
		String lastName = SoliniaPlayerFactory.getRandomNames(5, 1)[0];
		try {
			while (StateManager.getInstance().getPlayerManager().IsNewNameValid(foreName, lastName) == false) {
				foreName = SoliniaPlayerFactory.getRandomNames(5, 1)[0];
				lastName = SoliniaPlayerFactory.getRandomNames(5, 1)[0];
			}
		} catch (CoreStateInitException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if (!StateManager.getInstance().getPlayerManager().IsNewNameValid(foreName, lastName))
			throw new Exception("Invalid firstname+lastname");
		
		ISoliniaPlayer newPlayer = StateManager.getInstance().getPlayerManager().createNewPlayerAlt(plugin, player, true);
		if (newPlayer == null)
			throw new Exception("Failed to create character object");
		
		newPlayer.setRaceId(solRace.getId());
		newPlayer.setChosenRace(true);
		newPlayer.setClassId(solClass.getId());
		newPlayer.setChosenClass(true);
		
		newPlayer.setGender("MALE");
		
		newPlayer.setForenameAndLastName(foreName, lastName);
		
		newPlayer.updateMaxHp();
		SoliniaPlayerAdapter.Adapt(player).updateDisplayName();

		EntityUtils.teleportSafely(player,solClass.getRaceClass(solRace.getId()).getStartLocation());
		newPlayer.setBindPoint(solClass.getRaceClass(solRace.getId()).getStartWorld() + "," + solClass.getRaceClass(solRace.getId()).getStartX() + "," + solClass.getRaceClass(solRace.getId()).getStartY() + "," + solClass.getRaceClass(solRace.getId()).getStartZ());
		
		player.sendMessage("Your character has been stored and a new character created");
	}
}
