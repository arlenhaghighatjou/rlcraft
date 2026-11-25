package vazkii.quark.vanity.feature;

import java.util.List;
import java.util.Map;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.renderer.entity.layers.LayerElytra;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.resources.I18n;
import net.minecraft.init.Items;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemDye;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vazkii.arl.util.ItemNBTHelper;
import vazkii.quark.base.module.Feature;
import vazkii.quark.vanity.client.layer.LayerBetterElytra;
import vazkii.quark.vanity.recipe.ElytraDyeingRecipe;

public class DyableElytra extends Feature {
   public static final String TAG_ELYTRA_DYE = "quark:elytraDye";

   public DyableElytra() {
   }

   public void preInit(FMLPreInitializationEvent event) {
      new ElytraDyeingRecipe();
   }

   @SideOnly(Side.CLIENT)
   public void postInitClient() {
      Minecraft mc = Minecraft.getMinecraft();
      RenderManager manager = mc.getRenderManager();
      Map<String, RenderPlayer> renders = manager.getSkinMap();

      for(RenderPlayer render : renders.values()) {
         this.messWithRender(render);
      }

      mc.getItemColors().registerItemColorHandler((stack, tintIndex) -> {
         int color = ItemNBTHelper.getInt(stack, "quark:elytraDye", -1);
         return color != -1 && color != 15 ? ItemDye.DYE_COLORS[color] : -1;
      }, new Item[]{Items.ELYTRA});
   }

   @SideOnly(Side.CLIENT)
   private void messWithRender(RenderPlayer render) {
      List<LayerRenderer> list = (List)ObfuscationReflectionHelper.getPrivateValue(RenderLivingBase.class, render, "layerRenderers");
      LayerRenderer remove = null;

      for(LayerRenderer layer : list) {
         if (layer instanceof LayerElytra) {
            remove = layer;
            break;
         }
      }

      list.remove(remove);
      list.add(new LayerBetterElytra(render));
   }

   @SubscribeEvent
   @SideOnly(Side.CLIENT)
   public void onTooltip(ItemTooltipEvent event) {
      ItemStack stack = event.getItemStack();
      if (!stack.isEmpty() && stack.getItem() == Items.ELYTRA) {
         int color = ItemNBTHelper.getInt(stack, "quark:elytraDye", 15);
         EnumDyeColor dye = EnumDyeColor.byDyeDamage(color);
         if (dye != EnumDyeColor.WHITE) {
            event.getToolTip().add(I18n.format("quark.dyedElytra", new Object[]{I18n.format("quark.dye." + dye.getUnlocalizedName(), new Object[0])}));
         }
      }

   }

   public boolean hasSubscriptions() {
      return this.isClient();
   }

   public boolean requiresMinecraftRestartToEnable() {
      return true;
   }
}
