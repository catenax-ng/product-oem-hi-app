package net.catena_x.btp.hi.oem.common.database.hi.tables.infoitem;

import net.catena_x.btp.hi.oem.common.database.hi.base.HITableBase;
import net.catena_x.btp.hi.oem.common.model.enums.HIInfoKey;
import net.catena_x.btp.hi.oem.util.exceptions.OemHIException;
import net.catena_x.btp.libraries.util.database.annotations.TransactionDefaultCreateNew;
import net.catena_x.btp.libraries.util.database.annotations.TransactionDefaultUseExisting;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotNull;
import java.util.List;

@Component
public class HIInfoTableInternal extends HITableBase {
    @Autowired private HIInfoItemRepository HIInfoItemRepository;

    @TransactionDefaultUseExisting
    public void setInfoItemExternalTransaction(@NotNull final HIInfoKey key, @NotNull final String value)
            throws OemHIException {
        try {
            HIInfoItemRepository.insert(key.toString(), value);
        } catch (final Exception exception) {
            throw failed("Inserting info value failed!", exception);
        }
    }

    @TransactionDefaultCreateNew
    public void setInfoItemNewTransaction(@NotNull final HIInfoKey key, @NotNull final String value)
            throws OemHIException {
        setInfoItemExternalTransaction(key, value);
    }
    
    @TransactionDefaultUseExisting
    public void updateInfoItemExternalTransaction(@NotNull final HIInfoKey key, @NotNull final String value)
            throws OemHIException {
        try {
            HIInfoItemRepository.update(key.toString(), value);
        } catch (final Exception exception) {
            throw failed("Updating info value failed!", exception);
        }
    }

    @TransactionDefaultCreateNew
    public void updateInfoItemNewTransaction(@NotNull final HIInfoKey key, @NotNull final String value)
            throws OemHIException {
        updateInfoItemExternalTransaction(key, value);
    }
    
    @TransactionDefaultUseExisting
    public HIInfoItemDAO getInfoItemExternalTransaction(@NotNull final HIInfoKey key) throws OemHIException {
        try {
            return HIInfoItemRepository.queryByKey(key.toString());
        } catch (final Exception exception) {
            throw failed("Reading info item failed!", exception);
        }
    }

    @TransactionDefaultCreateNew
    public HIInfoItemDAO getInfoItemNewTransaction(@NotNull final HIInfoKey key) throws OemHIException {
        return getInfoItemExternalTransaction(key);
    }

    @TransactionDefaultUseExisting
    public List<HIInfoItemDAO> getAllExternalTransaction() throws OemHIException {
        try {
            return HIInfoItemRepository.queryAll();
        } catch (final Exception exception) {
            throw failed("Reading all info items failed!", exception);
        }
    }

    @TransactionDefaultCreateNew
    public List<HIInfoItemDAO> getAllNewTransaction() throws OemHIException {
        return getAllExternalTransaction();
    }

    @TransactionDefaultUseExisting
    public void deleteAllExternalTransaction() throws OemHIException {
        try {
            HIInfoItemRepository.deleteAll();
        } catch (final Exception exception) {
            throw failed("Deleting all info items failed!", exception);
        }
    }

    @TransactionDefaultCreateNew
    public void deleteAllNewTransaction() throws OemHIException {
        deleteAllExternalTransaction();
    }

    @TransactionDefaultUseExisting
    public void deleteExternalTransaction(@NotNull final HIInfoKey key) throws OemHIException {
        try {
            HIInfoItemRepository.delete(key.toString());
        } catch (final Exception exception) {
            throw failed("Deleting info item failed!", exception);
        }
    }

    @TransactionDefaultCreateNew
    public void deleteNewTransaction(@NotNull final HIInfoKey key) throws OemHIException {
        deleteExternalTransaction(key);
    }

    @TransactionDefaultUseExisting
    public String getInfoValueExternalTransaction(@NotNull final HIInfoKey key) throws OemHIException {
        return getInfoItemExternalTransaction(key).getValue();
    }

    @TransactionDefaultCreateNew
    public String getInfoValueNewTransaction(@NotNull final HIInfoKey key) throws OemHIException {
        return getInfoValueExternalTransaction(key);
    }
}
