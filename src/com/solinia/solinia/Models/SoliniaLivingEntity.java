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

import com.solinia.solinia.Adapters.SoliniaPlayerAdapter;
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
import com.solinia.solinia.Utils.SpellTargetType;
import com.solinia.solinia.Utils.Utils;

public class SoliniaLivingEntity implements ISoliniaLivingEntity {
	LivingEntity livingentity;
	private int level = 1;
	private int npcid;

	public SoliniaLivingEntity(LivingEntity livingentity) {
		this.livingentity = livingentity;
		
		String metaid = "";
		if (livingentity != null)
		for(MetadataValue val : livingentity.getMetadata("mobname"))
		{
			metaid = val.asString();
		}

		if (metaid != null)
			if(!metaid.equals(""))
				installNpcByMetaName(metaid);
	}

	private void installNpcByMetaName(String metaid) {
		if (isPlayer())
			return;
		
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
	public void modifyDamageEvent(LivingEntity damager, EntityDamageByEntityEvent event) {
		
		
		LivingEntity attacker = damager;
		
		// Change attacker to archer
		if (event.getDamager() instanceof Arrow)
		{
			Arrow arr = (Arrow)event.getDamager();
			if (arr.getShooter() instanceof LivingEntity)
			{
				attacker = (LivingEntity)arr.getShooter();
			} else {
			}
		} 
		
		if (attacker == null)
			return;
		
		if (attacker instanceof Player && getBukkitLivingEntity() instanceof Wolf)
		{
			if (isPet())
			{
				event.setCancelled(true);
				return;
			}
		}
		
		// damage shield response
		try
		{
			SoliniaEntitySpellEffects effects = StateManager.getInstance().getEntityManager().getActiveEntityEffects(getBukkitLivingEntity());
            
            if (effects != null && (!(event.getDamager() instanceof Arrow)))
            {
	            for(SoliniaActiveSpellEffect effect : effects.getActiveSpells())
	            {
	            	ISoliniaSpell spell = StateManager.getInstance().getConfigurationManager().getSpell(effect.getSpellId());
	            	if (spell.isDamageShield())
	            	{
	            		for(SpellEffect spelleffect : spell.getSpellEffects())
	            		{
	            			if (spelleffect.getSpellEffectType().equals(SpellEffectType.DamageShield))
	            			{
	            				// hurt enemy with damage shield
	            				if (spelleffect.getBase() < 0)
	            				attacker.damage(spelleffect.getBase() * -1);
	            			}
	            		}
	            	}
	            }
            }
		} catch (CoreStateInitException e)
		{
			return;
		}
		
		// Validate attackers weapon (player only)
		// Apply player skill damages
		if (attacker instanceof Player)
		{
			Player player = (Player)attacker;
			ISoliniaPlayer solplayer;
			try
			{
				solplayer = SoliniaPlayerAdapter.Adapt(player);
			} catch (CoreStateInitException e)
			{
				return;
			}
			
			if (player.getInventory().getItemInMainHand().getEnchantmentLevel(Enchantment.OXYGEN) > 999)
		    {
				try
				{
					ISoliniaItem soliniaitem = StateManager.getInstance().getConfigurationManager().getItem(player.getInventory().getItemInMainHand());
					if (soliniaitem != null)
					{
						if (soliniaitem.getAllowedClassNames().size() > 0)
						{
											if (solplayer.getClassObj() == null)
							{
								System.out.print("Player class was null");
								event.setCancelled(true);
								player.updateInventory();
								player.sendMessage(ChatColor.GRAY + "Your class cannot use this item");
				    			return;
							}
								    		
				    		if (!soliniaitem.getAllowedClassNames().contains(solplayer.getClassObj().getName()))
				    		{
				    			event.setCancelled(true);
				    			player.updateInventory();
				    			player.sendMessage(ChatColor.GRAY + "Your class cannot use this item");
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
					SoliniaPlayerSkill skill = solplayer.getSkill("ARCHERY");
					double racestatbonus = solplayer.getDexterity() + skill.getValue();
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
				Material materialinhand = player.getInventory().getItemInMainHand().getType();
			
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
					SoliniaPlayerSkill skill = solplayer.getSkill("SLASHING");
					double racestatbonus = solplayer.getStrength() + skill.getValue();
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
				Material materialinhand = player.getInventory().getItemInMainHand().getType();
			
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
					SoliniaPlayerSkill skill = solplayer.getSkill("CRUSHING");
					double racestatbonus = solplayer.getStrength() + skill.getValue();
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
			
			SkillReward reward = Utils.getSkillForMaterial(player.getInventory().getItemInMainHand().getType().toString());
			if (reward != null)
			{
				solplayer.tryIncreaseSkill(reward.getSkillname(),reward.getXp());
			}
		}
	}
	
	@Override
	public boolean isPet() {
		if (isPlayer())
			return false;
		
		return false;
	}

	@Override
	public int getLevel() {
		if (isPlayer())
		{
			try {
				ISoliniaPlayer solPlayer = SoliniaPlayerAdapter.Adapt((Player)this.getBukkitLivingEntity());
				return solPlayer.getLevel();
			} catch (CoreStateInitException e) {
				return 0;
			}
		}
		
		return level;
	}

	@Override
	public void setLevel(int level) {
		if (isPlayer())
			return;
		
		this.level = level;
	}

	@Override
	public void dropLoot() {
		if (isPlayer())
			return;
		
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
	public boolean isPlayer()
	{
		if (this.getBukkitLivingEntity() == null)
			return false;
		
		if (this.getBukkitLivingEntity() instanceof Player)
			return true;
		
		return false;
	}

	@Override
	public void emote(String message) {
		StateManager.getInstance().getChannelManager().sendToLocalChannel(this,message);
	}

	@Override
	public void doRandomChat() {
		if (isPlayer())
			return;
		
		try {
			ISoliniaNPC npc = StateManager.getInstance().getConfigurationManager().getNPC(this.getNpcid());
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
		if (isPlayer())
			return;
		
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
		if (isPlayer())
			return;
		
		if (livingEntity == null || this.livingentity == null)
			return;
		
		ISoliniaNPC npc;
		try {
			npc = StateManager.getInstance().getConfigurationManager().getNPC(this.getNpcid());
			if (npc.getClassid() < 1)
				return;
			
			// TODO move this out of the method, its name implies it will always cast
			// Randomise chance to cast (30%)
			//int chanceToCast = Utils.RandomBetween(1,100);
			//if (chanceToCast < 70)
			//	return;

			List<ISoliniaSpell> spells = StateManager.getInstance().getConfigurationManager().getSpellsByClassIdAndMaxLevel(npc.getClassid(), npc.getLevel());
			if (spells.size() == 0)
				return;
			
			List<ISoliniaSpell> hostileSpells = new ArrayList<ISoliniaSpell>();
			List<ISoliniaSpell> beneficialSpells = new ArrayList<ISoliniaSpell>();

			for(ISoliniaSpell spell : spells)
			{
				if (!spell.isBeneficial())
				{
					if (Utils.getSpellTargetType(spell.getTargettype()).equals(SpellTargetType.Target) || Utils.getSpellTargetType(spell.getTargettype()).equals(SpellTargetType.TargetOptional))
						hostileSpells.add(spell);
					continue;
				}
				
				if (Utils.getSpellTargetType(spell.getTargettype()).equals(SpellTargetType.Self) ||
						Utils.getSpellTargetType(spell.getTargettype()).equals(SpellTargetType.Target) ||
						Utils.getSpellTargetType(spell.getTargettype()).equals(SpellTargetType.TargetOptional))
					beneficialSpells.add(spell);
			}
			
			int chanceToCastBeneficial = Utils.RandomBetween(1, 10);
			
			boolean success = false;
			
			if (chanceToCastBeneficial > 7)
			{
				// Cast on self
				ISoliniaSpell spellToCast = Utils.getRandomItemFromList(beneficialSpells);
				if (getMana() > spellToCast.getMana())
				{
					success = spellToCast.tryApplyOnEntity(this.livingentity,this.livingentity);
				}
				if (success)
				{
					this.setMana(this.getMana() - spellToCast.getMana());
				}
			} else {
				ISoliniaSpell spellToCast = Utils.getRandomItemFromList(hostileSpells);
				if (getMana() > spellToCast.getMana())
				{
					success = spellToCast.tryApplyOnEntity(this.livingentity,livingEntity);
				}
				if (success)
				{
					this.setMana(this.getMana() - spellToCast.getMana());
				}
			}
		} catch (CoreStateInitException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void setMana(int amount) {
		if (isPlayer())
			return;
		
		if (this.getNpcid() < 1)
			return;
		
		try
		{
			ISoliniaNPC npc = StateManager.getInstance().getConfigurationManager().getNPC(this.getNpcid());
			if (npc == null)
				return;
			StateManager.getInstance().getEntityManager().setNPCMana(this.getBukkitLivingEntity(),npc,amount);
		} catch (CoreStateInitException e)
		{
			return;
		}
		
	}

	@Override
	public Integer getMana() {
		if (isPlayer())
		{
			try {
				ISoliniaPlayer solPlayer = SoliniaPlayerAdapter.Adapt((Player)this.getBukkitLivingEntity());
				return solPlayer.getMana();
			} catch (CoreStateInitException e) {
				return 0;
			}
		}
		
		if (this.getNpcid() < 1)
			return 0;
		
		try
		{
			ISoliniaNPC npc = StateManager.getInstance().getConfigurationManager().getNPC(this.getNpcid());
			if (npc == null)
				return 0;
			return StateManager.getInstance().getEntityManager().getNPCMana(this.getBukkitLivingEntity(),npc);
		} catch (CoreStateInitException e)
		{
			return 0;
		}
	}

	@Override
	public int getResistsFromActiveEffects(SpellResistType type) {
		int total = 0;
		SpellEffectType seekSpellEffectType = Utils.getSpellEffectTypeFromResistType(type);
		
		if (seekSpellEffectType != null)
		{
			try
			{
				SoliniaEntitySpellEffects effects = StateManager.getInstance().getEntityManager().getActiveEntityEffects(getBukkitLivingEntity());
		        	
				for(SoliniaActiveSpellEffect effect : effects.getActiveSpells())
		        {
		        	ISoliniaSpell spell = StateManager.getInstance().getConfigurationManager().getSpell(effect.getSpellId());
		        	for(SpellEffect spelleffect : spell.getSpellEffects())
		    		{
		    			if (spelleffect.getSpellEffectType().equals(SpellEffectType.ResistAll) || spelleffect.getSpellEffectType().equals(seekSpellEffectType))
		    			{
		    				total += spelleffect.getBase();
		    			}
		    		}
		        }
			} catch (CoreStateInitException e)
			{
				// skip over
			}
		}
		
		return total;
	}

	@Override
	public int getResists(SpellResistType type) {
		if (isPlayer())
		{
			try {
				return SoliniaPlayerAdapter.Adapt((Player)getBukkitLivingEntity()).getResist(type);
			} catch (CoreStateInitException e) {
				return 25;
			}
		} else {
			return 25 + getResistsFromActiveEffects(type);
		}
	}
}
