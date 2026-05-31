package com.archy.texasholder.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "dezhou_gameroom")
public class GameRoomDB {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "gameroomid")
    private Integer gameroomid;

    @Column(name = "roomid")
    private Integer roomid;

    @Column(name = "showname")
    private String showname;

    @Column(name = "name")
    private String name;

    @Column(name = "bbet")
    private Integer bbet;

    @Column(name = "sbet")
    private Integer sbet;

    @Column(name = "maxbuy")
    private Integer maxbuy;

    @Column(name = "minbuy")
    private Integer minbuy;

    @Column(name = "roomtype")
    private String roomtype;

    @Column(name = "creator")
    private String account;

    @Column(name = "createtime")
    private Long createtime;

    @Column(name = "releasetime")
    private Long releasetime;

}
