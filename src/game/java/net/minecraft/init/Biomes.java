package net.minecraft.init;

import net.minecraftforge.registries.ObjectHolderRegistry;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.Biome;

public abstract class Biomes
{
    public static Biome OCEAN;
    public static final Biome DEFAULT;
    public static Biome PLAINS;
    public static Biome DESERT;
    public static Biome EXTREME_HILLS;
    public static Biome FOREST;
    public static Biome TAIGA;
    public static Biome SWAMPLAND;
    public static Biome RIVER;
    public static Biome HELL;
    public static Biome SKY;
    public static Biome FROZEN_OCEAN;
    public static Biome FROZEN_RIVER;
    public static Biome ICE_PLAINS;
    public static Biome ICE_MOUNTAINS;
    public static Biome MUSHROOM_ISLAND;
    public static Biome MUSHROOM_ISLAND_SHORE;
    public static Biome BEACH;
    public static Biome DESERT_HILLS;
    public static Biome FOREST_HILLS;
    public static Biome TAIGA_HILLS;
    public static Biome EXTREME_HILLS_EDGE;
    public static Biome JUNGLE;
    public static Biome JUNGLE_HILLS;
    public static Biome JUNGLE_EDGE;
    public static Biome DEEP_OCEAN;
    public static Biome STONE_BEACH;
    public static Biome COLD_BEACH;
    public static Biome BIRCH_FOREST;
    public static Biome BIRCH_FOREST_HILLS;
    public static Biome ROOFED_FOREST;
    public static Biome COLD_TAIGA;
    public static Biome COLD_TAIGA_HILLS;
    public static Biome REDWOOD_TAIGA;
    public static Biome REDWOOD_TAIGA_HILLS;
    public static Biome EXTREME_HILLS_WITH_TREES;
    public static Biome SAVANNA;
    public static Biome SAVANNA_PLATEAU;
    public static Biome MESA;
    public static Biome MESA_ROCK;
    public static Biome MESA_CLEAR_ROCK;
    public static Biome VOID;
    public static Biome MUTATED_PLAINS;
    public static Biome MUTATED_DESERT;
    public static Biome MUTATED_EXTREME_HILLS;
    public static Biome MUTATED_FOREST;
    public static Biome MUTATED_TAIGA;
    public static Biome MUTATED_SWAMPLAND;
    public static Biome MUTATED_ICE_FLATS;
    public static Biome MUTATED_JUNGLE;
    public static Biome MUTATED_JUNGLE_EDGE;
    public static Biome MUTATED_BIRCH_FOREST;
    public static Biome MUTATED_BIRCH_FOREST_HILLS;
    public static Biome MUTATED_ROOFED_FOREST;
    public static Biome MUTATED_TAIGA_COLD;
    public static Biome MUTATED_REDWOOD_TAIGA;
    public static Biome MUTATED_REDWOOD_TAIGA_HILLS;
    public static Biome MUTATED_EXTREME_HILLS_WITH_TREES;
    public static Biome MUTATED_SAVANNA;
    public static Biome MUTATED_SAVANNA_ROCK;
    public static Biome MUTATED_MESA;
    public static Biome MUTATED_MESA_ROCK;
    public static Biome MUTATED_MESA_CLEAR_ROCK;

    private static Biome getRegisteredBiome(String id)
    {
        Biome biome = Biome.REGISTRY.getObject(new ResourceLocation(id));

        if (biome == null)
        {
            throw new IllegalStateException("Invalid Biome requested: " + id);
        }
        else
        {
            return biome;
        }
    }

