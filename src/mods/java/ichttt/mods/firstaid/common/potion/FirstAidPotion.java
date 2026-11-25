package ichttt.mods.firstaid.common.potion;

import ichttt.mods.firstaid.common.items.FirstAidItems;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nonnull;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class FirstAidPotion extends Potion {
   public FirstAidPotion(boolean isBadEffectIn, int liquidColorIn, String name) {
      super(isBadEffectIn, liquidColorIn);
      this.setRegistryName(new ResourceLocation("firstaid", name));
      this.setPotionName("item." + name + ".name");
   }

   @Nonnull
   public List<ItemStack> getCurativeItems() {
      return new ArrayList(0);
   }

   @SideOnly(Side.CLIENT)
   public void renderInventoryEffect(int x, int y, PotionEffect effect, Minecraft mc) {
      GlStateManager.pushMatrix();
      GlStateManager.scale(1.2D, 1.2D, 1.2D);
      mc.getRenderItem().renderItemAndEffectIntoGUI(new ItemStack(FirstAidItems.MORPHINE), Math.round((float)x / 1.2F) + 4, Math.round((float)y / 1.2F) + 5);
      GlStateManager.popMatrix();
   }

   @SideOnly(Side.CLIENT)
   public void renderHUDEffect(@Nonnull PotionEffect effect, Gui gui, int x, int y, float z, float alpha) {
      GlStateManager.pushMatrix();
      Minecraft.getMinecraft().getRenderItem().renderItemAndEffectIntoGUI(new ItemStack(FirstAidItems.MORPHINE), x + 4, y + 4);
      GlStateManager.popMatrix();
   }
}
