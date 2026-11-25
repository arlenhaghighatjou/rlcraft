package com.fantasticsource.tools.datastructures;

public class SortableTable {
   public static final int INITIAL_SIZE = 16;
   public static final int COMPARE_KEY = 1;
   public static final int COMPARE_DIFFERENCE = 2;
   private SortableTable.Column[] columns;
   private int used = 0;
   private int sortedColumn = -1;
   private boolean ascending = true;

   public SortableTable(Class... columns) {
      if (columns.length == 0) {
         throw new IllegalArgumentException("Must have at least 1 column, eg. new SortableTable(Integer.class)");
      } else {
         this.columns = new SortableTable.Column[columns.length];

         for(int i = 0; i < columns.length; ++i) {
            this.columns[i] = new SortableTable.Column(columns[i]);
         }

      }
   }

   public static void test() {
      SortableTable st = new SortableTable(new Class[]{String.class, Integer.class});
      st.label(0, "Name");
      st.label(1, "Age");
      st.add("Kyle", Integer.valueOf(27));
      st.add("Charlie", Integer.valueOf(19));
      st.add("Baby", Integer.valueOf(0));
      st.add("Joe", Integer.valueOf(32));
      st.add("Grandpa", Integer.valueOf(90));
      System.out.println("Original table...");
      st.print();
      System.out.println();
      st.startSorting(1);
      System.out.println("Sorted by age (ascending)...");
      st.print();
      System.out.println();
      st.add("Gerry", Integer.valueOf(26));
      System.out.println("Added entry; table automatically puts it in right spot...");
      st.print();
      System.out.println();
      st.startSorting(0, false);
      System.out.println("Sorted by name (descending)...");
      st.print();
      System.out.println();
      st.set(st.indexOf("Grandpa", 0), "Artemis", Integer.valueOf(90));
      System.out.println("Changed 'Grandpa' to 'Artemis'; table automatically resorts just that one entry...");
      st.print();
   }

   public int size() {
      return this.used;
   }

   public void add(Object... item) {
      if (item.length != this.columns.length) {
         throw new IllegalArgumentException("Item length (" + item.length + ") must match number of columns (" + this.columns.length + ")");
      } else {
         for(int i = 0; i < item.length; ++i) {
            if (!this.columns[i].c.isAssignableFrom(item[i].getClass())) {
               throw new IllegalArgumentException("All item objects' classes must match column classes. Column class match error on column " + i);
            }
         }

         if (this.used == this.columns[0].values.length) {
            this.expand();
         }

         for(int i = 0; i < this.columns.length; ++i) {
            this.columns[i].values[this.used] = item[i];
         }

         if (this.sortedColumn != -1) {
            if (this.ascending) {
               for(int i = this.used; i > 0 && this.columns[this.sortedColumn].greater(i - 1, i); --i) {
                  this.swap(i - 1, i);
               }
            } else {
               for(int i = this.used; i > 0 && this.columns[this.sortedColumn].greater(i, i - 1); --i) {
                  this.swap(i - 1, i);
               }
            }
         }

         ++this.used;
      }
   }

   public void set(int index, Object... item) {
      if (index >= this.used) {
         throw new ArrayIndexOutOfBoundsException("Items: " + this.used + ", index given: " + index);
      } else if (item.length != this.columns.length) {
         throw new IllegalArgumentException("Item length (" + item.length + ") must match number of columns (" + this.columns.length + ")");
      } else {
         for(int i = 0; i < item.length; ++i) {
            if (!this.columns[i].c.isAssignableFrom(item[i].getClass())) {
               throw new IllegalArgumentException("All item objects' classes must match column classes. Column class match error on column " + i);
            }
         }

         for(int i = 0; i < this.columns.length; ++i) {
            this.columns[i].values[index] = item[i];
         }

         if (this.sortedColumn != -1) {
            if (this.ascending) {
               int i;
               for(i = index; i > 0 && this.columns[this.sortedColumn].greater(i - 1, i); --i) {
                  this.swap(i - 1, i);
               }

               while(i < this.used - 1 && this.columns[this.sortedColumn].greater(i, i + 1)) {
                  this.swap(i, i + 1);
                  ++i;
               }
            } else {
               int i;
               for(i = index; i > 0 && this.columns[this.sortedColumn].greater(i, i - 1); --i) {
                  this.swap(i - 1, i);
               }

               while(i < this.used - 1 && this.columns[this.sortedColumn].greater(i + 1, i)) {
                  this.swap(i, i + 1);
                  ++i;
               }
            }
         }

      }
   }

   public Object get(int keyColumn, Object key, int valueColumn) {
      if (keyColumn >= 0 && keyColumn <= this.columns.length) {
         if (valueColumn >= 0 && valueColumn <= this.columns.length) {
            int index = this.indexOf(key, keyColumn);
            return index >= 0 && index <= this.used ? this.columns[valueColumn].values[index] : null;
         } else {
            throw new ArrayIndexOutOfBoundsException();
         }
      } else {
         throw new ArrayIndexOutOfBoundsException();
      }
   }

