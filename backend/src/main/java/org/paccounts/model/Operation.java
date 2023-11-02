package org.paccounts.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.paccounts.exception.IncompatibleParametersException;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;

/**
 * Any action of chain "money-good-money".
 */
@Document(collection = "operations")
@NoArgsConstructor
@Data
public class Operation {

    @Id
    private ObjectId id;

    @DBRef
    private Household household;

    private LocalDate periodMonth;

    private LocalDate date;

    @DBRef
    private Account accountDt;

    @DBRef
    private Household.Article articleDt;

    @DBRef
    private Account accountCt;

    @DBRef
    private Household.Article articleCt;

    @DBRef
    private Set<Household.Item> items;

    private BigDecimal sum;
    private BigDecimal quantity;
    private String description;

    public Operation(ObjectId id, Household household, LocalDate periodMonth, LocalDate date, Account accountDt, Household.Article articleDt, Account accountCt, Household.Article articleCt, Set<Household.Item> items, BigDecimal sum, BigDecimal quantity, String description) {

        if (!belongsToPeriod(date, periodMonth)) {
            throw new IncompatibleParametersException("Date [" + date + "] doesn't belong to specified period [" + periodMonth + "]!");
        }

        if (!isCompatible(articleDt, articleCt)) {
            throw new IncompatibleParametersException("Debet article [" + articleDt.getTitle() + "] has currency [" + articleDt.getCurrency().getTitle() + "] that differs from credit article [" + articleCt.getTitle() + "] currency [" + articleCt.getCurrency().getTitle() + "]!");
        }

        this.id = id;
        this.household = household;
        this.periodMonth = periodMonth.withDayOfMonth(1);
        this.date = date;
        this.accountDt = accountDt;
        this.articleDt = articleDt;
        this.accountCt = accountCt;
        this.articleCt = articleCt;
        this.items = items;
        this.sum = sum;
        this.quantity = quantity;
        this.description = description;
    }

    public Operation(Household household, LocalDate periodMonth, LocalDate date, Account accountDt, Household.Article articleDt, Account accountCt, Household.Article articleCt, Set<Household.Item> items, BigDecimal sum, BigDecimal quantity, String description) {

        if (!belongsToPeriod(date, periodMonth)) {
            throw new IncompatibleParametersException("Date [" + date + "] doesn't belong to specified period [" + periodMonth + "]!");
        }

        if (!isCompatible(articleDt, articleCt)) {
            throw new IncompatibleParametersException("Debet article [" + articleDt.getTitle() + "] has currency [" + articleDt.getCurrency().getTitle() + "] that differs from credit article [" + articleCt.getTitle() + "] currency [" + articleCt.getCurrency().getTitle() + "]!");
        }

        this.household = household;
        this.periodMonth = periodMonth.withDayOfMonth(1);
        this.date = date;
        this.accountDt = accountDt;
        this.articleDt = articleDt;
        this.accountCt = accountCt;
        this.articleCt = articleCt;
        this.items = items;
        this.sum = sum;
        this.quantity = quantity;
        this.description = description;
    }

    private boolean belongsToPeriod(LocalDate date, LocalDate period) {

        int periodYear = period.getYear();
        int dateYear = date.getYear();
        int periodMonthValue = period.getMonthValue();
        int dateMonthValue = date.getMonthValue();

        return periodYear == dateYear && periodMonthValue == dateMonthValue;
    }

    private boolean isCompatible(Household.Article articleDt, Household.Article articleCt) {

        Currency currencyDt = articleDt.getCurrency();
        Currency currencyCt = articleCt.getCurrency();

        return currencyDt.equals(currencyCt);
    }

    public void setDate(LocalDate date) {

        this.date = date;

        if (date != null) {
            this.periodMonth = date.withDayOfMonth(1);
        }
    }
}
