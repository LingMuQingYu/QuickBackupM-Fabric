package dev.skydynamic.quickbackupmulti;

import dev.skydynamic.quickbackupmulti.utils.config.Config;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;

import net.fabricmc.loader.api.FabricLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static dev.skydynamic.quickbackupmulti.utils.QbmManager.restore;
import static dev.skydynamic.quickbackupmulti.command.QuickBackupMultiCommandManager.RegisterCommand;

public final class QuickBackupMulti implements ModInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger("QuickBackupMulti");

	EnvType env = FabricLoader.getInstance().getEnvironmentType();

	@Override
	public void onInitialize() {
		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> RegisterCommand(dispatcher));

		ServerLifecycleEvents.SERVER_STARTED.register(server -> Config.TEMPCONFIG.setServerValue(server));
		ServerLifecycleEvents.SERVER_STOPPED.register(server -> {
			if (Config.TEMPCONFIG.isBackup) {
				if (env == EnvType.SERVER) {
					restore(Config.TEMPCONFIG.backupSlot);
					Config.TEMPCONFIG.setIsBackupValue(false);
					Config.TEMPCONFIG.server.stopped = false;
					Config.TEMPCONFIG.server.running = true;
					Config.TEMPCONFIG.server.runServer();
				}
			}
		});
	}
}