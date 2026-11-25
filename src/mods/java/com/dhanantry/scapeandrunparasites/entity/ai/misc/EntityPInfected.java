package com.dhanantry.scapeandrunparasites.entity.ai.misc;

import com.dhanantry.scapeandrunparasites.SRPMain;
import com.dhanantry.scapeandrunparasites.block.BlockGore;
import com.dhanantry.scapeandrunparasites.client.particle.SRPEnumParticle;
import com.dhanantry.scapeandrunparasites.entity.EntityRemain;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIInfectedSearch;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAINearestAttackableTargetStatus;
import com.dhanantry.scapeandrunparasites.entity.projectile.EntityGore;
import com.dhanantry.scapeandrunparasites.init.SRPBlocks;
import com.dhanantry.scapeandrunparasites.init.SRPPotions;
import com.dhanantry.scapeandrunparasites.network.SRPPacketParticle;
import com.dhanantry.scapeandrunparasites.util.ParasiteEventEntity;
import com.dhanantry.scapeandrunparasites.util.SRPAttributes;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfig;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfigSystems;
import com.google.common.base.Predicate;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockBush;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.passive.EntityWaterMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.MobEffects;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public abstract class EntityPInfected extends EntityParasiteBase {
   private String host;
   private int check;
   private int timer;
   private int tiick;
   protected boolean thisMelting;

   public EntityPInfected(World worldIn) {
      super(worldIn);
      this.targetTasks.addTask(4, new EntityAINearestAttackableTargetStatus(this, EntityPlayer.class, 0, true, false, (Predicate)null, SRPConfig.infectedSneakPen, SRPConfig.infectedInviPen));
      if (SRPConfig.mobattacking) {
         this.targetTasks.addTask(4, new EntityAINearestAttackableTargetStatus(this, EntityLiving.class, 0, true, !SRPConfigSystems.useOneMind, new Predicate<EntityLiving>() {
            public boolean apply(@Nullable EntityLiving entity) {
               return !(entity instanceof EntityWaterMob) && !(entity instanceof EntityAnimal) && !(entity instanceof EntityVillager) && !ParasiteEventEntity.checkEntity(entity, SRPConfig.mobattackingBlackList, SRPConfig.mobattackingBlackListWhite);
            }
         }, SRPConfig.infectedSneakPen, SRPConfig.infectedInviPen));
      }

      this.experienceValue = SRPAttributes.XP_INFECTED;
      this.damageCap = SRPConfig.infectedCap;
      this.canD = SRPConfig.infecteddespawn;
      this.MiniDamage = SRPConfig.infectedMinDamage;
      this.oneMindDeathValue = SRPConfig.infectedOneMindDeathV;
      this.foodSteal = 0.1F;
      this.tiick = 0;
      this.check = 0;
      this.timer = 0;
      this.host = "";
      this.valueEvDeath = SRPConfig.infectedLoosingEPValue;
      this.thisMelting = false;
   }

   public void onLivingUpdate() {
      super.onLivingUpdate();
      ++this.tiick;
      if (this.tiick >= 20 && !this.world.isRemote) {
         this.tiick = 0;
         if (this.killcount > SRPConfig.primitiveKills) {
            this.tasks.addTask(3, new EntityAIInfectedSearch(this, 1.1D, this.thisMelting));
            this.killcount = 0.0D;
         }

         if (this.host.length() != 0) {
            if (this.check == 0) {
               EntityLivingBase entityout = (EntityLivingBase)EntityList.createEntityByIDFromName(new ResourceLocation(this.host), this.world);
               if (entityout != null && !(entityout instanceof EntityMob) && SRPConfig.infectedAssimilation) {
                  this.check = 2;
                  this.world.setEntityState(this, (byte)14);
               } else {
                  this.check = 1;
                  this.world.setEntityState(this, (byte)15);
               }
            }

            ++this.timer;
            if (this.check >= 2 && this.timer > 20) {
               if (SRPConfigSystems.useEvolution) {
                  if (this.phaseCreated < SRPConfigSystems.evolutionAssimilatedDehiding) {
                     this.transform();
                  }
               } else {
                  this.transform();
               }
            }
         }

         this.InfectNearby(this, SRPConfigSystems.cothAura);
      }

      if (this.check >= 3 && this.world.isRemote) {
         this.spawnParticles(SRPEnumParticle.GSPLASH, 0, 0, 0);
      }

   }

   protected void fearPlayer(EntityLivingBase player) {
      try {
         if (player == null) {
            return;
         }

         if (!this.canEntityBeSeen(player)) {
            return;
         }

         if (!player.isPotionActive(SRPPotions.FEAR_E)) {
            player.addPotionEffect(new PotionEffect(SRPPotions.FEAR_E, 300, 0, false, false));
         } else if (player.getActivePotionEffect(SRPPotions.FEAR_E).getAmplifier() < 0) {
            player.addPotionEffect(new PotionEffect(SRPPotions.FEAR_E, 300, 0, false, false));
         }
      } catch (Exception var3) {
         ;
      }

   }

   private void transform() {
      if (this.getAttackTarget() == null && !this.isPotionActive(SRPPotions.EPEL_E)) {
         AxisAlignedBB axisalignedbb = (new AxisAlignedBB(this.posX, this.posY, this.posZ, this.posX + 1.0D, this.posY + 1.0D, this.posZ + 1.0D)).grow(10.0D, 3.0D, 10.0D);
         List<EntityLivingBase> moblist = this.world.getEntitiesWithinAABB(EntityLivingBase.class, axisalignedbb);
         boolean flag = true;

         for(EntityLivingBase mob : moblist) {
            if (mob != this && !(mob instanceof EntityParasiteBase)) {
               if (mob instanceof EntityPlayer) {
                  EntityPlayer player = (EntityPlayer)mob;
                  if (player.isCreative()) {
                     continue;
                  }
               }

               NBTTagCompound tags = mob.getEntityData();
               if (!tags.hasKey("srpcothimmunity") || tags.getInteger("srpcothimmunity") == 0) {
                  flag = false;
                  this.check = 2;
                  this.timer = 0;
                  this.world.setEntityState(this, (byte)14);
                  break;
               }
            }
         }

         if (flag) {
            ++this.check;
            this.world.setEntityState(this, (byte)16);
            this.addPotionEffect(new PotionEffect(MobEffects.SLOWNESS, 60, 3, false, false));
            SRPMain.network.sendToAll(new SRPPacketParticle(this.posX, this.posY, this.posZ, this.width, this.height, (byte)1));
            if (this.timer < 25) {
               return;
            }

            EntityLivingBase entityout = (EntityLivingBase)EntityList.createEntityByIDFromName(new ResourceLocation(this.host), this.world);
            entityout.copyLocationAndAnglesFrom(this);
            this.particleStatus((byte)7);
            this.world.removeEntity(this);
            if (this.hasCustomName()) {
               entityout.setCustomNameTag(this.getCustomNameTag());
               entityout.setAlwaysRenderNameTag(this.getAlwaysRenderNameTag());
            }

            entityout.addPotionEffect(new PotionEffect(SRPPotions.COTH_E, 3600, 2, false, false));
            this.world.spawnEntity(entityout);
            this.world.playEvent((EntityPlayer)null, 1026, new BlockPos(entityout), 0);
         }

      } else {
         this.timer = -200;
      }
   }

   public boolean attackEntityAsMob(Entity entityIn) {
      boolean flag = super.attackEntityAsMob(entityIn);
      if (flag && entityIn instanceof EntityLivingBase && this.rand.nextDouble() < (double)SRPConfigSystems.cothInfected) {
         EntityLivingBase target = (EntityLivingBase)entityIn;
         if (!target.isPotionActive(SRPPotions.COTH_E)) {
            if (target.isPotionActive(SRPPotions.EPEL_E)) {
               if (this.rand.nextDouble() >= SRPConfigSystems.epelChanceCOTH) {
                  target.addPotionEffect(new PotionEffect(SRPPotions.COTH_E, 3600, 0, false, false));
               }
            } else {
               target.addPotionEffect(new PotionEffect(SRPPotions.COTH_E, 3600, 0, false, false));
            }
         }
      }

      return flag;
   }

   public boolean attackEntityFrom(DamageSource source, float amount) {
      this.timer = -200;
      return super.attackEntityFrom(source, amount);
   }

   public void onKillEntity(EntityLivingBase entityLivingIn) {
      super.onKillEntity(entityLivingIn);
      this.particleStatus((byte)5);
      if (!this.world.isRemote && this.killcount > SRPConfig.primitiveKills) {
         this.tasks.addTask(3, new EntityAIInfectedSearch(this, 1.1D, this.thisMelting));
         this.killcount = 0.0D;
      }

   }

   private void InfectNearby(EntityLivingBase entity, int range) {
      if (SRPConfigSystems.cothAura != 0) {
         AxisAlignedBB axisalignedbb = (new AxisAlignedBB(entity.posX, entity.posY, entity.posZ, entity.posX + 1.0D, entity.posY + 1.0D, entity.posZ + 1.0D)).grow((double)range);

         for(EntityLivingBase mob : entity.world.getEntitiesWithinAABB(EntityLivingBase.class, axisalignedbb)) {
            if (mob != entity && !mob.isPotionActive(SRPPotions.COTH_E)) {
               if (mob.isPotionActive(SRPPotions.EPEL_E)) {
                  if (mob.world.rand.nextDouble() >= SRPConfigSystems.epelChanceCOTH) {
                     mob.addPotionEffect(new PotionEffect(SRPPotions.COTH_E, 4800, 0, false, false));
                  }
               } else {
                  mob.addPotionEffect(new PotionEffect(SRPPotions.COTH_E, 4800, 0, false, false));
               }
            }
         }

      }
   }

   public void setHost(String mobname) {
      this.host = mobname;
   }

   public String getHost() {
      return this.host;
   }

   protected void spawnGore() {
      int range = 2;
      double i1 = (double)MathHelper.floor(this.posY + 0.1D);
      double l1 = this.posX;
      double i2 = this.posZ;

      for(int k2 = -1 * range; k2 <= 1 * range && SRPConfig.paraGore; ++k2) {
         for(int l2 = -1 * range; l2 <= 1 * range; ++l2) {
            double i3 = l1 + (double)k2;
            double l = i2 + (double)l2;
            BlockPos blockpos = new BlockPos(i3, i1, l);
            Block block = this.world.getBlockState(blockpos).getBlock();
            Block blockDown = this.world.getBlockState(blockpos.down()).getBlock();
            if (block == Blocks.AIR && blockDown != Blocks.AIR && this.world.getBlockState(blockpos.down()).isFullBlock() && blockDown != SRPBlocks.InfestedStain && this.world.rand.nextInt(6) == 0) {
               this.world.setBlockState(blockpos, SRPBlocks.Gore.getDefaultState().withProperty(BlockGore.VARIANT, BlockGore.EnumType.SIM_FLAT));
            }
         }
      }

      if (this.world.getBlockState(this.getPosition().down()).isFullBlock() && (this.world.getBlockState(this.getPosition()).getBlock() instanceof BlockBush || this.world.getBlockState(this.getPosition()).getBlock() == Blocks.AIR)) {
         this.world.setBlockState(this.getPosition(), SRPBlocks.Gore.getDefaultState().withProperty(BlockGore.VARIANT, BlockGore.EnumType.SIM_BIG));
         EntityRemain nnn = new EntityRemain(this.world);
         nnn.setLocationAndAngles((double)this.getPosition().getX() + 0.5D, (double)this.getPosition().getY(), (double)this.getPosition().getZ() + 0.5D, 0.0F, 0.0F);
         nnn.setParasite(EntityList.getKey(this).toString());
         nnn.setSkin((byte)this.getSkin());
         nnn.setGoal(20 * SRPConfig.infectedRemainValue);
         this.world.spawnEntity(nnn);
      }

      for(int i = 0; i < 3 && SRPConfig.paraGore; ++i) {
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
         bomb.setType((byte)1);
         bomb.copyLocationAndAnglesFrom(this);
         bomb.setMotion(d3, d4, d5, 0.1D, 0.5D);
         this.world.spawnEntity(bomb);
      }

   }

   @SideOnly(Side.CLIENT)
   public void spawnEffectsGore() {
      for(int i = 0; i <= 100; ++i) {
         if (i % 5 == 0) {
            this.spawnParticlesGore(SRPEnumParticle.GSPLASH, 0, -1, -1);
         }
      }

      for(int i = 0; i <= 20; ++i) {
         if (i % 5 == 0) {
            this.spawnParticles(SRPEnumParticle.GCLOUD, 127, 0, 0);
         }

         if (i % 5 == 0) {
            this.spawnParticles(SRPEnumParticle.GSPLASH, 0, -1, -1);
         }
      }

   }

   public void writeEntityToNBT(NBTTagCompound compound) {
      super.writeEntityToNBT(compound);
      compound.setString("parasitehost", this.host);
   }

   public void readEntityFromNBT(NBTTagCompound compound) {
      super.readEntityFromNBT(compound);
      if (compound.hasKey("parasitehost", 8)) {
         this.setHost(compound.getString("parasitehost").toString());
      }

   }

   @SideOnly(Side.CLIENT)
   public void handleStatusUpdate(byte id) {
      if (id == 14) {
         this.check = 2;
      } else if (id == 15) {
         this.check = 1;
      } else if (id == 16) {
         ++this.check;
      } else {
         super.handleStatusUpdate(id);
      }

   }
}
