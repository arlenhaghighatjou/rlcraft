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

package net.minecraftforge.fml.client;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.lax1dude.eaglercraft.v1_8.EagRuntime;
import net.lax1dude.eaglercraft.v1_8.EaglerInputStream;
import net.lax1dude.eaglercraft.v1_8.minecraft.ResourceIndex;
import net.lax1dude.eaglercraft.v1_8.opengl.ImageData;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.resources.AbstractResourcePack;
import net.minecraftforge.fml.common.ModContainer;

public class FMLFolderResourcePack extends AbstractResourcePack
{
    private final ModContainer container;
    private final String folder;
    private final String assetsPrefix;
    private Set<String> domains;

    public FMLFolderResourcePack(ModContainer container, String folder)
    {
        super(folder);
        this.container = container;
        this.folder = folder;
        this.assetsPrefix = folder + "/assets/";
        this.resourceIndex = new ResourceIndex()
        {
            @Override
            protected Collection<String> getPropertiesFiles0()
            {
                return Collections.emptyList();
            }

            @Override
            protected Collection<String> getCITPotionsFiles0()
            {
                return Collections.emptyList();
            }
        };
    }

    @Override
    protected InputStream getInputStreamByName(String resourceName) throws IOException
    {
        InputStream stream = EagRuntime.getResourceStream("/" + folder + "/" + resourceName);
        if (stream != null)
        {
            return stream;
        }
        if ("pack.mcmeta".equals(resourceName))
        {
            return new EaglerInputStream(("{\"pack\":{\"description\":\"dummy FML pack for " + container.getName() + "\",\"pack_format\":2}}").getBytes(StandardCharsets.UTF_8));
        }
        throw new FileNotFoundException(folder + "/" + resourceName);
    }

    @Override
    protected boolean hasResourceName(String resourceName)
    {
        return EagRuntime.getResourceExists("/" + folder + "/" + resourceName);
    }

    @Override
    public Set<String> getResourceDomains()
    {
        if (domains == null)
        {
            domains = new HashSet<>();
            List<String> resources = EagRuntime.listResources(assetsPrefix);
            for (int i = 0, l = resources.size(); i < l; ++i)
            {
                String path = resources.get(i);
                int end = path.indexOf('/', assetsPrefix.length());
                if (end != -1)
                {
                    domains.add(path.substring(assetsPrefix.length(), end));
                }
            }
        }
        return domains;
    }

    @Override
    public ImageData getPackImage() throws IOException
    {
        String logoFile = container.getMetadata().logoFile;
        return TextureUtil.readBufferedImage(getInputStreamByName(logoFile.startsWith("/") ? logoFile.substring(1) : logoFile));
    }

    @Override
    public String getPackName()
    {
        return container.getName();
    }
}
