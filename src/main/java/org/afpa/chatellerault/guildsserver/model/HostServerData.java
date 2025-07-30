package org.afpa.chatellerault.guildsserver.model;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.afpa.chatellerault.guildsserver.util.TableFieldSpec;

import java.net.InetAddress;
import java.util.List;
import java.util.UUID;

@EqualsAndHashCode(callSuper = true)
@Data
@Builder
public class HostServerData extends BaseEntityData {
    UUID id;
    InetAddress ipAddress;
    int port;

    @Override
    public String tableName() {
        return "host";
    }

    @Override
    public List<TableFieldSpec> tableFields() {
        return List.of(
                TableFieldSpec.builder("id",
                        UUID.class, this::getId, val -> this.setId((UUID) val)
                ).isPrimaryKey(true).isGenerated(true).build(),
                TableFieldSpec.builder("ip_address",
                        InetAddress.class, this::getIpAddress, val -> this.setIpAddress((InetAddress) val)
                ).build(),
                TableFieldSpec.builder("port",
                        Integer.class, this::getPort, val -> this.setPort((int) val)
                ).build()
        );
    }
}
