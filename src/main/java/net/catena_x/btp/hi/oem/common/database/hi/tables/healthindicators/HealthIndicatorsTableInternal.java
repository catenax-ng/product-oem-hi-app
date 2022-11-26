package net.catena_x.btp.hi.oem.common.database.hi.tables.healthindicators;

import net.catena_x.btp.hi.oem.backend.hi_service.notifications.dto.supplierhiservice.items.HealthIndicatorOutput;
import net.catena_x.btp.hi.oem.common.database.hi.base.HITableBase;
import net.catena_x.btp.hi.oem.util.exceptions.OemHIException;
import net.catena_x.btp.libraries.util.database.annotations.TransactionDefaultCreateNew;
import net.catena_x.btp.libraries.util.database.annotations.TransactionDefaultUseExisting;
import net.catena_x.btp.libraries.util.database.annotations.TransactionSerializableCreateNew;
import net.catena_x.btp.libraries.util.database.annotations.TransactionSerializableUseExisting;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotNull;
import java.time.Instant;
import java.util.List;

@Component
public class HealthIndicatorsTableInternal extends HITableBase {
    @Autowired private HealthIndicatorsRepository healthIndicatorsRepository;

    @TransactionSerializableUseExisting
    public String updateHealthIndicatorsGetIdExternalTransaction(
            @NotNull final HealthIndicatorOutput newHealthIndicators,
            @NotNull final String vehicleId,
            @NotNull final Instant calculationTimestamp,
            @NotNull final long calculationSyncCounter) throws OemHIException {
        try {
            final String newId = generateNewId();
            healthIndicatorsRepository.insert(newId, vehicleId, newHealthIndicators.getComponentId(),
                    calculationTimestamp, calculationSyncCounter,
                    arrayToJsonString(newHealthIndicators.getHealthIndicatorValues()));

            return newId;
        }
        catch(final Exception exception) {
            throw failed("Upload health indicators failed!", exception);
        }
    }

    @TransactionSerializableCreateNew
    public String updateHealthIndicatorsGetIdNewTransaction(
            @NotNull final HealthIndicatorOutput newHealthIndicators,
            @NotNull final String vehicleId,
            @NotNull final Instant calculationTimestamp,
            @NotNull final long calculationSyncCounter) throws OemHIException {

        return updateHealthIndicatorsGetIdExternalTransaction(
                newHealthIndicators, vehicleId, calculationTimestamp, calculationSyncCounter);
    }

    @TransactionDefaultUseExisting
    public void deleteAllExternalTransaction() throws OemHIException {
        try {
            healthIndicatorsRepository.deleteAll();
        } catch(final Exception exception) {
            throw failed("Deleting all health indicators failed!", exception);
        }
    }

    @TransactionDefaultCreateNew
    public void deleteAllNewTransaction() throws OemHIException {
        deleteAllExternalTransaction();
    }

    @TransactionDefaultUseExisting
    public void deleteByIdExternalTransaction(@NotNull final String id) throws OemHIException {
        try {
            healthIndicatorsRepository.deleteById(id);
        } catch(final Exception exception) {
            throw failed("Deleting health indicators by id failed!", exception);
        }
    }

    @TransactionDefaultCreateNew
    public void deleteByIdNewTransaction(@NotNull final String id) throws OemHIException {
        deleteByIdExternalTransaction(id);
    }

    @TransactionDefaultUseExisting
    public void deleteByVehicleIdExternalTransaction(@NotNull final String vehicleId) throws OemHIException {
        try {
            healthIndicatorsRepository.deleteByVehicleId(vehicleId);
        } catch(final Exception exception) {
            throw failed("Deleting health indicators by vehicle is failed!", exception);
        }
    }

    @TransactionDefaultCreateNew
    public void deleteByVehicleIdNewTransaction(@NotNull final String vehicleId) throws OemHIException {
        deleteByVehicleIdExternalTransaction(vehicleId);
    }

    @TransactionDefaultUseExisting
    public void deleteByGearboxIdExternalTransaction(@NotNull final String gearboxId) throws OemHIException {
        try {
            healthIndicatorsRepository.deleteByGearboxId(gearboxId);
        } catch(final Exception exception) {
            throw failed("Deleting health indicators by gearbox is failed!", exception);
        }
    }

    @TransactionDefaultCreateNew
    public void deleteByGearboxIdNewTransaction(@NotNull final String gearboxId) throws OemHIException {
        deleteByGearboxIdExternalTransaction(gearboxId);
    }

