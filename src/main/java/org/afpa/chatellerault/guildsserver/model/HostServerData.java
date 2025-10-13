package org.afpa.chatellerault.guildsserver.model;

import lombok.Builder;
import lombok.Data;
import org.afpa.chatellerault.guildsserver.core.TableConfig;
import org.afpa.chatellerault.guildsserver.core.TableConfigField;

import java.net.InetAddress;
import java.util.List;
import java.util.UUID;

@Data
@Builder
public class HostServerData {
    UUID id;
    String name;
    InetAddress ipAddress;
    int port;

    public static final class HostServerTable extends TableConfig<HostServerData> {
        public static final String
                name = "host";
        public static final List<TableConfigField<HostServerData, ?>>
                fields = List.of(
                TableConfigField.builder(
                        "id", UUID.class,
                        HostServerData::getId,
                        HostServerData::setId
                ).isPrimaryKey(true).isGenerated(true).build(),
                TableConfigField.builder(
                        "name", String.class,
                        HostServerData::getName,
                        HostServerData::setName
                ).build(),
                TableConfigField.builder(
                        "ip_address", InetAddress.class,
                        HostServerData::getIpAddress,
                        HostServerData::setIpAddress
                ).build(),
                TableConfigField.builder(
                        "port", Integer.class,
                        HostServerData::getPort,
                        HostServerData::setPort
                ).build()
        );

        public HostServerTable() {
            super(name, fields);
        }
    }
}
