package c4.corpsecomplex.common.modules.compatibility.thut_wearables;

import c4.corpsecomplex.common.Module;
import c4.corpsecomplex.common.Submodule;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.Optional.Method;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerRespawnEvent;
import thut.wearables.ThutWearables;
import thut.wearables.network.PacketSyncWearables;

public class ThutModule extends Submodule {
   private static final String MOD_ID = "thut_wearables";
   static boolean keepThut;

   @SubscribeEvent
   @Method(
      modid = "thut_wearables"
   )
   public void onPlayerRespawnFinish(PlayerRespawnEvent e) {
      updateClientThut(e.player);
   }

   public ThutModule(Module parentModule) {
      super(parentModule, (String)null);
   }

   public void loadModuleConfig() {
      keepThut = this.getBool("Keep Thut Wearables", false, "Set to true to keep Thut Wearables on death", false);
   }

   public boolean hasEvents() {
      return true;
   }

   @Method(
      modid = "thut_wearables"
   )
   private static void updateClientThut(EntityPlayer player) {
      ThutWearables.packetPipeline.sendTo(new PacketSyncWearables(player), (EntityPlayerMP)player);
   }
}
