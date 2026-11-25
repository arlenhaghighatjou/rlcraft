package com.dhanantry.scapeandrunparasites.entity.ai.misc;

import com.dhanantry.scapeandrunparasites.block.IMetaName;
import com.dhanantry.scapeandrunparasites.client.particle.ParticleSpawner;
import com.dhanantry.scapeandrunparasites.client.particle.SRPEnumParticle;
import com.dhanantry.scapeandrunparasites.entity.EntityToxicCloud;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAINearestAttackableTargetStatus;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIParasiteFollow;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIWanderStatus;
import com.dhanantry.scapeandrunparasites.entity.projectile.EntityGore;
import com.dhanantry.scapeandrunparasites.init.SRPBlocks;
import com.dhanantry.scapeandrunparasites.init.SRPPotions;
import com.dhanantry.scapeandrunparasites.init.SRPSounds;
import com.dhanantry.scapeandrunparasites.item.tool.WeaponToolMeleeBase;
import com.dhanantry.scapeandrunparasites.item.tool.WeaponToolRangeBase;
import com.dhanantry.scapeandrunparasites.util.ParasiteEventEntity;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfig;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfigSystems;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfigWorld;
import com.dhanantry.scapeandrunparasites.world.SRPWorldData;
import com.dhanantry.scapeandrunparasites.world.biome.BiomeParasite;
import com.google.common.base.Predicate;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEndGateway;
import net.minecraft.block.BlockEndPortalFrame;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.BlockPortal;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemShield;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.pathfinding.PathNavigateFlying;
import net.minecraft.pathfinding.PathNavigateGround;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.stats.StatList;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.fml.common.eventhandler.Event.Result;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public abstract class EntityParasiteBase extends EntityMob {
   protected double killcount = 0.0D;
   protected boolean canD = true;
   private int waitInt;
   private boolean canWorkTask;
   private EntityParasiteBase owner;
   private static final DataParameter<Byte> SPECIAL = EntityDataManager.createKey(EntityParasiteBase.class, DataSerializers.BYTE);
   private static final DataParameter<Integer> SELFE = EntityDataManager.createKey(EntityParasiteBase.class, DataSerializers.VARINT);
   private static final DataParameter<Byte> SKIN = EntityDataManager.createKey(EntityParasiteBase.class, DataSerializers.BYTE);
   private static final DataParameter<Boolean> COLD_L = EntityDataManager.createKey(EntityParasiteBase.class, DataSerializers.BOOLEAN);
   protected int srpTicks;
   protected int lastActiveTime;
   protected int timeSinceIgnited;
   protected int fuseTime = 40;
   protected byte canModRender = 0;
   protected int attackCooldown;
   private byte madeRng = -1;
   private boolean spawnedByColo;
   protected byte type = 0;
   protected int damageCap = 1;
   protected float MiniDamage = 0.0F;
   protected byte phaseCreated;
   protected EntityParasiteBase.EntityAIJumping jumpT = new EntityParasiteBase.EntityAIJumping(this);
   protected EntityParasiteBase.EntityAIWait wait = new EntityParasiteBase.EntityAIWait();
   protected EntityAIParasiteFollow folow = new EntityAIParasiteFollow(this, 1.3D, 16.0D, 6.0D, true);
   protected EntityAIWanderStatus aiWander = new EntityAIWanderStatus(this, 1.0D, 0.001F, true);
   protected int oneMindDeathValue;
   protected float foodSteal;
   private float aniticks;
   private boolean still;
   private int stillTicks;
   private int canBeStored;
   private int dodFatherID = 0;
   public boolean canSpawnSpawn;
   protected int valueEvDeath;
   protected int liquidLeap;
   protected float blockH;
   protected int BGheight;
   protected int BGrange;
   protected boolean SkillBGflag;
   private ArrayList<String> inbBlockName = new ArrayList();
   private ArrayList<Integer> inbBlockNumber = new ArrayList();
   protected int attacking;
   protected double targetX;
   protected double targetZ;
   protected float leapMotionY;
   protected double jumpSpeed;
   protected int jumpR;
   protected boolean SkillLeapFlag;

   public EntityParasiteBase(World worldIn) {
      super(worldIn);
      this.tasks.addTask(0, this.wait);
      this.tasks.addTask(5, this.aiWander);
      this.tasks.addTask(6, this.folow);
      this.tasks.addTask(5, this.jumpT);
      this.canBeStored = 0;
      this.attackCooldown = 0;
      this.canWorkTask = true;
      this.srpTicks = 0;
      this.madeRng = -1;
      this.foodSteal = 0.001F;
      this.spawnedByColo = false;
      this.SkillBGflag = false;
      this.SkillLeapFlag = false;
      this.oneMindDeathValue = 1;
      this.canSpawnSpawn = false;
      this.still = false;
   }

   public void setCreatedPhase(byte in) {
      this.phaseCreated = in;
   }

   public abstract int getParasiteIDRegister();

   public void applyBonuses(int colony, int node, byte phase) {
      if (!this.spawnedByColo) {
         this.phaseCreated = phase;
         if (this.phaseCreated >= SRPConfigSystems.evolutionAssimilatedDehiding) {
            this.targetTasks.addTask(5, new EntityAINearestAttackableTargetStatus(this, EntityVillager.class, true));
         }

         if (this.phaseCreated >= SRPConfigSystems.evolutionTotalKill) {
            this.targetTasks.addTask(4, new EntityAINearestAttackableTargetStatus(this, EntityLivingBase.class, 0, !SRPConfigSystems.useOneMind, !SRPConfigSystems.useOneMind, new Predicate<EntityLivingBase>() {
               public boolean apply(@Nullable EntityLivingBase entity) {
                  return !(entity instanceof EntityPlayer) && !ParasiteEventEntity.checkEntity(entity, SRPConfig.mobattackingBlackList, SRPConfig.mobattackingBlackListWhite);
               }
            }, SRPConfig.adaptedSneakPen, SRPConfig.adaptedInviPen));
         }

         this.setColonyBonus(colony);
         this.setNodeBonus(node);
         this.spawnedByColo = true;
      }
   }

   protected void setColonyBonus(int totalColonyPoints) {
      if (totalColonyPoints != 0) {
         double bonus = (double)((float)totalColonyPoints / SRPConfigWorld.colonyExtraHealthPoint * SRPConfigWorld.colonyExtraHealthValue);
         double currentV = this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).getBaseValue();
         this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(currentV + currentV * bonus);
         this.setHealth((float)this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).getBaseValue());
         bonus = (double)((float)totalColonyPoints / SRPConfigWorld.colonyExtraArmorPoint * SRPConfigWorld.colonyExtraArmorValue);
         currentV = this.getEntityAttribute(SharedMonsterAttributes.ARMOR).getBaseValue();
         this.getEntityAttribute(SharedMonsterAttributes.ARMOR).setBaseValue(currentV + currentV * bonus);
         bonus = (double)((float)totalColonyPoints / SRPConfigWorld.colonyExtraDamagePoint * SRPConfigWorld.colonyExtraDamageValue);
         currentV = this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getBaseValue();
         this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(currentV + currentV * bonus);
         bonus = (double)((float)totalColonyPoints / SRPConfigWorld.colonyExtraKDResPoint * SRPConfigWorld.colonyExtraKDResValue);
         currentV = this.getEntityAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).getBaseValue();
         this.getEntityAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).setBaseValue(currentV + currentV * bonus);
         bonus = (double)((float)totalColonyPoints / SRPConfigWorld.colonyDamageCapPoint * SRPConfigWorld.colonyDamageCapValue);
         this.damageCap = (int)((double)this.damageCap + (double)this.damageCap * bonus);
      }
   }

   protected void setNodeBonus(int totalPoints) {
      if (totalPoints != 0) {
         String[] here = new String[3];

         for(String i : SRPConfigWorld.potionEffectForNodes) {
            here = i.split(";");
            int level = Integer.parseInt(here[0]);
            if (totalPoints >= level) {
               int amp = Integer.parseInt(here[2]);
               Potion potion = Potion.getPotionFromResourceLocation(here[1]);
               if (potion != null) {
                  this.addPotionEffect(new PotionEffect(potion, 7777, amp));
               }
            }
         }

      }
   }

   protected void entityInit() {
      super.entityInit();
      this.dataManager.register(SPECIAL, Byte.valueOf((byte)0));
      this.dataManager.register(SELFE, Integer.valueOf(-1));
      this.dataManager.register(SKIN, Byte.valueOf((byte)0));
      this.dataManager.register(COLD_L, Boolean.valueOf(false));
   }

   public void cannotDespawn(boolean in) {
      this.canD = in;
   }

   public boolean canDespawn() {
      return this.canD;
   }

   public void setApplyColonyB(boolean in) {
      this.spawnedByColo = in;
   }

   public boolean getApplyColonyB() {
      return this.spawnedByColo;
   }

   public void onLivingUpdate() {
      ++this.srpTicks;
      if (!this.isAIDisabled()) {
         if (this.getWait() > 0) {
            int j1 = this.getWait() - 1;
            if (j1 == 0) {
               this.setParasiteStatus(0);
            }

            this.setWait(j1);
         } else {
            super.onLivingUpdate();
         }

         if (!this.world.isRemote) {
            int k = this.getParasiteStatus();
            if (this.getAttackCooldownAni() != 0 || k == 1 || k == 2) {
               if (this.getAttackCooldownAni() != 0) {
                  int i = this.getAttackCooldownAni() - 1;
                  this.setAttackCooldownAni(i);
               }

               if (k == 1 || k == 2) {
                  if (this.getAttackTarget() != null) {
                     if (!this.getAttackTarget().isEntityAlive()) {
                        this.setAttackTarget((EntityLivingBase)null);
                        this.setParasiteStatus(0);
                     }
                  } else {
                     this.setParasiteStatus(0);
                     this.setAttackTarget((EntityLivingBase)null);
                  }
               }
            }

            if (this.srpTicks % 15 == 0 && this.world.getBlockState(this.getPosition()).getBlock() instanceof BlockLiquid && this.getAttackTarget() != null) {
               ++this.liquidLeap;
               if (this.liquidLeap > 4) {
                  this.liquidLeap = 4;
               }
            }

            if (this.srpTicks > 20) {
               this.srpTicks = 0;
               if (!this.inbBlockName.isEmpty() && this.rand.nextInt(50) == 0) {
                  this.spawnCyst();
               }

               if (this.getAttackTarget() instanceof EntityPlayer) {
                  this.fearPlayer(this.getAttackTarget());
               }

               if (SRPConfigSystems.useEvolution && this.killcount >= 0.0D) {
                  switch(this.phaseCreated) {
                  case 1:
                     this.killcount += SRPConfigSystems.phaseKillCountPlusOne;
                     break;
                  case 2:
                     this.killcount += SRPConfigSystems.phaseKillCountPlusTwo;
                     break;
                  case 3:
                     this.killcount += SRPConfigSystems.phaseKillCountPlusThree;
                     break;
                  case 4:
                     this.killcount += SRPConfigSystems.phaseKillCountPlusFour;
                     break;
                  case 5:
                     this.killcount += SRPConfigSystems.phaseKillCountPlusFive;
                     break;
                  case 6:
                     this.killcount += SRPConfigSystems.phaseKillCountPlusSix;
                     break;
                  case 7:
                     this.killcount += SRPConfigSystems.phaseKillCountPlusSeven;
                     break;
                  case 8:
                     this.killcount += SRPConfigSystems.phaseKillCountPlusEight;
                  }
               } else if (this.world.getDifficulty() == EnumDifficulty.HARD && this.killcount >= 0.0D) {
                  this.killcount += SRPConfig.killcountplus;
               }

               this.placeNidus();
            }

            if (this.liquidLeap >= 1) {
               double h = 0.1D;
               double str = 0.5D;
               this.getNavigator().clearPath();
               EntityLivingBase entitylivingbase = this.getAttackTarget();
               if (entitylivingbase != null) {
                  Block bl = this.world.getBlockState(this.getPosition()).getBlock();
                  if (!bl.isPassable(this.world, this.getPosition())) {
                     h = 0.3D;
                     str = 1.0D;
                  }

                  --this.liquidLeap;
                  double dd0 = entitylivingbase.posX - this.posX;
                  double dd1 = entitylivingbase.posZ - this.posZ;
                  float f = MathHelper.sqrt(dd0 * dd0 + dd1 * dd1);
                  this.motionX += dd0 / (double)f * str * 0.800000011920929D + this.motionX * 0.20000000298023224D;
                  this.motionZ += dd1 / (double)f * str * 0.800000011920929D + this.motionZ * 0.20000000298023224D;
                  this.motionY = h;
                  this.getLookHelper().setLookPosition(entitylivingbase.posX, entitylivingbase.posY + (double)entitylivingbase.getEyeHeight(), entitylivingbase.posZ, 10.0F, 40.0F);
               }
            }
         } else if (this.srpTicks > 20) {
            this.srpTicks = 0;
         }

         if (this.posX == this.prevPosX && this.posZ == this.prevPosZ) {
            ++this.stillTicks;
         } else {
            this.canBeStored = 55;
            this.stillTicks = 0;
            this.still = false;
         }

         if (this.stillTicks > 25) {
            this.still = true;
         }

         if (this.still) {
            this.aniticks += 0.0012F;
         } else {
            this.aniticks = 0.0F;
         }

         if (this.canBeStored > 0) {
            --this.canBeStored;
         }

      }
   }

   protected void fearPlayer(EntityLivingBase player) {
   }

   protected void placeNidus() {
      if (!SRPConfigSystems.useEvolution || this.phaseCreated > 0) {
         if (this.world.rand.nextInt(350) == 0 && this.type >= 11) {
            if (this.killcount >= 10.0D) {
               AxisAlignedBB axisalignedbb = (new AxisAlignedBB(this.posX, this.posY, this.posZ, this.posX + 1.0D, this.posY + 1.0D, this.posZ + 1.0D)).grow(SRPConfig.nexussivFollow);
               List<EntityParasiteBase> moblist = this.world.getEntitiesWithinAABB(EntityParasiteBase.class, axisalignedbb);
               int collec = 0;

               for(EntityParasiteBase mob : moblist) {
                  int idP = mob.getParasiteIDRegister();
                  if (idP == 73 || idP == 77 || idP == 78 || idP == 79) {
                     return;
                  }

                  collec = (int)((double)collec + mob.getKillC());
               }

               if (collec >= SRPConfig.reinforcerNidusStart) {
                  double d0 = (double)((float)this.posX + this.world.rand.nextFloat());
                  double d1 = (double)((float)this.posY + this.world.rand.nextFloat());
                  double d2 = (double)((float)this.posZ + this.world.rand.nextFloat());
                  double d3 = d0 - this.posX;
                  double d4 = d1 - this.posY;
                  double d5 = d2 - this.posZ;
                  double d6 = (double)MathHelper.sqrt(d3 * d3 + d4 * d4 + d5 * d5);
                  d3 = d3 / d6;
                  d4 = d4 / d6;
                  d5 = d5 / d6;
                  double d7 = 0.5D / (d6 / 4.0D + 0.1D);
                  d7 = d7 * (double)(this.world.rand.nextFloat() * this.world.rand.nextFloat() + 0.3F);
                  d3 = d3 * d7;
                  d4 = d4 * d7 * 2.0D;
                  d5 = d5 * d7;
                  EntityGore bomb = new EntityGore(this.world);
                  bomb.setType((byte)11);
                  bomb.copyLocationAndAnglesFrom(this);
                  bomb.setMotion(d3, d4, d5, 0.1D, 0.5D);
                  this.world.spawnEntity(bomb);
               }
            }
         }
      }
   }

   public void setDodFatherID(int in) {
      this.dodFatherID = in;
   }

   protected void updateAITasks() {
      super.updateAITasks();
      if (this.getWait() > 0) {
         int j1 = this.getWait() - 1;
         if (j1 == 0) {
            this.setParasiteStatus(0);
         }

         this.setWait(j1);
      }

   }

   public boolean attackEntityFrom(DamageSource source, float amount) {
      if (this.world.isRemote) {
         return false;
      } else {
         this.canBeStored = 55;
         if (this.madeRng == -1) {
            this.madeRng = (byte)this.rand.nextInt(2);
            if (this.madeRng == 0) {
               this.world.setEntityState(this, (byte)40);
            }
         }

         if (source == DamageSource.OUT_OF_WORLD) {
            return super.attackEntityFrom(source, amount);
         } else {
            if (source == DamageSource.IN_WALL) {
               this.skillBreakBlocks();
            }

            if (source == DamageSource.IN_FIRE || source == DamageSource.ON_FIRE) {
               if (this.isImmuneToFire) {
                  return false;
               }

               super.attackEntityFrom(source, amount * SRPConfig.firemultyplier);
            }

            for(int i = 0; i < SRPConfig.parasiteWeakTo.length; ++i) {
               if (SRPConfig.parasiteWeakTo[i] != null) {
                  String[] here = SRPConfig.parasiteWeakTo[i].split(";");
                  if (here[0].equals(source.getDamageType())) {
                     amount *= Float.parseFloat(here[1]);
                  }
               }
            }

            if (this.damageCap > 1) {
               if (ParasiteEventEntity.checkName(source.getDamageType(), SRPConfig.damageCapBlackList, SRPConfig.damageCapBlackListWhite)) {
                  return super.attackEntityFrom(source, amount);
               } else {
                  float damage = this.getMaxHealth() / (float)this.damageCap + this.getMaxHealth() % (float)this.damageCap * 0.5F;
                  if (amount >= damage) {
                     source.setDamageBypassesArmor();
                     this.addPotionEffect(new PotionEffect(SRPPotions.RAGE_E, 200, 1, false, false));
                  }

                  return super.attackEntityFrom(source, Math.min(amount, damage));
               }
            } else {
               if (SRPConfigSystems.useOneMind && source.getImmediateSource() instanceof EntityLivingBase && this.getAttackTarget() == source.getImmediateSource()) {
                  ParasiteEventEntity.alertOthers(this, this.getAttackTarget(), this.world, 3);
               }

               return super.attackEntityFrom(source, amount);
            }
         }
      }
   }

   public boolean attackEntityAsMob(Entity entityIn) {
      if (this.world.isRemote) {
         return false;
      } else {
         this.canBeStored = 55;
         boolean green = entityIn instanceof EntityLivingBase;
         if (green) {
            this.attackEntityAsMobMinimum((EntityLivingBase)entityIn);
         }

         boolean flag = super.attackEntityAsMob(entityIn);
         if (flag && green) {
            this.setAttackCooldownAni(100);
            if (SRPConfigSystems.useOneMind) {
               ParasiteEventEntity.alertOthers(this, (EntityLivingBase)entityIn, this.world, 3);
            }
         }

         return flag;
      }
   }

   public boolean attackEntityAsMobMinimum(EntityLivingBase target) {
      if (this.MiniDamage == 0.0F) {
         return false;
      } else {
         float f1 = target.getHealth();
         if (f1 <= 0.0F) {
            return false;
         } else {
            if (target instanceof EntityPlayer) {
               EntityPlayer entityplayer = (EntityPlayer)target;
               if (entityplayer.capabilities.isCreativeMode) {
                  return false;
               }

               entityplayer.addExhaustion(this.foodSteal);
               ItemStack itemstack1 = entityplayer.isHandActive() ? entityplayer.getActiveItemStack() : ItemStack.EMPTY;
               if (itemstack1.getItem() instanceof ItemShield && entityplayer.isActiveItemStackBlocking() && !entityplayer.getCooldownTracker().hasCooldown(itemstack1.getItem())) {
                  float f12 = 0.25F + (float)EnchantmentHelper.getEfficiencyModifier(this) * 0.05F;
                  if (this.rand.nextFloat() < f12 && this.type >= 31) {
                     entityplayer.getCooldownTracker().setCooldown(itemstack1.getItem(), 100);
                     this.world.setEntityState(entityplayer, (byte)30);
                  }

                  this.damageShield(this.MiniDamage);
                  return true;
               }
            }

            DamageSource s = DamageSource.causeMobDamage(this);
            float damage = 0.0F;
            if (target.isPotionActive(SRPPotions.VIRA_E)) {
               damage = this.MiniDamage * (float)(target.getActivePotionEffect(SRPPotions.VIRA_E).getAmplifier() + 1);
            }

            damage = damage + this.MiniDamage;

            try {
               if (target.getCombatTracker() != null && s != null) {
                  target.getCombatTracker().trackDamage(s, f1, damage);
               }
            } catch (Exception var11) {
               ;
            }

            if (target.getAbsorptionAmount() > 0.0F) {
               target.setHealth(f1 - damage / 2.0F);
               target.setAbsorptionAmount(target.getAbsorptionAmount() - damage / 2.0F);
            } else {
               target.setHealth(f1 - damage);
            }

            this.world.setEntityState(target, (byte)2);
            if (target.getHealth() <= 0.0F) {
               ItemStack itemstack = null;

               for(EnumHand enumhand : EnumHand.values()) {
                  ItemStack itemstack1 = target.getHeldItem(enumhand);
                  if (itemstack1.getItem() == Items.TOTEM_OF_UNDYING) {
                     itemstack = itemstack1.copy();
                     itemstack1.shrink(1);
                     break;
                  }
               }

               if (itemstack != null) {
                  if (target instanceof EntityPlayerMP) {
                     EntityPlayerMP entityplayermp = (EntityPlayerMP)target;
                     entityplayermp.addStat(StatList.getObjectUseStats(Items.TOTEM_OF_UNDYING));
                     CriteriaTriggers.USED_TOTEM.trigger(entityplayermp, itemstack);
                  }

                  target.setHealth(1.0F);
                  target.clearActivePotions();
                  target.addPotionEffect(new PotionEffect(MobEffects.REGENERATION, 900, 1));
                  target.addPotionEffect(new PotionEffect(MobEffects.ABSORPTION, 100, 1));
                  target.world.setEntityState(target, (byte)35);
               } else {
                  target.onDeath(s);
                  ++this.killcount;
               }
            }

            return true;
         }
      }
   }

   public void onKillEntity(EntityLivingBase entityLivingIn) {
      if (!this.world.isRemote) {
         if (this.killcount >= 0.0D) {
            ++this.killcount;
         }

         if (SRPConfigSystems.useEvolution) {
            SRPWorldData data = SRPWorldData.get(this.world);
            data.setTotalKills(SRPConfigSystems.valueKill, true, this.world, true);
         }

         if (entityLivingIn.isPotionActive(SRPPotions.COTH_E) && this.rand.nextDouble() < (double)SRPConfigSystems.cothConvert) {
            ParasiteEventEntity.convertEntity(entityLivingIn, entityLivingIn.getEntityData(), true, SRPConfigSystems.COTHVictimParasite);
         }

         this.addPotionEffect(new PotionEffect(MobEffects.REGENERATION, 80, 0, false, false));
         this.setWait(10);
      }
   }

   public boolean isPotionApplicable(PotionEffect potioneffectIn) {
      Potion potion = potioneffectIn.getPotion();
      return potion != MobEffects.POISON && potion != SRPPotions.COTH_E && potion != SRPPotions.VIRA_E && potion != SRPPotions.CORRO_E && potion != SRPPotions.DLER_E ? super.isPotionApplicable(potioneffectIn) : false;
   }

   protected boolean canBeRidden(Entity entityIn) {
      return !(entityIn instanceof EntityBoat) && !(entityIn instanceof EntityMinecart) ? super.canBeRidden(entityIn) : false;
   }

   public boolean canAttackClass(Class<? extends EntityLivingBase> cls) {
      if (cls != EntityPlayer.class && cls != EntityPlayerMP.class) {
         String name = null;

         try {
            name = EntityList.getKey(cls).toString();
         } catch (Exception var4) {
            return true;
         }

         if (name == null) {
            return true;
         } else if (name.contains("srparasites")) {
            return false;
         } else {
            return !SRPConfig.mobAttackingFull || !ParasiteEventEntity.checkName(name, SRPConfig.mobattackingBlackList, SRPConfig.mobattackingBlackListWhite);
         }
      } else {
         return true;
      }
   }

   public boolean isCreatureType(EnumCreatureType type, boolean forSpawnCount) {
      if (forSpawnCount) {
         return false;
      } else {
         return this instanceof EntityLiving && this.isNoDespawnRequired() ? false : type.getCreatureClass().isAssignableFrom(this.getClass());
      }
   }

   public int canBeStored() {
      return this.canBeStored;
   }

   public int getSelfeState() {
      return ((Integer)this.dataManager.get(SELFE)).intValue();
   }

   public void setSelfeState(int state) {
      this.dataManager.set(SELFE, Integer.valueOf(state));
   }

   public float getMiniDamage() {
      return this.MiniDamage;
   }

   public int getLLeap() {
      return this.liquidLeap;
   }

   public void setWorkTask(boolean in) {
      this.canWorkTask = in;
   }

   public boolean shouldWorkTask() {
      return this.canWorkTask;
   }

   public void setParasiteToFollow(@Nullable EntityParasiteBase in) {
      this.owner = in;
   }

   public EntityParasiteBase getParasiteFollowing() {
      if (this.owner != null) {
         if (this.owner.isEntityAlive()) {
            return this.owner;
         }

         this.setParasiteToFollow((EntityParasiteBase)null);
      }

      return null;
   }

   public byte getParasiteStatus() {
      return ((Byte)this.dataManager.get(SPECIAL)).byteValue();
   }

   public void setParasiteStatus(int state) {
      this.dataManager.set(SPECIAL, Byte.valueOf((byte)state));
   }

   public void setWait(int in) {
      this.waitInt = in;
   }

   public int getWait() {
      return this.waitInt;
   }

   public void resetIdleTime() {
      this.idleTime = 0;
   }

   public byte getPhaseCreated() {
      return this.phaseCreated;
   }

   public int getSkin() {
      return ((Byte)this.dataManager.get(SKIN)).intValue();
   }

   public boolean getColdL() {
      return ((Boolean)this.dataManager.get(COLD_L)).booleanValue();
   }

   public void setSkin(int texture) {
      this.dataManager.set(SKIN, Byte.valueOf((byte)texture));
   }

   public int getCCDeathValue() {
      return this.oneMindDeathValue;
   }

   public void setAttackCooldownAni(int i) {
      this.attackCooldown = i;
   }

   public int getAttackCooldownAni() {
      return this.attackCooldown;
   }

   public void setKillC(double i) {
      this.killcount = i;
   }

   public double getKillC() {
      return this.killcount;
   }

   public byte getParasiteType() {
      return this.type;
   }

   public void onDeath(DamageSource cause) {
      super.onDeath(cause);
      if (!this.world.isRemote) {
         this.spawnCyst();
         ParasiteEventEntity.spawnBeckon(this.world, cause, this);
         ParasiteEventEntity.leaveScent(this.world, cause, this);
         if (SRPConfigSystems.useEvolution && !this.isPotionActive(SRPPotions.DEBAR_E)) {
            SRPWorldData data = SRPWorldData.get(this.world);
            data.setTotalKills(-this.valueEvDeath, true, this.world, false);
         }

         if (cause.getTrueSource() != null && cause.getTrueSource() instanceof EntityPlayer) {
            this.checkWeapon((EntityPlayer)cause.getTrueSource());
         }
      }

   }

   private void checkWeapon(EntityPlayer playerIn) {
      if (SRPConfigSystems.useOneMind) {
         Item ga = playerIn.getItemStackFromSlot(EntityEquipmentSlot.MAINHAND).getItem();
         if ((ga instanceof WeaponToolMeleeBase || ga instanceof WeaponToolRangeBase) && this.rand.nextInt(10) == 0) {
            playerIn.addPotionEffect(new PotionEffect(SRPPotions.PREY_E, 600, 0, false, false));
         }

      }
   }

   protected void onDeathUpdate() {
      if (this.canModRender == 1) {
         if (this.madeRng == 0) {
            this.setSelfeState(1);
            this.setParasiteStatus(6);
            this.dyingBurst(true, 1);
         } else {
            this.onDeathUpdateOG();
         }
      } else if (this.canModRender == 2) {
         if (this.madeRng == 0) {
            this.selfExplode();
            this.OnDeathHelper();
         } else {
            this.onDeathUpdateOG();
         }
      } else {
         this.onDeathUpdateOG();
      }

   }

   protected void onDeathUpdateOG() {
      ++this.deathTime;
      if (this.deathTime == 20) {
         if (!this.world.isRemote && (this.isPlayer() || this.recentlyHit > 0 && this.canDropLoot() && this.world.getGameRules().getBoolean("doMobLoot"))) {
            int i = this.getExperiencePoints(this.attackingPlayer);
            i = ForgeEventFactory.getExperienceDrop(this, this.attackingPlayer, i);

            while(i > 0) {
               int j = EntityXPOrb.getXPSplit(i);
               i -= j;
               this.world.spawnEntity(new EntityXPOrb(this.world, this.posX, this.posY, this.posZ, j));
            }
         }

         this.setDead();
         if (this.world.isRemote) {
            for(int k = 0; k < 20; ++k) {
               this.spawnParticles(SRPEnumParticle.GCLOUD, 127, 0, 0);
            }
         }
      }

   }

   protected void dyingBurst(boolean fromDeath, int value) {
      int i = this.getSelfeState();
      if (i > 0 && this.timeSinceIgnited == 0) {
         ;
      }

      this.timeSinceIgnited += i * value;
      if (this.timeSinceIgnited < 0) {
         this.timeSinceIgnited = 0;
      }

      if (this.timeSinceIgnited >= this.fuseTime) {
         this.timeSinceIgnited = this.fuseTime;
         this.selfExplode();
         if (fromDeath) {
            this.OnDeathHelper();
         }
      }

   }

   public void OnDeathHelper() {
      if (!this.world.isRemote && (this.isPlayer() || this.recentlyHit > 0 && this.canDropLoot() && this.world.getGameRules().getBoolean("doMobLoot"))) {
         int i = this.getExperiencePoints(this.attackingPlayer);
         i = ForgeEventFactory.getExperienceDrop(this, this.attackingPlayer, i);

         while(i > 0) {
            int j = EntityXPOrb.getXPSplit(i);
            i -= j;
            this.world.spawnEntity(new EntityXPOrb(this.world, this.posX, this.posY, this.posZ, j));
         }
      }

   }

   protected void selfExplode() {
      if (this.world.isRemote) {
         this.spawnEffectsGore();
      }

      if (!this.world.isRemote) {
         this.spawnGore();
         this.playSound(SRPSounds.MOBEXPLOTION, 1.0F, 1.0F);
         this.dead = true;
         this.setDead();
         EntityToxicCloud entityareaeffectcloud = new EntityToxicCloud(this.world, this.posX, this.posY, this.posZ);
         entityareaeffectcloud.setRadius(this.width, this.height);
         entityareaeffectcloud.setWaitTime(10);
         entityareaeffectcloud.setDuration(entityareaeffectcloud.getDuration() / 2);
         entityareaeffectcloud.setRadiusPerTick(-entityareaeffectcloud.getRadius() / (float)entityareaeffectcloud.getDuration());
         entityareaeffectcloud.addEffect(new PotionEffect(MobEffects.POISON, 300, 0));
         entityareaeffectcloud.addEffect(new PotionEffect(SRPPotions.COTH_E, 3600, 0, false, false));
         this.world.spawnEntity(entityareaeffectcloud);
      }

   }

   protected void spawnGore() {
   }

   protected void collideWithEntity(Entity entityIn) {
      super.collideWithEntity(entityIn);
   }

   public boolean getCanSpawnHere() {
      if (SRPConfigSystems.useEvolution) {
         if (this.phaseCreated >= SRPConfigSystems.evolutionSpawningIgnoreSunlight) {
            IBlockState iblockstate = this.world.getBlockState((new BlockPos(this)).down());
            return iblockstate.canEntitySpawn(this) && this.world.getDifficulty() != EnumDifficulty.PEACEFUL && this.isValidLightLevelTwo() && SRPConfig.spawnDays <= (int)this.world.getWorldTime();
         } else {
            IBlockState iblockstate = this.world.getBlockState((new BlockPos(this)).down());
            return iblockstate.canEntitySpawn(this) && this.world.getDifficulty() != EnumDifficulty.PEACEFUL && this.isValidLightLevelOne() && SRPConfig.spawnDays <= (int)this.world.getWorldTime();
         }
      } else if (SRPConfig.ignoreL) {
         IBlockState iblockstate = this.world.getBlockState((new BlockPos(this)).down());
         return iblockstate.canEntitySpawn(this) && this.world.getDifficulty() != EnumDifficulty.PEACEFUL && this.isValidLightLevelTwo() && SRPConfig.spawnDays <= (int)this.world.getWorldTime();
      } else {
         IBlockState iblockstate = this.world.getBlockState((new BlockPos(this)).down());
         return iblockstate.canEntitySpawn(this) && this.world.getDifficulty() != EnumDifficulty.PEACEFUL && this.isValidLightLevelOne() && SRPConfig.spawnDays <= (int)this.world.getWorldTime();
      }
   }

   protected void despawnEntity() {
      Result result = null;
      if (this.isNoDespawnRequired()) {
         this.idleTime = 0;
      } else if ((this.idleTime & 31) == 31 && (result = ForgeEventFactory.canEntityDespawn(this)) != Result.DEFAULT) {
         if (result == Result.DENY) {
            this.idleTime = 0;
         } else {
            this.spawnCyst();
            this.storeBefDes();
            this.setDead();
         }
      } else {
         Entity entity = this.world.getClosestPlayerToEntity(this, -1.0D);
         if (entity != null) {
            double d0 = entity.posX - this.posX;
            double d1 = entity.posY - this.posY;
            double d2 = entity.posZ - this.posZ;
            double d3 = d0 * d0 + d1 * d1 + d2 * d2;
            if (this.canDespawn() && d3 > 16384.0D) {
               this.spawnCyst();
               this.storeBefDes();
               this.setDead();
            }

            if (this.idleTime > 600 && this.rand.nextInt(800) == 0 && d3 > 1024.0D && this.canDespawn()) {
               this.spawnCyst();
               this.storeBefDes();
               this.setDead();
            } else if (d3 < 1024.0D) {
               this.idleTime = 0;
            }
         }
      }

   }

   protected void storeBefDes() {
      if (this.dodFatherID != 0) {
         Entity dod = this.world.getEntityByID(this.dodFatherID);
         if (dod.isEntityAlive() && dod instanceof EntityPDispatcher) {
            ((EntityPDispatcher)dod).storeParasite(this);
         }
      }

   }

   protected boolean isValidLightLevelOne() {
      if (this.world.getBiome(this.getPosition()) instanceof BiomeParasite) {
         return this.isValidLightLevelTwo();
      } else {
         BlockPos blockpos = new BlockPos(this.posX, this.getEntityBoundingBox().minY, this.posZ);
         if (this.world.getLightFor(EnumSkyBlock.SKY, blockpos) > this.rand.nextInt(32)) {
            return false;
         } else {
            int i = this.world.getLightFromNeighbors(blockpos);
            if (this.world.isThundering()) {
               int j = this.world.getSkylightSubtracted();
               this.world.setSkylightSubtracted(10);
               i = this.world.getLightFromNeighbors(blockpos);
               this.world.setSkylightSubtracted(j);
            }

            return i <= this.rand.nextInt(8) && this.getBlockPathWeight(new BlockPos(this.posX, this.getEntityBoundingBox().minY, this.posZ)) >= 0.0F;
         }
      }
   }

   protected boolean isValidLightLevelTwo() {
      BlockPos blockpos = new BlockPos(this.posX, this.getEntityBoundingBox().minY, this.posZ);
      int light = this.world.getLightFor(EnumSkyBlock.BLOCK, blockpos);
      if (light <= this.rand.nextInt(1000) && light <= 7) {
         return this.rand.nextInt(8) == 0;
      } else {
         return false;
      }
   }

   private boolean isInParasiteBiome() {
      return this.rand.nextInt(8) == 0 && this.world.getBiome(this.getPosition()) instanceof BiomeParasite;
   }

   public IEntityLivingData onInitialSpawn(DifficultyInstance difficulty, IEntityLivingData livingdata) {
      IEntityLivingData floo = super.onInitialSpawn(difficulty, livingdata);
      return floo;
   }

   private boolean applyColdBiome() {
      Biome c = this.world.getBiome(this.getPosition());
      if (c.getTemperature(this.getPosition()) >= 0.15F && !c.isSnowyBiome()) {
         return false;
      } else {
         this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).getBaseValue() * 0.7D);
         this.dataManager.set(COLD_L, Boolean.valueOf(true));
         return true;
      }
   }

   public void writeEntityToNBT(NBTTagCompound compound) {
      super.writeEntityToNBT(compound);
      compound.setInteger("parasitetype", this.getSkin());
      compound.setBoolean("parasitedespawn", this.canDespawn());
      compound.setFloat("parasitekills", (float)this.getKillC());
      compound.setBoolean("parasitecolob", this.getApplyColonyB());
      compound.setBoolean("parasitecoldl", ((Boolean)this.dataManager.get(COLD_L)).booleanValue());
      compound.setByte("phasecreat", this.phaseCreated);
      NBTTagList allResS = new NBTTagList();
      NBTTagList allResI = new NBTTagList();
      if (this.inbBlockName.size() == this.inbBlockNumber.size()) {
         for(int i = 0; i < this.inbBlockName.size(); ++i) {
            String res = (String)this.inbBlockName.get(i);
            NBTTagCompound resT = new NBTTagCompound();
            resT.setString("blocks" + i, res);
            allResS.appendTag(resT);
            int resi = ((Integer)this.inbBlockNumber.get(i)).intValue();
            NBTTagCompound resU = new NBTTagCompound();
            resU.setInteger("blocki" + i, resi);
            allResI.appendTag(resU);
         }

         compound.setTag("srpinvblocksname", allResS);
         compound.setTag("srpinvblocksnumber", allResI);
      }
   }

   public void readEntityFromNBT(NBTTagCompound compound) {
      super.readEntityFromNBT(compound);
      if (compound.hasKey("parasitetype", 99)) {
         this.setSkin(compound.getInteger("parasitetype"));
      }

      if (compound.hasKey("parasitedespawn", 99)) {
         this.cannotDespawn(compound.getBoolean("parasitedespawn"));
      }

      if (compound.hasKey("parasitekills", 99)) {
         this.setKillC((double)compound.getFloat("parasitekills"));
      }

      if (compound.hasKey("parasitecolob", 99)) {
         this.setApplyColonyB(compound.getBoolean("parasitecolob"));
      }

      if (compound.hasKey("parasitecoldl", 99)) {
         this.dataManager.set(COLD_L, Boolean.valueOf(compound.getBoolean("parasitecoldl")));
      }

      if (compound.hasKey("phasecreat", 99)) {
         this.phaseCreated = compound.getByte("phasecreat");
      }

      if (compound.hasKey("srpinvblocksname")) {
         NBTTagList allResS = compound.getTagList("srpinvblocksname", 10);
         NBTTagList allResI = compound.getTagList("srpinvblocksnumber", 10);
         if (allResS.tagCount() != allResI.tagCount()) {
            return;
         }

         for(int i = 0; i < allResS.tagCount(); ++i) {
            NBTTagCompound resT = allResS.getCompoundTagAt(i);
            String res = resT.getString("blocks" + i);
            this.inbBlockName.add(i, res);
            NBTTagCompound resU = allResI.getCompoundTagAt(i);
            int resi = resU.getInteger("blocki" + i);
            this.inbBlockNumber.add(i, Integer.valueOf(resi));
         }
      }

   }

   public void particleStatus(byte id) {
      this.world.setEntityState(this, id);
   }

   @SideOnly(Side.CLIENT)
   protected void spawnEffectsGore() {
   }

   @SideOnly(Side.CLIENT)
   public void handleStatusUpdate(byte id) {
      switch(id) {
      case 5:
         for(int i = 0; i <= 10; ++i) {
            this.spawnParticles(SRPEnumParticle.RHAPPY, 0, 0, 0);
         }

         return;
      case 6:
         for(int i = 0; i <= 10; ++i) {
            this.spawnParticles(SRPEnumParticle.GCLOUD, 127, 0, 0);
         }

         return;
      case 7:
         for(int i = 0; i <= 10; ++i) {
            this.spawnParticles(EnumParticleTypes.EXPLOSION_LARGE);
         }

         return;
      case 8:
         for(int i = 0; i <= 10; ++i) {
            this.spawnParticles(SRPEnumParticle.BIOMASS, 0, 0, 0);
         }

         return;
      case 9:
      case 14:
      case 15:
      case 16:
      case 17:
      case 18:
      case 19:
      case 20:
      case 21:
      case 22:
      case 23:
      case 24:
      case 25:
      case 26:
      case 27:
      case 28:
      case 29:
      case 30:
      case 31:
      case 32:
      case 33:
      case 34:
      case 35:
      case 36:
      case 37:
      case 38:
      case 39:
      default:
         super.handleStatusUpdate(id);
         break;
      case 10:
         for(int i = 0; i <= 40; ++i) {
            this.spawnParticles(EnumParticleTypes.CRIT_MAGIC);
         }

         return;
      case 11:
         for(int i = 0; i <= 10; ++i) {
            this.spawnParticles(SRPEnumParticle.GCLOUD, 0, 0, 0);
         }

         return;
      case 12:
         for(int i = 0; i <= 10; ++i) {
            this.spawnParticles(SRPEnumParticle.GCLOUD, 0, 0, 0);
         }

         return;
      case 13:
         for(int i = 0; i <= 10; ++i) {
            this.spawnParticles(SRPEnumParticle.GSPLASH, 4, -1, -1);
         }

         return;
      case 40:
         this.madeRng = 0;
      case 41:
      case 42:
         break;
      case 43:
         int iddd = Block.getStateId(this.world.getBlockState((new BlockPos(this.posX, this.posY, this.posZ)).down()));

         for(int i = 0; i <= 10; ++i) {
            this.world.spawnParticle(EnumParticleTypes.BLOCK_CRACK, this.posX + (double)(this.rand.nextFloat() * this.width * 2.0F) - (double)this.width, this.posY, this.posZ + (double)(this.rand.nextFloat() * this.width * 2.0F) - (double)this.width, this.rand.nextGaussian() * 0.02D, this.rand.nextGaussian() * 0.02D, this.rand.nextGaussian() * 0.02D, new int[]{iddd});
         }
      }

   }

   @SideOnly(Side.CLIENT)
   public void spawnParticles(EnumParticleTypes particleType) {
      double d0 = this.rand.nextGaussian() * 0.02D;
      double d1 = this.rand.nextGaussian() * 0.02D;
      double d2 = this.rand.nextGaussian() * 0.02D;
      this.world.spawnParticle(particleType, this.posX + (double)(this.rand.nextFloat() * this.width * 2.0F) - (double)this.width, this.posY + 0.5D + (double)(this.rand.nextFloat() * this.height), this.posZ + (double)(this.rand.nextFloat() * this.width * 2.0F) - (double)this.width, d0, d1, d2, new int[0]);
   }

   @SideOnly(Side.CLIENT)
   public void spawnParticles(SRPEnumParticle particleType, int r, int g, int b) {
      double d0 = this.rand.nextGaussian() * 0.02D;
      double d1 = this.rand.nextGaussian() * 0.02D;
      double d2 = this.rand.nextGaussian() * 0.02D;
      ParticleSpawner.spawnParticle(particleType, this.posX + (double)(this.rand.nextFloat() * this.width * 2.0F) - (double)this.width, this.posY + 0.5D + (double)(this.rand.nextFloat() * this.height), this.posZ + (double)(this.rand.nextFloat() * this.width * 2.0F) - (double)this.width, d0, d1, d2, r, g, b);
   }

   @SideOnly(Side.CLIENT)
   public void spawnParticlesGore(SRPEnumParticle particleType, int r, int g, int b) {
      this.spawnParticlesGore(particleType, r, g, b, 1.0D, 4.0D);
   }

   @SideOnly(Side.CLIENT)
   public void spawnParticlesGore(SRPEnumParticle particleType, int r, int g, int b, double xF, double yF) {
      double d0 = (double)((float)this.posX + this.rand.nextFloat());
      double d1 = (double)((float)this.posY + this.rand.nextFloat());
      double d2 = (double)((float)this.posZ + this.rand.nextFloat());
      double d3 = d0 - this.posX;
      double d4 = d1 - this.posY;
      double d5 = d2 - this.posZ;
      double d6 = (double)MathHelper.sqrt(d3 * d3 + d4 * d4 + d5 * d5);
      d3 = d3 / d6;
      d4 = d4 / d6;
      d5 = d5 / d6;
      double d7 = 0.5D / (d6 / 4.0D + 0.1D);
      d7 = d7 * (double)(this.rand.nextFloat() * this.rand.nextFloat() + 0.3F);
      d3 = d3 * d7 * xF;
      d4 = d4 * d7 * yF;
      d5 = d5 * d7 * xF;
      d3 = Math.min(d3, 0.2D) * (Math.random() * 2.0D - 1.0D);
      d4 = Math.min(d4, 0.6D) * (Math.random() * 2.0D - 1.0D);
      d5 = Math.min(d5, 0.2D) * (Math.random() * 2.0D - 1.0D);
      ParticleSpawner.spawnParticle(particleType, this.posX, this.posY + 0.2D + (double)this.height, this.posZ, d3, d4, d5, r, g, b);
   }

   @SideOnly(Side.CLIENT)
   public void spawnParticles(SRPEnumParticle particleType, int r, int g, int b, double d0, double d1, double d2, double d3, double d4, double d5) {
      ParticleSpawner.spawnParticle(particleType, d0, d1, d2, d3, d4, d5, r, g, b);
   }

   @SideOnly(Side.CLIENT)
   public float getSelfeFlashIntensity(float p_70831_1_) {
      return ((float)this.lastActiveTime + (float)(this.timeSinceIgnited - this.lastActiveTime) * p_70831_1_) / (float)(this.fuseTime - 2);
   }

   @SideOnly(Side.CLIENT)
   public boolean isSwingingArms() {
      return false;
   }

   @SideOnly(Side.CLIENT)
   public float getscale(float p_70831_1_) {
      return 0.0F;
   }

   @SideOnly(Side.CLIENT)
   public boolean getStillAni() {
      return this.still;
   }

   @SideOnly(Side.CLIENT)
   public float getAniTick() {
      return this.aniticks;
   }

   public void doSpecialSkill(byte id) {
      switch(id) {
      case 13:
         this.skillBreakBlocks();
         return;
      case 14:
         this.skillLeap();
         return;
      default:
      }
   }

   public boolean getFinished(byte attID) {
      switch(attID) {
      case 13:
         return this.SkillBGflag;
      case 14:
         return this.SkillLeapFlag;
      default:
         return false;
      }
   }

   public void setFinished(byte attID, boolean in) {
      switch(attID) {
      case 13:
         this.SkillBGflag = in;
      case 14:
         this.SkillLeapFlag = in;
      default:
      }
   }

   public void setSkillBreakBlocksValues(float hardness, int heightIn, int rangeIn) {
      this.blockH = hardness;
      this.BGheight = heightIn;
      this.BGrange = rangeIn;
   }

   public void skillBreakBlocks() {
      if (!this.isDead) {
         if (this.blockH != 0.0F) {
            int blocksbroke = 0;
            if (ForgeEventFactory.getMobGriefingEvent(this.world, this)) {
               int i1 = MathHelper.floor(this.posY + 0.1D);
               double l1 = this.posX;
               double i2 = this.posZ;
               boolean flag = false;
               int Brangeatm = this.BGrange;
               int offsetT = 0;
               if (this.getAttackTarget() != null) {
                  EntityLivingBase target = this.getAttackTarget();
                  if (target.getDistanceSq(this.posX, target.posY, this.posZ) < 9.0D) {
                     if (target.posY - this.posY < -1.0D) {
                        offsetT -= 2;
                        if (!this.onGround) {
                           --offsetT;
                        }
                     } else if (target.posY - this.posY > 2.0D) {
                        ++offsetT;
                        this.BGrange = 0;
                     }
                  }
               }

               for(int k2 = -1 * this.BGrange; k2 <= 1 * this.BGrange; ++k2) {
                  for(int l2 = -1 * this.BGrange; l2 <= 1 * this.BGrange; ++l2) {
                     for(int j = 1 + offsetT; j <= this.BGheight + offsetT; ++j) {
                        double i3 = l1 + (double)k2;
                        double k = (double)(i1 + j);
                        double l = i2 + (double)l2;
                        BlockPos blockpos = new BlockPos(i3, k, l);
                        IBlockState iblockstate = this.world.getBlockState(blockpos);
                        Block block = iblockstate.getBlock();
                        float bHard = iblockstate.getBlockHardness(this.world, blockpos);
                        if (bHard <= this.blockH && bHard > 0.0F && (!(block instanceof IMetaName) || block == SRPBlocks.ParasiteCanister) && block != SRPBlocks.BiomeHeart && block != SRPBlocks.ColonyHeart && block != SRPBlocks.ParasiteRubbleDense && block != SRPBlocks.ParasiteCanisterActive && block != SRPBlocks.dodN && !(block instanceof BlockLiquid) && !(block instanceof BlockPortal) && !(block instanceof BlockEndGateway) && !(block instanceof BlockEndPortalFrame) && iblockstate.getMaterial() != Material.PORTAL) {
                           String name = block.getRegistryName().toString();
                           if (!this.blockException(name) && block != Blocks.AIR && block.canEntityDestroy(iblockstate, this.world, blockpos, this) && ForgeEventFactory.onEntityDestroyBlock(this, blockpos, iblockstate)) {
                              if (SRPConfig.cystActive) {
                                 int meta = block.getMetaFromState(iblockstate);
                                 flag = this.destroyBlockPos(blockpos, false) || flag;
                                 if (SRPConfig.doTileDrops) {
                                    this.addToBlockInv(name + ";" + meta);
                                 }
                              } else {
                                 this.destroyBlockPos(blockpos, SRPConfig.doTileDrops);
                              }

                              ++blocksbroke;
                           }
                        }
                     }
                  }
               }

               this.BGrange = Brangeatm;
               this.SkillBGflag = true;
            }
         }
      }
   }

   protected boolean destroyBlockPos(BlockPos pos, boolean dropBlock) {
      IBlockState iblockstate = this.world.getBlockState(pos);
      Block block = iblockstate.getBlock();
      if (block.isAir(iblockstate, this.world, pos)) {
         return false;
      } else {
         if (this.rand.nextDouble() < SRPConfig.blockParticleChance) {
            this.world.playEvent(2001, pos, Block.getStateId(iblockstate));
         }

         if (this.rand.nextDouble() < SRPConfig.blockSoundChance) {
            SoundType soundtype = block.getSoundType(Block.getStateById(Block.getStateId(iblockstate)), this.world, pos, (Entity)null);
            this.world.playSound((EntityPlayer)null, pos, soundtype.getBreakSound(), SoundCategory.BLOCKS, (soundtype.getVolume() + 1.0F) / 2.0F, soundtype.getPitch() * 0.8F);
         }

         if (dropBlock && this.rand.nextDouble() < SRPConfig.blockDropChance) {
            block.dropBlockAsItem(this.world, pos, iblockstate, 0);
         }

         return this.world.setBlockState(pos, Blocks.AIR.getDefaultState(), 3);
      }
   }

   protected boolean blockException(String name) {
      return ParasiteEventEntity.checkName(name, SRPConfig.parasiteGriefingBlackList, SRPConfig.parasiteGriefingWhite);
   }

   public void addToBlockInv(String name) {
      boolean flag = true;

      for(int i = 0; i < this.inbBlockName.size(); ++i) {
         if (((String)this.inbBlockName.get(i)).equals(name) && ((Integer)this.inbBlockNumber.get(i)).intValue() <= 64) {
            int iiii = ((Integer)this.inbBlockNumber.get(i)).intValue() + 1;
            this.inbBlockNumber.set(i, Integer.valueOf(iiii));
            flag = false;
            break;
         }
      }

      if (flag) {
         if (this.inbBlockName.size() >= 22) {
            this.spawnCyst();
         }

         this.inbBlockName.add(name);
         this.inbBlockNumber.add(Integer.valueOf(1));
      }

   }

   protected void spawnCyst() {
      if (!this.inbBlockName.isEmpty()) {
         double d0 = (double)((float)this.posX + this.world.rand.nextFloat());
         double d1 = (double)((float)this.posY + this.world.rand.nextFloat());
         double d2 = (double)((float)this.posZ + this.world.rand.nextFloat());
         double d3 = d0 - this.posX;
         double d4 = d1 - this.posY;
         double d5 = d2 - this.posZ;
         double d6 = (double)MathHelper.sqrt(d3 * d3 + d4 * d4 + d5 * d5);
         d3 = d3 / d6;
         d4 = d4 / d6;
         d5 = d5 / d6;
         double d7 = 0.5D / (d6 / 4.0D + 0.1D);
         d7 = d7 * (double)(this.world.rand.nextFloat() * this.world.rand.nextFloat() + 0.3F);
         d3 = d3 * d7;
         d4 = d4 * d7 * 2.0D;
         d5 = d5 * d7;
         EntityGore bomb = new EntityGore(this.world);
         bomb.setType((byte)10);
         bomb.copyLocationAndAnglesFrom(this);
         bomb.setMotion(d3, d4, d5, 0.25D, 0.75D);
         this.world.spawnEntity(bomb);
         bomb.inbBlockName = new ArrayList(this.inbBlockName);
         bomb.inbBlockNumber = new ArrayList(this.inbBlockNumber);
         this.inbBlockName = new ArrayList();
         this.inbBlockNumber = new ArrayList();
      }
   }

   public void setskillLeapValues(float leapY, double leapSpeed, int jumpRad) {
      this.leapMotionY = leapY;
      this.jumpSpeed = leapSpeed;
      this.jumpR = jumpRad;
   }

   protected void skillLeap() {
      if (this.leapMotionY != 0.0F) {
         if (this.getAttackTarget() != null && this.shouldWorkTask() && !this.isPotionActive(MobEffects.SLOWNESS) && this.getParasiteStatus() <= 2) {
            EntityLivingBase entitylivingbase = this.getAttackTarget();
            if (this.attacking == 0) {
               ++this.attacking;
               this.targetX = entitylivingbase.posX;
               this.targetZ = entitylivingbase.posZ;
            }
         }

         if (this.attacking >= 1) {
            ++this.attacking;
            this.skillBreakBlocks();
            if (this.attacking == 2 && this.onGround) {
               this.setParasiteStatus(10);
               this.getNavigator().clearPath();
               double d0 = this.targetX - this.posX;
               double d1 = this.targetZ - this.posZ;
               double f = (double)MathHelper.sqrt(d0 * d0 + d1 * d1);
               this.motionY = (double)this.leapMotionY;
               this.motionX += d0 / f * this.jumpSpeed * 0.9D + this.motionX * 0.3D;
               this.motionZ += d1 / f * this.jumpSpeed * 0.9D + this.motionZ * 0.3D;
            }

            if (this.attacking > 2 && this.onGround) {
               if (this.jumpR != 0) {
                  float damage = (float)this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getBaseValue();
                  AxisAlignedBB axisalignedbb = (new AxisAlignedBB(this.posX, this.posY, this.posZ, this.posX + 1.0D, this.posY + 1.0D, this.posZ + 1.0D)).grow((double)this.jumpR, 2.0D, (double)this.jumpR);

                  for(EntityLivingBase mob : this.world.getEntitiesWithinAABB(EntityLivingBase.class, axisalignedbb)) {
                     if (mob != this && !(mob instanceof EntityParasiteBase)) {
                        mob.knockBack(mob, 2.5F, this.posX - mob.posX, this.posZ - mob.posZ);
                        this.attackEntityAsMob(mob);
                     }
                  }
               }

               this.setParasiteStatus(0);
               this.attacking = 0;
               if (this.type >= 31 && this.height > 2.0F) {
                  this.playSound(SRPSounds.HITGROUND, 15.0F, 1.0F);
               }

               this.SkillLeapFlag = true;
            }
         }

      }
   }

   public class EntityAIJumping extends EntityAIBase {
      private final EntityLiving parent;
      private int secs;

      public EntityAIJumping(EntityLiving parentIn) {
         this.parent = parentIn;
         this.setMutexBits(4);
         if (parentIn.getNavigator() instanceof PathNavigateGround) {
            ((PathNavigateGround)parentIn.getNavigator()).setCanSwim(true);
         } else if (parentIn.getNavigator() instanceof PathNavigateFlying) {
            ((PathNavigateFlying)parentIn.getNavigator()).setCanFloat(true);
         }

      }

      public boolean shouldExecute() {
         ++this.secs;
         if (this.secs < 10) {
            return false;
         } else {
            this.secs = 0;
            EntityLivingBase target = this.parent.getAttackTarget();
            if (target == null) {
               return false;
            } else {
               if (target.getDistanceSq(this.parent.posX, target.posY, this.parent.posZ) < 4.0D && target.posY - (this.parent.posY + (double)this.parent.getEyeHeight()) > 1.0D && this.parent.onGround) {
                  this.parent.getNavigator().clearPath();
                  double dd0 = target.posX - this.parent.posX;
                  double dd1 = target.posZ - this.parent.posZ;
                  float f = MathHelper.sqrt(dd0 * dd0 + dd1 * dd1);
                  this.parent.motionX += dd0 / (double)f * 0.5D * 0.800000011920929D + this.parent.motionX * 0.20000000298023224D;
                  this.parent.motionZ += dd1 / (double)f * 0.5D * 0.800000011920929D + this.parent.motionZ * 0.20000000298023224D;
                  this.parent.motionY = 0.2D + (double)this.parent.height * 0.15D;
               }

               return false;
            }
         }
      }

      public void updateTask() {
         if (this.parent.getRNG().nextFloat() < 0.8F) {
            this.parent.getJumpHelper().setJumping();
         }

      }
   }

   class EntityAIWait extends EntityAIBase {
      public EntityAIWait() {
         this.setMutexBits(7);
      }

      public boolean shouldExecute() {
         return EntityParasiteBase.this.getWait() > 0;
      }

      public void updateTask() {
      }
   }
}
