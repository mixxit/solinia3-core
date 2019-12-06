package Tests;

import org.junit.Test;

import com.solinia.solinia.Utils.TextUtils;

import static org.junit.Assert.assertEquals;

import java.util.List;

public class TextUtilsTests {
	@Test
	public void QuestUtilsreplaceChatWordsWithHints() {
		String testString = "This item can be added to your /spellbook Word word can be added to your /spellbook";
		int testLength = 41;
		List<String> result = TextUtils.breakStringOfWordsIntoLines(testString, testLength);
        assertEquals(2, result.size());
        assertEquals("This item can be added to your /spellbook", result.toArray()[0]);
        assertEquals("Word word can be added to your /spellbook",  result.toArray()[1]);
    }
}
