package org.paccounts.service;

import org.bson.types.ObjectId;
import org.paccounts.dto.AccountDto;
import org.paccounts.dto.CurrencyDto;
import org.paccounts.dto.HouseholdDto;
import org.paccounts.dto.OperationDto;
import org.paccounts.model.Account;
import org.paccounts.model.Currency;
import org.paccounts.model.Household;
import org.paccounts.model.Operation;

import java.util.List;
import java.util.Optional;

public interface ApiGate {

    ///////////////////////////////////////////////////////////////////////////
    // CURRENCIES
    List<Currency> getCurrencies();

    Optional<Currency> getCurrencyById(ObjectId id);

    Optional<Currency> getCurrencyByCode(String id);

    Currency saveCurrency(CurrencyDto account);

    void deleteCurrencyById(ObjectId id);

    void deleteCurrencyByCode(String code);

    ///////////////////////////////////////////////////////////////////////////
    // ACCOUNTS
    List<Account> getAccounts();

    Optional<Account> getAccountById(ObjectId id);

    Optional<Account> getAccountByCode(String code);

    Account saveAccount(AccountDto dto);

    void deleteAccountById(ObjectId id);

    void deleteAccountByCode(String code);

    ///////////////////////////////////////////////////////////////////////////
    // HOUSEHOLDS
    List<Household> getHouseholds();

    Optional<Household> getHouseholdById(ObjectId id);

    Household saveHousehold(HouseholdDto dto);

    void deleteHouseholdById(ObjectId id);

    ///////////////////////////////////////////////////////////////////////////
    // OPERATIONS
    List<Operation> getOperations();

    Optional<Operation> getOperationById(ObjectId id);

    Operation saveOperation(OperationDto dto);

    void deleteOperationById(ObjectId id);

    void deleteOperationsAll();
}
