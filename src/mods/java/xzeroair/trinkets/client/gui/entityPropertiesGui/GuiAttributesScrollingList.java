package xzeroair.trinkets.client.gui.entityPropertiesGui;

import java.util.ArrayList;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraftforge.fml.client.GuiScrollingList;
import xzeroair.trinkets.traits.AbilityHandler;

public class GuiAttributesScrollingList extends GuiScrollingList {
   private GuiAttributesScreen parent;
   private ArrayList<AbilityHandler.AbilityHolder> abilities;
   private int id;

   public GuiAttributesScrollingList(int ID, GuiAttributesScreen parent, ArrayList<AbilityHandler.AbilityHolder> abilities, int listWidth, int slotHeight) {
      super(parent.mc, listWidth, parent.height, 32, parent.height - 88 + 4, 10, slotHeight, parent.width, parent.height);
      this.id = ID;
      this.parent = parent;
      this.abilities = abilities;
   }

   protected int getSize() {
      return this.abilities.size();
   }

   protected void elementClicked(int index, boolean doubleClick) {
      this.parent.selectAbilityIndex(index);
   }

   protected boolean isSelected(int index) {
      return this.parent.abilityIndexSelected(index);
   }

   protected void drawBackground() {
   }

   protected int getContentHeight() {
      return this.getSize() * this.slotHeight + 1;
   }

   ArrayList<AbilityHandler.AbilityHolder> getAbilities() {
      return this.abilities;
   }

   protected void drawSlot(int idx, int right, int top, int height, Tessellator tess) {
      if (idx < this.abilities.size()) {
         AbilityHandler.AbilityHolder ability = (AbilityHandler.AbilityHolder)this.abilities.get(idx);
         String name = ability == null ? "ERROR" : ability.getAbility().getDisplayName();
         FontRenderer font = this.parent.getFontRenderer();
         font.drawString(font.trimStringToWidth(name, this.listWidth - 10), this.left + 3, top + 2, 16777215);
      }
   }
}
