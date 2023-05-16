package ro.licenta.commons.config.notifications;

public interface VapidSession {

	public String getId();

	public String getEndpoint();

	public String getAuth();

	public String getP256DH();

}
