package mega.waka.entity.language;

import jakarta.persistence.*;
import lombok.*;
import mega.waka.entity.Member;

import java.time.LocalDate;

@Setter
@Getter
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OneDaysLanguage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    @Column(name="language_name")
    private String name;
    private String time;
    private LocalDate createDate;

    @ManyToOne
    @JoinColumn(name="member_id")
    private Member member;
}
