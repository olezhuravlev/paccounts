package org.paccounts.controller.rest;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.paccounts.config.CollectionsFactory;
import org.paccounts.dto.HouseholdDto;
import org.paccounts.model.Household;
import org.paccounts.service.ApiGate;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
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
public class HouseholdRestController {

    public static final String HOUSEHOLD_URL = "/household";
    public static final String HOUSEHOLD_KEY = "households";

    private final ApiGate apiGate;
    private final CollectionsFactory collectionsFactory;

    @PostMapping(HOUSEHOLD_URL)
    public ResponseEntity<Object> getAll() {

        List<Household> households = apiGate.getHouseholds();

        HashMap<String, Object> result = collectionsFactory.getRestResponseCollection();
        result.put(HOUSEHOLD_KEY, households.stream().map(HouseholdDto::convert).toList());

        return ResponseEntity.ok(result);
    }

    @PostMapping(path = HOUSEHOLD_URL, params = "id")
    public ResponseEntity<Object> getById(@RequestParam("id") String id) {

        Optional<Household> household = apiGate.getHouseholdById(new ObjectId(id));
        if (household.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        HashMap<String, Object> result = collectionsFactory.getRestResponseCollection();
        result.put(HOUSEHOLD_KEY, HouseholdDto.convert(household.get()));

        return ResponseEntity.ok(result);
    }

    @PutMapping(path = HOUSEHOLD_URL, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> save(@Valid @RequestBody HouseholdDto dto) {

        Household saved = apiGate.saveHousehold(dto);

        HashMap<String, Object> result = collectionsFactory.getRestResponseCollection();
        result.put(HOUSEHOLD_KEY, HouseholdDto.convert(saved));

        return ResponseEntity.ok(result);
    }

    @DeleteMapping(path = HOUSEHOLD_URL, params = "id")
    public ResponseEntity<Object> deleteById(@RequestParam("id") String id) {
        apiGate.deleteHouseholdById(new ObjectId(id));
        return ResponseEntity.ok(new HashMap<>());
    }
}
