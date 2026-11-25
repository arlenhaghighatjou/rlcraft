package betterquesting.api.nbt_doc;

public interface INbtDoc {
   String getUnlocalisedTitle();

   String getUnlocalisedName(String var1);

   String getUnlocalisedDesc(String var1);

   INbtDoc getParent();

   INbtDoc getChild(String var1);
}
