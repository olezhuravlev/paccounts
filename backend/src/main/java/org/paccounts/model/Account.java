package org.paccounts.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * An account to collect resources under (50, 51, 60, 62 etc.).
 */
@Document(collection = "accounts")
@AllArgsConstructor
@Data
public class Account {

    public enum Type {
        A, // Active - Asset
        P, // Passive - Liability
        AP // Active-passive
    }

    @Id
    private ObjectId id;
    private String code;
    private Type type;
    private String title;
    private String description;
}
