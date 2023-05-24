package mega.waka.entity.dto;

import lombok.*;
import mega.waka.entity.editor.OneDaysEditor;
import mega.waka.entity.language.OneDaysLanguage;
import mega.waka.entity.project.OneDaysProject;

import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResponseThirtyDaysSummariesDto {
    private List<OneDaysEditor> summariesEditors = new ArrayList<>();
    private List<OneDaysLanguage> summariesLanguages = new ArrayList<>();
    private List<OneDaysProject> summariesProjects = new ArrayList<>();
}
