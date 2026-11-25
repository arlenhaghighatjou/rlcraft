package com.fantasticsource.mctools.gui.screen;

import com.fantasticsource.mctools.gui.GUIScreen;
import com.fantasticsource.mctools.gui.element.GUIElement;
import com.fantasticsource.mctools.gui.element.other.GUIDarkenedBackground;
import com.fantasticsource.mctools.gui.element.other.GUIGradient;
import com.fantasticsource.mctools.gui.element.other.GUIGradientBorder;
import com.fantasticsource.mctools.gui.element.text.GUIColor;
import com.fantasticsource.mctools.gui.element.text.GUILabeledTextInput;
import com.fantasticsource.mctools.gui.element.text.GUINavbar;
import com.fantasticsource.mctools.gui.element.text.GUITextButton;
import com.fantasticsource.mctools.gui.element.text.GUITextSpacer;
import com.fantasticsource.mctools.gui.element.text.filter.FilterColor;
import com.fantasticsource.mctools.gui.element.text.filter.FilterInt;
import com.fantasticsource.mctools.gui.element.text.filter.FilterRangedFloat;
import com.fantasticsource.mctools.gui.element.text.filter.FilterRangedInt;
import com.fantasticsource.mctools.gui.element.view.GUIView;
import com.fantasticsource.tools.datastructures.Color;
import net.minecraft.client.Minecraft;

public class ColorSelectionGUI extends GUIScreen {
   public ColorSelectionGUI(GUIColor clickedElement) {
      this(clickedElement, 1.0D);
   }

