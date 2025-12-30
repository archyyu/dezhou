package com.archy.dezhou.beans;

import lombok.Data;

@Data
public class PukerState {
    private String suit; // "hearts", "diamonds", "clubs", "spades"
    private String rank; // "2", "3", ..., "10", "J", "Q", "K", "A"
    private String code; // "H2", "D10", "CA", etc.
    private int value; // Numerical value for comparison
    
    public PukerState(String suit, String rank, String code, int value) {
        this.suit = suit;
        this.rank = rank;
        this.code = code;
        this.value = value;
    }
}
