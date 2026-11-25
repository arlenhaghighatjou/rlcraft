package goblinbob.mobends.core.client.gui.elements;

public class GuiIndexedDropDownList extends GuiDropDownList<Integer> {
   public GuiIndexedDropDownList() {
   }

   public GuiDropDownList addEntry(String label) {
      int index = this.entries.size();
      return this.addEntry(label, Integer.valueOf(index));
   }
}
