package mega.waka.entity.editor;

import jakarta.persistence.*;
import lombok.*;
import mega.waka.entity.Member;

import java.time.LocalDate;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class OneDaysEditor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    @Column(name="editor_name")
    private String name;
    private String time;
    private LocalDate createDate;
    @ManyToOne
    @JoinColumn(name="member_id")
    private Member member;
}
