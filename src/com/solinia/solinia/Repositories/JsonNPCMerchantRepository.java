package com.solinia.solinia.Repositories;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.solinia.solinia.Factories.ISoliniaAARankTypeAdapterFactory;
import com.solinia.solinia.Factories.ISoliniaNPCMerchantEntryTypeAdapterFactory;
import com.solinia.solinia.Interfaces.IRepository;
import com.solinia.solinia.Interfaces.ISoliniaNPCMerchant;
import com.solinia.solinia.Models.SoliniaAARank;
import com.solinia.solinia.Models.SoliniaNPCMerchant;
import com.solinia.solinia.Models.SoliniaNPCMerchantEntry;

public class JsonNPCMerchantRepository implements IRepository<ISoliniaNPCMerchant> {

	private String filePath;
	private ConcurrentHashMap<Integer, ISoliniaNPCMerchant> npcmerchants = new ConcurrentHashMap<Integer, ISoliniaNPCMerchant>();

	@Override
	public void add(ISoliniaNPCMerchant item) {
		this.npcmerchants.put(item.getId(), item);
	}

	@Override
	public void add(Iterable<ISoliniaNPCMerchant> items) {
		for(ISoliniaNPCMerchant i : items)
		{
			this.npcmerchants.put(i.getId(), i);
		}
	}

	@Override
	public void update(ISoliniaNPCMerchant item) {
		this.npcmerchants.put(item.getId(), item);
	}

	@Override
	public void remove(ISoliniaNPCMerchant item) {
		this.npcmerchants.remove(item.getId());
	}

	@Override
	public void remove(Predicate<ISoliniaNPCMerchant> filter) {
		for(ISoliniaNPCMerchant i : npcmerchants.values().stream().filter(filter).collect(Collectors.toList()))
		{
			npcmerchants.remove(i.getId());
		}
	}

	@Override
	public List<ISoliniaNPCMerchant> query(Predicate<ISoliniaNPCMerchant> filter) {
		return npcmerchants.values().stream().filter(filter).collect(Collectors.toList());
	}

	@Override
	public void reload() {
		List<ISoliniaNPCMerchant> file = new ArrayList<ISoliniaNPCMerchant>();
		
		try {
			GsonBuilder gsonbuilder = new GsonBuilder();
			gsonbuilder.registerTypeAdapterFactory(new ISoliniaNPCMerchantEntryTypeAdapterFactory(SoliniaNPCMerchantEntry.class));
			Gson gson = gsonbuilder.create();
			BufferedReader br = new BufferedReader(new FileReader(filePath));
			file = gson.fromJson(br, new TypeToken<List<SoliniaNPCMerchant>>(){}.getType());
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		npcmerchants.clear();
		for(ISoliniaNPCMerchant i : file)
		{
			npcmerchants.put(i.getId(), i);
		}
		
		System.out.println("Reloaded " + npcmerchants.size() + " npcmerchants");
	}	
	
	@Override
	public void commit() {
		// TODO Auto-generated method stub
		GsonBuilder gsonbuilder = new GsonBuilder();
		gsonbuilder.setPrettyPrinting();
		gsonbuilder.registerTypeAdapterFactory(new ISoliniaNPCMerchantEntryTypeAdapterFactory(SoliniaNPCMerchantEntry.class));
		Gson gson = gsonbuilder.create();
		String jsonOutput = gson.toJson(npcmerchants.values(), new TypeToken<List<SoliniaNPCMerchant>>(){}.getType());
		try {
			
			File file = new File(filePath);
			if (!file.exists())
				file.createNewFile();
	        
			FileOutputStream fileOut = new FileOutputStream(file);
			OutputStreamWriter outWriter = new OutputStreamWriter(fileOut);
	        outWriter.append(jsonOutput);
	        outWriter.close();
	        fileOut.close();
	        
	        System.out.println("Commited " + npcmerchants.size() + " npcmerchants");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void setJsonFile(String filePath) {
		this.filePath = filePath;		
	}

}
