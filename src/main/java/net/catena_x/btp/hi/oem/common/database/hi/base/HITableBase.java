package net.catena_x.btp.hi.oem.common.database.hi.base;

import net.catena_x.btp.hi.oem.util.exceptions.OemHIException;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class HITableBase {
    protected String generateNewId() {
        return UUID.randomUUID().toString();
    }

    protected OemHIException failed() {
        return failed("Database statement failed!");
    }

    protected OemHIException failed(@Nullable String message) {
        return new OemHIException(message);
    }

    protected OemHIException failed(@Nullable String message, @Nullable Throwable cause) {
        return new OemHIException(message, cause);
    }

    protected OemHIException failed(@Nullable Throwable cause) {
        return new OemHIException(cause);
    }
}
