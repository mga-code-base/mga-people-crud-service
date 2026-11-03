package com.mygym.app.service;

import com.mygym.app.models.Admin;
import com.mygym.app.models.Member;
import com.mygym.app.models.Trainer;

final class PeopleFixtures {

    private PeopleFixtures() {
    }

    static Admin admin(String adminId, String username, String password, String mobile, String email, Boolean active) {
        return new Admin(null, adminId, null, username, password, mobile, email, active, null);
    }

    static Member member(String memberId, String username, String password) {
        return new Member(null, memberId, null, null, null, null, null, null, null, null, username, password, null, null, false, false);
    }

    static Trainer trainer(String trainerId, String username, String password, Boolean active) {
        return new Trainer(null, trainerId, null, username, password, null, null, active, null, null);
    }
}
