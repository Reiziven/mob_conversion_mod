package com.example.mobconversion.config;

import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.common.ModConfigSpec;
import java.util.List;

/**
 * MobConversionConfig
 */
public class MobConversionConfig {
    public static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();
    public static final ModConfigSpec SPEC;

    // Detection
    public static final ModConfigSpec.IntValue THREAT_DETECTION_RADIUS;
    public static final ModConfigSpec.IntValue BELL_DETECTION_RADIUS;
    public static final ModConfigSpec.IntValue SCAN_COOLDOWN_TICKS;
    public static final ModConfigSpec.IntValue ARTIFICIAL_VILLAGE_POI_RADIUS;

    // Village
    public static final ModConfigSpec.EnumValue<VillageScope> VILLAGE_SCOPE;
    public static final ModConfigSpec.BooleanValue REQUIRE_BELL;
    public static final ModConfigSpec.IntValue MAX_ENTITIES_PER_AREA;
    public static final ModConfigSpec.IntValue ENTITY_SEARCH_RADIUS;

    // Conversion
    public static final ModConfigSpec.BooleanValue CONVERT_NITWITS;
    public static final ModConfigSpec.BooleanValue CONVERT_UNEMPLOYED;
    public static final ModConfigSpec.BooleanValue CONVERT_UNTRADED_PROFESSIONALS;
    public static final ModConfigSpec.BooleanValue ALLOW_SELF_CONVERSION;
    public static final ModConfigSpec.BooleanValue DEFAULT_SPAWN_INSTEAD_OF_CONVERT;
    public static final ModConfigSpec.ConfigValue<List<? extends String>> EXEMPT_ATTACKER_ENTITY_IDS;

    // Effects
    public static final ModConfigSpec.BooleanValue CONVERSION_PARTICLES_ENABLED;
    public static final ModConfigSpec.IntValue CONVERSION_PARTICLE_DURATION_TICKS;

    // Entity Pool
    public static final ModConfigSpec.IntValue MAX_ENTRIES;
    public static final ModConfigSpec.DoubleValue DEFAULT_CHANCE;
    public static final ModConfigSpec.EnumValue<ConversionMode> DEFAULT_MODE;
    public static final ModConfigSpec.IntValue DEFAULT_PRIORITY;
    public static final ModConfigSpec.BooleanValue DEFAULT_DROP_LOOT;
    public static final ModConfigSpec.IntValue DEFAULT_COOLDOWN_TICKS;
    public static final ModConfigSpec.EnumValue<VillageScopeOverride> DEFAULT_VILLAGE_SCOPE;
    public static final ModConfigSpec.EnumValue<BooleanOverride> DEFAULT_REQUIRE_BELL;
    public static final ModConfigSpec.IntValue DEFAULT_MAX_LIMIT;
    public static final ModConfigSpec.ConfigValue<List<? extends String>> ENTRIES;

