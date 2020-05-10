package com.solinia.solinia.Commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.solinia.solinia.Adapters.SoliniaPlayerAdapter;
import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Interfaces.ISoliniaClass;
import com.solinia.solinia.Interfaces.ISoliniaPlayer;
import com.solinia.solinia.Utils.Utils;

import net.md_5.bungee.api.ChatColor;

public class CommandSkillCheck implements CommandExecutor {
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		
		if (sender instanceof Player) {
            Player player = (Player) sender;
            
            try
            {
	            ISoliniaPlayer solplayer = SoliniaPlayerAdapter.Adapt(player);
	            
	            if (solplayer == null)
	            {
	            	player.sendMessage("Failed to emote, player does not exist");
	            	return false;	
	            }
	            
	            List<String> skills = new ArrayList<String>();
	            skills.add("athletics");
	            skills.add("acrobatics");
	            skills.add("sleightofhand");
	            skills.add("stealth");
	            skills.add("arcana");
	            skills.add("history");
	            skills.add("investigation");
	            skills.add("nature");
	            skills.add("religion");
	            skills.add("animalhandling");
	            skills.add("insight");
	            skills.add("medicine");
	            skills.add("perception");
	            skills.add("survival");
	            skills.add("deception");
	            skills.add("intimidation");
	            skills.add("performance");
	            skills.add("persuasion");
	            
	            String skill = "perception";
	            
	            if (args.length == 0)
	            {
	            	player.sendMessage("Insufficient arguments, must provide skill from this list: " + String.join(",", skills));
	            	return false;
	            } else {
	            	skill = args[0].toLowerCase();
	            	if (!skills.contains(skill))
	            	{
	            		player.sendMessage("Invalid argument [" + skill + "], must provide skill from this list: " + String.join(", ", skills));
	            		return false;
	            	}
	            }
	            
	            int bonus = getClassRollBonus(skill,solplayer.getClassObj());
	            

	            String message = ChatColor.AQUA + "[ROLL!] * " + solplayer.getFullName() + " makes a skill check for " + skill + ". They roll: " + Utils.RandomBetween(0, 20) + "/20" + ChatColor.RESET;

	            if (bonus != 0)
	            {
	            	int roll = Utils.RandomBetween(0, 20);
	            	player.sendMessage(ChatColor.YELLOW+"[Silternius] Your roll has been assigned an appropriate class modifier ["+bonus+"]!");
	            	int bonusroll = roll+bonus;
	            	message = ChatColor.AQUA + "[ROLL!] * " + solplayer.getFullName() + " makes a skill check for " + skill + ". They roll: " + roll+"+"+bonus+"("+bonusroll+")" + "/20" + ChatColor.RESET;
	            }
	            solplayer.emote(message, false);
            } catch (CoreStateInitException e)
            {
            	player.sendMessage(e.getMessage());
            }
        }

