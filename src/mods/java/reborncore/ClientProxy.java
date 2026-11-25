package reborncore;

import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import reborncore.client.IconSupplier;
import reborncore.client.hud.StackInfoHUD;
import reborncore.client.models.HolidayRenderEvent;
import reborncore.client.multiblock.MultiblockRenderEvent;
import reborncore.common.multiblock.MultiblockClientTickHandler;

public class ClientProxy extends CommonProxy {
   public static MultiblockRenderEvent multiblockRenderEvent;

   public ClientProxy() {
   }

   public void preInit(FMLPreInitializationEvent event) {
      super.preInit(event);
      MinecraftForge.EVENT_BUS.register(HolidayRenderEvent.class);
      MinecraftForge.EVENT_BUS.register(new IconSupplier());
   }

   public void init(FMLInitializationEvent event) {
      super.init(event);
      MinecraftForge.EVENT_BUS.register(new StackInfoHUD());
      multiblockRenderEvent = new MultiblockRenderEvent();
      MinecraftForge.EVENT_BUS.register(multiblockRenderEvent);
   }

   public void postInit(FMLPostInitializationEvent event) {
      super.postInit(event);
      MinecraftForge.EVENT_BUS.register(new MultiblockClientTickHandler());
   }

   public World getClientWorld() {
      return Minecraft.getMinecraft().world;
   }

   public EntityPlayer getPlayer() {
      return Minecraft.getMinecraft().player;
   }

   public void getCrashData(List<String> list) {
      super.getCrashData(list);
      list.add("RenderEngine: " + (FMLClientHandler.instance().hasOptifine() ? "1" : "0"));
   }
}
