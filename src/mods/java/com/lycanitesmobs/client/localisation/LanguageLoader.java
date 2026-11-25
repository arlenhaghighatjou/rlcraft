package com.lycanitesmobs.client.localisation;

import java.util.HashMap;
import java.util.Map;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.IResourceManagerReloadListener;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class LanguageLoader implements IResourceManagerReloadListener {
   public static LanguageLoader INSTANCE;
   protected Map<String, String> map = new HashMap();

   public LanguageLoader() {
   }

   public static LanguageLoader getInstance() {
      if (INSTANCE == null) {
         INSTANCE = new LanguageLoader();
      }

      return INSTANCE;
   }

   public void onResourceManagerReload(IResourceManager resourceManager) {
   }
}
