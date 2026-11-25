package betterquesting.api2.client.gui.controls.filters;

import betterquesting.api2.client.gui.controls.IFieldFilter;

public abstract class FieldFilterNumber<T extends Number> implements IFieldFilter<T> {
   public static final FieldFilterNumber<Double> DOUBLE = new FieldFilterNumber<Double>(true) {
      public Double parseValue(String input) {
         try {
            return Double.parseDouble(input);
         } catch (Exception var3) {
            return 0.0D;
         }
      }
   };
   public static final FieldFilterNumber<Float> FLOAT = new FieldFilterNumber<Float>(true) {
      public Float parseValue(String input) {
         try {
            return Float.parseFloat(input);
         } catch (Exception var3) {
            return 0.0F;
         }
      }
   };
   public static final FieldFilterNumber<Long> LONG = new FieldFilterNumber<Long>(false) {
      public Long parseValue(String input) {
         try {
            return Long.parseLong(input);
         } catch (Exception var3) {
            return 0L;
         }
      }
   };
   public static final FieldFilterNumber<Integer> INT = new FieldFilterNumber<Integer>(false) {
      public Integer parseValue(String input) {
         try {
            return Integer.parseInt(input);
         } catch (Exception var3) {
            return Integer.valueOf(0);
         }
      }
   };
   public static final FieldFilterNumber<Short> SHORT = new FieldFilterNumber<Short>(false) {
      public Short parseValue(String input) {
         try {
            return Short.parseShort(input);
         } catch (Exception var3) {
            return 0;
         }
      }
   };
   public static final FieldFilterNumber<Byte> BYTE = new FieldFilterNumber<Byte>(false) {
      public Byte parseValue(String input) {
         try {
            return Byte.parseByte(input);
         } catch (Exception var3) {
            return 0;
         }
      }
   };
   private final boolean floating;

   public FieldFilterNumber(boolean floating) {
      this.floating = floating;
   }

   public boolean isValid(String input) {
      return this.floating ? !input.matches("[^.0123456789-]") : !input.matches("[^0123456789-]");
   }

   public String filterText(String input) {
      return this.floating ? input.replaceAll("[^.0123456789-]", "") : input.replaceAll("[^0123456789-]", "");
   }
}
