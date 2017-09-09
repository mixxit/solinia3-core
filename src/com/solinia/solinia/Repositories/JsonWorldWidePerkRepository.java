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
import com.google.gson.reflect.TypeToken;
import com.solinia.solinia.Interfaces.IRepository;
import com.solinia.solinia.Models.WorldWidePerk;

public class JsonWorldWidePerkRepository implements IRepository<WorldWidePerk> {

	private String filePath;
	private ConcurrentHashMap<Integer, WorldWidePerk> perks = new ConcurrentHashMap<Integer, WorldWidePerk>();

	@Override
	public void add(WorldWidePerk item) {
		this.perks.put(item.getId(), item);
	}

	@Override
	public void add(Iterable<WorldWidePerk> items) {
		for(WorldWidePerk i : items)
		{
			this.perks.put(i.getId(), i);
		}
	}

	@Override
	public void update(WorldWidePerk item) {
		this.perks.put(item.getId(), item);
	}

	@Override
	public void remove(WorldWidePerk item) {
		this.perks.remove(item.getId());
	}

	@Override
	public void remove(Predicate<WorldWidePerk> filter) {
		for(WorldWidePerk i : perks.values().stream().filter(filter).collect(Collectors.toList()))
		{
			perks.remove(i.getId());
		}
	}

	@Override
	public List<WorldWidePerk> query(Predicate<WorldWidePerk> filter) {
		return perks.values().stream().filter(filter).collect(Collectors.toList());
	}

	@Override
	public void reload() {
		List<WorldWidePerk> file = new ArrayList<WorldWidePerk>();
		
		try {
			Gson gson = new Gson();
			BufferedReader br = new BufferedReader(new FileReader(filePath));
			file = gson.fromJson(br, new TypeToken<List<WorldWidePerk>>(){}.getType());
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		perks.clear();
		for(WorldWidePerk i : file)
		{
			perks.put(i.getId(), i);
		}
		
		System.out.println("Reloaded " + perks.size() + " perks");
	}	
	
	@Override
	public void commit() {
		// never commit
	}

	public void setJsonFile(String filePath) {
		this.filePath = filePath;		
	}

}
