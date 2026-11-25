package com.fantasticsource.tools;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;

public class FieldFinder {
   private Class clss;
   private Class fieldClass;
   private ArrayList<Field> possibleFields;
   private StringBuilder log = new StringBuilder();

   private FieldFinder() {
   }

   public static FieldFinder getFinder(Class clss, Class fieldClass) throws NoSuchFieldException, IllegalAccessException {
      if (clss != null && fieldClass != null) {
         FieldFinder fieldFinder = new FieldFinder();
         fieldFinder.clss = clss;
         fieldFinder.fieldClass = fieldClass;
         fieldFinder.possibleFields = new ArrayList(Arrays.asList(clss.getDeclaredFields()));

         for(Field field : fieldFinder.possibleFields) {
            field.setAccessible(true);
            Field modifiersField = Field.class.getDeclaredField("modifiers");
            modifiersField.setAccessible(true);
            modifiersField.setInt(field, field.getModifiers() & -17);
         }

         fieldFinder.log.append("Created FieldFinder for ").append(fieldClass.getSimpleName()).append("s within a ").append(clss.getSimpleName()).append("\r\n");
         fieldFinder.log.append("Initial number of possible fields is ").append(fieldFinder.possibleFields.size()).append("\r\n\r\n");
         return fieldFinder;
      } else {
         return null;
      }
   }

   public FieldFinder removeIfNot(Object objectContainingFields, Object goalValue) throws IllegalAccessException {
      if (objectContainingFields.getClass() == this.clss && goalValue.getClass() == this.fieldClass) {
         for(Field field : (ArrayList)this.possibleFields.clone()) {
            Object observedValue = field.get(objectContainingFields);
            if (!observedValue.equals(goalValue)) {
               this.log.append("Removing field ").append(field.getName()).append("; Goal was <").append(goalValue).append(">, but the field's value was <").append(observedValue).append(">\r\n");
               this.possibleFields.remove(field);
            }
         }

         this.log.append("There are now ").append(this.possibleFields.size()).append(" possible fields remaining\r\n\r\n");
      }

      return this;
   }

   public int remainingPossibilities() {
      return this.possibleFields.size();
   }

   public String toString() {
      return this.toString((Object)null);
   }

   public String toString(Object objectContainingFields) {
      if (this.possibleFields.size() == 0) {
         return null;
      } else {
         StringBuilder str = new StringBuilder();
         if (objectContainingFields == null) {
            for(Field field : this.possibleFields) {
               str.append(field).append("\r\n");
            }
         } else {
            if (objectContainingFields.getClass() != this.clss) {
               return objectContainingFields + " is not an instance of " + this.clss.getName();
            }

            try {
               for(Field field : this.possibleFields) {
                  str.append(field).append(" = ").append(field.get(objectContainingFields)).append("\r\n");
               }
            } catch (IllegalAccessException var5) {
               var5.printStackTrace();
            }
         }

         return str.toString();
      }
   }

   public String getLog() {
      return this.getLog((Object)null);
   }

   public String getLog(Object objectContainingFields) {
      return this.log.toString() + "Remaining entries are as follows:\r\n\r\n" + this.toString(objectContainingFields) + "\r\n";
   }
}
