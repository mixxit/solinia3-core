package com.solinia.solinia.Utils;

import java.util.Arrays;
import java.util.List;

import com.solinia.solinia.Interfaces.ISoliniaNPCEventHandler;
import com.solinia.solinia.Models.InteractionType;

import net.md_5.bungee.api.ChatColor;

public class QuestUtils {
	public static String replaceChatWordsWithHints(String message, List<ISoliniaNPCEventHandler> eventHandlers) {
		message = ChatColor.AQUA + message + ChatColor.RESET;
		List<String> messages = Arrays.asList(ChatColor.stripColor(message).toUpperCase().split(" "));

		if (eventHandlers.size() > 0) {
			String searchlist = "";
			for (String messageword : messages) {
				searchlist += messageword + ",";
			}
		}

		for (ISoliniaNPCEventHandler handler : eventHandlers) {
			if (!handler.getInteractiontype().equals(InteractionType.CHAT))
				continue;

			if (!messages.contains(handler.getTriggerdata().toUpperCase()))
				continue;

			message = message.toLowerCase().replace(handler.getTriggerdata().toLowerCase(),
					"[" + handler.getTriggerdata().toLowerCase() + "]");
		}
		message = message.replace("[", "[" + ChatColor.LIGHT_PURPLE);
		message = message.replace("]", ChatColor.AQUA + "]");
		return message.toLowerCase();
	}
}
