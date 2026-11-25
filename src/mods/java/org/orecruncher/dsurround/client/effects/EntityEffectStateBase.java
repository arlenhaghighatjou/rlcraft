package org.orecruncher.dsurround.client.effects;

import java.lang.ref.WeakReference;
import java.util.Optional;
import javax.annotation.Nonnull;
import net.minecraft.entity.Entity;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class EntityEffectStateBase extends EffectStateBase implements IEntityEffectState {
   protected final WeakReference<Entity> subject;

   protected EntityEffectStateBase(@Nonnull Entity entity) {
      this.subject = new WeakReference(entity);
   }

   public EntityEffectStateBase(@Nonnull Entity entity, @Nonnull IParticleHelper ph, @Nonnull ISoundHelper sh) {
      super(ph, sh);
      this.subject = new WeakReference(entity);
   }

   @Nonnull
   public Optional<Entity> subject() {
      return Optional.ofNullable(this.subject.get());
   }

   public boolean isSubjectAlive() {
      Entity e = (Entity)this.subject.get();
      return e != null && e.isEntityAlive();
   }

   public double distanceSq(@Nonnull Entity entity) {
      Entity e = (Entity)this.subject.get();
      return e == null ? Double.MAX_VALUE : e.getDistanceSq(entity);
   }

   public long getWorldTime() {
      Entity e = (Entity)this.subject.get();
      return e == null ? 0L : e.getEntityWorld().getTotalWorldTime();
   }
}
