package org.paccounts.controller.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.bson.types.ObjectId;
import org.paccounts.AbstractTest;
import org.paccounts.component.ModelAndViewPopulator;
import org.paccounts.config.CollectionsFactory;
import org.paccounts.dto.OperationDto;
import org.paccounts.exception.GlobalControllerAdvice;
import org.paccounts.model.Account;
import org.paccounts.model.Household;
import org.paccounts.model.Operation;
import org.paccounts.service.ApiGate;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static org.hamcrest.Matchers.hasKey;
import static org.hamcrest.Matchers.hasToString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("Test Operation rest endpoints")
@WebMvcTest({OperationRestControllerTest.class, GlobalControllerAdvice.class})
@ContextConfiguration(classes = {OperationRestController.class, CollectionsFactory.class, ModelAndViewPopulator.class, GlobalControllerAdvice.class})
class OperationRestControllerTest extends AbstractTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    ApiGate apiGate;

    private static ObjectMapper OBJECT_MAPPER;

    @BeforeAll
    static void beforeAll() {
        // Having timestamps disabled LocalDate will be serialized as String (not as array).
        OBJECT_MAPPER = new ObjectMapper()
                .findAndRegisterModules()
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    @DisplayName("Get all operations")
    @Test
    void getAll() throws Exception {

        given(apiGate.getOperations()).willReturn(expected_operations);

        List<OperationDto> expectedDtos = expected_operations.stream().map(OperationDto::convert).collect(Collectors.toList());

        HashMap<String, Object> expectedResult = new HashMap<>();
        expectedResult.put(OperationRestController.OPERATION_KEY, expectedDtos);

        String expectedJson = OBJECT_MAPPER.writeValueAsString(expectedResult);
        mockMvc.perform(post(OperationRestController.OPERATION_URL))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedJson));
    }

    @DisplayName("Get Operation by id")
    @Test
    void getById() throws Exception {

        Operation operations = expected_operations.get(0);
        ObjectId objectId = operations.getId();
        given(apiGate.getOperationById(objectId)).willReturn(Optional.of(operations));

        HashMap<String, Object> expectedResult = new HashMap<>();
        expectedResult.put(OperationRestController.OPERATION_KEY, OperationDto.convert(operations));

        String expectedJson = OBJECT_MAPPER.writeValueAsString(expectedResult);
        mockMvc.perform(post(OperationRestController.OPERATION_URL).param("id", objectId.toString()))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedJson));

        // Find by non-existing id.
        ObjectId unknownId = new ObjectId();
        mockMvc.perform(post(OperationRestController.OPERATION_URL).param("id", unknownId.toString()))
                .andExpect(status().isNotFound());
    }

    @DisplayName("Save Operation")
    @Test
    void saveOperation() throws Exception {

        Household household = expected_households.get(0);
        LocalDate date = LocalDate.parse("2023-01-01");

        Account accountDt = expected_accounts.get(0);
        Account accountCt = expected_accounts.get(1);

        Set<Household.Article> articles = household.getArticles();
        Household.Article articleDt = articles.stream().filter(article -> accountDt.getId().equals(article.getAccount().getId())).findAny().get();
        Household.Article articleCt = articles.stream().filter(article -> accountCt.getId().equals(article.getAccount().getId())).findAny().get();

        Set<Household.Item> householdItems = household.getItems();

        Set<Household.Item> items = new LinkedHashSet<>();
        items.add(householdItems.stream().findAny().get());

        Operation operation = new Operation(new ObjectId(), household, date, date, accountDt, articleDt, accountCt, articleCt, items, BigDecimal.valueOf(11.11), BigDecimal.valueOf(22.22), "New Operation description");
        OperationDto operationDto = OperationDto.convert(operation);
        String operationDtoJson = OBJECT_MAPPER.writeValueAsString(operationDto);
        given(apiGate.saveOperation(operationDto)).willReturn(operation);

        HashMap<String, Object> expectedResult = new HashMap<>();
        expectedResult.put(OperationRestController.OPERATION_KEY, operationDto);
        String expectedJson = OBJECT_MAPPER.writeValueAsString(expectedResult);

        mockMvc.perform(put(OperationRestController.OPERATION_URL)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(operationDtoJson))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedJson));
    }

    @DisplayName("Save empty Operation")
    @Test
    void saveOperationEmpty() throws Exception {

        OperationDto operationDto = OperationDto.convert(new Operation());
        String operationDtoJson = OBJECT_MAPPER.writeValueAsString(operationDto);

        mockMvc.perform(put(OperationRestController.OPERATION_URL)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(operationDtoJson))
                .andExpect(status().isNotAcceptable())
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors", hasKey("date")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors.date[0]", hasToString("date-must-be-specified")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors", hasKey("householdId")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors.householdId[0]", hasToString("household-must-be-specified")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors", hasKey("accountDt")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors.accountDt[0]", hasToString("account-dt-must-be-specified")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors", hasKey("articleDt")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors.articleDt[0]", hasToString("article-dt-must-be-specified")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors", hasKey("accountCt")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors.accountCt[0]", hasToString("account-ct-must-be-specified")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors", hasKey("articleCt")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors.articleCt[0]", hasToString("article-ct-must-be-specified")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors", hasKey("items")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors.items[0]", hasToString("items-must-be-specified")));
    }

    @DisplayName("Delete Operation by id")
    @Test
    void deleteOperationById() throws Exception {

        Operation operations = expected_operations.get(0);
        ObjectId id = operations.getId();
        doNothing().when(apiGate).deleteOperationById(id);

        String expectedJson = new ObjectMapper().writeValueAsString(new HashMap<>());

        mockMvc.perform(delete(OperationRestController.OPERATION_URL).param("id", id.toString()))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedJson));

        // Delete by non-existing id.
        ObjectId unknownId = new ObjectId();
        doNothing().when(apiGate).deleteOperationById(unknownId);

        mockMvc.perform(delete(OperationRestController.OPERATION_URL).param("id", unknownId.toString()))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedJson));
    }
}
