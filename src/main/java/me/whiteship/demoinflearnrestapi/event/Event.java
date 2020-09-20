package me.whiteship.demoinflearnrestapi.event;

import java.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import me.whiteship.demoinflearnrestapi.accounts.Account;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter
@EqualsAndHashCode(of = "id")
@Entity
public class Event {
	
	
	@Id @GeneratedValue
	private Integer id;
	private String name;
	private String description;
	private LocalDateTime beginEnrollmentDateTime;
	private LocalDateTime closeEnrollmentDateTime;
	private LocalDateTime beginEventDateTime;
	private LocalDateTime endEventDateTime;
	private String location;	//(optional) 이게 없으면 온라인 모임
	private int basePrice;		//(optional)
	private int maxPrice;		//(optional)
	private int limitOfEnrollment;
	private boolean offline;
	private boolean free;
	@Enumerated(EnumType.STRING)
	private EventStatus eventStatus = EventStatus.DRAFT;
	@ManyToOne
	private Account manager;
	public void update() {
		// Update Free
		this.free = (basePrice == 0 && maxPrice == 0) ? true : false;
		
		// Update Offline 
		this.offline = this.location == null || this.location.isBlank() ? false: true;   
	}
}
