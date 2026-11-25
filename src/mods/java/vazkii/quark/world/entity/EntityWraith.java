package vazkii.quark.world.entity;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.init.Blocks;
import net.minecraft.init.MobEffects;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.World;
import vazkii.quark.world.feature.Wraiths;

public class EntityWraith extends EntityZombie {
   public static final ResourceLocation LOOT_TABLE = new ResourceLocation("quark:entities/wraith");
   private static final DataParameter<String> IDLE_SOUND = EntityDataManager.createKey(EntityWraith.class, DataSerializers.STRING);
   private static final DataParameter<String> HURT_SOUND = EntityDataManager.createKey(EntityWraith.class, DataSerializers.STRING);
   private static final DataParameter<String> DEATH_SOUND = EntityDataManager.createKey(EntityWraith.class, DataSerializers.STRING);
   private static final String TAG_IDLE_SOUND = "IdleSound";
   private static final String TAG_HURT_SOUND = "HurtSound";
   private static final String TAG_DEATH_SOUND = "DeathSound";

   public EntityWraith(World worldIn) {
      super(worldIn);
      this.isImmuneToFire = true;
   }

   protected void entityInit() {
      super.entityInit();
      this.dataManager.register(IDLE_SOUND, "");
      this.dataManager.register(HURT_SOUND, "");
      this.dataManager.register(DEATH_SOUND, "");
   }

   protected void applyEntityAttributes() {
      super.applyEntityAttributes();
      this.getEntityAttribute(SPAWN_REINFORCEMENTS_CHANCE).setBaseValue(0.0D);
      this.getEntityAttribute(SharedMonsterAttributes.ARMOR).setBaseValue(0.0D);
      this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.28D);
      this.getEntityAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).setBaseValue(1.0D);
      this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(40.0D);
   }

   protected void setEquipmentBasedOnDifficulty(@Nonnull DifficultyInstance difficulty) {
   }

   protected SoundEvent getAmbientSound() {
      return this.getSound(IDLE_SOUND);
   }

   protected SoundEvent getHurtSound(DamageSource damageSource) {
      return this.getSound(HURT_SOUND);
   }

   protected SoundEvent getDeathSound() {
      return this.getSound(DEATH_SOUND);
   }

   protected float getSoundPitch() {
      return this.rand.nextFloat() * 0.1F + 0.75F;
   }

   public SoundEvent getSound(DataParameter<String> param) {
      ResourceLocation loc = new ResourceLocation((String)this.dataManager.get(param));
      return (SoundEvent)SoundEvent.REGISTRY.getObject(loc);
   }

   public void onUpdate() {
      super.onUpdate();
      AxisAlignedBB aabb = this.getEntityBoundingBox();
      double x = aabb.minX + Math.random() * (aabb.maxX - aabb.minX);
      double y = aabb.minY + Math.random() * (aabb.maxY - aabb.minY);
      double z = aabb.minZ + Math.random() * (aabb.maxZ - aabb.minZ);
      this.getEntityWorld().spawnParticle(EnumParticleTypes.TOWN_AURA, x, y, z, 0.0D, 0.0D, 0.0D, new int[0]);
   }

   public boolean attackEntityAsMob(Entity entityIn) {
      boolean did = super.attackEntityAsMob(entityIn);
      if (did) {
         if (entityIn instanceof EntityLivingBase) {
            ((EntityLivingBase)entityIn).addPotionEffect(new PotionEffect(MobEffects.SLOWNESS, 60, 1));
         }

         double dx = this.posX - entityIn.posX;
         double dz = this.posZ - entityIn.posZ;
         Vec3d vec = (new Vec3d(dx, 0.0D, dz)).normalize().addVector(0.0D, 0.5D, 0.0D).normalize().scale(0.85D);
         this.motionX = vec.x;
         this.motionY = vec.y;
         this.motionZ = vec.z;
      }

      return did;
   }

   @Nullable
   public IEntityLivingData onInitialSpawn(DifficultyInstance difficulty, @Nullable IEntityLivingData data) {
      int idx = this.rand.nextInt(Wraiths.validWraithSounds.size());
      String sound = (String)Wraiths.validWraithSounds.get(idx);
      String[] split = sound.split("\\|");
      this.dataManager.set(IDLE_SOUND, split[0]);
      this.dataManager.set(HURT_SOUND, split[1]);
      this.dataManager.set(DEATH_SOUND, split[2]);
      return super.onInitialSpawn(difficulty, data);
   }

   public void fall(float distance, float damageMultiplier) {
   }

   public void writeEntityToNBT(NBTTagCompound compound) {
      super.writeEntityToNBT(compound);
      compound.setString("IdleSound", (String)this.dataManager.get(IDLE_SOUND));
      compound.setString("HurtSound", (String)this.dataManager.get(HURT_SOUND));
      compound.setString("DeathSound", (String)this.dataManager.get(DEATH_SOUND));
   }

   public void readEntityFromNBT(NBTTagCompound compound) {
      super.readEntityFromNBT(compound);
      this.dataManager.set(IDLE_SOUND, compound.getString("IdleSound"));
      this.dataManager.set(HURT_SOUND, compound.getString("HurtSound"));
      this.dataManager.set(DEATH_SOUND, compound.getString("DeathSound"));
   }

   protected ResourceLocation getLootTable() {
      return LOOT_TABLE;
   }

   public void setChild(boolean childZombie) {
   }

   public boolean isChild() {
      return false;
   }

   public boolean getCanSpawnHere() {
      BlockPos blockpos = new BlockPos(this.posX, this.getEntityBoundingBox().minY - 1.0D, this.posZ);
      return super.getCanSpawnHere() && this.getEntityWorld().getBlockState(blockpos).getBlock() == Blocks.SOUL_SAND;
   }
}
