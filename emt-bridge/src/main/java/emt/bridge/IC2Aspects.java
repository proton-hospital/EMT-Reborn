package emt.bridge;

import github.com.gengyoubo.tcmodern.api.scan.AspectValues;
import github.com.gengyoubo.tcmodern.api.scan.ScanKey;
import github.com.gengyoubo.tcmodern.api.scan.TcScanApi;
import github.com.gengyoubo.tcmodern.api.scan.TcScanRegistry;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.TieredItem;
import net.minecraftforge.registries.ForgeRegistries;

/**
 * Thaumcraft 4 aspects for IndustrialCraft 2 items and blocks. The IC2 port is a native 1.20.1 mod,
 * so Thaumcraft (running in MFCL) knows nothing about it: its items could not be scanned, put into
 * a crucible or used for research. The values are given to tc-modern-lib's scan registry as
 * defaults (a modpack can still override them); tc-modern-lib hands them on to the original
 * Thaumcraft object tags. Only loaded when tc-modern-lib is present.
 */
public final class IC2Aspects {
    /** The aspects of Thaumcraft 4.2.3.5; an unknown tag would make tc-modern-lib drop every rule. */
    static final Set<String> TC4 = Set.of("aer", "terra", "ignis", "aqua", "ordo", "perditio", "vacuos", "lux",
            "tempestas", "motus", "gelum", "vitreus", "victus", "venenum", "potentia", "permutatio", "metallum",
            "mortuus", "volatus", "tenebrae", "spiritus", "sano", "iter", "alienis", "praecantatio", "auram", "vitium",
            "limus", "herba", "arbor", "bestia", "corpus", "exanimis", "cognitio", "sensus", "humanus", "messis",
            "perfodio", "instrumentum", "meto", "telum", "tutamen", "fames", "lucrum", "fabrico", "pannus", "machina",
            "vinculum");

