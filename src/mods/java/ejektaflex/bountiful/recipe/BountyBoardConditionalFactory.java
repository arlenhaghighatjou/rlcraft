package ejektaflex.bountiful.recipe;

import com.google.gson.JsonObject;
import java.util.function.BooleanSupplier;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import net.minecraftforge.common.crafting.IConditionFactory;
import net.minecraftforge.common.crafting.JsonContext;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {1, 1, 13},
   bv = {1, 0, 3},
   k = 1,
   d1 = {"\u0000\u001e\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\u0018\u00002\u00020\u0001B\u0005¢\u0006\u0002\u0010\u0002J\u0018\u0010\u0003\u001a\u00020\u00042\u0006\u0010\u0005\u001a\u00020\u00062\u0006\u0010\u0007\u001a\u00020\bH\u0016¨\u0006\t"},
   d2 = {"Lejektaflex/bountiful/recipe/BountyBoardConditionalFactory;", "Lnet/minecraftforge/common/crafting/IConditionFactory;", "()V", "parse", "Ljava/util/function/BooleanSupplier;", "context", "Lnet/minecraftforge/common/crafting/JsonContext;", "json", "Lcom/google/gson/JsonObject;", "Bountiful"}
)
public final class BountyBoardConditionalFactory implements IConditionFactory {
   @NotNull
   public BooleanSupplier parse(@NotNull JsonContext context, @NotNull JsonObject json) {
      Intrinsics.checkParameterIsNotNull(context, "context");
      Intrinsics.checkParameterIsNotNull(json, "json");
      return null.INSTANCE;
   }

   public BountyBoardConditionalFactory() {
   }
}
