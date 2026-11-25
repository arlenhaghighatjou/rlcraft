package reborncore.common.blocks;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraftforge.event.entity.player.PlayerEvent.BreakSpeed;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickBlock;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import reborncore.api.ToolManager;
import reborncore.common.RebornCoreConfig;

public class BlockWrenchEventHandler {
   public static List<Block> wrenableBlocks = new ArrayList();

   public BlockWrenchEventHandler() {
   }

   @SubscribeEvent
   public static void rightClickBlockEvent(RightClickBlock event) {
      EnumHand hand = event.getHand();
      if (hand != EnumHand.OFF_HAND) {
         if (ToolManager.INSTANCE.canHandleTool(event.getEntityPlayer().getHeldItem(hand))) {
            IBlockState state = event.getWorld().getBlockState(event.getPos());
            if (wrenableBlocks.contains(state.getBlock())) {
               Block block = state.getBlock();
               block.onBlockActivated(event.getWorld(), event.getPos(), state, event.getEntityPlayer(), hand, event.getFace(), 0.0F, 0.0F, 0.0F);
               event.setCanceled(true);
               event.setCancellationResult(EnumActionResult.SUCCESS);
            }
         }

      }
   }

   @SubscribeEvent
   public static void getDigSpeed(BreakSpeed event) {
      if (wrenableBlocks.contains(event.getState().getBlock()) && RebornCoreConfig.wrenchRequired) {
         event.setNewSpeed(event.getOriginalSpeed() / 25.0F);
      }

   }
}
