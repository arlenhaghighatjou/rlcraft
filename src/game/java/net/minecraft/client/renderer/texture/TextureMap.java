package net.minecraft.client.renderer.texture;

import static net.lax1dude.eaglercraft.v1_8.internal.PlatformOpenGL.*;

import net.lax1dude.eaglercraft.v1_8.minecraft.EaglerTextureAtlasSprite;
import net.lax1dude.eaglercraft.v1_8.internal.IFramebufferGL;
import net.lax1dude.eaglercraft.v1_8.opengl.EaglercraftGPU;
import net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager;
import net.lax1dude.eaglercraft.v1_8.opengl.ImageData;
import net.lax1dude.eaglercraft.v1_8.opengl.ext.deferred.texture.EaglerTextureAtlasSpritePBR;
import net.lax1dude.eaglercraft.v1_8.opengl.ext.deferred.texture.PBRTextureMapUtils;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.StitcherException;
import net.minecraft.client.resources.IResource;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.crash.ICrashReportDetail;
import net.minecraft.util.ReportedException;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.lax1dude.eaglercraft.v1_8.log4j.LogManager;
import net.lax1dude.eaglercraft.v1_8.log4j.Logger;

@SideOnly(Side.CLIENT)
public class TextureMap extends AbstractTexture implements ITickableTextureObject
{
    private static final Logger LOGGER = LogManager.getLogger();
    public static final ResourceLocation LOCATION_MISSING_TEXTURE = new ResourceLocation("missingno");
    public static final ResourceLocation LOCATION_BLOCKS_TEXTURE = new ResourceLocation("textures/atlas/blocks.png");
    private final List<EaglerTextureAtlasSprite> listAnimatedSprites;
    private final Map<String, EaglerTextureAtlasSprite> mapRegisteredSprites;
    private final Map<String, EaglerTextureAtlasSprite> mapUploadedSprites;
    private final String basePath;
    private final ITextureMapPopulator iconCreator;
    private int mipmapLevels;
    private final EaglerTextureAtlasSprite missingImage;
	private boolean eaglerPBRMode;
	public int eaglerPBRMaterialTexture = -1;
	private static final int FRAMEBUFFER = 0x8D40;
	private static final int COLOR_ATTACHMENT = 0x8CE0;
	private static final int TEXTURE_2D = 0x0DE1;
	private IFramebufferGL[] animationTargets;
	private IFramebufferGL[] materialAnimationTargets;
	private int width;
	private int height;

    public TextureMap(String basePathIn)
    {
        this(basePathIn, (ITextureMapPopulator)null);
    }

    public TextureMap(String basePathIn, @Nullable ITextureMapPopulator iconCreatorIn)
    {
        this(basePathIn, iconCreatorIn, false);
    }

    public TextureMap(String basePathIn, boolean skipFirst)
    {
        this(basePathIn, null, skipFirst);
    }

    public TextureMap(String basePathIn, @Nullable ITextureMapPopulator iconCreatorIn, boolean skipFirst)
    {
        this.listAnimatedSprites = Lists.<EaglerTextureAtlasSprite>newArrayList();
        this.mapRegisteredSprites = Maps.<String, EaglerTextureAtlasSprite>newHashMap();
        this.mapUploadedSprites = Maps.<String, EaglerTextureAtlasSprite>newHashMap();
        this.missingImage = new EaglerTextureAtlasSprite("missingno");
		this.missingImage.setIndexInMap(0);
        this.basePath = basePathIn;
        this.iconCreator = iconCreatorIn;
    }

    private void initMissingImage()
    {
        int[] aint = TextureUtil.missingTextureData;
        this.missingImage.setIconWidth(16);
        this.missingImage.setIconHeight(16);
        int[][] aint1 = new int[this.mipmapLevels + 1][];
        aint1[0] = aint;
        this.missingImage.setFramesTextureData(Lists.<int[][]>newArrayList(aint1));
    }

    public void loadTexture(IResourceManager resourceManager) throws IOException
    {
        if (this.iconCreator != null)
        {
            this.loadSprites(resourceManager, this.iconCreator);
        }
    }

