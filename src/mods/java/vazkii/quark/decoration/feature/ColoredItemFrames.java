package vazkii.quark.decoration.feature;

import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vazkii.arl.recipe.RecipeHandler;
import vazkii.arl.util.ProxyRegistry;
import vazkii.quark.base.Quark;
import vazkii.quark.base.lib.LibMisc;
import vazkii.quark.base.module.Feature;
import vazkii.quark.decoration.client.render.RenderColoredItemFrame;
import vazkii.quark.decoration.entity.EntityColoredItemFrame;
import vazkii.quark.decoration.item.ItemColoredItemFrame;

public class ColoredItemFrames extends Feature {
   public static Item colored_item_frame;

   public ColoredItemFrames() {
   }

   public void preInit(FMLPreInitializationEvent event) {
      colored_item_frame = new ItemColoredItemFrame();
      String coloredItemFrameName = "quark:colored_item_frame";
      EntityRegistry.registerModEntity(new ResourceLocation(coloredItemFrameName), EntityColoredItemFrame.class, coloredItemFrameName, 0, Quark.instance, 256, 64, false);

      for(int i = 0; i < 16; ++i) {
         RecipeHandler.addShapelessOreDictRecipe(ProxyRegistry.newStack(colored_item_frame, 1, i), new Object[]{ProxyRegistry.newStack(Items.ITEM_FRAME), LibMisc.OREDICT_DYES.get(15 - i)});
      }

   }

   @SideOnly(Side.CLIENT)
   public void preInitClient() {
      RenderingRegistry.registerEntityRenderingHandler(EntityColoredItemFrame.class, RenderColoredItemFrame.FACTORY);
   }

   public boolean requiresMinecraftRestartToEnable() {
      return true;
   }
}
