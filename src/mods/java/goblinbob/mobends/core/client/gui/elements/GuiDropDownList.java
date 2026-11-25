package goblinbob.mobends.core.client.gui.elements;

import goblinbob.mobends.core.client.gui.GuiBendsMenu;
import goblinbob.mobends.core.client.gui.IChangeListener;
import goblinbob.mobends.core.client.gui.IObservable;
import goblinbob.mobends.core.util.Draw;
import goblinbob.mobends.core.util.GUtil;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import org.lwjgl.input.Mouse;

public class GuiDropDownList<T> implements IObservable {
   public static final int HEIGHT = 16;
   public static final int LIST_HEIGHT = 90;
   public static final int ELEMENT_HEIGHT = 11;
   public static final int SCROLLBAR_WIDTH = 5;
   private final FontRenderer fontRenderer;
   protected final List<GuiDropDownList.Entry<T>> entries;
   private int x;
   private int y;
   private int width;
   private int selectedIndex = 0;
   private int amountOfEntriesShown = 5;
   private boolean noValueAllowed = true;
   private boolean enabled = true;
   private boolean hovered = false;
   private boolean listHovered = false;
   private boolean scrollBarHovered = false;
   private boolean scrollBarGrabbed = false;
   private boolean dropped = false;
   private int hoveredEntryId = -1;
   private float scrollAmount;
   private int scrollBarY;
   private int scrollBarHeight;
   private int scrollBarGrabY;
   private List<IChangeListener> changeListeners = new LinkedList();

   public List<IChangeListener> getChangeListeners() {
      return this.changeListeners;
   }

   public GuiDropDownList() {
      this.fontRenderer = Minecraft.getMinecraft().fontRenderer;
      this.entries = new ArrayList();
      this.selectedIndex = 0;
      this.width = 94;
      this.scrollAmount = 0.0F;
   }

   public GuiDropDownList init() {
      this.entries.clear();
      this.selectedIndex = 0;
      this.scrollAmount = 0.0F;
      this.enabled = true;
      return this;
   }

   public GuiDropDownList setPosition(int x, int y) {
      this.x = x;
      this.y = y;
      return this;
   }

   public GuiDropDownList addEntry(String label, T value) {
      this.entries.add(new GuiDropDownList.Entry(label, value));
      this.updateDimensions();
      return this;
   }

   public GuiDropDownList setAmountOfEntriesShown(int amount) {
      this.amountOfEntriesShown = amount;
      this.updateDimensions();
      return this;
   }

   public void update(int mouseX, int mouseY) {
      if (this.enabled) {
         this.hovered = mouseX >= this.x && mouseX <= this.x + this.width && mouseY >= this.y && mouseY <= this.y + 16;
         this.listHovered = false;
         this.hoveredEntryId = -1;
         this.scrollBarHovered = false;
         if (this.scrollBarGrabbed) {
            this.scroll((float)(mouseY - this.y - 16 - this.scrollBarY - this.scrollBarGrabY) / 11.0F);
         } else if (mouseX >= this.x && mouseX <= this.x + this.getWidth() && mouseY >= this.y + 16 && mouseY <= this.y + this.getVisualHeight()) {
            if (mouseX >= this.x + this.getWidth() - 5 - 1) {
               if (mouseY >= this.y + 16 + this.scrollBarY && mouseY <= this.y + 16 + this.scrollBarY + this.scrollBarHeight) {
                  this.scrollBarHovered = true;
               }
            } else {
               this.hoveredEntryId = this.getScrollInEntries() + (mouseY - this.y - 16) / 11;
            }

            this.listHovered = true;
         }

      }
   }

