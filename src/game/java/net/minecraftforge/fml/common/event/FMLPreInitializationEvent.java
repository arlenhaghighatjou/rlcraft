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

import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.common.ModMetadata;
import net.minecraftforge.fml.common.LoaderState.ModState;

import net.lax1dude.eaglercraft.v1_8.log4j.LogManager;
import net.lax1dude.eaglercraft.v1_8.log4j.Logger;
import net.lax1dude.eaglercraft.v1_8.internal.vfs2.VFile2;

/**
 * Called before {@link FMLInitializationEvent} during mod startup.
 *
 * This is the first of three commonly called events during mod initialization.
 *
 * Recommended activities:
 * Setup your logging {@link #getModLog()}
 * Load any configuration data you might have {@link #getSuggestedConfigurationFile()}
 * Search for a version.properties file and load it {@link #getVersionProperties()}
 * Configure your {@link ModMetadata} programmatically {@link #getModMetadata()}
 * Discover parts of your mod by using annotation search {@link #getAsmData()}
 *
 * @see net.minecraftforge.fml.common.Mod.EventHandler for how to subscribe to this event
 * @author cpw
 */
public class FMLPreInitializationEvent extends FMLStateEvent
{
    private ModMetadata modMetadata;
    private VFile2 configurationDir;
    private VFile2 suggestedConfigFile;
    private ModContainer modContainer;

    public FMLPreInitializationEvent(Object... data)
    {
        super(data);
        this.configurationDir = (VFile2)data[0];
    }

    @Override
    public ModState getModState()
    {
        return ModState.PREINITIALIZED;
    }

    @Override
    public void applyModContainer(ModContainer activeContainer)
    {
        this.modContainer = activeContainer;
        this.modMetadata = activeContainer.getMetadata();
        this.suggestedConfigFile = new VFile2(configurationDir, activeContainer.getModId()+".cfg");
    }

    public ModMetadata getModMetadata()
    {
        return modMetadata;
    }

    public VFile2 getModConfigurationDirectory()
    {
        return configurationDir;
    }

    public VFile2 getSuggestedConfigurationFile()
    {
        return suggestedConfigFile;
    }

    public Logger getModLog()
    {
        return LogManager.getLogger(modContainer.getModId());
    }
}
