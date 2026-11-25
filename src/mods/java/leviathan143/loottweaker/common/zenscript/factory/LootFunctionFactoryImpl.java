package leviathan143.loottweaker.common.zenscript.factory;

import com.google.common.collect.Lists;
import crafttweaker.api.data.IData;
import crafttweaker.mc1120.data.NBTConverter;
import java.util.List;
import java.util.Map;
import leviathan143.loottweaker.common.lib.Arguments;
import leviathan143.loottweaker.common.lib.LootConditions;
import leviathan143.loottweaker.common.zenscript.JsonMapConversions;
import leviathan143.loottweaker.common.zenscript.LootTweakerContext;
import leviathan143.loottweaker.common.zenscript.wrapper.ZenLootFunctionWrapper;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.storage.loot.RandomValueRange;
import net.minecraft.world.storage.loot.functions.EnchantRandomly;
import net.minecraft.world.storage.loot.functions.EnchantWithLevels;
import net.minecraft.world.storage.loot.functions.LootingEnchantBonus;
import net.minecraft.world.storage.loot.functions.SetCount;
import net.minecraft.world.storage.loot.functions.SetDamage;
import net.minecraft.world.storage.loot.functions.SetMetadata;
import net.minecraft.world.storage.loot.functions.SetNBT;
import net.minecraft.world.storage.loot.functions.Smelt;

public class LootFunctionFactoryImpl {
   private final LootTweakerContext context;
   private final JsonMapConversions.Impl jsonMapConversions;

   public LootFunctionFactoryImpl(LootTweakerContext context) {
      this.context = context;
      this.jsonMapConversions = new JsonMapConversions.Impl(context);
   }

   public ZenLootFunctionWrapper enchantRandomly(String[] enchantIDList) {
      if (!Arguments.nonNull(this.context.getErrorHandler(), "enchantment IDs", enchantIDList)) {
         return ZenLootFunctionWrapper.INVALID;
      } else {
         List<Enchantment> enchantments = Lists.newArrayListWithCapacity(enchantIDList.length);

         for(String id : enchantIDList) {
            Enchantment ench = Enchantment.getEnchantmentByLocation(id);
            if (ench == null) {
               this.context.getErrorHandler().error("%s is not a valid enchantment id", id);
            } else {
               enchantments.add(ench);
            }
         }

         return new ZenLootFunctionWrapper(new EnchantRandomly(LootConditions.NONE, enchantments), this.context);
      }
   }

   public ZenLootFunctionWrapper enchantWithLevels(int min, int max, boolean isTreasure) {
      return new ZenLootFunctionWrapper(new EnchantWithLevels(LootConditions.NONE, new RandomValueRange((float)min, (float)max), isTreasure), this.context);
   }

   public ZenLootFunctionWrapper lootingEnchantBonus(int min, int max, int limit) {
      return new ZenLootFunctionWrapper(new LootingEnchantBonus(LootConditions.NONE, new RandomValueRange((float)min, (float)max), limit), this.context);
   }

   public ZenLootFunctionWrapper setCount(int min, int max) {
      return new ZenLootFunctionWrapper(new SetCount(LootConditions.NONE, new RandomValueRange((float)min, (float)max)), this.context);
   }

   public ZenLootFunctionWrapper setDamage(float min, float max) {
      if (max > 1.0F) {
         this.context.getErrorHandler().error("Items cannot recieve more than 100% damage!");
         return ZenLootFunctionWrapper.INVALID;
      } else {
         return new ZenLootFunctionWrapper(new SetDamage(LootConditions.NONE, new RandomValueRange(min, max)), this.context);
      }
   }

   public ZenLootFunctionWrapper setMetadata(int min, int max) {
      return new ZenLootFunctionWrapper(new SetMetadata(LootConditions.NONE, new RandomValueRange((float)min, (float)max)), this.context);
   }

   public ZenLootFunctionWrapper setNBT(IData nbtData) {
      NBTBase nbt = NBTConverter.from(nbtData);
      if (!(nbt instanceof NBTTagCompound)) {
         this.context.getErrorHandler().error("Expected compound nbt tag, got %s", nbtData);
         return ZenLootFunctionWrapper.INVALID;
      } else {
         return new ZenLootFunctionWrapper(new SetNBT(LootConditions.NONE, (NBTTagCompound)nbt), this.context);
      }
   }

   public ZenLootFunctionWrapper smelt() {
      return new ZenLootFunctionWrapper(new Smelt(LootConditions.NONE), this.context);
   }

   public ZenLootFunctionWrapper parse(Map<String, ?> json) {
      return this.jsonMapConversions.asLootFunction(json);
   }

   public ZenLootFunctionWrapper zenscript(ZenLambdaLootFunction.Delegate delegate) {
      return new ZenLootFunctionWrapper(new ZenLambdaLootFunction(delegate, LootConditions.NONE), this.context);
   }
}
