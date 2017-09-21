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
import com.solinia.solinia.Interfaces.ISoliniaItem;
import com.solinia.solinia.Interfaces.ISoliniaPlayer;
import com.solinia.solinia.Interfaces.ISoliniaSpell;
import com.solinia.solinia.Managers.StateManager;
import com.solinia.solinia.Models.SoliniaActiveSpellEffect;
import com.solinia.solinia.Models.SoliniaEntitySpellEffects;
import com.solinia.solinia.Models.SoliniaPlayerSkill;
import com.solinia.solinia.Models.SpellEffect;
import com.solinia.solinia.Models.SpellEffectType;

public class PlayerRegenTickTimer extends BukkitRunnable {

	@Override
	public void run() {

		for (Player player : Bukkit.getServer().getOnlinePlayers()) {
			grantPlayerRegenBonuses(player);
		}
	}

	private void grantPlayerRegenBonuses(Player player) {
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
		manaregen += getPlayerSneakingManaBonus(solplayer);
		
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
					if (item.getMpregen() > 0) {
						manaregen += item.getMpregen();
					}

					if (item.getHpregen() > 0) {
						hpregen += item.getHpregen();
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
		solplayer.increasePlayerMana(manaregen);
	}

	private int getPlayerSneakingManaBonus(ISoliniaPlayer solplayer) {
		int manaregen = 0;
		if (solplayer.getBukkitPlayer().isSneaking()) {
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
