package com.thomas.oneToManyRelationshipSetEagerWithBackwardNavigability.repositories;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.thomas.oneToManyRelationshipSetEagerWithBackwardNavigability.domains.A;

public interface ARepository extends CrudRepository<A, Long> {
	public List<A> findByMyString(String myString);
}