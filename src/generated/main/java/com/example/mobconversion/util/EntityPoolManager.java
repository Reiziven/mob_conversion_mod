package com.example.mobconversion.util;

import com.example.mobconversion.config.EntityPoolEntry;
import com.example.mobconversion.config.MobConversionConfig;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.phys.AABB;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public final class EntityPoolManager {
    private static final Logger LOGGER = LogManager.getLogger("MobConversion");
    private static volatile List<EntityPoolEntry> cached;
    private static final ConcurrentHashMap<UUID, Map<ResourceLocation, Long>> entryCooldowns = new ConcurrentHashMap<>();

    private EntityPoolManager() {}

    public static List<EntityPoolEntry> getEntries() {
        if (cached != null) return cached;
        
        List<? extends String> raw = MobConversionConfig.ENTRIES.get();
        int cap = MobConversionConfig.MAX_ENTRIES.get();
        List<EntityPoolEntry> parsed = raw.stream()
                .limit(cap)
                .map(EntityPoolEntry::parse)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .sorted(Comparator.comparingInt(EntityPoolEntry::priority))
                .collect(Collectors.toList());
        cached = parsed;
        return cached;
    }

    public static void invalidateCache() {
        cached = null;
    }

    public static Optional<EntityPoolEntry> select(ServerLevel level, Villager villager) {
        List<EntityPoolEntry> entries = cached != null ? cached : getEntries();
        long now = level.getGameTime();
        for (EntityPoolEntry e : entries) {
            Optional<EntityType<?>> optType = e.getEntityType();
            if (optType.isEmpty()) continue;

            EntityType<?> type = optType.get();
            if (!isAllowedAt(level, villager, e)) continue;
            if (isEntryOnCooldown(villager.getUUID(), e.entityId(), now)) continue;
            
            // Limit check per entity type
            int resolvedLimit = resolveLimit(e.maxLimit());
            if (isEntityLimitReached(level, villager, type, resolvedLimit)) {
                LOGGER.debug("Limit reached for {}: {} allowed", e.entityId(), resolvedLimit);
                continue;
            }

            if (level.getRandom().nextDouble() <= e.chance()) {
                setEntryCooldown(villager.getUUID(), e.entityId(), now + e.cooldownTicks());
                LOGGER.info("Selected entity {} for conversion (chance: {})", e.entityId(), e.chance());
                return Optional.of(e);
            }
        }
        return Optional.empty();
    }

    private static int resolveLimit(int entryLimit) {
        // If entry specifies a limit >= 0, use it.
        if (entryLimit >= 0) return entryLimit;
        
        // Otherwise, check defaultMaxLimit
        int defaultLimit = MobConversionConfig.DEFAULT_MAX_LIMIT.get();
        if (defaultLimit >= 0) return defaultLimit;
        
        // Finally, fallback to global area limit
        return MobConversionConfig.MAX_ENTITIES_PER_AREA.get();
    }

    private static boolean isEntityLimitReached(ServerLevel level, Villager villager, EntityType<?> type, int maxEntities) {
        if (maxEntities < 0) return false; // Should not happen with resolveLimit but for safety

        int radius = MobConversionConfig.ENTITY_SEARCH_RADIUS.get();
        AABB searchBox = villager.getBoundingBox().inflate(radius);

        // Count living entities of the specific type in the search radius
        List<? extends net.minecraft.world.entity.Entity> entities = level.getEntities(type, searchBox, net.minecraft.world.entity.Entity::isAlive);
        return entities.size() >= maxEntities;
    }

    private static boolean isAllowedAt(ServerLevel level, Villager villager, EntityPoolEntry e) {
        MobConversionConfig.VillageScopeOverride scope = e.villageScope();
        MobConversionConfig.BooleanOverride bell = e.requireBell();
        boolean natural = VillageHelper.isInsideNaturalVillage(level, villager.blockPosition());
        boolean artificial = VillageHelper.isInsideArtificialVillage(level, villager.blockPosition()) && !natural;
        boolean hasBell = VillageHelper.isBellNearby(level, villager.blockPosition());

        if (scope == MobConversionConfig.VillageScopeOverride.GLOBAL) {
            MobConversionConfig.VillageScope s = MobConversionConfig.VILLAGE_SCOPE.get();
            boolean requireBell = MobConversionConfig.REQUIRE_BELL.get();
            return switch (s) {
                case NONE -> true;
                case NATURAL -> natural && (!requireBell || hasBell);
                case ARTIFICIAL -> artificial;
                case BOTH -> (natural && (!requireBell || hasBell)) || artificial;
            };
        }

        switch (scope) {
            case NONE:
                return true;
            case NATURAL:
                boolean rb = bell == MobConversionConfig.BooleanOverride.GLOBAL
                        ? MobConversionConfig.REQUIRE_BELL.get()
                        : bell == MobConversionConfig.BooleanOverride.TRUE;
                return natural && (!rb || hasBell);
            case ARTIFICIAL:
                return artificial;
            case BOTH:
                boolean rb2 = bell == MobConversionConfig.BooleanOverride.GLOBAL
                        ? MobConversionConfig.REQUIRE_BELL.get()
                        : bell == MobConversionConfig.BooleanOverride.TRUE;
                return (natural && (!rb2 || hasBell)) || artificial;
            default:
                return false;
        }
    }

    private static boolean isEntryOnCooldown(UUID villagerId, ResourceLocation id, long now) {
        Map<ResourceLocation, Long> map = entryCooldowns.get(villagerId);
        if (map == null) return false;
        long until = map.getOrDefault(id, 0L);
        return now < until;
    }

    private static void setEntryCooldown(UUID villagerId, ResourceLocation id, long until) {
        entryCooldowns.computeIfAbsent(villagerId, k -> new ConcurrentHashMap<>()).put(id, until);
    }

    public static void clear(UUID villagerId) {
        entryCooldowns.remove(villagerId);
    }
}
