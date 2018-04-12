package com.solinia.solinia.Commands;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Creature;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Wolf;
import org.bukkit.inventory.ItemStack;

import com.solinia.solinia.Adapters.ItemStackAdapter;
import com.solinia.solinia.Adapters.SoliniaItemAdapter;
import com.solinia.solinia.Adapters.SoliniaLivingEntityAdapter;
import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Exceptions.SoliniaItemException;
import com.solinia.solinia.Interfaces.ISoliniaItem;
import com.solinia.solinia.Interfaces.ISoliniaLivingEntity;
import com.solinia.solinia.Interfaces.ISoliniaNPC;
import com.solinia.solinia.Interfaces.ISoliniaSpell;
import com.solinia.solinia.Managers.StateManager;
import com.solinia.solinia.Models.SoliniaActiveSpell;
import com.solinia.solinia.Models.SoliniaEntitySpells;
import com.solinia.solinia.Models.SoliniaLivingEntity;
import com.solinia.solinia.Utils.Utils;

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
					
					if (petcommand.equals("equip"))
					{
						PetEquip(player,pet);
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
				
				player.sendMessage("Pet subcommands: /pet back | /pet equip");
				return true;
			} catch (CoreStateInitException e)
			{
				
			}
		}
		return true;
	}

	private void PetEquip(Player player, LivingEntity pet) {
		ItemStack itemStack = player.getInventory().getItemInMainHand();
		if (itemStack == null)
		{
			player.sendMessage("Cannot give a pet nothing (empty main hand)");
			return;
		}
		
		if (itemStack.getType().equals(Material.AIR))
		{
			player.sendMessage("Cannot give a pet nothing (empty main hand)");
			return;
		}
		
		if (!Utils.IsSoliniaItem(itemStack))
		{
			player.sendMessage("Can only give pets Solinia Items (invalid item type)");
			return;
		}
		
		try {
			ISoliniaItem item = SoliniaItemAdapter.Adapt(itemStack);
			
			if (!item.isWeaponOrBowOrShield() && !item.isArmour() && !item.isJewelry())
			{
				player.sendMessage("Can only give pets wearable items");
				return;
			}
			
			if (item.isWeaponOrBowOrShield())
			{
				if (!item.getBasename().toUpperCase().contains("SHIELD"))
				{
					if (pet.getEquipment().getItemInMainHand() == null)
					{
						pet.getEquipment().setItemInMainHand(item.asItemStack());
						player.sendMessage("Equipped in main hand");
						return;
					}
	
					if (pet.getEquipment().getItemInMainHand().getType().equals(Material.AIR))
					{
						pet.getEquipment().setItemInMainHand(item.asItemStack());
						player.sendMessage("Equipped in main hand");
						return;
					}
				}
				
				if (pet.getEquipment().getItemInOffHand() == null)
				{
					pet.getEquipment().setItemInOffHand(item.asItemStack());
					player.sendMessage("Equipped in secondary hand");
					return;
				}

				if (pet.getEquipment().getItemInOffHand().getType().equals(Material.AIR))
				{
					pet.getEquipment().setItemInOffHand(item.asItemStack());
					player.sendMessage("Equipped in secondary hand");
					return;
				}

				player.sendMessage("No space to equip weapon");
				return;
			}
			
			if (item.isArmour())
			{
				if (item.getBasename().toUpperCase().contains("HELMET"))
				{
					if (pet.getEquipment().getHelmet() == null)
					{
						pet.getEquipment().setHelmet(item.asItemStack());
						player.sendMessage("Equipped in helmet");
						return;
					}

					if (pet.getEquipment().getHelmet().getType().equals(Material.AIR))
					{
						pet.getEquipment().setHelmet(item.asItemStack());
						player.sendMessage("Equipped in helmet");
						return;
					}
					
					player.sendMessage("No space to equip armour");
				}
				
				if (item.getBasename().toUpperCase().contains("CHESTPLATE"))
				{
					if (pet.getEquipment().getChestplate() == null)
					{
						pet.getEquipment().setChestplate(item.asItemStack());
						player.sendMessage("Equipped in chestplate");
						return;
					}

					if (pet.getEquipment().getChestplate().getType().equals(Material.AIR))
					{
						pet.getEquipment().setChestplate(item.asItemStack());
						player.sendMessage("Equipped in chestplate");
						return;
					}
					
					player.sendMessage("No space to equip armour");
				}
				
				if (item.getBasename().toUpperCase().contains("LEGGINGS"))
				{
					if (pet.getEquipment().getLeggings() == null)
					{
						pet.getEquipment().setLeggings(item.asItemStack());
						player.sendMessage("Equipped in leggings");
						return;
					}

					if (pet.getEquipment().getLeggings().getType().equals(Material.AIR))
					{
						pet.getEquipment().setLeggings(item.asItemStack());
						player.sendMessage("Equipped in leggings");
						return;
					}
					
					player.sendMessage("No space to equip armour");
				}
				
				if (item.getBasename().toUpperCase().contains("BOOTS"))
				{
					if (pet.getEquipment().getBoots() == null)
					{
						pet.getEquipment().setBoots(item.asItemStack());
						player.sendMessage("Equipped in boots");
						return;
					}

					if (pet.getEquipment().getBoots().getType().equals(Material.AIR))
					{
						pet.getEquipment().setBoots(item.asItemStack());
						player.sendMessage("Equipped in boots");
						return;
					}
					
					player.sendMessage("No space to equip armour");
				}
			}

			player.sendMessage("Could not equip");
				
		} catch (SoliniaItemException e) {
			
		} catch (CoreStateInitException e) {
		}

	}

}
