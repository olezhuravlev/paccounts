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
import org.paccounts.repository.AccountRepo;
import org.paccounts.repository.CurrencyRepo;
import org.paccounts.repository.HouseholdRepo;
import org.paccounts.repository.OperationRepo;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ApiGateImpl implements ApiGate {

    private final CurrencyRepo currencyRepo;
    private final AccountRepo accountRepo;
    private final HouseholdRepo householdRepo;
    private final OperationRepo operationRepo;

    public ApiGateImpl(CurrencyRepo currencyRepo, AccountRepo accountRepo, HouseholdRepo householdRepo, OperationRepo operationRepo) {

        this.currencyRepo = currencyRepo;
        this.accountRepo = accountRepo;
        this.householdRepo = householdRepo;
        this.operationRepo = operationRepo;
    }

    ///////////////////////////////////////////////////////////////////////////
    // CURRENCIES
    @Override
    public List<Currency> getCurrencies() {
        return currencyRepo.findAll();
    }

    @Override
    public Optional<Currency> getCurrencyById(ObjectId id) {
        return currencyRepo.findById(id);
    }

    @Override
    public Optional<Currency> getCurrencyByCode(String code) {
        return currencyRepo.findByCode(code);
    }

    @Override
    public Currency saveCurrency(CurrencyDto dto) {

        String code = dto.getCode();
        Optional<Currency> existing = currencyRepo.findByCode(code);

        Currency currency;
        if (existing.isEmpty()) {
            currency = new Currency(null, dto.getCode(), dto.getTitle(), dto.getDescription());
        } else {
            currency = existing.get();
            currency.setCode(dto.getCode());
            currency.setTitle(dto.getTitle());
            currency.setDescription(dto.getDescription());
        }

        return currencyRepo.save(currency);
    }

    @Override
    public void deleteCurrencyById(ObjectId id) {
        currencyRepo.deleteById(id);
    }

    @Override
    public void deleteCurrencyByCode(String code) {
        currencyRepo.deleteByCode(code);
    }

    ///////////////////////////////////////////////////////////////////////////
    // ACCOUNTS
    @Override
    public List<Account> getAccounts() {
        return accountRepo.findAll();
    }

    @Override
    public Optional<Account> getAccountById(ObjectId id) {
        return accountRepo.findById(id);
    }

    @Override
    public Optional<Account> getAccountByCode(String code) {
        return accountRepo.findByCode(code);
    }

    @Override
    public Account saveAccount(AccountDto dto) {

        String code = dto.getCode();
        Optional<Account> existing = accountRepo.findByCode(code);

        Account account;
        if (existing.isEmpty()) {
            account = new Account(null, dto.getCode(), Account.Type.valueOf(dto.getType()), dto.getTitle(), dto.getDescription());
        } else {
            account = existing.get();
            account.setCode(dto.getCode());
            account.setType(Account.Type.valueOf(dto.getType()));
            account.setTitle(dto.getTitle());
            account.setDescription(dto.getDescription());
        }

        return accountRepo.save(account);
    }

    @Override
    public void deleteAccountById(ObjectId id) {
        accountRepo.deleteById(id);
    }

    @Override
    public void deleteAccountByCode(String code) {
        accountRepo.deleteByCode(code);
    }

    ///////////////////////////////////////////////////////////////////////////
    // HOUSEHOLDS
    @Override
    public List<Household> getHouseholds() {
        return householdRepo.findAll();
    }

    @Override
    public Optional<Household> getHouseholdById(ObjectId id) {
        return householdRepo.findById(id);
    }

    @Override
    public Household saveHousehold(HouseholdDto dto) {

        Optional<Household> existing;
        String householdId = dto.getId();
        if (householdId == null) {
            existing = Optional.empty();
        } else {
            ObjectId id = extractId(householdId);
            existing = householdRepo.findById(id);
        }

        Household household;
        if (existing.isEmpty()) {
            household = new Household();
        } else {
            household = existing.get();
        }

        household.setTitle(dto.getTitle());
        household.setDescription(dto.getDescription());
        assignPeriodMonths(household, dto);
        assignArticles(household, dto);
        assignItems(household, dto);

        return householdRepo.save(household);
    }

    @Override
    public void deleteHouseholdById(ObjectId id) {
        householdRepo.deleteById(id);
    }

    private Household assignPeriodMonths(Household destination, HouseholdDto source) {

        Set<LocalDate> periodMonthSet = null;
        Set<String> periodMonthDtos = source.getPeriodMonths();
        if (periodMonthDtos != null) {
            periodMonthSet = periodMonthDtos.stream().map(LocalDate::parse).collect(Collectors.toSet());
        }
        destination.setPeriodMonths(periodMonthSet);

        return destination;
    }

    private Household assignArticles(Household destination, HouseholdDto source) {

        Set<Household.Article> articleSet = null;

        Set<HouseholdDto.ArticleDto> articleDtos = source.getArticles();
        if (articleDtos != null) {

            articleSet = new LinkedHashSet<>();

            // Retrieve related Currencies and Accounts.
            Set<ObjectId> currencyIds = new HashSet<>();
            Set<ObjectId> accountIds = new HashSet<>();
            for (HouseholdDto.ArticleDto dto : articleDtos) {

                String currencyId = dto.getCurrencyId();
                if (currencyId != null) {
                    currencyIds.add(extractId(currencyId));
                }

                String accountId = dto.getAccountId();
                if (accountId != null) {
                    accountIds.add(extractId(accountId));
                }
            }

            List<Currency> currenciesFound = currencyRepo.findAllById(currencyIds);
            List<Account> accountsFound = accountRepo.findAllById(accountIds);

            for (HouseholdDto.ArticleDto articleDto : articleDtos) {

                Household.Article article = new Household.Article();

                // Obtain ID.
                ObjectId id;
                String articleId = articleDto.getId();
                if (articleId == null) {
                    id = new ObjectId(); // MongoDb doesn't generate ID for nested documents.
                } else {
                    id = extractId(articleId);
                }
                article.setId(id);

                article.setTitle(articleDto.getTitle());
                article.setDescription(articleDto.getDescription());

                // Use retrieved Account.
                Account accountFound;
                String articleAccountIdStr = articleDto.getAccountId();
                if (articleAccountIdStr == null) {
                    accountFound = null;
                } else {
                    ObjectId articleAccountId = extractId(articleAccountIdStr);
                    accountFound = accountsFound.stream().filter(acc -> articleAccountId.equals(acc.getId())).findAny().orElse(null);
                }
                article.setAccount(accountFound);

                // Use retrieved Currency.
                Currency currencyFound;
                String articleCurrencyIdStr = articleDto.getCurrencyId();
                if (articleCurrencyIdStr == null) {
                    currencyFound = null;
                } else {
                    ObjectId articleCurrencyId = extractId(articleCurrencyIdStr);
                    currencyFound = currenciesFound.stream().filter(curr -> articleCurrencyId.equals(curr.getId())).findAny().orElse(null);
                }
                article.setCurrency(currencyFound);

                articleSet.add(article);
            }
        }

        destination.setArticles(articleSet);

        return destination;
    }

    private Household assignItems(Household destination, HouseholdDto source) {

        Set<Household.Item> itemSet = null;

        Set<HouseholdDto.ItemDto> itemDtos = source.getItems();
        if (itemDtos != null) {

            itemSet = new LinkedHashSet<>();

            for (HouseholdDto.ItemDto itemDto : itemDtos) {

                Household.Item item = new Household.Item();

                // Obtain ID.
                ObjectId id;
                String itemId = itemDto.getId();
                if (itemId == null) {
                    id = new ObjectId(); // MongoDb doesn't generate ID for nested documents.
                } else {
                    id = extractId(itemId);
                }
                item.setId(id);

                item.setTitle(itemDto.getTitle());
                item.setDescription(itemDto.getDescription());

                itemSet.add(item);
            }
        }

        destination.setItems(itemSet);

        return destination;
    }

    ///////////////////////////////////////////////////////////////////////////
    // OPERATIONS
    @Override
    public List<Operation> getOperations() {
        return operationRepo.findAll();
    }

    @Override
    public Optional<Operation> getOperationById(ObjectId id) {
        return operationRepo.findById(id);
    }

    @Override
    public Operation saveOperation(OperationDto dto) {

        Optional<Operation> existing;
        String operationId = dto.getId();

        ObjectId id = null;
        if (operationId == null) {
            existing = Optional.empty();
        } else {
            id = extractId(operationId);
            existing = operationRepo.findById(id);
        }

        Operation operation;
        if (existing.isEmpty()) {
            operation = new Operation();
            operation.setId(id);
        } else {
            operation = existing.get();
        }

        Household household = retrieveHousehold(dto);
        operation.setHousehold(household);

        Set<Account> householdAccounts = retrieveHouseholdAccounts(dto, household);

        operation.setAccountDt(retrieveAccountDt(dto, householdAccounts));
        operation.setAccountCt(retrieveAccountCt(dto, householdAccounts));
        operation.setArticleDt(retrieveArticleDt(dto, household));
        operation.setArticleCt(retrieveArticleCt(dto, household));
        operation.setItems(retrieveItems(dto, household));

        operation.setDate(dto.getDate());
        operation.setSum(dto.getSum());
        operation.setQuantity(dto.getQuantity());
        operation.setDescription(dto.getDescription());

        return operationRepo.save(operation);
    }

    private ObjectId extractId(String id) {

        ObjectId objectId;

        if (id == null) {
            return null;
        }

        try {
            objectId = new ObjectId(id);
        } catch (Exception e) {
            objectId = null;
        }

        return objectId;
    }

    private Household retrieveHousehold(OperationDto dto) {

        Household household = null;

        String householdIdStr = dto.getHouseholdId();
        if (householdIdStr != null) {
            ObjectId householdId = extractId(householdIdStr);
            if (householdId != null) {
                Optional<Household> householdFound = householdRepo.findById(householdId);
                if (householdFound.isPresent()) {
                    household = householdFound.get();
                }
            }
        }

        return household;
    }

    private Account retrieveAccountDt(OperationDto dto, Set<Account> householdAccounts) {

        Account account = null;

        AccountDto accountDt = dto.getAccountDt();
        if (!householdAccounts.isEmpty() && accountDt != null) {
            String accountDtIdStr = accountDt.getId();
            if (accountDtIdStr != null) {
                ObjectId accountDtId = extractId(accountDtIdStr);
                if (accountDt != null) {
                    account = householdAccounts.stream().filter(acc -> acc.getId().equals(accountDtId)).findAny().orElse(null);
                }
            }
        }

        return account;
    }

    private Account retrieveAccountCt(OperationDto dto, Set<Account> householdAccounts) {

        Account account = null;

        AccountDto accountCt = dto.getAccountCt();
        if (!householdAccounts.isEmpty() && accountCt != null) {
            String accountCtIdStr = accountCt.getId();
            if (accountCtIdStr != null) {
                ObjectId accountCtId = extractId(accountCtIdStr);
                if (accountCt != null) {
                    account = householdAccounts.stream().filter(acc -> acc.getId().equals(accountCtId)).findAny().orElse(null);
                }
            }
        }

        return account;
    }

    private Household.Article retrieveArticleDt(OperationDto dto, Household household) {

        if (household == null) {
            return null;
        }

        HouseholdDto.ArticleDto articleDt = dto.getArticleDt();
        if (articleDt == null) {
            return null;
        }

        Set<Household.Article> articles = household.getArticles();
        if (articles == null || articles.isEmpty()) {
            return null;
        }

        ObjectId articleDtId = extractId(articleDt.getId());
        if (articleDtId == null) {
            return null;
        }

        return articles.stream().filter(article -> article.getId().equals(articleDtId)).findAny().orElse(null);
    }

    private Household.Article retrieveArticleCt(OperationDto dto, Household household) {

        if (household == null) {
            return null;
        }

        HouseholdDto.ArticleDto articleCt = dto.getArticleCt();
        if (articleCt == null) {
            return null;
        }

        Set<Household.Article> articles = household.getArticles();
        if (articles == null || articles.isEmpty()) {
            return null;
        }

        ObjectId articleCtId = extractId(articleCt.getId());
        if (articleCtId == null) {
            return null;
        }

        return articles.stream().filter(article -> article.getId().equals(articleCtId)).findAny().orElse(null);
    }

    private Set<Household.Item> retrieveItems(OperationDto dto, Household household) {

        if (household == null) {
            return null;
        }

        Set<Household.Item> householdItems = household.getItems();
        if (householdItems == null || householdItems.isEmpty()) {
            return null;
        }

        Set<HouseholdDto.ItemDto> items = dto.getItems();
        if (items == null || items.isEmpty()) {
            return null;
        }

        Set<ObjectId> itemsIds = items.stream()
                .filter(itemDto -> itemDto != null)
                .map(itemDto -> itemDto.getId())
                .map(idStr -> extractId(idStr))
                .filter(id -> id != null)
                .collect(Collectors.toSet());

        if (itemsIds.isEmpty()) {
            return null;
        }

        Set<Household.Item> result = householdItems.stream()
                .filter(item -> itemsIds.contains(item.getId()))
                .collect(Collectors.toSet());

        // Items must be added either all together or no one of them.
        if (itemsIds.size() != result.size()) {
            return null;
        }

        return result;
    }

    private Set<Account> retrieveHouseholdAccounts(OperationDto dto, Household household) {

        if (household == null) {
            return new HashSet<>();
        }

        Set<ObjectId> accountsToFind = new HashSet<>();

        ObjectId accountDtId = null;
        AccountDto accountDt = dto.getAccountDt();
        if (accountDt != null) {
            accountDtId = extractId(accountDt.getId());
            if (accountDtId != null) {
                accountsToFind.add(accountDtId);
            }
        }

        ObjectId accountCtId;
        AccountDto accountCt = dto.getAccountCt();
        if (accountCt != null) {
            accountCtId = extractId(accountCt.getId());
            if (accountCtId != null) {
                accountsToFind.add(accountCtId);
            }
        }

        if (accountsToFind.isEmpty()) {
            return new HashSet<>();
        }

        Set<Account> accountsFound = accountRepo.findAllById(accountsToFind).stream().collect(Collectors.toSet());
        return accountsFound;
    }

    @Override
    public void deleteOperationById(ObjectId id) {
        operationRepo.deleteById(id);
    }

    @Override
    public void deleteOperationsAll() {
        operationRepo.deleteAll();
    }
}
