package com.archy.dezhou.beans;

import com.archy.dezhou.entity.Puke;

import lombok.Data;

@Data
public class PukerState {
    private String suit; // "hearts", "diamonds", "clubs", "spades"
    private String rank; // "2", "3", ..., "10", "J", "Q", "K", "A"
    
    public PukerState(Puke puke) {
        this.suit = puke.getTag();
        this.rank = puke.getNum() + "";
    }
}
