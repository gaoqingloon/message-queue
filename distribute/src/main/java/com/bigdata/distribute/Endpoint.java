package com.bigdata.distribute;

/**
 *
 */
public interface Endpoint {

    void handle(Packet packet);
}
