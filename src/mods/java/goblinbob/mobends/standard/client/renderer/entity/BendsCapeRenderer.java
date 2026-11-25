package goblinbob.mobends.standard.client.renderer.entity;

import goblinbob.mobends.standard.data.PlayerData;
import net.minecraft.client.model.PositionTextureVertex;
import net.minecraft.client.model.TexturedQuad;
import net.lax1dude.eaglercraft.v1_8.opengl.WorldRenderer;
import net.minecraft.client.renderer.GLAllocation;
import net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BendsCapeRenderer {
   public static final int MODEL_WIDTH = 10;
   public static final int MODEL_LENGTH = 16;
   public static final int MODEL_DEPTH = 1;
   public static final int SLAB_AMOUNT = 16;
   public static final ResourceLocation CAPE_TEXTURE = new ResourceLocation("mobends", "textures/cape.png");
   public BendsCapeRenderer.Slab[] slabs = new BendsCapeRenderer.Slab[16];

   public BendsCapeRenderer() {
      for(int i = 0; i < 16; ++i) {
         this.slabs[i] = new BendsCapeRenderer.Slab(i * 16 / 16);
         if (i > 0) {
            this.slabs[i - 1].setChildSlab(this.slabs[i]);
         }
      }

      this.slabs[0].rotationPointY = 0;
   }

   public void applyAnimation(PlayerData playerData) {
      double phase = (double)playerData.getCapeWavePhase();

      for(int i = 0; i < 16; ++i) {
         float waveSpeed = 0.2F;
         float waveFrequency = 7.2F;
         float waveOffset = (float)i / 16.0F;
         float magnitude = 5.0F * (0.7F + (float)(i / 16));
         this.slabs[i].setRotateAngle((float)(Math.cos(phase * (double)waveSpeed + (double)(waveOffset * waveFrequency)) * (double)magnitude));
      }

      this.slabs[0].rotate(-10.0F);
   }

   public void render(float scale) {
      this.slabs[0].render(scale);
   }

   static class Slab {
      float rotateAngle;
      public float textureWidth = 64.0F;
      public float textureHeight = 32.0F;
      private int textureOffsetX;
      private int textureOffsetY;
      private boolean compiled;
      private int displayList;
      private BendsCapeRenderer.Slab childSlab;
      public boolean showModel;
      public boolean isHidden;
      public int offsetX;
      public int offsetY;
      public int offsetZ;
      public int rotationPointX;
      public int rotationPointY;
      public int rotationPointZ;
      public int hingeOffset = 0;
      private final PositionTextureVertex[] vertexPositions;
      private final TexturedQuad[] quadList;
      public final float posX1;
      public final float posY1;
      public final float posZ1;
      public final float posX2;
      public final float posY2;
      public final float posZ2;

      public Slab(int texV) {
         this.textureWidth = 64.0F;
         this.textureHeight = 32.0F;
         this.showModel = true;
         this.rotationPointX = 0;
         this.rotationPointY = 1;
         this.rotationPointZ = 0;
         this.offsetX = -5;
         this.offsetY = 0;
         this.offsetZ = 0;
         int slabLength = 1;
         this.posX1 = (float)this.offsetX;
         this.posY1 = (float)this.offsetY;
         this.posZ1 = (float)this.offsetZ;
         this.posX2 = (float)(this.offsetX + 10);
         this.posY2 = (float)(this.offsetY + slabLength);
         this.posZ2 = (float)(this.offsetZ + 1);
         int texU = 0;
         this.vertexPositions = new PositionTextureVertex[8];
         this.quadList = new TexturedQuad[6];
         PositionTextureVertex positiontexturevertex7 = new PositionTextureVertex(this.posX1, this.posY1, this.posZ1, 0.0F, 0.0F);
         PositionTextureVertex positiontexturevertex = new PositionTextureVertex(this.posX2, this.posY1, this.posZ1, 0.0F, 8.0F);
         PositionTextureVertex positiontexturevertex1 = new PositionTextureVertex(this.posX2, this.posY2, this.posZ1, 8.0F, 8.0F);
         PositionTextureVertex positiontexturevertex2 = new PositionTextureVertex(this.posX1, this.posY2, this.posZ1, 8.0F, 0.0F);
         PositionTextureVertex positiontexturevertex3 = new PositionTextureVertex(this.posX1, this.posY1, this.posZ2, 0.0F, 0.0F);
         PositionTextureVertex positiontexturevertex4 = new PositionTextureVertex(this.posX2, this.posY1, this.posZ2, 0.0F, 8.0F);
         PositionTextureVertex positiontexturevertex5 = new PositionTextureVertex(this.posX2, this.posY2, this.posZ2, 8.0F, 8.0F);
         PositionTextureVertex positiontexturevertex6 = new PositionTextureVertex(this.posX1, this.posY2, this.posZ2, 8.0F, 0.0F);
         this.vertexPositions[0] = positiontexturevertex7;
         this.vertexPositions[1] = positiontexturevertex;
         this.vertexPositions[2] = positiontexturevertex1;
         this.vertexPositions[3] = positiontexturevertex2;
         this.vertexPositions[4] = positiontexturevertex3;
         this.vertexPositions[5] = positiontexturevertex4;
         this.vertexPositions[6] = positiontexturevertex5;
         this.vertexPositions[7] = positiontexturevertex6;
         this.quadList[0] = new TexturedQuad(new PositionTextureVertex[]{positiontexturevertex4, positiontexturevertex, positiontexturevertex1, positiontexturevertex5}, texU + 1 + 10, texV + 1, texU + 1 + 10 + 1, texV + 1 + slabLength, this.textureWidth, this.textureHeight);
         this.quadList[1] = new TexturedQuad(new PositionTextureVertex[]{positiontexturevertex7, positiontexturevertex3, positiontexturevertex6, positiontexturevertex2}, texU, texV + 1, texU + 1, texV + 1 + slabLength, this.textureWidth, this.textureHeight);
         this.quadList[2] = new TexturedQuad(new PositionTextureVertex[]{positiontexturevertex4, positiontexturevertex3, positiontexturevertex7, positiontexturevertex}, texU + 1, texV, texU + 1 + 10, texV + 1, this.textureWidth, this.textureHeight);
         this.quadList[3] = new TexturedQuad(new PositionTextureVertex[]{positiontexturevertex1, positiontexturevertex2, positiontexturevertex6, positiontexturevertex5}, texU + 1 + 10, texV + 1, texU + 1 + 10 + 10, texV, this.textureWidth, this.textureHeight);
         this.quadList[4] = new TexturedQuad(new PositionTextureVertex[]{positiontexturevertex, positiontexturevertex7, positiontexturevertex2, positiontexturevertex1}, texU + 1, texV + 1, texU + 1 + 10, texV + 1 + slabLength, this.textureWidth, this.textureHeight);
         this.quadList[5] = new TexturedQuad(new PositionTextureVertex[]{positiontexturevertex3, positiontexturevertex4, positiontexturevertex5, positiontexturevertex6}, texU + 1 + 10 + 1, texV + 1, texU + 1 + 10 + 1 + 10, texV + 1 + slabLength, this.textureWidth, this.textureHeight);
      }

      public void rotate(float f) {
         this.setRotateAngle(this.rotateAngle + f);
      }

      public BendsCapeRenderer.Slab setChildSlab(BendsCapeRenderer.Slab slab) {
         this.childSlab = slab;
         return this;
      }

      public void setRotateAngle(float rotateAngle) {
         this.rotateAngle = rotateAngle;
         this.hingeOffset = this.rotateAngle < 0.0F ? 1 : 0;
      }

      @SideOnly(Side.CLIENT)
      public void render(float scale) {
         if (!this.isHidden && this.showModel) {
            if (!this.compiled) {
               this.compileDisplayList(scale);
            }

            GlStateManager.pushMatrix();
            GlStateManager.translate((float)this.rotationPointX * scale, (float)this.rotationPointY * scale, (float)(this.rotationPointZ + this.hingeOffset) * scale);
            GlStateManager.rotate(this.rotateAngle, 1.0F, 0.0F, 0.0F);
            GlStateManager.translate(0.0F, 0.0F, (float)(-this.hingeOffset) * scale);
            GlStateManager.callList(this.displayList);
            if (this.childSlab != null) {
               this.childSlab.render(scale);
            }

            GlStateManager.popMatrix();
         }

      }

      private void compileDisplayList(float scale) {
         this.displayList = GLAllocation.generateDisplayLists(1);
         GlStateManager.glNewList(this.displayList, 4864);
         WorldRenderer bufferbuilder = Tessellator.getInstance().getBuffer();

         for(TexturedQuad texturedquad : this.quadList) {
            texturedquad.draw(bufferbuilder, scale);
         }

         GlStateManager.glEndList();
         this.compiled = true;
      }
   }
}