    static
    {
        if (!Bootstrap.isRegistered())
        {
            throw new RuntimeException("Accessed Biomes before Bootstrap!");
        }
        else
        {
            OCEAN = getRegisteredBiome("ocean");
            DEFAULT = OCEAN;
            PLAINS = getRegisteredBiome("plains");
            DESERT = getRegisteredBiome("desert");
            EXTREME_HILLS = getRegisteredBiome("extreme_hills");
            FOREST = getRegisteredBiome("forest");
            TAIGA = getRegisteredBiome("taiga");
            SWAMPLAND = getRegisteredBiome("swampland");
            RIVER = getRegisteredBiome("river");
            HELL = getRegisteredBiome("hell");
            SKY = getRegisteredBiome("sky");
            FROZEN_OCEAN = getRegisteredBiome("frozen_ocean");
            FROZEN_RIVER = getRegisteredBiome("frozen_river");
            ICE_PLAINS = getRegisteredBiome("ice_flats");
            ICE_MOUNTAINS = getRegisteredBiome("ice_mountains");
            MUSHROOM_ISLAND = getRegisteredBiome("mushroom_island");
            MUSHROOM_ISLAND_SHORE = getRegisteredBiome("mushroom_island_shore");
            BEACH = getRegisteredBiome("beaches");
            DESERT_HILLS = getRegisteredBiome("desert_hills");
            FOREST_HILLS = getRegisteredBiome("forest_hills");
            TAIGA_HILLS = getRegisteredBiome("taiga_hills");
            EXTREME_HILLS_EDGE = getRegisteredBiome("smaller_extreme_hills");
            JUNGLE = getRegisteredBiome("jungle");
            JUNGLE_HILLS = getRegisteredBiome("jungle_hills");
            JUNGLE_EDGE = getRegisteredBiome("jungle_edge");
            DEEP_OCEAN = getRegisteredBiome("deep_ocean");
            STONE_BEACH = getRegisteredBiome("stone_beach");
            COLD_BEACH = getRegisteredBiome("cold_beach");
            BIRCH_FOREST = getRegisteredBiome("birch_forest");
            BIRCH_FOREST_HILLS = getRegisteredBiome("birch_forest_hills");
            ROOFED_FOREST = getRegisteredBiome("roofed_forest");
            COLD_TAIGA = getRegisteredBiome("taiga_cold");
            COLD_TAIGA_HILLS = getRegisteredBiome("taiga_cold_hills");
            REDWOOD_TAIGA = getRegisteredBiome("redwood_taiga");
            REDWOOD_TAIGA_HILLS = getRegisteredBiome("redwood_taiga_hills");
            EXTREME_HILLS_WITH_TREES = getRegisteredBiome("extreme_hills_with_trees");
            SAVANNA = getRegisteredBiome("savanna");
            SAVANNA_PLATEAU = getRegisteredBiome("savanna_rock");
            MESA = getRegisteredBiome("mesa");
            MESA_ROCK = getRegisteredBiome("mesa_rock");
            MESA_CLEAR_ROCK = getRegisteredBiome("mesa_clear_rock");
            VOID = getRegisteredBiome("void");
            MUTATED_PLAINS = getRegisteredBiome("mutated_plains");
            MUTATED_DESERT = getRegisteredBiome("mutated_desert");
            MUTATED_EXTREME_HILLS = getRegisteredBiome("mutated_extreme_hills");
            MUTATED_FOREST = getRegisteredBiome("mutated_forest");
            MUTATED_TAIGA = getRegisteredBiome("mutated_taiga");
            MUTATED_SWAMPLAND = getRegisteredBiome("mutated_swampland");
            MUTATED_ICE_FLATS = getRegisteredBiome("mutated_ice_flats");
            MUTATED_JUNGLE = getRegisteredBiome("mutated_jungle");
            MUTATED_JUNGLE_EDGE = getRegisteredBiome("mutated_jungle_edge");
            MUTATED_BIRCH_FOREST = getRegisteredBiome("mutated_birch_forest");
            MUTATED_BIRCH_FOREST_HILLS = getRegisteredBiome("mutated_birch_forest_hills");
            MUTATED_ROOFED_FOREST = getRegisteredBiome("mutated_roofed_forest");
            MUTATED_TAIGA_COLD = getRegisteredBiome("mutated_taiga_cold");
            MUTATED_REDWOOD_TAIGA = getRegisteredBiome("mutated_redwood_taiga");
            MUTATED_REDWOOD_TAIGA_HILLS = getRegisteredBiome("mutated_redwood_taiga_hills");
            MUTATED_EXTREME_HILLS_WITH_TREES = getRegisteredBiome("mutated_extreme_hills_with_trees");
            MUTATED_SAVANNA = getRegisteredBiome("mutated_savanna");
            MUTATED_SAVANNA_ROCK = getRegisteredBiome("mutated_savanna_rock");
            MUTATED_MESA = getRegisteredBiome("mutated_mesa");
            MUTATED_MESA_ROCK = getRegisteredBiome("mutated_mesa_rock");
            MUTATED_MESA_CLEAR_ROCK = getRegisteredBiome("mutated_mesa_clear_rock");
        }
    }

