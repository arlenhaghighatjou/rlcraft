package net.ilexiconn.llibrary.client.lang;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RemoteLanguageContainer {
   public RemoteLanguageContainer.LangContainer[] languages;

   public RemoteLanguageContainer() {
   }

   public class LangContainer {
      public String locale;
      public String downloadURL;

      public LangContainer() {
      }
   }
}
