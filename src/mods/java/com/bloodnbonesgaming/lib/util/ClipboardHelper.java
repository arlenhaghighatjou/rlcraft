package com.bloodnbonesgaming.lib.util;

import net.lax1dude.eaglercraft.v1_8.EagRuntime;

public class ClipboardHelper {
   public static void copyToClipboard(String string) {
      EagRuntime.setClipboard(string);
   }
}
