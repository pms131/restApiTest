package me.whiteship.demoinflearnrestapi.accounts;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Set;

import org.junit.Rule;
import org.junit.jupiter.api.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("tests")
class AccountServiceTest {
	
	@Rule
	public ExpectedException expectedException = ExpectedException.none();

	@Autowired 
	AccountService accountService;
	
	@Autowired
	PasswordEncoder passwordEncoder;
	
	@Test
	public void findByUsername() {
		// Given
		String password = "munsu";
		String username = "munsu@email.com";
		Account account = Account.builder()
							.email(username)
							.password(password)
							.roles(Set.of(AccountRole.ADMIN, AccountRole.USER))
							.build();
		
		this.accountService.saveAccount(account);
		
		// When
		UserDetailsService userDetailService = accountService;
		UserDetails userDetails = userDetailService.loadUserByUsername(username);
		
		// Then
		assertThat(this.passwordEncoder.matches(password, userDetails.getPassword())).isTrue();
	}

	@Test
	public void findByUsernameFail() {
		
		// Expected
		String username = "random@email.com";
		
		expectedException.expect(UsernameNotFoundException.class);
		expectedException.expectMessage(Matchers.contains(username));
		
		
		// When
		accountService.loadUserByUsername(username);
		
		/* 2번째 예외처리 방법
		try {
			accountService.loadUserByUsername(username);
			fail("supposed to be failed");
		} catch (UsernameNotFoundException e) {
			assertThat(e.getMessage()).containsSequence(username);
		}
		*/
	}
}
