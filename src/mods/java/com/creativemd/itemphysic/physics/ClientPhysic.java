package com.creativemd.itemphysic.physics;

import com.creativemd.itemphysic.ItemDummyContainer;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import net.minecraft.client.Minecraft;
import net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.GlStateManager.DestFactor;
import net.minecraft.client.renderer.GlStateManager.SourceFactor;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderEntityItem;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

public class ClientPhysic {
   public static Minecraft mc = Minecraft.getMinecraft();
   public static long tick;
   public static double rotation;
   public static EaglercraftRandom random = new EaglercraftRandom();
   private static Field renderOutlines = ReflectionHelper.findField(Render.class, new String[]{"renderOutlines", "renderOutlines"});
   private static Method getTeamColor = ReflectionHelper.findMethod(Render.class, "getTeamColor", "getTeamColor", new Class[]{Entity.class});
   private static Field skipPhysicRenderer = ReflectionHelper.findField(EntityItem.class, new String[]{"skipPhysicRenderer"});
   private static Field isInWeb = ReflectionHelper.findField(Entity.class, new String[]{"isInWeb", "isInWeb"});

   public ClientPhysic() {
   }

   public static ResourceLocation getEntityTexture() {
      return TextureMap.LOCATION_BLOCKS_TEXTURE;
   }

   @SideOnly(Side.CLIENT)
   public static void setPositionAndRotationDirect(EntityItem item, double x, double y, double z, float yaw, float pitch, int posRotationIncrements, boolean teleport) {
      item.setPosition(x, y, z);
   }

