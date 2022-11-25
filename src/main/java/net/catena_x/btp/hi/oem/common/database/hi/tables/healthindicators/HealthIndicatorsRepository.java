package net.catena_x.btp.hi.oem.common.database.hi.tables.healthindicators;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

import javax.validation.constraints.NotNull;
import java.time.Instant;
import java.util.List;

public interface HealthIndicatorsRepository extends Repository<HealthIndicatorsDAO, String> {
    @Modifying
    void insert(@Param("id") @NotNull final String id,
                @Param("vehicle_id") @NotNull final String vehicleId,
                @Param("calculation_timestamp") @NotNull final Instant calculationTimestamp,
                @Param("calculation_sync_counter") @NotNull final long calculationSyncCounter,
                @Param("values") @NotNull final String values);
    @Modifying void deleteAll();
    @Modifying void deleteById(@Param("id") @NotNull final String id);
    @Modifying void deleteByVehicleId(@Param("vehicle_id") @NotNull final String vehicleId);
    @Modifying void deleteCalculatedUntil(@Param("calculated_until") @NotNull final Instant calculatedUntil);
    @Modifying void deleteCalculationSyncCounterUntil(
            @Param("calculation_sync_counter") @NotNull final long calculationSyncCounter);
    List<HealthIndicatorsDAO> queryAll();
    HealthIndicatorsDAO queryById(@Param("id") @NotNull final String id);
    List<HealthIndicatorsDAO> queryAllOrderByCalculationTimestamp();
    List<HealthIndicatorsDAO> queryAllOrderByCalculationSyncCounter();
    List<HealthIndicatorsDAO> queryByVehicleId(@Param("vehicle_id") @NotNull final String vehicleId);
    List<HealthIndicatorsDAO> queryByVehicleIdOrderByCalculationSyncCounter(
            @Param("vehicle_id") @NotNull final String vehicleId);
    List<HealthIndicatorsDAO> queryByCalculationSince(
            @Param("calculation_timestamp_since") @NotNull final Instant calculationTimestampSince);
    List<HealthIndicatorsDAO> queryByCalculationUntil(
            @Param("calculation_timestamp_until") @NotNull final Instant calculationTimestampUntil);
    List<HealthIndicatorsDAO> queryByCalculationSyncCounterSince(
            @Param("calculation_sync_counter_since") @NotNull final long calculationSyncCounterSince);
    List<HealthIndicatorsDAO> queryByCalculationSyncCounterUntil(
            @Param("calculation_sync_counter_until") @NotNull final long calculationSyncCounterUntil);
}
