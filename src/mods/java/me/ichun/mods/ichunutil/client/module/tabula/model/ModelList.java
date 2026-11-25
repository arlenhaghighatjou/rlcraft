package me.ichun.mods.ichunutil.client.module.tabula.model;

import java.util.ArrayList;
import net.minecraft.client.model.ModelBase;

public final class ModelList {
   public static ArrayList<ModelInfo> models = new ArrayList();
   public static ArrayList<Class<? extends ModelBase>> modelBlacklist = new ArrayList();

   public ModelList() {
   }
}
