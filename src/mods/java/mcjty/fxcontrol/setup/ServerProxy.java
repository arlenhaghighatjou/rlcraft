package mcjty.fxcontrol.setup;

import com.google.common.util.concurrent.ListenableFuture;
import java.util.concurrent.Callable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class ServerProxy implements IProxy {
   public ServerProxy() {
   }

   public void preInit(FMLPreInitializationEvent e) {
   }

   public void init(FMLInitializationEvent e) {
   }

   public void postInit(FMLPostInitializationEvent e) {
   }

   public World getClientWorld() {
      throw new IllegalStateException("This should only be called from client side");
   }

   public EntityPlayer getClientPlayer() {
      throw new IllegalStateException("This should only be called from client side");
   }

   public <V> ListenableFuture<V> addScheduledTaskClient(Callable<V> callableToSchedule) {
      throw new IllegalStateException("This should only be called from client side");
   }

   public ListenableFuture<Object> addScheduledTaskClient(Runnable runnableToSchedule) {
      throw new IllegalStateException("This should only be called from client side");
   }
}
