package com.cloudcredo.cloudfoundry.test;

/**
 * An object that represents the Credentials JSON that is returned from Cloud Foundry on creation of a new service.
 *
 * @author: chris
 * @date: 29/04/2013
 */
class Credentials {

    private String nodeId;
    private String name;
    private String hostname;
    private String port;
    private String username;
    private String user;
    private String pass;
    private String url;
    private String vhost;
    private String passwd;
    private String password;
    private String host;

    public String getNodeId() {
        return nodeId;
    }

    void setNode_id(String nodeId) {
        this.nodeId = nodeId;
    }

    public String getUser() {
        return user;
    }

    void setUser(String user) {
        this.user = user;
    }

    public String getPassword() {
        return password;
    }

    void setPassword(String password) {
        this.password = password;
    }

    public String getHost() {
        return host;
    }

    void setHost(String host) {
        this.host = host;
    }


    public String getVhost() {
        return vhost;
    }

    void setVhost(String vhost) {
        this.vhost = vhost;
    }

    public String getPasswd() {
        return passwd;
    }

    void setPasswd(String passwd) {
        this.passwd = passwd;
    }

    public String getHostname() {
        return hostname;
    }

    void setHostname(String hostname) {
        this.hostname = hostname;
    }

    public String getPort() {
        return port;
    }

    void setPort(String port) {
        this.port = port;
    }

    public String getUsername() {
        return username;
    }

    void setUsername(String username) {
        this.username = username;
    }

    public String getPass() {
        return pass;
    }

    void setPass(String pass) {
        this.pass = pass;
    }

    public String getUrl() {
        return url;
    }

    void setUrl(String url) {
        this.url = url;
    }

    public String getName() {
        return name;
    }

    void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Credentials{" +
                "name='" + name + '\'' +
                ", hostname='" + hostname + '\'' +
                ", port='" + port + '\'' +
                ", username='" + username + '\'' +
                ", pass='" + pass + '\'' +
                ", url='" + url + '\'' +
                '}';
    }
}
