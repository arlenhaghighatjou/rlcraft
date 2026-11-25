package ivorius.ivtoolkit.models;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

public class Attributes implements Iterable<Attribute>, Comparator<Attribute> {
   protected long mask;
   protected final List<Attribute> attributes = new ArrayList();
   protected boolean sorted = true;

   public Attributes() {
   }

   public final void sort() {
      if (!this.sorted) {
         Collections.sort(this.attributes, this);
         this.sorted = true;
      }

   }

   public final long getMask() {
      return this.mask;
   }

   public final Attribute get(long type) {
      if (this.has(type)) {
         for(Attribute attribute : this.attributes) {
            if (attribute.type == type) {
               return attribute;
            }
         }
      }

      return null;
   }

   public final <T extends Attribute> T get(Class<T> clazz, long type) {
      return (T)this.get(type);
   }

   public final List<Attribute> get(List<Attribute> out, long type) {
      for(Attribute attribute : this.attributes) {
         if ((attribute.type & type) != 0L) {
            out.add(attribute);
         }
      }

      return out;
   }

   public void clear() {
      this.mask = 0L;
      this.attributes.clear();
   }

   public int size() {
      return this.attributes.size();
   }

   private final void enable(long mask) {
      this.mask |= mask;
   }

   private final void disable(long mask) {
      this.mask &= ~mask;
   }

   public final void set(Attribute attribute) {
      int idx = this.indexOf(attribute.type);
      if (idx < 0) {
         this.enable(attribute.type);
         this.attributes.add(attribute);
         this.sorted = false;
      } else {
         this.attributes.set(idx, attribute);
      }

   }

   public final void set(Attribute attribute1, Attribute attribute2) {
      this.set(attribute1);
      this.set(attribute2);
   }

   public final void set(Attribute attribute1, Attribute attribute2, Attribute attribute3) {
      this.set(attribute1);
      this.set(attribute2);
      this.set(attribute3);
   }

   public final void set(Attribute attribute1, Attribute attribute2, Attribute attribute3, Attribute attribute4) {
      this.set(attribute1);
      this.set(attribute2);
      this.set(attribute3);
      this.set(attribute4);
   }

   public final void set(Attribute... attributes) {
      for(Attribute attr : attributes) {
         this.set(attr);
      }

   }

   public final void set(Iterable<Attribute> attributes) {
      for(Attribute attr : attributes) {
         this.set(attr);
      }

   }

   public final void remove(long mask) {
      for(int i = 0; i < this.attributes.size(); ++i) {
         long type = ((Attribute)this.attributes.get(i)).type;
         if ((mask & type) == type) {
            this.attributes.remove(i);
            this.disable(type);
            this.sorted = false;
         }
      }

   }

   public final boolean has(long type) {
      return type != 0L && (this.mask & type) == type;
   }

   protected int indexOf(long type) {
      if (this.has(type)) {
         for(int i = 0; i < this.attributes.size(); ++i) {
            if (((Attribute)this.attributes.get(i)).type == type) {
               return i;
            }
         }
      }

      return -1;
   }

   public final boolean same(Attributes other, boolean compareValues) {
      if (other == this) {
         return true;
      } else if (other != null && this.mask == other.mask) {
         if (!compareValues) {
            return true;
         } else {
            this.sort();
            other.sort();

            for(int i = 0; i < this.attributes.size(); ++i) {
               if (!((Attribute)this.attributes.get(i)).equals((Attribute)other.attributes.get(i))) {
                  return false;
               }
            }

            return true;
         }
      } else {
         return false;
      }
   }

   public final boolean same(Attributes other) {
      return this.same(other, false);
   }

   public final int compare(Attribute arg0, Attribute arg1) {
      return (int)(arg0.type - arg1.type);
   }

   public final Iterator<Attribute> iterator() {
      return this.attributes.iterator();
   }

   public int hashCode() {
      this.sort();
      int n = this.attributes.size();
      int result = 71 + (int)this.mask;
      int m = 1;

      for(Attribute attribute : this.attributes) {
         result = (int)((long)result + this.mask * (long)attribute.hashCode() * (long)(m = m * 7 & '\uffff'));
      }

      return result;
   }

   public boolean equals(Object other) {
      if (other == null) {
         return false;
      } else if (other == this) {
         return true;
      } else if (!(other instanceof Attributes)) {
         return false;
      } else {
         return this.hashCode() == other.hashCode();
      }
   }
}
