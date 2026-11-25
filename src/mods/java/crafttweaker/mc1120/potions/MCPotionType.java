package crafttweaker.mc1120.potions;

import crafttweaker.api.minecraft.CraftTweakerMC;
import crafttweaker.api.potions.IPotionEffect;
import crafttweaker.api.potions.IPotionType;
import java.util.Objects;
import net.minecraft.potion.PotionType;

public class MCPotionType implements IPotionType {
   public final PotionType potionType;

   public MCPotionType(PotionType potionType) {
      this.potionType = potionType;
   }

   public PotionType getInternal() {
      return this.potionType;
   }

   public IPotionEffect[] getPotionEffects() {
      return CraftTweakerMC.getIPotionEffects(this.potionType.getEffects());
   }

   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (o != null && this.getClass() == o.getClass()) {
         MCPotionType that = (MCPotionType)o;
         return Objects.equals(this.potionType, that.potionType);
      } else {
         return false;
      }
   }

   public int hashCode() {
      return Objects.hash(new Object[]{this.potionType});
   }
}
