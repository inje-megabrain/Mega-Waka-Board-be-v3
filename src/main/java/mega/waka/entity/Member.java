package mega.waka.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
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
    private String fourteenDays;
    private String secretKey;
    private String thirtyDays;
    @OneToMany(cascade = CascadeType.ALL,fetch = FetchType.LAZY)
    private List<Project> projects = new ArrayList<>();
    @OneToMany(cascade = CascadeType.ALL,fetch = FetchType.LAZY)
    private List<Language> languages = new ArrayList<>();
    @OneToMany(cascade = CascadeType.ALL,fetch = FetchType.LAZY)
    private List<Editor> editors = new ArrayList<>();
    @OneToMany(cascade = CascadeType.ALL,fetch = FetchType.LAZY)
    private List<SevenDaysProject> sevenprojects = new ArrayList<>();
    @OneToMany(cascade = CascadeType.ALL,fetch = FetchType.LAZY)
    private List<SevenDaysLanguage> sevenlanguages = new ArrayList<>();
    @OneToMany(cascade = CascadeType.ALL,fetch = FetchType.LAZY)
    private List<SevenDaysEditor> seveneditors = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL,fetch = FetchType.LAZY)
    private List<ThirtyDaysProject> thirtyDaysProjects = new ArrayList<>();
    @OneToMany(cascade = CascadeType.ALL,fetch = FetchType.LAZY)
    private List<ThirtyDaysLanguage> thirtyDaysLanguage = new ArrayList<>();
    @OneToMany(cascade = CascadeType.ALL,fetch = FetchType.LAZY)
    private List<ThirtyDaysEditor> thirtyDaysEditors = new ArrayList<>();

    private LocalDate startDate; //1년 구분자
    private LocalDate updateDate; //30일 구분

}
