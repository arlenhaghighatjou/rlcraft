package slimeknights.mantle.configurate.transformation;

import java.util.Comparator;

class NodePathComparator implements Comparator<Object[]> {
   NodePathComparator() {
   }

   public int compare(Object[] a, Object[] b) {
      for(int i = 0; i < Math.min(a.length, b.length); ++i) {
         if (a[i] != ConfigurationTransformation.WILDCARD_OBJECT && b[i] != ConfigurationTransformation.WILDCARD_OBJECT) {
            if (!(a[i] instanceof Comparable)) {
               return a[i].equals(b[i]) ? 0 : a[i].hashCode().compareTo(Integer.valueOf(b[i].hashCode()));
            }

            int comp = ((Comparable)a[i]).compareTo(b[i]);
            switch(comp) {
            case 0:
               break;
            default:
               return comp;
            }
         } else if (a[i] != ConfigurationTransformation.WILDCARD_OBJECT || b[i] != ConfigurationTransformation.WILDCARD_OBJECT) {
            return a[i] == ConfigurationTransformation.WILDCARD_OBJECT ? 1 : -1;
         }
      }

      if (a.length > b.length) {
         return -1;
      } else if (b.length > a.length) {
         return 1;
      } else {
         return 0;
      }
   }
}
