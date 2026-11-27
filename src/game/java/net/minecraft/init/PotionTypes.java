package net.minecraft.init;

import net.minecraftforge.registries.ObjectHolderRegistry;
import com.google.common.collect.Sets;
import java.util.Set;
import net.minecraft.potion.PotionType;
import net.minecraft.util.ResourceLocation;

public class PotionTypes
{
    private static final Set<PotionType> CACHE;
    public static PotionType EMPTY;
    public static PotionType WATER;
    public static PotionType MUNDANE;
    public static PotionType THICK;
    public static PotionType AWKWARD;
    public static PotionType NIGHT_VISION;
    public static PotionType LONG_NIGHT_VISION;
    public static PotionType INVISIBILITY;
    public static PotionType LONG_INVISIBILITY;
    public static PotionType LEAPING;
    public static PotionType LONG_LEAPING;
    public static PotionType STRONG_LEAPING;
    public static PotionType FIRE_RESISTANCE;
    public static PotionType LONG_FIRE_RESISTANCE;
    public static PotionType SWIFTNESS;
    public static PotionType LONG_SWIFTNESS;
    public static PotionType STRONG_SWIFTNESS;
    public static PotionType SLOWNESS;
    public static PotionType LONG_SLOWNESS;
    public static PotionType WATER_BREATHING;
    public static PotionType LONG_WATER_BREATHING;
    public static PotionType HEALING;
    public static PotionType STRONG_HEALING;
    public static PotionType HARMING;
    public static PotionType STRONG_HARMING;
    public static PotionType POISON;
    public static PotionType LONG_POISON;
    public static PotionType STRONG_POISON;
    public static PotionType REGENERATION;
    public static PotionType LONG_REGENERATION;
    public static PotionType STRONG_REGENERATION;
    public static PotionType STRENGTH;
    public static PotionType LONG_STRENGTH;
    public static PotionType STRONG_STRENGTH;
    public static PotionType WEAKNESS;
    public static PotionType LONG_WEAKNESS;

    private static PotionType getRegisteredPotionType(String id)
    {
        PotionType potiontype = PotionType.REGISTRY.getObject(new ResourceLocation(id));

        if (!CACHE.add(potiontype))
        {
            throw new IllegalStateException("Invalid Potion requested: " + id);
        }
        else
        {
            return potiontype;
        }
    }

    static
    {
        if (!Bootstrap.isRegistered())
        {
            throw new RuntimeException("Accessed Potions before Bootstrap!");
        }
        else
        {
            CACHE = Sets.<PotionType>newHashSet();
            EMPTY = getRegisteredPotionType("empty");
            WATER = getRegisteredPotionType("water");
            MUNDANE = getRegisteredPotionType("mundane");
            THICK = getRegisteredPotionType("thick");
            AWKWARD = getRegisteredPotionType("awkward");
            NIGHT_VISION = getRegisteredPotionType("night_vision");
            LONG_NIGHT_VISION = getRegisteredPotionType("long_night_vision");
            INVISIBILITY = getRegisteredPotionType("invisibility");
            LONG_INVISIBILITY = getRegisteredPotionType("long_invisibility");
            LEAPING = getRegisteredPotionType("leaping");
            LONG_LEAPING = getRegisteredPotionType("long_leaping");
            STRONG_LEAPING = getRegisteredPotionType("strong_leaping");
            FIRE_RESISTANCE = getRegisteredPotionType("fire_resistance");
            LONG_FIRE_RESISTANCE = getRegisteredPotionType("long_fire_resistance");
            SWIFTNESS = getRegisteredPotionType("swiftness");
            LONG_SWIFTNESS = getRegisteredPotionType("long_swiftness");
            STRONG_SWIFTNESS = getRegisteredPotionType("strong_swiftness");
            SLOWNESS = getRegisteredPotionType("slowness");
            LONG_SLOWNESS = getRegisteredPotionType("long_slowness");
            WATER_BREATHING = getRegisteredPotionType("water_breathing");
            LONG_WATER_BREATHING = getRegisteredPotionType("long_water_breathing");
            HEALING = getRegisteredPotionType("healing");
            STRONG_HEALING = getRegisteredPotionType("strong_healing");
            HARMING = getRegisteredPotionType("harming");
            STRONG_HARMING = getRegisteredPotionType("strong_harming");
            POISON = getRegisteredPotionType("poison");
            LONG_POISON = getRegisteredPotionType("long_poison");
            STRONG_POISON = getRegisteredPotionType("strong_poison");
            REGENERATION = getRegisteredPotionType("regeneration");
            LONG_REGENERATION = getRegisteredPotionType("long_regeneration");
            STRONG_REGENERATION = getRegisteredPotionType("strong_regeneration");
            STRENGTH = getRegisteredPotionType("strength");
            LONG_STRENGTH = getRegisteredPotionType("long_strength");
            STRONG_STRENGTH = getRegisteredPotionType("strong_strength");
            WEAKNESS = getRegisteredPotionType("weakness");
            LONG_WEAKNESS = getRegisteredPotionType("long_weakness");
            CACHE.clear();
        }
    }

