package moe.plushie.armourers_workshop.api.common.skin.type;

import java.util.ArrayList;

public interface ISkinTypeRegistry {
   boolean registerSkin(ISkinType var1);

   ISkinType getSkinTypeFromRegistryName(String var1);

   ISkinPartType getSkinPartFromRegistryName(String var1);

   ArrayList<ISkinType> getRegisteredSkinTypes();
}
