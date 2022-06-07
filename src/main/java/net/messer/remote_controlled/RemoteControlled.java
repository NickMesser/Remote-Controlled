package net.messer.remote_controlled;

import com.llamalad7.mixinextras.MixinExtrasBootstrap;
import net.fabricmc.api.ModInitializer;
import net.messer.remote_controlled.item.ModItems;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RemoteControlled implements ModInitializer {

	public static final String MOD_ID = "remote_controlled";
	public static final Logger LOGGER = LoggerFactory.getLogger("modid");


	@Override
	public void onInitialize() {
		ModItems.registerModItems();
	}
}
