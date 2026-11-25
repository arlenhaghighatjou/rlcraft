package org.orecruncher.dsurround.client.effects;

import java.util.List;
import java.util.Optional;
import javax.annotation.Nonnull;
import net.minecraft.entity.Entity;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.orecruncher.dsurround.registry.RegistryManager;
import org.orecruncher.dsurround.registry.effect.EntityEffectInfo;
import org.orecruncher.lib.collections.ObjectArray;

@SideOnly(Side.CLIENT)
public class EntityEffectLibrary {
   protected final ObjectArray<IEntityEffectFactoryFilter> filters = new ObjectArray();
   protected final ObjectArray<IEntityEffectFactory> factories = new ObjectArray();
   protected final IParticleHelper particleHelper;
   protected final ISoundHelper soundHelper;

   public EntityEffectLibrary(@Nonnull IParticleHelper ph, @Nonnull ISoundHelper sh) {
      this.particleHelper = ph;
      this.soundHelper = sh;
   }

   public void register(@Nonnull IEntityEffectFactoryFilter filter, @Nonnull IEntityEffectFactory factory) {
      this.filters.add(filter);
      this.factories.add(factory);
   }

   @Nonnull
   public Optional<EntityEffectHandler> create(@Nonnull Entity entity) {
      ObjectArray<EntityEffect> effectToApply = new ObjectArray();
      EntityEffectInfo eei = RegistryManager.EFFECTS.getEffects(entity);

      for(int i = 0; i < this.filters.size(); ++i) {
         if (((IEntityEffectFactoryFilter)this.filters.get(i)).applies(entity, eei)) {
            List<EntityEffect> r = ((IEntityEffectFactory)this.factories.get(i)).create(entity);
            effectToApply.addAll(r);
         }
      }

      EntityEffectHandler result;
      if (effectToApply.size() > 0) {
         result = new EntityEffectHandler(entity, effectToApply, this.particleHelper, this.soundHelper);
      } else {
         result = new EntityEffectHandler.Dummy(entity);
      }

      return Optional.of(result);
   }
}
