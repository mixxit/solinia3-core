package com.solinia.solinia.Utils;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;

import org.apache.commons.lang.WordUtils;

public class TextUtils {
	public static String[] splitStringIntoBlocksOfSize(String string, int size)
	{
		return string.split("(?<=\\G.{"+size+"})");
	}
	
	public static String replaceChar(String str, char ch, int index) {
	    StringBuilder myString = new StringBuilder(str);
	    myString.setCharAt(index, ch);
	    return myString.toString();
	}
	
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
	
	public static String ToBase64UTF8(String text)
	{
		try {
			return Base64.getEncoder().encodeToString(text.getBytes("utf-8"));
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return Base64.getEncoder().encodeToString(text.getBytes());
		}
	}
	
	public static String FromBase64UTF8(String base64)
	{
		try {
			return new String(Base64.getDecoder().decode(base64),"utf-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return new String(Base64.getDecoder().decode(base64));
		}
	}


}
