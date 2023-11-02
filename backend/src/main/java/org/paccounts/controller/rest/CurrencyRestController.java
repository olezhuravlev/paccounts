package org.paccounts.controller.rest;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.paccounts.config.CollectionsFactory;
import org.paccounts.dto.CurrencyDto;
import org.paccounts.model.Currency;
import org.paccounts.service.ApiGate;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class CurrencyRestController {

    public static final String CURRENCY_URL = "/currency";
    public static final String CURRENCY_KEY = "currencies";

    private final ApiGate apiGate;
    private final CollectionsFactory collectionsFactory;

    @PostMapping(CURRENCY_URL)
    @ResponseBody
    public ResponseEntity<Object> getAll() {

        List<Currency> currencies = apiGate.getCurrencies();

        HashMap<String, Object> result = collectionsFactory.getRestResponseCollection();
        result.put(CURRENCY_KEY, currencies.stream().map(CurrencyDto::convert).toList());

        return ResponseEntity.ok(result);
    }

    @PostMapping(path = CURRENCY_URL, params = "id")
    public ResponseEntity<Object> getById(@RequestParam("id") String id) {

        Optional<Currency> currency = apiGate.getCurrencyById(new ObjectId(id));
        if (currency.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        HashMap<String, Object> result = collectionsFactory.getRestResponseCollection();
        result.put(CURRENCY_KEY, CurrencyDto.convert(currency.get()));

        return ResponseEntity.ok(result);
    }

    @PostMapping(path = CURRENCY_URL + "/{code}")
    public ResponseEntity<Object> getByCode(@PathVariable("code") String code) {

        Optional<Currency> currency = apiGate.getCurrencyByCode(code);
        if (currency.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        HashMap<String, Object> result = collectionsFactory.getRestResponseCollection();
        result.put(CURRENCY_KEY, CurrencyDto.convert(currency.get()));

        return ResponseEntity.ok(result);
    }

    @PutMapping(path = CURRENCY_URL, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> save(@Valid @RequestBody CurrencyDto dto) {

        Currency saved = apiGate.saveCurrency(dto);

        HashMap<String, Object> result = collectionsFactory.getRestResponseCollection();
        result.put(CURRENCY_KEY, CurrencyDto.convert(saved));

        return ResponseEntity.ok(result);
    }

    @DeleteMapping(path = CURRENCY_URL, params = "id")
    public ResponseEntity<Object> deleteById(@RequestParam("id") String id) {
        apiGate.deleteCurrencyById(new ObjectId(id));
        return ResponseEntity.ok(new HashMap<>());
    }

    @DeleteMapping(path = CURRENCY_URL + "/{code}")
    public ResponseEntity<Object> deleteByCode(@PathVariable("code") String code) {
        apiGate.deleteCurrencyByCode(code);
        return ResponseEntity.ok(new HashMap<>());
    }
}
