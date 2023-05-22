package mega.waka.entity.redis;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import mega.waka.entity.editor.SevenDaysEditor;
import mega.waka.entity.language.SevenDaysLanguage;
import mega.waka.entity.project.SevenDaysProject;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;
import org.springframework.data.redis.core.index.Indexed;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Getter
@RedisHash(value = "sevenDaysResultHistory") //redis repository에 저장될 객체임을 명시
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SevenDaysResultHistory {
    @Id
    private String id;

    private List<SevenDaysEditor> sevenDaysEditors = new ArrayList<>();
    private List<SevenDaysLanguage> sevenDaysLanguages = new ArrayList<>();
    private List<SevenDaysProject> sevenDaysProjects = new ArrayList<>();

}
