package jpabook.jpashop.repository.order.query;

import jakarta.persistence.EntityManager;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class OrderQueryRepository {
    private final EntityManager em;

    public List<OrderQueryDto> findOrderQueryDtos() {
        List<OrderQueryDto> orders = findOrders();
        orders.forEach(o -> {
            List<OrderItemQueryDto> orderItems = findOrderItems(o.getOrderId()); // N+1 문제 - 쿼리가 N번 나간다.
            o.setOrderItems(orderItems);
        });
        return orders;
    }

    public List<OrderQueryDto> findAllByDto_optimization() {
        List<OrderQueryDto> orders = findOrders();
        List<Long> orderIds = orders.stream()
                .map(OrderQueryDto::getOrderId)
                .toList();

        Map<Long, List<OrderItemQueryDto>> orderItemMap = findOrderItemsByOrderIds(orderIds);

        orders.forEach(o -> o.setOrderItems(orderItemMap.get(o.getOrderId())));
        return orders;
    }

    private Map<Long, List<OrderItemQueryDto>> findOrderItemsByOrderIds(List<Long> orderIds) {
        return em.createQuery("select new OrderItemQueryDto(oi.order.id, i.name, oi.orderPrice, oi.count)" +
                        " from OrderItem oi" +
                        " join oi.item i" +
                        " where oi.order.id in :orderIds", OrderItemQueryDto.class)
                .setParameter("orderIds", orderIds).getResultList()
                .stream().collect(Collectors.groupingBy(OrderItemQueryDto::getOrderId));
    }

    private List<OrderItemQueryDto> findOrderItems(Long orderId) {
        return em.createQuery("select new OrderItemQueryDto(oi.order.id, i.name, oi.orderPrice, oi.count) from OrderItem oi " +
                        "join oi.item i " +
                        "where oi.order.id = :orderId", OrderItemQueryDto.class)
                .setParameter("orderId", orderId)
                .getResultList();
    }

    // 주의할 점, dto로 조회할 때는 fetch join을 사용하면 안된다.
    private List<OrderQueryDto> findOrders() {
        // JPQL에서 new Operation을 사용할 때, 패키지명을 생략할 수 있는 경우는 repository 패키지 내에서만 가능하다.
        return em.createQuery("select new OrderQueryDto(o.id, m.name, o.orderDate, o.status, d.address) from Order o " +
                "join o.member m " +
                "join o.delivery d ", OrderQueryDto.class).getResultList();

    }

    public List<OrderFlatDto> findAllByDto_flat() {
        return em.createQuery("select new OrderFlatDto(o.id, m.name, o.orderDate, o.status, d.address, i.name, oi.orderPrice, oi.count) " +
                "from Order o " +
                "join o.member m " +
                "join o.delivery d " +
                "join o.orderItems oi " +
                "join oi.item i", OrderFlatDto.class).getResultList();

    }
}