    public void loadSprites(IResourceManager resourceManager, ITextureMapPopulator iconCreatorIn)
    {
		for (EaglerTextureAtlasSprite sprite : this.listAnimatedSprites) sprite.clearFramesTextureData();
        this.mapRegisteredSprites.clear();
        net.minecraftforge.client.ForgeHooksClient.onTextureStitchedPre(this);
        iconCreatorIn.registerSprites(this);
		if (this == Minecraft.getMinecraft().getTextureMapBlocks())
		{
			net.optifine.ConnectedTextures.updateIcons(this);
			net.optifine.CustomItems.updateIcons(this);
			net.optifine.BetterGrass.updateIcons(this);
		}
        this.initMissingImage();
        this.deleteGlTexture();
        this.loadTextureAtlas(resourceManager);
    }

    public void loadTextureAtlas(IResourceManager resourceManager)
    {
        int i = Minecraft.getGLMaximumTextureSize();
        Stitcher stitcher = new Stitcher(i, this.eaglerPBRMode ? i >> 1 : i, 0, this.mipmapLevels);
        this.mapUploadedSprites.clear();
        this.listAnimatedSprites.clear();
        int j = Integer.MAX_VALUE;
        int k = 1 << this.mipmapLevels;
        net.minecraftforge.fml.common.FMLLog.log.info("Max texture size: {}", i);
        net.minecraftforge.fml.common.ProgressManager.ProgressBar bar = net.minecraftforge.fml.common.ProgressManager.push("Texture stitching", this.mapRegisteredSprites.size());
        loadedSprites.clear();

        for (Entry<String, EaglerTextureAtlasSprite> entry : Maps.newHashMap(this.mapRegisteredSprites).entrySet())
        {
            final ResourceLocation location = new ResourceLocation(entry.getKey());
            bar.step(location.toString());
            j = loadTexture(stitcher, resourceManager, location, entry.getValue(), bar, j, k);
        }
        finishLoading(stitcher, bar, j, k);
    }

