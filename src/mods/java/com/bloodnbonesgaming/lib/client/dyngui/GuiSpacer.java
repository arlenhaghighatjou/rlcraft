package com.bloodnbonesgaming.lib.client.dyngui;

import java.beans.ConstructorProperties;

public class GuiSpacer {
   private final int columns;
   private final int rows;

   @ConstructorProperties({"columns", "rows"})
   public GuiSpacer(int columns, int rows) {
      this.columns = columns;
      this.rows = rows;
   }
}
