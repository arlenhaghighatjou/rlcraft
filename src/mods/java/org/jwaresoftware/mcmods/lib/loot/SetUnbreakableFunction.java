package org.jwaresoftware.mcmods.lib.loot;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.Random;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.conditions.LootCondition;
import net.minecraft.world.storage.loot.functions.LootFunction;
import org.jwaresoftware.mcmods.lib.ItemStacks;
import org.jwaresoftware.mcmods.lib.LibInfo;
import org.jwaresoftware.mcmods.lib.SharedGlue;

public final class SetUnbreakableFunction extends LootFunction {
   final boolean _unrepairable;

   public SetUnbreakableFunction(LootCondition[] conditions, boolean unrepairable) {
      super(conditions);
      this._unrepairable = unrepairable;
   }

   public ItemStack apply(ItemStack stack, Random rand, LootContext context) {
      if (!stack.isEmpty()) {
         NBTTagCompound nbt = ItemStacks.getTagCompoundNonNull(stack);
         nbt.setBoolean("Unbreakable", true);
         if (this._unrepairable && stack.isItemStackDamageable()) {
            stack.setRepairCost(SharedGlue.UNREPAIRABLE_TOOL_COST());
         }
      }

      return stack;
   }

   public static class Serializer extends net.minecraft.world.storage.loot.functions.LootFunction.Serializer<SetUnbreakableFunction> {
      public Serializer(boolean strict) {
         super(new ResourceLocation(LibInfo.MOD_ID(), "set_unbreakable"), SetUnbreakableFunction.class);
      }

      public SetUnbreakableFunction deserialize(JsonObject object, JsonDeserializationContext deserializationContext, LootCondition[] conditions) {
         boolean unrepairable = !JsonUtils.getBoolean(object, "repairable", false);
         return new SetUnbreakableFunction(conditions, unrepairable);
      }

      public void serialize(JsonObject object, SetUnbreakableFunction lfunction, JsonSerializationContext serializationContext) {
         if (!lfunction._unrepairable) {
            object.add("repairable", serializationContext.serialize(Boolean.valueOf(true)));
         }

      }
   }
}
