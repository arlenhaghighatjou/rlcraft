package com.dhanantry.scapeandrunparasites.entity;

import com.dhanantry.scapeandrunparasites.client.particle.ParticleSpawner;
import com.dhanantry.scapeandrunparasites.client.particle.SRPEnumParticle;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;
import com.dhanantry.scapeandrunparasites.init.SRPBlocks;
import com.dhanantry.scapeandrunparasites.init.SRPPotions;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class EntityRemain extends Entity {
   private int plus;
   private int count;
   private int goal;
   private boolean active;
   private String parasite;
   private float health;
   private byte skin;

   public EntityRemain(World in) {
      super(in);
      this.setSize(0.5F, 0.5F);
      this.active = false;
   }

   public void onUpdate() {
      super.onUpdate();
      if (!this.world.isRemote) {
         if (this.ticksExisted % 20 == 0 && (this.world.getBlockState(this.getPosition()).getBlock() != SRPBlocks.Gore || this.parasite == null)) {
            this.setDead();
         }

         if (this.active) {
            this.count += this.plus;
            if (this.count > this.goal) {
               EntityParasiteBase out = (EntityParasiteBase)EntityList.createEntityByIDFromName(new ResourceLocation(this.parasite), this.world);
               out.copyLocationAndAnglesFrom(this);
               out.onInitialSpawn(this.world.getDifficultyForLocation(new BlockPos(out)), (IEntityLivingData)null);
               out.setSkin(this.skin);
               out.setHealth(out.getMaxHealth() * this.health);
               this.world.spawnEntity(out);
               out.particleStatus((byte)7);
               out.addPotionEffect(new PotionEffect(SRPPotions.DEBAR_E, 400, 0, false, false));
               this.world.setBlockState(this.getPosition(), Blocks.AIR.getDefaultState());
               this.setDead();
            }

            if (this.count % 10 == 0) {
               this.world.setEntityState(this, (byte)18);
            }
         }
      }

   }

   public float getEyeHeight() {
      return this.height;
   }

   public boolean canBeCollidedWith() {
      return true;
   }

   public void setParasite(String in) {
      this.parasite = in;
   }

   public void setGoal(int g) {
      this.goal = g;
   }

   public void setPlus(int p) {
      if (this.plus < p) {
         this.plus = p;
         this.active = true;
      }
   }

   public void setSkin(byte in) {
      this.skin = in;
   }

   public void setHealth(float in) {
      if (this.health < in) {
         this.health = in;
      }
   }

   public boolean getActive() {
      return this.active;
   }

   protected void readEntityFromNBT(NBTTagCompound compound) {
      if (compound.hasKey("parasiteparasite", 8)) {
         this.parasite = compound.getString("parasiteparasite").toString();
      }

      if (compound.hasKey("parasiteactive", 99)) {
         this.active = compound.getBoolean("parasiteactive");
      }

      if (compound.hasKey("parasitepoint", 99)) {
         this.count = compound.getInteger("parasitepoint");
      }

      if (compound.hasKey("parasiteplus", 99)) {
         this.plus = compound.getInteger("parasiteplus");
      }

      if (compound.hasKey("parasitegoal", 99)) {
         this.goal = compound.getInteger("parasitegoal");
      }

      if (compound.hasKey("parasiteskin", 99)) {
         this.skin = compound.getByte("parasiteskin");
      }

      if (compound.hasKey("parasitehealth", 99)) {
         this.health = compound.getFloat("parasitehealth");
      }

   }

   protected void writeEntityToNBT(NBTTagCompound compound) {
      if (this.parasite != null) {
         compound.setString("parasiteparasite", this.parasite);
      }

      compound.setBoolean("parasiteactive", this.active);
      compound.setInteger("parasitepoint", this.count);
      compound.setInteger("parasiteplus", this.plus);
      compound.setInteger("parasitegoal", this.goal);
      compound.setByte("parasiteskin", this.skin);
      compound.setFloat("parasitehealth", this.health);
   }

   public void handleStatusUpdate(byte id) {
      if (id == 18) {
         for(int i = 0; i <= 1; ++i) {
            this.spawnParticles(SRPEnumParticle.BIOMASS, 0, 0, 0);
         }
      } else {
         super.handleStatusUpdate(id);
      }

   }

   @SideOnly(Side.CLIENT)
   public void spawnParticles(SRPEnumParticle particleType, int r, int g, int b) {
      double d0 = this.rand.nextGaussian() * 0.02D;
      double d1 = this.rand.nextGaussian() * 0.02D;
      double d2 = this.rand.nextGaussian() * 0.02D;
      ParticleSpawner.spawnParticle(particleType, this.posX + (double)(this.rand.nextFloat() * this.width * 2.0F) - (double)this.width, this.posY + 0.5D + (double)this.rand.nextFloat() * ((double)this.height + 0.5D), this.posZ + (double)(this.rand.nextFloat() * this.width * 2.0F) - (double)this.width, d0, d1, d2, r, g, b);
   }

   protected void entityInit() {
   }
}
