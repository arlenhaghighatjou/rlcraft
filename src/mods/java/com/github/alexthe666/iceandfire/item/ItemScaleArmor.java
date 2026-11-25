package com.github.alexthe666.iceandfire.item;

import com.github.alexthe666.iceandfire.IceAndFire;
import com.github.alexthe666.iceandfire.client.StatCollector;
import com.github.alexthe666.iceandfire.enums.EnumDragonArmor;
import com.github.alexthe666.iceandfire.enums.EnumDragonEgg;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemArmor.ArmorMaterial;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemScaleArmor extends ItemArmor {
   public EnumDragonArmor armor_type;
   public EnumDragonEgg eggType;

   public ItemScaleArmor(EnumDragonEgg eggType, EnumDragonArmor armorType, ArmorMaterial material, int renderIndex, EntityEquipmentSlot slot) {
      super(material, renderIndex, slot);
      this.armor_type = armorType;
      this.eggType = eggType;
      this.setCreativeTab(IceAndFire.TAB);
   }

   @SideOnly(Side.CLIENT)
   public ModelBiped getArmorModel(EntityLivingBase entityLiving, ItemStack itemStack, EntityEquipmentSlot armorSlot, ModelBiped _default) {
      return (ModelBiped)IceAndFire.PROXY.getArmorModel(this.armor_type.ordinal() < 3 ? (this.renderIndex == 2 ? 1 : 0) : (this.renderIndex == 2 ? 3 : 2));
   }

   public String getArmorTexture(ItemStack stack, Entity entity, EntityEquipmentSlot slot, String type) {
      return "iceandfire:textures/models/armor/" + this.armor_type.name() + (this.renderIndex == 2 ? "_legs.png" : ".png");
   }

   @SideOnly(Side.CLIENT)
   public void addInformation(ItemStack stack, @Nullable World player, List<String> tooltip, ITooltipFlag advanced) {
      tooltip.add(this.eggType.color + StatCollector.translateToLocal("dragon." + this.eggType.toString().toLowerCase()));
   }
}
