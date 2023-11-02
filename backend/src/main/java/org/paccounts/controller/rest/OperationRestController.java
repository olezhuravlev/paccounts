package org.paccounts.controller.rest;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.paccounts.config.CollectionsFactory;
import org.paccounts.dto.OperationDto;
import org.paccounts.model.Operation;
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
public class OperationRestController {

    public static final String OPERATION_URL = "/operation";
    public static final String OPERATION_KEY = "operations";

    private final ApiGate apiGate;
    private final CollectionsFactory collectionsFactory;

    @PostMapping(OPERATION_URL)
    public ResponseEntity<Object> getAll() {

        List<Operation> operations = apiGate.getOperations();

        HashMap<String, Object> result = collectionsFactory.getRestResponseCollection();
        result.put(OPERATION_KEY, operations.stream().map(OperationDto::convert).toList());

        return ResponseEntity.ok(result);
    }

    @PostMapping(path = OPERATION_URL, params = "id")
    public ResponseEntity<Object> getById(@RequestParam("id") String id) {

        Optional<Operation> operation = apiGate.getOperationById(new ObjectId(id));
        if (operation.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        HashMap<String, Object> result = collectionsFactory.getRestResponseCollection();
        result.put(OPERATION_KEY, OperationDto.convert(operation.get()));

        return ResponseEntity.ok(result);
    }

    @PutMapping(path = OPERATION_URL, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> save(@Valid @RequestBody OperationDto dto) {

        Operation saved = apiGate.saveOperation(dto);

        HashMap<String, Object> result = collectionsFactory.getRestResponseCollection();
        result.put(OPERATION_KEY, OperationDto.convert(saved));

        return ResponseEntity.ok(result);
    }

    @DeleteMapping(path = OPERATION_URL, params = "id")
    public ResponseEntity<Object> deleteById(@RequestParam("id") String id) {
        apiGate.deleteOperationById(new ObjectId(id));
        return ResponseEntity.ok(new HashMap<>());
    }

    @DeleteMapping(path = OPERATION_URL + "/all")
    public ResponseEntity<Object> deleteAll() {
        apiGate.deleteOperationsAll();
        return ResponseEntity.ok(new HashMap<>());
    }
}
