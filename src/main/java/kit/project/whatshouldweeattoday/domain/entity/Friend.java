package kit.project.whatshouldweeattoday.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class Friend {
    @Id
    @GeneratedValue
    @Column(name = "FRIEND_ID")
    private Long id;
    private String nickname;
}
