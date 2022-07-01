package net.messer.remote_controlled.item.custom;

import net.messer.remote_controlled.RemoteControlled;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;

import java.util.Objects;


public class RemoteControl extends Item {
    BlockPos blockPosition;
    String blockWorldID;

    public RemoteControl(Settings settings) {
        super(settings);
    }
    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        if(world.isClient)
            return super.use(world, user, hand);

        var stackInHand = user.getStackInHand(hand);
        if(user.isSneaking())
        {
            BlockHitResult lookingAt = (BlockHitResult) user.raycast(10,0,true);
            BlockState foundBlockState = world.getBlockState(lookingAt.getBlockPos());

            if(foundBlockState == null || foundBlockState.getBlock() == Blocks.AIR)
                return TypedActionResult.fail(stackInHand);

            var block = Registry.BLOCK.getId(foundBlockState.getBlock()).toString();
            if(RemoteControlled.CONFIG.BlockBlackList.contains(Registry.BLOCK.getId(foundBlockState.getBlock()).toString())){
                user.sendMessage(Text.literal("Block is blacklisted from being used by a remote."), true);
                return TypedActionResult.fail(stackInHand);
            }


            write_npt(stackInHand,lookingAt, world);

            return super.use(world, user, hand);
        }

        if(stackInHand.hasNbt()){
            this.read_npt(stackInHand);
            BlockHitResult lookingAt = (BlockHitResult) user.raycast(10,0,true);
            var blockWorld = Objects.requireNonNull(world.getServer()).getWorld(RegistryKey.of(Registry.WORLD_KEY, new Identifier(blockWorldID)));

            if(blockWorld == null) {
                clear_nbt(stackInHand);
                user.sendMessage(Text.literal("Block cannot be found."), true);
                return TypedActionResult.fail(stackInHand);
            }

            var blockState = blockWorld.getBlockState(blockPosition);
            var blockEntity = blockWorld.getBlockEntity(blockPosition);

            if(blockState == null || blockState.getBlock() == Blocks.AIR)
            {
                clear_nbt(stackInHand);
                user.sendMessage(Text.literal("Block cannot be found."), true);
                return TypedActionResult.fail(stackInHand);
            }

            if(blockEntity instanceof NamedScreenHandlerFactory)
            {
                user.openHandledScreen((NamedScreenHandlerFactory) blockWorld.getBlockEntity(blockPosition));
            }
            else{
                blockState.getBlock().onUse(blockState, blockWorld, blockPosition, user, hand, lookingAt);
            }
        }
        return TypedActionResult.success(stackInHand,true);
    }

    @Override
    public boolean hasGlint(ItemStack stack) {
        return stack.hasNbt();
    }

    public void clear_nbt(ItemStack stack){
        var nbt = stack.getOrCreateNbt();
        nbt.remove("pos");
        nbt.remove("world");
        stack.setNbt(nbt);
    }

    public void write_npt(ItemStack stack, BlockHitResult hitResult, World worldOfBlock){
        var nbt = stack.getOrCreateNbt();
        if(hitResult!= null){
            nbt.putLong("pos", hitResult.getBlockPos().asLong());
            nbt.putString("world", worldOfBlock.getRegistryKey().getValue().toString());
        }
        stack.setNbt(nbt);
    }

    public void read_npt(ItemStack stack){
        var nbt = stack.getOrCreateNbt();
        this.blockPosition = BlockPos.fromLong(nbt.getLong("pos"));
        this.blockWorldID = nbt.getString("world");
    }
}
