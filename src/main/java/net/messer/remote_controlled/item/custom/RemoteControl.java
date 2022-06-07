package net.messer.remote_controlled.item.custom;

import net.messer.remote_controlled.RemoteControlled;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.FurnaceBlockEntity;
import net.minecraft.block.entity.LockableContainerBlockEntity;
import net.minecraft.block.entity.LootableContainerBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.screen.ForgingScreenHandler;
import net.minecraft.screen.FurnaceScreenHandler;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandlerFactory;
import net.minecraft.text.LiteralText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;



public class RemoteControl extends Item {
    BlockPos storedPositon;

    public RemoteControl(Settings settings) {
        super(settings);
    }
    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        if(!world.isClient)
            super.use(world, user, hand);

        var stackInHand = user.getStackInHand(hand);
        if(user.isSneaking())
        {
            BlockHitResult lookingAt = (BlockHitResult) user.raycast(10,0,true);
            BlockState foundBlockState = world.getBlockState(lookingAt.getBlockPos());
            BlockEntity foundBlockEntity = world.getBlockEntity(lookingAt.getBlockPos());

            if(foundBlockState.getBlock() == Blocks.AIR)
                return super.use(world, user, hand);

            if(foundBlockEntity instanceof NamedScreenHandlerFactory screen){
                write_npt(stackInHand, lookingAt);
                return super.use(world, user, hand);
            }
            if(foundBlockState != null){
                write_npt(stackInHand,lookingAt);
                return super.use(world, user, hand);
            }

            return super.use(world, user, hand);
        }

        if(stackInHand.hasNbt()){
            this.read_npt(stackInHand);
            BlockHitResult lookingAt = (BlockHitResult) user.raycast(10,0,true);
            var blockState = world.getBlockState(storedPositon);
            var blockEntity = world.getBlockEntity(storedPositon);

            if(blockState.getBlock() == Blocks.AIR)
                clear_nbt(stackInHand);

            if(blockEntity instanceof NamedScreenHandlerFactory)
            {
                user.openHandledScreen((NamedScreenHandlerFactory) world.getBlockEntity(storedPositon));
            }
            else if(blockState != null)
            {
                blockState.onUse(world, user, hand,lookingAt);
            }
            else{
                clear_nbt(stackInHand);
                user.sendMessage(new LiteralText("Block cannot be found."), true);
            }
        }
        return super.use(world, user, hand);
    }

    @Override
    public boolean hasGlint(ItemStack stack) {
        return stack.hasNbt();
    }

    public void clear_nbt(ItemStack stack){
        var nbt = stack.getNbt();
        nbt.remove("pos");
        stack.setNbt(nbt);
    }

    public void write_npt(ItemStack stack, BlockHitResult hitResult){
        var nbt = stack.getOrCreateNbt();
        if(hitResult!= null){
            nbt.putLong("pos", hitResult.getBlockPos().asLong());
        }
        stack.setNbt(nbt);
    }

    public void read_npt(ItemStack stack){
        var nbt = stack.getOrCreateNbt();
        this.storedPositon = BlockPos.fromLong(nbt.getLong("pos"));
    }
}