   public boolean mouseClicked(int mouseX, int mouseY, int event) {
      if (!this.isEnabled()) {
         return false;
      } else {
         this.scrollBarGrabbed = false;
         if (this.hovered) {
            this.dropped = !this.dropped;
         } else if (this.dropped) {
            if (mouseX >= this.x + this.getWidth() - 5 - 1) {
               this.scrollBarGrabbed = true;
               if (this.scrollBarHovered) {
                  this.scrollBarGrabY = mouseY - this.y - 16 - this.scrollBarY;
               } else {
                  this.scrollBarGrabY = this.scrollBarHeight / 2;
               }
            } else {
               if (this.hoveredEntryId >= 0 && this.hoveredEntryId < this.entries.size() + (this.noValueAllowed ? 1 : 0) && this.hoveredEntryId != this.selectedIndex) {
                  this.selectedIndex = this.hoveredEntryId;
                  this.notifyChanged();
               }

               this.dropped = false;
            }

            return true;
         }

         return mouseX >= this.x && mouseX <= this.x + this.width && mouseY >= this.y && mouseY <= this.y + this.getVisualHeight();
      }
   }

   public void mouseReleased(int mouseX, int mouseY, int event) {
      if (this.isEnabled()) {
         this.scrollBarGrabbed = false;
      }
   }

   public boolean handleMouseInput() {
      if (!this.isEnabled()) {
         return false;
      } else {
         int mouseWheelRoll = -Mouse.getEventDWheel();
         if (this.dropped && this.listHovered) {
            if (mouseWheelRoll != 0) {
               mouseWheelRoll = mouseWheelRoll > 0 ? 1 : -1;
               this.scroll((float)mouseWheelRoll * 0.5F);
            }

            return true;
         } else if (this.hovered) {
            if (mouseWheelRoll != 0) {
               mouseWheelRoll = mouseWheelRoll > 0 ? 1 : -1;
               this.selectedIndex = GUtil.clamp(this.selectedIndex + mouseWheelRoll, 0, this.entries.size() + (this.noValueAllowed ? 0 : -1));
               this.notifyChanged();
            }

            return true;
         } else {
            this.hoveredEntryId = -1;
            return false;
         }
      }
   }

   public boolean isEnabled() {
      return this.enabled;
   }

   public GuiDropDownList enable() {
      this.enabled = true;
      return this;
   }

   public GuiDropDownList disable() {
      this.enabled = false;
      return this;
   }

   public void display() {
      if (this.isEnabled()) {
         Draw.rectangle(this.x, this.y, this.width, 16, -6250336);
         Draw.rectangle(this.x + 1, this.y + 1, this.width - 2, 14, !this.hovered && !this.dropped ? -16777216 : -14540254);
         boolean noValue = this.noValueAllowed && this.selectedIndex == 0;
         String text = noValue ? "None" : this.fontRenderer.trimStringToWidth(this.getSelectedEntry().getLabel(), this.getWidth() - 20);
         this.fontRenderer.drawStringWithShadow(text, (float)(this.x + 5), (float)(this.y + 4), noValue ? 10066329 : 14869218);
         Draw.rectangleHorizontalGradient((float)(this.x + this.width - 40), (float)(this.y + 1), 27.0F, 14.0F, 0, !this.hovered && !this.dropped ? -16777216 : -14540254);
         Minecraft.getMinecraft().getTextureManager().bindTexture(GuiBendsMenu.ICONS_TEXTURE);
         Draw.texturedModalRect(this.x + this.width - 12, this.y + 3, 94, 24 + (!this.hovered && !this.dropped ? 0 : 10), 10, 10);
         if (this.dropped) {
            int elements = this.getNumberOfElements();
            Draw.rectangle(this.x, this.y + 16, this.width, elements * 11 + 3, -7829368);
            Draw.rectangle(this.x + 1, this.y + 16, this.width - 2, elements * 11 + 2, -16777216);

            for(int i = 0; i < elements; ++i) {
               int entryID = this.getScrollInEntries() + i - (this.noValueAllowed ? 1 : 0);
               noValue = this.noValueAllowed && i == 0;
               if (this.hoveredEntryId == this.getScrollInEntries() + i) {
                  Draw.rectangle(this.x + 1, this.y + 16 + i * 11, this.width - 2, 11, -14535873);
               } else if (this.selectedIndex == this.getScrollInEntries() + i) {
                  Draw.rectangle(this.x + 1, this.y + 16 + i * 11, this.width - 2, 11, -15395547);
               }

               String name = noValue ? "None" : this.fontRenderer.trimStringToWidth(((GuiDropDownList.Entry)this.entries.get(entryID)).getLabel(), this.getWidth());
               this.fontRenderer.drawStringWithShadow(name, (float)(this.x + 3), (float)(this.y + 16 + i * 11 + 2), noValue ? 10066329 : 14869218);
            }

            if (this.shouldShowScrollBar()) {
               Draw.rectangle(this.x + this.width - 5 - 1, this.y + 16, 5, elements * 11 + 2, -14540254);
               Draw.rectangle(this.x + this.width - 5 - 1, this.y + 16 + this.scrollBarY, 5, this.scrollBarHeight, this.scrollBarHovered ? -10066330 : -12303292);
            }
         }

      }
   }

