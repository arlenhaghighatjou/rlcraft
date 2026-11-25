package io.github.classgraph;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class InfoList<T extends HasName> extends PotentiallyUnmodifiableList<T> {
   static final long serialVersionUID = 1L;

   InfoList() {
   }

   InfoList(int sizeHint) {
      super(sizeHint);
   }

   InfoList(Collection<T> infoCollection) {
      super(infoCollection);
   }

   public boolean equals(Object o) {
      return super.equals(o);
   }

   public int hashCode() {
      return super.hashCode();
   }

   public List<String> getNames() {
      if (this.isEmpty()) {
         return Collections.emptyList();
      } else {
         List<String> names = new ArrayList(this.size());

         for(T i : this) {
            if (i != null) {
               names.add(i.getName());
            }
         }

         return names;
      }
   }

   public List<String> getAsStrings() {
      if (this.isEmpty()) {
         return Collections.emptyList();
      } else {
         List<String> toStringVals = new ArrayList(this.size());

         for(T i : this) {
            toStringVals.add(i == null ? "null" : i.toString());
         }

         return toStringVals;
      }
   }
}
