package crafttweaker.api.data;

import java.util.List;
import java.util.Map;

public interface IDataConverter<T> {
   T fromBool(boolean var1);

   T fromByte(byte var1);

   T fromShort(short var1);

   T fromInt(int var1);

   T fromLong(long var1);

   T fromFloat(float var1);

   T fromDouble(double var1);

   T fromString(String var1);

   T fromList(List<IData> var1);

   T fromMap(Map<String, IData> var1);

   T fromByteArray(byte[] var1);

   T fromIntArray(int[] var1);
}
