package net.catena_x.btp.hi.oem.common.database.hi.tables.healthindicators;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "healthindicators")
@NamedNativeQuery(name = "HealthIndicatorsDAO.insert",
        query = "INSERT INTO healthindicators (id, vehicle_id, gearbox_id, calculation_timestamp, " +
                "calculation_sync_counter, values) "
                + "VALUES (:id, :vehicle_id, :gearbox_id, :calculation_timestamp, :calculation_sync_counter, :values)")
@NamedNativeQuery(name = "HealthIndicatorsDAO.deleteAll",
        query = "DELETE FROM healthindicators")
@NamedNativeQuery(name = "HealthIndicatorsDAO.deleteById",
        query = "DELETE FROM healthindicators WHERE id=:id")
@NamedNativeQuery(name = "HealthIndicatorsDAO.deleteByVehicleId",
        query = "DELETE FROM healthindicators WHERE vehicleId=:vehicle_id")
@NamedNativeQuery(name = "HealthIndicatorsDAO.deleteByGearboxId",
        query = "DELETE FROM healthindicators WHERE gearbox_id=:gearbox_id")
@NamedNativeQuery(name = "HealthIndicatorsDAO.deleteCalculatedUntil",
        query = "DELETE FROM healthindicators WHERE calculation_timestamp<=:calculated_until")
@NamedNativeQuery(name = "HealthIndicatorsDAO.deleteCalculationSyncCounterUntil",
        query = "DELETE FROM healthindicators WHERE calculation_sync_counter<=:calculation_sync_counter")
@NamedNativeQuery(name = "HealthIndicatorsDAO.queryAll", resultClass = HealthIndicatorsDAO.class,
        query = "SELECT * FROM healthindicators")
@NamedNativeQuery(name = "HealthIndicatorsDAO.queryById", resultClass = HealthIndicatorsDAO.class,
        query = "DELETE * FROM healthindicators WHERE id=:id")
@NamedNativeQuery(name = "HealthIndicatorsDAO.queryAllOrderByCalculationTimestamp",
        resultClass = HealthIndicatorsDAO.class,
        query = "SELECT * FROM healthindicators ORDER BY calculation_timestamp")
@NamedNativeQuery(name = "HealthIndicatorsDAO.queryAllOrderByCalculationSyncCounter",
        resultClass = HealthIndicatorsDAO.class,
        query = "SELECT * FROM healthindicators ORDER BY calculation_sync_counter")
@NamedNativeQuery(name = "HealthIndicatorsDAO.queryByVehicleId", resultClass = HealthIndicatorsDAO.class,
        query = "SELECT * FROM healthindicators WHERE vehicle_id=:vehicle_id")
@NamedNativeQuery(name = "HealthIndicatorsDAO.queryByGearboxId", resultClass = HealthIndicatorsDAO.class,
        query = "SELECT * FROM healthindicators WHERE gearbox_id=:gearbox_id")
@NamedNativeQuery(name = "HealthIndicatorsDAO.queryByVehicleIdOrderByCalculationSyncCounter",
        resultClass = HealthIndicatorsDAO.class,
        query = "SELECT * FROM healthindicators WHERE vehicle_id=:vehicle_id ORDER BY calculation_sync_counter")
@NamedNativeQuery(name = "HealthIndicatorsDAO.queryByGearboxIdOrderByCalculationSyncCounter",
        resultClass = HealthIndicatorsDAO.class,
        query = "SELECT * FROM healthindicators WHERE gearbox_id=:gearbox_id ORDER BY calculation_sync_counter")
@NamedNativeQuery(name = "HealthIndicatorsDAO.queryByCalculationSince", resultClass = HealthIndicatorsDAO.class,
        query = "SELECT * FROM healthindicators WHERE calculation_timestamp<=:calculation_timestamp_since")
@NamedNativeQuery(name = "HealthIndicatorsDAO.queryByCalculationUntil", resultClass = HealthIndicatorsDAO.class,
        query = "SELECT * FROM healthindicators WHERE calculation_timestamp<=:calculation_timestamp_until")
@NamedNativeQuery(name = "HealthIndicatorsDAO.queryByCalculationSyncCounterSince",
        resultClass = HealthIndicatorsDAO.class,
        query = "SELECT * FROM healthindicators WHERE calculation_sync_counter>=:calculation_sync_counter_since")
@NamedNativeQuery(name = "HealthIndicatorsDAO.queryByCalculationSyncCounterUntil",
        resultClass = HealthIndicatorsDAO.class,
        query = "SELECT * FROM healthindicators WHERE calculation_sync_counter<=:calculation_sync_counter_until")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class HealthIndicatorsDAO {
    @Id
    @Column(name="id", length=50, updatable=false, nullable=false, unique=true)
    private String id;

    @Column(name="calculation_timestamp", updatable=false, nullable=false)
    private Instant calculationTimestamp;

    @Column(name="calculation_sync_counter", updatable=false, nullable=false)
    private long calculationSyncCounter;

    @Column(name="vehicle_id", length=50, updatable=false, nullable=false)
    private String vehicleId;

    @Column(name="gearbox_id", length=50, updatable=false, nullable=false)
    private String gearboxId;

    @Column(name="values", length=65000, updatable=false, nullable=false)
    private String values;
}
