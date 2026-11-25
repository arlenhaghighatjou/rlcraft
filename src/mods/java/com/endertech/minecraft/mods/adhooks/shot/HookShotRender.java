package com.endertech.minecraft.mods.adhooks.shot;

import com.endertech.common.CommonMath;
import com.endertech.common.FloatBounds;
import com.endertech.minecraft.forge.ForgeLocating;
import com.endertech.minecraft.forge.ForgeRendering;
import com.endertech.minecraft.forge.entities.ForgeEntity;
import com.endertech.minecraft.forge.math.Rotation;
import com.endertech.minecraft.forge.math.Vect3d;
import com.endertech.minecraft.forge.units.UnitId;
import com.endertech.minecraft.mods.adhooks.init.HookType;
import com.endertech.minecraft.mods.adhooks.items.Rope;
import net.minecraft.client.Minecraft;
import net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public class HookShotRender extends Render<HookShot> {
   public HookShotRender(RenderManager renderManager) {
      super(renderManager);
   }

   public ResourceLocation getTextureLocation(HookType hookType, String namePostfix) {
      String name = UnitId.buildRegName(new String[]{hookType.hook.getId().getRegName(), namePostfix});
      return ForgeLocating.itemTexture("adhooks", name, new String[0]);
   }

   public ResourceLocation getTextureLocation(HookType hookType) {
      return hookType == HookType.WEB ? this.getTextureLocation(hookType, "") : TextureMap.LOCATION_BLOCKS_TEXTURE;
   }

   public ResourceLocation getBackTextureLocation(HookType hookType) {
      return this.getTextureLocation(hookType, "back");
   }

   public void renderHook(HookShot hookShot, double x, double y, double z, float yaw, float tickProgress) {
      HookType hookType = hookShot.getHookType();
      Rotation shotRotation = hookShot.getRotation(tickProgress);
      double pixel = 0.0625D;
      GlStateManager.pushMatrix();
      GlStateManager.translate((float)x, (float)y, (float)z);
      GlStateManager.enableRescaleNormal();
      GlStateManager.rotate(180.0F, 0.0F, 1.0F, 0.0F);
      GlStateManager.rotate(shotRotation.yaw, 0.0F, 1.0F, 0.0F);
      GlStateManager.rotate(shotRotation.pitch, 1.0F, 0.0F, 0.0F);
      GlStateManager.translate(-0.03125D, 0.0D, -0.4D);
      this.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
      if (this.renderOutlines) {
         GlStateManager.enableColorMaterial();
         GlStateManager.enableOutlineMode(this.getTeamColor(hookShot));
      }

      Minecraft.getMinecraft().getRenderItem().renderItem(hookType.hook.getDefaultInstance(), TransformType.GROUND);
      if (this.renderOutlines) {
         GlStateManager.disableOutlineMode();
         GlStateManager.disableColorMaterial();
      }

      GlStateManager.disableRescaleNormal();
      GlStateManager.popMatrix();
   }

   public void renderWeb(HookShot hookShot, double x, double y, double z, float yaw, float tickProgress) {
      if (hookShot != null && hookShot.getHookType() != null) {
         HookType hookType = hookShot.getHookType();
         Rotation shotRotation = hookShot.getRotation(tickProgress);
         float hookThickness = 0.0625F;
         GlStateManager.pushMatrix();
         GlStateManager.enableRescaleNormal();
         GlStateManager.disableLighting();
         GlStateManager.translate(x, y, z);
         GlStateManager.rotate(shotRotation.yaw - 90.0F, 0.0F, 1.0F, 0.0F);
         GlStateManager.rotate(shotRotation.pitch, 0.0F, 0.0F, 1.0F);
         switch(hookType) {
         case WEB:
            this.bindTexture(this.getBackTextureLocation(hookType));
            GlStateManager.rotate(90.0F, 0.0F, 1.0F, 0.0F);
            break;
         default:
            this.bindTexture(this.getTextureLocation(hookType));
            GlStateManager.rotate(45.0F, 0.0F, 0.0F, 1.0F);
         }

         double scale = 0.5D;
         GlStateManager.enableRescaleNormal();
         GlStateManager.scale(scale, scale, scale);
         switch(hookType) {
         case WEB:
            GlStateManager.translate(-0.5D, -0.5D, 0.03125D);
            break;
         default:
            GlStateManager.translate(-0.1D, -0.9D, 0.03125D);
         }

         ForgeRendering.renderItemLayerIn2D(16, 16, 0.0625F, 0.0F, 0.0F, 1.0F, 1.0F, 0.0F, 0.0F, 1.0F, 1.0F, 0.0D, 0.0D, 0.0D);
         GlStateManager.disableRescaleNormal();
         GlStateManager.enableLighting();
         GlStateManager.popMatrix();
      }
   }

   public void renderRope(HookShot hookShot, double x, double y, double z, float rotationYaw, float tickProgress) {
      if (hookShot != null) {
         EntityLivingBase shooter = hookShot.getThrower();
         if (shooter != null) {
            Vect3d launcherOffset = Vect3d.ZERO;
            Rotation shooterRotation = ForgeEntity.getRotation(shooter, tickProgress);
            boolean shooterIsMe = shooter == Minecraft.getMinecraft().player;
            if (shooterIsMe) {
               switch(this.renderManager.options.thirdPersonView) {
               case 1:
               case 2:
                  launcherOffset = Vect3d.from(-0.1D, 0.0D, 0.0D);
                  break;
               default:
                  launcherOffset = Vect3d.from(-0.6D, -0.6D, 0.0D);
               }
            }

            launcherOffset = launcherOffset.rotateAroundX(-shooterRotation.pitch).rotateAroundY(-shooterRotation.yaw);
            Vect3d launcherPos = hookShot.getLauncherPosition(tickProgress).add(launcherOffset);
            Vect3d hookShotPos = hookShot.getCurPosition(tickProgress);
            Vect3d ropeVec = launcherPos.subtract(hookShotPos);
            HookType hookType = hookShot.getHookType();
            if (hookType.rope != null) {
               Rope rope = hookType.rope;
               float hookStrength = hookType.hook != null ? hookType.hook.getStrength() : 0.0F;
               float tensionForce = hookShot.getTensionForce();
               float tensionFactor = CommonMath.notZero((double)hookStrength) && CommonMath.notZero((double)rope.getElasticity()) ? tensionForce / hookStrength : 0.0F;
               float ropeWidth = (new FloatBounds(Rope.WIDTH_BOUNDS.getMin(), shooterIsMe ? rope.width : 4.0F)).approxDown(tensionFactor).floatValue();
               float sagging = hookShot.getSagging();
               Vect3d viewPos = Vect3d.from(this.renderManager.viewerPosX, this.renderManager.viewerPosY, this.renderManager.viewerPosZ);
               GlStateManager.pushMatrix();
               GlStateManager.disableTexture2D();
               GlStateManager.disableLighting();
               GlStateManager.disableCull();
               GlStateManager.translate(x, y, z);
               GL11.glEnable(2848);
               if (rope.color != null) {
                  GlStateManager.color(rope.color.getRed().toFloat(), rope.color.getGreen().toFloat(), rope.color.getBlue().toFloat());
               }

               Vect3d fromPoint = Vect3d.ZERO;
               int i = 1;

               while(true) {
                  rope.getClass();
                  if (i > 16) {
                     GL11.glDisable(2848);
                     GlStateManager.resetColor();
                     GlStateManager.enableCull();
                     GlStateManager.enableLighting();
                     GlStateManager.enableTexture2D();
                     GlStateManager.popMatrix();
                     break;
                  }

                  float var10000 = (float)i;
                  rope.getClass();
                  float ropeProgress = var10000 / 16.0F;
                  float sagHeight = MathHelper.sin(ropeProgress * 3.1415927F) * sagging;
                  Vect3d toPoint = Vect3d.from(ropeVec.x * (double)ropeProgress, ropeVec.y * (double)ropeProgress - (double)sagHeight, ropeVec.z * (double)ropeProgress);
                  float viewDistance = (float)viewPos.distance(hookShotPos.add(toPoint));
                  float scale = CommonMath.notZero((double)viewDistance) ? 10.0F / viewDistance : 1.0F;
                  float ropeWidthScaled = (new FloatBounds(Rope.WIDTH_BOUNDS.getMin(), ropeWidth)).approxUp(scale).floatValue();
                  GlStateManager.glLineWidth(ropeWidthScaled);
                  GlStateManager.glBegin(1);
                  GL11.glVertex3d(fromPoint.x, fromPoint.y, fromPoint.z);
                  GL11.glVertex3d(toPoint.x, toPoint.y, toPoint.z);
                  GlStateManager.glEnd();
                  fromPoint = toPoint;
                  ++i;
               }
            }

         }
      }
   }

   public void doRender(HookShot hookShot, double x, double y, double z, float yaw, float tickProgress) {
      if (!hookShot.isHookingEntity()) {
         if (hookShot.getHookType() == HookType.WEB) {
            this.renderWeb(hookShot, x, y, z, yaw, tickProgress);
         } else {
            this.renderHook(hookShot, x, y, z, yaw, tickProgress);
         }
      }

      this.renderRope(hookShot, x, y, z, yaw, tickProgress);
      super.doRender(hookShot, x, y, z, yaw, tickProgress);
   }

   protected ResourceLocation getEntityTexture(HookShot entity) {
      return null;
   }

   public boolean isMultipass() {
      return false;
   }

   public static class Factory implements IRenderFactory<HookShot> {
      public Factory() {
      }

      public Render<? super HookShot> createRenderFor(RenderManager manager) {
         return new HookShotRender(manager);
      }
   }
}
