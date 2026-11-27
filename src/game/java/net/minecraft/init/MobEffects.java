package net.minecraft.init;

import net.minecraftforge.registries.ObjectHolderRegistry;
import javax.annotation.Nullable;
import net.minecraft.potion.Potion;
import net.minecraft.util.ResourceLocation;

public class MobEffects
{
    public static Potion SPEED;
    public static Potion SLOWNESS;
    public static Potion HASTE;
    public static Potion MINING_FATIGUE;
    public static Potion STRENGTH;
    public static Potion INSTANT_HEALTH;
    public static Potion INSTANT_DAMAGE;
    public static Potion JUMP_BOOST;
    public static Potion NAUSEA;
    public static Potion REGENERATION;
    public static Potion RESISTANCE;
    public static Potion FIRE_RESISTANCE;
    public static Potion WATER_BREATHING;
    public static Potion INVISIBILITY;
    public static Potion BLINDNESS;
    public static Potion NIGHT_VISION;
    public static Potion HUNGER;
    public static Potion WEAKNESS;
    public static Potion POISON;
    public static Potion WITHER;
    public static Potion HEALTH_BOOST;
    public static Potion ABSORPTION;
    public static Potion SATURATION;
    public static Potion GLOWING;
    public static Potion LEVITATION;
    public static Potion LUCK;
    public static Potion UNLUCK;

    @Nullable
    private static Potion getRegisteredMobEffect(String id)
    {
        Potion potion = Potion.REGISTRY.getObject(new ResourceLocation(id));

        if (potion == null)
        {
            throw new IllegalStateException("Invalid MobEffect requested: " + id);
        }
        else
        {
            return potion;
        }
    }

    static
    {
        if (!Bootstrap.isRegistered())
        {
            throw new RuntimeException("Accessed MobEffects before Bootstrap!");
        }
        else
        {
            SPEED = getRegisteredMobEffect("speed");
            SLOWNESS = getRegisteredMobEffect("slowness");
            HASTE = getRegisteredMobEffect("haste");
            MINING_FATIGUE = getRegisteredMobEffect("mining_fatigue");
            STRENGTH = getRegisteredMobEffect("strength");
            INSTANT_HEALTH = getRegisteredMobEffect("instant_health");
            INSTANT_DAMAGE = getRegisteredMobEffect("instant_damage");
            JUMP_BOOST = getRegisteredMobEffect("jump_boost");
            NAUSEA = getRegisteredMobEffect("nausea");
            REGENERATION = getRegisteredMobEffect("regeneration");
            RESISTANCE = getRegisteredMobEffect("resistance");
            FIRE_RESISTANCE = getRegisteredMobEffect("fire_resistance");
            WATER_BREATHING = getRegisteredMobEffect("water_breathing");
            INVISIBILITY = getRegisteredMobEffect("invisibility");
            BLINDNESS = getRegisteredMobEffect("blindness");
            NIGHT_VISION = getRegisteredMobEffect("night_vision");
            HUNGER = getRegisteredMobEffect("hunger");
            WEAKNESS = getRegisteredMobEffect("weakness");
            POISON = getRegisteredMobEffect("poison");
            WITHER = getRegisteredMobEffect("wither");
            HEALTH_BOOST = getRegisteredMobEffect("health_boost");
            ABSORPTION = getRegisteredMobEffect("absorption");
            SATURATION = getRegisteredMobEffect("saturation");
            GLOWING = getRegisteredMobEffect("glowing");
            LEVITATION = getRegisteredMobEffect("levitation");
            LUCK = getRegisteredMobEffect("luck");
            UNLUCK = getRegisteredMobEffect("unluck");
        }
    }

