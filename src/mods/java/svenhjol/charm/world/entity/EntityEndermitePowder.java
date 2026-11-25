package svenhjol.charm.world.entity;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class EntityEndermitePowder extends Entity {
   public int ticks;
   private static final DataParameter<Integer> TARGET_X = EntityDataManager.createKey(EntityEndermitePowder.class, DataSerializers.VARINT);
   private static final DataParameter<Integer> TARGET_Z = EntityDataManager.createKey(EntityEndermitePowder.class, DataSerializers.VARINT);
   private static final String TAG_TARGET_X = "targetX";
   private static final String TAG_TARGET_Z = "targetZ";

   public EntityEndermitePowder(World world) {
      super(world);
      this.ticks = 0;
      this.setSize(0.0F, 0.0F);
   }

   public EntityEndermitePowder(World world, int x, int z) {
      this(world);
      this.dataManager.set(TARGET_X, Integer.valueOf(x));
      this.dataManager.set(TARGET_Z, Integer.valueOf(z));
   }

   protected void entityInit() {
      this.dataManager.register(TARGET_X, Integer.valueOf(0));
      this.dataManager.register(TARGET_Z, Integer.valueOf(0));
   }

   public void onUpdate() {
      super.onUpdate();
      double posSpread = 0.5D;
      double scale = 0.2D;
      double rise = 0.08D;
      int maxLiveTime = 1000;
      int particles = 18;
      if (maxLiveTime - this.ticks < particles) {
         particles = maxLiveTime - this.ticks;
      }

      Vec3d vec = (new Vec3d((double)((Integer)this.dataManager.get(TARGET_X)).intValue(), this.posY, (double)((Integer)this.dataManager.get(TARGET_Z)).intValue())).subtract(this.posX, this.posY, this.posZ).normalize().scale(scale);
      double bpx = this.posX + vec.x * (double)this.ticks;
      double bpy = this.posY + vec.y * (double)this.ticks + (double)this.ticks * rise;
      double bpz = this.posZ + vec.z * (double)this.ticks;

      for(int i = 0; i < particles; ++i) {
         double px = bpx + (Math.random() - 0.5D) * posSpread;
         double py = bpy + (Math.random() - 0.5D) * posSpread;
         double pz = bpz + (Math.random() - 0.5D) * posSpread;
         this.world.spawnParticle(EnumParticleTypes.PORTAL, px, py, pz, 0.2D, 0.12D, 0.1D, new int[0]);
      }

      ++this.ticks;
      if (this.ticks > maxLiveTime || this.world.getBlockState(new BlockPos(bpx, bpy, bpz)).isBlockNormalCube()) {
         this.setDead();
      }

   }

   protected void readEntityFromNBT(NBTTagCompound tag) {
      this.dataManager.set(TARGET_X, Integer.valueOf(tag.getInteger("targetX")));
      this.dataManager.set(TARGET_Z, Integer.valueOf(tag.getInteger("targetZ")));
   }

   protected void writeEntityToNBT(NBTTagCompound tag) {
      tag.setInteger("targetX", ((Integer)this.dataManager.get(TARGET_X)).intValue());
      tag.setInteger("targetZ", ((Integer)this.dataManager.get(TARGET_Z)).intValue());
   }
}
