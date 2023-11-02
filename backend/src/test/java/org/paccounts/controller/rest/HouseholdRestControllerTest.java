package org.paccounts.controller.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.bson.types.ObjectId;
import org.paccounts.AbstractTest;
import org.paccounts.component.ModelAndViewPopulator;
import org.paccounts.config.CollectionsFactory;
import org.paccounts.dto.HouseholdDto;
import org.paccounts.exception.GlobalControllerAdvice;
import org.paccounts.model.Household;
import org.paccounts.service.ApiGate;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

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

@DisplayName("Test Household rest endpoints")
@WebMvcTest({HouseholdRestControllerTest.class, GlobalControllerAdvice.class})
@ContextConfiguration(classes = {HouseholdRestController.class, CollectionsFactory.class, ModelAndViewPopulator.class, GlobalControllerAdvice.class})
class HouseholdRestControllerTest extends AbstractTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    ApiGate apiGate;

    @DisplayName("Get all households")
    @Test
    void getAll() throws Exception {

        given(apiGate.getHouseholds()).willReturn(expected_households);

        List<HouseholdDto> expectedDtos = expected_households.stream().map(HouseholdDto::convert).collect(Collectors.toList());

        HashMap<String, Object> expectedResult = new HashMap<>();
        expectedResult.put(HouseholdRestController.HOUSEHOLD_KEY, expectedDtos);

        String expectedJson = new ObjectMapper().writeValueAsString(expectedResult);
        mockMvc.perform(post(HouseholdRestController.HOUSEHOLD_URL))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedJson));
    }

    @DisplayName("Get Household by id")
    @Test
    void getById() throws Exception {

        Household household = expected_households.get(0);
        ObjectId objectId = household.getId();
        given(apiGate.getHouseholdById(objectId)).willReturn(Optional.of(household));

        HashMap<String, Object> expectedResult = new HashMap<>();
        expectedResult.put(HouseholdRestController.HOUSEHOLD_KEY, HouseholdDto.convert(household));

        String expectedJson = new ObjectMapper().writeValueAsString(expectedResult);
        mockMvc.perform(post(HouseholdRestController.HOUSEHOLD_URL).param("id", objectId.toString()))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedJson));

        // Find by non-existing id.
        ObjectId unknownId = new ObjectId();
        mockMvc.perform(post(HouseholdRestController.HOUSEHOLD_URL).param("id", unknownId.toString()))
                .andExpect(status().isNotFound());
    }

    @DisplayName("Save Household")
    @Test
    void saveHousehold() throws Exception {

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
        String householdDtoJson = new ObjectMapper().writeValueAsString(householdDto);
        given(apiGate.saveHousehold(householdDto)).willReturn(household);

        HashMap<String, Object> expectedResult = new HashMap<>();
        expectedResult.put(HouseholdRestController.HOUSEHOLD_KEY, householdDto);
        String expectedJson = new ObjectMapper().writeValueAsString(expectedResult);

        mockMvc.perform(put(HouseholdRestController.HOUSEHOLD_URL)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(householdDtoJson))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedJson));
    }

    @DisplayName("Save Household's Article with wrong Account")
    @Test
    void saveHouseholdWrongAccount() throws Exception {

        Set<LocalDate> periodMonths = new LinkedHashSet<>();

        Set<Household.Article> articles = new LinkedHashSet<>();
        articles.add(new Household.Article(new ObjectId(), null, expected_currencies.get(0), "New Test Article 1", "New Test Article 1 description"));

        Set<Household.Item> items = new LinkedHashSet<>();

        Household household = new Household(new ObjectId(), "New Test Household title", "New Test Household description", periodMonths, articles, items);
        HouseholdDto householdDto = HouseholdDto.convert(household);
        String householdDtoJson = new ObjectMapper().writeValueAsString(householdDto);

        mockMvc.perform(put(HouseholdRestController.HOUSEHOLD_URL)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .content(householdDtoJson))
                .andExpect(status().isNotAcceptable())
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors", hasKey("articles_accountId")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors.articles_accountId[0]", hasToString("account-id-must-be-specified")));
    }

    @DisplayName("Save Household's Article with wrong Currency")
    @Test
    void saveHouseholdWrongCurrency() throws Exception {

        Set<LocalDate> periodMonths = new LinkedHashSet<>();

        Set<Household.Article> articles = new LinkedHashSet<>();
        articles.add(new Household.Article(new ObjectId(), expected_accounts.get(0), null, "New Test Article 1", "New Test Article 1 description"));

        Set<Household.Item> items = new LinkedHashSet<>();

        Household household = new Household(new ObjectId(), "New Test Household title", "New Test Household description", periodMonths, articles, items);
        HouseholdDto householdDto = HouseholdDto.convert(household);
        String householdDtoJson = new ObjectMapper().writeValueAsString(householdDto);

        mockMvc.perform(put(HouseholdRestController.HOUSEHOLD_URL)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .content(householdDtoJson))
                .andExpect(status().isNotAcceptable())
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors", hasKey("articles_currencyId")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors.articles_currencyId[0]", hasToString("currency-id-must-be-specified")));
    }

    @DisplayName("Delete Household by id")
    @Test
    void deleteHouseholdById() throws Exception {

        Household household = expected_households.get(0);
        ObjectId id = household.getId();
        doNothing().when(apiGate).deleteHouseholdById(id);

        String expectedJson = new ObjectMapper().writeValueAsString(new HashMap<>());

        mockMvc.perform(delete(HouseholdRestController.HOUSEHOLD_URL).param("id", id.toString()))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedJson));

        // Delete by non-existing id.
        ObjectId unknownId = new ObjectId();
        doNothing().when(apiGate).deleteHouseholdById(unknownId);

        mockMvc.perform(delete(HouseholdRestController.HOUSEHOLD_URL).param("id", unknownId.toString()))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedJson));
    }
}
