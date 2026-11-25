package artifacts.common.loot.functions;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.Random;
import javax.annotation.ParametersAreNonnullByDefault;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionType;
import net.minecraft.potion.PotionUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.conditions.LootCondition;
import net.minecraft.world.storage.loot.functions.LootFunction;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class AddRandomEffect extends LootFunction {
   public AddRandomEffect(LootCondition[] conditions) {
      super(conditions);
   }

   public ItemStack apply(ItemStack stack, Random random, LootContext context) {
      PotionType type = (PotionType)PotionType.REGISTRY.getRandomObject(random);
      if (type == PotionType.getPotionTypeForName("empty")) {
         type = PotionType.getPotionTypeForName("water");
      }

      PotionUtils.addPotionToItemStack(stack, type);
      return stack;
   }

   public static class Serializer extends net.minecraft.world.storage.loot.functions.LootFunction.Serializer<AddRandomEffect> {
      public Serializer() {
         super(new ResourceLocation("artifacts", "add_random_effect"), AddRandomEffect.class);
      }

      public void serialize(JsonObject object, AddRandomEffect function, JsonSerializationContext serializationContext) {
      }

      public AddRandomEffect deserialize(JsonObject object, JsonDeserializationContext deserializationContext, LootCondition[] conditions) {
         return new AddRandomEffect(conditions);
      }
   }
}
