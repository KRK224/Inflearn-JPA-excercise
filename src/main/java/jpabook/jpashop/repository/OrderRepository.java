package jpabook.jpashop.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import java.util.List;
import jpabook.jpashop.domain.Order;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

@Repository
@RequiredArgsConstructor
public class OrderRepository {
    private final EntityManager em;

    public void save(Order order) {
        em.persist(order);
    }

    public Order findOne(Long id) {
        return em.find(Order.class, id);
    }

    public List<Order> findAll(OrderSearch orderSearch) {
        // 동적 쿼리는 queryDSL로 배우자...
        String jpql = "select order from Order order join order.member m";
        return em.createQuery(jpql +
                        " where m.name like :name" +
                        " and order.status = :status", Order.class)
                .setParameter("name", orderSearch.getMemberName())
                .setParameter("status", orderSearch.getOrderStatus())
//                .setFirstResult(100) // 페이징
                .setMaxResults(1000) // 최대 1000건
                .getResultList();
    }

    public List<Order> findAllByString(OrderSearch orderSearch) {
        //language=JPAQL
        String jpql = "select o From Order o join o.member m";
        boolean isFirstCondition = true;
//주문 상태 검색
        if (orderSearch.getOrderStatus() != null) {
            if (isFirstCondition) {
                jpql += " where";
                isFirstCondition = false;
            } else {
                jpql += " and";
            }
            jpql += " o.status = :status";
        }
//회원 이름 검색
        if (StringUtils.hasText(orderSearch.getMemberName())) {
            if (isFirstCondition) {
                jpql += " where";
                isFirstCondition = false;
            } else {
                jpql += " and";
            }
            jpql += " m.name like :name";
        }
        TypedQuery<Order> query = em.createQuery(jpql, Order.class) .setMaxResults(1000); //최대 1000건
        if (orderSearch.getOrderStatus() != null) {
            query = query.setParameter("status", orderSearch.getOrderStatus());
        }
        if (StringUtils.hasText(orderSearch.getMemberName())) {
            query = query.setParameter("name", orderSearch.getMemberName());
        }
        return query.getResultList();
    }

    public List<Order> findAllWithMemberDelivery() {
        return em.createQuery("select o from Order o " +
                "join fetch o.member m " +
                "join fetch o.delivery d", Order.class).getResultList();
    }

    public List<Order> findAllByItem() {
        return em.createQuery("select distinct o from Order o " +
                "join fetch o.member m " +
                "join fetch o.delivery d " +
                "join fetch o.orderItems oi " +
                "join fetch oi.item i", Order.class)
                // 1:N 관계에서 join fetch를 하면 메모리 상에서 페이징을 하기 때문에 위험하다.
//                .setFirstResult(1) // 페이징 0부터 시작,
//                .setMaxResults(100)
                .getResultList();
    }

    public List<Order> findAllWithMemberDelivery(int offset, int limit) {
        return em.createQuery("select o from Order o " +
                "join fetch o.member m " +
                "join fetch o.delivery d", Order.class)
                .setFirstResult(offset)
                .setMaxResults(limit)
                .getResultList();
    }
}
