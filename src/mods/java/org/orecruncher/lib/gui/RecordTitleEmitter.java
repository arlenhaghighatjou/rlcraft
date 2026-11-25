package org.orecruncher.lib.gui;

import javax.annotation.Nonnull;
import net.minecraft.client.Minecraft;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RecordTitleEmitter {
   private static final int VANILLA_DISPLAY_TIME = 60;
   private static final int DEFAULT_DISPLAY_TIME = 200;
   protected final Minecraft mc;
   protected final String title;
   protected final RecordTitleEmitter.ITimeKeeper time;
   protected int expiry;

   public RecordTitleEmitter(@Nonnull String title, @Nonnull RecordTitleEmitter.ITimeKeeper time) {
      this(title, time, 200);
   }

   public RecordTitleEmitter(@Nonnull String title, @Nonnull RecordTitleEmitter.ITimeKeeper time, int ticksToDisplay) {
      this.mc = Minecraft.getMinecraft();
      this.title = TextFormatting.GOLD + title;
      this.time = time;
      this.expiry = this.time.getTickMark() + ticksToDisplay - 60;
   }

   public void update() {
      if (this.time.getTickMark() <= this.expiry) {
         this.mc.ingameGUI.setOverlayMessage(this.title, false);
      } else {
         this.expiry = 0;
      }

   }

   public interface ITimeKeeper {
      int getTickMark();
   }
}
