package kit.project.whatshouldweeattoday.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class Meet extends BaseTimeEntity {
    @Id
    @GeneratedValue
    @Column(name = "MEET_ID")
    private Long id;
    private String meetLocate;
    private String meetMenu;

    @OneToOne(mappedBy = "meet", fetch = FetchType.LAZY)
    private Chat chat;
}
