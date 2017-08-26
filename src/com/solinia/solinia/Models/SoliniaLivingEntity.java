package com.solinia.solinia.Models;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Wolf;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityDamageEvent.DamageModifier;

import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Exceptions.SoliniaItemException;
import com.solinia.solinia.Factories.SoliniaItemFactory;
import com.solinia.solinia.Interfaces.ISoliniaItem;
import com.solinia.solinia.Interfaces.ISoliniaLivingEntity;
import com.solinia.solinia.Interfaces.ISoliniaPlayer;
import com.solinia.solinia.Managers.StateManager;
import com.solinia.solinia.Utils.Utils;

public class SoliniaLivingEntity implements ISoliniaLivingEntity {
	LivingEntity livingentity;
	private int level = 1;

	public SoliniaLivingEntity(LivingEntity livingentity) {
		this.livingentity = livingentity;
	}

	@Override
	public LivingEntity getBukkitLivingEntity() {
		// TODO Auto-generated method stub
		return this.livingentity;
	}
	
	@Override
	public void modifyDamageEvent(ISoliniaPlayer player, EntityDamageByEntityEvent event) {
		Player attacker = null;
		if (event.getDamager() instanceof Player)
		{
			attacker = (Player)event.getDamager();
		}
		
		// Change attacker to archer
		if (event.getDamager() instanceof Arrow)
		{
			Arrow arr = (Arrow)event.getDamager();
			if (arr.getShooter() instanceof Player)
			{
				attacker = (Player)arr.getShooter();
			} else {
			}
		} 
		
		if (attacker == null)
			return;
		
		if (getBukkitLivingEntity() instanceof Wolf)
		{
			if (isPet())
			{
				event.setCancelled(true);
				return;
			}
		}
		
		if (attacker.getInventory().getItemInMainHand().getEnchantmentLevel(Enchantment.OXYGEN) > 999)
	    {
			try
			{
				ISoliniaItem soliniaitem = StateManager.getInstance().getConfigurationManager().getItem(attacker.getInventory().getItemInMainHand());
				if (soliniaitem != null)
				{
					if (soliniaitem.getAllowedClassNames().size() > 0)
					{
										if (player.getClassObj() == null)
						{
							System.out.print("Player class was null");
							event.setCancelled(true);
							player.getBukkitPlayer().updateInventory();
							player.getBukkitPlayer().sendMessage(ChatColor.GRAY + "Your class cannot use this item");
			    			return;
						}
							    		
			    		if (!soliniaitem.getAllowedClassNames().contains(player.getClassObj().getName()))
			    		{
			    			event.setCancelled(true);
			    			player.getBukkitPlayer().updateInventory();
			    			player.getBukkitPlayer().sendMessage(ChatColor.GRAY + "Your class cannot use this item");
			    			return;
			    		}
					}
				}
			} catch (Exception e)
			{
				e.printStackTrace();
			}
	    }

		double currentdamage = event.getDamage(EntityDamageEvent.DamageModifier.BASE);
		if (currentdamage < 1)
		{
			currentdamage++;
		}
		
		if (event.getDamager() instanceof Arrow)
		{
			Arrow arr = (Arrow)event.getDamager();
			if (arr.getShooter() instanceof Player)
			{
				// Apply archery modifier
				SoliniaPlayerSkill skill = player.getSkill("ARCHERY");
				double racestatbonus = player.getDexterity() + skill.getValue();
				double bonus = racestatbonus / 100;
				double damagemlt = currentdamage * bonus;
				double newdmg = damagemlt;
				double damagepct = newdmg / event.getDamage(EntityDamageEvent.DamageModifier.BASE); 
				try
				{
					event.setDamage(EntityDamageEvent.DamageModifier.ARMOR, event.getDamage(EntityDamageEvent.DamageModifier.ARMOR) * damagepct); 
				} catch (Exception e1) {}
				try
				{
					event.setDamage(EntityDamageEvent.DamageModifier.MAGIC, event.getDamage(EntityDamageEvent.DamageModifier.MAGIC) * damagepct); 
				} catch (Exception e1) {}
				try
				{
					event.setDamage(EntityDamageEvent.DamageModifier.RESISTANCE, event.getDamage(EntityDamageEvent.DamageModifier.RESISTANCE) * damagepct); 
				} catch (Exception e1) {}
				try
				{
					event.setDamage(EntityDamageEvent.DamageModifier.BLOCKING, event.getDamage(EntityDamageEvent.DamageModifier.BLOCKING) * damagepct); 
				} catch (Exception e1) {}
				
				event.setDamage(DamageModifier.BASE, newdmg);
			}
		}
		
		// SLASHING
		if (event.getCause() == DamageCause.ENTITY_ATTACK) {
			Material materialinhand = attacker.getInventory().getItemInMainHand().getType();
		
			if (materialinhand.equals(Material.WOOD_SWORD) || 
					materialinhand.equals(Material.WOOD_AXE) ||
					materialinhand.equals(Material.STONE_SWORD) ||
					materialinhand.equals(Material.STONE_AXE) ||
					materialinhand.equals(Material.IRON_SWORD) ||
					materialinhand.equals(Material.IRON_AXE) ||
					materialinhand.equals(Material.GOLD_SWORD) ||
					materialinhand.equals(Material.GOLD_AXE) ||
					materialinhand.equals(Material.DIAMOND_SWORD) ||
					materialinhand.equals(Material.DIAMOND_AXE) 
			)
			{
				// Apply slashing modifier
				SoliniaPlayerSkill skill = player.getSkill("SLASHING");
				double racestatbonus = player.getStrength() + skill.getValue();
				double bonus = racestatbonus / 100;
				double damagemlt = currentdamage * bonus;
				double newdmg = damagemlt;
				double damagepct = newdmg / event.getDamage(EntityDamageEvent.DamageModifier.BASE); 
				try
				{
					event.setDamage(EntityDamageEvent.DamageModifier.ARMOR, event.getDamage(EntityDamageEvent.DamageModifier.ARMOR) * damagepct); 
				} catch (Exception e1) {}
				try
				{
					event.setDamage(EntityDamageEvent.DamageModifier.MAGIC, event.getDamage(EntityDamageEvent.DamageModifier.MAGIC) * damagepct); 
				} catch (Exception e1) {}
				try
				{
					event.setDamage(EntityDamageEvent.DamageModifier.RESISTANCE, event.getDamage(EntityDamageEvent.DamageModifier.RESISTANCE) * damagepct); 
				} catch (Exception e1) {}
				try
				{
					event.setDamage(EntityDamageEvent.DamageModifier.BLOCKING, event.getDamage(EntityDamageEvent.DamageModifier.BLOCKING) * damagepct); 
				} catch (Exception e1) {}
				
				event.setDamage(DamageModifier.BASE, newdmg);
			}
		}
		
		// CRUSHING
		if (event.getCause() == DamageCause.ENTITY_ATTACK) {
			Material materialinhand = attacker.getInventory().getItemInMainHand().getType();
		
			if (materialinhand.equals(Material.STICK) || 
					materialinhand.equals(Material.WOOD_SPADE) ||
					materialinhand.equals(Material.STONE_SPADE) ||
					materialinhand.equals(Material.IRON_SPADE) ||
					materialinhand.equals(Material.GOLD_SPADE) ||
					materialinhand.equals(Material.DIAMOND_SPADE) ||
					materialinhand.equals(Material.AIR)
			)
			{
				// Apply slashing modifier
				SoliniaPlayerSkill skill = player.getSkill("CRUSHING");
				double racestatbonus = player.getStrength() + skill.getValue();
				double bonus = racestatbonus / 100;
				double damagemlt = currentdamage * bonus;
				double newdmg = damagemlt;
				double damagepct = newdmg / event.getDamage(EntityDamageEvent.DamageModifier.BASE); 
				try
				{
					event.setDamage(EntityDamageEvent.DamageModifier.ARMOR, event.getDamage(EntityDamageEvent.DamageModifier.ARMOR) * damagepct); 
				} catch (Exception e1) {}
				try
				{
					event.setDamage(EntityDamageEvent.DamageModifier.MAGIC, event.getDamage(EntityDamageEvent.DamageModifier.MAGIC) * damagepct); 
				} catch (Exception e1) {}
				try
				{
					event.setDamage(EntityDamageEvent.DamageModifier.RESISTANCE, event.getDamage(EntityDamageEvent.DamageModifier.RESISTANCE) * damagepct); 
				} catch (Exception e1) {}
				try
				{
					event.setDamage(EntityDamageEvent.DamageModifier.BLOCKING, event.getDamage(EntityDamageEvent.DamageModifier.BLOCKING) * damagepct); 
				} catch (Exception e1) {}
				
				event.setDamage(DamageModifier.BASE, newdmg);
			}
		}
		
		SkillReward reward = Utils.getSkillForMaterial(attacker.getInventory().getItemInMainHand().getType().toString());
		if (reward != null)
		{
			player.tryIncreaseSkill(reward.getSkillname(),reward.getXp());
		}
	}
	
	@Override
	public boolean isPet() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int getLevel() {
		return level;
	}

	@Override
	public void setLevel(int level) {
		this.level = level;
	}

	@Override
	public void dropLoot() {
		try {
			getBukkitLivingEntity().getWorld().dropItem(this.getBukkitLivingEntity().getLocation(),SoliniaItemFactory.GenerateRandomLoot().asItemStack());
		} catch (CoreStateInitException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SoliniaItemException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
