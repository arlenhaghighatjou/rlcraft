package atomicstryker.ruins.common;

import java.util.List;

import net.lax1dude.eaglercraft.v1_8.EagRuntime;
import net.lax1dude.eaglercraft.v1_8.internal.vfs2.VFile2;

/**
 * As curse finally outlawed zip files, the setup portion of Ruins will have to
 * be automated and extract from the jar itself.
 */
public class ConfigFolderPreparator
{

    /**
     * check if target folder .minecraft/config/ruins_config exists, if not,
     * copy it from the jar
     */
    public static void copyFromJarIfNotPresent(VFile2 targetDir)
    {
        if (!targetDir.listFilenames(false).isEmpty())
        {
            return;
        }

        final String prefix = "AS_Ruins/" + RuinsMod.TEMPLATE_PATH_JAR + "/";
        List<String> files = EagRuntime.listResources(prefix);
        for (int i = 0, l = files.size(); i < l; ++i)
        {
            String path = files.get(i);
            new VFile2(targetDir, path.substring(prefix.length())).setAllBytes(EagRuntime.getRequiredResourceBytes(path));
        }
    }

}
