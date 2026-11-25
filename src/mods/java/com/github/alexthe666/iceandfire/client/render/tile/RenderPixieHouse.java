package com.github.alexthe666.iceandfire.client.render.tile;

import com.github.alexthe666.iceandfire.block.BlockPixieHouse;
import com.github.alexthe666.iceandfire.client.model.ModelPixie;
import com.github.alexthe666.iceandfire.client.model.ModelPixieHouse;
import com.github.alexthe666.iceandfire.client.render.entity.RenderPixie;
import com.github.alexthe666.iceandfire.entity.tile.TileEntityPixieHouse;
import net.ilexiconn.llibrary.client.util.ItemTESRContext;
import net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager;
import net.lax1dude.eaglercraft.v1_8.opengl.OpenGlHelper;
import net.minecraft.client.renderer.GlStateManager.DestFactor;
import net.minecraft.client.renderer.GlStateManager.SourceFactor;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.EnumSkyBlock;
import org.lwjgl.opengl.GL11;

public class RenderPixieHouse extends TileEntitySpecialRenderer<TileEntityPixieHouse> {
   private static final ModelPixieHouse MODEL = new ModelPixieHouse();
   private static final ModelPixie MODEL_PIXIE = new ModelPixie();
   private static final ResourceLocation TEXTURE_0 = new ResourceLocation("iceandfire:textures/models/pixie/house/pixie_house_0.png");
   private static final ResourceLocation TEXTURE_1 = new ResourceLocation("iceandfire:textures/models/pixie/house/pixie_house_1.png");
   private static final ResourceLocation TEXTURE_2 = new ResourceLocation("iceandfire:textures/models/pixie/house/pixie_house_2.png");
   private static final ResourceLocation TEXTURE_3 = new ResourceLocation("iceandfire:textures/models/pixie/house/pixie_house_3.png");
   private static final ResourceLocation TEXTURE_4 = new ResourceLocation("iceandfire:textures/models/pixie/house/pixie_house_4.png");
   private static final ResourceLocation TEXTURE_5 = new ResourceLocation("iceandfire:textures/models/pixie/house/pixie_house_5.png");

   public RenderPixieHouse() {
   }

   public void render(TileEntityPixieHouse entity, double x, double y, double z, float f, int f1, float alpha) {
      int rotation = 0;
      int meta = 0;
      if (entity != null && entity.getWorld() != null && entity.getWorld().getBlockState(entity.getPos()).getBlock() instanceof BlockPixieHouse) {
         meta = entity.houseType;
         if (entity.getWorld().getBlockState(entity.getPos()).getValue(BlockPixieHouse.FACING) == EnumFacing.NORTH) {
            rotation = 180;
         }

         if (entity.getWorld().getBlockState(entity.getPos()).getValue(BlockPixieHouse.FACING) == EnumFacing.EAST) {
            rotation = -90;
         }

         if (entity.getWorld().getBlockState(entity.getPos()).getValue(BlockPixieHouse.FACING) == EnumFacing.WEST) {
            rotation = 90;
         }
      } else if (ItemTESRContext.INSTANCE.getCurrentStack() != ItemStack.EMPTY) {
         meta = ItemTESRContext.INSTANCE.getCurrentStack().getItemDamage();
      }

      GL11.glPushMatrix();
      GL11.glTranslatef((float)x + 0.5F, (float)y + 1.501F, (float)z + 0.5F);
      GL11.glPushMatrix();
      GL11.glRotatef(180.0F, 1.0F, 0.0F, 0.0F);
      GL11.glRotatef((float)rotation, 0.0F, 1.0F, 0.0F);
      if (entity != null && entity.getWorld() != null && entity.hasPixie) {
         GL11.glPushMatrix();
         GL11.glTranslatef(0.0F, 0.95F, 0.0F);
         GL11.glScalef(0.55F, 0.55F, 0.55F);
         GL11.glPushMatrix();
         switch(entity.pixieType) {
         case 1:
            this.bindTexture(RenderPixie.TEXTURE_1);
            break;
         case 2:
            this.bindTexture(RenderPixie.TEXTURE_2);
            break;
         case 3:
            this.bindTexture(RenderPixie.TEXTURE_3);
            break;
         case 4:
            this.bindTexture(RenderPixie.TEXTURE_4);
            break;
         case 5:
            this.bindTexture(RenderPixie.TEXTURE_5);
            break;
         default:
            this.bindTexture(RenderPixie.TEXTURE_0);
         }

         GL11.glPushMatrix();
         GlStateManager.enableBlend();
         GlStateManager.blendFunc(SourceFactor.ONE, DestFactor.CONSTANT_ALPHA);
         GlStateManager.disableLighting();
         GlStateManager.depthMask(true);
         OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 61680.0F, 0.0F);
         GlStateManager.enableLighting();
         GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
         GlStateManager.enableColorMaterial();
         MODEL_PIXIE.animateInHouse(entity);
         GlStateManager.disableColorMaterial();
         int i = entity.getWorld().getCombinedLight(entity.getPos(), entity.getWorld().getLightFor(EnumSkyBlock.BLOCK, entity.getPos()));
         int j = i % 65536;
         int k = i / 65536;
         OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, (float)j, (float)k);
         GlStateManager.depthMask(true);
         GlStateManager.disableBlend();
         GlStateManager.setActiveTexture(OpenGlHelper.lightmapTexUnit);
         GlStateManager.enableTexture2D();
         GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
         GL11.glPopMatrix();
         GL11.glPopMatrix();
         GL11.glPopMatrix();
      }

      switch(meta) {
      case 0:
         this.bindTexture(TEXTURE_0);
         break;
      case 1:
         this.bindTexture(TEXTURE_1);
         break;
      case 2:
         this.bindTexture(TEXTURE_2);
         break;
      case 3:
         this.bindTexture(TEXTURE_3);
         break;
      case 4:
         this.bindTexture(TEXTURE_4);
         break;
      case 5:
         this.bindTexture(TEXTURE_5);
      }

      GL11.glPushMatrix();
      GL11.glDisable(2884);
      MODEL.render(0.0625F);
      GL11.glEnable(2884);
      GL11.glPopMatrix();
      GL11.glPopMatrix();
      GL11.glPopMatrix();
   }
}
