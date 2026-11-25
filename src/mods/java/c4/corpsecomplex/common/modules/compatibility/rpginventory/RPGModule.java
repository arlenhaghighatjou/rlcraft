package c4.corpsecomplex.common.modules.compatibility.rpginventory;

import c4.corpsecomplex.common.Module;
import c4.corpsecomplex.common.Submodule;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.Optional.Method;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerRespawnEvent;
import subaraki.rpginventory.network.PacketHandler;
import subaraki.rpginventory.network.PacketInventoryToClient;

public class RPGModule extends Submodule {
   private static final String MOD_ID = "rpginventory";
   static boolean keepRPG;

   @SubscribeEvent
   @Method(
      modid = "rpginventory"
   )
   public void onPlayerRespawnFinish(PlayerRespawnEvent e) {
      updateClientRPG(e.player);
   }

   public RPGModule(Module parentModule) {
      super(parentModule, (String)null);
   }

   public void loadModuleConfig() {
      keepRPG = this.getBool("Keep RPG Inventory", false, "Set to true to keep RPG Inventory on death", false);
   }

   public boolean hasEvents() {
      return true;
   }

   @Method(
      modid = "rpginventory"
   )
   private static void updateClientRPG(EntityPlayer player) {
      PacketHandler.NETWORK.sendTo(new PacketInventoryToClient(player), (EntityPlayerMP)player);
   }
}
