package com.ustore.teammanagement.core.Specifications;

import com.ustore.teammanagement.core.entity.Member;
import com.ustore.teammanagement.core.enums.MemberStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.jpa.domain.Specification;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MemberSpecificationTest {

    @Mock
    private Root<Member> root;

    @Mock
    private CriteriaQuery<?> query;


    @Mock
    private CriteriaBuilder cb;

    @Mock
    private Predicate predicate;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void shouldReturnNullWhenNameIsNull() {
        Specification<Member> spec = MemberSpecification.withName(null);
        Predicate result = spec.toPredicate(root, query, cb);
        assertNull(result);
    }

    @Test
    void shouldReturnPredicateWhenNameIsProvided() {
//        when(root.get("name")).thenReturn(mock(javax.persistence.criteria.Path.class));
//        when(cb.lower(any())).thenReturn(mock(javax.persistence.criteria.Expression.class));
        when(cb.like(any(), anyString())).thenReturn(predicate);

        Specification<Member> spec = MemberSpecification.withName("Carlos");
        Predicate result = spec.toPredicate(root, query, cb);

        assertNotNull(result);
        verify(cb).like(any(), eq("%carlos%"));
    }

    @Test
    void shouldReturnPredicateWhenStatusIsProvided() {
//        when(root.get("status")).thenReturn(mock(javax.persistence.criteria.Path.class));
        when(cb.equal(any(), eq(MemberStatus.ACTIVE))).thenReturn(predicate);

        Specification<Member> spec = MemberSpecification.withStatus(MemberStatus.ACTIVE);
        Predicate result = spec.toPredicate(root, query, cb);

        assertNotNull(result);
        verify(cb).equal(any(), eq(MemberStatus.ACTIVE));
    }

    @Test
    void shouldReturnNullWhenStatusIsNull() {
        Specification<Member> spec = MemberSpecification.withStatus(null);
        Predicate result = spec.toPredicate(root, query, cb);
        assertNull(result);
    }

//    @Test
//    void shouldThrowExceptionWhenTryingToInstantiate() {
//        assertThrows(UnsupportedOperationException.class, MemberSpecification::new);
//    }
}