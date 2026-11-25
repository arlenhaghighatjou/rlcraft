package org.orecruncher.dsurround.client.fx.particle.mote;

import java.util.function.Predicate;
import javax.annotation.Nonnull;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.orecruncher.dsurround.client.fx.particle.ParticleBase;
import org.orecruncher.dsurround.client.handlers.EnvironStateHandler;
import org.orecruncher.dsurround.lib.compat.ModEnvironment;
import org.orecruncher.lib.collections.ObjectArray;
import org.orecruncher.lib.gfx.OpenGlState;

@SideOnly(Side.CLIENT)
public class ParticleCollection extends ParticleBase {
   private static final Predicate<IParticleMote> UPDATE_REMOVE = (mote) -> {
      mote.onUpdate();
      return mote.isAlive();
   };
   protected static final int MAX_PARTICLES = 4000;
   protected static final int ALLOCATION_SIZE = 128;
   protected static final int TICK_GRACE = 2;
   protected final ObjectArray<IParticleMote> myParticles = new ObjectArray(128);
   protected final ResourceLocation texture;
   protected int lastTickUpdate;
   protected OpenGlState glState;
   public static final ParticleCollection.ICollectionFactory FACTORY = ParticleCollection::new;

   public ParticleCollection(@Nonnull World world, @Nonnull ResourceLocation tex) {
      super(world, 0.0D, 0.0D, 0.0D);
      this.canCollide = false;
      this.texture = tex;
      this.lastTickUpdate = EnvironStateHandler.EnvironState.getTickCounter();
   }

   public boolean canFit() {
      return this.myParticles.size() < 4000;
   }

   public void addParticle(@Nonnull IParticleMote mote) {
      if (this.canFit()) {
         this.myParticles.add(mote);
      }

   }

   public ObjectArray<IParticleMote> getParticles() {
      return this.myParticles;
   }

   public int size() {
      return this.myParticles.size();
   }

   public boolean shouldDie() {
      boolean timeout = EnvironStateHandler.EnvironState.getTickCounter() - this.lastTickUpdate > 2;
      return timeout || this.size() == 0 || this.world != EnvironStateHandler.EnvironState.getWorld();
   }

   public void onUpdate() {
      if (this.isAlive()) {
         this.lastTickUpdate = EnvironStateHandler.EnvironState.getTickCounter();
         this.myParticles.removeIf(UPDATE_REMOVE);
         if (this.shouldDie()) {
            this.setExpired();
         }

      }
   }

   @Nonnull
   protected VertexFormat getVertexFormat() {
      return DefaultVertexFormats.PARTICLE_POSITION_TEX_COLOR_LMAP;
   }

   public void renderParticle(@Nonnull BufferBuilder buffer, @Nonnull Entity entityIn, float partialTicks, float rotX, float rotZ, float rotYZ, float rotXY, float rotXZ) {
      if (this.myParticles.size() != 0) {
         this.bindTexture(this.texture);
         this.preRender();
         buffer.begin(7, this.getVertexFormat());

         for(int i = 0; i < this.myParticles.size(); ++i) {
            ((IParticleMote)this.myParticles.get(i)).renderParticle(buffer, entityIn, partialTicks, rotX, rotZ, rotYZ, rotXY, rotXZ);
         }

         Tessellator.getInstance().draw();
         this.postRender();
      }
   }

   protected boolean enableLighting() {
      return ModEnvironment.Albedo.isLoaded();
   }

   protected void preRender() {
      this.glState = OpenGlState.push();
      if (this.enableLighting()) {
         GlStateManager.enableLighting();
      } else {
         GlStateManager.disableLighting();
      }

   }

   protected void postRender() {
      OpenGlState.pop(this.glState);
      this.glState = null;
   }

   public int getFXLayer() {
      return 3;
   }

   public interface ICollectionFactory {
      ParticleCollection create(@Nonnull World var1, @Nonnull ResourceLocation var2);
   }
}