    public static void registerHolders()
    {
        ObjectHolderRegistry.INSTANCE.registerVanilla(Biome.class, new ResourceLocation("minecraft:ocean"), Biome.class, v -> OCEAN = v);
        ObjectHolderRegistry.INSTANCE.registerVanilla(Biome.class, new ResourceLocation("minecraft:plains"), Biome.class, v -> PLAINS = v);
        ObjectHolderRegistry.INSTANCE.registerVanilla(Biome.class, new ResourceLocation("minecraft:desert"), Biome.class, v -> DESERT = v);
        ObjectHolderRegistry.INSTANCE.registerVanilla(Biome.class, new ResourceLocation("minecraft:extreme_hills"), Biome.class, v -> EXTREME_HILLS = v);
        ObjectHolderRegistry.INSTANCE.registerVanilla(Biome.class, new ResourceLocation("minecraft:forest"), Biome.class, v -> FOREST = v);
        ObjectHolderRegistry.INSTANCE.registerVanilla(Biome.class, new ResourceLocation("minecraft:taiga"), Biome.class, v -> TAIGA = v);
        ObjectHolderRegistry.INSTANCE.registerVanilla(Biome.class, new ResourceLocation("minecraft:swampland"), Biome.class, v -> SWAMPLAND = v);
        ObjectHolderRegistry.INSTANCE.registerVanilla(Biome.class, new ResourceLocation("minecraft:river"), Biome.class, v -> RIVER = v);
        ObjectHolderRegistry.INSTANCE.registerVanilla(Biome.class, new ResourceLocation("minecraft:hell"), Biome.class, v -> HELL = v);
        ObjectHolderRegistry.INSTANCE.registerVanilla(Biome.class, new ResourceLocation("minecraft:sky"), Biome.class, v -> SKY = v);
        ObjectHolderRegistry.INSTANCE.registerVanilla(Biome.class, new ResourceLocation("minecraft:frozen_ocean"), Biome.class, v -> FROZEN_OCEAN = v);
        ObjectHolderRegistry.INSTANCE.registerVanilla(Biome.class, new ResourceLocation("minecraft:frozen_river"), Biome.class, v -> FROZEN_RIVER = v);
        ObjectHolderRegistry.INSTANCE.registerVanilla(Biome.class, new ResourceLocation("minecraft:ice_flats"), Biome.class, v -> ICE_PLAINS = v);
        ObjectHolderRegistry.INSTANCE.registerVanilla(Biome.class, new ResourceLocation("minecraft:ice_mountains"), Biome.class, v -> ICE_MOUNTAINS = v);
        ObjectHolderRegistry.INSTANCE.registerVanilla(Biome.class, new ResourceLocation("minecraft:mushroom_island"), Biome.class, v -> MUSHROOM_ISLAND = v);
        ObjectHolderRegistry.INSTANCE.registerVanilla(Biome.class, new ResourceLocation("minecraft:mushroom_island_shore"), Biome.class, v -> MUSHROOM_ISLAND_SHORE = v);
        ObjectHolderRegistry.INSTANCE.registerVanilla(Biome.class, new ResourceLocation("minecraft:beaches"), Biome.class, v -> BEACH = v);
        ObjectHolderRegistry.INSTANCE.registerVanilla(Biome.class, new ResourceLocation("minecraft:desert_hills"), Biome.class, v -> DESERT_HILLS = v);
        ObjectHolderRegistry.INSTANCE.registerVanilla(Biome.class, new ResourceLocation("minecraft:forest_hills"), Biome.class, v -> FOREST_HILLS = v);
        ObjectHolderRegistry.INSTANCE.registerVanilla(Biome.class, new ResourceLocation("minecraft:taiga_hills"), Biome.class, v -> TAIGA_HILLS = v);
        ObjectHolderRegistry.INSTANCE.registerVanilla(Biome.class, new ResourceLocation("minecraft:smaller_extreme_hills"), Biome.class, v -> EXTREME_HILLS_EDGE = v);
        ObjectHolderRegistry.INSTANCE.registerVanilla(Biome.class, new ResourceLocation("minecraft:jungle"), Biome.class, v -> JUNGLE = v);
        ObjectHolderRegistry.INSTANCE.registerVanilla(Biome.class, new ResourceLocation("minecraft:jungle_hills"), Biome.class, v -> JUNGLE_HILLS = v);
        ObjectHolderRegistry.INSTANCE.registerVanilla(Biome.class, new ResourceLocation("minecraft:jungle_edge"), Biome.class, v -> JUNGLE_EDGE = v);
        ObjectHolderRegistry.INSTANCE.registerVanilla(Biome.class, new ResourceLocation("minecraft:deep_ocean"), Biome.class, v -> DEEP_OCEAN = v);
        ObjectHolderRegistry.INSTANCE.registerVanilla(Biome.class, new ResourceLocation("minecraft:stone_beach"), Biome.class, v -> STONE_BEACH = v);
        ObjectHolderRegistry.INSTANCE.registerVanilla(Biome.class, new ResourceLocation("minecraft:cold_beach"), Biome.class, v -> COLD_BEACH = v);
        ObjectHolderRegistry.INSTANCE.registerVanilla(Biome.class, new ResourceLocation("minecraft:birch_forest"), Biome.class, v -> BIRCH_FOREST = v);
        ObjectHolderRegistry.INSTANCE.registerVanilla(Biome.class, new ResourceLocation("minecraft:birch_forest_hills"), Biome.class, v -> BIRCH_FOREST_HILLS = v);
        ObjectHolderRegistry.INSTANCE.registerVanilla(Biome.class, new ResourceLocation("minecraft:roofed_forest"), Biome.class, v -> ROOFED_FOREST = v);
        ObjectHolderRegistry.INSTANCE.registerVanilla(Biome.class, new ResourceLocation("minecraft:taiga_cold"), Biome.class, v -> COLD_TAIGA = v);
        ObjectHolderRegistry.INSTANCE.registerVanilla(Biome.class, new ResourceLocation("minecraft:taiga_cold_hills"), Biome.class, v -> COLD_TAIGA_HILLS = v);
        ObjectHolderRegistry.INSTANCE.registerVanilla(Biome.class, new ResourceLocation("minecraft:redwood_taiga"), Biome.class, v -> REDWOOD_TAIGA = v);
        ObjectHolderRegistry.INSTANCE.registerVanilla(Biome.class, new ResourceLocation("minecraft:redwood_taiga_hills"), Biome.class, v -> REDWOOD_TAIGA_HILLS = v);
        ObjectHolderRegistry.INSTANCE.registerVanilla(Biome.class, new ResourceLocation("minecraft:extreme_hills_with_trees"), Biome.class, v -> EXTREME_HILLS_WITH_TREES = v);
        ObjectHolderRegistry.INSTANCE.registerVanilla(Biome.class, new ResourceLocation("minecraft:savanna"), Biome.class, v -> SAVANNA = v);
        ObjectHolderRegistry.INSTANCE.registerVanilla(Biome.class, new ResourceLocation("minecraft:savanna_rock"), Biome.class, v -> SAVANNA_PLATEAU = v);
        ObjectHolderRegistry.INSTANCE.registerVanilla(Biome.class, new ResourceLocation("minecraft:mesa"), Biome.class, v -> MESA = v);
        ObjectHolderRegistry.INSTANCE.registerVanilla(Biome.class, new ResourceLocation("minecraft:mesa_rock"), Biome.class, v -> MESA_ROCK = v);
        ObjectHolderRegistry.INSTANCE.registerVanilla(Biome.class, new ResourceLocation("minecraft:mesa_clear_rock"), Biome.class, v -> MESA_CLEAR_ROCK = v);
        ObjectHolderRegistry.INSTANCE.registerVanilla(Biome.class, new ResourceLocation("minecraft:void"), Biome.class, v -> VOID = v);
        ObjectHolderRegistry.INSTANCE.registerVanilla(Biome.class, new ResourceLocation("minecraft:mutated_plains"), Biome.class, v -> MUTATED_PLAINS = v);
        ObjectHolderRegistry.INSTANCE.registerVanilla(Biome.class, new ResourceLocation("minecraft:mutated_desert"), Biome.class, v -> MUTATED_DESERT = v);
        ObjectHolderRegistry.INSTANCE.registerVanilla(Biome.class, new ResourceLocation("minecraft:mutated_extreme_hills"), Biome.class, v -> MUTATED_EXTREME_HILLS = v);
        ObjectHolderRegistry.INSTANCE.registerVanilla(Biome.class, new ResourceLocation("minecraft:mutated_forest"), Biome.class, v -> MUTATED_FOREST = v);
        ObjectHolderRegistry.INSTANCE.registerVanilla(Biome.class, new ResourceLocation("minecraft:mutated_taiga"), Biome.class, v -> MUTATED_TAIGA = v);
        ObjectHolderRegistry.INSTANCE.registerVanilla(Biome.class, new ResourceLocation("minecraft:mutated_swampland"), Biome.class, v -> MUTATED_SWAMPLAND = v);
        ObjectHolderRegistry.INSTANCE.registerVanilla(Biome.class, new ResourceLocation("minecraft:mutated_ice_flats"), Biome.class, v -> MUTATED_ICE_FLATS = v);
        ObjectHolderRegistry.INSTANCE.registerVanilla(Biome.class, new ResourceLocation("minecraft:mutated_jungle"), Biome.class, v -> MUTATED_JUNGLE = v);
        ObjectHolderRegistry.INSTANCE.registerVanilla(Biome.class, new ResourceLocation("minecraft:mutated_jungle_edge"), Biome.class, v -> MUTATED_JUNGLE_EDGE = v);
        ObjectHolderRegistry.INSTANCE.registerVanilla(Biome.class, new ResourceLocation("minecraft:mutated_birch_forest"), Biome.class, v -> MUTATED_BIRCH_FOREST = v);
        ObjectHolderRegistry.INSTANCE.registerVanilla(Biome.class, new ResourceLocation("minecraft:mutated_birch_forest_hills"), Biome.class, v -> MUTATED_BIRCH_FOREST_HILLS = v);
        ObjectHolderRegistry.INSTANCE.registerVanilla(Biome.class, new ResourceLocation("minecraft:mutated_roofed_forest"), Biome.class, v -> MUTATED_ROOFED_FOREST = v);
        ObjectHolderRegistry.INSTANCE.registerVanilla(Biome.class, new ResourceLocation("minecraft:mutated_taiga_cold"), Biome.class, v -> MUTATED_TAIGA_COLD = v);
        ObjectHolderRegistry.INSTANCE.registerVanilla(Biome.class, new ResourceLocation("minecraft:mutated_redwood_taiga"), Biome.class, v -> MUTATED_REDWOOD_TAIGA = v);
        ObjectHolderRegistry.INSTANCE.registerVanilla(Biome.class, new ResourceLocation("minecraft:mutated_redwood_taiga_hills"), Biome.class, v -> MUTATED_REDWOOD_TAIGA_HILLS = v);
        ObjectHolderRegistry.INSTANCE.registerVanilla(Biome.class, new ResourceLocation("minecraft:mutated_extreme_hills_with_trees"), Biome.class, v -> MUTATED_EXTREME_HILLS_WITH_TREES = v);
        ObjectHolderRegistry.INSTANCE.registerVanilla(Biome.class, new ResourceLocation("minecraft:mutated_savanna"), Biome.class, v -> MUTATED_SAVANNA = v);
        ObjectHolderRegistry.INSTANCE.registerVanilla(Biome.class, new ResourceLocation("minecraft:mutated_savanna_rock"), Biome.class, v -> MUTATED_SAVANNA_ROCK = v);
        ObjectHolderRegistry.INSTANCE.registerVanilla(Biome.class, new ResourceLocation("minecraft:mutated_mesa"), Biome.class, v -> MUTATED_MESA = v);
        ObjectHolderRegistry.INSTANCE.registerVanilla(Biome.class, new ResourceLocation("minecraft:mutated_mesa_rock"), Biome.class, v -> MUTATED_MESA_ROCK = v);
        ObjectHolderRegistry.INSTANCE.registerVanilla(Biome.class, new ResourceLocation("minecraft:mutated_mesa_clear_rock"), Biome.class, v -> MUTATED_MESA_CLEAR_ROCK = v);
    }
}
