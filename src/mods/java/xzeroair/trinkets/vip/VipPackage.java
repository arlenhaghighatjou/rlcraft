package xzeroair.trinkets.vip;

import java.util.ArrayList;
import java.util.List;

public class VipPackage {
   private String group;
   private int id;
   private List<String> groupQuotes;

   public VipPackage(String group, int ID) {
      this.group = group;
      this.id = ID;
      this.groupQuotes = new ArrayList();
   }

   public String getGroup() {
      return this.group;
   }

   public int getGroupID() {
      return this.id;
   }

   public List<String> getGroupQuotes() {
      return this.groupQuotes;
   }

   public void addQuote(String... quote) {
      for(String s : quote) {
         this.groupQuotes.add(s);
      }

   }

   public VipPackage setGroupQuotes(List<String> groupQuotes) {
      this.groupQuotes = groupQuotes;
      return this;
   }
}
