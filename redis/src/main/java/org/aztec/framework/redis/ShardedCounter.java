package org.aztec.framework.redis;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import redis.clients.jedis.Client;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisShardInfo;
import redis.clients.util.Hashing;
import redis.clients.util.Sharded;

/**
 * �����Ƭ�ڵ�λ�õĹ�����
 * 
 * @author tanson lam
 * @create 2016��7��15��
 */
public class ShardedCounter extends Sharded<Jedis, JedisShardInfo> {

	public ShardedCounter(List<JedisShardInfo> shards) {
		super(shards);
	}

	public ShardedCounter(List<JedisShardInfo> shards, Hashing algo) {
		super(shards, algo);
	}

	public ShardedCounter(List<JedisShardInfo> shards, Pattern tagPattern) {
		super(shards, tagPattern);
	}

	public ShardedCounter(List<JedisShardInfo> shards, Hashing algo,
			Pattern tagPattern) {
		super(shards, algo, tagPattern);
	}

	public static void main(String[] args) {
		List<JedisShardInfo> shards = new ArrayList<JedisShardInfo>();
		shards.add(new JedisShardInfo("120.20.25.210", 6000));
		shards.add(new JedisShardInfo("120.20.25.210", 6004));
		ShardedCounter shardedUtil = new ShardedCounter(shards, Hashing.MURMUR_HASH);
		for (int i = 0; i < 100; i++) {
			Client client = shardedUtil.getShard(i + "").getClient();
			System.out.println("key:" + i + " ,host:" + client.getHost() + ":"
					+ client.getPort());
		}

	}
}
