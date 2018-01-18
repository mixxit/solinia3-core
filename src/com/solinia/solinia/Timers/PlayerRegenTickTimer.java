package com.solinia.solinia.Timers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import com.solinia.solinia.Adapters.SoliniaPlayerAdapter;
import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Interfaces.ISoliniaAAAbility;
import com.solinia.solinia.Interfaces.ISoliniaItem;
import com.solinia.solinia.Interfaces.ISoliniaPlayer;
import com.solinia.solinia.Interfaces.ISoliniaSpell;
import com.solinia.solinia.Managers.StateManager;
import com.solinia.solinia.Models.SoliniaPlayerSkill;
import com.solinia.solinia.Models.SpellEffect;
import com.solinia.solinia.Models.SpellEffectType;
import com.solinia.solinia.Utils.ItemStackUtils;
import com.solinia.solinia.Utils.Utils;

public class PlayerRegenTickTimer extends BukkitRunnable {

	@Override
	public void run() {

		for (Player player : Bukkit.getServer().getOnlinePlayers()) {
			grantPlayerRegenBonuses(player);
		}
	}

	private void grantPlayerRegenBonuses(Player player) {
		if (player.isDead())
			return;
		
		// Apply Crouch Mana Regen Bonus
		int manaregen = 1;
		
		ISoliniaPlayer solplayer = null;
		try {
			solplayer = SoliniaPlayerAdapter.Adapt(player);
		} catch (CoreStateInitException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		if (solplayer == null)
			return;
		
		// a players mana regen based on if they are meditating (sneaking)
		manaregen += getPlayerMeditatingManaBonus(solplayer);
		
		ISoliniaAAAbility aa = null;
		try
		{
			aa = StateManager.getInstance().getConfigurationManager().getFirstAAAbilityBySysname("MENTALCLARITY");
		} catch (CoreStateInitException e)
		{
			
		}
		
		int aamanaregenrank = 0;
		
		if (aa != null)
		{
			aamanaregenrank = Utils.getRankOfAAAbility(player, aa);
			manaregen += aamanaregenrank;
		}
		
		// Hp and Mana Regen from Items
		int hpregen = 0;
		
		List<ItemStack> itemStackBonuses = new ArrayList<ItemStack>() {{ add(player.getInventory().getItemInMainHand()); add(player.getInventory().getItemInOffHand()); addAll(Arrays.asList(player.getInventory().getArmorContents())); }};
		
		for (ItemStack itemstack : itemStackBonuses) {
			if (itemstack == null)
				continue;
			if (itemstack.getEnchantmentLevel(Enchantment.OXYGEN) > 999) {
				
				try {
					
					ISoliniaItem item = StateManager.getInstance().getConfigurationManager().getItem(itemstack);
					if (item == null)
						continue;

					
					
					//System.out.println("Checking mpregen for " + player.getName() + " " + item.getDisplayname());
					if (item.getMpregen() > 0) {
						manaregen += item.getMpregen();
					}

					if (item.getHpregen() > 0) {
						hpregen += item.getHpregen();
					}
										
					Integer augmentationId = ItemStackUtils.getAugmentationItemId(itemstack);
					if (augmentationId != null && augmentationId != 0)
					{
						ISoliniaItem augItem = StateManager.getInstance().getConfigurationManager().getItem(augmentationId);
						//System.out.println("Checking aug mpregen for " + player.getName() + " " + augItem.getDisplayname() + " " + augItem.getMpregen());
						if (augItem.getHpregen() > 0)
						{
							hpregen += augItem.getHpregen();
						}
						if (augItem.getMpregen() > 0)
						{
							manaregen += augItem.getMpregen();
						}
					}
				
				} catch (CoreStateInitException e) {
					e.printStackTrace();
				}
				
			}
		}

		// Process HP Regeneration
		if (hpregen > 0) {
			int amount = (int) Math.round(player.getHealth()) + hpregen;
			if (amount > player.getMaxHealth()) {
				amount = (int) Math.round(player.getMaxHealth());
			}
			
			if (amount < 0)
				amount = 0;

			player.setHealth(amount);
		}
		
		// Process Mana Regeneration
		//System.out.println(player.getName() + " was found to have " + manaregen + " mana regen");
		solplayer.increasePlayerMana(manaregen);
	}

	private int getPlayerMeditatingManaBonus(ISoliniaPlayer solplayer) {
		int manaregen = 0;
		if (solplayer.isMeditating())
		{
			SoliniaPlayerSkill meditationskill = solplayer.getSkill("MEDITATION");
			int bonusmana = 3 + (meditationskill.getValue() / 15);

			manaregen += bonusmana;

			// apply meditation skill increase
			Random r = new Random();
			int randomInt = r.nextInt(100) + 1;
			if (randomInt > 90) {
				int currentvalue = 0;
				SoliniaPlayerSkill skill = solplayer.getSkill("MEDITATION");
				if (skill != null) {
					currentvalue = skill.getValue();
				}

				if ((currentvalue + 1) <= solplayer.getSkillCap("MEDITATION")) {
					solplayer.setSkill("MEDITATION", currentvalue + 1);
				}

			}
		}
		return manaregen;
	}
}
