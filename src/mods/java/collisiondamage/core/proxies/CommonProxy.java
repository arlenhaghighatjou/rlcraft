package collisiondamage.core.proxies;

import collision.handlers.EventHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.IThreadListener;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class CommonProxy {
   public CommonProxy() {
   }

   public boolean isClient() {
      return false;
   }

   public void registerHandlers() {
      (new EventHandler()).register(MinecraftForge.EVENT_BUS);
   }

   public IThreadListener getListener(MessageContext ctx) {
      return (WorldServer)ctx.getServerHandler().player.world;
   }

   public EntityPlayer getPlayer(MessageContext ctx) {
      return ctx.getServerHandler().player;
   }
}
