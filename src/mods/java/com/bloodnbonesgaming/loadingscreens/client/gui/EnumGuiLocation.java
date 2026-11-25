package com.bloodnbonesgaming.loadingscreens.client.gui;

public enum EnumGuiLocation {
   TOP_LEFT {
      public int getX(int displayWidth, int imageWidth) {
         return 0;
      }

      public int getY(int displayHeight, int imageHeight) {
         return 0;
      }
   },
   TOP_CENTER {
      public int getX(int displayWidth, int imageWidth) {
         return displayWidth / 2 - imageWidth / 2;
      }

      public int getY(int displayHeight, int imageHeight) {
         return 0;
      }
   },
   TOP_RIGHT {
      public int getX(int displayWidth, int imageWidth) {
         return displayWidth - imageWidth;
      }

      public int getY(int displayHeight, int imageHeight) {
         return 0;
      }
   },
   LEFT {
      public int getX(int displayWidth, int imageWidth) {
         return 0;
      }

      public int getY(int displayHeight, int imageHeight) {
         return displayHeight / 2 - imageHeight / 2;
      }
   },
   CENTER {
      public int getX(int displayWidth, int imageWidth) {
         return displayWidth / 2 - imageWidth / 2;
      }

      public int getY(int displayHeight, int imageHeight) {
         return displayHeight / 2 - imageHeight / 2;
      }
   },
   RIGHT {
      public int getX(int displayWidth, int imageWidth) {
         return displayWidth - imageWidth;
      }

      public int getY(int displayHeight, int imageHeight) {
         return displayHeight / 2 - imageHeight / 2;
      }
   },
   BOTTOM_LEFT {
      public int getX(int displayWidth, int imageWidth) {
         return 0;
      }

      public int getY(int displayHeight, int imageHeight) {
         return displayHeight - imageHeight;
      }
   },
   BOTTOM_CENTER {
      public int getX(int displayWidth, int imageWidth) {
         return displayWidth / 2 - imageWidth / 2;
      }

      public int getY(int displayHeight, int imageHeight) {
         return displayHeight - imageHeight;
      }
   },
   BOTTOM_RIGHT {
      public int getX(int displayWidth, int imageWidth) {
         return displayWidth - imageWidth;
      }

      public int getY(int displayHeight, int imageHeight) {
         return displayHeight - imageHeight;
      }
   };

   private EnumGuiLocation() {
   }

   public abstract int getX(int var1, int var2);

   public abstract int getY(int var1, int var2);
}
