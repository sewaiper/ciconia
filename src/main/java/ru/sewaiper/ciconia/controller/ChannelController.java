package ru.sewaiper.ciconia.controller;

import org.drinkless.tdlib.TdApi;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.sewaiper.ciconia.service.channel.ChannelService;

@RestController
@RequestMapping("/api/v1/channel")
public class ChannelController {

    private final ChannelService channelService;

    public ChannelController(ChannelService channelService) {
        this.channelService = channelService;
    }

    @GetMapping
    public TdApi.Chat searchChat(@RequestParam String link) {
        return channelService.searchChat(link);
    }
}
