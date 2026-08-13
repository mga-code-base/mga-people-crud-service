package com.mygym.app.util;

public class PeopleCrudConstants {
	public static final String ADMIN_CRUD_REST_BASE = "/crud/admins";
	
	public static final String CREATE_PRIMARY_ENDPOINT = "/createprimary";
	
	public static final String ID_PLACEHOLDER_ENDPOINT = "/{id}";
	
	public static final String INACTIVE_ADMIN_ENDPOINT = "/inactiveadmins";
	
	public static final String UPDATE_ADMIN_STATUS_ENDPOINT = "/updatestatus/{id}";
	
	public static final String MEMBER_CRUD_REST_BASE = "/crud/members";
	
	public static final String INACTIVE_MEMBER_ENDPOINT = "/allinactive";
	
	public static final String USERNAME_PLACEHOLDER_ENDPOINT = "/username/{username}";

	public static final String UPDATE_END_DATE_ENDPOINT = "/enddate/{id}";
	
	public static final String UPDATE_TRAINER_ID_ENDPOINT = "/trainer/{id}";
	
	public static final String GET_BY_TRAINER_ID_ENDPOINT = "/bytrainerid/{trainerid}";
	
	public static final String UPDATE_FACE_REG_STATUS_ENDPOINT = "/faceregis/{id}";
	
	public static final String UPDATE_MEMBERSHIP_STATUS_ENDPOINT = "/approve/{id}";
	
	public static final String TRAINER_CRUD_REST_BASE = "/crud/trainers";
	
	public static final String ALL_TRAINERS_ENDPOINT = "/all";
	
	public static final String ALL_INACTIVE_TRAINERS_ENDPOINT = "/allinactive";
	
	public static final String TRAINER_ID_PLACEHOLDER_ENDPOINT = "/{trainerid}";
	
	public static final String APPROVE_BY_TRAINER_ID_ENDPOINT = "/approve/{trainerid}";
	
	public static final String DISABLE_BY_TRAINER_ID_ENDPOINT = "/disable/{trainerid}";
}
