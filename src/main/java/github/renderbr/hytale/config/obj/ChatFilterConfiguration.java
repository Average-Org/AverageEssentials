package github.renderbr.hytale.config.obj;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class ChatFilterConfiguration {
    public String[] bannableTerms = new String[]{};

    public ArrayList<Pattern> GetTermsAsRegexPatterns(ChatFilterType chatFilterType) {
        String[] patterns = new String[]{};

        switch (chatFilterType) {
            case ChatFilterType.BANNABLE -> patterns = bannableTerms;
            case ChatFilterType.CENSORABLE -> patterns = termsToCensor;
            case ChatFilterType.REMOVABLE -> patterns = termsToDisable;
        }

        ArrayList<Pattern> bannedPatterns = new ArrayList<>();

        for (String bannedTerm : patterns) {
            String regex = "\\b" + Pattern.quote(bannedTerm) + "\\b";
            bannedPatterns.add(Pattern.compile(regex, Pattern.CASE_INSENSITIVE));
        }

        return bannedPatterns;
    }

    public String[] termsToCensor = new String[]{};
    public String[] termsToDisable = new String[]{};
    public boolean allowUsersToUseChatColorCodes = true;
    public boolean allowUsersToEmbedLinks = false;
}
