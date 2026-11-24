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

package net.minecraftforge.fml.common.event;

import net.minecraftforge.fml.common.LoaderState.ModState;

import com.google.common.collect.ListMultimap;

/**
 * An internal FML event used to signal the construction of mods. Should not be used by mods.
 */
public class FMLConstructionEvent extends FMLStateEvent
{
    private ListMultimap<String,String> reverseDependencies;

    @SuppressWarnings("unchecked")
    public FMLConstructionEvent(Object... eventData)
    {
        this.reverseDependencies = (ListMultimap<String, String>) eventData[0];
    }
    @Override
    public ModState getModState()
    {
        return ModState.CONSTRUCTED;
    }
    public ListMultimap<String, String> getReverseDependencies()
    {
        return reverseDependencies;
    }
}
