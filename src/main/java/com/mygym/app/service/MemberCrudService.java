package com.mygym.app.service;

import java.time.LocalDate;
import java.util.List;
import java.util.function.UnaryOperator;

import org.springframework.stereotype.Service;

import com.mygym.app.models.Member;
import com.mygym.app.models.MemberWrapper;
import com.mygym.app.repository.MemberRepository;

@Service
public class MemberCrudService {

    private final MemberRepository repository;
    private final IdGenerationService idGenerationService;
    private final PasswordService passwordService;

    public MemberCrudService(MemberRepository repository, IdGenerationService idGenerationService, PasswordService passwordService) {
        this.repository = repository;
        this.idGenerationService = idGenerationService;
        this.passwordService = passwordService;
    }

    public Member createMember(Member member) {
        if (repository.findByUsername(member.username()).isPresent()) {
            return Member.notFound();
        }
        return repository.save(member
                .withMemberId(idGenerationService.nextMemberId())
                .withPassword(passwordService.hash(member.password())));
    }

    public List<Member> getAllMembers() {
        return repository.findAll();
    }

    public Member getMemberById(String id) {
        return repository.findByMemberId(id).orElse(Member.notFound());
    }

    public Member getMemberByUsername(String username) {
        return repository.findByUsername(username).orElse(Member.notFound());
    }

    public Member authenticate(String username, String password) {
        return repository.findByUsername(username)
                .filter(member -> passwordService.matches(password, member.password()))
                .map(member -> passwordService.needsUpgrade(member.password())
                        ? repository.save(member.withPassword(passwordService.hash(password)))
                        : member)
                .orElse(Member.notFound());
    }

    public Member updateMember(String id, Member updatedData) {
        return repository.findByMemberId(id)
                .map(existing -> repository.save(existing.withProfileFrom(updatedData,
                        passwordService.hashOrKeep(updatedData.password(), existing.password()))))
                .orElse(Member.notFound());
    }

    public Boolean deleteMember(String id) {
        repository.deleteByMemberId(id);
        return true;
    }

    public MemberWrapper findInactiveMembers() {
        return new MemberWrapper(repository.findInactiveMembers());
    }

    public MemberWrapper findByTrainerId(String trainerId) {
        return new MemberWrapper(repository.findByTrainerAssigned(trainerId));
    }

    public Boolean updateFaceRegistrationStatus(String memberId) {
        return modify(memberId, member -> member.withIsFacialRegCompleted(true));
    }

    public Boolean approveMembership(String id, String duration) {
        LocalDate today = LocalDate.now();
        return modify(id, member -> member.withMembership(true, today.toString(),
                today.plusDays(Long.parseLong(duration)).toString()));
    }

    public Boolean updateMembershipEndDate(String memberId, Integer duration) {
        return modify(memberId, member -> {
            LocalDate base = member.membershipEndDate() != null ? LocalDate.parse(member.membershipEndDate()) : LocalDate.now();
            return member.withMembership(member.isActive(), member.membershipStartDate(), base.plusDays(duration).toString());
        });
    }

    public Boolean updateTrainerForMember(String memberId, String trainerId) {
        return modify(memberId, member -> member.withTrainerAssigned(trainerId));
    }

    private Boolean modify(String memberId, UnaryOperator<Member> change) {
        repository.findByMemberId(memberId).ifPresent(member -> repository.save(change.apply(member)));
        return true;
    }
}
