package com.solinia.solinia.Managers;

public class StateManager {

	private static final CoreState state = new CoreState();
    
    private StateManager() 
    {
    	
    }

    public static CoreState getInstance() {
         return state;	
    }
}
