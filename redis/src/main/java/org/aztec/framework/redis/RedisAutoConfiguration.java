package org.aztec.framework.redis;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;

@Configuration
public class RedisAutoConfiguration {

	@Bean
	  public LettuceConnectionFactory redisConnectionFactory() {

	    return new LettuceConnectionFactory(new RedisStandaloneConfiguration("server", 6379));
	  }
}
