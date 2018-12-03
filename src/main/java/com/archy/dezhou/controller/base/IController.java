package com.archy.dezhou.controller.base;

import com.archy.dezhou.entity.RequestDto;
import io.netty.handler.codec.http.FullHttpResponse;

public interface IController {

    void process(RequestDto requestDto, FullHttpResponse response);

    void access(RequestDto requestDto, FullHttpResponse response);

}
