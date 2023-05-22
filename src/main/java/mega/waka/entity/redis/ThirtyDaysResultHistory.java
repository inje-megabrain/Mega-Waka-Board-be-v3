package mega.waka.entity.redis;

import lombok.*;
import mega.waka.entity.editor.ThirtyDaysEditor;
import mega.waka.entity.language.ThirtyDaysLanguage;
import mega.waka.entity.project.ThirtyDaysProject;
import org.hibernate.annotations.Index;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

import java.util.ArrayList;
import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Setter
public class ThirtyDaysResultHistory {
    @Id
    private String id;

    private List<ThirtyDaysEditor> thirtyDaysEditors = new ArrayList<>();
    private List<ThirtyDaysLanguage> thirtyDaysLanguages = new ArrayList<>();
    private List<ThirtyDaysProject> thirtyDaysProjects = new ArrayList<>();
}
