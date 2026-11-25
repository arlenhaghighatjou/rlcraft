package leviathan143.loottweaker.common.zenscript.factory;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.item.IItemStack;
import crafttweaker.api.minecraft.CraftTweakerMC;
import crafttweaker.api.util.IRandom;
import crafttweaker.mc1120.util.MCRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import leviathan143.loottweaker.common.zenscript.wrapper.ZenLootContext;
import net.minecraft.item.ItemStack;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.conditions.LootCondition;
import net.minecraft.world.storage.loot.functions.LootFunction;
import net.minecraft.world.storage.loot.functions.LootFunction.Serializer;
import stanhebben.zenscript.annotations.ZenClass;

public class ZenLambdaLootFunction extends LootFunction {
   public static final Serializer<ZenLambdaLootFunction> SERIALISER = new ZenLambdaLootFunction.Serialiser();
   private static final List<ZenLambdaLootFunction> INSTANCES = new ArrayList();
   private final ZenLambdaLootFunction.Delegate delegate;
   private final int id;

   public ZenLambdaLootFunction(ZenLambdaLootFunction.Delegate delegate, LootCondition[] conditions) {
      super(conditions);
      this.delegate = delegate;
      INSTANCES.add(this);
      this.id = INSTANCES.size() - 1;
   }

   public ItemStack apply(ItemStack stack, Random rand, LootContext context) {
      IItemStack stackIn = CraftTweakerMC.getIItemStack(stack);
      IItemStack stackOut = this.delegate.apply(stackIn, new MCRandom(rand), new ZenLootContext(context));
      return CraftTweakerMC.getItemStack(stackOut);
   }

   @ZenRegister
   @ZenClass("loottweaker.CustomLootFunction")
   @FunctionalInterface
   public interface Delegate {
      IItemStack apply(IItemStack var1, IRandom var2, ZenLootContext var3);
   }

   private static class Serialiser extends Serializer<ZenLambdaLootFunction> {
      protected Serialiser() {
         super(new ResourceLocation("loottweaker", "zen_lambda"), ZenLambdaLootFunction.class);
      }

      public void serialize(JsonObject object, ZenLambdaLootFunction function, JsonSerializationContext context) {
         object.addProperty("lambda_id", Integer.valueOf(function.id));
      }

      public ZenLambdaLootFunction deserialize(JsonObject object, JsonDeserializationContext context, LootCondition[] conditions) {
         return (ZenLambdaLootFunction)ZenLambdaLootFunction.INSTANCES.get(JsonUtils.getInt(object, "lambda_id"));
      }
   }
}
