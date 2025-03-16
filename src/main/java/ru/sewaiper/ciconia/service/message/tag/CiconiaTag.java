package ru.sewaiper.ciconia.service.message.tag;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

public enum CiconiaTag {

    CONSUMER("потребительский"),
    TRANSPORT("транспорт"),
    MEDICINE("медицина"),
    IT("информационные технологии"),
    REAL_ESTATE("недвижимость"),
    RAW_MATERIAL("сырьё"),
    TELECOMMUNICATIONS("телекоммуникации"),
    FINANCE("финансы"),
    ELECTRIC_UTILITIES("электроэнергетика"),
    ENERGY("энергетика"),
    POLITICS("политика");

    private static final Map<String, CiconiaTag> LABEL_MAP = Arrays.stream(CiconiaTag.values())
            .collect(Collectors.toMap(CiconiaTag::label, Function.identity()));

    public static CiconiaTag fromLabel(String label) {
        return Optional.ofNullable(LABEL_MAP.get(label))
                .orElseThrow(IllegalArgumentException::new);
    }

    public static List<String> labels() {
        return Arrays.stream(CiconiaTag.values())
                .map(CiconiaTag::label)
                .toList();
    }

    private final String label;

    CiconiaTag(String label) {
        this.label = label;
    }

    public String label() {
        return this.label;
    }
}
