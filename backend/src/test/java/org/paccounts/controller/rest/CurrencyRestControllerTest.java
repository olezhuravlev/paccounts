package org.paccounts.controller.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.bson.types.ObjectId;
import org.paccounts.AbstractTest;
import org.paccounts.component.ModelAndViewPopulator;
import org.paccounts.config.CollectionsFactory;
import org.paccounts.dto.CurrencyDto;
import org.paccounts.exception.GlobalControllerAdvice;
import org.paccounts.model.Currency;
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

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
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

@DisplayName("Test Currency rest endpoints")
@WebMvcTest({CurrencyRestControllerTest.class, GlobalControllerAdvice.class})
@ContextConfiguration(classes = {CurrencyRestController.class, CollectionsFactory.class, ModelAndViewPopulator.class, GlobalControllerAdvice.class})
class CurrencyRestControllerTest extends AbstractTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    ApiGate apiGate;

    @DisplayName("Get all currencies")
    @Test
    void getAll() throws Exception {

        given(apiGate.getCurrencies()).willReturn(expected_currencies);

        List<CurrencyDto> expectedDtos = expected_currencies.stream().map(CurrencyDto::convert).collect(Collectors.toList());

        HashMap<String, Object> expectedResult = new HashMap<>();
        expectedResult.put(CurrencyRestController.CURRENCY_KEY, expectedDtos);

        String expectedJson = new ObjectMapper().writeValueAsString(expectedResult);
        mockMvc.perform(post(CurrencyRestController.CURRENCY_URL))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedJson));
    }

    @DisplayName("Get Currency by id")
    @Test
    void getById() throws Exception {

        Currency currency = expected_currencies.get(0);
        ObjectId objectId = currency.getId();
        given(apiGate.getCurrencyById(objectId)).willReturn(Optional.of(currency));

        HashMap<String, Object> expectedResult = new HashMap<>();
        expectedResult.put(CurrencyRestController.CURRENCY_KEY, CurrencyDto.convert(currency));

        String expectedJson = new ObjectMapper().writeValueAsString(expectedResult);
        mockMvc.perform(post(CurrencyRestController.CURRENCY_URL).param("id", objectId.toString()))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedJson));

        // Find by non-existing id.
        ObjectId unknownId = new ObjectId();
        mockMvc.perform(post(CurrencyRestController.CURRENCY_URL).param("id", unknownId.toString()))
                .andExpect(status().isNotFound());
    }

    @DisplayName("Get Currency by code")
    @Test
    void getByCode() throws Exception {

        Currency currency = expected_currencies.get(0);
        String code = currency.getCode();
        given(apiGate.getCurrencyByCode(code)).willReturn(Optional.of(currency));

        HashMap<String, Object> expectedResult = new HashMap<>();
        expectedResult.put(CurrencyRestController.CURRENCY_KEY, CurrencyDto.convert(currency));

        String expectedJson = new ObjectMapper().writeValueAsString(expectedResult);
        mockMvc.perform(post(CurrencyRestController.CURRENCY_URL + "/{code}", code))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedJson));

        // Find by non-existing code.
        String unknownCode = new ObjectId().toString();
        mockMvc.perform(post(CurrencyRestController.CURRENCY_URL + "/{code}", unknownCode))
                .andExpect(status().isNotFound());
    }

    @DisplayName("Save Currency")
    @Test
    void saveCurrency() throws Exception {

        Currency currency = new Currency(new ObjectId(), "New Test Currency code", "New Test Currency title", "New Test Currency Description");
        CurrencyDto currencyDto = CurrencyDto.convert(currency);
        String currencyDtoJson = new ObjectMapper().writeValueAsString(currencyDto);
        given(apiGate.saveCurrency(currencyDto)).willReturn(currency);

        HashMap<String, Object> expectedResult = new HashMap<>();
        expectedResult.put(CurrencyRestController.CURRENCY_KEY, currencyDto);
        String expectedJson = new ObjectMapper().writeValueAsString(expectedResult);

        mockMvc.perform(put(CurrencyRestController.CURRENCY_URL)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(currencyDtoJson))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedJson));
    }

    @DisplayName("Save Currency with wrong code")
    @Test
    void saveCurrencyWrongCode() throws Exception {

        List<Currency> currencys = Arrays.asList(
                new Currency(new ObjectId(), null, "New Test Currency title", "New Test Currency Description"),
                new Currency(new ObjectId(), "", "New Test Currency title", "New Test Currency Description")
        );

        for (Currency currency : currencys) {

            CurrencyDto currencyDto = CurrencyDto.convert(currency);
            String currencyDtoJson = new ObjectMapper().writeValueAsString(currencyDto);

            mockMvc.perform(put(CurrencyRestController.CURRENCY_URL)
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .accept(MediaType.APPLICATION_JSON_VALUE)
                            .content(currencyDtoJson))
                    .andExpect(status().isNotAcceptable())
                    .andExpect(MockMvcResultMatchers.jsonPath("$.errors", hasKey("code")))
                    .andExpect(MockMvcResultMatchers.jsonPath("$.errors.code[0]", hasToString("code-must-be-specified")));
        }
    }

    @DisplayName("Save Currency with wrong title")
    @Test
    void saveCurrencyWrongTitle() throws Exception {

        List<Currency> currencys = Arrays.asList(
                new Currency(new ObjectId(), "New Test Currency code", null, "New Test Currency Description"),
                new Currency(new ObjectId(), "New Test Currency code", "", "New Test Currency Description"));

        for (Currency currency : currencys) {
            CurrencyDto currencyDto = CurrencyDto.convert(currency);
            String currencyDtoJson = new ObjectMapper().writeValueAsString(currencyDto);

            mockMvc.perform(put(CurrencyRestController.CURRENCY_URL)
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .content(currencyDtoJson))
                    .andExpect(status().isNotAcceptable())
                    .andExpect(MockMvcResultMatchers.jsonPath("$.errors", hasKey("title")))
                    .andExpect(MockMvcResultMatchers.jsonPath("$.errors.title[0]", hasToString("title-must-be-specified")));
        }
    }

    @DisplayName("Delete Currency by id")
    @Test
    void deleteCurrencyById() throws Exception {

        Currency currency = expected_currencies.get(0);
        ObjectId id = currency.getId();
        doNothing().when(apiGate).deleteCurrencyById(id);

        String expectedJson = new ObjectMapper().writeValueAsString(new HashMap<>());

        mockMvc.perform(delete(CurrencyRestController.CURRENCY_URL).param("id", id.toString()))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedJson));

        // Delete by non-existing id.
        ObjectId unknownId = new ObjectId();
        doNothing().when(apiGate).deleteCurrencyById(unknownId);

        mockMvc.perform(delete(CurrencyRestController.CURRENCY_URL).param("id", unknownId.toString()))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedJson));
    }

    @DisplayName("Delete Currency by code")
    @Test
    void deleteCurrencyByCode() throws Exception {

        Currency currency = expected_currencies.get(0);
        String code = currency.getCode();

        doNothing().when(apiGate).deleteCurrencyByCode(code);

        String expectedJson = new ObjectMapper().writeValueAsString(new HashMap<>());

        mockMvc.perform(delete(CurrencyRestController.CURRENCY_URL + "/{code}", code))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedJson));

        // Delete by non-existing code.
        String unknownCode = new ObjectId().toString();
        doNothing().when(apiGate).deleteCurrencyByCode(unknownCode);

        mockMvc.perform(delete(CurrencyRestController.CURRENCY_URL + "/{code}", unknownCode))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedJson));
    }
}
