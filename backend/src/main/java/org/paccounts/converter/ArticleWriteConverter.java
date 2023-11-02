package org.paccounts.converter;

import org.bson.Document;
import org.paccounts.model.Household;
import org.springframework.core.convert.converter.Converter;

public class ArticleWriteConverter implements Converter<Household.Article, Document> {

    @Override
    public Document convert(Household.Article source) {

        Document document = new Document();
        document.put("_id", source.getId());
        document.put("title", source.getTitle());
        document.put("description", source.getDescription());

        return document;
    }
}
