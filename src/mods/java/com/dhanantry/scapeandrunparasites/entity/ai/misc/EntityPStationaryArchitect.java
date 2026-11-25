package com.dhanantry.scapeandrunparasites.entity.ai.misc;

import com.dhanantry.scapeandrunparasites.block.BlockInfestedStain;
import com.dhanantry.scapeandrunparasites.client.particle.ParticleSpawner;
import com.dhanantry.scapeandrunparasites.client.particle.SRPEnumParticle;
import com.dhanantry.scapeandrunparasites.entity.projectile.EntityBomb;
import com.dhanantry.scapeandrunparasites.init.SRPBlocks;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfig;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfigSystems;
import com.dhanantry.scapeandrunparasites.world.SRPWorldData;
import com.dhanantry.scapeandrunparasites.world.biome.BiomeParasite;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public abstract class EntityPStationaryArchitect extends EntityPStationary implements EntityCanSummon {
   protected int totalP;
   protected int actualP;
   protected boolean convert = true;
   protected int[] mobID;
   protected int[] mobPT;
   protected boolean rsSpawned = false;
   protected byte stage;
   protected float body;
   public int neededTime;
   protected int actualTime;
   protected int topParticles;
   private boolean canGrowTo;
   private int bombC;

   public EntityPStationaryArchitect(World worldIn) {
      super(worldIn);
      this.killcount = -10.0D;
      this.canD = SRPConfig.rsDespawn;
      this.canGrowTo = true;
      this.borderOrb = -1;
   }

   public void setCanGrowTo(boolean in) {
      this.canGrowTo = in;
   }

   public void setConvert(boolean in) {
      this.convert = in;
   }

   public int setGT(int minGrowTime, int maxGrowTime) {
      int atm = maxGrowTime - minGrowTime + 1;
      return atm > 0 && maxGrowTime > minGrowTime ? this.rand.nextInt(atm) + minGrowTime : 1;
   }

   public void setActualT(int in) {
      if (this.canGrowTo) {
         this.actualTime = in;
      }

   }

   public int getActualT() {
      return this.actualTime;
   }

   public int getNeededTime() {
      return this.neededTime;
   }

   public void onLivingUpdate() {
      super.onLivingUpdate();
      if (this.topParticles > 0) {
         --this.topParticles;
      }

      this.freeDead();
      if (this.bombC > 0) {
         --this.bombC;
      }

   }

   protected void freeDead() {
      if (!this.world.isRemote && this.isDead) {
         if (this.convert || this.getRSChance(this.phaseCreated) == 0.0D) {
            World world = this.world;
            BlockPos floor = (new BlockPos(this)).down();
            Block bbb = world.getBlockState(floor).getBlock();
            if (bbb == SRPBlocks.InfestedStain || bbb == SRPBlocks.InfestedRubble) {
               int meta = bbb.getMetaFromState(world.getBlockState(floor));
               if (meta <= SRPConfigSystems.rsBlockRevertStage && meta <= this.stage || this.getRSChance(this.phaseCreated) == 0.0D) {
                  this.world.setBlockState(floor, SRPBlocks.InfestedStain.getDefaultState().withProperty(BlockInfestedStain.STAGE, Integer.valueOf(5)));
                  world.updateBlockTick(floor, SRPBlocks.InfestedStain.getDefaultState().withProperty(BlockInfestedStain.STAGE, Integer.valueOf(5)).getBlock(), 40, 5);
               }
            }
         }

         if (this.rsSpawned) {
            SRPWorldData data = SRPWorldData.get(this.world);
            data.setCurrentV(-1);
         }
      }

   }

   public float getBODY() {
      return this.body;
   }

   public byte getStageV() {
      return this.stage;
   }

   public void addID(int id, int points) {
      for(int i = 0; i < this.mobID.length; ++i) {
         if (this.mobID[i] == -777) {
            this.mobID[i] = id;
            this.mobPT[i] = points;
            return;
         }
      }

   }

   public int IDable() {
      int flag = 0;

      for(int i = 0; i < this.mobID.length; ++i) {
         if (this.mobID[i] == -777) {
            ++flag;
         }
      }

      if (flag > this.totalP) {
         flag = this.totalP;
      }

      return flag;
   }

   public void checkID() {
      for(int i = 0; i < this.mobID.length; ++i) {
         if (this.mobID[i] > 0) {
            Entity flag = this.world.getEntityByID(this.mobID[i]);
            if (flag == null) {
               this.mobID[i] = -777;
               int negative = this.mobPT[i] * -1;
               this.setActualParasites(negative);
            }
         }
      }

   }

   public int[] getIDList() {
      return this.mobID;
   }

   public int[] getPointList() {
      return this.mobPT;
   }

   public int getTotalParasites() {
      return this.totalP;
   }

   public int getActualParasites() {
      return this.actualP;
   }

   public void setActualParasites(int i) {
      this.actualP += i;
   }

   public void setRSSpawned(boolean in) {
      this.rsSpawned = in;
   }

   public boolean getRSSpawned() {
      return this.rsSpawned;
   }

   public abstract float getBombDamage();

   public boolean canDespawn() {
      return this.world.getBiome(this.getPosition()) instanceof BiomeParasite ? true : super.canDespawn();
   }

   public boolean attackEntityFrom(DamageSource source, float amount) {
      float red = this.getParasiteStatus() == 0 ? 0.4F : 1.0F;
      boolean flag = super.attackEntityFrom(source, amount * red);
      if (flag && this.bombC == 0 && source != DamageSource.OUT_OF_WORLD) {
         if (source.getTrueSource() instanceof EntityLivingBase && source.getTrueSource().getDistanceSq(this) > 36.0D) {
            return flag;
         }

         for(int i = 0; i < this.stage * 2; ++i) {
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
            d7 = d7 * (double)(this.world.rand.nextFloat() * this.world.rand.nextFloat() + 0.65F);
            d3 = d3 * d7;
            d4 = d4 * d7 * (double)this.stage * 3.0D;
            d5 = d5 * d7;
            EntityBomb bomb = new EntityBomb(this.world, this, false);
            bomb.setFuse(60);
            bomb.setStren(0.0F);
            bomb.setSkin(1);
            bomb.setDamage(this.getBombDamage(), this.stage);
            bomb.rotationPitch -= -20.0F;
            bomb.copyLocationAndAnglesFrom(this);
            bomb.setMotion(d3, d4, d5, 0.35D, (double)((float)this.stage / 10.0F * 2.0F));
            this.world.spawnEntity(bomb);
            bomb.updateSTR();
         }

         this.bombC = 80;
      }

      return flag;
   }

   public void writeEntityToNBT(NBTTagCompound compound) {
      super.writeEntityToNBT(compound);
      compound.setBoolean("belongtors", this.getRSSpawned());
      compound.setInteger("tickse", this.actualTime);
      compound.setInteger("neededtime", this.neededTime);
      compound.setBoolean("cangrowto", this.canGrowTo);
   }

   public void readEntityFromNBT(NBTTagCompound compound) {
      super.readEntityFromNBT(compound);
      if (compound.hasKey("belongtors", 99)) {
         this.setRSSpawned(compound.getBoolean("belongtors"));
      }

      if (compound.hasKey("tickse", 99)) {
         this.actualTime = compound.getInteger("tickse");
      }

      if (compound.hasKey("neededtime", 99)) {
         this.neededTime = compound.getInteger("neededtime");
      }

      if (compound.hasKey("cangrowto", 99)) {
         this.canGrowTo = compound.getBoolean("cangrowto");
      }

   }

   public void handleStatusUpdate(byte id) {
      if (id == 12) {
         this.topParticles = 20;
      } else {
         super.handleStatusUpdate(id);
      }

   }

   @SideOnly(Side.CLIENT)
   public void spawnParticlesTop(SRPEnumParticle particleType, int r, int g, int b) {
      double d0 = this.rand.nextGaussian() * 0.02D;
      double d1 = this.rand.nextGaussian() * 0.02D;
      double d2 = this.rand.nextGaussian() * 0.02D;
      ParticleSpawner.spawnParticle(particleType, this.posX + (double)(this.rand.nextFloat() * this.width * 2.0F) - (double)this.width, this.posY + (double)this.height + (double)this.rand.nextFloat() * ((double)(this.height - this.getEyeHeight()) + 0.5D), this.posZ + (double)(this.rand.nextFloat() * this.width * 2.0F) - (double)this.width, d0, d1, d2, r, g, b);
   }
}
