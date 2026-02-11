package github.renderbr.hytale.config.obj;

import org.junit.jupiter.api.Test;

import java.util.regex.Pattern;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class ChatFilterConfigurationTest {

    @Test
    public void testGetTermsAsRegexPatternsBannable() {
        ChatFilterConfiguration config = new ChatFilterConfiguration();
        config.bannableTerms = new String[]{"badword", "another"};

        ArrayList<Pattern> patterns = config.GetTermsAsRegexPatterns(ChatFilterType.BANNABLE);

        assertEquals(2, patterns.size());

        assertTrue(patterns.getFirst().matcher("That is a badword").find());
        assertTrue(patterns.getFirst().matcher("BADWORD").find());
        assertFalse(patterns.get(0).matcher("mybadword").find()); // word boundary
        assertFalse(patterns.get(0).matcher("badwordis").find()); // word boundary

        assertTrue(patterns.get(1).matcher("another word").find());
    }

    @Test
    public void testGetTermsAsRegexPatternsCensorable() {
        ChatFilterConfiguration config = new ChatFilterConfiguration();
        config.termsToCensor = new String[]{"censorMe"};

        ArrayList<Pattern> patterns = config.GetTermsAsRegexPatterns(ChatFilterType.CENSORABLE);

        assertEquals(1, patterns.size());
        assertTrue(patterns.getFirst().matcher("Please censorMe now").find());
    }

    @Test
    public void testGetTermsAsRegexPatternsRemovable() {
        ChatFilterConfiguration config = new ChatFilterConfiguration();
        config.termsToDisable = new String[]{"removeMe"};

        ArrayList<Pattern> patterns = config.GetTermsAsRegexPatterns(ChatFilterType.REMOVABLE);

        assertEquals(1, patterns.size());
        assertTrue(patterns.getFirst().matcher("Please removeMe now").find());
    }
}
