package jpabook.jpashop.domain;

import lombok.Getter;

import javax.persistence.Embeddable;
import javax.persistence.Embedded;
import java.util.Objects;

@Embeddable//내장타입임을 명시
@Getter
public class Address {
    private String city;
    private String street;
    private String zipcode;


    protected  Address(){}

    public Address(String city,String street,String zipcode)
    {
        this.city=city;
        this.street=street;
        this.zipcode=zipcode;
    }

    //값 타입은 불변타입으로 만들어야한다. setter를 닫고 생성자로만 설정할 수 있게.

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Address address = (Address) o;
        return Objects.equals(city, address.city) &&
                Objects.equals(street, address.street) &&
                Objects.equals(zipcode, address.zipcode);
    }
        //값 비교를 위해 당연히 오버라이딩 해줘야한다. 값 타입은
    @Override
    public int hashCode() {
        return Objects.hash(city, street, zipcode);
    }
}