    @TransactionDefaultUseExisting
    public void deleteCalculatedUntilExternalTransaction(@NotNull final Instant calculatedUntil) throws OemHIException {
        try {
            healthIndicatorsRepository.deleteCalculatedUntil(calculatedUntil);
        } catch(final Exception exception) {
            throw failed("Deleting health indicators by calculation timestamp is failed!", exception);
        }
    }

    @TransactionDefaultCreateNew
    public void deleteCalculatedUntilNewTransaction(@NotNull final Instant calculatedUntil) throws OemHIException {
        deleteCalculatedUntilExternalTransaction(calculatedUntil);
    }

    @TransactionDefaultUseExisting
    public void deleteCalculationSyncCounterUntilExternalTransaction(@NotNull final long calculationSyncCounter)
            throws OemHIException {
        try {
            healthIndicatorsRepository.deleteCalculationSyncCounterUntil(calculationSyncCounter);
        } catch(final Exception exception) {
            throw failed("Deleting health indicators by calculation sync counter is failed!", exception);
        }
    }

    @TransactionDefaultCreateNew
    public void deleteCalculationSyncCounterUntilNewTransaction(@NotNull final long calculationSyncCounter)
            throws OemHIException {
        deleteCalculationSyncCounterUntilExternalTransaction(calculationSyncCounter);
    }

    @TransactionDefaultUseExisting
    public HealthIndicatorsDAO getByIdExternalTransaction(@NotNull final String id) throws OemHIException {
        try {
            return healthIndicatorsRepository.queryById(id);
        }
        catch(final Exception exception) {
            throw failed("Querying database for health indicators by id failed!", exception);
        }
    }

    @TransactionDefaultCreateNew
    public HealthIndicatorsDAO getByIdNewTransaction(@NotNull final String id) throws OemHIException {
        return getByIdExternalTransaction(id);
    }

    @TransactionDefaultUseExisting
    public List<HealthIndicatorsDAO> getByVehicleIdExternalTransaction(@NotNull final String vehicleId)
            throws OemHIException {
        try {
            return healthIndicatorsRepository.queryByVehicleId(vehicleId);
        }
        catch(final Exception exception) {
            throw failed("Querying database for health indicators by vehicle id failed!", exception);
        }
    }

    @TransactionDefaultCreateNew
    public List<HealthIndicatorsDAO> getByVehicleIdNewTransaction(final String vehicleId) throws OemHIException {
        return getByVehicleIdExternalTransaction(vehicleId);
    }

    @TransactionDefaultUseExisting
    public List<HealthIndicatorsDAO> getByGearboxIdExternalTransaction(@NotNull final String gearboxId)
            throws OemHIException {
        try {
            return healthIndicatorsRepository.queryByGearboxId(gearboxId);
        }
        catch(final Exception exception) {
            throw failed("Querying database for health indicators by gearbox id failed!", exception);
        }
    }

    @TransactionDefaultCreateNew
    public List<HealthIndicatorsDAO> getByGearboxIdNewTransaction(final String gearboxId) throws OemHIException {
        return getByGearboxIdExternalTransaction(gearboxId);
    }

    @TransactionDefaultUseExisting
    public List<HealthIndicatorsDAO> getByVehicleIdOrderByCalculationSyncCounterExternalTransaction(
            @NotNull final String vehicleId) throws OemHIException {
        try {
            return healthIndicatorsRepository.queryByVehicleIdOrderByCalculationSyncCounter(vehicleId);
        }
        catch(final Exception exception) {
            throw failed("Querying database for health indicators by vehicle id failed!", exception);
        }
    }

    @TransactionDefaultCreateNew
    public List<HealthIndicatorsDAO> getByVehicleIdOrderByCalculationSyncCounterNewTransaction(final String vehicleId)
            throws OemHIException {
        return getByVehicleIdOrderByCalculationSyncCounterExternalTransaction(vehicleId);
    }

    @TransactionDefaultUseExisting
    public List<HealthIndicatorsDAO> getByGearboxIdOrderByCalculationSyncCounterExternalTransaction(
            @NotNull final String gearboxId) throws OemHIException {
        try {
            return healthIndicatorsRepository.queryByGearboxIdOrderByCalculationSyncCounter(gearboxId);
        }
        catch(final Exception exception) {
            throw failed("Querying database for health indicators by vehicle id failed!", exception);
        }
    }

    @TransactionDefaultCreateNew
    public List<HealthIndicatorsDAO> getByGearboxIdOrderByCalculationSyncCounterNewTransaction(final String gearboxId)
            throws OemHIException {
        return getByGearboxIdOrderByCalculationSyncCounterExternalTransaction(gearboxId);
    }

