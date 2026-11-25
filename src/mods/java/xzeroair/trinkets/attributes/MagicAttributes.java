package xzeroair.trinkets.attributes;

import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.entity.ai.attributes.RangedAttribute;

public class MagicAttributes {
   public static final IAttribute MAX_MANA = (new RangedAttribute((IAttribute)null, "xat.entityMagic.maxMana", 100.0D, 0.0D, Double.MAX_VALUE)).setDescription("Max Mana").setShouldWatch(true);
   public static final IAttribute regen = (new RangedAttribute((IAttribute)null, "xat.entityMagic.regen", 1.0D, 0.0D, Double.MAX_VALUE)).setDescription("Mana Regen").setShouldWatch(true);
   public static final IAttribute regenCooldown = (new RangedAttribute((IAttribute)null, "xat.entityMagic.regen.cooldown", 1.0D, 0.0D, Double.MAX_VALUE)).setDescription("Magic Regen Cooldown").setShouldWatch(true);
   public static final IAttribute affinity = (new RangedAttribute((IAttribute)null, "xat.entityMagic.affinity", 0.0D, 0.0D, Double.MAX_VALUE)).setDescription("Magic Affinity").setShouldWatch(true);

   public MagicAttributes() {
   }
}
