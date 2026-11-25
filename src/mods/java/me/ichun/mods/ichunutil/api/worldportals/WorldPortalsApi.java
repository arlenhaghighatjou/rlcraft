package me.ichun.mods.ichunutil.api.worldportals;

public class WorldPortalsApi {
   private static IApi apiImpl = new ApiDummy();

   public WorldPortalsApi() {
   }

   public static IApi getApiImpl() {
      return apiImpl;
   }

   public static void setApiImpl(IApi apiImpl) {
      apiImpl = apiImpl;
   }
}
