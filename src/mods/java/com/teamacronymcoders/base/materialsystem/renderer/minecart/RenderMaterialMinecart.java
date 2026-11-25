package com.teamacronymcoders.base.materialsystem.renderer.minecart;

import com.teamacronymcoders.base.materialsystem.entity.minecart.EntityMaterialMinecart;
import com.teamacronymcoders.base.renderer.entity.loader.IEntityRenderer;
import com.teamacronymcoders.base.renderer.entity.minecart.RenderMinecartBase;
import java.awt.Color;
import java.util.Optional;
import java.util.function.Consumer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderMaterialMinecart extends RenderMinecartBase<EntityMaterialMinecart> implements IEntityRenderer {
   public RenderMaterialMinecart() {
      super((RenderManager)null);
   }

   public RenderMaterialMinecart(RenderManager renderManager) {
      super(renderManager);
   }

   protected void renderCartModel(EntityMaterialMinecart entity) {
      Optional<Color> color = Optional.ofNullable(entity.getMaterialPart().getFullColor());
      GlStateManager.pushMatrix();
      color.ifPresent((value) -> {
         GlStateManager.color((float)value.getRed() / 255.0F, (float)value.getGreen() / 255.0F, (float)value.getBlue() / 255.0F);
      });
      GlStateManager.enableBlend();
      GlStateManager.blendFunc(770, 771);
      this.modelMinecart.render(entity, 0.0F, 0.0F, -0.1F, 0.0F, 0.0F, 0.0625F);
      GlStateManager.popMatrix();
   }

   public Class getEntityClass() {
      return EntityMaterialMinecart.class;
   }

   public IRenderFactory getRenderFactory() {
      return RenderMaterialMinecart::new;
   }
}
