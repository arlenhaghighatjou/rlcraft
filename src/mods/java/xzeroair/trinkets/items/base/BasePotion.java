package xzeroair.trinkets.items.base;

import java.util.function.Consumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import xzeroair.trinkets.capabilities.Capabilities;
import xzeroair.trinkets.races.EntityRace;
import xzeroair.trinkets.util.TrinketsConfig;
import xzeroair.trinkets.util.compat.SurvivalCompat;
import xzeroair.trinkets.util.helpers.MagicHelper;

public class BasePotion extends Potion {
   protected final ResourceLocation ICON;
   protected String name;
   protected int maxDuration;
   protected int indexX;
   protected int indexY;

   public BasePotion(String name, int color, int duration, boolean isBadEffect) {
      this("xat", name, color, duration, isBadEffect);
   }

   public BasePotion(String modid, String name, int color, int duration, boolean isBadEffect) {
      this(modid, name, color, duration, isBadEffect, -1, -1, new ResourceLocation(modid, "textures/potions/" + name + ".png"));
   }

   public BasePotion(String modid, String name, int color, int duration, boolean isBadEffect, int IconX, int IconY, ResourceLocation texture) {
      super(isBadEffect, color);
      this.indexX = IconX;
      this.indexY = IconY;
      this.name = name;
      this.ICON = texture;
      this.maxDuration = duration;
      this.setPotionName(modid + ".effect." + name);
      this.setRegistryName(new ResourceLocation(modid, name));
   }

   @SideOnly(Side.CLIENT)
   public void renderInventoryEffect(PotionEffect effect, Gui gui, int x, int y, float z) {
      this.renderInventoryEffect(x, y, effect, Minecraft.getMinecraft());
   }

   @SideOnly(Side.CLIENT)
   public void renderInventoryEffect(int x, int y, PotionEffect effect, Minecraft mc) {
      if (this.getTexture() != null) {
         mc.getTextureManager().bindTexture(this.getTexture());
         if (this.indexX >= 0 && this.indexY >= 0) {
            int i1 = (this.indexX + this.indexY) * 8;
            Gui.drawModalRectWithCustomSizedTexture(x + 6, y + 7, (float)(0 + i1 % 8 * 18), (float)(198 + i1 / 8 * 18), 18, 18, 256.0F, 256.0F);
         } else {
            Gui.drawModalRectWithCustomSizedTexture(x + 6, y + 7, 0.0F, 0.0F, 18, 18, 18.0F, 18.0F);
         }

      }
   }

   @SideOnly(Side.CLIENT)
   public void renderHUDEffect(PotionEffect effect, Gui gui, int x, int y, float z, float alpha) {
      this.renderHUDEffect(x, y, effect, Minecraft.getMinecraft(), alpha);
   }

   @SideOnly(Side.CLIENT)
   public void renderHUDEffect(int x, int y, PotionEffect effect, Minecraft mc, float alpha) {
      if (this.getTexture() != null) {
         mc.getTextureManager().bindTexture(this.getTexture());
         if (this.indexX >= 0 && this.indexY >= 0) {
            int i1 = (this.indexX + this.indexY) * 8;
            Gui.drawModalRectWithCustomSizedTexture(x + 3, y + 3, (float)(i1 % 8 * 18), (float)(198 + i1 / 8 * 18), 18, 18, 256.0F, 256.0F);
         } else {
            Gui.drawModalRectWithCustomSizedTexture(x + 3, y + 3, 0.0F, 0.0F, 18, 18, 18.0F, 18.0F);
         }

      }
   }

   public boolean isReady(int duration, int amplifier) {
      return duration <= 1;
   }

   public boolean isInstant() {
      return this.maxDuration <= 0;
   }

   public void affectEntity(Entity source, Entity indirectSource, EntityLivingBase entity, int amplifier, double health) {
      Capabilities.getEntityProperties(entity, (prop) -> {
         if (this.name.equals("restorative")) {
            prop.setImbuedRace((EntityRace)null);
            MagicHelper.refillMana(entity);
         } else if (this.name.equals("glowing")) {
            MagicHelper.refillMana(entity);
         } else if (this.name.equals("glittering")) {
            MagicHelper.refillManaByPrecentage(entity, 0.5F);
         } else {
            MagicHelper.refillManaByPrecentage(entity, 0.25F);
         }

      });
      float heals = 0.0F;
      int thirst = 0;
      int saturation = 0;
      if (!this.name.equals("restorative") && !this.name.equals("glowing")) {
         if (this.name.equals("glittering")) {
            entity.addPotionEffect(new PotionEffect(MobEffects.ABSORPTION, 300, 0, false, false));
            heals = entity.getMaxHealth() * 0.5F;
            thirst = 10;
            saturation = 10;
         } else {
            heals = entity.getMaxHealth() * 0.25F;
            thirst = 5;
            saturation = 0;
         }
      } else {
         heals = entity.getMaxHealth();
         thirst = 20;
         saturation = 20;
      }

      if (heals > 0.0F) {
         entity.heal(heals);
      }

      if (TrinketsConfig.SERVER.Potion.potion_thirst && entity instanceof EntityPlayer) {
         SurvivalCompat.addThirst(entity, thirst, saturation);
         SurvivalCompat.clearThirst(entity);
      }

   }

   public ResourceLocation getTexture() {
      return this.ICON;
   }

   public boolean hasStatusIcon() {
      return this.ICON != null;
   }

   public boolean shouldRenderHUD(PotionEffect effect) {
      return this.hasStatusIcon();
   }

   public boolean shouldRender(PotionEffect effect) {
      return this.hasStatusIcon();
   }

   public boolean shouldRenderInvText(PotionEffect effect) {
      return this.hasStatusIcon();
   }
}
