package ro.licenta.commons.domain;

import java.util.Arrays;
import java.util.List;

public final class Roles {
	
	public static final String ROLE_ADMIN = "ROLE_ADMIN";
	public static final String ROLE_WEB = "ROLE_WEB";
	public static final String ROLE_EXPORT = "ROLE_EXPORT";
	public static final String ROLE_SITE_STATUS = "ROLE_SITE_STATUS";
	public static final String ROLE_OPTILINK_API = "ROLE_OPTILINK_API";

	public static final String WEB_EXPLICIT = "WEB";
	public static final String ADMIN_EXPLICIT = "ADMIN";
	public static final String OPTILINK_API_EXPLICIT = "OPTILINK_API";;

	public static final String ADMIN = "hasAnyRole('ADMIN')";
	public static final String WEB = "hasAnyRole('WEB')";
	public static final String EXPORT = "hasAnyRole('EXPORT')";
	public static final String SITE_STATUS = "hasAnyRole('SITE_STATUS')";
	public static final String ADMIN_OR_SITE_STATUS = "hasAnyRole('ADMIN', 'SITE_STATUS')";
	public static final String OPTILINK_API = "hasAnyRole('OPTILINK_API')";


	
	public static List<String> all() {
		return Arrays.asList(ROLE_ADMIN, ROLE_WEB, ROLE_EXPORT, ROLE_SITE_STATUS, ROLE_OPTILINK_API);
	}
	
}
