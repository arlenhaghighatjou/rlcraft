package com.github.alexthe666.iceandfire.item;

import com.github.alexthe666.iceandfire.IceAndFire;
import com.github.alexthe666.iceandfire.client.StatCollector;
import com.github.alexthe666.iceandfire.enums.EnumSeaSerpent;
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

public class ItemSeaSerpentArmor extends ItemArmor {
   public EnumSeaSerpent armor_type;

   public ItemSeaSerpentArmor(EnumSeaSerpent armorType, ArmorMaterial material, int renderIndex, EntityEquipmentSlot slot) {
      super(material, renderIndex, slot);
      this.armor_type = armorType;
      this.setCreativeTab(IceAndFire.TAB);
   }

   @SideOnly(Side.CLIENT)
   public ModelBiped getArmorModel(EntityLivingBase entityLiving, ItemStack itemStack, EntityEquipmentSlot armorSlot, ModelBiped _default) {
      return (ModelBiped)IceAndFire.PROXY.getArmorModel(this.renderIndex == 2 ? 9 : 8);
   }

   public String getArmorTexture(ItemStack stack, Entity entity, EntityEquipmentSlot slot, String type) {
      return "iceandfire:textures/models/armor/armor_tide_" + this.armor_type.resourceName + (this.renderIndex == 2 ? "_legs.png" : ".png");
   }

   @SideOnly(Side.CLIENT)
   public void addInformation(ItemStack stack, @Nullable World player, List<String> tooltip, ITooltipFlag advanced) {
      tooltip.add(this.armor_type.color + StatCollector.translateToLocal("sea_serpent." + this.armor_type.resourceName));
      tooltip.add(StatCollector.translateToLocal("item.iceandfire.sea_serpent_armor.desc_0"));
      tooltip.add(StatCollector.translateToLocal("item.iceandfire.sea_serpent_armor.desc_1"));
   }
}
