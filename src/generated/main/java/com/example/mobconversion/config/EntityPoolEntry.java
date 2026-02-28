package com.example.mobconversion.config;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;

import java.util.Optional;

public record EntityPoolEntry(
        ResourceLocation entityId,
        double chance,
        MobConversionConfig.ConversionMode mode,
        int priority,
        boolean dropLoot,
        int cooldownTicks,
        MobConversionConfig.VillageScopeOverride villageScope,
        MobConversionConfig.BooleanOverride requireBell,
        int maxLimit
) {
    public static Optional<EntityPoolEntry> parse(String entry) {
        if (entry == null || entry.isBlank()) return Optional.empty();

        String[] parts = entry.split("[/|]");
        ResourceLocation id = ResourceLocation.tryParse(parts[0].trim());
        if (id == null) return Optional.empty();

        double chance = parts.length > 1 ? parseDouble(parts[1], MobConversionConfig.DEFAULT_CHANCE.get()) : MobConversionConfig.DEFAULT_CHANCE.get();
        MobConversionConfig.ConversionMode mode = parts.length > 2 ? parseMode(parts[2], MobConversionConfig.DEFAULT_MODE.get()) : MobConversionConfig.DEFAULT_MODE.get();
        int priority = parts.length > 3 ? parseInt(parts[3], MobConversionConfig.DEFAULT_PRIORITY.get()) : MobConversionConfig.DEFAULT_PRIORITY.get();
        boolean dropLoot = parts.length > 4 ? parseBoolean(parts[4], MobConversionConfig.DEFAULT_DROP_LOOT.get()) : MobConversionConfig.DEFAULT_DROP_LOOT.get();
        int cooldown = parts.length > 5 ? parseInt(parts[5], MobConversionConfig.DEFAULT_COOLDOWN_TICKS.get()) : MobConversionConfig.DEFAULT_COOLDOWN_TICKS.get();
        MobConversionConfig.VillageScopeOverride scope = parts.length > 6 ? parseScope(parts[6], MobConversionConfig.DEFAULT_VILLAGE_SCOPE.get()) : MobConversionConfig.DEFAULT_VILLAGE_SCOPE.get();
        MobConversionConfig.BooleanOverride bell = parts.length > 7 ? parseBooleanOverride(parts[7], MobConversionConfig.DEFAULT_REQUIRE_BELL.get()) : MobConversionConfig.DEFAULT_REQUIRE_BELL.get();
        int limit = parts.length > 8 ? parseInt(parts[8], MobConversionConfig.DEFAULT_MAX_LIMIT.get()) : MobConversionConfig.DEFAULT_MAX_LIMIT.get();

        return Optional.of(new EntityPoolEntry(id, chance, mode, priority, dropLoot, cooldown, scope, bell, limit));
    }

    private static double parseDouble(String s, double defaultValue) {
        try {
            return Double.parseDouble(s.trim());
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    private static int parseInt(String s, int defaultValue) {
        try {
            return Integer.parseInt(s.trim());
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    private static boolean parseBoolean(String s, boolean defaultValue) {
        String trimmed = s.trim().toLowerCase();
        if (trimmed.equals("true")) return true;
        if (trimmed.equals("false")) return false;
        return defaultValue;
    }

    private static MobConversionConfig.ConversionMode parseMode(String s, MobConversionConfig.ConversionMode defaultValue) {
        try {
            return MobConversionConfig.ConversionMode.valueOf(s.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            return defaultValue;
        }
    }

    private static MobConversionConfig.VillageScopeOverride parseScope(String s, MobConversionConfig.VillageScopeOverride defaultValue) {
        try {
            return MobConversionConfig.VillageScopeOverride.valueOf(s.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            return defaultValue;
        }
    }

    private static MobConversionConfig.BooleanOverride parseBooleanOverride(String s, MobConversionConfig.BooleanOverride defaultValue) {
        try {
            String val = s.trim().toUpperCase();
            if (val.equals("TRUE")) return MobConversionConfig.BooleanOverride.TRUE;
            if (val.equals("FALSE")) return MobConversionConfig.BooleanOverride.FALSE;
            return MobConversionConfig.BooleanOverride.valueOf(val);
        } catch (IllegalArgumentException e) {
            return defaultValue;
        }
    }

    public boolean isValid() {
        return BuiltInRegistries.ENTITY_TYPE.containsKey(entityId);
    }

    public Optional<EntityType<?>> getEntityType() {
        return BuiltInRegistries.ENTITY_TYPE.getOptional(entityId);
    }
}
