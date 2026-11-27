package net.minecraft.init;

import net.minecraftforge.registries.ObjectHolderRegistry;
import javax.annotation.Nullable;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.util.ResourceLocation;

public class Enchantments
{
    public static Enchantment PROTECTION = getRegisteredEnchantment("protection");
    public static Enchantment FIRE_PROTECTION = getRegisteredEnchantment("fire_protection");
    public static Enchantment FEATHER_FALLING = getRegisteredEnchantment("feather_falling");
    public static Enchantment BLAST_PROTECTION = getRegisteredEnchantment("blast_protection");
    public static Enchantment PROJECTILE_PROTECTION = getRegisteredEnchantment("projectile_protection");
    public static Enchantment RESPIRATION = getRegisteredEnchantment("respiration");
    public static Enchantment AQUA_AFFINITY = getRegisteredEnchantment("aqua_affinity");
    public static Enchantment THORNS = getRegisteredEnchantment("thorns");
    public static Enchantment DEPTH_STRIDER = getRegisteredEnchantment("depth_strider");
    public static Enchantment FROST_WALKER = getRegisteredEnchantment("frost_walker");
    public static Enchantment BINDING_CURSE = getRegisteredEnchantment("binding_curse");
    public static Enchantment SHARPNESS = getRegisteredEnchantment("sharpness");
    public static Enchantment SMITE = getRegisteredEnchantment("smite");
    public static Enchantment BANE_OF_ARTHROPODS = getRegisteredEnchantment("bane_of_arthropods");
    public static Enchantment KNOCKBACK = getRegisteredEnchantment("knockback");
    public static Enchantment FIRE_ASPECT = getRegisteredEnchantment("fire_aspect");
    public static Enchantment LOOTING = getRegisteredEnchantment("looting");
    public static Enchantment SWEEPING = getRegisteredEnchantment("sweeping");
    public static Enchantment EFFICIENCY = getRegisteredEnchantment("efficiency");
    public static Enchantment SILK_TOUCH = getRegisteredEnchantment("silk_touch");
    public static Enchantment UNBREAKING = getRegisteredEnchantment("unbreaking");
    public static Enchantment FORTUNE = getRegisteredEnchantment("fortune");
    public static Enchantment POWER = getRegisteredEnchantment("power");
    public static Enchantment PUNCH = getRegisteredEnchantment("punch");
    public static Enchantment FLAME = getRegisteredEnchantment("flame");
    public static Enchantment INFINITY = getRegisteredEnchantment("infinity");
    public static Enchantment LUCK_OF_THE_SEA = getRegisteredEnchantment("luck_of_the_sea");
    public static Enchantment LURE = getRegisteredEnchantment("lure");
    public static Enchantment MENDING = getRegisteredEnchantment("mending");
    public static Enchantment VANISHING_CURSE = getRegisteredEnchantment("vanishing_curse");

    @Nullable
    private static Enchantment getRegisteredEnchantment(String id)
    {
        Enchantment enchantment = Enchantment.REGISTRY.getObject(new ResourceLocation(id));

        if (enchantment == null)
        {
            throw new IllegalStateException("Invalid Enchantment requested: " + id);
        }
        else
        {
            return enchantment;
        }
    }

    static
    {
        if (!Bootstrap.isRegistered())
        {
            throw new RuntimeException("Accessed Enchantments before Bootstrap!");
        }
    }

