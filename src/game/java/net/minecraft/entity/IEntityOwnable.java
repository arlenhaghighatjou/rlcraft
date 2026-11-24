package net.minecraft.entity;

import net.lax1dude.eaglercraft.v1_8.EaglercraftUUID;
import javax.annotation.Nullable;

public interface IEntityOwnable
{
    @Nullable
    EaglercraftUUID getOwnerId();

    @Nullable
    Entity getOwner();
}
