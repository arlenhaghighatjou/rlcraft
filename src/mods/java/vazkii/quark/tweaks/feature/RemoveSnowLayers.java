package vazkii.quark.tweaks.feature;

import net.minecraft.block.BlockSnow;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemSpade;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickBlock;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import vazkii.quark.base.module.Feature;

public class RemoveSnowLayers extends Feature {
   public RemoveSnowLayers() {
   }

   @SubscribeEvent
   public void onPlayerRightClick(RightClickBlock event) {
      if (event.getFace() == EnumFacing.UP) {
         World world = event.getWorld();
         BlockPos pos = event.getPos();
         EntityPlayer player = event.getEntityPlayer();
         IBlockState state = world.getBlockState(pos);
         if (state.getBlock() == Blocks.SNOW_LAYER || state.getBlock() == Blocks.SNOW) {
            ItemStack stack = event.getEntityPlayer().getHeldItem(event.getHand());
            if (!stack.isEmpty() && stack.getItem() instanceof ItemSpade) {
               if (state.getBlock() == Blocks.SNOW) {
                  world.setBlockState(pos, Blocks.SNOW_LAYER.getDefaultState().withProperty(BlockSnow.LAYERS, Integer.valueOf(7)));
               } else {
                  int layers = ((Integer)state.getValue(BlockSnow.LAYERS)).intValue();
                  if (layers == 1) {
                     world.setBlockToAir(pos);
                  } else {
                     world.setBlockState(pos, state.withProperty(BlockSnow.LAYERS, Integer.valueOf(layers - 1)));
                  }
               }

               world.playSound(event.getEntityPlayer(), pos, SoundEvents.ITEM_SHOVEL_FLATTEN, SoundCategory.BLOCKS, 1.0F, 1.0F);
               stack.damageItem(1, event.getEntityPlayer());
               player.swingArm(event.getHand());
               event.setCanceled(true);
            }

         }
      }
   }

   public boolean hasSubscriptions() {
      return true;
   }

   public String getFeatureIngameConfigName() {
      return "Shave Snow Layers";
   }
}
