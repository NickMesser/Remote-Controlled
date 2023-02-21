package net.messer.remote_controlled.item.custom;

import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.messer.remote_controlled.RemoteControlled;
import net.messer.remote_controlled.config.RemoteControlledConfig;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import team.reborn.energy.api.base.SimpleBatteryItem;

import java.awt.*;
import java.util.List;
import java.util.Objects;


public class EnergyRemoteControl extends Item  implements SimpleBatteryItem {
    BlockPos blockPosition;
    String blockWorldID;
    public Block storedBlock;
    RemoteControlledConfig.EnergyRemoteConfig energyRemoteConfig;

    public EnergyRemoteControl(Settings settings) {
        super(settings);
        this.energyRemoteConfig = RemoteControlled.CONFIG.EnergyRemoteConfig;
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

            if(energyRemoteConfig.BlockBlackList.contains(Registries.BLOCK.getId(foundBlockState.getBlock()).toString())){
                sendMessage(user, "Block is blacklisted from being used by a remote.");
                return TypedActionResult.fail(stackInHand);
            }


            write_npt(stackInHand,lookingAt,foundBlockState, world);

            return super.use(world, user, hand);
        }

        if(stackInHand.hasNbt()){
            this.read_npt(stackInHand);
            BlockHitResult lookingAt = (BlockHitResult) user.raycast(10,0,true);
            var blockWorld = Objects.requireNonNull(world.getServer()).getWorld(RegistryKey.of(RegistryKeys.WORLD, new Identifier(blockWorldID)));
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

            if(!canUseRemote(user, stackInHand))
                return TypedActionResult.fail(stackInHand);

            if(!user.isCreative()){
                try(Transaction transaction = Transaction.openOuter()){
                    if(this.tryUseEnergy(stackInHand, energyRemoteConfig.EnergyPerUse)){
                        transaction.commit();
                    }
                    else{
                        return TypedActionResult.fail(stackInHand);
                    }
                }
            }

            if(blockEntity instanceof NamedScreenHandlerFactory)
            {
                user.openHandledScreen((NamedScreenHandlerFactory) blockWorld.getBlockEntity(blockPosition));
            }
            else{
                blockState.getBlock().onUse(blockState, blockWorld, blockPosition, user, hand, lookingAt);
            }

            return TypedActionResult.success(stackInHand,true);
        }
        return TypedActionResult.fail(stackInHand);
    }

    private boolean canUseRemote(PlayerEntity user, ItemStack remoteStack){

        if(user.isCreative())
            return true;

        if(energyRemoteConfig.RangeOfRemote != -1 && !blockPosition.isWithinDistance(user.getPos(),energyRemoteConfig.RangeOfRemote))
        {
            sendMessage(user, "Remote is out of configured range.");
            return false;
        }

        if(this.getStoredEnergy(remoteStack) < energyRemoteConfig.EnergyPerUse){
            sendMessage(user, "Not enough power to use remote.");
            return false;
        }

        return true;
    }

    @Override
    public boolean isItemBarVisible(ItemStack stack) {
        return true;
    }

    @Override
    public int getItemBarStep(ItemStack stack) {
        return (int) Math.round(((double)getStoredEnergy(stack) / (double) getEnergyCapacity()) * 13d);
    }

    @Override
    public int getItemBarColor(ItemStack stack) {
        return Color.decode("#00FF00").getRGB();
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
        nbt.putString("block", Registries.BLOCK.getId(blockState.getBlock()).toString());
        stack.setNbt(nbt);
    }

    public void read_npt(ItemStack stack){
        var nbt = stack.getOrCreateNbt();
        this.blockPosition = BlockPos.fromLong(nbt.getLong("pos"));
        this.blockWorldID = nbt.getString("world");
        this.storedBlock = Registries.BLOCK.get(new Identifier(nbt.getString("block")));
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

    @Override
    public long getEnergyCapacity() {
        return energyRemoteConfig.EnergyRemoteCapacity;
    }

    @Override
    public long getEnergyMaxInput() {
        return 4000;
    }

    @Override
    public long getEnergyMaxOutput() {
        return 4000;
    }

    public void sendMessage(PlayerEntity player, String message){
        player.sendMessage(Text.literal(message), true);
    }
}
