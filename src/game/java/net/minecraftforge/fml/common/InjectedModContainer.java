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

import java.util.List;
import java.util.Map;
import java.util.Set;

import net.minecraftforge.fml.common.versioning.ArtifactVersion;
import net.minecraftforge.fml.common.versioning.VersionRange;
import net.minecraftforge.fml.client.IModGuiFactory;
import net.minecraftforge.fml.common.event.FMLEvent;

import javax.annotation.Nullable;

public class InjectedModContainer implements ModContainer
{
    public final ModContainer wrappedContainer;

    public InjectedModContainer(ModContainer mc)
    {
        this.wrappedContainer = mc;
    }

    @Override
    public String getModId()
    {
        return wrappedContainer.getModId();
    }

    @Override
    public String getName()
    {
        return wrappedContainer.getName();
    }

    @Override
    public String getVersion()
    {
        return wrappedContainer.getVersion();
    }

    @Override
    public ModMetadata getMetadata()
    {
        return wrappedContainer.getMetadata();
    }

    @Override
    public void bindMetadata(MetadataCollection mc)
    {
        wrappedContainer.bindMetadata(mc);
    }

    @Override
    public void setEnabledState(boolean enabled)
    {
        wrappedContainer.setEnabledState(enabled);
    }

    @Override
    public Set<ArtifactVersion> getRequirements()
    {
        return wrappedContainer.getRequirements();
    }

    @Override
    public List<ArtifactVersion> getDependencies()
    {
        return wrappedContainer.getDependencies();
    }

    @Override
    public List<ArtifactVersion> getDependants()
    {
        return wrappedContainer.getDependants();
    }

    @Override
    public String getSortingRules()
    {
        return wrappedContainer.getSortingRules();
    }

    @Override
    public boolean registerBus(LoadController controller)
    {
        return wrappedContainer.registerBus(controller);
    }

    @Override
    public void handleModStateEvent(FMLEvent event)
    {
        wrappedContainer.handleModStateEvent(event);
    }

    @Override
    public boolean matches(Object mod)
    {
        return wrappedContainer.matches(mod);
    }

    @Override
    public Object getMod()
    {
        return wrappedContainer.getMod();
    }

    @Override
    public ArtifactVersion getProcessedVersion()
    {
        return wrappedContainer.getProcessedVersion();
    }

    @Override
    public boolean isImmutable()
    {
        return true;
    }

    @Override
    public String getDisplayVersion()
    {
        return wrappedContainer.getDisplayVersion();
    }

    @Override
    public VersionRange acceptableMinecraftVersionRange()
    {
        return wrappedContainer.acceptableMinecraftVersionRange();
    }

    @Nullable
    public WorldAccessContainer getWrappedWorldAccessContainer()
    {
        if (wrappedContainer instanceof WorldAccessContainer)
        {
            return (WorldAccessContainer) wrappedContainer;
        }
        else
        {
            return null;
        }
    }

    @Override
    public String toString()
    {
        return "Wrapped{"+wrappedContainer.toString()+"}";
    }

    @Override
    public Map<String, String> getCustomModProperties()
    {
        return wrappedContainer.getCustomModProperties();
    }

    @Override
    public Map<String, String> getSharedModDescriptor()
    {
        return wrappedContainer.getSharedModDescriptor();
    }

    @Override
    public Disableable canBeDisabled()
    {
        return wrappedContainer.canBeDisabled();
    }

    @Override
    public IModGuiFactory getGuiFactory()
    {
        return wrappedContainer.getGuiFactory();
    }

    @Override
    public net.minecraft.client.resources.IResourcePack createResourcePack()
    {
        return wrappedContainer.createResourcePack();
    }

    @Override
    public java.io.File getSource()
    {
        return wrappedContainer.getSource();
    }

    @Override
    public boolean shouldLoadInEnvironment()
    {
        return true;
    }

}
