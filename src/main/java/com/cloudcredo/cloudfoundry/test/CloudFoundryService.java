package com.cloudcredo.cloudfoundry.test;

public enum CloudFoundryService {

    RABBITMQ("rabbitmq", "RMQaaS.provision.rabbit_node", "RMQaaS.provision.rabbit_node_free", "2.4"),
    REDIS("redis", "redis_node", "RaaS.provision.redis_node_0", "1.8"),
    MONGODB("mongodb", "mongodb_node", "RaaS.provision.mongodb_node_0", "2.8");

    public final String serviceName;
    public final String serviceNode;
    public String newServiceMessageSubjectName;
    public String serviceVersion;

    CloudFoundryService(String nodeName, String serviceNode, String subject, String serviceVersion) {
        this.serviceName = nodeName;
        this.serviceNode = serviceNode;
        this.newServiceMessageSubjectName = subject;
        this.serviceVersion = serviceVersion;
    }
}