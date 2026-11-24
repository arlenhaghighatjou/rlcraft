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

package net.minecraftforge.fml.common;

import net.minecraftforge.fml.common.event.FMLConstructionEvent;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLLoadCompleteEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerAboutToStartEvent;
import net.minecraftforge.fml.common.event.FMLServerStartedEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.event.FMLServerStoppedEvent;
import net.minecraftforge.fml.common.event.FMLServerStoppingEvent;
import net.minecraftforge.fml.common.event.FMLStateEvent;

import java.util.function.Function;

/**
 * The state enum used to help track state progression for the loader
 * @author cpw
 *
 */
public enum LoaderState
{
    NOINIT("Uninitialized",null),
    LOADING("Loading",null),
    CONSTRUCTING("Constructing mods",FMLConstructionEvent::new),
    PREINITIALIZATION("Pre-initializing mods", FMLPreInitializationEvent::new),
    INITIALIZATION("Initializing mods", FMLInitializationEvent::new),
    POSTINITIALIZATION("Post-initializing mods", FMLPostInitializationEvent::new),
    AVAILABLE("Mod loading complete", FMLLoadCompleteEvent::new),
    SERVER_ABOUT_TO_START("Server about to start", FMLServerAboutToStartEvent::new),
    SERVER_STARTING("Server starting", FMLServerStartingEvent::new),
    SERVER_STARTED("Server started", FMLServerStartedEvent::new),
    SERVER_STOPPING("Server stopping", FMLServerStoppingEvent::new),
    SERVER_STOPPED("Server stopped", FMLServerStoppedEvent::new),
    ERRORED("Mod Loading errored",null);


    private Function<Object[], FMLStateEvent> eventFactory;
    private String name;

    private LoaderState(String name, Function<Object[], FMLStateEvent> event)
    {
        this.name = name;
        this.eventFactory = event;
    }

    public LoaderState transition(boolean errored)
    {
        if (errored)
        {
            return ERRORED;
        }
        // stopping -> available
        if (this == SERVER_STOPPED)
        {
            return AVAILABLE;
        }
        return values()[ordinal() < values().length ? ordinal()+1 : ordinal()];
    }

    public boolean hasEvent()
    {
        return eventFactory != null;
    }

    public FMLStateEvent getEvent(Object... eventData)
    {
        return eventFactory.apply(eventData);
    }
    public LoaderState requiredState()
    {
        if (this == NOINIT) return NOINIT;
        return LoaderState.values()[this.ordinal()-1];
    }
    
    public String getPrettyName()
    {
        return name;
    }
    
    public enum ModState
    {
        UNLOADED       ("Unloaded",         "U"),
        LOADED         ("Loaded",           "L"),
        CONSTRUCTED    ("Constructed",      "C"),
        PREINITIALIZED ("Pre-initialized",  "H"),
        INITIALIZED    ("Initialized",      "I"),
        POSTINITIALIZED("Post-initialized", "J"),
        AVAILABLE      ("Available",        "A"),
        DISABLED       ("Disabled",         "D"),
        ERRORED        ("Errored",          "E");

        private String label;
        private String marker;

        private ModState(String label, String marker)
        {
            this.label = label;
            this.marker = marker;
        }

        @Override
        public String toString()
        {
            return this.label;
        }

        public String getMarker()
        {
            return this.marker;
        }
    }
}
