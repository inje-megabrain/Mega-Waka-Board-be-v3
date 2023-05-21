package mega.waka.entity.project;

import jakarta.persistence.*;
import lombok.*;
import mega.waka.entity.Member;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OneDaysProject {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    @Column(name="project_name")
    private String name;
    private String time;
    private LocalDate createDate;

    @ManyToOne
    @JoinColumn(name="member_id")
    private Member member;
}
