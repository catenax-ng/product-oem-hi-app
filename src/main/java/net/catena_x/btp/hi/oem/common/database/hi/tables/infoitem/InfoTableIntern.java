package net.catena_x.btp.hi.oem.common.database.hi.tables.infoitem;

import net.catena_x.btp.hi.oem.common.database.hi.base.HITableBase;
import net.catena_x.btp.hi.oem.common.model.enums.InfoKey;
import net.catena_x.btp.hi.oem.util.exceptions.OemHIException;
import net.catena_x.btp.libraries.util.database.annotations.TransactionDefaultCreateNew;
import net.catena_x.btp.libraries.util.database.annotations.TransactionDefaultUseExisting;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotNull;
import java.util.List;

@Component
public class InfoTableIntern extends HITableBase {
    @Autowired private InfoItemRepository infoItemRepository;

    @TransactionDefaultUseExisting
    public void setInfoItemExternTransaction(@NotNull final InfoKey key, @NotNull final String value)
            throws OemHIException {
        try {
            infoItemRepository.insert(key.toString(), value);
        } catch (final Exception exception) {
            throw failed("Inserting info value failed!", exception);
        }
    }

    @TransactionDefaultCreateNew
    public void setInfoItemNewTransaction(@NotNull final InfoKey key, @NotNull final String value)
            throws OemHIException {
        setInfoItemExternTransaction(key, value);
    }

    @TransactionDefaultUseExisting
    public InfoItemDAO getInfoItemExternTransaction(@NotNull final InfoKey key) throws OemHIException {
        try {
            return infoItemRepository.queryByKey(key.toString());
        } catch (final Exception exception) {
            throw failed("Reading info item failed!", exception);
        }
    }

    @TransactionDefaultCreateNew
    public InfoItemDAO getInfoItemNewTransaction(@NotNull final InfoKey key) throws OemHIException {
        return getInfoItemExternTransaction(key);
    }

    @TransactionDefaultUseExisting
    public List<InfoItemDAO> getAllExternTransaction() throws OemHIException {
        try {
            return infoItemRepository.queryAll();
        } catch (final Exception exception) {
            throw failed("Reading all info items failed!", exception);
        }
    }

    @TransactionDefaultCreateNew
    public List<InfoItemDAO> getAllNewTransaction() throws OemHIException {
        return getAllExternTransaction();
    }

    @TransactionDefaultUseExisting
    public void deleteAllExternTransaction() throws OemHIException {
        try {
            infoItemRepository.deleteAll();
        } catch (final Exception exception) {
            throw failed("Deleting all info items failed!", exception);
        }
    }

    @TransactionDefaultCreateNew
    public void deleteAllNewTransaction() throws OemHIException {
        deleteAllExternTransaction();
    }

    @TransactionDefaultUseExisting
    public String getInfoValueExternTransaction(@NotNull final InfoKey key) throws OemHIException {
        return getInfoItemExternTransaction(key).getValue();
    }

    @TransactionDefaultCreateNew
    public String getInfoValueNewTransaction(@NotNull final InfoKey key) throws OemHIException {
        return getInfoValueExternTransaction(key);
    }
}
