package com.solinia.solinia.Models;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class DebuggerSettings {
	private ConcurrentHashMap<String, String> debugValues = new ConcurrentHashMap<String, String>();
	
	public void toggleDebug(String classToDebug, String methodToDebug, String focusId) {
		String key = classToDebug+"-"+methodToDebug;
		
		if (debugValues.get(key) == null)
		{
			debugValues.put(key,focusId);
			return;
		}
		
		if (debugValues.get(key).equals("0"))
			debugValues.put(key,focusId);
		else
			debugValues.remove(key);
	}
	
	public boolean isDebugging(String classToDebug, String methodToDebug)
	{
		String key = classToDebug+"-"+methodToDebug;
		if (!this.debugValues.containsKey(key))
			return false;
		
		if (this.debugValues.get(key).equals("0"))
			return false;
		
		return true;
	}

}
