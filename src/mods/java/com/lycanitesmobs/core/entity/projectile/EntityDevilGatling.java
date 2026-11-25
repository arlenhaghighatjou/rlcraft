package com.lycanitesmobs.core.entity.projectile;

import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.ObjectManager;
import com.lycanitesmobs.client.AssetManager;
import com.lycanitesmobs.core.entity.BaseProjectileEntity;
import com.lycanitesmobs.core.info.ObjectLists;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class EntityDevilGatling extends BaseProjectileEntity {
   public Entity shootingEntity;
   public int expireTime = 15;

   public EntityDevilGatling(World world) {
      super(world);
   }

   public EntityDevilGatling(World world, EntityLivingBase entityLivingBase) {
      super(world, entityLivingBase);
   }

   public EntityDevilGatling(World world, double x, double y, double z) {
      super(world, x, y, z);
   }

   public void setup() {
      this.entityName = "devilgatling";
      this.modInfo = LycanitesMobs.modInfo;
      this.setDamage(4);
      this.ripper = true;
   }

   public void onUpdate() {
      super.onUpdate();
      if (this.posY > (double)(this.getEntityWorld().getHeight() + 20)) {
         this.setDead();
      }

      if (this.ticksExisted >= this.expireTime * 20) {
         this.setDead();
      }

   }

   protected float getGravityVelocity() {
      return 0.0F;
   }

   public void onDamage(EntityLivingBase target, float damage, boolean attackSuccess) {
      super.onDamage(target, damage, attackSuccess);
      if (this.rand.nextBoolean()) {
         List<Potion> goodEffects = new ArrayList();

         for(Object potionEffectObj : target.getActivePotionEffects()) {
            if (potionEffectObj instanceof PotionEffect) {
               Potion potion = ((PotionEffect)potionEffectObj).getPotion();
               if (potion != null && ObjectLists.inEffectList("buffs", potion)) {
                  goodEffects.add(potion);
               }
            }
         }

         if (!goodEffects.isEmpty()) {
            if (goodEffects.size() > 1) {
               target.removePotionEffect((Potion)goodEffects.get(this.rand.nextInt(goodEffects.size())));
            } else {
               target.removePotionEffect((Potion)goodEffects.get(0));
            }
         }
      }

      if (ObjectManager.getEffect("decay") != null) {
         target.addPotionEffect(new PotionEffect(ObjectManager.getEffect("decay"), this.getEffectDuration(60), 0));
      }

   }

   public void onImpactVisuals() {
      for(int i = 0; i < 8; ++i) {
         this.getEntityWorld().spawnParticle(EnumParticleTypes.EXPLOSION_NORMAL, this.posX, this.posY, this.posZ, 0.0D, 0.0D, 0.0D, new int[0]);
      }

   }

   public SoundEvent getLaunchSound() {
      return AssetManager.getSound("devilgatling");
   }

   public float getBrightness() {
      return 1.0F;
   }

   @SideOnly(Side.CLIENT)
   public int getBrightnessForRender() {
      return 15728880;
   }
}
