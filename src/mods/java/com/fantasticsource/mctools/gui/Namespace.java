package com.fantasticsource.mctools.gui;

import com.fantasticsource.mctools.gui.element.text.GUITextInput;
import java.util.ArrayList;

public class Namespace {
   public final ArrayList<GUITextInput> inputs = new ArrayList();

   public Namespace() {
   }

   public boolean contains(String value) {
      if (value == null) {
         for(GUITextInput input : this.inputs) {
            if (input.getText() == null) {
               return true;
            }
         }
      } else {
         for(GUITextInput input : this.inputs) {
            if (value.equals(input.getText())) {
               return true;
            }
         }
      }

      return false;
   }

   public boolean containsIgnoreObject(String value, GUITextInput ignore) {
      if (value == null) {
         for(GUITextInput input : this.inputs) {
            if (input != ignore && input.getText() == null) {
               return true;
            }
         }
      } else {
         for(GUITextInput input : this.inputs) {
            if (input != ignore && value.equals(input.getText())) {
               return true;
            }
         }
      }

      return false;
   }

   public String getFirstAvailableNumberedName(String baseName) {
      if (!this.contains(baseName)) {
         return baseName;
      } else {
         int i;
         for(i = 2; this.contains(baseName + i); ++i) {
            ;
         }

         return baseName + i;
      }
   }
}
