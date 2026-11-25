package com.dhanantry.scapeandrunparasites.entity.ai;

import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityCanMelt;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityPInfected;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;
import com.dhanantry.scapeandrunparasites.entity.monster.crude.EntityLesh;
import java.util.List;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;

public class EntityAIInfectedSearch extends EntityAIBase {
   private final EntityParasiteBase parent;
   private World world;
   private int parentID;
   private double moveSpeed;
   private EntityParasiteBase[] array = new EntityParasiteBase[4];
   private int delay;
   private boolean canmelt;

   public EntityAIInfectedSearch(EntityParasiteBase animal, double speedIn, boolean melt) {
      this.parent = animal;
      this.world = animal.world;
      this.moveSpeed = speedIn;
      this.parentID = animal.getEntityId();
      this.canmelt = melt;
   }

   public boolean shouldExecute() {
      if (this.parent.ticksExisted % 40 == 0) {
         return false;
      } else {
         return this.parent.getAttackTarget() != null ? false : this.LeshCount();
      }
   }

   public void resetTask() {
   }

   public void updateTask() {
      if (this.parent.ticksExisted % 20 == 0 && this.parent.getKillC() >= 0.0D) {
         int count = 0;
         AxisAlignedBB axisalignedbb = (new AxisAlignedBB(this.parent.posX, this.parent.posY, this.parent.posZ, this.parent.posX + 1.0D, this.parent.posY + 1.0D, this.parent.posZ + 1.0D)).grow(this.parent.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).getAttributeValue());
         List<EntityPInfected> moblist = this.parent.world.getEntitiesWithinAABB(EntityPInfected.class, axisalignedbb);

         for(EntityLesh mob : this.parent.world.getEntitiesWithinAABB(EntityLesh.class, axisalignedbb)) {
            if (mob != null) {
               ++count;
            }
         }

         if (count >= 3 && this.canmelt) {
            EntityCanMelt meh = (EntityCanMelt)this.parent;
            meh.melt();
         } else {
            for(EntityPInfected mob : moblist) {
               if (mob != null && mob != this.parent && mob instanceof EntityCanMelt && this.parent.canEntityBeSeen(mob) && mob.getAttackTarget() == null) {
                  EntityCanMelt meh = (EntityCanMelt)mob;
                  if (!meh.isMelting()) {
                     ++count;
                     meh.melt();
                     if (count >= 3 && this.canmelt) {
                        EntityCanMelt moh = (EntityCanMelt)this.parent;
                        moh.melt();
                        return;
                     }
                  }
               }
            }

            if (this.canmelt) {
               EntityCanMelt out = (EntityCanMelt)this.parent;
               out.melt();
            }

         }
      }
   }

   protected boolean LeshCount() {
      int countL = 0;
      int countI = 0;
      boolean more = false;
      AxisAlignedBB axisalignedbb = (new AxisAlignedBB(this.parent.posX, this.parent.posY, this.parent.posZ, this.parent.posX + 1.0D, this.parent.posY + 1.0D, this.parent.posZ + 1.0D)).grow(this.parent.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).getAttributeValue());

      for(EntityParasiteBase mob : this.parent.world.getEntitiesWithinAABB(EntityParasiteBase.class, axisalignedbb)) {
         if (mob != null) {
            if (mob instanceof EntityLesh && this.parent.canEntityBeSeen(mob)) {
               EntityLesh meh = (EntityLesh)mob;
               countL += meh.getTimes();
            }

            if (mob != this.parent && this.parent.canEntityBeSeen(mob) && mob instanceof EntityCanMelt && mob.getAttackTarget() == null) {
               EntityCanMelt meh = (EntityCanMelt)mob;
               if (!meh.isMelting()) {
                  ++countI;
               }
            }
         }
      }

      if (countL >= 1 && countL <= 3 && this.canmelt) {
         EntityCanMelt meh = (EntityCanMelt)this.parent;
         meh.melt();
         return false;
      } else if (countI >= 3) {
         return true;
      } else {
         return false;
      }
   }
}
