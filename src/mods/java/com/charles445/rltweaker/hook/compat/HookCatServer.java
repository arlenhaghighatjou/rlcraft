package com.charles445.rltweaker.hook.compat;

import com.charles445.rltweaker.RLTweaker;
import com.charles445.rltweaker.util.ErrorUtil;
import com.charles445.rltweaker.util.ReflectUtil;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import javax.annotation.Nullable;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.fml.common.ModContainer;

public class HookCatServer {
   @Nullable
   private static Class c_CraftingHelper$FactoryLoader = ReflectUtil.findClassOrNull("net.minecraftforge.common.crafting.CraftingHelper$FactoryLoader");
   @Nullable
   private static Class cArray_CraftingHelper$FactoryLoader = ReflectUtil.toArrayClass(c_CraftingHelper$FactoryLoader);
   @Nullable
   private static Method m_CraftingHelper_loadFactories = ReflectUtil.findMethodOrNull(CraftingHelper.class, "loadFactories", ModContainer.class, String.class, cArray_CraftingHelper$FactoryLoader);

   public HookCatServer() {
   }

   public static void loadAdvancementFactories(ModContainer mod, String path, Object[] loaders) {
      if (c_CraftingHelper$FactoryLoader != null && m_CraftingHelper_loadFactories != null && cArray_CraftingHelper$FactoryLoader != null) {
         if (mod.getModId().equals("disenchanter")) {
            RLTweaker.logger.info("Removing erroneous Disenchanter factories to avoid CatServer crash");
         } else {
            try {
               m_CraftingHelper_loadFactories.invoke((Object)null, mod, path, loaders);
            } catch (IllegalArgumentException | InvocationTargetException | IllegalAccessException var4) {
               RLTweaker.logger.error("CatServer hook failed to invoke reflection. Advancements condition factories will be broken!", var4);
               ErrorUtil.logSilent("CatServer Advancement Factories Invocation");
            }

         }
      } else {
         RLTweaker.logger.error("CatServer hook failed to setup reflection. Advancements condition factories will be broken!");
         ErrorUtil.logSilent("CatServer Advancement Factories Setup");
      }
   }
}
