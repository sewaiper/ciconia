package ru.sewaiper.ciconia.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.sewaiper.ciconia.configuration.properties.BriefProperties;
import ru.sewaiper.ciconia.service.message.brief.SimpleBriefService;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SimpleBriefServiceTest {

    private SimpleBriefService briefService;

    @BeforeEach
    void setUp() {
        var properties = new BriefProperties();
        properties.setMaxSize(10);

        briefService = new SimpleBriefService(properties);
    }

    @Test
    void text_successTest() {
        var text = """
                Librisid venenatis dico sumo elitr quaeque reformidans suas tantas oratio
                tantas graece qualisque nascetur aenean consectetuer. Sagittislitora meliore
                signiferumque pulvinar tota option scelerisque phasellus libris intellegebat
                aliquip id invenire erroribus. Penatibusdicit graece verear posidonium explicari
                ad. Nostercorrumpit donec aeque recteque aliquip.
                """;

        var expected = "Librisid venenatis dico sumo elitr quaeque reformidans suas tantas oratio...";

        var actual = briefService.getBrief(text);

        assertEquals(expected, actual);
    }

    @Test
    void punctuation_successTest() {
        var text = """
                Librisid venenatis dico sumo elitr quaeque reformidans suas tantas oratio,
                tantas graece qualisque nascetur aenean consectetuer. Sagittislitora meliore
                signiferumque pulvinar tota option scelerisque phasellus libris intellegebat
                aliquip id invenire erroribus. Penatibusdicit graece verear posidonium explicari
                ad. Nostercorrumpit donec aeque recteque aliquip.
                """;

        var expected = "Librisid venenatis dico sumo elitr quaeque reformidans suas tantas oratio...";

        var actual = briefService.getBrief(text);

        assertEquals(expected, actual);
    }
}
