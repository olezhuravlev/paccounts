package org.paccounts.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.paccounts.model.Account;
import org.paccounts.model.Household;
import org.paccounts.model.Operation;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;
import java.util.stream.Collectors;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class OperationDto {

    private String id;

    @NotBlank(message = "household-must-be-specified")
    private String householdId;

    private LocalDate periodMonth;

    @NotNull(message = "date-must-be-specified")
    private LocalDate date;

    @NotNull(message = "account-dt-must-be-specified")
    private AccountDto accountDt;

    @NotNull(message = "article-dt-must-be-specified")
    private HouseholdDto.ArticleDto articleDt;

    @NotNull(message = "account-ct-must-be-specified")
    private AccountDto accountCt;

    @NotNull(message = "article-ct-must-be-specified")
    private HouseholdDto.ArticleDto articleCt;

    @NotEmpty(message = "items-must-be-specified")
    private Set<HouseholdDto.ItemDto> items;

    private BigDecimal sum;
    private BigDecimal quantity;
    private String description;

    public static OperationDto convert(Operation operation) {

        ObjectId objectId = operation.getId();
        String idStr = objectId == null ? null : objectId.toString();

        String householdIdStr = null;
        Household household = operation.getHousehold();
        if (household != null) {
            householdIdStr = household.getId() == null ? null : household.getId().toString();
        }

        LocalDate date = operation.getDate();
        LocalDate periodMonth = null;
        if (date != null) {
            periodMonth = date.withDayOfMonth(1);
        }

        AccountDto accountDtDto = null;
        Account accountDt = operation.getAccountDt();
        if (accountDt != null) {
            accountDtDto = AccountDto.convert(accountDt);
        }

        HouseholdDto.ArticleDto articleDtDto = null;
        Household.Article articleDt = operation.getArticleDt();
        if (articleDt != null) {
            articleDtDto = HouseholdDto.ArticleDto.convert(articleDt);
        }

        AccountDto accountCtDto = null;
        Account accountCt = operation.getAccountCt();
        if (accountCt != null) {
            accountCtDto = AccountDto.convert(accountCt);
        }

        HouseholdDto.ArticleDto articleCtDto = null;
        Household.Article articleCt = operation.getArticleCt();
        if (articleCt != null) {
            articleCtDto = HouseholdDto.ArticleDto.convert(articleCt);
        }

        Set<HouseholdDto.ItemDto> itemsDto = null;
        Set<Household.Item> items = operation.getItems();
        if (items != null) {
            itemsDto = items.stream().filter(item -> item != null).map(item -> HouseholdDto.ItemDto.convert(item)).collect(Collectors.toSet());
        }

        BigDecimal sum = operation.getSum();
        BigDecimal quantity = operation.getQuantity();
        String description = operation.getDescription();

        return new OperationDto(idStr, householdIdStr, periodMonth, date, accountDtDto, articleDtDto, accountCtDto, articleCtDto, itemsDto, sum, quantity, description);
    }
}
