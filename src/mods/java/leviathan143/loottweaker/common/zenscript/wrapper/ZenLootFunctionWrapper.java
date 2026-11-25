package leviathan143.loottweaker.common.zenscript.wrapper;

import crafttweaker.annotations.ZenRegister;
import java.util.Arrays;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.Predicate;
import java.util.stream.Stream;
import leviathan143.loottweaker.common.darkmagic.LootFunctionAccessors;
import leviathan143.loottweaker.common.lib.Arguments;
import leviathan143.loottweaker.common.zenscript.LootTweakerContext;
import net.minecraft.world.storage.loot.conditions.LootCondition;
import net.minecraft.world.storage.loot.functions.LootFunction;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

@ZenRegister
@ZenClass("loottweaker.vanilla.loot.LootFunction")
public class ZenLootFunctionWrapper {
   public static final ZenLootFunctionWrapper INVALID = new ZenLootFunctionWrapper((LootFunction)null, (LootTweakerContext)null);
   public final LootFunction function;
   private final LootTweakerContext context;

   public ZenLootFunctionWrapper(LootFunction function, LootTweakerContext context) {
      this.function = function;
      this.context = context;
   }

   @ZenMethod
   public ZenLootFunctionWrapper addConditions(ZenLootConditionWrapper[] conditions) {
      if (this.function == null) {
         return this;
      } else if (!Arguments.nonNull(this.context.getErrorHandler(), "conditions", conditions)) {
         return INVALID;
      } else {
         LootFunctionAccessors.setConditions(this.function, (LootCondition[])Stream.concat(Arrays.stream(this.function.getConditions()), Arrays.stream(conditions).filter(ZenLootConditionWrapper::isValid).map(ZenLootConditionWrapper::unwrap)).toArray((x$0) -> {
            return new LootCondition[x$0];
         }));
         return this;
      }
   }

   public LootFunction unwrap() {
      return this.function;
   }

   public boolean isValid() {
      return this.function != null;
   }
}
