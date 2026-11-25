package xzeroair.trinkets.init;

import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;

public class TrinketsDamageSource extends EntityDamageSource {
   public static final TrinketsDamageSource poison = (new TrinketsDamageSource("xat.poison")).setIsPoisonDamage(true);
   public static final TrinketsDamageSource bleeding = new TrinketsDamageSource("xat.bleed");
   public static final TrinketsDamageSource water = new TrinketsDamageSource("xat.water");
   private boolean isPoisonDamage;

   public TrinketsDamageSource(String damageType) {
      this(damageType, (Entity)null);
   }

   public TrinketsDamageSource(String damageType, @Nullable Entity damageSource) {
      super(damageType, damageSource);
      this.damageSourceEntity = damageSource;
   }

   public boolean isPoisonDamage() {
      return this.isPoisonDamage;
   }

   public TrinketsDamageSource setIsPoisonDamage(boolean isPoison) {
      this.isPoisonDamage = isPoison;
      return this;
   }

   public TrinketsDamageSource setDirectSource(Entity damageSource) {
      this.damageSourceEntity = damageSource;
      return this;
   }

   public static TrinketsDamageSource causeDamageFrom(String damageType, Entity damageSource) {
      return new TrinketsDamageSource(damageType, damageSource);
   }

   public ITextComponent getDeathMessage(EntityLivingBase entityLivingBaseIn) {
      ITextComponent source = new TextComponentTranslation("damagetype." + this.damageType, new Object[0]);
      String s = "death.attack." + this.damageType;
      return new TextComponentTranslation(s, new Object[]{entityLivingBaseIn.getDisplayName(), source});
   }
}
