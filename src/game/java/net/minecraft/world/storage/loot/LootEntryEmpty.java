package net.minecraft.world.storage.loot;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.Collection;
import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import net.minecraft.item.ItemStack;
import net.minecraft.world.storage.loot.conditions.LootCondition;

public class LootEntryEmpty extends LootEntry
{
    public LootEntryEmpty(int weightIn, int qualityIn, LootCondition[] conditionsIn, String entryName)
    {
        super(weightIn, qualityIn, conditionsIn, entryName);
    }

    public void addLoot(Collection<ItemStack> stacks, EaglercraftRandom rand, LootContext context)
    {
    }

    protected void serialize(JsonObject json, JsonSerializationContext context)
    {
    }

    public static LootEntryEmpty deserialize(JsonObject object, JsonDeserializationContext deserializationContext, int weightIn, int qualityIn, LootCondition[] conditionsIn)
    {
        return new LootEntryEmpty(weightIn, qualityIn, conditionsIn, net.minecraftforge.common.ForgeHooks.readLootEntryName(object, "empty"));
    }
}