    /** Explicit values: registry path = aspect amount pairs. */
    private static final String[] TABLE = {
            // rubber
            "rubber=limus 1 motus 1", "resin=limus 2 arbor 1", "blockrubber=limus 2 motus 2 tutamen 1",
            "blockharz=limus 4 arbor 1", "rubberwood=arbor 4 limus 1", "rubbersapling=arbor 1 herba 2 victus 1",
            "blockrubleaves=herba 1 arbor 1", "treetap=arbor 2 instrumentum 1",
            "treetapelectric=arbor 2 instrumentum 2 potentia 2",
            // components
            "partcircuit=potentia 2 machina 2 cognitio 1", "partcircuitadv=potentia 4 machina 3 cognitio 3 lux 1",
            "partcoil=potentia 2 metallum 2 vinculum 1", "partelemotor=machina 3 motus 3 potentia 2 metallum 2",
            "powerunit=machina 4 potentia 4 motus 2", "powerunitsmall=machina 2 potentia 2 motus 1",
            "heatconductor=ignis 2 metallum 2 iter 1", "copperboiler=ignis 2 aqua 2 metallum 4",
            "advancedalloy=metallum 4 tutamen 3 ordo 1", "mixed_metal_ingot=metallum 4 ordo 1 tutamen 1",
            "iridiumshard=metallum 1 lucrum 1 ordo 1", "iridium_ore=metallum 4 lucrum 4 ordo 2",
            "iridium_plate=metallum 8 lucrum 6 tutamen 8 ordo 4",
            "coalball=ignis 4 potentia 2 terra 1", "compressedcoalball=ignis 6 potentia 4 ordo 2",
            "coalchunk=ignis 8 potentia 6 ordo 4", "industrialdiamond=vitreus 4 lucrum 2 ordo 2 ignis 1",
            "carbonmesh=ignis 2 ordo 2 pannus 1", "partcarbonfibre=ignis 1 ordo 1 pannus 1",
            "carbon_plate=tutamen 3 ordo 2 ignis 2", "rawcrystalmemory=vitreus 2 cognitio 2",
            "crystalmemory=vitreus 3 cognitio 4 machina 1", "turningblanks=metallum 2 fabrico 1",
            "turningblankswood=arbor 2 fabrico 1", "ironshaft=metallum 4 machina 1 motus 1",
            "steelshaft=metallum 5 ordo 1 machina 1 motus 1", "partcfpowder=terra 2 aer 1",
            "silicon_dioxide_dust=vitreus 2 terra 1", "energium_dust=potentia 3 lucrum 1",
            "upgradekit=machina 4 potentia 4", "blockmachine=machina 2 metallum 6",
            "blockadvmachine=machina 4 metallum 8 ordo 2", "miningpipe=metallum 1 perfodio 1 iter 1",
            "miningtip=metallum 1 perfodio 2",
            // energy storage items
            "batsu=potentia 2 metallum 1 perditio 1", "batredischarged=potentia 4 metallum 2",
            "advbat=potentia 6 metallum 3 ignis 1", "energycrystal=potentia 8 vitreus 4 lucrum 2",
            "batlamacrystal=potentia 16 vitreus 6 sensus 4 cognitio 2",
            "batchargere=potentia 4 metallum 2 permutatio 2", "batchargeadv=potentia 6 metallum 3 permutatio 2",
            "batchargecrystal=potentia 8 vitreus 4 permutatio 2",
            "batchargelamacrystal=potentia 16 vitreus 6 permutatio 4",
            // cells and cans
            "emptycell=vacuos 1 metallum 1", "fuelrod=vacuos 1 metallum 2", "watercell=aqua 2 metallum 1",
            "aircell=aer 3 metallum 1", "celllava=ignis 3 terra 1 metallum 1",
            "cellpahoehoelava=ignis 2 terra 2 metallum 1", "cellcoolant=gelum 3 aqua 1 metallum 1",
            "cellhotcoolant=ignis 2 aqua 1 metallum 1", "celldistilledwater=aqua 3 ordo 1 metallum 1",
            "cellsteam=aqua 2 aer 2 metallum 1", "cellsuperheatedsteam=aqua 2 aer 2 ignis 1 metallum 1",
            "celluumatter=permutatio 4 ordo 4 alienis 2 metallum 1", "cellbiomass=herba 3 victus 1 metallum 1",
            "cellbiogas=aer 2 ignis 1 herba 1 metallum 1", "cellwaterelectro=aqua 2 potentia 2 metallum 1",
            "cellcf=terra 2 aer 1 metallum 1", "fluidcell=vacuos 2 metallum 2 aqua 1",
            "hydrationcell=aqua 4 victus 1", "tincan=vacuos 1 metallum 1", "filledtincan=fames 2 metallum 1",
            // nuclear
            "uran=potentia 6 venenum 4 metallum 3", "uranium235=potentia 8 venenum 4 metallum 2",
            "uranium235small=potentia 1 venenum 1", "uranium238=metallum 3 venenum 3 potentia 1",
            "plutonium=potentia 8 venenum 6 mortuus 2", "plutoniumsmall=potentia 1 venenum 1",
            "mox=potentia 8 venenum 6 metallum 3", "rtgpellet=potentia 6 venenum 4 ignis 2",
            "reactorlithiumcell=potentia 2 vitreus 2 metallum 2", "tritiumcell=potentia 4 aqua 1 venenum 1",
            "reactorplating=tutamen 4 metallum 4", "reactorplatingheat=tutamen 3 metallum 4 ignis 2",
            "reactorplatingexplosive=tutamen 6 metallum 4", "reactorreflector=ordo 4 metallum 2 tutamen 2",
            "reactorreflectorthick=ordo 8 metallum 4 tutamen 4", "reactorcondensator=gelum 4 aqua 2 metallum 1",
            "reactorcondensatorlap=gelum 6 aqua 2 sensus 2", "reactorcoolantsimple=gelum 4 aqua 2 metallum 2",
            "reactorcoolanttriple=gelum 8 aqua 4 metallum 4", "reactorcoolantsix=gelum 16 aqua 6 metallum 6",
            "nuclearreactor=potentia 16 ignis 8 machina 8 metallum 8", "reactorchamber=metallum 6 tutamen 4",
            "reactorvessel=tutamen 6 metallum 4", "reactoraccesshatch=tutamen 4 metallum 4 vacuos 2",
            "reactorredstoneport=tutamen 4 metallum 4 potentia 2", "reactorfluidport=tutamen 4 metallum 4 aqua 2",
            "uranium_block=metallum 20 venenum 20 potentia 10",
            // waste, farming, food
            "scrap=perditio 2 fabrico 1", "scrapbox=perditio 4 lucrum 2 fabrico 2",
            "slag=perditio 2 terra 1 ignis 1", "ashes=perditio 1 ignis 1", "plantball=herba 2 victus 1",
            "biochaff=herba 3 messis 1", "fertilizer=herba 2 victus 2 terra 1",
            "weedex=venenum 3 herba 1 mortuus 1", "weed=herba 1 perditio 1", "terrawart=herba 2 sano 3 victus 2",
            "grinpowder=venenum 4 perditio 2", "coffeebeans=messis 2 herba 1 motus 1",
            "coffeepowder=messis 1 motus 2 perditio 1", "hops=messis 2 herba 1", "mugempty=vacuos 1 terra 2",
            "mugcoffee=fames 2 motus 3 aqua 1", "mugcoffeecold=fames 2 motus 2 aqua 1 gelum 1",
            "mugcoffeedark=fames 2 motus 2 aqua 1 tenebrae 1", "mugbooze=fames 1 venenum 1 aqua 2",
            "barrel=arbor 4 vacuos 2", "blockbarrel=arbor 4 vacuos 2 aqua 1", "cropseed=herba 1 victus 1 messis 1",
            "blockcrop=arbor 1 herba 1", "weedingtrowel=instrumentum 1 herba 1 meto 1",
            "cropnalyzer=sensus 3 herba 2 machina 1", "cropmatron=herba 4 victus 2 machina 3",
            "crophavester=messis 4 machina 3 metallum 2",
            // explosives
            "dynamite=perditio 3 ignis 2", "dynamitesticky=perditio 3 ignis 2 limus 1",
            "blockdynamite=perditio 3 ignis 2", "blockdynamiteremote=perditio 3 ignis 2 potentia 1",
            "remote=potentia 1 cognitio 1 machina 2", "blockitnt=perditio 6 ignis 4 potentia 2",
            "blocknuke=perditio 32 potentia 16 venenum 8 ignis 8",
            // building blocks
            "blockalloy=terra 4 tutamen 4 metallum 1", "blockalloyglass=vitreus 4 tutamen 4",
            "blockbasalt=terra 3 ignis 1", "blockfoam=terra 2 tutamen 1", "blockwall=terra 2 tutamen 1",
            "blockreinforcedfoam=terra 2 tutamen 4 metallum 1", "blockscaffold=arbor 2 iter 1",
            "blockironscaffold=metallum 2 iter 1", "ironfence=metallum 2 tutamen 1",
            "luminatordark=lux 4 potentia 2 vitreus 2", "dooralloy=tutamen 4 metallum 3 motus 1",
            "blockdooralloy=tutamen 4 metallum 3 motus 1", "coin=lucrum 2 metallum 1",
            // tools
            "toolbox=vacuos 3 instrumentum 1 metallum 2", "containmentbox=vacuos 3 tutamen 2 metallum 4",
            "odscanner=sensus 4 potentia 2 terra 2 machina 2", "ovscanner=sensus 6 potentia 4 terra 2 lucrum 2",
            "toolmeter=sensus 2 potentia 2", "forgehammer=instrumentum 3 metallum 3 fabrico 2",
            "cutter=instrumentum 2 metallum 2 meto 1", "lathetool=instrumentum 2 metallum 2 fabrico 1",
            "toolwrench=instrumentum 3 metallum 2 machina 1",
            "toolwrenchelectric=instrumentum 3 metallum 2 machina 1 potentia 2", "windmeter=aer 2 sensus 2",
            "foamsprayer=instrumentum 2 terra 2 aer 1", "freq=iter 2 potentia 1 cognitio 1",
            "obscurator=tenebrae 2 sensus 2 potentia 2", "plasmalauncher=telum 6 potentia 8 ignis 4",
            "nanosaber=telum 8 potentia 4 metallum 2", "tooldrill=perfodio 3 potentia 2 metallum 2",
            "toolddrill=perfodio 4 potentia 2 vitreus 2", "tooliridiumdrill=perfodio 6 potentia 6 lucrum 2",
            "toolchainsaw=arbor 2 meto 3 potentia 2 metallum 2", "toolhoe=messis 2 instrumentum 1 potentia 2",
            "toolmininglaser=perfodio 4 lux 3 potentia 4 telum 2",
            "toolbronzepickaxe=perfodio 2 metallum 3 instrumentum 1", "toolbronzeaxe=meto 2 metallum 3 instrumentum 1",
            "toolbronzesword=telum 2 metallum 3", "toolbronzespade=perfodio 1 metallum 2 instrumentum 1",
            "toolbronzehoe=messis 1 metallum 2 instrumentum 1",
            "ironblockcuttingblade=instrumentum 2 metallum 3",
            "advironblockcuttingblade=instrumentum 3 metallum 4 ordo 1",
            "diamondblockcuttingblade=instrumentum 4 metallum 3 vitreus 2",
            // armour
            "armorbronzehelmet=tutamen 3 metallum 4", "armorbronzechestplate=tutamen 6 metallum 6",
            "armorbronzelegs=tutamen 5 metallum 5", "armorbronzeboots=tutamen 2 metallum 3",
            "armoralloychestplate=tutamen 8 metallum 4", "armornanohelmet=tutamen 5 potentia 4 metallum 2",
            "armornanochestplate=tutamen 7 potentia 4 metallum 2", "armornanolegs=tutamen 6 potentia 4 metallum 2",
            "armornanoboots=tutamen 4 potentia 4 metallum 2",
            "armorquantumhelmet=tutamen 8 potentia 8 metallum 4 lucrum 2",
            "armorquantumchestplate=tutamen 12 potentia 8 metallum 4 lucrum 2",
            "armorquantumlegs=tutamen 10 potentia 8 metallum 4 lucrum 2",
            "armorquantumboots=tutamen 6 potentia 8 metallum 4 lucrum 2",
            "solarhelmet=lux 3 potentia 2 tutamen 2", "staticboots=potentia 2 motus 1 tutamen 1",
            "nightvisiongoggles=sensus 3 lux 2 potentia 2", "armorrubboots=tutamen 1 limus 2 motus 1",
            "armorhazmathelmet=tutamen 2 limus 2 sano 1 aqua 1", "armorhazmatchestplate=tutamen 3 limus 3 sano 1",
            "armorhazmatleggings=tutamen 3 limus 2 sano 1", "armorjetpack=volatus 4 ignis 2 metallum 2",
            "armorjetpackelectric=volatus 4 potentia 4 metallum 2", "armorbatpack=potentia 6 tutamen 1",
            "armoradvbatpack=potentia 12 tutamen 1", "armorenergypack=potentia 24 tutamen 2",
            "armorcfpack=terra 2 aer 2 vacuos 2",
            // boats
            "boat=iter 2 aqua 2 ordo 1", "boatrubber=iter 2 aqua 2 limus 1", "boatelectric=iter 2 aqua 2 potentia 2",
            "boatrubberbroken=perditio 1 limus 1",
            // generators and storage
            "generator=potentia 4 ignis 2 machina 2 metallum 4", "geogenerator=potentia 4 ignis 4 machina 2",
            "solargenerator=lux 4 potentia 2 machina 1", "watergenerator=aqua 4 potentia 2 machina 2",
            "windgenerator=aer 4 potentia 2 machina 2", "rtgenerator=potentia 6 venenum 2 machina 2",
            "semifluidgenerator=potentia 4 aqua 2 ignis 2 machina 2", "kineticgenerator=motus 4 potentia 4 machina 3",
            "manualkineticgenerator=motus 4 humanus 2 machina 2", "waterkineticgenerator=motus 4 aqua 4 machina 3",
            "windkineticgenerator=motus 4 aer 4 machina 3", "stirlingkineticgenerator=motus 4 ignis 3 machina 3",
            "steamkineticgenerator=motus 4 aqua 3 ignis 1 machina 3",
            "electrickineticgenerator=motus 4 potentia 3 machina 3", "stirlinggenerator=potentia 4 ignis 3 machina 2",
            "solidheatgenerator=ignis 4 machina 2 metallum 2", "fluidheatgenerator=ignis 4 aqua 2 machina 2",
            "rtheatgenerator=ignis 4 venenum 2 machina 2", "electricheatgenerator=ignis 4 potentia 2 machina 2",
            "steamgenerator=ignis 2 aqua 4 machina 2", "liquidheatexchanger=ignis 2 aqua 2 permutatio 2",
            "condenser=aqua 4 gelum 2 machina 2", "steamturbine=aqua 2 aer 2 motus 3 metallum 3",
            "steamturbineblade=aer 2 motus 2 metallum 2", "electric=potentia 4 arbor 2 metallum 1",
            "cesu=potentia 8 metallum 4", "mfe=potentia 16 vitreus 4 metallum 4",
            "mfsu=potentia 32 vitreus 8 lucrum 4 metallum 4", "chargepad=potentia 4 iter 1 machina 1",
            "chargepadcesu=potentia 8 iter 1 machina 1", "chargepadmfe=potentia 16 iter 1 machina 1",
            "chargepadmfsu=potentia 32 iter 1 machina 1", "transformerlv=potentia 2 permutatio 2 metallum 2",
            "transformermv=potentia 4 permutatio 2 metallum 4", "transformerhv=potentia 8 permutatio 2 metallum 4",
            "transformerev=potentia 16 permutatio 4 metallum 4", "tesla=potentia 8 telum 4 tempestas 2",
            // machines
            "macerator=perditio 4 machina 4 potentia 2 metallum 4", "extractor=limus 2 machina 4 potentia 2",
            "compressor=ordo 3 machina 4 potentia 2", "elecfurnace=ignis 3 machina 3 potentia 2",
            "induction=ignis 6 machina 4 potentia 4", "ironfurnace=ignis 3 metallum 4",
            "recycler=perditio 6 machina 4 potentia 2", "matter=permutatio 8 potentia 16 alienis 4 machina 8",
            "replicator=permutatio 8 fabrico 6 alienis 4 machina 6", "scanner=sensus 6 cognitio 4 machina 4",
            "patternstorage=cognitio 6 machina 3", "terra=terra 8 permutatio 8 machina 4",
            "machine2=iter 16 alienis 8 potentia 8", "miner=perfodio 6 machina 4 potentia 2",
            "advminer=perfodio 10 machina 6 potentia 4", "pump=aqua 4 motus 2 machina 3",
            "magnetizer=potentia 4 metallum 6 ordo 2", "electrolyzer=aqua 4 potentia 4 permutatio 2",
            "centrifuge=ignis 4 motus 4 machina 4", "orewashingplant=aqua 4 machina 4 ordo 1",
            "metalformer=metallum 6 fabrico 4 machina 3", "canner=vacuos 4 machina 3",
            "solidcanner=vacuos 4 machina 3 fames 1", "fluidbottler=vacuos 4 aqua 2 machina 3",
            "blastfurnace=ignis 6 metallum 8 machina 2", "blockcutter=instrumentum 4 machina 4",
            "lathe=fabrico 4 machina 3", "fermenter=herba 4 aer 2 machina 2", "sortingmachine=ordo 6 machina 4",
            "itembuffer=vacuos 4 machina 2", "fluiddistributor=aqua 3 iter 2 machina 2",
            "fluidregulator=aqua 3 ordo 2 machina 2", "solardestiller=lux 2 aqua 3 machina 1",
            "personal=tutamen 6 vacuos 4", "personaltrader=lucrum 4 permutatio 2 vacuos 2",
            "personaltraderenergy=lucrum 4 permutatio 2 potentia 2",
            "tfbp=cognitio 1 vacuos 1", "tfbpcultivation=cognitio 2 herba 3", "tfbpirrigation=cognitio 2 aqua 3",
            "tfbpchilling=cognitio 2 gelum 3", "tfbpdesertification=cognitio 2 ignis 2 perditio 1",
            "tfbpflatification=cognitio 2 terra 2 perditio 1", "tfbpmushroom=cognitio 2 herba 2 tenebrae 1",
    };

