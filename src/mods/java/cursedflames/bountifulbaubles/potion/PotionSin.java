package cursedflames.bountifulbaubles.potion;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class PotionSin extends Potion {
   public ResourceLocation TEXTURE = new ResourceLocation("bountifulbaubles", "textures/gui/sinful.png");
   String UUID_DAMAGE = "d0b248eb-3abb-4584-9cc5-2aaa06146300";
   String UUID_ARMOR = "d01deb3c-0e03-4d1f-b402-a9a47db42ccd";
   String UUID_ARMOR_TOUGHNESS = "beaf841f-4962-4bb8-8952-43f4c1e0de76";

   protected PotionSin() {
      super(false, 1053463);
      this.setRegistryName("bountifulbaubles", "sinful");
      this.setPotionName("bountifulbaubles.effect.sinful");
      this.registerPotionAttributeModifier(SharedMonsterAttributes.ATTACK_DAMAGE, this.UUID_DAMAGE, 0.25D, 1);
      this.registerPotionAttributeModifier(SharedMonsterAttributes.ARMOR, this.UUID_ARMOR, 3.0D, 0);
      this.registerPotionAttributeModifier(SharedMonsterAttributes.ARMOR_TOUGHNESS, this.UUID_ARMOR_TOUGHNESS, 1.0D, 0);
   }

   @SideOnly(Side.CLIENT)
   public void renderInventoryEffect(int x, int y, PotionEffect effect, Minecraft mc) {
      super.renderInventoryEffect(x, y, effect, mc);
      mc.renderEngine.bindTexture(this.TEXTURE);
      GlStateManager.enableBlend();
      Gui.drawModalRectWithCustomSizedTexture(x + 6, y + 7, 0.0F, 0.0F, 18, 18, 18.0F, 18.0F);
   }

   @SideOnly(Side.CLIENT)
   public void renderHUDEffect(int x, int y, PotionEffect effect, Minecraft mc, float alpha) {
      super.renderHUDEffect(x, y, effect, mc, alpha);
      mc.renderEngine.bindTexture(this.TEXTURE);
      GlStateManager.enableBlend();
      Gui.drawModalRectWithCustomSizedTexture(x + 3, y + 3, 0.0F, 0.0F, 18, 18, 18.0F, 18.0F);
   }
}
