package com.doitincloud.appserver.data;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(path="/dbkv")
public interface KeyValueRepository extends PagingAndSortingRepository<KeyValue, Long> {

    public KeyValue findByName(@Param("name") String name);

}
