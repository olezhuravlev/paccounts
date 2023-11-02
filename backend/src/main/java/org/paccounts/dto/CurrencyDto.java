package org.paccounts.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.paccounts.model.Currency;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class CurrencyDto {

    private String id;

    @NotBlank(message = "code-must-be-specified")
    private String code;

    @NotBlank(message = "title-must-be-specified")
    private String title;

    private String description;

    public static CurrencyDto convert(Currency currency) {
        ObjectId objectId = currency.getId();
        String id = objectId == null ? null : objectId.toString();
        return new CurrencyDto(id, currency.getCode(), currency.getTitle(), currency.getDescription());
    }
}
