package bq_standard.advancment_hacks;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.fml.common.FMLCommonHandler;

public class AdvListenerManager {
   public static final AdvListenerManager INSTANCE = new AdvListenerManager();
   private final List<BqsAdvListener<?>> listenerList = new ArrayList();

   public AdvListenerManager() {
   }

   public void registerListener(BqsAdvListener<?> listener) {
      MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
      if (server != null) {
         server.addScheduledTask(() -> {
            this.listenerList.add(listener);
         });
      }

   }

   public void updateAll() {
      MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();

      for(EntityPlayerMP player : server.getPlayerList().getPlayers()) {
         for(BqsAdvListener<?> advl : this.listenerList) {
            advl.unregisterSelf(player.getAdvancements());
            if (advl.verify()) {
               advl.registerSelf(player.getAdvancements());
            }
         }
      }

      this.listenerList.removeIf((advlx) -> {
         return !advl.verify();
      });
   }
}
