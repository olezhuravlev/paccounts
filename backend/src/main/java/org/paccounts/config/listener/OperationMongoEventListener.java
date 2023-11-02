package org.paccounts.config.listener;

import com.mongodb.DBRef;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.paccounts.model.Household;
import org.paccounts.model.Operation;
import org.paccounts.repository.HouseholdRepo;
import org.springframework.data.mongodb.core.mapping.event.AbstractMongoEventListener;
import org.springframework.data.mongodb.core.mapping.event.AfterConvertEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class OperationMongoEventListener extends AbstractMongoEventListener<Operation> {

    private final HouseholdRepo householdRepo;

    public OperationMongoEventListener(HouseholdRepo householdRepo) {
        this.householdRepo = householdRepo;
    }

    @Override
    public void onAfterConvert(AfterConvertEvent<Operation> event) {

        super.onAfterConvert(event);

        Operation operation = event.getSource();
        Document document = event.getDocument();

        if (operation.getArticleDt() == null && document != null) {
            Household.Article articleDt = retrieveArticle("articleDt", document);
            operation.setArticleDt(articleDt);
        }

        if (operation.getArticleCt() == null && document != null) {
            Household.Article articleCt = retrieveArticle("articleCt", document);
            operation.setArticleCt(articleCt);
        }

        operation.setItems(retrieveItems(document));
    }

    private Household.Article retrieveArticle(Object key, Document document) {

        DBRef householdDbRef = (DBRef) document.get("household");
        if (householdDbRef == null) {
            return null;
        }

        DBRef articleDbRef = (DBRef) document.get(key);
        if (articleDbRef == null) {
            return null;
        }

        ObjectId householdId = (ObjectId) householdDbRef.getId();
        ObjectId articleId = (ObjectId) articleDbRef.getId();

        Optional<Household> household = householdRepo.findWithEmbeddedArticle(householdId, articleId);
        Set<Household.Article> articleSet = household.get().getArticles();
        List<Household.Article> article = new ArrayList<>(articleSet);

        return article.get(0);
    }

    private Set<Household.Item> retrieveItems(Document document) {

        List<DBRef> itemsDBRefs = (List<DBRef>) document.get("items");
        if (itemsDBRefs == null) {
            return null;
        }

        DBRef householdDbRef = (DBRef) document.get("household");
        ObjectId householdId = (ObjectId) householdDbRef.getId();

        List<ObjectId> itemsObjectIds = itemsDBRefs.stream()
                .map(dbRef -> (ObjectId) dbRef.getId())
                .toList();

        Optional<Household> household = householdRepo.findWithEmbeddedItems(householdId);
        Set<Household.Item> items = household.get().getItems().stream()
                .filter(item -> itemsObjectIds.contains(item.getId()))
                .collect(Collectors.toSet());

        return items;
    }
}