    /** Materials found in IC2 item names: main aspect, extra aspect. */
    private static final String[][] MATERIALS = {
            {"copper", "metallum", "permutatio"}, {"tin", "metallum", "vitreus"}, {"lead", "metallum", "ordo"},
            {"silver", "metallum", "lucrum"}, {"gold", "metallum", "lucrum"}, {"bronze", "metallum", "instrumentum"},
            {"steel", "metallum", "ordo"}, {"iron", "metallum", null}, {"uranium", "metallum", "venenum"},
            {"lapis", "sensus", "terra"}, {"obsidian", "terra", "tenebrae"}, {"lithium", "potentia", "vitreus"},
            {"sulfur", "ignis", "perditio"}, {"coal", "ignis", "potentia"}, {"diamond", "vitreus", "lucrum"},
            {"clay", "terra", "aqua"}, {"stone", "terra", null},
    };

    private static final Map<String, Map<String, Integer>> EXPLICIT = new HashMap<>();

    static {
        for (String line : TABLE) {
            String[] kv = line.split("=");
            String[] parts = kv[1].split(" ");
            Map<String, Integer> values = new LinkedHashMap<>();
            for (int i = 0; i < parts.length; i += 2) {
                values.merge(parts[i], Integer.parseInt(parts[i + 1]), Integer::sum);
            }
            EXPLICIT.put(kv[0], values);
        }
    }