   @SideOnly(Side.CLIENT)
   public static boolean doRender(RenderEntityItem renderer, Entity entity, double x, double y, double z, float entityYaw, float partialTicks) {
      EntityItem item = (EntityItem)entity;
      ItemStack itemstack = item.getItem();

      try {
         if (item.getAge() == 0 || ItemDummyContainer.CONFIG_RENDERING.vanillaRendering || skipPhysicRenderer.getBoolean(item)) {
            return false;
         }
      } catch (IllegalAccessException | IllegalArgumentException var31) {
         var31.printStackTrace();
      }

      rotation = (double)(System.nanoTime() - tick) / 2500000.0D * (double)ItemDummyContainer.CONFIG_RENDERING.rotateSpeed;
      if (!mc.inGameHasFocus) {
         rotation = 0.0D;
      }

      GlStateManager.enableRescaleNormal();
      IBakedModel ibakedmodel = mc.getRenderItem().getItemModelWithOverrides(itemstack, entity.world, (EntityLivingBase)null);
      boolean is3D = ibakedmodel.isGui3d();
      boolean applyEffects = item.getAge() != 0 && (is3D || mc.getRenderManager().options != null);
      int i = itemstack.isEmpty() ? 187 : Item.getIdFromItem(itemstack.getItem()) + itemstack.getMetadata();
      random.setSeed((long)i);
      boolean flag = false;
      ResourceLocation location = getEntityTexture();
      if (location != null) {
         renderer.bindTexture(location);
         renderer.getRenderManager().renderEngine.getTexture(location).setBlurMipmap(false, false);
         flag = true;
      }

      GlStateManager.alphaFunc(516, 0.1F);
      GlStateManager.enableBlend();
      RenderHelper.enableStandardItemLighting();
      GlStateManager.tryBlendFuncSeparate(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA, SourceFactor.ONE, DestFactor.ZERO);
      GlStateManager.pushMatrix();
      int j = getModelCount(itemstack);
      float f2 = ibakedmodel.getItemCameraTransforms().getTransform(TransformType.GROUND).scale.y;
      GlStateManager.translate((float)x, (float)y, (float)z);
      GL11.glRotatef(90.0F, 1.0F, 0.0F, 0.0F);
      GL11.glRotatef(item.rotationYaw, 0.0F, 0.0F, 1.0F);
      if (is3D) {
         GlStateManager.translate(0.0D, -0.2D, -0.08D);
      } else if (entity.world.getBlockState(entity.getPosition()).getBlock() != Blocks.SNOW_LAYER && entity.world.getBlockState(entity.getPosition().down()).getBlock() != Blocks.SOUL_SAND) {
         GlStateManager.translate(0.0D, 0.0D, -0.04D);
      } else {
         GlStateManager.translate(0.0D, 0.0D, -0.14D);
      }

      if (applyEffects) {
         if (!is3D) {
            if (item != null && !Double.isNaN(item.posX) && !Double.isNaN(item.posY) && !Double.isNaN(item.posZ) && item.world != null) {
               if (item.onGround) {
                  item.rotationPitch = 0.0F;
               } else {
                  double rotation = rotation * 2.0D;
                  Fluid fluid = CommonPhysic.getFluid(item);
                  if (fluid != null) {
                     rotation /= (double)(fluid.getDensity() / 1000 * 10);
                  }

                  item.rotationPitch = (float)((double)item.rotationPitch + rotation);
               }
            }
         } else if (!item.onGround) {
            double rotation = rotation * 2.0D;
            Fluid fluid = CommonPhysic.getFluid(item);
            if (fluid == null) {
               fluid = CommonPhysic.getFluid(item, true);
            }

            if (fluid != null) {
               rotation /= (double)(fluid.getDensity() / 1000 * 10);
            }

            try {
               if (isInWeb.getBoolean(item)) {
                  rotation /= 50.0D;
               }
            } catch (IllegalAccessException | IllegalArgumentException var30) {
               ;
            }

            item.rotationPitch = (float)((double)item.rotationPitch + rotation);
         } else if (ItemDummyContainer.CONFIG_RENDERING.oldRotation) {
            for(int side = 0; side < 4; ++side) {
               double rotation = (double)(side * 90);
               double range = 5.0D;
               if ((double)item.rotationPitch > rotation - range && (double)item.rotationPitch < rotation + range) {
                  item.rotationPitch = (float)rotation;
               }
            }

            if (item.rotationPitch != 0.0F && item.rotationPitch != 90.0F && item.rotationPitch != 180.0F && item.rotationPitch != 270.0F) {
               double Abstand0 = (double)Math.abs(item.rotationPitch);
               double Abstand90 = (double)Math.abs(item.rotationPitch - 90.0F);
               double Abstand180 = (double)Math.abs(item.rotationPitch - 180.0F);
               double Abstand270 = (double)Math.abs(item.rotationPitch - 270.0F);
               if (Abstand0 <= Abstand90 && Abstand0 <= Abstand180 && Abstand0 <= Abstand270) {
                  if (item.rotationPitch < 0.0F) {
                     item.rotationPitch = (float)((double)item.rotationPitch + rotation);
                  } else {
                     item.rotationPitch = (float)((double)item.rotationPitch - rotation);
                  }
               }

               if (Abstand90 < Abstand0 && Abstand90 <= Abstand180 && Abstand90 <= Abstand270) {
                  if (item.rotationPitch - 90.0F < 0.0F) {
                     item.rotationPitch = (float)((double)item.rotationPitch + rotation);
                  } else {
                     item.rotationPitch = (float)((double)item.rotationPitch - rotation);
                  }
               }

               if (Abstand180 < Abstand90 && Abstand180 < Abstand0 && Abstand180 <= Abstand270) {
                  if (item.rotationPitch - 180.0F < 0.0F) {
                     item.rotationPitch = (float)((double)item.rotationPitch + rotation);
                  } else {
                     item.rotationPitch = (float)((double)item.rotationPitch - rotation);
                  }
               }

               if (Abstand270 < Abstand90 && Abstand270 < Abstand180 && Abstand270 < Abstand0) {
                  if (item.rotationPitch - 270.0F < 0.0F) {
                     item.rotationPitch = (float)((double)item.rotationPitch + rotation);
                  } else {
                     item.rotationPitch = (float)((double)item.rotationPitch - rotation);
                  }
               }
            }
         }

         double height = 0.2D;
         if (is3D) {
            GlStateManager.translate(0.0D, height, 0.0D);
         }

         GlStateManager.rotate(item.rotationPitch, 1.0F, 0.0F, 0.0F);
         if (is3D) {
            GlStateManager.translate(0.0D, -height, 0.0D);
         }
      }

      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
      boolean renderOutlines = false;

      try {
         renderOutlines = renderOutlines.getBoolean(renderer);
      } catch (IllegalAccessException | IllegalArgumentException var29) {
         var29.printStackTrace();
      }

      if (!is3D) {
         float f3 = -0.0F * (float)(j - 1) * 0.5F;
         float f4 = -0.0F * (float)(j - 1) * 0.5F;
         float f5 = -0.09375F * (float)(j - 1) * 0.5F;
         GlStateManager.translate(f3, f4, f5);
      }

      if (renderOutlines) {
         GlStateManager.enableColorMaterial();

         try {
            GlStateManager.enableOutlineMode(((Integer)getTeamColor.invoke(entity)).intValue());
         } catch (Exception var28) {
            var28.printStackTrace();
         }
      }

      for(int k = 0; k < j; ++k) {
         if (is3D) {
            GlStateManager.pushMatrix();
            if (k > 0) {
               float f7 = (random.nextFloat() * 2.0F - 1.0F) * 0.15F;
               float f9 = (random.nextFloat() * 2.0F - 1.0F) * 0.15F;
               float f6 = (random.nextFloat() * 2.0F - 1.0F) * 0.15F;
               GlStateManager.translate(renderer.shouldSpreadItems() ? f7 : 0.0F, renderer.shouldSpreadItems() ? f9 : 0.0F, f6);
            }

            IBakedModel transformedModel = ForgeHooksClient.handleCameraTransforms(ibakedmodel, TransformType.GROUND, false);
            mc.getRenderItem().renderItem(itemstack, transformedModel);
            GlStateManager.popMatrix();
         } else {
            GlStateManager.pushMatrix();
            IBakedModel transformedModel = ForgeHooksClient.handleCameraTransforms(ibakedmodel, TransformType.GROUND, false);
            mc.getRenderItem().renderItem(itemstack, transformedModel);
            GlStateManager.popMatrix();
            GlStateManager.translate(0.0F, 0.0F, 0.05375F);
         }
      }

      if (renderOutlines) {
         GlStateManager.disableOutlineMode();
         GlStateManager.disableColorMaterial();
      }

      GlStateManager.popMatrix();
      GlStateManager.disableRescaleNormal();
      GlStateManager.disableBlend();
      renderer.bindTexture(getEntityTexture());
      if (flag) {
         renderer.getRenderManager().renderEngine.getTexture(location).restoreLastBlurMipmap();
      }

      return true;
   }

   public static int getModelCount(ItemStack stack) {
      int i = 1;
      if (stack.getCount() > 48) {
         i = 5;
      } else if (stack.getCount() > 32) {
         i = 4;
      } else if (stack.getCount() > 16) {
         i = 3;
      } else if (stack.getCount() > 1) {
         i = 2;
      }

      return i;
   }
}
