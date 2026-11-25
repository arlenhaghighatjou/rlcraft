package com.dhanantry.scapeandrunparasites.entity.tile;

import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityPStationaryArchitect;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;
import com.dhanantry.scapeandrunparasites.entity.monster.deterrent.nexus.EntityDod;
import com.dhanantry.scapeandrunparasites.entity.monster.deterrent.nexus.EntityDodSII;
import com.dhanantry.scapeandrunparasites.entity.monster.deterrent.nexus.EntityDodSIII;
import com.dhanantry.scapeandrunparasites.entity.monster.deterrent.nexus.EntityDodSIV;
import com.dhanantry.scapeandrunparasites.init.SRPSounds;
import com.dhanantry.scapeandrunparasites.util.ParasiteEventEntity;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfig;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfigSystems;
import com.dhanantry.scapeandrunparasites.world.SRPWorldData;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.AxisAlignedBB;

public class TileEntityDod extends TileEntity implements ITickable {
   private int ticksSinceSync;
   private int killC;

   public TileEntityDod() {
   }

   public void readFromNBT(NBTTagCompound compound) {
      super.readFromNBT(compound);
      if (compound.hasKey("parasitekillc", 3)) {
         this.killC = compound.getInteger("parasitekillc");
      }

   }

   public NBTTagCompound writeToNBT(NBTTagCompound compound) {
      super.writeToNBT(compound);
      compound.setInteger("parasitekillc", this.killC);
      return compound;
   }

   public void update() {
      if (!this.world.isRemote) {
         ++this.ticksSinceSync;
         if (this.ticksSinceSync % 20 != 0) {
            return;
         }

         double coll = 0.0D;
         AxisAlignedBB axisalignedbb = (new AxisAlignedBB((double)this.pos.getX(), (double)this.pos.getY(), (double)this.pos.getZ(), (double)(this.pos.getX() + 1), (double)(this.pos.getY() + 1), (double)(this.pos.getZ() + 1))).grow(16.0D);

         for(EntityParasiteBase mob : this.world.getEntitiesWithinAABB(EntityParasiteBase.class, axisalignedbb)) {
            if (mob.getKillC() > 1.0D) {
               coll += mob.getKillC();
               mob.setKillC(0.0D);
            }
         }

         if (coll != 0.0D) {
            this.killC = (int)((double)this.killC + coll);
         }

         if (this.killC > SRPConfig.reinforcerNidusValue) {
            int ccc = 0;
            axisalignedbb = (new AxisAlignedBB((double)this.pos.getX(), (double)this.pos.getY(), (double)this.pos.getZ(), (double)(this.pos.getX() + 1), (double)(this.pos.getY() + 1), (double)(this.pos.getZ() + 1))).grow(SRPConfig.turretFollow);

            for(EntityParasiteBase mob : this.world.getEntitiesWithinAABB(EntityPStationaryArchitect.class, axisalignedbb)) {
               if (mob instanceof EntityDod || mob instanceof EntityDodSII || mob instanceof EntityDodSIII || mob instanceof EntityDodSIV) {
                  ++ccc;
               }
            }

            if (ccc == 0) {
               if (ParasiteEventEntity.entityChunkCount(this.world, this.pos, EntityPStationaryArchitect.class) == 0) {
                  EntityDod dod = new EntityDod(this.world);
                  dod.setPosition((double)this.pos.getX() + 0.5D, (double)(this.pos.getY() + 1), (double)this.pos.getZ() + 0.5D);
                  if (!dod.world.getCollisionBoxes(dod, dod.getEntityBoundingBox()).isEmpty()) {
                     dod.setDead();
                  } else {
                     this.world.spawnEntity(dod);
                     dod.particleStatus((byte)7);
                     if (SRPConfigSystems.rsSounds) {
                        dod.playSound(SRPSounds.DODSI, 4.0F, 1.0F);
                     }
                  }
               } else if (SRPConfigSystems.useEvolution) {
                  SRPWorldData data = SRPWorldData.get(this.world);
                  data.setTotalKills(this.killC * SRPConfig.reinforcerNidusMult, true, this.world, true);
               }
            }

            this.world.setBlockState(this.getPos(), Blocks.AIR.getDefaultState());
         }
      }

   }
}
