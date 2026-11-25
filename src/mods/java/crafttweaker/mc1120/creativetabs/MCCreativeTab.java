package crafttweaker.mc1120.creativetabs;

import crafttweaker.api.creativetabs.ICreativeTab;
import crafttweaker.api.minecraft.CraftTweakerMC;
import java.util.Objects;
import java.util.function.Predicate;
import net.minecraft.creativetab.CreativeTabs;

public class MCCreativeTab implements ICreativeTab {
   public final CreativeTabs tab;
   private final String label;

   public MCCreativeTab(CreativeTabs tab, String label) {
      this.tab = tab;
      this.label = label;
   }

   public static ICreativeTab getICreativeTab(CreativeTabs tab) {
      return tab == null ? null : (ICreativeTab)CraftTweakerMC.creativeTabs.values().stream().filter((ta) -> {
         return ta.getInternal().equals(tab);
      }).findFirst().orElse((Object)null);
   }

   public void setBackgroundImageName(String backgroundImage) {
      this.tab.setBackgroundImageName(backgroundImage);
   }

   public int getSearchBarWidth() {
      return this.tab.getSearchbarWidth();
   }

   public String getTabLabel() {
      return this.label;
   }

   public void setNoScrollBar() {
      this.tab.setNoScrollbar();
   }

   public void setNoTitle() {
      this.tab.setNoTitle();
   }

   public CreativeTabs getInternal() {
      return this.tab;
   }

   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (o != null && this.getClass() == o.getClass()) {
         MCCreativeTab that = (MCCreativeTab)o;
         return Objects.equals(this.tab, that.tab) && Objects.equals(this.label, that.label);
      } else {
         return false;
      }
   }

   public int hashCode() {
      return Objects.hash(new Object[]{this.tab, this.label});
   }
}
