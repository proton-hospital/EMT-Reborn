package emt.bridge;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import github.com.gengyoubo.mfcl.api.LegacyRuntime;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.registries.ForgeRegistries;

/** /emtbridge selftest - checks that legacy EMT items accept and keep Forge Energy. */
final class BridgeCommand {
    private BridgeCommand() {
    }

    static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("emtbridge")
                .requires(source -> source.hasPermission(2))
                .then(Commands.literal("selftest").executes(BridgeCommand::selftest))
                .then(Commands.literal("items").executes(BridgeCommand::items))
                .then(Commands.literal("recipes").executes(ctx -> {
                    // what a client (and JEI) receives: each IC2 recipe set after a network round trip
                    for (String id : new String[]{"ic2:macerator/recipes", "ic2:ore_washer/recipes",
                            "ic2:thermal_centrifuge/recipes", "ic2:metal_former/rolling-recipes"}) {
                        var recipe = ctx.getSource().getServer().getRecipeManager().byKey(new ResourceLocation(id)).orElse(null);
                        if (recipe == null) {
                            say(ctx, "FAIL missing " + id);
                            continue;
                        }
                        try {
                            @SuppressWarnings("unchecked")
                            var serializer = (net.minecraft.world.item.crafting.RecipeSerializer<net.minecraft.world.item.crafting.Recipe<?>>) recipe.getSerializer();
                            var buf = new net.minecraft.network.FriendlyByteBuf(io.netty.buffer.Unpooled.buffer());
                            serializer.toNetwork(buf, recipe);
                            Object back = serializer.fromNetwork(recipe.getId(), buf);
                            java.util.Set<String> found = new java.util.TreeSet<>();
                            collectEmt(back, found, 0, java.util.Collections.newSetFromMap(new java.util.IdentityHashMap<>()));
                            say(ctx, (found.isEmpty() ? "FAIL " : "OK   ") + id + " (client copy) has EMT items " + found);
                        } catch (RuntimeException e) {
                            say(ctx, "FAIL " + id + ": " + e);
                        }
                    }
                    return 1;
                }))
                .then(Commands.literal("machines").then(Commands.literal("start").executes(ctx -> machines(ctx, 0)))
                        .then(Commands.literal("check").executes(ctx -> machines(ctx, 1))))
                .then(Commands.literal("stack").then(Commands.argument("id", com.mojang.brigadier.arguments.StringArgumentType.greedyString()).executes(ctx -> {
                    String id = com.mojang.brigadier.arguments.StringArgumentType.getString(ctx, "id");
                    Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(id));
                    LegacyItems.runtime().ifPresent(rt -> {
                        ItemStack fresh = new ItemStack(item);
                        say(ctx, "fresh " + id + " nbt=" + fresh.getTag() + " -> legacy " + rt.mappings().toLegacy("mfcl:item_stack", fresh).map(o -> rt.snapshot(o).toString()).orElse("none"));
                        int slash = id.indexOf("/meta_");
                        if (slash > 0) {
                            String key = rt.mappings().toLegacy("mfcl:item_stack", fresh).map(o -> rt.snapshot(o).key()).orElse("?");
                            int meta = Integer.parseInt(id.substring(slash + 6));
                            rt.mappings().toHost("mfcl:item_stack", rt.stack(new github.com.gengyoubo.mfcl.api.LegacyStack(key, meta, 1, "")), ItemStack.class).ifPresent(h ->
                                    say(ctx, "legacy " + key + "@" + meta + " -> host " + ForgeRegistries.ITEMS.getKey(h.getItem()) + " nbt=" + h.getTag()));
                        }
                    });
                    return 1;
                })))
                .then(Commands.literal("plate").executes(ctx -> {
                    // craft the thaumium plate with a real (legacy-made) thaumium ingot
                    LegacyItems.runtime().ifPresent(rt -> {
                        ItemStack ingot = rt.mappings().toHost("mfcl:item_stack", rt.stack(new github.com.gengyoubo.mfcl.api.LegacyStack("Thaumcraft:ItemResource", 2, 1, "")), ItemStack.class).orElse(ItemStack.EMPTY);
                        var menu = new net.minecraft.world.inventory.TransientCraftingContainer(new net.minecraft.world.inventory.AbstractContainerMenu(null, -1) {
                            @Override public ItemStack quickMoveStack(net.minecraft.world.entity.player.Player p, int i) { return ItemStack.EMPTY; }
                            @Override public boolean stillValid(net.minecraft.world.entity.player.Player p) { return true; }
                        }, 3, 3);
                        menu.setItem(1, new ItemStack(ForgeRegistries.ITEMS.getValue(new ResourceLocation("ic2:forgehammer"))));
                        menu.setItem(4, ingot);
                        menu.setItem(7, new ItemStack(net.minecraft.world.item.Items.OBSIDIAN));
                        var level = ctx.getSource().getLevel();
                        var found = ctx.getSource().getServer().getRecipeManager().getRecipeFor(net.minecraft.world.item.crafting.RecipeType.CRAFTING, menu, level);
                        ItemStack out = found.map(r -> r.assemble(menu, level.registryAccess())).orElse(ItemStack.EMPTY);
                        String legacy = out.isEmpty() ? "none" : rt.mappings().toLegacy("mfcl:item_stack", out).map(o -> rt.snapshot(o).toString()).orElse("none");
                        boolean ok = legacy.contains("EMT:EMTItems") && legacy.contains("metadata=5");
                        say(ctx, (ok ? "OK   " : "FAIL ") + "thaumium plate craft: ingot=" + ingot.getTag() + " recipe=" + found.map(r -> r.getId().toString()).orElse("none") + " -> " + legacy);
                    });
                    return 1;
                }))
                .then(Commands.literal("uses").then(Commands.argument("path", com.mojang.brigadier.arguments.StringArgumentType.greedyString()).executes(ctx -> {
                    // every recipe that mentions the given emt item path (input or output)
                    String path = com.mojang.brigadier.arguments.StringArgumentType.getString(ctx, "path");
                    var access = ctx.getSource().getServer().registryAccess();
                    for (var recipe : ctx.getSource().getServer().getRecipeManager().getRecipes()) {
                        java.util.Set<String> found = new java.util.TreeSet<>();
                        try {
                            collectEmt(recipe, found, 0, java.util.Collections.newSetFromMap(new java.util.IdentityHashMap<>()));
                        } catch (RuntimeException ignored) {
                        }
                        if (found.contains(path)) {
                            ItemStack result = ItemStack.EMPTY;
                            try {
                                result = recipe.getResultItem(access);
                            } catch (RuntimeException ignored) {
                            }
                            say(ctx, recipe.getId() + " [" + recipe.getClass().getSimpleName() + "] result="
                                    + ForgeRegistries.ITEMS.getKey(result.getItem()) + " emt=" + found);
                        }
                    }
                    return 1;
                })))
                .then(Commands.literal("aspects").executes(ctx -> {
                    if (!net.minecraftforge.fml.ModList.get().isLoaded("tcmodernlib")) {
                        say(ctx, "tc-modern-lib is not installed");
                        return 0;
                    }
                    IC2Aspects.check().forEach(line -> say(ctx, line));
                    return 1;
                }))
                .then(Commands.literal("archer").executes(ctx -> {
                    net.minecraft.core.BlockPos p = ctx.getSource().getLevel().getSharedSpawnPos();
                    net.minecraft.world.entity.animal.SnowGolem g = Spawns.archer(ctx.getSource().getLevel(), p.getX() + 0.5, p.getY() + 1, p.getZ() + 0.5);
                    boolean ok = g != null && g.isAlive() && g.getMaxHealth() == 40f && g.getTags().contains(Spawns.ARCHER_TAG);
                    say(ctx, (ok ? "OK   " : "FAIL ") + "archer: " + (g == null ? "null" : g.getName().getString() + " hp=" + g.getHealth() + "/" + g.getMaxHealth()));
                    if (g != null) {
                        g.hurt(g.damageSources().genericKill(), 1000f);
                        say(ctx, "archer killed, dead=" + g.isDeadOrDying());
                    }
                    return 1;
                }))
                .then(Commands.literal("blocks").executes(ctx -> {
                    net.minecraft.server.level.ServerLevel level = ctx.getSource().getLevel();
                    net.minecraft.core.BlockPos base = level.getSharedSpawnPos().above(60);
                    int n = 0;
                    for (net.minecraft.world.level.block.Block block : ForgeRegistries.BLOCKS) {
                        ResourceLocation id = ForgeRegistries.BLOCKS.getKey(block);
                        if (id == null || !"emt".equals(id.getNamespace())) {
                            continue;
                        }
                        java.util.List<net.minecraft.world.level.block.state.BlockState> states = new java.util.ArrayList<>();
                        net.minecraft.world.level.block.state.properties.Property<?> meta = block.getStateDefinition().getProperty("legacy_meta");
                        if (meta instanceof net.minecraft.world.level.block.state.properties.IntegerProperty ip) {
                            for (int v : ip.getPossibleValues()) {
                                states.add(block.defaultBlockState().setValue(ip, v));
                            }
                        } else {
                            states.add(block.defaultBlockState());
                        }
                        for (net.minecraft.world.level.block.state.BlockState st : states) {
                            net.minecraft.core.BlockPos p = base.offset((n % 16) * 3, 0, (n / 16) * 3);
                            level.setBlock(p.below(), net.minecraft.world.level.block.Blocks.STONE.defaultBlockState(), 3);
                            level.setBlock(p, st, 3);
                            say(ctx, "placed " + st + " at " + p.toShortString() + " -> " + level.getBlockState(p) + " be=" + (level.getBlockEntity(p) == null ? "none" : level.getBlockEntity(p).getClass().getSimpleName()));
                            n++;
                        }
                    }
                    say(ctx, "placed " + n + " EMT block states");
                    return n;
                }))
                .then(Commands.literal("hole").then(Commands.literal("start").executes(ctx -> {
                    net.minecraft.core.BlockPos p = holePos(ctx);
                    int r = LegacyItems.runtime().map(rt -> LegacyItems.bridge(rt)).map(b -> b.testHole(p.getX(), p.getY(), p.getZ())).orElse(-2);
                    say(ctx, "hole start at " + p.toShortString() + ": " + r + " block=" + ctx.getSource().getLevel().getBlockState(p));
                    return 1;
                })).then(Commands.literal("check").executes(ctx -> {
                    net.minecraft.core.BlockPos p = holePos(ctx);
                    net.minecraft.world.level.block.state.BlockState state = ctx.getSource().getLevel().getBlockState(p);
                    say(ctx, (state.is(net.minecraft.world.level.block.Blocks.STONE) ? "OK   " : "FAIL ") + "hole closed: block=" + state);
                    return 1;
                })))
                .then(Commands.literal("tag").then(Commands.argument("tag", net.minecraft.commands.arguments.ResourceLocationArgument.id())
                        .executes(ctx -> {
                            ResourceLocation tag = net.minecraft.commands.arguments.ResourceLocationArgument.getId(ctx, "tag");
                            int n = 0;
                            for (net.minecraft.core.Holder<Item> h : net.minecraft.core.registries.BuiltInRegistries.ITEM
                                    .getTagOrEmpty(net.minecraft.tags.TagKey.create(net.minecraft.core.registries.Registries.ITEM, tag))) {
                                say(ctx, "#" + tag + " -> " + ForgeRegistries.ITEMS.getKey(h.value()));
                                n++;
                            }
                            say(ctx, "#" + tag + ": " + n + " items");
                            return n;
                        })))
                .then(Commands.literal("ids").then(Commands.argument("filter", com.mojang.brigadier.arguments.StringArgumentType.word())
                        .executes(ctx -> {
                            String filter = com.mojang.brigadier.arguments.StringArgumentType.getString(ctx, "filter");
                            int n = 0;
                            for (Item item : ForgeRegistries.ITEMS) {
                                ResourceLocation id = ForgeRegistries.ITEMS.getKey(item);
                                if (id != null && id.toString().contains(filter)) {
                                    say(ctx, id + " | " + new ItemStack(item).getHoverName().getString());
                                    n++;
                                }
                            }
                            return n;
                        }))));
    }

    /** Lists the 1.20.1 ids of all EMT mirror items (useful for recipes and /give). */
    private static int items(CommandContext<CommandSourceStack> ctx) {
        LegacyRuntime runtime = LegacyItems.runtime().orElse(null);
        int n = 0;
        for (Item item : ForgeRegistries.ITEMS) {
            ResourceLocation id = ForgeRegistries.ITEMS.getKey(item);
            if (id != null && LegacyItems.MODERN_NAMESPACE.equals(id.getNamespace())) {
                String key = runtime == null ? "?" : LegacyItems.legacyKey(runtime, item);
                say(ctx, id + " <- " + key + " | " + new ItemStack(item).getHoverName().getString());
                n++;
            }
        }
        say(ctx, n + " EMT items");
        return n;
    }

    private static net.minecraft.core.BlockPos holePos(CommandContext<CommandSourceStack> ctx) {
        net.minecraft.core.BlockPos spawn = ctx.getSource().getLevel().getSharedSpawnPos();
        return new net.minecraft.core.BlockPos(spawn.getX(), 200, spawn.getZ());
    }

    /** Finds item stacks / ingredients of the "emt" namespace anywhere inside a recipe object. */
    private static void collectEmt(Object o, java.util.Set<String> found, int depth, java.util.Set<Object> seen) {
        if (o == null || depth > 6 || !seen.add(o)) {
            return;
        }
        if (o instanceof ItemStack stack) {
            ResourceLocation key = ForgeRegistries.ITEMS.getKey(stack.getItem());
            if (key != null && "emt".equals(key.getNamespace())) {
                found.add(key.getPath());
            }
            return;
        }
        if (o instanceof net.minecraft.world.item.crafting.Ingredient ing) {
            for (ItemStack s : ing.getItems()) {
                collectEmt(s, found, depth + 1, seen);
            }
            return;
        }
        if (o instanceof Iterable<?> it) {
            for (Object e : it) {
                collectEmt(e, found, depth + 1, seen);
            }
            return;
        }
        if (o.getClass().isArray()) {
            if (!o.getClass().getComponentType().isPrimitive()) {
                for (Object e : (Object[]) o) {
                    collectEmt(e, found, depth + 1, seen);
                }
            }
            return;
        }
        if (o instanceof String || o instanceof Number || o instanceof ResourceLocation || o.getClass().isEnum()) {
            return;
        }
        for (Class<?> c = o.getClass(); c != null && c != Object.class; c = c.getSuperclass()) {
            for (java.lang.reflect.Field f : c.getDeclaredFields()) {
                if (java.lang.reflect.Modifier.isStatic(f.getModifiers()) || f.getType().isPrimitive()) {
                    continue;
                }
                try {
                    f.setAccessible(true);
                    collectEmt(f.get(o), found, depth + 1, seen);
                } catch (ReflectiveOperationException | RuntimeException ignored) {
                }
            }
        }
    }

    private static int machines(CommandContext<CommandSourceStack> ctx, int phase) {
        net.minecraft.core.BlockPos p = holePos(ctx).above(5);
        String r = LegacyItems.runtime().map(rt -> LegacyItems.bridge(rt)).map(b -> b.testMachines(p.getX(), p.getY(), p.getZ(), phase)).orElse("no bridge");
        say(ctx, "machines " + (phase == 0 ? "start" : "check") + ": " + r);
        return 1;
    }

    private static void say(CommandContext<CommandSourceStack> ctx, String text) {
        ctx.getSource().sendSuccess(() -> Component.literal(text), false);
        EmtBridge.LOGGER.info("[selftest] {}", text);
    }

    private static int selftest(CommandContext<CommandSourceStack> ctx) {
        LegacyRuntime runtime = LegacyItems.runtime().orElse(null);
        if (runtime == null) {
            say(ctx, "FAIL: MFCL legacy runtime is not available");
            return 0;
        }
        say(ctx, "MFCL runtime " + runtime.sourceVersion() + ", legacy mods: " + runtime.loadedMods());
        FeBridgeAccess bridge = LegacyItems.bridge(runtime);
        if (bridge == null) {
            say(ctx, "FAIL: legacy emt.compat.FeBridge not reachable (is EMT-Reborn-legacy in mods/1.7.10?)");
            return 0;
        }
        int tested = 0;
        for (Item item : ForgeRegistries.ITEMS) {
            ResourceLocation id = ForgeRegistries.ITEMS.getKey(item);
            if (id == null || !LegacyItems.MODERN_NAMESPACE.equals(id.getNamespace())) {
                continue;
            }
            ItemStack stack = new ItemStack(item);
            IEnergyStorage energy = stack.getCapability(ForgeCapabilities.ENERGY).orElse(null);
            String key = LegacyItems.legacyKey(runtime, item);
            if (energy == null) {
                say(ctx, id + " (" + key + "): no energy (not electric)");
                continue;
            }
            int capacity = energy.getMaxEnergyStored();
            int simulated = energy.receiveEnergy(500, true);
            int received = energy.receiveEnergy(500, false);
            int stored = energy.getEnergyStored();
            ItemStack copy = stack.copy();
            int storedCopy = copy.getCapability(ForgeCapabilities.ENERGY).map(IEnergyStorage::getEnergyStored).orElse(-1);
            int extracted = energy.extractEnergy(5, false);
            boolean ok = capacity > 0 && simulated == received && received > 0 && stored == received && storedCopy == stored;
            say(ctx, (ok ? "OK   " : "FAIL ") + id + " (" + key + "): capacity=" + capacity + " sim=" + simulated
                    + " received=" + received + " stored=" + stored + " copyStored=" + storedCopy
                    + " extracted=" + extracted + " canExtract=" + energy.canExtract() + " nbt=" + stack.getTag());
            armorTest(ctx, id, stack, capacity);
            float[] boots = Boots.values(stack);
            if (boots != null) {
                say(ctx, "OK   " + id + " boots: speedBonus=" + boots[0] + " jumpBonus=" + boots[1]);
            }
            tested++;
        }
        net.minecraft.world.item.Item chestItem = ForgeRegistries.ITEMS.getValue(ResourceLocation.fromNamespaceAndPath("emt", "item_armor_quantum_chestplate"));
        if (chestItem != null) {
            ItemStack chest = new ItemStack(chestItem);
            int a = LegacyCall.on(chest, (b, s) -> b.chestAttach(s, 1, 0, "{id:\"ic2:armorjetpackelectric\",Count:1b}"), true).orElse(-1);
            int w = ChestParts.wingMode(chest);
            String jet = LegacyCall.obj(chest, FeBridgeAccess::chestJetpack).orElse("?");
            int d = LegacyCall.on(chest, FeBridgeAccess::chestDetach, true).orElse(-1);
            String real = "{id:\"ic2:armorjetpackelectric\",Count:1b,tag:{Damage:0,charge:30000.0d}}";
            ItemStack chest2 = new ItemStack(chestItem);
            int a2 = LegacyCall.on(chest2, (b, s) -> b.chestAttach(s, 1, 0, real), true).orElse(-1);
            String back2 = LegacyCall.obj(chest2, FeBridgeAccess::chestJetpack).orElse("?");
            say(ctx, (a2 == 1 && real.equals(back2) ? "OK   " : "FAIL ") + "chest jetpack with tag: " + back2 + " snbt=" + chest2.getTagElement("mfcl_legacy").getString("snbt"));
            ItemStack broken = new ItemStack(chestItem);
            broken.getOrCreateTagElement("mfcl_legacy").putString("snbt", "{wing:1b,jetpackModern:\"{id:\\\"ic2:armorjetpackelectric\\\",tag:{Damage:0,charge:30000.0d}}\",charge:5.0d,}");
            boolean fixed = NbtRepair.repair(broken);
            String back3 = LegacyCall.obj(broken, FeBridgeAccess::chestJetpack).orElse("?");
            say(ctx, (fixed && back3.contains("charge:30000.0d") ? "OK   " : "FAIL ") + "old chest repaired=" + fixed + " jetpack=" + back3);
            // the forms really found in a save: unescaped (written by MFCL) and half-hexed by 0.3.6
            String item = "{Count:1b,ForgeCaps:{Parent:{storage:30000.0d}},id:\"ic2:armorjetpackelectric\",tag:{Damage:0,charge:30000.0d}}";
            String[] saved = {
                    "{charge:2000000.0d,jetpackModern:\"" + item + "\",wing:1b,}",
                    "{charge:2000000.0d,jetpackHex:\"" + NbtRepair.hex("{Count:1b,ForgeCaps:{Parent:{storage:30000.0d}},id:") + "\"ic2:armorjetpackelectric\",tag:{Damage:0,charge:30000.0d}}\",wing:1b,}"};
            for (String snbt : saved) {
                ItemStack old = new ItemStack(chestItem);
                old.getOrCreateTagElement("mfcl_legacy").putString("snbt", snbt);
                boolean rep = NbtRepair.repair(old);
                boolean again = NbtRepair.repair(old);
                String got = LegacyCall.obj(old, FeBridgeAccess::chestJetpack).orElse("?");
                int wing = ChestParts.wingMode(old);
                int charge = LegacyCall.intOn(old, FeBridgeAccess::stored);
                say(ctx, (rep && !again && item.equals(got) && wing == 1 && charge == 2000000 ? "OK   " : "FAIL ") + "saved chest repaired=" + rep
                        + " again=" + again + " wing=" + wing + " charge=" + charge + " jetpack=" + got + " snbt=" + old.getTagElement("mfcl_legacy").getString("snbt"));
            }
            say(ctx, "chest parts: attach=" + a + " wing=" + w + " jetpack=" + jet + " detach=" + d + " after=" + ChestParts.wingMode(chest));
        }
        Item gogglesItem = ForgeRegistries.ITEMS.getValue(ResourceLocation.fromNamespaceAndPath("emt", "quantum_goggles_revealing"));
        if (gogglesItem != null) {
            ItemStack g = new ItemStack(gogglesItem);
            g.getOrCreateTagElement("mfcl_legacy").putString("snbt", "{charge:5000.0d,}");
            int used = LegacyCall.on(g, (b, s) -> b.use(s, 1000), true).orElse(-1);
            int left = LegacyCall.intOn(g, FeBridgeAccess::stored);
            int tooMuch = LegacyCall.on(g, (b, s) -> b.use(s, 99999), true).orElse(-1);
            int drained = LegacyCall.on(g, (b, s) -> b.drain(s, 99999), true).orElse(-1);
            boolean ok = used == 1000 && left == 4000 && tooMuch == 0 && drained == 4000 && LegacyCall.intOn(g, FeBridgeAccess::stored) == 0;
            say(ctx, (ok ? "OK   " : "FAIL ") + "armour power: use=" + used + " left=" + left + " useTooMuch=" + tooMuch + " drain=" + drained);
        }
        say(ctx, "selftest finished, electric EMT items tested: " + tested);
        return tested;
    }

    /** Fully charges a copy, then asks the legacy ISpecialArmor what a 4 HP hit costs. */
    private static void armorTest(CommandContext<CommandSourceStack> ctx, ResourceLocation id, ItemStack original, int capacity) {
        ItemStack stack = original.copy();
        if (!LegacyCall.boolOn(stack, (b, s) -> b.isSpecialArmor(s) ? 1 : 0)) {
            return;
        }
        stack.getOrCreateTagElement("mfcl_legacy").putString("snbt", "{charge:" + capacity + ".0d,}");
        IEnergyStorage energy = stack.getCapability(ForgeCapabilities.ENERGY).orElseThrow(IllegalStateException::new);
        int before = energy.getEnergyStored();
        int display = LegacyCall.intOn(stack, (b, s) -> b.armorDisplay(s, 3));
        int ratio = LegacyCall.intOn(stack, (b, s) -> b.armorRatioMicros(s, 4.0f, 3));
        int max = LegacyCall.intOn(stack, (b, s) -> b.armorMaxMilli(s, 4.0f, 3));
        int absorbedMilli = (int) Math.round(4.0 * Math.min(ratio / 1_000_000.0, max / 1000.0 / 4.0) * 1000);
        LegacyCall.on(stack, (b, s) -> b.armorDamage(s, absorbedMilli, 3), true);
        int after = energy.getEnergyStored();
        boolean ok = before == capacity && ratio > 0 && max > 0 && after < before;
        say(ctx, (ok ? "OK   " : "FAIL ") + id + " armour: display=" + display + " ratio=" + ratio / 1_000_000.0
                + " maxHP=" + max / 1000.0 + " hit 4HP absorbs " + absorbedMilli / 1000.0 + "HP, charge " + before + " -> " + after);
        int empty = LegacyCall.intOn(new ItemStack(original.getItem()), (b, s) -> b.armorMaxMilli(s, 4.0f, 3));
        say(ctx, (empty == 0 ? "OK   " : "FAIL ") + id + " armour without charge absorbs max " + empty / 1000.0 + "HP");
    }
}
