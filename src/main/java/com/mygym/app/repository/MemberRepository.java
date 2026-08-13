package com.mygym.app.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import com.mygym.app.models.Member;

@Repository
public interface MemberRepository extends MongoRepository<Member, String> {

	Optional<Member> findByMemberId(String memberId);
	
	Optional<Member> findByUsername(String username);
	
	List<Member> findByTrainerAssigned(String assignedTrainerId);
	
	void deleteByMemberId(String memberId);
	
	
    @Query(value = "{ 'memberId': { '$ne': 0 }, '$or': [ { 'isActive': null }, { 'isActive': false } ] }", 
            fields = "{ 'password': 0 }")
    List<Member> findInactiveMembers();
}
