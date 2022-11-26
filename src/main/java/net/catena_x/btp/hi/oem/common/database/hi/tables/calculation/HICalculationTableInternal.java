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
public class HICalculationTableInternal extends HITableBase {
    @Autowired private HICalculationRepository HICalculationRepository;

    @TransactionSerializableUseExisting
    public void insertExternalTransaction(@NotNull final String id, @NotNull final Instant calculationTimestamp,
                                          @NotNull final long calculationSyncCounter,
                                          @NotNull final CalculationStatus status) throws OemHIException {
        try {
            HICalculationRepository.insert(id, calculationTimestamp, calculationSyncCounter, status.toString());
        } catch (final Exception exception) {
            throw failed("Inserting calculation failed!", exception);
        }
    }

    @TransactionSerializableCreateNew
    public void insertNewTransaction(@NotNull final String id, @NotNull final Instant calculationTimestamp,
                                     @NotNull final long calculationSyncCounter,
                                     @NotNull final CalculationStatus status) throws OemHIException {
        insertExternalTransaction(id, calculationTimestamp, calculationSyncCounter, status);
    }

    @TransactionSerializableUseExisting
    public String insertGetIdExternalTransaction(@NotNull final Instant calculationTimestamp,
                                                 @NotNull final long calculationSyncCounter,
                                                 @NotNull final CalculationStatus status) throws OemHIException {
        try {
            final String id = generateNewId();
            HICalculationRepository.insert(id, calculationTimestamp, calculationSyncCounter, status.toString());
            return id;
        } catch (final Exception exception) {
            throw failed("Inserting calculation failed!", exception);
        }
    }

    @TransactionSerializableCreateNew
    public String insertGetIdNewTransaction(@NotNull final Instant calculationTimestamp,
                                            @NotNull final long calculationSyncCounter,
                                            @NotNull final CalculationStatus status) throws OemHIException {
        return insertGetIdExternalTransaction(calculationTimestamp, calculationSyncCounter, status);
    }

    @TransactionSerializableUseExisting
    public void createNowExternalTransaction(@NotNull final String id, @NotNull final long calculationSyncCounter)
            throws OemHIException {
        try {
            HICalculationRepository.createNow(id, calculationSyncCounter, CalculationStatus.CREATED.toString());
        } catch (final Exception exception) {
            throw failed("Inserting calculation failed!", exception);
        }
    }

    @TransactionSerializableCreateNew
    public void createNowNewTransaction(@NotNull final String id, @NotNull final long calculationSyncCounter)
            throws OemHIException {
        createNowExternalTransaction(id, calculationSyncCounter);
    }

    @TransactionSerializableUseExisting
    public String createNowGetIdExternalTransaction(@NotNull final long calculationSyncCounter) throws OemHIException {
        try {
            final String id = generateNewId();
            HICalculationRepository.createNow(id, calculationSyncCounter, CalculationStatus.CREATED.toString());
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
            HICalculationRepository.updateStatus(id, newStatus.toString());
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
            HICalculationRepository.setMessage(id, message);
        } catch (final Exception exception) {
            throw failed("Updating message for calculation failed!", exception);
        }
    }

    @TransactionSerializableCreateNew
    public void setMessageNewTransaction(@NotNull final String id, @NotNull final String message)
            throws OemHIException {
        setMessageExternalTransaction(id, message);
    }

    @TransactionSerializableUseExisting
    public void deleteByIdExternalTransaction(@NotNull final String id) throws OemHIException {
        try {
            HICalculationRepository.deleteById(id);
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
            HICalculationRepository.deleteByStatus(status.toString());
        } catch (final Exception exception) {
            throw failed("Deleting calculation failed!", exception);
        }
    }

    @TransactionSerializableCreateNew
    public void deleteByStatusNewTransaction(@NotNull final CalculationStatus status) throws OemHIException {
        deleteByStatusExternalTransaction(status);
    }

