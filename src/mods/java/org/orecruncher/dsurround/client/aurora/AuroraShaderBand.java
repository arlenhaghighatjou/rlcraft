package org.orecruncher.dsurround.client.aurora;

import net.lax1dude.eaglercraft.v1_8.opengl.WorldRenderer;
import net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;
import org.orecruncher.dsurround.client.shader.Shaders;
import org.orecruncher.lib.gfx.OpenGlState;
import org.orecruncher.lib.gfx.OpenGlUtil;
import org.orecruncher.lib.gfx.shaders.ShaderProgram;
import org.orecruncher.lib.gfx.shaders.ShaderProgram.IShaderUseCallback;
import org.orecruncher.lib.math.MathStuff;

@SideOnly(Side.CLIENT)
public class AuroraShaderBand extends AuroraBase {
   private static final double zero = 0.0D;
   private static final float v1 = 0.0F;
   private static final float v2 = 1.0F;
   protected static final VertexUploader uploader = new VertexUploader();
   protected ShaderProgram program = Shaders.AURORA;
   protected IShaderUseCallback callback = (shader) -> {
      shader.set("time", new float[]{AuroraUtils.getTimeSeconds() * 0.75F});
      shader.set("resolution", new float[]{this.getAuroraWidth(), this.getAuroraHeight()});
      shader.set("topColor", this.getFadeColor());
      shader.set("middleColor", this.getMiddleColor());
      shader.set("bottomColor", this.getBaseColor());
      shader.set("alpha", new float[]{this.getAlpha()});
   };
   protected final float auroraWidth;
   protected final float panelTexWidth;
   protected final WorldRenderer buffer;

   public AuroraShaderBand(long seed) {
      super(seed, true);
      this.auroraWidth = (float)this.band.getNodeList().length * this.band.getNodeWidth();
      this.panelTexWidth = this.band.getNodeWidth() / this.auroraWidth;
      this.buffer = this.createList();
   }

   protected float getAlpha() {
      return MathStuff.clamp((float)this.band.getAlphaLimit() / 255.0F * this.tracker.ageRatio() * 2.0F, 0.0F, 1.0F);
   }

   protected float getAuroraWidth() {
      return this.auroraWidth;
   }

   protected float getAuroraHeight() {
      return 18.0F;
   }

   protected WorldRenderer createList() {
      WorldRenderer renderer = new WorldRenderer(4096);
      Panel[] array = this.band.getNodeList();
      renderer.begin(5, DefaultVertexFormats.POSITION_TEX);
      double posY = (double)array[0].getModdedY();
      double posX = (double)array[0].tetX;
      double posZ = (double)array[0].tetZ;
      renderer.pos(posX, 0.0D, posZ).tex(0.0D, 0.0D).endVertex();
      renderer.pos(posX, posY, posZ).tex(0.0D, 1.0D).endVertex();

      for(int i = 0; i < array.length - 1; ++i) {
         float u1 = (float)i * this.panelTexWidth;
         float u2 = u1 + this.panelTexWidth;
         double posX2;
         double posZ2;
         double posY2;
         if (i < array.length - 2) {
            Panel nodePlus = array[i + 1];
            posX2 = (double)nodePlus.tetX;
            posZ2 = (double)nodePlus.tetZ;
            posY2 = (double)nodePlus.getModdedY();
         } else {
            Panel node = array[i];
            posX2 = (double)node.posX;
            posZ2 = (double)node.getModdedZ();
            posY2 = 0.0D;
         }

         renderer.pos(posX2, 0.0D, posZ2).tex((double)u2, 0.0D).endVertex();
         renderer.pos(posX2, posY2, posZ2).tex((double)u2, 1.0D).endVertex();
      }

      renderer.finishDrawing();
      return renderer;
   }

   public void render(float partialTick) {
      if (this.program != null) {
         this.band.translate(partialTick);
         double tranY = this.getTranslationY(partialTick);
         double tranX = this.getTranslationX(partialTick);
         double tranZ = this.getTranslationZ(partialTick);
         OpenGlState glState = OpenGlState.push();
         GlStateManager.disableLighting();
         OpenGlUtil.setAuroraBlend();
         GL11.glFrontFace(2304);

         try {
            this.program.use(this.callback);

            for(int b = 0; b < this.bandCount; ++b) {
               GlStateManager.pushMatrix();
               GlStateManager.translate(tranX, tranY, tranZ + (double)(this.offset * (float)b));
               GlStateManager.scale(0.5D, 10.0D, 0.5D);
               uploader.draw(this.buffer);
               GlStateManager.popMatrix();
            }
         } catch (Exception var18) {
            var18.printStackTrace();
            this.program = null;
         } finally {
            try {
               if (this.program != null) {
                  this.program.unUse();
               }
            } catch (Throwable var17) {
               ;
            }

         }

         GL11.glFrontFace(2305);
         OpenGlState.pop(glState);
      }
   }

   public String toString() {
      return "<SHADER> " + super.toString();
   }
}
