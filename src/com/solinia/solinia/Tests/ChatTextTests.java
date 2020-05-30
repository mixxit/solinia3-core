package com.solinia.solinia.Tests;

import org.junit.Test;

import com.solinia.solinia.Interfaces.ISoliniaNPCEventHandler;
import com.solinia.solinia.Models.InteractionType;
import com.solinia.solinia.Models.SoliniaNPCEventHandler;
import com.solinia.solinia.Utils.ChatUtils;
import com.solinia.solinia.Utils.QuestUtils;
import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

public class ChatTextTests {
	@Test
	public void QuestUtilsreplaceChatWordsWithHints() {
		String expectsContainsWord = "[§dforward§b]";
		
		ISoliniaNPCEventHandler firstHandler = new SoliniaNPCEventHandler();
		firstHandler.setInteractiontype(InteractionType.CHAT);
		firstHandler.setTriggerdata("HAIL");
		firstHandler.setChatresponse("The old woman slowly raises her head to gaze at you. The sound of waves and creaking boards pervades your head, you hear a voices carry over the wind that seem to be calling out to you. You feel the need to go forward");
		ISoliniaNPCEventHandler secondHandler = new SoliniaNPCEventHandler();
		secondHandler.setInteractiontype(InteractionType.CHAT);
		secondHandler.setTriggerdata("FORWARD");
		secondHandler.setChatresponse("Your vision blurs and you feel your breathing stop. You are held motionless in front of the old woman and begin to feel water fill your mouth and nose as you slowly drown");
		List<ISoliniaNPCEventHandler> handlers = new ArrayList<ISoliniaNPCEventHandler>();
		handlers.add(firstHandler);
		handlers.add(secondHandler);

        assertEquals(true, QuestUtils.replaceChatWordsWithHints(firstHandler.getChatresponse(), handlers).contains(expectsContainsWord));
    }
	
	@Test
	public void garbleText100PercentReturnsAll() {
		String test = "This is my test messageThis is my test messageThis is my test messageThis is my test message This is";
		String message = ChatUtils.garbleText(test, 100);

        assertEquals(test,message);
    }
	
	@Test
	public void garbleText90PercentReturns90() {
		String test = "This is my test";
		String message = ChatUtils.garbleText(test, 100);

        assertEquals(test,message);
    }
}
