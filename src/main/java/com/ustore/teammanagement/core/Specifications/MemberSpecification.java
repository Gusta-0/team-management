package com.ustore.teammanagement.core.Specifications;

import com.ustore.teammanagement.core.entity.Member;
import com.ustore.teammanagement.core.enums.MemberStatus;
import com.ustore.teammanagement.core.enums.Role;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class MemberSpecification {

    private MemberSpecification() {
        throw new UnsupportedOperationException("Classe utilitária, não instancie.");
    }

    public static Specification<Member> withName(String name) {
        return (root, query, cb) ->
                name == null ? null : cb.like(cb.lower(root.get("name")), "%" + name.toLowerCase() + "%");
    }

    public static Specification<Member> withEmail(String email) {
        return (root, query, cb) ->
                email == null ? null : cb.like(cb.lower(root.get("email")), "%" + email.toLowerCase() + "%");
    }

    public static Specification<Member> withDepartment(String department) {
        return (root, query, cb) ->
                department == null ? null : cb.like(cb.lower(root.get("department")), "%" + department.toLowerCase() + "%");
    }

    public static Specification<Member> withStatus(MemberStatus status) {
        return (root, query, cb) ->
                status == null ? null : cb.equal(root.get("status"), status);
    }

    public static Specification<Member> withRole(Role role) {
        return (root, query, cb) ->
                role == null ? null : cb.equal(root.get("role"), role);
    }

    public static Specification<Member> withSearch(String name, String email) {
        return Specification.anyOf(
                withName(name),
                withEmail(email)
        );
    }

    public static Specification<Member> withFilters(String department, MemberStatus status, Role role) {
        return Specification.allOf(
                withDepartment(department),
                withStatus(status),
                withRole(role)
        );
    }

    public static Specification<Member> withAnalysisFilters(String department, String memberName) {
        return (root, query, builder) -> {

            List<Predicate> predicates = new ArrayList<>();

            if (department != null && !department.isBlank()) {
                predicates.add(builder.like(
                        builder.lower(root.get("department")),
                        "%" + department.toLowerCase() + "%"
                ));
            }

            if (memberName != null && !memberName.isBlank()) {
                predicates.add(builder.like(
                        builder.lower(root.get("name")),
                        "%" + memberName.toLowerCase() + "%"
                ));
            }

            return builder.and(predicates.toArray(new Predicate[0]));
        };
    }


}