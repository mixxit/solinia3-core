package com.solinia.solinia.Factories;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import com.solinia.solinia.Interfaces.ISoliniaAAEffect;

public class ISoliniaAAEffectTypeAdapterFactory implements TypeAdapterFactory {
	  private final Class<? extends ISoliniaAAEffect> implementationClass;

	  public ISoliniaAAEffectTypeAdapterFactory(Class<? extends ISoliniaAAEffect> implementationClass) {
	     this.implementationClass = implementationClass;
	  }

	  @SuppressWarnings("unchecked")
	  @Override
	  public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
	    if (!ISoliniaAAEffect.class.equals(type.getRawType())) return null;

	    return (TypeAdapter<T>) gson.getAdapter(implementationClass);
	  }
	}
