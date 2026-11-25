package artifacts.common.entity;

import artifacts.common.ModConfig;
import artifacts.common.init.ModLootTables;
import artifacts.common.init.ModSoundEvents;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.block.Block;
import net.minecraft.block.BlockChest;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAITarget;
import net.minecraft.entity.ai.EntityMoveHelper;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget.Sorter;
import net.minecraft.entity.ai.EntityMoveHelper.Action;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.init.MobEffects;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.pathfinding.PathNavigateGround;
import net.minecraft.scoreboard.Team;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;
import net.minecraft.world.storage.loot.ILootContainer;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickBlock;
import net.minecraftforge.event.world.BlockEvent.BreakEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.eventhandler.Event.Result;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class EntityMimic extends EntityLiving implements IMob {
   public int ticksInAir;
   public boolean isDormant;

   public EntityMimic(World world) {
      super(world);
      this.moveHelper = new EntityMimic.MimicMoveHelper(this);
      this.setSize(0.875F, 0.875F);
      this.experienceValue = 20;
   }

   @Nullable
   public IEntityLivingData onInitialSpawn(DifficultyInstance difficulty, @Nullable IEntityLivingData livingdata) {
      this.setRandomMoveRotation();
      return super.onInitialSpawn(difficulty, livingdata);
   }

   public void setMoveRotation(float rotation, boolean aggressive) {
      ((EntityMimic.MimicMoveHelper)this.moveHelper).setDirection(rotation, aggressive);
   }

   public void setRandomMoveRotation() {
      this.setMoveRotation((float)(this.rand.nextInt(4) * 90), false);
   }

   public SoundCategory getSoundCategory() {
      return SoundCategory.HOSTILE;
   }

   public boolean canDespawn() {
      return false;
   }

   protected boolean canBeRidden(Entity entity) {
      return false;
   }

   protected boolean canFitPassenger(Entity entity) {
      return false;
   }

   public boolean isPushedByWater() {
      return false;
   }

   public boolean canBreatheUnderwater() {
      return true;
   }

   protected void collideWithEntity(Entity entity) {
   }

   @Nullable
   public AxisAlignedBB getCollisionBoundingBox() {
      return this.isEntityAlive() ? this.getEntityBoundingBox() : null;
   }

   protected void applyEntityAttributes() {
      super.applyEntityAttributes();
      this.getAttributeMap().registerAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(4.0D);
      this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(100.0D);
      this.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(24.0D);
      this.getEntityAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).setBaseValue(1.0D);
      this.getEntityAttribute(SharedMonsterAttributes.ARMOR).setBaseValue(4.0D);
   }

   protected void initEntityAI() {
      this.tasks.addTask(1, new EntityMimic.AIMimicFloat(this));
      this.tasks.addTask(2, new EntityMimic.AIMimicAttack(this));
      this.tasks.addTask(3, new EntityMimic.AIMimicFaceRandom(this));
      this.tasks.addTask(5, new EntityMimic.AIMimicHop(this));
      this.targetTasks.addTask(1, new EntityMimic.AIMimicFindNearestPlayer(this));
   }

   public void writeEntityToNBT(NBTTagCompound compound) {
      super.writeEntityToNBT(compound);
      compound.setInteger("ticksInAir", this.ticksInAir);
      compound.setBoolean("isDormant", this.isDormant);
   }

   public void readEntityFromNBT(NBTTagCompound compound) {
      super.readEntityFromNBT(compound);
      this.ticksInAir = compound.getInteger("ticksInAir");
      this.isDormant = compound.getBoolean("isDormant");
   }

   public void onUpdate() {
      if (!this.world.isRemote && this.world.getDifficulty() == EnumDifficulty.PEACEFUL) {
         this.isDead = true;
      }

      super.onUpdate();
      if (!this.isInWater() && !this.isInLava()) {
         if (!this.onGround) {
            ++this.ticksInAir;
         } else {
            if (this.ticksInAir > 0) {
               this.playSound(this.getLandingSound(), this.getSoundVolume(), this.getSoundPitch());
               this.ticksInAir = 0;
            }

            if (this.getAttackTarget() != null && this.ticksExisted % 20 == 0) {
               double d0 = this.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).getAttributeValue();
               if (this.getDistanceSq(this.getAttackTarget()) > d0 * d0) {
                  this.setAttackTarget((EntityLivingBase)null);
               }
            }

            if (this.getAttackTarget() == null) {
               this.setDormant(true);
            }
         }
      } else {
         this.ticksInAir = 0;
         if (this.isDormant) {
            this.setDormant(false);
         }
      }

   }

   public void applyEntityCollision(Entity entityIn) {
      super.applyEntityCollision(entityIn);
      if (entityIn instanceof EntityLivingBase) {
         this.dealDamage((EntityLivingBase)entityIn);
      }

   }

   public void onCollideWithPlayer(EntityPlayer player) {
      this.dealDamage(player);
   }

   private void dealDamage(EntityLivingBase entity) {
      if (!this.isDormant && this.ticksInAir > 0 && this.getDistanceSq(entity) < 1.5D && entity.attackEntityFrom(DamageSource.causeMobDamage(this), (float)this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getAttributeValue())) {
         this.applyEnchantments(this, entity);
      }

   }

   public boolean isEntityInvulnerable(DamageSource source) {
      return super.isEntityInvulnerable(source) || this.ticksInAir <= 0 && !source.isCreativePlayer() && !source.isFireDamage() && !source.isUnblockable();
   }

   public boolean attackEntityFrom(DamageSource source, float amount) {
      if (!super.attackEntityFrom(source, amount)) {
         return false;
      } else {
         if (source.getTrueSource() instanceof EntityLivingBase && (!(source.getTrueSource() instanceof EntityPlayer) || !((EntityPlayer)source.getTrueSource()).capabilities.disableDamage)) {
            this.setAttackTarget((EntityLivingBase)source.getTrueSource());
         }

         this.setDormant(false);
         return true;
      }
   }

   public void setInWeb() {
   }

   public float getEyeHeight() {
      return 0.7F;
   }

   protected SoundEvent getHurtSound(DamageSource damageSource) {
      return ModSoundEvents.MIMIC_HURT;
   }

   protected SoundEvent getDeathSound() {
      return ModSoundEvents.MIMIC_DEATH;
   }

   protected SoundEvent getJumpingSound() {
      return ModSoundEvents.MIMIC_OPEN;
   }

   protected SoundEvent getLandingSound() {
      return ModSoundEvents.MIMIC_CLOSE;
   }

   @Nullable
   protected ResourceLocation getLootTable() {
      return ModLootTables.MIMIC_UNDERGROUND;
   }

   protected void jump() {
      this.motionY = 0.5D;
      this.isAirBorne = true;
      ForgeHooks.onLivingJump(this);
   }

   public void setDormant(boolean dormant) {
      this.isDormant = dormant;
   }

   public void setAwakeWithTarget(EntityPlayer player) {
      this.setAttackTarget(player);
      this.setDormant(false);
   }

   public boolean processInteract(EntityPlayer player, EnumHand hand) {
      if (!player.isCreative()) {
         this.setAwakeWithTarget(player);
         player.swingArm(EnumHand.MAIN_HAND);
         return true;
      } else {
         return super.processInteract(player, hand);
      }
   }

   static class AIMimicAttack extends EntityAIBase {
      private final EntityMimic mimic;
      private int executeTimeRemaining;

      public AIMimicAttack(EntityMimic mimic) {
         this.mimic = mimic;
         this.setMutexBits(2);
      }

      public boolean shouldExecute() {
         EntityLivingBase entitylivingbase = this.mimic.getAttackTarget();
         if (entitylivingbase == null) {
            return false;
         } else if (!entitylivingbase.isEntityAlive()) {
            return false;
         } else {
            return !(entitylivingbase instanceof EntityPlayer) || !((EntityPlayer)entitylivingbase).capabilities.disableDamage;
         }
      }

      public void startExecuting() {
         this.executeTimeRemaining = 300;
         super.startExecuting();
      }

      public boolean shouldContinueExecuting() {
         EntityLivingBase entitylivingbase = this.mimic.getAttackTarget();
         if (entitylivingbase == null) {
            return false;
         } else if (!entitylivingbase.isEntityAlive()) {
            return false;
         } else if (entitylivingbase instanceof EntityPlayer && ((EntityPlayer)entitylivingbase).capabilities.disableDamage) {
            return false;
         } else {
            return --this.executeTimeRemaining > 0;
         }
      }

      public void updateTask() {
         if (this.mimic.getAttackTarget() != null) {
            this.mimic.faceEntity(this.mimic.getAttackTarget(), 10.0F, 10.0F);
            ((EntityMimic.MimicMoveHelper)this.mimic.getMoveHelper()).setDirection(this.mimic.rotationYaw, true);
         }

      }
   }

   static class AIMimicFaceRandom extends EntityAIBase {
      private final EntityMimic mimic;
      private int chosenDegrees;
      private int timeUntilNextFaceRandom;

      public AIMimicFaceRandom(EntityMimic mimic) {
         this.mimic = mimic;
         this.setMutexBits(2);
      }

      public boolean shouldExecute() {
         return this.mimic.getAttackTarget() == null && (this.mimic.onGround || this.mimic.isInWater() || this.mimic.isInLava() || this.mimic.isPotionActive(MobEffects.LEVITATION));
      }

      public void updateTask() {
         if (--this.timeUntilNextFaceRandom <= 0) {
            this.timeUntilNextFaceRandom = 40 + this.mimic.getRNG().nextInt(60);
            if (this.mimic.isDormant) {
               this.chosenDegrees = Math.round(this.mimic.rotationYaw / 90.0F) * 90;
               this.mimic.setPosition(Math.floor(this.mimic.posX) + 0.5D, this.mimic.posY, Math.floor(this.mimic.posZ) + 0.5D);
            } else if (this.mimic.onGround && !this.mimic.isInWater() && !this.mimic.isInLava() && !this.mimic.isPotionActive(MobEffects.LEVITATION)) {
               this.chosenDegrees = this.mimic.getRNG().nextInt(4) * 90;
               if (this.mimic.onGround) {
                  this.mimic.setDormant(true);
               }
            } else {
               this.chosenDegrees = this.mimic.getRNG().nextInt(4) * 90;
            }
         }

         ((EntityMimic.MimicMoveHelper)this.mimic.getMoveHelper()).setDirection((float)this.chosenDegrees, false);
      }
   }

   static class AIMimicFindNearestPlayer extends EntityAIBase {
      private final EntityMimic mimic;
      private final Predicate<Entity> predicate;
      private final Sorter sorter;
      private EntityLivingBase target;

      public AIMimicFindNearestPlayer(EntityMimic mimic) {
         this.mimic = mimic;
         this.predicate = (target) -> {
            if (!(target instanceof EntityPlayer)) {
               return false;
            } else if (((EntityPlayer)target).capabilities.disableDamage) {
               return false;
            } else {
               return target.getDistanceSq(this.mimic) <= this.startTargetRange() * this.startTargetRange() && EntityAITarget.isSuitableTarget(this.mimic, (EntityLivingBase)target, false, true);
            }
         };
         this.sorter = new Sorter(mimic);
      }

      public boolean shouldExecute() {
         World var10000 = this.mimic.world;
         AxisAlignedBB var10002 = this.mimic.getEntityBoundingBox().grow(this.startTargetRange(), 4.0D, this.startTargetRange());
         Predicate var10003 = this.predicate;
         this.predicate.getClass();
         List<EntityPlayer> list = var10000.getEntitiesWithinAABB(EntityPlayer.class, var10002, var10003::test);
         list.sort(this.sorter);
         if (list.isEmpty()) {
            return false;
         } else {
            this.target = (EntityLivingBase)list.get(0);
            if (this.mimic.isDormant) {
               this.mimic.setDormant(false);
            }

            return true;
         }
      }

      public boolean shouldContinueExecuting() {
         EntityLivingBase entitylivingbase = this.mimic.getAttackTarget();
         if (entitylivingbase == null) {
            return false;
         } else if (!entitylivingbase.isEntityAlive()) {
            return false;
         } else if (entitylivingbase instanceof EntityPlayer && ((EntityPlayer)entitylivingbase).capabilities.disableDamage) {
            return false;
         } else {
            Team team = this.mimic.getTeam();
            Team team1 = entitylivingbase.getTeam();
            if (team != null && team1 == team) {
               return false;
            } else {
               double targetRange = this.maxTargetRange();
               if (this.mimic.getDistanceSq(entitylivingbase) > targetRange * targetRange) {
                  return false;
               } else {
                  return !(entitylivingbase instanceof EntityPlayerMP) || !((EntityPlayerMP)entitylivingbase).interactionManager.isCreative();
               }
            }
         }
      }

      public void startExecuting() {
         this.mimic.setAttackTarget(this.target);
         super.startExecuting();
      }

      public void resetTask() {
         this.mimic.setAttackTarget((EntityLivingBase)null);
         super.startExecuting();
      }

      protected double maxTargetRange() {
         return this.mimic.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).getAttributeValue();
      }

      protected double startTargetRange() {
         return 4.0D;
      }
   }

   static class AIMimicFloat extends EntityAIBase {
      private final EntityMimic mimic;

      public AIMimicFloat(EntityMimic mimic) {
         this.mimic = mimic;
         this.setMutexBits(5);
         ((PathNavigateGround)mimic.getNavigator()).setCanSwim(true);
      }

      public boolean shouldExecute() {
         return this.mimic.isInWater() || this.mimic.isInLava();
      }

      public void updateTask() {
         if (this.mimic.getRNG().nextFloat() < 0.8F) {
            this.mimic.getJumpHelper().setJumping();
         }

         ((EntityMimic.MimicMoveHelper)this.mimic.getMoveHelper()).setSpeed(1.3D);
      }
   }

   static class AIMimicHop extends EntityAIBase {
      private final EntityMimic mimic;

      public AIMimicHop(EntityMimic mimic) {
         this.mimic = mimic;
         this.setMutexBits(5);
      }

      public boolean shouldExecute() {
         return !this.mimic.isDormant;
      }

      public void updateTask() {
         ((EntityMimic.MimicMoveHelper)this.mimic.getMoveHelper()).setSpeed(1.15D);
      }
   }

   @EventBusSubscriber(
      modid = "artifacts"
   )
   static class MimicEventHandler {
      MimicEventHandler() {
      }

      @SubscribeEvent(
         priority = EventPriority.LOW
      )
      public static void onRightClick(RightClickBlock event) {
         if (event.getUseBlock() != Result.DENY && !event.getWorld().isRemote && event.getEntityPlayer() != null && ModConfig.general.unlootedChestMimicRatio > 0.0D) {
            BlockPos pos = event.getPos();
            World world = event.getWorld();
            TileEntity tile = world.getTileEntity(pos);
            Block block = world.getBlockState(pos).getBlock();
            EntityPlayer player = event.getEntityPlayer();
            if (tile instanceof TileEntityChest && block instanceof BlockChest && !player.isSpectator()) {
               if (!Arrays.asList(ModConfig.general.unlootedChestDimensions).contains(Integer.valueOf(event.getWorld().provider.getDimension()))) {
                  return;
               }

               if (world.getBlockState(pos.up()).doesSideBlockChestOpening(world, pos.up(), EnumFacing.DOWN)) {
                  return;
               }

               if (((ILootContainer)tile).getLootTable() != null) {
                  ((TileEntityChest)tile).fillWithLoot(player);
                  if ((double)world.rand.nextFloat() <= ModConfig.general.unlootedChestMimicRatio) {
                     event.setCanceled(true);
                     world.setBlockState(pos, Blocks.AIR.getDefaultState(), 3);
                     EntityMimic mimic = new EntityMimic(world);
                     mimic.setPosition((double)pos.getX() + 0.5D, (double)pos.getY(), (double)pos.getZ() + 0.5D);
                     mimic.enablePersistence();
                     mimic.setAwakeWithTarget(player);
                     world.spawnEntity(mimic);
                  }
               }
            }

         }
      }

      @SubscribeEvent(
         priority = EventPriority.LOW
      )
      public static void onBlockBreak(BreakEvent event) {
         if (!event.getWorld().isRemote && event.getPlayer() != null && ModConfig.general.unlootedChestMimicRatio > 0.0D) {
            BlockPos pos = event.getPos();
            World world = event.getWorld();
            TileEntity tile = world.getTileEntity(pos);
            Block block = world.getBlockState(pos).getBlock();
            EntityPlayer player = event.getPlayer();
            if (tile instanceof TileEntityChest && block instanceof BlockChest && !player.isSpectator()) {
               if (!Arrays.asList(ModConfig.general.unlootedChestDimensions).contains(Integer.valueOf(event.getWorld().provider.getDimension()))) {
                  return;
               }

               if (((ILootContainer)tile).getLootTable() != null) {
                  ((TileEntityChest)tile).fillWithLoot(player);
                  if ((double)world.rand.nextFloat() <= ModConfig.general.unlootedChestMimicRatio) {
                     event.setCanceled(true);
                     world.setBlockState(pos, Blocks.AIR.getDefaultState(), 3);
                     EntityMimic mimic = new EntityMimic(world);
                     mimic.setPosition((double)pos.getX() + 0.5D, (double)pos.getY(), (double)pos.getZ() + 0.5D);
                     mimic.enablePersistence();
                     mimic.setAwakeWithTarget(player);
                     world.spawnEntity(mimic);
                  }
               }
            }

         }
      }
   }

   static class MimicMoveHelper extends EntityMoveHelper {
      private float rotationDegrees;
      private int jumpDelay;
      private final EntityMimic mimic;
      private boolean isAggressive;

      public MimicMoveHelper(EntityMimic mimic) {
         super(mimic);
         this.mimic = mimic;
         this.rotationDegrees = 180.0F * mimic.rotationYaw / 3.1415927F;
         this.jumpDelay = mimic.rand.nextInt(30) + 30;
      }

      public void setDirection(float rotation, boolean isAggressive) {
         this.rotationDegrees = rotation;
         this.isAggressive = isAggressive;
      }

      public void setSpeed(double speed) {
         this.speed = speed;
         this.action = Action.MOVE_TO;
      }

      public void onUpdateMoveHelper() {
         this.entity.rotationYaw = this.limitAngle(this.entity.rotationYaw, this.rotationDegrees, 90.0F);
         this.entity.rotationYawHead = this.entity.rotationYaw;
         this.entity.renderYawOffset = this.entity.rotationYaw;
         if (this.action != Action.MOVE_TO) {
            this.entity.setMoveForward(0.0F);
         } else {
            this.action = Action.WAIT;
            if (this.entity.onGround) {
               this.entity.setAIMoveSpeed((float)(this.speed * this.entity.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).getAttributeValue()));
               if (this.jumpDelay-- > 0) {
                  this.mimic.moveStrafing = 0.0F;
                  this.mimic.moveForward = 0.0F;
                  this.entity.setAIMoveSpeed(0.0F);
               } else {
                  this.jumpDelay = this.mimic.rand.nextInt(30) + 30;
                  if (this.isAggressive) {
                     this.jumpDelay /= 3;
                  }

                  this.mimic.getJumpHelper().setJumping();
                  this.mimic.playSound(this.mimic.getJumpingSound(), this.mimic.getSoundVolume(), this.mimic.getSoundPitch());
               }
            } else {
               this.entity.setAIMoveSpeed((float)(this.speed * this.entity.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).getAttributeValue()));
            }
         }

      }
   }
}
