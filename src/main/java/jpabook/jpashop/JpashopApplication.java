package jpabook.jpashop;


import com.fasterxml.jackson.datatype.hibernate5.jakarta.Hibernate5JakartaModule;
import com.fasterxml.jackson.datatype.hibernate5.jakarta.Hibernate5JakartaModule.Feature;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class JpashopApplication {

	public static void main(String[] args) {
		SpringApplication.run(JpashopApplication.class, args);
	}

	// 엔티티를 그대로 노출했을 때의 임시 방편,, 하지 말자!!!
	@Bean
	Hibernate5JakartaModule hibernate5Module() {
		Hibernate5JakartaModule hibernate5Module = new Hibernate5JakartaModule();
		// 강제로 LAZY 로딩을 사용하게 설정, 하지 않으면 null 값이 나온다.
		// controller에서 지연 로딩을 강제로 호출하면 동일하다.
//		hibernate5Module.configure(Feature.FORCE_LAZY_LOADING, true);
		return hibernate5Module;
	}

}
