package net.messer.remote_controlled.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.messer.remote_controlled.item.ModItems;
import net.messer.remote_controlled.item.custom.EnergyRemoteControl;
import net.messer.remote_controlled.item.custom.RemoteControl;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(PlayerEntity.class)
public class PlayerEntityMixin {
    @ModifyExpressionValue( method = "tick", at = @At(value = "INVOKE",target = "Lnet/minecraft/screen/ScreenHandler;canUse(Lnet/minecraft/entity/player/PlayerEntity;)Z"))
    private boolean allowRemoteAccess(boolean original){
        var currentPlayer = ((PlayerEntity)(Object)this);
        var itemInHand = currentPlayer.getMainHandStack().getItem();
        if(itemInHand instanceof RemoteControl || itemInHand instanceof EnergyRemoteControl){
            return true;
        }
        else {
            return original;
        }
    }
}
