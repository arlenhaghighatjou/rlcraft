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

public class EntityHellfireBarrier extends BaseProjectileEntity {
   public EntityHellfireWall[][] hellfireWalls;
   protected int hellfireWidth = 5;
   protected int hellfireHeight = 3;
   protected int hellfireSize = 10;
   public boolean wall = false;
   public int time = 0;
   public int timeMax = 40;
   public float angle = 90.0F;
   public double rotation = 0.0D;

   public EntityHellfireBarrier(World par1World) {
      super(par1World);
   }

   public EntityHellfireBarrier(World par1World, EntityLivingBase par2EntityLivingBase) {
      super(par1World, par2EntityLivingBase);
   }

   public EntityHellfireBarrier(World par1World, double par2, double par4, double par6) {
      super(par1World, par2, par4, par6);
   }

   public void setup() {
      this.entityName = "hellfirebarrier";
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
                     if (this.wall) {
                        this.hellfireWalls[row][col] = new EntityHellfireWall(this.getEntityWorld(), this.getThrower());
                     } else {
                        this.hellfireWalls[row][col] = new EntityHellfireBarrierPart(this.getEntityWorld(), this.getThrower());
                     }
                  } else if (this.wall) {
                     this.hellfireWalls[row][col] = new EntityHellfireWall(this.getEntityWorld(), this.posX, this.posY + (double)(this.hellfireSize * row), this.posZ);
                  } else {
                     this.hellfireWalls[row][col] = new EntityHellfireBarrierPart(this.getEntityWorld(), this.posX, this.posY + (double)(this.hellfireSize * row), this.posZ);
                  }

                  double rotationRadians = Math.toRadians(this.rotation);
                  double x = (double)((float)col / (float)this.hellfireWidth * (float)(this.hellfireSize * (this.hellfireWidth - 1))) * Math.cos(rotationRadians) + Math.sin(rotationRadians);
                  double z = (double)((float)col / (float)this.hellfireWidth * (float)(this.hellfireSize * (this.hellfireWidth - 1))) * Math.sin(rotationRadians) - Math.cos(rotationRadians);
                  this.hellfireWalls[row][col].posX = this.posX + x;
                  this.hellfireWalls[row][col].posY = this.posY + (double)(this.hellfireSize * row);
                  this.hellfireWalls[row][col].posZ = this.posZ + z;
                  this.hellfireWalls[row][col].projectileLife = 40;
                  this.getEntityWorld().spawnEntity(this.hellfireWalls[row][col]);
                  this.hellfireWalls[row][col].setProjectileScale((float)this.hellfireSize * 2.5F);
               }
            }
         }

         for(int row = 0; row < this.hellfireHeight; ++row) {
            for(int col = 0; col < this.hellfireWidth; ++col) {
               double rotationRadians = Math.toRadians(this.rotation);
               double x = (double)((float)col / (float)this.hellfireWidth * (float)(this.hellfireSize * (this.hellfireWidth - 1))) * Math.cos(rotationRadians) + Math.sin(rotationRadians);
               double z = (double)((float)col / (float)this.hellfireWidth * (float)(this.hellfireSize * (this.hellfireWidth - 1))) * Math.sin(rotationRadians) - Math.cos(rotationRadians);
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
