package kit.project.whatshouldweeattoday.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor // 기본 생성자의 접근 제어를 Protected로 설정함으로써 무분별한 객체 생성을 예방함
//@Builder
public class User {

    @Id @GeneratedValue
    @Column(name = "USER_ID")
    private Long id;
    private String loginId;
    private String loginPw;
    private String nickname;
    private int gender;
    private int age;

    @OneToMany(mappedBy = "user")
    private List<Bookmark> bookmarkList = new ArrayList<>();
}
