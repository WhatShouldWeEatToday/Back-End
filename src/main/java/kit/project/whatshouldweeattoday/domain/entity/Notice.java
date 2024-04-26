package kit.project.whatshouldweeattoday.domain.entity;

import jakarta.persistence.*;
import kit.project.whatshouldweeattoday.domain.type.NoticeType;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class Notice {
    @Id
    @GeneratedValue
    @Column(name = "NOTICE_ID")
    private Long id;
    private String content;
    private NoticeType noticeType;

    @OneToOne(mappedBy = "notice", fetch = FetchType.LAZY)
    private Chat chat;
}