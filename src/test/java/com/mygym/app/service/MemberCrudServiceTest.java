package com.mygym.app.service;

import static com.mygym.app.service.PeopleFixtures.member;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import com.mygym.app.models.Member;
import com.mygym.app.repository.MemberRepository;

@ExtendWith(MockitoExtension.class)
class MemberCrudServiceTest {

    @Mock
    private MemberRepository repository;

    @Mock
    private IdGenerationService idGenerationService;

    @Spy
    private PasswordService passwordService = new PasswordService();

    @InjectMocks
    private MemberCrudService memberCrudService;

    @Test
    @DisplayName("Should create member with the next sequence ID and a hashed password")
    void testCreateMember_Success() {
        when(repository.findByUsername("member1")).thenReturn(Optional.empty());
        when(idGenerationService.nextMemberId()).thenReturn("5001");
        when(repository.save(any(Member.class))).thenAnswer(i -> i.getArgument(0));

        Member created = memberCrudService.createMember(member(null, "member1", "pw"));

        assertEquals("5001", created.memberId());
        assertTrue(passwordService.matches("pw", created.password()));
    }

    @Test
    @DisplayName("Should return member with ID '-1' if username is taken")
    void testCreateMember_UsernameTaken_ReturnsMinusOneId() {
        when(repository.findByUsername("existing_user")).thenReturn(Optional.of(member("5001", "existing_user", "x")));

        Member created = memberCrudService.createMember(member(null, "existing_user", "pw"));

        assertEquals("-1", created.memberId());
        verify(repository, never()).save(any());
    }

    @Test
    @DisplayName("Should keep the stored password when an update omits it")
    void testUpdateMember_NoPassword_KeepsExistingHash() {
        String hash = passwordService.hash("pw");
        when(repository.findByMemberId("5001")).thenReturn(Optional.of(member("5001", "m", hash)));
        when(repository.save(any(Member.class))).thenAnswer(i -> i.getArgument(0));

        Member updated = memberCrudService.updateMember("5001", member("5001", "m", null));

        assertEquals(hash, updated.password());
    }

    @Test
    @DisplayName("Should approve membership and set start/end dates")
    void testApproveMembership_Success() {
        when(repository.findByMemberId("5001")).thenReturn(Optional.of(member("5001", "m", null)));

        assertTrue(memberCrudService.approveMembership("5001", "30"));

        ArgumentCaptor<Member> saved = ArgumentCaptor.forClass(Member.class);
        verify(repository).save(saved.capture());
        assertTrue(saved.getValue().isActive());
        assertEquals(LocalDate.now().toString(), saved.getValue().membershipStartDate());
        assertEquals(LocalDate.now().plusDays(30).toString(), saved.getValue().membershipEndDate());
    }

    @Test
    @DisplayName("Should update facial registration completed status")
    void testUpdateFaceRegistrationStatus_Success() {
        when(repository.findByMemberId("5001")).thenReturn(Optional.of(member("5001", "m", null)));

        assertTrue(memberCrudService.updateFaceRegistrationStatus("5001"));

        ArgumentCaptor<Member> saved = ArgumentCaptor.forClass(Member.class);
        verify(repository).save(saved.capture());
        assertTrue(saved.getValue().isFacialRegCompleted());
    }
}
