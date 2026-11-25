package com.github.alexthe666.iceandfire.item;

import com.github.alexthe666.iceandfire.IceAndFire;
import com.github.alexthe666.iceandfire.client.StatCollector;
import com.github.alexthe666.iceandfire.core.ModItems;
import com.github.alexthe666.iceandfire.entity.EntityDeathWorm;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.item.Item.ToolMaterial;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.oredict.OreDictionary;

public class ItemModSword extends ItemSword {
   private final ToolMaterial toolMaterial;

   public ItemModSword(ToolMaterial toolmaterial, String gameName, String name) {
      super(toolmaterial);
      this.setUnlocalizedName(name);
      this.setCreativeTab(IceAndFire.TAB);
      this.setRegistryName("iceandfire", gameName);
      this.toolMaterial = toolmaterial;
   }

   public boolean getIsRepairable(ItemStack toRepair, ItemStack repair) {
      ItemStack mat = this.toolMaterial.getRepairItemStack();
      if (this.toolMaterial == ModItems.silverTools) {
         for(ItemStack ingot : OreDictionary.getOres("ingotSilver")) {
            if (OreDictionary.itemMatches(repair, ingot, false)) {
               return true;
            }
         }
      }

      return !mat.isEmpty() && OreDictionary.itemMatches(mat, repair, false) ? true : super.getIsRepairable(toRepair, repair);
   }

   public boolean hitEntity(ItemStack stack, EntityLivingBase target, EntityLivingBase attacker) {
      if (this == ModItems.silver_sword && target.getCreatureAttribute() == EnumCreatureAttribute.UNDEAD) {
         target.attackEntityFrom(DamageSource.MAGIC, 2.0F);
      }

      if (this.toolMaterial == ModItems.myrmexChitin) {
         if (target.getCreatureAttribute() != EnumCreatureAttribute.ARTHROPOD) {
            target.attackEntityFrom(DamageSource.GENERIC, 4.0F);
         }

         if (target instanceof EntityDeathWorm) {
            target.attackEntityFrom(DamageSource.GENERIC, 4.0F);
         }
      }

      if (this == ModItems.myrmex_desert_sword_venom || this == ModItems.myrmex_jungle_sword_venom) {
         target.addPotionEffect(new PotionEffect(MobEffects.POISON, 200, 2));
      }

      return super.hitEntity(stack, target, attacker);
   }

   public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
      if (this == ModItems.silver_sword) {
         tooltip.add(TextFormatting.GREEN + StatCollector.translateToLocal("silvertools.hurt"));
      }

      if (this == ModItems.myrmex_desert_sword_venom || this == ModItems.myrmex_jungle_sword_venom) {
         tooltip.add(TextFormatting.DARK_GREEN + StatCollector.translateToLocal("myrmextools.poison"));
         tooltip.add(TextFormatting.GREEN + StatCollector.translateToLocal("myrmextools.hurt"));
      }

      if (this == ModItems.myrmex_desert_sword || this == ModItems.myrmex_jungle_sword) {
         tooltip.add(TextFormatting.GREEN + StatCollector.translateToLocal("myrmextools.hurt"));
      }

   }
}