   public int getWidth() {
      return this.width;
   }

   public int getVisualHeight() {
      return this.dropped ? 16 + this.getNumberOfElements() * 11 + 3 : 16;
   }

   public GuiDropDownList.Entry<T> getSelectedEntry() {
      if (this.selectedIndex >= 0 && this.selectedIndex < this.entries.size() + (this.noValueAllowed ? 1 : 0)) {
         return this.noValueAllowed && this.selectedIndex == 0 ? null : (GuiDropDownList.Entry)this.entries.get(this.selectedIndex - (this.noValueAllowed ? 1 : 0));
      } else {
         return null;
      }
   }

   public int getNumberOfElements() {
      int elements = this.entries.size() + (this.noValueAllowed ? 1 : 0);
      if (this.amountOfEntriesShown > 0) {
         elements = Math.min(elements, this.amountOfEntriesShown);
      }

      return elements;
   }

   public GuiDropDownList<T> allowNoValue() {
      this.noValueAllowed = true;
      return this;
   }

   public GuiDropDownList<T> forbidNoValue() {
      this.noValueAllowed = false;
      return this;
   }

   public boolean shouldShowScrollBar() {
      return this.entries.size() > this.getNumberOfElements();
   }

   public float getScrollPercentage() {
      return this.scrollAmount / (float)(this.entries.size() - this.getNumberOfElements());
   }

   public int getScrollInEntries() {
      return (int)Math.max(0.0F, this.scrollAmount);
   }

   public void scroll(float amount) {
      this.scrollAmount = GUtil.clamp(this.scrollAmount + amount, 0.0F, (float)(this.entries.size() - this.getNumberOfElements()));
      this.scrollBarY = (int)(this.getScrollPercentage() * (float)(this.getNumberOfElements() * 11 + 2 - this.scrollBarHeight));
   }

   public void selectValue(T value) {
      int previouslySelected = this.selectedIndex;

      for(int i = 0; i < this.entries.size(); ++i) {
         if (((GuiDropDownList.Entry)this.entries.get(i)).value.equals(value)) {
            this.selectedIndex = i + (this.noValueAllowed ? 1 : 0);
            if (previouslySelected != this.selectedIndex) {
               this.notifyChanged();
            }

            return;
         }
      }

      this.selectedIndex = 0;
      if (previouslySelected != this.selectedIndex) {
         this.notifyChanged();
      }

   }

   public T getSelectedValue() {
      return (T)(this.getSelectedEntry() == null ? null : this.getSelectedEntry().getValue());
   }

   public void updateDimensions() {
      this.scrollBarHeight = (int)((float)this.getNumberOfElements() / (float)this.entries.size() * (float)(this.getNumberOfElements() * 11 + 2));
      this.scrollBarY = (int)(this.getScrollPercentage() * (float)(this.getNumberOfElements() * 11 + 2 - this.scrollBarHeight));
   }

   public static class Entry<T> {
      private String label;
      private T value;

      public Entry(String label, T value) {
         this.label = label;
         this.value = value;
      }

      public String getLabel() {
         return this.label;
      }

      public T getValue() {
         return this.value;
      }
   }
}
