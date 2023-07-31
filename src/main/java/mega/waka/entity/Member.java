package mega.waka.entity;

import jakarta.persistence.*;
import lombok.*;
import mega.waka.entity.editor.SevenDaysEditor;
import mega.waka.entity.language.SevenDaysLanguage;
import mega.waka.entity.project.SevenDaysProject;
import org.hibernate.annotations.BatchSize;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Member {
    @Id
    private UUID id;
    private String name;
    private String organization;
    private String sevenDays;
    private String secretKey;
    private String image;
    private String department;

    @OneToMany(cascade = CascadeType.ALL,fetch = FetchType.LAZY,orphanRemoval = true)
    @BatchSize(size = 100)
    private List<SevenDaysProject> sevenprojects = new ArrayList<>();
    @OneToMany(cascade = CascadeType.ALL,fetch = FetchType.LAZY,orphanRemoval = true)
    @BatchSize(size = 100)
    private List<SevenDaysLanguage> sevenlanguages = new ArrayList<>();
    @OneToMany(cascade = CascadeType.ALL,fetch = FetchType.LAZY,orphanRemoval = true)
    @BatchSize(size = 100)
    private List<SevenDaysEditor> seveneditors = new ArrayList<>();

    @OneToOne(fetch = FetchType.EAGER,cascade = CascadeType.ALL)
    private Money money;
    private LocalDateTime startDate;
}
