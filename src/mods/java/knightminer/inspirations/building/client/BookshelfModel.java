package knightminer.inspirations.building.client;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import javax.annotation.Nullable;
import knightminer.inspirations.Inspirations;
import knightminer.inspirations.building.block.BlockBookshelf;
import knightminer.inspirations.shared.client.TextureModel;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.lax1dude.eaglercraft.v1_8.opengl.VertexFormat;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.common.property.IExtendedBlockState;

public class BookshelfModel extends TextureModel {
   public static final Cache<BookshelfModel.BookshelfCacheKey, IBakedModel> BOOK_CACHE = CacheBuilder.newBuilder().maximumSize(30L).build();

   public BookshelfModel(IBakedModel standard, IModel model, VertexFormat format) {
      super(standard, model, format, "texture", true);
   }

   public List<BakedQuad> getQuads(IBlockState state, EnumFacing side, long rand) {
      IBakedModel bakedModel = this.originalModel;
      if (state instanceof IExtendedBlockState) {
         IExtendedBlockState extendedState = (IExtendedBlockState)state;
         String texture = (String)extendedState.getValue(BlockBookshelf.TEXTURE);
         Integer booksValue = (Integer)extendedState.getValue(BlockBookshelf.BOOKS);
         int books = booksValue != null ? booksValue.intValue() : 0;

         try {
            bakedModel = (IBakedModel)BOOK_CACHE.get(new BookshelfModel.BookshelfCacheKey(extendedState.getClean(), texture, books), () -> {
               Builder<String, String> builder = ImmutableMap.builder();
               if (texture != null) {
                  builder.put("texture", texture);
               }

               for(int i = 0; i < 14; ++i) {
                  if ((books & 1 << i) == 0) {
                     builder.put("#book" + i, "");
                     builder.put("#bookLabel" + i, "");
                  }
               }

               return this.getTexturedModel(builder.build());
            });
         } catch (ExecutionException var11) {
            Inspirations.log.error(var11);
         }
      }

      return bakedModel.getQuads(state, side, rand);
   }

   private static class BookshelfCacheKey {
      private IBlockState state;
      @Nullable
      private String texture;
      private int books;

      public BookshelfCacheKey(IBlockState state, @Nullable String texture, int books) {
         this.state = state;
         this.texture = texture;
         this.books = books;
      }

      public boolean equals(Object o) {
         if (this == o) {
            return true;
         } else if (o != null && this.getClass() == o.getClass()) {
            BookshelfModel.BookshelfCacheKey that = (BookshelfModel.BookshelfCacheKey)o;
            return this.books == that.books && this.state == that.state && (this.texture == that.texture || this.texture != null && this.texture.equals(that.texture));
         } else {
            return false;
         }
      }

      public int hashCode() {
         return 31 * (31 * this.state.hashCode() + (this.texture == null ? 0 : this.texture.hashCode())) + this.books;
      }
   }
}
