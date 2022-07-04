package net.messer.remote_controlled;

import com.llamalad7.mixinextras.MixinExtrasBootstrap;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.JanksonConfigSerializer;
import net.fabricmc.api.ModInitializer;
import net.messer.remote_controlled.config.RemoteConfig;
import net.messer.remote_controlled.item.ModItems;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RemoteControlled implements ModInitializer {

	public static final String MOD_ID = "remote_controlled";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	public static RemoteConfig CONFIG;

	@Override
	public void onInitialize() {
		AutoConfig.register(RemoteConfig.class, JanksonConfigSerializer::new);
		CONFIG = AutoConfig.getConfigHolder(RemoteConfig.class).getConfig();
		ModItems.registerModItems();
	}
}