   public ColorSelectionGUI(GUIColor clickedElement, double textScale) {
      super(textScale);
      if (Minecraft.getMinecraft().currentScreen instanceof GUIScreen) {
         GUIScreen.showStacked(this);
      } else {
         Minecraft.getMinecraft().displayGuiScreen(this);
      }

      this.drawStack = false;
      Color color = clickedElement.getValue();
      GUIView left = new GUIView(this, 0.3D, 1.0D);
      GUIView center = new GUIView(this, 0.3D, 1.0D);
      GUIView right = new GUIView(this, 0.3D, 1.0D);
      FilterRangedInt filter0to255 = FilterRangedInt.get(0, 255);
      FilterRangedFloat filter0to1 = FilterRangedFloat.get(0.0F, 1.0F);
      GUILabeledTextInput hex = new GUILabeledTextInput(this, "Hex: ", color.hex8(), FilterColor.INSTANCE);
      GUILabeledTextInput dec = new GUILabeledTextInput(this, "Dec: ", "" + color.color(), FilterInt.INSTANCE);
      GUILabeledTextInput r = new GUILabeledTextInput(this, "Red (0-255): ", "" + color.r(), filter0to255);
      GUILabeledTextInput g = new GUILabeledTextInput(this, "Green (0-255): ", "" + color.g(), filter0to255);
      GUILabeledTextInput b = new GUILabeledTextInput(this, "Blue (0-255): ", "" + color.b(), filter0to255);
      GUILabeledTextInput a = new GUILabeledTextInput(this, "Alpha (0-255): ", "" + color.a(), filter0to255);
      GUILabeledTextInput rf = new GUILabeledTextInput(this, "Red (0-1): ", "" + color.rf(), filter0to1);
      GUILabeledTextInput gf = new GUILabeledTextInput(this, "Green (0-1): ", "" + color.gf(), filter0to1);
      GUILabeledTextInput bf = new GUILabeledTextInput(this, "Blue (0-1): ", "" + color.bf(), filter0to1);
      GUILabeledTextInput af = new GUILabeledTextInput(this, "Alpha (0-1): ", "" + color.af(), filter0to1);
      GUIGradient preview = new GUIGradient(this, 1.0D, 0.3D, color);
      GUITextButton save = new GUITextButton(this, "Save", Color.GREEN);
      GUITextButton cancel = new GUITextButton(this, "Cancel", Color.RED);
      this.root.add(new GUIDarkenedBackground(this));
      this.root.add(new GUINavbar(this));
      this.root.add(save.addClickActions(new Runnable[]{() -> {
         clickedElement.setValue(color);
         this.close();
      }}));
      this.root.add(cancel.addClickActions(new Runnable[]{this::close}));
      this.root.add(new GUIGradientBorder(this, 1.0D, 0.01D, 1.0D, Color.GRAY, Color.BLANK));
      this.root.addAll(new GUIElement[]{new GUIElement(this, 0.025D, 1.0D), left, new GUIElement(this, 0.025D, 1.0D), center, new GUIElement(this, 0.025D, 1.0D), right});
      left.add(new GUITextSpacer(this));
      left.add(r);
      r.input.addEditActions(new Runnable[]{() -> {
         if (r.input.isActive() && r.input.valid()) {
            color.setR(filter0to255.parse(r.input.getText()).intValue());
            preview.setColor(color);
            hex.setInput(color.hex8());
            dec.setInput("" + color.color());
            rf.setInput("" + color.rf());
         }

      }});
      left.add(new GUIElement(this, 1.0D, 0.0D));
      left.add(g);
      g.input.addEditActions(new Runnable[]{() -> {
         if (g.input.isActive() && g.input.valid()) {
            color.setG(filter0to255.parse(g.input.getText()).intValue());
            preview.setColor(color);
            hex.setInput(color.hex8());
            dec.setInput("" + color.color());
            gf.setInput("" + color.gf());
         }

      }});
      left.add(new GUIElement(this, 1.0D, 0.0D));
      left.add(b);
      b.input.addEditActions(new Runnable[]{() -> {
         if (b.input.isActive() && b.input.valid()) {
            color.setB(filter0to255.parse(b.input.getText()).intValue());
            preview.setColor(color);
            hex.setInput(color.hex8());
            dec.setInput("" + color.color());
            bf.setInput("" + color.bf());
         }

      }});
      left.add(new GUIElement(this, 1.0D, 0.0D));
      left.add(a);
      a.input.addEditActions(new Runnable[]{() -> {
         if (a.input.isActive() && a.input.valid()) {
            color.setA(filter0to255.parse(a.input.getText()).intValue());
            preview.setColor(color);
            hex.setInput(color.hex8());
            dec.setInput("" + color.color());
            af.setInput("" + color.af());
         }

      }});
      center.add(new GUITextSpacer(this));
      center.add(hex);
      hex.input.addEditActions(new Runnable[]{() -> {
         if (hex.input.isActive() && hex.input.valid()) {
            color.setColor(FilterColor.INSTANCE.parse(hex.input.getText()).intValue());
            preview.setColor(color);
            dec.setInput("" + color.color());
            r.setInput("" + color.r());
            g.setInput("" + color.g());
            b.setInput("" + color.b());
            a.setInput("" + color.a());
            rf.setInput("" + color.rf());
            gf.setInput("" + color.gf());
            bf.setInput("" + color.bf());
            af.setInput("" + color.af());
         }

      }});
      center.add(new GUITextSpacer(this));
      center.add(dec);
      dec.input.addEditActions(new Runnable[]{() -> {
         if (dec.input.isActive() && dec.input.valid()) {
            color.setColor(FilterInt.INSTANCE.parse(dec.input.getText()).intValue());
            preview.setColor(color);
            hex.setInput(color.hex8());
            r.setInput("" + color.r());
            g.setInput("" + color.g());
            b.setInput("" + color.b());
            a.setInput("" + color.a());
            rf.setInput("" + color.rf());
            gf.setInput("" + color.gf());
            bf.setInput("" + color.bf());
            af.setInput("" + color.af());
         }

      }});
      center.add(new GUITextSpacer(this));
      center.add(preview);
      right.add(new GUITextSpacer(this));
      right.add(rf);
      rf.input.addEditActions(new Runnable[]{() -> {
         if (rf.input.isActive() && rf.input.valid()) {
            color.setRF(filter0to1.parse(rf.input.getText()).floatValue());
            preview.setColor(color);
            hex.setInput(color.hex8());
            dec.setInput("" + color.color());
            r.setInput("" + color.r());
         }

      }});
      right.add(new GUIElement(this, 1.0D, 0.0D));
      right.add(gf);
      gf.input.addEditActions(new Runnable[]{() -> {
         if (gf.input.isActive() && gf.input.valid()) {
            color.setGF(filter0to1.parse(gf.input.getText()).floatValue());
            preview.setColor(color);
            hex.setInput(color.hex8());
            dec.setInput("" + color.color());
            g.setInput("" + color.g());
         }

      }});
      right.add(new GUIElement(this, 1.0D, 0.0D));
      right.add(bf);
      bf.input.addEditActions(new Runnable[]{() -> {
         if (bf.input.isActive() && bf.input.valid()) {
            color.setBF(filter0to1.parse(bf.input.getText()).floatValue());
            preview.setColor(color);
            hex.setInput(color.hex8());
            dec.setInput("" + color.color());
            b.setInput("" + color.b());
         }

      }});
      right.add(new GUIElement(this, 1.0D, 0.0D));
      right.add(af);
      af.input.addEditActions(new Runnable[]{() -> {
         if (af.input.isActive() && af.input.valid()) {
            color.setAF(filter0to1.parse(af.input.getText()).floatValue());
            preview.setColor(color);
            hex.setInput(color.hex8());
            dec.setInput("" + color.color());
            a.setInput("" + color.a());
         }

      }});
   }

   public String title() {
      return "Color";
   }
}
