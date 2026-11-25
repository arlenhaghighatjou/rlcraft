package slimeknights.mantle.client.book.data;

public class BookmarkData {
   public String text;
   public int color;
   public String page;

   public BookmarkData() {
      this("");
   }

   public BookmarkData(String page) {
      this(page, "");
   }

   public BookmarkData(String page, String text) {
      this.text = "";
      this.color = 4605510;
      this.page = "";
      this.page = page;
      this.text = text;
   }
}
