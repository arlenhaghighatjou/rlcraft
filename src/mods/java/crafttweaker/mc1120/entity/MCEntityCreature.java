package crafttweaker.mc1120.entity;

import crafttweaker.api.entity.IEntityCreature;
import crafttweaker.api.minecraft.CraftTweakerMC;
import crafttweaker.api.world.IBlockPos;
import net.minecraft.entity.EntityCreature;

public class MCEntityCreature extends MCEntityLiving implements IEntityCreature {
   private final EntityCreature entityCreature;

   public MCEntityCreature(EntityCreature entity) {
      super(entity);
      this.entityCreature = entity;
   }

   public boolean hasPath() {
      return this.entityCreature.hasPath();
   }

   public boolean isWithinHomeDistance() {
      return this.entityCreature.isWithinHomeDistanceCurrentPosition();
   }

   public boolean isPositionWithinHomeDistance(IBlockPos pos) {
      return this.entityCreature.isWithinHomeDistanceFromPosition(CraftTweakerMC.getBlockPos(pos));
   }

   public void setHomePositionAndDistance(IBlockPos pos, int distance) {
      this.entityCreature.setHomePosAndDistance(CraftTweakerMC.getBlockPos(pos), distance);
   }

   public IBlockPos getHomePosition() {
      return CraftTweakerMC.getIBlockPos(this.entityCreature.getHomePosition());
   }

   public float getMaximumHomeDistance() {
      return this.entityCreature.getMaximumHomeDistance();
   }

   public void detachHome() {
      this.entityCreature.detachHome();
   }

   public boolean hasHome() {
      return this.entityCreature.hasHome();
   }
}
