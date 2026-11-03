package com.mygym.app.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.mygym.app.models.Admin;
import com.mygym.app.models.Member;
import com.mygym.app.models.MemberWrapper;
import com.mygym.app.models.Trainer;

class PasswordRedactingAdviceTest {

    private static Member member(String password) {
        return new Member(null, "1", null, null, null, null, null, null, null, null, "m", password, null, null, true, false);
    }

    @Test
    @DisplayName("Should strip password hashes from single entities")
    void testSingleEntities() {
        assertNull(((Member) PasswordRedactingAdvice.redact(member("$2a$hash"))).password());
        assertNull(((Admin) PasswordRedactingAdvice.redact(new Admin(null, "1", null, "a", "$2a$hash", null, null, true, "1"))).password());
        assertNull(((Trainer) PasswordRedactingAdvice.redact(new Trainer(null, "1", null, "t", "$2a$hash", null, null, true, null, null))).password());
    }

    @Test
    @DisplayName("Should strip password hashes inside wrappers and lists")
    void testCollections() {
        MemberWrapper wrapper = (MemberWrapper) PasswordRedactingAdvice.redact(new MemberWrapper(List.of(member("$2a$hash"))));
        assertNull(wrapper.allMembers().get(0).password());

        List<?> list = (List<?>) PasswordRedactingAdvice.redact(List.of(member("$2a$hash")));
        assertNull(((Member) list.get(0)).password());
    }

    @Test
    @DisplayName("Should leave other response types untouched")
    void testOtherTypes() {
        assertEquals(Boolean.TRUE, PasswordRedactingAdvice.redact(Boolean.TRUE));
        assertEquals("5001", PasswordRedactingAdvice.redact("5001"));
        assertNull(PasswordRedactingAdvice.redact(null));
    }
}
