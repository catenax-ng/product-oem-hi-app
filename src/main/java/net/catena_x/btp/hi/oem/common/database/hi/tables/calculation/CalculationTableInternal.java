package net.catena_x.btp.hi.oem.common.database.hi.tables.calculation;

import net.catena_x.btp.hi.oem.common.database.hi.base.HITableBase;
import net.catena_x.btp.hi.oem.common.model.enums.CalculationStatus;
import net.catena_x.btp.hi.oem.util.exceptions.OemHIException;
import net.catena_x.btp.libraries.util.database.annotations.TransactionSerializableCreateNew;
import net.catena_x.btp.libraries.util.database.annotations.TransactionSerializableUseExisting;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotNull;
import java.time.Instant;
import java.util.List;

@Component
public class CalculationTableInternal extends HITableBase {
    @Autowired private CalculationRepository calculationRepository;

    @TransactionSerializableUseExisting
    public String insertGetIdExternalTransaction(@NotNull final Instant calculationTimestamp,
                                                 @NotNull final long calculationSyncCounter,
                                                 @NotNull final CalculationStatus status) throws OemHIException {
        try {
            final String id = generateNewId();
            calculationRepository.insert(id, calculationTimestamp, calculationSyncCounter, status.toString());
            return id;
        } catch (final Exception exception) {
            throw failed("Inserting calculation failed!", exception);
        }
    }

    @TransactionSerializableCreateNew
    public String insertGetIdNewTransaction(@NotNull final Instant calculationTimestamp,
                                            @NotNull final long calculationSyncCounter,
                                            @NotNull final CalculationStatus status)
            throws OemHIException {
        return insertGetIdExternalTransaction(calculationTimestamp, calculationSyncCounter, status);
    }

    @TransactionSerializableUseExisting
    public String createNowGetIdExternalTransaction(@NotNull final long calculationSyncCounter) throws OemHIException {
        try {
            final String id = generateNewId();
            calculationRepository.createNow(id, calculationSyncCounter, CalculationStatus.CREATED.toString());
            return id;
        } catch (final Exception exception) {
            throw failed("Inserting calculation failed!", exception);
        }
    }

    @TransactionSerializableCreateNew
    public String createNowGetIdNewTransaction(@NotNull final long calculationSyncCounter)
            throws OemHIException {
        return createNowGetIdExternalTransaction(calculationSyncCounter);
    }

    @TransactionSerializableUseExisting
    public void updateStatusExternalTransaction(@NotNull final String id, @NotNull final CalculationStatus newStatus)
            throws OemHIException {
        try {
            calculationRepository.updateStatus(id, newStatus.toString());
        } catch (final Exception exception) {
            throw failed("Updating calculation status failed!", exception);
        }
    }

    @TransactionSerializableCreateNew
    public void updateStatusNewTransaction(@NotNull final String id, @NotNull final CalculationStatus newStatus)
            throws OemHIException {
        updateStatusExternalTransaction(id, newStatus);
    }

    @TransactionSerializableUseExisting
    public void setMessageExternalTransaction(@NotNull final String id, @NotNull final String message)
            throws OemHIException {
        try {
            calculationRepository.setMessage(id, message);
        } catch (final Exception exception) {
            throw failed("Updating message for calculation failed!", exception);
        }
    }

    @TransactionSerializableCreateNew
    public void setSMessageNewTransaction(@NotNull final String id, @NotNull final String message)
            throws OemHIException {
        setMessageExternalTransaction(id, message);
    }

    @TransactionSerializableUseExisting
    public void deleteByIdExternalTransaction(@NotNull final String id) throws OemHIException {
        try {
            calculationRepository.deleteById(id);
        } catch (final Exception exception) {
            throw failed("Deleting calculation failed!", exception);
        }
    }

    @TransactionSerializableCreateNew
    public void deleteByIdNewTransaction(@NotNull final String id) throws OemHIException {
        deleteByIdExternalTransaction(id);
    }

    @TransactionSerializableUseExisting
    public void deleteByStatusExternalTransaction(@NotNull final CalculationStatus status)
            throws OemHIException {
        try {
            calculationRepository.deleteByStatus(status.toString());
        } catch (final Exception exception) {
            throw failed("Deleting calculation failed!", exception);
        }
    }

    @TransactionSerializableCreateNew
    public void deleteByStatusNewTransaction(@NotNull final CalculationStatus status)
            throws OemHIException {
        deleteByStatusExternalTransaction(status);
    }

    @TransactionSerializableUseExisting
    public void deleteCalculatedUntilExternalTransaction(@NotNull final Instant calculatedUntil)
            throws OemHIException {
        try {
            calculationRepository.deleteCalculatedUntil(calculatedUntil);
        } catch (final Exception exception) {
            throw failed("Deleting calculation failed!", exception);
        }
    }

    @TransactionSerializableCreateNew
    public void deleteCalculatedUntilNewTransaction(@NotNull final Instant calculatedUntil)
            throws OemHIException {
        deleteCalculatedUntilExternalTransaction(calculatedUntil);
    }

    @TransactionSerializableUseExisting
    public void deleteCalculationSyncCounterUntilExternalTransaction(@NotNull final long calculationSyncCounter)
            throws OemHIException {
        try {
            calculationRepository.deleteCalculationSyncCounterUntil(calculationSyncCounter);
        } catch (final Exception exception) {
            throw failed("Deleting calculation failed!", exception);
        }
    }

