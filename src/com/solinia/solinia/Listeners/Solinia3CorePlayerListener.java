package com.solinia.solinia.Listeners;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.craftbukkit.v1_13_R2.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryType.SlotType;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;

import com.solinia.solinia.Solinia3CorePlugin;
import com.solinia.solinia.Adapters.SoliniaItemAdapter;
import com.solinia.solinia.Adapters.SoliniaLivingEntityAdapter;
import com.solinia.solinia.Adapters.SoliniaPlayerAdapter;
import com.solinia.solinia.Events.PlayerZoneTickEvent;
import com.solinia.solinia.Events.SoliniaPlayerJoinEvent;
import com.solinia.solinia.Events.SoliniaSyncPlayerChatEvent;
import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Exceptions.SoliniaItemException;
import com.solinia.solinia.Interfaces.ISoliniaGroup;
import com.solinia.solinia.Interfaces.ISoliniaItem;
import com.solinia.solinia.Interfaces.ISoliniaLivingEntity;
import com.solinia.solinia.Interfaces.ISoliniaPlayer;
import com.solinia.solinia.Managers.ConfigurationManager;
import com.solinia.solinia.Managers.StateManager;
import com.solinia.solinia.Models.SoliniaWorld;
import com.solinia.solinia.Models.SoliniaZone;
import com.solinia.solinia.Models.UniversalMerchant;
import com.solinia.solinia.Utils.ItemStackUtils;
import com.solinia.solinia.Utils.Utils;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import net.milkbowl.vault.economy.EconomyResponse;
import net.minecraft.server.v1_13_R2.EntityHuman;
import net.minecraft.server.v1_13_R2.GenericAttributes;

public class Solinia3CorePlayerListener implements Listener {

	Solinia3CorePlugin plugin;

	public Solinia3CorePlayerListener(Solinia3CorePlugin solinia3CorePlugin) {
		// TODO Auto-generated constructor stub
		plugin = solinia3CorePlugin;
	}
	
