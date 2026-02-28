package com.example.mobconversion.event;

import com.example.mobconversion.util.MobConversionManager;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.npc.Villager;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.EntityLeaveLevelEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.living.LivingDropsEvent;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;

public class MobConversionEvents {

    @SubscribeEvent
    public void onEntityTick(EntityTickEvent.Post event) {
        if (event.getEntity() instanceof Villager villager && villager.level() instanceof ServerLevel serverLevel) {
            MobConversionManager.onVillagerTick(serverLevel, villager);
        }
    }

    @SubscribeEvent
    public void onLivingDeath(LivingDeathEvent event) {
        if (event.getEntity() instanceof Villager villager) {
            MobConversionManager.clearCooldown(villager.getUUID());
        }
    }

    @SubscribeEvent
    public void onEntityLeaveLevel(EntityLeaveLevelEvent event) {
        if (event.getEntity() instanceof Villager villager) {
            MobConversionManager.clearCooldown(villager.getUUID());
        }
    }

    @SubscribeEvent
    public void onLivingHurt(LivingDamageEvent.Pre event) {
        if (event.getEntity() instanceof Villager villager && villager.level() instanceof ServerLevel serverLevel) {
            MobConversionManager.onVillagerAttacked(serverLevel, villager, event.getSource().getEntity());
        }
    }

    @SubscribeEvent
    public void onServerTick(ServerTickEvent.Post event) {
        MobConversionManager.tickParticles();
    }

    @SubscribeEvent
    public void onLivingDrops(LivingDropsEvent event) {
        if (event.getEntity().getPersistentData().getBoolean("mobconversion_nodrops")) {
            event.getDrops().clear();
        }
    }
}
