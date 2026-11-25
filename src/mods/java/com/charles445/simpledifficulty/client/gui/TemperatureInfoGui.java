package com.charles445.simpledifficulty.client.gui;

import com.charles445.simpledifficulty.api.config.QuickConfig;
import com.charles445.simpledifficulty.api.temperature.ITemperatureDynamicModifier;
import com.charles445.simpledifficulty.api.temperature.ITemperatureModifier;
import com.charles445.simpledifficulty.api.temperature.TemperatureRegistry;
import com.charles445.simpledifficulty.api.temperature.TemperatureUtil;
import com.charles445.simpledifficulty.config.ModConfig;
import com.charles445.simpledifficulty.util.WorldUtil;
import java.util.Map;
import java.util.TreeMap;
import java.util.Map.Entry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.client.event.RenderGameOverlayEvent.Post;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;

public class TemperatureInfoGui {
   private final Minecraft mc = Minecraft.getMinecraft();
   private int updateCounter = 0;
   public int xPadding = 2;
   public int yPadding = 2;
   public int transparency = -587202560;
   public int defaultColor;
   public int coldColor;
   public int hotColor;
   public Map<String, Float> resultMap;
   public int resultCumulative;

   public TemperatureInfoGui() {
      this.defaultColor = 16777215 | this.transparency;
      this.coldColor = 7829503 | this.transparency;
      this.hotColor = 16742263 | this.transparency;
      this.resultMap = new TreeMap();
      this.resultCumulative = 0;
   }

   @SubscribeEvent
   public void onPostRenderGameOverlay(Post event) {
      if (event.getType() == ElementType.TEXT && QuickConfig.isTemperatureEnabled() && ModConfig.client.temperatureReadout && !this.mc.gameSettings.showDebugInfo && (this.mc.player.getPermissionLevel() >= 2 || this.mc.isSingleplayer() || this.mc.player.isCreative())) {
         ScaledResolution resolution = event.getResolution();
         this.displayTemperature(resolution.getScaledWidth(), resolution.getScaledHeight());
      }

   }

   @SubscribeEvent
   public void onClientTick(ClientTickEvent event) {
      if (event.phase == Phase.END) {
         ++this.updateCounter;
         if (this.updateCounter % 10 == 0 && ModConfig.client.temperatureReadout) {
            this.updateTemperature();
         }
      }

   }

   public void updateTemperature() {
      if (this.mc.world != null && this.mc.player != null) {
         World world = this.mc.world;
         EntityPlayer player = this.mc.player;
         BlockPos pos = WorldUtil.getSidedBlockPos(world, player);
         this.resultMap.clear();
         float cumulative = 0.0F;

         for(ITemperatureModifier modifier : TemperatureRegistry.modifiers.values()) {
            float result = modifier.getWorldInfluence(world, pos);
            result = result + modifier.getPlayerInfluence(player);
            this.resultMap.put(modifier.getName(), Float.valueOf(result));
            cumulative += result;
         }

         for(ITemperatureDynamicModifier modifier : TemperatureRegistry.dynamicModifiers.values()) {
            float oldCumulative = cumulative;
            float var8 = modifier.applyDynamicWorldInfluence(world, pos, cumulative);
            cumulative = modifier.applyDynamicPlayerInfluence(player, var8);
            this.resultMap.put(modifier.getName(), Float.valueOf(cumulative - oldCumulative));
         }

         this.resultCumulative = (int)cumulative;
      }
   }

   public void displayTemperature(int width, int height) {
      GlStateManager.enableBlend();
      int yIncrement = this.mc.fontRenderer.FONT_HEIGHT;
      int xOffset = 0;
      int yOffset = 0;

      for(Entry<String, Float> entry : this.resultMap.entrySet()) {
         xOffset = 0;
         String name = (String)entry.getKey();
         Float value = (Float)entry.getValue();
         int valueColor = this.defaultColor;
         if (value.floatValue() > 0.0F) {
            valueColor = this.hotColor;
         } else if (value.floatValue() < 0.0F) {
            valueColor = this.coldColor;
         }

         xOffset = this.mc.fontRenderer.drawString(name + ": ", (float)(this.xPadding + xOffset), (float)(this.yPadding + yOffset), this.defaultColor, true);
         xOffset = this.mc.fontRenderer.drawString("" + value, (float)(this.xPadding + xOffset), (float)(this.yPadding + yOffset), valueColor, true);
         yOffset += yIncrement;
      }

      int valueColor = this.defaultColor;
      switch(TemperatureUtil.getTemperatureEnum(this.resultCumulative)) {
      case BURNING:
      case HOT:
         valueColor = this.hotColor;
         break;
      case COLD:
      case FREEZING:
         valueColor = this.coldColor;
      case NORMAL:
      }

      xOffset = 0;
      xOffset = this.mc.fontRenderer.drawString("---------", (float)(this.xPadding + xOffset), (float)(this.yPadding + yOffset), this.defaultColor, true);
      yOffset = yOffset + yIncrement;
      xOffset = 0;
      xOffset = this.mc.fontRenderer.drawString("Result: ", (float)(this.xPadding + xOffset), (float)(this.yPadding + yOffset), this.defaultColor, true);
      xOffset = this.mc.fontRenderer.drawString("" + this.resultCumulative, (float)(this.xPadding + xOffset), (float)(this.yPadding + yOffset), valueColor, true);
      int var10000 = yOffset + yIncrement;
      GlStateManager.disableBlend();
   }
}
