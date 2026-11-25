package com.teamacronymcoders.base.materialsystem.parttype;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.teamacronymcoders.base.materialsystem.MaterialUser;
import com.teamacronymcoders.base.materialsystem.items.ItemMaterialArmor;
import com.teamacronymcoders.base.materialsystem.materialparts.MaterialPart;
import com.teamacronymcoders.base.materialsystem.partdata.DataPartParsers;
import com.teamacronymcoders.base.materialsystem.partdata.MaterialPartData;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import javax.annotation.Nonnull;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemArmor.ArmorMaterial;
import net.minecraftforge.common.util.EnumHelper;

public class ArmorPartType extends PartType {
   private Map<Integer, ItemStack> chestPlates;

   public ArmorPartType() {
      super("Armor");
      List<PartDataPiece> dataPieceList = Lists.newArrayList();
      dataPieceList.add(new PartDataPiece("enchantability"));
      dataPieceList.add(new PartDataPiece("durability"));
      dataPieceList.add(new PartDataPiece("reduction"));
      dataPieceList.add(new PartDataPiece("toughness"));
      this.setData(dataPieceList);
   }

   public void setup(@Nonnull MaterialPart materialPart, @Nonnull MaterialUser materialUser) {
      ArmorMaterial iron = ArmorMaterial.IRON;
      MaterialPartData data = materialPart.getData();
      int enchantability = ((Integer)data.getValue("enchantability", Integer.valueOf(iron.getEnchantability()), DataPartParsers::getInt)).intValue();
      float toughness = ((Float)data.getValue("toughness", Float.valueOf(iron.getToughness()), DataPartParsers::getFloat)).floatValue();
      int durability = ((Integer)data.getValue("durability", Integer.valueOf(15), DataPartParsers::getInt)).intValue();
      List<Integer> damageReduction = data.<Integer>getValues("reduction", new Integer[]{Integer.valueOf(2), Integer.valueOf(5), Integer.valueOf(6), Integer.valueOf(2)}, DataPartParsers::getInt, 4);
      int[] damageArray = new int[damageReduction.size()];

      for(int i = 0; i < damageReduction.size(); ++i) {
         damageArray[i] = ((Integer)damageReduction.get(i)).intValue();
      }

      String name = materialPart.getMaterial().getUnlocalizedName();
      ArmorMaterial armorMaterial = EnumHelper.addArmorMaterial(name, name, durability, damageArray, enchantability, SoundEvents.ITEM_ARMOR_EQUIP_IRON, toughness);
      if (this.chestPlates == null) {
         this.chestPlates = Maps.newHashMap();
      }

      materialUser.registerItem(new ItemMaterialArmor(materialPart, armorMaterial, EntityEquipmentSlot.HEAD));
      materialUser.registerItem(new ItemMaterialArmor(materialPart, armorMaterial, EntityEquipmentSlot.LEGS));
      materialUser.registerItem(new ItemMaterialArmor(materialPart, armorMaterial, EntityEquipmentSlot.FEET));
      ItemMaterialArmor chestPlate = new ItemMaterialArmor(materialPart, armorMaterial, EntityEquipmentSlot.CHEST);
      this.chestPlates.put(Integer.valueOf(materialPart.getId()), new ItemStack(chestPlate));
      materialUser.registerItem(chestPlate);
   }

   public ItemStack getItemStack(MaterialPart materialPart) {
      return (ItemStack)this.chestPlates.get(Integer.valueOf(materialPart.getId()));
   }
}
