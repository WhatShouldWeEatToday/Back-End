package kit.project.whatshouldweeattoday.domain.entity;

import jakarta.persistence.*;
import kit.project.whatshouldweeattoday.domain.type.NoticeType;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class Notice extends BaseTimeEntity{
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "NOTICE_ID")
    private Long id;
    private String content;
    private NoticeType noticeType;

    @OneToOne(mappedBy = "notice", fetch = FetchType.LAZY)
    private Chat chat;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MEMBER_ID")
    private Member member;

    public Notice(Member member, String content,NoticeType noticeType) {
        this.member = member;
        this.content = content;
        this.noticeType=noticeType;
    }
}
