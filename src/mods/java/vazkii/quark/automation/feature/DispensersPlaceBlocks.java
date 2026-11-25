package vazkii.quark.automation.feature;

import java.util.Arrays;
import java.util.List;
import javax.annotation.Nonnull;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDispenser;
import net.minecraft.block.BlockPistonBase;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Bootstrap.BehaviorDispenseOptional;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import vazkii.quark.base.module.Feature;
import vazkii.quark.misc.feature.LockDirectionHotkey;

public class DispensersPlaceBlocks extends Feature {
   public static List<String> blacklist;

   public DispensersPlaceBlocks() {
   }

   public void setupConfig() {
      String[] blacklistArray = this.loadPropStringList("Blacklist", "Blocks that dispensers should not be able to place", new String[]{"minecraft:water", "minecraft:flowing_water", "minecraft:lava", "minecraft:flowing_lava", "minecraft:fire", "botania:specialflower"});
      blacklist = Arrays.asList(blacklistArray);
   }

   public void postInit() {
      for(ResourceLocation r : Block.REGISTRY.getKeys()) {
         Block block = (Block)Block.REGISTRY.getObject(r);
         Item item = Item.getItemFromBlock(block);
         if (item instanceof ItemBlock && !blacklist.contains(r.toString()) && !BlockDispenser.DISPENSE_BEHAVIOR_REGISTRY.containsKey(item)) {
            BlockDispenser.DISPENSE_BEHAVIOR_REGISTRY.putObject(item, new DispensersPlaceBlocks.BehaviourBlock((ItemBlock)item, block));
         }
      }

   }

   public boolean requiresMinecraftRestartToEnable() {
      return true;
   }

   public String[] getIncompatibleMods() {
      return new String[]{"blockdispenser"};
   }

   public class BehaviourBlock extends BehaviorDispenseOptional {
      private final ItemBlock item;
      private final Block block;

      public BehaviourBlock(ItemBlock item, Block block) {
         this.item = item;
         this.block = block;
      }

      @Nonnull
      public ItemStack dispenseStack(IBlockSource source, ItemStack stack) {
         this.successful = false;
         EnumFacing facing = (EnumFacing)source.getBlockState().getValue(BlockDispenser.FACING);
         BlockPos pos = source.getBlockPos().offset(facing);
         World world = source.getWorld();
         if (world.isAirBlock(pos) && this.block.canPlaceBlockAt(world, pos)) {
            int meta = this.item.getMetadata(stack.getItemDamage());
            IBlockState state;
            if (!(this.block instanceof BlockPistonBase)) {
               state = this.block.getStateFromMeta(meta);
            } else {
               state = this.block.getDefaultState();
            }

            this.successful = true;
            LockDirectionHotkey.setBlockRotated(world, state, pos, facing);
            SoundType soundtype = this.block.getSoundType(state, world, pos, (Entity)null);
            world.playSound((EntityPlayer)null, pos, soundtype.getPlaceSound(), SoundCategory.BLOCKS, (soundtype.getVolume() + 1.0F) / 2.0F, soundtype.getPitch() * 0.8F);
            stack.shrink(1);
            return stack;
         } else {
            return stack;
         }
      }
   }
}
