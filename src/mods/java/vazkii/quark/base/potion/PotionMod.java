package vazkii.quark.base.potion;

import javax.annotation.Nonnull;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vazkii.arl.util.ProxyRegistry;

public class PotionMod extends Potion {
   public static final ResourceLocation TEXTURE = new ResourceLocation("quark", "textures/misc/potions.png");
   protected final String bareName;
   private final int iconX;
   private final int iconY;

   public PotionMod(String name, boolean badEffect, int color, int iconIndex) {
      super(badEffect, color);
      this.iconX = iconIndex % 8;
      this.iconY = iconIndex / 8;
      this.setPotionName(name);
      this.setRegistryName(new ResourceLocation("quark:" + name));
      ProxyRegistry.register(this);
      this.bareName = name;
   }

   @Nonnull
   public String getName() {
      return "quark.potion." + this.bareName + ".name";
   }

   @SideOnly(Side.CLIENT)
   public void renderHUDEffect(@Nonnull PotionEffect effect, Gui gui, int x, int y, float z, float alpha) {
      GlStateManager.color(1.0F, 1.0F, 1.0F, alpha);
      Minecraft.getMinecraft().renderEngine.bindTexture(TEXTURE);
      gui.drawTexturedModalRect(x + 3, y + 3, this.iconX * 18, 198 + this.iconY * 18, 18, 18);
      GlStateManager.color(1.0F, 1.0F, 1.0F);
   }

   @SideOnly(Side.CLIENT)
   public void renderInventoryEffect(@Nonnull PotionEffect effect, Gui gui, int x, int y, float z) {
      Minecraft.getMinecraft().renderEngine.bindTexture(TEXTURE);
      gui.drawTexturedModalRect(x + 6, y + 7, this.iconX * 18, 198 + this.iconY * 18, 18, 18);
   }
}
