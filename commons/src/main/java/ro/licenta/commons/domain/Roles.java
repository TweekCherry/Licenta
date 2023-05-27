package ro.licenta.commons.domain;

public final class Roles {
	
	public static final String ROLE_ADMIN = "ROLE_ADMIN";
	public static final String ROLE_CLIENT = "ROLE_CLIENT";
	public static final String ROLE_MEDIC = "ROLE_MEDIC";
	
	public static final String ADMIN_EXPLICIT = "ADMIN";
	public static final String CLIENT_EXPLICIT = "CLIENT";
	public static final String MEDIC_EXPLICIT = "MEDIC";

	public static final String ADMIN = "hasAnyRole('ADMIN')";
	public static final String CLIENT = "hasAnyRole('CLIENT')";
	public static final String MEDIC = "hasAnyRole('MEDIC')";
	
}
