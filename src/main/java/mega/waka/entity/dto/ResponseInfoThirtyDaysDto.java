package mega.waka.entity.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import mega.waka.entity.Money;
import mega.waka.entity.editor.SevenDaysEditor;
import mega.waka.entity.editor.ThirtyDaysEditor;
import mega.waka.entity.language.SevenDaysLanguage;
import mega.waka.entity.language.ThirtyDaysLanguage;
import mega.waka.entity.project.SevenDaysProject;
import mega.waka.entity.project.ThirtyDaysProject;
import org.springframework.data.redis.core.RedisHash;

import java.util.ArrayList;
import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ResponseInfoThirtyDaysDto {
    private String name;
    @Builder.Default
    private List<ThirtyDaysEditor> totalEditors = new ArrayList<>();
    @Builder.Default
    private List<ThirtyDaysLanguage> totalLanguages = new ArrayList<>();
    @Builder.Default
    private List<ThirtyDaysProject> totalProejects = new ArrayList<>();
    private String oranization;
    private Money money;
    private String imageURL;
}