    private int loadTexture(Stitcher stitcher, IResourceManager resourceManager, ResourceLocation location, EaglerTextureAtlasSprite textureatlassprite, net.minecraftforge.fml.common.ProgressManager.ProgressBar bar, int j, int k)
    {
        if (loadedSprites.contains(location)) {
            return j;
        }
        ResourceLocation resourcelocation = this.getResourceLocation(textureatlassprite);

        for(ResourceLocation loading : loadingSprites)
        {
            if(location.equals(loading))
            {
                final String error = "circular texture dependencies, stack: [" + com.google.common.base.Joiner.on(", ").join(loadingSprites) + "]";
                net.minecraftforge.fml.client.FMLClientHandler.instance().trackBrokenTexture(resourcelocation, error);
                return j;
            }
        }
        loadingSprites.addLast(location);
        try
        {
            for (ResourceLocation dependency : textureatlassprite.getDependencies())
            {
                if (!mapRegisteredSprites.containsKey(dependency.toString()))
                {
                    registerSprite(dependency);
                }
                EaglerTextureAtlasSprite depSprite = mapRegisteredSprites.get(dependency.toString());
                j = loadTexture(stitcher, resourceManager, dependency, depSprite, bar, j, k);
            }
            try
            {
            if (textureatlassprite.hasCustomLoader(resourceManager, resourcelocation))
            {
                if (textureatlassprite.load(resourceManager, resourcelocation, l -> mapRegisteredSprites.get(l.toString())))
                {
                    return j;
                }
            }
            else
            {
				try (IResource resource = resourceManager.getResource(resourcelocation))
				{
					ImageData image = TextureUtil.readBufferedImage(resource.getInputStream());
					ImageData[] levels = new ImageData[this.mipmapLevels + 1];
					levels[0] = image;
					if (textureatlassprite instanceof EaglerTextureAtlasSpritePBR)
					{
						ImageData[] normals = new ImageData[levels.length];
						ImageData[] materials = new ImageData[levels.length];
						normals[0] = PBRTextureMapUtils.locateCompanionTexture(resourceManager, resource, "_n");
						materials[0] = PBRTextureMapUtils.locateCompanionTexture(resourceManager, resource, "_s");
						boolean staticNormals = normals[0] == null;
						boolean staticMaterials = materials[0] == null;
						if (staticNormals) normals[0] = PBRTextureMapUtils.defaultNormalsTexture;
						if (staticMaterials) materials[0] = PBRTextureMapUtils.generateMaterialTextureFor(
								textureatlassprite.getIconName(), textureatlassprite.optifineBaseTextureName);
						PBRTextureMapUtils.unifySizes(0, levels, normals, materials);
						textureatlassprite.loadSpritePBR(new ImageData[][] { levels, normals, materials },
								resource.getMetadata("animation"), staticNormals, staticMaterials);
					}
					else
					{
						textureatlassprite.loadSprite(levels, resource.getMetadata("animation"));
					}
				}
            }
            }
            catch (RuntimeException runtimeexception)
            {
                net.minecraftforge.fml.client.FMLClientHandler.instance().trackBrokenTexture(resourcelocation, runtimeexception.getMessage());
                return j;
            }
            catch (IOException ioexception)
            {
                net.minecraftforge.fml.client.FMLClientHandler.instance().trackMissingTexture(resourcelocation);
                return j;
            }

            j = Math.min(j, Math.min(textureatlassprite.getIconWidth(), textureatlassprite.getIconHeight()));
            int j1 = Math.min(Integer.lowestOneBit(textureatlassprite.getIconWidth()), Integer.lowestOneBit(textureatlassprite.getIconHeight()));

            if (j1 < k)
            {
                // FORGE: do not lower the mipmap level, just log the problematic textures
                LOGGER.warn("Texture {} with size {}x{} will have visual artifacts at mip level {}, it can only support level {}. Please report to the mod author that the texture should be some multiple of 16x16.", resourcelocation, Integer.valueOf(textureatlassprite.getIconWidth()), Integer.valueOf(textureatlassprite.getIconHeight()), Integer.valueOf(MathHelper.log2(k)), Integer.valueOf(MathHelper.log2(j1)));
            }

			generateMipmaps(textureatlassprite);
			stitcher.addSprite(textureatlassprite);
            return j;
        }
        finally
        {
            loadingSprites.removeLast();
            loadedSprites.add(location);
        }
    }

