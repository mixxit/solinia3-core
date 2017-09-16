package com.solinia.solinia.Models;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

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
import org.bukkit.metadata.MetadataValue;

import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Interfaces.ISoliniaClass;
import com.solinia.solinia.Interfaces.ISoliniaItem;
import com.solinia.solinia.Interfaces.ISoliniaLivingEntity;
import com.solinia.solinia.Interfaces.ISoliniaLootDrop;
import com.solinia.solinia.Interfaces.ISoliniaLootDropEntry;
import com.solinia.solinia.Interfaces.ISoliniaLootTable;
import com.solinia.solinia.Interfaces.ISoliniaLootTableEntry;
import com.solinia.solinia.Interfaces.ISoliniaNPC;
import com.solinia.solinia.Interfaces.ISoliniaPlayer;
import com.solinia.solinia.Interfaces.ISoliniaSpell;
import com.solinia.solinia.Managers.StateManager;
import com.solinia.solinia.Utils.Utils;

public class SoliniaLivingEntity implements ISoliniaLivingEntity {
	LivingEntity livingentity;
	private int level = 1;
	private int npcid;

	public SoliniaLivingEntity(LivingEntity livingentity) {
		
		String metaid = "";
		if (livingentity != null)
		for(MetadataValue val : livingentity.getMetadata("mobname"))
		{
			metaid = val.asString();
		}
		
		this.livingentity = livingentity;

		if (metaid != null)
			if(!metaid.equals(""))
				installNpcByMetaName(metaid);
	}

