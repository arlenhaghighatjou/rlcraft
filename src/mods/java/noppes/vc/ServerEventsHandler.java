package noppes.vc;

import net.minecraftforge.fml.common.eventhandler.EventBus;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickBlock;
import noppes.vc.blocks.tiles.TileBanner;

public class ServerEventsHandler {
   public ServerEventsHandler() {
   }

   public void register(EventBus bus) {
      bus.register(RightClickBlock.class, this::invoke);
   }

   public void invoke(RightClickBlock event) {
      if (event.getHand() == EnumHand.MAIN_HAND) {
         EntityPlayer player = event.getEntityPlayer();
         BlockPos pos = event.getPos();
         IBlockState state = player.world.getBlockState(pos);
         Block block = state.getBlock();
         if (!player.world.isRemote && (block == VCBlocks.banner || block == VCBlocks.wall_banner || block == VCBlocks.sign)) {
            ItemStack item = player.inventory.getCurrentItem();
            if (item == null || item.getItem() == null) {
               return;
            }

            int meta = block.getMetaFromState(state);
            if (meta >= 7) {
               pos = pos.down();
            }

            TileBanner tile = (TileBanner)player.world.getTileEntity(pos);
            if (!tile.canEdit()) {
               return;
            }

            if (!player.world.isRemote) {
               tile.icon = item.copy();
               tile.markDirty();
               player.world.notifyBlockUpdate(pos, state, state, 3);
               event.setCanceled(true);
            }
         }

      }
   }
}
