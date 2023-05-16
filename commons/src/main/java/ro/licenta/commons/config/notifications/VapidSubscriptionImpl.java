package ro.licenta.commons.config.notifications;

import com.fasterxml.jackson.annotation.JsonIgnore;

import reactor.core.Disposable;

public class VapidSubscriptionImpl implements VapidSubscription {
	private static final long serialVersionUID = 7190267205146310821L;
	
	private final String key;
	@JsonIgnore
	private final Disposable disposable;

	public VapidSubscriptionImpl(String key, Disposable disposable) {
		this.disposable = disposable;
		this.key = key;
	}
	
	@Override
	public String getKey() {
		return key;
	}

	@Override
	public void cancel() {
		disposable.dispose();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((key == null) ? 0 : key.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		VapidSubscriptionImpl other = (VapidSubscriptionImpl) obj;
		if (key == null) {
			if (other.key != null)
				return false;
		} else if (!key.equals(other.key))
			return false;
		return true;
	}

}
