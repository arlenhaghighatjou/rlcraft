package me.ichun.mods.ichunutil.common.module.tabula.formats.types;

import java.io.File;
import me.ichun.mods.ichunutil.common.module.tabula.project.ProjectInfo;

public abstract class Importer {
   public Importer() {
   }

   public abstract ProjectInfo createProjectInfo(File var1);

   public abstract int getProjectVersion();
}
