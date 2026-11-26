package net.silentchaos512.scalinghealth.event;

import net.lax1dude.eaglercraft.v1_8.HString;
import net.minecraftforge.fml.common.eventhandler.EventBus;
import com.google.common.base.Predicate;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.client.event.RenderGameOverlayEvent.Post;
import net.minecraftforge.fml.relauncher.Side;
import net.silentchaos512.scalinghealth.config.Config;
import net.silentchaos512.scalinghealth.entity.EntityBlightFire;
import net.silentchaos512.scalinghealth.lib.EnumAreaDifficultyMode;
import net.silentchaos512.scalinghealth.utils.SHPlayerDataHandler;

public final class ScalingHealthClientEvents {
   private static final float DEBUG_TEXT_SCALE = 0.6F;

   private ScalingHealthClientEvents() {
   }

   public static void register(EventBus bus) {
      bus.register(Post.class, ScalingHealthClientEvents::renderTick);
   }

   public static void renderTick(Post event) {
      if (Config.Debug.debugMode && Config.Debug.debugOverlay && Minecraft.getMinecraft().world != null && event.getType() == ElementType.ALL) {
         FontRenderer fontRender = Minecraft.getMinecraft().fontRenderer;
         GlStateManager.pushMatrix();
         GlStateManager.scale(0.6F, 0.6F, 1.0F);
         String text = getDebugText();
         int y = 3;

         for(String line : text.split("\n")) {
            String[] array = line.split("=");
            if (array.length == 2) {
               fontRender.drawString(array[0].trim(), 3, y, 16777215);
               fontRender.drawString(array[1].trim(), 100, y, 16777215);
            } else {
               fontRender.drawString(line, 3, y, 16777215);
            }

            y += 10;
         }

         GlStateManager.popMatrix();
      }

   }

   private static String getDebugText() {
      World world = Minecraft.getMinecraft().world;
      EntityPlayer player = Minecraft.getMinecraft().player;
      SHPlayerDataHandler.PlayerData data = SHPlayerDataHandler.get(player);
      EnumAreaDifficultyMode areaMode = Config.Difficulty.AREA_DIFFICULTY_MODE;
      if (data == null) {
         return "Player data is null!";
      } else {
         StringBuilder ret = new StringBuilder();
         ret.append(HString.format("Area Difficulty = %.4f (%s)\n", areaMode.getAreaDifficulty(world, player.getPosition()), areaMode.name()));
         ret.append(HString.format("Player Difficulty = %.4f\n", data.getDifficulty()));
         ret.append("Player Health = ").append(player.getHealth()).append(" / ").append(player.getMaxHealth()).append("\n");
         IAttributeInstance attr = player.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH);
         if (!attr.getModifiers().isEmpty()) {
            for(AttributeModifier mod : attr.getModifiers()) {
               ret.append("         ").append(mod).append("\n");
            }
         } else {
            ret.append("        No modifiers! That should not happen.\n");
         }

         int regenTimer = PlayerBonusRegenHandler.getTimerForPlayer(player);
         ret.append(HString.format("Regen Timer = %d (%ds)", regenTimer, regenTimer / 20)).append("\n");
         ret.append(HString.format("Food = %d (%.2f)", player.getFoodStats().getFoodLevel(), player.getFoodStats().getSaturationLevel())).append("\n");
         int blightCount = world.getEntities(EntityLivingBase.class, BlightHandler::isBlight).size();
         int blightFires = world.getEntities(EntityBlightFire.class, (e) -> {
            return true;
         }).size();
         ret.append(HString.format("Blights (Fires) = %d (%d)", blightCount, blightFires)).append("\n");
         int mobsProcessed = DifficultyHandler.debugGetMobsProcessed();
         float mobsProcessedRate = DifficultyHandler.debugGetMobsProcessedRate();
         ret.append(HString.format("Mobs processed = %d (%.1f/s avg)", mobsProcessed, mobsProcessedRate)).append("\n");
         return ret.toString();
      }
   }
}
