package com.archy.texasholder.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.Builder;

@Entity
@Data
@Builder
@Table(name = "dezhou_room")
public class RoomDB {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

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

}
