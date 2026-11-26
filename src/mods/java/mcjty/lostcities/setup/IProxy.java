package mcjty.lostcities.setup;

import net.lax1dude.eaglercraft.v1_8.futures.ListenableFuture;
import java.util.concurrent.Callable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public interface IProxy {
   void preInit(FMLPreInitializationEvent var1);

   void init(FMLInitializationEvent var1);

   void postInit(FMLPostInitializationEvent var1);

   World getClientWorld();

   EntityPlayer getClientPlayer();

   <V> ListenableFuture<V> addScheduledTaskClient(Callable<V> var1);

   ListenableFuture<Object> addScheduledTaskClient(Runnable var1);
}
