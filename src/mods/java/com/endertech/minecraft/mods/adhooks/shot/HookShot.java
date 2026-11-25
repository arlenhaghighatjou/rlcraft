package com.endertech.minecraft.mods.adhooks.shot;

import com.endertech.common.CommonMath;
import com.endertech.common.FloatBounds;
import com.endertech.minecraft.forge.entities.ForgeEntity;
import com.endertech.minecraft.forge.math.Combustion;
import com.endertech.minecraft.forge.math.ForgeMath;
import com.endertech.minecraft.forge.math.Rotation;
import com.endertech.minecraft.forge.math.Vect3d;
import com.endertech.minecraft.forge.world.ForgeWorld;
import com.endertech.minecraft.mods.adhooks.init.HookType;
import com.endertech.minecraft.mods.adhooks.items.Hook;
import com.endertech.minecraft.mods.adhooks.items.Launcher;
import com.endertech.minecraft.mods.adhooks.items.Rope;
import com.endertech.minecraft.mods.adhooks.motion.BoostUpMsg;
import com.endertech.minecraft.mods.adhooks.motion.EntityTarget;
import com.endertech.minecraft.mods.adhooks.motion.MotionController;
import com.endertech.minecraft.mods.adhooks.motion.SinglePlayerTarget;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityMultiPart;
import net.minecraft.entity.IProjectile;
import net.minecraft.entity.MultiPartEntityPart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSourceIndirect;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.registry.IThrowableEntity;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class HookShot extends ForgeEntity implements IThrowableEntity, IProjectile {
   public static final float GRAVITY_VELOCITY = 0.005F;
   public static final int RENDER_DISTANCE = Rope.LENGTH_BOUNDS.getMax().intValue();
   private float shootStrength;
   private float size;
   private EnumFacing hitSide;
   private Combustion combustion;

   public HookShot(World world) {
      super(world);
      this.shootStrength = 1.5F;
      this.size = 0.6F;
      this.hitSide = null;
      this.combustion = new Combustion();
      this.setSize(this.size, this.size);
      this.ignoreFrustumCheck = true;
   }

   public HookShot(World world, EntityLivingBase shooter, HookType hookType) {
      this(world);
      this.dataManager.set(HookShot.WatchedInt.SHOOTER_ID.key, Integer.valueOf(ForgeEntity.getId(shooter)));
      this.setHookType(hookType);
      this.setLocationAndAngles(shooter.posX, shooter.posY + (double)shooter.getEyeHeight(), shooter.posZ, shooter.rotationYaw, shooter.rotationPitch);
      this.posX -= (double)(ForgeMath.cos(this.rotationYaw) * 0.16F);
      this.posY -= 0.10000000149011612D;
      this.posZ -= (double)(ForgeMath.sin(this.rotationYaw) * 0.16F);
      this.setPosition(this.posX, this.posY, this.posZ);
      this.motionX = (double)(-ForgeMath.sin(this.rotationYaw) * ForgeMath.cos(this.rotationPitch));
      this.motionZ = (double)(ForgeMath.cos(this.rotationYaw) * ForgeMath.cos(this.rotationPitch));
      this.motionY = (double)(-ForgeMath.sin(this.rotationPitch));
      this.shoot(this.motionX, this.motionY, this.motionZ, this.shootStrength, 1.0F);
      if (world != null && shooter != null) {
         world.playSound((EntityPlayer)null, shooter.posX, shooter.posY, shooter.posZ, SoundEvents.ENTITY_ARROW_SHOOT, SoundCategory.NEUTRAL, 1.0F, 1.0F / (this.rand.nextFloat() * 0.4F + 1.2F) + 0.5F);
      }

   }

   protected void entityInit() {
      super.entityInit();

      for(HookShot.WatchedBool b : HookShot.WatchedBool.values()) {
         this.dataManager.register(b.key, Boolean.valueOf(false));
      }

      for(HookShot.WatchedInt i : HookShot.WatchedInt.values()) {
         this.dataManager.register(i.key, Integer.valueOf(0));
      }

      for(HookShot.WatchedFloat f : HookShot.WatchedFloat.values()) {
         this.dataManager.register(f.key, Float.valueOf(0.0F));
      }

      this.setState(HookShot.State.SHOOTING);
   }

   public boolean isLoosening() {
      return ((Boolean)this.dataManager.get(HookShot.WatchedBool.LOOSENING.key)).booleanValue();
   }

   public void setLoosening(boolean loosening) {
      if (this.isServerSide()) {
         this.dataManager.set(HookShot.WatchedBool.LOOSENING.key, Boolean.valueOf(loosening));
      }

   }

   public boolean isPulling() {
      return ((Boolean)this.dataManager.get(HookShot.WatchedBool.PULLING.key)).booleanValue();
   }

   public void setPulling(boolean pulling) {
      if (this.isServerSide()) {
         this.dataManager.set(HookShot.WatchedBool.PULLING.key, Boolean.valueOf(pulling));
      }

   }

   public HookShot.State getPrevState() {
      int i = ((Integer)this.dataManager.get(HookShot.WatchedInt.PREV_STATE.key)).intValue();
      return HookShot.State.values()[i];
   }

   public HookShot.State getState() {
      int i = ((Integer)this.dataManager.get(HookShot.WatchedInt.STATE.key)).intValue();
      return HookShot.State.values()[i];
   }

   public void setState(HookShot.State state) {
      if (this.isServerSide()) {
         HookShot.State prevState = this.getState();
         if (state != prevState) {
            this.dataManager.set(HookShot.WatchedInt.PREV_STATE.key, Integer.valueOf(prevState.ordinal()));
            this.dataManager.set(HookShot.WatchedInt.STATE.key, Integer.valueOf(state.ordinal()));
         }
      }

   }

   public float getRopeLength() {
      return ((Float)this.dataManager.get(HookShot.WatchedFloat.ROPE_LENGTH.key)).floatValue();
   }

   public void setRopeLength(float ropeLength) {
      if (this.isServerSide()) {
         this.dataManager.set(HookShot.WatchedFloat.ROPE_LENGTH.key, Float.valueOf(ropeLength));
      }

   }

   public float getSagging() {
      return ((Float)this.dataManager.get(HookShot.WatchedFloat.SAGGING.key)).floatValue();
   }

   public void setSagging(float sagging) {
      if (this.isServerSide()) {
         if (sagging < 0.0F) {
            sagging = 0.0F;
         }

         this.dataManager.set(HookShot.WatchedFloat.SAGGING.key, Float.valueOf(sagging));
      }

   }

   public void setHookType(HookType value) {
      if (this.isServerSide() && value != null) {
         this.dataManager.set(HookShot.WatchedInt.HOOK_TYPE.key, Integer.valueOf(value.ordinal()));
      }

   }

   public HookType getHookType() {
      int ordinal = ((Integer)this.dataManager.get(HookShot.WatchedInt.HOOK_TYPE.key)).intValue();
      return HookType.values()[ordinal];
   }

   public boolean canHookOnReeling() {
      return this.getHookType() == HookType.PUDGE;
   }

   public HookShot.BlockAction getBlockAction(World world, BlockPos pos) {
      IBlockState state = world.getBlockState(pos);
      Material material = state.getMaterial();
      if (ForgeWorld.isAirBlock(state)) {
         return HookShot.BlockAction.PASS;
      } else {
         HookType hookType = this.getHookType();
         Hook hook = hookType.hook;
         switch(hookType) {
         case WEB:
            if (hook.canHookAnyBlock) {
               return HookShot.BlockAction.HOOK;
            } else {
               if (material == Material.SAND) {
                  return HookShot.BlockAction.BOUNCE;
               }

               return HookShot.BlockAction.HOOK;
            }
         case PUDGE:
            if (material == Material.GLASS) {
               return HookShot.BlockAction.BREAK;
            } else {
               if (material == Material.LEAVES) {
                  return HookShot.BlockAction.BREAK;
               }

               return HookShot.BlockAction.BOUNCE;
            }
         case SPEAR:
            if (material == Material.GLASS) {
               return HookShot.BlockAction.BREAK;
            } else if (material == Material.LEAVES) {
               return HookShot.BlockAction.PASS;
            } else if (material == Material.WOOD) {
               return HookShot.BlockAction.HOOK;
            } else {
               if (!hook.canHookAnyBlock) {
                  float hardness = state.getBlockHardness(world, pos);
                  if (hardness < 0.0F || hardness > hookType.hook.getStrength() / 2.0F) {
                     return HookShot.BlockAction.BOUNCE;
                  }
               }

               return HookShot.BlockAction.HOOK;
            }
         default:
            return HookShot.BlockAction.BOUNCE;
         }
      }
   }

   protected Vect3d getHitPosition() {
      return Vect3d.from((double)((Float)this.dataManager.get(HookShot.WatchedFloat.HIT_X.key)).floatValue(), (double)((Float)this.dataManager.get(HookShot.WatchedFloat.HIT_Y.key)).floatValue(), (double)((Float)this.dataManager.get(HookShot.WatchedFloat.HIT_Z.key)).floatValue());
   }

   public Vect3d getLauncherPosition() {
      return getCenterPosition(this.getThrower());
   }

   public Vect3d getLauncherPosition(float tickProgress) {
      return getCenterPosition(this.getThrower(), tickProgress);
   }

   public void setHitPosition(Vect3d hitPosition) {
      if (this.isServerSide() && hitPosition != null) {
         this.dataManager.set(HookShot.WatchedFloat.HIT_X.key, Float.valueOf((float)hitPosition.x));
         this.dataManager.set(HookShot.WatchedFloat.HIT_Y.key, Float.valueOf((float)hitPosition.y));
         this.dataManager.set(HookShot.WatchedFloat.HIT_Z.key, Float.valueOf((float)hitPosition.z));
      }

   }

   public boolean isHookingBlock() {
      return this.getState() == HookShot.State.HOOKING_BLOCK;
   }

   public boolean isHookingEntity() {
      return this.getState() == HookShot.State.HOOKING_ENTITY;
   }

   public boolean isLauncherInMainHand() {
      ItemStack itemStack = this.getThrower() != null ? this.getThrower().getHeldItemMainhand() : null;
      return itemStack != null && itemStack.getItem() instanceof Launcher && Launcher.isAttachedToHookShot(itemStack, this);
   }

   public BlockPos getHookedBlockPos() {
      return new BlockPos(((Integer)this.dataManager.get(HookShot.WatchedInt.HOOKED_BLOCK_X.key)).intValue(), ((Integer)this.dataManager.get(HookShot.WatchedInt.HOOKED_BLOCK_Y.key)).intValue(), ((Integer)this.dataManager.get(HookShot.WatchedInt.HOOKED_BLOCK_Z.key)).intValue());
   }

   private void setHookedBlockPos(RayTraceResult rayTraceResult) {
      if (this.isServerSide() && rayTraceResult != null && rayTraceResult.getBlockPos() != null) {
         this.dataManager.set(HookShot.WatchedInt.HOOKED_BLOCK_X.key, Integer.valueOf(rayTraceResult.getBlockPos().getX()));
         this.dataManager.set(HookShot.WatchedInt.HOOKED_BLOCK_Y.key, Integer.valueOf(rayTraceResult.getBlockPos().getY()));
         this.dataManager.set(HookShot.WatchedInt.HOOKED_BLOCK_Z.key, Integer.valueOf(rayTraceResult.getBlockPos().getZ()));
      }

   }

   private void onTargetHit(RayTraceResult rayTraceResult) {
      if (rayTraceResult != null) {
         if (this.getState() != HookShot.State.REELING || this.canHookOnReeling()) {
            Vect3d hitPosition;
            hitPosition = Vect3d.from(rayTraceResult.hitVec);
            label47:
            switch(rayTraceResult.typeOfHit) {
            case BLOCK:
               BlockPos pos = rayTraceResult.getBlockPos();
               HookShot.BlockAction action = this.getBlockAction(this.world, pos);
               if (action == HookShot.BlockAction.BOUNCE && this.getHook().canHookAnyBlock) {
                  action = HookShot.BlockAction.HOOK;
               }

               switch(action) {
               case HOOK:
                  this.hitSide = rayTraceResult.sideHit;
                  Vect3d offsetVec = Vect3d.from(this.hitSide.getDirectionVec()).scale(0.05D);
                  hitPosition = hitPosition.add(offsetVec);
                  this.setHookedBlockPos(rayTraceResult);
                  this.setHitPosition(hitPosition);
                  this.setState(HookShot.State.HOOKING_BLOCK);
                  break label47;
               case BREAK:
                  IBlockState state = this.world.getBlockState(pos);
                  this.world.destroyBlock(pos, state.getMaterial() != Material.GLASS);
               case BOUNCE:
                  this.setState(HookShot.State.REELING);
               case PASS:
               default:
                  return;
               }
            case ENTITY:
               Entity target = rayTraceResult.entityHit;
               if (target instanceof MultiPartEntityPart) {
                  IEntityMultiPart dragon = ((MultiPartEntityPart)target).parent;
                  if (dragon instanceof Entity) {
                     target = (Entity)dragon;
                  }
               }

               if (!this.canBeAttachedTo(target)) {
                  return;
               }

               Hook hook = this.getHookType().hook;
               if (hook != null && hook.getDamage() > 0.0F) {
                  DamageSource damageSource = (new EntityDamageSourceIndirect("arrow", this, this.getThrower())).setProjectile();
                  target.attackEntityFrom(damageSource, hook.getDamage());
               }

               this.dataManager.set(HookShot.WatchedInt.HOOKED_ENTITY_ID.key, Integer.valueOf(target.getEntityId()));
               this.setState(HookShot.State.HOOKING_ENTITY);
               break;
            default:
               return;
            }

            float ropeLength = (float)(this.getLauncherPosition().distance(hitPosition) + (double)this.getHookType().launcher.getReelingSpeed());
            this.setRopeLength(ropeLength);
            this.playSound(SoundEvents.ENTITY_ARROW_HIT, 1.0F, 1.2F / (this.rand.nextFloat() * 0.2F + 0.9F));
         }
      }
   }

   protected boolean canBeAttachedTo(Entity entity) {
      HookType type = this.getHookType();
      Hook hook = type.hook;
      if (entity != null && hook != null && this.getThrower() != null) {
         if (entity instanceof EntityPlayer) {
            if (hook.affectsPlayers) {
               return true;
            }
         } else if (hook.affectsNPCs) {
            float maxWeight = hook.maxEntityWeight;
            float targetWeight = ForgeEntity.getWeight(entity, false);
            return targetWeight <= maxWeight;
         }

         return false;
      } else {
         return false;
      }
   }

   protected boolean isSinglePlayer() {
      if (this.world instanceof WorldServer) {
         MinecraftServer server = ((WorldServer)this.world).getMinecraftServer();
         if (server != null) {
            return server.isSinglePlayer();
         }
      }

      return ForgeWorld.isClientSide(this.world) ? Minecraft.getMinecraft().isSingleplayer() : true;
   }

   protected void updatePhysics() {
      HookType hookType = this.getHookType();
      HookShot.State state = this.getState();
      EntityLivingBase shooter = this.getThrower();
      Entity hookedEntity = state == HookShot.State.HOOKING_ENTITY ? this.findHookedEntity() : null;
      if (shooter != null && !shooter.isDead) {
         if (shooter instanceof EntityPlayerMP) {
            EntityPlayerMP player = (EntityPlayerMP)shooter;
            if (Launcher.findAttachedLauncher(player.inventory, this) == null) {
               this.setDead();
               return;
            }
         }

         Vect3d launcherPosition = this.getLauncherPosition();
         Vect3d tensionVec = Vect3d.ZERO;
         float tensionForce = 0.0F;
         float hookDistance = this.getHookDistance();
         Vect3d hookPosition = this.getCurPosition();
         FloatBounds ropeLengthBounds = hookType.rope.getLengthBounds(shooter, hookedEntity);
         float ropeLength = ropeLengthBounds.enclose(Float.valueOf(this.getRopeLength())).floatValue();
         float reelingSpeed = hookType.launcher.getReelingSpeed();
         switch(state) {
         case SHOOTING:
            ropeLength = hookDistance + reelingSpeed;
            if (this.isServerSide() && ropeLength > ropeLengthBounds.getMax().floatValue()) {
               this.setState(HookShot.State.REELING);
               return;
            }
            break;
         case REELING:
            float reelingSpeedMult = 4.0F;
            if (this.isPulling()) {
               reelingSpeedMult *= 2.0F;
            }

            Vect3d motionVec = launcherPosition.subtract(hookPosition).resize((double)(reelingSpeed * reelingSpeedMult));
            this.setMotion(motionVec);
            float hookNextDistance = (float)launcherPosition.distance(this.getNextPosition());
            ropeLength = hookDistance;
            if (hookDistance <= ropeLengthBounds.getMin().floatValue() || hookDistance <= hookNextDistance) {
               this.setDead();
               return;
            }
            break;
         case HOOKING_BLOCK:
         case HOOKING_ENTITY:
            if (ropeLength < hookDistance) {
               tensionForce = hookType.rope.getTensionForce(ropeLength, hookDistance);
               tensionVec = hookPosition.subtract(launcherPosition).resize((double)tensionForce);
               float hookStrength = hookType.hook.getStrength();
               if (tensionForce > hookStrength) {
                  this.setState(HookShot.State.REELING);
                  return;
               }

               setFallDistance(shooter, 0.0F, true);
            }
         }

         switch(state) {
         case HOOKING_BLOCK:
            this.addMotionTo(shooter, tensionVec);
            break;
         case HOOKING_ENTITY:
            if (hookedEntity == null) {
               this.setState(HookShot.State.REELING);
               return;
            }

            float shooterWeightAdvantage = 2.0F;
            float shooterWeight = getWeight(shooter, false) * 2.0F;
            shooterWeight = shooterWeight + getWeight(shooter, true);
            float targetWeight = getWeight(hookedEntity, true);
            float totalWeight = shooterWeight + targetWeight;
            if (totalWeight == 0.0F) {
               totalWeight = 1.0F;
            }

            if (ropeLength <= hookDistance || tensionVec.notZero()) {
               setFallDistance(hookedEntity, 0.0F, true);
            }

            if (hookType == HookType.PUDGE && ropeLength <= ropeLengthBounds.getMin().floatValue()) {
               this.addMotionTo(hookedEntity, tensionVec);
               this.setDead();
               return;
            }

            this.addMotionTo(hookedEntity, tensionVec.invert().scale((double)(shooterWeight / totalWeight)));
            this.addMotionTo(shooter, tensionVec.scale((double)(targetWeight / totalWeight)));
         }

         ropeLength = ropeLengthBounds.enclose(Float.valueOf(ropeLength)).floatValue();
         if (this.isServerSide()) {
            this.setRopeLength(ropeLength);
            this.setSagging(ropeLength - hookDistance);
            this.setState(state);
            this.setTensionForce(tensionForce);
         }

      } else {
         this.setDead();
      }
   }

   private void setTensionForce(float tensionForce) {
      if (this.isServerSide()) {
         this.dataManager.set(HookShot.WatchedFloat.TENSION_FORCE.key, Float.valueOf(tensionForce));
      }

   }

   public float getHookDistance() {
      return (float)this.getLauncherPosition().distance(this.getCurPosition());
   }

   protected void updateControlling() {
      if (this.isServerSide()) {
         HookType hookType = this.getHookType();
         EntityLivingBase shooter = this.getThrower();
         if (this.isLauncherInMainHand() && hookType != null && hookType.launcher != null && shooter != null) {
            HookShot.State state = this.getState();
            float ropeLength = this.getRopeLength();
            float pullingSpeed = hookType.launcher.getReelingSpeed();
            switch(state) {
            case HOOKING_BLOCK:
            case HOOKING_ENTITY:
               if (shooter.isSneaking()) {
                  pullingSpeed *= 0.5F;
               }

               if (this.isPulling()) {
                  ropeLength -= pullingSpeed;
               }

               if (this.isLoosening() && hookType != HookType.PUDGE) {
                  ropeLength += pullingSpeed;
               }
            default:
               if (!((Boolean)this.dataManager.get(HookShot.WatchedBool.DROPPING.key)).booleanValue()) {
                  switch(state) {
                  case HOOKING_BLOCK:
                     if (shooter.isSprinting() && CommonMath.notZero((double)this.getTensionForce())) {
                        ropeLength += pullingSpeed;
                     }
                     break;
                  case HOOKING_ENTITY:
                     if (hookType == HookType.PUDGE && !this.isLoosening()) {
                        ropeLength -= pullingSpeed * 2.0F;
                     }
                  }

                  if (ropeLength < 0.0F) {
                     ropeLength = 0.0F;
                  }

                  this.setRopeLength(ropeLength);
               } else {
                  pullingSpeed = pullingSpeed * 4.0F;
                  ropeLength = ropeLength - pullingSpeed;
                  if (this.isPulling() && state == HookShot.State.HOOKING_BLOCK && (CommonMath.notZero((double)this.getTensionForce()) || ropeLength <= this.getHookDistance())) {
                     this.boostUpShooter();
                  }

                  this.setState(HookShot.State.REELING);
               }
            }
         }
      }
   }

   public Rope getRope() {
      HookType hookType = this.getHookType();
      return hookType.rope;
   }

   public Launcher getLauncher() {
      HookType hookType = this.getHookType();
      return hookType.launcher;
   }

   public Hook getHook() {
      HookType hookType = this.getHookType();
      return hookType.hook;
   }

   protected void addMotionTo(Entity entity, Vect3d motion) {
      EntityTarget<?> target = (EntityTarget)MotionController.getTarget(entity.getLowestRidingEntity()).orElse((Object)null);
      if (target != null) {
         target.addDampedMotion(motion);
         if (isClientSide(entity) && target instanceof SinglePlayerTarget) {
            ((SinglePlayerTarget)target).setSwinging(!entity.onGround);
         }
      }

   }

   public void readEntityFromNBT(NBTTagCompound nbt) {
      this.setDead();
   }

   public boolean canBeCollidedWith() {
      return false;
   }

   @Nullable
   public EntityLivingBase getThrower() {
      Entity entity = ForgeWorld.findEntity(this.world, ((Integer)this.dataManager.get(HookShot.WatchedInt.SHOOTER_ID.key)).intValue());
      return entity instanceof EntityLivingBase ? (EntityLivingBase)entity : null;
   }

   public void setThrower(Entity entity) {
      if (entity instanceof EntityLivingBase) {
         this.dataManager.set(HookShot.WatchedInt.SHOOTER_ID.key, Integer.valueOf(ForgeEntity.getId(entity)));
      }

   }

   public Block getHookedBlock() {
      return ForgeWorld.getBlock(this.world, this.getHookedBlockPos());
   }

   public boolean hookedBlockExists() {
      return this.getHookedBlock() != Blocks.AIR;
   }

   @Nullable
   public Entity findHookedEntity() {
      return findEntity(this.world, ((Integer)this.dataManager.get(HookShot.WatchedInt.HOOKED_ENTITY_ID.key)).intValue());
   }

   public boolean hookedEntityIsAlive() {
      Entity entity = this.findHookedEntity();
      return entity != null && entity.isEntityAlive();
   }

   public void onUpdate() {
      super.onUpdate();
      this.move();
      this.setPrevRotation(this.getCurRotation());
      this.updateControlling();
      this.updateCombustion();
      this.updatePhysics();
      this.updateCollisions();
      this.updateTargetState();
      this.updateMotion();
      this.updateRotation();
   }

   private void updateCombustion() {
      if (this.isServerSide()) {
         double fireResistance = (double)this.getHookType().hook.resistance;
         boolean fireInfluenced = false;
         switch(this.getState()) {
         case HOOKING_BLOCK:
            fireInfluenced = ForgeWorld.getBlock(this.world, this.getHookedBlockPos().offset(this.hitSide)) == Blocks.FIRE;
            break;
         case HOOKING_ENTITY:
            Entity entity = this.findHookedEntity();
            fireInfluenced = entity != null && entity.isBurning();
         }

         if (this.isBurning()) {
            this.combustion.fire();
         }

         if (this.combustion.getFireInfluencedTime().inSeconds() > fireResistance) {
            this.combustion.fire();
         }

         if (this.combustion.isBurning()) {
            this.setFire(1);
         }

         if (this.combustion.getBurningTime().inSeconds() > fireResistance) {
            this.setDead();
         }

         this.combustion.update(fireInfluenced);
      }

   }

   private void updateCollisions() {
      if (this.isServerSide()) {
         HookShot.State state = this.getState();
         if (!this.isHookingTarget()) {
            if (state == HookShot.State.REELING) {
               if (!this.canHookOnReeling()) {
                  return;
               }

               if (this.getHookType() == HookType.PUDGE && this.getPrevState() == HookShot.State.HOOKING_ENTITY) {
                  return;
               }
            }

            List<RayTraceResult> rayTraceResultsList = new ArrayList();
            if (this.world != null) {
               rayTraceResultsList.add(this.world.rayTraceBlocks(this.getPrevPosition().toVec3d(), this.getCurPosition().toVec3d(), false, true, false));
            }

            AxisAlignedBB aaBB = this.getBB().expand(this.motionX, this.motionY, this.motionZ);
            List<Entity> list = this.getEntitiesWithinAABB(aaBB);
            Entity shooter = this.getThrower();
            Entity shooterRide = shooter != null ? shooter.getRidingEntity() : null;

            for(Entity target : list) {
               if (target == shooter && state == HookShot.State.REELING) {
                  this.setDead();
                  return;
               }

               if (this.isServerSide() && target.canBeCollidedWith() && target != shooter && target != shooterRide) {
                  RayTraceResult rayTraceResult = getBB(target).calculateIntercept(this.getCurPosition().toVec3d(), this.getNextPosition().toVec3d());
                  if (rayTraceResult != null) {
                     rayTraceResultsList.add(new RayTraceResult(target, getCenterPosition(target).toVec3d()));
                  }
               }
            }

            RayTraceResult nearestResult = null;
            double minDistance = Double.MAX_VALUE;

            for(RayTraceResult rayTraceResult : rayTraceResultsList) {
               if (rayTraceResult != null) {
                  double distance = this.getCurPosition().distance(Vect3d.from(rayTraceResult.hitVec));
                  if (distance < minDistance) {
                     nearestResult = rayTraceResult;
                     minDistance = distance;
                  }
               }
            }

            if (nearestResult != null) {
               this.onTargetHit(nearestResult);
            }

         }
      }
   }

   private void updateTargetState() {
      if (this.isServerSide()) {
         switch(this.getState()) {
         case HOOKING_BLOCK:
            if (!this.hookedBlockExists()) {
               this.setState(HookShot.State.REELING);
            }
            break;
         case HOOKING_ENTITY:
            Entity entity = this.findHookedEntity();
            if (entity == null || entity.isDead || entity instanceof EntityLivingBase && ((EntityLivingBase)entity).isSwingInProgress && entity.isSneaking() || entity instanceof EntityPlayer && entity.isRiding() && ((EntityPlayer)entity).isSwingInProgress || hasSwingingPlayerPassenger(entity)) {
               this.setState(HookShot.State.REELING);
            }
         }
      }

   }

   public boolean isHookingTarget() {
      switch(this.getState()) {
      case HOOKING_BLOCK:
      case HOOKING_ENTITY:
         return true;
      default:
         return false;
      }
   }

   private void updateRotation() {
      if (this.isHookingTarget()) {
         this.setAllRotations(this.getCurRotation());
      } else {
         Vect3d motion = this.getMotion();
         if (motion.notZero()) {
            if (this.getState() == HookShot.State.REELING) {
               motion = motion.invert();
            }

            this.setCurRotation(motion.rotation());
         }
      }

   }

   private void updateMotion() {
      HookShot.State state = this.getState();
      switch(state) {
      case HOOKING_BLOCK:
         if (this.hookedBlockExists()) {
            this.setAllPositions(this.getHitPosition());
            this.stopMoving();
            this.onGround = true;
         }
         break;
      case HOOKING_ENTITY:
         if (!this.isRiding()) {
            Entity entity = this.findHookedEntity();
            if (entity != null) {
               this.stopMoving();
               this.startRiding(entity, true);
               if (this.getHookType() == HookType.PUDGE) {
                  entity.dismountRidingEntity();
               }
            }
         }

         this.onGround = true;
         break;
      default:
         if (this.isRiding()) {
            this.dismountRidingEntity();
         }

         this.onGround = false;
         double motionFactor = 0.9900000095367432D;
         if (this.isInWater()) {
            Vect3d bubblePos = this.getCurPosition().subtract(this.getMotion().scale(0.25D));

            for(int i = 0; i < 4; ++i) {
               ForgeWorld.spawnParticle(this.world, EnumParticleTypes.WATER_BUBBLE, bubblePos, this.getMotion());
            }

            motionFactor = 0.800000011920929D;
         }

         this.setMotion(this.getMotion().scale(motionFactor));
         this.motionY -= 0.004999999888241291D;
      }

   }

   public void shoot(double x, double y, double z, float velocity, float inaccuracy) {
      Vect3d vec = Vect3d.from(x, y, z).normalize().scale((double)velocity);
      Rotation rotation = new Rotation(vec.pitch(), vec.yaw());
      this.setMotion(vec);
      this.setAllRotations(rotation);
   }

   public void setDead() {
      if (this.getThrower() instanceof EntityPlayerMP) {
         InventoryPlayer inventory = ((EntityPlayerMP)this.getThrower()).inventory;
         ItemStack launcher = Launcher.findAttachedLauncher(inventory, this);
         if (launcher != null) {
            Launcher.unattach(launcher);
         }
      }

      super.setDead();
   }

   public float getTensionForce() {
      return ((Float)this.dataManager.get(HookShot.WatchedFloat.TENSION_FORCE.key)).floatValue();
   }

   @SideOnly(Side.CLIENT)
   public boolean isInRangeToRenderDist(double distance) {
      return distance < (double)(RENDER_DISTANCE * RENDER_DISTANCE);
   }

   @SideOnly(Side.CLIENT)
   public AxisAlignedBB getRenderBoundingBox() {
      return this.getBB().union(getBB(this.getThrower()));
   }

   protected void boostUpShooter() {
      EntityLivingBase shooter = this.getThrower();
      Launcher launcher = this.getLauncher();
      if (shooter instanceof EntityPlayerMP && launcher != null) {
         float strength = launcher.getBoostStrength();
         (new BoostUpMsg(strength)).sendTo((EntityPlayerMP)shooter);
         setFallDistance(shooter, 0.0F, true);
      }

   }

   /** @deprecated */
   @Deprecated
   protected Vect3d getPlayerMovement() {
      Entity shooter = this.getThrower();
      if (!(shooter instanceof EntityPlayer)) {
         return Vect3d.ZERO;
      } else {
         Vect3d direction = Vect3d.ZERO;
         EntityPlayer player = (EntityPlayer)shooter;
         EntityDataManager value = this.getDataManager();
         if (((Boolean)value.get(HookShot.WatchedBool.MOVING_FORWARD.key)).booleanValue()) {
            direction = direction.move(0.0D, 0.0D, 1.0D);
         }

         if (((Boolean)value.get(HookShot.WatchedBool.MOVING_BACKWARD.key)).booleanValue()) {
            direction = direction.move(0.0D, 0.0D, -1.0D);
         }

         if (((Boolean)value.get(HookShot.WatchedBool.MOVING_LEFT.key)).booleanValue()) {
            direction = direction.move(1.0D, 0.0D, 0.0D);
         }

         if (((Boolean)value.get(HookShot.WatchedBool.MOVING_RIGHT.key)).booleanValue()) {
            direction = direction.move(-1.0D, 0.0D, 0.0D);
         }

         if (direction.notZero()) {
            direction = direction.rotateAroundY(-player.rotationYaw).normalize();
         }

         double strength = 0.03D;
         if (player.isSprinting()) {
            strength *= 2.0D;
         }

         if (!player.onGround) {
            strength = 0.015D + ForgeEntity.getMotion(player).length() * 0.01D;
         }

         Vect3d motion = direction.resize(strength);
         if (((Boolean)value.get(HookShot.WatchedBool.JUMPING.key)).booleanValue() && (player.isInWater() || player.isInLava())) {
            motion = motion.move(0.0D, 0.12D, 0.0D);
         }

         return motion;
      }
   }

   static enum BlockAction {
      BOUNCE,
      BREAK,
      HOOK,
      PASS;

      private BlockAction() {
      }
   }

   public static enum State {
      SHOOTING,
      REELING,
      HOOKING_BLOCK,
      HOOKING_ENTITY;

      private State() {
      }
   }

   public static enum WatchedBool {
      PULLING,
      LOOSENING,
      DROPPING,
      MOVING_FORWARD,
      MOVING_BACKWARD,
      MOVING_LEFT,
      MOVING_RIGHT,
      JUMPING;

      public final DataParameter<Boolean> key = EntityDataManager.createKey(HookShot.class, DataSerializers.BOOLEAN);

      private WatchedBool() {
      }
   }

   public static enum WatchedFloat {
      HIT_X,
      HIT_Y,
      HIT_Z,
      ROPE_LENGTH,
      SAGGING,
      TENSION_FORCE;

      public final DataParameter<Float> key = EntityDataManager.createKey(HookShot.class, DataSerializers.FLOAT);

      private WatchedFloat() {
      }
   }

   public static enum WatchedInt {
      HOOKED_BLOCK_X,
      HOOKED_BLOCK_Y,
      HOOKED_BLOCK_Z,
      HOOKED_ENTITY_ID,
      HOOK_TYPE,
      PREV_STATE,
      SHOOTER_ID,
      STATE;

      public final DataParameter<Integer> key = EntityDataManager.createKey(HookShot.class, DataSerializers.VARINT);

      private WatchedInt() {
      }
   }
}
