package com.oblivioussp.spartanweaponry.item;

import com.oblivioussp.spartanweaponry.api.ToolMaterialEx;
import com.oblivioussp.spartanweaponry.api.WeaponProperties;
import com.oblivioussp.spartanweaponry.util.ConfigHandler;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;

public class ItemBattleaxe extends ItemWeaponBase {
   public ItemBattleaxe(String unlocName, ToolMaterialEx material) {
      super(unlocName, material, ConfigHandler.damageBaseBattleaxe, ConfigHandler.damageMultiplierBattleaxe, (double)ConfigHandler.speedBattleaxe, WeaponProperties.TWO_HANDED_1, WeaponProperties.VERSATILE);
      this.setHarvestLevel("axe", material.getHarvestLevel());
      this.displayName = "battleaxe_custom";
   }

   public ItemBattleaxe(String unlocName, String externalModId, ToolMaterialEx material) {
      this(unlocName, material);
      this.modId = externalModId;
   }

   public float getDestroySpeed(ItemStack stack, IBlockState state) {
      Material material = state.getMaterial();
      if (material != Material.WOOD && material != Material.PLANTS && material != Material.VINE) {
         for(String type : this.getToolClasses(stack)) {
            if (state.getBlock().isToolEffective(type, state)) {
               return this.materialEx.getEfficiency();
            }
         }

         return super.getDestroySpeed(stack, state);
      } else {
         return this.materialEx.getEfficiency();
      }
   }

   /** @deprecated */
   @Deprecated
   public ItemBattleaxe(String unlocName, String externalModId, ToolMaterialEx material, float weaponDamage) {
      this(unlocName, externalModId, material);
   }
}
