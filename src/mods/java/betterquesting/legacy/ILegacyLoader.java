package betterquesting.legacy;

import com.google.gson.JsonElement;

public interface ILegacyLoader {
   void readFromJson(JsonElement var1);

   void readProgressFromJson(JsonElement var1);
}
