package org.orecruncher.dsurround.client.renderer;

import javax.annotation.Nonnull;
import net.minecraft.client.Minecraft;
import net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.orecruncher.dsurround.ModOptions;
import org.orecruncher.dsurround.client.handlers.EnvironStateHandler;
import org.orecruncher.lib.math.MathStuff;

@SideOnly(Side.CLIENT)
public class BadgeRenderLayer implements LayerRenderer<EntityLivingBase> {
   private static final float CONST = 2.8647888F;
   protected final BadgeRenderLayer.IBadgeDisplayCheck displayCheck;
   protected final BadgeRenderLayer.IEntityBadgeProvider badgeProvider;

   public BadgeRenderLayer(@Nonnull BadgeRenderLayer.IBadgeDisplayCheck check, @Nonnull BadgeRenderLayer.IEntityBadgeProvider provider) {
      this.displayCheck = check;
      this.badgeProvider = provider;
   }

   public void doRenderLayer(@Nonnull EntityLivingBase entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
      if (!this.displayCheck.showBadge() && this.badgeProvider.show(entity)) {
         double rangeSq = (double)(ModOptions.effects.specialEffectRange * ModOptions.effects.specialEffectRange);
         double distSq = entity.getDistanceSq(EnvironStateHandler.EnvironState.getPlayer());
         if (distSq <= rangeSq) {
            ItemStack stackToRender = this.badgeProvider.getStackToDisplay(entity);
            if (!stackToRender.isEmpty()) {
               float age = (float)entity.ticksExisted + partialTicks;
               float s = this.badgeProvider.scale(entity);
               float dY = this.badgeProvider.adjustY(entity) + MathStuff.sin(age / 20.0F) * 0.1F;
               GlStateManager.pushMatrix();
               GlStateManager.rotate(age * 2.8647888F, 0.0F, 1.0F, 0.0F);
               GlStateManager.rotate(180.0F, 0.0F, 0.0F, 1.0F);
               GlStateManager.translate(0.0F, dY, 0.0F);
               GlStateManager.scale(s, s, s);
               Minecraft.getMinecraft().getRenderItem().renderItem(stackToRender, TransformType.GROUND);
               GlStateManager.popMatrix();
            }
         }
      }
   }

   public boolean shouldCombineTextures() {
      return false;
   }

   public interface IBadgeDisplayCheck {
      boolean showBadge();
   }

   public interface IEntityBadgeProvider {
      @Nonnull
      ItemStack getStackToDisplay(@Nonnull EntityLivingBase var1);

      default float adjustY(@Nonnull EntityLivingBase e) {
         return 0.15F;
      }

      default float scale(@Nonnull EntityLivingBase e) {
         return 0.5F;
      }

      default boolean show(@Nonnull EntityLivingBase e) {
         return true;
      }
   }
}
