package org.paccounts.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Money and other high-liquid assets.
 */
@Document(collection = "currencies")
@AllArgsConstructor
@Data
public class Currency {

    @Id
    private ObjectId id;
    private String code;
    private String title;
    private String description;
}
