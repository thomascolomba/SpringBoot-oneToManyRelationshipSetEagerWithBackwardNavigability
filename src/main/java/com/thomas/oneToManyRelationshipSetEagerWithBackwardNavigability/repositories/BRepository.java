package com.thomas.oneToManyRelationshipSetEagerWithBackwardNavigability.repositories;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.thomas.oneToManyRelationshipSetEagerWithBackwardNavigability.domains.B;


public interface BRepository extends CrudRepository<B, Long> {
	List<B> findByMyInt(int myInt);
}