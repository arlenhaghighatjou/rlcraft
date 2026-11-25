package vazkii.quark.tweaks.feature;

import net.minecraft.block.Block;
import net.minecraft.block.BlockDoor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.SoundCategory;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.LeftClickBlock;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import vazkii.quark.base.module.Feature;

public class KnockOnDoors extends Feature {
   public KnockOnDoors() {
   }

   @SubscribeEvent
   public void leftClick(LeftClickBlock event) {
      if (event.getEntityPlayer().getHeldItemMainhand().isEmpty()) {
         IBlockState state = event.getWorld().getBlockState(event.getPos());
         Block block = state.getBlock();
         if (block instanceof BlockDoor && state.getMaterial() == Material.WOOD) {
            event.getWorld().playSound((EntityPlayer)null, (double)event.getPos().getX(), (double)event.getPos().getY(), (double)event.getPos().getZ(), block.getSoundType(state, event.getWorld(), event.getPos(), event.getEntityPlayer()).getPlaceSound(), SoundCategory.PLAYERS, 1.0F, 1.0F);
         }
      }

   }

   public boolean hasSubscriptions() {
      return true;
   }
}
