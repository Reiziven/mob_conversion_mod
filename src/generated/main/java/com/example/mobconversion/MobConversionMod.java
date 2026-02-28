package com.example.mobconversion;

import com.example.mobconversion.config.MobConversionConfig;
import com.example.mobconversion.event.MobConversionEvents;
import com.example.mobconversion.util.EntityPoolManager;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.neoforge.common.NeoForge;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.nio.file.Files;
import java.nio.file.Path;

@Mod(MobConversionMod.MOD_ID)
public class MobConversionMod {
    public static final String MOD_ID = "mobconversion";
    private static final Logger LOGGER = LogManager.getLogger("MobConversion");

    public MobConversionMod(IEventBus modEventBus, ModContainer container) {
        LOGGER.info("Initializing Mob Conversion Mod for NeoForge 1.21.1");

        // Config Migration
        Path configDir = FMLPaths.CONFIGDIR.get();
        migrateConfig(configDir, "villagerguard-common.toml", MOD_ID + "-common.toml");
        migrateConfig(configDir, "villagerguardconversion-common.toml", MOD_ID + "-common.toml");

        container.registerConfig(ModConfig.Type.COMMON, MobConversionConfig.SPEC);
        modEventBus.addListener(this::onConfigReload);

        // Register events
        NeoForge.EVENT_BUS.register(new MobConversionEvents());
    }

    private void migrateConfig(Path dir, String oldName, String newName) {
        Path oldPath = dir.resolve(oldName);
        Path newPath = dir.resolve(newName);
        if (Files.exists(oldPath) && Files.notExists(newPath)) {
            try {
                Files.copy(oldPath, newPath);
                LOGGER.info("Migrated legacy config from {} to {}", oldName, newName);
            } catch (Exception e) {
                LOGGER.warn("Failed to migrate legacy config from {}", oldName, e);
            }
        }
    }

    private void onConfigReload(ModConfigEvent event) {
        if (event.getConfig().getModId().equals(MOD_ID)) {
            EntityPoolManager.invalidateCache();
            LOGGER.info("Config reloaded, entity pool cache invalidated.");
        }
    }
}
