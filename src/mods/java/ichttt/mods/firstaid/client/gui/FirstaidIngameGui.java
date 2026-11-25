package ichttt.mods.firstaid.client.gui;

import ichttt.mods.firstaid.FirstAid;
import ichttt.mods.firstaid.api.CapabilityExtendedHealthSystem;
import ichttt.mods.firstaid.api.damagesystem.AbstractDamageablePart;
import ichttt.mods.firstaid.api.damagesystem.AbstractPlayerDamageModel;
import java.lang.reflect.Field;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiIngame;
import net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.client.GuiIngameForge;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.client.event.RenderGameOverlayEvent.Post;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class FirstaidIngameGui {
   private static final Field eventParentField;

   public FirstaidIngameGui() {
   }

   public static void renderHealth(GuiIngame gui, int width, int height) {
      Minecraft mc = Minecraft.getMinecraft();
      mc.mcProfiler.startSection("health");
      AbstractPlayerDamageModel damageModel = (AbstractPlayerDamageModel)mc.player.getCapability(CapabilityExtendedHealthSystem.INSTANCE, (EnumFacing)null);
      int criticalHalfHearts;
      if (damageModel != null) {
         float criticalHealth = Float.MAX_VALUE;

         for(AbstractDamageablePart part : damageModel) {
            if (part.canCauseDeath) {
               criticalHealth = Math.min(criticalHealth, part.currentHealth);
            }
         }

         criticalHealth = criticalHealth / (float)damageModel.getCurrentMaxHealth() * mc.player.getMaxHealth();
         criticalHalfHearts = MathHelper.ceil(criticalHealth);
      } else {
         criticalHalfHearts = 0;
      }

      GlStateManager.enableBlend();
      EntityPlayer player = (EntityPlayer)mc.getRenderViewEntity();
      int health = MathHelper.ceil(player.getHealth());
      boolean highlight = gui.healthUpdateCounter > (long)gui.updateCounter && (gui.healthUpdateCounter - (long)gui.updateCounter) / 3L % 2L == 1L;
      if (health < gui.playerHealth && player.hurtResistantTime > 0) {
         gui.lastSystemTime = Minecraft.getSystemTime();
         gui.healthUpdateCounter = (long)(gui.updateCounter + 20);
      } else if (health > gui.playerHealth && player.hurtResistantTime > 0) {
         gui.lastSystemTime = Minecraft.getSystemTime();
         gui.healthUpdateCounter = (long)(gui.updateCounter + 10);
      }

      if (Minecraft.getSystemTime() - gui.lastSystemTime > 1000L) {
         gui.playerHealth = health;
         gui.lastPlayerHealth = health;
         gui.lastSystemTime = Minecraft.getSystemTime();
      }

      gui.playerHealth = health;
      int healthLast = gui.lastPlayerHealth;
      IAttributeInstance attrMaxHealth = player.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH);
      float healthMax = (float)attrMaxHealth.getAttributeValue();
      float absorb = (float)MathHelper.ceil(player.getAbsorptionAmount());
      int healthRows = MathHelper.ceil((healthMax + absorb) / 2.0F / 10.0F);
      int rowHeight = Math.max(10 - (healthRows - 2), 3);
      gui.rand.setSeed((long)(gui.updateCounter * 312871));
      int left = width / 2 - 91;
      int top = height - GuiIngameForge.left_height;
      GuiIngameForge.left_height += healthRows * rowHeight;
      if (rowHeight != 10) {
         GuiIngameForge.left_height += 10 - rowHeight;
      }

      int regen = -1;
      if (player.isPotionActive(MobEffects.REGENERATION)) {
         regen = gui.updateCounter % 25;
      }

      int BACKGROUND = highlight ? 25 : 16;
      int MARGIN = 16;
      if (player.isPotionActive(MobEffects.POISON)) {
         MARGIN += 36;
      } else if (player.isPotionActive(MobEffects.WITHER)) {
         MARGIN += 72;
      }

      float absorbRemaining = absorb;

      for(int i = MathHelper.ceil((healthMax + absorb) / 2.0F) - 1; i >= 0; --i) {
         boolean thisHalfCritical = i * 2 + 1 == criticalHalfHearts;
         int TOP = 9 * (i * 2 < criticalHalfHearts && !thisHalfCritical ? 5 : 0);
         int row = MathHelper.ceil((float)(i + 1) / 10.0F) - 1;
         int x = left + i % 10 * 8;
         int y = top - row * rowHeight;
         if (health <= 4) {
            y += gui.rand.nextInt(2);
         }

         if (i == regen) {
            y -= 2;
         }

         gui.drawTexturedModalRect(x, y, BACKGROUND, TOP, 9, 9);
         if (highlight) {
            if (thisHalfCritical) {
               float oldBlitOffset = gui.zLevel;
               gui.zLevel += 1000.0F;
               gui.drawTexturedModalRect(x, y, MARGIN + 63, 45, 9, 9);
               gui.zLevel = oldBlitOffset;
            }

            if (i * 2 + 1 < healthLast) {
               gui.drawTexturedModalRect(x + (thisHalfCritical ? 5 : 0), y, MARGIN + 54 + (thisHalfCritical ? 5 : 0), TOP, 9 - (thisHalfCritical ? 5 : 0), 9);
            } else if (i * 2 + 1 == healthLast) {
               gui.drawTexturedModalRect(x, y, MARGIN + 63, TOP, 9, 9);
            }
         }

         if (absorbRemaining > 0.0F) {
            if (absorbRemaining == absorb && absorb % 2.0F == 1.0F) {
               gui.drawTexturedModalRect(x, y, MARGIN + 153, TOP, 9, 9);
               --absorbRemaining;
            } else {
               gui.drawTexturedModalRect(x, y, MARGIN + 144, TOP, 9, 9);
               absorbRemaining -= 2.0F;
            }
         } else {
            if (thisHalfCritical) {
               float oldBlitOffset = gui.zLevel;
               gui.zLevel += 1000.0F;
               gui.drawTexturedModalRect(x, y, MARGIN + 45, 45, 9, 9);
               gui.zLevel = oldBlitOffset;
            }

            if (i * 2 + 1 < health) {
               gui.drawTexturedModalRect(x + (thisHalfCritical ? 5 : 0), y, MARGIN + 36 + (thisHalfCritical ? 5 : 0), TOP, 9 - (thisHalfCritical ? 5 : 0), 9);
            } else if (i * 2 + 1 == health && !thisHalfCritical) {
               gui.drawTexturedModalRect(x, y, MARGIN + 45, TOP, 9, 9);
            }
         }
      }

      GlStateManager.disableBlend();
      mc.mcProfiler.endSection();
      if (eventParentField != null) {
         RenderGameOverlayEvent event;
         try {
            event = (RenderGameOverlayEvent)eventParentField.get(gui);
         } catch (IllegalAccessException var28) {
            FirstAid.LOGGER.error("Failed to access eventParentField", var28);
            event = null;
         }

         if (event != null) {
            MinecraftForge.EVENT_BUS.post(new Post(event, ElementType.HEALTH));
         }
      }

   }

   static {
      Field f;
      try {
         f = GuiIngameForge.class.getDeclaredField("eventParent");
         f.setAccessible(true);
      } catch (ReflectiveOperationException var2) {
         FirstAid.LOGGER.error("Failed to get eventParent", var2);
         f = null;
      }

      eventParentField = f;
   }
}
