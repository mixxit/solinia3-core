package com.solinia.solinia.Utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class MathUtils {
	public static boolean ValueWithin(int spell_id, int isequal1, int isequals2) {
		if (spell_id == isequal1)
			return true;
		if (spell_id == isequals2)
			return true;
		return false;
	}
	
	public static boolean isInteger(String s) {
	    return isInteger(s,10);
	}

	public static boolean isInteger(String s, int radix) {
	    if(s.isEmpty()) return false;
	    for(int i = 0; i < s.length(); i++) {
	        if(i == 0 && s.charAt(i) == '-') {
	            if(s.length() == 1) return false;
	            else continue;
	        }
	        if(Character.digit(s.charAt(i),radix) < 0) return false;
	    }
	    return true;
	}

	
	public static <T> List<List<T>> getPagination(Collection<T> c, Integer pageSize) {
		return getPages(c,pageSize);
	}
	
	public static <T> List<List<T>> getPages(Collection<T> c, Integer pageSize) {
	    if (c == null)
	        return Collections.emptyList();
	    List<T> list = new ArrayList<T>(c);
	    if (pageSize == null || pageSize <= 0 || pageSize > list.size())
	        pageSize = list.size();
	    int numPages = (int) Math.ceil((double)list.size() / (double)pageSize);
	    List<List<T>> pages = new ArrayList<List<T>>(numPages);
	    for (int pageNum = 0; pageNum < numPages;)
	        pages.add(list.subList(pageNum * pageSize, Math.min(++pageNum * pageSize, list.size())));
	    return pages;
	}
	
	public static <T> T getRandomItemFromList(List<T> list) {
		return list.get(ThreadLocalRandom.current().nextInt(list.size()));
	}
	
	public static <T> List<T> pickNRandom(List<T> lst, int n) {
	    List<T> copy = new LinkedList<T>(lst);
	    Collections.shuffle(copy);
	    return copy.subList(0, n);
	}

	public static List<Integer> pickNRandomIndex(int length, int n) {
		List<Integer> list = IntStream.range(0, length-1).boxed().collect(Collectors.toList());
	    Collections.shuffle(list);
	    return list.subList(0, n-1);
	}
	
	public static double calculateYaw(double deltax, double deltaz) {
		double viewDirection = 90.0D;
		if (deltaz != 0.0D) {
			viewDirection = Math.toDegrees(Math.atan(-deltax / deltaz));
		}
		if (deltaz < 0.0D) {

			viewDirection += 180.0D;
		} else if ((deltax > 0.0D) && (deltaz > 0.0D)) {

			viewDirection += 360.0D;
		}

		return viewDirection;
	}

	public static double calculatePitch(double x, double y, double z) {
		double pitch = 0.0D;
		double a = Math.sqrt(x * x + z * z);
		double theta = Math.atan(y / a);
		pitch = -Math.toDegrees(theta);

		return pitch;
	}
	
	public static boolean RandomChance(int minmum) {
		Random r = new Random();
		int randomInt = r.nextInt(100) + 1;
		if (randomInt > minmum) {
			return true;
		}

		return false;
	}

	public static boolean RandomRoll(int max) {
		Random r = new Random();
		int random = MathUtils.RandomBetween(0, 100);
		if (random < max) {
			return true;
		}

		return false;
	}

	public static boolean Roll(int chance)
	{
		return MathUtils.RandomBetween(0, 100) < chance;
	}
	
	public static boolean Roll(float chance)
	{
		return MathUtils.RandomBetween(0, 100)/100F < chance;
	}
	
	public static int RandomBetween(int minnumber, int maxnumber) {
		Random r = new Random();
		return r.nextInt((maxnumber - minnumber) + 1) + minnumber;
	}
	
	public static float RandomBetween(float minnumber, float maxnumber) {
		Random r = new Random();
		return minnumber + r.nextFloat() * (minnumber - maxnumber);
	}
}