    public static void registerHolders()
    {
        ObjectHolderRegistry.INSTANCE.registerVanilla(Enchantment.class, new ResourceLocation("minecraft:protection"), Enchantment.class, v -> PROTECTION = v);
        ObjectHolderRegistry.INSTANCE.registerVanilla(Enchantment.class, new ResourceLocation("minecraft:fire_protection"), Enchantment.class, v -> FIRE_PROTECTION = v);
        ObjectHolderRegistry.INSTANCE.registerVanilla(Enchantment.class, new ResourceLocation("minecraft:feather_falling"), Enchantment.class, v -> FEATHER_FALLING = v);
        ObjectHolderRegistry.INSTANCE.registerVanilla(Enchantment.class, new ResourceLocation("minecraft:blast_protection"), Enchantment.class, v -> BLAST_PROTECTION = v);
        ObjectHolderRegistry.INSTANCE.registerVanilla(Enchantment.class, new ResourceLocation("minecraft:projectile_protection"), Enchantment.class, v -> PROJECTILE_PROTECTION = v);
        ObjectHolderRegistry.INSTANCE.registerVanilla(Enchantment.class, new ResourceLocation("minecraft:respiration"), Enchantment.class, v -> RESPIRATION = v);
        ObjectHolderRegistry.INSTANCE.registerVanilla(Enchantment.class, new ResourceLocation("minecraft:aqua_affinity"), Enchantment.class, v -> AQUA_AFFINITY = v);
        ObjectHolderRegistry.INSTANCE.registerVanilla(Enchantment.class, new ResourceLocation("minecraft:thorns"), Enchantment.class, v -> THORNS = v);
        ObjectHolderRegistry.INSTANCE.registerVanilla(Enchantment.class, new ResourceLocation("minecraft:depth_strider"), Enchantment.class, v -> DEPTH_STRIDER = v);
        ObjectHolderRegistry.INSTANCE.registerVanilla(Enchantment.class, new ResourceLocation("minecraft:frost_walker"), Enchantment.class, v -> FROST_WALKER = v);
        ObjectHolderRegistry.INSTANCE.registerVanilla(Enchantment.class, new ResourceLocation("minecraft:binding_curse"), Enchantment.class, v -> BINDING_CURSE = v);
        ObjectHolderRegistry.INSTANCE.registerVanilla(Enchantment.class, new ResourceLocation("minecraft:sharpness"), Enchantment.class, v -> SHARPNESS = v);
        ObjectHolderRegistry.INSTANCE.registerVanilla(Enchantment.class, new ResourceLocation("minecraft:smite"), Enchantment.class, v -> SMITE = v);
        ObjectHolderRegistry.INSTANCE.registerVanilla(Enchantment.class, new ResourceLocation("minecraft:bane_of_arthropods"), Enchantment.class, v -> BANE_OF_ARTHROPODS = v);
        ObjectHolderRegistry.INSTANCE.registerVanilla(Enchantment.class, new ResourceLocation("minecraft:knockback"), Enchantment.class, v -> KNOCKBACK = v);
        ObjectHolderRegistry.INSTANCE.registerVanilla(Enchantment.class, new ResourceLocation("minecraft:fire_aspect"), Enchantment.class, v -> FIRE_ASPECT = v);
        ObjectHolderRegistry.INSTANCE.registerVanilla(Enchantment.class, new ResourceLocation("minecraft:looting"), Enchantment.class, v -> LOOTING = v);
        ObjectHolderRegistry.INSTANCE.registerVanilla(Enchantment.class, new ResourceLocation("minecraft:sweeping"), Enchantment.class, v -> SWEEPING = v);
        ObjectHolderRegistry.INSTANCE.registerVanilla(Enchantment.class, new ResourceLocation("minecraft:efficiency"), Enchantment.class, v -> EFFICIENCY = v);
        ObjectHolderRegistry.INSTANCE.registerVanilla(Enchantment.class, new ResourceLocation("minecraft:silk_touch"), Enchantment.class, v -> SILK_TOUCH = v);
        ObjectHolderRegistry.INSTANCE.registerVanilla(Enchantment.class, new ResourceLocation("minecraft:unbreaking"), Enchantment.class, v -> UNBREAKING = v);
        ObjectHolderRegistry.INSTANCE.registerVanilla(Enchantment.class, new ResourceLocation("minecraft:fortune"), Enchantment.class, v -> FORTUNE = v);
        ObjectHolderRegistry.INSTANCE.registerVanilla(Enchantment.class, new ResourceLocation("minecraft:power"), Enchantment.class, v -> POWER = v);
        ObjectHolderRegistry.INSTANCE.registerVanilla(Enchantment.class, new ResourceLocation("minecraft:punch"), Enchantment.class, v -> PUNCH = v);
        ObjectHolderRegistry.INSTANCE.registerVanilla(Enchantment.class, new ResourceLocation("minecraft:flame"), Enchantment.class, v -> FLAME = v);
        ObjectHolderRegistry.INSTANCE.registerVanilla(Enchantment.class, new ResourceLocation("minecraft:infinity"), Enchantment.class, v -> INFINITY = v);
        ObjectHolderRegistry.INSTANCE.registerVanilla(Enchantment.class, new ResourceLocation("minecraft:luck_of_the_sea"), Enchantment.class, v -> LUCK_OF_THE_SEA = v);
        ObjectHolderRegistry.INSTANCE.registerVanilla(Enchantment.class, new ResourceLocation("minecraft:lure"), Enchantment.class, v -> LURE = v);
        ObjectHolderRegistry.INSTANCE.registerVanilla(Enchantment.class, new ResourceLocation("minecraft:mending"), Enchantment.class, v -> MENDING = v);
        ObjectHolderRegistry.INSTANCE.registerVanilla(Enchantment.class, new ResourceLocation("minecraft:vanishing_curse"), Enchantment.class, v -> VANISHING_CURSE = v);
    }
}
