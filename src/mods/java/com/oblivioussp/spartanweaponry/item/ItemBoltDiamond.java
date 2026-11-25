package com.oblivioussp.spartanweaponry.item;

import com.oblivioussp.spartanweaponry.entity.projectile.EntityBolt;
import com.oblivioussp.spartanweaponry.entity.projectile.EntityBoltDiamond;
import com.oblivioussp.spartanweaponry.entity.projectile.EntityBoltTipped;
import com.oblivioussp.spartanweaponry.util.ConfigHandler;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ItemBoltDiamond extends ItemBolt {
   public ItemBoltDiamond(String unlocName) {
      super(unlocName, ConfigHandler.boltDiamondBaseDamage, ConfigHandler.boltDiamondRangeMultiplier, ConfigHandler.boltDiamondArmorPiercingFactor);
   }

   public EntityBolt createBolt(World worldIn, ItemStack stack, EntityLivingBase shooter) {
      EntityBoltTipped bolt = new EntityBoltDiamond(worldIn, shooter);
      bolt.setDamage((double)this.baseDamage);
      bolt.setPotionEffect(stack);
      return bolt;
   }

   public boolean isInfinite(ItemStack stack, ItemStack crossbow, EntityPlayer player) {
      return false;
   }
}
