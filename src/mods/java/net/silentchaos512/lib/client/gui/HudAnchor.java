package net.silentchaos512.lib.client.gui;

public enum HudAnchor {
   TOP_LEFT {
      public int getX(int scaledWidth, int padding) {
         return padding;
      }

      public int getY(int scaledHeight, int padding) {
         return padding;
      }
   },
   TOP_CENTER {
      public int getX(int scaledWidth, int padding) {
         return scaledWidth / 2;
      }

      public int getY(int scaledHeight, int padding) {
         return padding;
      }
   },
   TOP_RIGHT {
      public int getX(int scaledWidth, int padding) {
         return scaledWidth - padding;
      }

      public int getY(int scaledHeight, int padding) {
         return padding;
      }
   },
   CENTER_LEFT {
      public int getX(int scaledWidth, int padding) {
         return padding;
      }

      public int getY(int scaledHeight, int padding) {
         return scaledHeight / 2;
      }
   },
   CENTER {
      public int getX(int scaledWidth, int padding) {
         return scaledWidth / 2;
      }

      public int getY(int scaledHeight, int padding) {
         return scaledHeight / 2;
      }
   },
   CENTER_RIGHT {
      public int getX(int scaledWidth, int padding) {
         return scaledWidth - padding;
      }

      public int getY(int scaledHeight, int padding) {
         return scaledHeight / 2;
      }
   },
   BOTTOM_LEFT {
      public int getX(int scaledWidth, int padding) {
         return padding;
      }

      public int getY(int scaledHeight, int padding) {
         return scaledHeight - padding;
      }
   },
   BOTTOM_CENTER {
      public int getX(int scaledWidth, int padding) {
         return scaledWidth / 2;
      }

      public int getY(int scaledHeight, int padding) {
         return scaledHeight - padding;
      }
   },
   BOTTOM_RIGHT {
      public int getX(int scaledWidth, int padding) {
         return scaledWidth - padding;
      }

      public int getY(int scaledHeight, int padding) {
         return scaledHeight - padding;
      }
   };

   private HudAnchor() {
   }

   public abstract int getX(int var1, int var2);

   public abstract int getY(int var1, int var2);

   public int getX(int scaledWidth) {
      return this.getX(scaledWidth, 0);
   }

   public int getY(int scaledHeight) {
      return this.getY(scaledHeight, 0);
   }

   public int offsetX(int scaledWidth, int amount) {
      return this.getX(scaledWidth, 0) + amount;
   }

   public int offsetY(int scaledHeight, int amount) {
      return this.getY(scaledHeight, 0) + amount;
   }
}
