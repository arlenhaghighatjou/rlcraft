package vazkii.quark.vanity.client.emotes;

import com.google.common.collect.ImmutableSet;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import javax.annotation.Nonnull;
import net.minecraft.client.resources.AbstractResourcePack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vazkii.quark.base.Quark;
import vazkii.quark.vanity.feature.EmoteSystem;

@SideOnly(Side.CLIENT)
public class CustomEmoteIconResourcePack extends AbstractResourcePack {
   private final List<String> verifiedNames = new ArrayList();
   private final List<String> existingNames = new ArrayList();

   public CustomEmoteIconResourcePack() {
      super(EmoteSystem.emotesDir);
   }

   @Nonnull
   public Set<String> getResourceDomains() {
      return ImmutableSet.of("quark_custom");
   }

   @Nonnull
   public InputStream getInputStream(ResourceLocation location) throws IOException {
      return this.getInputStreamByName(location.toString());
   }

   @Nonnull
   protected InputStream getInputStreamByName(@Nonnull String name) throws IOException {
      if (name.equals("pack.mcmeta")) {
         return Quark.class.getResourceAsStream("/proxypack.mcmeta");
      } else {
         File file = this.getFile(name);
         if (!file.exists()) {
            throw new FileNotFoundException(name);
         } else {
            return new FileInputStream(file);
         }
      }
   }

   public boolean resourceExists(ResourceLocation location) {
      return this.hasResourceName(location.toString());
   }

   protected boolean hasResourceName(@Nonnull String name) {
      if (!this.verifiedNames.contains(name)) {
         File file = this.getFile(name);
         if (file.exists()) {
            this.existingNames.add(name);
         }

         this.verifiedNames.add(name);
      }

      return this.existingNames.contains(name);
   }

   private File getFile(String name) {
      String filename = name.substring(name.indexOf(":") + 1) + ".png";
      return new File(EmoteSystem.emotesDir, filename);
   }

   @Nonnull
   public String getPackName() {
      return "quark-emote-pack";
   }
}