    @TransactionSerializableCreateNew
    public void deleteCalculationSyncCounterUntilNewTransaction(@NotNull final long calculationSyncCounter)
            throws OemHIException {
        deleteCalculationSyncCounterUntilExternalTransaction(calculationSyncCounter);
    }

    @TransactionSerializableUseExisting
    public List<CalculationDAO> getAllExternalTransaction() throws OemHIException {
        try {
            return calculationRepository.queryAll();
        } catch (final Exception exception) {
            throw failed("Querying calculations failed!", exception);
        }
    }

    @TransactionSerializableCreateNew
    public List<CalculationDAO> getAllNewTransaction() throws OemHIException {
        return getAllExternalTransaction();
    }

    @TransactionSerializableUseExisting
    public CalculationDAO getByIdExternalTransaction(@NotNull final String id) throws OemHIException {
        try {
            return calculationRepository.queryById(id);
        } catch (final Exception exception) {
            throw failed("Querying calculation by id failed!", exception);
        }
    }

    @TransactionSerializableCreateNew
    public CalculationDAO getByIdNewTransaction(@NotNull final String id) throws OemHIException {
        return getByIdExternalTransaction(id);
    }

    @TransactionSerializableUseExisting
    public List<CalculationDAO> getByStatusExternalTransaction(@NotNull final CalculationStatus status)
            throws OemHIException {
        try {
            return calculationRepository.queryByStatus(status.toString());
        } catch (final Exception exception) {
            throw failed("Querying calculations by status failed!", exception);
        }
    }

    @TransactionSerializableCreateNew
    public List<CalculationDAO> getByStatusNewTransaction(@NotNull final CalculationStatus status)
            throws OemHIException {
        return getByStatusExternalTransaction(status);
    }

    @TransactionSerializableUseExisting
    public List<CalculationDAO> getAllOrderByCalculationTimestampExternalTransaction() throws OemHIException {
        try {
            return calculationRepository.queryAllOrderByCalculationTimestamp();
        } catch (final Exception exception) {
            throw failed("Querying calculations failed!", exception);
        }
    }

    @TransactionSerializableCreateNew
    public List<CalculationDAO> getAllOrderByCalculationTimestampNewTransaction() throws OemHIException {
        return getAllOrderByCalculationTimestampExternalTransaction();
    }

    @TransactionSerializableUseExisting
    public List<CalculationDAO> getAllOrderByCalculationSyncCounterExternalTransaction() throws OemHIException {
        try {
            return calculationRepository.queryAllOrderByCalculationSyncCounter();
        } catch (final Exception exception) {
            throw failed("Querying calculations failed!", exception);
        }
    }

    @TransactionSerializableCreateNew
    public List<CalculationDAO> getAllOrderByCalculationSyncCounterNewTransaction() throws OemHIException {
        return getAllOrderByCalculationSyncCounterExternalTransaction();
    }

    @TransactionSerializableUseExisting
    public List<CalculationDAO> getByCalculationSinceExternalTransaction(
            @NotNull final Instant calculationTimestampSince) throws OemHIException {
        try {
            return calculationRepository.queryByCalculationSince(calculationTimestampSince);
        } catch (final Exception exception) {
            throw failed("Querying calculations failed!", exception);
        }
    }

    @TransactionSerializableCreateNew
    public List<CalculationDAO> getByCalculationSinceNewTransaction(
            @NotNull final Instant calculationTimestampSince) throws OemHIException {
        return getByCalculationSinceExternalTransaction(calculationTimestampSince);
    }

    @TransactionSerializableUseExisting
    public List<CalculationDAO> getByCalculationUntilExternalTransaction(
            @NotNull final Instant calculationTimestampUntil) throws OemHIException {
        try {
            return calculationRepository.queryByCalculationUntil(calculationTimestampUntil);
        } catch (final Exception exception) {
            throw failed("Querying calculations failed!", exception);
        }
    }

    @TransactionSerializableCreateNew
    public List<CalculationDAO> getByCalculationUntilNewTransaction(
            @NotNull final Instant calculationTimestampUntil) throws OemHIException {
        return getByCalculationUntilExternalTransaction(calculationTimestampUntil);
    }

    @TransactionSerializableUseExisting
    public List<CalculationDAO> getByCalculationSyncCounterSinceExternalTransaction(
            @NotNull final long calculationSyncCounterSince) throws OemHIException {
        try {
            return calculationRepository.queryByCalculationSyncCounterSince(calculationSyncCounterSince);
        } catch (final Exception exception) {
            throw failed("Querying calculations failed!", exception);
        }
    }

    @TransactionSerializableCreateNew
    public List<CalculationDAO> getByCalculationSyncCounterSinceNewTransaction(
            @NotNull final long calculationSyncCounterSince) throws OemHIException {
        return getByCalculationSyncCounterSinceExternalTransaction(calculationSyncCounterSince);
    }

    @TransactionSerializableUseExisting
    public List<CalculationDAO> getByCalculationSyncCounterUntilExternalTransaction(
            @NotNull final long calculationSyncCounterUntil) throws OemHIException {
        try {
            return calculationRepository.queryByCalculationSyncCounterUntil(calculationSyncCounterUntil);
        } catch (final Exception exception) {
            throw failed("Querying calculations failed!", exception);
        }
    }

    @TransactionSerializableCreateNew
    public List<CalculationDAO> getByCalculationSyncCounterUntilNewTransaction(
            @NotNull final long calculationSyncCounterUntil) throws OemHIException {
        return getByCalculationSyncCounterUntilExternalTransaction(calculationSyncCounterUntil);
    }
}