    @TransactionSerializableUseExisting
    public void deleteCalculatedUntilExternalTransaction(@NotNull final Instant calculatedUntil) throws OemHIException {
        try {
            HICalculationRepository.deleteCalculatedUntil(calculatedUntil);
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
            HICalculationRepository.deleteCalculationSyncCounterUntil(calculationSyncCounter);
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
    public List<HICalculationDAO> getAllExternalTransaction() throws OemHIException {
        try {
            return HICalculationRepository.queryAll();
        } catch (final Exception exception) {
            throw failed("Querying calculations failed!", exception);
        }
    }

    @TransactionSerializableCreateNew
    public List<HICalculationDAO> getAllNewTransaction() throws OemHIException {
        return getAllExternalTransaction();
    }

    @TransactionSerializableUseExisting
    public HICalculationDAO getByIdExternalTransaction(@NotNull final String id) throws OemHIException {
        try {
            return HICalculationRepository.queryById(id);
        } catch (final Exception exception) {
            throw failed("Querying calculation by id failed!", exception);
        }
    }

    @TransactionSerializableCreateNew
    public HICalculationDAO getByIdNewTransaction(@NotNull final String id) throws OemHIException {
        return getByIdExternalTransaction(id);
    }

    @TransactionSerializableUseExisting
    public List<HICalculationDAO> getByStatusExternalTransaction(@NotNull final CalculationStatus status)
            throws OemHIException {
        try {
            return HICalculationRepository.queryByStatus(status.toString());
        } catch (final Exception exception) {
            throw failed("Querying calculations by status failed!", exception);
        }
    }

    @TransactionSerializableCreateNew
    public List<HICalculationDAO> getByStatusNewTransaction(@NotNull final CalculationStatus status)
            throws OemHIException {
        return getByStatusExternalTransaction(status);
    }

    @TransactionSerializableUseExisting
    public List<HICalculationDAO> getAllOrderByCalculationTimestampExternalTransaction() throws OemHIException {
        try {
            return HICalculationRepository.queryAllOrderByCalculationTimestamp();
        } catch (final Exception exception) {
            throw failed("Querying calculations failed!", exception);
        }
    }

    @TransactionSerializableCreateNew
    public List<HICalculationDAO> getAllOrderByCalculationTimestampNewTransaction() throws OemHIException {
        return getAllOrderByCalculationTimestampExternalTransaction();
    }

    @TransactionSerializableUseExisting
    public List<HICalculationDAO> getAllOrderByCalculationSyncCounterExternalTransaction() throws OemHIException {
        try {
            return HICalculationRepository.queryAllOrderByCalculationSyncCounter();
        } catch (final Exception exception) {
            throw failed("Querying calculations failed!", exception);
        }
    }

    @TransactionSerializableCreateNew
    public List<HICalculationDAO> getAllOrderByCalculationSyncCounterNewTransaction() throws OemHIException {
        return getAllOrderByCalculationSyncCounterExternalTransaction();
    }

    @TransactionSerializableUseExisting
    public List<HICalculationDAO> getByCalculationSinceExternalTransaction(
            @NotNull final Instant calculationTimestampSince) throws OemHIException {
        try {
            return HICalculationRepository.queryByCalculationSince(calculationTimestampSince);
        } catch (final Exception exception) {
            throw failed("Querying calculations failed!", exception);
        }
    }

    @TransactionSerializableCreateNew
    public List<HICalculationDAO> getByCalculationSinceNewTransaction(
            @NotNull final Instant calculationTimestampSince) throws OemHIException {
        return getByCalculationSinceExternalTransaction(calculationTimestampSince);
    }

    @TransactionSerializableUseExisting
    public List<HICalculationDAO> getByCalculationUntilExternalTransaction(
            @NotNull final Instant calculationTimestampUntil) throws OemHIException {
        try {
            return HICalculationRepository.queryByCalculationUntil(calculationTimestampUntil);
        } catch (final Exception exception) {
            throw failed("Querying calculations failed!", exception);
        }
    }

    @TransactionSerializableCreateNew
    public List<HICalculationDAO> getByCalculationUntilNewTransaction(
            @NotNull final Instant calculationTimestampUntil) throws OemHIException {
        return getByCalculationUntilExternalTransaction(calculationTimestampUntil);
    }

    @TransactionSerializableUseExisting
    public List<HICalculationDAO> getByCalculationSyncCounterSinceExternalTransaction(
            @NotNull final long calculationSyncCounterSince) throws OemHIException {
        try {
            return HICalculationRepository.queryByCalculationSyncCounterSince(calculationSyncCounterSince);
        } catch (final Exception exception) {
            throw failed("Querying calculations failed!", exception);
        }
    }

    @TransactionSerializableCreateNew
    public List<HICalculationDAO> getByCalculationSyncCounterSinceNewTransaction(
            @NotNull final long calculationSyncCounterSince) throws OemHIException {
        return getByCalculationSyncCounterSinceExternalTransaction(calculationSyncCounterSince);
    }

    @TransactionSerializableUseExisting
    public List<HICalculationDAO> getByCalculationSyncCounterUntilExternalTransaction(
            @NotNull final long calculationSyncCounterUntil) throws OemHIException {
        try {
            return HICalculationRepository.queryByCalculationSyncCounterUntil(calculationSyncCounterUntil);
        } catch (final Exception exception) {
            throw failed("Querying calculations failed!", exception);
        }
    }

    @TransactionSerializableCreateNew
    public List<HICalculationDAO> getByCalculationSyncCounterUntilNewTransaction(
            @NotNull final long calculationSyncCounterUntil) throws OemHIException {
        return getByCalculationSyncCounterUntilExternalTransaction(calculationSyncCounterUntil);
    }
}