    private IC2Aspects() {
    }

    private static Map<String, Integer> of(Object... pairs) {
        Map<String, Integer> values = new LinkedHashMap<>();
        for (int i = 0; i < pairs.length; i += 2) {
            if (pairs[i] != null) {
                values.merge((String) pairs[i], (Integer) pairs[i + 1], Integer::sum);
            }
        }
        return values;
    }

    private static String[] material(String name) {
        for (String[] m : MATERIALS) {
            if (name.contains(m[0])) {
                return m;
            }
        }
        return null;
    }

    /** Aspects of an IC2 registry path, or null when nothing fits. */
    static Map<String, Integer> values(String name, Item item) {
        Map<String, Integer> explicit = EXPLICIT.get(name);
        if (explicit != null) {
            return explicit;
        }
        String[] m = material(name);
        if (m != null) {
            String a = m[1];
            String b = m[2];
            if (name.startsWith("dense_") && name.endsWith("_plate")) {
                return of(a, 16, b, 4, "fabrico", 2);
            }
            if (name.endsWith("_plate")) {
                return of(a, 3, b, 1, "fabrico", 1);
            }
            if (name.endsWith("_ingot")) {
                return of(a, 3, b, 1);
            }
            if (name.endsWith("_casing")) {
                return of(a, 1, b, 1);
            }
            if (name.startsWith("tiny_") && name.endsWith("_dust")) {
                return of(a, 1);
            }
            if (name.endsWith("_dust")) {
                return of(a, 2, b, 1, "perditio", 1);
            }
            if (name.startsWith("purified_crushed_")) {
                return of(a, 2, b, 1, "aqua", 1, "ordo", 1);
            }
            if (name.startsWith("crushed_")) {
                return of(a, 2, b, 1, "terra", 1, "perditio", 1);
            }
            if (name.endsWith("_ore")) {
                return of("terra", 1, a, 2, b, 1);
            }
            if (name.endsWith("_block")) {
                return of(a, 20, b, 6);
            }
        }
        if (name.contains("cable")) {
            String[] cm = material(name);
            return of("potentia", 1, "iter", 1, cm == null ? "metallum" : cm[1], 1,
                    name.contains("glass") ? "vitreus" : name.contains("detector") ? "sensus" : name.contains("splitter") ? "ordo" : null, 1,
                    name.endsWith("i") || name.endsWith("ii") ? "limus" : null, 1);
        }
        if (name.startsWith("reactoruranium") || name.startsWith("reactormox")) {
            int size = name.contains("quad") ? 4 : name.contains("dual") ? 2 : 1;
            return name.endsWith("depleted") ? of("venenum", 2 * size, "metallum", 2 * size, "perditio", 2)
                    : of("potentia", 6 * size, "venenum", 3 * size, "metallum", 2 * size);
        }
        if (name.startsWith("reactorvent")) {
            return of("aer", 4, "gelum", 2, "metallum", 3);
        }
        if (name.startsWith("reactorheatswitch")) {
            return of("ignis", 2, "permutatio", 2, "metallum", 3);
        }
        if (name.contains("rotor")) {
            String material = name.startsWith("wood") ? "arbor" : name.startsWith("carbon") || name.startsWith("wcarbon") ? "ordo" : "metallum";
            return of(material, name.contains("blade") ? 3 : 6, "aer", 2, "motus", 2, "machina", 1);
        }
        if (name.startsWith("upgrademodule")) {
            return of("machina", 2, "potentia", 1, "motus", 1);
        }
        if (name.startsWith("toolpainter")) {
            return of("sensus", 2, "instrumentum", 1);
        }
        if (name.endsWith("_bucket")) {
            return of("metallum", 8, "aqua", 2);
        }
        if (name.startsWith("itembatre")) {
            return EXPLICIT.get("batredischarged");
        }
        if (item instanceof ArmorItem) {
            return of("tutamen", 4, "metallum", 2);
        }
        if (item instanceof TieredItem) {
            return of("instrumentum", 3, "metallum", 2);
        }
        if (item instanceof BlockItem) {
            return of("machina", 3, "metallum", 4);
        }
        return name.equals("debug") ? null : of("fabrico", 2, "metallum", 1);
    }

