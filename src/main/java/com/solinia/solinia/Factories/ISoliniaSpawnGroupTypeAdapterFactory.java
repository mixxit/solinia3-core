package com.solinia.solinia.Factories;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import com.solinia.solinia.Interfaces.ISoliniaSpawnGroup;

public class ISoliniaSpawnGroupTypeAdapterFactory implements TypeAdapterFactory {
	  private final Class<? extends ISoliniaSpawnGroup> implementationClass;

	  public ISoliniaSpawnGroupTypeAdapterFactory(Class<? extends ISoliniaSpawnGroup> implementationClass) {
	     this.implementationClass = implementationClass;
	  }

	  @SuppressWarnings("unchecked")
	  @Override
	  public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
	    if (!ISoliniaSpawnGroup.class.equals(type.getRawType())) return null;

	    return (TypeAdapter<T>) gson.getAdapter(implementationClass);
	  }
	}