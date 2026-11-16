package emt.bridge;

import github.com.gengyoubo.mfcl.api.LegacyObject;
import github.com.gengyoubo.mfcl.api.LegacyRuntime;
import java.util.Optional;
import java.util.function.ToIntBiFunction;
import net.minecraft.world.item.ItemStack;

/** Runs a FeBridge call on a legacy copy of a 1.20.1 EMT mirror stack, optionally writing the result back. */
final class LegacyCall {
    private LegacyCall() {
    }

    static Optional<Integer> on(ItemStack stack, ToIntBiFunction<FeBridgeAccess, LegacyObject> action, boolean writeBack) {
        if (stack.isEmpty() || stack.getCount() != 1 || !LegacyItems.isEmtItem(stack.getItem())) {
            return Optional.empty();
        }
        Optional<LegacyRuntime> runtime = LegacyItems.runtime();
        if (runtime.isEmpty()) {
            return Optional.empty();
        }
        try {
            LegacyRuntime rt = runtime.get();
            FeBridgeAccess bridge = LegacyItems.bridge(rt);
            String key = bridge == null ? null : LegacyItems.legacyKey(rt, stack.getItem());
            if (key == null) {
                return Optional.empty();
            }
            LegacyObject legacy = LegacyItems.materialize(rt, LegacyItems.toLegacy(key, stack));
            int result = action.applyAsInt(bridge, legacy);
            if (writeBack && result > 0) {
                LegacyItems.writeBack(stack, rt.snapshot(legacy));
            }
            return Optional.of(result);
        } catch (RuntimeException e) {
            EmtBridge.warnOnce("call", "EMT legacy call failed for " + stack, e);
            return Optional.empty();
        }
    }

    static int intOn(ItemStack stack, ToIntBiFunction<FeBridgeAccess, LegacyObject> action) {
        return on(stack, action, false).orElse(0);
    }

    static boolean boolOn(ItemStack stack, ToIntBiFunction<FeBridgeAccess, LegacyObject> action) {
        return on(stack, action, false).map(v -> v != 0).orElse(false);
    }

    /** Like {@link #on} for methods returning an object (e.g. a String); never writes back. */
    static <T> Optional<T> obj(ItemStack stack, java.util.function.BiFunction<FeBridgeAccess, LegacyObject, T> action) {
        if (stack.isEmpty() || !LegacyItems.isEmtItem(stack.getItem())) {
            return Optional.empty();
        }
        Optional<LegacyRuntime> runtime = LegacyItems.runtime();
        if (runtime.isEmpty()) {
            return Optional.empty();
        }
        try {
            LegacyRuntime rt = runtime.get();
            FeBridgeAccess bridge = LegacyItems.bridge(rt);
            String key = bridge == null ? null : LegacyItems.legacyKey(rt, stack.getItem());
            if (key == null) {
                return Optional.empty();
            }
            return Optional.ofNullable(action.apply(bridge, LegacyItems.materialize(rt, LegacyItems.toLegacy(key, stack))));
        } catch (RuntimeException e) {
            EmtBridge.warnOnce("call-obj", "EMT legacy call failed for " + stack, e);
            return Optional.empty();
        }
    }
}
