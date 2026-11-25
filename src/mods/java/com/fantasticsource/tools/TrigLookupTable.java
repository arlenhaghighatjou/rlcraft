package com.fantasticsource.tools;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.function.Function;

public class TrigLookupTable {
   protected static final LinkedHashMap<Integer, TrigLookupTable> TABLES = new LinkedHashMap();
   public static final TrigLookupTable TRIG_TABLE_1024 = new TrigLookupTable(1024);
   private double[] table;
   private double[] invtable;

   public TrigLookupTable(int granularity) {
      this.table = new double[granularity];
      this.invtable = new double[granularity];
      double step = 6.283185307179586D / (double)granularity;
      double invstep = 2.0D / (double)granularity;

      for(int i = 0; i < granularity; ++i) {
         double theta = step * (double)i;
         if (theta == 3.141592653589793D) {
            this.table[i] = 0.0D;
         } else {
            this.table[i] = Math.sin(theta);
         }

         double val = invstep * (double)i - 1.0D;
         this.invtable[i] = Math.asin(val);
      }

   }

   public static TrigLookupTable getInstance(int granularity) {
      return (TrigLookupTable)TABLES.computeIfAbsent(Integer.valueOf(granularity), (o) -> {
         return new TrigLookupTable(granularity);
      });
   }

   public static void test() {
      TrigLookupTable t = new TrigLookupTable(1024);
      double[] doubles = new double[1000000];
      System.out.println("All tests are 1000000 runs of each method\r\n");
      System.out.println("The TrigLookupTable has a granularity of 1024\r\n");
      Date d1 = new Date();

      for(int i = 0; i < 1000000; ++i) {
         doubles[i] = t.sin(Math.random() * 2000000.0D - 1000000.0D);
      }

      Date d2 = new Date();

      for(int var21 = 0; var21 < 1000000; ++var21) {
         doubles[var21] = Math.sin(Math.random() * 2000000.0D - 1000000.0D);
      }

      Date d3 = new Date();
      System.out.println("TrigLookupTable.sin = " + (d2.getTime() - d1.getTime()));
      System.out.println("Math.sin = " + (d3.getTime() - d2.getTime()) + "\r\n");
      d1 = new Date();

      for(int var22 = 0; var22 < 1000000; ++var22) {
         doubles[var22] = t.cos(Math.random() * 2000000.0D - 1000000.0D);
      }

      d2 = new Date();

      for(int var23 = 0; var23 < 1000000; ++var23) {
         doubles[var23] = Math.cos(Math.random() * 2000000.0D - 1000000.0D);
      }

      d3 = new Date();
      System.out.println("TrigLookupTable.cos = " + (d2.getTime() - d1.getTime()));
      System.out.println("Math.cos = " + (d3.getTime() - d2.getTime()) + "\r\n");
      d1 = new Date();

      for(int var24 = 0; var24 < 1000000; ++var24) {
         doubles[var24] = t.tan(Math.random() * 2000000.0D - 1000000.0D);
      }

      d2 = new Date();

      for(int var25 = 0; var25 < 1000000; ++var25) {
         doubles[var25] = Math.tan(Math.random() * 2000000.0D - 1000000.0D);
      }

      d3 = new Date();
      System.out.println("TrigLookupTable.tan = " + (d2.getTime() - d1.getTime()));
      System.out.println("Math.tan = " + (d3.getTime() - d2.getTime()) + "\r\n");
      d1 = new Date();

      for(int var26 = 0; var26 < 1000000; ++var26) {
         doubles[var26] = t.arcsin(Math.random() * 2.0D - 1.0D);
      }

      d2 = new Date();

      for(int var27 = 0; var27 < 1000000; ++var27) {
         doubles[var27] = Math.asin(Math.random() * 2.0D - 1.0D);
      }

      d3 = new Date();
      System.out.println("TrigLookupTable.arcsin = " + (d2.getTime() - d1.getTime()));
      System.out.println("Math.asin = " + (d3.getTime() - d2.getTime()) + "\r\n");
      d1 = new Date();

      for(int var28 = 0; var28 < 1000000; ++var28) {
         doubles[var28] = t.arccos(Math.random() * 2.0D - 1.0D);
      }

      d2 = new Date();

      for(int var29 = 0; var29 < 1000000; ++var29) {
         doubles[var29] = Math.acos(Math.random() * 2.0D - 1.0D);
      }

      d3 = new Date();
      System.out.println("TrigLookupTable.arccos = " + (d2.getTime() - d1.getTime()));
      System.out.println("Math.acos = " + (d3.getTime() - d2.getTime()) + "\r\n");
      d1 = new Date();

      for(int var30 = 0; var30 < 1000000; ++var30) {
         doubles[var30] = t.arctan(Math.random() * 2000000.0D - 1000000.0D);
      }

      d2 = new Date();

      for(int var31 = 0; var31 < 1000000; ++var31) {
         doubles[var31] = Math.atan(Math.random() * 2000000.0D - 1000000.0D);
      }

      d3 = new Date();
      System.out.println("TrigLookupTable.arctan = " + (d2.getTime() - d1.getTime()));
      System.out.println("Math.atan = " + (d3.getTime() - d2.getTime()) + "\r\n");
   }

