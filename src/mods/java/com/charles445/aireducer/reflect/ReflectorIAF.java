package com.charles445.aireducer.reflect;

import javax.annotation.Nullable;

public class ReflectorIAF {
   @Nullable
   public static ReflectorIAF reflector;
   public final Class c_iceandfire_MyrmexAIEscortEntity = Class.forName("com.github.alexthe666.iceandfire.entity.ai.MyrmexAIEscortEntity");
   public final Class c_iceandfire_MyrmexAIFindHidingSpot = Class.forName("com.github.alexthe666.iceandfire.entity.ai.MyrmexAIFindHidingSpot");
   public final Class c_iceandfire_MyrmexAIForage = Class.forName("com.github.alexthe666.iceandfire.entity.ai.MyrmexAIForage");
   public final Class c_iceandfire_MyrmexAILeaveHive = Class.forName("com.github.alexthe666.iceandfire.entity.ai.MyrmexAILeaveHive");
   public final Class c_iceandfire_MyrmexAIMoveThroughHive = Class.forName("com.github.alexthe666.iceandfire.entity.ai.MyrmexAIMoveThroughHive");
   public final Class c_iceandfire_MyrmexAIWanderHiveCenter = Class.forName("com.github.alexthe666.iceandfire.entity.ai.MyrmexAIWanderHiveCenter");

   public ReflectorIAF() throws Exception {
      reflector = this;
   }
}
