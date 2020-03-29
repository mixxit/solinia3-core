package com.solinia.solinia.Utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.WordUtils;

public class TextUtils {
	public static List<String> breakStringOfWordsIntoLines(String input, int maxLineLength) {
		List<String> lines = new ArrayList<String>();
		lines = Arrays.asList(addLinebreaks(input,maxLineLength).split(System.lineSeparator()));
	    return lines;
	}
	
	public static String addLinebreaks(String input, int maxLineLength) {
		String lines =  WordUtils.wrap(input, maxLineLength, System.lineSeparator(), false);
		return lines;
	}
	
	public static String FormatAsName(String name) {
		// TODO Auto-generated method stub
		return CapitaliseFirstLetter(name);
	}

	public static String CapitaliseFirstLetter(String word) {
		return word.substring(0, 1).toUpperCase() + word.substring(1).toLowerCase();
	}


}
