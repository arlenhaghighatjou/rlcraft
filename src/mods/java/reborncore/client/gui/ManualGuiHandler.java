package reborncore.client.gui;

import javax.annotation.Nullable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;
import reborncore.modcl.manual.GuiTeamRebornManual;

public class ManualGuiHandler implements IGuiHandler {
   public ManualGuiHandler() {
   }

   @Nullable
   public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
      return null;
   }

   @Nullable
   public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
      return ID == 0 ? new GuiTeamRebornManual() : null;
   }
}
