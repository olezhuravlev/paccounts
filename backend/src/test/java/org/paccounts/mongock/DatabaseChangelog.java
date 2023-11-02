package org.paccounts.mongock;

import com.github.cloudyrock.mongock.ChangeLog;
import com.github.cloudyrock.mongock.ChangeSet;
import com.mongodb.client.MongoDatabase;
import org.paccounts.TestData;
import org.paccounts.model.Account;
import org.paccounts.model.Currency;
import org.paccounts.model.Household;
import org.paccounts.model.Operation;
import org.paccounts.repository.AccountRepo;
import org.paccounts.repository.CurrencyRepo;
import org.paccounts.repository.HouseholdRepo;
import org.paccounts.repository.OperationRepo;

import java.util.List;

@ChangeLog
public class DatabaseChangelog {

    private final List<Currency> currencies;
    private final List<Account> accounts;
    private final List<Household> households;
    private final List<Operation> operations;

    public DatabaseChangelog() {
        currencies = TestData.getCurrencies();
        accounts = TestData.getAccounts();
        households = TestData.getHouseholds();
        operations = TestData.getOperations();
    }

    @ChangeSet(order = "000", id = "dropDb", author = "olezhuravlev", runAlways = true)
    public void dropDb(MongoDatabase db) {
        db.drop();
    }

    @ChangeSet(order = "001", id = "insertCurrencies", author = "olezhuravlev")
    public void insertCurrencies(CurrencyRepo repo) {
        repo.saveAll(currencies);
    }

    @ChangeSet(order = "002", id = "insertAccounts", author = "olezhuravlev")
    public void insertAccounts(AccountRepo repo) {
        repo.saveAll(accounts);
    }

    @ChangeSet(order = "003", id = "insertHouseholds", author = "olezhuravlev")
    public void insertHouseholds(HouseholdRepo repo) {
        repo.saveAll(households);
    }

    @ChangeSet(order = "004", id = "insertOperations", author = "olezhuravlev")
    public void insertOperations(OperationRepo repo) {
        repo.saveAll(operations);
    }
}
