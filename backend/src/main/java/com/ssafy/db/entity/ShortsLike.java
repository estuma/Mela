package com.ssafy.db.entity;

import com.sun.istack.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;

@Entity
@Getter
@Setter
public class ShortsLike {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @NotNull
    Long shortsLikeIdx;


    @ManyToOne
    @JoinColumn(name="user_idx", referencedColumnName="userIdx")
    @OnDelete(action = OnDeleteAction.CASCADE)
    @NotNull
    User userIdx;


    @ManyToOne
    @JoinColumn(name="shorts_idx", referencedColumnName="shortsIdx")
    @OnDelete(action = OnDeleteAction.CASCADE)
    @NotNull
    Shorts shortsIdx;
}
