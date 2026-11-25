package hunternif.mc.atlas.client.gui;

import net.minecraft.client.resources.I18n;

public enum ExportUpdateListener {
   INSTANCE;

   public float maxProgress;
   public int currentProgress;
   public String status;
   public String header;

   private ExportUpdateListener() {
   }

   public void setStatusString(String status, Object... data) {
      this.status = I18n.format(status, data);
   }

   public void setHeaderString(String header, Object... data) {
      this.header = I18n.format(header, data);
   }

   public void setProgressMax(int max) {
      this.maxProgress = (float)max;
      this.currentProgress = 0;
   }

   public void setProgress(int progress) {
      this.currentProgress = progress;
   }

   public void addProgress(int amount) {
      this.currentProgress += amount;
   }
}
