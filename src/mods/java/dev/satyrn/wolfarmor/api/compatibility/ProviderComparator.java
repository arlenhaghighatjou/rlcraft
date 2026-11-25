package dev.satyrn.wolfarmor.api.compatibility;

import java.util.Comparator;

public class ProviderComparator implements Comparator<Provider.ProviderType> {
   public ProviderComparator() {
   }

   public int compare(Provider.ProviderType o1, Provider.ProviderType o2) {
      short o1Priority = o1.getPriority();
      short o2Priority = o2.getPriority();
      return Short.compare(o1Priority, o2Priority);
   }
}
