package crafttweaker.mc1120.entity;

import crafttweaker.api.entity.IEntityAnimal;
import crafttweaker.api.item.IItemStack;
import crafttweaker.api.minecraft.CraftTweakerMC;
import crafttweaker.api.player.IPlayer;
import net.minecraft.entity.passive.EntityAnimal;

public class MCEntityAnimal extends MCEntityAgeable implements IEntityAnimal {
   private final EntityAnimal entityAnimal;

   public MCEntityAnimal(EntityAnimal entity) {
      super(entity);
      this.entityAnimal = entity;
   }

   public boolean isBreedingItem(IItemStack itemStack) {
      return this.entityAnimal.isBreedingItem(CraftTweakerMC.getItemStack(itemStack));
   }

   public void setInLove(IPlayer player) {
      this.entityAnimal.setInLove(CraftTweakerMC.getPlayer(player));
   }

   public IPlayer getLoveCause() {
      return CraftTweakerMC.getIPlayer(this.entityAnimal.getLoveCause());
   }

   public boolean isInLove() {
      return this.entityAnimal.isInLove();
   }

   public void resetInLove() {
      this.entityAnimal.resetInLove();
   }

   public boolean canMateWith(IEntityAnimal other) {
      return this.entityAnimal.canMateWith(CraftTweakerMC.getEntityAnimal(other));
   }
}
