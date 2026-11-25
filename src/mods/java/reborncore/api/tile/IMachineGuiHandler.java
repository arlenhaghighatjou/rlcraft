package reborncore.api.tile;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface IMachineGuiHandler {
   void open(EntityPlayer var1, BlockPos var2, World var3);
}
