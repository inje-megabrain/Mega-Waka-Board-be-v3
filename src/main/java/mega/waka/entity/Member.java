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
    private String oneDay;
    private String sevenDays;
    private String fourteenDays;
    private String thirtyDays;
    private String secretKey;
    private String image;
    private String department;

    @OneToMany(cascade = CascadeType.ALL,fetch = FetchType.LAZY)
    private List<FourteenDaysProject> fourtyDaysProjects = new ArrayList<>();
    @OneToMany(cascade = CascadeType.ALL,fetch = FetchType.LAZY)
    private List<FourteenDaysLanguage> fourtyDaysLanguages = new ArrayList<>();
    @OneToMany(cascade = CascadeType.ALL,fetch = FetchType.LAZY)
    private List<FourteenDaysEditor> fourtyDaysEditors = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL,fetch = FetchType.LAZY)
    private List<SevenDaysProject> sevenprojects = new ArrayList<>();
    @OneToMany(cascade = CascadeType.ALL,fetch = FetchType.LAZY)
    private List<SevenDaysLanguage> sevenlanguages = new ArrayList<>();
    @OneToMany(cascade = CascadeType.ALL,fetch = FetchType.LAZY)
    private List<SevenDaysEditor> seveneditors = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL,fetch = FetchType.LAZY)
    private List<OneDaysEditor> oneDaysEditors = new ArrayList<>();
    @OneToMany(cascade = CascadeType.ALL,fetch = FetchType.LAZY)
    private List<OneDaysProject> oneDaysProjects = new ArrayList<>();
    @OneToMany(cascade = CascadeType.ALL,fetch = FetchType.LAZY)
    private List<OneDaysLanguage> oneDaysLanguages = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL,fetch = FetchType.LAZY)
    private List<ThirtyDaysEditor> thirtyDaysEditors = new ArrayList<>();
    @OneToMany(cascade = CascadeType.ALL,fetch = FetchType.LAZY)
    private List<ThirtyDaysProject> thirtyDaysProjects = new ArrayList<>();
    @OneToMany(cascade = CascadeType.ALL,fetch = FetchType.LAZY)
    private List<ThirtyDaysLanguage> thirtyDaysLanguages = new ArrayList<>();
    @OneToOne
    private Money money;
    private LocalDateTime startDate;
}
