package jpabook.jpashop.domain;

import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter // 값 타입은 변경 불가능하게 설계해야 한다.
@NoArgsConstructor(access = AccessLevel.PROTECTED) // JPA 스펙상 엔티티나 임베디드 타입은 기본 생성자를 public 또는 protected로 설정해야 한다.
public class Address {
    private String city;
    private String street;
    private String zipcode;



    public Address(String city, String street, String zipcode) {
        // 이렇게 설정을 하면 JPA에서는 기본 생성자로 프록시 객체(리플렉션 기술)를 생성하기 때문에,
        // 기본 생성자도 필요하다
        this.city = city;
        this.street = street;
        this.zipcode = zipcode;
    }
}
