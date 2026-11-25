package reborncore.common.logic;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.GameRegistry;
import reborncore.RebornCore;
import reborncore.RebornRegistry;

public class LogicUtils {
   public LogicUtils() {
   }

   public static Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
      if (world.getTileEntity(new BlockPos(x, y, z)) != null && world.getTileEntity(new BlockPos(x, y, z)) instanceof LogicController) {
         LogicController machine = (LogicController)world.getTileEntity(new BlockPos(x, y, z));
         return new LogicContainer(player, machine);
      } else {
         return null;
      }
   }

   public static Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
      if (world.getTileEntity(new BlockPos(x, y, z)) != null && world.getTileEntity(new BlockPos(x, y, z)) instanceof LogicController) {
         LogicController machine = (LogicController)world.getTileEntity(new BlockPos(x, y, z));
         return new LogicGui(player, machine);
      } else {
         return null;
      }
   }

   public static void registerLogicController(Block block, LogicController logicController) {
      RebornRegistry.registerBlock(block, logicController.getName());
      GameRegistry.registerTileEntity(logicController.getClass(), logicController.getName());
   }

   public static void openGui(EntityPlayer player, LogicController machine) {
      if (!player.isSneaking()) {
         player.openGui(RebornCore.INSTANCE, 0, machine.getWorld(), machine.getPos().getX(), machine.getPos().getY(), machine.getPos().getZ());
      }

   }
}
