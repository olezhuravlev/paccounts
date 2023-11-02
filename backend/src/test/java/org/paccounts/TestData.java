package org.paccounts;

import org.bson.types.ObjectId;
import org.paccounts.model.Account;
import org.paccounts.model.Currency;
import org.paccounts.model.Household;
import org.paccounts.model.Operation;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class TestData {

    public static final List<Currency> CURRENCIES;
    public static final List<Account> ACCOUNTS;
    public static final List<Household> HOUSEHOLDS;
    public static final List<Operation> OPERATIONS;

    static {

        ///////////////////////////////////////////////////////////////////////
        // COMMON COLLECTIONS
        CURRENCIES = new ArrayList<>();
        CURRENCIES.add(new Currency(new ObjectId(), "1", "Test Currency 1", "Currency #1 for using in tests"));
        CURRENCIES.add(new Currency(new ObjectId(), "2", "Test Currency 2", "Currency #2 for using in tests"));
        CURRENCIES.add(new Currency(new ObjectId(), "3", "Test Currency 3", "Currency #3 for using in tests"));

        ACCOUNTS = new ArrayList<>();
        ACCOUNTS.add(new Account(new ObjectId(), "1", Account.Type.A, "Test Account 1", "Account #1 for using in tests"));
        ACCOUNTS.add(new Account(new ObjectId(), "2", Account.Type.A, "Test Account 2", "Account #2 for using in tests"));
        ACCOUNTS.add(new Account(new ObjectId(), "3", Account.Type.A, "Test Account 3", "Account #3 for using in tests"));

        ///////////////////////////////////////////////////////////////////////
        // HOUSEHOLD #1
        Household household1 = new Household(new ObjectId(), "Test Household 1", "Household #1 for using in tests", new LinkedHashSet<>(), new LinkedHashSet<>(), new LinkedHashSet<>());

        LocalDate periodMonth_1_1 = LocalDate.of(2020, 1, 1);
        LocalDate periodMonth_1_2 = LocalDate.of(2020, 2, 1);
        LocalDate periodMonth_1_3 = LocalDate.of(2020, 3, 1);

        household1.addPeriodMonth(periodMonth_1_1);
        household1.addPeriodMonth(periodMonth_1_2);
        household1.addPeriodMonth(periodMonth_1_3);

        // article_Household_Account_Number
        Household.Article article_1_1_1 = new Household.Article(new ObjectId(), ACCOUNTS.get(0), CURRENCIES.get(0), "Household #1 Test Article 1", "Household #1 Article #1 for using in tests");
        Household.Article article_1_1_2 = new Household.Article(new ObjectId(), ACCOUNTS.get(0), CURRENCIES.get(0), "Household #1 Test Article 2", "Household #1 Article #2 for using in tests");
        Household.Article article_1_2_3 = new Household.Article(new ObjectId(), ACCOUNTS.get(1), CURRENCIES.get(0), "Household #1 Test Article 3", "Household #1 Article #3 for using in tests");
        Household.Article article_1_2_4 = new Household.Article(new ObjectId(), ACCOUNTS.get(1), CURRENCIES.get(0), "Household #1 Test Article 4", "Household #1 Article #4 for using in tests");
        Household.Article article_1_3_5 = new Household.Article(new ObjectId(), ACCOUNTS.get(2), CURRENCIES.get(0), "Household #1 Test Article 5", "Household #1 Article #5 for using in tests");
        Household.Article article_1_3_6 = new Household.Article(new ObjectId(), ACCOUNTS.get(2), CURRENCIES.get(0), "Household #1 Test Article 6", "Household #1 Article #6 for using in tests");

        household1.addArticle(article_1_1_1);
        household1.addArticle(article_1_1_2);
        household1.addArticle(article_1_2_3);
        household1.addArticle(article_1_2_4);
        household1.addArticle(article_1_3_5);
        household1.addArticle(article_1_3_6);

        Household.Item item_1_1 = new Household.Item(new ObjectId(), "Household #1 Test Item 1", "Household #1 Item #1 for using in tests");
        Household.Item item_1_2 = new Household.Item(new ObjectId(), "Household #1 Test Item 2", "Household #1 Item #2 for using in tests");
        Household.Item item_1_3 = new Household.Item(new ObjectId(), "Household #1 Test Item 3", "Household #1 Item #3 for using in tests");

        household1.addItem(item_1_1);
        household1.addItem(item_1_2);
        household1.addItem(item_1_3);

        ///////////////////////////////////////////////////////////////////////
        // HOUSEHOLD #2
        Household household2 = new Household(new ObjectId(), "Test Household 2", "Household #2 for using in tests", new LinkedHashSet<>(), new LinkedHashSet<>(), new LinkedHashSet<>());

        LocalDate periodMonth_2_1 = LocalDate.of(2020, 1, 1);
        LocalDate periodMonth_2_2 = LocalDate.of(2020, 4, 1);

        household2.addPeriodMonth(periodMonth_2_1);
        household2.addPeriodMonth(periodMonth_2_2);

        // article_Household_Account_Number
        Household.Article article_2_1_1 = new Household.Article(new ObjectId(), ACCOUNTS.get(0), CURRENCIES.get(1), "Household #2 Test Article 1", "Household #2 Article #1 for using in tests");
        Household.Article article_2_2_2 = new Household.Article(new ObjectId(), ACCOUNTS.get(1), CURRENCIES.get(1), "Household #2 Test Article 2", "Household #2 Article #2 for using in tests");
        Household.Article article_2_3_3 = new Household.Article(new ObjectId(), ACCOUNTS.get(2), CURRENCIES.get(1), "Household #2 Test Article 3", "Household #2 Article #3 for using in tests");

        household2.addArticle(article_2_1_1);
        household2.addArticle(article_2_2_2);
        household2.addArticle(article_2_3_3);

        Household.Item item_2_1 = new Household.Item(new ObjectId(), "Household #2 Test Item 1", "Household #2 Item #1 for using in tests");
        Household.Item item_2_2 = new Household.Item(new ObjectId(), "Household #2 Test Item 2", "Household #2 Item #2 for using in tests");
        Household.Item item_2_3 = new Household.Item(new ObjectId(), "Household #2 Test Item 3", "Household #2 Item #3 for using in tests");
        Household.Item item_2_4 = new Household.Item(new ObjectId(), "Household #2 Test Item 4", "Household #2 Item #4 for using in tests");
        Household.Item item_2_5 = new Household.Item(new ObjectId(), "Household #2 Test Item 5", "Household #2 Item #5 for using in tests");

        household2.addItem(item_2_1);
        household2.addItem(item_2_2);
        household2.addItem(item_2_3);
        household2.addItem(item_2_4);
        household2.addItem(item_2_5);

        HOUSEHOLDS = new ArrayList<>();
        HOUSEHOLDS.add(household1);
        HOUSEHOLDS.add(household2);

        ///////////////////////////////////////////////////////////////////////
        // OPERATIONS
        OPERATIONS = new ArrayList<>();

        // Null as collection.
        Set<Household.Item> items_1_1 = null;

        // Empty collection.
        Set<Household.Item> items_1_2 = new LinkedHashSet<>();

        // Collection with the only item.
        Set<Household.Item> items_1_3 = new LinkedHashSet<>();
        items_1_3.add(item_1_1);

        // Collection with many items.
        Set<Household.Item> items_2_1 = new LinkedHashSet<>();
        items_2_1.addAll(household2.getItems());

        Set<Household.Item> items_2_2 = new LinkedHashSet<>();
        items_2_2.addAll(household2.getItems());

        Operation operation_1_1 = new Operation(new ObjectId(), HOUSEHOLDS.get(0), periodMonth_1_1, LocalDate.of(2020, 1, 1), ACCOUNTS.get(0), article_1_1_1, ACCOUNTS.get(1), article_1_2_3, items_1_1, BigDecimal.valueOf(1), BigDecimal.valueOf(2000), "Household #1 Operation #1 for using in tests");
        Operation operation_1_2 = new Operation(new ObjectId(), HOUSEHOLDS.get(0), periodMonth_1_2, LocalDate.of(2020, 2, 1), ACCOUNTS.get(1), article_1_2_3, ACCOUNTS.get(2), article_1_3_5, items_1_2, BigDecimal.valueOf(1000000), BigDecimal.valueOf(2000000.20), "Household #1 Operation #2 for using in tests");
        Operation operation_1_3 = new Operation(new ObjectId(), HOUSEHOLDS.get(0), periodMonth_1_3, LocalDate.of(2020, 3, 1), ACCOUNTS.get(2), article_1_3_5, ACCOUNTS.get(0), article_1_1_2, items_1_3, BigDecimal.valueOf(10.10), BigDecimal.valueOf(0.20), "Household #1 Operation #3 for using in tests");
        Operation operation_1_4 = new Operation(new ObjectId(), HOUSEHOLDS.get(0), periodMonth_1_3, LocalDate.of(2020, 3, 9), ACCOUNTS.get(1), article_1_2_4, ACCOUNTS.get(2), article_1_3_6, items_1_3, BigDecimal.valueOf(10.10), BigDecimal.valueOf(0.01), "Household #1 Operation #4 for using in tests");
        Operation operation_2_1 = new Operation(new ObjectId(), HOUSEHOLDS.get(1), periodMonth_2_1, LocalDate.of(2020, 1, 1), ACCOUNTS.get(0), article_2_1_1, ACCOUNTS.get(1), article_2_2_2, items_2_1, BigDecimal.valueOf(1), BigDecimal.valueOf(2000), "Household #2 Operation #1 for using in tests");
        Operation operation_2_2 = new Operation(new ObjectId(), HOUSEHOLDS.get(1), periodMonth_2_2, LocalDate.of(2020, 4, 1), ACCOUNTS.get(1), article_2_2_2, ACCOUNTS.get(2), article_2_3_3, items_2_2, BigDecimal.valueOf(1000000), BigDecimal.valueOf(2000000.20), "Household #2 Operation #2 for using in tests");

        OPERATIONS.add(operation_1_1);
        OPERATIONS.add(operation_1_2);
        OPERATIONS.add(operation_1_3);
        OPERATIONS.add(operation_1_4);
        OPERATIONS.add(operation_2_1);
        OPERATIONS.add(operation_2_2);
    }

    public static List<Currency> getCurrencies() {
        return CURRENCIES;
    }

    public static List<Account> getAccounts() {
        return ACCOUNTS;
    }

    public static List<Household> getHouseholds() {
        return HOUSEHOLDS;
    }

    public static List<Operation> getOperations() {
        return OPERATIONS;
    }
}
