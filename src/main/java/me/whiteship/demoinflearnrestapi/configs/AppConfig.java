package me.whiteship.demoinflearnrestapi.configs;

import java.util.Set;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

import me.whiteship.demoinflearnrestapi.accounts.Account;
import me.whiteship.demoinflearnrestapi.accounts.AccountRole;
import me.whiteship.demoinflearnrestapi.accounts.AccountService;

@Configuration
public class AppConfig {

	@Bean
	public ModelMapper modelMapper() {
		return new ModelMapper();
	}
	
	@Bean
	public PasswordEncoder passwordEncoder() {
		return PasswordEncoderFactories.createDelegatingPasswordEncoder();
	}
	
	@Bean
	public ApplicationRunner applicationRunner() {
		return new ApplicationRunner() {
			
			@Autowired
			AccountService accountService;
			
			@Override
			public void run(ApplicationArguments args) throws Exception {
				Account account = Account.builder()
										.email("munsu@email.com")
										.password("munsu")
										.roles(Set.of(AccountRole.ADMIN, AccountRole.USER))
										.build()
										;
				
				accountService.saveAccount(account);
						
			}
		};
	}
}
