package noppes.vc.items;

import net.minecraft.entity.Entity;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemArmor.ArmorMaterial;
import net.minecraft.nbt.NBTTagCompound;

public class ItemArmorBasicColorable extends ItemArmorBasic {
   private final String texture;

   public ItemArmorBasicColorable(ArmorMaterial par2EnumArmorMaterial, EntityEquipmentSlot par4, String texture) {
      super(par2EnumArmorMaterial, par4, texture);
      this.texture = texture;
   }

   public String getArmorTexture(ItemStack stack, Entity entity, EntityEquipmentSlot slot, String type) {
      return "overlay".equals(type) ? "variedcommodities:textures/models/armor/" + this.texture + "_1_overlay.png" : "variedcommodities:textures/models/armor/" + this.texture + "_1.png";
   }

   public int getColor(ItemStack par1ItemStack) {
      NBTTagCompound nbttagcompound = par1ItemStack.getTagCompound();
      if (nbttagcompound != null) {
         NBTTagCompound nbttagcompound1 = nbttagcompound.getCompoundTag("display");
         if (nbttagcompound1 != null && nbttagcompound1.hasKey("color", 3)) {
            return nbttagcompound1.getInteger("color");
         }
      }

      return 10511680;
   }

   public void removeColor(ItemStack par1ItemStack) {
      NBTTagCompound nbttagcompound = par1ItemStack.getTagCompound();
      if (nbttagcompound != null) {
         NBTTagCompound nbttagcompound1 = nbttagcompound.getCompoundTag("display");
         if (nbttagcompound1.hasKey("color")) {
            nbttagcompound1.removeTag("color");
         }
      }

   }

   public void setColor(ItemStack par1ItemStack, int par2) {
      NBTTagCompound nbttagcompound = par1ItemStack.getTagCompound();
      if (nbttagcompound == null) {
         nbttagcompound = new NBTTagCompound();
         par1ItemStack.setTagCompound(nbttagcompound);
      }

      NBTTagCompound nbttagcompound1 = nbttagcompound.getCompoundTag("display");
      if (!nbttagcompound.hasKey("display", 10)) {
         nbttagcompound.setTag("display", nbttagcompound1);
      }

      nbttagcompound1.setInteger("color", par2);
   }

   public boolean hasColor(ItemStack par1ItemStack) {
      NBTTagCompound nbttagcompound = par1ItemStack.getTagCompound();
      return nbttagcompound != null && nbttagcompound.hasKey("display", 10) ? nbttagcompound.getCompoundTag("display").hasKey("color", 3) : false;
   }
}
