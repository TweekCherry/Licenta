/**
 * 
 */
package ro.licenta.commons.components;

import java.util.Objects;
import java.util.function.Function;

import com.github.benmanes.caffeine.cache.AsyncLoadingCache;
import com.github.benmanes.caffeine.cache.Caffeine;

import reactor.core.publisher.Mono;
import reactor.util.annotation.Nullable;

public class ReactiveMap<KEY, VALUE> {

	private AsyncLoadingCache<KEY, VALUE> cache;
	
	public ReactiveMap(Function<KEY, Mono<VALUE>> transformer) {
		Objects.requireNonNull(transformer);
		cache = Caffeine.newBuilder()
			.buildAsync((key, executor) -> transformer.apply(key).toFuture());
	}
	
	public Mono<VALUE> find(@Nullable KEY key) {
		if (key == null) {
			return Mono.empty();
		}
		return Mono.defer(() -> Mono.fromFuture(cache.get(key)));
	}
}
