package net.catena_x.btp.hi.oem.common.database.hi.tables.calculation;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "calculations")
@NamedNativeQuery(name = "CalculationDAO.insert",
        query = "INSERT INTO calculations (id, calculation_timestamp, calculation_sync_counter, status) "
                + "VALUES (:id, :calculation_timestamp, :calculation_sync_counter, :status)")
@NamedNativeQuery(name = "CalculationDAO.createNow",
        query = "INSERT INTO calculations (id, calculation_timestamp, calculation_sync_counter, status) "
                + "VALUES (:id, CURRENT_TIMESTAMP, :calculation_sync_counter, :status)")
@NamedNativeQuery(name = "CalculationDAO.updateStatus",
        query = "UPDATE calculations SET status=:status WHERE id=:id")
@NamedNativeQuery(name = "CalculationDAO.setMessage",
        query = "UPDATE calculations SET message=:message WHERE id=:id")
@NamedNativeQuery(name = "CalculationDAO.deleteAll",
        query = "DELETE FROM calculations")
@NamedNativeQuery(name = "CalculationDAO.deleteById",
        query = "DELETE FROM calculations WHERE id=:id")
@NamedNativeQuery(name = "CalculationDAO.deleteByStatus",
        query = "DELETE FROM calculations WHERE status=:status")
@NamedNativeQuery(name = "CalculationDAO.deleteCalculatedUntil",
        query = "DELETE FROM calculations WHERE calculation_timestamp<=:calculated_until")
@NamedNativeQuery(name = "CalculationDAO.deleteCalculationSyncCounterUntil",
        query = "DELETE FROM calculations WHERE calculation_sync_counter<=:calculation_sync_counter")
@NamedNativeQuery(name = "CalculationDAO.queryAll", resultClass = CalculationDAO.class,
        query = "SELECT * FROM calculations")
@NamedNativeQuery(name = "CalculationDAO.queryById", resultClass = CalculationDAO.class,
        query = "SELECT * FROM calculations WHERE id=:id")
@NamedNativeQuery(name = "CalculationDAO.queryByStatus", resultClass = CalculationDAO.class,
        query = "SELECT * FROM calculations WHERE status=:status")
@NamedNativeQuery(name = "CalculationDAO.queryAllOrderByCalculationTimestamp", resultClass = CalculationDAO.class,
        query = "SELECT * FROM calculations ORDER BY calculation_timestamp")
@NamedNativeQuery(name = "CalculationDAO.queryAllOrderByCalculationSyncCounter", resultClass = CalculationDAO.class,
        query = "SELECT * FROM calculations ORDER BY calculation_sync_counter")
@NamedNativeQuery(name = "CalculationDAO.queryByCalculationSince", resultClass = CalculationDAO.class,
        query = "SELECT * FROM calculations WHERE calculation_timestamp<=:calculation_timestamp_since")
@NamedNativeQuery(name = "CalculationDAO.queryByCalculationUntil", resultClass = CalculationDAO.class,
        query = "SELECT * FROM calculations WHERE calculation_timestamp<=:calculation_timestamp_until")
@NamedNativeQuery(name = "CalculationDAO.queryByCalculationSyncCounterSince", resultClass = CalculationDAO.class,
        query = "SELECT * FROM calculations WHERE sync_counter>=:calculation_sync_counter_since")
@NamedNativeQuery(name = "CalculationDAO.queryByCalculationSyncCounterUntil", resultClass = CalculationDAO.class,
        query = "SELECT * FROM calculations WHERE sync_counter<=:calculation_sync_counter_until")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CalculationDAO {
    @Id
    @Column(name="id", length=50, updatable=false, nullable=false, unique=true)
    private String id;

    @Column(name="calculation_timestamp", updatable=false, nullable=false)
    private Instant calculationTimestamp;

    @Column(name="calculation_sync_counter", updatable=false, nullable=false)
    private long calculationSyncCounter;

    @Column(name="status", updatable=true, nullable=false)
    private String status;

    @Column(name="message", length=65000, updatable=true, nullable=true)
    private String message;
}
