package mega.waka.entity.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import mega.waka.entity.Money;
import mega.waka.entity.editor.SevenDaysEditor;
import mega.waka.entity.language.SevenDaysLanguage;
import mega.waka.entity.project.SevenDaysProject;
import org.json.simple.JSONArray;
import org.springframework.data.redis.core.RedisHash;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ResponseInfoDto {
    private String name;
    @Builder.Default
    private List<SevenDaysEditor> totalEditors = new ArrayList<>();
    @Builder.Default
    private List<SevenDaysLanguage> totalLanguages = new ArrayList<>();
    @Builder.Default
    private List<SevenDaysProject> totalProejects = new ArrayList<>();
    private String oranization;
    private Money money;
    private String imageURL;
    private String department;
}
