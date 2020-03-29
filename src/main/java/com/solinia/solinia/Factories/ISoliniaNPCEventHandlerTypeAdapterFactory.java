package com.solinia.solinia.Factories;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import com.solinia.solinia.Interfaces.ISoliniaNPCEventHandler;

public class ISoliniaNPCEventHandlerTypeAdapterFactory implements TypeAdapterFactory {
	  private final Class<? extends ISoliniaNPCEventHandler> implementationClass;

	  public ISoliniaNPCEventHandlerTypeAdapterFactory(Class<? extends ISoliniaNPCEventHandler> implementationClass) {
	     this.implementationClass = implementationClass;
	  }

	  @Override
	  public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
	    if (!ISoliniaNPCEventHandler.class.equals(type.getRawType())) return null;

	    return (TypeAdapter<T>) gson.getAdapter(implementationClass);
	  }
	}