    private void finishLoading(Stitcher stitcher, net.minecraftforge.fml.common.ProgressManager.ProgressBar bar, int j, int k)
    {
        net.minecraftforge.fml.common.ProgressManager.pop(bar);
        int l = Math.min(j, k);
        int i1 = MathHelper.log2(l);

        if (false) // FORGE: do not lower the mipmap level
        if (i1 < this.mipmapLevels)
        {
            LOGGER.warn("{}: dropping miplevel from {} to {}, because of minimum power of two: {}", this.basePath, Integer.valueOf(this.mipmapLevels), Integer.valueOf(i1), Integer.valueOf(l));
            this.mipmapLevels = i1;
        }

        this.missingImage.generateMipmaps(this.mipmapLevels);
        stitcher.addSprite(this.missingImage);
        bar = net.minecraftforge.fml.common.ProgressManager.push("Texture creation", 2);

        try
        {
            bar.step("Stitching");
            stitcher.doStitch();
        }
        catch (StitcherException stitcherexception)
        {
            throw stitcherexception;
        }

        LOGGER.info("Created: {}x{} {}-atlas", Integer.valueOf(stitcher.getCurrentWidth()), Integer.valueOf(stitcher.getCurrentHeight()), this.basePath);
        bar.step("Allocating GL texture");
        TextureUtil.allocateTextureImpl(this.getGlTextureId(), this.mipmapLevels, stitcher.getCurrentWidth(), stitcher.getCurrentHeight());
		this.width = stitcher.getCurrentWidth();
		this.height = stitcher.getCurrentHeight();
		if (this.eaglerPBRMode)
		{
			this.eaglerPBRMaterialTexture = GlStateManager.generateTexture();
			TextureUtil.allocateTextureImpl(this.eaglerPBRMaterialTexture, this.mipmapLevels, this.width, this.height << 1);
		}
        Map<String, EaglerTextureAtlasSprite> map = Maps.<String, EaglerTextureAtlasSprite>newHashMap(this.mapRegisteredSprites);

        net.minecraftforge.fml.common.ProgressManager.pop(bar);
        bar = net.minecraftforge.fml.common.ProgressManager.push("Texture mipmap and upload", stitcher.getStichSlots().size());

        for (EaglerTextureAtlasSprite textureatlassprite1 : stitcher.getStichSlots())
        {
            bar.step(textureatlassprite1.getIconName());
            {
                String s = textureatlassprite1.getIconName();
                map.remove(s);
                this.mapUploadedSprites.put(s, textureatlassprite1);

                try
                {
					TextureUtil.bindTexture(this.getGlTextureId());
                    TextureUtil.uploadTextureMipmap(textureatlassprite1.getFrameTextureData(textureatlassprite1.getInitialFrameIndex()), textureatlassprite1.getIconWidth(), textureatlassprite1.getIconHeight(), textureatlassprite1.getOriginX(), textureatlassprite1.getOriginY(), false, false);
					if (this.eaglerPBRMaterialTexture != -1) this.uploadMaterial(textureatlassprite1);
                }
                catch (Throwable throwable)
                {
                    CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Stitching texture atlas");
                    CrashReportCategory crashreportcategory = crashreport.makeCategory("Texture being stitched together");
                    crashreportcategory.addCrashSection("Atlas path", this.basePath);
                    crashreportcategory.addCrashSection("Sprite", textureatlassprite1);
                    throw new ReportedException(crashreport);
                }

                if (textureatlassprite1.hasAnimationMetadata())
                {
                    this.listAnimatedSprites.add(textureatlassprite1);
                }
            }
        }

        for (EaglerTextureAtlasSprite textureatlassprite2 : map.values())
        {
            textureatlassprite2.copyFrom(this.missingImage);
        }
		TextureUtil.bindTexture(this.getGlTextureId());
        net.minecraftforge.client.ForgeHooksClient.onTextureStitchedPost(this);
        net.minecraftforge.fml.common.ProgressManager.pop(bar);
    }

    private void generateMipmaps(final EaglerTextureAtlasSprite texture)
    {

        try
        {
            texture.generateMipmaps(this.mipmapLevels);
        }
        catch (Throwable throwable)
        {
            CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Applying mipmap");
            CrashReportCategory crashreportcategory = crashreport.makeCategory("Sprite being mipmapped");
            crashreportcategory.addDetail("Sprite name", new ICrashReportDetail<String>()
            {
                public String call() throws Exception
                {
                    return texture.getIconName();
                }
            });
            crashreportcategory.addDetail("Sprite size", new ICrashReportDetail<String>()
            {
                public String call() throws Exception
                {
                    return texture.getIconWidth() + " x " + texture.getIconHeight();
                }
            });
            crashreportcategory.addDetail("Sprite frames", new ICrashReportDetail<String>()
            {
                public String call() throws Exception
                {
                    return texture.getFrameCount() + " frames";
                }
            });
            crashreportcategory.addCrashSection("Mipmap levels", Integer.valueOf(this.mipmapLevels));
            throw new ReportedException(crashreport);
        }
    }

    private ResourceLocation getResourceLocation(EaglerTextureAtlasSprite p_184396_1_)
    {
        ResourceLocation resourcelocation = new ResourceLocation(p_184396_1_.getIconName());
		String path = resourcelocation.getResourcePath();
		if (path.startsWith("mcpatcher/") || path.startsWith("optifine/")) {
			return new ResourceLocation(resourcelocation.getResourceDomain(), path + ".png");
		}
        return new ResourceLocation(resourcelocation.getResourceDomain(), String.format("%s/%s%s", this.basePath, resourcelocation.getResourcePath(), ".png"));
    }

    public EaglerTextureAtlasSprite getAtlasSprite(String iconName)
    {
        EaglerTextureAtlasSprite textureatlassprite = this.mapUploadedSprites.get(iconName);

        if (textureatlassprite == null)
        {
            textureatlassprite = this.missingImage;
        }

        return textureatlassprite;
    }

