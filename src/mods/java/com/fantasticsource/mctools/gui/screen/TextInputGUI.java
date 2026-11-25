package com.fantasticsource.mctools.gui.screen;

import com.fantasticsource.mctools.gui.GUIScreen;
import com.fantasticsource.mctools.gui.element.GUIElement;
import com.fantasticsource.mctools.gui.element.other.GUIDarkenedBackground;
import com.fantasticsource.mctools.gui.element.text.GUILabeledTextInput;
import com.fantasticsource.mctools.gui.element.text.GUINavbar;
import com.fantasticsource.mctools.gui.element.text.GUITextButton;
import com.fantasticsource.mctools.gui.element.text.GUITextSpacer;
import com.fantasticsource.mctools.gui.element.text.filter.FilterNone;
import com.fantasticsource.mctools.gui.element.text.filter.TextFilter;
import com.fantasticsource.tools.datastructures.Color;

public class TextInputGUI extends GUIScreen {
   public final String title;
   public boolean pressedDone;
   public GUILabeledTextInput input;
   public GUITextButton doneButton;
   public GUITextButton cancelButton;

   public TextInputGUI(String title, String inputLabel, String defaultInput) {
      this(title, inputLabel, defaultInput, FilterNone.INSTANCE, 1.0D);
   }

   public TextInputGUI(String title, String inputLabel, String defaultInput, double textScale) {
      this(title, inputLabel, defaultInput, FilterNone.INSTANCE, textScale);
   }

   public TextInputGUI(String title, String inputLabel) {
      this(title, inputLabel, FilterNone.INSTANCE, 1.0D);
   }

   public TextInputGUI(String title, String inputLabel, double textScale) {
      this(title, inputLabel, "", FilterNone.INSTANCE, textScale);
   }

   public TextInputGUI(String title, String inputLabel, TextFilter filter) {
      this(title, inputLabel, filter, 1.0D);
   }

   public TextInputGUI(String title, String inputLabel, TextFilter filter, double textScale) {
      this(title, inputLabel, "", filter, textScale);
   }

   public TextInputGUI(String title, String inputLabel, String defaultInput, TextFilter filter) {
      this(title, inputLabel, defaultInput, filter, 1.0D);
   }

   public TextInputGUI(String title, String inputLabel, String defaultInput, TextFilter filter, double textScale) {
      super(textScale);
      this.pressedDone = false;
      this.title = title;
      this.show();
      this.drawStack = false;
      this.root.addAll(new GUIElement[]{new GUIDarkenedBackground(this), new GUINavbar(this)});
      this.root.setSubElementAutoplaceMethod((byte)8);
      this.input = new GUILabeledTextInput(this, inputLabel, defaultInput, filter);
      this.doneButton = new GUITextButton(this, "Done", Color.GREEN);
      this.cancelButton = new GUITextButton(this, "Cancel", Color.RED);
      this.root.addAll(new GUIElement[]{new GUITextSpacer(this), this.input, new GUITextSpacer(this), this.doneButton.addClickActions(new Runnable[]{() -> {
         this.pressedDone = true;
         this.close();
      }}), new GUITextSpacer(this), this.cancelButton.addClickActions(new Runnable[]{this::close})});
      this.input.input.setActive(true);
   }

   public String title() {
      return this.title;
   }
}
