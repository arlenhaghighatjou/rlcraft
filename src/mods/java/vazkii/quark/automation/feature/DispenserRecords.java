package vazkii.quark.automation.feature;

import java.util.function.Consumer;
import javax.annotation.Nonnull;
import net.minecraft.block.BlockDispenser;
import net.minecraft.block.BlockJukebox;
import net.minecraft.block.BlockJukebox.TileEntityJukebox;
import net.minecraft.block.state.IBlockState;
import net.minecraft.dispenser.BehaviorDefaultDispenseItem;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemRecord;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import vazkii.quark.base.module.Feature;

public class DispenserRecords extends Feature {
   public DispenserRecords() {
   }

   public void postInit() {
      DispenserRecords.BehaviourRecord behaviour = new DispenserRecords.BehaviourRecord();
      Item.REGISTRY.forEach((i) -> {
         if (i instanceof ItemRecord) {
            BlockDispenser.DISPENSE_BEHAVIOR_REGISTRY.putObject(i, behaviour);
         }

      });
   }

   public boolean requiresMinecraftRestartToEnable() {
      return true;
   }

   public class BehaviourRecord extends BehaviorDefaultDispenseItem {
      public BehaviourRecord() {
      }

      @Nonnull
      protected ItemStack dispenseStack(IBlockSource source, ItemStack stack) {
         EnumFacing facing = (EnumFacing)source.getBlockState().getValue(BlockDispenser.FACING);
         BlockPos pos = source.getBlockPos().offset(facing);
         World world = source.getWorld();
         IBlockState state = world.getBlockState(pos);
         if (state.getBlock() == Blocks.JUKEBOX) {
            TileEntityJukebox jukebox = (TileEntityJukebox)world.getTileEntity(pos);
            if (jukebox != null) {
               ItemStack currentRecord = jukebox.getRecord();
               ((BlockJukebox)state.getBlock()).insertRecord(world, pos, state, stack);
               world.playEvent((EntityPlayer)null, 1010, pos, Item.getIdFromItem(stack.getItem()));
               return currentRecord;
            }
         }

         return super.dispenseStack(source, stack);
      }
   }
}
