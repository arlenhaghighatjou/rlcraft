package crafttweaker.mc1120.damage;

import crafttweaker.api.damage.IDamageSource;
import crafttweaker.api.entity.IEntity;
import crafttweaker.api.minecraft.CraftTweakerMC;
import java.util.Objects;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.DamageSource;

public class MCDamageSource implements IDamageSource {
   private final DamageSource source;

   public MCDamageSource(DamageSource source) {
      this.source = source;
   }

   public boolean canHarmInCreative() {
      return this.source.canHarmInCreative();
   }

   public String getDamageType() {
      return this.source.getDamageType();
   }

   public String getDeathMessage(IEntity entity) {
      return entity.getInternal() instanceof EntityLivingBase ? this.source.getDeathMessage((EntityLivingBase)entity.getInternal()).getFormattedText() : "ERROR";
   }

   public float getHungerDamage() {
      return this.source.getHungerDamage();
   }

   public IEntity getImmediateSource() {
      return CraftTweakerMC.getIEntity(this.source.getImmediateSource());
   }

   public IEntity getTrueSource() {
      return CraftTweakerMC.getIEntity(this.source.getTrueSource());
   }

   public boolean isCreativePlayer() {
      return this.source.isCreativePlayer();
   }

   public boolean isDamageAbsolute() {
      return this.source.isDamageAbsolute();
   }

   public boolean isDifficultyScaled() {
      return this.source.isDamageAbsolute();
   }

   public boolean isExplosion() {
      return this.source.isExplosion();
   }

   public boolean isFireDamage() {
      return this.source.isFireDamage();
   }

   public boolean isMagicDamage() {
      return this.source.isMagicDamage();
   }

   public boolean isProjectile() {
      return this.source.isProjectile();
   }

   public IDamageSource setDamageAllowedInCreativeMode() {
      return new MCDamageSource(this.source.setDamageAllowedInCreativeMode());
   }

   public IDamageSource setDamageBypassesArmor() {
      return new MCDamageSource(this.source.setDamageBypassesArmor());
   }

   public IDamageSource setDamageIsAbsolute() {
      return new MCDamageSource(this.source.setDamageIsAbsolute());
   }

   public IDamageSource setDifficultyScaled() {
      return new MCDamageSource(this.source.setDifficultyScaled());
   }

   public IDamageSource setExplosion() {
      return new MCDamageSource(this.source.setExplosion());
   }

   public IDamageSource setFireDamage() {
      return new MCDamageSource(this.source.setFireDamage());
   }

   public IDamageSource setMagicDamage() {
      return new MCDamageSource(this.source.setMagicDamage());
   }

   public IDamageSource setProjectile() {
      return new MCDamageSource(this.source.setProjectile());
   }

   public Object getInternal() {
      return this.source;
   }

   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (o != null && this.getClass() == o.getClass()) {
         MCDamageSource that = (MCDamageSource)o;
         return Objects.equals(this.source, that.source);
      } else {
         return false;
      }
   }

   public int hashCode() {
      return Objects.hash(new Object[]{this.source});
   }
}
