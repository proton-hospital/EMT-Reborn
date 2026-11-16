package emt.bridge;

import github.com.gengyoubo.mfcl.api.spi.LegacyItemProvider;

/**
 * Gives every metadata variant of EMT's multi-variant legacy items (materials, solar panels,
 * machines, essentia generators, baubles...) its own 1.20.1 item id, e.g. emt:emt_items/meta_5,
 * the same way TC4: Modern does it for Thaumcraft. Needed for JEI, tags and JSON recipes.
 */
final class EmtItemSplits implements LegacyItemProvider {
    @Override
    public ItemSplit split(String legacyKey, ItemDeclaration declaration) {
        if (!legacyKey.startsWith(LegacyItems.LEGACY_MOD + ":")) {
            return null;
        }
        return declaration.maxDamage() <= 0 && declaration.metadata().size() >= 2 ? LegacyItemProvider.byMetadata(declaration) : null;
    }
}
