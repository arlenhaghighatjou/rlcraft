package lumien.custommainmenu.lib.texts;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.IResource;
import net.minecraft.util.ResourceLocation;

public class TextResourceLocation implements IText {
   String string;
   ResourceLocation resourceLocation;

   public TextResourceLocation(String resourceString) {
      this.resourceLocation = new ResourceLocation(resourceString);
      this.string = "";
   }

   public String get() {
      if (this.string == null) {
         return "";
      } else {
         if (this.string.equals("")) {
            IResource resource = null;

            try {
               resource = Minecraft.getMinecraft().getResourceManager().getResource(this.resourceLocation);
            } catch (IOException var8) {
               var8.printStackTrace();
            }

            if (resource == null) {
               this.string = null;
               return "";
            }

            BufferedReader in = null;
            in = new BufferedReader(new InputStreamReader(resource.getInputStream()));
            StringBuilder builder = new StringBuilder();
            String inputLine = null;

            try {
               inputLine = in.readLine();
            } catch (IOException var7) {
               var7.printStackTrace();
            }

            while(true) {
               builder.append(inputLine);

               try {
                  inputLine = in.readLine();
               } catch (IOException var9) {
                  var9.printStackTrace();
                  break;
               }

               if (inputLine != null) {
                  builder.append("\n");
               }

               if (inputLine == null) {
                  break;
               }
            }

            try {
               in.close();
            } catch (IOException var6) {
               var6.printStackTrace();
            }

            this.string = builder.toString();
         }

         return this.string;
      }
   }
}
