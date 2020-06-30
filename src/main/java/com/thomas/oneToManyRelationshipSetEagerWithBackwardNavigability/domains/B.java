package com.thomas.oneToManyRelationshipSetEagerWithBackwardNavigability.domains;

import javax.persistence.*;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
//import lombok.ToString;

import java.io.Serializable;

@Entity
@Table(name = "B")
@NoArgsConstructor
@Setter @Getter
//@ToString
@EqualsAndHashCode(of = {"id"})
public class B implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int myInt;

    @ManyToOne
    @JoinColumn(name = "a_id", nullable = false)
    private A a;

    public B(int myInt, A a) {
        this.myInt = myInt;
        this.a = a;
    }

    @Override
    public String toString() {
        return "B{" +
                "id=" + id +
                ", myInt=" + myInt +
                ", a.myString=" + a.getMyString() +
                '}';
    }
}