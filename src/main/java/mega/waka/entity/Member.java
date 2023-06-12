package mega.waka.entity;

import jakarta.persistence.*;
import lombok.*;
import mega.waka.entity.editor.FourteenDaysEditor;
import mega.waka.entity.editor.OneDaysEditor;
import mega.waka.entity.editor.SevenDaysEditor;
import mega.waka.entity.editor.ThirtyDaysEditor;
import mega.waka.entity.language.FourteenDaysLanguage;
import mega.waka.entity.language.OneDaysLanguage;
import mega.waka.entity.language.SevenDaysLanguage;
import mega.waka.entity.language.ThirtyDaysLanguage;
import mega.waka.entity.project.FourteenDaysProject;
import mega.waka.entity.project.OneDaysProject;
import mega.waka.entity.project.SevenDaysProject;
import mega.waka.entity.project.ThirtyDaysProject;
import org.hibernate.annotations.BatchSize;

import java.time.LocalDateTime;
import java.util.*;

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
    private String oneDay;
    private String sevenDays;
    private String fourteenDays;
    private String thirtyDays;
    private String secretKey;
    private String image;
    private String department;

    @OneToMany(cascade = CascadeType.ALL,fetch = FetchType.LAZY)
    @BatchSize(size = 100)
    private List<FourteenDaysProject> fourtyDaysProjects = new ArrayList<>();
    @OneToMany(cascade = CascadeType.ALL,fetch = FetchType.LAZY)
    @BatchSize(size = 100)
    private List<FourteenDaysLanguage> fourtyDaysLanguages = new ArrayList<>();
    @OneToMany(cascade = CascadeType.ALL,fetch = FetchType.LAZY)
    @BatchSize(size = 100)
    private List<FourteenDaysEditor> fourtyDaysEditors = new ArrayList<>();
    @OneToMany(cascade = CascadeType.ALL,fetch = FetchType.LAZY)
    @BatchSize(size = 100)
    private Set<SevenDaysProject> sevenprojects = new HashSet<>();
    @OneToMany(cascade = CascadeType.ALL,fetch = FetchType.LAZY)
    @BatchSize(size = 100)
    private Set<SevenDaysLanguage> sevenlanguages = new HashSet<>();
    @OneToMany(cascade = CascadeType.ALL,fetch = FetchType.LAZY)
    @BatchSize(size = 100)
    private Set<SevenDaysEditor> seveneditors = new HashSet<>();

    @OneToMany(cascade = CascadeType.ALL,fetch = FetchType.LAZY)
    @BatchSize(size = 100)
    private List<OneDaysEditor> oneDaysEditors = new ArrayList<>();
    @OneToMany(cascade = CascadeType.ALL,fetch = FetchType.LAZY)
    @BatchSize(size = 100)
    private List<OneDaysProject> oneDaysProjects = new ArrayList<>();
    @OneToMany(cascade = CascadeType.ALL,fetch = FetchType.LAZY)
    @BatchSize(size = 100)
    private List<OneDaysLanguage> oneDaysLanguages = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL,fetch = FetchType.LAZY)
    @BatchSize(size = 100)
    private List<ThirtyDaysEditor> thirtyDaysEditors = new ArrayList<>();
    @OneToMany(cascade = CascadeType.ALL,fetch = FetchType.LAZY)
    @BatchSize(size = 100)
    private List<ThirtyDaysProject> thirtyDaysProjects = new ArrayList<>();
    @OneToMany(cascade = CascadeType.ALL,fetch = FetchType.LAZY)
    @BatchSize(size = 100)
    private List<ThirtyDaysLanguage> thirtyDaysLanguages = new ArrayList<>();

    @OneToOne(fetch = FetchType.EAGER,cascade = CascadeType.ALL)
    private Money money;
    private LocalDateTime startDate;
}
