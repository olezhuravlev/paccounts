package org.paccounts.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.paccounts.model.Account;
import org.paccounts.model.Currency;
import org.paccounts.model.Household;

import java.time.LocalDate;
import java.util.Set;
import java.util.stream.Collectors;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class HouseholdDto {

    private String id;

    @NotBlank(message = "title-must-be-specified")
    private String title;

    private String description;

    private Set<String> periodMonths;

    @Valid
    private Set<ArticleDto> articles;

    @Valid
    private Set<ItemDto> items;

    @NoArgsConstructor
    @AllArgsConstructor
    @Data
    public static class ArticleDto {

        private String id;

        @NotBlank(message = "account-id-must-be-specified")
        private String accountId;

        @NotBlank(message = "currency-id-must-be-specified")
        private String currencyId;

        @NotBlank(message = "title-must-be-specified")
        private String title;

        private String description;

        public static ArticleDto convert(Household.Article article) {

            ObjectId objectId = article.getId();
            String articleId = objectId == null ? null : objectId.toString();

            Account account = article.getAccount();
            String accountId = account == null ? null : account.getId().toString();

            Currency currency = article.getCurrency();
            String currencyId = currency == null ? null : currency.getId().toString();

            return new ArticleDto(articleId, accountId, currencyId, article.getTitle(), article.getDescription());
        }
    }

    @NoArgsConstructor
    @AllArgsConstructor
    @Data
    public static class ItemDto {

        private String id;

        @NotBlank(message = "title-must-be-specified")
        private String title;

        private String description;

        public static ItemDto convert(Household.Item item) {
            ObjectId objectId = item.getId();
            String itemId = objectId == null ? null : objectId.toString();
            return new ItemDto(itemId, item.getTitle(), item.getDescription());
        }
    }

    public static HouseholdDto convert(Household household) {

        ObjectId objectId = household.getId();
        String householdId = objectId == null ? null : objectId.toString();

        Set<String> periodMonthDatesStr = null;
        Set<LocalDate> periodMonthSet = household.getPeriodMonths();
        if (periodMonthSet != null) {
            periodMonthDatesStr = periodMonthSet.stream().map(LocalDate::toString).collect(Collectors.toSet());
        }

        Set<HouseholdDto.ArticleDto> articleDtos = null;
        Set<Household.Article> articleSet = household.getArticles();
        if (articleSet != null) {
            articleDtos = articleSet.stream().map(HouseholdDto.ArticleDto::convert).collect(Collectors.toSet());
        }

        Set<HouseholdDto.ItemDto> itemDtos = null;
        Set<Household.Item> itemSet = household.getItems();
        if (itemSet != null) {
            itemDtos = itemSet.stream().map(HouseholdDto.ItemDto::convert).collect(Collectors.toSet());
        }

        return new HouseholdDto(householdId, household.getTitle(), household.getDescription(), periodMonthDatesStr, articleDtos, itemDtos);
    }
}
