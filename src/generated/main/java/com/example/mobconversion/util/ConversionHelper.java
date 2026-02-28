package com.example.mobconversion.util;

import com.example.mobconversion.config.MobConversionConfig;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.phys.AABB;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public final class ConversionHelper {

    private ConversionHelper() {}

    public static Optional<Villager> selectConversionTarget(ServerLevel level, Villager detector) {
        int radius = MobConversionConfig.THREAT_DETECTION_RADIUS.get();
        AABB searchBox = detector.getBoundingBox().inflate(radius);

        List<Villager> candidates = level.getEntitiesOfClass(
                Villager.class,
                searchBox,
                v -> v.isAlive() && !v.isSpectator() && v != detector
        );

        Optional<Villager> nearest = candidates.stream()
                .filter(v -> isEligible(v))
                .min(Comparator.comparingDouble(v -> v.distanceToSqr(detector)));

        if (nearest.isPresent()) {
            return nearest;
        }

        if (MobConversionConfig.ALLOW_SELF_CONVERSION.get() && isEligible(detector)) {
            return Optional.of(detector);
        }

        return Optional.empty();
    }

    public static boolean isEligible(Villager villager) {
        VillagerProfession profession = villager.getVillagerData().getProfession();

        if (profession == VillagerProfession.NITWIT) {
            return MobConversionConfig.CONVERT_NITWITS.get();
        }

        if (profession == VillagerProfession.NONE) {
            return MobConversionConfig.CONVERT_UNEMPLOYED.get();
        }

        if (MobConversionConfig.CONVERT_UNTRADED_PROFESSIONALS.get()) {
            return villager.getVillagerData().getLevel() <= 1 && villager.getVillagerXp() == 0;
        }

        return false;
    }
}
