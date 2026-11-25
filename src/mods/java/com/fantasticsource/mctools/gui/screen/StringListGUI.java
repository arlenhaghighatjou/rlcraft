package com.fantasticsource.mctools.gui.screen;

import com.fantasticsource.mctools.gui.GUIScreen;
import com.fantasticsource.mctools.gui.Namespace;
import com.fantasticsource.mctools.gui.element.GUIElement;
import com.fantasticsource.mctools.gui.element.other.GUIDarkenedBackground;
import com.fantasticsource.mctools.gui.element.other.GUIVerticalScrollbar;
import com.fantasticsource.mctools.gui.element.text.GUILabeledTextInput;
import com.fantasticsource.mctools.gui.element.text.GUINavbar;
import com.fantasticsource.mctools.gui.element.text.GUITextButton;
import com.fantasticsource.mctools.gui.element.text.filter.FilterNotEmpty;
import com.fantasticsource.mctools.gui.element.view.GUIList;
import com.fantasticsource.tools.datastructures.Color;
import java.util.LinkedHashSet;
import java.util.function.Function;
import java.util.function.Predicate;

public class StringListGUI extends GUIScreen {
   protected String title;

   protected StringListGUI(String title) {
      this.title = title;
   }

   public static void show(String title, final String prompt, final String baseName, LinkedHashSet<String> strings) {
      final StringListGUI gui = new StringListGUI(title);
      showStacked(gui);
      gui.drawStack = false;
      gui.root.add(new GUIDarkenedBackground(gui));
      GUINavbar navbar = new GUINavbar(gui);
      GUITextButton done = new GUITextButton(gui, "Done", Color.GREEN);
      GUITextButton cancel = new GUITextButton(gui, "Cancel", Color.RED);
      gui.root.addAll(new GUIElement[]{navbar, done, cancel});
      GUIList list = new GUIList(gui, true, 0.98D, 1.0D - (cancel.y + cancel.height), new GUIElement[0]) {
         public GUIElement[] newLineDefaultElements() {
            Namespace namespace = (Namespace)gui.namespaces.computeIfAbsent("Strings", (o) -> {
               return new Namespace();
            });
            String nameString = namespace.getFirstAvailableNumberedName(baseName);
            GUILabeledTextInput input = (new GUILabeledTextInput(gui, prompt, nameString, FilterNotEmpty.INSTANCE)).setNamespace("Strings");
            return new GUIElement[]{input};
         }
      };
      list.addRemoveChildActions(new Predicate[]{(element) -> {
         if (element instanceof GUIList.Line) {
            GUIList.Line line = (GUIList.Line)element;
            GUILabeledTextInput labeledTextInput = (GUILabeledTextInput)line.getLineElement(0);
            ((Namespace)gui.namespaces.get("Strings")).inputs.remove(labeledTextInput.input);
         }

         return false;
      }});
      GUIVerticalScrollbar scrollbar = new GUIVerticalScrollbar(gui, 0.02D, 1.0D - (cancel.y + cancel.height), Color.GRAY, Color.BLANK, Color.WHITE, Color.BLANK, list);
      gui.root.addAll(new GUIElement[]{list, scrollbar});

      for(String s : strings) {
         ((GUILabeledTextInput)list.addLine().getLineElement(0)).setText(s);
      }

      cancel.addRecalcActions(new Runnable[]{() -> {
         list.height = 1.0D - (cancel.y + cancel.height);
         scrollbar.height = 1.0D - (cancel.y + cancel.height);
      }});
      cancel.addClickActions(new Runnable[]{gui::close});
      done.addClickActions(new Runnable[]{() -> {
         for(GUIList.Line line : list.getLines()) {
            if (!((GUILabeledTextInput)line.getLineElement(0)).valid()) {
               return;
            }
         }

         strings.clear();

         for(GUIList.Line line : list.getLines()) {
            strings.add(((GUILabeledTextInput)line.getLineElement(0)).getText());
         }

         gui.close();
      }});
   }

   public String title() {
      return this.title;
   }
}
