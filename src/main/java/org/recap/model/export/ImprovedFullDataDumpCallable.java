package org.recap.model.export;

import org.recap.model.jpa.BibliographicEntity;
import org.recap.model.jpa.ItemEntity;
import org.recap.model.search.DataDumpSearchResult;
import org.recap.repository.BibliographicDetailsRepository;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import javax.script.ScriptException;
import java.util.*;
import java.util.concurrent.Callable;

/**
 * Created by peris on 10/26/16.
 */
@Service
@Scope("prototype")
public class ImprovedFullDataDumpCallable implements Callable {

    List<LinkedHashMap> dataDumpSearchResults;

    BibliographicDetailsRepository bibliographicDetailsRepository;

    public ImprovedFullDataDumpCallable(List<LinkedHashMap> dataDumpSearchResults, BibliographicDetailsRepository bibliographicDetailsRepository) {
        this.dataDumpSearchResults = dataDumpSearchResults;
        this.bibliographicDetailsRepository = bibliographicDetailsRepository;

    }

    @Override
    public Object call() throws Exception {

        List<BibliographicEntity> bibliographicEntities = new ArrayList<>();

        for (Iterator<LinkedHashMap> iterator = dataDumpSearchResults.iterator(); iterator.hasNext(); ) {
            LinkedHashMap linkedHashMap = iterator.next();
            Integer bibId = (Integer) linkedHashMap.get("bibId");
            List<Integer> itemIds = (List<Integer>) linkedHashMap.get("itemIds");
            List<BibliographicEntity> bibliographicEntityList = bibliographicDetailsRepository.getBibliographicEntityList(Arrays.asList(bibId));
            BibliographicEntity bibliographicEntity = bibliographicEntityList.get(0);
            bibliographicEntity.getItemEntities();
            for (Iterator<BibliographicEntity> bibliographicEntityIterator = bibliographicEntityList.iterator(); bibliographicEntityIterator.hasNext(); ) {
                BibliographicEntity retrievedBibEntity =  bibliographicEntityIterator.next();
                List<ItemEntity> itemEntities = retrievedBibEntity.getItemEntities();

                List<ItemEntity> filteredItems = new ArrayList<>();
                for (Iterator<ItemEntity> itemEntityIterator = itemEntities.iterator(); itemEntityIterator.hasNext(); ) {
                    ItemEntity itemEntity = itemEntityIterator.next();
                    if(itemIds.contains(itemEntity.getItemId())){
                        filteredItems.add(itemEntity);
                    }
                }

                bibliographicEntity.setItemEntities(filteredItems);
                bibliographicEntities.add(bibliographicEntity);
            }
        }

        return bibliographicEntities;
    }
}
