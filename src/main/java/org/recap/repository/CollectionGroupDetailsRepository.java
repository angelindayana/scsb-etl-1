package org.recap.repository;

import org.recap.model.jpa.CollectionGroupEntity;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * Created by angelind on 27/6/16.
 */
public interface CollectionGroupDetailsRepository extends PagingAndSortingRepository<CollectionGroupEntity, Integer> {

    CollectionGroupEntity findByCollectionGroupCode(String collectionGroupCode);

}