    static AspectValues aspects(Map<String, Integer> values) {
        for (String tag : values.keySet()) {
            if (!TC4.contains(tag)) {
                throw new IllegalArgumentException("not a Thaumcraft 4 aspect: " + tag);
            }
        }
        return new AspectValues(values);
    }

    /** /emtbridge aspects: what the registry holds and what original Thaumcraft sees for a few IC2 items. */
    static java.util.List<String> check() {
        java.util.List<String> out = new java.util.ArrayList<>();
        long rules = TcScanApi.registry().definitions().keySet().stream().filter(k -> k.id().startsWith("ic2:")).count();
        out.add((rules > 300 ? "OK   " : "FAIL ") + "ic2 scan rules: " + rules);
        LegacyItems.runtime().ifPresentOrElse(rt -> {
            for (String id : new String[]{"ic2:copper_ingot", "ic2:macerator", "ic2:energycrystal", "ic2:resin", "ic2:mfsu", "ic2:dense_iron_plate"}) {
                try {
                    var legacy = github.com.gengyoubo.tcmodern.api.aspect.TcItemAspects.of(rt, rt.hostRepresentations().item(id));
                    out.add((legacy.isPresent() ? "OK   " : "FAIL ") + "thaumcraft aspects of " + id + ": "
                            + legacy.map(v -> v.amounts().toString()).orElse("none"));
                } catch (RuntimeException e) {
                    out.add("FAIL thaumcraft aspects of " + id + ": " + e);
                }
            }
        }, () -> out.add("FAIL no MFCL runtime"));
        return out;
    }

