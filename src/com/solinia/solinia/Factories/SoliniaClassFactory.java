package com.solinia.solinia.Factories;

import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Exceptions.SoliniaClassCreationException;
import com.solinia.solinia.Interfaces.ISoliniaClass;
import com.solinia.solinia.Managers.StateManager;
import com.solinia.solinia.Models.SoliniaClass;

public class SoliniaClassFactory {

	public static ISoliniaClass CreateClass(String classname, boolean adminonly) throws CoreStateInitException, SoliniaClassCreationException {
		if (StateManager.getInstance().getConfigurationManager().getClassObj(classname.toUpperCase()) != null)
			throw new SoliniaClassCreationException("Class already exists");
		
		SoliniaClass classObj = new SoliniaClass();
		classObj.setId(StateManager.getInstance().getConfigurationManager().getNextClassId());
		classObj.setName(classname.toUpperCase());
		classObj.setAdmin(adminonly);
		StateManager.getInstance().getConfigurationManager().addClass(classObj);
		return classObj;
	}

}
