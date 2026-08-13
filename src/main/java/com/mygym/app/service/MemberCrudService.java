package com.mygym.app.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

import org.springframework.stereotype.Service;

import com.mygym.app.models.Member;
import com.mygym.app.models.MemberWrapper;
import com.mygym.app.repository.MemberRepository;

/**
 * Service managing member lifecycle, trainer assignments, and membership duration extensions.
 */
@Service
public class MemberCrudService {

    private final MemberRepository repository;
    private final IdGenerationService service;
    
    public MemberCrudService(MemberRepository repository, IdGenerationService service) {
        this.service = service;
        this.repository = repository;
    }

    public Member createMember(Member member) {
        if (repository.findByUsername(member.getUsername()).isPresent()) {
            return new Member("-1");
        }
        member.setMemberId(String.valueOf(service.getMemberId()));
        service.incrementMemberId();
        return repository.save(member);
    }

    public List<Member> getAllMembers() {
        return repository.findAll();
    }

    public Member getMemberById(String id) {
        return repository.findByMemberId(id).map(member -> member).orElse(new Member("-1"));
    }
    
    public Member getMemberByUsername(String username) {
        return repository.findByUsername(username).map(member -> member).orElse(new Member("-1"));
    }

    public Member updateMember(String id, Member updatedData) {
        Member existing = getMemberById(id);
        existing.set_id(existing.get_id());
        existing.setName(updatedData.getName());
        existing.setMobileNumber(updatedData.getMobileNumber());
        existing.setEmailAddress(updatedData.getEmailAddress());
        existing.setMembershipStartDate(updatedData.getMembershipStartDate());
        existing.setMembershipEndDate(updatedData.getMembershipEndDate());
        existing.setIsPrivilegedMember(updatedData.getIsPrivilegedMember());
        existing.setLocalityPincode(updatedData.getLocalityPincode());
        existing.setPhoto(updatedData.getPhoto());
        existing.setUsername(updatedData.getUsername());
        existing.setPassword(updatedData.getPassword());
        existing.setTrainerAssigned(updatedData.getTrainerAssigned());
        existing.setAddedByAdminId(updatedData.getAddedByAdminId());
        return repository.save(existing);
    }

    public Boolean deleteMember(String id) {
        try {
            repository.deleteByMemberId(id);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    
    public MemberWrapper findInactiveMembers() {
        List<Member> inactiveMembers = repository.findInactiveMembers();

        return (Objects.isNull(inactiveMembers) || inactiveMembers.isEmpty()) 
                ? new MemberWrapper() 
                : new MemberWrapper(inactiveMembers);
    }
    
    public MemberWrapper findByTrainerId(String trainerId) {
        List<Member> members = repository.findByTrainerAssigned(trainerId);
        return (Objects.isNull(members) || members.isEmpty()) 
                ? new MemberWrapper() 
                : new MemberWrapper(members);
    }
    
    public Boolean updateFaceRegistrationStatus(String adminId) {
        repository.findByMemberId(adminId).ifPresent(admin -> {
            admin.setIsFacialRegCompleted(true);
            repository.save(admin);
        });
        return true;
    }

    public Boolean approveMembership(String id, String duration) {
        repository.findByMemberId(id).ifPresent(admin -> {
            admin.setIsActive(true);
            admin.setMembershipStartDate(LocalDate.now().toString());
            admin.setMembershipEndDate(LocalDate.now().plusDays(Long.valueOf(duration)).toString());
            repository.save(admin);
        });
        return true;
    }

    public Boolean updateMembershipEndDate(String memberId, Integer duration) {
        repository.findByMemberId(memberId).ifPresent(admin -> {
            if (Objects.nonNull(admin.getMembershipEndDate()))	
                admin.setMembershipEndDate(
                        LocalDate.parse(admin.getMembershipEndDate())
                        .plusDays(Long.valueOf(duration)).toString());
            else
                admin.setMembershipEndDate(LocalDate.now()
                        .plusDays(Long.valueOf(duration)).toString());
            repository.save(admin);
        });
        return true;
    }

    public Boolean updateTrainerForMember(String memberId, String trainerId) {
        repository.findByMemberId(memberId).ifPresent(admin -> {
            admin.setTrainerAssigned(trainerId);
            repository.save(admin);
        });
        return true;
    }
}