    @TransactionDefaultUseExisting
    public List<HealthIndicatorsDAO> getCalculatedSinceExternalTransaction(@NotNull final Instant timestamp)
            throws OemHIException {
        try {
            return healthIndicatorsRepository.queryByCalculationSince(timestamp);
        }
        catch(final Exception exception) {
            throw failed("Querying database for health indicators by calculation timestamp failed!", exception);
        }
    }

    @TransactionDefaultCreateNew
    public List<HealthIndicatorsDAO> getCalculatedSinceNewTransaction(@NotNull final Instant timestamp)
            throws OemHIException {
        return getCalculatedSinceExternalTransaction(timestamp);
    }

    @TransactionDefaultUseExisting
    public List<HealthIndicatorsDAO> getCalculatedUntilExternalTransaction(@NotNull final Instant timestamp)
            throws OemHIException {
        try {
            return healthIndicatorsRepository.queryByCalculationUntil(timestamp);
        }
        catch(final Exception exception) {
            throw failed("Querying database for health indicators by calculation timestamp failed!", exception);
        }
    }

    @TransactionDefaultCreateNew
    public List<HealthIndicatorsDAO> getCalculatedUntilNewTransaction(@NotNull final Instant timestamp)
            throws OemHIException {
        return getCalculatedUntilExternalTransaction(timestamp);
    }

    @TransactionDefaultUseExisting
    public List<HealthIndicatorsDAO> getCalculationSyncCounterSinceExternalTransaction(
            @NotNull final long calculationSyncCounterSince) throws OemHIException {
        try {
            return healthIndicatorsRepository.queryByCalculationSyncCounterSince(calculationSyncCounterSince);
        }
        catch(final Exception exception) {
            throw failed("Querying database for health indicators by calculation timestamp failed!", exception);
        }
    }

    @TransactionDefaultCreateNew
    public List<HealthIndicatorsDAO> getCalculationSyncCounterSinceNewTransaction(
            @NotNull final long calculationSyncCounterSince) throws OemHIException {
        return getCalculationSyncCounterSinceExternalTransaction(calculationSyncCounterSince);
    }

    @TransactionDefaultUseExisting
    public List<HealthIndicatorsDAO> getCalculationSyncCounterUntilExternalTransaction(
            @NotNull final long calculationSyncCounterUntil) throws OemHIException {
        try {
            return healthIndicatorsRepository.queryByCalculationSyncCounterUntil(calculationSyncCounterUntil);
        }
        catch(final Exception exception) {
            throw failed("Querying database for health indicators by calculation timestamp failed!", exception);
        }
    }

    @TransactionDefaultCreateNew
    public List<HealthIndicatorsDAO> getCalculationSyncCounterUntilNewTransaction(
            @NotNull final long calculationSyncCounterUntil) throws OemHIException {
        return getCalculationSyncCounterUntilExternalTransaction(calculationSyncCounterUntil);
    }

    @TransactionDefaultUseExisting
    public List<HealthIndicatorsDAO> getAllExternalTransaction() throws OemHIException {
        try {
            return healthIndicatorsRepository.queryAll();
        }
        catch(final Exception exception) {
            throw failed("Querying database for all health indicators failed!", exception);
        }
    }

    @TransactionDefaultCreateNew
    public List<HealthIndicatorsDAO> getAllNewTransaction() throws OemHIException {
        return getAllExternalTransaction();
    }

    @TransactionDefaultUseExisting
    public List<HealthIndicatorsDAO> getAllOrderByCalculationTimestampExternalTransaction() throws OemHIException {
        try {
            return healthIndicatorsRepository.queryAllOrderByCalculationTimestamp();
        }
        catch(final Exception exception) {
            throw failed("Querying database for all health indicators failed!", exception);
        }
    }

    @TransactionDefaultCreateNew
    public List<HealthIndicatorsDAO> getAllOrderByCalculationTimestampNewTransaction() throws OemHIException {
        return getAllOrderByCalculationTimestampExternalTransaction();
    }

    @TransactionDefaultUseExisting
    public List<HealthIndicatorsDAO> getAllOrderByCalculationSyncCounterExternalTransaction() throws OemHIException {
        try {
            return healthIndicatorsRepository.queryAllOrderByCalculationSyncCounter();
        }
        catch(final Exception exception) {
            throw failed("Querying database for all health indicators failed!", exception );
        }
    }

    @TransactionDefaultCreateNew
    public List<HealthIndicatorsDAO> getAllOrderByCalculationSyncCounterNewTransaction() throws OemHIException {
        return getAllOrderByCalculationSyncCounterExternalTransaction();
    }
}
