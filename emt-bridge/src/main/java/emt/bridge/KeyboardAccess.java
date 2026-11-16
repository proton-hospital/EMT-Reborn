package emt.bridge;

import github.com.gengyoubo.mfcl.api.mla.LegacyMethod;
import github.com.gengyoubo.mfcl.api.mla.LegacyType;

/** MLA contract for emt.compat.EMTKeyboard in the legacy EMT jar. */
@LegacyType("emt.compat.EMTKeyboard")
public interface KeyboardAccess {
    int JUMP = 1;
    int SNEAK = 1 << 1;
    int FORWARD = 1 << 2;
    int MODE_SWITCH = 1 << 3;
    int BOOST = 1 << 4;
    int ALT = 1 << 5;
    int UNEQUIP = 1 << 6;

    @LegacyMethod(name = "setState", isStatic = true, descriptor = "(Ljava/lang/String;I)I")
    int setState(String uuid, int mask);
}
