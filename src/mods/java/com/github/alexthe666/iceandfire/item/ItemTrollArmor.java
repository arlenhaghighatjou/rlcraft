package com.github.alexthe666.iceandfire.item;

import com.github.alexthe666.iceandfire.IceAndFire;
import com.github.alexthe666.iceandfire.client.StatCollector;
import com.github.alexthe666.iceandfire.enums.EnumTroll;
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
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemTrollArmor extends ItemArmor {
   public EnumTroll troll;

   public ItemTrollArmor(EnumTroll troll, ArmorMaterial material, int renderIndex, EntityEquipmentSlot slot) {
      super(material, renderIndex, slot);
      this.troll = troll;
      this.setCreativeTab(IceAndFire.TAB);
      this.setUnlocalizedName("iceandfire." + troll.name().toLowerCase() + "_troll_leather_" + this.getArmorPart(slot));
      this.setRegistryName(troll.name().toLowerCase() + "_troll_leather_" + this.getArmorPart(slot));
   }

   public ArmorMaterial getArmorMaterial() {
      return this.troll.material;
   }

   private String getArmorPart(EntityEquipmentSlot slot) {
      switch(slot) {
      case HEAD:
         return "helmet";
      case CHEST:
         return "chestplate";
      case LEGS:
         return "leggings";
      case FEET:
         return "boots";
      default:
         return "";
      }
   }

   @SideOnly(Side.CLIENT)
   public ModelBiped getArmorModel(EntityLivingBase entityLiving, ItemStack itemStack, EntityEquipmentSlot armorSlot, ModelBiped _default) {
      return (ModelBiped)IceAndFire.PROXY.getArmorModel(this.renderIndex == 2 ? 7 : 6);
   }

   public String getArmorTexture(ItemStack stack, Entity entity, EntityEquipmentSlot slot, String type) {
      return "iceandfire:textures/models/armor/armor_troll_" + this.troll.name().toLowerCase() + (this.renderIndex == 2 ? "_legs.png" : ".png");
   }

   @SideOnly(Side.CLIENT)
   public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
      tooltip.add(TextFormatting.GREEN + StatCollector.translateToLocal("item.iceandfire.troll_leather_armor_" + this.getArmorPart(this.armorType) + ".desc"));
   }
}
