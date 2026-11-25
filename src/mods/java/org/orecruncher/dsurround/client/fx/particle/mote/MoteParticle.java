package org.orecruncher.dsurround.client.fx.particle.mote;

import java.util.Random;
import javax.annotation.Nonnull;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos.MutableBlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.orecruncher.lib.chunk.ClientChunkCache;
import org.orecruncher.lib.random.XorShiftRandom;

@SideOnly(Side.CLIENT)
public abstract class MoteParticle implements IParticleMote {
   protected static final Random RANDOM = XorShiftRandom.current();
   protected static final RenderManager RENDERER = Minecraft.getMinecraft().getRenderManager();
   protected final World world;
   protected boolean isAlive = true;
   protected double posX;
   protected double posY;
   protected double posZ;
   protected final MutableBlockPos position = new MutableBlockPos();
   protected int slX16;
   protected int blX16;
   protected int red;
   protected int green;
   protected int blue;
   protected int alpha;

   public MoteParticle(@Nonnull World world, double x, double y, double z) {
      this.world = world;
      this.setPosition(x, y, z);
      this.configureColor();
   }

   public void setPosition(double posX, double posY, double posZ) {
      this.posX = posX;
      this.posY = posY;
      this.posZ = posZ;
      this.position.setPos(posX, posY, posZ);
   }

   public void configureColor() {
      this.red = this.green = this.blue = this.alpha = 255;
   }

   public boolean isAlive() {
      return !this.isAlive;
   }

   public void kill() {
      this.isAlive = false;
   }

   public void onUpdate() {
      if (!this.isAlive()) {
         this.update();
         if (!this.isAlive()) {
            this.setPosition(this.posX, this.posY, this.posZ);
            this.updateBrightness();
         }

      }
   }

   protected void update() {
   }

   public void updateBrightness() {
      int combinedLight = this.getBrightnessForRender(0.0F);
      this.slX16 = combinedLight >> 16 & '\uffff';
      this.blX16 = combinedLight & '\uffff';
   }

   protected final double interpX() {
      return RENDERER.viewerPosX;
   }

   protected final double interpY() {
      return RENDERER.viewerPosY;
   }

   protected final double interpZ() {
      return RENDERER.viewerPosZ;
   }

   protected float renderX(float partialTicks) {
      return (float)(this.posX - this.interpX());
   }

   protected float renderY(float partialTicks) {
      return (float)(this.posY - this.interpY());
   }

   protected float renderZ(float partialTicks) {
      return (float)(this.posZ - this.interpZ());
   }

   protected void applyColor(@Nonnull BufferBuilder buffer) {
      buffer.color(this.red, this.green, this.blue, this.alpha);
   }

   protected void applyLightmap(@Nonnull BufferBuilder buffer) {
      buffer.lightmap(this.slX16, this.blX16);
   }

   protected void drawVertex(BufferBuilder buffer, double x, double y, double z, double u, double v) {
      buffer.pos(x, y, z).tex(u, v);
      this.applyColor(buffer);
      this.applyLightmap(buffer);
      buffer.endVertex();
   }

   public abstract void renderParticle(BufferBuilder var1, Entity var2, float var3, float var4, float var5, float var6, float var7, float var8);

   public int getBrightnessForRender(float partialTicks) {
      return ClientChunkCache.instance().getCombinedLight(this.position, 0);
   }
}
