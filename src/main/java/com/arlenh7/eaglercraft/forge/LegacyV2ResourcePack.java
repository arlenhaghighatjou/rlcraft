package com.arlenh7.eaglercraft.forge;

import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;
import java.util.Set;

import net.lax1dude.eaglercraft.v1_8.opengl.ImageData;
import net.lax1dude.eaglercraft.v1_8.minecraft.ResourceIndex;
import net.minecraft.client.resources.IResourcePack;
import net.minecraft.client.resources.data.IMetadataSection;
import net.minecraft.client.resources.data.MetadataSerializer;
import net.minecraft.util.ResourceLocation;

public final class LegacyV2ResourcePack implements IResourcePack
{
	private final IResourcePack pack;

	public LegacyV2ResourcePack(IResourcePack pack)
	{
		this.pack = pack;
	}

	private ResourceLocation legacyLanguage(ResourceLocation location)
	{
		String path = location.getResourcePath();
		if (!path.startsWith("lang/") || !path.endsWith(".lang") || path.equals("lang/swg_de.lang"))
		{
			return location;
		}

		int regionStart = path.indexOf('_', "lang/".length()) + 1;
		if (regionStart == 0) return location;
		String legacyPath = path.substring(0, regionStart)
				+ path.substring(regionStart, path.length() - ".lang".length()).toUpperCase(Locale.ROOT)
				+ ".lang";
		return new ResourceLocation(location.getResourceDomain(), path)
		{
			@Override
			public String getResourcePath()
			{
				return legacyPath;
			}
		};
	}

	@Override
	public InputStream getInputStream(ResourceLocation location) throws IOException
	{
		return this.pack.getInputStream(legacyLanguage(location));
	}

	@Override
	public boolean resourceExists(ResourceLocation location)
	{
		return this.pack.resourceExists(legacyLanguage(location));
	}

	@Override
	public Set<String> getResourceDomains()
	{
		return this.pack.getResourceDomains();
	}

	@Override
	public <T extends IMetadataSection> T getPackMetadata(MetadataSerializer serializer, String name) throws IOException
	{
		return this.pack.getPackMetadata(serializer, name);
	}

	@Override
	public ImageData getPackImage() throws IOException
	{
		return this.pack.getPackImage();
	}

	@Override
	public String getPackName()
	{
		return this.pack.getPackName();
	}

	@Override
	public ResourceIndex getEaglerFileIndex()
	{
		return this.pack.getEaglerFileIndex();
	}
}
