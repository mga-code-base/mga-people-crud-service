package com.mygym.app.service;

import com.mygym.app.models.Member;
import com.mygym.app.models.MemberWrapper;
import com.mygym.app.repository.MemberRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MemberCrudServiceTest {

    @Mock
    private MemberRepository repository;

    @Mock
    private IdGenerationService idGenerationService;

    @InjectMocks
    private MemberCrudService memberCrudService;

    @Test
    @DisplayName("Should create member and assign sequence ID successfully")
    void testCreateMember_Success() {
        Member input = new Member();
        input.setUsername("member1");

        when(repository.findByUsername("member1")).thenReturn(Optional.empty());
        when(idGenerationService.getMemberId()).thenReturn(5001L);
        when(repository.save(any(Member.class))).thenAnswer(i -> i.getArgument(0));

        Member created = memberCrudService.createMember(input);

        assertEquals("5001", created.getMemberId());
        verify(idGenerationService, times(1)).incrementMemberId();
    }

    @Test
    @DisplayName("Should return member with ID '-1' if username is taken")
    void testCreateMember_UsernameTaken_ReturnsMinusOneId() {
        Member input = new Member();
        input.setUsername("existing_user");

        when(repository.findByUsername("existing_user")).thenReturn(Optional.of(new Member("5001")));

        Member created = memberCrudService.createMember(input);

        assertEquals("-1", created.getMemberId());
        verify(repository, never()).save(any());
    }

    @Test
    @DisplayName("Should approve membership and set start/end dates")
    void testApproveMembership_Success() {
        Member member = new Member();
        member.setMemberId("5001");

        when(repository.findByMemberId("5001")).thenReturn(Optional.of(member));

        Boolean approved = memberCrudService.approveMembership("5001", "30");

        assertTrue(approved);
        assertTrue(member.getIsActive());
        assertEquals(LocalDate.now().toString(), member.getMembershipStartDate());
        assertEquals(LocalDate.now().plusDays(30).toString(), member.getMembershipEndDate());
        verify(repository, times(1)).save(member);
    }

    @Test
    @DisplayName("Should update facial registration completed status")
    void testUpdateFaceRegistrationStatus_Success() {
        Member member = new Member();
        member.setMemberId("5001");
        member.setIsFacialRegCompleted(false);

        when(repository.findByMemberId("5001")).thenReturn(Optional.of(member));

        Boolean updated = memberCrudService.updateFaceRegistrationStatus("5001");

        assertTrue(updated);
        assertTrue(member.getIsFacialRegCompleted());
        verify(repository, times(1)).save(member);
    }
}