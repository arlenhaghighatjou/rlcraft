/*
 * Minecraft Forge
 * Copyright (c) 2016-2020.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation version 2.1
 * of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */

package net.minecraftforge.common;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import java.util.ArrayList;
import java.util.List;

public final class EnumPlantType
{
    private static final List<EnumPlantType> VALUES = new ArrayList<>();
    public static final EnumPlantType Plains = new EnumPlantType("Plains");
    public static final EnumPlantType Desert = new EnumPlantType("Desert");
    public static final EnumPlantType Beach = new EnumPlantType("Beach");
    public static final EnumPlantType Cave = new EnumPlantType("Cave");
    public static final EnumPlantType Water = new EnumPlantType("Water");
    public static final EnumPlantType Nether = new EnumPlantType("Nether");
    public static final EnumPlantType Crop = new EnumPlantType("Crop");

    private final String name;
    private final int ordinal;

    private EnumPlantType(String name)
    {
        this.name = name;
        this.ordinal = VALUES.size();
        VALUES.add(this);
    }

    public static EnumPlantType valueOf(String name)
    {
        java.util.Objects.requireNonNull(name, "name");
        for (int i = 0, l = VALUES.size(); i < l; ++i)
        {
            EnumPlantType t = VALUES.get(i);
            if (t.name.equals(name)) return t;
        }
        throw new IllegalArgumentException("No enum constant net.minecraftforge.common.EnumPlantType." + name);
    }

    public static EnumPlantType[] values()
    {
        return VALUES.toArray(new EnumPlantType[VALUES.size()]);
    }

    public String name()
    {
        return this.name;
    }

    public int ordinal()
    {
        return this.ordinal;
    }

    @Override
    public String toString()
    {
        return this.name;
    }

    /**
     * Getting a custom {@link EnumPlantType}, or an existing one if it has the same name as that one. Your plant should implement {@link IPlantable}
     * and return this custom type in {@link IPlantable#getPlantType(IBlockAccess, BlockPos)}.
     * 
     * <p>If your new plant grows on blocks like any one of them above, never create a new {@link EnumPlantType}. 
     * This enumeration is only functioning in 
     * {@link net.minecraft.block.Block#canSustainPlant(IBlockState, IBlockAccess, BlockPos, EnumFacing, IPlantable)},
     * which you are supposed to override this function in your new block and create a new plant type to grow on that block.
     * 
     * <p>You can create an instance of your plant type in your API and let your/others mods access it. It will be faster than calling this method.
     * @param name the name of the type of plant, you had better follow the style above
     * @return the acquired {@link EnumPlantType}, a new one if not found.
     */
    public static EnumPlantType getPlantType(String name) 
    {
        for (int i = 0, l = VALUES.size(); i < l; ++i)
        {
            EnumPlantType t = VALUES.get(i);
            if (t.name.equalsIgnoreCase(name)) return t;
        }
        return new EnumPlantType(name);
    }
}
