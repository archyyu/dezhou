package com.archy.dezhou.controller.base;

import io.netty.handler.codec.http.FullHttpResponse;

public abstract class Controller implements IController {

    @Override
    public void process(String cmd,String body,FullHttpResponse response)
    {
        this.access(cmd,body,response);
    }

}