    public void updateAnimations()
    {
		if (this.listAnimatedSprites.isEmpty()) return;
		for (EaglerTextureAtlasSprite sprite : this.listAnimatedSprites)
		{
			if (sprite instanceof EaglerTextureAtlasSpritePBR) sprite.updateAnimationPBR();
			else sprite.updateAnimation();
		}
		if (this.animationTargets == null) this.animationTargets = new IFramebufferGL[this.mipmapLevels + 1];
		if (this.eaglerPBRMaterialTexture != -1 && this.materialAnimationTargets == null)
		{
			this.materialAnimationTargets = new IFramebufferGL[this.mipmapLevels + 1];
		}
		try
		{
			for (int level = 0; level < this.animationTargets.length; ++level)
			{
				bindAnimationTarget(this.animationTargets, this.getGlTextureId(), level);
				int levelWidth = this.width >> level;
				int levelHeight = this.height >> level;
				GlStateManager.viewport(0, 0, levelWidth, levelHeight);
				for (EaglerTextureAtlasSprite sprite : this.listAnimatedSprites)
				{
					if (sprite instanceof EaglerTextureAtlasSpritePBR) sprite.copyAnimationFramePBR(0, levelWidth, levelHeight, level);
					else sprite.copyAnimationFrame(levelWidth, levelHeight, level);
				}
				if (this.materialAnimationTargets != null)
				{
					bindAnimationTarget(this.materialAnimationTargets, this.eaglerPBRMaterialTexture, level);
					GlStateManager.viewport(0, 0, levelWidth, levelHeight << 1);
					for (EaglerTextureAtlasSprite sprite : this.listAnimatedSprites)
					{
						if (sprite instanceof EaglerTextureAtlasSpritePBR) sprite.copyAnimationFramePBR(1, levelWidth, levelHeight << 1, level);
					}
				}
			}
		}
		finally
		{
			_wglBindFramebuffer(FRAMEBUFFER, null);
		}
    }

	private void uploadMaterial(EaglerTextureAtlasSprite sprite)
	{
		int[][] normals;
		int[][] materials;
		if (sprite instanceof EaglerTextureAtlasSpritePBR)
		{
			int[][][] frames = ((EaglerTextureAtlasSpritePBR)sprite).getFramePBRTextureData(sprite.getInitialFrameIndex());
			normals = frames[1];
			materials = frames[2];
		}
		else
		{
			normals = constantMipmaps(sprite, PBRTextureMapUtils.defaultNormalsTexture.pixels[0]);
			materials = constantMipmaps(sprite, PBRTextureMapUtils.generateMaterialTextureFor(
					sprite.getIconName(), sprite.optifineBaseTextureName).pixels[0]);
		}
		TextureUtil.bindTexture(this.eaglerPBRMaterialTexture);
		TextureUtil.uploadTextureMipmap(normals, sprite.getIconWidth(), sprite.getIconHeight(),
				sprite.getOriginX(), sprite.getOriginY(), false, false);
		TextureUtil.uploadTextureMipmap(materials, sprite.getIconWidth(), sprite.getIconHeight(),
				sprite.getOriginX(), sprite.getOriginY() + this.height, false, false);
	}

	private int[][] constantMipmaps(EaglerTextureAtlasSprite sprite, int color)
	{
		int[][] pixels = new int[this.mipmapLevels + 1][];
		for (int level = 0; level < pixels.length; ++level)
		{
			pixels[level] = new int[(sprite.getIconWidth() >> level) * (sprite.getIconHeight() >> level)];
			java.util.Arrays.fill(pixels[level], color);
		}
		return pixels;
	}

	private static void bindAnimationTarget(IFramebufferGL[] targets, int texture, int level)
	{
		IFramebufferGL target = targets[level];
		if (target == null)
		{
			target = targets[level] = _wglCreateFramebuffer();
			_wglBindFramebuffer(FRAMEBUFFER, target);
			_wglFramebufferTexture2D(FRAMEBUFFER, COLOR_ATTACHMENT, TEXTURE_2D,
					EaglercraftGPU.getNativeTexture(texture), level);
		}
		else
		{
			_wglBindFramebuffer(FRAMEBUFFER, target);
		}
	}

