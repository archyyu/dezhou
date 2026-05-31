package com.archy.texasholder.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name="dezhou_gameaction")
public class GameActionDB {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "game_id")
    private Integer gameId;

    @Column(name = "user_id")
    private Integer userId;

    @Column(name = "action")
    private String action;

    @Column(name = "money")
    private Integer money;

    @Column(name = "round")
    private Integer round;

    @Column(name = "turn")
    private Integer turn;
    
}
