package com.archy.dezhou.controller.base;

import com.archy.dezhou.entity.RequestDto;
import io.netty.handler.codec.http.FullHttpResponse;

public abstract class Controller implements IController {

    @Override
    public void process(RequestDto requestDto, FullHttpResponse response)
    {

        this.access(requestDto,response);
        
    }

}
