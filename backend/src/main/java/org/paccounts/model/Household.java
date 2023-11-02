package org.paccounts.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.util.Set;

/**
 * Budget holders.
 */
@Document(collection = "households")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Household {

    @Id
    private ObjectId id;
    private String title;
    private String description;
    private Set<LocalDate> periodMonths;
    private Set<Article> articles;
    private Set<Item> items;

    /**
     * Detailing for Account (cash, bank money, supplier etc.).
     */
    @Document(collection = "articles")
    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    public static class Article {

        @Id
        private ObjectId id;

        @DBRef
        private Account account;

        @DBRef
        private Currency currency;

        private String title;
        private String description;
    }

    /**
     * Item of accounting (bought/sold 'bread', 'milk', 'water 1L' etc.).
     */
    @Document(collection = "items")
    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    public static class Item {
        @Id
        private ObjectId id;
        private String title;
        private String description;
    }

    public void addPeriodMonth(LocalDate date) {
        LocalDate firstDate = date.withDayOfMonth(1);
        periodMonths.add(firstDate);
    }

    public void removePeriodMonth(LocalDate date) {
        LocalDate firstDate = date.withDayOfMonth(1);
        periodMonths.remove(firstDate);
    }

    public void addArticle(Article article) {
        articles.add(article);
    }

    public void removeArticle(Article article) {
        articles.remove(article);
    }

    public void addItem(Item item) {
        items.add(item);
    }

    public void removeItem(Item item) {
        items.remove(item);
    }
}
