package reborncore.modcl.manual.pages;

import net.minecraft.client.Minecraft;
import reborncore.common.util.StringUtils;
import reborncore.modcl.manual.GuiTeamRebornManual;

public class PageHome extends ManualPage {
   public PageHome() {
   }

   public String title() {
      return StringUtils.t("item.reborncore:manual.name");
   }

   public void draw(Minecraft mc, GuiTeamRebornManual manual) {
   }

   public ManualPage nextPage() {
      return null;
   }
}
