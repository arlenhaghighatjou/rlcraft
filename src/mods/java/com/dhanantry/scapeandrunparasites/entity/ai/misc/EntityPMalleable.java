package com.dhanantry.scapeandrunparasites.entity.ai.misc;

import com.dhanantry.scapeandrunparasites.client.particle.SRPEnumParticle;
import com.dhanantry.scapeandrunparasites.entity.EntityScaryOrb;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIBlockLight;
import com.dhanantry.scapeandrunparasites.init.SRPItems;
import com.dhanantry.scapeandrunparasites.init.SRPPotions;
import com.dhanantry.scapeandrunparasites.init.SRPSounds;
import com.dhanantry.scapeandrunparasites.util.ParasiteEventEntity;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfig;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfigSystems;
import java.util.ArrayList;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.CooldownTracker;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraftforge.event.ForgeEventFactory;

public abstract class EntityPMalleable extends EntityParasiteBase {
   private static final DataParameter<Byte> HIT = EntityDataManager.createKey(EntityPMalleable.class, DataSerializers.BYTE);
   private ArrayList<String> resistanceS = new ArrayList();
   private ArrayList<Integer> resistanceI = new ArrayList();
   protected int newDamageCooldown = 0;
   protected float pointReduction = 0.1F;
   protected int pointCap = 10;
   protected int DamageTypeCap = 5;
   protected double chanceLearn = 0.5D;
   protected double chanceLearnFire = 0.0D;
   private int onFireA;
   private int onProj;
   protected float adaptationCap = 1.0F;
   protected float regen = 0.0F;
   protected int regenEff = 1;
   protected int regenUse = 1;
   private int lastTimeDama;
   private int stun;
   public boolean colonySpawned;
   protected int fuseOrb;
   protected int orbStartTimer;
   protected int orbItemCool;
   private int orbVersionCooldown;
   protected double foodRott = 0.0D;
   protected int foodRootNumber = 1;
   private int limitOrb;
   protected int borderOrb;
   private boolean skillOrb;

   public EntityPMalleable(World worldIn) {
      super(worldIn);
      if (SRPConfig.canTargetBlock) {
         this.tasks.addTask(7, new EntityAIBlockLight(this, 20, 5));
      }

   }

   protected void entityInit() {
      super.entityInit();
      this.dataManager.register(HIT, Byte.valueOf((byte)0));
   }

   public void onLivingUpdate() {
      super.onLivingUpdate();
      if (!this.world.isRemote) {
         if (this.srpTicks == 10) {
            if (this.isPotionActive(SRPPotions.RES_E)) {
               this.removeAllResistance(1);
            }

            if (this.orbVersionCooldown > 0) {
               --this.orbVersionCooldown;
            }
         }

         if (this.onFireA > 0) {
            --this.onFireA;
         }

         if (this.onProj > 0) {
            --this.onProj;
         }

         if (this.newDamageCooldown > 0) {
            --this.newDamageCooldown;
         }

         if (this.lastTimeDama > 0) {
            --this.lastTimeDama;
         } else if (!this.isDead && this.getHealth() > 0.0F && this.regen > 0.0F && this.killcount > 1.0D && this.getHealth() < this.getMaxHealth()) {
            this.setHealth(this.getHealth() + this.regen);
            --this.regenUse;
            if (this.regenUse <= 0) {
               --this.killcount;
               this.regenUse = this.regenEff;
            }
         }

         if (this.stun >= 0) {
            --this.stun;
            if (this.stun < 0 && this.getParasiteStatus() == 25) {
               this.setParasiteStatus(0);
            }
         }
      }

   }

