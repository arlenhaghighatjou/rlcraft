package org.orecruncher.dsurround.client.sound;

import javax.annotation.Nonnull;
import net.minecraft.entity.EntityLivingBase;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class EntityEmitter extends Emitter {
   protected final EntityLivingBase entity;

   public EntityEmitter(@Nonnull EntityLivingBase entity, @Nonnull SoundEffect sound) {
      super(sound);
      this.entity = entity;
   }

   protected SoundInstance createSound() {
      return this.effect.createTrackingSound(this.entity, true);
   }
}
