package com.solinia.solinia.Commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Managers.StateManager;

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
				
				player.sendMessage("Pet Name: " + pet.getName());
				player.sendMessage("Pet HP: " + pet.getHealth() + "/" + pet.getMaxHealth());
				EntityInsentient entityhandle = (EntityInsentient) ((org.bukkit.craftbukkit.v1_12_R1.entity.CraftLivingEntity) pet).getHandle();
				double dmg = entityhandle.getAttributeInstance(GenericAttributes.ATTACK_DAMAGE).getValue();
				player.sendMessage("Pet DMG: " + dmg);
				return true;
			} catch (CoreStateInitException e)
			{
				
			}
		}
		return true;
	}

}
