package svenhjol.charm.brewing.potion;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;
import net.minecraft.potion.PotionType;
import net.minecraft.util.ResourceLocation;
import svenhjol.charm.base.CharmPotion;
import svenhjol.charm.brewing.feature.Decay;
import svenhjol.meson.registry.ProxyRegistry;

public class DecayPotion extends CharmPotion {
   public static PotionType type;
   public static List<PotionEffect> effects = new ArrayList();

   public DecayPotion() {
      super("decay", true, 2490656, 0);
      effects.add(new PotionEffect(MobEffects.WITHER, Decay.duration * 20, Decay.strength));
      PotionEffect[] e = (PotionEffect[])effects.toArray(new PotionEffect[0]);
      type = (PotionType)(new PotionType(this.name, e)).setRegistryName(new ResourceLocation(this.getModId(), this.name));
      ProxyRegistry.register(type);
   }

   public String getModId() {
      return "charm";
   }
}
