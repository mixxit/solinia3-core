package com.solinia.solinia.Commands;

import java.sql.Timestamp;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Sittable;
import org.bukkit.inventory.ItemStack;

import com.solinia.solinia.Adapters.SoliniaItemAdapter;
import com.solinia.solinia.Adapters.SoliniaLivingEntityAdapter;
import com.solinia.solinia.Adapters.SoliniaPlayerAdapter;
import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Exceptions.SoliniaItemException;
import com.solinia.solinia.Interfaces.ISoliniaItem;
import com.solinia.solinia.Interfaces.ISoliniaLivingEntity;
import com.solinia.solinia.Interfaces.ISoliniaNPC;
import com.solinia.solinia.Interfaces.ISoliniaPlayer;
import com.solinia.solinia.Interfaces.ISoliniaSpell;
import com.solinia.solinia.Managers.StateManager;
import com.solinia.solinia.Models.HINT;
import com.solinia.solinia.Models.SkillType;
import com.solinia.solinia.Models.SoliniaActiveSpell;
import com.solinia.solinia.Models.SoliniaEntitySpells;
import com.solinia.solinia.Utils.ChatUtils;
import com.solinia.solinia.Utils.EntityUtils;
import com.solinia.solinia.Utils.ItemStackUtils;
import com.solinia.solinia.Utils.Utils;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
import net.minecraft.server.v1_14_R1.EntityInsentient;
import net.minecraft.server.v1_14_R1.GenericAttributes;

