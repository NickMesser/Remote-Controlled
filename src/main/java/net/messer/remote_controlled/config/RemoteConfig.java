package net.messer.remote_controlled.config;

import blue.endless.jankson.Comment;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;

import java.util.ArrayList;
import java.util.List;

@Config(name = "furc")
public class RemoteConfig  implements ConfigData {

    @ConfigEntry.Gui.CollapsibleObject
    @Comment("What blocks can the remote not control?")
    public List<String> BlockBlackList = new ArrayList<>();

    @ConfigEntry.Gui.CollapsibleObject
    @Comment("How much xp per use does the remote take? Default:  (No xp)")
    public int XpPerUse = 7;

    @ConfigEntry.Gui.CollapsibleObject
    @Comment("How far can the remote work? Default is unlimited at -1. Anything over will also lock the remote to the dimension of the block.")
    public int RangeOfRemote = -1;

    public long EnergyRemoteCapacity = 200000;
}
