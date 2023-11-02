package org.paccounts.service;

import org.bson.types.ObjectId;
import org.paccounts.AbstractTest;
import org.paccounts.ApplicationTestConfig;
import org.paccounts.dto.AccountDto;
import org.paccounts.dto.CurrencyDto;
import org.paccounts.dto.HouseholdDto;
import org.paccounts.model.Account;
import org.paccounts.model.Currency;
import org.paccounts.model.Household;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.time.Duration;
import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("ApiGate tests")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@ContextConfiguration(classes = {ApplicationTestConfig.class})
class ApiGateImplTest extends AbstractTest {

    @Autowired
    private ApiGate apiGate;

    private static final String MONGO_IMAGE_NAME = "mongo:6.0.5";

    @Container
    static MongoDBContainer mongoDBContainer = new MongoDBContainer(DockerImageName.parse(MONGO_IMAGE_NAME))
            //.withCopyFileToContainer(MountableFile.forClasspathResource("/mongo-init"), "/initdb")
            .withStartupTimeout(Duration.ofSeconds(3))
            .waitingFor(Wait.forListeningPort());

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        mongoDBContainer.start();
        //mongoDBContainer.execInContainer("./initdb/mongoimport.sh");
        registry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
    }

    ///////////////////////////////////////////////////////////////////////////
    // CURRENCIES
    @DisplayName("Retrieve all currencies from DB")
    @Test
    void getCurrencies() {
        List<Currency> currencies = apiGate.getCurrencies();
        assertThat(currencies).containsExactlyInAnyOrderElementsOf(expected_currencies);
    }

    @DisplayName("Find Currency by id")
    @Test
    void getCurrencyById() {

        Currency expected = expected_currencies.get(0);
        ObjectId id = expected.getId();

        Optional<Currency> found = apiGate.getCurrencyById(id);
        assertThat(found).isPresent().get().isEqualTo(expected);
    }

    @DisplayName("Save new Currency")
    @Test
    @DirtiesContext
    void saveCurrency() {

        CurrencyDto dto = new CurrencyDto(null, "4", "New Test Currency 1", "New Currency #1 for using in tests");
        Currency saved = apiGate.saveCurrency(dto);

        ObjectId id = saved.getId();
        Optional<Currency> found = apiGate.getCurrencyById(id);
        assertThat(saved).isEqualTo(found.get());
    }

    @DisplayName("Delete Currency by id")
    @Test
    @DirtiesContext
    void deleteCurrencyById() {

        Currency expected = expected_currencies.get(0);
        ObjectId id = expected.getId();

        Optional<Currency> found = apiGate.getCurrencyById(id);
        assertThat(found).isPresent();

        apiGate.deleteCurrencyById(id);
        Optional<Currency> deleted = apiGate.getCurrencyById(id);
        assertThat(deleted).isNotPresent();
    }

    ///////////////////////////////////////////////////////////////////////////
    // ACCOUNTS
    @DisplayName("Retrieve all accounts from DB")
    @Test
    void getAccounts() {
        List<Account> accounts = apiGate.getAccounts();
        assertThat(accounts).containsExactlyInAnyOrderElementsOf(expected_accounts);
    }

    @DisplayName("Find Account by id")
    @Test
    void getAccountById() {

        Account expected = expected_accounts.get(0);
        ObjectId id = expected.getId();

        Optional<Account> found = apiGate.getAccountById(id);
        assertThat(found).isPresent().get().isEqualTo(expected);
    }

    @DisplayName("Save new Account")
    @Test
    @DirtiesContext
    void saveAccount() {

        AccountDto dto = new AccountDto(null, "New Test Account code", Account.Type.A.toString(), "New Test Account title", "New Test Account Description");
        Account savedAccount = apiGate.saveAccount(dto);

        ObjectId id = savedAccount.getId();
        Optional<Account> found = apiGate.getAccountById(id);
        assertThat(savedAccount).isEqualTo(found.get());
    }

    @DisplayName("Delete Account by id")
    @Test
    @DirtiesContext
    void deleteAccountById() {

        Account expected = expected_accounts.get(0);
        ObjectId id = expected.getId();

        Optional<Account> found = apiGate.getAccountById(id);
        assertThat(found).isPresent();

        apiGate.deleteAccountById(id);
        Optional<Account> deleted = apiGate.getAccountById(id);
        assertThat(deleted).isNotPresent();
    }

    ///////////////////////////////////////////////////////////////////////////
    // HOUSEHOLDS
    @DisplayName("Retrieve all households from DB")
    @Test
    void getHouseholds() {
        List<Household> households = apiGate.getHouseholds();
        assertThat(households).containsExactlyInAnyOrderElementsOf(expected_households);
    }

    @DisplayName("Find Household by id")
    @Test
    void getHouseholdById() {

        Household expected = expected_households.get(0);
        ObjectId id = expected.getId();

        Optional<Household> found = apiGate.getHouseholdById(id);
        assertThat(found).isPresent().get().isEqualTo(expected);
    }

    @DisplayName("Save new Household")
    @Test
    @DirtiesContext
    void saveHousehold() {

        Set<LocalDate> periodMonths = new LinkedHashSet<>();
        periodMonths.add(LocalDate.parse("2020-01-01"));
        periodMonths.add(LocalDate.parse("2020-02-15"));
        periodMonths.add(LocalDate.parse("2020-03-31"));

        Set<Household.Article> articles = new LinkedHashSet<>();
        articles.add(new Household.Article(new ObjectId(), expected_accounts.get(0), expected_currencies.get(0), "New Test Article 1", "New Test Article 1 description"));
        articles.add(new Household.Article(new ObjectId(), expected_accounts.get(1), expected_currencies.get(1), "New Test Article 2", "New Test Article 2 description"));
        articles.add(new Household.Article(new ObjectId(), expected_accounts.get(2), expected_currencies.get(2), "New Test Article 3", "New Test Article 3 description"));

        Set<Household.Item> items = new LinkedHashSet<>();
        items.add(new Household.Item(new ObjectId(), "New Test Article 1", "New Test Article 1 description"));
        items.add(new Household.Item(new ObjectId(), "New Test Article 2", "New Test Article 2 description"));
        items.add(new Household.Item(new ObjectId(), "New Test Article 3", "New Test Article 3 description"));

        Household household = new Household(new ObjectId(), "New Test Household title", "New Test Household description", periodMonths, articles, items);
        HouseholdDto householdDto = HouseholdDto.convert(household);

        Household saved = apiGate.saveHousehold(householdDto);

        ObjectId id = saved.getId();

        Optional<Household> found = apiGate.getHouseholdById(id);
        assertThat(saved).isEqualTo(found.get());
    }

    @DisplayName("Save new Household with empty collections")
    @Test
    @DirtiesContext
    void saveHouseholdEmpty() {

        Set<LocalDate> periodMonths = new LinkedHashSet<>();
        Set<Household.Article> articles = new LinkedHashSet<>();
        Set<Household.Item> items = new LinkedHashSet<>();

        Household household = new Household(new ObjectId(), "New Test Household title", "New Test Household description", periodMonths, articles, items);
        HouseholdDto householdDto = HouseholdDto.convert(household);

        Household saved = apiGate.saveHousehold(householdDto);

        ObjectId id = saved.getId();

        Optional<Household> found = apiGate.getHouseholdById(id);
        assertThat(saved).isEqualTo(found.get());
    }

    @DisplayName("Delete Household by id")
    @Test
    @DirtiesContext
    void deleteHouseholdById() {

        Household expected = expected_households.get(0);
        ObjectId id = expected.getId();

        Optional<Household> found = apiGate.getHouseholdById(id);
        assertThat(found).isPresent();

        apiGate.deleteHouseholdById(id);
        Optional<Household> deleted = apiGate.getHouseholdById(id);
        assertThat(deleted).isNotPresent();
    }

    ///////////////////////////////////////////////////////////////////////////
    // OPERATIONS
    @DisplayName("Retrieve all operations from DB")
    @Test
    void getOperations() {
//        List<Operation> operations = apiGate.getOperations();
//        assertThat(operations).containsExactlyInAnyOrderElementsOf(expected_operations);
    }

    @DisplayName("Find Operation by id")
    @Test
    void getOperationById() {

//        Operation expected = expected_operations.get(0);
//        ObjectId id = expected.getId();
//
//        Optional<Operation> found = apiGate.getOperationById(id);
//        assertThat(found).isPresent().get().isEqualTo(expected);
    }

    @DisplayName("Save new Operation")
    @Test
    @DirtiesContext
    void saveOperation() {

//        Household household = expected_households.get(0);
//        List<Household.Article> articles = new ArrayList<>(household.getArticles());
//        Set<Household.Item> items = household.getItems();
//
//        LocalDate date = expected_period_months.get(0).getDate().plusDays(10);
//
//        Operation operation = new Operation(household,
//                expected_period_months.get(0),
//                date,
//                expected_accounts.get(0), articles.get(0),
//                expected_accounts.get(1), articles.get(1),
//                items,
//                BigDecimal.valueOf(1), BigDecimal.valueOf(2), "New Operation #1 for using in tests");
//
//        Operation saved = apiGate.saveOperation(operation);
//
//        ObjectId id = saved.getId();
//        Optional<Operation> found = apiGate.getOperationById(id);
//        assertThat(saved).usingRecursiveComparison().isEqualTo(found.get());
    }

    @DisplayName("Delete Operation by id")
    @Test
    @DirtiesContext
    void deleteOperationById() {

//        for (Operation expected : expected_operations) {
//
//            ObjectId id = expected.getId();
//
//            Optional<Operation> found = apiGate.getOperationById(id);
//            assertThat(found).isPresent();
//
//            apiGate.deleteOperationById(id);
//
//            Optional<Operation> deleted = apiGate.getOperationById(id);
//            assertThat(deleted).isNotPresent();
//        }
    }
}
