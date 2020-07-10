package org.aztec.framework.heartbeat.impl;

import org.aztec.framework.heartbeat.entity.ServerNode;


@FunctionalInterface
public interface ServerEvaluator {

    public double getScore(ServerNode node);
}