   public double sin(double theta) {
      theta = Tools.posMod(theta, 6.283185307179586D);
      int i = (int)Math.round((double)this.table.length * theta / 6.283185307179586D);
      if (i == this.table.length) {
         i = 0;
      }

      return this.table[i];
   }

   public double cos(double theta) {
      return this.sin(theta + 1.5707963267948966D);
   }

   public double tan(double theta) {
      return this.sin(theta) / this.cos(theta);
   }

   public double arcsin(double input) {
      if (input >= -1.0D && input <= 1.0D) {
         int i = (int)Math.round((double)this.invtable.length * (input + 1.0D) * 0.5D);
         return i == this.invtable.length ? 1.5707963267948966D : this.invtable[i];
      } else {
         throw new IllegalArgumentException("arcsin() and arccos() can only take in from -1 to 1.  Input was " + input);
      }
   }

   public double arccos(double input) {
      return 1.5707963267948966D - this.arcsin(input);
   }

   public double arctan(double input) {
      if (input == 0.0D) {
         return 0.0D;
      } else if (input == Double.POSITIVE_INFINITY) {
         return 1.5707963267948966D;
      } else if (input == Double.NEGATIVE_INFINITY) {
         return -1.5707963267948966D;
      } else if (input > 0.0D) {
         if (input < 1.0D) {
            return this.arcsin(input / Math.sqrt(1.0D + input * input));
         } else {
            double x = 1.0D / input;
            return this.arccos(x / Math.sqrt(1.0D + x * x));
         }
      } else if (input > -1.0D) {
         return this.arcsin(input / Math.sqrt(1.0D + input * input));
      } else {
         double x = 1.0D / input;
         return this.arccos(x / Math.sqrt(1.0D + x * x)) - 3.141592653589793D;
      }
   }

   public double arctanFullcircle(double x1, double y1, double x2, double y2) {
      double x = x2 - x1;
      double y = y1 - y2;
      if (x > 0.0D) {
         if (y > 0.0D) {
            return x > y ? this.arcsin(y / Math.sqrt(x * x + y * y)) : this.arccos(x / Math.sqrt(x * x + y * y));
         } else if (y < 0.0D) {
            return x > -y ? 6.283185307179586D + this.arcsin(y / Math.sqrt(x * x + y * y)) : 6.283185307179586D - this.arccos(x / Math.sqrt(x * x + y * y));
         } else {
            return 0.0D;
         }
      } else if (x < 0.0D) {
         if (y > 0.0D) {
            return -x > y ? 3.141592653589793D - this.arcsin(y / Math.sqrt(x * x + y * y)) : this.arccos(x / Math.sqrt(x * x + y * y));
         } else if (y < 0.0D) {
            return x < y ? 3.141592653589793D - this.arcsin(y / Math.sqrt(x * x + y * y)) : 6.283185307179586D - this.arccos(x / Math.sqrt(x * x + y * y));
         } else {
            return 3.141592653589793D;
         }
      } else if (y > 0.0D) {
         return 1.5707963267948966D;
      } else {
         return y < 0.0D ? 4.71238898038469D : 0.0D;
      }
   }

   public String toString() {
      StringBuilder str = new StringBuilder("" + this.table[0]);

      for(int i = 1; i < this.table.length; ++i) {
         str.append("\r\n").append(this.table[i]);
      }

      return str.toString();
   }

   public double[] getArray() {
      return this.table;
   }

   public double[] getInvArray() {
      return this.invtable;
   }

   public int getGranularity() {
      return this.table.length;
   }

   static {
      TABLES.put(Integer.valueOf(1024), TRIG_TABLE_1024);
   }
}
