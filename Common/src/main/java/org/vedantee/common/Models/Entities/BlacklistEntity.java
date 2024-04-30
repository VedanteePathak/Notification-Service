package org.adrij.common.Models.Entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "blacklist")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BlacklistEntity extends BaseEntity{
    @Column(name = "phone_number", unique = true)
    private String phoneNumber;
}