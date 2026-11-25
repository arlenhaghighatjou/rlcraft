package com.lycanitesmobs.core.entity.projectile;

import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.api.IGroupDemon;
import com.lycanitesmobs.client.AssetManager;
import com.lycanitesmobs.core.entity.BaseProjectileEntity;
import com.lycanitesmobs.core.entity.creature.EntityRahovart;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class EntityHellfireWave extends BaseProjectileEntity {
   public EntityHellfireWall[][] hellfireWalls;
   protected int hellfireWidth = 5;
   protected int hellfireHeight = 3;
   protected int hellfireSize = 10;
   public int time = 0;
   public int timeMax = 200;
   public float angle = 90.0F;
   public double rotation = 0.0D;

   public EntityHellfireWave(World world) {
      super(world);
   }

   public EntityHellfireWave(World world, EntityLivingBase shooterEntity) {
      super(world, shooterEntity);
   }

   public EntityHellfireWave(World world, double x, double y, double z) {
      super(world, x, y, z);
   }

   public void setup() {
      this.entityName = "hellfirewave";
      this.modInfo = LycanitesMobs.modInfo;
      this.setDamage(0);
      this.setProjectileScale(0.0F);
      this.setSize(2.0F, 2.0F);
      this.movement = false;
      this.ripper = true;
      this.pierceBlocks = true;
      this.projectileLife = 100;
      this.animationFrameMax = 59;
      this.noClip = true;
      this.waterProof = true;
      this.lavaProof = true;
   }

   public boolean isBurning() {
      return false;
   }

   public void onUpdate() {
      if (!this.getEntityWorld().isRemote) {
         if (this.time++ >= this.timeMax) {
            this.setDead();
         }

         if (this.hellfireWalls == null) {
            this.hellfireWalls = new EntityHellfireWall[this.hellfireHeight][this.hellfireWidth];

            for(int row = 0; row < this.hellfireHeight; ++row) {
               for(int col = 0; col < this.hellfireWidth; ++col) {
                  if (this.getThrower() != null) {
                     this.hellfireWalls[row][col] = new EntityHellfireWavePart(this.getEntityWorld(), this.getThrower());
                  } else {
                     this.hellfireWalls[row][col] = new EntityHellfireWavePart(this.getEntityWorld(), this.posX, this.posY + 5.0D + (double)(this.hellfireSize * row), this.posZ);
                  }

                  this.hellfireWalls[row][col].posY = this.posY + (double)(this.hellfireSize * row);
                  this.getEntityWorld().spawnEntity(this.hellfireWalls[row][col]);
                  this.hellfireWalls[row][col].setProjectileScale((float)(this.hellfireSize * 2));
               }
            }
         }

         for(int row = 0; row < this.hellfireHeight; ++row) {
            for(int col = 0; col < this.hellfireWidth; ++col) {
               double rotationRadians = Math.toRadians(((double)((float)col / (float)this.hellfireWidth * this.angle - this.angle / 2.0F) + this.rotation) % 360.0D);
               double x = (double)((float)this.time / (float)this.timeMax * 200.0F) * Math.cos(rotationRadians) - Math.sin(rotationRadians);
               double z = (double)((float)this.time / (float)this.timeMax * 200.0F) * Math.sin(rotationRadians) + Math.cos(rotationRadians);
               this.hellfireWalls[row][col].posX = this.posX + x;
               this.hellfireWalls[row][col].posY = this.posY + (double)(this.hellfireSize * row);
               this.hellfireWalls[row][col].posZ = this.posZ + z;
               this.hellfireWalls[row][col].projectileLife = 40;
               if (this.isDead) {
                  this.hellfireWalls[row][col].setDead();
               }
            }
         }

      }
   }

   public boolean onEntityLivingDamage(EntityLivingBase entityLiving) {
      if (!entityLiving.isImmuneToFire()) {
         entityLiving.setFire(this.getEffectDuration(10) / 20);
      }

      return true;
   }

   public boolean canDamage(EntityLivingBase targetEntity) {
      EntityLivingBase owner = this.getThrower();
      if (owner == null) {
         if (targetEntity instanceof EntityRahovart) {
            return false;
         }

         if (targetEntity instanceof IGroupDemon) {
            return false;
         }
      }

      return super.canDamage(targetEntity);
   }

   public SoundEvent getLaunchSound() {
      return AssetManager.getSound("hellfirewave");
   }

   public float getBrightness() {
      return 1.0F;
   }

   @SideOnly(Side.CLIENT)
   public int getBrightnessForRender() {
      return 15728880;
   }

   public ResourceLocation getTexture() {
      if (AssetManager.getTexture("hellfirewall") == null) {
         AssetManager.addTexture("hellfirewall", this.modInfo, "textures/items/hellfirewall.png");
      }

      return AssetManager.getTexture("hellfirewall");
   }
}