   public boolean attackEntityFrom(DamageSource source, float amount) {
      if (this.world.isRemote) {
         return false;
      } else {
         this.dataManager.set(HIT, Byte.valueOf((byte)0));
         if (SRPConfigSystems.useOneMind && source.getTrueSource() instanceof EntityLivingBase && ParasiteEventEntity.alertOthers(this, (EntityLivingBase)source.getTrueSource(), this.world, 3)) {
            this.addPotionEffect(new PotionEffect(SRPPotions.SENS_E, 300, 3, false, false));
         }

         if (source != DamageSource.OUT_OF_WORLD && source != DamageSource.IN_WALL) {
            if ((source == DamageSource.IN_FIRE || source == DamageSource.ON_FIRE) && this.rand.nextDouble() < this.chanceLearnFire) {
               this.onFireA = SRPConfig.fireTickWindow;
            }

            float times = 0.0F;
            if (source.getImmediateSource() instanceof EntityLivingBase) {
               if (source.getImmediateSource() instanceof EntityPlayer) {
                  times = (float)this.hasResistance(((EntityPlayer)source.getImmediateSource()).getItemStackFromSlot(EntityEquipmentSlot.MAINHAND).getItem().getRegistryName().toString());
               } else {
                  String mobname = EntityList.getKey(source.getImmediateSource()).toString();
                  if (mobname == null) {
                     times = (float)this.hasResistance(source.getDamageType());
                  } else {
                     times = (float)this.hasResistance(mobname);
                  }
               }
            } else if (source.getTrueSource() instanceof EntityLivingBase) {
               if (source.getTrueSource() instanceof EntityPlayer) {
                  times = (float)this.hasResistance(((EntityPlayer)source.getTrueSource()).getItemStackFromSlot(EntityEquipmentSlot.MAINHAND).getItem().getRegistryName().toString());
               } else {
                  String mobname = EntityList.getKey(source.getTrueSource()).toString();
                  if (mobname == null) {
                     times = (float)this.hasResistance(source.getDamageType());
                  } else {
                     times = (float)this.hasResistance(mobname);
                  }
               }
            } else {
               times = (float)this.hasResistance(source.getDamageType());
            }

            float bonus = times * this.pointReduction * amount;
            amount = Math.max(amount - bonus * this.adaptationCap, 0.0F);
            if (bonus != 0.0F && amount != 0.0F) {
               this.particleStatus((byte)9);
               this.particleStatus((byte)9);
            }

            if (amount == 0.0F) {
               this.particleStatus((byte)9);
               this.particleStatus((byte)9);
            }

            this.outOfRange(source);
            boolean flag = super.attackEntityFrom(source, amount);
            if (flag) {
               this.lastTimeDama = SRPConfig.regenTimer;
               switch(this.getHitStatus()) {
               case 1:
                  this.playSound(SRPSounds.ADAPTATION_P, this.getSoundVolume(), (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F + 1.0F);
                  return flag;
               case 2:
                  this.playSound(SRPSounds.ADAPTATION_F, this.getSoundVolume(), (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F + 1.0F);
                  return flag;
               }
            }

            return flag;
         } else {
            return super.attackEntityFrom(source, amount);
         }
      }
   }

   protected boolean processInteract(EntityPlayer player, EnumHand hand) {
      if (this.world.isRemote) {
         return true;
      } else {
         Item wea = player.getItemStackFromSlot(EntityEquipmentSlot.MAINHAND).getItem();
         if (wea != SRPItems.itembase) {
            return super.processInteract(player, hand);
         } else {
            String out = "Current adaptation for " + EntityList.getKey(this).toString() + " (Damage type, points): ";

            for(int i = 0; i < this.resistanceS.size(); ++i) {
               out = out + "[" + (String)this.resistanceS.get(i) + ", " + this.resistanceI.get(i) + "] ";
            }

            player.sendMessage(new TextComponentString(out));
            return true;
         }
      }
   }

   public boolean attackEntityAsMob(Entity entityIn) {
      if (this.borderOrb > 0) {
         return false;
      } else {
         boolean flag = super.attackEntityAsMob(entityIn);
         if (flag) {
            this.attackEntityAsMobFood(entityIn, true);
         }

         return flag;
      }
   }

   public boolean attackEntityAsMobFood(Entity target, boolean hit) {
      if (this.rand.nextDouble() >= this.foodRott && hit) {
         return false;
      } else if (!(target instanceof EntityPlayer)) {
         return false;
      } else if (this.foodRootNumber <= 0) {
         return false;
      } else {
         EntityPlayer player = (EntityPlayer)target;
         if (player.capabilities.isCreativeMode) {
            return false;
         } else {
            for(int i = 0; i < player.inventory.mainInventory.size(); ++i) {
               if (((ItemStack)player.inventory.mainInventory.get(i)).getItem() instanceof ItemFood) {
                  int amm = this.rand.nextInt(this.foodRootNumber) + 1;
                  ((ItemStack)player.inventory.mainInventory.get(i)).shrink(amm);
                  ItemStack stack = new ItemStack(SRPItems.infected_drop, amm);
                  EntityItem entityitem = new EntityItem(this.world, target.posX, target.posY, target.posZ, stack);
                  entityitem.setDefaultPickupDelay();
                  this.world.spawnEntity(entityitem);
                  return true;
               }
            }

            if (player.getItemStackFromSlot(EntityEquipmentSlot.OFFHAND).getItem() instanceof ItemFood) {
               int amm = this.rand.nextInt(this.foodRootNumber) + 1;
               player.getItemStackFromSlot(EntityEquipmentSlot.OFFHAND).shrink(amm);
               ItemStack stack = new ItemStack(SRPItems.infected_drop, amm);
               EntityItem entityitem = new EntityItem(this.world, target.posX, target.posY, target.posZ, stack);
               entityitem.setDefaultPickupDelay();
               this.world.spawnEntity(entityitem);
               return true;
            } else {
               return false;
            }
         }
      }
   }

   public int hasResistance(String damage) {
      if (!this.isPotionActive(SRPPotions.RES_E) && !this.dead) {
         if (this.checkList(damage)) {
            return 0;
         } else if (this.DamageTypeCap <= 0) {
            return 0;
         } else {
            if (this.rand.nextDouble() < this.getChanceLearn()) {
               this.addResistance(damage);
            }

            for(int i = 0; i < this.resistanceS.size(); ++i) {
               if (((String)this.resistanceS.get(i)).equals(damage)) {
                  int dama = ((Integer)this.resistanceI.get(i)).intValue();
                  if (dama <= this.pointCap) {
                     this.dataManager.set(HIT, Byte.valueOf((byte)1));
                  } else {
                     this.dataManager.set(HIT, Byte.valueOf((byte)2));
                  }

                  return Math.min(dama, this.pointCap);
               }
            }

            return 0;
         }
      } else {
         return 0;
      }
   }

   private double getChanceLearn() {
      double bonus = 0.0D;
      if (SRPConfig.adaptationDimStrong.length > 0) {
         String[] here = new String[2];

         for(int i = 0; i < SRPConfig.adaptationDimStrong.length; ++i) {
            here = SRPConfigSystems.evolutionDimStart[i].split(";");
            int dim = Integer.parseInt(here[0]);
            double b = Double.parseDouble(here[1]);
            if (dim == this.world.provider.getDimension()) {
               bonus = b;
               break;
            }
         }
      }

      return this.chanceLearn + this.chanceLearn * bonus;
   }

   public void addResistance(String damage) {
      if (this.onFireA <= 0) {
         if (this.newDamageCooldown <= 0) {
            boolean flag = true;

            for(int i = 0; i < this.resistanceS.size(); ++i) {
               if (((String)this.resistanceS.get(i)).equals(damage)) {
                  int iiii = ((Integer)this.resistanceI.get(i)).intValue() + 1;
                  this.resistanceI.set(i, Integer.valueOf(iiii));
                  flag = false;
                  if (iiii <= this.pointCap) {
                     this.particleStatus((byte)5);
                     this.particleStatus((byte)5);
                  }
                  break;
               }
            }

            if (flag) {
               if (this.resistanceS.size() >= this.DamageTypeCap) {
                  return;
               }

               this.resistanceS.add(damage);
               this.resistanceI.add(Integer.valueOf(1));
               this.particleStatus((byte)5);
               this.particleStatus((byte)5);
            }

            this.newDamageCooldown = SRPConfig.adaptationNewDamageCooldon;
         }
      }
   }

   public void removeAllResistance(int value) {
      for(int i = 0; i < this.resistanceS.size(); ++i) {
         int iiii = ((Integer)this.resistanceI.get(i)).intValue() - value;
         if (iiii <= 0) {
            this.resistanceI.remove(i);
            this.resistanceS.remove(i);
         } else {
            this.resistanceI.set(i, Integer.valueOf(iiii));
         }
      }

   }

   public ArrayList<String> getResistanceS() {
      return this.resistanceS;
   }

   public ArrayList<Integer> getResistanceI() {
      return this.resistanceI;
   }

   public void copyResistancesFrom(EntityPMalleable in) {
      this.resistanceS = in.getResistanceS();
      this.resistanceI = in.getResistanceI();
   }

   public void increaseAllResistances() {
      for(int i = 0; i < this.resistanceI.size(); ++i) {
         int iiii = ((Integer)this.resistanceI.get(i)).intValue() + 1;
         this.resistanceI.set(i, Integer.valueOf(iiii));
      }

   }

   public void cutResistances(int in) {
      if (this.DamageTypeCap > 0) {
         this.stun += 40;
         this.addPotionEffect(new PotionEffect(MobEffects.SLOWNESS, 40, 100, false, false));
         this.setParasiteStatus(25);
         this.playSound(SRPSounds.TENDRIL, 3.0F, 1.0F);
         if (SRPConfigSystems.rageEnable) {
            this.addPotionEffect(new PotionEffect(SRPPotions.RAGE_E, 200, 1, false, false));
         }

         SRPPotions.applyStackPotion(SRPPotions.BLEED_E, this, 100, 0);
         if (in % 2 != 0) {
            ++in;
         }

         this.DamageTypeCap -= in;
         if (this.resistanceI.size() != this.resistanceS.size()) {
            this.resistanceI = new ArrayList();
            this.resistanceS = new ArrayList();
            this.DamageTypeCap = 0;
         } else {
            int size = this.resistanceS.size() - in;
            if (size <= 0) {
               this.resistanceI = new ArrayList();
               this.resistanceS = new ArrayList();
            } else {
               while(in <= 0) {
                  if (this.resistanceI.isEmpty()) {
                     this.DamageTypeCap = 0;
                     return;
                  }

                  if (this.resistanceI.size() == 0) {
                     this.DamageTypeCap = 0;
                     return;
                  }

                  this.resistanceI.remove(0);
                  this.resistanceS.remove(0);
                  --in;
               }

            }
         }
      }
   }

   public void increaseDamageCap(int in) {
      this.DamageTypeCap += in;
   }

   private boolean checkList(String damage) {
      return ParasiteEventEntity.checkName(damage, SRPConfig.damageTypeBlackList, SRPConfig.damageTypeBlackListWhite);
   }

   private void outOfRange(DamageSource source) {
      if (this.getAttackTarget() == null) {
         if (source.getTrueSource() instanceof Entity) {
            Entity sss = source.getTrueSource();
            if (sss instanceof EntityPlayer) {
               EntityPlayer pl = (EntityPlayer)sss;
               if (pl.capabilities.disableDamage || pl.isSpectator()) {
                  return;
               }
            }

            if (this.canEntityBeSeen(sss) && SRPPotions.applySense(this, 600, this.getDistanceSq(sss), 50)) {
               this.getNavigator().tryMoveToXYZ(sss.posX, sss.posY, sss.posZ, 1.3D);
            }
         } else if (this.getNavigator().getPath() == null) {
            Vec3d vec3d = RandomPositionGenerator.findRandomTargetBlockAwayFrom(this, 16, 7, new Vec3d(this.posX, this.posY, this.posZ));
            if (vec3d != null && !this.getNavigator().tryMoveToXYZ(vec3d.x, vec3d.y, vec3d.z, 1.3D)) {
               vec3d = RandomPositionGenerator.findRandomTargetBlockAwayFrom(this, 16, 7, new Vec3d(this.posX, this.posY, this.posZ));
               if (vec3d != null && !this.getNavigator().tryMoveToXYZ(vec3d.x, vec3d.y, vec3d.z, 1.3D) && this.ticksExisted % 20 == 0) {
                  this.getNavigator().clearPath();
                  double dd0 = vec3d.x - this.posX;
                  double dd1 = vec3d.z - this.posZ;
                  float f = MathHelper.sqrt(dd0 * dd0 + dd1 * dd1);
                  this.motionX += dd0 / (double)f * 1.0D * 0.800000011920929D + this.motionX * 0.20000000298023224D;
                  this.motionZ += dd1 / (double)f * 1.0D * 0.800000011920929D + this.motionZ * 0.20000000298023224D;
                  this.motionY = 0.15D;
               }
            }
         }
      }

   }

   public byte getHitStatus() {
      return ((Byte)this.dataManager.get(HIT)).byteValue();
   }

   public void setOrbVersionCooldown(int in) {
      this.orbVersionCooldown = in;
   }

   public String getMostCommonDamage() {
      if (this.isPotionActive(SRPPotions.RES_E)) {
         return null;
      } else {
         String atm = null;
         int times = 0;

         for(int i = 0; i < this.resistanceS.size(); ++i) {
            if (((Integer)this.resistanceI.get(i)).intValue() > times) {
               times = ((Integer)this.resistanceI.get(i)).intValue();
               atm = (String)this.resistanceS.get(i);
            }
         }

         return atm;
      }
   }

   public void removeCommonDamage(String damage, int times) {
      for(int i = 0; i < this.resistanceS.size(); ++i) {
         if (((String)this.resistanceS.get(i)).equals(damage)) {
            int neww = ((Integer)this.resistanceI.get(i)).intValue() - times;
            if (neww <= 0) {
               this.resistanceS.remove(i);
               this.resistanceI.remove(i);
            } else {
               this.resistanceI.set(i, Integer.valueOf(neww));
            }
         }
      }

   }

   public boolean scaryOrbEffect(EntityLivingBase in, int totalMobs) {
      if (in instanceof EntityPlayer) {
         EntityPlayer player = (EntityPlayer)in;
         if (player.capabilities.isCreativeMode) {
            return false;
         }

         player.addExhaustion(this.foodSteal * (float)SRPConfig.orbFoodMult);
         if (this.orbItemCool > 0) {
            CooldownTracker track = player.getCooldownTracker();

            for(int i = 0; i < player.inventory.mainInventory.size(); ++i) {
               if (track.getCooldown(((ItemStack)player.inventory.mainInventory.get(i)).getItem(), 1.0F) == 0.0F) {
                  track.setCooldown(((ItemStack)player.inventory.mainInventory.get(i)).getItem(), this.orbItemCool);
               }
            }
         }

         this.attackEntityAsMobFood(player, false);
      } else {
         if (in == this) {
            return false;
         }

         ResourceLocation enti = EntityList.getKey(in);
         if (enti == null) {
            return false;
         }

         if (ParasiteEventEntity.checkName(enti.toString(), SRPConfig.mobattackingBlackList, SRPConfig.mobattackingBlackListWhite)) {
            return false;
         }
      }

      in.addPotionEffect(new PotionEffect(SRPPotions.COTH_E, 1200, 3, false, false));
      return true;
   }

   public void onDeath(DamageSource cause) {
      super.onDeath(cause);
      if (!this.world.isRemote) {
         ParasiteEventEntity.checkColony(this.world, cause, this);
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

   public void writeEntityToNBT(NBTTagCompound compound) {
      super.writeEntityToNBT(compound);
      compound.setBoolean("paracolony", this.colonySpawned);
      if (this.resistanceS.size() == this.resistanceI.size()) {
         NBTTagList allResS = new NBTTagList();
         NBTTagList allResI = new NBTTagList();

         for(int i = 0; i < this.resistanceS.size(); ++i) {
            String res = (String)this.resistanceS.get(i);
            NBTTagCompound resT = new NBTTagCompound();
            resT.setString("resistance" + i, res);
            allResS.appendTag(resT);
            int resi = ((Integer)this.resistanceI.get(i)).intValue();
            NBTTagCompound resU = new NBTTagCompound();
            resU.setInteger("resistance" + i, resi);
            allResI.appendTag(resU);
         }

         compound.setInteger("damagetypecap", this.DamageTypeCap);
         compound.setTag("sprresistances", allResS);
         compound.setTag("sprresistancei", allResI);
      }
   }

   public void readEntityFromNBT(NBTTagCompound compound) {
      super.readEntityFromNBT(compound);
      if (compound.hasKey("paracolony", 99)) {
         this.colonySpawned = compound.getBoolean("paracolony");
      }

      if (compound.hasKey("damagetypecap", 99)) {
         this.DamageTypeCap = compound.getInteger("damagetypecap");
      }

      if (compound.hasKey("sprresistances")) {
         NBTTagList allResS = compound.getTagList("sprresistances", 10);
         NBTTagList allResI = compound.getTagList("sprresistancei", 10);
         if (allResS.tagCount() != allResI.tagCount()) {
            return;
         }

         for(int i = 0; i < allResS.tagCount(); ++i) {
            NBTTagCompound resT = allResS.getCompoundTagAt(i);
            String res = resT.getString("resistance" + i);
            this.resistanceS.add(i, res);
            NBTTagCompound resU = allResI.getCompoundTagAt(i);
            int resi = resU.getInteger("resistance" + i);
            this.resistanceI.add(i, Integer.valueOf(resi));
         }
      }

   }

   public boolean getFinished(byte attID) {
      switch(attID) {
      case 21:
         return this.skillOrb;
      default:
         return super.getFinished(attID);
      }
   }

   public void setFinished(byte attID, boolean in) {
      switch(attID) {
      case 21:
         this.skillOrb = in;
         return;
      default:
         super.setFinished(attID, in);
      }
   }

   public void doSpecialSkill(byte id) {
      switch(id) {
      case 21:
         this.scaryOrb();
         return;
      default:
         super.doSpecialSkill(id);
      }
   }

   private void scaryOrb() {
      if (this.borderOrb != -1 && (this.orbVersionCooldown <= 0 || this.borderOrb != 0)) {
         if (this.borderOrb == 0) {
            AxisAlignedBB axisalignedbb = (new AxisAlignedBB(this.posX, this.posY, this.posZ, this.posX + 1.0D, this.posY + 1.0D, this.posZ + 1.0D)).grow((double)(this.fuseOrb / 2 + 1));

            for(EntityPMalleable mob : this.world.getEntitiesWithinAABB(EntityPMalleable.class, axisalignedbb)) {
               if (mob != this && mob.getParasiteType() <= this.getParasiteType()) {
                  mob.setOrbVersionCooldown(4);
               }
            }
         }

         this.setParasiteStatus(9);
         this.getNavigator().clearPath();
         if (this.ticksExisted % 20 == 0) {
            ++this.borderOrb;
            if (this.borderOrb == 1) {
               Entity ttt = new EntityScaryOrb(this.world, this, this.fuseOrb, this.orbStartTimer);
               ttt.copyLocationAndAnglesFrom(this);
               this.world.spawnEntity(ttt);
               this.playSound(SRPSounds.ORB_S, 1.0F, 1.0F);
            }

            if (this.borderOrb > 3) {
               this.skillOrb = true;
               this.setParasiteStatus(0);
               this.borderOrb = 0;
               this.limitOrb = 0;
            }

         }
      } else {
         this.skillOrb = true;
      }
   }
}
