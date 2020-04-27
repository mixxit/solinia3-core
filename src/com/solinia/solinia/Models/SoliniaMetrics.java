package com.solinia.solinia.Models;

import java.math.BigDecimal;
import java.util.HashMap;

public class SoliniaMetrics {
	public int playersOnline = 0;
	public BigDecimal economySize = BigDecimal.ZERO;
	public int playersAfk = 0;
	public HashMap<Integer,Integer> levelDistribution = new HashMap<Integer,Integer>();
	public HashMap<String,Integer> classDistribution = new HashMap<String,Integer>();
	public HashMap<String,HashMap<Integer,Integer>> classLevelDistribution = new HashMap<String,HashMap<Integer,Integer>>();
	public HashMap<Integer,Integer> tierDistribution = new HashMap<Integer,Integer>();
	public HashMap<String,HashMap<Integer,Integer>> classTierDistribution = new HashMap<String,HashMap<Integer,Integer>>();
}
