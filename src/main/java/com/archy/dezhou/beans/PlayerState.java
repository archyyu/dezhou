package com.archy.dezhou.beans;

import java.util.List;

import com.archy.dezhou.entity.Player;

import lombok.Data;

@Data
public class PlayerState {
    private int playerId;
        private String playerName;
        private int seatId;
        private int chips;
        private int currentBet;
        private boolean hasLooked;
        private boolean isActive;
        private boolean isAllIn;
        private boolean isDealer;
        private List<PukerState> cards;
        private String status; // "playing", "folded", "all-in", etc.
        
        public PlayerState(Player player) {
            this.playerId = player.getUid();
            this.playerName = player.getAccount();
            this.seatId = player.getSeatId();
            this.chips = player.getChips();
            this.currentBet = player.getCurrentBet();
            this.hasLooked = player.isHasLooked();
            this.isActive = player.isActive();
            this.isAllIn = player.isAllIn();
            this.isDealer = player.isDealer();
        }
        
}
