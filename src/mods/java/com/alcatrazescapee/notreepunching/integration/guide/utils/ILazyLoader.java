package com.alcatrazescapee.notreepunching.integration.guide.utils;

import amerifrance.guideapi.api.impl.Book;
import java.util.function.Consumer;

public interface ILazyLoader {
   static void init(Book book) {
      book.getCategoryList().forEach((cat) -> {
         cat.entries.values().forEach((ent) -> {
            ent.pageList.forEach((page) -> {
               if (page instanceof ILazyLoader) {
                  ((ILazyLoader)page).loadPost();
               }

            });
         });
      });
   }

   void loadPost();
}
