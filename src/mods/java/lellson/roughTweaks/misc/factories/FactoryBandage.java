package lellson.roughTweaks.misc.factories;

import com.google.gson.JsonObject;
import java.util.function.BooleanSupplier;
import lellson.roughTweaks.misc.RoughConfig;
import net.minecraftforge.common.crafting.IConditionFactory;
import net.minecraftforge.common.crafting.JsonContext;

public class FactoryBandage implements IConditionFactory {
   public FactoryBandage() {
   }

   public BooleanSupplier parse(JsonContext context, JsonObject json) {
      return () -> {
         return RoughConfig.bandageHealCount > 0;
      };
   }
}
