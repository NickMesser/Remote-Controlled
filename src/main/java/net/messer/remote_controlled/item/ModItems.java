package net.messer.remote_controlled.item;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.messer.remote_controlled.RemoteControlled;
import net.messer.remote_controlled.item.custom.RemoteControl;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class ModItems {

    public static final Item REMOTE_CONTROL = registerItem("remote_control",
            new RemoteControl(new FabricItemSettings().group(ItemGroup.MISC).maxCount(1)));

    private static Item registerItem(String name, Item item){
        return Registry.register(Registry.ITEM, new Identifier(RemoteControlled.MOD_ID, name), item);
    }

    public static void registerModItems(){
        RemoteControlled.LOGGER.info("Registering items for " + RemoteControlled.MOD_ID);
    }
}
