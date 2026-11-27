package dev.satyrn.wolfarmor.client;

import dev.satyrn.wolfarmor.WolfArmorMod;
import dev.satyrn.wolfarmor.api.compatibility.Compatibility;
import dev.satyrn.wolfarmor.api.util.Items;
import dev.satyrn.wolfarmor.client.event.RenderEventHandler;
import dev.satyrn.wolfarmor.common.CommonProxy;
import dev.satyrn.wolfarmor.item.ItemWolfArmor;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.entity.passive.EntityWolf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IThreadListener;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLLoadCompleteEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
@EventBusSubscriber({Side.CLIENT})
public class ClientProxy extends CommonProxy {
   public ClientProxy() {
   }

   public void registerEntityRenderingHandlers() {
      RenderManager renderManager = Minecraft.getMinecraft().getRenderManager();
      RenderLiving<?> renderWolf = (RenderLiving)renderManager.entityRenderMap.get(EntityWolf.class);
      LayerRenderer<?> armor = Compatibility.getArmorLayer(renderWolf);
      LayerRenderer<?> backpack = Compatibility.getBackpackLayer(renderWolf);
      if (armor != null) {
         renderWolf.addLayer(armor);
      } else {
         WolfArmorMod.getLogger().warning("Unable to initialize armor layer renderer!");
      }

      if (backpack != null) {
         renderWolf.addLayer(backpack);
      } else {
         WolfArmorMod.getLogger().warning("Unable to initialize backpack layer renderer!");
      }

   }

   public void registerItemRenders(@Nonnull FMLInitializationEvent initializationEvent) {
      registerItemModel(Items.LEATHER_WOLF_ARMOR);
      registerItemModel(Items.CHAINMAIL_WOLF_ARMOR);
      registerItemModel(Items.IRON_WOLF_ARMOR);
      registerItemModel(Items.GOLD_WOLF_ARMOR);
      registerItemModel(Items.DIAMOND_WOLF_ARMOR);
   }

   public void registerItemColorHandlers(@Nonnull FMLInitializationEvent initializationEvent) {
      Minecraft.getMinecraft().getItemColors().registerItemColorHandler(new IItemColor() {
         public int colorMultiplier(@Nonnull ItemStack stack, int tintIndex) {
            return tintIndex > 0 ? -1 : ((ItemWolfArmor)stack.getItem()).getColor(stack);
         }
      }, new Item[]{Items.LEATHER_WOLF_ARMOR});
   }

   @SideOnly(Side.CLIENT)
   private static void registerItemModel(@Nullable Item item) {
      if (item != null && item.getRegistryName() != null) {
         ModelResourceLocation resource = new ModelResourceLocation(item.getRegistryName().toString(), "inventory");
         ModelLoader.registerItemVariants(item, new ResourceLocation[]{item.getRegistryName()});
         Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(item, 0, resource);
      }

   }

   public IThreadListener getThreadFromContext(MessageContext context) {
      return (IThreadListener)(context.side.isClient() ? Minecraft.getMinecraft() : super.getThreadFromContext(context));
   }

   public EntityPlayer getPlayerFromContext(MessageContext context) {
      return (EntityPlayer)(context.side.isClient() ? Minecraft.getMinecraft().player : super.getPlayerFromContext(context));
   }

   public Side getCurrentSide() {
      return Side.CLIENT;
   }

   public void registerEventListeners() {
      super.registerEventListeners();
      (new RenderEventHandler()).register(MinecraftForge.EVENT_BUS);
   }

   public void postInit(FMLPostInitializationEvent event) {
      super.postInit(event);
      Compatibility.registerLayerProviders();
      Compatibility.postInit_Client(event);
   }

   public void loadComplete(@Nonnull FMLLoadCompleteEvent loadCompleteEvent) {
      super.loadComplete(loadCompleteEvent);
      Compatibility.loadComplete_Client(loadCompleteEvent);
   }

   public void preInit(@Nonnull FMLPreInitializationEvent preInitializationEvent) {
      super.preInit(preInitializationEvent);
      Compatibility.preInit_Client(preInitializationEvent);
   }

   public void init(@Nonnull FMLInitializationEvent initializationEvent) {
      super.init(initializationEvent);
      Compatibility.init_Client(initializationEvent);
   }
}