public class CommandPet implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		// TODO Auto-generated method stub
		if (sender instanceof Player)
		{
			try
			{
				Player player = (Player)sender;
				LivingEntity pet = StateManager.getInstance().getEntityManager().getPet(player.getUniqueId());
				
				if (pet == null)
				{
					player.sendMessage("You don't have a pet");
					return true;
				}
				
				if (pet instanceof Sittable)
				{
					Sittable sittable = (Sittable)pet;
					if (sittable.isSitting())
					{
						player.sendMessage("You cannot control a pet which is sitting");
						return true;
					}
				}
				
				ISoliniaLivingEntity petLivingEntity = SoliniaLivingEntityAdapter.Adapt(pet);

				ISoliniaPlayer solPlayer = SoliniaPlayerAdapter.Adapt(player);
				if (petLivingEntity == null || solPlayer == null)
					return false;
				
				
				if (args.length > 0)
				{
					String petcommand = args[0];
					if (petcommand.equals("leave"))
					{
						if (petLivingEntity != null)
						{
							petLivingEntity.clearHateList();
							if (petLivingEntity.IsCorePet())
							{
								solPlayer.killAllPets();
							}
							else
							{
								StateManager.getInstance().getEntityManager().removePet(player.getUniqueId(),
										!petLivingEntity.isCharmed());
								petLivingEntity.clearHateList();
							}
							
							player.setLastDamageCause(null);
						}
					}
					
					if (petcommand.equals("back"))
					{
						if (petLivingEntity != null)
						{
							ISoliniaNPC npc = StateManager.getInstance().getConfigurationManager().getNPC(petLivingEntity.getNpcid());
							if (npc != null)
							if (npc.isPetControllable() == false)
							{
								player.sendMessage("This pet is not controllable");
								return true;
							}
							
							player.setLastDamageCause(null);
							petLivingEntity.clearHateList();
							ChatUtils.SendHint(player,HINT.PET_BACKINGOFFTGT,"",false);

							EntityUtils.teleportSafely(pet,player.getLocation());
						}
						
					}
					
					if (petcommand.equals("attack"))
					{
						if (solPlayer.getEntityTarget() == null) {
							ChatUtils.SendHint(player, HINT.NEED_TARGET, "", false);
						}
						
						LivingEntity targetentity = solPlayer.getEntityTarget();
						if (targetentity != null && !targetentity.getUniqueId().equals(player.getUniqueId())) {
							if (petLivingEntity != null)
							{
								ISoliniaNPC npc = StateManager.getInstance().getConfigurationManager().getNPC(petLivingEntity.getNpcid());
								if (npc != null)
								if (npc.isPetControllable() == false)
								{
									player.sendMessage("This pet is not controllable");
									return true;
								}
							}
							
							EntityUtils.teleportSafely(pet,player.getLocation());
							petLivingEntity.setAttackTarget(null);

							// Mez cancel target
							Timestamp mezExpiry = StateManager.getInstance().getEntityManager().getMezzed(targetentity);
			
							if (mezExpiry != null) {
								petLivingEntity.setAttackTarget(null);
								player.sendMessage("You cannot send your pet to attack a mezzed target");
								return false;
							}
							
							if (pet.getUniqueId().equals(targetentity.getUniqueId()))
							{
								petLivingEntity.setAttackTarget(null);
								player.sendMessage("You cannot send your pet to attack itself");
								return false;
							}

							if (petLivingEntity.getOwnerEntity().getUniqueId().equals(targetentity.getUniqueId()))
							{
								petLivingEntity.setAttackTarget(null);
								player.sendMessage("You cannot send your pet to attack you!");
								return false;
							}
							
							ISoliniaPlayer tmpPlayer = SoliniaPlayerAdapter.Adapt(player);
							if (tmpPlayer != null && tmpPlayer.isInGroup(targetentity))
							{
								petLivingEntity.setAttackTarget(null);
								player.sendMessage("You cannot send your pet to attack your group!");
								return false;
							}
							
							if (!pet.getUniqueId().equals(targetentity.getUniqueId()))
							{
								petLivingEntity.setAttackTarget(targetentity);
								player.sendMessage("You send your pet to attack!");
								return true;
							}
							
							ChatUtils.SendHint(player,HINT.PET_ATTACKINGTGT,targetentity.getCustomName(),false);
							
						}
						
						
						return true;
					}
					
					if (petcommand.equals("equip"))
					{
						if (petLivingEntity != null)
						{
							ISoliniaNPC npc = StateManager.getInstance().getConfigurationManager().getNPC(petLivingEntity.getNpcid());
							if (npc != null)
							if (npc.isPetControllable() == false)
							{
								player.sendMessage("This pet is not controllable/equippable");
								return true;
							}
						}
						
						PetEquip(player,pet);
					}
					
					if (petcommand.equals("hatelist"))
					{
						player.sendMessage("Hate List: ");
						if (petLivingEntity != null)
						{
							petLivingEntity.sendHateList(player);							
						}
					}
				}
				
				player.sendMessage("Pet Name: " + pet.getName() + " Pet Level: " + petLivingEntity.getMentorLevel());
				player.sendMessage("Pet HP: " + pet.getHealth() + "/" + pet.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue());
				EntityInsentient entityhandle = (EntityInsentient) ((org.bukkit.craftbukkit.v1_14_R1.entity.CraftLivingEntity) pet).getHandle();
				double dmg = entityhandle.getAttributeInstance(GenericAttributes.ATTACK_DAMAGE).getValue();
				player.sendMessage("Pet DMG: " + dmg + " (Hand to Hand)");
				Entity target = ((Creature)pet).getTarget();
				if (target == null)
				{
					player.sendMessage("Pet Target: None");
				} else {
					player.sendMessage("Pet Target: " + target.getCustomName() + "/" + target.getName());
				}

				player.sendMessage("STR: " + petLivingEntity.getStrength() + " STA: " + petLivingEntity.getStamina() + " AGI: " + petLivingEntity.getAgility() + " DEX: " + petLivingEntity.getDexterity() + " INT: " + petLivingEntity.getIntelligence() + " WIS: " + petLivingEntity.getWisdom() + " CHA: " + petLivingEntity.getCharisma());
	            player.sendMessage("Pet Armour Class Mitigation (AC): " + petLivingEntity.getMitigationAC());
	            player.sendMessage("Pet Attack Value: " + petLivingEntity.getTotalAtk());
	            player.sendMessage("Pet Attack Speed: " + ChatColor.GOLD+ petLivingEntity.getHaste() + "%" + ChatColor.RESET);
	            player.sendMessage("Pet MainWeapon Attack Rate (Seconds): " + ChatColor.GOLD+ petLivingEntity.getAutoAttackTimerFrequencySeconds() + ChatColor.RESET);
	            player.sendMessage("Pet Total Rune of: " + petLivingEntity.getRune());
				
	            player.sendMessage("Skills:");
	            if (petLivingEntity.getClassObj() != null)
	            {
	            	if (petLivingEntity.getClassObj().getDodgelevel() > 0)
		            	if (petLivingEntity.getMentorLevel() >= petLivingEntity.getClassObj().getDodgelevel())
		            		player.sendMessage(ChatColor.GRAY + "Dodge Skill: " + petLivingEntity.getSkill(SkillType.Dodge));	            	
	            	if (petLivingEntity.getClassObj().getRipostelevel() > 0)
		            	if (petLivingEntity.getMentorLevel() >= petLivingEntity.getClassObj().getRipostelevel())
		            		player.sendMessage(ChatColor.GRAY + "Riposte Skill: " + petLivingEntity.getSkill(SkillType.Riposte));	            	
	            	if (petLivingEntity.getClassObj().getDoubleattacklevel() > 0)
	            		if (petLivingEntity.getMentorLevel() >= petLivingEntity.getClassObj().getDoubleattacklevel())
	            			player.sendMessage(ChatColor.GRAY + "Double Attack Skill: " + petLivingEntity.getSkill(SkillType.DoubleAttack));	            	
	            	if (petLivingEntity.getClassObj().getSafefalllevel() > 0)
	            		if (petLivingEntity.getMentorLevel() >= petLivingEntity.getClassObj().getSafefalllevel())
	            			player.sendMessage(ChatColor.GRAY + "Safefall Skill: " + petLivingEntity.getSkill(SkillType.SafeFall));	            	
	            	if (petLivingEntity.getClassObj().getDualwieldlevel() > 0)
	            		if (petLivingEntity.getMentorLevel() >= petLivingEntity.getClassObj().getDualwieldlevel())
	            			player.sendMessage(ChatColor.GRAY + "Dual Wield: " + petLivingEntity.getSkill(SkillType.DualWield));	            	
	            }
	            
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
				
				player.sendMessage("Pet subcommands: /pet back | /pet equip | /pet attack | /pet leave");
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
		
		if (itemStack.getType() == null || itemStack.getType().equals(Material.AIR))
		{
			player.sendMessage("Cannot give a pet nothing (empty main hand)");
			return;
		}
		
		if (!ItemStackUtils.IsSoliniaItem(itemStack))
		{
			player.sendMessage("Can only give pets Solinia Items (invalid item type)");
			return;
		}
		
		try {
			ISoliniaItem item = SoliniaItemAdapter.Adapt(itemStack);
			ISoliniaLivingEntity livingEntity = SoliniaLivingEntityAdapter.Adapt(pet);
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
						player.getInventory().setItemInMainHand(null);
						player.updateInventory();
						livingEntity.updateMaxHp();
						player.sendMessage("Equipped in main hand");
						return;
					}
	
					if (pet.getEquipment().getItemInMainHand().getType() == null || pet.getEquipment().getItemInMainHand().getType().equals(Material.AIR))
					{
						pet.getEquipment().setItemInMainHand(item.asItemStack());
						player.getInventory().setItemInMainHand(null);
						player.updateInventory();
						livingEntity.updateMaxHp();
						player.sendMessage("Equipped in main hand");
						return;
					}
				}
				
				if (pet.getEquipment().getItemInOffHand() == null)
				{
					pet.getEquipment().setItemInOffHand(item.asItemStack());
					player.getInventory().setItemInMainHand(null);
					player.updateInventory();
					livingEntity.updateMaxHp();
					player.sendMessage("Equipped in secondary hand");
					return;
				}

				if (pet.getEquipment().getItemInOffHand().getType() == null || pet.getEquipment().getItemInOffHand().getType().equals(Material.AIR))
				{
					pet.getEquipment().setItemInOffHand(item.asItemStack());
					player.getInventory().setItemInMainHand(null);
					player.updateInventory();
					livingEntity.updateMaxHp();
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
						player.getInventory().setItemInMainHand(null);
						player.updateInventory();
						livingEntity.updateMaxHp();
						player.sendMessage("Equipped in helmet");
						return;
					}

					if (pet.getEquipment().getHelmet().getType() == null || pet.getEquipment().getHelmet().getType().equals(Material.AIR))
					{
						pet.getEquipment().setHelmet(item.asItemStack());
						player.getInventory().setItemInMainHand(null);
						player.updateInventory();
						livingEntity.updateMaxHp();
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
						player.getInventory().setItemInMainHand(null);
						player.updateInventory();
						livingEntity.updateMaxHp();
						player.sendMessage("Equipped in chestplate");
						return;
					}

					if (pet.getEquipment().getChestplate().getType() == null || pet.getEquipment().getChestplate().getType().equals(Material.AIR))
					{
						pet.getEquipment().setChestplate(item.asItemStack());
						player.getInventory().setItemInMainHand(null);
						player.updateInventory();
						livingEntity.updateMaxHp();
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
						player.getInventory().setItemInMainHand(null);
						player.updateInventory();
						livingEntity.updateMaxHp();
						player.sendMessage("Equipped in leggings");
						return;
					}

					if (pet.getEquipment().getLeggings().getType() == null || pet.getEquipment().getLeggings().getType().equals(Material.AIR))
					{
						pet.getEquipment().setLeggings(item.asItemStack());
						player.getInventory().setItemInMainHand(null);
						player.updateInventory();
						livingEntity.updateMaxHp();
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
						player.getInventory().setItemInMainHand(null);
						player.updateInventory();
						livingEntity.updateMaxHp();
						player.sendMessage("Equipped in boots");
						return;
					}

					if (pet.getEquipment().getBoots().getType() == null || pet.getEquipment().getBoots().getType().equals(Material.AIR))
					{
						pet.getEquipment().setBoots(item.asItemStack());
						player.getInventory().setItemInMainHand(null);
						player.updateInventory();
						livingEntity.updateMaxHp();
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
