package leviathan143.loottweaker.common.zenscript.factory;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.util.IRandom;
import crafttweaker.mc1120.util.MCRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import leviathan143.loottweaker.common.zenscript.wrapper.ZenLootContext;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.conditions.LootCondition;
import net.minecraft.world.storage.loot.conditions.LootCondition.Serializer;
import stanhebben.zenscript.annotations.ZenClass;

public class ZenLambdaLootCondition implements LootCondition {
   public static final Serializer<ZenLambdaLootCondition> SERIALISER = new ZenLambdaLootCondition.Serialiser();
   private static final List<ZenLambdaLootCondition> INSTANCES = new ArrayList();
   private final ZenLambdaLootCondition.Delegate delegate;
   private final int id;

   public ZenLambdaLootCondition(ZenLambdaLootCondition.Delegate delegate) {
      this.delegate = delegate;
      INSTANCES.add(this);
      this.id = INSTANCES.size() - 1;
   }

   public boolean testCondition(Random rand, LootContext context) {
      return this.delegate.test(new MCRandom(rand), new ZenLootContext(context));
   }

   @ZenRegister
   @ZenClass("loottweaker.CustomLootCondition")
   @FunctionalInterface
   public interface Delegate {
      boolean test(IRandom var1, ZenLootContext var2);
   }

   private static class Serialiser extends Serializer<ZenLambdaLootCondition> {
      protected Serialiser() {
         super(new ResourceLocation("loottweaker", "zen_lambda"), ZenLambdaLootCondition.class);
      }

      public void serialize(JsonObject object, ZenLambdaLootCondition function, JsonSerializationContext context) {
         object.addProperty("lambda_id", Integer.valueOf(function.id));
      }

      public ZenLambdaLootCondition deserialize(JsonObject json, JsonDeserializationContext context) {
         return (ZenLambdaLootCondition)ZenLambdaLootCondition.INSTANCES.get(JsonUtils.getInt(json, "lambda_id"));
      }
   }
}
