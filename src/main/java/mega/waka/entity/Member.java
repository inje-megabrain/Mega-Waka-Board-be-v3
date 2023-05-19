package mega.waka.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Setter
@Builder
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
}
