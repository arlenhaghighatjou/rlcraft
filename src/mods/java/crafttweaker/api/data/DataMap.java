package crafttweaker.api.data;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class DataMap implements IData {
   public static final DataMap EMPTY = new DataMap(new HashMap(), true);
   private final Map<String, IData> data;
   private final boolean immutable;

   public DataMap(Map<String, IData> data, boolean immutable) {
      this.data = data;
      this.immutable = immutable;
   }

   public IData add(IData other) {
      Map<String, IData> result = new HashMap();
      Map<String, IData> otherMap = other.asMap();

      for(Entry<String, IData> entry : this.data.entrySet()) {
         result.put(entry.getKey(), entry.getValue());
      }

      for(Entry<String, IData> entry : otherMap.entrySet()) {
         if (result.containsKey(entry.getKey()) && entry.getValue() instanceof DataMap && result.get(entry.getKey()) instanceof DataMap) {
            result.put(entry.getKey(), ((IData)result.get(entry.getKey())).add((IData)entry.getValue()));
         } else if (result.containsKey(entry.getKey()) && entry.getValue() instanceof DataList && result.get(entry.getKey()) instanceof DataList) {
            result.put(entry.getKey(), ((IData)result.get(entry.getKey())).add((IData)entry.getValue()));
         } else {
            result.put(entry.getKey(), entry.getValue());
         }
      }

      return new DataMap(result, this.immutable);
   }

   public IData sub(IData other) {
      Map<String, IData> result = new HashMap();
      Map<String, IData> otherMap = other.asMap();

      for(Entry<String, IData> entry : this.data.entrySet()) {
         result.put(entry.getKey(), entry.getValue());
      }

      for(Entry<String, IData> entry : otherMap.entrySet()) {
         if (entry.getValue() instanceof DataMap && result.get(entry.getKey()) instanceof DataMap) {
            result.put(entry.getKey(), ((IData)result.get(entry.getKey())).sub((IData)entry.getValue()));
         } else {
            result.remove(entry.getKey());
         }
      }

      return new DataMap(result, this.immutable);
   }

   public IData mul(IData other) {
      throw new UnsupportedOperationException("Cannot multiply maps");
   }

   public IData div(IData other) {
      throw new UnsupportedOperationException("Cannot divide maps");
   }

   public IData mod(IData other) {
      throw new UnsupportedOperationException("Cannot perform modulo with maps");
   }

   public IData and(IData other) {
      throw new UnsupportedOperationException("Maps do not support bitwise operations");
   }

   public IData or(IData other) {
      throw new UnsupportedOperationException("Maps do not support bitwise operations");
   }

   public IData xor(IData other) {
      throw new UnsupportedOperationException("Maps do not support bitwise operations");
   }

   public IData neg() {
      throw new UnsupportedOperationException("Cannot negate maps.");
   }

   public IData not() {
      throw new UnsupportedOperationException("Maps do not support bitwise operations");
   }

   public boolean asBool() {
      throw new UnsupportedOperationException("Cannot cast map to bool");
   }

   public byte asByte() {
      throw new UnsupportedOperationException("Cannot cast map to byte");
   }

   public short asShort() {
      throw new UnsupportedOperationException("Cannot cast map to short");
   }

   public int asInt() {
      throw new UnsupportedOperationException("Cannot cast map to int");
   }

   public long asLong() {
      throw new UnsupportedOperationException("Cannot cast map to long");
   }

   public float asFloat() {
      throw new UnsupportedOperationException("Cannot cast map to float");
   }

   public double asDouble() {
      throw new UnsupportedOperationException("Cannot cast map to double");
   }

   public String asString() {
      StringBuilder result = new StringBuilder();
      result.append('{');
      boolean first = true;

      for(Entry<String, IData> entry : this.data.entrySet()) {
         if (first) {
            first = false;
         } else {
            result.append(", ");
         }

         if (this.isValidIdentifier((String)entry.getKey())) {
            result.append((String)entry.getKey());
         } else {
            result.append("\"").append((String)entry.getKey()).append("\"");
         }

         result.append(": ");
         result.append(((IData)entry.getValue()).toString());
      }

      result.append('}');
      return result.toString();
   }

   public List<IData> asList() {
      return null;
   }

   public Map<String, IData> asMap() {
      return this.immutable ? Collections.unmodifiableMap(this.data) : this.data;
   }

   public byte[] asByteArray() {
      return null;
   }

   public int[] asIntArray() {
      return null;
   }

   public IData getAt(int i) {
      return this.memberGet(Integer.toString(i));
   }

   public void setAt(int i, IData value) {
      this.memberSet(Integer.toString(i), value);
   }

   public IData memberGet(String name) {
      return (IData)this.data.get(name);
   }

   public void memberSet(String name, IData data) {
      if (this.immutable) {
         throw new UnsupportedOperationException("this map is not modifiable");
      } else {
         this.data.put(name, data);
      }
   }

   public int length() {
      return this.data.size();
   }

   public boolean contains(IData data) {
      if (data instanceof DataString) {
         return this.data.containsKey(data.asString());
      } else {
         Map<String, IData> dataMap = data.asMap();
         if (dataMap == null) {
            return false;
         } else {
            for(Entry<String, IData> dataEntry : dataMap.entrySet()) {
               if (!this.data.containsKey(dataEntry.getKey())) {
                  return false;
               }

               if (!((IData)this.data.get(dataEntry.getKey())).contains((IData)dataEntry.getValue())) {
                  return false;
               }
            }

            return true;
         }
      }
   }

   public int compareTo(IData data) {
      throw new UnsupportedOperationException("Cannot compare maps");
   }

   public boolean equals(IData data) {
      if (this == data) {
         return true;
      } else {
         Map<String, IData> dataMap = data.asMap();
         if (dataMap.size() != this.data.size()) {
            return false;
         } else {
            for(Entry<String, IData> dataEntry : this.data.entrySet()) {
               if (!dataMap.containsKey(dataEntry.getKey())) {
                  return false;
               }

               if (!((IData)dataMap.get(dataEntry.getKey())).equals((IData)dataEntry.getValue())) {
                  return false;
               }
            }

            return true;
         }
      }
   }

   public IData immutable() {
      if (this.immutable) {
         return this;
      } else {
         Map<String, IData> result = new HashMap();

         for(Entry<String, IData> entry : this.data.entrySet()) {
            result.put(entry.getKey(), ((IData)entry.getValue()).immutable());
         }

         return new DataMap(result, true);
      }
   }

   public IData update(IData data) {
      if (this.immutable) {
         data = data.immutable();
      }

      Map<String, IData> result = new HashMap();

      for(Entry<String, IData> entry : this.data.entrySet()) {
         result.put(entry.getKey(), entry.getValue());
      }

      for(Entry<String, IData> entry : data.asMap().entrySet()) {
         result.put(entry.getKey(), entry.getValue());
      }

      return new DataMap(result, this.immutable);
   }

   public <T> T convert(IDataConverter<T> converter) {
      return converter.fromMap(this.data);
   }

   public String toString() {
      return this.asString();
   }

   private boolean isValidIdentifier(String str) {
      if (!Character.isJavaIdentifierStart(str.charAt(0))) {
         return false;
      } else {
         for(int i = 1; i < str.length(); ++i) {
            if (!Character.isJavaIdentifierPart(str.charAt(i))) {
               return false;
            }
         }

         return true;
      }
   }
}
