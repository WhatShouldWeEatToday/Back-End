package kit.project.whatshouldweeattoday.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

@Entity
@Getter
@NoArgsConstructor
public class Notice extends BaseTimeEntity{
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "NOTICE_ID")
    private Long id;
    private String content;

    @OneToOne(mappedBy = "notice", fetch = FetchType.LAZY)
    private Chat chat;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MEMBER_ID")
    private Member member;

    public Notice(Member member, String content) {
        this.member = member;
        this.content = content;
    }
}
