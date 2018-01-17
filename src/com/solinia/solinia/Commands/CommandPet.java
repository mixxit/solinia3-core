package com.solinia.solinia.Commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Creature;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Wolf;

import com.solinia.solinia.Adapters.SoliniaLivingEntityAdapter;
import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Interfaces.ISoliniaLivingEntity;
import com.solinia.solinia.Interfaces.ISoliniaNPC;
import com.solinia.solinia.Interfaces.ISoliniaSpell;
import com.solinia.solinia.Managers.StateManager;
import com.solinia.solinia.Models.SoliniaActiveSpell;
import com.solinia.solinia.Models.SoliniaEntitySpells;
import com.solinia.solinia.Models.SoliniaLivingEntity;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.minecraft.server.v1_12_R1.EntityInsentient;
import net.minecraft.server.v1_12_R1.GenericAttributes;

public class CommandPet implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		// TODO Auto-generated method stub
		if (sender instanceof Player)
		{
			try
			{
				Player player = (Player)sender;
				LivingEntity pet = StateManager.getInstance().getEntityManager().getPet(player);
				if (pet == null)
				{
					player.sendMessage("You don't have a pet");
					return true;
				}
				
				if (args.length > 0)
				{
					String petcommand = args[0];
					if (petcommand.equals("back"))
					{
						Wolf c = (Wolf)pet;
						player.setLastDamageCause(null);
						player.sendMessage("* As you wish my master");
						c.setTarget(null);
					}
				}
				
				player.sendMessage("Pet Name: " + pet.getName());
				player.sendMessage("Pet HP: " + pet.getHealth() + "/" + pet.getMaxHealth());
				ISoliniaLivingEntity petLivingEntity = SoliniaLivingEntityAdapter.Adapt(pet);
				EntityInsentient entityhandle = (EntityInsentient) ((org.bukkit.craftbukkit.v1_12_R1.entity.CraftLivingEntity) pet).getHandle();
				double dmg = entityhandle.getAttributeInstance(GenericAttributes.ATTACK_DAMAGE).getValue();
				player.sendMessage("Pet DMG: " + dmg);

				player.sendMessage("STR: " + petLivingEntity.getStrength() + " STA: " + petLivingEntity.getStamina() + " AGI: " + petLivingEntity.getAgility() + " DEX: " + petLivingEntity.getDexterity() + " INT: " + petLivingEntity.getIntelligence() + " WIS: " + petLivingEntity.getWisdom() + " CHA: " + petLivingEntity.getCharisma());
	            player.sendMessage("Pet Armour Class Mitigation : " + petLivingEntity.getMitigationAC());
	            player.sendMessage("Pet Attack Value: " + petLivingEntity.getAttk());
	            player.sendMessage("Pet Total Rune of: " + petLivingEntity.getRune());
				
				player.sendMessage("Active Effects:");
				SoliniaEntitySpells spells = StateManager.getInstance().getEntityManager().getActiveEntitySpells(pet);
				for(SoliniaActiveSpell activeSpell : spells.getActiveSpells())
	            {
	            	ISoliniaSpell spell = StateManager.getInstance().getConfigurationManager().getSpell(activeSpell.getSpellId());
	            	String removetext = "";
	            	ChatColor spellcolor = ChatColor.GREEN;
	            	if (spell.isBeneficial())
	            	{
	            		removetext = "Removable spell";
	            	} else {
	            		removetext = "Unremovable spell";
	            		spellcolor = ChatColor.RED;
	            	}
	            	
	            	TextComponent tc = new TextComponent();
					tc.setText("- " + spellcolor + spell.getName() + ChatColor.RESET + " " + activeSpell.getTicksLeft() + " ticks left - ");
					TextComponent tc2 = new TextComponent();
					tc2.setText(removetext);
					tc.addExtra(tc2);
					sender.spigot().sendMessage(tc);	
	            	
	            }
				
				return true;
			} catch (CoreStateInitException e)
			{
				
			}
		}
		return true;
	}

}
