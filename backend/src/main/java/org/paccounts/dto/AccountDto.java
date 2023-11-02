package org.paccounts.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.paccounts.model.Account;


@NoArgsConstructor
@AllArgsConstructor
@Data
public class AccountDto {

    private String id;

    @NotBlank(message = "code-must-be-specified")
    private String code;

    @NotBlank(message = "account-type-must-be-specified")
    private String type;

    @NotBlank(message = "title-must-be-specified")
    private String title;

    private String description;

    public static AccountDto convert(Account account) {

        ObjectId objectId = account.getId();
        String id = objectId == null ? null : objectId.toString();

        Account.Type accountType = account.getType();
        String accountTypeString = null;
        if (accountType != null) {
            accountTypeString = accountType.toString();
        }

        return new AccountDto(id, account.getCode(), accountTypeString, account.getTitle(), account.getDescription());
    }
}
