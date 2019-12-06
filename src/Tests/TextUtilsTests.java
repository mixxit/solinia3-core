package Tests;

import org.junit.Test;

import com.solinia.solinia.Utils.TextUtils;

import static org.junit.Assert.assertEquals;

import java.util.List;

public class TextUtilsTests {
	@Test
	public void TextUtilsSplitString() {
		String testString = "This item can be added to your /spellbook Word word can be added to your /spellbook";
		int testLength = 41;
		List<String> result = TextUtils.breakStringOfWordsIntoLines(testString, testLength);
        assertEquals(2, result.size());
        assertEquals("This item can be added to your /spellbook", result.toArray()[0]);
        assertEquals("Word word can be added to your /spellbook",  result.toArray()[1]);
    }
	
	@Test
	public void TextUtilsSplitClasses() {
		String testString = "§fWARRIOR:(§e254§f)§r §fCLERIC:(§e254§f)§r §fPALADIN:(§e254§f)§r §fRANGER:(§e254§f)§r §fSHADOWKNIGHT:(§e254§f)§r §fDRUID:(§e254§f)§r §fMONK:(§e254§f)§r §fROGUE:(§e254§f)§r §fSHAMAN:(§e254§f)§r §fNECROMANCER:(§e254§f)§r §fWIZARD:(§e254§f)§r §fMAGICIAN:(§e254§f)§r §fENCHANTER:(§e254§f)§r §fBEASTLORD:(§e254§f)§r §fBERSERKER:(§e254§f)§r";
		int testLength = 64;
		List<String> result = TextUtils.breakStringOfWordsIntoLines(testString, testLength);
        assertEquals(7, result.size());
        assertEquals("§fWARRIOR:(§e254§f)§r §fCLERIC:(§e254§f)§r §fPALADIN:(§e254§f)§r", result.toArray()[0]);
        assertEquals("§fRANGER:(§e254§f)§r §fSHADOWKNIGHT:(§e254§f)§r",  result.toArray()[1]);
    }
}
