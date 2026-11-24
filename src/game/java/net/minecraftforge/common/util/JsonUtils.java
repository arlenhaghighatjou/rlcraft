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

package net.minecraftforge.common.util;


import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTException;
import net.minecraft.nbt.NBTTagCompound;

import javax.annotation.Nullable;

public class JsonUtils
{
    @Nullable
    public static NBTTagCompound readNBT(JsonObject json, String key)
    {
        if (net.minecraft.util.JsonUtils.hasField(json, key))
        {
            try
            {
                return JsonToNBT.getTagFromJson(net.minecraft.util.JsonUtils.getString(json, key));
            } catch (NBTException e)
            {
                throw new JsonSyntaxException("Malformed NBT tag", e);
            }
        } else
        {
            return null;
        }
    }
}
