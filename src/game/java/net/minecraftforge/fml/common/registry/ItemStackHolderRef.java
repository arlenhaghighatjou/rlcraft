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

package net.minecraftforge.fml.common.registry;

import java.util.function.Consumer;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.FMLLog;

/**
 * Internal class used in tracking item stack holder references
 *
 * @author cpw
 *
 */
class ItemStackHolderRef {
    private String itemName;
    private int meta;
    private String serializednbt;
    private Consumer<ItemStack> setter;

    ItemStackHolderRef(String itemName, int meta, String serializednbt, Consumer<ItemStack> setter)
    {
        this.itemName = itemName;
        this.meta = meta;
        this.serializednbt = serializednbt;
        this.setter = setter;
    }

    public void apply()
    {
        ItemStack is;
        try
        {
            is = GameRegistry.makeItemStack(itemName, meta, 1, serializednbt);
        } catch (RuntimeException e)
        {
            FMLLog.log.error("Caught exception processing itemstack {},{},{}", itemName, meta, serializednbt);
            throw e;
        }
        setter.accept(is);
    }
}
