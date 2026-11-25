package xzeroair.trinkets.items.potions;

import java.util.function.Consumer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import xzeroair.trinkets.api.TrinketHelper;
import xzeroair.trinkets.capabilities.Capabilities;
import xzeroair.trinkets.items.base.BasePotion;
import xzeroair.trinkets.traits.AbilityHandler;
import xzeroair.trinkets.traits.abilities.AbilityIceImmunity;

public class IceResistance extends BasePotion {
   public IceResistance(String name, int duration, int color, boolean isBadEffect) {
      super(name, color, duration, isBadEffect);
   }

   public boolean isInstant() {
      return false;
   }

   public boolean isReady(int duration, int amplifier) {
      return true;
   }

   public void affectEntity(Entity source, Entity indirectSource, EntityLivingBase entity, int amplifier, double health) {
   }

   public void performEffect(EntityLivingBase entity, int amplifier) {
      try {
         Capabilities.getEntityProperties(entity, (prop) -> {
            AbilityHandler handler = prop.getAbilityHandler();
            AbilityIceImmunity ability = new AbilityIceImmunity();
            handler.registerAbility(this.getRegistryName().toString(), new TrinketHelper.SlotInformation(TrinketHelper.SlotInformation.ItemHandlerType.POTION), ability);
         });
      } catch (Exception var4) {
         ;
      }

   }
}
