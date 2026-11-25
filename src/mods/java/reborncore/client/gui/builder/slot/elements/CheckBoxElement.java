package reborncore.client.gui.builder.slot.elements;

import java.util.function.Predicate;
import reborncore.client.gui.builder.GuiBase;
import reborncore.common.tile.RebornMachineTile;

public class CheckBoxElement extends ElementBase {
   public String label;
   public String type;
   public int labelColor;
   public int slotID;
   public RebornMachineTile machineBase;
   Predicate<CheckBoxElement> ticked;
   private Sprite.CheckBox checkBoxSprite;

   public CheckBoxElement(String label, int labelColor, int x, int y, String type, int slotID, Sprite.CheckBox checkBoxSprite, RebornMachineTile machineBase, Predicate<CheckBoxElement> ticked) {
      super(x, y, checkBoxSprite.getNormal());
      this.checkBoxSprite = checkBoxSprite;
      this.type = type;
      this.slotID = slotID;
      this.machineBase = machineBase;
      this.label = label;
      this.labelColor = labelColor;
      this.ticked = ticked;
      if (ticked.test(this)) {
         this.container.setSprite(0, checkBoxSprite.getTicked());
      } else {
         this.container.setSprite(0, checkBoxSprite.getNormal());
      }

      this.addPressAction((element, gui, provider, mouseX, mouseY) -> {
         if (ticked.test(this)) {
            element.container.setSprite(0, checkBoxSprite.getTicked());
         } else {
            element.container.setSprite(0, checkBoxSprite.getNormal());
         }

         return (void)true;
      });
   }

   public void draw(GuiBase gui) {
      ISprite sprite = this.checkBoxSprite.getNormal();
      if (this.ticked.test(this)) {
         sprite = this.checkBoxSprite.getTicked();
      }

      this.drawSprite(gui, sprite, this.x, this.y);
      this.drawString(gui, this.label, this.x + this.checkBoxSprite.getNormal().width + 5, this.y + this.getHeight(gui.getMachine()) / 2 - gui.mc.fontRenderer.FONT_HEIGHT / 2, this.labelColor);
   }
}
