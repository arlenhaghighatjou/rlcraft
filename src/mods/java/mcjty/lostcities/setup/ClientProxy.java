package mcjty.lostcities.setup;

import com.google.common.util.concurrent.ListenableFuture;
import java.util.concurrent.Callable;
import mcjty.lostcities.ClientEventHandlers;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class ClientProxy implements IProxy {
   public ClientProxy() {
   }

   public void preInit(FMLPreInitializationEvent e) {
   }

   public void init(FMLInitializationEvent e) {
      MinecraftForge.EVENT_BUS.register(new ClientEventHandlers());
   }

   public void postInit(FMLPostInitializationEvent e) {
   }

   public World getClientWorld() {
      return Minecraft.getMinecraft().world;
   }

   public EntityPlayer getClientPlayer() {
      return Minecraft.getMinecraft().player;
   }

   public <V> ListenableFuture<V> addScheduledTaskClient(Callable<V> callableToSchedule) {
      return Minecraft.getMinecraft().addScheduledTask(callableToSchedule);
   }

   public ListenableFuture<Object> addScheduledTaskClient(Runnable runnableToSchedule) {
      return Minecraft.getMinecraft().addScheduledTask(runnableToSchedule);
   }
}
