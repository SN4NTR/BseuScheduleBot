package com.example.schedule.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import static javax.persistence.GenerationType.IDENTITY;

/**
 * @author Aliaksandr Miron
 */
@Data
@Table
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Schedule {

    @Id
    @Column
    @GeneratedValue(strategy = IDENTITY)
    private Integer id;

    @Column(length = 1000)
    private String monday;

    @Column(length = 1000)
    private String tuesday;

    @Column(length = 1000)
    private String wednesday;

    @Column(length = 1000)
    private String thursday;

    @Column(length = 1000)
    private String friday;

    @Column(length = 1000)
    private String saturday;
}
