package com.aimcodes.fashionBlog.entities;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.type.UUIDCharType;

import javax.persistence.*;
import java.util.Date;
import java.util.UUID;

@MappedSuperclass
@Data
@AllArgsConstructor
@NoArgsConstructor
public abstract class BaseEntity {
    @Id
    @GeneratedValue(generator = "UUID")
    @Column(name="id")
    private Long id;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "createdAt", nullable = false)
    private Date createdAt;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="last_updated")
    private Date updatedAt;


    @PrePersist
    public void createdAt(){
        this.createdAt = new Date();
    }

    @PreUpdate
    public void updatedAt(){
        this.updatedAt = new Date();
    }
}
