package com.lycanitesmobs.core.block.effect;

import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.ObjectManager;
import com.lycanitesmobs.client.AssetManager;
import com.lycanitesmobs.core.block.BlockFireBase;
import com.lycanitesmobs.core.config.ConfigBase;
import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.Item;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockDoomfire extends BlockFireBase {
   public BlockDoomfire() {
      super(Material.FIRE, LycanitesMobs.modInfo, "doomfire");
      this.tickRate = 30;
      this.dieInRain = false;
      this.triggerTNT = false;
      this.agingRate = 8;
      this.spreadChance = 3.0F;
      this.removeOnTick = !ConfigBase.getConfig(this.group, "general").getBool("Features", "Enable Doomfire", true);
      this.removeOnNoFireTick = ConfigBase.getConfig(this.group, "general").getBool("Features", "Remove Doomfire on No Fire Tick", false);
      this.setLightOpacity(1);
      this.setLightLevel(0.8F);
   }

   public Item getItemDropped(IBlockState state, EaglercraftRandom random, int zero) {
      return null;
   }

   public void onEntityCollidedWithBlock(World world, BlockPos pos, IBlockState state, Entity entity) {
      super.onEntityCollidedWithBlock(world, pos, state, entity);
      if (entity instanceof EntityLivingBase) {
         Potion decay = ObjectManager.getEffect("decay");
         if (decay != null) {
            PotionEffect effect = new PotionEffect(decay, 100, 0);
            EntityLivingBase entityLiving = (EntityLivingBase)entity;
            if (entityLiving.isPotionApplicable(effect)) {
               entityLiving.addPotionEffect(effect);
            }
         }
      }

      if (!(entity instanceof EntityItem) || ((EntityItem)entity).getItem().getItem() != ObjectManager.getItem("hellfirecharge")) {
         if (!entity.isImmuneToFire()) {
            entity.attackEntityFrom(DamageSource.IN_FIRE, 2.0F);
            entity.setFire(5);
         }
      }
   }

   @SideOnly(Side.CLIENT)
   public void randomDisplayTick(IBlockState state, World world, BlockPos pos, EaglercraftRandom random) {
      super.randomDisplayTick(state, world, pos, random);
      int x = pos.getX();
      int y = pos.getY();
      int z = pos.getZ();
      if (random.nextInt(24) == 0) {
         world.playSound((double)((float)x + 0.5F), (double)((float)y + 0.5F), (double)((float)z + 0.5F), AssetManager.getSound("doomfire"), SoundCategory.BLOCKS, 0.5F + random.nextFloat(), random.nextFloat() * 0.7F + 0.3F, false);
      }

      for(int particleCount = 0; particleCount < 12; ++particleCount) {
         float particleX = (float)x + random.nextFloat();
         float particleY = (float)y + random.nextFloat() * 0.5F;
         float particleZ = (float)z + random.nextFloat();
         world.spawnParticle(EnumParticleTypes.REDSTONE, (double)particleX, (double)particleY, (double)particleZ, 0.0D, 0.0D, 0.0D, new int[0]);
      }

   }
}
