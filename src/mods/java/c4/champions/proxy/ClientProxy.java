package c4.champions.proxy;

import c4.champions.client.EventHandlerClient;
import c4.champions.client.fx.ParticleRank;
import c4.champions.client.renderer.RenderArcticSpark;
import c4.champions.client.renderer.RenderCinderSpark;
import c4.champions.common.entity.EntityArcticSpark;
import c4.champions.common.entity.EntityCinderSpark;
import c4.champions.common.init.ChampionsRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EntityList.EntityEggInfo;
import net.minecraft.item.Item;
import net.minecraft.item.ItemMonsterPlacer;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;

@EventBusSubscriber(
   modid = "champions",
   value = {Side.CLIENT}
)
public class ClientProxy implements IProxy {
   public ClientProxy() {
   }

   public void preInit(FMLPreInitializationEvent evt) {
      RenderingRegistry.registerEntityRenderingHandler(EntityArcticSpark.class, RenderArcticSpark.FACTORY);
      RenderingRegistry.registerEntityRenderingHandler(EntityCinderSpark.class, RenderCinderSpark.FACTORY);
   }

   public void init(FMLInitializationEvent evt) {
      MinecraftForge.EVENT_BUS.register(new EventHandlerClient());
      Minecraft.getMinecraft().getItemColors().registerItemColorHandler((stack, tintIndex) -> {
         EntityEggInfo entitylist$entityegginfo = (EntityEggInfo)EntityList.ENTITY_EGGS.get(ItemMonsterPlacer.getNamedIdFrom(stack));
         if (entitylist$entityegginfo == null) {
            return -1;
         } else {
            return tintIndex == 0 ? entitylist$entityegginfo.primaryColor : entitylist$entityegginfo.secondaryColor;
         }
      }, new Item[]{ChampionsRegistry.championEgg});
   }

   public void generateRankParticle(EntityLivingBase living, int color) {
      Minecraft.getMinecraft().effectRenderer.addEffect(new ParticleRank(living.world, living.posX + (living.getRNG().nextDouble() - 0.5D) * (double)living.width, living.posY + living.getRNG().nextDouble() * (double)living.height, living.posZ + (living.getRNG().nextDouble() - 0.5D) * (double)living.width, 0.0D, 0.0D, 0.0D, color));
   }

   @SubscribeEvent
   public static void registerModels(ModelRegistryEvent evt) {
      ModelLoader.setCustomModelResourceLocation(ChampionsRegistry.championEgg, 0, new ModelResourceLocation(ChampionsRegistry.championEgg.getRegistryName(), "inventory"));
   }
}
