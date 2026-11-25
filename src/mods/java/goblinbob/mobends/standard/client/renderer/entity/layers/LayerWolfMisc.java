package goblinbob.mobends.standard.client.renderer.entity.layers;

import goblinbob.mobends.core.client.Mesh;
import goblinbob.mobends.core.data.EntityData;
import goblinbob.mobends.core.data.EntityDatabase;
import goblinbob.mobends.core.util.Color;
import goblinbob.mobends.core.util.MeshBuilder;
import goblinbob.mobends.standard.data.WolfData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.passive.EntityWolf;
import net.minecraft.util.ResourceLocation;

public class LayerWolfMisc implements LayerRenderer<EntityWolf> {
   private static final ResourceLocation WOLF_MISC_TEXTURE = new ResourceLocation("mobends", "textures/entity/wolf_misc.png");
   private static final int textureWidth = 8;
   private static final int textureHeight = 8;
   protected final TextureManager textureManager = Minecraft.getMinecraft().getTextureManager();
   private Mesh mouthBottom = new Mesh(DefaultVertexFormats.POSITION_TEX_COLOR_NORMAL, 6);
   private Mesh mouthTop;
   private Mesh mouthInside;
   private Mesh tongue;

   public LayerWolfMisc() {
      this.mouthBottom.beginDrawing(4);
      MeshBuilder.texturedXZPlane(this.mouthBottom, -1.5D, 0.0D, -4.0D, 3.0D, 4.0D, true, Color.WHITE, new int[]{0, 0, 3, 4}, 8, 8);
      this.mouthBottom.finishDrawing();
      this.mouthTop = new Mesh(DefaultVertexFormats.POSITION_TEX_COLOR_NORMAL, 6);
      this.mouthTop.beginDrawing(4);
      MeshBuilder.texturedXZPlane(this.mouthTop, -1.5D, 1.0D, -4.0D, 3.0D, 4.0D, false, Color.WHITE, new int[]{0, 0, 3, 4}, 8, 8);
      this.mouthTop.finishDrawing();
      this.mouthInside = new Mesh(DefaultVertexFormats.POSITION_TEX_COLOR_NORMAL, 6);
      this.mouthInside.beginDrawing(4);
      MeshBuilder.texturedXZPlane(this.mouthInside, -1.5D, -4.099999904632568D, -3.0D, 3.0D, 1.0D, true, Color.WHITE, new int[]{0, 0, 3, 4}, 8, 8);
      this.mouthInside.finishDrawing();
      this.tongue = new Mesh(DefaultVertexFormats.POSITION_TEX_COLOR_NORMAL, 6);
      this.tongue.beginDrawing(4);
      MeshBuilder.texturedXZPlane(this.tongue, -1.5D, 0.0D, -4.0D, 3.0D, 6.0D, true, Color.WHITE, new int[]{3, 0, 6, 6}, 8, 8);
      this.tongue.finishDrawing();
   }

   public void doRenderLayer(EntityWolf wolf, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
      EntityData<?> entityData = EntityDatabase.instance.get(wolf);
      if (entityData instanceof WolfData) {
         WolfData data = (WolfData)entityData;
         this.textureManager.bindTexture(WOLF_MISC_TEXTURE);
         boolean isChild = wolf.isChild();
         GlStateManager.pushMatrix();
         if (isChild) {
            GlStateManager.translate(0.0F, 10.0F * scale, 0.0F * scale);
            data.body.applyLocalTransform(scale * 0.5F);
         } else {
            data.body.applyLocalTransform(scale);
         }

         data.head.applyLocalTransform(scale);
         GlStateManager.enableCull();
         GlStateManager.pushMatrix();
         GlStateManager.rotate(90.0F, 1.0F, 0.0F, 0.0F);
         GlStateManager.scale(scale, scale, scale);
         this.mouthInside.display();
         GlStateManager.popMatrix();
         GlStateManager.pushMatrix();
         data.nose.applyLocalTransform(scale);
         GlStateManager.scale(scale, scale, scale);
         this.mouthTop.display();
         GlStateManager.popMatrix();
         GlStateManager.pushMatrix();
         data.mouth.applyLocalTransform(scale);
         GlStateManager.scale(scale, scale, scale);
         this.mouthBottom.display();
         GlStateManager.popMatrix();
         GlStateManager.pushMatrix();
         data.tongue.applyLocalTransform(scale);
         GlStateManager.scale(scale, scale, scale);
         this.tongue.display();
         GlStateManager.popMatrix();
         GlStateManager.popMatrix();
      }

   }

   public boolean shouldCombineTextures() {
      return false;
   }
}
