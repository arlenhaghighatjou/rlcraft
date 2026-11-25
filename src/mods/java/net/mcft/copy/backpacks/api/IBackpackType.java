package net.mcft.copy.backpacks.api;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;

public interface IBackpackType {
   void onSpawnedWith(EntityLivingBase var1, IBackpack var2, String var3);

   void onEquip(EntityLivingBase var1, TileEntity var2, IBackpack var3);

   void onUnequip(EntityLivingBase var1, TileEntity var2, IBackpack var3);

   void onPlacedInteract(EntityPlayer var1, TileEntity var2, IBackpack var3);

   void onEquippedInteract(EntityPlayer var1, EntityLivingBase var2, IBackpack var3);

   void onEquippedTick(EntityLivingBase var1, IBackpack var2);

   void onDeath(EntityLivingBase var1, IBackpack var2);

   void onEquippedBroken(EntityLivingBase var1, IBackpack var2);

   void onFaultyRemoval(EntityLivingBase var1, IBackpack var2);

   void onBlockBreak(TileEntity var1, IBackpack var2);

   IBackpackData createBackpackData(ItemStack var1);

   default int getLidMaxTicks() {
      return 5;
   }

   default float getLidAngle(int lidTicks, int prevLidTicks, float partialTicks) {
      float progress = (float)lidTicks + (float)(lidTicks - prevLidTicks) * partialTicks;
      progress = Math.max(0.0F, Math.min((float)this.getLidMaxTicks(), progress)) / (float)this.getLidMaxTicks();
      return (1.0F - (float)Math.pow((double)(1.0F - progress), 2.0D)) * 45.0F;
   }
}
