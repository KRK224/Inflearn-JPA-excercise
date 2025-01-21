package jpabook.jpashop.api;

import java.util.List;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.repository.OrderRepository;
import jpabook.jpashop.repository.OrderSearch;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * xToOne (ManyToOne, OneToOne) 관계 최적화
 * Order
 * Order -> Member (ManyToOne)
 * Order -> Delivery (OneToOne)
 */
@RestController
@RequiredArgsConstructor
public class OrderSimpleApiController {
    private final OrderRepository orderRepository;

    @GetMapping("/api/v1/simple-orders")
    public List<Order> ordersV1() {
        /**
         * N+1 문제 발생.
         * JPQL은 SQL로 번역되어 DB에서 데이터를 가져온다.
         * 따라서, join 쿼리로 성능 최적화 대신에 LAZY로 설정된 데이터를 가져올 때마다 쿼리를 날린다. (N+1 문제)
         */
        List<Order> allByString = orderRepository.findAllByString(new OrderSearch());
        for (Order order : allByString) {
            order.getMember().getName(); // Lazy 강제 초기화
            order.getDelivery().getAddress(); // Lazy 강제 초기화
        }
        return allByString;
    }
}
