package reborncore.common.logic;

import javax.annotation.Nullable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;

public class LogicControllerGuiHandler implements IGuiHandler {
   public LogicControllerGuiHandler() {
   }

   @Nullable
   public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
      if (world.getTileEntity(new BlockPos(x, y, z)) != null && world.getTileEntity(new BlockPos(x, y, z)) instanceof LogicController) {
         LogicController machine = (LogicController)world.getTileEntity(new BlockPos(x, y, z));
         return new LogicContainer(player, machine);
      } else {
         return null;
      }
   }

   @Nullable
   public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
      if (world.getTileEntity(new BlockPos(x, y, z)) != null && world.getTileEntity(new BlockPos(x, y, z)) instanceof LogicController) {
         LogicController machine = (LogicController)world.getTileEntity(new BlockPos(x, y, z));
         return new LogicGui(player, machine);
      } else {
         return null;
      }
   }
}
