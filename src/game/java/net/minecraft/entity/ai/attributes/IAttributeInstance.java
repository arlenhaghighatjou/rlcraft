package net.minecraft.entity.ai.attributes;

import java.util.Collection;
import net.lax1dude.eaglercraft.v1_8.EaglercraftUUID;
import javax.annotation.Nullable;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public interface IAttributeInstance
{
    IAttribute getAttribute();

    double getBaseValue();

    void setBaseValue(double baseValue);

    Collection<AttributeModifier> getModifiersByOperation(int operation);

    Collection<AttributeModifier> getModifiers();

    boolean hasModifier(AttributeModifier modifier);

    @Nullable
    AttributeModifier getModifier(EaglercraftUUID uuid);

    void applyModifier(AttributeModifier modifier);

    void removeModifier(AttributeModifier modifier);

    void removeModifier(EaglercraftUUID p_188479_1_);

    @SideOnly(Side.CLIENT)
    void removeAllModifiers();

    double getAttributeValue();
}
