package org.orecruncher.dsurround.client.fx;

import javax.annotation.Nonnull;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.orecruncher.dsurround.client.fx.particle.mote.ParticleCollection;

@SideOnly(Side.CLIENT)
public class LightedCollectionHelper extends CollectionHelper {
   public LightedCollectionHelper(@Nonnull String name, @Nonnull ResourceLocation texture) {
      this(name, ParticleCollection.FACTORY, texture);
   }

   public LightedCollectionHelper(@Nonnull String name, @Nonnull ParticleCollection.ICollectionFactory factory, @Nonnull ResourceLocation texture) {
      super(name, factory, texture);
      MinecraftForge.EVENT_BUS.register(this);
   }
}
