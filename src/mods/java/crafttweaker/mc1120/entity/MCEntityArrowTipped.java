package crafttweaker.mc1120.entity;

import crafttweaker.api.entity.IEntityArrowTipped;
import crafttweaker.api.item.IItemStack;
import crafttweaker.api.minecraft.CraftTweakerMC;
import crafttweaker.api.potions.IPotionEffect;
import net.minecraft.entity.projectile.EntityTippedArrow;

public class MCEntityArrowTipped extends MCEntityArrow implements IEntityArrowTipped {
   private final EntityTippedArrow entityArrowTipped;

   public MCEntityArrowTipped(EntityTippedArrow entityArrowTipped) {
      super(entityArrowTipped);
      this.entityArrowTipped = entityArrowTipped;
   }

   public void setPotionEffect(IItemStack stack) {
      this.entityArrowTipped.setPotionEffect(CraftTweakerMC.getItemStack(stack));
   }

   public void addPotionEffect(IPotionEffect effect) {
      this.entityArrowTipped.addEffect(CraftTweakerMC.getPotionEffect(effect));
   }
}
