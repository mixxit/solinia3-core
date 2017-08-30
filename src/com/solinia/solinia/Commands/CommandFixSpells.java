package com.solinia.solinia.Commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Factories.SoliniaNPCFactory;
import com.solinia.solinia.Interfaces.ISoliniaSpell;
import com.solinia.solinia.Managers.StateManager;
import com.solinia.solinia.Models.SoliniaSpellClass;

public class CommandFixSpells implements CommandExecutor {
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		
		if (!(sender instanceof Player) && !(sender instanceof ConsoleCommandSender))
			return false;
		
		if (sender instanceof Player)
		{
			Player player = (Player) sender;
			if (!player.isOp())
			{
				player.sendMessage("This is an operator only command");
				return false;
			}
		}
		
		try {
			for(ISoliniaSpell spell : StateManager.getInstance().getConfigurationManager().getSpells())
			{
				List<SoliniaSpellClass> allowedClasses = new ArrayList<SoliniaSpellClass>();
				if (spell.getClasses1() > 0 && spell.getClasses1() < 140)
					allowedClasses.add(new SoliniaSpellClass("WARRIOR",spell.getClasses1()));
				if (spell.getClasses2() > 0 && spell.getClasses2() < 140)
					allowedClasses.add(new SoliniaSpellClass("CLERIC",spell.getClasses2()));
				if (spell.getClasses3() > 0 && spell.getClasses3() < 140)
					allowedClasses.add(new SoliniaSpellClass("PALADIN",spell.getClasses3()));
				if (spell.getClasses4() > 0 && spell.getClasses4() < 140)
					allowedClasses.add(new SoliniaSpellClass("RANGER",spell.getClasses4()));
				if (spell.getClasses5() > 0 && spell.getClasses5() < 140)
					allowedClasses.add(new SoliniaSpellClass("SHADOWKNIGHT",spell.getClasses5()));
				if (spell.getClasses6() > 0 && spell.getClasses6() < 140)
					allowedClasses.add(new SoliniaSpellClass("DRUID",spell.getClasses6()));
				if (spell.getClasses7() > 0 && spell.getClasses7() < 140)
					allowedClasses.add(new SoliniaSpellClass("MONK",spell.getClasses7()));
				if (spell.getClasses8() > 0 && spell.getClasses8() < 140)
					allowedClasses.add(new SoliniaSpellClass("BARD",spell.getClasses8()));
				if (spell.getClasses9() > 0 && spell.getClasses9() < 140)
					allowedClasses.add(new SoliniaSpellClass("ROGUE",spell.getClasses9()));
				if (spell.getClasses10() > 0 && spell.getClasses10() < 140)
					allowedClasses.add(new SoliniaSpellClass("SHAMAN",spell.getClasses10()));
				if (spell.getClasses11() > 0 && spell.getClasses11() < 140)
					allowedClasses.add(new SoliniaSpellClass("NECROMANCER",spell.getClasses11()));
				if (spell.getClasses12() > 0 && spell.getClasses12() < 140)
					allowedClasses.add(new SoliniaSpellClass("WIZARD",spell.getClasses12()));
				if (spell.getClasses13() > 0 && spell.getClasses13() < 140)
					allowedClasses.add(new SoliniaSpellClass("MAGICIAN",spell.getClasses13()));
				if (spell.getClasses14() > 0 && spell.getClasses14() < 140)
					allowedClasses.add(new SoliniaSpellClass("ENCHANTER",spell.getClasses14()));
				if (spell.getClasses15() > 0 && spell.getClasses15() < 140)
					allowedClasses.add(new SoliniaSpellClass("BEASTLORD",spell.getClasses15()));
				if (spell.getClasses16() > 0 && spell.getClasses16() < 140)
					allowedClasses.add(new SoliniaSpellClass("BERSERKER",spell.getClasses16()));
				
				spell.setAllowedClasses(allowedClasses);
			}
		} catch (CoreStateInitException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return true;
	}
}
