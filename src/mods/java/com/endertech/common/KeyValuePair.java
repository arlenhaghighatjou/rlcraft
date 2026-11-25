package com.endertech.common;

public final class KeyValuePair {
   public static final KeyValuePair EMPTY = from("");
   public static final String SEPARATOR = "=";
   public final String key;
   public final String value;

   public KeyValuePair(String key, String value) {
      this.key = key.trim();
      this.value = value.trim();
   }

   public static KeyValuePair from(String asString) {
      int sepStart = asString.indexOf("=");
      if (sepStart == -1) {
         return new KeyValuePair(asString, "");
      } else {
         int sepEnd = sepStart + "=".length();
         String aKey = asString.substring(0, sepStart);
         String aValue = sepEnd >= asString.length() ? "" : asString.substring(sepEnd);
         return new KeyValuePair(aKey, aValue);
      }
   }

   public boolean isEmpty() {
      return this.key.isEmpty() && this.value.isEmpty();
   }

   public boolean equals(Object obj) {
      if (!(obj instanceof KeyValuePair)) {
         return false;
      } else {
         KeyValuePair other = (KeyValuePair)obj;
         return this.key.equals(other.key) && this.value.equals(other.value);
      }
   }

   public int hashCode() {
      return this.key.hashCode() ^ this.value.hashCode();
   }

   public String toString() {
      return this.isEmpty() ? "" : this.key + "=" + this.value;
   }
}
