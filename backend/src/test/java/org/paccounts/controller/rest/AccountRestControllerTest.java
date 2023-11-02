package org.paccounts.controller.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.bson.types.ObjectId;
import org.paccounts.AbstractTest;
import org.paccounts.component.ModelAndViewPopulator;
import org.paccounts.config.CollectionsFactory;
import org.paccounts.dto.AccountDto;
import org.paccounts.exception.GlobalControllerAdvice;
import org.paccounts.model.Account;
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

@DisplayName("Test Account rest endpoints")
@WebMvcTest({AccountRestControllerTest.class, GlobalControllerAdvice.class})
@ContextConfiguration(classes = {AccountRestController.class, CollectionsFactory.class, ModelAndViewPopulator.class, GlobalControllerAdvice.class})
class AccountRestControllerTest extends AbstractTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    ApiGate apiGate;

    @DisplayName("Get all accounts")
    @Test
    void getAll() throws Exception {

        given(apiGate.getAccounts()).willReturn(expected_accounts);

        List<AccountDto> expectedDtos = expected_accounts.stream().map(AccountDto::convert).collect(Collectors.toList());

        HashMap<String, Object> expectedResult = new HashMap<>();
        expectedResult.put(AccountRestController.ACCOUNT_KEY, expectedDtos);

        String expectedJson = new ObjectMapper().writeValueAsString(expectedResult);
        mockMvc.perform(post(AccountRestController.ACCOUNT_URL))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedJson));
    }

    @DisplayName("Get Account by id")
    @Test
    void getById() throws Exception {

        Account account = expected_accounts.get(0);
        ObjectId objectId = account.getId();
        given(apiGate.getAccountById(objectId)).willReturn(Optional.of(account));

        HashMap<String, Object> expectedResult = new HashMap<>();
        expectedResult.put(AccountRestController.ACCOUNT_KEY, AccountDto.convert(account));

        String expectedJson = new ObjectMapper().writeValueAsString(expectedResult);
        mockMvc.perform(post(AccountRestController.ACCOUNT_URL).param("id", objectId.toString()))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedJson));

        // Find by non-existing id.
        ObjectId unknownId = new ObjectId();
        mockMvc.perform(post(AccountRestController.ACCOUNT_URL).param("id", unknownId.toString()))
                .andExpect(status().isNotFound());
    }

    @DisplayName("Get Account by code")
    @Test
    void getByCode() throws Exception {

        Account account = expected_accounts.get(0);
        String code = account.getCode();
        given(apiGate.getAccountByCode(code)).willReturn(Optional.of(account));

        HashMap<String, Object> expectedResult = new HashMap<>();
        expectedResult.put(AccountRestController.ACCOUNT_KEY, AccountDto.convert(account));

        String expectedJson = new ObjectMapper().writeValueAsString(expectedResult);
        mockMvc.perform(post(AccountRestController.ACCOUNT_URL + "/{code}", code))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedJson));

        // Find by non-existing code.
        String unknownCode = new ObjectId().toString();
        mockMvc.perform(post(AccountRestController.ACCOUNT_URL + "/{code}", unknownCode))
                .andExpect(status().isNotFound());
    }

    @DisplayName("Save Account")
    @Test
    void saveAccount() throws Exception {

        Account account = new Account(new ObjectId(), "New Test Account code", Account.Type.A, "New Test Account title", "New Test Account Description");
        AccountDto accountDto = AccountDto.convert(account);
        String accountDtoJson = new ObjectMapper().writeValueAsString(accountDto);
        given(apiGate.saveAccount(accountDto)).willReturn(account);

        HashMap<String, Object> expectedResult = new HashMap<>();
        expectedResult.put(AccountRestController.ACCOUNT_KEY, accountDto);
        String expectedJson = new ObjectMapper().writeValueAsString(expectedResult);

        mockMvc.perform(put(AccountRestController.ACCOUNT_URL)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(accountDtoJson))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedJson));
    }

    @DisplayName("Save Account with wrong code")
    @Test
    void saveAccountWrongCode() throws Exception {

        List<Account> accounts = Arrays.asList(
                new Account(new ObjectId(), null, Account.Type.A, "New Test Account title", "New Test Account Description"),
                new Account(new ObjectId(), "", Account.Type.A, "New Test Account title", "New Test Account Description")
        );

        for (Account account : accounts) {

            AccountDto accountDto = AccountDto.convert(account);
            String accountDtoJson = new ObjectMapper().writeValueAsString(accountDto);

            mockMvc.perform(put(AccountRestController.ACCOUNT_URL)
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .accept(MediaType.APPLICATION_JSON_VALUE)
                            .content(accountDtoJson))
                    .andExpect(status().isNotAcceptable())
                    .andExpect(MockMvcResultMatchers.jsonPath("$.errors", hasKey("code")))
                    .andExpect(MockMvcResultMatchers.jsonPath("$.errors.code[0]", hasToString("code-must-be-specified")));
        }
    }

    @DisplayName("Save Account without specified type")
    @Test
    void saveAccountWrongType() throws Exception {

        Account account = new Account(new ObjectId(), "New Test Account code", null, "New Test Account title", "New Test Account Description");
        AccountDto accountDto = AccountDto.convert(account);
        String accountDtoJson = new ObjectMapper().writeValueAsString(accountDto);

        mockMvc.perform(put(AccountRestController.ACCOUNT_URL)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(accountDtoJson))
                .andExpect(status().isNotAcceptable())
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors", hasKey("type")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors.type[0]", hasToString("account-type-must-be-specified")));
    }

    @DisplayName("Save Account with wrong title")
    @Test
    void saveAccountWrongTitle() throws Exception {

        List<Account> accounts = Arrays.asList(
                new Account(new ObjectId(), "New Test Account code", Account.Type.A, null, "New Test Account Description"),
                new Account(new ObjectId(), "New Test Account code", Account.Type.A, "", "New Test Account Description"));

        for (Account account : accounts) {
            AccountDto accountDto = AccountDto.convert(account);
            String accountDtoJson = new ObjectMapper().writeValueAsString(accountDto);

            mockMvc.perform(put(AccountRestController.ACCOUNT_URL)
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .content(accountDtoJson))
                    .andExpect(status().isNotAcceptable())
                    .andExpect(MockMvcResultMatchers.jsonPath("$.errors", hasKey("title")))
                    .andExpect(MockMvcResultMatchers.jsonPath("$.errors.title[0]", hasToString("title-must-be-specified")));
        }
    }

    @DisplayName("Delete Account by id")
    @Test
    void deleteAccountById() throws Exception {

        Account account = expected_accounts.get(0);
        ObjectId id = account.getId();
        doNothing().when(apiGate).deleteAccountById(id);

        String expectedJson = new ObjectMapper().writeValueAsString(new HashMap<>());

        mockMvc.perform(delete(AccountRestController.ACCOUNT_URL).param("id", id.toString()))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedJson));

        // Delete by non-existing id.
        ObjectId unknownId = new ObjectId();
        doNothing().when(apiGate).deleteAccountById(unknownId);

        mockMvc.perform(delete(AccountRestController.ACCOUNT_URL).param("id", unknownId.toString()))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedJson));
    }

    @DisplayName("Delete Account by code")
    @Test
    void deleteAccountByCode() throws Exception {

        Account account = expected_accounts.get(0);
        String code = account.getCode();

        doNothing().when(apiGate).deleteAccountByCode(code);

        String expectedJson = new ObjectMapper().writeValueAsString(new HashMap<>());

        mockMvc.perform(delete(AccountRestController.ACCOUNT_URL + "/{code}", code))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedJson));

        // Delete by non-existing code.
        String unknownCode = new ObjectId().toString();
        doNothing().when(apiGate).deleteAccountByCode(unknownCode);

        mockMvc.perform(delete(AccountRestController.ACCOUNT_URL + "/{code}", unknownCode))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedJson));
    }
}
