package com.teamacronymcoders.base.materialsystem.entity.minecart;

import com.teamacronymcoders.base.entities.EntityMinecartBase;
import com.teamacronymcoders.base.materialsystem.MaterialSystem;
import com.teamacronymcoders.base.materialsystem.items.minecart.ItemMaterialMinecart;
import com.teamacronymcoders.base.materialsystem.materialparts.MaterialPart;
import net.minecraft.item.ItemMinecart;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.world.World;

public class EntityMaterialMinecart extends EntityMinecartBase {
   private MaterialPart materialPart;
   private static final DataParameter<String> MATERIAL_PART_STRING = EntityDataManager.createKey(EntityMaterialMinecart.class, DataSerializers.STRING);

   public EntityMaterialMinecart(World world) {
      super(world);
   }

   public EntityMaterialMinecart(World world, MaterialPart materialPart) {
      this(world);
      this.materialPart = materialPart;
      this.dataManager.set(MATERIAL_PART_STRING, materialPart.getUnlocalizedName());
   }

   protected void entityInit() {
      super.entityInit();
      this.dataManager.register(MATERIAL_PART_STRING, "");
   }

   public ItemStack getCartItem() {
      return this.getMaterialPart().getItemStack();
   }

   public boolean canBeRidden() {
      return true;
   }

   public ItemMinecart getItem() {
      return (ItemMaterialMinecart)this.getMaterialPart().getItemStack().getItem();
   }

   public MaterialPart getMaterialPart() {
      if (this.materialPart == null) {
         this.materialPart = MaterialSystem.getMaterialPart((String)this.dataManager.get(MATERIAL_PART_STRING));
      }

      return this.materialPart;
   }

   public String getName() {
      String name;
      if (this.hasCustomName()) {
         name = this.getCustomNameTag();
      } else {
         name = this.getMaterialPart().getLocalizedName();
      }

      return name;
   }

   public void readEntityFromNBT(NBTTagCompound nbtTagCompound) {
      this.dataManager.set(MATERIAL_PART_STRING, nbtTagCompound.getString("material_part"));
   }

   public void writeEntityToNBT(NBTTagCompound nbtTagCompound) {
      nbtTagCompound.setString("material_part", this.getMaterialPart().getUnlocalizedName());
   }
}