        return true;
	}

	private int getClassRollBonus(String skill, ISoliniaClass classObj) {
		if (classObj == null)
			return 0;
		
		switch (skill)
		{
		case "athletics":
			switch(classObj.getName().toUpperCase())
			{
				case "WARRIOR":
				return 2;
				case "CLERIC":
				return 0;
				case "RANGER":
				return 1;
				case "ROGUE":
				return 1;
				case "WIZARD":
				return -1;
				case "PALADIN":
				return 1;
				case "SHADOWKNIGHT":
				return 1;
				case "SHAMAN":
				return 1;
				case "DRUID":
				return 1;
				case "BARD":
				return 1;
				case "MAGICIAN":
				return -1;
				case "MONK":
				return 2;
				case "NECROMANCER":
				return -1;
				case "ENCHANTER":
				return 0;
				case "BEASTLORD":
				return 2;
				case "BERSERKER":
				return 2;
				default:
					return 0;
			}
		case "acrobatics":
			switch(classObj.getName().toUpperCase())
			{
				case "WARRIOR":
				return -2;
				case "CLERIC":
				return 1;
				case "RANGER":
				return 1;
				case "ROGUE":
				return 2;
				case "WIZARD":
				return 1;
				case "PALADIN":
				return 1;
				case "SHADOWKNIGHT":
				return 1;
				case "SHAMAN":
				return 1;
				case "DRUID":
				return 1;
				case "BARD":
				return 2;
				case "MAGICIAN":
				return 1;
				case "MONK":
				return 2;
				case "NECROMANCER":
				return 1;
				case "ENCHANTER":
				return 1;
				case "BEASTLORD":
				return 1;
				case "BERSERKER":
				return 0;
				default:
					return 0;
			}
		case "sleightofhand":
			switch(classObj.getName().toUpperCase())
			{
				case "WARRIOR":
				return 0;
				case "CLERIC":
				return 0;
				case "RANGER":
				return 0;
				case "ROGUE":
				return 2;
				case "WIZARD":
				return 1;
				case "PALADIN":
				return 0;
				case "SHADOWKNIGHT":
				return 0;
				case "SHAMAN":
				return 0;
				case "DRUID":
				return -1;
				case "BARD":
				return 1;
				case "MAGICIAN":
				return 2;
				case "MONK":
				return 0;
				case "NECROMANCER":
				return 1;
				case "ENCHANTER":
				return 1;
				case "BEASTLORD":
				return 0;
				case "BERSERKER":
				return 0;
				default:
					return 0;
			}
		case "stealth":
			switch(classObj.getName().toUpperCase())
			{
				case "WARRIOR":
				return -1;
				case "CLERIC":
				return 0;
				case "RANGER":
				return 1;
				case "ROGUE":
				return 2;
				case "WIZARD":
				return 1;
				case "PALADIN":
				return -1;
				case "SHADOWKNIGHT":
				return 1;
				case "SHAMAN":
				return 0;
				case "DRUID":
				return 1;
				case "BARD":
				return 1;
				case "MAGICIAN":
				return 0;
				case "MONK":
				return 0;
				case "NECROMANCER":
				return 1;
				case "ENCHANTER":
				return 2;
				case "BEASTLORD":
				return 1;
				case "BERSERKER":
				return -1;
				default:
					return 0;
			}
		case "arcana":
			switch(classObj.getName().toUpperCase())
			{
				case "WARRIOR":
				return -2;
				case "CLERIC":
				return 0;
				case "RANGER":
				return -1;
				case "ROGUE":
				return -2;
				case "WIZARD":
				return 2;
				case "PALADIN":
				return -1;
				case "SHADOWKNIGHT":
				return 1;
				case "SHAMAN":
				return 1;
				case "DRUID":
				return 1;
				case "BARD":
				return -1;
				case "MAGICIAN":
				return 2;
				case "MONK":
				return -2;
				case "NECROMANCER":
				return 2;
				case "ENCHANTER":
				return 2;
				case "BEASTLORD":
				return -1;
				case "BERSERKER":
				return -2;
				default:
					return 0;
			}
		case "history":
			switch(classObj.getName().toUpperCase())
			{
				case "WARRIOR":
				return 1;
				case "CLERIC":
				return 1;
				case "RANGER":
				return -2;
				case "ROGUE":
				return -2;
				case "WIZARD":
				return 2;
				case "PALADIN":
				return 1;
				case "SHADOWKNIGHT":
				return 1;
				case "SHAMAN":
				return 0;
				case "DRUID":
				return 1;
				case "BARD":
				return 2;
				case "MAGICIAN":
				return 0;
				case "MONK":
				return 0;
				case "NECROMANCER":
				return 1;
				case "ENCHANTER":
				return -1;
				case "BEASTLORD":
				return -1;
				case "BERSERKER":
				return 1;
				default:
					return 0;
			}
		case "investigation":
			switch(classObj.getName().toUpperCase())
			{
				case "WARRIOR":
				return 1;
				case "CLERIC":
				return 1;
				case "RANGER":
				return 1;
				case "ROGUE":
				return 0;
				case "WIZARD":
				return 2;
				case "PALADIN":
				return 0;
				case "SHADOWKNIGHT":
				return 0;
				case "SHAMAN":
				return 0;
				case "DRUID":
				return 1;
				case "BARD":
				return 0;
				case "MAGICIAN":
				return 0;
				case "MONK":
				return 0;
				case "NECROMANCER":
				return 1;
				case "ENCHANTER":
				return -1;
				case "BEASTLORD":
				return 2;
				case "BERSERKER":
				return -1;
				default:
					return 0;
			}
		case "nature":
			switch(classObj.getName().toUpperCase())
			{
				case "WARRIOR":
				return 0;
				case "CLERIC":
				return -1;
				case "RANGER":
				return 2;
				case "ROGUE":
				return 0;
				case "WIZARD":
				return -2;
				case "PALADIN":
				return -1;
				case "SHADOWKNIGHT":
				return -2;
				case "SHAMAN":
				return 2;
				case "DRUID":
				return 2;
				case "BARD":
				return -1;
				case "MAGICIAN":
				return -1;
				case "MONK":
				return 0;
				case "NECROMANCER":
				return -2;
				case "ENCHANTER":
				return -1;
				case "BEASTLORD":
				return 2;
				case "BERSERKER":
				return 0;
				default:
					return 0;
			}
		case "religion":
			switch(classObj.getName().toUpperCase())
			{
				case "WARRIOR":
				return 0;
				case "CLERIC":
				return 2;
				case "RANGER":
				return -1;
				case "ROGUE":
				return -2;
				case "WIZARD":
				return -1;
				case "PALADIN":
				return 2;
				case "SHADOWKNIGHT":
				return 1;
				case "SHAMAN":
				return 0;
				case "DRUID":
				return 1;
				case "BARD":
				return -1;
				case "MAGICIAN":
				return -1;
				case "MONK":
				return 2;
				case "NECROMANCER":
				return 0;
				case "ENCHANTER":
				return -1;
				case "BEASTLORD":
				return -1;
				case "BERSERKER":
				return 0;
				default:
					return 0;
			}
		case "animalhandling":
			switch(classObj.getName().toUpperCase())
			{
				case "WARRIOR":
				return 0;
				case "CLERIC":
				return 1;
				case "RANGER":
				return 1;
				case "ROGUE":
				return -1;
				case "WIZARD":
				return -1;
				case "PALADIN":
				return 0;
				case "SHADOWKNIGHT":
				return -1;
				case "SHAMAN":
				return 2;
				case "DRUID":
				return 0;
				case "BARD":
				return 0;
				case "MAGICIAN":
				return 1;
				case "MONK":
				return 0;
				case "NECROMANCER":
				return 0;
				case "ENCHANTER":
				return 1;
				case "BEASTLORD":
				return 2;
				case "BERSERKER":
				return 1;
				default:
					return 0;
			}
		case "insight":
			switch(classObj.getName().toUpperCase())
			{
				case "WARRIOR":
				return 0;
				case "CLERIC":
				return 2;
				case "RANGER":
				return 0;
				case "ROGUE":
				return 0;
				case "WIZARD":
				return 2;
				case "PALADIN":
				return 1;
				case "SHADOWKNIGHT":
				return 0;
				case "SHAMAN":
				return -1;
				case "DRUID":
				return 0;
				case "BARD":
				return -1;
				case "MAGICIAN":
				return 0;
				case "MONK":
				return 0;
				case "NECROMANCER":
				return -1;
				case "ENCHANTER":
				return 1;
				case "BEASTLORD":
				return -1;
				case "BERSERKER":
				return 1;
				default:
					return 0;
			}
		case "medicine":
			switch(classObj.getName().toUpperCase())
			{
				case "WARRIOR":
				return 0;
				case "CLERIC":
				return 2;
				case "RANGER":
				return 1;
				case "ROGUE":
				return -1;
				case "WIZARD":
				return -1;
				case "PALADIN":
				return 1;
				case "SHADOWKNIGHT":
				return -1;
				case "SHAMAN":
				return 2;
				case "DRUID":
				return 1;
				case "BARD":
				return 0;
				case "MAGICIAN":
				return 1;
				case "MONK":
				return 0;
				case "NECROMANCER":
				return -1;
				case "ENCHANTER":
				return -1;
				case "BEASTLORD":
				return 1;
				case "BERSERKER":
				return 1;
				default:
					return 0;
			}
		case "perception":
			switch(classObj.getName().toUpperCase())
			{
				case "WARRIOR":
				return 1;
				case "CLERIC":
				return 0;
				case "RANGER":
				return 2;
				case "ROGUE":
				return 1;
				case "WIZARD":
				return 0;
				case "PALADIN":
				return 1;
				case "SHADOWKNIGHT":
				return 0;
				case "SHAMAN":
				return 1;
				case "DRUID":
				return 1;
				case "BARD":
				return 0;
				case "MAGICIAN":
				return 0;
				case "MONK":
				return 1;
				case "NECROMANCER":
				return 0;
				case "ENCHANTER":
				return 1;
				case "BEASTLORD":
				return 1;
				case "BERSERKER":
				return -1;
				default:
					return 0;
			}
		case "survival":
			switch(classObj.getName().toUpperCase())
			{
				case "WARRIOR":
				return 1;
				case "CLERIC":
				return 0;
				case "RANGER":
				return 2;
				case "ROGUE":
				return 1;
				case "WIZARD":
				return -1;
				case "PALADIN":
				return 0;
				case "SHADOWKNIGHT":
				return -1;
				case "SHAMAN":
				return 1;
				case "DRUID":
				return 1;
				case "BARD":
				return -1;
				case "MAGICIAN":
				return -2;
				case "MONK":
				return 1;
				case "NECROMANCER":
				return -1;
				case "ENCHANTER":
				return -1;
				case "BEASTLORD":
				return 2;
				case "BERSERKER":
				return 1;
				default:
					return 0;
			}
		case "deception":
			switch(classObj.getName().toUpperCase())
			{
				case "WARRIOR":
				return 1;
				case "CLERIC":
				return -2;
				case "RANGER":
				return 0;
				case "ROGUE":
				return 2;
				case "WIZARD":
				return -1;
				case "PALADIN":
				return -2;
				case "SHADOWKNIGHT":
				return 2;
				case "SHAMAN":
				return -1;
				case "DRUID":
				return -2;
				case "BARD":
				return 1;
				case "MAGICIAN":
				return 0;
				case "MONK":
				return -1;
				case "NECROMANCER":
				return 2;
				case "ENCHANTER":
				return 2;
				case "BEASTLORD":
				return -1;
				case "BERSERKER":
				return 1;
				default:
					return 0;
			}
		case "intimidation":
			switch(classObj.getName().toUpperCase())
			{
				case "WARRIOR":
				return 1;
				case "CLERIC":
				return -2;
				case "RANGER":
				return 0;
				case "ROGUE":
				return 1;
				case "WIZARD":
				return 2;
				case "PALADIN":
				return 1;
				case "SHADOWKNIGHT":
				return 2;
				case "SHAMAN":
				return -2;
				case "DRUID":
				return -2;
				case "BARD":
				return -1;
				case "MAGICIAN":
				return 1;
				case "MONK":
				return 0;
				case "NECROMANCER":
				return 2;
				case "ENCHANTER":
				return -1;
				case "BEASTLORD":
				return 0;
				case "BERSERKER":
				return 2;
				default:
					return 0;
			}
		case "performance":
			switch(classObj.getName().toUpperCase())
			{
				case "WARRIOR":
				return 1;
				case "CLERIC":
				return 0;
				case "RANGER":
				return -2;
				case "ROGUE":
				return 1;
				case "WIZARD":
				return 0;
				case "PALADIN":
				return 0;
				case "SHADOWKNIGHT":
				return 0;
				case "SHAMAN":
				return 0;
				case "DRUID":
				return -1;
				case "BARD":
				return 2;
				case "MAGICIAN":
				return 1;
				case "MONK":
				return 1;
				case "NECROMANCER":
				return 0;
				case "ENCHANTER":
				return 0;
				case "BEASTLORD":
				return -2;
				case "BERSERKER":
				return 1;
				default:
					return 0;
			}
		case "persuasion":
			switch(classObj.getName().toUpperCase())
			{
				case "WARRIOR":
				return 1;
				case "CLERIC":
				return 1;
				case "RANGER":
				return 0;
				case "ROGUE":
				return 1;
				case "WIZARD":
				return 1;
				case "PALADIN":
				return 1;
				case "SHADOWKNIGHT":
				return -1;
				case "SHAMAN":
				return -1;
				case "DRUID":
				return -1;
				case "BARD":
				return 1;
				case "MAGICIAN":
				return 0;
				case "MONK":
				return 0;
				case "NECROMANCER":
				return 0;
				case "ENCHANTER":
				return 2;
				case "BEASTLORD":
				return -1;
				case "BERSERKER":
				return 0;
				default:
					return 0;
			}
		default:
			return 0;
		}
	}
}
