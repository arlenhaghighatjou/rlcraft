package slimeknights.mantle.client;

import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.client.GuiIngameForge;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.client.event.RenderGameOverlayEvent.Pre;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ExtraHeartRenderHandler {
   private static final ResourceLocation ICON_HEARTS = new ResourceLocation("mantle", "textures/gui/hearts.png");
   private static final ResourceLocation ICON_ABSORB = new ResourceLocation("mantle", "textures/gui/absorb.png");
   private static final ResourceLocation ICON_VANILLA = Gui.ICONS;
   private final Minecraft mc = Minecraft.getMinecraft();
   private int updateCounter = 0;
   private int playerHealth = 0;
   private int lastPlayerHealth = 0;
   private long healthUpdateCounter = 0L;
   private long lastSystemTime = 0L;
   private EaglercraftRandom rand = new EaglercraftRandom();
   private int height;
   private int width;
   private int regen;
   private static int left_height = 39;

   public ExtraHeartRenderHandler() {
   }

   public void drawTexturedModalRect(int x, int y, int textureX, int textureY, int width, int height) {
      Minecraft.getMinecraft().ingameGUI.drawTexturedModalRect(x, y, textureX, textureY, width, height);
   }

   @SubscribeEvent(
      priority = EventPriority.LOW
   )
   public void renderHealthbar(Pre event) {
      Entity renderViewEnity = this.mc.getRenderViewEntity();
      if (event.getType() == ElementType.HEALTH && !event.isCanceled() && renderViewEnity instanceof EntityPlayer) {
         EntityPlayer player = (EntityPlayer)this.mc.getRenderViewEntity();
         left_height = GuiIngameForge.left_height;
         ScaledResolution resolution = event.getResolution();
         this.width = resolution.getScaledWidth();
         this.height = resolution.getScaledHeight();
         event.setCanceled(true);
         this.updateCounter = this.mc.ingameGUI.getUpdateCounter();
         this.mc.mcProfiler.startSection("health");
         GlStateManager.enableBlend();
         int health = MathHelper.ceil(player.getHealth());
         boolean highlight = this.healthUpdateCounter > (long)this.updateCounter && (this.healthUpdateCounter - (long)this.updateCounter) / 3L % 2L == 1L;
         if (health < this.playerHealth && player.hurtResistantTime > 0) {
            this.lastSystemTime = Minecraft.getSystemTime();
            this.healthUpdateCounter = (long)(this.updateCounter + 20);
         } else if (health > this.playerHealth && player.hurtResistantTime > 0) {
            this.lastSystemTime = Minecraft.getSystemTime();
            this.healthUpdateCounter = (long)(this.updateCounter + 10);
         }

         if (Minecraft.getSystemTime() - this.lastSystemTime > 1000L) {
            this.playerHealth = health;
            this.lastPlayerHealth = health;
            this.lastSystemTime = Minecraft.getSystemTime();
         }

         this.playerHealth = health;
         int healthLast = this.lastPlayerHealth;
         IAttributeInstance attrMaxHealth = player.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH);
         float healthMax = (float)attrMaxHealth.getAttributeValue();
         float absorb = (float)MathHelper.ceil(player.getAbsorptionAmount());
         healthMax = Math.min(healthMax, 20.0F);
         health = Math.min(health, 20);
         absorb = Math.min(absorb, 20.0F);
         int healthRows = MathHelper.ceil((healthMax + absorb) / 2.0F / 10.0F);
         int rowHeight = Math.max(10 - (healthRows - 2), 3);
         this.rand.setSeed((long)(this.updateCounter * 312871));
         int left = this.width / 2 - 91;
         int top = this.height - left_height;
         left_height += healthRows * rowHeight;
         if (rowHeight != 10) {
            left_height += 10 - rowHeight;
         }

         this.regen = -1;
         if (player.isPotionActive(MobEffects.REGENERATION)) {
            this.regen = this.updateCounter % 25;
         }

         int TOP = 9 * (this.mc.world.getWorldInfo().isHardcoreModeEnabled() ? 5 : 0);
         int BACKGROUND = highlight ? 25 : 16;
         int MARGIN = 16;
         if (player.isPotionActive(MobEffects.POISON)) {
            MARGIN += 36;
         } else if (player.isPotionActive(MobEffects.WITHER)) {
            MARGIN += 72;
         }

         float absorbRemaining = absorb;

         for(int i = MathHelper.ceil((healthMax + absorb) / 2.0F) - 1; i >= 0; --i) {
            int row = MathHelper.ceil((float)(i + 1) / 10.0F) - 1;
            int x = left + i % 10 * 8;
            int y = top - row * rowHeight;
            if (health <= 4) {
               y += this.rand.nextInt(2);
            }

            if (i == this.regen) {
               y -= 2;
            }

            this.drawTexturedModalRect(x, y, BACKGROUND, TOP, 9, 9);
            if (highlight) {
               if (i * 2 + 1 < healthLast) {
                  this.drawTexturedModalRect(x, y, MARGIN + 54, TOP, 9, 9);
               } else if (i * 2 + 1 == healthLast) {
                  this.drawTexturedModalRect(x, y, MARGIN + 63, TOP, 9, 9);
               }
            }

            if (absorbRemaining > 0.0F) {
               if (absorbRemaining == absorb && absorb % 2.0F == 1.0F) {
                  this.drawTexturedModalRect(x, y, MARGIN + 153, TOP, 9, 9);
                  --absorbRemaining;
               } else {
                  this.drawTexturedModalRect(x, y, MARGIN + 144, TOP, 9, 9);
                  absorbRemaining -= 2.0F;
               }
            } else if (i * 2 + 1 < health) {
               this.drawTexturedModalRect(x, y, MARGIN + 36, TOP, 9, 9);
            } else if (i * 2 + 1 == health) {
               this.drawTexturedModalRect(x, y, MARGIN + 45, TOP, 9, 9);
            }
         }

         this.renderExtraHearts(left, top, player);
         this.renderExtraAbsorption(left, top - rowHeight, player);
         this.mc.getTextureManager().bindTexture(ICON_VANILLA);
         GuiIngameForge.left_height += 10;
         if (absorb > 0.0F) {
            GuiIngameForge.left_height += 10;
         }

         event.setCanceled(true);
         GlStateManager.disableBlend();
         this.mc.mcProfiler.endSection();
      }
   }

   private void renderExtraHearts(int xBasePos, int yBasePos, EntityPlayer player) {
      int potionOffset = this.getPotionOffset(player);
      this.mc.getTextureManager().bindTexture(ICON_HEARTS);
      int hp = MathHelper.ceil(player.getHealth());
      this.renderCustomHearts(xBasePos, yBasePos, potionOffset, hp, false);
   }

   private void renderCustomHearts(int xBasePos, int yBasePos, int potionOffset, int count, boolean absorb) {
      int regenOffset = absorb ? 10 : 0;

      for(int iter = 0; iter < count / 20; ++iter) {
         int renderHearts = (count - 20 * (iter + 1)) / 2;
         int heartIndex = iter % 11;
         if (renderHearts > 10) {
            renderHearts = 10;
         }

         for(int i = 0; i < renderHearts; ++i) {
            int y = this.getYRegenOffset(i, regenOffset);
            if (absorb) {
               this.drawTexturedModalRect(xBasePos + 8 * i, yBasePos + y, 0, 54, 9, 9);
            }

            this.drawTexturedModalRect(xBasePos + 8 * i, yBasePos + y, 0 + 18 * heartIndex, potionOffset, 9, 9);
         }

         if (count % 2 == 1 && renderHearts < 10) {
            int y = this.getYRegenOffset(renderHearts, regenOffset);
            if (absorb) {
               this.drawTexturedModalRect(xBasePos + 8 * renderHearts, yBasePos + y, 0, 54, 9, 9);
            }

            this.drawTexturedModalRect(xBasePos + 8 * renderHearts, yBasePos + y, 9 + 18 * heartIndex, potionOffset, 9, 9);
         }
      }

   }

   private int getYRegenOffset(int i, int offset) {
      return i + offset == this.regen ? -2 : 0;
   }

   private int getPotionOffset(EntityPlayer player) {
      int potionOffset = 0;
      PotionEffect potion = player.getActivePotionEffect(MobEffects.WITHER);
      if (potion != null) {
         potionOffset = 18;
      }

      potion = player.getActivePotionEffect(MobEffects.POISON);
      if (potion != null) {
         potionOffset = 9;
      }

      if (this.mc.world.getWorldInfo().isHardcoreModeEnabled()) {
         potionOffset += 27;
      }

      return potionOffset;
   }

   private void renderExtraAbsorption(int xBasePos, int yBasePos, EntityPlayer player) {
      int potionOffset = this.getPotionOffset(player);
      this.mc.getTextureManager().bindTexture(ICON_ABSORB);
      int absorb = MathHelper.ceil(player.getAbsorptionAmount());
      this.renderCustomHearts(xBasePos, yBasePos, potionOffset, absorb, true);
   }
}
