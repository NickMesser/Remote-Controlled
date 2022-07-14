package net.messer.remote_controlled.config;

import blue.endless.jankson.Comment;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;

import java.util.ArrayList;
import java.util.List;

@Config(name = "furc")
public class RemoteControlledConfig implements ConfigData {

    @ConfigEntry.Gui.CollapsibleObject
    public RemoteConfig RemoteConfig = new RemoteConfig();

    @ConfigEntry.Gui.CollapsibleObject
    public EnergyRemoteConfig EnergyRemoteConfig = new EnergyRemoteConfig();


    public static class RemoteConfig{
        @Comment("What blocks can the remote not control?")
        public List<String> BlockBlackList = new ArrayList<>();

        @Comment("How much xp per use does the remote take? Default: 7")
        public int XpPerUse = 7;

        @Comment("How far can the remote work? Default is unlimited at -1. Anything over will also lock the remote to the dimension of the block.")
        public int RangeOfRemote = -1;
    }

    public static class EnergyRemoteConfig{
        @Comment("What blocks can the remote not control?")
        public List<String> BlockBlackList = new ArrayList<>();

        @Comment("How much energy per use does the remote take? Default: 4000")
        public int EnergyPerUse = 4000;

        @Comment("How far can the remote work? Default is unlimited at -1. Anything over will also lock the remote to the dimension of the block.")
        public int RangeOfRemote = -1;

        public long EnergyRemoteCapacity = 200000;
    }
}
