package com.bloodnbonesgaming.lib.util;

import java.awt.Desktop;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.StringSelection;

public class ClipboardHelper {
   public ClipboardHelper() {
   }

   public static void copyToClipboard(String string) {
      if (Desktop.isDesktopSupported()) {
         StringSelection message = new StringSelection(string);
         Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
         clipboard.setContents(message, (ClipboardOwner)null);
      }

   }
}
