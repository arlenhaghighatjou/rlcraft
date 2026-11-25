package xzeroair.trinkets.vip;

import java.util.ArrayList;
import java.util.List;

public class VipUser {
   private String ID;
   private String Username;
   private List<String> Quotes;
   private List<VipPackage> Groups;

   public VipUser(String uuid, String username) {
      this.ID = uuid;
      this.Username = username;
      this.Quotes = new ArrayList();
      this.Groups = new ArrayList();
   }

   public String getID() {
      return this.ID;
   }

   public String getUsername() {
      return this.Username;
   }

   public List<String> getQuotes() {
      return this.Quotes;
   }

   public List<VipPackage> getGroups() {
      return this.Groups;
   }

   public VipUser setQuotes(List<String> quotes) {
      this.Quotes = quotes;
      return this;
   }

   public VipUser setGroups(List<VipPackage> groups) {
      this.Groups = groups;
      return this;
   }
}
