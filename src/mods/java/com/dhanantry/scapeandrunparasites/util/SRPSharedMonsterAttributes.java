package com.dhanantry.scapeandrunparasites.util;

import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.entity.ai.attributes.RangedAttribute;

public class SRPSharedMonsterAttributes extends SharedMonsterAttributes {
   public static final IAttribute SRPMAX_HEALTH = (new RangedAttribute((IAttribute)null, "generic.maxHealth", 20.0D, 1.401298464324817E-45D, 3.4028234663852886E38D)).setDescription("Max Health").setShouldWatch(true);
   public static final IAttribute SRPFOLLOW_RANGE = (new RangedAttribute((IAttribute)null, "generic.followRange", 32.0D, 0.0D, 2048.0D)).setDescription("Follow Range");
   public static final IAttribute SRPKNOCKBACK_RESISTANCE = (new RangedAttribute((IAttribute)null, "generic.knockbackResistance", 0.0D, 0.0D, 1.0D)).setDescription("Knockback Resistance");
   public static final IAttribute SRPMOVEMENT_SPEED = (new RangedAttribute((IAttribute)null, "generic.movementSpeed", 0.699999988079071D, 0.0D, 1024.0D)).setDescription("Movement Speed").setShouldWatch(true);
   public static final IAttribute SRPATTACK_DAMAGE = new RangedAttribute((IAttribute)null, "generic.attackDamage", 2.0D, 0.0D, 3.4028234663852886E38D);
   public static final IAttribute SRPARMOR = (new RangedAttribute((IAttribute)null, "generic.armor", 0.0D, 0.0D, 500.0D)).setShouldWatch(true);
   public static final IAttribute SRPARMOR_TOUGHNESS = (new RangedAttribute((IAttribute)null, "generic.armorToughness", 0.0D, 0.0D, 3.4028234663852886E38D)).setShouldWatch(true);

   public SRPSharedMonsterAttributes() {
   }
}
