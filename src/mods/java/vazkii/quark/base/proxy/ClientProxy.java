package vazkii.quark.base.proxy;

import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.resources.IResourcePack;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.client.resource.IResourceType;
import net.minecraftforge.client.resource.VanillaResourceType;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import vazkii.quark.base.client.ContributorRewardHandler;
import vazkii.quark.base.client.ResourceProxy;
import vazkii.quark.base.client.gui.config.ConfigEvents;
import vazkii.quark.base.module.ModuleLoader;
import vazkii.quark.vanity.client.emotes.EmoteHandler;
import vazkii.quark.vanity.feature.EmoteSystem;

public class ClientProxy extends CommonProxy {
   private static ResourceProxy resourceProxy;

   public ClientProxy() {
   }

   public void preInit(FMLPreInitializationEvent event) {
      List<IResourcePack> packs = (List)ObfuscationReflectionHelper.getPrivateValue(Minecraft.class, Minecraft.getMinecraft(), "defaultResourcePacks");
      resourceProxy = new ResourceProxy(event.getSourceFile());
      packs.add(resourceProxy);
      EmoteSystem.addResourcePack(packs);
      super.preInit(event);
      ModuleLoader.preInitClient(event);
      FMLClientHandler.instance().refreshResources(new IResourceType[]{VanillaResourceType.TEXTURES});
   }

   public void init(FMLInitializationEvent event) {
      ContributorRewardHandler.setupClient();
      super.init(event);
      ModuleLoader.initClient(event);
      MinecraftForge.EVENT_BUS.register(ConfigEvents.class);
   }

   public void postInit(FMLPostInitializationEvent event) {
      super.postInit(event);
      ModuleLoader.postInitClient(event);
   }

   public void doEmote(String playerName, String emoteName, int tier) {
      World world = Minecraft.getMinecraft().world;
      EntityPlayer player = world.getPlayerEntityByName(playerName);
      if (player instanceof AbstractClientPlayer) {
         EmoteHandler.putEmote((AbstractClientPlayer)player, emoteName, tier);
      }

   }

   public void addResourceOverride(String path, String file) {
      resourceProxy.addResource(path, file);
   }
}
