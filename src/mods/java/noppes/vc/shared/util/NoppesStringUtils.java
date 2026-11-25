package noppes.vc.shared.util;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.util.Arrays;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.translation.I18n;
import noppes.vc.VariedCommodities;

public class NoppesStringUtils {
   static final int[] illegalChars = new int[]{34, 60, 62, 124, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 58, 42, 63, 92, 47};

   public NoppesStringUtils() {
   }

   public static String cleanFileName(String badFileName) {
      StringBuilder cleanName = new StringBuilder();

      for(int i = 0; i < badFileName.length(); ++i) {
         int c = badFileName.charAt(i);
         if (Arrays.binarySearch(illegalChars, c) < 0) {
            cleanName.append((char)c);
         }
      }

      return cleanName.toString();
   }

   public static String formatText(String text, Object... obs) {
      for(Object ob : obs) {
         if (ob instanceof EntityPlayer) {
            String username = ((EntityPlayer)ob).getDisplayNameString();
            text = text.replace("{player}", username);
            text = text.replace("@p", username);
         }
      }

      text = text.replace("&", Character.toChars(167)[0] + "");
      return text;
   }

   public static void setClipboardContents(String aString) {
      StringSelection stringSelection = new StringSelection(aString);
      Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
      clipboard.setContents(stringSelection, new ClipboardOwner() {
         public void lostOwnership(Clipboard arg0, Transferable arg1) {
         }
      });
   }

   public static String getClipboardContents() {
      String result = "";
      Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
      Transferable contents = clipboard.getContents((Object)null);
      boolean hasTransferableText = contents != null && contents.isDataFlavorSupported(DataFlavor.stringFlavor);
      if (hasTransferableText) {
         try {
            result = (String)contents.getTransferData(DataFlavor.stringFlavor);
         } catch (Exception var5) {
            VariedCommodities.Log.error(var5);
         }
      }

      return result;
   }

   public static String translate(Object... arr) {
      String s = "";

      for(Object str : arr) {
         s = s + I18n.translateToLocal(str.toString());
      }

      return s;
   }

   public static String[] splitLines(String s) {
      return s.split("\r\n|\r|\n");
   }

   public static String newLine() {
      return System.getProperty("line.separator");
   }

   static {
      Arrays.sort(illegalChars);
   }
}
