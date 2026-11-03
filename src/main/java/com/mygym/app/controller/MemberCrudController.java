package com.mygym.app.controller;

import static com.mygym.app.util.PeopleCrudConstants.AUTHENTICATE_ENDPOINT;
import static com.mygym.app.util.PeopleCrudConstants.GET_BY_TRAINER_ID_ENDPOINT;
import static com.mygym.app.util.PeopleCrudConstants.ID_PLACEHOLDER_ENDPOINT;
import static com.mygym.app.util.PeopleCrudConstants.INACTIVE_MEMBER_ENDPOINT;
import static com.mygym.app.util.PeopleCrudConstants.MEMBER_CRUD_REST_BASE;
import static com.mygym.app.util.PeopleCrudConstants.UPDATE_END_DATE_ENDPOINT;
import static com.mygym.app.util.PeopleCrudConstants.UPDATE_FACE_REG_STATUS_ENDPOINT;
import static com.mygym.app.util.PeopleCrudConstants.UPDATE_MEMBERSHIP_STATUS_ENDPOINT;
import static com.mygym.app.util.PeopleCrudConstants.UPDATE_TRAINER_ID_ENDPOINT;
import static com.mygym.app.util.PeopleCrudConstants.USERNAME_PLACEHOLDER_ENDPOINT;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.mygym.app.dto.LoginRequest;
import com.mygym.app.models.Member;
import com.mygym.app.models.MemberWrapper;
import com.mygym.app.service.MemberCrudService;

@RestController
@RequestMapping(MEMBER_CRUD_REST_BASE)
public class MemberCrudController {

    private final MemberCrudService service;

    public MemberCrudController(MemberCrudService service) {
        this.service = service;
    }

    @PostMapping
    public Member create(@RequestBody Member member) {
        return service.createMember(member);
    }

    @GetMapping
    public List<Member> readAll() {
        return service.getAllMembers();
    }
    
    @GetMapping(INACTIVE_MEMBER_ENDPOINT)
    public MemberWrapper readAllInactive() {
        return service.findInactiveMembers();
    }

    @GetMapping(ID_PLACEHOLDER_ENDPOINT)
    public Member readById(@PathVariable("id") String id) {
        return service.getMemberById(id);
    }
    
    @GetMapping(USERNAME_PLACEHOLDER_ENDPOINT)
    public Member readByUsername(@PathVariable("username") String username) {
        return service.getMemberByUsername(username);
    }

    @PostMapping(AUTHENTICATE_ENDPOINT)
    public Member authenticate(@RequestBody LoginRequest request) {
        return service.authenticate(request.username(), request.password());
    }

    @PutMapping(ID_PLACEHOLDER_ENDPOINT)
    public Member update(@PathVariable("id") String id, @RequestBody Member member) {
        return service.updateMember(id, member);
    }
    
    @PutMapping(UPDATE_END_DATE_ENDPOINT)
    public Boolean updateMembershipEndDay(@PathVariable("id") String memberId, @RequestParam("duration") Integer duration) {
    	return service.updateMembershipEndDate(memberId, duration);
    }
    
    @PutMapping(UPDATE_TRAINER_ID_ENDPOINT)
    public Boolean updateTrainerForMember(@PathVariable("id") String memberId, @RequestParam("trainerId") String trainerId) {
    	return service.updateTrainerForMember(memberId, trainerId);
    }

    @DeleteMapping(ID_PLACEHOLDER_ENDPOINT)
    public Boolean delete(@PathVariable("id") String id) {
        return service.deleteMember(id);
    }
    
    @GetMapping(GET_BY_TRAINER_ID_ENDPOINT)
    public MemberWrapper findByTrainerId(@PathVariable("trainerid") String trainerId) {
        return service.findByTrainerId(trainerId);
    }
    
    @PutMapping(UPDATE_FACE_REG_STATUS_ENDPOINT)
    public Boolean updateFaceRegistration(@PathVariable("id") String trainerId) {
        return service.updateFaceRegistrationStatus(trainerId);
    }
    
    @PutMapping(UPDATE_MEMBERSHIP_STATUS_ENDPOINT)
    public Boolean approveMembership(@PathVariable("id") String id, @RequestParam("duration") String duration) {
        return service.approveMembership(id, duration);
    }
}
