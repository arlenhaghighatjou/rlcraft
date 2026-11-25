package c4.champions.common.affix.affix;

import c4.champions.common.affix.core.AffixBase;
import c4.champions.common.affix.core.AffixCategory;
import c4.champions.common.affix.core.AffixNBT;
import c4.champions.common.capability.IChampionship;
import c4.champions.common.config.ConfigHandler;
import javax.vecmath.Vector3d;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;

public class AffixVortex extends AffixBase {
   public AffixVortex() {
      super("vortex", AffixCategory.CC);
   }

   public void onUpdate(EntityLiving entity, IChampionship cap) {
      if (!entity.world.isRemote) {
         EntityLivingBase target = entity.getAttackTarget();
         if (isValidAffixTarget(entity, target, true)) {
            AffixNBT.Boolean vortex = (AffixNBT.Boolean)AffixNBT.getData(cap, this.getIdentifier(), AffixNBT.Boolean.class);
            if (entity.ticksExisted % 40 == 0) {
               float chance = vortex.mode ? 0.7F : 0.4F;
               if (entity.getRNG().nextFloat() < chance) {
                  vortex.mode = !vortex.mode;
                  vortex.saveData(entity);
               }
            }

            if (vortex.mode) {
               double x = entity.posX;
               double y = entity.posY;
               double z = entity.posZ;
               double strength = ConfigHandler.affix.vortex.strength;
               Vector3d vec = new Vector3d(x, y, z);
               vec.sub(new Vector3d(target.posX, target.posY, target.posZ));
               vec.normalize();
               vec.scale(strength);
               target.motionX += vec.x;
               target.motionY += vec.y;
               target.motionZ += vec.z;
               if (target instanceof EntityPlayer) {
                  ((EntityPlayer)target).velocityChanged = true;
               }
            }
         }
      }

   }
}
