package reborncore.api;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ToolManager implements ICustomToolHandler {
   public static final ToolManager INSTANCE = new ToolManager();
   public List<ICustomToolHandler> customToolHandlerList = new ArrayList();

   public ToolManager() {
   }

   public boolean handleTool(ItemStack stack, BlockPos pos, World world, EntityPlayer player, EnumFacing side, boolean damage) {
      if (stack != null && !stack.isEmpty()) {
         if (stack.getItem() instanceof IToolHandler) {
            return ((IToolHandler)stack.getItem()).handleTool(stack, pos, world, player, side, damage);
         } else {
            for(ICustomToolHandler customToolHandler : this.customToolHandlerList) {
               if (customToolHandler.canHandleTool(stack)) {
                  return customToolHandler.handleTool(stack, pos, world, player, side, damage);
               }
            }

            return false;
         }
      } else {
         return false;
      }
   }

   public boolean canHandleTool(ItemStack stack) {
      if (stack != null && !stack.isEmpty()) {
         if (stack.getItem() instanceof IToolHandler) {
            return true;
         } else {
            for(ICustomToolHandler customToolHandler : this.customToolHandlerList) {
               if (customToolHandler.canHandleTool(stack)) {
                  return true;
               }
            }

            return false;
         }
      } else {
         return false;
      }
   }
}
