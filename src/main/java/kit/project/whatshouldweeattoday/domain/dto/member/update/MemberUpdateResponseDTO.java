package kit.project.whatshouldweeattoday.domain.dto.member.update;

import kit.project.whatshouldweeattoday.domain.entity.Member;
import lombok.Builder;
import lombok.Getter;

@Getter
public class MemberUpdateResponseDTO {

    private String nickname;
    private String gender;
    private int age;

    @Builder
    public MemberUpdateResponseDTO(String nickname, String gender, int age) {
        this.nickname = nickname;
        this.gender = gender;
        this.age = age;
    }

    /* Entity -> DTO */
    public MemberUpdateResponseDTO(Member member) {
        this.nickname = member.getNickname();
        this.gender = member.getGender();
        this.age = member.getAge();
    }
}