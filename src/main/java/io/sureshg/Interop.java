package io.sureshg;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Use @ParametersAreNonnullByDefault and all parameters and return
 * types are never null unless explicitly annotated @Nullable.
 *
 * @author Suresh
 */
public class Interop {

    @Nullable
    String test(@Nonnull String msg) {
        return "Hello, " + msg;
    }
}
