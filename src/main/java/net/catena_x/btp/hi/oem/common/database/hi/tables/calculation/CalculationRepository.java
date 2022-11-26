package net.catena_x.btp.hi.oem.common.database.hi.tables.calculation;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

import javax.validation.constraints.NotNull;
import java.time.Instant;
import java.util.List;

public interface CalculationRepository extends Repository<CalculationDAO, String> {
    @Modifying void createNow(@Param("id") @NotNull final String id,
                              @Param("calculation_sync_counter") @NotNull final long calculationSyncCounter,
                              @Param("status") @NotNull final String status);
    @Modifying void insert(@Param("id") @NotNull final String id,
                           @Param("calculation_timestamp") @NotNull final Instant calculationTimestamp,
                           @Param("calculation_sync_counter") @NotNull final long calculationSyncCounter,
                           @Param("status") @NotNull final String status);
    @Modifying void updateStatus(@Param("id") @NotNull final String id, @Param("status") @NotNull final String status);
    @Modifying void setMessage(@Param("id") @NotNull final String id,
                               @Param("message") @NotNull final String message);
    @Modifying void deleteAll();
    @Modifying void deleteById(@Param("id") @NotNull final String id);
    @Modifying void deleteByStatus(@Param("status") @NotNull final String status);
    @Modifying void deleteCalculatedUntil(@Param("calculated_until") @NotNull final Instant calculatedUntil);
    @Modifying void deleteCalculationSyncCounterUntil(
            @Param("calculation_sync_counter") @NotNull final long calculationSyncCounter);
    List<CalculationDAO> queryAll();
    CalculationDAO queryById(@Param("id") @NotNull final String id);
    List<CalculationDAO> queryByStatus(@Param("status") @NotNull final String status);
    List<CalculationDAO> queryAllOrderByCalculationTimestamp();
    List<CalculationDAO> queryAllOrderByCalculationSyncCounter();
    List<CalculationDAO> queryByCalculationSince(
            @Param("calculation_timestamp_since") @NotNull final Instant calculationTimestampSince);
    List<CalculationDAO> queryByCalculationUntil(
            @Param("calculation_timestamp_until") @NotNull final Instant calculationTimestampUntil);
    List<CalculationDAO> queryByCalculationSyncCounterSince(
            @Param("calculation_sync_counter_since") @NotNull final long calculationSyncCounterSince);
    List<CalculationDAO> queryByCalculationSyncCounterUntil(
            @Param("calculation_sync_counter_until") @NotNull final long calculationSyncCounterUntil);
}