	private void installNpcByMetaName(String metaid) {
		if (!metaid.contains("NPCID_"))
			return;
		
		int npcId = Integer.parseInt(metaid.substring(6));
		try
		{
			ISoliniaNPC npc = StateManager.getInstance().getConfigurationManager().getNPC(npcId);
			
			if (npc == null)
				return;
			
			setLevel(npc.getLevel());
			setNpcid(npc.getId());
		} catch (CoreStateInitException e)
		{
			e.printStackTrace();
		}
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
			if (getNpcid() > 0)
			{
				ISoliniaNPC npc = StateManager.getInstance().getConfigurationManager().getNPC(getNpcid());
				if (npc.getLoottableid() == 0)
					return;

				ISoliniaLootTable table = StateManager.getInstance().getConfigurationManager().getLootTable(npc.getLoottableid());

				List<ISoliniaLootDropEntry> absoluteitems = new ArrayList<ISoliniaLootDropEntry>();
				List<ISoliniaLootDropEntry> rollitems = new ArrayList<ISoliniaLootDropEntry>();

				for (ISoliniaLootTableEntry entry : StateManager.getInstance().getConfigurationManager().getLootTable(table.getId()).getEntries()) {
					ISoliniaLootDrop droptable = StateManager.getInstance().getConfigurationManager().getLootDrop(entry.getLootdropid());
					for (ISoliniaLootDropEntry dropentry : StateManager.getInstance().getConfigurationManager().getLootDrop(droptable.getId()).getEntries()) {
						if (dropentry.isAlways() == true) {
							absoluteitems.add(dropentry);
							continue;
						}

						rollitems.add(dropentry);
					}
				}

				// Now we have prepared our loot list items let's choose which will
				// drop

				System.out.println("Prepared a Loot List of ABS: " + absoluteitems.size() + " and ROLL: " + rollitems.size());

				if (absoluteitems.size() == 0 && rollitems.size() == 0)
					return;

				int dropcount = StateManager.getInstance().getWorldPerkDropCountModifier();

				Random r = new Random();
				int randomInt = r.nextInt(100) + 1;

				if (rollitems.size() > 0) {
					// Based on the chance attempt to drop this item
					for (int i = 0; i < dropcount; i++) {
						ISoliniaLootDropEntry droptableentry = rollitems.get(new Random().nextInt(rollitems.size()));
						ISoliniaItem item = StateManager.getInstance().getConfigurationManager().getItem(droptableentry.getItemid());

						randomInt = r.nextInt(100) + 1;
						System.out.println("Rolled a " + randomInt + " against a max of " + droptableentry.getChance()
								+ " for item: " + item.getDisplayname());
						if (randomInt <= droptableentry.getChance()) {
							getBukkitLivingEntity().getLocation().getWorld().dropItem(getBukkitLivingEntity().getLocation(),
									item.asItemStack());
						}
					}
				}

				// Always drop these items
				if (absoluteitems.size() > 0) {
					for (int i = 0; i < absoluteitems.size(); i++) {
						ISoliniaItem item = StateManager.getInstance().getConfigurationManager().getItem(absoluteitems.get(i).getItemid());
						for (int c = 0; c < absoluteitems.get(i).getCount(); c++) {
							getBukkitLivingEntity().getLocation().getWorld().dropItem(getBukkitLivingEntity().getLocation(),
									item.asItemStack());
						}
					}
				}
			} else {
				/*
				 * This is no longer needed now we have loot drops
				int itemDropMinimum = 95;
				if (Utils.RandomChance(itemDropMinimum))
				{
					if (getBukkitLivingEntity() instanceof Monster)
						getBukkitLivingEntity().getWorld().dropItem(this.getBukkitLivingEntity().getLocation(),SoliniaItemFactory.GenerateRandomLoot().asItemStack());
				}
				*/
			}
		} catch (CoreStateInitException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public int getNpcid() {
		return npcid;
	}

	@Override
	public void setNpcid(int npcid) {
		this.npcid = npcid;
	}

	@Override
	public void emote(String message) {
		StateManager.getInstance().getChannelManager().sendToLocalChannel(this,message);
	}

	@Override
	public void doRandomChat() {
		ISoliniaNPC npc;
		try {
			npc = StateManager.getInstance().getConfigurationManager().getNPC(this.getNpcid());
			if (npc.getRandomchatTriggerText() == null || npc.getRandomchatTriggerText().equals(""))
				return;
			
			// 2% chance of saying something
			int random = Utils.RandomBetween(1, 100);
			if (random < 2)
			{
				this.emote(ChatColor.AQUA + npc.getName() + " says '" + npc.getRandomchatTriggerText() + "'" + ChatColor.RESET);
			}
		} catch (CoreStateInitException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Override
	public void doSlayChat() {
		ISoliniaNPC npc;
		try {
			npc = StateManager.getInstance().getConfigurationManager().getNPC(this.getNpcid());
			if (npc.getKillTriggerText() == null || npc.getKillTriggerText().equals(""))
				return;
			
			this.emote(ChatColor.AQUA + npc.getName() + " says '" + npc.getKillTriggerText() + "'" + ChatColor.RESET);
		} catch (CoreStateInitException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void doSpellCast(LivingEntity livingEntity) {
		ISoliniaNPC npc;
		try {
			npc = StateManager.getInstance().getConfigurationManager().getNPC(this.getNpcid());
			if (npc.getClassid() < 1)
				return;
			
			// TODO move this out of the method, its name implies it will always cast
			// Randomise chance to cast (30%)
			int chanceToCast = Utils.RandomBetween(1,100);
			if (chanceToCast < 70)
				return;

			List<ISoliniaSpell> spells = StateManager.getInstance().getConfigurationManager().getSpellsByClassIdAndMaxLevel(npc.getClassid(), npc.getLevel());
			if (spells.size() == 0)
				return;
			
			List<ISoliniaSpell> hostileSpells = new ArrayList<ISoliniaSpell>();
			
			for(ISoliniaSpell spell : spells)
			{
				if (!spell.isBeneficial())
					hostileSpells.add(spell);
					
			}
			
			ISoliniaSpell spellToCast = Utils.getRandomItemFromList(hostileSpells);
			System.out.println("Entity casting spell: " + spellToCast.getName());
			spellToCast.tryApplyOnEntity(this.livingentity,livingEntity);
			
			
		} catch (CoreStateInitException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
