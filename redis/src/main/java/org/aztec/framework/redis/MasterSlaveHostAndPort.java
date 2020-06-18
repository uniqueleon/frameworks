package org.aztec.framework.redis;

import java.util.Comparator;
import java.util.Set;

import redis.clients.jedis.HostAndPort;

/**
 * ���ӵ���Ϣ
 * 
 * @author tanson lam
 * @create 2016��7��16��
 */
public class MasterSlaveHostAndPort {
	/**
	 * ����Ⱥ��master����
	 */
	private final String masterName;
	/**
	 * master�ڵ���Ϣ
	 */
	private final HostAndPort master;
	/**
	 * ���slave�Ľڵ���Ϣ
	 */
	private final Set<HostAndPort> slaves;

	/**
	 * ������
	 * 
	 * @param masterName
	 * @param master
	 * @param slaves
	 */
	public MasterSlaveHostAndPort(String masterName, HostAndPort master,
			Set<HostAndPort> slaves) {
		super();
		this.masterName = masterName;
		this.master = master;
		this.slaves = slaves;
	}

	public String getMasterName() {
		return masterName;
	}

	public HostAndPort getMaster() {
		return master;
	}

	public Set<HostAndPort> getSlaves() {
		return slaves;
	}

	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((master == null) ? 0 : master.hashCode());
		result = prime * result
				+ ((masterName == null) ? 0 : masterName.hashCode());
		result = prime * result + ((slaves == null) ? 0 : slaves.hashCode());
		return result;
	}

	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		MasterSlaveHostAndPort other = (MasterSlaveHostAndPort) obj;
		if (master == null) {
			if (other.master != null)
				return false;
		} else if (!master.equals(other.master))
			return false;
		if (masterName == null) {
			if (other.masterName != null)
				return false;
		} else if (!masterName.equals(other.masterName))
			return false;
		if (slaves == null) {
			if (other.slaves != null)
				return false;
		} else if (!slaves.equals(other.slaves))
			return false;
		return true;
	}

	public String toString() {
		return "{masterName=" + masterName + ", master=" + master + ", slaves="
				+ slaves + "}";
	}

	static public class MasterSlaveHostAndPortComparator implements
			Comparator<MasterSlaveHostAndPort> {

		@Override
		public int compare(MasterSlaveHostAndPort o1, MasterSlaveHostAndPort o2) {

			if (o1.equals(o2))
				return 0;

			if (o1.getMaster().getHost().equals(o2.getMaster().getHost())) {
				if (o1.getMaster().getPort() > o2.getMaster().getPort())
					return 1;
			}

			if (compartTo(o1.getMaster().getHost(), o2.getMaster().getHost()) > 0) {
				return 1;
			}
			return -1;
		}
	}

	private static long[] parseIp(String ip) {
		ip = ip.replace(".", "#");
		long result[] = new long[4];
		String[] ip1 = ip.split("#");
		if (ip != null) {
			result[0] = Long.parseLong(ip1[0]);
			result[1] = Long.parseLong(ip1[1]);
			result[2] = Long.parseLong(ip1[2]);
			result[3] = Long.parseLong(ip1[3]);
		}
		return result;
	}

	public static int compartTo(String ip1, String ip2) {
		long[] ip11 = parseIp(ip1);
		long[] ip22 = parseIp(ip2);
		long ip1Result = 0, ip2Result = 0;
		for (int i = 0; i < 4; i++) {
			ip1Result += (ip11[i] << (24 - i * 8));
		}
		for (int i = 0; i < 4; i++) {
			ip2Result += (ip22[i] << (24 - i * 8));
		}
		if (ip1Result - ip2Result > 0) {
			return 1;
		} else if (ip1Result - ip2Result < 0) {
			return -1;
		} else {
			return 0;
		}
	}

}
