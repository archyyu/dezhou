package com.archy.dezhou.controller.base;

import io.netty.handler.codec.http.FullHttpResponse;

public interface IController {

    void process(String cmd,String body,FullHttpResponse response);

    void access(String cmd, String body, FullHttpResponse response);

}
