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
public class HIHealthIndicatorsTableInternal extends HITableBase {
    @Autowired private HIHealthIndicatorsRepository HIHealthIndicatorsRepository;

    @TransactionSerializableUseExisting
    public String updateHealthIndicatorsGetIdExternalTransaction(
            @NotNull final HealthIndicatorOutput newHealthIndicators,
            @NotNull final String vehicleId,
            @NotNull final Instant calculationTimestamp,
            @NotNull final long calculationSyncCounter) throws OemHIException {
        try {
            final String newId = generateNewId();
            HIHealthIndicatorsRepository.insert(newId, vehicleId, newHealthIndicators.getComponentId(),
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
            HIHealthIndicatorsRepository.deleteAll();
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
            HIHealthIndicatorsRepository.deleteById(id);
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
            HIHealthIndicatorsRepository.deleteByVehicleId(vehicleId);
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
            HIHealthIndicatorsRepository.deleteByGearboxId(gearboxId);
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
            HIHealthIndicatorsRepository.deleteCalculatedUntil(calculatedUntil);
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
            HIHealthIndicatorsRepository.deleteCalculationSyncCounterUntil(calculationSyncCounter);
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
    public HIHealthIndicatorsDAO getByIdExternalTransaction(@NotNull final String id) throws OemHIException {
        try {
            return HIHealthIndicatorsRepository.queryById(id);
        }
        catch(final Exception exception) {
            throw failed("Querying database for health indicators by id failed!", exception);
        }
    }

    @TransactionDefaultCreateNew
    public HIHealthIndicatorsDAO getByIdNewTransaction(@NotNull final String id) throws OemHIException {
        return getByIdExternalTransaction(id);
    }

    @TransactionDefaultUseExisting
    public List<HIHealthIndicatorsDAO> getByVehicleIdExternalTransaction(@NotNull final String vehicleId)
            throws OemHIException {
        try {
            return HIHealthIndicatorsRepository.queryByVehicleId(vehicleId);
        }
        catch(final Exception exception) {
            throw failed("Querying database for health indicators by vehicle id failed!", exception);
        }
    }

    @TransactionDefaultCreateNew
    public List<HIHealthIndicatorsDAO> getByVehicleIdNewTransaction(final String vehicleId) throws OemHIException {
        return getByVehicleIdExternalTransaction(vehicleId);
    }

    @TransactionDefaultUseExisting
    public List<HIHealthIndicatorsDAO> getByGearboxIdExternalTransaction(@NotNull final String gearboxId)
            throws OemHIException {
        try {
            return HIHealthIndicatorsRepository.queryByGearboxId(gearboxId);
        }
        catch(final Exception exception) {
            throw failed("Querying database for health indicators by gearbox id failed!", exception);
        }
    }

    @TransactionDefaultCreateNew
    public List<HIHealthIndicatorsDAO> getByGearboxIdNewTransaction(final String gearboxId) throws OemHIException {
        return getByGearboxIdExternalTransaction(gearboxId);
    }

    @TransactionDefaultUseExisting
    public List<HIHealthIndicatorsDAO> getByVehicleIdOrderByCalculationSyncCounterExternalTransaction(
            @NotNull final String vehicleId) throws OemHIException {
        try {
            return HIHealthIndicatorsRepository.queryByVehicleIdOrderByCalculationSyncCounter(vehicleId);
        }
        catch(final Exception exception) {
            throw failed("Querying database for health indicators by vehicle id failed!", exception);
        }
    }

    @TransactionDefaultCreateNew
    public List<HIHealthIndicatorsDAO> getByVehicleIdOrderByCalculationSyncCounterNewTransaction(final String vehicleId)
            throws OemHIException {
        return getByVehicleIdOrderByCalculationSyncCounterExternalTransaction(vehicleId);
    }

    @TransactionDefaultUseExisting
    public List<HIHealthIndicatorsDAO> getByGearboxIdOrderByCalculationSyncCounterExternalTransaction(
            @NotNull final String gearboxId) throws OemHIException {
        try {
            return HIHealthIndicatorsRepository.queryByGearboxIdOrderByCalculationSyncCounter(gearboxId);
        }
        catch(final Exception exception) {
            throw failed("Querying database for health indicators by vehicle id failed!", exception);
        }
    }

    @TransactionDefaultCreateNew
    public List<HIHealthIndicatorsDAO> getByGearboxIdOrderByCalculationSyncCounterNewTransaction(final String gearboxId)
            throws OemHIException {
        return getByGearboxIdOrderByCalculationSyncCounterExternalTransaction(gearboxId);
    }

    @TransactionDefaultUseExisting
    public List<HIHealthIndicatorsDAO> getCalculatedSinceExternalTransaction(@NotNull final Instant timestamp)
            throws OemHIException {
        try {
            return HIHealthIndicatorsRepository.queryByCalculationSince(timestamp);
        }
        catch(final Exception exception) {
            throw failed("Querying database for health indicators by calculation timestamp failed!", exception);
        }
    }

    @TransactionDefaultCreateNew
    public List<HIHealthIndicatorsDAO> getCalculatedSinceNewTransaction(@NotNull final Instant timestamp)
            throws OemHIException {
        return getCalculatedSinceExternalTransaction(timestamp);
    }

    @TransactionDefaultUseExisting
    public List<HIHealthIndicatorsDAO> getCalculatedUntilExternalTransaction(@NotNull final Instant timestamp)
            throws OemHIException {
        try {
            return HIHealthIndicatorsRepository.queryByCalculationUntil(timestamp);
        }
        catch(final Exception exception) {
            throw failed("Querying database for health indicators by calculation timestamp failed!", exception);
        }
    }

    @TransactionDefaultCreateNew
    public List<HIHealthIndicatorsDAO> getCalculatedUntilNewTransaction(@NotNull final Instant timestamp)
            throws OemHIException {
        return getCalculatedUntilExternalTransaction(timestamp);
    }

    @TransactionDefaultUseExisting
    public List<HIHealthIndicatorsDAO> getCalculationSyncCounterSinceExternalTransaction(
            @NotNull final long calculationSyncCounterSince) throws OemHIException {
        try {
            return HIHealthIndicatorsRepository.queryByCalculationSyncCounterSince(calculationSyncCounterSince);
        }
        catch(final Exception exception) {
            throw failed("Querying database for health indicators by calculation timestamp failed!", exception);
        }
    }

    @TransactionDefaultCreateNew
    public List<HIHealthIndicatorsDAO> getCalculationSyncCounterSinceNewTransaction(
            @NotNull final long calculationSyncCounterSince) throws OemHIException {
        return getCalculationSyncCounterSinceExternalTransaction(calculationSyncCounterSince);
    }

    @TransactionDefaultUseExisting
    public List<HIHealthIndicatorsDAO> getCalculationSyncCounterUntilExternalTransaction(
            @NotNull final long calculationSyncCounterUntil) throws OemHIException {
        try {
            return HIHealthIndicatorsRepository.queryByCalculationSyncCounterUntil(calculationSyncCounterUntil);
        }
        catch(final Exception exception) {
            throw failed("Querying database for health indicators by calculation timestamp failed!", exception);
        }
    }

    @TransactionDefaultCreateNew
    public List<HIHealthIndicatorsDAO> getCalculationSyncCounterUntilNewTransaction(
            @NotNull final long calculationSyncCounterUntil) throws OemHIException {
        return getCalculationSyncCounterUntilExternalTransaction(calculationSyncCounterUntil);
    }

    @TransactionDefaultUseExisting
    public List<HIHealthIndicatorsDAO> getAllExternalTransaction() throws OemHIException {
        try {
            return HIHealthIndicatorsRepository.queryAll();
        }
        catch(final Exception exception) {
            throw failed("Querying database for all health indicators failed!", exception);
        }
    }

    @TransactionDefaultCreateNew
    public List<HIHealthIndicatorsDAO> getAllNewTransaction() throws OemHIException {
        return getAllExternalTransaction();
    }

    @TransactionDefaultUseExisting
    public List<HIHealthIndicatorsDAO> getAllOrderByCalculationTimestampExternalTransaction() throws OemHIException {
        try {
            return HIHealthIndicatorsRepository.queryAllOrderByCalculationTimestamp();
        }
        catch(final Exception exception) {
            throw failed("Querying database for all health indicators failed!", exception);
        }
    }

    @TransactionDefaultCreateNew
    public List<HIHealthIndicatorsDAO> getAllOrderByCalculationTimestampNewTransaction() throws OemHIException {
        return getAllOrderByCalculationTimestampExternalTransaction();
    }

    @TransactionDefaultUseExisting
    public List<HIHealthIndicatorsDAO> getAllOrderByCalculationSyncCounterExternalTransaction() throws OemHIException {
        try {
            return HIHealthIndicatorsRepository.queryAllOrderByCalculationSyncCounter();
        }
        catch(final Exception exception) {
            throw failed("Querying database for all health indicators failed!", exception );
        }
    }

    @TransactionDefaultCreateNew
    public List<HIHealthIndicatorsDAO> getAllOrderByCalculationSyncCounterNewTransaction() throws OemHIException {
        return getAllOrderByCalculationSyncCounterExternalTransaction();
    }
}