	@Override
	public void deleteGlTexture()
	{
		if (this.animationTargets != null)
		{
			for (IFramebufferGL target : this.animationTargets) if (target != null) _wglDeleteFramebuffer(target);
			this.animationTargets = null;
		}
		if (this.materialAnimationTargets != null)
		{
			for (IFramebufferGL target : this.materialAnimationTargets) if (target != null) _wglDeleteFramebuffer(target);
			this.materialAnimationTargets = null;
		}
		if (this.eaglerPBRMaterialTexture != -1)
		{
			TextureUtil.deleteTexture(this.eaglerPBRMaterialTexture);
			this.eaglerPBRMaterialTexture = -1;
		}
		super.deleteGlTexture();
	}

    public EaglerTextureAtlasSprite registerSprite(ResourceLocation location)
	{
		return this.registerSprite(location, null);
	}

	public EaglerTextureAtlasSprite registerSprite(ResourceLocation location, String optifineBaseTexture)
    {
        if (location == null)
        {
            throw new IllegalArgumentException("Location cannot be null!");
        }
        else
        {
            EaglerTextureAtlasSprite textureatlassprite = this.mapRegisteredSprites.get(location.toString());

            if (textureatlassprite == null)
            {
                textureatlassprite = this.eaglerPBRMode ? new EaglerTextureAtlasSpritePBR(location.toString()) : EaglerTextureAtlasSprite.makeAtlasSprite(location);
				textureatlassprite.setIndexInMap(this.mapRegisteredSprites.size() + 1);
                this.mapRegisteredSprites.put(location.toString(), textureatlassprite);
            }
			if (optifineBaseTexture != null) textureatlassprite.optifineBaseTextureName = optifineBaseTexture;

            return textureatlassprite;
        }
    }

	public int getCountRegisteredSprites() {
		return this.mapRegisteredSprites.size();
	}

    public void tick()
    {
        this.updateAnimations();
    }

    public void setMipmapLevels(int mipmapLevelsIn)
    {
        this.mipmapLevels = mipmapLevelsIn;
    }

    public EaglerTextureAtlasSprite getMissingSprite()
    {
        return this.missingImage;
    }

	public void setEnablePBREagler(boolean enable)
	{
		this.eaglerPBRMode = enable;
	}

    //===================================================================================================
    //                                           Forge Start
    //===================================================================================================

    private final java.util.Deque<ResourceLocation> loadingSprites = new java.util.ArrayDeque<>();
    private final java.util.Set<ResourceLocation> loadedSprites = new java.util.HashSet<>();

    /**
     * Grabs the registered entry for the specified name, returning null if there was not a entry.
     * Opposed to registerIcon, this will not instantiate the entry, useful to test if a mapping exists.
     *
     * @param name The name of the entry to find
     * @return The registered entry, null if nothing was registered.
     */
    @Nullable
    public EaglerTextureAtlasSprite getTextureExtry(String name)
    {
        return mapRegisteredSprites.get(name);
    }

    /**
     * Adds a texture registry entry to this map for the specified name if one does not already exist.
     * Returns false if the map already contains a entry for the specified name.
     *
     * @param entry Entry instance
     * @return True if the entry was added to the map, false otherwise.
     */
    public boolean setTextureEntry(EaglerTextureAtlasSprite entry)
    {
        String name = entry.getIconName();
        if (!mapRegisteredSprites.containsKey(name))
        {
			entry.setIndexInMap(this.mapRegisteredSprites.size() + 1);
            mapRegisteredSprites.put(name, entry);
            return true;
        }
        return false;
    }

    public String getBasePath()
    {
        return basePath;
    }

    public int getMipmapLevels()
    {
        return mipmapLevels;
    }

	public int getWidth()
	{
		return this.width;
	}

	public int getHeight()
	{
		return this.height;
	}
}
