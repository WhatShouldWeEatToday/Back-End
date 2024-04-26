package kit.project.whatshouldweeattoday.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
public class User {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "USER_ID")
    private Long id;

    @Column(nullable = false, unique = true)
    private String loginId;

    @Column(nullable = false)
    private String loginPw;

    @Column(nullable = false, unique = true)
    private String nickname;
    private String gender;
    private int age;

    @OneToMany(mappedBy = "user")
    private List<Friendship> friendshipList = new ArrayList<>();

    @OneToMany(mappedBy = "user")
    private List<Bookmark> bookmarkList = new ArrayList<>();

    public User(String loginId, String loginPw, String nickname, String gender, int age) {
        this.loginId = loginId;
        this.loginPw = loginPw;
        this.nickname = nickname;
        this.gender = gender;
        this.age = age;
    }

    public void updateUser(String nickname, String gender, int age) {
        this.nickname = nickname;
        this.gender = gender;
        this.age = age;
    }
}