    /** Registers the defaults; returns the number of IC2 items given aspects. */
    public static int install() {
        TcScanRegistry registry = TcScanApi.registry();
        int count = 0;
        for (Map.Entry<net.minecraft.resources.ResourceKey<Item>, Item> entry : ForgeRegistries.ITEMS.getEntries()) {
            ResourceLocation id = entry.getKey().location();
            if (!"ic2".equals(id.getNamespace())) {
                continue;
            }
            Map<String, Integer> values = values(id.getPath(), entry.getValue());
            if (values == null || values.isEmpty()) {
                continue;
            }
            try {
                AspectValues aspects = aspects(values);
                registry.registerDefault(ScanKey.item(id.toString()), TcScanRegistry.Definition.aspects(aspects));
                if (entry.getValue() instanceof BlockItem block) {
                    ResourceLocation blockId = ForgeRegistries.BLOCKS.getKey(block.getBlock());
                    if (blockId != null) {
                        registry.registerDefault(ScanKey.block(blockId.toString()), TcScanRegistry.Definition.aspects(aspects));
                    }
                }
                count++;
            } catch (RuntimeException e) {
                EmtBridge.warnOnce("aspects-" + id, "Could not give Thaumcraft aspects to " + id, e);
            }
        }
        EmtBridge.LOGGER.info("EMT: Thaumcraft aspects for {} IndustrialCraft 2 items", count);
        return count;
    }
}
