package com.oblivioussp.spartanshields.item;

import com.oblivioussp.spartanshields.util.ConfigHandler;
import com.oblivioussp.spartanshields.util.StringHelper;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Item.ToolMaterial;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Optional.Interface;
import net.minecraftforge.fml.common.Optional.Method;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vazkii.botania.api.mana.IManaUsingItem;
import vazkii.botania.api.mana.ManaItemHandler;

@Interface(
   iface = "vazkii.botania.api.mana.IManaUsingItem",
   modid = "botania"
)
public class ItemShieldBotania extends ItemShieldBasic implements IManaUsingItem {
   protected int manaPerDamage;

   public ItemShieldBotania(String unlocName, int maxDamage, ToolMaterial toolMaterial) {
      super(unlocName, maxDamage, toolMaterial);
      this.manaPerDamage = 60;
   }

   public ItemShieldBotania(String unlocName, int maxDamage, ToolMaterial toolMaterial, int manaUsePerDamage) {
      this(unlocName, maxDamage, toolMaterial);
      this.manaPerDamage = manaUsePerDamage;
   }

   public void onUpdate(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
      if (!worldIn.isRemote && entityIn instanceof EntityPlayer && stack.getItemDamage() > 0 && this.requestManaForRepair(stack, (EntityPlayer)entityIn, this.manaPerDamage * 2)) {
         stack.setItemDamage(this.getDamage(stack) - 1);
      }

   }

   @Method(
      modid = "botania"
   )
   protected boolean requestManaForRepair(ItemStack stack, EntityPlayer player, int manaRequested) {
      return ManaItemHandler.requestManaExactForTool(stack, player, manaRequested, true);
   }

   @SideOnly(Side.CLIENT)
   public void addInformation(ItemStack stack, @Nullable World world, List<String> tooltip, ITooltipFlag flagIn) {
      if (!Loader.isModLoaded("botania") && !ConfigHandler.forceDisableUncraftableTooltips) {
         tooltip.add(TextFormatting.RED + StringHelper.translateString("cantCraftMissingMods", "tooltip"));
         tooltip.add(TextFormatting.RED + StringHelper.translateString("botania", "mod"));
      }

      tooltip.add(StringHelper.translateFormattedString("protection", "tooltip", this.getMaxDamage()));
      this.addEffectsTooltip(stack, world, tooltip, flagIn);
      tooltip.add("");
      this.addShieldBashTooltip(stack, world, tooltip, flagIn);
   }

   public boolean usesMana(ItemStack stack) {
      return true;
   }
}
