package mega.waka.entity.project;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import mega.waka.entity.Member;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class SevenDaysProject {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String time;
    @JsonIgnore
    @JoinColumn(name = "member_id")
    @ManyToOne
    private Member member;
}
