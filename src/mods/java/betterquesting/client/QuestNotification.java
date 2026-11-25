package betterquesting.client;

import betterquesting.api.storage.BQ_Settings;
import betterquesting.api.utils.RenderUtils;
import betterquesting.api2.utils.QuestTranslation;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.ScaledResolution;
import net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager;
import net.minecraft.client.renderer.GlStateManager.DestFactor;
import net.minecraft.client.renderer.GlStateManager.SourceFactor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.client.event.RenderGameOverlayEvent.Post;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class QuestNotification {
   private static final List<QuestNotification.QuestNotice> notices = new ArrayList();

   public QuestNotification() {
   }

   public static void ScheduleNotice(String mainTxt, String subTxt, ItemStack icon, String sound) {
      if (BQ_Settings.questNotices) {
         notices.add(new QuestNotification.QuestNotice(mainTxt, subTxt, icon, sound));
      }

   }

   public static void resetNotices() {
      notices.clear();
   }

   @SubscribeEvent
   public void onDrawScreen(Post event) {
      if (event.getType() == ElementType.ALL) {
         if (notices.size() > 0) {
            if (notices.size() < 20 && BQ_Settings.questNotices) {
               Minecraft mc = Minecraft.getMinecraft();
               ScaledResolution resolution = new ScaledResolution(mc);
               int width = resolution.getScaledWidth();
               int height = resolution.getScaledHeight();
               QuestNotification.QuestNotice notice = (QuestNotification.QuestNotice)notices.get(0);
               if (!notice.init) {
                  if (mc.isGamePaused() || mc.currentScreen != null) {
                     return;
                  }

                  notice.init = true;
                  notice.startTime = Minecraft.getSystemTime();
                  mc.getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(new SoundEvent(new ResourceLocation(notice.sound)), 1.0F));
               }

               if (notice.getTime() >= 6.0F) {
                  notices.remove(0);
               } else {
                  GlStateManager.pushMatrix();
                  float scale = width > 600 ? 1.5F : 1.0F;
                  GlStateManager.scale(scale, scale, scale);
                  width = MathHelper.ceil((float)width / scale);
                  height = MathHelper.ceil((float)height / scale);
                  float alpha = notice.getTime() <= 4.0F ? Math.min(1.0F, notice.getTime()) : Math.max(0.0F, 5.0F - notice.getTime());
                  alpha = MathHelper.clamp(alpha, 0.02F, 1.0F);
                  int color = (new Color(1.0F, 1.0F, 1.0F, alpha)).getRGB();
                  GlStateManager.color(1.0F, 1.0F, 1.0F, alpha);
                  if (notice.icon != null) {
                     RenderUtils.RenderItemStack(mc, notice.icon, width / 2 - 8, height / 4 - 20, "", new Color(1.0F, 1.0F, 1.0F, alpha));
                  }

                  GlStateManager.enableBlend();
                  GlStateManager.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA);
                  String tmp = TextFormatting.UNDERLINE + "" + TextFormatting.BOLD + QuestTranslation.translate(notice.mainTxt);
                  int txtW = RenderUtils.getStringWidth(tmp, mc.fontRenderer);
                  mc.fontRenderer.drawString(tmp, (float)(width / 2 - txtW / 2), (float)(height / 4), color, false);
                  tmp = QuestTranslation.translate(notice.subTxt);
                  txtW = RenderUtils.getStringWidth(tmp, mc.fontRenderer);
                  mc.fontRenderer.drawString(tmp, (float)(width / 2 - txtW / 2), (float)(height / 4 + 12), color, false);
                  GlStateManager.popMatrix();
               }
            } else {
               notices.clear();
            }
         }
      }
   }

   public static class QuestNotice {
      public long startTime = Minecraft.getSystemTime();
      public boolean init = false;
      private final String mainTxt;
      private final String subTxt;
      private final ItemStack icon;
      private final String sound;

      public QuestNotice(String mainTxt, String subTxt, ItemStack icon, String sound) {
         this.mainTxt = mainTxt;
         this.subTxt = subTxt;
         this.icon = icon;
         this.sound = sound;
      }

      public float getTime() {
         return (float)(Minecraft.getSystemTime() - this.startTime) / 1000.0F;
      }
   }
}
