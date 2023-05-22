package mega.waka.entity.redis;

import lombok.*;
import mega.waka.entity.editor.SevenDaysEditor;
import mega.waka.entity.language.SevenDaysLanguage;
import mega.waka.entity.project.SevenDaysProject;
import org.springframework.data.annotation.Id;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SevenDaysResultHistory {
    @Id
    private String id;
    private List<SevenDaysEditor> sevenDaysEditors = new ArrayList<>();
    private List<SevenDaysLanguage> sevenDaysLanguages = new ArrayList<>();
    private List<SevenDaysProject> sevenDaysProjects = new ArrayList<>();

}
