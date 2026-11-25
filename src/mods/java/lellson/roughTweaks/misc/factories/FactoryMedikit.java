package lellson.roughTweaks.misc.factories;

import com.google.gson.JsonObject;
import java.util.function.BooleanSupplier;
import lellson.roughTweaks.misc.RoughConfig;
import net.minecraftforge.common.crafting.IConditionFactory;
import net.minecraftforge.common.crafting.JsonContext;

public class FactoryMedikit implements IConditionFactory {
   public FactoryMedikit() {
   }

   public BooleanSupplier parse(JsonContext context, JsonObject json) {
      return () -> {
         return RoughConfig.medikitHealCount > 0 && RoughConfig.bandageHealCount > 0 && RoughConfig.salveHealCount > 0 && RoughConfig.plasterHealCount > 0;
      };
   }
}
