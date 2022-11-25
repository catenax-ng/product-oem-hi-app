package net.catena_x.btp.hi.oem.util.exceptions;

import org.jetbrains.annotations.Nullable;

import javax.validation.constraints.NotNull;

public class OemHIException extends Exception {
    public OemHIException() {
        super();
    }

    public OemHIException(@Nullable final String message) {
        super(message);
    }

    public OemHIException(@Nullable final String message, @Nullable final Throwable cause) {
        super(message, cause);
    }

    public OemHIException(@Nullable final Throwable cause) {
        super(cause);
    }

    protected OemHIException(@Nullable final String message, @Nullable final Throwable cause,
                             @NotNull final boolean enableSuppression,
                             @NotNull final boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
