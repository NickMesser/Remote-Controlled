package net.messer.remote_controlled.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.messer.remote_controlled.item.ModItems;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ServerPlayerEntity.class)
public class ServerPlayerEntityMixin {
    @ModifyExpressionValue( method = "tick", at = @At(value = "INVOKE",target = "Lnet/minecraft/screen/ScreenHandler;canUse(Lnet/minecraft/entity/player/PlayerEntity;)Z"))
    private boolean allowRemoteAccess(boolean original){
        var currentPlayer = ((PlayerEntity)(Object)this);
        if(currentPlayer.getMainHandStack().getItem() == ModItems.REMOTE_CONTROL){
            return true;
        }
        else {
            return original;
        }
    }
}