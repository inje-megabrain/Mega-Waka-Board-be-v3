package mega.waka.entity.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import mega.waka.entity.Money;
import mega.waka.entity.editor.OneDaysEditor;
import mega.waka.entity.editor.SevenDaysEditor;
import mega.waka.entity.language.OneDaysLanguage;
import mega.waka.entity.language.SevenDaysLanguage;
import mega.waka.entity.project.OneDaysProject;
import mega.waka.entity.project.SevenDaysProject;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ResponseMemberDto {
    private String name;
    private String organization;
    private String oneDay;
    private String sevenDays;
    private String fourteenDays;
    private String thirtyDays;
    private String image;
    private LocalDateTime startDate;
    private String department;
    private Money money;
}
