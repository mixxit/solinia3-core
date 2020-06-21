package com.solinia.solinia.Utils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.solinia.solinia.Adapters.SoliniaPlayerAdapter;
import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Interfaces.ISoliniaClass;
import com.solinia.solinia.Interfaces.ISoliniaGod;
import com.solinia.solinia.Interfaces.ISoliniaItem;
import com.solinia.solinia.Interfaces.ISoliniaPlayer;
import com.solinia.solinia.Interfaces.ISoliniaRace;
import com.solinia.solinia.Managers.StateManager;
import com.solinia.solinia.Models.HINT;
import com.solinia.solinia.Models.HintSetting;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

public class ChatUtils {
	public static void sendValidFields(CommandSender sender, Class classType) {
		List<String> fields = new ArrayList<String>();
		
		try {
			for(Field field : classType.getFields())
			{
				field.setAccessible(true);
				fields.add(field.getName());
			}
			
		} catch (SecurityException | IllegalArgumentException e) {
			// its fine just ignore it
		}
		
		String str = String.join(" ", fields);
		sendStringToSenderInBlocksOfSize(sender,str,256);
		
	}
	
	
	
	private static <T> String getFieldValue(T object, String field1, String field2, Class classType) {
		String matchedValue = getFieldValue(object,field1,classType);
		
		if ((field2 != null && !field2.equals("")) && (matchedValue.equals("")))
		{
			try {
				Field f = classType.getDeclaredField(field2);
				f.setAccessible(true);
				matchedValue = f.get(object).toString(); 
			} catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) 
			{
				
			}
		}
		
		if (matchedValue == null)
			matchedValue = "";
		
