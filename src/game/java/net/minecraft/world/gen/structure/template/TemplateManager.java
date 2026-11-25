package net.minecraft.world.gen.structure.template;

import com.google.common.collect.Maps;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.datafix.DataFixer;
import net.minecraft.util.datafix.FixTypes;
import net.lax1dude.eaglercraft.v1_8.IOUtils;
import net.lax1dude.eaglercraft.v1_8.EaglerOutputStream;
import net.lax1dude.eaglercraft.v1_8.internal.vfs2.VFile2;
import net.lax1dude.eaglercraft.v1_8.sp.server.WorldsDB;

public class TemplateManager
{
    private final Map<String, Template> templates = Maps.<String, Template>newHashMap();
    private final String baseFolder;
	private final VFile2 virtualFolder;
    private final DataFixer fixer;

    public TemplateManager(String p_i47239_1_, DataFixer p_i47239_2_)
    {
        this.baseFolder = p_i47239_1_;
		this.virtualFolder = null;
        this.fixer = p_i47239_2_;
    }

	public TemplateManager(VFile2 virtualFolder, DataFixer fixer)
	{
		this.baseFolder = null;
		this.virtualFolder = virtualFolder;
		this.fixer = fixer;
	}

    public Template getTemplate(@Nullable MinecraftServer server, ResourceLocation id)
    {
        Template template = this.get(server, id);

        if (template == null)
        {
            template = new Template();
            this.templates.put(id.getResourcePath(), template);
        }

        return template;
    }

    @Nullable
    public Template get(@Nullable MinecraftServer server, ResourceLocation templatePath)
    {
        String s = templatePath.getResourcePath();

        if (this.templates.containsKey(s))
        {
            return this.templates.get(s);
        }
        else
        {
            if (server == null)
            {
                this.readTemplateFromJar(templatePath);
            }
            else
            {
                this.readTemplate(templatePath);
            }

            return this.templates.containsKey(s) ? (Template)this.templates.get(s) : null;
        }
    }

    public boolean readTemplate(ResourceLocation server)
    {
        String s = server.getResourcePath();
		if (this.virtualFolder != null)
		{
			VFile2 file = this.getVirtualTemplateFile(s);
			if (file == null) return false;
			if (!file.exists()) return this.readTemplateFromJar(server);
			try (InputStream input = file.getInputStream())
			{
				this.readTemplateFromStream(s, input);
				return true;
			}
			catch (IOException ex)
			{
				return false;
			}
		}
        File file1 = new File(this.baseFolder, s + ".nbt");

        if (!file1.exists())
        {
            return this.readTemplateFromJar(server);
        }
        else
        {
            InputStream inputstream = null;
            boolean flag;

            try
            {
                inputstream = new FileInputStream(file1);
                this.readTemplateFromStream(s, inputstream);
                return true;
            }
            catch (Throwable var10)
            {
                flag = false;
            }
            finally
            {
                IOUtils.closeQuietly(inputstream);
            }

            return flag;
        }
    }

    private boolean readTemplateFromJar(ResourceLocation id)
    {
        String s = id.getResourceDomain();
        String s1 = id.getResourcePath();
        InputStream inputstream = null;
        boolean flag;

        try
        {
            String path = com.arlenh7.eaglercraft.forge.ModAssets.find(s + "/structures/" + s1 + ".nbt");
            inputstream = path == null ? null : net.lax1dude.eaglercraft.v1_8.EagRuntime.getResourceStream("/" + path);
            this.readTemplateFromStream(s1, inputstream);
            return true;
        }
        catch (Throwable var10)
        {
            flag = false;
        }
        finally
        {
            IOUtils.closeQuietly(inputstream);
        }

        return flag;
    }

    private void readTemplateFromStream(String id, InputStream stream) throws IOException
    {
        NBTTagCompound nbttagcompound = CompressedStreamTools.readCompressed(stream);

        if (!nbttagcompound.hasKey("DataVersion", 99))
        {
            nbttagcompound.setInteger("DataVersion", 500);
        }

        Template template = new Template();
        template.read(this.fixer.process(FixTypes.STRUCTURE, nbttagcompound));
        this.templates.put(id, template);
    }

    public boolean writeTemplate(@Nullable MinecraftServer server, ResourceLocation id)
    {
        String s = id.getResourcePath();

        if (server != null && this.templates.containsKey(s))
        {
			if (this.virtualFolder != null)
			{
				VFile2 file = this.getVirtualTemplateFile(s);
				if (file == null) return false;
				try
				{
					EaglerOutputStream encoded = new EaglerOutputStream();
					CompressedStreamTools.writeCompressed(this.templates.get(s).writeToNBT(new NBTTagCompound()), encoded);
					file.setAllBytes(encoded.toByteArray());
					return true;
				}
				catch (IOException | RuntimeException ex)
				{
					return false;
				}
			}
            File file1 = new File(this.baseFolder);

            if (!file1.exists())
            {
                if (!file1.mkdirs())
                {
                    return false;
                }
            }
            else if (!file1.isDirectory())
            {
                return false;
            }

            File file2 = new File(file1, s + ".nbt");
            Template template = this.templates.get(s);
            OutputStream outputstream = null;
            boolean flag;

            try
            {
                NBTTagCompound nbttagcompound = template.writeToNBT(new NBTTagCompound());
                outputstream = new FileOutputStream(file2);
                CompressedStreamTools.writeCompressed(nbttagcompound, outputstream);
                return true;
            }
            catch (Throwable var13)
            {
                flag = false;
            }
            finally
            {
                IOUtils.closeQuietly(outputstream);
            }

            return flag;
        }
        else
        {
            return false;
        }
    }

    public void remove(ResourceLocation templatePath)
    {
        this.templates.remove(templatePath.getResourcePath());
    }

	private VFile2 getVirtualTemplateFile(String name)
	{
		if (name.isEmpty() || name.startsWith("/") || name.indexOf('\\') >= 0 || name.contains("..")) return null;
		VFile2 file = WorldsDB.newVFile(this.virtualFolder, name + ".nbt");
		return file.getPath().startsWith(this.virtualFolder.getPath() + "/") ? file : null;
	}
}
