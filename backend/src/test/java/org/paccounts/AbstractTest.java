package org.paccounts;

import org.paccounts.model.Account;
import org.paccounts.model.Currency;
import org.paccounts.model.Household;
import org.paccounts.model.Operation;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractTest {

    protected final List<Currency> expected_currencies = new ArrayList<>(TestData.getCurrencies());
    protected final List<Account> expected_accounts = new ArrayList<>(TestData.getAccounts());
    protected final List<Household> expected_households = new ArrayList<>(TestData.getHouseholds());
    protected final List<Operation> expected_operations = new ArrayList<>(TestData.getOperations());
}
