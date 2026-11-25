package vazkii.quark.client.feature;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.settings.GameSettings.Options;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.client.event.RenderGameOverlayEvent.Post;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent.KeyInputEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vazkii.quark.base.client.ModKeybinds;
import vazkii.quark.base.module.Feature;

public class AutoJumpHotkey extends Feature {
   private static final float DISPLAY_TIME = 10.0F;
   private static final float FADE_TIME = 10.0F;
   private static final float TOTAL_TIME = 20.0F;
   private static final ResourceLocation AUTOJUMP_OFF = new ResourceLocation("quark", "textures/misc/autojump_off.png");
   private static final ResourceLocation AUTOJUMP_ON = new ResourceLocation("quark", "textures/misc/autojump_on.png");
   public static int hudTime;

   public AutoJumpHotkey() {
   }

   @SideOnly(Side.CLIENT)
   public void preInitClient() {
      ModKeybinds.initAutoJumpKey();
   }

   @SubscribeEvent
   @SideOnly(Side.CLIENT)
   public void onKeyInput(KeyInputEvent event) {
      Minecraft mc = Minecraft.getMinecraft();
      boolean down = ModKeybinds.autoJumpKey.isKeyDown();
      if (mc.inGameHasFocus && down) {
         boolean b = mc.gameSettings.getOptionOrdinalValue(Options.AUTO_JUMP);
         int i = b ? 1 : 0;
         mc.gameSettings.setOptionValue(Options.AUTO_JUMP, ~i & 1);
         hudTime = 20;
      }

   }

   @SubscribeEvent
   @SideOnly(Side.CLIENT)
   public void onClientTick(ClientTickEvent event) {
      if (event.phase == Phase.END && hudTime > 0) {
         --hudTime;
      }

   }

   @SubscribeEvent
   @SideOnly(Side.CLIENT)
   public void onRenderOverlay(Post event) {
      if (event.getType() == ElementType.ALL && hudTime != 0) {
         Minecraft mc = Minecraft.getMinecraft();
         ScaledResolution res = event.getResolution();
         int x = res.getScaledWidth() / 2 - 16;
         int y = res.getScaledHeight() - 100;
         boolean autojumpEnabled = mc.gameSettings.getOptionOrdinalValue(Options.AUTO_JUMP);
         float alpha = ((float)hudTime + event.getPartialTicks()) / 10.0F;
         GlStateManager.pushMatrix();
         GlStateManager.enableBlend();
         GlStateManager.blendFunc(770, 771);
         GlStateManager.color(1.0F, 1.0F, 1.0F, alpha);
         mc.renderEngine.bindTexture(autojumpEnabled ? AUTOJUMP_ON : AUTOJUMP_OFF);
         Gui.drawModalRectWithCustomSizedTexture(x, y, 0.0F, 0.0F, 32, 32, 32.0F, 32.0F);
         GlStateManager.popMatrix();
      }
   }

   public boolean hasSubscriptions() {
      return this.isClient();
   }

   public boolean requiresMinecraftRestartToEnable() {
      return true;
   }
}
