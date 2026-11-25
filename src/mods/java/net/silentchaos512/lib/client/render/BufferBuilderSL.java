package net.silentchaos512.lib.client.render;

import java.nio.ByteBuffer;
import net.lax1dude.eaglercraft.v1_8.opengl.WorldRenderer;
import net.minecraft.client.renderer.Tessellator;
import net.lax1dude.eaglercraft.v1_8.opengl.VertexFormat;

/** @deprecated */
@Deprecated
public class BufferBuilderSL {
   public static final BufferBuilderSL INSTANCE = new BufferBuilderSL();
   private WorldRenderer buffer;

   public BufferBuilderSL() {
   }

   public BufferBuilderSL acquireBuffer(Tessellator tess) {
      this.buffer = tess.getBuffer();
      return this;
   }

   public BufferBuilderSL internalAcquireMC12(WorldRenderer bufferBuilder) {
      this.buffer = bufferBuilder;
      return this;
   }

   public WorldRenderer internalGetBufferMC12() {
      return this.buffer;
   }

   public void sortVertexData(float p_181674_1_, float p_181674_2_, float p_181674_3_) {
      this.buffer.sortVertexData(p_181674_1_, p_181674_2_, p_181674_3_);
   }

   public void reset() {
      this.buffer.reset();
   }

   public void begin(int glMode, VertexFormat format) {
      this.buffer.begin(glMode, format);
   }

   public BufferBuilderSL tex(double u, double v) {
      this.buffer.tex(u, v);
      return this;
   }

   public BufferBuilderSL lightmap(int p_187314_1_, int p_187314_2_) {
      this.buffer.lightmap(p_187314_1_, p_187314_2_);
      return this;
   }

   public void putBrightness4(int p_178962_1_, int p_178962_2_, int p_178962_3_, int p_178962_4_) {
      this.buffer.putBrightness4(p_178962_1_, p_178962_2_, p_178962_3_, p_178962_4_);
   }

   public void putPosition(double x, double y, double z) {
      this.buffer.putPosition(x, y, z);
   }

   public int getColorIndex(int vertexIndex) {
      return this.buffer.getColorIndex(vertexIndex);
   }

   public void putColorMultiplier(float red, float green, float blue, int vertexIndex) {
      this.buffer.putColorMultiplier(red, green, blue, vertexIndex);
   }

   public void putColorRGB_F(float red, float green, float blue, int vertexIndex) {
      this.buffer.putColorRGB_F(red, green, blue, vertexIndex);
   }

   public void putColorRGBA(int index, int red, int green, int blue) {
      this.buffer.putColorRGBA(index, red, green, blue);
   }

   public void noColor() {
      this.buffer.noColor();
   }

   public BufferBuilderSL color(float red, float green, float blue, float alpha) {
      this.buffer.color(red, green, blue, alpha);
      return this;
   }

   public BufferBuilderSL color(int red, int green, int blue, int alpha) {
      this.buffer.color(red, green, blue, alpha);
      return this;
   }

   public void addVertexData(int[] vertexData) {
      this.buffer.addVertexData(vertexData);
   }

   public void endVertex() {
      this.buffer.endVertex();
   }

   public BufferBuilderSL pos(double x, double y, double z) {
      this.buffer.pos(x, y, z);
      return this;
   }

   public void putNormal(float x, float y, float z) {
      this.buffer.putNormal(x, y, z);
   }

   public BufferBuilderSL normal(float x, float y, float z) {
      this.buffer.normal(x, y, z);
      return this;
   }

   public void setTranslation(double x, double y, double z) {
      this.buffer.setTranslation(x, y, z);
   }

   public void finishDrawing() {
      this.buffer.finishDrawing();
   }

   public ByteBuffer getByteBuffer() {
      return this.buffer.getByteBuffer();
   }

   public VertexFormat getVertexFormat() {
      return this.buffer.getVertexFormat();
   }

   public int getVertexCount() {
      return this.buffer.getVertexCount();
   }

   public int getDrawMode() {
      return this.buffer.getDrawMode();
   }

   public void putColor4(int argb) {
      this.buffer.putColor4(argb);
   }

   public void putColorRGB_F4(float red, float green, float blue) {
      this.buffer.putColorRGB_F4(red, green, blue);
   }

   public void putColorRGBA(int index, int red, int green, int blue, int alpha) {
      this.buffer.putColorRGBA(index, red, green, blue, alpha);
   }

   public boolean isColorDisabled() {
      return this.buffer.isColorDisabled();
   }
}
