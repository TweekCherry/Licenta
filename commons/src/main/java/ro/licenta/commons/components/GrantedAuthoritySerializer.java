/**
 * 
 */
package ro.licenta.commons.components;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

public class GrantedAuthoritySerializer extends JsonDeserializer<Set<GrantedAuthority>> {

	@Override
	public Set<GrantedAuthority> deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JacksonException {
		ObjectCodec codec = p.getCodec();
		JsonNode node = codec.readTree(p);
		
		Set<GrantedAuthority> authorities = new HashSet<>();
		node.forEach(child -> {
			authorities.add(new SimpleGrantedAuthority(child.get("authority").asText()));
		});
		return authorities;
	}

}
