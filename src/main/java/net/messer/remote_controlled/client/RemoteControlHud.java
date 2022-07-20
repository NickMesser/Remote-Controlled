package net.messer.remote_controlled.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.messer.remote_controlled.item.custom.EnergyRemoteControl;
import net.messer.remote_controlled.item.custom.RemoteControl;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;

public class RemoteControlHud implements ClientModInitializer, HudRenderCallback {
    @Override
    public void onInitializeClient() {
        HudRenderCallback.EVENT.register(this);
    }

    @Override
    public void onHudRender(MatrixStack matrixStack, float tickDelta) {
        var player = MinecraftClient.getInstance().player;
        var itemInHand = player.getStackInHand(player.getActiveHand());
        if (itemInHand.getItem() instanceof RemoteControl remote) {
            remote.read_npt(itemInHand);
            if (remote.storedBlock != null) {
                MinecraftClient.getInstance().textRenderer.drawWithShadow(matrixStack, "Controlling: ", 10, 10, 0xFFFFFFFF);

                ItemStack stack = new ItemStack(remote.storedBlock.asItem());
                MinecraftClient.getInstance().getItemRenderer().renderGuiItemIcon(stack, 68, 5);
            }
        }

        if (itemInHand.getItem() instanceof EnergyRemoteControl energyRemoteControl) {
            energyRemoteControl.read_npt(itemInHand);
            if (energyRemoteControl.storedBlock != null) {
                MinecraftClient.getInstance().textRenderer.drawWithShadow(matrixStack, "Controlling: ", 10, 10, 0xFFFFFFFF);

                ItemStack stack = new ItemStack(energyRemoteControl.storedBlock.asItem());
                MinecraftClient.getInstance().getItemRenderer().renderGuiItemIcon(stack, 68, 5);
            }
        }
    }
}
