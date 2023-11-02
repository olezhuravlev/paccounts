package org.paccounts.controller.rest;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.paccounts.config.CollectionsFactory;
import org.paccounts.dto.AccountDto;
import org.paccounts.model.Account;
import org.paccounts.service.ApiGate;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
public class AccountRestController {

    public static final String ACCOUNT_URL = "/account";
    public static final String ACCOUNT_KEY = "accounts";

    private final ApiGate apiGate;
    private final CollectionsFactory collectionsFactory;

    @PostMapping(ACCOUNT_URL)
    public ResponseEntity<Object> getAll() {

        List<Account> accounts = apiGate.getAccounts();

        HashMap<String, Object> result = collectionsFactory.getRestResponseCollection();
        result.put(ACCOUNT_KEY, accounts.stream().map(AccountDto::convert).toList());

        return ResponseEntity.ok(result);
    }

    @PostMapping(path = ACCOUNT_URL, params = "id")
    public ResponseEntity<Object> getById(@RequestParam("id") String id) {

        Optional<Account> account = apiGate.getAccountById(new ObjectId(id));
        if (account.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        HashMap<String, Object> result = collectionsFactory.getRestResponseCollection();
        result.put(ACCOUNT_KEY, AccountDto.convert(account.get()));

        return ResponseEntity.ok(result);
    }

    @PostMapping(path = ACCOUNT_URL + "/{code}")
    public ResponseEntity<Object> getByCode(@PathVariable("code") String code) {

        Optional<Account> account = apiGate.getAccountByCode(code);
        if (account.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        HashMap<String, Object> result = collectionsFactory.getRestResponseCollection();
        result.put(ACCOUNT_KEY, AccountDto.convert(account.get()));

        return ResponseEntity.ok(result);
    }

    @PutMapping(path = ACCOUNT_URL, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> save(@Valid @RequestBody AccountDto dto) {

        Account saved = apiGate.saveAccount(dto);

        HashMap<String, Object> result = collectionsFactory.getRestResponseCollection();
        result.put(ACCOUNT_KEY, AccountDto.convert(saved));

        return ResponseEntity.ok(result);
    }

    @DeleteMapping(path = ACCOUNT_URL, params = "id")
    public ResponseEntity<Object> deleteById(@RequestParam("id") String id) {
        apiGate.deleteAccountById(new ObjectId(id));
        return ResponseEntity.ok(new HashMap<>());
    }

    @DeleteMapping(path = ACCOUNT_URL + "/{code}")
    public ResponseEntity<Object> deleteByCode(@PathVariable("code") String code) {
        apiGate.deleteAccountByCode(code);
        return ResponseEntity.ok(new HashMap<>());
    }
}
