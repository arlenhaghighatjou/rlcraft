package lellson.roughTweaks.misc.factories;

import com.google.gson.JsonObject;
import java.util.function.BooleanSupplier;
import lellson.roughTweaks.misc.RoughConfig;
import net.minecraftforge.common.crafting.IConditionFactory;
import net.minecraftforge.common.crafting.JsonContext;

public class FactoryPlaster implements IConditionFactory {
   public FactoryPlaster() {
   }

   public BooleanSupplier parse(JsonContext context, JsonObject json) {
      return () -> {
         return RoughConfig.plasterHealCount > 0;
      };
   }
}
