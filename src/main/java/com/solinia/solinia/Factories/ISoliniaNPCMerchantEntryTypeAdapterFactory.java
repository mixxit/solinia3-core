package com.solinia.solinia.Factories;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import com.solinia.solinia.Interfaces.ISoliniaNPCMerchantEntry;

public class ISoliniaNPCMerchantEntryTypeAdapterFactory implements TypeAdapterFactory {
	  private final Class<? extends ISoliniaNPCMerchantEntry> implementationClass;

	  public ISoliniaNPCMerchantEntryTypeAdapterFactory(Class<? extends ISoliniaNPCMerchantEntry> implementationClass) {
	     this.implementationClass = implementationClass;
	  }

	  @SuppressWarnings("unchecked")
	  @Override
	  public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
	    if (!ISoliniaNPCMerchantEntry.class.equals(type.getRawType())) return null;

	    return (TypeAdapter<T>) gson.getAdapter(implementationClass);
	  }
	}