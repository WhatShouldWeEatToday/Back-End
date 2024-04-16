package kit.project.whatshouldweeattoday.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class Image {

    @Id
    @GeneratedValue
    @Column(name = "IMAGE_ID")
    private Long id;
    private String imageRoute;

    @OneToOne(mappedBy = "image", fetch = FetchType.LAZY)
    private Review review;
}
