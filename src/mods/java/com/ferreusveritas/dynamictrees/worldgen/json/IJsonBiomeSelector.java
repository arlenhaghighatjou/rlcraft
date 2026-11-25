package com.ferreusveritas.dynamictrees.worldgen.json;

import com.google.gson.JsonElement;
import java.util.function.Predicate;
import net.minecraft.world.biome.Biome;

public interface IJsonBiomeSelector {
   Predicate<Biome> getFilter(JsonElement var1);
}
