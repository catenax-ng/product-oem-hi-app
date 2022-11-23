package net.catena_x.btp.hi.oem.backend.util.exceptions;

import org.jetbrains.annotations.Nullable;

import javax.validation.constraints.NotNull;

public class HIBackendException extends Exception {
    public HIBackendException() {
        super();
    }

    public HIBackendException(@Nullable final String message) {
        super(message);
    }

    public HIBackendException(@Nullable final String message, @Nullable final Throwable cause) {
        super(message, cause);
    }

    public HIBackendException(@Nullable final Throwable cause) {
        super(cause);
    }

    protected HIBackendException(@Nullable final String message, @Nullable final Throwable cause,
                                 @NotNull final boolean enableSuppression,
                                 @NotNull final boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
