package vazkii.quark.misc.entity;

import javax.annotation.Nonnull;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class EntitySoulPowder extends Entity {
   private static final DataParameter<Integer> TARGET_X = EntityDataManager.createKey(EntitySoulPowder.class, DataSerializers.VARINT);
   private static final DataParameter<Integer> TARGET_Z = EntityDataManager.createKey(EntitySoulPowder.class, DataSerializers.VARINT);
   private int liveTicks;
   private static final String TAG_TARGET_X = "targetX";
   private static final String TAG_TARGET_Z = "targetZ";

   public EntitySoulPowder(World worldIn) {
      super(worldIn);
      this.liveTicks = 0;
      this.setSize(0.0F, 0.0F);
   }

   public EntitySoulPowder(World worldIn, int x, int z) {
      this(worldIn);
      this.dataManager.set(TARGET_X, Integer.valueOf(x));
      this.dataManager.set(TARGET_Z, Integer.valueOf(z));
   }

   protected void entityInit() {
      this.dataManager.register(TARGET_X, Integer.valueOf(0));
      this.dataManager.register(TARGET_Z, Integer.valueOf(0));
   }

   public void onUpdate() {
      super.onUpdate();
      double posSpread = 0.4D;
      double scale = 0.08D;
      double rotateSpread = 1.5D;
      double rise = 0.05D;
      int maxLiveTime = 6000;
      int particles = 20;
      double trigArg = (double)this.liveTicks * 0.32D;
      if (maxLiveTime - this.liveTicks < particles) {
         particles = maxLiveTime - this.liveTicks;
      }

      Vec3d vec = (new Vec3d((double)((Integer)this.dataManager.get(TARGET_X)).intValue(), this.posY, (double)((Integer)this.dataManager.get(TARGET_Z)).intValue())).subtract(this.posX, this.posY, this.posZ).normalize().scale(scale);
      double bpx = this.posX + vec.x * (double)this.liveTicks + Math.cos(trigArg) * rotateSpread;
      double bpy = this.posY + vec.y * (double)this.liveTicks + (double)this.liveTicks * rise;
      double bpz = this.posZ + vec.z * (double)this.liveTicks + Math.sin(trigArg) * rotateSpread;

      for(int i = 0; i < particles; ++i) {
         double px = bpx + (Math.random() - 0.5D) * posSpread;
         double py = bpy + (Math.random() - 0.5D) * posSpread;
         double pz = bpz + (Math.random() - 0.5D) * posSpread;
         this.world.spawnParticle(EnumParticleTypes.REDSTONE, px, py, pz, 0.2D, 0.12D, 0.1D, new int[0]);
         if (Math.random() < 0.05D) {
            this.world.spawnParticle(EnumParticleTypes.FALLING_DUST, px, py, pz, 0.0D, 0.0D, 0.0D, new int[]{Block.getStateId(Blocks.SOUL_SAND.getDefaultState())});
         }
      }

      if (Math.random() < 0.1D) {
         this.world.playSound((EntityPlayer)null, bpx, bpy, bpz, SoundEvents.ENTITY_GHAST_AMBIENT, SoundCategory.PLAYERS, 0.2F, 1.0F);
      }

      ++this.liveTicks;
      if (this.liveTicks > maxLiveTime || this.world.getBlockState(new BlockPos(bpx, bpy, bpz)).isBlockNormalCube()) {
         this.setDead();
      }

   }

   protected void readEntityFromNBT(@Nonnull NBTTagCompound compound) {
      this.dataManager.set(TARGET_X, Integer.valueOf(compound.getInteger("targetX")));
      this.dataManager.set(TARGET_Z, Integer.valueOf(compound.getInteger("targetZ")));
   }

   protected void writeEntityToNBT(@Nonnull NBTTagCompound compound) {
      compound.setInteger("targetX", ((Integer)this.dataManager.get(TARGET_X)).intValue());
      compound.setInteger("targetZ", ((Integer)this.dataManager.get(TARGET_Z)).intValue());
   }
}
