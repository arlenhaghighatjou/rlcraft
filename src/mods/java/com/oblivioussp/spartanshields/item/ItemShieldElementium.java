package com.oblivioussp.spartanshields.item;

import com.oblivioussp.spartanshields.util.StringHelper;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Item.ToolMaterial;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Optional.Interface;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vazkii.botania.api.item.IPixieSpawner;

@Interface(
   iface = "vazkii.botania.api.item.IPixieSpawner",
   modid = "botania"
)
public class ItemShieldElementium extends ItemShieldBotania implements IPixieSpawner {
   public ItemShieldElementium(String unlocName, int maxDamage, ToolMaterial toolMaterial) {
      super(unlocName, maxDamage, toolMaterial);
   }

   @SideOnly(Side.CLIENT)
   public void addEffectsTooltip(ItemStack stack, @Nullable World world, List<String> tooltip, ITooltipFlag flagIn) {
      tooltip.add(TextFormatting.GOLD + StringHelper.translateFormattedString("onBlock", "tooltip", "spartanshields", StringHelper.translateFormattedString("shieldElementium.desc", "tooltip", (int)(this.getPixieChance(stack) * 100.0F))));
   }

   public float getPixieChance(ItemStack stack) {
      return 0.1F;
   }
}
