package com.solinia.solinia.Managers;

public class StateManager {

	private static final SoliniaState state = new SoliniaState();
    
    private StateManager() 
    {
    	
    }

    public static SoliniaState getInstance() {
         return state;	
    }
}