   public Object get(int column, int index) {
      if (index >= 0 && index <= this.used) {
         if (column >= 0 && column <= this.columns.length) {
            return this.columns[column].values[index];
         } else {
            throw new ArrayIndexOutOfBoundsException();
         }
      } else {
         throw new ArrayIndexOutOfBoundsException();
      }
   }

   public Object[] getColumn(int column) {
      if (column >= 0 && column <= this.columns.length) {
         Object[] result = new Object[this.used];
         System.arraycopy(this.columns[column].values, 0, result, 0, this.used);
         return result;
      } else {
         throw new ArrayIndexOutOfBoundsException();
      }
   }

   public Object[] getRow(int index) {
      if (index >= 0 && index <= this.used) {
         Object[] result = new Object[this.columns.length];

         for(int i = 0; i < this.columns.length; ++i) {
            result[i] = this.columns[i].values[index];
         }

         return result;
      } else {
         throw new ArrayIndexOutOfBoundsException();
      }
   }

   public void delete(int index) {
      if (index >= this.used) {
         throw new ArrayIndexOutOfBoundsException("Item count: " + this.used + ", index given: " + index);
      } else {
         --this.used;

         for(int i = index; i < this.used; ++i) {
            for(SortableTable.Column column : this.columns) {
               column.values[i] = column.values[i + 1];
            }
         }

      }
   }

   public boolean delete(Object o, int column) {
      if (column >= this.columns.length) {
         throw new ArrayIndexOutOfBoundsException("Column count: " + this.columns.length + ", index given: " + column);
      } else {
         int i = this.columns[column].indexOf(o);
         if (i == -1) {
            return false;
         } else {
            this.delete(i);
            return true;
         }
      }
   }

   public void clear() {
      this.used = 0;
   }

   public void startSorting(int column) {
      this.startSorting(column, this.ascending);
   }

   public void startSorting(int column, boolean ascending) {
      if (column >= this.columns.length) {
         throw new ArrayIndexOutOfBoundsException("Column count: " + this.columns.length + ", index given: " + column);
      } else {
         this.ascending = ascending;
         this.sortedColumn = column;
         if (ascending) {
            for(int i2 = this.used - 1; i2 > 0; --i2) {
               for(int i = 0; i < i2; ++i) {
                  if (this.columns[column].greater(i, i + 1)) {
                     this.swap(i, i + 1);
                  }
               }
            }
         } else {
            for(int i2 = this.used - 1; i2 > 0; --i2) {
               for(int i = 0; i < i2; ++i) {
                  if (this.columns[column].greater(i + 1, i)) {
                     this.swap(i, i + 1);
                  }
               }
            }
         }

      }
   }

   public void swap(int index1, int index2) {
      for(SortableTable.Column column : this.columns) {
         Object o = column.values[index1];
         column.values[index1] = column.values[index2];
         column.values[index2] = o;
      }

   }

   public void stopSorting() {
      this.sortedColumn = -1;
   }

   public int indexOf(Object value, int column) {
      return this.columns[column].indexOf(value);
   }

   public boolean contains(Object value, int column) {
      return this.columns[column].contains(value);
   }

   private void expand() {
      int newSize = this.columns[0].values.length * 2;
      Object[] objects = new Object[newSize];

      for(SortableTable.Column column : this.columns) {
         System.arraycopy(column.values, 0, objects, 0, this.used);
         column.values = objects.clone();
      }

   }

   public void label(int column, String label) {
      if (column >= this.columns.length) {
         throw new ArrayIndexOutOfBoundsException("Columns: " + this.columns.length + ", index given: " + column);
      } else {
         this.columns[column].label = label;
      }
   }

   public void labels(String... labels) {
      int i = 0;

      for(String label : labels) {
         this.label(i++, label);
      }

   }

   public void unit(int column, String unit) {
      if (column >= this.columns.length) {
         throw new ArrayIndexOutOfBoundsException("Columns: " + this.columns.length + ", index given: " + column);
      } else {
         this.columns[column].unit = unit;
      }
   }

   public void units(String... units) {
      int i = 0;

      for(String unit : units) {
         this.unit(i++, unit);
      }

   }

   public String toString() {
      String[] strings = new String[this.columns.length * (1 + this.used)];
      int[] maxes = new int[this.columns.length];

      for(int i = 0; i < this.columns.length; ++i) {
         strings[i] = this.columns[i].label;
         maxes[i] = strings[i].length();
      }

      for(int i2 = 0; i2 < this.used; ++i2) {
         for(int i = 0; i < this.columns.length; ++i) {
            strings[(1 + i2) * this.columns.length + i] = this.columns[i].toString(i2);
            if (strings[(1 + i2) * this.columns.length + i].length() > maxes[i]) {
               maxes[i] = strings[(1 + i2) * this.columns.length + i].length();
            }
         }
      }

      StringBuilder result = new StringBuilder();

      for(int i = 0; i < this.columns.length; ++i) {
         result.append(strings[i]);

         for(int i3 = strings[i].length(); i3 < maxes[i] + 1; ++i3) {
            result.append(" ");
         }
      }

      result.append("\r\n");

      for(int i2 = 0; i2 < this.used; ++i2) {
         for(int i = 0; i < this.columns.length; ++i) {
            result.append(strings[(1 + i2) * this.columns.length + i]);

            for(int i3 = strings[(1 + i2) * this.columns.length + i].length(); i3 < maxes[i] + 1; ++i3) {
               result.append(" ");
            }
         }

         result.append("\r\n");
      }

      return result.toString();
   }

