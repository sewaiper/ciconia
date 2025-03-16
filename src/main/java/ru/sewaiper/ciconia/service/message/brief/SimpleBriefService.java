package ru.sewaiper.ciconia.service.message.brief;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import ru.sewaiper.ciconia.configuration.properties.BriefProperties;

import java.util.Set;
import java.util.StringTokenizer;

@Service
public class SimpleBriefService implements BriefService {
    
    private static final Set<Character> PUNCTUATION = Set.of(
            ',', '.', '!', ';'
    );

    private final BriefProperties properties;

    public SimpleBriefService(BriefProperties properties) {
        this.properties = properties;
    }

    @Override
    public String getBrief(String text) {
        var maxSize = properties.getMaxSize();
        var tokenizer = new StringTokenizer(text, " \n");

        if (tokenizer.countTokens() < maxSize) {
            return text;
        }

        var brief = new StringBuilder();
        int count = 0;
        while (count < maxSize) {
            brief.append(tokenizer.nextToken());
            if (count != maxSize - 1) {
                brief.append(' ');
            }
            count++;
        }

        return addEllipsis(brief);
    }

    private String addEllipsis(StringBuilder brief) {
        var lastCharPos = brief.length() - 1;
        var lastChar = brief.charAt(lastCharPos);
        
        if (PUNCTUATION.contains(lastChar)) {
            brief.deleteCharAt(lastCharPos);
        }
        
        brief.append("...");

        return StringUtils.trim(brief.toString());
    }
}