    public static void registerHolders()
    {
        ObjectHolderRegistry.INSTANCE.registerVanilla(PotionType.class, new ResourceLocation("minecraft:empty"), PotionType.class, v -> EMPTY = v);
        ObjectHolderRegistry.INSTANCE.registerVanilla(PotionType.class, new ResourceLocation("minecraft:water"), PotionType.class, v -> WATER = v);
        ObjectHolderRegistry.INSTANCE.registerVanilla(PotionType.class, new ResourceLocation("minecraft:mundane"), PotionType.class, v -> MUNDANE = v);
        ObjectHolderRegistry.INSTANCE.registerVanilla(PotionType.class, new ResourceLocation("minecraft:thick"), PotionType.class, v -> THICK = v);
        ObjectHolderRegistry.INSTANCE.registerVanilla(PotionType.class, new ResourceLocation("minecraft:awkward"), PotionType.class, v -> AWKWARD = v);
        ObjectHolderRegistry.INSTANCE.registerVanilla(PotionType.class, new ResourceLocation("minecraft:night_vision"), PotionType.class, v -> NIGHT_VISION = v);
        ObjectHolderRegistry.INSTANCE.registerVanilla(PotionType.class, new ResourceLocation("minecraft:long_night_vision"), PotionType.class, v -> LONG_NIGHT_VISION = v);
        ObjectHolderRegistry.INSTANCE.registerVanilla(PotionType.class, new ResourceLocation("minecraft:invisibility"), PotionType.class, v -> INVISIBILITY = v);
        ObjectHolderRegistry.INSTANCE.registerVanilla(PotionType.class, new ResourceLocation("minecraft:long_invisibility"), PotionType.class, v -> LONG_INVISIBILITY = v);
        ObjectHolderRegistry.INSTANCE.registerVanilla(PotionType.class, new ResourceLocation("minecraft:leaping"), PotionType.class, v -> LEAPING = v);
        ObjectHolderRegistry.INSTANCE.registerVanilla(PotionType.class, new ResourceLocation("minecraft:long_leaping"), PotionType.class, v -> LONG_LEAPING = v);
        ObjectHolderRegistry.INSTANCE.registerVanilla(PotionType.class, new ResourceLocation("minecraft:strong_leaping"), PotionType.class, v -> STRONG_LEAPING = v);
        ObjectHolderRegistry.INSTANCE.registerVanilla(PotionType.class, new ResourceLocation("minecraft:fire_resistance"), PotionType.class, v -> FIRE_RESISTANCE = v);
        ObjectHolderRegistry.INSTANCE.registerVanilla(PotionType.class, new ResourceLocation("minecraft:long_fire_resistance"), PotionType.class, v -> LONG_FIRE_RESISTANCE = v);
        ObjectHolderRegistry.INSTANCE.registerVanilla(PotionType.class, new ResourceLocation("minecraft:swiftness"), PotionType.class, v -> SWIFTNESS = v);
        ObjectHolderRegistry.INSTANCE.registerVanilla(PotionType.class, new ResourceLocation("minecraft:long_swiftness"), PotionType.class, v -> LONG_SWIFTNESS = v);
        ObjectHolderRegistry.INSTANCE.registerVanilla(PotionType.class, new ResourceLocation("minecraft:strong_swiftness"), PotionType.class, v -> STRONG_SWIFTNESS = v);
        ObjectHolderRegistry.INSTANCE.registerVanilla(PotionType.class, new ResourceLocation("minecraft:slowness"), PotionType.class, v -> SLOWNESS = v);
        ObjectHolderRegistry.INSTANCE.registerVanilla(PotionType.class, new ResourceLocation("minecraft:long_slowness"), PotionType.class, v -> LONG_SLOWNESS = v);
        ObjectHolderRegistry.INSTANCE.registerVanilla(PotionType.class, new ResourceLocation("minecraft:water_breathing"), PotionType.class, v -> WATER_BREATHING = v);
        ObjectHolderRegistry.INSTANCE.registerVanilla(PotionType.class, new ResourceLocation("minecraft:long_water_breathing"), PotionType.class, v -> LONG_WATER_BREATHING = v);
        ObjectHolderRegistry.INSTANCE.registerVanilla(PotionType.class, new ResourceLocation("minecraft:healing"), PotionType.class, v -> HEALING = v);
        ObjectHolderRegistry.INSTANCE.registerVanilla(PotionType.class, new ResourceLocation("minecraft:strong_healing"), PotionType.class, v -> STRONG_HEALING = v);
        ObjectHolderRegistry.INSTANCE.registerVanilla(PotionType.class, new ResourceLocation("minecraft:harming"), PotionType.class, v -> HARMING = v);
        ObjectHolderRegistry.INSTANCE.registerVanilla(PotionType.class, new ResourceLocation("minecraft:strong_harming"), PotionType.class, v -> STRONG_HARMING = v);
        ObjectHolderRegistry.INSTANCE.registerVanilla(PotionType.class, new ResourceLocation("minecraft:poison"), PotionType.class, v -> POISON = v);
        ObjectHolderRegistry.INSTANCE.registerVanilla(PotionType.class, new ResourceLocation("minecraft:long_poison"), PotionType.class, v -> LONG_POISON = v);
        ObjectHolderRegistry.INSTANCE.registerVanilla(PotionType.class, new ResourceLocation("minecraft:strong_poison"), PotionType.class, v -> STRONG_POISON = v);
        ObjectHolderRegistry.INSTANCE.registerVanilla(PotionType.class, new ResourceLocation("minecraft:regeneration"), PotionType.class, v -> REGENERATION = v);
        ObjectHolderRegistry.INSTANCE.registerVanilla(PotionType.class, new ResourceLocation("minecraft:long_regeneration"), PotionType.class, v -> LONG_REGENERATION = v);
        ObjectHolderRegistry.INSTANCE.registerVanilla(PotionType.class, new ResourceLocation("minecraft:strong_regeneration"), PotionType.class, v -> STRONG_REGENERATION = v);
        ObjectHolderRegistry.INSTANCE.registerVanilla(PotionType.class, new ResourceLocation("minecraft:strength"), PotionType.class, v -> STRENGTH = v);
        ObjectHolderRegistry.INSTANCE.registerVanilla(PotionType.class, new ResourceLocation("minecraft:long_strength"), PotionType.class, v -> LONG_STRENGTH = v);
        ObjectHolderRegistry.INSTANCE.registerVanilla(PotionType.class, new ResourceLocation("minecraft:strong_strength"), PotionType.class, v -> STRONG_STRENGTH = v);
        ObjectHolderRegistry.INSTANCE.registerVanilla(PotionType.class, new ResourceLocation("minecraft:weakness"), PotionType.class, v -> WEAKNESS = v);
        ObjectHolderRegistry.INSTANCE.registerVanilla(PotionType.class, new ResourceLocation("minecraft:long_weakness"), PotionType.class, v -> LONG_WEAKNESS = v);
    }
}
