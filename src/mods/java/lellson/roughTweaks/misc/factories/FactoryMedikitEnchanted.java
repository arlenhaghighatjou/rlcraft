package lellson.roughTweaks.misc.factories;

import com.google.gson.JsonObject;
import java.util.function.BooleanSupplier;
import lellson.roughTweaks.misc.RoughConfig;
import net.minecraftforge.common.crafting.IConditionFactory;
import net.minecraftforge.common.crafting.JsonContext;

public class FactoryMedikitEnchanted implements IConditionFactory {
   public FactoryMedikitEnchanted() {
   }

   public BooleanSupplier parse(JsonContext context, JsonObject json) {
      return () -> {
         return RoughConfig.medikitEnchantedHealCount > 0 && RoughConfig.medikitHealCount > 0;
      };
   }
}
