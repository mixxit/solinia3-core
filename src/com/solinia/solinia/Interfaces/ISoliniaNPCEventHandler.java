package com.solinia.solinia.Interfaces;

import com.solinia.solinia.Models.InteractionType;

public interface ISoliniaNPCEventHandler {

	InteractionType getInteractiontype();

	void setInteractiontype(InteractionType interactiontype);

	String getTriggerdata();

	void setTriggerdata(String triggerdata);

	String getChatresponse();

	void setChatresponse(String chatresponse);
	
}
