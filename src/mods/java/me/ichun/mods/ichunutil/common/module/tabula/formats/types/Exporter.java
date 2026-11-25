package me.ichun.mods.ichunutil.common.module.tabula.formats.types;

import me.ichun.mods.ichunutil.client.gui.window.IWorkspace;
import me.ichun.mods.ichunutil.client.gui.window.element.IListable;
import me.ichun.mods.ichunutil.common.module.tabula.project.ProjectInfo;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public abstract class Exporter implements IListable {
   public final String name;

   public Exporter(String name) {
      this.name = name;
   }

   public abstract boolean export(ProjectInfo var1, Object... var2);

   @SideOnly(Side.CLIENT)
   public boolean override(IWorkspace workspace) {
      return false;
   }

   @SideOnly(Side.CLIENT)
   public String getName() {
      return this.name;
   }
}
