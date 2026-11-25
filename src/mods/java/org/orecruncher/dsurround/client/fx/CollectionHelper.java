package org.orecruncher.dsurround.client.fx;

import java.lang.ref.WeakReference;
import javax.annotation.Nonnull;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.orecruncher.dsurround.client.fx.particle.mote.ParticleCollection;
import org.orecruncher.dsurround.client.handlers.EnvironStateHandler;
import org.orecruncher.lib.gfx.ParticleHelper;

@SideOnly(Side.CLIENT)
public class CollectionHelper {
   protected final String name;
   protected final ParticleCollection.ICollectionFactory factory;
   protected final ResourceLocation texture;
   protected WeakReference<ParticleCollection> collection;

   public CollectionHelper(@Nonnull String name, @Nonnull ResourceLocation texture) {
      this(name, ParticleCollection.FACTORY, texture);
   }

   public CollectionHelper(@Nonnull String name, @Nonnull ParticleCollection.ICollectionFactory factory, @Nonnull ResourceLocation texture) {
      this.name = name;
      this.texture = texture;
      this.factory = factory;
   }

   @Nonnull
   public String name() {
      return this.name;
   }

   @Nonnull
   public ParticleCollection get() {
      ParticleCollection pc = this.collection != null ? (ParticleCollection)this.collection.get() : null;
      if (pc == null || !pc.isAlive() || pc.shouldDie()) {
         pc = this.factory.create(EnvironStateHandler.EnvironState.getWorld(), this.texture);
         this.collection = new WeakReference(pc);
         ParticleHelper.addParticle(pc);
      }

      return pc;
   }

   public void clear() {
      ParticleCollection pc = this.collection != null ? (ParticleCollection)this.collection.get() : null;
      if (pc != null) {
         pc.setExpired();
         this.collection = null;
      }

   }

   public String toString() {
      StringBuilder builder = new StringBuilder();
      builder.append(this.name).append('=');
      ParticleCollection pc = this.collection != null ? (ParticleCollection)this.collection.get() : null;
      if (pc == null) {
         builder.append("No Collection");
      } else if (!pc.isAlive()) {
         builder.append("Expired");
      } else if (pc.shouldDie()) {
         builder.append("Should Die");
      } else {
         builder.append(pc.size());
      }

      return builder.toString();
   }
}
