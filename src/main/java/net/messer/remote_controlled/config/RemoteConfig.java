package net.messer.remote_controlled.config;

import blue.endless.jankson.Comment;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;

import java.util.ArrayList;
import java.util.List;

@Config(name = "furc")
public class RemoteConfig  implements ConfigData {

    @Comment("What blocks can the remote not control?")
    public List<String> BlockBlackList = new ArrayList<>();

}
