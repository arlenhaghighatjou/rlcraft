package com.mujmajnkraft.bettersurvival.items;

import com.mujmajnkraft.bettersurvival.BetterSurvival;
import com.mujmajnkraft.bettersurvival.config.ForgeConfigHandler;
import com.mujmajnkraft.bettersurvival.integration.SoManyEnchantmentsCompat;
import java.util.List;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Item.ToolMaterial;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemBattleAxe extends ItemCustomWeapon {
   public ItemBattleAxe(ToolMaterial material) {
      super(material, ForgeConfigHandler.weapons.battleAxeDmgMod, ForgeConfigHandler.weapons.battleAxeSpd);
      this.setRegistryName("mujmajnkraftsbettersurvival", "item" + material.name().toLowerCase() + "battleaxe");
      this.setUnlocalizedName(material.name().toLowerCase() + "battleaxe");
   }

   @SideOnly(Side.CLIENT)
   public void addInformation(ItemStack stack, World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
      super.addInformation(stack, worldIn, tooltip, flagIn);
      String s = I18n.format("mujmajnkraftsbettersurvival.battleaxe.desc", new Object[0]);
      tooltip.add(TextFormatting.AQUA + s);
   }

   public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment) {
      return BetterSurvival.isSMELoaded && SoManyEnchantmentsCompat.isCombatAxeSMEEnchant(enchantment.type) ? true : super.canApplyAtEnchantingTable(stack, enchantment);
   }
}
