package ru.sewaiper.ciconia.service.message.publish;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import ru.sewaiper.ciconia.bot.CiconiaBot;
import ru.sewaiper.ciconia.bot.CiconiaMessageTo;
import ru.sewaiper.ciconia.client.telegraph.TelegraphClient;
import ru.sewaiper.ciconia.client.telegraph.domain.html.BlockquoteNode;
import ru.sewaiper.ciconia.client.telegraph.domain.html.BrNode;
import ru.sewaiper.ciconia.client.telegraph.domain.html.HeadNode;
import ru.sewaiper.ciconia.client.telegraph.domain.html.HtmlNode;
import ru.sewaiper.ciconia.client.telegraph.domain.html.LinkNode;
import ru.sewaiper.ciconia.client.telegraph.domain.response.Page;
import ru.sewaiper.ciconia.error.PublishException;
import ru.sewaiper.ciconia.model.PacketMessage;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class TelegraphPublishService implements PublishService {

    private static final Logger LOG = LoggerFactory.getLogger(TelegraphPublishService.class);

    private final CiconiaBot ciconiaBot;
    private final TelegraphClient telegraphClient;
    private final DateTimeFormatter dateTimeFormatter;

    public TelegraphPublishService(CiconiaBot ciconiaBot,
                                   TelegraphClient telegraphClient) {
        this.ciconiaBot = ciconiaBot;
        this.telegraphClient = telegraphClient;

        dateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm", Locale.getDefault());
    }

    @Override
    @Async("publishHandleExecutor")
    public void print(PublishRequest request) {
        try {
            doSend(request);
        } catch (Exception e) {
            LOG.error("Unable to publish packet of {} messages for {}",
                    request.content().size(), request.readerId(), e);
        }
    }

    private void doSend(PublishRequest request) {
        var response =
                telegraphClient.createPage(getTitle(), prepareDocument(request.content()));

        if (!response.isOk()) {
            throw new PublishException("Telegraph error: " + response.getError());
        }

        var link = Optional.ofNullable(response.getResult())
                .map(Page::url)
                .filter(StringUtils::isNotBlank)
                .orElseThrow(() -> new PublishException("Invalid telegraph response. URL created page is missing"));

        ciconiaBot.sendMessage(new CiconiaMessageTo(request.readerId(), link));
    }

    private String getTitle() {
        var now = LocalDateTime.now();
        return String.format("Новости за %s", now.format(dateTimeFormatter));
    }

    private List<HtmlNode> prepareDocument(List<PacketMessage> messages) {
        var content = new ArrayList<HtmlNode>();

        var undef = new ArrayList<PacketMessage>();
        var groups = messages
                .stream()
                .filter(it -> isNotUndef(it, undef))
                .collect(Collectors.groupingBy(PacketMessage::getTag));

        var tags = groups.keySet();

        tags.stream()
                .map(tag -> getSection(tag.label(), groups.get(tag)))
                .flatMap(Collection::stream)
                .forEach(content::add);

        if (!undef.isEmpty()) {
            content.addAll(getSection("Прочее", undef));
        }

        return content;
    }

    private boolean isNotUndef(PacketMessage message, List<PacketMessage> undef) {
        if (message.getTag() != null) {
            return true;
        }
        undef.add(message);
        return false;
    }

    private List<HtmlNode> getSection(String tag, List<PacketMessage> messages) {

        var section = new ArrayList<HtmlNode>();

        var title = new HeadNode(4, StringUtils.capitalize(tag));

        section.add(title);

        messages.stream()
                .map(this::getMessageInfo)
                .flatMap(Collection::stream)
                .forEach(section::add);

        return section;
    }

    private List<HtmlNode> getMessageInfo(PacketMessage message) {
        var brief = new BlockquoteNode(message.getBrief());
        var link = new LinkNode(message.getLink(), "Источник");
        return List.of(brief, link, new BrNode());
    }
}
