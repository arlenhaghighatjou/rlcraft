package vazkii.quark.decoration.client.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ModelManager;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.init.Items;
import net.minecraft.item.ItemDye;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vazkii.arl.util.ModelHandler;
import vazkii.quark.decoration.entity.EntityColoredItemFrame;
import vazkii.quark.decoration.entity.EntityFlatItemFrame;

@SideOnly(Side.CLIENT)
public class RenderColoredItemFrame extends RenderFlatItemFrame {
   public static final IRenderFactory<EntityItemFrame> FACTORY = RenderColoredItemFrame::new;

   public RenderColoredItemFrame(RenderManager renderManagerIn) {
      super(renderManagerIn);
   }

   protected void renderModel(EntityFlatItemFrame entity, Minecraft mc) {
      EntityColoredItemFrame entityColored = (EntityColoredItemFrame)entity;
      BlockRendererDispatcher blockrendererdispatcher = mc.getBlockRendererDispatcher();
      ModelManager modelmanager = blockrendererdispatcher.getBlockModelShapes().getModelManager();
      IBakedModel woodModel;
      IBakedModel coloredModel;
      if (!entity.getDisplayedItem().isEmpty() && entity.getDisplayedItem().getItem() == Items.FILLED_MAP) {
         woodModel = modelmanager.getModel((ModelResourceLocation)ModelHandler.resourceLocations.get("colored_item_frame_map_wood"));
         coloredModel = modelmanager.getModel((ModelResourceLocation)ModelHandler.resourceLocations.get("colored_item_frame_map"));
      } else {
         woodModel = modelmanager.getModel((ModelResourceLocation)ModelHandler.resourceLocations.get("colored_item_frame_wood"));
         coloredModel = modelmanager.getModel((ModelResourceLocation)ModelHandler.resourceLocations.get("colored_item_frame_normal"));
      }

      blockrendererdispatcher.getBlockModelRenderer().renderModelBrightnessColor(woodModel, 1.0F, 1.0F, 1.0F, 1.0F);
      int color = ItemDye.DYE_COLORS[15 - entityColored.getColor()];
      float r = (float)(color >> 16 & 255) / 255.0F;
      float g = (float)(color >> 8 & 255) / 255.0F;
      float b = (float)(color & 255) / 255.0F;
      blockrendererdispatcher.getBlockModelRenderer().renderModelBrightnessColor(coloredModel, 1.0F, r, g, b);
   }
}
