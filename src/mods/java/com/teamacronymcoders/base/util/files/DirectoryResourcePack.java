package com.teamacronymcoders.base.util.files;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import javax.annotation.Nonnull;
import net.minecraft.client.resources.AbstractResourcePack;

public class DirectoryResourcePack extends AbstractResourcePack {
   private Set<String> domains;

   public DirectoryResourcePack(File resourceFolder) {
      super(resourceFolder);
   }

   @Nonnull
   protected InputStream getInputStreamByName(@Nonnull String name) throws IOException {
      return new FileInputStream(this.getFile(name));
   }

   protected boolean hasResourceName(@Nonnull String name) {
      return this.getFile(name).exists();
   }

   private File getFile(String name) {
      return new File(this.resourcePackFile, name.replace("assets/", ""));
   }

   @Nonnull
   public Set<String> getResourceDomains() {
      if (this.domains == null) {
         this.domains = new HashSet();
         String[] folderNames = this.resourcePackFile.list();
         if (folderNames != null) {
            this.domains.addAll(Arrays.asList(folderNames));
         }
      }

      return this.domains;
   }
}
