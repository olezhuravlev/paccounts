package org.paccounts.converter;

import org.bson.Document;
import org.bson.types.ObjectId;
import org.paccounts.model.Account;
import org.paccounts.model.Currency;
import org.paccounts.model.Household;
import org.paccounts.service.ApiGate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class ArticleReadConverter implements Converter<Document, Household.Article> {

    @Autowired
    private ApiGate apiGate;

    @Override
    public Household.Article convert(Document source) {

        return new Household.Article(
                (ObjectId) source.get("_id"),
                retrieveAccount(source),
                retrieveCurrency(source),
                (String) source.get("title"),
                (String) source.get("description"));
    }

    private Account retrieveAccount(Document source) {
        Object id = source.get("account");
        Optional<Account> found = apiGate.getAccountById((ObjectId) id);
        return found.get();
    }

    private Currency retrieveCurrency(Document source) {
        Object id = source.get("currency");
        Optional<Currency> found = apiGate.getCurrencyById((ObjectId) id);
        return found.get();
    }
}