		return matchedValue;
	}
	
	private static <T> String getFieldValue(T object, String field1, Class classType) {
		String matchedValue = "";
		try {
			Field f = classType.getDeclaredField(field1);
			f.setAccessible(true);
			matchedValue = f.get(object).toString(); 
		} catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) 
		{
			
		}
		
		if (matchedValue == null)
			matchedValue = "";
		
		return matchedValue;
	}
	
	public static void sendStringToSenderInBlocksOfSize(CommandSender sender, String message, int blocksize)
	{
		sender.sendMessage(TextUtils.splitStringIntoBlocksOfSize(message,blocksize));
	}
	
	
	public static <T> void sendFilterByCriteria(List<T> dataSet, CommandSender sender, String[] args, Class classType)
	{
		int found = 0;
		if (args.length < 3)
		{
			sendValidFields(sender,classType);			
			sender.sendMessage("Criteria must include a search term and value - ie .criteria fieldname fieldvalue - See above for valid fields");
		} else {
			String field = args[1];
			String value = args[2];
			
			try {
				Field f = classType.getDeclaredField(field);
				f.setAccessible(true);
				
				for(T object : dataSet)
				{
					String matchedValue = f.get(object).toString(); 
					
					if (!matchedValue.toLowerCase().equals(value.toLowerCase()))
						continue;
					
					found++;
					String itemmessage = getFieldValue(object,"id","",classType) + " - " + getFieldValue(object,"displayname","name",classType);
					sender.sendMessage(itemmessage);
				}
				
			} catch (NoSuchFieldException e) {
				sendValidFields(sender,classType);
				sender.sendMessage("Object could not be located by search criteria (field not found) See above for list of valid fields");
			} catch (SecurityException e) {
				sender.sendMessage("Object could not be located by search criteria (field is private)");
			} catch (IllegalArgumentException e) {
				sender.sendMessage("Object could not be located by search criteria (argument issue)");
			} catch (IllegalAccessException e) {
				sender.sendMessage("Object could not be located by search criteria (access issue)");
			}
			
			if (found == 0)
			{
				sender.sendMessage("Object could not be located by search criteria (no matches)");
			}
			
		}
	}

	
	public static void sendGodInfo(CommandSender sender) throws CoreStateInitException {
		for (ISoliniaGod entry : StateManager.getInstance().getConfigurationManager().getGods()) {
			TextComponent tc = new TextComponent();
			tc.setText(ChatColor.RED + "~ GOD: " + ChatColor.GOLD + entry.getName().toUpperCase() + ChatColor.GRAY
					+ " [" + entry.getId() + "] - " + ChatColor.RESET);
			TextComponent tc2 = new TextComponent();
			tc2.setText("Hover for more details");
			String details = ChatColor.GOLD + entry.getName() + ChatColor.RESET + System.lineSeparator() + "Recommended Alignment: "
					+ ChatColor.GOLD + entry.getAlignment() + ChatColor.RESET + System.lineSeparator() + entry.getDescription();
			tc2.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(details).create()));
			tc.addExtra(tc2);
			sender.spigot().sendMessage(tc);
		}
	}
	
	public static void sendRaceInfo(CommandSender sender) throws CoreStateInitException {
		List<ISoliniaClass> classes = StateManager.getInstance().getConfigurationManager().getClasses();
		for (ISoliniaRace race : StateManager.getInstance().getConfigurationManager().getRaces()) {
			if (race.isAdmin())
				continue;

			String classBuilder = "";
			for (ISoliniaClass solclass : classes) {
				if (solclass.getValidRaceClasses().containsKey(race.getId()))
					classBuilder += solclass.getName() + " ";
			}

			TextComponent tc = new TextComponent();
			tc.setText(ChatColor.RED + "~ RACE: " + ChatColor.GOLD + race.getName().toUpperCase() + ChatColor.GRAY
					+ " [" + race.getId() + "] - " + ChatColor.RESET);
			TextComponent tc2 = new TextComponent();
			tc2.setText("Hover for more details");
			String details = ChatColor.GOLD + race.getName() + ChatColor.RESET + System.lineSeparator() + "Recommended Alignment: "
					+ ChatColor.GOLD + race.getAlignment() + ChatColor.RESET + System.lineSeparator() + race.getDescription() + System.lineSeparator() + "STR: "
					+ ChatColor.GOLD + race.getStrength() + ChatColor.RESET + " STA: " + ChatColor.GOLD
					+ race.getStamina() + ChatColor.RESET + " AGI: " + ChatColor.GOLD + race.getAgility()
					+ ChatColor.RESET + " DEX: " + ChatColor.GOLD + race.getDexterity() + ChatColor.RESET + " INT: "
					+ ChatColor.GOLD + race.getIntelligence() + ChatColor.RESET + " WIS: " + ChatColor.GOLD
					+ race.getWisdom() + ChatColor.RESET + " CHA: " + ChatColor.GOLD + race.getCharisma()
					+ ChatColor.GOLD + System.lineSeparator() + " Classes: " + ChatColor.RESET + classBuilder;
			tc2.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(details).create()));
			tc.addExtra(tc2);
			sender.spigot().sendMessage(tc);
		}
	}

	
	public static void SendHintToServer(HINT hint, String referenceCode) {
		for(World world : Bukkit.getWorlds())
		{
			for (Player player : world.getPlayers())
			{
				ChatUtils.SendHint(player, hint, referenceCode, false);
			}
		}
		System.out.println("ANNOUNCE:" + hint.name() + ":"+ referenceCode);
	}
	
	public static void SendHint(LivingEntity entity, HINT hint, String referenceCode, boolean sendNearby)
	{
		SendHint(entity, hint, referenceCode, sendNearby, null);
	}

	public static void SendHint(LivingEntity entity, HINT hint, String referenceCode, boolean sendNearby, ItemStack itemStack) {
		if (entity == null)
			return;
		
		String channelCode = ""+hint.ordinal();
	
		String message = "";
		boolean showItemLinks = false;
		switch (hint)
		{
		case NPC_RAMPAGE:
			message = "* " + entity.getCustomName() +  " goes on a RAMPAGE!";
			break;
		case PET_FLURRY:
			message = "* " + entity.getCustomName() +  " unleashes a flurry of attacks!";
			break;
		case NPC_FLURRY:
			message = "* " + entity.getCustomName() +  " unleashes a flurry of attacks!";
			break;
		case YOU_FLURRY:
			message = "* You unleash a flurry of attacks";
			break;
		case SKILLUP:
			String[] skill = referenceCode.split("\\^");
			message = ChatColor.YELLOW + "* You get better at " + skill[0] + " (" + skill[1] + ")";
			break;
		case INTERRUPTED:
			message = referenceCode + "'s casting was interrupted";
			break;
		case PET_BEGIN_ABILITY:
			String[] potherBeginAbility = referenceCode.split("\\^");
			message = potherBeginAbility[0] + " begins their ability [" + potherBeginAbility[1] + "]";
			break;
		case NPC_BEGIN_ABILITY:
			String[] notherBeginAbility = referenceCode.split("\\^");
			message = notherBeginAbility[0] + " begins their ability [" + notherBeginAbility[1] + "]";
			break;
		case OTHER_BEGIN_ABILITY:
			String[] otherBeginAbility = referenceCode.split("\\^");
			message = otherBeginAbility[0] + " begins their ability [" + otherBeginAbility[1] + "]";
			break;
		case FINISH_ABILITY:
			message = "You finish your ability";
			break;
		case PET_BACKINGOFFTGT:
			message = "As you wish master";
			break;
		case PET_ATTACKINGTGT:
			message = "Attacking "+referenceCode+" master";
			break;
		case OOC_MESSAGE:
			message = referenceCode;
			if (message.contains("itemlink"))
				showItemLinks = true;
			break;
		case DISCORD_MESSAGE:
			message = referenceCode;
			break;
		case MASTERWUFULL:
			message = "The spirit of The Master fills you!  You gain " +referenceCode+ " additional attack(s).";
			break;
		case HITFORDMGBY:
			String[] referenceCodes = referenceCode.split(",");
			String defender = referenceCodes[0];
			String damage = referenceCodes[1];
			String skilltype = referenceCodes[2];
			String attacker = referenceCodes[3];
			message = attacker + " hit " + defender + " for " + damage + " points of " + skilltype + " damage";
			break;
		case HITTHEMBUTMISSED:
			message = "You tried to hit " + referenceCode + ", but missed!";
			break;
		case PETHITTHEMBUTMISSED:
			message = "Your pet tried to hit " + referenceCode + ", but missed!";
			break;
		case HITYOUBUTMISSED:
			message = referenceCode + " tried to hit you but missed!";
			break;
		case NEED_TARGET:
			message = "You must select a target (See SoliniaMOD Keybinds)";
			break;
		case PICKEDUP_SPELL:
			message = "You have picked up a spell. You can add this to your spellbook with /spellbook add. Up to 8 spells can be memorised and cast from the memorisation bar at the top of the screen  (See SoliniaMOD Keybinds)";
			break;
		case INSUFFICIENT_REAGENTS:
			message = "Insufficient Reagents ["+referenceCode+"] (Check spell and see /reagents)";
			break;
		case RUNE_ABSORBED:
			message = "Your Rune absorbed " + referenceCode + " points of damage";
			break;
		case SERVER_SAVE_BEGIN:
			message = "RPG State is backing up, this may take some time";
			break;
		case SERVER_SAVE_FINISH:
			message = "RPG State backup complete";
			break;
		case SPELL_INVALIDEFFECT:
			message = "This is not a valid effect for this entity: " + referenceCode;
			break;
		case FOCUSEFFECTFLICKER:
			String[] referenceCodesFocus = referenceCode.split("\\^");
			message = "Your " + referenceCodesFocus[0] + " " + referenceCodesFocus[1];
			break;
		case INSUFFICIENT_LEVEL_GEAR:
			message = "You are not sufficient level to use this equipment";
			break;
		case EXCEEDED_CLAIMXP:
			message = "You have exceeded your maximum pending XP! Please /claimxp your additional XP before more can be gained (max: " + referenceCode + ")";
			break;
		case PLAYER_JOIN:
			message = referenceCode;
			break;
		case BER_CRITICAL_DMG:
			message = "Your berserker status causes additional critical blow damage! " + referenceCode;
			break;
		case CRITICAL_DMG:
			message = "You scored additional critical damage! " + referenceCode;
			break;
		case CHARM_CHA_FAIL:
			message = ChatColor.DARK_AQUA + "Your pet has freed themselves of your control! (Charisma)" + ChatColor.RESET;
			break;
		case STARTS_TO_SING:
			String[] sings = referenceCode.split("\\^");
			message = sings[0] + " starts to sing " + sings[1];
			break;		
		case STOPS_SINGING:
			String[] stopsing = referenceCode.split("\\^");
			message = stopsing[0] + " stops singing " + stopsing[1];
			break;		
		case SPELL_WORN_OFF_OF:
			String[] referenceCodesWornOffOf = referenceCode.split("\\^");
			message = ChatColor.DARK_AQUA + "Your " + referenceCodesWornOffOf[0] + " spell has worn off of " + referenceCodesWornOffOf[1]  + ChatColor.RESET;
			break;
		case CAST_ON_YOU_SONG:
			message = referenceCode;
			break;		
		case CAST_ON_YOU:
			message = referenceCode;
			break;		
		case CAST_ON_OTHER_SONG:
			String[] castonothersong = referenceCode.split("\\^");
			message = castonothersong[0] + castonothersong[1];
			break;		
		case CAST_ON_OTHER:
			String[] castonother = referenceCode.split("\\^");
			message = castonother[0] + castonother[1];
			break;		
		case ASSASSINATES:
			message = referenceCode + " ASSASSINATES their victim!!";
			break;		
		case BONUSEXPERIENCE:
			message = "You were given bonus XP from a xp bonus /hotzone or potion! (See /stats && /hotzones)";
			break;
		case GAINEXPERIENCE:
			message = ChatColor.YELLOW + "You gain experience (" + referenceCode + "% into level)";
			break;
		case LOSTEXPERIENCE:
			message = ChatColor.RED + "You lost experience (" + referenceCode + "% into level)";
			break;
		case FACTION_GOTBETTER:
			message = "Your faction standing with " + referenceCode + " could not possibly got better";
			break;
		case FACTION_GOTWORSE:
			message = "Your faction standing with " + referenceCode + " could not possibly got worse";
			break;
		case FACTION_COULDNOTGETWORSE:
			message = "Your faction standing with " + referenceCode + " could not possibly get any worse";
			break;
		case FACTION_COULDNOTGETBETTER:
			message = "Your faction standing with " + referenceCode + " could not possibly get any better";
			break;
		case DUALWIELD:
			message = "You dual wield!";
			break;
		case DOUBLEATTACK:
			message = "You double attack!";
			break;
		case SLAYUNDEAD:
			message = referenceCode +"'s holy blade cleanses their target!";
			break;
		case ITEM_DISCOVERED:
			String[] itemDiscoveryData = referenceCode.split("\\^");
			try
			{
				ISoliniaItem discoveredSolItem = StateManager.getInstance().getConfigurationManager().getItem(Integer.parseInt(itemDiscoveryData[0]));
				if (discoveredSolItem == null)
					return;
				itemStack = discoveredSolItem.asItemStack();
				showItemLinks = true;
			} catch (Exception e)
			{
				e.printStackTrace();
				return;
			}
			message = "An item has been discovered by " + itemDiscoveryData[1];
			break;
		case ARTIFACT_DISCOVERED:
			try
			{
				ISoliniaItem discoveredSolItem = StateManager.getInstance().getConfigurationManager().getItem(Integer.parseInt(referenceCode));
				if (discoveredSolItem == null)
					return;
				itemStack = discoveredSolItem.asItemStack();
				showItemLinks = true;
			} catch (Exception e)
			{
				e.printStackTrace();
				return;
			}
			message = "A unique artifact has been discovered";
			break;
		}
		
		try
		{
			TextComponent tc = new TextComponent("");
			
			if (!hint.equals(HINT.OOC_MESSAGE))
			{
				TextComponent clickTextComponent = new TextComponent("");
				String title = ChatColor.GRAY + "<"+channelCode+">"+ ChatColor.RESET;
				clickTextComponent.setText(title);
				clickTextComponent.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/togglehint " + hint.name().toUpperCase()));
				clickTextComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Click to toggle off").create()));
				tc.addExtra(clickTextComponent);
			}

			TextComponent fullTextComponent = new TextComponent(TextComponent.fromLegacyText(ChatColor.GRAY + message + ChatColor.RESET));
			if (itemStack != null)
				fullTextComponent = decorateTextComponentsWithHovers(fullTextComponent, itemStack, showItemLinks);
			
			tc.addExtra(fullTextComponent);
				
			if (entity instanceof Player)
			{
				ISoliniaPlayer solPlayer = SoliniaPlayerAdapter.Adapt((Player)entity);
				if (solPlayer != null && solPlayer.getHintSetting(hint) != HintSetting.Off)
				{
					((Player)entity).spigot().sendMessage(solPlayer.getHintSettingAsChatMessageType(hint), tc);
				}
			}
			
			if(sendNearby && entity != null)
			for (Player player : Bukkit.getOnlinePlayers()) {
				if (player.getLocation().distance(entity.getLocation()) <= ChatUtils.GetLocalSayRange(entity.getLocation().getWorld().getName()))
				{
					// already received message thankst
					if (player.getUniqueId().equals(entity.getUniqueId()))
						continue;
					ISoliniaPlayer solPlayer = SoliniaPlayerAdapter.Adapt(player);
					if (solPlayer != null && solPlayer.getHintSetting(hint) != HintSetting.Off)
					player.spigot().sendMessage(solPlayer.getHintSettingAsChatMessageType(hint),tc);
				}
			}
		} catch (CoreStateInitException e)
		{
			
		}
		
	}
	
	public static TextComponent decorateTextComponentsWithHovers(TextComponent tc, ItemStack itemStack, boolean showItemLinks) {
		if (itemStack != null && showItemLinks)
		{
			try
			{
				if (ItemStackUtils.IsSoliniaItem(itemStack))
				{
					int extraIndex = -1;
					for(int i = 0; i < tc.getExtra().size(); i++)
						if (tc.getExtra().get(i) instanceof TextComponent && ((TextComponent)tc.getExtra().get(i)).getText().contains("itemlink"))
							extraIndex = i;
					
					if (extraIndex > -1)
					{
						String text = ((TextComponent)tc.getExtra().get(extraIndex)).getText();
						((TextComponent)tc.getExtra().get(extraIndex)).setText(text.replace("itemlink", ""));
					}
				}
				TextComponent itemLinkComponent = new TextComponent();
				String title = " <" + itemStack.getItemMeta().getDisplayName() + ">";
				itemLinkComponent.setText(title);
				itemLinkComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_ITEM, new ComponentBuilder(ItemStackUtils.ConvertItemStackToJsonRegular(itemStack)).create()));
				tc.addExtra(itemLinkComponent);
			} catch (Exception e)
			{
				System.out.println("Could not create itemlink for message: " + tc.getText() + " with itemStack");
			}
		}
		
		return tc;
	}

	public static HintSetting getDefaultHintLocation(HINT hint) {
		
		// WARNING
		// THIS SHOULD ABSOLUTELY NEVER RETURN NULL
		switch (hint)
		{
		case OOC_MESSAGE:
			return HintSetting.Chat;
		case DISCORD_MESSAGE:
			return HintSetting.Chat;
		case HITFORDMGBY:
			return HintSetting.ActionBar;
		case MASTERWUFULL: 
			return HintSetting.Chat;
		case HITTHEMBUTMISSED: 
			return HintSetting.ActionBar;
		case HITYOUBUTMISSED: 
			return HintSetting.ActionBar;
		case PETHITTHEMBUTMISSED: 
			return HintSetting.ActionBar;
		case PICKEDUP_SPELL: 
			return HintSetting.Chat;
		case NEED_TARGET: 
			return HintSetting.Chat;
		case INSUFFICIENT_REAGENTS:
			return HintSetting.Chat;
		case PET_ATTACKINGTGT:
			return HintSetting.Chat;
		case PET_BACKINGOFFTGT:
			return HintSetting.Chat;
		case RUNE_ABSORBED:
			return HintSetting.Chat;
		case SERVER_SAVE_BEGIN:
			return HintSetting.Chat;
		case SERVER_SAVE_FINISH:
			return HintSetting.Chat;
		case SPELL_INVALIDEFFECT:
			return HintSetting.Chat;
		case FOCUSEFFECTFLICKER:
			return HintSetting.Chat;
		case SPELL_WORN_OFF_OF:
			return HintSetting.Chat;
		case INSUFFICIENT_LEVEL_GEAR:
			return HintSetting.Chat;
		case NPC_BEGIN_ABILITY:
			return HintSetting.Chat;
		case PET_BEGIN_ABILITY:
			return HintSetting.Chat;
		case SKILLUP:
			return HintSetting.Chat;
		case OTHER_BEGIN_ABILITY:
			return HintSetting.Chat;
		case INTERRUPTED:
			return HintSetting.Chat;
		case FINISH_ABILITY:
			return HintSetting.Chat;
		case ITEM_DISCOVERED:
			return HintSetting.Chat;
		case ARTIFACT_DISCOVERED:
			return HintSetting.Chat;
		case CHARM_CHA_FAIL:
			return HintSetting.Chat;
		case EXCEEDED_CLAIMXP:
			return HintSetting.Chat;
		case CRITICAL_DMG:
			return HintSetting.Chat;
		case BER_CRITICAL_DMG:
			return HintSetting.Chat;
		case PLAYER_JOIN:
			return HintSetting.Chat;
		case STARTS_TO_SING:
			return HintSetting.Chat;
		case STOPS_SINGING:
			return HintSetting.Chat;
		case CAST_ON_YOU_SONG:
			return HintSetting.Chat;
		case CAST_ON_YOU:
			return HintSetting.Chat;
		case CAST_ON_OTHER_SONG:
			return HintSetting.Chat;
		case CAST_ON_OTHER:
			return HintSetting.Chat;
		case ASSASSINATES:
			return HintSetting.Chat;
		case SLAYUNDEAD:
			return HintSetting.Chat;
		case GAINEXPERIENCE:
			return HintSetting.Chat;
		case LOSTEXPERIENCE:
			return HintSetting.Chat;
		case BONUSEXPERIENCE:
			return HintSetting.Chat;
		case FACTION_GOTBETTER:
			return HintSetting.Chat;
		case FACTION_COULDNOTGETBETTER:
			return HintSetting.Chat;
		case FACTION_GOTWORSE:
			return HintSetting.Chat;
		case FACTION_COULDNOTGETWORSE:
			return HintSetting.Chat;
		case DUALWIELD:
			return HintSetting.Chat;
		case DOUBLEATTACK:
			return HintSetting.Chat;
		case NPC_RAMPAGE:
			return HintSetting.Chat;
		case PET_FLURRY:
			return HintSetting.Chat;
		case YOU_FLURRY:
			return HintSetting.Chat;
		case NPC_FLURRY:
			return HintSetting.Chat;
		}
		
		return HintSetting.Chat;
	}

	
	public static String garbleText(String coremessage, int languageLearnedPercent) {
		try
		{
			int percentKnown = languageLearnedPercent;
			if (percentKnown > 100)
				percentKnown = 100;
			if (percentKnown < 1)
				percentKnown = 1;
			
			int charsToReplace = coremessage.length()-(int)(((float)coremessage.length()/(float)100F) * (float)percentKnown);
			if (charsToReplace < 1)
				return coremessage;
			
			List<Integer> replaceIndexes = MathUtils.pickNRandomIndex(coremessage.length(), charsToReplace);
			
			for (Integer index : replaceIndexes) {
				coremessage = TextUtils.replaceChar(coremessage,ChatUtils.ConvertToRunic(String.valueOf(coremessage.charAt(index))).charAt(0),index);
			}
			
			return coremessage;
		} catch (Exception e)
		{
			e.printStackTrace();
			return coremessage;
		}
	}
	
	public static String ConvertToRunic(String message) {
		List<String> runicChars = UTF8Utils.GetRunic();

		String newmessage = "";
		for (int i = 0; i < message.length(); i++) {
			if (message.toCharArray()[i] == ' ') {
				newmessage += message.toCharArray()[i];
			} else {
				newmessage += MathUtils.getRandomItemFromList(runicChars);
			}
		}

		return newmessage;
	}
	
	public static int GetLocalSayRange(String worldName)
	{
		try
		{
			return StateManager.getInstance().getConfigurationManager().getWorld(worldName).getLocalchatrange();
		} catch (CoreStateInitException e)
		{
			return 25;
		}
	}
	
	public static int GetLocalShoutRange(String worldName)
	{
		try
		{
			return StateManager.getInstance().getConfigurationManager().getWorld(worldName).getShoutchatrange();
		} catch (CoreStateInitException e)
		{
			return 64;
		}
	}
	
	public static int GetLocalWhisperRange(String worldName)
	{
		try
		{
			return StateManager.getInstance().getConfigurationManager().getWorld(worldName).getWhisperchatrange();
		} catch (CoreStateInitException e)
		{
			return 5;
		}
	}
}