   public void print() {
      System.out.print(this.toString());
   }

   private class Column {
      private static final byte BYTE = 0;
      private static final byte SHORT = 1;
      private static final byte INT = 2;
      private static final byte LONG = 3;
      private static final byte FLOAT = 4;
      private static final byte DOUBLE = 5;
      private static final byte BOOLEAN = 6;
      private static final byte CHAR = 7;
      private static final byte STRING = 8;
      private static final byte CLASS = 9;
      byte comparison;
      String label;
      String unit;
      Object[] values;
      Class c;

      Column(Class c) {
         this("", c);
      }

      Column(String label, Class c) {
         this.unit = "";
         this.values = new Object[16];
         this.label = label;
         this.c = c;
         if (Integer.class.isAssignableFrom(c)) {
            this.comparison = 2;
         } else if (Boolean.class.isAssignableFrom(c)) {
            this.comparison = 6;
         } else if (String.class.isAssignableFrom(c)) {
            this.comparison = 8;
         } else if (Float.class.isAssignableFrom(c)) {
            this.comparison = 4;
         } else if (Character.class.isAssignableFrom(c)) {
            this.comparison = 7;
         } else if (Double.class.isAssignableFrom(c)) {
            this.comparison = 5;
         } else if (Short.class.isAssignableFrom(c)) {
            this.comparison = 1;
         } else if (Long.class.isAssignableFrom(c)) {
            this.comparison = 3;
         } else if (!Byte.class.isAssignableFrom(c)) {
            this.comparison = 9;
         }

      }

      boolean greater(int index1, int index2) {
         switch(this.comparison) {
         case 0:
            return ((Byte)this.values[index1]).byteValue() > ((Byte)this.values[index2]).byteValue();
         case 1:
            return ((Short)this.values[index1]).shortValue() > ((Short)this.values[index2]).shortValue();
         case 2:
            return ((Integer)this.values[index1]).intValue() > ((Integer)this.values[index2]).intValue();
         case 3:
            return ((Long)this.values[index1]).longValue() > ((Long)this.values[index2]).longValue();
         case 4:
            return ((Float)this.values[index1]).floatValue() > ((Float)this.values[index2]).floatValue();
         case 5:
            return ((Double)this.values[index1]).doubleValue() > ((Double)this.values[index2]).doubleValue();
         case 6:
            return ((Boolean)this.values[index1]).booleanValue() && !((Boolean)this.values[index2]).booleanValue();
         case 7:
            return ((Character)this.values[index1]).charValue() > ((Character)this.values[index2]).charValue();
         case 8:
            String str1 = (String)this.values[index1];
            String str2 = (String)this.values[index2];
            int i = 0;

            for(int min = Math.min(str1.length(), str2.length()); i < min; ++i) {
               char c1;
               if (str1.length() > i) {
                  c1 = str1.charAt(i);
               } else {
                  c1 = 0;
               }

               char c2;
               if (str2.length() > i) {
                  c2 = str2.charAt(i);
               } else {
                  c2 = 0;
               }

               if (c1 > c2) {
                  return true;
               }

               if (c1 < c2) {
                  return false;
               }
            }

            return str1.length() > str2.length();
         default:
            String str1 = this.values[index1].getClass().getSimpleName();
            String str2 = this.values[index2].getClass().getSimpleName();
            int i = 0;

            for(int min = Math.min(str1.length(), str2.length()); i < min; ++i) {
               char c1;
               if (str1.length() > i) {
                  c1 = str1.charAt(i);
               } else {
                  c1 = 0;
               }

               char c2;
               if (str2.length() > i) {
                  c2 = str2.charAt(i);
               } else {
                  c2 = 0;
               }

               if (c1 > c2) {
                  return true;
               }

               if (c1 < c2) {
                  return false;
               }
            }

            return str1.length() > str2.length();
         }
      }

      int indexOf(Object value) {
         for(int i = 0; i < SortableTable.this.used; ++i) {
            if (value.equals(this.values[i])) {
               return i;
            }
         }

         return -1;
      }

      boolean contains(Object value) {
         return this.indexOf(value) != -1;
      }

      String toString(int index) {
         return this.comparison != 9 ? this.values[index] + this.unit : this.values[index].getClass().getSimpleName() + this.unit;
      }
   }
}
