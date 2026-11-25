package net.ilexiconn.llibrary.server.snackbar;

import net.ilexiconn.llibrary.LLibrary;

public enum SnackbarHandler {
   INSTANCE;

   private SnackbarHandler() {
   }

   public void showSnackbar(Snackbar snackbar) {
      LLibrary.PROXY.showSnackbar(snackbar);
   }
}
