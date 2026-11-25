package ivorius.reccomplex.gui;

import ivorius.reccomplex.RecurrentComplex;
import ivorius.reccomplex.commands.RCCommands;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.Entity;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.util.text.event.HoverEvent;
import net.minecraft.util.text.event.ClickEvent.Action;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiHider {
   protected static GuiScreen hiddenGUI;
   protected static GuiHider.Visualizer visualizer;

   public GuiHider() {
   }

   public static boolean canHide() {
      return hiddenGUI == null;
   }

   public static boolean hideGUI() {
      return hideGUI((GuiHider.Visualizer)null);
   }

   public static boolean hideGUI(GuiHider.Visualizer visualizer) {
      if (!canHide()) {
         return false;
      } else {
         Minecraft mc = Minecraft.getMinecraft();
         hiddenGUI = mc.currentScreen;
         visualizer = visualizer;
         if (hiddenGUI == null) {
            return false;
         } else {
            mc.displayGuiScreen((GuiScreen)null);
            ITextComponent reopen = new TextComponentString("/" + RCCommands.reopen.getName());
            reopen.getStyle().setColor(TextFormatting.GREEN);
            reopen.getStyle().setClickEvent(new ClickEvent(Action.RUN_COMMAND, "/" + RCCommands.reopen.getName()));
            reopen.getStyle().setHoverEvent(new HoverEvent(net.minecraft.util.text.event.HoverEvent.Action.SHOW_TEXT, RecurrentComplex.translations.get("commands.rcreopen.run")));
            mc.player.sendMessage(RecurrentComplex.translations.format("commands.rc.didhide", reopen));
            return true;
         }
      }
   }

   public static boolean canReopen() {
      return hiddenGUI != null;
   }

   public static boolean reopenGUI() {
      if (!canReopen()) {
         return false;
      } else {
         GuiScreen hiddenGUI = hiddenGUI;
         hiddenGUI = null;
         visualizer = null;
         Minecraft mc = Minecraft.getMinecraft();
         mc.displayGuiScreen(hiddenGUI);
         if (mc.currentScreen == null) {
            hiddenGUI = hiddenGUI;
            return false;
         } else {
            return true;
         }
      }
   }

   public static void tryReopenGUI() {
      Minecraft mc = Minecraft.getMinecraft();
      if (!canReopen()) {
         mc.player.sendMessage(RecurrentComplex.translations.get("commands.rcreopen.nogui"));
      } else {
         if (!reopenGUI()) {
            mc.player.sendMessage(RecurrentComplex.translations.get("commands.rcreopen.fail"));
         }

      }
   }

   public static void draw(Entity renderEntity, float partialTicks) {
      Minecraft mc = Minecraft.getMinecraft();
      if (renderEntity == mc.player && visualizer != null) {
         visualizer.draw(renderEntity, partialTicks);
      }

   }

   public interface Visualizer {
      void draw(Entity var1, float var2);
   }
}
