package net.catena_x.btp.hi.oem.common.database.hi.tables.infoitem;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "info", uniqueConstraints={@UniqueConstraint(columnNames = {"key"})})
@NamedNativeQuery(name = "InfoItemDAO.insert",
        query = "INSERT INTO info (key, value) VALUES (:key, :value)")
@NamedNativeQuery(name = "InfoItemDAO.update",
        query = "UPDATE info SET value=:value WHERE key=:key")
@NamedNativeQuery(name = "InfoItemDAO.deleteAll",
        query = "DELETE FROM info")
@NamedNativeQuery(name = "InfoItemDAO.delete",
        query = "DELETE FROM info WHERE key=:key")
@NamedNativeQuery(name = "InfoItemDAO.queryAll", resultClass = HIInfoItemDAO.class,
        query = "SELECT key, value FROM info")
@NamedNativeQuery(name = "InfoItemDAO.queryByKey", resultClass = HIInfoItemDAO.class,
        query = "SELECT key, value FROM info WHERE key=:key")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class HIInfoItemDAO {
    @Id
    @Column(name="key", length=50, nullable=false)
    private String key;

    @Column(name="value", length=20000, nullable=false)
    private String value;
}