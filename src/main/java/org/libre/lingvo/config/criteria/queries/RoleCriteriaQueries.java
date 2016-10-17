package org.libre.lingvo.config.criteria.queries;

import org.libre.lingvo.entities.Role;
import org.libre.lingvo.entities.Role_;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.ParameterExpression;
import javax.persistence.criteria.Root;

/**
 * Created by igorek2312 on 23.09.16.
 */
@Configuration
public class RoleCriteriaQueries extends AbstractCriteriaQueriesConfig {
    @Bean
    public CriteriaQuery<Role> findRoleByNameCriteriaQuery(){
        CriteriaQuery<Role> cq = cb.createQuery(Role.class);
        Root<Role> root = cq.from(Role.class);
        ParameterExpression<String> parameter = cb.parameter(String.class, "name");
        cq.select(root);
        cq.where(cb.equal(root.get(Role_.name),parameter));
        return cq;
    }
}