    public static void registerHolders()
    {
        ObjectHolderRegistry.INSTANCE.registerVanilla(Potion.class, new ResourceLocation("minecraft:speed"), Potion.class, v -> SPEED = v);
        ObjectHolderRegistry.INSTANCE.registerVanilla(Potion.class, new ResourceLocation("minecraft:slowness"), Potion.class, v -> SLOWNESS = v);
        ObjectHolderRegistry.INSTANCE.registerVanilla(Potion.class, new ResourceLocation("minecraft:haste"), Potion.class, v -> HASTE = v);
        ObjectHolderRegistry.INSTANCE.registerVanilla(Potion.class, new ResourceLocation("minecraft:mining_fatigue"), Potion.class, v -> MINING_FATIGUE = v);
        ObjectHolderRegistry.INSTANCE.registerVanilla(Potion.class, new ResourceLocation("minecraft:strength"), Potion.class, v -> STRENGTH = v);
        ObjectHolderRegistry.INSTANCE.registerVanilla(Potion.class, new ResourceLocation("minecraft:instant_health"), Potion.class, v -> INSTANT_HEALTH = v);
        ObjectHolderRegistry.INSTANCE.registerVanilla(Potion.class, new ResourceLocation("minecraft:instant_damage"), Potion.class, v -> INSTANT_DAMAGE = v);
        ObjectHolderRegistry.INSTANCE.registerVanilla(Potion.class, new ResourceLocation("minecraft:jump_boost"), Potion.class, v -> JUMP_BOOST = v);
        ObjectHolderRegistry.INSTANCE.registerVanilla(Potion.class, new ResourceLocation("minecraft:nausea"), Potion.class, v -> NAUSEA = v);
        ObjectHolderRegistry.INSTANCE.registerVanilla(Potion.class, new ResourceLocation("minecraft:regeneration"), Potion.class, v -> REGENERATION = v);
        ObjectHolderRegistry.INSTANCE.registerVanilla(Potion.class, new ResourceLocation("minecraft:resistance"), Potion.class, v -> RESISTANCE = v);
        ObjectHolderRegistry.INSTANCE.registerVanilla(Potion.class, new ResourceLocation("minecraft:fire_resistance"), Potion.class, v -> FIRE_RESISTANCE = v);
        ObjectHolderRegistry.INSTANCE.registerVanilla(Potion.class, new ResourceLocation("minecraft:water_breathing"), Potion.class, v -> WATER_BREATHING = v);
        ObjectHolderRegistry.INSTANCE.registerVanilla(Potion.class, new ResourceLocation("minecraft:invisibility"), Potion.class, v -> INVISIBILITY = v);
        ObjectHolderRegistry.INSTANCE.registerVanilla(Potion.class, new ResourceLocation("minecraft:blindness"), Potion.class, v -> BLINDNESS = v);
        ObjectHolderRegistry.INSTANCE.registerVanilla(Potion.class, new ResourceLocation("minecraft:night_vision"), Potion.class, v -> NIGHT_VISION = v);
        ObjectHolderRegistry.INSTANCE.registerVanilla(Potion.class, new ResourceLocation("minecraft:hunger"), Potion.class, v -> HUNGER = v);
        ObjectHolderRegistry.INSTANCE.registerVanilla(Potion.class, new ResourceLocation("minecraft:weakness"), Potion.class, v -> WEAKNESS = v);
        ObjectHolderRegistry.INSTANCE.registerVanilla(Potion.class, new ResourceLocation("minecraft:poison"), Potion.class, v -> POISON = v);
        ObjectHolderRegistry.INSTANCE.registerVanilla(Potion.class, new ResourceLocation("minecraft:wither"), Potion.class, v -> WITHER = v);
        ObjectHolderRegistry.INSTANCE.registerVanilla(Potion.class, new ResourceLocation("minecraft:health_boost"), Potion.class, v -> HEALTH_BOOST = v);
        ObjectHolderRegistry.INSTANCE.registerVanilla(Potion.class, new ResourceLocation("minecraft:absorption"), Potion.class, v -> ABSORPTION = v);
        ObjectHolderRegistry.INSTANCE.registerVanilla(Potion.class, new ResourceLocation("minecraft:saturation"), Potion.class, v -> SATURATION = v);
        ObjectHolderRegistry.INSTANCE.registerVanilla(Potion.class, new ResourceLocation("minecraft:glowing"), Potion.class, v -> GLOWING = v);
        ObjectHolderRegistry.INSTANCE.registerVanilla(Potion.class, new ResourceLocation("minecraft:levitation"), Potion.class, v -> LEVITATION = v);
        ObjectHolderRegistry.INSTANCE.registerVanilla(Potion.class, new ResourceLocation("minecraft:luck"), Potion.class, v -> LUCK = v);
        ObjectHolderRegistry.INSTANCE.registerVanilla(Potion.class, new ResourceLocation("minecraft:unluck"), Potion.class, v -> UNLUCK = v);
    }
}
