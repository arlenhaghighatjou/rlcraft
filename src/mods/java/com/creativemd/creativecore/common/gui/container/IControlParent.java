package com.creativemd.creativecore.common.gui.container;

import com.creativemd.creativecore.common.gui.CoreControl;
import java.util.List;
import net.minecraft.entity.player.EntityPlayer;

public interface IControlParent {
   List getControls();

   void refreshControls();

   CoreControl get(String var1);

   boolean has(String var1);

   EntityPlayer getPlayer();

   default void removeControls(String... exclude) {
      List controls = this.getControls();
      int i = 0;

      while(i < controls.size()) {
         boolean canBeDeleted = true;
         if (controls.get(i) instanceof CoreControl) {
            for(int j = 0; j < exclude.length; ++j) {
               if (((CoreControl)controls.get(i)).name.contains(exclude[j])) {
                  canBeDeleted = false;
                  break;
               }
            }
         }

         if (canBeDeleted) {
            controls.remove(i);
         } else {
            ++i;
         }
      }

   }

   default void moveControlBehind(CoreControl control, CoreControl controlInBack) {
      List controls = this.getControls();
      if (controls.contains(controlInBack) && controls.remove(control) && controls.indexOf(controlInBack) + 1 < controls.size()) {
         controls.add(controls.indexOf(controlInBack) + 1, control);
      } else {
         this.moveControlToBottom(control);
      }

      this.refreshControls();
   }

   default void moveControlAbove(CoreControl control, CoreControl controlInFront) {
      List controls = this.getControls();
      if (controls.contains(controlInFront) && controls.remove(control)) {
         controls.add(controls.indexOf(controlInFront), control);
      }

      this.refreshControls();
   }

   default void moveControlToTop(CoreControl control) {
      List controls = this.getControls();
      if (controls.remove(control)) {
         controls.add(0, control);
      }

      this.refreshControls();
   }

   default void moveControlToBottom(CoreControl control) {
      List controls = this.getControls();
      if (controls.remove(control)) {
         controls.add(control);
      }

      this.refreshControls();
   }
}
