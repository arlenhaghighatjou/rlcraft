package com.github.alexthe666.iceandfire.item;

import com.github.alexthe666.iceandfire.IceAndFire;
import com.github.alexthe666.iceandfire.client.StatCollector;
import com.github.alexthe666.iceandfire.core.ModItems;
import com.github.alexthe666.iceandfire.entity.EntityDeathWorm;
import com.google.common.collect.Sets;
import java.util.List;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTool;
import net.minecraft.item.Item.ToolMaterial;
import net.minecraft.util.DamageSource;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.oredict.OreDictionary;

public class ItemModAxe extends ItemTool {
   private static final Set<Block> EFFECTIVE_ON = Sets.newHashSet(new Block[]{Blocks.PLANKS, Blocks.BOOKSHELF, Blocks.LOG, Blocks.LOG2, Blocks.CHEST, Blocks.PUMPKIN, Blocks.LIT_PUMPKIN, Blocks.MELON_BLOCK, Blocks.LADDER, Blocks.OAK_DOOR, Blocks.WOODEN_PRESSURE_PLATE});

   public ItemModAxe(ToolMaterial toolmaterial, String gameName, String name) {
      super(toolmaterial, EFFECTIVE_ON);
      this.attackDamage = toolmaterial == ModItems.boneTools ? 8.0F : 6.0F;
      this.attackSpeed = -3.0F;
      this.setUnlocalizedName(name);
      this.setCreativeTab(IceAndFire.TAB);
      this.setRegistryName("iceandfire", gameName);
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
      if (this == ModItems.silver_axe && target.getCreatureAttribute() == EnumCreatureAttribute.UNDEAD) {
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

      return super.hitEntity(stack, target, attacker);
   }

   public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
      if (this == ModItems.silver_axe) {
         tooltip.add(TextFormatting.GREEN + StatCollector.translateToLocal("silvertools.hurt"));
      }

      if (this == ModItems.myrmex_desert_axe || this == ModItems.myrmex_jungle_axe) {
         tooltip.add(TextFormatting.GREEN + StatCollector.translateToLocal("myrmextools.hurt"));
      }

   }

   public float getDestroySpeed(ItemStack stack, IBlockState state) {
      Material material = state.getMaterial();
      return material != Material.WOOD && material != Material.PLANTS && material != Material.VINE ? super.getDestroySpeed(stack, state) : this.efficiency;
   }

   public boolean canDisableShield(ItemStack stack, ItemStack shield, EntityLivingBase entity, EntityLivingBase attacker) {
      return true;
   }
}
