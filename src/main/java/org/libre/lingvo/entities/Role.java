package org.libre.lingvo.entities;

import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.security.core.GrantedAuthority;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by igorek2312 on 08.09.16.
 */

@Entity
public class Role implements GrantedAuthority, Serializable {

    @Id
    @GeneratedValue
    private int id;

    @NotEmpty
    private String name;

    @OneToMany(mappedBy = "pk.role")
    private Set<UserRole> userRoles = new HashSet<>();

    public Role() {
    }

    public Role(Integer id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<UserRole> getUserRoles() {
        return userRoles;
    }

    public void setUserRoles(Set<UserRole> userRoles) {
        this.userRoles = userRoles;
    }

    @Override
    public String getAuthority() {
        return name;
    }
}

