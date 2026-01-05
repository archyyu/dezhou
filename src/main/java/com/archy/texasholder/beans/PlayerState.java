package com.archy.texasholder.beans;

import java.util.List;

import com.archy.texasholder.entity.Player;
import com.archy.texasholder.entity.Puke;

import lombok.Data;

@Data
public class PlayerState {
    private int playerId;
        private String playerName;
        private int seatId;
        private int chips;
        private int currentBet;
        private int totalBet;
        private boolean hasLooked;
        private boolean isActive;
        private boolean isAllIn;
        private boolean isDealer;
        private List<Puke> cards;
        private int playerCareer;
        private int playerState;
        
        public PlayerState(Player player) {
            this.playerId = player.getUid();
            this.playerName = player.getAccount();
            this.seatId = player.getSeatId();
            this.chips = player.getRmoney();
            this.currentBet = player.getTempBet();
            this.totalBet = player.getTotalGambleBet();
            this.hasLooked = player.isHasLooked();
            this.isActive = player.isActive();
            this.isAllIn = player.isAllIn();
            this.isDealer = player.isDealer();
            this.cards = player.getOwnPukers();
            this.playerCareer = player.getPlayerState().value();
            this.playerState = player.getGameState().value();
        }
        
}
