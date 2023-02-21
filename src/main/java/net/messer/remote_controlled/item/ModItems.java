package net.messer.remote_controlled.item;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.messer.remote_controlled.RemoteControlled;
import net.messer.remote_controlled.item.custom.EnergyRemoteControl;
import net.messer.remote_controlled.item.custom.RemoteControl;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModItems {

    public static final Item REMOTE_CONTROL = registerItem("remote_control",
            new RemoteControl(new FabricItemSettings().maxCount(1)));

    public static final Item ENERGY_REMOTE_CONTROL = registerItem("energy_remote_control",
            new EnergyRemoteControl(new FabricItemSettings().maxCount(1)));

    private static Item registerItem(String name, Item item){
        return Registry.register(Registries.ITEM, new Identifier(RemoteControlled.MOD_ID, name), item);
    }

    public static void addItemsToItemGroup() {
        addToItemGroup(ItemGroups.TOOLS, REMOTE_CONTROL);
        addToItemGroup(ItemGroups.TOOLS, ENERGY_REMOTE_CONTROL);
    }

    private static void addToItemGroup(ItemGroup group, Item item) {
        ItemGroupEvents.modifyEntriesEvent(group).register(entries -> entries.add(item));
    }

    public static void registerModItems(){
        RemoteControlled.LOGGER.info("Registering items for " + RemoteControlled.MOD_ID);
        addItemsToItemGroup();
    }
}
