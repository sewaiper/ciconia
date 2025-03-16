package ru.sewaiper.ciconia.service;

import org.apache.commons.collections4.IterableUtils;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import ru.sewaiper.ciconia.BaseTest;
import ru.sewaiper.ciconia.fake.TelegraphFaker;
import ru.sewaiper.ciconia.repository.packet.PacketRepository;
import ru.sewaiper.ciconia.service.message.packet.DefaultPacketService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class DefaultPacketServiceTest extends BaseTest {

    private final static Logger LOG = LoggerFactory.getLogger(DefaultPacketServiceTest.class);

    @Autowired
    private DefaultPacketService packetService;

    @Autowired
    private PacketRepository packetRepository;

    @Test
    void append_successTest() {
        var userCnt = 20;
        var packetSize = 10;
        var messageCnt = 20;

        var channel = createChannel();

        for (int i = 0; i < userCnt; i++) {
            var user = createUser();
            subscribe(user.getId(), channel.getId());
            packetService.configurePacket(user.getId(), packetSize, "PT10M");
        }

        doReturn(TelegraphFaker.page())
                .when(telegraphClient).createPage(anyString(), anyList());

        for (int i = 0; i < messageCnt; i++) {
            var message = createMessage(channel.getId());
            packetService.append(message);
        }

        LOG.info("All messages is sent");

        var expectedPackets = messageCnt/packetSize;

        var actualPackets = IterableUtils.toList(packetRepository.findAll());
        assertNotNull(actualPackets);
        assertEquals((expectedPackets + 1) * userCnt, actualPackets.size());

        verify(telegraphClient, times(expectedPackets * userCnt))
                .createPage(anyString(), anyList());
    }
}