    static {
        BUILDER.push("detection");
        THREAT_DETECTION_RADIUS = BUILDER
                .comment("Distance in blocks for detecting hostile monsters.\nDefault: 15\nRange: 8 ~ 128")
                .defineInRange("threatDetectionRadius", 15, 8, 128);
        BELL_DETECTION_RADIUS = BUILDER
                .comment("Distance in blocks for bell searching.\nDefault: 80\nRange: 8 ~ 128")
                .defineInRange("bellDetectionRadius", 80, 8, 128);
        SCAN_COOLDOWN_TICKS = BUILDER
                .comment("Interval in ticks between checks (20 ticks = 1 second).\nDefault: 80\nRange: 20 ~ 600")
                .defineInRange("scanCooldownTicks", 80, 20, 600);
        ARTIFICIAL_VILLAGE_POI_RADIUS = BUILDER
                .comment("Distance in blocks to detect artificial villages via POIs.\nDefault: 64\nRange: 8 ~ 128")
                .defineInRange("artificialVillagePoiRadius", 64, 8, 128);
        BUILDER.pop();

        BUILDER.push("village");
        VILLAGE_SCOPE = BUILDER
                .comment("Scope of villages permitted for conversion.\nAllowed Values: NATURAL, ARTIFICIAL, BOTH, NONE")
                .defineEnum("villageScope", VillageScope.BOTH);
        REQUIRE_BELL = BUILDER
                .comment("If true, it requires a nearby bell for conversion.")
                .define("requireBell", false);
        MAX_ENTITIES_PER_AREA = BUILDER
                .comment("Maximum number of converted/spawned entities of the same type in an area.\nDefault: 2\nRange: 0 ~ 50")
                .defineInRange("maxEntitiesPerArea", 2, 0, 50);
        ENTITY_SEARCH_RADIUS = BUILDER
                .comment("Search radius to count existing entities.\nDefault: 80\nRange: 8 ~ 256")
                .defineInRange("entitySearchRadius", 80, 8, 256);
        BUILDER.pop();

        BUILDER.push("conversion");
        CONVERT_NITWITS = BUILDER
                .comment("Allow conversion of Nitwits.")
                .define("convertNitwits", true);
        CONVERT_UNEMPLOYED = BUILDER
                .comment("Allow the conversion of unemployed villagers.")
                .define("convertUnemployed", true);
        CONVERT_UNTRADED_PROFESSIONALS = BUILDER
                .comment("Allow the conversion of professionals who have never been traded.")
                .define("convertUntradedProfessionals", true);
        ALLOW_SELF_CONVERSION = BUILDER
                .comment("If no other villagers are found, the detector converts.")
                .define("allowSelfConversion", true);
        DEFAULT_SPAWN_INSTEAD_OF_CONVERT = BUILDER
                .comment("If true, spawn a mob and keep the villager (default if not specified in entry).")
                .define("spawnInsteadOfConvert", false);
        EXEMPT_ATTACKER_ENTITY_IDS = BUILDER
                .comment("Entity IDs exempt from attack-triggered conversion if in the same village.")
                .defineList(
                        "exemptSameVillageAttackers",
                        List.of("minecraft:villager", "minecraft:iron_golem"),
                        value -> value instanceof String s && ResourceLocation.tryParse(s) != null
                );
        BUILDER.pop();

        BUILDER.push("effects");
        CONVERSION_PARTICLES_ENABLED = BUILDER
                .comment("If true, spawn particles when conversion occurs.")
                .define("conversionParticlesEnabled", true);
        CONVERSION_PARTICLE_DURATION_TICKS = BUILDER
                .comment("Duration in ticks for conversion particles.\nDefault: 40\nRange: 1 ~ 200")
                .defineInRange("conversionParticleDurationTicks", 40, 1, 200);
        BUILDER.pop();

        BUILDER.push("entity_pool");
        MAX_ENTRIES = BUILDER
                .comment("Maximum entities allowed in the pool")
                .defineInRange("maxEntries", 10, 1, 100);
        DEFAULT_CHANCE = BUILDER
                .comment("Default chance for conversion/spawn")
                .defineInRange("defaultChance", 1.0, 0.0, 1.0);
        DEFAULT_MODE = BUILDER
                .comment("Default mode for conversion/spawn")
                .defineEnum("defaultMode", ConversionMode.CONVERT);
        DEFAULT_PRIORITY = BUILDER
                .comment("Default priority for conversion/spawn")
                .defineInRange("defaultPriority", 1, 1, 100);
        DEFAULT_DROP_LOOT = BUILDER
                .comment("Default drop loot setting")
                .define("defaultDropLoot", false);
        DEFAULT_COOLDOWN_TICKS = BUILDER
                .comment("Default cooldown in ticks (20 ticks = 1 second)")
                .defineInRange("defaultCooldownTicks", 0, 0, 1000000);
        DEFAULT_VILLAGE_SCOPE = BUILDER
                .comment("Default village scope override")
                .defineEnum("defaultVillageScope", VillageScopeOverride.GLOBAL);
        DEFAULT_REQUIRE_BELL = BUILDER
                .comment("Default require bell override")
                .defineEnum("defaultRequireBell", BooleanOverride.GLOBAL);
        DEFAULT_MAX_LIMIT = BUILDER
                .comment("Default max limit of entities of the same type in an area override")
                .defineInRange("defaultMaxLimit", -1, -1, 100);
        ENTRIES = BUILDER
                .comment("Format:\n# entity/chance/mode/priority/dropLoot/cooldown/villageScope/requireBell/maxLimit\n# Separator is '/' or '|'\n# maxLimit: -1 uses defaultMaxLimit or maxEntitiesPerArea")
                .defineList(
                        "entries",
                        List.of(
                                "irons_spellbooks:priest/0.2/CONVERT/1/true/6000/BOTH/false/2"
                        ),
                        value -> value instanceof String
                );
        BUILDER.pop();

        SPEC = BUILDER.build();
    }

    public enum VillageScope {
        NATURAL, ARTIFICIAL, BOTH, NONE
    }

    public enum VillageScopeOverride {
        NATURAL, ARTIFICIAL, BOTH, NONE, GLOBAL
    }

    public enum ConversionMode {
        SPAWN, CONVERT
    }

    public enum BooleanOverride {
        TRUE, FALSE, GLOBAL
    }
}