	@EventHandler
	public void onPlayerZoneTick(PlayerZoneTickEvent event)
	{
		if (event.isCancelled())
			return;
		
		if (event.getPlayer() == null || event.getZone() == null)
			return;
		
		if (event.getPlayer().getBukkitPlayer().isDead())
			return;
		
		int hpregen = event.getZone().getHpRegen();
		int mpregen = event.getZone().getManaRegen();
		
		if (hpregen > 0) {
			int amount = (int) Math.round(event.getPlayer().getBukkitPlayer().getHealth()) + hpregen;
			if (amount > event.getPlayer().getBukkitPlayer().getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue()) {
				amount = (int) Math.round(event.getPlayer().getBukkitPlayer().getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue());
			}
			
			if (amount < 0)
				amount = 0;

			if (!event.getPlayer().getBukkitPlayer().isDead())
			event.getPlayer().getSoliniaLivingEntity().setHealth(amount);
		}
		
		if (mpregen > 0)
			event.getPlayer().increasePlayerMana(mpregen);
	}
	
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) {

		try
	    {
			LivingEntity pet = StateManager.getInstance().getEntityManager().getPet(event.getPlayer().getUniqueId());
			if (pet != null) {
				ISoliniaLivingEntity petsolEntity = SoliniaLivingEntityAdapter.Adapt(pet);
				StateManager.getInstance().getEntityManager().removePet(event.getPlayer().getUniqueId(), !petsolEntity.isCharmed());
			}
			
			ISoliniaPlayer solPlayer = SoliniaPlayerAdapter.Adapt(event.getPlayer());
			if (solPlayer != null)
				solPlayer.setLastLocation(event.getPlayer().getLocation());
	    } catch (CoreStateInitException e)
		{
	    	
		}
		
		// enable knockback effects
	    try
	    {
	      Player player = event.getPlayer();
	      EntityHuman entityHuman = ((CraftPlayer)player).getHandle();
	      entityHuman.getAttributeInstance(GenericAttributes.c).setValue(0.0D);
	    }
	    catch (Exception ex)
	    {
	      ex.printStackTrace();
	    }
		
		ISoliniaGroup group = StateManager.getInstance().getGroupByMember(event.getPlayer().getUniqueId());
		if (group != null) {
			StateManager.getInstance().removePlayerFromGroup(event.getPlayer());
		}
		
		if (!event.getPlayer().hasPermission("essentials.silentquit"))
		StateManager.getInstance().getChannelManager().sendToDiscordMC(null,
				StateManager.getInstance().getChannelManager().getDiscordMainChannelId(),
				event.getPlayer().getName() + " has quit the game");
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onBlockBreak(BlockBreakEvent event) {
		if (event.isCancelled())
			return;

		if (ConfigurationManager.LumberingMaterials.contains(event.getBlock().getType().name())) {
			onLumber(event);
		}

		if (ConfigurationManager.MiningMaterials.contains(event.getBlock().getType().name())) {
			onMine(event);
		}

		if (ConfigurationManager.ForagingMaterials.contains(event.getBlock().getType().name())) {
			onForage(event);
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onBlockPlace(BlockPlaceEvent event) {
		try {
			ISoliniaItem soliniaitem = StateManager.getInstance().getConfigurationManager().getItem(event.getItemInHand());
			if (soliniaitem != null)
			{
				if (soliniaitem.getAbilityid() > 0 && soliniaitem.isSkullItem())
				{
					event.getPlayer().sendMessage(ChatColor.GRAY + "You cannot place a customised head item with an ability as a block");
					Utils.CancelEvent(event);
				}
			}
		} catch (CoreStateInitException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	public void onLumber(BlockBreakEvent event) {
		if (event.isCancelled())
			return;

		try {
			ISoliniaPlayer solplayer = SoliniaPlayerAdapter.Adapt((Player) event.getPlayer());

			SoliniaZone zone = solplayer.getFirstZone();
			solplayer.tryIncreaseSkill("LOGGING", 1);

			if (zone != null) {
				int minskill = 0;
				if (zone.getForestryMinSkill() > 0) {
					minskill = zone.getForestryMinSkill();
				}

				if (!solplayer.getSkillCheck("LOGGING", minskill + 50)) {
					return;
				}

				if (zone.getForestryLootTableId() > 0)
					Utils.DropLoot(zone.getForestryLootTableId(), event.getPlayer().getWorld(),
							event.getPlayer().getLocation());
			}

			SoliniaWorld world = solplayer.getSoliniaWorld();
			if (world != null) {
				int minskill = 0;
				if (world.getForestryMinSkill() > 0) {
					minskill = zone.getForestryMinSkill();
				}

				if (!solplayer.getSkillCheck("LOGGING", minskill + 50)) {
					return;
				}

				if (world.getForestryLootTableId() > 0)
					Utils.DropLoot(world.getForestryLootTableId(), event.getPlayer().getWorld(),
							event.getPlayer().getLocation());
			}

		} catch (CoreStateInitException e) {
			// do nothing
		}
	}

	public void onForage(BlockBreakEvent event) {
		if (event.isCancelled())
			return;

		try {
			ISoliniaPlayer solplayer = SoliniaPlayerAdapter.Adapt((Player) event.getPlayer());

			SoliniaZone zone = solplayer.getFirstZone();
			solplayer.tryIncreaseSkill("FORAGE", 1);

			if (zone != null) {
				int minskill = 0;
				if (zone.getForagingMinSkill() > 0) {
					minskill = zone.getForagingMinSkill();
				}

				if (!solplayer.getSkillCheck("FORAGE", minskill + 50)) {
					return;
				}

				if (zone.getForagingLootTableId() > 0)
					Utils.DropLoot(zone.getForagingLootTableId(), event.getPlayer().getWorld(),
							event.getPlayer().getLocation());
			}

			SoliniaWorld world = solplayer.getSoliniaWorld();
			if (world != null) {
				int minskill = 0;
				if (world.getForagingMinSkill() > 0) {
					minskill = zone.getForagingMinSkill();
				}

				if (!solplayer.getSkillCheck("FORAGE", minskill + 50)) {
					return;
				}

				if (world.getForagingLootTableId() > 0)
					Utils.DropLoot(world.getForagingLootTableId(), event.getPlayer().getWorld(),
							event.getPlayer().getLocation());
			}

		} catch (CoreStateInitException e) {
			// do nothing
		}
	}

	public void onMine(BlockBreakEvent event) {
		if (event.isCancelled())
			return;

		try {
			ISoliniaPlayer solplayer = SoliniaPlayerAdapter.Adapt((Player) event.getPlayer());

			SoliniaZone zone = solplayer.getFirstZone();
			solplayer.tryIncreaseSkill("MINING", 1);

			if (zone != null) {
				int minskill = 0;
				if (zone.getMiningMinSkill() > 0) {
					minskill = zone.getMiningMinSkill();
				}

				if (!solplayer.getSkillCheck("MINING", minskill + 50)) {
					return;
				}

				if (zone.getMiningLootTableId() > 0)
					Utils.DropLoot(zone.getMiningLootTableId(), event.getPlayer().getWorld(),
							event.getPlayer().getLocation());
			}

			SoliniaWorld world = solplayer.getSoliniaWorld();
			if (world != null) {
				int minskill = 0;
				if (world.getMiningMinSkill() > 0) {
					minskill = zone.getMiningMinSkill();
				}

				if (!solplayer.getSkillCheck("MINING", minskill + 50)) {
					return;
				}

				if (world.getMiningLootTableId() > 0)
					Utils.DropLoot(world.getMiningLootTableId(), event.getPlayer().getWorld(),
							event.getPlayer().getLocation());
			}

		} catch (CoreStateInitException e) {
			// do nothing
		}
	}

	@EventHandler
	public void onFish(PlayerFishEvent event) {
		if (event.isCancelled())
			return;

		try {
			ISoliniaPlayer solplayer = SoliniaPlayerAdapter.Adapt((Player) event.getPlayer());

			SoliniaZone zone = solplayer.getFirstZone();
			solplayer.tryIncreaseSkill("FISHING", 1);

			if (zone != null) {
				int minskill = 0;
				if (zone.getFishingMinSkill() > 0) {
					minskill = zone.getFishingMinSkill();
				}

				if (!solplayer.getSkillCheck("FISHING", minskill + 50)) {
					return;
				}

				if (zone.getFishingLootTableId() > 0)
					Utils.DropLoot(zone.getFishingLootTableId(), event.getPlayer().getWorld(),
							event.getPlayer().getLocation());
			}

			SoliniaWorld world = solplayer.getSoliniaWorld();
			if (world != null) {
				int minskill = 0;
				if (world.getFishingMinSkill() > 0) {
					minskill = zone.getFishingMinSkill();
				}

				if (!solplayer.getSkillCheck("FISHING", minskill + 50)) {
					return;
				}

				if (world.getFishingLootTableId() > 0)
					Utils.DropLoot(world.getFishingLootTableId(), event.getPlayer().getWorld(),
							event.getPlayer().getLocation());
			}

		} catch (CoreStateInitException e) {
			// do nothing
		}
	}

	@EventHandler
	public void onPlayerSneak(PlayerToggleSneakEvent event) {
		if (event.isCancelled())
			return;

		try {
			ISoliniaPlayer solplayer = SoliniaPlayerAdapter.Adapt((Player) event.getPlayer());
			if (solplayer.getClassObj() != null) {
				if (solplayer.getClassObj().isSneakFromCrouch()) {
					// too spammy, move ot action bar
					event.getPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR,
							new TextComponent(ChatColor.GRAY + "* You sneak, hiding from enemies not currently engaged"));
				}
			}
		} catch (CoreStateInitException e) {
			// do nothing
		}
	}

	@EventHandler
	public void onPlayerMove(PlayerMoveEvent event) {
		try {
			Player player = event.getPlayer();
			
			// If player is casting spell and spell is not allowed to be used during moving then cancel
			
			/*
			 * TODO - allow a tiny bit of movement with a chance to interupt
			CastingSpell castingSpell = StateManager.getInstance().getEntityManager().getCasting(event.getPlayer());
			if (castingSpell != null)
			{
				if (castingSpell.getSpell() != null && castingSpell.getSpell().getUninterruptable() == 0)
				{
					if (event.getTo().getBlockX() != event.getFrom().getBlockX() ||
							event.getTo().getBlockY() != event.getFrom().getBlockY() || 
									event.getTo().getBlockZ() != event.getFrom().getBlockZ()
							) {
						StateManager.getInstance().getEntityManager().interruptCasting(event.getPlayer());
					}
					
				}
			}
			*/
			
			if (
					event.getFrom().getBlockX() != event.getTo().getBlockX() ||
					event.getFrom().getBlockY() != event.getTo().getBlockY() ||
					event.getFrom().getBlockZ() != event.getTo().getBlockZ()				
				) 
			{
				// cancel feigned if moving
				try
				{
					boolean feigned = StateManager.getInstance().getEntityManager().isFeignedDeath(event.getPlayer().getUniqueId());
					if (feigned == true)
					{
						StateManager.getInstance().getEntityManager().setFeignedDeath(event.getPlayer().getUniqueId(), false);
					}
				} catch (CoreStateInitException e)
				{
					
				}


			} 
			
			// Prevent jump when slowed
			if (event.getTo().getBlockY() > event.getFrom().getBlockY()) {
				if (event.getPlayer().hasPotionEffect(PotionEffectType.SLOW)) {
					Utils.CancelEvent(event);
					event.getPlayer().sendMessage(ChatColor.GRAY + "* Your legs are bound and unable to jump!");
					return;
				}

			}

			Timestamp mezExpiry = StateManager.getInstance().getEntityManager().getMezzed((LivingEntity) player);
			if (mezExpiry != null) {
				player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ChatColor.GRAY + "* You are mezzed!"));
				if (event.getTo().getY() < event.getFrom().getY()) {
					event.getTo().setX(event.getFrom().getX());
					event.getTo().setZ(event.getFrom().getZ());
					event.getTo().setYaw(event.getFrom().getYaw());
					event.getTo().setPitch(event.getFrom().getPitch());

				} else {
					event.getTo().setX(event.getFrom().getX());
					event.getTo().setY(event.getFrom().getY());
					event.getTo().setZ(event.getFrom().getZ());
					event.getTo().setYaw(event.getFrom().getYaw());
					event.getTo().setPitch(event.getFrom().getPitch());
				}
				return;
			}
			
			Timestamp stunExpiry = StateManager.getInstance().getEntityManager().getStunned((LivingEntity) player);
			if (stunExpiry != null) {
				player.sendMessage("* You are stunned!");
				if (event.getTo().getY() < event.getFrom().getY()) {
					event.getTo().setX(event.getFrom().getX());
					event.getTo().setZ(event.getFrom().getZ());
					event.getTo().setYaw(event.getFrom().getYaw());
					event.getTo().setPitch(event.getFrom().getPitch());

				} else {
					event.getTo().setX(event.getFrom().getX());
					event.getTo().setY(event.getFrom().getY());
					event.getTo().setZ(event.getFrom().getZ());
					event.getTo().setYaw(event.getFrom().getYaw());
					event.getTo().setPitch(event.getFrom().getPitch());
				}
				return;
			}
			
			tryUpdateFollowers(player);

		} catch (CoreStateInitException e) {
			// do nothing
		}
	}

	private void tryUpdateFollowers(Player player) {
		try
		{
			for(UUID follower : StateManager.getInstance().getEntityManager().getFollowers(player.getUniqueId()))
			{
				Entity ent = Bukkit.getEntity(follower);
				if (ent == null)
					continue;
				
				if (!(ent instanceof Player))
					continue;
				
				if (ent.isDead())
					continue;
				
				if (player.getLocation().distance(ent.getLocation()) > 20)
				{
					ent.sendMessage("You are too far from your target, follow cancelled");
					StateManager.getInstance().getEntityManager().setFollowing(player.getUniqueId(), null);
					continue;
				}
				
				Utils.tryFollow((Player)ent, player, 4);
			}
		} catch (CoreStateInitException e)
		{
			
		}
	}

	@EventHandler
	public void onPlayerSwapHandItems(PlayerSwapHandItemsEvent event) {
		if (event.getPlayer().isSneaking())
		{
			try {
				StateManager.getInstance().getEntityManager().setEntityTarget(event.getPlayer(),
						event.getPlayer());
				Utils.CancelEvent(event);
				return;
			} catch (CoreStateInitException e)
			{
				
			}
		}
		
		if (event.isCancelled())
			return;

		try {
			ItemStack itemstack = event.getOffHandItem();
			if (itemstack == null)
				return;

			if (Utils.IsSoliniaItem(itemstack) && !itemstack.getType().equals(Material.ENCHANTED_BOOK)) {

				ISoliniaPlayer solplayer = SoliniaPlayerAdapter.Adapt((Player) event.getPlayer());
				ISoliniaItem soliniaitem = StateManager.getInstance().getConfigurationManager().getItem(itemstack);
				if (soliniaitem.getAllowedClassNames().size() == 0)
					return;

				if (solplayer.getClassObj() == null) {
					Utils.CancelEvent(event);
					;
					event.getPlayer().sendMessage(ChatColor.GRAY + "Your class cannot wear this armour");
					return;
				}

				if (!soliniaitem.getAllowedClassNames().contains(solplayer.getClassObj().getName().toUpperCase())) {
					Utils.CancelEvent(event);
					;
					event.getPlayer().getPlayer().sendMessage(ChatColor.GRAY + "Your class cannot wear this armour");
					return;
				}

				if (soliniaitem.getMinLevel() > solplayer.getLevel()) {
					Utils.CancelEvent(event);
					;
					event.getPlayer().getPlayer()
							.sendMessage(ChatColor.GRAY + "Your are not sufficient level wear this armour");
					return;
				}

				solplayer.scheduleUpdateMaxHp();
			}
		} catch (CoreStateInitException e) {

		}
	}

	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent event) {
		event.setDeathMessage("");

		try
	    {
			LivingEntity pet = StateManager.getInstance().getEntityManager().getPet(event.getEntity().getUniqueId());
			if (pet != null) {
				ISoliniaLivingEntity petsolEntity = SoliniaLivingEntityAdapter.Adapt(pet);
				StateManager.getInstance().getEntityManager().removePet(event.getEntity().getUniqueId(), !petsolEntity.isCharmed());
			}
			
			if (event.getEntity() instanceof Player)
			{
				ISoliniaPlayer solPlayer = SoliniaPlayerAdapter.Adapt(event.getEntity());
				if (solPlayer != null)
					solPlayer.setLastLocation(event.getEntity().getLocation());
			}
	    } catch (CoreStateInitException e)
		{
	    	
		}
		
		try {
			StateManager.getInstance().getEntityManager().clearEntityEffects(event.getEntity().getUniqueId());
			ISoliniaPlayer player = SoliniaPlayerAdapter.Adapt(event.getEntity());
			if (player != null) {
				double experienceLoss = Utils.calculateExpLoss(player);
				player.reducePlayerNormalExperience(experienceLoss);
				player.dropResurrectionItem((int) experienceLoss);
			}
		} catch (CoreStateInitException e) {

		}
	}

	@EventHandler
	public void onInventoryClose(InventoryCloseEvent event) {
		if (Utils.isInventoryMerchant(event.getInventory())) {
			onMerchantInventoryClose(event);
			return;
		}
	}

	@EventHandler
	public void onInventoryDrag(InventoryDragEvent event) {
		// More hassle than it is worth, cancel it always
		if (Utils.isInventoryMerchant(event.getInventory())) {
			Utils.CancelEvent(event);
			;
			return;
		}
		
	}

	@EventHandler
	public void onInventoryClick(InventoryClickEvent event) {
		// more trouble than its worth
		if (event.getClick().equals(ClickType.NUMBER_KEY))
		{
			Utils.CancelEvent(event);
			return;
		}
		

		if (Utils.isInventoryMerchant(event.getInventory())) {
			onMerchantInventoryClick(event);
			return;
		}

		if (event.isCancelled())
			return;

		try {
			if (StateManager.getInstance().getPlayerManager()
					.getApplyingAugmentation(event.getView().getPlayer().getUniqueId()) != null
					&& StateManager.getInstance().getPlayerManager()
							.getApplyingAugmentation(event.getView().getPlayer().getUniqueId()) > 0) {
				event.getView().getPlayer().sendMessage("* Attempting to apply augmentation");
				ItemStack targetItemStack = event.getCurrentItem();
				ISoliniaItem sourceAugSoliniaItem = StateManager.getInstance().getConfigurationManager()
						.getItem(StateManager.getInstance().getPlayerManager()
								.getApplyingAugmentation(event.getView().getPlayer().getUniqueId()));

				if (!sourceAugSoliniaItem.isAugmentation()) {
					event.getView().getPlayer()
							.sendMessage("The item you are attempting to apply from is not an augmentation");
					StateManager.getInstance().getPlayerManager()
							.setApplyingAugmentation(event.getView().getPlayer().getUniqueId(), 0);
					event.getView().getPlayer().sendMessage("* Ended applying Augmentation");
					Utils.CancelEvent(event);
					return;
				}

				if (!Utils.IsSoliniaItem(targetItemStack)
						|| targetItemStack.getType().equals(Material.ENCHANTED_BOOK)) {
					event.getView().getPlayer().sendMessage("This augmentation cannot be applied to this item type");
					StateManager.getInstance().getPlayerManager()
							.setApplyingAugmentation(event.getView().getPlayer().getUniqueId(), 0);
					event.getView().getPlayer().sendMessage("* Ended applying Augmentation");
					Utils.CancelEvent(event);
					return;
				}

				if (targetItemStack.getAmount() != 1) {
					event.getView().getPlayer().sendMessage(
							"You cannot apply an augmentation to multiple items at once, please seperate the target item");
					StateManager.getInstance().getPlayerManager()
							.setApplyingAugmentation(event.getView().getPlayer().getUniqueId(), 0);
					System.out.println("Ended applying augmentation");
					event.getView().getPlayer().sendMessage("* Ended applying Augmentation");
					Utils.CancelEvent(event);
					return;
				}

				if (ItemStackUtils.getAugmentationItemId(targetItemStack) != null
						&& ItemStackUtils.getAugmentationItemId(targetItemStack) != 0) {
					event.getView().getPlayer().sendMessage("This item already has an augmentation applied");
					StateManager.getInstance().getPlayerManager()
							.setApplyingAugmentation(event.getView().getPlayer().getUniqueId(), 0);
					System.out.println("Ended applying augmentation");
					event.getView().getPlayer().sendMessage("* Ended applying Augmentation");
					Utils.CancelEvent(event);
					return;
				}

				ISoliniaItem targetSoliniaItem = StateManager.getInstance().getConfigurationManager()
						.getItem(targetItemStack);
				if (!targetSoliniaItem.getAcceptsAugmentationSlotType()
						.equals(sourceAugSoliniaItem.getAugmentationFitsSlotType())) {
					event.getView().getPlayer().sendMessage("This augmentation does not fit in this items slot type");
					StateManager.getInstance().getPlayerManager()
							.setApplyingAugmentation(event.getView().getPlayer().getUniqueId(), 0);
					System.out.println("Ended applying augmentation");
					event.getView().getPlayer().sendMessage("* Ended applying Augmentation");
					Utils.CancelEvent(event);
					return;
				}

				if (Utils.getPlayerTotalCountOfItemId(((Player) event.getView().getPlayer()),
						sourceAugSoliniaItem.getId()) < 1) {
					event.getView().getPlayer().sendMessage(
							"You do not have enough of this augmentation in your inventory to apply it to an item");
					StateManager.getInstance().getPlayerManager()
							.setApplyingAugmentation(event.getView().getPlayer().getUniqueId(), 0);
					System.out.println("Ended applying augmentation");
					event.getView().getPlayer().sendMessage("* Ended applying Augmentation");
					Utils.CancelEvent(event);
					return;
				}

				targetItemStack = ItemStackUtils.applyAugmentation(targetSoliniaItem, targetItemStack, sourceAugSoliniaItem.getId());
				((Player) event.getView().getPlayer()).getWorld()
						.dropItemNaturally(((Player) event.getView().getPlayer()).getLocation(), targetItemStack);
				((Player) event.getView().getPlayer()).getInventory().setItem(event.getSlot(), null);
				((Player) event.getView().getPlayer()).updateInventory();
				Utils.removeItemsFromInventory(((Player) event.getView().getPlayer()), sourceAugSoliniaItem.getId(), 1);

				event.getView().getPlayer().sendMessage("Augmentation Applied to Item Successfully");
				StateManager.getInstance().getPlayerManager()
						.setApplyingAugmentation(event.getView().getPlayer().getUniqueId(), 0);
				System.out.println("Ended applying augmentation");
				event.getView().getPlayer().sendMessage("* Ended applying Augmentation");
				Utils.CancelEvent(event);

				return;
			}

			ISoliniaPlayer solplayer = SoliniaPlayerAdapter.Adapt((Player) event.getView().getPlayer());

			// REMOVING EVENTS
			
			if (event.getSlotType().equals(SlotType.ARMOR) || event.getSlot() == 40) {
				ItemStack item = event.getWhoClicked().getInventory().getItem(event.getSlot());
				if (item != null && !item.getType().equals(Material.AIR)) {
					if (Utils.IsSoliniaItem(item)) {
						ISoliniaItem soliniaitem = StateManager.getInstance().getConfigurationManager().getItem(item);
						if (soliniaitem.getHp() > 0 || soliniaitem.getStamina() > 0) {
							solplayer.scheduleUpdateMaxHp();
						}
					}
				}
			}

			// ADDING EVENTS

			// If armour slot modified, update MaxHP
			// Shift clicking
			if (event.isShiftClick()) {
				ItemStack itemstack = event.getCurrentItem();
				if (itemstack == null)
					return;
				if (Utils.IsSoliniaItem(itemstack) && !itemstack.getType().equals(Material.ENCHANTED_BOOK)) {
					ISoliniaItem soliniaitem = StateManager.getInstance().getConfigurationManager().getItem(itemstack);
					
					if (soliniaitem.getMinLevel() > solplayer.getLevel()) {
						Utils.CancelEvent(event);
						event.getView().getPlayer()
								.sendMessage(ChatColor.GRAY + "Your are not sufficient level wear this armour");
						return;
					}
					
					if (soliniaitem.getAllowedClassNames().size() == 0)
					{
						solplayer.scheduleUpdateMaxHp();
						return;
					}

					if (solplayer.getClassObj() == null) {
						Utils.CancelEvent(event);
						event.getView().getPlayer().sendMessage(ChatColor.GRAY + "Your class cannot wear this armour");
						return;
					}

					if (!soliniaitem.getAllowedClassNames().contains(solplayer.getClassObj().getName().toUpperCase())) {
						Utils.CancelEvent(event);
						event.getView().getPlayer().sendMessage(ChatColor.GRAY + "Your class cannot wear this armour");
						return;
					}

					solplayer.scheduleUpdateMaxHp();
				}
			}

			// Actual clicking
			if (event.getSlotType().equals(SlotType.ARMOR)) {
				ItemStack itemstack = event.getCursor();
				if (itemstack == null)
					return;

				if (Utils.IsSoliniaItem(itemstack) && !itemstack.getType().equals(Material.ENCHANTED_BOOK)) {
					ISoliniaItem soliniaitem = StateManager.getInstance().getConfigurationManager().getItem(itemstack);

					if (soliniaitem.getMinLevel() > solplayer.getLevel()) {
						Utils.CancelEvent(event);
						;
						event.getView().getPlayer()
								.sendMessage(ChatColor.GRAY + "Your are not sufficient level wear this armour");
						return;
					}

					if (soliniaitem.getAllowedClassNames().size() == 0)
					{
						solplayer.scheduleUpdateMaxHp();
						return;
					}

					if (solplayer.getClassObj() == null) {
						Utils.CancelEvent(event);
						;
						event.getView().getPlayer().sendMessage(ChatColor.GRAY + "Your class cannot wear this armour");
						return;
					}

					if (!soliniaitem.getAllowedClassNames().contains(solplayer.getClassObj().getName().toUpperCase())) {
						Utils.CancelEvent(event);
						;
						event.getView().getPlayer().sendMessage(ChatColor.GRAY + "Your class cannot wear this armour");
						return;
					}

					solplayer.scheduleUpdateMaxHp();
				}
			}

			// shield changes to slotid 40
			if (event.getSlot() == 40) {
				ItemStack itemstack = event.getCursor();
				if (itemstack == null)
					return;
				if (Utils.IsSoliniaItem(itemstack) && !itemstack.getType().equals(Material.ENCHANTED_BOOK)) {
					ISoliniaItem soliniaitem = StateManager.getInstance().getConfigurationManager().getItem(itemstack);
					
					if (soliniaitem.getMinLevel() > solplayer.getLevel()) {
						Utils.CancelEvent(event);
						;
						event.getView().getPlayer()
								.sendMessage(ChatColor.GRAY + "Your are not sufficient level wear this armour");
						return;
					}
					
					if (soliniaitem.getAllowedClassNames().size() == 0)
					{
						solplayer.scheduleUpdateMaxHp();
						return;
					}

					if (solplayer.getClassObj() == null) {
						Utils.CancelEvent(event);
						;
						event.getView().getPlayer().sendMessage(ChatColor.GRAY + "Your class cannot wear this armour");
						return;
					}

					if (!soliniaitem.getAllowedClassNames().contains(solplayer.getClassObj().getName().toUpperCase())) {
						Utils.CancelEvent(event);
						;
						event.getView().getPlayer().sendMessage(ChatColor.GRAY + "Your class cannot wear this armour");
						return;
					}

					solplayer.scheduleUpdateMaxHp();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private void onMerchantInventoryClose(InventoryCloseEvent event) {

	}

	@EventHandler
	public void onDropItemEvent(PlayerDropItemEvent event) {

		// This is to stop drops after closing shop
		if (Utils.IsSoliniaItem(event.getItemDrop().getItemStack()))
			if (event.getItemDrop().getItemStack().getItemMeta().getDisplayName().startsWith("Display Item: ")) {
				event.getItemDrop().getItemStack().setAmount(0);
			}
	}

	private void onMerchantInventoryClick(InventoryClickEvent event) {
		UniversalMerchant universalmerchant = null;
		int page = 0;
		int nextpage = 0;
		try {
			universalmerchant = StateManager.getInstance().getEntityManager()
					.getUniversalMerchant(Utils.getInventoryUniversalMerchant(event.getInventory()));
			page = Utils.getInventoryPage(event.getInventory());
			nextpage = Utils.getInventoryPage(event.getInventory());
		} catch (Exception e) {
			event.getView().getPlayer().sendMessage(e.getMessage());
			e.printStackTrace();
			Utils.CancelEvent(event);
			;
			return;
		}

		if (universalmerchant == null) {
			event.getView().getPlayer().sendMessage("Could not find universal mercahnt or page " + page);
			System.out.println("Could not find universal mercahnt or page " + page);
			Utils.CancelEvent(event);
			;
			return;
		}

		if (event.getRawSlot() < 0) {
			Utils.CancelEvent(event);
			;
			return;
		}

		if (event.getCursor() == null || event.getCursor().getType().equals(Material.AIR)) {
			if (event.getRawSlot() > 26) {

				try {
					ISoliniaItem item = StateManager.getInstance().getConfigurationManager()
							.getItem(event.getCurrentItem());
					if (item == null) {
						event.getView().getPlayer().sendMessage("Merchants are not interested in this item");
						Utils.CancelEvent(event);
						return;
					}

					if (item.isTemporary()) {
						event.getView().getPlayer().sendMessage("Merchants are not interested in temporary items");
						Utils.CancelEvent(event);
						return;
					}

					// Picked up sellable item

				} catch (CoreStateInitException e) {
					Utils.CancelEvent(event);
					event.getView().getPlayer().sendMessage("Cannot sell/buy right now");
					return;
				}

				return;
			} else {
				ItemStack pickingUpItem = event.getCurrentItem();
				if (pickingUpItem.getType().equals(Material.BARRIER)) {
					// event.getView().getPlayer().sendMessage("Ignoring barrier");
					Utils.CancelEvent(event);
					return;
				}

				// Do not allow movement of UI movers
				if (event.getRawSlot() == 18 || event.getRawSlot() == 26) {
					// event.getView().getPlayer().sendMessage("Moving Left or Right");
					if (event.getRawSlot() == 18) {
						if ((page - 1) > 0) {
							event.getView().getPlayer().closeInventory();
							universalmerchant.sendMerchantItemListToPlayer((Player) event.getView().getPlayer(),
									page - 1);
						}
					}

					if (event.getRawSlot() == 26) {
						if (nextpage != 0) {
							event.getView().getPlayer().closeInventory();
							universalmerchant.sendMerchantItemListToPlayer((Player) event.getView().getPlayer(),
									nextpage + 1);
						}
					}
					Utils.CancelEvent(event);
					return;
				}

				// Do not allow movement of identifiers
				if (event.getRawSlot() == 19) {
					// event.getView().getPlayer().sendMessage("Ignoring identifier block");
					Utils.CancelEvent(event);
					return;
				}

				// Picking up merchant item
				// event.getView().getPlayer().sendMessage("Picking up merchant item");
				ItemStack buyStack = event.getCurrentItem().clone();
				if (event.isRightClick())
				{
					buyStack.setAmount(64);
				}
				
				// Cursor events are deprecated, must be done next tick before a cancel
				final UUID uuid = event.getView().getPlayer().getUniqueId();
				final ItemStack cursorstack = buyStack;
				Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(
						Bukkit.getPluginManager().getPlugin("Solinia3Core"), new Runnable() {
							public void run() {
								Bukkit.getPlayer(uuid).setItemOnCursor(cursorstack);
								}
						}
				);

				Utils.CancelEvent(event);
				return;
			}

		}

		if (event.getCursor() != null && !event.getCursor().getType().equals(Material.AIR)) {
			// Clicking item in cursor onto a slot
			if (event.getRawSlot() > 26) {
				// Dropping own item or buying
				if (event.getCursor().getItemMeta() != null &&  event.getCursor().getItemMeta().getDisplayName().startsWith("Display Item: ")) {
					// Buying
					// event.getView().getPlayer().sendMessage("Buying item");

					try {
						if (!event.getCurrentItem().getType().equals(Material.AIR)) {
							event.getView().getPlayer()
									.sendMessage("You must place the item you wish to buy on an empty slot");
							// Cursor events are deprecated, must be done next tick before a cancel
							final UUID uuid = event.getView().getPlayer().getUniqueId();
							Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(
									Bukkit.getPluginManager().getPlugin("Solinia3Core"), new Runnable() {
										public void run() {
											Bukkit.getPlayer(uuid).setItemOnCursor(new ItemStack(Material.AIR));
											}
									}
							);
							Utils.CancelEvent(event);
							return;
						}

						ISoliniaItem item = StateManager.getInstance().getConfigurationManager()
								.getItem(event.getCursor());
						long individualprice = item.getWorth();

						// Try to read from the itemstack worth
						Integer merchantworth = ItemStackUtils.getMerchantItemWorth(event.getCursor());

						if (event.getCursor().getItemMeta().getDisplayName().contains("Display Item"))
							if (merchantworth != null && merchantworth != individualprice) {
								individualprice = merchantworth;
							}

						// Total price

						long price = individualprice * event.getCursor().getAmount();

						if (price > StateManager.getInstance().getEconomy()
								.getBalance((Player) event.getView().getPlayer())) {
							event.getView().getPlayer().sendMessage(
									"You do not have sufficient balance to buy this item in that quantity.");
							
							// Cursor events are deprecated, must be done next tick before a cancel
							final UUID uuid = event.getView().getPlayer().getUniqueId();
							Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(
									Bukkit.getPluginManager().getPlugin("Solinia3Core"), new Runnable() {
										public void run() {
											Bukkit.getPlayer(uuid).setItemOnCursor(new ItemStack(Material.AIR));
											}
									}
							);
							Utils.CancelEvent(event);
							return;
						}

						EconomyResponse responsewithdraw = StateManager.getInstance().getEconomy().withdrawPlayer(
								Bukkit.getOfflinePlayer(((Player) event.getView().getPlayer()).getUniqueId()), price);
						if (responsewithdraw.transactionSuccess()) {
							ItemStack purchase = item.asItemStack();
							purchase.setAmount(event.getCursor().getAmount());
							// Cursor events are deprecated, must be done next tick before a cancel
							final UUID uuid = event.getView().getPlayer().getUniqueId();
							Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(
									Bukkit.getPluginManager().getPlugin("Solinia3Core"), new Runnable() {
										public void run() {
											Bukkit.getPlayer(uuid).setItemOnCursor(new ItemStack(Material.AIR));
											}
									}
							);
							Utils.CancelEvent(event);
							;
							event.getClickedInventory().setItem(event.getSlot(), purchase);
							event.getView().getPlayer().sendMessage(ChatColor.YELLOW + "* You pay $" + price + " for "
									+ event.getCursor().getAmount() + " " + item.getDisplayname());
							return;

						} else {
							System.out.println("Error withdrawing money from your account "
									+ String.format(responsewithdraw.errorMessage));
							event.getView().getPlayer()
									.sendMessage(ChatColor.YELLOW + "* Error withdrawing money from your account "
											+ String.format(responsewithdraw.errorMessage));

							// Cursor events are deprecated, must be done next tick before a cancel
							final UUID uuid = event.getView().getPlayer().getUniqueId();
							Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(
									Bukkit.getPluginManager().getPlugin("Solinia3Core"), new Runnable() {
										public void run() {
											Bukkit.getPlayer(uuid).setItemOnCursor(new ItemStack(Material.AIR));
											}
									}
							);
							Utils.CancelEvent(event);
							return;
						}
					} catch (CoreStateInitException e) {
						event.getView().getPlayer().sendMessage("Cannot buy items from the merchant right now");
						// Cursor events are deprecated, must be done next tick before a cancel
						final UUID uuid = event.getView().getPlayer().getUniqueId();
						Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(
								Bukkit.getPluginManager().getPlugin("Solinia3Core"), new Runnable() {
									public void run() {
										Bukkit.getPlayer(uuid).setItemOnCursor(new ItemStack(Material.AIR));
										}
								}
						);
						Utils.CancelEvent(event);
						return;
					}
				} else {
					// Dropping own item
					return;
				}

			} else {
				// Selling items or dropping item back
				if (event.getCursor().getItemMeta() != null && event.getCursor().getItemMeta().getDisplayName().startsWith("Display Item: ")) {
					// Returning store item
					// Cursor events are deprecated, must be done next tick before a cancel
					final UUID uuid = event.getView().getPlayer().getUniqueId();
					Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(
							Bukkit.getPluginManager().getPlugin("Solinia3Core"), new Runnable() {
								public void run() {
									Bukkit.getPlayer(uuid).setItemOnCursor(new ItemStack(Material.AIR));
									}
							}
					);
					Utils.CancelEvent(event);
					return;

				} else {
					// Selling
					// event.getView().getPlayer().sendMessage("Selling item to merchant");

					try {
						ISoliniaItem item = StateManager.getInstance().getConfigurationManager()
								.getItem(event.getCursor());
						long individualprice = item.getWorth();

						// Total price
						long price = individualprice * event.getCursor().getAmount();
						
						final UUID finaluuid = event.getView().getPlayer().getUniqueId();
						final long finalprice = price;
						Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(
								Bukkit.getPluginManager().getPlugin("Solinia3Core"), new Runnable() {
									public void run() {
											if (Bukkit.getPlayer(finaluuid).getItemOnCursor() == null)
											{
												return;
											}
											
											if (Bukkit.getPlayer(finaluuid).getItemOnCursor().getType().equals(Material.AIR))
											{
												return;
											}
											
											Bukkit.getPlayer(finaluuid).setItemOnCursor(new ItemStack(Material.AIR));
											
											
											EconomyResponse responsedeposit = StateManager.getInstance().getEconomy()
													.depositPlayer((Player) event.getView().getPlayer(), finalprice);
											if (responsedeposit.transactionSuccess()) {
												// Add to buy back list
												// StateManager.getInstance().getEntityManager().addTemporaryMerchantItem(npc.getId(),
												// item.getId(), event.getCursor().getAmount());
												event.getView().getPlayer()
														.sendMessage(ChatColor.YELLOW + "* You recieve $" + finalprice + " as payment");
												// Cursor events are deprecated, must be done next tick before a cancel
												
											} else {
												System.out.println("Error depositing money to users account "
														+ String.format(responsedeposit.errorMessage));
												event.getView().getPlayer()
														.sendMessage(ChatColor.YELLOW + "* Error depositing money to your account "
																+ String.format(responsedeposit.errorMessage));
											}
										}
								}
						);
						Utils.CancelEvent(event);
						return;

						
					} catch (CoreStateInitException e) {
						event.getView().getPlayer().sendMessage("Cannot sell item to merchant right now");
						Utils.CancelEvent(event);
						return;
					}
				}
			}
		}

		event.getView().getPlayer().sendMessage("Please alert an admin of this message code: GMMI1");
		Utils.CancelEvent(event);
		return;
	}

	@EventHandler
	public void onPlayerRespawn(PlayerRespawnEvent event) {
		try {
			ISoliniaPlayer solplayer = SoliniaPlayerAdapter.Adapt(event.getPlayer());
			if (solplayer != null) {
				
				try
			    {
					LivingEntity pet = StateManager.getInstance().getEntityManager().getPet(event.getPlayer().getUniqueId());
					if (pet != null) {
						ISoliniaLivingEntity petsolEntity = SoliniaLivingEntityAdapter.Adapt(pet);
							StateManager.getInstance().getEntityManager().removePet(event.getPlayer().getUniqueId(), !petsolEntity.isCharmed());
					}
			    } catch (CoreStateInitException e)
				{
			    	
				}
				
				
				
				solplayer.updateMaxHp();
				if (solplayer.getBindPoint() != null && !solplayer.getBindPoint().equals("")) {
					String[] loc = solplayer.getBindPoint().split(",");

					Location location = new Location(Bukkit.getWorld(loc[0]), Double.parseDouble(loc[1]),
							Double.parseDouble(loc[2]), Double.parseDouble(loc[3]));

					event.setRespawnLocation(location);
					event.getPlayer().teleport(location);
				}
				
				ISoliniaPlayer solPlayer = SoliniaPlayerAdapter.Adapt(event.getPlayer());
				if (solPlayer != null)
					solPlayer.setLastLocation(event.getPlayer().getLocation());
			}
		} catch (CoreStateInitException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		
		// disable knockback effects
		try
	    {
	      Player player = event.getPlayer();
	      EntityHuman entityHuman = ((CraftPlayer)player).getHandle();
	      entityHuman.getAttributeInstance(GenericAttributes.c).setValue(1.0D);
	    }
	    catch (Exception ex)
	    {
	      ex.printStackTrace();
	    }
		
		SoliniaPlayerJoinEvent soliniaevent;
		try {
			ISoliniaPlayer solplayer = SoliniaPlayerAdapter.Adapt(event.getPlayer());

			soliniaevent = new SoliniaPlayerJoinEvent(event, solplayer);
			solplayer.updateDisplayName();
			solplayer.updateMaxHp();
			Bukkit.getPluginManager().callEvent(soliniaevent);

			// patch
			if (solplayer.getClassObj() != null)
				solplayer.setChosenClass(true);
			else
				solplayer.setChosenClass(false);

			// patch
			if (solplayer.getRace() != null)
				solplayer.setChosenRace(true);
			else
				solplayer.setChosenRace(false);

			if (solplayer.getWorld().getPlayerIpNameMappings().get(event.getPlayer().getAddress().getAddress().toString()) == null)
			{
				solplayer.getWorld().getPlayerIpNameMappings().put(event.getPlayer().getAddress().getAddress().toString(), new ArrayList<String>());
			}
			
			String players = "";
			try
			{
				if (!solplayer.getWorld().getPlayerIpNameMappings().get(event.getPlayer().getAddress().getAddress().toString()).contains(event.getPlayer().getUniqueId().toString()))
					solplayer.getWorld().getPlayerIpNameMappings().get(event.getPlayer().getAddress().getAddress().toString()).add(event.getPlayer().getUniqueId().toString());
				
				ArrayList<String> playerUuids = solplayer.getWorld().getPlayerIpNameMappings().get(event.getPlayer().getAddress().getAddress().toString());
				
				
				for(String playerUuid : playerUuids)
				{
					try
					{
						players += Bukkit.getOfflinePlayer(UUID.fromString(playerUuid)).getName() + " ";
					} catch (Exception e)
					{
						
					}
				}
			
			} catch (Exception e)
			{
				// not vital if this fails
			}
			
			if (!event.getPlayer().hasPermission("essentials.silentjoin"))
				StateManager.getInstance().getChannelManager().sendToDiscordMC(solplayer,StateManager.getInstance().getChannelManager().getDiscordMainChannelId(),event.getPlayer().getName() + "(" + solplayer.getFullName() + ") has joined the game (" + players.trim() + ")");

			try
		    {
				LivingEntity pet = StateManager.getInstance().getEntityManager().getPet(event.getPlayer().getUniqueId());
				if (pet != null) {
					ISoliniaLivingEntity petsolEntity = SoliniaLivingEntityAdapter.Adapt(pet);
					StateManager.getInstance().getEntityManager().removePet(event.getPlayer().getUniqueId(), !petsolEntity.isCharmed());
				}
		    } catch (CoreStateInitException e)
			{
		    	
			}

		} catch (CoreStateInitException e) {
			event.getPlayer().kickPlayer("Server initialising");
		}
	}

	@EventHandler
	public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
		if (event.isCancelled())
			return;
		
	}

	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event) 
	{
		// Handle changing armour
		if ((event.getHand() == EquipmentSlot.HAND || event.getHand() == EquipmentSlot.OFF_HAND)
				&& (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK)) {
			try {
				Utils.checkArmourEquip(SoliniaPlayerAdapter.Adapt(event.getPlayer()), event);
			} catch (CoreStateInitException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		// we only care about main hand interactions
		if (event.getHand() != null)
		if (!event.getHand().equals(EquipmentSlot.HAND))
			return;
		
		HandleHandInteraction(event);
	}

	private boolean tryRightClickAutoAttack(Player player, ItemStack itemStackInHand)
	{
		try
		{
			// check if player is toggling auto attack
			// left click while sneaking
			if (player.isSneaking() && 
					(itemStackInHand.getType().name().equals("BOW") || ConfigurationManager.WeaponMaterials.contains(itemStackInHand.getType().name()))) 
			{
				ISoliniaPlayer soliniaPlayer = SoliniaPlayerAdapter.Adapt(player);
				if (soliniaPlayer != null)
				{
					soliniaPlayer.toggleAutoAttack();
					return true;
				}
			}
		} catch (CoreStateInitException e)
		{
			
		}
		
		return false;
	}
	
	private boolean tryLeftClickTarget(Player player)
	{
		try
		{
			// also allow targetting via pet control rod
			if (player.isSneaking())
			{
					LivingEntity targetmob = Utils.getTargettedLivingEntity(player, 50);
					
					StateManager.getInstance().getEntityManager().setEntityTarget(player,
							targetmob);
					return true;
				
			}
		} catch (CoreStateInitException e)
		{
			
		}
		return false;
	}
	
	private void HandleHandInteraction(PlayerInteractEvent event) {
		if (event.getItem() != null && (event.getAction().equals(Action.RIGHT_CLICK_AIR) || event.getAction().equals(Action.RIGHT_CLICK_BLOCK)))
			if (tryRightClickAutoAttack(event.getPlayer(), event.getItem()))
			{
				Utils.CancelEvent(event);
				return;
			}
			
		if ((event.getAction().equals(Action.LEFT_CLICK_AIR) || event.getAction().equals(Action.LEFT_CLICK_BLOCK)))
		if (tryLeftClickTarget(event.getPlayer()))
		{
			Utils.CancelEvent(event);
			return;
		}
		
		// Right click air is a cancelled event so we have to ignore it when checking
		// iscancelled
		// We need it for spells
		if (event.getAction() != Action.RIGHT_CLICK_AIR) {
			if (event.isCancelled())
				return;
		}
		
		// cancel feigened if targetting
		try
		{
			boolean feigned = StateManager.getInstance().getEntityManager().isFeignedDeath(event.getPlayer().getUniqueId());
			if (feigned == true)
			{
				StateManager.getInstance().getEntityManager().setFeignedDeath(event.getPlayer().getUniqueId(), false);
			}
		} catch (CoreStateInitException e)
		{
					
		}
		
		if (Utils.isMezzed((LivingEntity) event.getPlayer()))
		{
			Utils.CancelEvent(event);
			return;
		}

		if (Utils.isStunned((LivingEntity) event.getPlayer()))
		{
			Utils.CancelEvent(event);
			return;
		}

		try {
			SoliniaPlayerAdapter.Adapt(event.getPlayer()).interact(event);
		} catch (CoreStateInitException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@EventHandler
	public void onPlayerConsumeEvent(PlayerItemConsumeEvent event) {
		if (event.isCancelled())
			return;

		try {
			ISoliniaItem item = SoliniaItemAdapter.Adapt(event.getItem());
			item.consume(plugin, event.getPlayer());
		} catch (SoliniaItemException | CoreStateInitException e) {

		}
	}

	@EventHandler
	public void onChat(AsyncPlayerChatEvent event) {
		if (event.isCancelled())
			return;
		
		final UUID playerUuid = event.getPlayer().getUniqueId();
		final String message = event.getMessage();
		
		// We control all chat!
		AsyncPlayerChatEvent rawEvent = (AsyncPlayerChatEvent)event;
		Utils.CancelEvent(rawEvent);
		
		Bukkit.getScheduler().runTask(plugin, new Runnable() {
		    @Override
		    public void run() {
		    	SoliniaSyncPlayerChatEvent soliniaevent;
				try {
					Entity playerEntity = Bukkit.getEntity(playerUuid);
					if (playerEntity == null)
						return;
					
					if (!(playerEntity instanceof Player))
						return;
					
					ISoliniaPlayer solplayer = SoliniaPlayerAdapter.Adapt((Player)playerEntity);

					if (solplayer.getLanguage() == null || solplayer.getLanguage().equals("UNKNOWN")) {
						if (solplayer.getRace() != null) {
							solplayer.setLanguage(solplayer.getRace().getName().toUpperCase());
						}
					}

					soliniaevent = new SoliniaSyncPlayerChatEvent(solplayer, message);
					if (soliniaevent != null && Bukkit.getPluginManager() != null)
						Bukkit.getPluginManager().callEvent(soliniaevent);
				} catch (CoreStateInitException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		    }
		});
	}

}
