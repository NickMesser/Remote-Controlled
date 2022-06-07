package net.messer.remote_controlled;

import com.llamalad7.mixinextras.MixinExtrasBootstrap;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.loader.api.entrypoint.PreLaunchEntrypoint;

public class RemoteControlledPreLaunch implements PreLaunchEntrypoint {
    @Override
    public void onPreLaunch() {
        MixinExtrasBootstrap.init();
    }
}
