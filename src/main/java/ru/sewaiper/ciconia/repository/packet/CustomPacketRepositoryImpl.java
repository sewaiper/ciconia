package ru.sewaiper.ciconia.repository.packet;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;
import ru.sewaiper.ciconia.model.PacketMessage;
import ru.sewaiper.ciconia.service.message.tag.CiconiaTag;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Component
public class CustomPacketRepositoryImpl implements CustomPacketRepository {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public CustomPacketRepositoryImpl(DataSource dataSource) {
        this.jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
    }

    private static final String CONTENT_QUERY = """
            select
                m.brief ,
                m.link ,
                m.tag
            from packets_content pc
                left join messages m on pc.message_id = m.id
            where pc.packet_id = :packetId
            group by m.id
            """;

    @Override
    public List<PacketMessage> findContent(long packetId) {

        var params = new MapSqlParameterSource()
                .addValue("packetId", packetId);

        return jdbcTemplate.query(CONTENT_QUERY, params, this::toMessage);
    }

    private PacketMessage toMessage(ResultSet rs, int num) throws SQLException {
        var entity = new PacketMessage();

        var tag = Optional.ofNullable(rs.getObject("tag", Integer.class))
                .map(idx -> CiconiaTag.values()[idx])
                .orElse(null);

        entity.setBrief(rs.getString("brief"));
        entity.setLink(rs.getString("link"));
        entity.setTag(tag);
        return entity;
    }
}
