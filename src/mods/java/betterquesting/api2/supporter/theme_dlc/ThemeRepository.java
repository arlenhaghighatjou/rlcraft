package betterquesting.api2.supporter.theme_dlc;

import betterquesting.api.utils.JsonHelper;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.annotation.Nonnull;

public class ThemeRepository {
   private final String repoAddress;
   public String repoName = "Unknown Theme Repository";
   private final List<CatalogueEntry> entries = new ArrayList();

   public ThemeRepository(@Nonnull String address) {
      this.repoAddress = address;
   }

   public String getAddress() {
      return this.repoAddress;
   }

   public List<CatalogueEntry> getEntries() {
      return Collections.unmodifiableList(this.entries);
   }

   public void loadRepository(@Nonnull JsonObject json) {
      this.repoName = JsonHelper.GetString(json, "repoName", "Unknown Repository");
   }
}
