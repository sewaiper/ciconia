package ru.sewaiper.ciconia.service.message.tag;

import java.util.Optional;

public interface TagService {

    Optional<CiconiaTag> tagging(long messageId, String text);
}
