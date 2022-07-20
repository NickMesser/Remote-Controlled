package net.messer.remote_controlled.item.custom;

import net.messer.remote_controlled.RemoteControlled;
import net.messer.remote_controlled.config.RemoteControlledConfig;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;


public class RemoteControl extends Item {
    BlockPos blockPosition;
    String blockWorldID;
    public Block storedBlock;
    RemoteControlledConfig.RemoteConfig remoteConfig;

    public RemoteControl(Settings settings) {
        super(settings);
        this.remoteConfig = RemoteControlled.CONFIG.RemoteConfig;
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

            if(remoteConfig.BlockBlackList.contains(Registry.BLOCK.getId(foundBlockState.getBlock()).toString())){
                sendMessage(user, "Block is blacklisted from being used by a remote.");
                return TypedActionResult.fail(stackInHand);
            }


            write_npt(stackInHand,lookingAt,foundBlockState, world);

            return super.use(world, user, hand);
        }

        if(stackInHand.hasNbt()){
            this.read_npt(stackInHand);
            BlockHitResult lookingAt = (BlockHitResult) user.raycast(10,0,true);
            var blockWorld = Objects.requireNonNull(world.getServer()).getWorld(RegistryKey.of(Registry.WORLD_KEY, new Identifier(blockWorldID)));

            if(blockWorld == null) {
                clear_nbt(stackInHand);
                sendMessage(user, "Block cannot be found.");
                return TypedActionResult.fail(stackInHand);
            }

            var blockState = blockWorld.getBlockState(blockPosition);
            var blockEntity = blockWorld.getBlockEntity(blockPosition);

            if(blockState == null || blockState.getBlock() == Blocks.AIR || storedBlock != blockState.getBlock())
            {
                clear_nbt(stackInHand);
                sendMessage(user, "Block cannot be found.");
                return TypedActionResult.fail(stackInHand);
            }

            if(!canUseRemote(user))
                return TypedActionResult.fail(stackInHand);


            if(blockEntity instanceof NamedScreenHandlerFactory)
            {
                user.openHandledScreen((NamedScreenHandlerFactory) blockWorld.getBlockEntity(blockPosition));
            }
            else{
                blockState.getBlock().onUse(blockState, blockWorld, blockPosition, user, hand, lookingAt);
            }

            if(remoteConfig.XpPerUse != -1 && !user.isCreative()) //Only subtract xp if not set to -1
                user.addExperience(-remoteConfig.XpPerUse);

            return TypedActionResult.success(stackInHand,true);
        }
        return TypedActionResult.fail(stackInHand);
    }

    private boolean canUseRemote(PlayerEntity user){
        if(user.isCreative())
            return true;

        if(remoteConfig.RangeOfRemote != -1 && !blockPosition.isWithinDistance(user.getPos(),remoteConfig.RangeOfRemote))
        {
            sendMessage(user, "Remote is out of configured range.");
            return false;
        }

        if(user.totalExperience <= remoteConfig.XpPerUse && remoteConfig.XpPerUse != -1 && !user.isCreative()){
            sendMessage(user, "Not enough xp to use remote.");
            return false;
        }

        return true;
    }

    public void clear_nbt(ItemStack stack){
        var nbt = stack.getOrCreateNbt();
        nbt.remove("pos");
        nbt.remove("world");
        nbt.remove("block");
        stack.setNbt(nbt);
    }

    public void write_npt(ItemStack stack, BlockHitResult hitResult,  BlockState blockState, World worldOfBlock){
        var nbt = stack.getOrCreateNbt();
        nbt.putLong("pos", hitResult.getBlockPos().asLong());
        nbt.putString("world", worldOfBlock.getRegistryKey().getValue().toString());
        nbt.putString("block", Registry.BLOCK.getId(blockState.getBlock()).toString());
        stack.setNbt(nbt);
    }

    public void read_npt(ItemStack stack){
        var nbt = stack.getOrCreateNbt();
        this.blockPosition = BlockPos.fromLong(nbt.getLong("pos"));
        this.blockWorldID = nbt.getString("world");
        this.storedBlock = Registry.BLOCK.get(new Identifier(nbt.getString("block")));
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        if(stack.hasNbt()){
            read_npt(stack);
            tooltip.add(Text.literal("§eControlling§r: " + storedBlock.getName().getString()));
        }
        else {
            tooltip.add(Text.literal("§eSHIFT§r + §eRIGHT CLICK§r a block to control it."));
        }

        super.appendTooltip(stack, world, tooltip, context);
    }

    public void sendMessage(PlayerEntity player, String message){
        player.sendMessage(Text.literal(message), true);
    }
}
