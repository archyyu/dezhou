package com.archy.dezhou.controller;

import com.archy.dezhou.controller.base.Controller;
import com.archy.dezhou.entity.RequestDto;
import io.netty.handler.codec.http.FullHttpResponse;

import java.nio.charset.Charset;

public class GameController extends Controller {


    @Override
    public void access(RequestDto requestDto, FullHttpResponse response) {


        if(requestDto.getFn() == "login"){

        }
        else if(requestDto.getFn() == "") {

        }
        else if(requestDto.getFn() == ""){

        }
        else{

        }

        response.content().writeCharSequence(new String("asas"), Charset.defaultCharset());

    }


}
