package crafttweaker.mc1120.entity;

import crafttweaker.api.entity.IEntityMob;
import crafttweaker.api.minecraft.CraftTweakerMC;
import crafttweaker.api.player.IPlayer;
import net.minecraft.entity.monster.EntityMob;

public class MCEntityMob extends MCEntityCreature implements IEntityMob {
   private final EntityMob entityMob;

   public MCEntityMob(EntityMob entity) {
      super(entity);
      this.entityMob = entity;
   }

   public boolean isPreventingPlayerRest(IPlayer player) {
      return this.entityMob.isPreventingPlayerRest(CraftTweakerMC.getPlayer(player));
   }
}
