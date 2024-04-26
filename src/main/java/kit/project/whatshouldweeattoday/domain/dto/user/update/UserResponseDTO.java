package kit.project.whatshouldweeattoday.domain.dto.user.update;

import kit.project.whatshouldweeattoday.domain.entity.User;
import lombok.Builder;
import lombok.Getter;

@Getter
public class UserResponseDTO {

    private String nickname;
    private String gender;
    private int age;

    @Builder
    public UserResponseDTO(String nickname, String gender, int age) {
        this.nickname = nickname;
        this.gender = gender;
        this.age = age;
    }

    /* Entity -> DTO */
    public UserResponseDTO(User user) {
        this.nickname = user.getNickname();
        this.gender = user.getGender();
        this.age = user.getAge();
    }
}